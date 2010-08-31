/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.command.conversion;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Siriquelle
 */
public class GetIntegerClassListFromIntTypeArray {

    public static List<Integer> run(int[] values)
    {
        List<Integer> il = new LinkedList<Integer>();

        for (int i : values)
        {
            il.add(i);
        }

        return il;
    }
}
