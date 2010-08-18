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
public class SetFavouriteTweets {
 public static List<Tweet> run(List<Tweet> tweetList, List<Tweet> favouritesList)
    {

        if (favouritesList != null)
        {
            for (Tweet tweet : tweetList)
            {
                for (Tweet fav : favouritesList)
                {
                    if (fav.getId().equals(tweet.getId()))
                    {
                        tweet.setFavourite(Boolean.TRUE);
                    }
                }
            }
        }
        return tweetList;
    }
}
