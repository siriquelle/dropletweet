/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.domain.Tweet;
import com.dropletweet.model.Droplet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

/**
 *
 * @author Siriquelle
 */
public class DropletUtil {

    public static Porter porter = new Porter();

    /**
     * 
     * @param key 
     * @param href
     * @param tweet
     * @return
     */
    public static List getAllTweets(Droplet droplet)
    {
        List<Tweet> tweetList = new LinkedList<Tweet>();
        tweetList.add(droplet.getSeed());
        return fillTweetListFromWave(droplet.getWave(), tweetList);
    }

    public static List getAllPeeps(Droplet droplet)
    {
        List<String> peepList = new LinkedList<String>();
        peepList.add(droplet.getSeed().getFrom_user());
        return removeDuplicates(fillPeepListFromWave(droplet.getWave(), peepList));
    }

    public static String getKeyTerm(Droplet droplet)
    {
        List<Tweet> tweetList = getAllTweets(droplet);
        Bag termList = new HashBag();

        String mostFrequentWord = "";
        Integer mostFrequentCount = 0;
        for (Tweet tweet : tweetList)
        {
            String[] words = tweet.getText().split(" ");
            for (String word : words)
            {
                if (word.length() > 0)
                {
                    termList.add(porter.stripAffixes(word));
                }
            }
        }

        List<String> wordList = new LinkedList<String>();
        wordList.addAll(termList);
        wordList = removeDuplicates(wordList);
        wordList = removeCommonTerms(wordList);
        wordList = removeConversationParticipants(wordList, getAllPeeps(droplet));
        for (String word : wordList)
        {
            Integer tempCount = termList.getCount(word);

            if (tempCount > mostFrequentCount)
            {
                mostFrequentCount = tempCount;
                mostFrequentWord = word;
            } else if (tempCount == mostFrequentCount)
            {
                mostFrequentWord = (mostFrequentWord + ", " + word);
            }
        }

        return mostFrequentWord;
    }

    private static List<Tweet> fillTweetListFromWave(LinkedList<Droplet> wave, List<Tweet> tweetList)
    {
        for (int i = 0; i < wave.size(); i++)
        {
            tweetList.add(wave.get(i).getSeed());
            if (wave.get(i).getWave().size() > 0)
            {
                fillTweetListFromWave(wave.get(i).getWave(), tweetList);
            }
        }
        return tweetList;
    }

    private static List<String> fillPeepListFromWave(LinkedList<Droplet> wave, List<String> peepList)
    {
        for (int i = 0; i < wave.size(); i++)
        {
            peepList.add(wave.get(i).getSeed().getFrom_user());
            if (wave.get(i).getWave().size() > 0)
            {
                fillPeepListFromWave(wave.get(i).getWave(), peepList);
            }
        }
        return peepList;
    }

    public static List<String> removeDuplicates(List<String> peepList)
    {

        HashSet h = new HashSet(peepList);
        peepList.clear();
        peepList.addAll(h);

        return peepList;
    }

    private static List<String> removeCommonTerms(List<String> wordList)
    {
        Iterator iter = wordList.iterator();
        while (iter.hasNext())
        {
            String word = (String) iter.next();
            for (String stop : TextUtil.STOP_WORDS_EN)
            {
                if (word.equals(stop))
                {
                    iter.remove();
                    break;
                }
            }
        }
        return wordList;
    }

    private static List<String> removeConversationParticipants(List<String> wordList, List<String> peepList)
    {
        Iterator iter = wordList.iterator();

        while (iter.hasNext())
        {
            String word = (String) iter.next();
            for (String peep : peepList)
            {
                if (word.equals(peep))
                {
                    iter.remove();
                    break;
                }
            }
        }

        return wordList;
    }
}
