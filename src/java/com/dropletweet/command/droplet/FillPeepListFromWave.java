/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import com.dropletweet.model.Droplet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class FillPeepListFromWave {

    public static List<String> run(LinkedList<Droplet> wave, List<String> peepList)
    {
        for (int i = 0; i < wave.size(); i++)
        {
            peepList.add(wave.get(i).getSeed().getFrom_user().toLowerCase().trim());
            if (wave.get(i).getWave().size() > 0)
            {
                FillPeepListFromWave.run(wave.get(i).getWave(), peepList);
            }
        }
        return peepList;
    }
}
