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
     * @param tweet
     * @return
     */
    public static String swapLinksForAnchors(String tweet)
    {
        String link = "";
        String anchor = "";

        int linkStart = 0;
        int linkEnd = 0;

        tweet = tweet.concat(" ");

        try
        {
            while (linkStart != -1)
            {
                linkStart = tweet.indexOf("http://", linkEnd);
                if (linkEnd != -1)
                {
                    linkEnd = tweet.indexOf(" ", linkStart);
                    if (!(String.valueOf(tweet.charAt(linkEnd - 1)).matches("[a-zA-Z0-9_]")))
                    {
                        linkEnd--;
                    }
                    link = tweet.substring(linkStart, linkEnd);

                    anchor = "<a href=\"" + link + "\" class=\"outlink\" >" + link + "</a>";
                    tweet = tweet.replace(link, anchor);
                    linkEnd = tweet.indexOf("</a>", linkEnd);
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
    public static String swapScreenNamesForLinks(String tweet)
    {
        String screenName = "";
        String anchor = "";

        int linkStart = 0;
        int linkEnd = 0;

        tweet = tweet.concat(" ");

        try
        {
            while (linkStart != -1)
            {
                linkStart = tweet.indexOf("@", linkEnd);
                if (linkEnd != -1)
                {
                    linkEnd = tweet.indexOf(" ", linkStart);
                    if (!(String.valueOf(tweet.charAt(linkEnd - 1)).matches("[a-zA-Z0-9_]")))
                    {
                        linkEnd--;
                    }
                    screenName = tweet.substring(linkStart, linkEnd);

                    anchor = "<a href=\"http://twitter/com/" + screenName + "\" class=\"outlink\" >" + screenName + "</a>";
                    tweet = tweet.replace(screenName, anchor);
                    linkEnd = tweet.indexOf("</a>", linkEnd);
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
    public static String swapHashTagsForLinks(String tweet)
    {
        String hashtag = "";
        String anchor = "";

        int linkStart = 0;
        int linkEnd = 0;

        tweet = tweet.concat(" ");

        try
        {
            while (linkStart != -1)
            {
                linkStart = tweet.indexOf("#", linkEnd);
                if (linkEnd != -1)
                {
                    linkEnd = tweet.indexOf(" ", linkStart);
                    if (!(String.valueOf(tweet.charAt(linkEnd - 1)).matches("[a-zA-Z0-9_]")))
                    {
                        linkEnd--;
                    }
                    hashtag = tweet.substring(linkStart, linkEnd);

                    anchor = "<a href=\"http://twitter.com/#search?q=" + hashtag + "\" class=\"outlink\" >" + hashtag + "</a>";
                    tweet = tweet.replace(hashtag, anchor);
                    linkEnd = tweet.indexOf("</a>", linkEnd);
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
        tweet = swapLinksForAnchors(tweet);
        tweet = swapScreenNamesForLinks(tweet);
        tweet = swapHashTagsForLinks(tweet);

        return tweet;
    }
}
