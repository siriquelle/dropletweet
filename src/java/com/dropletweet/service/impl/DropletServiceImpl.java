/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.service.impl;

import com.dropletweet.dao.ConversationDao;
import com.dropletweet.dao.SignupDao;
import com.dropletweet.domain.Signup;
import com.dropletweet.domain.User;
import com.dropletweet.service.DropletService;
import com.dropletweet.domain.Tweet;
import java.util.List;
import com.dropletweet.dao.TweetDao;
import com.dropletweet.dao.UserDao;
import com.dropletweet.domain.Conversation;

/**
 *
 * @author Siriquelle
 */
public class DropletServiceImpl implements DropletService {

    private TweetDao tweetDao;
    private UserDao userDao;
    private ConversationDao conversationDao;
    private SignupDao signupDao;

    //TWEET SERVICE
    @Override
    public List<Tweet> retieveAllTweets()
    {
        return tweetDao.getAll();
    }

    @Override
    public List<Tweet> getAllTweetsByUserId(Integer id)
    {
        return tweetDao.getAllByFromUserID(id);
    }

    @Override
    public List<Tweet> getAllTweetsByToTweetId(Long id)
    {
        return tweetDao.getAllByToTweetID(id);
    }

    @Override
    public Tweet getTweetById(Long id)
    {
        return tweetDao.getByID(id);
    }

    @Override
    public void persistTweet(Tweet tweet)
    {
        tweetDao.save(tweet);
    }

    @Override
    public void deleteTweet(Tweet tweet)
    {
        tweetDao.delete(tweet);
    }

    @Override
    public void persistTweetList(List<Tweet> tweetList)
    {
        for (Tweet tweet : tweetList)
        {
            if (tweet != null && tweet.getIn_reply_to_id() != null && tweet.getIn_reply_to_id() >-1)
            {
                tweetDao.save(tweet);
            }
        }
    }
    //USER SERVICE

    @Override
    public List<User> retieveAllUsers()
    {
        return userDao.getAll();
    }

    @Override
    public User getUserById(Integer id)
    {
        return userDao.getByID(id);
    }

    @Override
    public User getUserByScreen_Name(String screen_name)
    {
        return userDao.getByScreen_Name(screen_name);
    }

    @Override
    public void persistUser(User user)
    {

        userDao.save(user);
    }

    @Override
    public void deleteUser(User user)
    {
        userDao.delete(user);
    }

//CONVERSATION SERVICE
    @Override
    public List<Conversation> retieveAllConversations()
    {
        return conversationDao.getAll();
    }

    @Override
    public Conversation getConversationById(Integer id)
    {
        return conversationDao.getByID(id);
    }

    @Override
    public Conversation getConversationByUserIdTweetIdCombination(Integer userId, Long tweetId)
    {

        return conversationDao.getByUserTweetID(userId, tweetId);
    }

    @Override
    public List<Conversation> getAllConversationsByUserId(Integer userId)
    {
        return conversationDao.getAllByUserId(userId);
    }

    @Override
    public void persistConversation(Conversation conversation)
    {
        conversationDao.save(conversation);
    }

    @Override
    public void deleteConversation(Conversation conversation)
    {
        conversationDao.delete(conversation);
    }

//SIGNUP SERVICE
    @Override
    public List<Signup> retieveAllSignups()
    {
        return signupDao.getAll();
    }

    @Override
    public void persistSignup(Signup signup)
    {
        signupDao.save(signup);
    }

    @Override
    public void deleteSignup(Signup signup)
    {
        signupDao.delete(signup);
    }
    //DI for the DAO

    /**
     * Set the value of tweetDao
     *
     * @param tweetDao new value of tweetDao
     */
    public void setTweetDao(TweetDao tweetDao)
    {
        this.tweetDao = tweetDao;
    }

    /**
     * Set the value of userDao
     *
     * @param userDao new value of userDao
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     *
     * @param conversationDao
     */
    public void setConversationDao(ConversationDao conversationDao)
    {
        this.conversationDao = conversationDao;
    }

    /**
     * Set the value of userDao
     *
     * @param signupDao
     */
    public void setSignupDao(SignupDao signupDao)
    {
        this.signupDao = signupDao;
    }
}
