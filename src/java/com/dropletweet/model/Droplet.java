/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model;

import com.dropletweet.domain.*;
import java.util.LinkedList;


/**
 * Use this class to create a conversation object in twitter.
 * @author Siriquelle
 */
public class Droplet {

    private Tweet seed;
    /**
     * This is a list of droplets that contains a seed tweet and a wave of responses.
     */
    protected LinkedList<Droplet> wave;

    /**
     * Get the value of wave
     *
     * @return the value of wave
     */
    public LinkedList<Droplet> getWave() {
        return wave;
    }

    /**
     * Set the value of wave
     *
     * @param wave new value of wave
     */
    public void setWave(LinkedList<Droplet> wave) {
        this.wave = wave;
    }

    /**
     * Get the value of seed
     *
     * @return the value of seed
     */
    public Tweet getSeed() {
        return seed;
    }

    /**
     * Set the value of seed
     *
     * @param seed new value of seed
     */
    public void setSeed(Tweet seed) {
        this.seed = seed;
    }
}
