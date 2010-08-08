/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class Twitter4jAdapterUtil {
    //

    /*******************************************************************************/
    /*START Twitter4j to Droplet ADAPTER METHODS*/
    /*******************************************************************************/
    /**
     *
     * @param twitter
     * @param statusList
     * @throws TwitterException
     */
    public static List<Tweet> getFormattedTweetListFromStatusList(List<Status> statusList) throws TwitterException
    {
        List<Tweet> formattedTweetList = new LinkedList();
        for (Status status : statusList)
        {
            Tweet tweet = new Tweet(status);
            tweet.setPrettyText(TweetUtil.swapAllForLinks(tweet.getText()));
            tweet.setPrettyTime(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
            formattedTweetList.add(tweet);
        }
        return formattedTweetList;
    }

    /**
     *
     * @param twitter
     * @param tweetList
     * @throws TwitterException
     */
    public static List<Tweet> getFormattedTweetListFromTweetList(List<Tweet> tweetList) throws TwitterException
    {

        for (Tweet tweet : tweetList)
        {
            if (tweet.getCreated_at() != null && tweet.getText() != null)
            {
                tweet.setPrettyText(TweetUtil.swapAllForLinks(tweet.getText()));
                tweet.setPrettyTime(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
            }
        }
        return tweetList;
    }

    public static List<Tweet> getDropletTweetListFromTwitter4jListOfTweets(List tweets)
    {
        List<com.dropletweet.domain.Tweet> tweetList = new LinkedList();

        for (twitter4j.Tweet tweet : (List<twitter4j.Tweet>) tweets)
        {
            com.dropletweet.domain.Tweet dt = new com.dropletweet.domain.Tweet(tweet);
            tweetList.add(dt);
        }

        return tweetList;
    }

    public static List<Tweet> getDropletTweetListFromTwitter4jDirectMessages(List directMessages)
    {
        List<Tweet> tweetList = new LinkedList();

        for (DirectMessage directMessage : (List<DirectMessage>) directMessages)
        {
            Tweet dt = new Tweet(directMessage);
            tweetList.add(dt);
        }

        return tweetList;
    }
    /*******************************************************************************/
    /*END Twitter4j to Droplet ADAPTER METHODS*/
    /*******************************************************************************/
}
