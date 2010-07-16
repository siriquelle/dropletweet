/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.mvc;

import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.service.DropletService;
import com.dropletweet.util.DLog;
import com.dropletweet.util.TweetTextUtil;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class DropletController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        modelMap.clear();
        String view = "redirect:/index.jsp";
        modelMap.put("view", view);

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
        }
        DLog.log("MODEL MAP SIZE:" + String.valueOf(modelMap.size()));

        return new ModelAndView((String) modelMap.get("view"), "modelMap", modelMap);
    }

    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map doIndexView(HttpServletRequest request) throws Exception
    {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null)
        {
            this.doDropletView(request);
        }
        modelMap.put("view", "index");
        return modelMap;
    }

    private Map doSigninView(HttpServletRequest request) throws Exception
    {
        HttpSession session = request.getSession();
        String authorizationURL = this.getAuthorizationURL(session).toExternalForm();
        if (authorizationURL == null)
        {
            authorizationURL = "index";
        } else
        {
            authorizationURL = "redirect:" + authorizationURL;
        }

        modelMap.put("view", authorizationURL);
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
        HttpSession session = request.getSession();
        String refresh = request.getParameter("refresh");
        Twitter twitter = null;
        if (request.getSession().getAttribute("requestToken") == null)
        {
            twitter = (Twitter) session.getAttribute("twitter");
        } else
        {
            twitter = this.getAuthorizedTwitter(session);
        }
        if (refresh != null)
        {
            User user = new User(twitter.verifyCredentials());
            modelMap.put("user", user);
            List<Tweet> statusList = new LinkedList();
            for (Status status : twitter.getFriendsTimeline())
            {
                Tweet tweet = new Tweet(status);
                tweet.setText(TweetTextUtil.swapAllForLinks(tweet.getText()));
                statusList.add(tweet);
            }
            Collections.reverse(statusList);
            modelMap.put("statusList", statusList);
        } else
        {
            modelMap.put("user", (User) session.getAttribute("user"));
            modelMap.put("statusList", (List<Tweet>) session.getAttribute("statusList"));
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
    private URL getAuthorizationURL(HttpSession session) throws Exception
    {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(dropletProperties.getProperty("twitter.oauth.application.key"), dropletProperties.getProperty("twitter.oauth.application.secret"));

        RequestToken requestToken = twitter.getOAuthRequestToken();
        String authorizationURL = requestToken.getAuthorizationURL();
        session.setAttribute("twitter", twitter);
        session.setAttribute("requestToken", requestToken);

        session.setAttribute("authorizationURL", authorizationURL);
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

            twitter.getOAuthAccessToken(requestToken);
            User twitterUser = new User(twitter.verifyCredentials());
            dropletService.persistUser(twitterUser);

            session.setAttribute("user", twitterUser);
            session.setAttribute("statusList", twitter.getFriendsTimeline());
            session.removeAttribute("requestToken");
            session.removeAttribute("authorizationURL");

        } catch (TwitterException e)
        {
            DLog.log(e.getMessage());
        }

        return twitter;
    }
    /**
     * DEPENDANCY INJECTION OBJECTS
     */
    protected Map modelMap;
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
     * Set the value of modelMap
     *
     * @param modelMap new value of modelMap
     */
    public void setModelMap(Map modelMap)
    {
        this.modelMap = modelMap;
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
}
