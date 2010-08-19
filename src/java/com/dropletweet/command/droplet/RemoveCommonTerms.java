/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.constants.WordArrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class RemoveCommonTerms {

    public static List<String> run(List<String> wordList)
    {
        Iterator iter = wordList.iterator();
        while (iter.hasNext())
        {
            String word = (String) iter.next();
            Boolean wordFound = false;
            for (String stop : WordArrays.STOP_WORDS_EN)
            {
                if (word.equals(stop))
                {
                    iter.remove();
                    wordFound = true;
                    break;
                }
            }
            if (!wordFound)
            {
                for (String stop : WordArrays.COMMON_WORDS_EN)
                {
                    if (word.equals(stop))
                    {
                        iter.remove();
                        break;
                    }
                }
            }
        }
        return wordList;
    }
}
