/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.log;

import com.dropletweet.mvc.DropletController;

/**
 *
 * @author Siriquelle
 */
public class DConsole {

    public static void log(String message)
    {
        try
        {
            DropletController.dropletLoadingBean.setMessage(message);
        } catch (Exception e)
        {
            DLog.log(e.toString());
        }
    }

    public static String read()
    {
        String message = "";
        try
        {
            message = DropletController.dropletLoadingBean.getMessage();
        } catch (Exception e)
        {
            DLog.log(e.toString());
            message = e.toString();
        }
        return message;
    }
}
