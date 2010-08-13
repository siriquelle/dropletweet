/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.text;

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
            if (c.toString().matches("[\"'\\‘’\\[\\]\\(\\)\\{\\}\\<\\>-]"))
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
