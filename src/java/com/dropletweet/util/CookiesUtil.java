/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import javax.servlet.http.Cookie;

/**
 *
 * @author Siriquelle
 */
public class CookiesUtil {

    public static String getValue(Cookie[] cookies, String key)
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

    public static Cookie destroyCookie(Cookie[] cookies, String key)
    {
        Cookie cookie = null;
        for (Cookie c : cookies)
        {
            if (c.getName().equals(key))
            {
                c.setMaxAge(-1);
                c.setValue("accessToken");
                cookie = c;
                break;
            }
        }
        return cookie;
    }
}
