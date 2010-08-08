/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.util;

import com.dropletweet.log.DLog;
import com.dropletweet.domain.Tweet;
import com.dropletweet.model.Droplet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import ptstemmer.Stemmer;
import ptstemmer.Stemmer.StemmerType;
import ptstemmer.exceptions.PTStemmerException;

/**
 *
 * @author Siriquelle
 */
public class DropletUtil {

    public static Stemmer stemmer;

    static
    {
        try
        {
            stemmer = Stemmer.StemmerFactory(StemmerType.PORTER);
            stemmer.enableCaching(1000);
            stemmer.ignore(TextUtil.STOP_WORDS_EN);
            stemmer.ignore(TextUtil.IGNORE_WORDS_EN);
        } catch (PTStemmerException ex)
        {
            Logger.getLogger(DropletUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    public static String getKeyTerms(Droplet droplet)
    {
        List<Tweet> tweetList = getAllTweets(droplet);
        List<String> wordList = new LinkedList<String>();
        List<String> hashTagList = new LinkedList<String>();
        Bag wordBag = new HashBag();
        String keyTerms = "";
        Integer keyTermCount = 0;
//
        wordBag = fillWordBagFromTweetList(tweetList, wordBag);
//
        wordList.addAll(wordBag);
        wordList = removeDuplicates(wordList);
        wordList = removeCommonTerms(wordList);
        wordList = removeConversationParticipants(wordList, getAllPeeps(droplet));
//
        hashTagList = extractHashTagsFromWordList(wordList);
//
        DLog.log(wordList.toString());
        wordList = stemWords(wordList);
        DLog.log(wordList.toString());
//
        wordList = removeDuplicates(wordList);
        wordList = removeCommonTerms(wordList);

        for (String word : wordList)
        {
            Integer tempCount = wordBag.getCount(word);

            if (tempCount > keyTermCount)
            {
                keyTermCount = tempCount;
                keyTerms = word;
            } else if (tempCount == keyTermCount)
            {
                keyTerms = (keyTerms + " " + word);
            }
        }

        for (String hashTag : hashTagList)
        {
            keyTerms = (keyTerms + " " + hashTag);
        }

        return keyTerms;
    }

    private static Bag fillWordBagFromTweetList(List<Tweet> tweetList, Bag wordBag)
    {
        for (Tweet tweet : tweetList)
        {
            String[] words = tweet.getText().split(" ");
            for (String word : words)
            {
                if (word.length() > 0)
                {
                    wordBag.add(TextUtil.removePunctuation(word).toLowerCase());
                }
            }
        }
        return wordBag;
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
            peepList.add(wave.get(i).getSeed().getFrom_user().trim());
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
                if (word.toLowerCase().equals(peep.toLowerCase()) || word.toLowerCase().equals("@" + peep.toLowerCase()))
                {
                    iter.remove();
                    break;
                }
            }
        }

        return wordList;
    }

    private static List<String> stemWords(List<String> wordList)
    {

        for (int i = 0; i < wordList.size(); i++)
        {
            if (stemmer != null && wordList.get(i) != null && wordList.get(i).length() > 0)
            {
                wordList.set(i, stemmer.getWordStem(wordList.get(i)));
            }
        }

        return wordList;
    }

    private static List<String> extractHashTagsFromWordList(List<String> wordList)
    {
        List<String> hashTags = new LinkedList<String>();
        Iterator iter = wordList.iterator();
        while (iter.hasNext())
        {
            String word = (String) iter.next();
            if (word.startsWith("#"))
            {
                iter.remove();
                hashTags.add(TextUtil.removePunctuation(word));
            }
        }

        return hashTags;
    }
}
