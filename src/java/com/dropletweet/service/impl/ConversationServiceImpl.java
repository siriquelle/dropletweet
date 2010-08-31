/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.service.impl;

import com.dropletweet.command.droplet.GetAllPeeps;
import com.dropletweet.command.droplet.GetAllTweets;
import com.dropletweet.command.droplet.GetKeyTerms;
import com.dropletweet.command.tweet.EncodeTweetTextQuotes;
import com.dropletweet.command.tweet.GetDateAsPrettyTime;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.constants.DurationValues;
import com.dropletweet.service.ConversationService;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.log.DConsole;
import com.dropletweet.model.Search;
import com.dropletweet.model.Single;
import com.dropletweet.model.Droplet;
import com.dropletweet.service.DropletService;
import com.dropletweet.log.DLog;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
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
    private DropletService dropletService;

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
    // <editor-fold defaultstate="collapsed" desc="Return a JSON Formatted twitter conversaiton in JIT format for Infovis">
    {
        DLog.log("START GET JIT");
        Droplet droplet = getDropletConversation(url);
        StringBuilder jit = new StringBuilder();

        jit.append(this.fillJIT(droplet, Boolean.TRUE));

        String jcon = jit.toString();

        while (jcon.contains("}{"))
        {
            jcon = jcon.replace("}{", "},{");
        }
        if (!jcon.isEmpty())
        {
            DConsole.log("Charging...");
        }
        DLog.log("END GET JIT");
        return jcon.trim();

    }// </editor-fold>

    /**
     * 
     * @param droplet
     * @param showStats
     * @return
     */
    private String fillJIT(Droplet droplet, Boolean showStats)
    // <editor-fold defaultstate="collapsed" desc="Created the structure of the JIT JSON object">
    {
        DLog.log("START FILL JIT");
        StringBuilder jit = new StringBuilder();
        Tweet tweet = droplet.getSeed();
        if (tweet != null)
        {

            jit.append("{");
            jit.append("\"id\": \"").append(tweet.getId()).append("\",");
            jit.append("\"name\": \"").append(tweet.getFrom_user()).append("\",");
            jit.append("\"data\": {");
            if (showStats)
            {
                jit.append("\"stats\": {");
                jit.append("\"tweetCount\" : \"").append(GetAllTweets.run(droplet).size()).append("\",");
                jit.append("\"peepCount\" : \"").append(GetAllPeeps.run(droplet)).append("\",");
                jit.append("\"terms\" : \"").append(GetKeyTerms.run(droplet)).append("\"");
                jit.append("},");
            }
            String formattedText = SwapAllForLinks.run(droplet.getSeed().getText());
            formattedText = EncodeTweetTextQuotes.run(formattedText);
            jit.append("\"text\" : \"").append(formattedText).append("\",");
            jit.append("\"id\" : \"").append(tweet.getId()).append("\",");
            jit.append("\"from_user\" : \"").append(tweet.getFrom_user()).append("\",");

            String prettyDate = GetDateAsPrettyTime.run(tweet.getCreated_at());
            jit.append("\"created_at\" : \"").append(prettyDate).append("\",");
            jit.append("\"profile_image_url\" : \"").append(tweet.getProfile_image_url()).append("\",");
            jit.append("\"source\" : \"").append(EncodeTweetTextQuotes.run(tweet.getSource())).append("\"");

            jit.append("},");

            jit.append("\"children\": [");
            if (droplet != null && droplet.getWave() != null && !droplet.getWave().isEmpty())
            {
                for (Droplet d : droplet.getWave())
                {
                    jit.append(fillJIT(d, Boolean.FALSE));
                }
            }
            jit.append("]");
            jit.append("}");
            if (!jit.toString().isEmpty())
            {
                DConsole.log("Charging...");
            }
            DLog.log("END FILL JIT");
        }
        return jit.toString();

    }// </editor-fold>

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
        DConsole.log("Getting Seed");
        Long id = this.getSeedIDFromURL(seedURL);
        Tweet seedTweet = dropletService.getTweetById(id);
        User seedUser = null;
        Single seed = null;

        if (seedTweet == null)
        {
            try
            {
                String testCase = "http://api.twitter.com/1/statuses/show/" + id + ".json";
                seed = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL(testCase).openStream())), Single.class);
                seedTweet = new Tweet(seed);
                seedUser = seed.getUser();

                if (dropletService.getUserByScreen_Name(seed.getUser().getScreen_name()) != null && dropletService.getUserByScreen_Name(seed.getUser().getScreen_name()).getStatuses_count() == null)
                {
                    dropletService.deleteUser(dropletService.getUserByScreen_Name(seed.getUser().getScreen_name()));
                }

                dropletService.persistUser(seedUser);
            } catch (IOException ex)
            {
                DConsole.log("Road Block @" + seedURL.substring("http://twitter.com/".length(), seedURL.indexOf("/status/")));
                DLog.sleepLong();
            }
        }
        if (seedTweet != null)
        {
            DLog.log("SEED UPDATED: " + seedTweet.getUpdated());
            DConsole.log("Seed Found @" + seedTweet.getFrom_user());
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
    private Long getSeedIDFromURL(String seedURL)
    // <editor-fold defaultstate="collapsed" desc="Gets the Seed id for this conversation from the given url">
    {
        DLog.log("START GET SEED ID FROM URL");
        String name = new File(seedURL).getName();
        DConsole.log("Drilling Up " + name);
        Long id = Long.valueOf(name);
        Tweet tweet = dropletService.getTweetById(id);
        Tweet tweetReply = null;
        //Checks the local database for any tweets and returns the seed if found
        if (tweet != null)
        {
            DLog.log("START CHECK LOCAL DATABASE FOR SEED TWEET");
            DConsole.log("Checking locally for seed tweet");
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
                            if (tweetReply == null)
                            {
                                id = getSeedIDFromURL("http://twitter.com/" + tweet.getFrom_user() + "/status/" + tweet.getId());
                                return id;
                            }
                        }
                    } else
                    {
                        id = getSeedIDFromURL("http://twitter.com/" + tweet.getTo_user() + "/status/" + tweet.getIn_reply_to_id());
                        if (id != null)
                        {
                            return id;
                        }
                    }
                } else
                {
                    id = tweet.getId();
                    return id;
                }

            } while (tweetReply != null && tweet.getIn_reply_to_id() != null && tweet.getIn_reply_to_id() > 0);
            id = tweet.getId();
            DConsole.log(String.valueOf(id));
            DLog.log("END CHECK LOCAL DATABASE FOR SEED TWEET");
        } else
        {
            String fromUser = seedURL.substring(seedURL.indexOf("com/") + 4, seedURL.indexOf("/status"));
            id = getSeedIdFromTwitter(fromUser, id);
        }
        DConsole.log(String.valueOf(id));
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
        Search search = null;
        if (seedTweet != null)
        {
            DLog.log("START GET SEARCH RESULTS FROM TWITTER");
            DConsole.log("Tracking Down @" + seedTweet.getFrom_user());

            if (seedTweet.getUpdated() == null || (seedTweet.getUpdated().getTime() < (Calendar.getInstance().getTimeInMillis() - DurationValues.SIX_MINUTES_IN_MILLISECONDS)))
            {
                User seedUser = dropletService.getUserByScreen_Name(seedTweet.getFrom_user());

                Long since_id = null;


                if (seedUser == null)
                {
                    seedUser = new User(seedTweet);
                } else
                {
                    since_id = seedUser.getLatest_tweet_id();
                }
                Tweet fisrtTweet = getFirstTweetByFromUserId(seedUser.getId());
                Long first_id = null;
                if (fisrtTweet != null)
                {
                    first_id = fisrtTweet.getId();
                }

                try
                {
                    search = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL("http://search.twitter.com/search.json?q=to:" + seedTweet.getFrom_user() + "&since_id=" + since_id + "&rpp=350").openStream())), Search.class);
                    int size = search.getResults().size();
                    while (!search.getResults().isEmpty() && seedTweet.getId() > search.getResults().getLast().getId())
                    {
                        search.getResults().addAll(gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL("http://search.twitter.com/search.json?q=to:" + seedTweet.getFrom_user() + "&max_id=" + search.getResults().get(0) + "&since_id=" + first_id + " &rpp=350").openStream())), Search.class).getResults());
                        Collections.sort(search.getResults());
                        first_id = search.getResults().getLast().getId();
                        if (size == search.getResults().size())
                        {
                            break;
                        } else
                        {
                            size = search.getResults().size();
                        }
                    }

                    Collections.sort(search.getResults());
                    seedTweet.setUpdated(Calendar.getInstance().getTime());
                    dropletService.persistTweet(seedTweet);
                    dropletService.persistUser(seedUser);

                    DLog.log("TWEET UPDATE TIME : " + String.valueOf(seedTweet.getUpdated().getTime()));
                } catch (FileNotFoundException ex)
                {
                    DLog.log(ex.getMessage());
                    DConsole.log(ex.getMessage());
                    DLog.sleepLong();
                } catch (IOException ioe)
                {
                    DLog.log(ioe.getMessage());
                    DConsole.log(ioe.getMessage());
                    DLog.sleepLong();
                }
            }
            DConsole.log("Cleaning up @" + seedTweet.getFrom_user());
            DLog.log("END GET SEARCH RESULTS FROM TWITTER");
        }
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
        if (seed != null)
        {
            DConsole.log("Getting Replies @" + seed.getFrom_user());

            wave.addAll(this.getLocalReplies(seed));
            if (results != null)
            {
                wave.addAll(this.getRemoteReplies(seed, results));
            }
            DConsole.log("Found replies @" + seed.getFrom_user());
            DLog.log("END GET ALL REPLIES TO THE SEED TWEET");
        }
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
        DConsole.log("Rooting Up @" + seed.getFrom_user());
        LinkedList<Droplet> dropletList = new LinkedList<Droplet>();

        if (!dropletService.getAllTweetsByToTweetId(seed.getId()).isEmpty())
        {
            List<Tweet> replyList = dropletService.getAllTweetsByToTweetId(seed.getId());

            for (Tweet tweet : replyList)
            {
                DConsole.log("Found you @" + seed.getFrom_user());
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
        User user = dropletService.getUserByScreen_Name(seed.getFrom_user());
        DConsole.log("Picking Up @" + seed.getFrom_user());
        LinkedList<Droplet> dropletList = new LinkedList<Droplet>();
        if (results != null)
        {
            for (Tweet reply : results.getResults())
            {
                if (reply.getId() != null
                        && (user.getLatest_tweet_id() == null || user.getLatest_tweet_id() < reply.getId())
                        && (reply.getTo_user() != null && reply.getIn_reply_to_id() == null))
                {
                    Long in_reply_to_id = getIn_reply_to_id(reply);
                    DConsole.log("Surfing Around @" + reply.getFrom_user());

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
                    user.setLatest_tweet_id(reply.getId());
                    dropletService.persistUser(user);
                }


            }
            DConsole.log("Completed @" + seed.getFrom_user());
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
            DLog.sleep();
            node = cleaner.clean(new URL("http://twitter.com/" + reply.getFrom_user() + "/status/" + reply.getId()));

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
                        break;
                    }
                }
            }
        } catch (IOException ex)
        {
            DLog.log(ex.getMessage());
            DConsole.log("Brick Wall @" + reply.getFrom_user());
            DLog.sleepLong();
            Logger.getLogger(ConversationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            dropletService.deleteTweet(reply);
        }
        return id;
    }// </editor-fold>

    /**
     * 
     * @param fromUser
     * @param id
     * @return
     */
    private Long getSeedIdFromTwitter(String fromUser, Long id)
    {
        DLog.log("START CHECK TWITTER FOR SEED ID");
        DConsole.log("Checking Twitter for seed tweet");
        try
        {
            //Queries twitter for the seed tweet in this conversation
            TagNode node = cleaner.clean(new URL("http://twitter.com/" + fromUser + "/status/" + id));
            TagNode[] myNodes = node.getElementsByName("a", true);
            for (TagNode tag : myNodes)
            {
                if (tag.getText().toString().contains("in reply to"))
                {
                    id = this.getSeedIDFromURL(tag.getAttributeByName("href"));
                }
            }
            DConsole.log(String.valueOf(id));
            DLog.log("END CHECK TWITTER FOR SEED ID");

        } catch (IOException ex)
        {
            id = null;
            DConsole.log("Road Block @" + fromUser);
            DLog.sleepLong();
            Logger.getLogger(ConversationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    private Tweet getFirstTweetByFromUserId(Integer userId)
    {
        Tweet tweet = null;
        List<Tweet> tweetList = dropletService.getAllTweetsByToUserId(userId);
        if (!tweetList.isEmpty())
        {
            Collections.sort(tweetList);
            Collections.reverse(tweetList);

            if (tweetList.get(0) != null)
            {
                tweet = tweetList.get(0);
            }
        }
        return tweet;
    }

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
