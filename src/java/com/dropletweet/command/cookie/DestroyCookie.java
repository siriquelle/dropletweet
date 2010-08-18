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
public class DestroyCookie {

  public static Cookie run(Cookie[] cookies, String key)
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
