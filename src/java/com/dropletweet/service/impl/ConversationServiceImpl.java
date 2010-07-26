/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.service.impl;

import com.dropletweet.service.ConversationService;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.model.Search;
import com.dropletweet.model.Single;
import com.dropletweet.model.Droplet;
import com.dropletweet.service.DropletService;
import com.dropletweet.util.DLog;
import com.dropletweet.util.TweetUtil;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author Siriquelle
 */
public class ConversationServiceImpl implements ConversationService {

    private Gson gson = new Gson();
    private HtmlCleaner cleaner = new HtmlCleaner();
    private final Integer TWENTY_MINUTES_IN_MILLISECONDS = 1200000;
    private final Integer SIX_HOURS_IN_MILLISECONDS = 64800000;
    DropletService dropletService;

    /**
     *
     * @param url
     * @return
     */
    @Override
    public Droplet getDropletConversation(String url)
    // <editor-fold defaultstate="collapsed" desc="Returns a Droplet object containing all tweets in the queried conversation">
    {
        DLog.log("START GET DROPLET CONVERSATION");
        Tweet seedTweet = this.getSeedTweet(url);
        Droplet droplet = new Droplet();
        droplet.setSeed(seedTweet);
        Search results = this.getSearchResults(seedTweet);
        droplet.setWave(this.getAllDropletReplies(seedTweet, results));
        DLog.log("END GET DROPLET CONVERSATION");
        return droplet;
    }// </editor-fold>

    /**
     *
     * @param url
     * @return
     */
    @Override
    public String getJsonConversation(String url)
    // <editor-fold defaultstate="collapsed" desc="Returns a JSON Formatted twitter conversation including local and remote tweets">
    {
        DLog.log("START GET JSON CONVERSATION");
        Droplet droplet = this.getDropletConversation(url);
        String json = gson.toJson(droplet);
        DLog.log("END GET JSON CONVERSATION");
        return json;
    }// </editor-fold>
    //JIT SERVICE

    /**
     *
     * @param url
     * @return
     */
    @Override
    public String getJITConversation(String url)
    {
        DLog.log("START GET JIT");
        Droplet droplet = getDropletConversation(url);
        StringBuilder jit = new StringBuilder();

        jit.append(this.fillJIT(droplet));

        String jcon = jit.toString();

        while (jcon.contains("}{"))
        {
            jcon = jcon.replace("}{", "},{");
        }
        DLog.log("END GET JIT");
        return jcon.trim();

    }

    private String fillJIT(Droplet droplet)
    {
        DLog.log("START FILL JIT");
        StringBuilder jit = new StringBuilder();
        Tweet tweet = droplet.getSeed();
        jit.append("{");
        jit.append("\"id\": \"").append(tweet.getId()).append("\",");
        jit.append("\"name\": \"").append(tweet.getFrom_user()).append("\",");
        jit.append("\"data\": {");
        String formattedText = TweetUtil.swapAllForLinks(droplet.getSeed().getText());
        formattedText = TweetUtil.encodeTweetTextQuotes(formattedText);
        jit.append("\"text\" : \"").append(formattedText).append("\",");
        jit.append("\"id\" : \"").append(tweet.getId()).append("\",");
        jit.append("\"from_user\" : \"").append(tweet.getFrom_user()).append("\",");
        String prettyDate = TweetUtil.getDateAsPrettyTime(tweet.getCreated_at());
        jit.append("\"created_at\" : \"").append(prettyDate).append("\",");
        jit.append("\"profile_image_url\" : \"").append(tweet.getProfile_image_url()).append("\",");
        jit.append("\"source\" : \"").append(TweetUtil.encodeTweetTextQuotes(tweet.getSource())).append("\"");
        jit.append("},");
        jit.append("\"children\": [");
        if (!droplet.getWave().isEmpty())
        {
            for (Droplet d : droplet.getWave())
            {
                jit.append(fillJIT(d));
            }
        }
        jit.append("]");
        jit.append("}");
        DLog.log("END FILL JIT");
        return jit.toString();

    }

    /**
     *
     * @param seedURL
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private Tweet getSeedTweet(String seedURL)
    // <editor-fold defaultstate="collapsed" desc="Gets the seed tweet in this conversation">
    {
        DLog.log("START GET SEED TWEET");
        Tweet seedTweet = dropletService.getTweetById(this.getSeedIDFromURL(seedURL));
        User seedUser = null;
        Single seed = null;

        if (seedTweet == null)
        {
            try
            {
                String testCase = "http://api.twitter.com/1/statuses/show/" + this.getSeedIDFromURL(seedURL) + ".json";
                seed = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL(testCase).openStream())), Single.class);
                seedTweet = new Tweet(seed);
                seedUser = seed.getUser();

                if (dropletService.getUserByScreen_Name(seed.getUser().getScreen_name()) != null && dropletService.getUserByScreen_Name(seed.getUser().getScreen_name()).getStatuses_count() == null)
                {
                    dropletService.deleteUser(dropletService.getUserByScreen_Name(seed.getUser().getScreen_name()));
                }

                if (seedUser.getLatest_tweet_id() == null || seedUser.getLatest_tweet_id() < seedTweet.getId())
                {
                    seedUser.setLatest_tweet_id(seedTweet.getId());
                }
                dropletService.persistUser(seedUser);
            } catch (IOException ex)
            {
                Logger.getLogger(ConversationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        DLog.log("SEED UPDATED: " + seedTweet.getUpdated());
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
    private Long getSeedIDFromURL(String seedURL)
    // <editor-fold defaultstate="collapsed" desc="Gets the Seed id for this conversation from the given url">
    {
        DLog.log("START GET SEED ID FROM URL");

        String name = new File(seedURL).getName();
        Long id = Long.valueOf(name);
        Tweet tweet = dropletService.getTweetById(id);
        Tweet tweetReply = null;
        //Checks the local database for any tweets and returns the seed if found
        if (tweet != null)
        {
            DLog.log("START CHECK LOCAL DATABASE FOR SEED TWEET");
            do
            {
                if (tweet.getIn_reply_to_id() != null && tweet.getIn_reply_to_id() > 0)
                {
                    tweetReply = dropletService.getTweetById(tweet.getIn_reply_to_id());

                    if (tweetReply != null)
                    {
                        tweet = tweetReply;
                        if (tweet.getIn_reply_to_id() != null && tweet.getIn_reply_to_id() > 0)
                        {
                            tweetReply = dropletService.getTweetById(tweet.getIn_reply_to_id());
                        }
                    } else
                    {
                        id = getSeedIDFromURL("http://twitter.com/" + tweet.getFrom_user() + "/status/" + tweet.getId());
                        return id;
                    }
                } else
                {
                    id = tweet.getId();
                    return id;
                }

            } while (tweetReply != null && tweet.getIn_reply_to_id() != null && tweet.getIn_reply_to_id() > 0);
            DLog.log("END CHECK LOCAL DATABASE FOR SEED TWEET");
        } else
        {
            try
            {
                DLog.log("START CHECK TWITTER FOR SEED TWEET");
                //Queries twitter for the seed tweet in this conversation
                String fromUser = seedURL.substring(seedURL.indexOf("com/") + 4, seedURL.indexOf("/status"));
                TagNode node = cleaner.clean(new URL("http://twitter.com/" + fromUser + "/status/" + id));
                TagNode[] myNodes = node.getElementsByName("a", true);
                for (TagNode tag : myNodes)
                {
                    if (tag.getText().toString().contains("in reply to"))
                    {
                        id = this.getSeedIDFromURL(tag.getAttributeByName("href"));
                        return id;
                    }
                }
                DLog.log("END CHECK TWITTER FOR SEED TWEET");
            } catch (IOException ex)
            {
                Logger.getLogger(ConversationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        DLog.log("CURRENT TIME : " + String.valueOf(new Date().getTime() - TWENTY_MINUTES_IN_MILLISECONDS));
        Search search = null;
        if (seedTweet.getUpdated() == null || (seedTweet.getUpdated().getTime() < (new Date().getTime() - TWENTY_MINUTES_IN_MILLISECONDS)))
        {
            Long since_id = null;
            User seedUser = dropletService.getUserByScreen_Name(seedTweet.getFrom_user());

            if (seedUser == null)
            {
                seedUser = new User(seedTweet);
            } else
            {
                since_id = seedUser.getLatest_tweet_id();
            }

            try
            {
                search = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL("http://search.twitter.com/search.json?q=to:" + seedTweet.getFrom_user() + "&since_id=" + since_id + "&rpp=10000").openStream())), Search.class);

                if (search != null)
                {
                    Iterator<Tweet> iter = search.getResults().descendingIterator();
                    while (iter.hasNext())
                    {
                        Tweet tweet = iter.next();
                        dropletService.persistTweet(tweet);
                        seedUser.setLatest_tweet_id(tweet.getId());
                    }
                }
            } catch (FileNotFoundException ex)
            {
                DLog.log(ex.getMessage());
            } catch (IOException ioe)
            {
                seedUser.setLatest_tweet_id(null);
            }
            seedTweet.setUpdated(Calendar.getInstance().getTime());
            dropletService.persistTweet(seedTweet);
            dropletService.persistUser(seedUser);
            DLog.log("TWEET UPDATE TIME : " + String.valueOf(seedTweet.getUpdated().getTime()));

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
    private LinkedList<Droplet> getAllDropletReplies(Tweet seed, Search results)
    // <editor-fold defaultstate="collapsed" desc="Returns a JSON object with all the replies to the given seed attached">
    {
        DLog.log("START GET ALL REPLIES TO THE SEED TWEET");

        LinkedList<Droplet> wave = new LinkedList<Droplet>();
        wave.addAll(this.getLocalReplies(seed));
        if (results != null)
        {
            wave.addAll(this.getRemoteReplies(seed, results));
        }
        DLog.log("END GET ALL REPLIES TO THE SEED TWEET");

        return wave;
    }// </editor-fold>

    /**
     *
     * @param seedId
     * @return
     * @throws IOException
     */
    private LinkedList<Droplet> getLocalReplies(Tweet seed)
    // <editor-fold defaultstate="collapsed" desc="Returns a json object of all the replies to the seedId">
    {
        DLog.log("START GET REPLIES STORED LOCALLY TO THE SEED TWEET");
        LinkedList<Droplet> dropletList = new LinkedList<Droplet>();

        if (!dropletService.getAllTweetsByToTweetId(seed.getId()).isEmpty())
        {
            List<Tweet> replyList = dropletService.getAllTweetsByToTweetId(seed.getId());

            for (Tweet tweet : replyList)
            {
                Droplet droplet = new Droplet();
                droplet.setSeed(tweet);

                Search checkReplyResults = this.getSearchResults(tweet);

                droplet.setWave(getAllDropletReplies(tweet, checkReplyResults));
                dropletList.add(droplet);
            }

        }
        DLog.log("END GET REPLIES STORED LOCALLY TO THE SEED TWEET");
        return dropletList;
    }// </editor-fold>

    /**
     *
     * @param results
     * @param seedId
     * @return
     * @throws IOException
     * @throws NumberFormatException
     */
    private LinkedList<Droplet> getRemoteReplies(Tweet seed, Search results)
    // <editor-fold defaultstate="collapsed" desc="returns a JSON object containing all remote replies to this seed">
    {
        DLog.log("START GET REPLIES TO THE SEED TWEET FROM TWITTER");
        LinkedList<Droplet> dropletList = new LinkedList<Droplet>();
        if (results != null)
        {
            for (Tweet reply : results.getResults())
            {
                Long in_reply_to_id = getIn_reply_to_id(reply);

                if (in_reply_to_id != null && in_reply_to_id.equals(seed.getId()))
                {

                    Search checkReplyResults = this.getSearchResults(reply);
                    Droplet droplet = new Droplet();
                    droplet.setSeed(reply);
                    if (checkReplyResults != null)
                    {
                        droplet.setWave(this.getAllDropletReplies(reply, checkReplyResults));
                    }
                    dropletList.add(droplet);
                }
            }
        } else
        {
            DLog.log("RESULTS OBJECT IS NULL");
        }

        DLog.log("END GET REPLIES TO THE SEED TWEET FROM TWITTER");
        return dropletList;
    }// </editor-fold>

    /**
     *
     * @param reply
     * @return
     * @throws NumberFormatException
     */
    private Long getIn_reply_to_id(Tweet reply)
    // <editor-fold defaultstate="collapsed" desc="Gets the Id of the tweet that this Tweet is in reply to">
    {
        Long id = null;
        TagNode node = null;
        try
        {
            node = cleaner.clean(new URL("http://twitter.com/" + reply.getFrom_user() + "/status/" + reply.getId()));
        } catch (IOException ex)
        {
            Logger.getLogger(ConversationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            dropletService.deleteTweet(reply);
        }
        if (node != null)
        {
            TagNode[] myNodes = node.getElementsByName("a", true);
            for (TagNode tag : myNodes)
            {
                if (tag.getText().toString().contains("in reply to"))
                {
                    DLog.log("TWEET REPLY FOUND");
                    id = Long.valueOf(new File(tag.getAttributeByName("href")).getName());
                    DLog.log("START UPDATE TWEET WITH REPLY ID");
                    reply.setIn_reply_to_id(id);
                    dropletService.persistTweet(reply);
                    DLog.log("END UPDATE TWEET WITH REPLY ID");
                }
            }
        }
        return id;
    }// </editor-fold>

    //DI DROPLET SERVICE
    /**
     * Set the value of dropletService
     *
     * @param dropletService
     */
    public void setDropletService(DropletService dropletService)
    {
        this.dropletService = dropletService;
    }
}
