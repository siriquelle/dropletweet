package com.dropletweet.service;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Signup;
import java.util.List;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;

public interface DropletService {

//TWEET SERVICE
    public List<Tweet> retieveAllTweets();

    public List<Tweet> getAllTweetsByFromUserId(Integer id);

    public List<Tweet> getAllTweetsByToUserId(Integer id);

    public List<Tweet> getAllTweetsByToTweetId(Long id);

    public Tweet getTweetById(Long id);

    public void persistTweet(Tweet tweet);

    public void persistTweetList(List<Tweet> tweetList);

    public void deleteTweet(Tweet tweet);

//USER SERVICE
    public List<User> retieveAllUsers();

    public User getUserById(Integer id);

    public User getUserByScreen_Name(String screen_name);

    public void persistUser(User tweet);

    public void deleteUser(User tweet);
//CONVERSATION SERVICE

    public List<Conversation> retieveAllConversations();

    public Conversation getConversationById(Integer id);

    public Conversation getConversationByUserIdTweetIdCombination(Integer userId, Long tweetId);

    public List<Conversation> getAllConversationsByUserId(Integer userId);

    public void persistConversation(Conversation conversation);

    public void deleteConversation(Conversation conversation);

//SIGNUP SERVICE
    public List<Signup> retieveAllSignups();

    public void persistSignup(Signup signup);

    public void deleteSignup(Signup signup);
}
