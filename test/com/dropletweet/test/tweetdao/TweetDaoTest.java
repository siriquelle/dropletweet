/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.tweetdao;

import com.dropletweet.dao.TweetDao;
import com.dropletweet.test.StartUp;
import com.dropletweet.domain.Tweet;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Siriquelle
 */
public class TweetDaoTest {

    public TweetDaoTest()
    {
        StartUp.startUp();
        tweetDao = (TweetDao) StartUp.ctx.getBean("tweetDao");
    }
    //
    private static final String random = "RANDOM1";
    private static final Long id = new Long(6);
    private static final Integer from_user_id = 2;
    private static final Integer to_user_id = 3;
    private TweetDao tweetDao;
    private Tweet tweet;

    /**
     *
     */
    @Test
    public void TestTweetDaoSave()
    {
        System.out.println("------------------");
        System.out.println("Begin Insert Tweet");
        System.out.println("------------------");
        tweet = new Tweet();
        tweet.setId(id);
        tweet.setIn_reply_to_id(id);
        tweet.setFrom_user(random);
        tweet.setFrom_user_id(from_user_id);
        tweet.setIso_language_code(random);
        tweet.setLocation(random);
        tweet.setProfile_image_url(random);
        tweet.setSource(random);
        tweet.setText(random);
        tweet.setTo_user(random);
        tweet.setTo_user_id(to_user_id);
        tweetDao.save(tweet);
        System.out.println("------------------");
        System.out.println("End Insert Tweet");
        System.out.println("------------------");
    }

    @Test
    public void TestTweetDaoGetById()
    {
        System.out.println("------------------");
        System.out.println("Begin Get Tweet");
        System.out.println("------------------");
        tweet = tweetDao.getByID(id);
        System.out.println("Name: " + tweet.getFrom_user());
        System.out.println("------------------");
        System.out.println("End Get Tweet");
        System.out.println("------------------");
    }

    @Test
    public void TestTweetDaoGetAllByUserId()
    {
        System.out.println("------------------");
        System.out.println("Begin Get Tweet");
        System.out.println("------------------");
        List<Tweet> tweets = tweetDao.getAllByFromUserID(from_user_id);
        for (Tweet t : tweets)
        {
            System.out.println("Name: " + t.getFrom_user());
        }
        System.out.println("------------------");
        System.out.println("End Get Tweet");
        System.out.println("------------------");
    }

    @Test
    public void TestTweetDaoDelete()
    {
        System.out.println("------------------");
        System.out.println("Begin Delete Tweet");
        System.out.println("------------------");
        System.out.println("Size Before Delete: " + tweetDao.getAll().size());
        tweet = tweetDao.getByID(id);
        tweetDao.delete(tweet);
        System.out.println("Size After Delete: " + tweetDao.getAll().size());
        System.out.println("------------------");
        System.out.println("End Get Tweet");
        System.out.println("------------------");
    }
}
