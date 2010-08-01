/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dropletweet.model.bean;

/**
 *
 * @author Siriquelle
 */
public class AjaxConversationStatisticsBean {

    protected Integer tweetCount;
    protected Integer peepCount;
    
    /**
     * Get the value of peepCount
     *
     * @return the value of peepCount
     */
    public Integer getPeepCount()
    {
        return peepCount;
    }

    /**
     * Set the value of peepCount
     *
     * @param peepCount new value of peepCount
     */
    public void setPeepCount(Integer peepCount)
    {
        this.peepCount = peepCount;
    }

    /**
     * Get the value of tweetCount
     *
     * @return the value of tweetCount
     */
    public Integer getTweetCount()
    {
        return tweetCount;
    }

    /**
     * Set the value of tweetCount
     *
     * @param tweetCount new value of tweetCount
     */
    public void setTweetCount(Integer tweetCount)
    {
        this.tweetCount = tweetCount;
    }


}
