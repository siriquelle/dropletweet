/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.command.text.RemovePunctuation;
import com.dropletweet.domain.Tweet;
import java.util.List;
import org.apache.commons.collections.Bag;

/**
 *
 * @author Siriquelle
 */
public class FillWordBagFromTweetList {

    public static Bag run(List<Tweet> tweetList, Bag wordBag)
    {
        for (Tweet tweet : tweetList)
        {
            String[] words = tweet.getText().split("\\s");
            for (String word : words)
            {
                if (!word.isEmpty())
                {
                    wordBag.add(RemovePunctuation.run(word).toLowerCase().trim());
                }
            }
        }
        return wordBag;
    }
}
