/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.command.droplet;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class RemoveConversationParticipants {

    public static List<String> run(List<String> wordList, List<String> peepList)
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
}
