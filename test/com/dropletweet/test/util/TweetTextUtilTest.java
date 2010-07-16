/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.util;

import org.junit.Test;
import com.dropletweet.util.TweetTextUtil;
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
        DLog.log("BEGIN : swapLinksForAnchorsTest");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetTextUtil.swapLinksForAnchors(random));
        DLog.log("END : swapLinksForAnchorsTest");
    }

    /**
     *
     */
    @Test
    public void swapScreenNamesForLinks()
    {
        DLog.log("BEGIN : swapScreenNamesForLinks");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetTextUtil.swapScreenNamesForLinks(random));
        DLog.log("END : swapScreenNamesForLinks");
    }

    /**
     *
     */
    @Test
    public void swapHashTagsForLinks()
    {
        DLog.log("BEGIN : swapHashTagsForLinks(");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetTextUtil.swapHashTagsForLinks(random));
        DLog.log("END : swapHashTagsForLinks(");
    }

    /**
     * 
     */
    @Test
    public void swapAllForLinks()
    {
        DLog.log("BEGIN : swapAllForLinks(");
        DLog.log("BEFORE : " + random);
        DLog.log("AFTER : " + TweetTextUtil.swapAllForLinks(random));
        DLog.log("END : swapAllForLinks(");
    }
}
