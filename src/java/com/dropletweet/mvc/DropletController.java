/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc;

import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.service.DropletService;
import com.dropletweet.util.DLog;
import com.dropletweet.util.TweetTextUtil;
import com.ocpsoft.pretty.time.PrettyTime;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
            modelMap.putAll(doAjaxPostTweet(request));
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
            updateModelMap(this.getAuthorizedTwitter(session), request);
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
        modelMap.put("view", "index");
        HttpSession session = request.getSession();
        String refresh = request.getParameter("refresh");
        if ((Map) session.getAttribute("modelMap") == null || ((Twitter) session.getAttribute("twitter") == null))
        {
            request.getSession().invalidate();
            modelMap.put("view", "redirect:index.htm");
        } else if ((Map) session.getAttribute("modelMap") != null && (Twitter) session.getAttribute("twitter") != null && refresh != null)
        {
            updateModelMap((Twitter) session.getAttribute("twitter"), request);
        } else
        {
            modelMap.putAll((Map) session.getAttribute("modelMap"));
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
            String action = request.getParameter("action");
            if (action.equals("home"))
            {
                statusList = twitter.getFriendsTimeline();
            } else if (action.equals("replies"))
            {
                statusList = twitter.getMentions();
            } else if (action.equals("dms"))
            {
                tweetList = this.getDropletTweetListFromTwitter4jDirectMessages(twitter.getDirectMessages());
            } else if (action.equals("favourites"))
            {
                statusList = twitter.getFavorites();
            } else if (action.equals("retweets"))
            {
                statusList = twitter.getRetweetedToMe();
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
            modelMap.put("user", user);
            modelMap.put("tweetList", tweetList);
            modelMap.put("view", "ajax/statuslist");

            session.setAttribute("modelMap", modelMap);

        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modelMap;
    }

    /**
     *
     */
    public Map doAjaxPostTweet(HttpServletRequest request)
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
                Status status = null;
                if (in_reply_to_id.length() > 0)
                {
                    status = twitter.updateStatus(tweet_text, Long.valueOf(in_reply_to_id));
                } else
                {
                    status = twitter.updateStatus(tweet_text);
                }

                tweet = new Tweet(status);
                dropletService.persistTweet(tweet);

            }
        } catch (TwitterException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (tweet != null)
        {
            tweet.setCreated_at(getDateAsPrettyTime(tweet.getCreated_at()));
            tweet.setText(TweetTextUtil.swapAllForLinks(tweet.getText()));
            modelMap.put("tweet", tweet);
        }
        modelMap.put("view", "ajax/actions");
        return modelMap;
    }

    /*******************************************************************************/
    /*END AJAX Request Handlers*/
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

    /**
     *
     * @param twitter
     * @param session
     * @throws TwitterException
     */
    private void updateModelMap(Twitter twitter, HttpServletRequest request) throws TwitterException
    {
        Map modelMap = getModelMap(request);
        HttpSession session = request.getSession();
        User user = new User(twitter.verifyCredentials());
        dropletService.persistUser(user);
        List<Status> statusList = twitter.getFriendsTimeline();
        List<Tweet> tweetList = getFormattedTweetListFromStatusList(statusList);
        modelMap.put("user", user);
        modelMap.put("tweetList", tweetList);
        session.setAttribute("modelMap", modelMap);
    }

    private Map getModelMap(HttpServletRequest request)
    {
        HttpSession session = request.getSession();

        if (session.getAttribute("modelMap") == null)
        {
            session.setAttribute("modelMap", new HashMap(0));
        }
        return (Map) session.getAttribute("modelMap");
    }

    private Map saveModelMap(HttpServletRequest request, Map modelMap)
    {
        HttpSession session = request.getSession();
        session.setAttribute("modelMap", modelMap);
        return modelMap;
    }

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
            tweet.setText(TweetTextUtil.swapAllForLinks(tweet.getText()));
            tweet.setCreated_at(getDateAsPrettyTime(tweet.getCreated_at()));
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
            tweet.setText(TweetTextUtil.swapAllForLinks(tweet.getText()));
            try
            {
                DateFormat df = new SimpleDateFormat(dropletProperties.getProperty("twitter.dateformat.search.api"));
                tweet.setCreated_at(prettyTime.format(df.parse(tweet.getCreated_at())));
            } catch (ParseException ex)
            {
                Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    /**
     * START DEPENDANCY INJECTION OBJECTS
     */
    protected PrettyTime prettyTime;
    protected DropletService dropletService;
    protected Properties dropletProperties;

    /**
     * Set the value of rssService
     *
     * @param dropletService
     */
    public void setDropletService(DropletService dropletService)
    {
        this.dropletService = dropletService;
    }

    /**
     * 
     * @param prettyTime
     */
    public void setPrettyTime(PrettyTime prettyTime)
    {
        this.prettyTime = prettyTime;
    }

    /**
     * Set the value of dropletProperties
     *
     * @param dropletProperties new value of dropletProperties
     * @throws IOException
     */
    public void setDropletProperties(Properties dropletProperties) throws IOException
    {
        dropletProperties.load(DropletController.class.getResourceAsStream("droplet.properties"));
        this.dropletProperties = dropletProperties;
    }

    private String getDateAsPrettyTime(String created_at)
    {
        try
        {
            DateFormat df = new SimpleDateFormat(dropletProperties.getProperty("twitter.dateformat.search.api"));
            created_at = prettyTime.format(df.parse(created_at));
        } catch (ParseException ex)
        {
            Logger.getLogger(DropletController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return created_at;

    }
}
