/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.twitter.conversation;

import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class GetDropletTweetListFromTwitter4jListOfTweets {

    public static List<Tweet> run(List tweets)
    {
        List<com.dropletweet.domain.Tweet> tweetList = new LinkedList();

        for (twitter4j.Tweet tweet : (List<twitter4j.Tweet>) tweets)
        {
            com.dropletweet.domain.Tweet dt = new com.dropletweet.domain.Tweet(tweet);
            tweetList.add(dt);
        }

        return tweetList;
    }
}
