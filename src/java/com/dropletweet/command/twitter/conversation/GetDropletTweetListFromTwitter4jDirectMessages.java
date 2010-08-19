/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.twitter.conversation;

import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;
import twitter4j.DirectMessage;

/**
 *
 * @author Siriquelle
 */
public class GetDropletTweetListFromTwitter4jDirectMessages {

    public static List<Tweet> run(List directMessages)
    {
        List<Tweet> tweetList = new LinkedList();

        for (DirectMessage directMessage : (List<DirectMessage>) directMessages)
        {
            Tweet dt = new Tweet(directMessage);
            tweetList.add(dt);
        }

        return tweetList;
    }
}
