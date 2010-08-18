/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.text.test;

import com.dropletweet.command.text.EncodeHTML;
import com.dropletweet.log.DLog;
import org.junit.Test;

/**
 *
 * @author Siriquelle
 */
public class EncodeHTMLTest {

    @Test
    public void encodeHTMLTest()
    {
        DLog.log(EncodeHTML.run(TestPhrase.JAPANESE_TEXT_TEST_PHRASE));
    }
}
