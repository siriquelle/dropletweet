/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model;

import java.util.LinkedList;

/**
 *
 * @author Siriquelle
 */
public class BoundingBox {

    public BoundingBox()
    {
        System.out.println("CREATING AN ATTRIBUTE OBJECT");
    }
    private String type;
    private LinkedList<LinkedList<LinkedList<Double>>> coordinates;

    /**
     * Get the value of coordinates
     *
     * @return the value of coordinates
     */
    public LinkedList<LinkedList<LinkedList<Double>>> getCoordinates()
    {
        return coordinates;
    }

    /**
     * Set the value of coordinates
     *
     * @param coordinates new value of coordinates
     */
    public void setCoordinates(LinkedList<LinkedList<LinkedList<Double>>> coordinates)
    {
        this.coordinates = coordinates;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(String type)
    {
        this.type = type;
    }
}
