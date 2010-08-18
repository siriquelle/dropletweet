/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet;

import com.dropletweet.command.text.EncodeHTML;
import com.dropletweet.log.DLog;

/**
 *
 * @author Siriquelle
 */
public class SwapAllForLinks {

    /**
     *
     * @param tweet
     * @return
     */
    public static String run(String tweet)
    {
        DLog.log("START SWAP ALL FOR LINKS");
        tweet = EncodeHTML.run(tweet);

        tweet = SwapForAnchors.run("http://", "", "_blank", tweet, "outlink url");
        tweet = SwapForAnchors.run("#", "#", "_self", tweet, "outlink hash");
        tweet = SwapForAnchors.run("@", "#", "_self", tweet, "outlink person");

        DLog.log(tweet);
        DLog.log("END SWAP ALL FOR LINKS");
        return tweet.replace("\n", "");
    }
}
