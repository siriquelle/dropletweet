/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet.test;

import com.dropletweet.command.text.test.TestPhrase;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.command.tweet.SwapForAnchors;
import org.junit.Test;

import com.dropletweet.log.DLog;

/**
 *
 * @author Siriquelle
 */
public class TweetUtilTest {

    public TweetUtilTest()
    {
    }
    //
    String random = TestPhrase.LINK_EQUALS;

    /**
     *
     */
    //@Test
    public void swapLinksForAnchorsTest()
    {
        String temp = random;
        DLog.log("BEGIN : swapLinksForAnchorsTest");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + SwapForAnchors.run("http://", "", "_self", random, "outlink"));
        DLog.log("END : swapLinksForAnchorsTest");
        random = temp;
    }

    /**
     *
     */
    //@Test
    public void swapScreenNamesForLinks()
    {
        String temp = random;
        DLog.log("BEGIN : swapScreenNamesForLinks");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + SwapForAnchors.run("@", "http://twitter.com/", "_self", random, "outlink"));
        DLog.log("END : swapScreenNamesForLinks");
        random = temp;
    }

    /**
     *
     */
    //@Test
    public void swapHashTagsForLinks()
    {
        String temp = random;
        DLog.log("BEGIN : swapHashTagsForLinks");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + SwapForAnchors.run("#", "http://twitter.com/#search?q=", "_self", random, "outlink"));
        DLog.log("END : swapHashTagsForLinks");
        random = temp;
    }

    /**
     * 
     */
    @Test
    public void swapAllForLinks()
    {
        String temp = random;
        DLog.log("BEGIN : swapAllForLinks(");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + SwapAllForLinks.run(random));
        DLog.log("END : swapAllForLinks(");
        random = temp;
    }
}
