/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.modelmap;

import com.dropletweet.command.twitter.conversation.GetFormattedDropletTweetListFromTwitter4jStatusList;
import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.log.DLog;
import com.dropletweet.service.DropletService;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class UpdateModelMap {
    /*
     * @param twitter
     * @param session
     * @throws TwitterException
     */

    public static Map run(Twitter twitter, HttpServletRequest request, DropletService dropletService)
    {
        Map modelMap = GetModelMap.run(request);

        try
        {
            Paging paging = new Paging().count(65);
            List<Status> statusListFriends = twitter.getFriendsTimeline(paging);
            List<Status> statusListRetweets = twitter.getRetweetedByMe(paging);

            List<Tweet> friendsList = GetFormattedDropletTweetListFromTwitter4jStatusList.run(statusListFriends);
            List<Tweet> replyList = new LinkedList<Tweet>();
            List<Tweet> dmList = new LinkedList<Tweet>();
            List<Tweet> dmSentList = new LinkedList<Tweet>();
            List<Tweet> sentList = new LinkedList<Tweet>();
            List<Tweet> favouritesList = new LinkedList<Tweet>();
            List<Tweet> retweetList = GetFormattedDropletTweetListFromTwitter4jStatusList.run(statusListRetweets);
            List<Tweet> discussionList = new LinkedList<Tweet>();

            modelMap.put(AppValues.LIST_NAME_TWEET_LIST, friendsList);
            modelMap.put(AppValues.LIST_NAME_FRIENDS_LIST, friendsList);
            modelMap.put(AppValues.LIST_NAME_DISCUSSION_LIST, discussionList);
            modelMap.put(AppValues.LIST_NAME_REPLY_LIST, replyList);
            modelMap.put(AppValues.LIST_NAME_DM_LIST, dmList);
            modelMap.put(AppValues.LIST_NAME_DM_SENT_LIST, dmSentList);
            modelMap.put(AppValues.LIST_NAME_SENT_LIST, sentList);
            modelMap.put(AppValues.LIST_NAME_FAVOURITES_LIST, favouritesList);
            modelMap.put(AppValues.LIST_NAME_RETWEET_LIST, retweetList);

            User user = new User(twitter.verifyCredentials());
            user.setLatest_tweet_id(friendsList.get(0).getId());
            dropletService.persistUser(user);
            modelMap.put(AppValues.MODELMAP_KEY_USER, user);

        } catch (TwitterException ex)
        {
            request.getSession().invalidate();
            DLog.log(ex.getMessage());
        }
        return modelMap;
    }
}
