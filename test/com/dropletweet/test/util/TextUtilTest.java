/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.util;

import org.junit.Test;
import com.dropletweet.log.DLog;
import com.dropletweet.util.TextUtil;

/**
 *
 * @author Siriquelle
 */
public class TextUtilTest {

    public TextUtilTest()
    {
    }
    //
    private static String random = "At the request!!:?..,#][=-asdas$$^$&&fsdf sdfsdf))(sdfsdf(*^%$^";

    @Test
    public void removePunctuationTest()
    {
        DLog.log(TextUtil.removePunctuation(random));
    }
}
