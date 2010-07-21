/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.props.DropletProperties;
import com.dropletweet.service.DropletService;
import com.dropletweet.util.DLog;
import com.dropletweet.util.TweetUtil;
import java.net.URL;
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
            modelMap.putAll(doTweetAjaxAction(request));
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
        try
        {
            HttpSession session = request.getSession();
            Twitter twitter = (Twitter) session.getAttribute("twitter");
            User user = new User(twitter.verifyCredentials());
            dropletService.persistUser(user);
            List<Status> statusList = null;
            List<Tweet> tweetList = null;
            List<Tweet> oldList = null;
            String action = request.getParameter("action");
            String listType = null;
            if (action.equals("home"))
            {
                listType = "friendsList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getFriendsTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getFriendsTimeline(new Paging(Long.valueOf("15000000000")));

            } else if (action.equals("replies"))
            {
                listType = "replyList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getMentions(new Paging(oldList.get(0).getId()))
                        : twitter.getMentions(new Paging(Long.valueOf("15000000000")));
            } else if (action.equals("dms"))
            {
                listType = "dmList";
                oldList = (List<Tweet>) modelMap.get(listType);
                tweetList = (oldList.size() > 0)
                        ? this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(new Paging(oldList.get(0).getId())))
                        : this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(new Paging(Long.valueOf("15000000000"))));

            } else if (action.equals("favourites"))
            {
                listType = "favouritesList";
                statusList = twitter.getFavorites();
            } else if (action.equals("retweets"))
            {
                listType = "sentList";
                oldList = (List<Tweet>) modelMap.get(listType);
                statusList = (oldList.size() > 0)
                        ? twitter.getUserTimeline(new Paging(oldList.get(0).getId()))
                        : twitter.getUserTimeline(new Paging(Long.valueOf("15000000000")));

            } else if (action.equals("search"))
            {

                String query = request.getParameter("q");
                tweetList = this.getDropletTweetListFromTwitter4jListOfTweets(twitter.search(new Query(query)).getTweets());

            } else if (action.equals("conversations"))
            {
                for (Conversation con : dropletService.getAllConversationsByUserId(user.getId()))
                {
                    tweetList.add(con.getTweet());
                }
            }
            if (statusList != null)
            {
                tweetList = getFormattedTweetListFromStatusList(statusList);
            } else if (tweetList != null)
            {
                tweetList = getFormattedTweetListFromTweetList(tweetList);
            }
            tweetList.addAll(oldList);

            modelMap.put("user", user);
            modelMap.put("tweetList", tweetList);
            modelMap.put(listType, tweetList);
            modelMap.put("view", "ajax/statuslist");

        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modelMap;
    }

    /**
     *
     */
    public Map doTweetAjaxAction(HttpServletRequest request)
    {
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
            } else if (action.equals("post"))
            {
                String tweet_text = request.getParameter("tweet_text");
                String in_reply_to_id = request.getParameter("in_reply_to_id");
                if (in_reply_to_id.length() > 0)
                {
                    dropletService.persistTweet(new Tweet(twitter.updateStatus(tweet_text, Long.valueOf(in_reply_to_id))));
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
                    tweet.setText("delete complete.");
                }
            }
        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
            tweet = null;
        }

        if (tweet != null && tweet.getCreated_at() != null)
        {
            tweet.setCreated_at(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
            tweet.setText(TweetUtil.swapAllForLinks(tweet.getText()));
        }

        modelMap.put("latestTweet", tweet);
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
        twitter.setOAuthConsumer(dropletProperties.getProperty("twitter.oauth.application.key"), dropletProperties.getProperty("twitter.oauth.application.secret"));

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

            User user = new User(twitter.verifyCredentials());
            List<Status> statusListFriends = twitter.getFriendsTimeline(new Paging(Long.valueOf("15000000000")));
            List<Tweet> friendsList = getFormattedTweetListFromStatusList(statusListFriends);
            DLog.sleep();
            List<Status> statusListReplies = twitter.getMentions(new Paging(Long.valueOf("15000000000")));
            List<Tweet> replyList = getFormattedTweetListFromStatusList(statusListReplies);
            DLog.sleep();
            List<Tweet> dmList = getFormattedTweetListFromTweetList(this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages(new Paging(Long.valueOf("15000000000")))));
            DLog.sleep();
            List<Status> statusListSent = twitter.getUserTimeline(new Paging(Long.valueOf("15000000000")));
            List<Tweet> sentList = getFormattedTweetListFromStatusList(statusListSent);
            modelMap.put("tweetList", friendsList);
            modelMap.put("friendsList", friendsList);
            modelMap.put("replyList", replyList);
            modelMap.put("dmList", dmList);
            modelMap.put("sentList", sentList);
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
            tweet.setText(TweetUtil.swapAllForLinks(tweet.getText()));
            tweet.setCreated_at(TweetUtil.getDateAsPrettyTime(tweet.getCreated_at()));
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
        List<Tweet> formattedTweetList = new LinkedList();
        for (Tweet tweet : tweetList)
        {
            tweet.setText(TweetUtil.swapAllForLinks(tweet.getText()));

            TweetUtil.getDateAsPrettyTime(tweet.getCreated_at());

            formattedTweetList.add(tweet);
        }
        return formattedTweetList;
    }

    private List<Tweet> getDropletTweetListFromTwitter4jListOfTweets(List tweets)
    {
        List<Tweet> tweetList = new LinkedList();

        for (twitter4j.Tweet tweet : (List<twitter4j.Tweet>) tweets)
        {
            Tweet dt = new Tweet(tweet);
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
}
