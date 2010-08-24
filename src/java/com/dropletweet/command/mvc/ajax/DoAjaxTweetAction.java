/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.mvc.ajax;

import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.modelmap.GetTweetFromSession;
import com.dropletweet.command.tweet.Clean;
import com.dropletweet.command.tweet.GetDateAsPrettyTime;
import com.dropletweet.command.tweet.RemoveTweetFromListByValue;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.log.DLog;
import com.dropletweet.model.bean.AjaxTweetActionBean;
import com.dropletweet.mvc.DropletController;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author Siriquelle
 */
public class DoAjaxTweetAction {

    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX TWEET ACTIONS*/
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DO AJAX TWEET ACTION - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */// </editor-fold>
    public static Map run(HttpServletRequest request, HttpServletResponse response, DropletProperties dropletProperties, DropletService dropletService) throws UnsupportedEncodingException
    // <editor-fold defaultstate="collapsed" desc="DO AJAX TWEET ACTION - Handles ajaxian requests that relate to individual tweet actions">
    {
        AjaxTweetActionBean ajaxTweetActionBean = new AjaxTweetActionBean();
        Map modelMap = GetModelMap.run(request);
        User user = (User) modelMap.get(AppValues.MODELMAP_KEY_USER);
        String action = request.getParameter(AppValues.REQUEST_KEY_ACTION);

        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER);
        Tweet tweet = null;

        try
        {
            if (action.equals(AppValues.TWEET_ACTION_VALUE_RETWEET))
            {
                String tweetId = request.getParameter(AppValues.REQUEST_KEY_TWEET_ID);
                DLog.log(Long.valueOf(tweetId).toString());
                tweet = new Tweet(twitter.retweetStatus(Long.valueOf(tweetId)));

                List<Tweet> retweetList = (List<Tweet>) modelMap.get(AppValues.LIST_NAME_RETWEET_LIST);
                retweetList.add(tweet);
                modelMap.put(AppValues.LIST_NAME_RETWEET_LIST, retweetList);

            } else if (action.equals(AppValues.TWEET_ACTION_VALUE_POST))
            {
                String tweet_text = request.getParameter(AppValues.REQUEST_KEY_TWEET_TEXT);

                String in_reply_to_id = request.getParameter(AppValues.REQUEST_KEY_IN_REPLY_TO_ID);
                if (in_reply_to_id.length() > 0)
                {
                    tweet = new Tweet(twitter.updateStatus(tweet_text, Long.valueOf(in_reply_to_id)));
                    dropletService.persistTweet(tweet);
                    //
                    ajaxTweetActionBean.setId(String.valueOf(in_reply_to_id));
                    ajaxTweetActionBean.setName(tweet.getTo_user());
                } else
                {
                    tweet = new Tweet(twitter.updateStatus(tweet_text));
                }

            } else if (action.equals(AppValues.TWEET_ACTION_VALUE_DELETE))
            {
                String tweetId = request.getParameter(AppValues.REQUEST_KEY_TWEET_ID);
                Status status = null;
                if (tweetId.length() > 0)
                {
                    status = twitter.destroyStatus(Long.valueOf(tweetId));
                    tweet = new Tweet(status);
                    Conversation con = dropletService.getConversationByUserIdTweetIdCombination(user.getId(), tweet.getId());
                    if (con != null)
                    {
                        dropletService.deleteConversation(con);
                    }
                    modelMap.put(AppValues.LIST_NAME_TWEET_LIST, RemoveTweetFromListByValue.run((List<Tweet>) modelMap.get(AppValues.LIST_NAME_TWEET_LIST), tweet));
                    modelMap.put(AppValues.LIST_NAME_FRIENDS_LIST, RemoveTweetFromListByValue.run((List<Tweet>) modelMap.get(AppValues.LIST_NAME_FRIENDS_LIST), tweet));
                    modelMap.put(AppValues.LIST_NAME_SENT_LIST, RemoveTweetFromListByValue.run((List<Tweet>) modelMap.get(AppValues.LIST_NAME_SENT_LIST), tweet));
                    tweet = Clean.run(tweet);
                    if (dropletService.getTweetById(tweet.getId()) != null)
                    {
                        dropletService.persistTweet(tweet);
                    }
                    ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.delete.complete"));
                }
            } else if (action.equals(AppValues.TWEET_ACTION_VALUE_FAVOURITE))
            {
                String tweetId = request.getParameter(AppValues.REQUEST_KEY_TWEET_ID);
                Status status = null;
                if (tweetId.length() > 0)
                {
                    List<Tweet> favouritesList = (List<Tweet>) modelMap.get(AppValues.LIST_NAME_FAVOURITES_LIST);
                    try
                    {
                        status = twitter.createFavorite(Long.valueOf(tweetId));
                        tweet = new Tweet(status);
                        favouritesList.add(tweet);

                    } catch (TwitterException twitterException)
                    {
                        status = twitter.destroyFavorite(Long.valueOf(tweetId));
                        tweet = new Tweet(status);
                        if (favouritesList != null)
                        {
                            for (Tweet t : favouritesList)
                            {
                                if (t.getId().equals(tweet.getId()))
                                {
                                    favouritesList.remove(t);
                                    break;
                                }
                            }
                        }
                    }
                    modelMap.put(AppValues.LIST_NAME_FAVOURITES_LIST, favouritesList);
                }

                ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.favourite.complete"));
            } else if (action.equals(AppValues.TWEET_ACTION_VALUE_SPAM))
            {
                String userId = request.getParameter(AppValues.REQUEST_KEY_USER_ID);
                if (userId.length() > 0)
                {
                    twitter.reportSpam(Integer.parseInt(userId));
                    ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.spam.complete"));
                }
            } else if (action.equals(AppValues.TWEET_ACTION_VALUE_TRACK))
            {
                String tweetId = request.getParameter(AppValues.REQUEST_KEY_TWEET_ID);
                String listType = request.getParameter(AppValues.REQUEST_KEY_LIST_TYPE);
                if (tweetId.length() > 0)
                {
                    tweet = GetTweetFromSession.run(Long.valueOf(tweetId), listType, modelMap, dropletService);
                    if (tweet != null)
                    {
                        if (tweet.getTracked() == true)
                        {
                            List<Conversation> conversationList = dropletService.getAllConversationsByUserId(((User) modelMap.get(AppValues.MODELMAP_KEY_USER)).getId());
                            if (conversationList != null)
                            {
                                for (Conversation con : conversationList)
                                {
                                    if (con.getTweet().getId().equals(tweet.getId()))
                                    {
                                        dropletService.deleteConversation(con);
                                        ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.track.deleted"));
                                    }
                                }
                            }
                        } else
                        {
                            try
                            {
                                dropletService.persistTweet(tweet);
                                dropletService.persistConversation(new Conversation((User) modelMap.get(AppValues.MODELMAP_KEY_USER), tweet));
                            } catch (Exception e)
                            {
                                DLog.log(e.getMessage());
                            }
                            ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.track.complete"));
                        }
                    }
                }
            } else if (action.equals(AppValues.TWEET_ACTION_VALUE_FOLLOW))
            {
                String screen_name = request.getParameter(AppValues.REQUEST_KEY_SCREEN_NAME);
                if (screen_name != null && screen_name.length() > 0)
                {
                    try
                    {
                        twitter.createFriendship(screen_name);
                        ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.follow.complete"));
                    } catch (TwitterException twitterException)
                    {
                        DLog.log(twitterException.getMessage());
                        twitter.destroyFriendship(screen_name);
                        ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.unfollow.complete"));
                    }
                } else
                {
                    ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.follow.failed"));
                }
            }
        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
            tweet = null;
            ajaxTweetActionBean.setError(dropletProperties.getProperty("droplet.ajax.action.error.common"));
        }

        if (tweet != null && tweet.getCreated_at() != null && !action.equals("conversations"))
        {
            ajaxTweetActionBean.setLink(tweet.getSource());
            ajaxTweetActionBean.setText(SwapAllForLinks.run(tweet.getText()));
            try
            {
                ajaxTweetActionBean.setTime(GetDateAsPrettyTime.run(tweet.getCreated_at()));
            } catch (Exception e)
            {
                DLog.log(e.toString());
            }
        }
        modelMap.put(AppValues.MODELMAP_KEY_AJAX_TWEET_ACTION_BEAN, ajaxTweetActionBean);
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_ACTIONS);
        return modelMap;
    }// </editor-fold>
    /*******************************************************************************/
    /*END AJAX TWEET ACTIONS*/
    /*******************************************************************************/
    /**                                                                           **/
}
