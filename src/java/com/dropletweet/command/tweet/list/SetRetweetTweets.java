/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.command.tweet.list;

import com.dropletweet.domain.Tweet;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class SetRetweetTweets {
 public static List<Tweet> run(List<Tweet> tweetList, List<Tweet> retweetList)
    {

        if (retweetList != null)
        {
            for (Tweet tweet : tweetList)
            {
                for (Tweet ret : retweetList)
                {
                    if (ret.getId().equals(tweet.getId()))
                    {
                        tweet.setRetweet(Boolean.TRUE);
                    }
                }
            }
        }
        return tweetList;
    }
}
