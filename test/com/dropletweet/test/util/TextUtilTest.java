/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.util;

import com.dropletweet.command.text.RemovePunctuation;
import org.junit.Test;
import com.dropletweet.log.DLog;
import com.dropletweet.command.text.WordArray;

/**
 *
 * @author Siriquelle
 */
public class TextUtilTest {

    public TextUtilTest()
    {
    }
    //
    private static String random = "At the @cooper_mrequest!!:?..,#][=-a{&#345;}sdas$$^$&&fsdf sdfsdf))(sdfsdf(*^%$^";

    @Test
    public void removePunctuationTest()
    {
        DLog.log(RemovePunctuation.run(random));
    }
}
