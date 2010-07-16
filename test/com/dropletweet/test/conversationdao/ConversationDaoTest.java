/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.conversationdao;

import com.dropletweet.dao.ConversationDao;
import com.dropletweet.dao.TweetDao;
import com.dropletweet.dao.UserDao;
import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.test.StartUp;
import com.dropletweet.domain.User;
import com.dropletweet.util.DLog;
import org.junit.Test;

/**
 *
 * @author Siriquelle
 */
public class ConversationDaoTest {
    //

    private static final String random = "RANDOM1";
    private static final Integer user_id = 55;
    private static final Long tweet_id = new Long(110000033);
    private static final Integer conversation_id = 3;
//
    private ConversationDao conversationDao;
    private UserDao userDao;
    private TweetDao tweetDao;
//
    private User user;
    private Tweet tweet;
    private Conversation conversation;

    public ConversationDaoTest()
    {
        StartUp.startUp();
        conversationDao = (ConversationDao) StartUp.ctx.getBean("conversationDao");
        userDao = (UserDao) StartUp.ctx.getBean("userDao");
        tweetDao = (TweetDao) StartUp.ctx.getBean("tweetDao");
        this.startUp();
    }

    public void startUp()
    {
        createNewUserAndTweetObjects();
    }

    /**
     *
     */
    @Test
    public void TestConversationDaoSave()
    {

        DLog.log("BEGIN Insert Conversation");

        conversation = new Conversation(user, tweet);
        conversationDao.save(conversation);

        DLog.log("END Insert Conversation");

    }

    @Test
    public void TestConversationDaoGetById()
    {

        DLog.log("BEGIN Get Conversation");

        conversation = conversationDao.getByID(2);
        DLog.log(String.valueOf(conversation.getId()));

        DLog.log("END Get Conversation");

    }

    @Test
    public void TestGetAllConversations()
    {

        DLog.log("BEGIN Get All Conversations");
        DLog.log(String.valueOf(conversationDao.getAll().size()));
        DLog.log("END Get All Conversations");

    }

    @Test
    public void TestGetAllConversationsByUserId()
    {

        DLog.log("BEGIN Get All Conversations By User Id");
        DLog.log(String.valueOf(conversationDao.getAllByUserId(user.getId()).size()));
        DLog.log("END Get All Conversations By User Id");

    }

    private void createNewUserAndTweetObjects()
    {
        user = new User();
        user.setId(user_id);
        user.setScreen_name(random);
        userDao.save(user);
        //
        tweet = new Tweet();
        tweet.setId(tweet_id);
        tweet.setFrom_user_id(user_id);
        tweet.setFrom_user(random);
        tweetDao.save(tweet);
    }

    private void removeOldObjects()
    {
//remove old objects
        if (userDao.getByID(user_id) != null)
        {
            userDao.delete(userDao.getByID(user_id));
        }
        if (tweetDao.getByID(tweet_id) != null)
        {
            tweetDao.delete(tweetDao.getByID(tweet_id));
        }
        if (conversationDao.getByID(conversation_id) != null)
        {
            conversationDao.delete(conversationDao.getByID(conversation_id));
        }
    }
}
