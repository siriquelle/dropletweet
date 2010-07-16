/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.service.impl.bin;

import com.dropletweet.service.ConversationService;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.model.Search;
import com.dropletweet.model.Single;
import com.dropletweet.model.Droplet;
import com.dropletweet.service.DropletService;
import com.dropletweet.util.DLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
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
public class ConversationServiceImplOld implements ConversationService {

    private Gson gson = new Gson();
    private HtmlCleaner cleaner = new HtmlCleaner();
    DropletService dropletService;


    public Droplet getDropletConversation(String seedURL)
    // <editor-fold defaultstate="collapsed" desc="Returns a Droplet object containing all tweets in the queried conversation">
    {
        String json = getJsonConversation(seedURL);

        JsonParser parse = new JsonParser();
        JsonElement jel = parse.parse(json);

        Droplet droplet = gson.fromJson(jel, Droplet.class);

        return droplet;
    }// </editor-fold>

    public String getJsonConversation(String seedURL)
    // <editor-fold defaultstate="collapsed" desc="Returns a JSON Formatted twitter conversation including local and remote tweets">
    {
        DLog.log("START GET JSON CONVERSATION");
        StringBuffer json = new StringBuffer();

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

        DLog.log("END GET JSON CONVERSATION");
        String jcon = json.toString();

        while (jcon.contains("}{"))
        {
            jcon = jcon.replace("}{", "},{");
        }
        
        DLog.log(jcon);
        return jcon;
    }// </editor-fold>

    /**
     * 
     * @param seedURL
     * @return
     */
    public String getJITConversation(String seedURL)
    {
        Droplet droplet = getDropletConversation(seedURL);
        StringBuffer jit = new StringBuffer();

        jit.append("{");
        jit.append("\"id\": \"" + droplet.getSeed().getId() + "\",");
        jit.append("\"name\": \"" + droplet.getSeed().getFrom_user() + "\",");
        jit.append("\"data\": {");
        jit.append("\"tweet\" : \"" + droplet.getSeed().getText().replace("\n", "") + "\"");
        jit.append("},");
        jit.append("\"children\": [");
        jit.append(getAllJITReplies(droplet.getWave()));
        jit.append("]");
        jit.append("}");

        String jcon = jit.toString();

        while (jcon.contains("}{"))
        {
            jcon = jcon.replace("}{", "},{");
        }

        return jcon.trim();

    }

    /**
     *
     * @param wave
     * @return
     */
    private String getAllJITReplies(LinkedList<Droplet> wave)
    {

        StringBuffer jit = new StringBuffer();
        for (Droplet droplet : wave)
        {
            jit.append("{");
            jit.append("\"id\": \"" + droplet.getSeed().getId() + "\",");
            jit.append("\"name\": \"" + droplet.getSeed().getFrom_user() + "\",");
            jit.append("\"data\": {");
            jit.append("\"tweet\" : \"" + droplet.getSeed().getText().replace("\n", "") + "\"");
            jit.append("},");
            jit.append("\"children\": [");
            if (!droplet.getWave().isEmpty())
            {
                jit.append(getAllJITReplies(droplet.getWave()));
            }
            jit.append("]");
            jit.append("}");
        }

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
                if (seedUser.getLatest_tweet_id() == null || seedUser.getLatest_tweet_id() < seedTweet.getId())
                {
                    seedUser.setLatest_tweet_id(seedTweet.getId());
                }
                dropletService.persistTweet(seedTweet);
                dropletService.persistUser(seedUser);
            } catch (IOException ex)
            {
                Logger.getLogger(ConversationServiceImplOld.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        DLog.log("SEED: " + seedTweet.getSource());
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

        //Checks the local database for any tweets and returns the seed if found
        if (dropletService.getTweetById(id) != null)
        {
            DLog.log("START CHECK LOCAL DATABASE FOR SEED TWEET");
            Tweet tweet = dropletService.getTweetById(id);
            while (tweet != null && dropletService.getTweetById(tweet.getIn_reply_to_id()) != null)
            {
                tweet = dropletService.getTweetById(tweet.getIn_reply_to_id());
                if (tweet != null && tweet.getIn_reply_to_id() == null)
                {
                    return tweet.getId();
                }

            }
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
                    }
                }
                DLog.log("END CHECK TWITTER FOR SEED TWEET");
            } catch (IOException ex)
            {
                Logger.getLogger(ConversationServiceImplOld.class.getName()).log(Level.SEVERE, null, ex);
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
        Long since_id = null;
        Search search = null;

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
        } catch (IOException ex)
        {
            Logger.getLogger(ConversationServiceImplOld.class.getName()).log(Level.SEVERE, null, ex);
        }
        dropletService.persistUser(seedUser);


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

        if (!dropletService.getAllTweetsByToTweetId(seedId).isEmpty())
        {
            List<Tweet> replyList = dropletService.getAllTweetsByToTweetId(seedId);

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
        if (results != null)
        {
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
        } else
        {
            DLog.log("RESULTS OBJECT IS NULL");
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
            Logger.getLogger(ConversationServiceImplOld.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     *
     * @param message
     */
    private void log(String message)
    {
        System.out.println("-----------------------");
        System.out.println(message);
        System.out.println("-----------------------");
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
