/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.model.bean.AjaxTweetActionBean;
import com.dropletweet.model.bean.AjaxUserBean;
import com.dropletweet.model.bean.AjaxUtilBean;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import com.dropletweet.log.DLog;
import com.dropletweet.command.cookie.DestroyCookie;
import com.dropletweet.command.cookie.GetCookieValue;
import com.dropletweet.command.twitter.GetAuthorizationURL;
import com.dropletweet.command.twitter.GetAuthorizedTwitter;
import com.dropletweet.command.twitter.GetAuthorizedTwitterFromKeySecret;
import com.dropletweet.command.modelmap.GetModelMap;
import com.dropletweet.command.modelmap.GetTweetFromSession;
import com.dropletweet.command.modelmap.SaveModelMap;
import com.dropletweet.command.modelmap.UpdateModelMap;
import com.dropletweet.command.tweet.Clean;
import com.dropletweet.command.tweet.GetDateAsPrettyTime;
import com.dropletweet.command.tweet.RemoveTweetFromListByValue;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.command.tweet.list.GetDiscussionTweets;
import com.dropletweet.command.tweet.list.SetFavouriteTweets;
import com.dropletweet.command.tweet.list.SetPrettyTime;
import com.dropletweet.command.tweet.list.SetRetweetTweets;
import com.dropletweet.command.tweet.list.SetTrackedTweets;
import com.dropletweet.command.twitter.conversation.GetDropletTweetListFromTwitter4jDirectMessages;
import com.dropletweet.command.twitter.conversation.GetDropletTweetListFromTwitter4jListOfTweets;
import com.dropletweet.command.twitter.conversation.GetFormattedDropletTweetListFromTwitter4jStatusList;
import com.dropletweet.command.tweet.list.GetFormattedDropletTweetListFromDropletTweetList;
import com.dropletweet.constants.AppValues;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class DropletController extends AbstractController {
    // <editor-fold defaultstate="collapsed" desc="HANDLE REQUEST INTERNAL - DOCS">

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */// </editor-fold>
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    // <editor-fold defaultstate="collapsed" desc="HANDLE REQUEST INTERNAL - Performs delegation of requests process">
    {
        request.setCharacterEncoding("UTF-8");
//**                                                                        **//
        DLog.log("START GETTING USERS SESSION");
        Map modelMap = GetModelMap.run(request);
        DLog.log("END GETTING USERS SESSION");
//**                                                                        **//
        DLog.log("START SETTING UP DEFAULT DESTINATION");
        String view = AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX;
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, view);
        DLog.log("END SETTING UP DEFAULT DESTINATION");
//**                                                                        **//
        DLog.log("START PROCESSING REQUEST");
        String uri = request.getRequestURI();
        if (uri.contains(AppValues.VIEW_INDEX_NAME))
        {
            modelMap.putAll(doIndexView(request, response));
        } else if (uri.contains(AppValues.VIEW_DROPLET_NAME))
        {
            modelMap.putAll(doDropletView(request, response));
        } else if (uri.contains(AppValues.VIEW_SIGNIN_NAME))
        {
            modelMap.putAll(doSigninView(request, response));
        } else if (uri.contains(AppValues.AJAX_STATUSLIST_NAME))
        {
            modelMap.putAll(doAjaxStatusList(request, response));
        } else if (uri.contains(AppValues.AJAX_TWEET_NAME))
        {
            modelMap.putAll(doAjaxTweetAction(request, response));
        } else if (uri.contains(AppValues.AJAX_USER_NAME))
        {
            modelMap.putAll(doAjaxUser(request, response));
        } else if (uri.contains(AppValues.AJAX_UTIL_NAME))
        {
            modelMap.putAll(doAjaxUtil(request, response));
        }
        DLog.log("END PROCESSING REQUEST");
//**                                                                        **//
        DLog.log("START SAVING MODEL MAP");
        modelMap.putAll(SaveModelMap.run(request, modelMap));
        DLog.log("END SAVING MODEL MAP");
//**                                                                        **//
        return new ModelAndView((String) modelMap.get(AppValues.MODELMAP_KEY_VIEW), AppValues.MODELMAP_KEY_MODELMAP, modelMap);
    }// </editor-fold>

    /*******************************************************************************/
    /*START PAGE VIEW REQUEST HANDLERS*/
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DO INDEX VIEW - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws Exception
     */// </editor-fold>
    private Map doIndexView(HttpServletRequest request, HttpServletResponse response) throws Exception
    // <editor-fold defaultstate="collapsed" desc="DO INDEX VIEW - Controls the default entry point process">
    {
//**                                                                        **//
        Map modelMap = GetModelMap.run(request);
        HttpSession session = request.getSession();
        if (session.getAttribute(AppValues.SESSION_KEY_USER) != null)
        {
            modelMap.putAll(this.doDropletView(request, response));
        } else
        {
            if (GetCookieValue.run(request.getCookies(), AppValues.COOKIE_KEY_ACCESS_TOKEN) != null)
            {
                modelMap.putAll(this.doSigninView(request, response));
            } else
            {
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.VIEW_VALUE_INDEX);
            }
        }
//**                                                                        **//
        return modelMap;
    }// </editor-fold>

    /**                                                                           **/
    // <editor-fold defaultstate="collapsed" desc="DO SIGN IN VIEW - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws Exception
     */// </editor-fold>
    private Map doSigninView(HttpServletRequest request, HttpServletResponse response) throws Exception
    // <editor-fold defaultstate="collapsed" desc="DO SIGN IN VIEW - Controles the signiture process">
    {
        Map modelMap = GetModelMap.run(request);
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX);
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        String accessToken = AppValues.COOKIE_KEY_ACCESS_TOKEN;
        if (request.getParameter(AppValues.REQUEST_KEY_LOGOUT) == null)
        {

            accessToken = GetCookieValue.run(cookies, accessToken);
            accessToken = (accessToken == null) ? AppValues.COOKIE_KEY_ACCESS_TOKEN : accessToken;
            if (!accessToken.equals(AppValues.COOKIE_KEY_ACCESS_TOKEN))
            {
                String tokenKey = accessToken.substring(0, accessToken.indexOf(AppValues.VALUE_SEPARATOR_UNDERSCORE));
                String tokenSecret = accessToken.substring(accessToken.indexOf(AppValues.VALUE_SEPARATOR_UNDERSCORE) + 1, accessToken.length());
                modelMap.putAll(UpdateModelMap.run(GetAuthorizedTwitterFromKeySecret.run(request, tokenKey, tokenSecret, dropletProperties), request, dropletService));
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_DROPLET);
            } else if ((RequestToken) session.getAttribute(AppValues.SESSION_KEY_REQUEST_TOKEN) == null)
            {
                String authorizationURL = GetAuthorizationURL.run(session, dropletProperties).toExternalForm();
                if (authorizationURL != null)
                {
                    modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_PREFIX + authorizationURL);
                }
            } else
            {
                modelMap.putAll(UpdateModelMap.run(GetAuthorizedTwitter.run(request, response), request, dropletService));
                modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_DROPLET);
            }

        } else
        {
            session.invalidate();
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

            response.addCookie(DestroyCookie.run(cookies, accessToken));

            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX);
        }
        return modelMap;
    }// </editor-fold>

    /**                                                                           **/
    // <editor-fold defaultstate="collapsed" desc="DO DROPLET VIEW - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws TwitterException
     */// </editor-fold>
    private Map doDropletView(HttpServletRequest request, HttpServletResponse response) throws TwitterException
    // <editor-fold defaultstate="collapsed" desc="DO DROPLET VIEW - Performs post sign in processes">
    {
        Map modelMap = GetModelMap.run(request);

        HttpSession session = request.getSession();
        if ((Map) session.getAttribute(AppValues.SESSION_KEY_MODELMAP) == null || ((Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER) == null))
        {
            request.getSession().invalidate();
            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.MODELMAP_VIEW_VALUE_REDIRECT_INDEX);
        } else
        {
            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.VIEW_VALUE_DROPLET);
        }

        return modelMap;
    }// </editor-fold>

    /**                                                                           **/
    /*******************************************************************************/
    /*END PAGE VIEW REQUEST HANDLERS*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX HANDLERS*/
    /*******************************************************************************/
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
    private Map doAjaxStatusList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException
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
    public Map doAjaxTweetAction(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException
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
                tweet = new Tweet(twitter.retweetStatus(Long.valueOf(tweetId)).getRetweetedStatus());

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
    /*******************************************************************************/
    /*START AJAX UTIL*/
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DO AJAX UTIL - DOCS">
    /**
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */// </editor-fold>
    public Map doAjaxUtil(HttpServletRequest request, HttpServletResponse response)
    // <editor-fold defaultstate="collapsed" desc="DO AJAX UTIL - Handles ajaxian requests that seek utilities">
    {
        String action = request.getParameter(AppValues.REQUEST_KEY_ACTION);
        Map modelMap = GetModelMap.run(request);
        AjaxUtilBean ajaxUtilBean = new AjaxUtilBean();

        if (action.equals(AppValues.UTIL_ACTION_VALUE_GET_LATEST_URL))
        {
            String url = dropletProperties.getProperty("droplet.ajax.util.default.url");
            if ((User) modelMap.get(AppValues.MODELMAP_KEY_USER) != null)
            {
                User user = (User) modelMap.get(AppValues.MODELMAP_KEY_USER);
                Conversation latestConversation = (dropletService.getAllConversationsByUserId(user.getId()).size() > 0)
                        ? dropletService.getAllConversationsByUserId(user.getId()).get(dropletService.getAllConversationsByUserId(user.getId()).size() - 1)
                        : null;

                if (latestConversation != null)
                {
                    url = "http://twitter.com/" + latestConversation.getTweet().getFrom_user() + "/status/" + latestConversation.getTweet().getId();
                }
            } else
            {
                if ((String) this.getServletContext().getAttribute(AppValues.SERVLET_CONTEXT_KEY_LATEST_URL) != null)
                {
                    url = (String) this.getServletContext().getAttribute(AppValues.SERVLET_CONTEXT_KEY_LATEST_URL);
                }
            }
            ajaxUtilBean.setMessage(url);
        }

        modelMap.put(AppValues.MODELMAP_KEY_AJAX_UTIL_BEAN, ajaxUtilBean);
        modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_UTIL);
        return modelMap;
    }// </editor-fold>

    /*******************************************************************************/
    /*END AJAX UTIL*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX USER*/
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DO AJAX USER - DOCS">
    /**
     *
     * @param request
     * @param response
     * @return
     */// </editor-fold>
    public Map doAjaxUser(HttpServletRequest request, HttpServletResponse response)
    // <editor-fold defaultstate="collapsed" desc="DO AJAX USER - Handles ajaxian requests that relate to a user">
    {
        String action = request.getParameter(AppValues.REQUEST_KEY_ACTION);
        Map modelMap = GetModelMap.run(request);
        AjaxUserBean ajaxUserBean = null;
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute(AppValues.SESSION_KEY_TWITTER);

        if (action.equals(AppValues.USER_ACTION_VALUE_GET_USER_INFO))
        {
            String userName = request.getParameter(AppValues.REQUEST_KEY_SCREEN_NAME);
            if (userName != null && userName.length() > 0)
            {
                try
                {
                    twitter4j.User user = null;

                    if (userName.equals(((User) modelMap.get(AppValues.MODELMAP_KEY_USER)).getScreen_name()))
                    {
                        user = (twitter4j.User) twitter.verifyCredentials();
                    } else
                    {
                        user = (twitter4j.User) twitter.showUser(userName);
                    }

                    ajaxUserBean = new AjaxUserBean((twitter4j.User) user);

                } catch (TwitterException ex)
                {
                    DLog.log(ex.getMessage());
                }
            }
            modelMap.put(AppValues.MODELMAP_KEY_VIEW, AppValues.AJAX_OUT_LOCATION_USER);
        }
        modelMap.put(AppValues.MODELMAP_KEY_AJAX_USER_BEAN, ajaxUserBean);
        return modelMap;
    }// </editor-fold>
    /*******************************************************************************/
    /*END AJAX USER*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*END AJAX HANDLERS*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START DEPENDANCY INJECTION */
    /*******************************************************************************/
    // <editor-fold defaultstate="collapsed" desc="DEPENDANCY INJECTION OBJECTS">
    protected DropletService dropletService;
    protected DropletProperties dropletProperties;

    /**
     * Set the value of rssService
     *
     * @param dropletService
     */
    public void setDropletService(DropletService dropletService)
    {
        this.dropletService = dropletService;
    }

    public void setDropletProperties(DropletProperties dropletProperties)
    {
        this.dropletProperties = dropletProperties;
    }
    // </editor-fold>
    /*******************************************************************************/
    /*END DEPENDANCY INJECTION */
    /*******************************************************************************/
}
