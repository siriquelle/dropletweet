/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.twitter;

import com.dropletweet.props.DropletProperties;
import java.net.URL;
import javax.servlet.http.HttpSession;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;

/**
 *
 * @author Siriquelle
 */
public class GetAuthorizationURL {

    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static URL run(HttpSession session, DropletProperties dropletProperties) throws Exception
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
}
