/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.domain;

import java.io.Serializable;

/**
 * This class represents a Twitter User.
 *
 * @author Siriquelle
 */
public class Conversation implements Serializable {

    protected Integer id;
    protected User user;
    protected Tweet tweet;

    public Conversation()
    {
    }

    public Conversation(User user, Tweet tweet)
    {
        this.user = user;
        this.tweet = tweet;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Tweet getTweet()
    {
        return tweet;
    }

    public void setTweet(Tweet tweet)
    {
        this.tweet = tweet;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
