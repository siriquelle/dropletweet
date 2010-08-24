/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.domain.Tweet;
import com.dropletweet.log.DLog;
import com.dropletweet.model.Droplet;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.HashBag;

/**
 *
 * @author Siriquelle
 */
public class GetKeyTerms {

    public static String run(Droplet droplet)
    {
        List<Tweet> tweetList = GetAllTweets.run(droplet);
        List<String> wordList = new LinkedList<String>();
        List<String> hashTagList = new LinkedList<String>();
        Bag wordBag = new HashBag();
        String keyTerms = "";
        Integer keyTermCount = 0;
//
        wordBag = FillWordBagFromTweetList.run(tweetList, wordBag);
        wordList.addAll(wordBag.uniqueSet());

        wordList = RemoveCommonTerms.run(wordList);
        wordList = RemoveConversationParticipants.run(wordList, GetAllPeeps.run(droplet));
//
        hashTagList = ExtractHashTagsFromWordList.run(wordList);
//
        DLog.log(wordList.toString());
        wordList = StemWords.run(wordList);
        DLog.log(wordList.toString());
//
        wordList = RemoveDuplicates.run(wordList);
        wordList = RemoveCommonTerms.run(wordList);

        for (String word : wordList)
        {
            if (!word.isEmpty())
            {
                Integer tempCount = wordBag.getCount(word);
                keyTerms = (word + "," + tempCount + "|") + keyTerms;
            }
        }

        for (String hashTag : hashTagList)
        {
            keyTerms = (keyTerms + " " + hashTag);
        }

        return keyTerms;
    }
}
