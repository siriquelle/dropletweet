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
public class SetFollowingTweets {

    public static List<Tweet> run(List<Tweet> tweetList, List<Integer> ids)
    {

        if (ids != null)
        {
            for (Tweet tweet : tweetList)
            {
                for (Integer i : ids)
                {
                    if (tweet.getFrom_user_id().equals(i))
                    {
                        tweet.setFollowing(Boolean.TRUE);
                        break;
                    }
                }
            }
        }
        return tweetList;
    }
}
