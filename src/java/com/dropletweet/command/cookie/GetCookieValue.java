/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.cookie;

import javax.servlet.http.Cookie;

/**
 *
 * @author Siriquelle
 */
public class GetCookieValue {

    public static String run(Cookie[] cookies, String key)
    {
        String value = null;
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if (cookie.getName().equals(key))
                {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }
}
