/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.command.droplet;

import com.dropletweet.model.Droplet;
import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class GetAllTweets {
    /**
     *
     * @param key
     * @param href
     * @param tweet
     * @return
     */
    public static List run(Droplet droplet)
    {
        List<Tweet> tweetList = new LinkedList<Tweet>();
        tweetList.add(droplet.getSeed());
        return FillTweetListFromWave.run(droplet.getWave(), tweetList);
    }
}
