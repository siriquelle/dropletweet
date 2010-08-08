/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.log.DLog;
import com.dropletweet.props.DropletProperties;
import java.net.URL;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class TwitterUtil {

    /*******************************************************************************/
    /*START OAUTH AND TWITTER METHODS*/
    /*******************************************************************************/
    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static URL getAuthorizationURL(HttpSession session, DropletProperties dropletProperties) throws Exception
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
    public static Twitter getAuthorizedTwitter(HttpServletRequest request, HttpServletResponse response)
    {
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute("twitter");
        RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
        try
        {

            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
            Cookie cookie = new Cookie("accessToken", accessToken.getToken() + "_" + accessToken.getTokenSecret());
            cookie.setMaxAge(TimeUtil.SEVEN_DAYS_IN_SECONDS);
            cookie.setComment("This cookie enables you to login automatically, for one week, to dropletweet. This means you do not have to negotiate with twitter for authorization every time you visit, :)");
            response.addCookie(cookie);
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(accessToken.getToken(), accessToken.getTokenSecret(), null));
            session.removeAttribute("requestToken");

        } catch (TwitterException e)
        {
            DLog.log(e.getMessage());
        }

        return twitter;
    }

    /**
     *
     * @param request
     * @return
     */
    public static Twitter getAuthorizedTwitter(HttpServletRequest request, String tokenKey, String tokenSecret, DropletProperties dropletProperties)
    {
        AccessToken accessToken = new AccessToken(tokenKey, tokenSecret);
        HttpSession session = request.getSession();

        Twitter twitter = new TwitterFactory().getOAuthAuthorizedInstance(
                dropletProperties.getProperty("twitter.oauth.application.key"),
                dropletProperties.getProperty("twitter.oauth.application.secret"),
                accessToken);
        try
        {
            twitter.verifyCredentials();
            session.setAttribute("twitter", twitter);
        } catch (TwitterException ex)
        {
            DLog.log(ex.getMessage());
        }

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(accessToken.getToken(), accessToken.getTokenSecret(), null));
        return twitter;
    }
    /*******************************************************************************/
    /*END OAUTH AND TWITTER METHODS*/
    /*******************************************************************************/
}
