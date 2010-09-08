/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet.list;

import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class GetDiscussionTweets {

    public static List<Tweet> run(List<Tweet> tweetList)
    {
        //
        List<Tweet> tl = new LinkedList<Tweet>();
        //
        for (Tweet t : tweetList)
        {
            if (t.getIn_reply_to_id() == null || t.getIn_reply_to_id() > 0)
            {
                tl.add(t);
            }
        }
        return tl;
    }
}
