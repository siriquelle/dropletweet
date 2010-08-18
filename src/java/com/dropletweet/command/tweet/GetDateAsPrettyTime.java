/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet;

import com.dropletweet.log.DLog;
import com.dropletweet.props.DropletProperties;
import com.ocpsoft.pretty.time.PrettyTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 *
 * @author Siriquelle
 */
public class GetDateAsPrettyTime {

    protected static PrettyTime prettyTime;
    protected static Properties dropletProperties;

    static
    {
        prettyTime = new PrettyTime();
        dropletProperties = new DropletProperties();
    }

    public static String run(String created_at)
    {

        DateFormat df = null;
        if (created_at.charAt(3) != ',')
        {
            df = new SimpleDateFormat(dropletProperties.getProperty("twitter.dateformat.search.api"));
        } else
        {
            df = new SimpleDateFormat(dropletProperties.getProperty("twitter.dateformat.rest.api"));
        }
        try
        {
            created_at = prettyTime.format(df.parse(created_at));
        } catch (ParseException ex)
        {
            DLog.log(ex.getMessage());
        }

        return created_at;
    }
}
