/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.props.DropletProperties;
import com.ocpsoft.pretty.time.PrettyTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Siriquelle
 */
public class TweetUtil {


    /**
     * 
     * @param key 
     * @param href
     * @param tweet
     * @return
     */
    public static String swapForAnchors(String key, String href, String tweet)
    {
        DLog.log("START SWAP FOR ANCHORS");
        DLog.log(tweet);
        String value = "";
        String anchor = "";

        int linkStart = 0;
        int linkEnd = 0;

        tweet = tweet.concat(" ");

        try
        {
            int count = 0;
            while (linkStart != -1 && count++ < 15)
            {
                linkStart = tweet.indexOf(key, linkEnd);

                if (linkEnd > -1)
                {
                    linkEnd = tweet.indexOf(" ", linkStart);
                    if (!(String.valueOf(tweet.charAt(linkEnd - 1)).matches("[a-zA-Z0-9_]")))
                    {
                        linkEnd--;
                    }

                    if (linkStart < linkEnd && linkStart >= 0)
                    {
                        if (linkStart - 1 == -1 || tweet.substring(linkStart - 1, linkStart).matches(" "))
                        {
                            value = tweet.substring(linkStart, linkEnd);

                            String link = (String.valueOf(value.charAt(0)).equals("@")) ? (href + value.substring(1, value.length())) : (href + value);

                            anchor = "<a href=\"" + link + "\" class=\"outlink\" target=\"_blank\" >" + value + "</a>";
                            tweet = tweet.replace(value, anchor);
                            linkEnd = tweet.indexOf("</a>", linkEnd);
                        }
                        if (count == 14)
                        {
                            DLog.log(anchor);
                        }
                    } else
                    {
                        linkEnd = linkStart;
                    }
                } else
                {
                    linkStart = linkEnd;
                }
            }
        } catch (Exception e)
        {
            DLog.log(e.toString());
        }

        DLog.log("END SWAP FOR ANCHORS");
        return tweet;
    }

    /**
     * 
     * @param tweet
     * @return
     */
    public static String swapAllForLinks(String tweet)
    {
        DLog.log("START SWAP ALL FOR LINKS");
        tweet = swapForAnchors("http://", "", tweet);

        tweet = swapForAnchors("@", "http://twitter.com/", tweet);

        tweet = swapForAnchors("#", "http://twitter.com/#search?q=", tweet);
        DLog.log(tweet);
        DLog.log("END SWAP ALL FOR LINKS");
        return tweet.replace("\n", "");
    }

    public static String encodeTweetTextQuotes(String tweet)
    {

        tweet = tweet.replaceAll("\"", "'");
        tweet = tweet.replace("\n", "");
        tweet = tweet.trim();
        return tweet;
    }
    protected static PrettyTime prettyTime = new PrettyTime();
    protected static DropletProperties dropletProperties = new DropletProperties();


    public static String getDateAsPrettyTime(String created_at)
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

            Logger.getLogger(TweetUtil.class.getName()).log(Level.SEVERE, null, ex);

        }


        return created_at;
    }
}