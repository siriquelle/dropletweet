/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet.list;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class SetTrackedTweets {

    public static List<Tweet> run(List<Tweet> tweetList, List<Conversation> conversationList)
    {
        if (conversationList != null)
        {
            for (Tweet tweet : tweetList)
            {
                for (Conversation con : conversationList)
                {
                    if (con.getTweet().getId().equals(tweet.getId()))
                    {
                        tweet.setTracked(Boolean.TRUE);
                    }
                }
            }
        }
        return tweetList;
    }
}
