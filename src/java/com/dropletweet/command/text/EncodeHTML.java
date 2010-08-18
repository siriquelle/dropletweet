/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.text;

import com.dropletweet.log.DLog;
import org.springframework.util.StringUtils;

/**
 *
 * @author Siriquelle
 */
public class EncodeHTML {

    public static String run(String s)
    {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++)
        {

            Character c = s.charAt(i);
            DLog.log(String.valueOf(Integer.valueOf(c)) +" : " + String.valueOf(c));
            if (c.toString().matches("[\"'\\‘’\\[\\]\\(\\)\\{\\}\\<\\>-\\?–]") ||
                    c > 255 || c < 32)
            {
                out.append("&#").append((int) c).append(";");
            } else
            {
                out.append(c);
            }
        }
        return out.toString();
    }
}
