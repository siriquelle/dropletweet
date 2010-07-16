/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.domain;

/**
 * This class represents a Twitter User.
 *
 * @author Siriquelle
 */
public class Signup {

    protected Integer id;
    protected String email;

    public Signup()
    {
    }

    /**
     *
     * @return the value of id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     *
     * @return the value of created_at
     */
    public String getEmail()
    {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email)
    {
        this.email = email;
    }
}
