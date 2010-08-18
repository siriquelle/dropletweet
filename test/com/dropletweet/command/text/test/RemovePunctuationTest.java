/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.text.test;

import com.dropletweet.command.text.RemovePunctuation;
import org.junit.Test;
import com.dropletweet.log.DLog;
import com.dropletweet.command.text.WordArray;
import com.dropletweet.command.text.test.TestPhrase;

/**
 *
 * @author Siriquelle
 */
public class RemovePunctuationTest {

    @Test
    public void removePunctuationTest()
    {
        DLog.log(RemovePunctuation.run(TestPhrase.PARENTHESES_TEST_PHRASE));
    }
}
