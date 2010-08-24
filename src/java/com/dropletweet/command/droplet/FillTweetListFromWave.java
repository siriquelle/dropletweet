/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.model.Droplet;
import com.dropletweet.domain.Tweet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class FillTweetListFromWave {

    public static List<Tweet> run(LinkedList<Droplet> wave, List<Tweet> tweetList)
    {
        for (int i = 0; i < wave.size(); i++)
        {
            tweetList.add(wave.get(i).getSeed());

            if (wave != null && wave.get(i) != null && wave.get(i).getWave() != null && wave.get(i).getWave().size() > 0)
            {
                FillTweetListFromWave.run(wave.get(i).getWave(), tweetList);
            }
        }
        return tweetList;
    }
}
