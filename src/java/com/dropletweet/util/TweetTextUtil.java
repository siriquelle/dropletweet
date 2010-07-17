/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

/**
 *
 * @author Siriquelle
 */
public class TweetTextUtil {

    /**
     * 
     * @param key 
     * @param href
     * @param tweet
     * @return
     */
    public static String swapForAnchors(String key, String href, String tweet)
    {
        String value = "";
        String anchor = "";

        int linkStart = 0;
        int linkEnd = 0;

        tweet = tweet.concat(" ");

        try
        {
            while (linkStart != -1)
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
                        value = tweet.substring(linkStart, linkEnd);

                        String link = (String.valueOf(value.charAt(0)).equals("@")) ? (href + value.substring(1, value.length())) : (href + value);

                        anchor = "<a href=\"" + link + "\" class=\"outlink\" target=\"_blank\" >" + value + "</a>";
                        tweet = tweet.replace(value, anchor);
                        linkEnd = tweet.indexOf("</a>", linkEnd);
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
        return tweet;
    }

    /**
     * 
     * @param tweet
     * @return
     */
    public static String swapAllForLinks(String tweet)
    {
        tweet = swapForAnchors("http://", "", tweet);
        DLog.log("FIRST STEP");
        tweet = swapForAnchors("@", "http://twitter.com/", tweet);
        DLog.log("SECOND STEP");
        tweet = swapForAnchors("#", "http://twitter.com/#search?q=", tweet);

        return tweet;
    }
}
