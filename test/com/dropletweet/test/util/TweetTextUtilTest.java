/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.util;

import org.junit.Test;
import com.dropletweet.util.TweetUtil;
import com.dropletweet.util.DLog;

/**
 *
 * @author Siriquelle
 */
public class TweetTextUtilTest {

    public TweetTextUtilTest()
    {
    }
    //
    private static String random = "At the request of @NoelDickover & @poplifegirl, a 2nd #CrisisCongress  stream for Macs. #CCIC (Broadcasting live at http://ustre.am/kTPH)";

    /**
     *
     */
    @Test
    public void swapLinksForAnchorsTest()
    {
        String temp = random;
        DLog.log("BEGIN : swapLinksForAnchorsTest");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetUtil.swapForAnchors("http://", "", "_self", random, "outlink"));
        DLog.log("END : swapLinksForAnchorsTest");
        random = temp;
    }

    /**
     *
     */
    @Test
    public void swapScreenNamesForLinks()
    {
        String temp = random;
        DLog.log("BEGIN : swapScreenNamesForLinks");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetUtil.swapForAnchors("@", "http://twitter.com/", "_self", random, "outlink"));
        DLog.log("END : swapScreenNamesForLinks");
        random = temp;
    }

    /**
     *
     */
    @Test
    public void swapHashTagsForLinks()
    {
        String temp = random;
        DLog.log("BEGIN : swapHashTagsForLinks");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetUtil.swapForAnchors("#", "http://twitter.com/#search?q=", "_self", random, "outlink"));
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
        DLog.log("AFTER : " + TweetUtil.swapAllForLinks(random));
        DLog.log("END : swapAllForLinks(");
        random = temp;
    }
}
