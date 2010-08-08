/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.api.retrieve;

import com.dropletweet.dao.TweetDao;
import com.dropletweet.dao.UserDao;
import com.dropletweet.test.StartUp;
import com.dropletweet.model.Search;
import com.dropletweet.model.Single;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.log.DLog;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.junit.Test;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This test will first scan a local database for any tweets that exist locally that relate
 * to the tweet that is being queried. the purpose of first determining if any tweets exits
 * that relate to the current seed tweet is to circumvent twitters limitation of only serving
 * tweets that have been replied to a user for a single day and also, to reduce the load on the
 * twitter servers and the applications queries to twitter.
 *
 * The class will first scan the local repository for relative tweets and then search twitter for
 * any new tweets.
 *
 * If any new tweets are found they will be stored locally to facilitate any further queries and
 * added to the api response for the current query.
 *
 * TODO: Check local repository for tweets and add them to the search results.
 *
 * @author Siriquelle
 */
public class Retrieve {

    @Test
    public void Test_Toyota()
    {
        DLog.log("START Toyota Ireland TEST-");
        String seedURL = "http://twitter.com/patphelan/status/19519742138";
        Retrieve retrieve = new Retrieve();
        retrieve.Setup();
        String json = retrieve.getJSONConversation(seedURL);
        while (json.contains("}{"))
        {
            json = json.replace("}{", "},{");
        }
        DLog.log(json);
        DLog.log("END Toyota Ireland TEST-");

    }

    public void ItteratorTest()
    // <editor-fold defaultstate="collapsed" desc="Testing the merging of two lists">
    {

        DLog.log("RUNNING ITTERATOR TEST-");

        List<Tweet> tweetList = new ArrayList<Tweet>();

        tweetList.add(new Tweet());
        tweetList.add(new Tweet());
        tweetList.add(new Tweet());

        int count = tweetList.size();

        for (Tweet tweet : tweetList)
        {
            System.out.print("RANDOM" + count);
            if (--count > 0)
            {
                System.out.print(",");
            }

        }
        count = tweetList.size();
        if (tweetList.size() > 0)
        {
            System.out.print(",");
        }

        for (Tweet tweet : tweetList)
        {
            System.out.print("RANDOM" + count);
            if (--count > 0)
            {
                System.out.print(",");
            }

        }

        DLog.log("FINISHED ITTERATOR TEST");

    }// </editor-fold>
    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private TweetDao tweetDao;
    private UserDao userDao;
    private Gson gson = new Gson();
    private HtmlCleaner cleaner = new HtmlCleaner();

    public Retrieve()
    {
        DLog.log("RETRIEVE OBJECT CREATED");
    }

    public void Setup()
    {
        DLog.log("START RETRIEVE SETUP");
        StartUp.startUp();
        tweetDao = (TweetDao) StartUp.ctx.getBean("tweetDao");
        userDao = (UserDao) StartUp.ctx.getBean("userDao");
        DLog.log("END RETRIEVE SETUP");
    }

    public String getJSONConversation(String seedURL)
    // <editor-fold defaultstate="collapsed" desc="Returns a JSON Formatted twitter conversation including local and remote tweets">
    {
        DLog.log("START GET JSON CONVERSATION");
        StringBuilder json = new StringBuilder();
        try
        {

            Tweet seedTweet = this.getSeedTweet(seedURL);
            Search results = this.getSearchResults(seedTweet);

            json.append("{");
            json.append("\"seed\":");
            json.append(gson.toJson(seedTweet));
            json.append(", \"wave\" :");
            json.append("[");
            json.append(this.getAllReplies(results, seedTweet.getId()));
            json.append("]");
            json.append("}");

        } catch (InterruptedException ex)
        {
            Logger.getLogger(Retrieve.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Retrieve.class.getName()).log(Level.SEVERE, null, ex);
        }
        DLog.log("END GET JSON CONVERSATION");
        return json.toString();
    }// </editor-fold>

    /**
     *
     * @param seedURL
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private Tweet getSeedTweet(String seedURL) throws InterruptedException, IOException
    // <editor-fold defaultstate="collapsed" desc="Gets the seed tweet in this conversation">
    {
        DLog.log("START GET SEED TWEET");
        Tweet seedTweet = tweetDao.getByID(this.getSeedIDFromURL(seedURL));
        User seedUser = null;
        Single seed = null;

        if (seedTweet == null)
        {
            String testCase = "http://api.twitter.com/1/statuses/show/" + this.getSeedIDFromURL(seedURL) + ".json";

            seed = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL(testCase).openStream())), Single.class);

            seedTweet = new Tweet(seed);

            seedUser = seed.getUser();

            if (seedUser.getLatest_tweet_id() == null || seedUser.getLatest_tweet_id() < seedTweet.getId())
            {
                seedUser.setLatest_tweet_id(seedTweet.getId());
            }

            tweetDao.save(seedTweet);
            userDao.save(seedUser);

        }

        DLog.log("END GET SEED TWEET");
        return seedTweet;
    }// </editor-fold>

    /**
     *
     * @param seedURL
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private Long getSeedIDFromURL(String seedURL) throws InterruptedException, IOException
    // <editor-fold defaultstate="collapsed" desc="Gets the Seed id for this conversation from the given url">
    {
        DLog.log("START GET SEED ID FROM URL");

        String name = new File(seedURL).getName();
        Long id = Long.valueOf(name);

        //Checks the local database for any tweets and returns the seed if found
        if (tweetDao.getByID(id) != null)
        {
            DLog.log("START CHECK LOCAL DATABASE FOR SEED TWEET");
            Tweet tweet = tweetDao.getByID(id);
            while (tweet.getIn_reply_to_id() != null)
            {
                tweet = tweetDao.getByID(tweet.getIn_reply_to_id());
                if (tweet != null && tweet.getIn_reply_to_id() == null)
                {
                    return tweet.getId();
                } else if (tweet == null)
                {
                    id = getSeedIDFromURL("http://twitter.com/" + tweet.getTo_user() + "/status/" + tweet.getIn_reply_to_id());
                }
            }
            DLog.log("END CHECK LOCAL DATABASE FOR SEED TWEET");
        } else
        {
            DLog.log("START CHECK TWITTER FOR SEED TWEET");
            //Queries twitter for the seed tweet in this conversation
            String fromUser = seedURL.substring((seedURL.indexOf(".com/") + 4), seedURL.indexOf("/status"));

            TagNode node = cleaner.clean(new URL("http://twitter.com/" + fromUser + "/status/" + id));
            TagNode[] myNodes = node.getElementsByName("a", true);

            for (TagNode tag : myNodes)
            {
                if (tag.getText().toString().contains("in reply to"))
                {
                    id = this.getSeedIDFromURL(tag.getAttributeByName("href"));
                }
            }
            DLog.log("END CHECK TWITTER FOR SEED TWEET");
        }
        DLog.log("END GET SEED ID FROM URL");
        return id;
    }// </editor-fold>

    /**
     *
     * @param gson
     * @param seedTweet
     * @return
     * @throws JsonParseException
     * @throws IOException
     */
    private Search getSearchResults(Tweet seedTweet)
    // <editor-fold defaultstate="collapsed" desc="Searches local database for latest tweet recorded and queries twitter for any tweets after it.">
    {
        DLog.log("START GET SEARCH RESULTS FROM TWITTER");
        Long since_id = null;
        Search search = null;

        User seedUser = userDao.getByID(seedTweet.getFrom_user_id());

        if (seedUser == null)
        {
            seedUser = new User(seedTweet);
        } else
        {
            since_id = seedUser.getLatest_tweet_id();
        }

        try
        {
            search = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL("http://search.twitter.com/search.json?q=to:" + seedTweet.getFrom_user() + "&rpp=1000&since_id=" + since_id).openStream())), Search.class);
            if (!search.getResults().isEmpty())
            {
                seedUser.setLatest_tweet_id(search.getResults().getFirst().getId());
            }
            for (Tweet tweet : search.getResults())
            {
                tweetDao.save(tweet);
            }
            userDao.save(seedUser);
        } catch (IOException ex)
        {
            Logger.getLogger(Retrieve.class.getName()).log(Level.SEVERE, null, ex);
        }

        DLog.log("END GET SEARCH RESULTS FROM TWITTER");
        return search;

    }// </editor-fold>

    /**
     *
     * @param results
     * @param seedId
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws JsonParseException
     */
    private StringBuffer getAllReplies(Search results, Long seedId)
    // <editor-fold defaultstate="collapsed" desc="Returns a JSON object with all the replies to the given seed attached">
    {
        DLog.log("START GET ALL REPLIES TO THE SEED TWEET");

        StringBuffer json = new StringBuffer();

        json.append(getLocalReplies(seedId));
        json.append(getRemoteReplies(results, seedId));

        DLog.log("END GET ALL REPLIES TO THE SEED TWEET");

        return json;
    }// </editor-fold>

    /**
     *
     * @param seedId
     * @return
     * @throws IOException
     */
    private StringBuffer getLocalReplies(Long seedId)
    // <editor-fold defaultstate="collapsed" desc="Returns a json object of all the replies to the seedId">
    {
        DLog.log("START GET REPLIES STORED LOCALLY TO THE SEED TWEET");
        StringBuffer json = new StringBuffer();

        if (!tweetDao.getAllByToTweetID(seedId).isEmpty())
        {
            List<Tweet> replyList = tweetDao.getAllByToTweetID(seedId);

            for (Tweet tweet : replyList)
            {

                json.append("{");
                json.append("\"seed\":");
                json.append(gson.toJson(tweet));
                Search checkReplyResults = this.getSearchResults(tweet);
                json.append(", \"wave\" :");
                json.append("[");
                json.append(getAllReplies(checkReplyResults, tweet.getId()));
                json.append("]");
                json.append("}");

            }

        }
        DLog.log("END GET REPLIES STORED LOCALLY TO THE SEED TWEET");
        return json;
    }// </editor-fold>

    /**
     *
     * @param results
     * @param seedId
     * @return
     * @throws IOException
     * @throws NumberFormatException
     */
    private StringBuffer getRemoteReplies(Search results, Long seedId)
    // <editor-fold defaultstate="collapsed" desc="returns a JSON object containing all remote replies to this seed">
    {
        DLog.log("START GET REPLIES TO THE SEED TWEET FROM TWITTER");
        StringBuffer json = new StringBuffer();
        Integer i = results.getResults().size();
        Integer j = 0;
        for (Tweet reply : results.getResults())
        {
            Long id = getIn_reply_to_id(reply);

            if (id != null && id.equals(seedId))
            {

                Search checkReplyResults = this.getSearchResults(reply);

                json.append("{");
                json.append("\"seed\":");
                json.append(gson.toJson(reply));
                if (checkReplyResults != null)
                {
                    json.append(", \"wave\" :");
                    json.append("[");
                    json.append(getAllReplies(checkReplyResults, reply.getId()));
                    json.append("]");
                }
                json.append("}");

            }
        }

        DLog.log("END GET REPLIES TO THE SEED TWEET FROM TWITTER");
        return json;
    }// </editor-fold>

    /**
     *
     * @param reply
     * @return
     * @throws NumberFormatException
     */
    private Long getIn_reply_to_id(Tweet reply) throws NumberFormatException
    // <editor-fold defaultstate="collapsed" desc="Gets the Id of the tweet that this Tweet is in reply to">
    {
        Long id = null;
        TagNode node = null;
        try
        {
            node = cleaner.clean(new URL("http://twitter.com/" + reply.getFrom_user() + "/status/" + reply.getId()));
        } catch (IOException ex)
        {
            Logger.getLogger(Retrieve.class.getName()).log(Level.SEVERE, null, ex);
        }
        TagNode[] myNodes = node.getElementsByName("a", true);
        for (TagNode tag : myNodes)
        {
            if (tag.getText().toString().contains("in reply to"))
            {
                DLog.log("TWEET REPLY FOUND");
                id = Long.valueOf(new File(tag.getAttributeByName("href")).getName());
                DLog.log("START UPDATE TWEET WITH REPLY ID");
                reply.setIn_reply_to_id(id);
                tweetDao.save(reply);
                DLog.log("END UPDATE TWEET WITH REPLY ID");
            }
        }
        return id;
    }// </editor-fold>
}
