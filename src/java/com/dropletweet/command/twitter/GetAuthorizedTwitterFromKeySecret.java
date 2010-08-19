/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.twitter;

import com.dropletweet.log.DLog;
import com.dropletweet.props.DropletProperties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

/**
 *
 * @author Siriquelle
 */
public class GetAuthorizedTwitterFromKeySecret {

    /**
     *
     * @param request
     * @return
     */
    public static Twitter run(HttpServletRequest request, String tokenKey, String tokenSecret, DropletProperties dropletProperties)
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
}
