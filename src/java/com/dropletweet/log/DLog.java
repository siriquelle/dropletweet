/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.log;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Siriquelle
 */
public class DLog {

    /**
     *
     * @param message
     */
    public static void log(String message)
    {
        System.out.println("-----------------------");
        System.out.println(message);
        System.out.println("-----------------------");
    }

    public static void sleep()
    {
        try
        {
            Thread.sleep(new Long(new Random().nextInt(250) + 1));
        } catch (InterruptedException ex)
        {
            Logger.getLogger(DLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
