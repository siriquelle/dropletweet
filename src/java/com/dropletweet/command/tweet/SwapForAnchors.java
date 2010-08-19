/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.tweet;

import com.dropletweet.command.text.EncodeHTML;
import com.dropletweet.log.DLog;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Siriquelle
 */
public class SwapForAnchors {

    /**
     *
     * @param key
     * @param href
     * @param tweet
     * @return
     */
    public static String run(String key, String href, String target, String tweet, String cssClass)
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


                    if (linkStart < linkEnd && linkStart >= 0)
                    {
                        if (linkStart - 1 == -1 || tweet.substring(linkStart - 1, linkStart).matches("[\\s;:-]"))
                        {
                            value = tweet.substring(linkStart, linkEnd);
                            value = StringEscapeUtils.unescapeHtml(value);
                            int end = value.length();

                            while ((String.valueOf(value.charAt(end - 1)).matches("[^a-zA-Z0-9_/]"))
                                    || (String.valueOf(value.charAt(end - 2)).matches("[^a-zA-Z0-9_/\\.]")))
                            {
                                end--;
                            }
                            String tempValue = value;
                            String ass = "";
                            value = value.substring(0, end);
                            if (value.length() < tempValue.length())
                            {
                                ass = tempValue.substring(value.length(), tempValue.length());

                                ass = ass.replaceAll("\\.", "\\\\.");
                            }

                            value = EncodeHTML.run(value);
                            ass = EncodeHTML.run(ass);
                            //value = value.replaceAll("\\?", "\\\\?");
                            //
                            String link = (String.valueOf(value.charAt(0)).equals("@")) ? (href + value.substring(1, value.length())) : (href + value);

                            anchor = "<a href=\"" + link + "\" class=\"" + cssClass + "\" target=\"" + target + "\" >" + value + "</a>";
                            String regex = "(?<!#)" + "(" + value + ")" + "(?=[^a-zA-Z0-9/<])(?=" + ass + ")";
                            DLog.log(regex);
                            tweet = tweet.replaceFirst(regex, anchor);
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
}
