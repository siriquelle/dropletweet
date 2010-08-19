/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.twitter;

import com.dropletweet.log.DLog;
import com.dropletweet.constants.DurationValues;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class GetAuthorizedTwitter {

    /**
     *
     * @param request
     * @return
     */
    public static Twitter run(HttpServletRequest request, HttpServletResponse response)
    {
        HttpSession session = request.getSession();
        Twitter twitter = (Twitter) session.getAttribute("twitter");
        RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
        try
        {

            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
            Cookie cookie = new Cookie("accessToken", accessToken.getToken() + "_" + accessToken.getTokenSecret());
            cookie.setMaxAge(DurationValues.SEVEN_DAYS_IN_SECONDS);
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
}
