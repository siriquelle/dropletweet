/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.command.tweet.list;

import com.dropletweet.domain.Tweet;
import com.dropletweet.log.DLog;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class GetDiscussionTweets {

    public static List<Tweet> run(List<Tweet> tweetList)
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
}
