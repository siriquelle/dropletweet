/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.model.bean.AjaxTweetActionBean;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import com.dropletweet.util.DLog;
import com.dropletweet.util.TweetUtil;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class DropletController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
//
        DLog.log("GET USERS SESSION");
        Map modelMap = this.getModelMap(request);
//
        DLog.log("SET UP DEFAULT DESTINATION");
        String view = "redirect:/index.jsp";
        modelMap.put("view", view);
//
        DLog.log("PROCESS REQUESTS");
        String uri = request.getRequestURI();
        if (uri.contains("index.htm"))
        {
            modelMap.putAll(doIndexView(request));
        } else if (uri.contains("droplet.htm"))
        {
            modelMap.putAll(doDropletView(request));
        } else if (uri.contains("signin.htm"))
        {
            modelMap.putAll(doSigninView(request));
        } else if (uri.contains("statuslist.ajax"))
        {
            modelMap.putAll(doAjaxStatusList(request));
        } else if (uri.contains("tweet.ajax"))
        {
            modelMap.putAll(doAjaxTweetAction(request));
        }
//
        modelMap.putAll(saveModelMap(request, modelMap));
//
        return new ModelAndView((String) modelMap.get("view"), "modelMap", modelMap);
    }

    /*******************************************************************************/
    /*START PAGE VIEW REQUEST HANDLERS*/
    /*******************************************************************************/
    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map doIndexView(HttpServletRequest request) throws Exception
    {
        Map modelMap = getModelMap(request);
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null)
        {
            this.doDropletView(request);
        }
        modelMap.put("view", "index");
        return modelMap;

    }

    /**
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private Map doSigninView(HttpServletRequest request) throws Exception
    {
        Map modelMap = getModelMap(request);
        modelMap.put("view", "redirect:index.htm");
        HttpSession session = request.getSession();
        if (request.getParameter("logout") == null)
        {
            if ((RequestToken) session.getAttribute("requestToken") == null)
            {
                String authorizationURL = this.getAuthorizationURL(session).toExternalForm();
                if (authorizationURL != null)
                {
                    modelMap.put("view", "redirect:" + authorizationURL);
                }
            } else
            {
                modelMap.putAll(updateModelMap(this.getAuthorizedTwitter(session), request));
                modelMap.put("view", "redirect:droplet.htm");
            }

        } else
        {
            session.invalidate();
            modelMap.put("view", "redirect:index.htm");
        }
        return modelMap;
    }

    /**
     *
     * @param request
     * @return
     * @throws TwitterException
     */
    private Map doDropletView(HttpServletRequest request) throws TwitterException
    {
        Map modelMap = getModelMap(request);

        HttpSession session = request.getSession();
        if ((Map) session.getAttribute("modelMap") == null || ((Twitter) session.getAttribute("twitter") == null))
        {
            request.getSession().invalidate();
            modelMap.put("view", "redirect:index.htm");
        } else
        {
            modelMap.put("view", "droplet");
        }

        return modelMap;
    }

    /*******************************************************************************/
    /*END PAGE VIEW REQUEST HANDLERS*/
    /*******************************************************************************/
    //
    /*******************************************************************************/
    /*START AJAX Request Handlers*/
    /*******************************************************************************/
    /**
     *
     * @param type
     * @param request
     * @return
     */
    private Map doAjaxStatusList(HttpServletRequest request)
    {
        Map modelMap = getModelMap(request);
        User user = null;
        Twitter twitter = null;
        HttpSession session = null;
        String action = null;
        String listType = null;
        List<Status> statusList = null;
        List<Tweet> tweetList = null;
        List<Tweet> oldList = null;
        List<Conversation> conversationList = null;
        try
        {
            session = request.getSession();
            twitter = (Twitter) session.getAttribute("twitter");
            user = new User(twitter.verifyCredentials());
            dropletService.persistUser(user);

            action = request.getParameter("action");

            if (action.equals("friendsList"))
            {
                listType = "friendsList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getFriendsTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getFriendsTimeline();

            } else if (action.equals("replyList"))
            {
                listType = "replyList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getMentions(new Paging(oldList.get(0).getId()))
                        : twitter.getMentions();
            } else if (action.equals("dms"))
            {
                listType = "dmList";
                oldList = (List<Tweet>) modelMap.get(listType);
                List<Tweet> dmSentList = (List<Tweet>) modelMap.get("dmSentList");
                tweetList = (oldList.size() > 0)
                        ? this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(new Paging(oldList.get(0).getId())))
                        : this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages());

                dmSentList = (dmSentList.size() > 0)
                        ? this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getSentDirectMessages(new Paging(dmSentList.get(0).getId())))
                        : this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getSentDirectMessages());

                modelMap.put("dmSentList", dmSentList);
                tweetList.addAll(dmSentList);
            } else if (action.equals("favouritesList"))
            {
                listType = "favouritesList";
                tweetList = (List<Tweet>) modelMap.get(listType);
            } else if (action.equals("sentList"))
            {
                listType = "sentList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getUserTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getUserTimeline();

            } else if (action.contains("search"))
            {
                listType = "search";
                String query = request.getParameter("q");
                List<twitter4j.Tweet> tl = twitter.search(new Query(query).rpp(100)).getTweets();
                if (tl != null)
                {
                    tweetList = this.getDropletTweetListFromTwitter4jListOfTweets(tl);
                }
            } else if (action.equals("conversationList"))
            {
                conversationList = dropletService.getAllConversationsByUserId(user.getId());
                tweetList = new LinkedList<Tweet>();
                if (conversationList != null && conversationList.size() > 0)
                {
                    for (Conversation con : conversationList)
                    {
                        tweetList.add(con.getTweet());
                    }
                }
            } else if (action.equals("more"))
            {
                listType = request.getParameter("listType");

                if (listType != null)
                {
                    if (listType.contains("search"))
                    {
                        oldList = (List<Tweet>) modelMap.get("search");
                    } else
                    {
                        oldList = (List<Tweet>) modelMap.get(listType);
                    }

                    if (oldList != null)
                    {
                        if (listType.equals("friendsList"))
                        {
                            tweetList = getFormattedTweetListFromStatusList(twitter.getFriendsTimeline(new Paging().count(20).maxId(oldList.get(oldList.size() - 1).getId() - 1)));
                        } else if (listType.equals("sentList"))
                        {
                            tweetList = getFormattedTweetListFromStatusList(twitter.getUserTimeline(new Paging().count(20).maxId(oldList.get(oldList.size() - 1).getId() - 1)));
                        } else if (listType.contains("search"))
                        {
                            String q = listType.substring("search_".length());
                            if (q != null)
                            {
                                List<twitter4j.Tweet> tl = twitter.search(new Query(q).rpp(20).maxId(oldList.get(oldList.size() - 1).getId() - 1)).getTweets();
                                if (tl != null)
                                {
                                    tweetList = this.getDropletTweetListFromTwitter4jListOfTweets(tl);
                                }
                            }
                        }
                    }
                }
            }


            if (statusList != null)
            {
                tweetList = getFormattedTweetListFromStatusList(statusList);
            } else if (tweetList != null)
            {
                if (action != null)
                {
                    tweetList = getFormattedTweetListFromTweetList(tweetList);
                }
            }
            if (oldList != null)
            {
                tweetList.addAll(oldList);
            }

            if (tweetList != null)
            {
                tweetList = setTrackedTweets(tweetList, modelMap);
                tweetList = setFavouriteTweets(tweetList, modelMap);
                tweetList = setRetweetTweets(tweetList, modelMap);
                Collections.sort(tweetList);
                Collections.reverse(tweetList);
            }

        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
        }

        modelMap.put("user", user);
        modelMap.put("tweetList", tweetList);
        modelMap.put(listType, tweetList);
        modelMap.put("view", "ajax/statuslist");
        return modelMap;
    }

    /**
     *
     */
    public Map doAjaxTweetAction(HttpServletRequest request)
    {
        AjaxTweetActionBean ajaxTweetActionBean = new AjaxTweetActionBean();
        Map modelMap = getModelMap(request);
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute("twitter");
        Tweet tweet = null;

        try
        {
            if (action.equals("retweet"))
            {
                String tweetId = request.getParameter("tweetId");
                tweet = new Tweet(twitter.retweetStatus(Long.valueOf(tweetId)));

                List<Tweet> retweetList = (List<Tweet>) modelMap.get("retweetList");
                retweetList.add(tweet);
                modelMap.put("retweetList", retweetList);

            } else if (action.equals("post"))
            {
                String tweet_text = request.getParameter("tweet_text");
                String in_reply_to_id = request.getParameter("in_reply_to_id");
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

            } else if (action.equals("delete"))
            {
                String tweetId = request.getParameter("tweetId");
                Status status = null;
                if (tweetId.length() > 0)
                {
                    status = twitter.destroyStatus(Long.valueOf(tweetId));
                    tweet = new Tweet(status);

                    modelMap.put("tweetList", TweetUtil.removeTweetFromListByValue((List<Tweet>) modelMap.get("tweetList"), tweet));
                    modelMap.put("friendsList", TweetUtil.removeTweetFromListByValue((List<Tweet>) modelMap.get("friendsList"), tweet));
                    modelMap.put("sentList", TweetUtil.removeTweetFromListByValue((List<Tweet>) modelMap.get("sentList"), tweet));
                    tweet = TweetUtil.clean(tweet);
                    if (dropletService.getTweetById(tweet.getId()) != null)
                    {
                        dropletService.persistTweet(tweet);
                    }
                    ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.delete.complete"));
                }
            } else if (action.equals("favourite"))
            {
                String tweetId = request.getParameter("tweetId");
                Status status = null;
                if (tweetId.length() > 0)
                {
                    List<Tweet> favouritesList = (List<Tweet>) modelMap.get("favouritesList");
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
                    modelMap.put("favouritesList", favouritesList);
                }

                ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.favourite.complete"));
            } else if (action.equals("spam"))
            {
                String userId = request.getParameter("userId");
                if (userId.length() > 0)
                {
                    twitter.reportSpam(Integer.parseInt(userId));
                    ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.spam.complete"));
                }
            } else if (action.equals("track"))
            {
                String tweetId = request.getParameter("tweetId");
                String listType = request.getParameter("listType");
                if (tweetId.length() > 0)
                {
                    tweet = getTweetFromSession(Long.valueOf(tweetId), listType, modelMap);
                    if (tweet != null)
                    {
                        if (tweet.getTracked() == true)
                        {
                            List<Conversation> conversationList = dropletService.getAllConversationsByUserId(((User) modelMap.get("user")).getId());
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
                            dropletService.persistTweet(tweet);
                            dropletService.persistConversation(new Conversation((User) modelMap.get("user"), tweet));
                            ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.track.complete"));
                        }
                    }
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
            ajaxTweetActionBean.setText(TweetUtil.swapAllForLinks(tweet.getText()));
            try
            {
                ajaxTweetActionBean.setTime(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
            } catch (Exception e)
            {
                DLog.log(e.toString());
            }
        }
        modelMap.put("ajaxTweetActionBean", ajaxTweetActionBean);
        modelMap.put("view", "ajax/actions");
        return modelMap;
    }

    /*******************************************************************************/
    /*END AJAX Request Handlers*/
    /*******************************************************************************/
    //
    /*******************************************************************************/
    /*START OAUTH AND TWITTER METHODS*/
    /*******************************************************************************/
    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    private URL getAuthorizationURL(HttpSession session) throws Exception
    {

        session.removeAttribute("twitter");
        session.removeAttribute("requestToken");
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(dropletProperties.getProperty("twitter.oauth.application.key"),
                dropletProperties.getProperty("twitter.oauth.application.secret"));

        RequestToken requestToken = twitter.getOAuthRequestToken();
        String authorizationURL = requestToken.getAuthorizationURL();
        session.setAttribute("twitter", twitter);
        session.setAttribute("requestToken", requestToken);
        return new URL(authorizationURL);
    }

    /**
     * 
     * @param request
     * @return
     */
    private Twitter getAuthorizedTwitter(HttpSession session)
    {
        Twitter twitter = (Twitter) session.getAttribute("twitter");
        RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
        try
        {

            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(accessToken.getToken(), accessToken.getTokenSecret(), null));

            twitter.verifyCredentials();
            session.removeAttribute("requestToken");

        } catch (TwitterException e)
        {
            DLog.log(e.getMessage());
        }

        return twitter;
    }

    /*******************************************************************************/
    /*END OAUTH AND TWITTER METHODS*/
    /*******************************************************************************/
    //
    /*******************************************************************************/
    /*START MODEL MAP MANIPULATION METHODS*/
    /*******************************************************************************/
    /**
     *
     * @param twitter
     * @param session
     * @throws TwitterException
     */
    private Map updateModelMap(Twitter twitter, HttpServletRequest request)
    {
        Map modelMap = getModelMap(request);

        try
        {
            Paging paging = new Paging().count(45);
            User user = new User(twitter.verifyCredentials());
            List<Status> statusListFriends = twitter.getFriendsTimeline(paging);
            List<Tweet> friendsList = getFormattedTweetListFromStatusList(statusListFriends);
            DLog.sleep();
            List<Status> statusListReplies = twitter.getMentions(paging);
            List<Tweet> replyList = getFormattedTweetListFromStatusList(statusListReplies);
            DLog.sleep();
            List<Tweet> dmList = getFormattedTweetListFromTweetList(this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(paging)));
            DLog.sleep();
            List<Tweet> dmSentList = this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getSentDirectMessages(paging));
            DLog.sleep();
            List<Status> statusListSent = twitter.getUserTimeline(paging);
            List<Tweet> sentList = getFormattedTweetListFromStatusList(statusListSent);
            DLog.sleep();
            List<Status> statusFavouritesList = twitter.getFavorites();
            List<Tweet> favouritesList = getFormattedTweetListFromStatusList(statusFavouritesList);
            List<Status> statusRetweetsByMe = twitter.getRetweetedByMe();
            List<Tweet> retweetsList = getFormattedTweetListFromStatusList(statusRetweetsByMe);
            DLog.sleep();

            modelMap.put("tweetList", friendsList);
            modelMap.put("friendsList", friendsList);
            modelMap.put("replyList", replyList);
            modelMap.put("dmList", dmList);
            modelMap.put("dmSentList", dmSentList);
            modelMap.put("sentList", sentList);
            modelMap.put("favouritesList", favouritesList);
            modelMap.put("retweetsList", retweetsList);
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
    private Map getModelMap(HttpServletRequest request)
    {
        HttpSession session = request.getSession();

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
    private Map saveModelMap(HttpServletRequest request, Map modelMap)
    {
        HttpSession session = request.getSession();
        session.setAttribute("modelMap", modelMap);
        return modelMap;
    }

    /*******************************************************************************/
    /*END MODEL MAP MANIPULATION METHODS*/
    /*******************************************************************************/
    //
    /*******************************************************************************/
    /*START Twitter4j to Droplet ADAPTER METHODS*/
    /*******************************************************************************/
    /**
     *
     * @param twitter
     * @param statusList
     * @throws TwitterException
     */
    private List<Tweet> getFormattedTweetListFromStatusList(List<Status> statusList) throws TwitterException
    {
        List<Tweet> formattedTweetList = new LinkedList();
        for (Status status : statusList)
        {
            Tweet tweet = new Tweet(status);
            tweet.setPrettyText(TweetUtil.swapAllForLinks(tweet.getText()));
            tweet.setPrettyTime(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
            formattedTweetList.add(tweet);
        }
        return formattedTweetList;
    }

    /**
     *
     * @param twitter
     * @param tweetList
     * @throws TwitterException
     */
    private List<Tweet> getFormattedTweetListFromTweetList(List<Tweet> tweetList) throws TwitterException
    {

        for (Tweet tweet : tweetList)
        {
            tweet.setPrettyText(TweetUtil.swapAllForLinks(tweet.getText()));
            if (!tweet.getTracked())
            {
                tweet.setPrettyTime(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
            }
        }
        return tweetList;
    }

    private List<Tweet> getDropletTweetListFromTwitter4jListOfTweets(List tweets)
    {
        List<com.dropletweet.domain.Tweet> tweetList = new LinkedList();

        for (twitter4j.Tweet tweet : (List<twitter4j.Tweet>) tweets)
        {
            com.dropletweet.domain.Tweet dt = new com.dropletweet.domain.Tweet(tweet);
            tweetList.add(dt);
        }

        return tweetList;
    }

    private List<Tweet> getDropletTweetListFromTwitter4jDirectMessages(List directMessages)
    {
        List<Tweet> tweetList = new LinkedList();

        for (DirectMessage directMessage : (List<DirectMessage>) directMessages)
        {
            Tweet dt = new Tweet(directMessage);
            tweetList.add(dt);
        }

        return tweetList;
    }
    /*******************************************************************************/
    /*END Twitter4j to Droplet ADAPTER METHODS*/
    /*******************************************************************************/
    //
    /*******************************************************************************/
    /*START DEPENDANCY INJECTION OBJECTS*/
    /*******************************************************************************/
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

    private Tweet getTweetFromSession(Long tweetId, String listType, Map modelMap)
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

    private List<Tweet> setTrackedTweets(List<Tweet> tweetList, Map modelMap)
    {
        User user = (User) modelMap.get("user");
        List<Conversation> conversationList = dropletService.getAllConversationsByUserId(user.getId());
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

    private List<Tweet> setFavouriteTweets(List<Tweet> tweetList, Map modelMap)
    {
        List<Tweet> favouritesList = (List<Tweet>) modelMap.get("favouritesList");
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

    private List<Tweet> setRetweetTweets(List<Tweet> tweetList, Map modelMap)
    {
        List<Tweet> retweetList = (List<Tweet>) modelMap.get("retweetList");
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
}
