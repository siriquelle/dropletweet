/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model;

/**
 *
 * @author Siriquelle
 */
public class Attribute {

    public Attribute()
    {
        System.out.println("CREATING AN ATTRIBUTE OBJECT");
    }
    private String street_address;

    /**
     * Get the value of street_address
     *
     * @return the value of street_address
     */
    public String getStreet_address()
    {
        return street_address;
    }

    /**
     * Set the value of street_address
     *
     * @param street_address new value of street_address
     */
    public void setStreet_address(String street_address)
    {
        this.street_address = street_address;
    }
}
    
