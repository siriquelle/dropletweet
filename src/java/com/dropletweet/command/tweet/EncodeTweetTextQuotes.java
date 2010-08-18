/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet;

/**
 *
 * @author Siriquelle
 */
public class EncodeTweetTextQuotes {

    /**
     *
     * @param tweet
     * @return
     */
    public static String run(String tweet)
    {
        tweet = tweet.replaceAll("\"", "'");
        tweet = tweet.replace("\n", "");
        tweet = tweet.trim();
        return tweet;
    }
}
