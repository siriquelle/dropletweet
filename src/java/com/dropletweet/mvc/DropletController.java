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
import com.dropletweet.util.CookiesUtil;
import com.dropletweet.util.ModelMapUtil;
import com.dropletweet.util.TweetListUtil;
import com.dropletweet.util.TweetUtil;
import com.dropletweet.util.Twitter4jAdapterUtil;
import com.dropletweet.util.TwitterUtil;
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

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        request.setCharacterEncoding("UTF-8");
//**                                                                        **//
        DLog.log("START GETTING USERS SESSION");
        Map modelMap = ModelMapUtil.getModelMap(request);
        DLog.log("END GETTING USERS SESSION");
//**                                                                        **//
        DLog.log("START SETTING UP DEFAULT DESTINATION");
        String view = "redirect:/index.jsp";
        modelMap.put("view", view);
        DLog.log("END SETTING UP DEFAULT DESTINATION");
//**                                                                        **//
        DLog.log("START PROCESSING REQUEST");
        String uri = request.getRequestURI();
        if (uri.contains("index.htm"))
        {
            modelMap.putAll(doIndexView(request, response));
        } else if (uri.contains("droplet.htm"))
        {
            modelMap.putAll(doDropletView(request, response));
        } else if (uri.contains("signin.htm"))
        {
            modelMap.putAll(doSigninView(request, response));
        } else if (uri.contains("statuslist.ajax"))
        {
            modelMap.putAll(doAjaxStatusList(request, response));
        } else if (uri.contains("tweet.ajax"))
        {
            modelMap.putAll(doAjaxTweetAction(request, response));
        } else if (uri.contains("user.ajax"))
        {
            modelMap.putAll(doAjaxUser(request, response));
        } else if (uri.contains("util.ajax"))
        {
            modelMap.putAll(doAjaxUtil(request, response));
        }
        DLog.log("END PROCESSING REQUEST");
//**                                                                        **//
        DLog.log("START SAVING MODEL MAP");
        modelMap.putAll(ModelMapUtil.saveModelMap(request, modelMap));
        DLog.log("END SAVING MODEL MAP");
//**                                                                        **//
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
    private Map doIndexView(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
//**                                                                        **//
        Map modelMap = ModelMapUtil.getModelMap(request);
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null)
        {
            modelMap.putAll(this.doDropletView(request, response));
        } else
        {
            if (CookiesUtil.getValue(request.getCookies(), "accessToken") != null)
            {
                modelMap.putAll(this.doSigninView(request, response));
            } else
            {
                modelMap.put("view", "index");
            }
        }
//**                                                                        **//
        return modelMap;
    }

    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map doSigninView(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Map modelMap = ModelMapUtil.getModelMap(request);
        modelMap.put("view", "redirect:index.htm");
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        String accessToken = "accessToken";
        if (request.getParameter("logout") == null)
        {

            accessToken = CookiesUtil.getValue(cookies, accessToken);
            accessToken = (accessToken == null) ? "accessToken" : accessToken;
            if (!accessToken.equals("accessToken"))
            {
                String tokenKey = accessToken.substring(0, accessToken.indexOf("_"));
                String tokenSecret = accessToken.substring(accessToken.indexOf("_") + 1, accessToken.length());
                modelMap.putAll(ModelMapUtil.updateModelMap(TwitterUtil.getAuthorizedTwitter(request, tokenKey, tokenSecret, dropletProperties), request, dropletService));
                modelMap.put("view", "redirect:droplet.htm");
            } else if ((RequestToken) session.getAttribute("requestToken") == null)
            {
                String authorizationURL = TwitterUtil.getAuthorizationURL(session, dropletProperties).toExternalForm();
                if (authorizationURL != null)
                {
                    modelMap.put("view", "redirect:" + authorizationURL);
                }
            } else
            {
                modelMap.putAll(ModelMapUtil.updateModelMap(TwitterUtil.getAuthorizedTwitter(request, response), request, dropletService));
                modelMap.put("view", "redirect:droplet.htm");
            }

        } else
        {
            session.invalidate();
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

            response.addCookie(CookiesUtil.destroyCookie(cookies, accessToken));

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
    private Map doDropletView(HttpServletRequest request, HttpServletResponse response) throws TwitterException
    {
        Map modelMap = ModelMapUtil.getModelMap(request);

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
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX HANDLERS*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX TWEET LIST**/
    /*******************************************************************************/
    /**
     *
     * @param type
     * @param request
     * @return
     */
    private Map doAjaxStatusList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException
    {
        Map modelMap = ModelMapUtil.getModelMap(request);
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
            twitter = (Twitter) session.getAttribute("twitter");
            user = (User) modelMap.get("user");

            action = request.getParameter("action");

            if (action.equals("friendsList"))
            {
                listType = "friendsList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getFriendsTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getFriendsTimeline(paging);

            } else if (action.equals("replyList"))
            {
                listType = "replyList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getMentions(new Paging(oldList.get(0).getId()))
                        : twitter.getMentions(paging);
            } else if (action.equals("dmList"))
            {
                listType = "dmList";
                oldList = (List<Tweet>) modelMap.get(listType);
                List<Tweet> dmSentList = (List<Tweet>) modelMap.get("dmSentList");
                tweetList = (oldList.size() > 0)
                        ? Twitter4jAdapterUtil.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(new Paging(oldList.get(0).getId())))
                        : Twitter4jAdapterUtil.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(paging));

                dmSentList = (dmSentList.size() > 0)
                        ? Twitter4jAdapterUtil.getDropletTweetListFromTwitter4jDirectMessages(twitter.getSentDirectMessages(new Paging(dmSentList.get(0).getId())))
                        : Twitter4jAdapterUtil.getDropletTweetListFromTwitter4jDirectMessages(twitter.getSentDirectMessages(paging));

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
                        : twitter.getUserTimeline(paging);

            } else if (action.contains("search"))
            {
                listType = "search";
                String query = request.getParameter("q");

                if (query.contains("from:"))
                {
                    statusList = new LinkedList<twitter4j.Status>();
                    String screenName = query.substring("from:".length(), query.length());
                    statusList.addAll(twitter.getUserTimeline(screenName));

                } else
                {

                    List<twitter4j.Tweet> tl = twitter.search(new Query(query).rpp(100)).getTweets();
                    if (tl != null)
                    {
                        tweetList = Twitter4jAdapterUtil.getDropletTweetListFromTwitter4jListOfTweets(tl);
                    }
                }

            } else if (action.equals("conversationList"))
            {
                conversationList = dropletService.getAllConversationsByUserId(user.getId());

                if (conversationList != null && conversationList.size() > 0)
                {
                    for (Conversation con : conversationList)
                    {
                        tweetList.add(con.getTweet());
                    }
                }
            } else if (action.equals("discussionList"))
            {
                DLog.log("//TODO: CREATE A LIST OF ALL TWEETS FROM FRIENDS TIMELINE THAT HAVE IN_REPLY_TO_IDs");

                listType = "discussionList";

                oldList = (List<Tweet>) modelMap.get("friendsList");
                statusList = (oldList.size() > 0)
                        ? twitter.getFriendsTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getFriendsTimeline(paging);
                List<Tweet> newList = Twitter4jAdapterUtil.getFormattedTweetListFromStatusList(statusList);
                newList.addAll(oldList);
                modelMap.put("friendsList", newList);

                tweetList = TweetListUtil.getDiscussionTweets(newList);

                statusList = null;
                oldList = null;
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
                            tweetList = Twitter4jAdapterUtil.getFormattedTweetListFromStatusList(twitter.getFriendsTimeline(new Paging().count(30).maxId(oldList.get(oldList.size() - 1).getId() - 1)));
                        } else if (listType.equals("sentList"))
                        {
                            tweetList = Twitter4jAdapterUtil.getFormattedTweetListFromStatusList(twitter.getUserTimeline(new Paging().count(30).maxId(oldList.get(oldList.size() - 1).getId() - 1)));
                        } else if (listType.contains("search"))
                        {
                            String q = listType.substring("search_".length());
                            if (q != null)
                            {
                                List<twitter4j.Tweet> tl = twitter.search(new Query(q).rpp(20).maxId(oldList.get(oldList.size() - 1).getId() - 1)).getTweets();
                                if (tl != null)
                                {
                                    tweetList = Twitter4jAdapterUtil.getDropletTweetListFromTwitter4jListOfTweets(tl);
                                }
                            }
                        }
                    }
                }
            }


            if (statusList != null && statusList.size() > 0)
            {
                tweetList = Twitter4jAdapterUtil.getFormattedTweetListFromStatusList(statusList);
            } else if (tweetList != null && tweetList.size() > 0)
            {
                if (action != null)
                {
                    tweetList = Twitter4jAdapterUtil.getFormattedTweetListFromTweetList(tweetList);
                }
            }
            if (oldList != null && oldList.size() > 0)
            {
                tweetList.addAll(oldList);
            }

            if (tweetList != null && tweetList.size() > 0)
            {
                tweetList = TweetListUtil.setTrackedTweets(tweetList, dropletService.getAllConversationsByUserId(user.getId()));
                tweetList = TweetListUtil.setFavouriteTweets(tweetList, (List<Tweet>) modelMap.get("favouritesList"));
                tweetList = TweetListUtil.setRetweetTweets(tweetList, (List<Tweet>) modelMap.get("retweetsList"));
                tweetList = TweetListUtil.setPrettyTime(tweetList);
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

    /*******************************************************************************/
    /*END AJAX TWEET LIST*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX TWEET ACTIONS*/
    /*******************************************************************************/
    /**
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public Map doAjaxTweetAction(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException
    {
        AjaxTweetActionBean ajaxTweetActionBean = new AjaxTweetActionBean();
        Map modelMap = ModelMapUtil.getModelMap(request);
        User user = (User) modelMap.get("user");
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
                    Conversation con = dropletService.getConversationByUserIdTweetIdCombination(user.getId(), tweet.getId());
                    if (con != null)
                    {
                        dropletService.deleteConversation(con);
                    }
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
                    tweet = ModelMapUtil.getTweetFromSession(Long.valueOf(tweetId), listType, modelMap, dropletService);
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
                            try
                            {
                                dropletService.persistTweet(tweet);
                                dropletService.persistConversation(new Conversation((User) modelMap.get("user"), tweet));
                            } catch (Exception e)
                            {
                                DLog.log(e.getMessage());
                            }
                            ajaxTweetActionBean.setText(dropletProperties.getProperty("droplet.ajax.action.track.complete"));
                        }
                    }
                }
            } else if (action.equals("follow"))
            {
                String screen_name = request.getParameter("screen_name");
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
    /*END AJAX TWEET ACTIONS*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX UTIL*/
    /*******************************************************************************/
    /**
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public Map doAjaxUtil(HttpServletRequest request, HttpServletResponse response)
    {
        String action = request.getParameter("action");
        Map modelMap = ModelMapUtil.getModelMap(request);
        AjaxUtilBean ajaxUtilBean = new AjaxUtilBean();

        if (action.equals("get_latest_url"))
        {
            String url = dropletProperties.getProperty("droplet.ajax.util.default.url");
            if ((User) modelMap.get("user") != null)
            {
                User user = (User) modelMap.get("user");
                Conversation latestConversation = (dropletService.getAllConversationsByUserId(user.getId()).size() > 0)
                        ? dropletService.getAllConversationsByUserId(user.getId()).get(dropletService.getAllConversationsByUserId(user.getId()).size() - 1)
                        : null;

                if (latestConversation != null)
                {
                    url = "http://twitter.com/" + latestConversation.getTweet().getFrom_user() + "/status/" + latestConversation.getTweet().getId();
                }
            } else
            {
                if ((String) this.getServletContext().getAttribute("latest_url") != null)
                {
                    url = (String) this.getServletContext().getAttribute("latest_url");
                }
            }
            ajaxUtilBean.setMessage(url);
        }

        modelMap.put("ajaxUtilBean", ajaxUtilBean);
        modelMap.put("view", "ajax/util");
        return modelMap;
    }

    /*******************************************************************************/
    /*END AJAX UTIL*/
    /*******************************************************************************/
    /**                                                                           **/
    /*******************************************************************************/
    /*START AJAX USER*/
    /*******************************************************************************/
    public Map doAjaxUser(HttpServletRequest request, HttpServletResponse response)
    {
        String action = request.getParameter("action");
        Map modelMap = ModelMapUtil.getModelMap(request);
        AjaxUserBean ajaxUserBean = null;
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute("twitter");

        if (action.equals("get_user_info"))
        {
            String userName = request.getParameter("screen_name");
            if (userName != null && userName.length() > 0)
            {
                try
                {
                    twitter4j.User user = null;

                    if (userName.equals(((User) modelMap.get("user")).getScreen_name()))
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
            modelMap.put("view", "ajax/user");
        }
        modelMap.put("ajaxUserBean", ajaxUserBean);
        return modelMap;
    }
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
    /*******************************************************************************/
    /*END DEPENDANCY INJECTION */
    /*******************************************************************************/
}
