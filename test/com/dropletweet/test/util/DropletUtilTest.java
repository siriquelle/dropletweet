/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.test.util;

import com.dropletweet.util.Porter;
import com.dropletweet.util.TextUtil;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import ptstemmer.Stemmer;
import ptstemmer.Stemmer.StemmerType;
import ptstemmer.exceptions.PTStemmerException;

/**
 *
 * @author Siriquelle
 */
public class DropletUtilTest {

    public static Stemmer stemmer;

    static
    {
        try
        {
            stemmer = Stemmer.StemmerFactory(StemmerType.PORTER);
            stemmer.enableCaching(1000);
            stemmer.ignore(TextUtil.STOP_WORDS_EN);
        } catch (PTStemmerException ex)
        {
            Logger.getLogger(DropletUtilTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testStemWords()
    {
        List<String> wordList = new LinkedList<String>();
        wordList.addAll(Arrays.asList(new String[]
                {
                    "ENJOY", "ONE", "", "SWIMMER", "HARDER", "SIX", "I", "JOE", "@JOE"
                }));

        stemWords(wordList);

    }

    private static List<String> stemWords(List<String> wordList)
    {

        for (int i = 0; i < wordList.size(); i++)
        {
            wordList.set(i, stemmer.getWordStem(wordList.get(i)));
        }

        return wordList;
    }
}