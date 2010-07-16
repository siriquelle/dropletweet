/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author Siriquelle
 */
public class StartUp {

    public StartUp()
    {
    }
    public static ApplicationContext ctx;

    public static void startUp()
    {

        ctx = new FileSystemXmlApplicationContext(new String[]
                {
                    "web/WEB-INF/config/spring-services.xml",
                    "web/WEB-INF/config/spring-hibernate.xml"
                });

    }
}
