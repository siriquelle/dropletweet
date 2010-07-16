/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.api.parse;

import com.dropletweet.dao.TweetDao;
import com.dropletweet.dao.UserDao;
import com.dropletweet.test.StartUp;
import com.dropletweet.model.Search;
import com.dropletweet.model.Single;
import com.dropletweet.domain.Tweet;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
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
public class ParseAndStore {

    @Test
    public void Test()
    {
        try
        {
            new ParseAndStore();
        } catch (IOException ex)
        {
            Logger.getLogger(ParseAndStore.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ParseAndStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private TweetDao tweetDao;
    private UserDao userDao;

    public ParseAndStore() throws IOException, InterruptedException
    {
        StartUp.startUp();

        tweetDao = (TweetDao) StartUp.ctx.getBean("tweetDao");
        userDao = (UserDao) StartUp.ctx.getBean("userDao");

        Gson gson = new Gson();

//Test Case One
        String seedURL = "http://twitter.com/fanclerks/status/16786421756";

//Test Case Data Source :
        String testCase = "http://api.twitter.com/1/statuses/show/" + this.getSeed(seedURL) + ".json";
        Single seed = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL(testCase).openStream())), Single.class);
        Tweet seedTweet = new Tweet(seed);

        tweetDao.save(seedTweet);
        userDao.save(seed.getUser());

//Creates a new JSon object form the seed,
        StringBuffer json = new StringBuffer();
        json.append("{");
        json.append("\"seed\":");
        json.append(gson.toJson(seedTweet));
//Prints a log of the first Seed Tweet.
        this.logSeedTweet(seedTweet);
// Search Results Data Source:
        Search results = this.getSearchResults(gson, seedTweet);

        if (results.getResults() != null)
        {
            json.append(", \"wave\" :");
            json.append("[");
            json.append(this.getAllReplies(results, seedTweet.getId()));
            json.append("]");
        }

        //
        json.append("}");
//
        System.out.println("------------------------");
        System.out.println("Done: " + json);
        System.out.println("------------------------");
//
    }

    private Long getSeed(String seedURL) throws IOException, InterruptedException
    {

        HtmlCleaner cleaner = new HtmlCleaner();
        String name = new File(seedURL).getName();
        Long id = Long.valueOf(name);
        String fromUser = seedURL.substring((seedURL.indexOf(".com/") + 4), seedURL.indexOf("/status"));

        Thread.sleep(new Random().nextInt(250));

        TagNode node = cleaner.clean(new URL("http://twitter.com/" + fromUser + "/status/" + id));
        TagNode[] myNodes = node.getElementsByName("a", true);
//
        for (TagNode tag : myNodes)
        {
            if (tag.getText().toString().contains("in reply to"))
            {
                id = this.getSeed(tag.getAttributeByName("href"));
            }
        }

        return id;
    }

    private StringBuffer getAllReplies(Search results, Long seedId) throws IOException, InterruptedException, JsonParseException
    {

        HtmlCleaner cleaner = new HtmlCleaner();
        StringBuffer json = new StringBuffer();
        Gson gson = new Gson();

        try
        {
            Integer i = results.getResults().size();
            Integer j = 0;

            for (Tweet checkReply : results.getResults())
            {

//
                Long id = null;
                Integer sleepTime = new Random().nextInt(250);
                Thread.sleep(sleepTime * 2);
                this.logPasses(++j, sleepTime);
                System.out.println("Source: " + "http://twitter.com/" + checkReply.getFrom_user() + "/status/" + checkReply.getId());

                TagNode node = cleaner.clean(new URL("http://twitter.com/" + checkReply.getFrom_user() + "/status/" + checkReply.getId()));
                TagNode[] myNodes = node.getElementsByName("a", true);
//
                for (TagNode tag : myNodes)
                {
                    if (tag.getText().toString().contains("in reply to"))
                    {
                        id = Long.valueOf(new File(tag.getAttributeByName("href")).getName());
                        this.logRelevantReply(id, seedId);

                        if (id.equals(seedId))
                        {
                            checkReply.setIn_reply_to_id(id);
                            tweetDao.save(checkReply);

                            json.append("{");
                            json.append("\"seed\":");
                            json.append(gson.toJson(checkReply));
//start Test Case Two Code Block
                            System.out.println("---------------------------");
                            System.out.println("http://search.twitter.com/search.json?q=to:" + checkReply.getFrom_user() + "&rpp=1000&since_id=" + checkReply.getId());
                            System.out.println("---------------------------");
                            Search checkReplyResults = this.getSearchResults(gson, checkReply);
                            if (checkReplyResults.getResults() != null)
                            {
                                json.append(", \"wave\" :");
                                json.append("[");
                                    json.append(getAllReplies(checkReplyResults, checkReply.getId()));
                                json.append("]");
                            } else
                            {
                                this.logNoResults();
                            }
//end Test Case Two Code Block
                            json.append("}");
                            if (j < i-1)
                            {
                                json.append(",");
                            }
                        }
                    }
                }
            }

        } catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        } finally
        {
            return json;
        }
    }

    private Search getSearchResults(Gson gson, Tweet seedTweet) throws JsonParseException, IOException
    {
        Search results = gson.fromJson((Reader) new BufferedReader(new InputStreamReader(new URL("http://search.twitter.com/search.json?q=to:" + seedTweet.getFrom_user() + "&rpp=1000&since_id=" + seedTweet.getId()).openStream())), Search.class);
        return results;
    }

    private void logNoResults()
    {
        System.out.println("__!!NO RESULTS!!__");
    }

    private void logRelevantReply(Long id, Long seedId)
    {
        //
        System.out.println("---------------------------");
        System.out.println("The in_reply_to ID : " + id);
        System.out.println("The seed ID : " + seedId);
        System.out.println("---------------------------");

        if (id.equals(seedId))
        {
            System.out.println("__!!MATCHING IDS!!__");
        } else
        {
            System.out.println("__!!NO MATCH!!__");
        }
        System.out.println("---------------------------");
        System.out.println("");
    }

    private void logPasses(int i, int sleepTime)
    {
//
        System.out.println("---------------------------");
        System.out.println("Passes: " + i);
        System.out.println("Random Sleep: " + sleepTime);
        System.out.println("---------------------------");
        System.out.println("");
    }

    private void logSeedTweet(Tweet seedTweet)
    {
        System.out.println("---------------------------");
        System.out.println(" Seed: http://search.twitter.com/search.json?q=to:" + seedTweet.getFrom_user() + "&rpp=1000&since_id=" + seedTweet.getId());
        System.out.println("---------------------------");
    }
}
