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
public class GetAllPeeps {

    public static List run(Droplet droplet)
    {
        List<String> peepList = new LinkedList<String>();
        peepList.add(droplet.getSeed().getFrom_user().toLowerCase().trim());
        return RemoveDuplicates.run(FillPeepListFromWave.run(droplet.getWave(), peepList));
    }
}
