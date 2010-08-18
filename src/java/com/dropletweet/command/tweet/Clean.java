/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet;

import com.dropletweet.domain.Tweet;

/**
 *
 * @author Siriquelle
 */
public class Clean {

    public static Tweet run(Tweet tweet)
    {
        tweet.setCreated_at(null);
        tweet.setFrom_user(null);
        tweet.setFrom_user_id(null);
        tweet.setIso_language_code(null);
        tweet.setLocation(null);
        tweet.setProfile_image_url(null);
        tweet.setSource(null);
        tweet.setText("This tweet has been deleted by the owner.");
        tweet.setTo_user(null);
        tweet.setTo_user_id(null);
        return tweet;
    }
}
