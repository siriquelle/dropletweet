/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.twitter.conversation;

import com.dropletweet.command.tweet.GetDateAsPrettyTime;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class GetFormattedDropletTweetListFromTwitter4jStatusList {

    /**
     *
     * @param twitter
     * @param statusList
     * @throws TwitterException
     */
    public static List<Tweet> run(List<Status> statusList) throws TwitterException
    {
        List<Tweet> formattedTweetList = new LinkedList();
        for (Status status : statusList)
        {
            Tweet tweet = new Tweet(status);
            tweet.setPrettyText(SwapAllForLinks.run(tweet.getText()));
            tweet.setPrettyTime(GetDateAsPrettyTime.run(tweet.getCreated_at()));
            formattedTweetList.add(tweet);
        }
        return formattedTweetList;
    }
}
