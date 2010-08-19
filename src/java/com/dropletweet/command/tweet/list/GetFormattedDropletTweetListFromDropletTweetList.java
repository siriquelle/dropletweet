/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet.list;

import com.dropletweet.command.tweet.GetDateAsPrettyTime;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.domain.Tweet;
import java.util.List;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class GetFormattedDropletTweetListFromDropletTweetList {

    /**
     *
     * @param twitter
     * @param tweetList
     * @throws TwitterException
     */
    public static List<Tweet> run(List<Tweet> tweetList) throws TwitterException
    {

        for (Tweet tweet : tweetList)
        {
            if (tweet.getCreated_at() != null && tweet.getText() != null)
            {
                tweet.setPrettyText(SwapAllForLinks.run(tweet.getText()));
                tweet.setPrettyTime(GetDateAsPrettyTime.run(tweet.getCreated_at()));
            }
        }
        return tweetList;
    }
}
