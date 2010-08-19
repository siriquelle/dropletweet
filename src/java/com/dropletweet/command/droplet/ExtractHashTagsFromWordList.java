/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.command.text.RemovePunctuation;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class ExtractHashTagsFromWordList {

    public static List<String> run(List<String> wordList)
    {
        List<String> hashTags = new LinkedList<String>();
        Iterator iter = wordList.iterator();
        while (iter.hasNext())
        {
            String word = (String) iter.next();

            if (word.startsWith("#"))
            {
                iter.remove();
                hashTags.add(RemovePunctuation.run(word));
            }
        }

        return hashTags;
    }
}
