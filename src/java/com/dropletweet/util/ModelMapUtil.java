/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.log.DLog;
import com.dropletweet.service.DropletService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class ModelMapUtil {

    /*******************************************************************************/
    /*START MODEL MAP METHODS*/
    /*******************************************************************************/
    /**
     *
     * @param twitter
     * @param session
     * @throws TwitterException
     */
    public static Map updateModelMap(Twitter twitter, HttpServletRequest request, DropletService dropletService)
    {
        Map modelMap = getModelMap(request);

        try
        {
            Paging paging = new Paging().count(65);
            List<Status> statusListFriends = twitter.getFriendsTimeline(paging);

            List<Tweet> friendsList = Twitter4jAdapterUtil.getFormattedTweetListFromStatusList(statusListFriends);
            List<Tweet> replyList = new LinkedList<Tweet>();
            List<Tweet> dmList = new LinkedList<Tweet>();
            List<Tweet> dmSentList = new LinkedList<Tweet>();
            List<Tweet> sentList = new LinkedList<Tweet>();
            List<Tweet> favouritesList = new LinkedList<Tweet>();
            List<Tweet> retweetsList = new LinkedList<Tweet>();
            List<Tweet> discussionList = new LinkedList<Tweet>();

            modelMap.put("tweetList", friendsList);
            modelMap.put("friendsList", friendsList);
            modelMap.put("discussionList", discussionList);
            modelMap.put("replyList", replyList);
            modelMap.put("dmList", dmList);
            modelMap.put("dmSentList", dmSentList);
            modelMap.put("sentList", sentList);
            modelMap.put("favouritesList", favouritesList);
            modelMap.put("retweetList", retweetsList);


            User user = new User(twitter.verifyCredentials());
            user.setLatest_tweet_id(friendsList.get(0).getId());
            dropletService.persistUser(user);
            modelMap.put("user", user);

        } catch (TwitterException ex)
        {
            request.getSession().invalidate();
            DLog.log(ex.getMessage());
        }
        return modelMap;
    }

    /**
     *
     * @param request
     * @return
     */
    public static Map getModelMap(HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);

        if (session.getAttribute("modelMap") == null)
        {
            session.setAttribute("modelMap", new HashMap(0));
        }
        return (Map) session.getAttribute("modelMap");
    }

    /**
     *
     * @param request
     * @param modelMap
     * @return
     */
    public static Map saveModelMap(HttpServletRequest request, Map modelMap)
    {
        HttpSession session = request.getSession();
        session.setAttribute("modelMap", modelMap);
        return modelMap;
    }

    public static Tweet getTweetFromSession(Long tweetId, String listType, Map modelMap, DropletService dropletService)
    {
        Tweet trackTweet = null;

        if (listType.equals("conversationList"))
        {
            List<Conversation> conversationList = dropletService.getAllConversationsByUserId(((User) modelMap.get("user")).getId());
            if (conversationList != null)
            {
                for (Conversation con : conversationList)
                {
                    if (con.getTweet().getId().equals(tweetId))
                    {
                        trackTweet = con.getTweet();
                        trackTweet.setTracked(Boolean.TRUE);
                        break;
                    }
                }
            }
        } else
        {
            List<Tweet> tweetList = (List<Tweet>) modelMap.get(listType);
            if (tweetList != null)
            {
                for (Tweet tweet : tweetList)
                {
                    if (tweet.getId().equals(tweetId))
                    {
                        trackTweet = tweet;
                        break;
                    }
                }
            }
        }
        return trackTweet;
    }
    /*******************************************************************************/
    /*END MODEL MAP METHODS*/
    /*******************************************************************************/
}
