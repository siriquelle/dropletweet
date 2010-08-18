/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet;

import com.dropletweet.domain.Tweet;
import com.dropletweet.log.DLog;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class RemoveTweetFromListByValue {

    public static List<Tweet> run(List<Tweet> tweetList, Tweet tweet)
    {
        if (tweetList != null)
        {
            for (Tweet t : tweetList)
            {
                if (t != null)
                {
                    DLog.log(String.valueOf(t.getId()));
                    DLog.log(String.valueOf(tweet.getId()));
                    if (t.getId().equals(tweet.getId()))
                    {
                        tweetList.remove(t);
                        return tweetList;
                    }
                }
            }
        }
        return tweetList;
    }
}
