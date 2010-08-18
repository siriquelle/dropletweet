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

        DLog.log(SwapAllForLinks.run(TestPhrase.PUNCTUATION_TWITTER_CANNOT_HANDLE_TEST_PHRASE));

    }
}
