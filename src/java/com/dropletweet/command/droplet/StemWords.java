/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.constants.WordArrays;
import com.dropletweet.log.DLog;
import java.util.List;
import ptstemmer.Stemmer;
import ptstemmer.Stemmer.StemmerType;
import ptstemmer.exceptions.PTStemmerException;

/**
 *
 * @author Siriquelle
 */
public class StemWords {

    private static Stemmer stemmer;

    static
    {
        try
        {
            stemmer = Stemmer.StemmerFactory(StemmerType.PORTER);
            stemmer.enableCaching(1000);
            stemmer.ignore(WordArrays.STOP_WORDS_EN);
            stemmer.ignore(WordArrays.IGNORE_WORDS_EN);
        } catch (PTStemmerException ex)
        {
            DLog.log(ex.getMessage());
        }
    }

    public static List<String> run(List<String> wordList)
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
}
