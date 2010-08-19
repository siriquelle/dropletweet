/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet.test;

import com.dropletweet.command.text.test.TestPhrase;
import com.dropletweet.command.tweet.SwapAllForLinks;
import com.dropletweet.log.DLog;
import org.junit.Test;

/**
 *
 * @author Siriquelle
 */
public class SwapAllForLinksTest {

    @Test
    public void swapAllForLinksTest()
    {

        DLog.log(SwapAllForLinks.run(TestPhrase.ANGLE_BRACKETS_TEST_PHRASE));
        /*
        DLog.log(SwapAllForLinks.run(TestPhrase.IDENTICAL_TAGS_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.JAPANESE_TEXT_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.LEO_TEST_PHASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.LINK_WITH_PARAMETERS_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.ODD_PUNCTUATION_TEST_PHASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.PARENTHESES_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.PUNCTUATION_TWITTER_CANNOT_HANDLE_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.SPECIAL_CHAR_PREPENDED_TO_NAME_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.SPECIAL_MAC_QUOTE_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.SUFFIX_JPG_TEST_ANGLE_BRACKETS_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.SPECIAL_CHAR_PREPENDED_TO_NAME_TEST_PHRASE));
        //
        DLog.log(SwapAllForLinks.run(TestPhrase.TOPGOLD_SOUTH_TIPP_CO_CO));
         * */

    }
}
