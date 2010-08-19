/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.droplet;

import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class RemoveDuplicates {

    public static List<String> run(List<String> list)
    {

        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);

        return list;
    }
}
