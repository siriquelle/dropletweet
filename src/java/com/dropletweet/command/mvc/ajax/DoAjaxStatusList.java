/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.ajax;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.tweet.list.GetDiscussionTweets;
import com.dropletweet.command.tweet.list.GetFormattedDropletTweetListFromDropletTweetList;
import com.dropletweet.command.tweet.list.SetFavouriteTweets;
import com.dropletweet.command.tweet.list.SetPrettyTime;
import com.dropletweet.command.tweet.list.SetRetweetTweets;
import com.dropletweet.command.tweet.list.SetTrackedTweets;
import com.dropletweet.command.twitter.conversation.GetDropletTweetListFromTwitter4jDirectMessages;
import com.dropletweet.command.twitter.conversation.GetDropletTweetListFromTwitter4jListOfTweets;
import com.dropletweet.command.twitter.conversation.GetFormattedDropletTweetListFromTwitter4jStatusList;
import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.mvc.DropletController;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class DoAjaxStatusList {

    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX TWEET LIST**/
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DO AJAX STATUS LIST - DOCS">
    /**
     *
     * @param type
     * @param request
     * @return
     */// </editor-fold>
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService) throws UnsupportedEncodingException
    // <editor-fold defaultstate="collapsed" desc="DO AJAX STATUS LIST - Handles ajaxian requests that relate to status list management">
    {
        Map modelMap = GetModelMap.run(request);
        User user = null;
        Twitter twitter = null;
        HttpSession session = null;
        String action = null;
        String listType = null;
        List<Status> statusList = null;
        List<Tweet> oldList = null;
        List<Conversation> conversationList = null;
        List<Tweet> tweetList = new LinkedList<Tweet>();
        Paging paging = new Paging().count(65);

        try
        {
            session = request.getSession();
            twitter = (Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER);
            user = (User) modelMap.get(AppValues.MODELMAP_KEY_USER);

            action = request.getParameter(AppValues.REQUEST_KEY_ACTION);

            if (action.equals(AppValues.LIST_NAME_FRIENDS_LIST))
            {
                listType = AppValues.LIST_NAME_FRIENDS_LIST;
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getFriendsTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getFriendsTimeline(paging);

            } else if (action.equals(AppValues.LIST_NAME_REPLY_LIST))
            {
                listType = AppValues.LIST_NAME_REPLY_LIST;
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getMentions(new Paging(oldList.get(0).getId()))
                        : twitter.getMentions(paging);
            } else if (action.equals(AppValues.LIST_NAME_DM_LIST))
            {
                listType = AppValues.LIST_NAME_DM_LIST;
                oldList = (List<Tweet>) modelMap.get(listType);

                List<Tweet> dmSentList = new LinkedList<Tweet>();

                tweetList = (oldList.size() > 0)
                        ? GetDropletTweetListFromTwitter4jDirectMessages.run(twitter.getDirectMessages(new Paging(oldList.get(0).getId())))
                        : GetDropletTweetListFromTwitter4jDirectMessages.run(twitter.getDirectMessages(paging.count(30)));

                dmSentList = (oldList.size() > 0)
                        ? GetDropletTweetListFromTwitter4jDirectMessages.run(twitter.getSentDirectMessages(new Paging(oldList.get(0).getId())))
                        : GetDropletTweetListFromTwitter4jDirectMessages.run(twitter.getSentDirectMessages(paging.count(30)));

                tweetList.addAll(dmSentList);
            } else if (action.equals(AppValues.LIST_NAME_FAVOURITES_LIST))
            {
                listType = AppValues.LIST_NAME_FAVOURITES_LIST;
                tweetList = (List<Tweet>) modelMap.get(listType);
            } else if (action.equals(AppValues.LIST_NAME_SENT_LIST))
            {
                listType = AppValues.LIST_NAME_SENT_LIST;
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getUserTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getUserTimeline(paging);

            } else if (action.contains(AppValues.LIST_NAME_SEARCH))
            {
                listType = AppValues.LIST_NAME_SEARCH;
                String query = request.getParameter(AppValues.REQUEST_KEY_Q);

                if (query.contains(AppValues.Q_VALUE_FROM))
                {
                    statusList = new LinkedList<twitter4j.Status>();
                    String screenName = query.substring(AppValues.Q_VALUE_FROM.length(), query.length());
                    statusList.addAll(twitter.getUserTimeline(screenName));

                } else
                {

                    List<twitter4j.Tweet> tl = twitter.search(new Query(query).rpp(100)).getTweets();
                    if (tl != null)
                    {
                        tweetList = GetDropletTweetListFromTwitter4jListOfTweets.run(tl);
                    }
                }

            } else if (action.equals(AppValues.LIST_NAME_CONVERSATION_LIST))
            {
                conversationList = dropletService.getAllConversationsByUserId(user.getId());

                if (conversationList != null && conversationList.size() > 0)
                {
                    for (Conversation con : conversationList)
                    {
                        tweetList.add(con.getTweet());
                    }
                }
            } else if (action.equals(AppValues.LIST_NAME_DISCUSSION_LIST))
            {
                listType = AppValues.LIST_NAME_DISCUSSION_LIST;

                oldList = (List<Tweet>) modelMap.get(AppValues.LIST_NAME_FRIENDS_LIST);
                statusList = (oldList.size() > 0)
                        ? twitter.getFriendsTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getFriendsTimeline(paging);
                List<Tweet> newList = GetFormattedDropletTweetListFromTwitter4jStatusList.run(statusList);
                newList.addAll(oldList);
                modelMap.put(AppValues.LIST_NAME_FRIENDS_LIST, newList);

                tweetList = GetDiscussionTweets.run(newList);

                statusList = null;
                oldList = null;
            } else if (action.equals(AppValues.ACTION_VALUE_MORE))
            {
                listType = request.getParameter(AppValues.REQUEST_KEY_LIST_TYPE);

                if (listType != null)
                {
                    if (listType.contains(AppValues.LIST_NAME_SEARCH))
                    {
                        oldList = (List<Tweet>) modelMap.get(AppValues.LIST_NAME_SEARCH);
                    } else
                    {
                        oldList = (List<Tweet>) modelMap.get(listType);
                    }

                    if (oldList != null)
                    {
                        if (listType.equals(AppValues.LIST_NAME_FRIENDS_LIST))
                        {
                            tweetList = GetFormattedDropletTweetListFromTwitter4jStatusList.run(twitter.getFriendsTimeline(new Paging().count(30).maxId(oldList.get(oldList.size() - 1).getId() - 1)));
                        } else if (listType.equals(AppValues.LIST_NAME_SENT_LIST))
                        {
                            tweetList = GetFormattedDropletTweetListFromTwitter4jStatusList.run(twitter.getUserTimeline(new Paging().count(30).maxId(oldList.get(oldList.size() - 1).getId() - 1)));
                        } else if (listType.contains(AppValues.LIST_NAME_SEARCH))
                        {
                            String q = listType.substring(AppValues.LIST_NAME_SEARCH.concat(AppValues.VALUE_SEPARATOR_UNDERSCORE).length());
                            if (q != null)
                            {
                                List<twitter4j.Tweet> tl = twitter.search(new Query(q).rpp(20).maxId(oldList.get(oldList.size() - 1).getId() - 1)).getTweets();
                                if (tl != null)
                                {
                                    tweetList = GetDropletTweetListFromTwitter4jListOfTweets.run(tl);
                                }
                            }
                        }
                    }
                }
            }


            if (statusList != null && statusList.size() > 0)
            {
                tweetList = GetFormattedDropletTweetListFromTwitter4jStatusList.run(statusList);
            } else if (tweetList != null && tweetList.size() > 0)
            {
                if (action != null)
                {
                    tweetList = GetFormattedDropletTweetListFromDropletTweetList.run(tweetList);
                }
            }
            if (oldList != null && oldList.size() > 0)
            {
                tweetList.addAll(oldList);
            }

            if (tweetList != null && tweetList.size() > 0)
            {
                tweetList = SetTrackedTweets.run(tweetList, dropletService.getAllConversationsByUserId(user.getId()));
                tweetList = SetFavouriteTweets.run(tweetList, (List<Tweet>) modelMap.get(AppValues.LIST_NAME_FAVOURITES_LIST));

                tweetList = SetRetweetTweets.run(tweetList, (List<Tweet>) modelMap.get(AppValues.LIST_NAME_RETWEET_LIST));
                tweetList = SetPrettyTime.run(tweetList);
                Collections.sort(tweetList);
                Collections.reverse(tweetList);

            }
            dropletService.persistTweetList(tweetList);
        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
        }

        modelMap.put(AppValues.MODELMAP_KEY_USER, user);
        modelMap.put(AppValues.LIST_NAME_TWEET_LIST, tweetList);
        modelMap.put(listType, tweetList);
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_STATUSLIST);
        return modelMap;
    }
    // </editor-fold>
    /*******************************************************************************/
    /*END AJAX TWEET LIST*/
    /*******************************************************************************/
    /**                                                                           **/
}
