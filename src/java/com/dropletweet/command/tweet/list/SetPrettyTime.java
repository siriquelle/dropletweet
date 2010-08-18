/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet.list;

import com.dropletweet.command.tweet.GetDateAsPrettyTime;
import com.dropletweet.domain.Tweet;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class SetPrettyTime {

    public static List<Tweet> run(List<Tweet> tweetList)
    {
        if (tweetList != null)
        {
            for (Tweet tweet : tweetList)
            {
                if (tweet.getCreated_at() != null)
                {
                    tweet.setPrettyTime(GetDateAsPrettyTime.run(tweet.getCreated_at()));
                }
            }
        }
        return tweetList;
    }
}
