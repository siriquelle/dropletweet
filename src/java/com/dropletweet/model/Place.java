/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model;

/**
 *
 * @author Siriquelle
 */
public class Place {

    private String country;
    private Attribute attributes;
    private String country_code;
    private String full_name;
    private BoundingBox bounding_box;
    private String name;
    private String id;
    private String place_type;
    private String url;

    /**
     * Get the value of url
     *
     * @return the value of url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Set the value of url
     *
     * @param url new value of url
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Get the value of place_type
     *
     * @return the value of place_type
     */
    public String getPlace_type()
    {
        return place_type;
    }

    /**
     * Set the value of place_type
     *
     * @param place_type new value of place_type
     */
    public void setPlace_type(String place_type)
    {
        this.place_type = place_type;
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the value of bounding_box
     *
     * @return the value of bounding_box
     */
    public BoundingBox getBounding_box()
    {
        return bounding_box;
    }

    /**
     * Set the value of bounding_box
     *
     * @param bounding_box new value of bounding_box
     */
    public void setBounding_box(BoundingBox bounding_box)
    {
        this.bounding_box = bounding_box;
    }

    /**
     * Get the value of full_name
     *
     * @return the value of full_name
     */
    public String getFull_name()
    {
        return full_name;
    }

    /**
     * Set the value of full_name
     *
     * @param full_name new value of full_name
     */
    public void setFull_name(String full_name)
    {
        this.full_name = full_name;
    }

    /**
     * Get the value of country_code
     *
     * @return the value of country_code
     */
    public String getCountry_code()
    {
        return country_code;
    }

    /**
     * Set the value of country_code
     *
     * @param country_code new value of country_code
     */
    public void setCountry_code(String country_code)
    {
        this.country_code = country_code;
    }

    /**
     * Get the value of attributes
     *
     * @return the value of attributes
     */
    public Attribute getAttributes()
    {
        return attributes;
    }

    /**
     * Set the value of attributes
     *
     * @param attributes new value of attributes
     */
    public void setAttributes(Attribute attributes)
    {
        this.attributes = attributes;
    }

    /**
     * Get the value of country
     *
     * @return the value of country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * Set the value of country
     *
     * @param country new value of country
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
}
