/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.modelmap;

import com.dropletweet.constants.AppValues;
import com.dropletweet.domain.Conversation;
import com.dropletweet.domain.Tweet;
import com.dropletweet.domain.User;
import com.dropletweet.service.DropletService;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Siriquelle
 */
public class GetTweetFromSession {

    public static Tweet run(Long tweetId, String listType, Map modelMap, DropletService dropletService)
    {
        Tweet trackTweet = null;

        if (listType.equals(AppValues.LIST_NAME_CONVERSATION_LIST))
        {
            List<Conversation> conversationList = dropletService.getAllConversationsByUserId(((User) modelMap.get(AppValues.MODELMAP_KEY_USER)).getId());
            if (conversationList != null)
            {
                for (Conversation con : conversationList)
                {
                    if (con.getTweet().getId().equals(tweetId))
                    {
                        trackTweet = con.getTweet();
                        trackTweet.setTracked(Boolean.TRUE);
                        break;
                    }
                }
            }
        } else
        {
            if (listType.contains(AppValues.LIST_NAME_SEARCH))
            {
                listType = AppValues.LIST_NAME_SEARCH;
            }
            List<Tweet> tweetList = (List<Tweet>) modelMap.get(listType);
            if (tweetList != null)
            {
                for (Tweet tweet : tweetList)
                {
                    if (tweet.getId().equals(tweetId))
                    {
                        trackTweet = tweet;
                        break;
                    }
                }
            }
        }
        return trackTweet;
    }
}
