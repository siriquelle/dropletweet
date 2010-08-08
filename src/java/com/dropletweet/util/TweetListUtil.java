/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.log.DLog;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class TweetListUtil {

    /*******************************************************************************/
    /*START TWEET LIST METHODS*/
    /*******************************************************************************/
    public static List<Tweet> getDiscussionTweets(List<Tweet> tweetList)
    {
        //
        Iterator iter = tweetList.listIterator();
        while (iter.hasNext())
        {
            Tweet t = (Tweet) iter.next();
            DLog.log(t.getIn_reply_to_id().toString());
            if (t.getIn_reply_to_id() == null || t.getIn_reply_to_id() <= 0)
            {
                iter.remove();
            }
        }
        return tweetList;
    }

    public static List<Tweet> setTrackedTweets(List<Tweet> tweetList, List<Conversation> conversationList)
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

    public static List<Tweet> setFavouriteTweets(List<Tweet> tweetList, List<Tweet> favouritesList)
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

    public static List<Tweet> setRetweetTweets(List<Tweet> tweetList, List<Tweet> retweetList)
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

    public static List<Tweet> setPrettyTime(List<Tweet> tweetList)
    {
        if (tweetList != null)
        {
            for (Tweet tweet : tweetList)
            {
                if (tweet.getCreated_at() != null)
                {
                    tweet.setPrettyTime(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
                }
            }
        }
        return tweetList;
    }
    /*******************************************************************************/
    /*END TWEET LIST METHODS*/
    /*******************************************************************************/
}
