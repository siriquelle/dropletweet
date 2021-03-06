/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.cookie;

import com.dropletweet.constants.AppValues;
import javax.servlet.http.Cookie;

/**
 *
 * @author Siriquelle
 */
public class DestroyCookie {

    public static Cookie run(Cookie[] cookies, String key)
    {
        Cookie cookie = null;
        for (Cookie c : cookies)
        {
            if (c.getName().equals(key))
            {
                c.setMaxAge(0);
                c.setValue(AppValues.COOKIE_KEY_ACCESS_TOKEN);
                cookie = c;
                break;
            }
        }
        return cookie;
    }
}
