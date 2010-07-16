/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model;

import com.dropletweet.domain.*;

/**
 *
 * @author Siriquelle
 */
public class Single {

    private Boolean truncated;
    private String created_at;
    private Integer in_reply_to_status_id;
    private Coordinate geo;
    private String contributors;
    private Place place;
    private String source;
    private String in_reply_to_screen_name;
    private Coordinate coordinate;
    private User user;
    private Long id;
    private Boolean favorited;
    private String text;
    private Integer in_reply_to_user_id;

    /**
     * Get the value of in_reply_to_user_id
     *
     * @return the value of in_reply_to_user_id
     */
    public Integer getIn_reply_to_user_id()
    {
        return in_reply_to_user_id;
    }

    /**
     * Set the value of in_reply_to_user_id
     *
     * @param in_reply_to_user_id new value of in_reply_to_user_id
     */
    public void setIn_reply_to_user_id(Integer in_reply_to_user_id)
    {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    /**
     * Get the value of text
     *
     * @return the value of text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Set the value of text
     *
     * @param text new value of text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Get the value of favorited
     *
     * @return the value of favorited
     */
    public Boolean getFavorited()
    {
        return favorited;
    }

    /**
     * Set the value of favorited
     *
     * @param favorited new value of favorited
     */
    public void setFavorited(Boolean favorited)
    {
        this.favorited = favorited;
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Get the value of user
     *
     * @return the value of user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * Set the value of user
     *
     * @param user new value of user
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     *
     * @return
     */
    public String getContributors()
    {
        return contributors;
    }

    /**
     *
     * @param contributors
     */
    public void setContributors(String contributors)
    {
        this.contributors = contributors;
    }

    /**
     *
     * @return
     */
    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    /**
     *
     * @param coordinate
     */
    public void setCoordinate(Coordinate coordinate)
    {
        this.coordinate = coordinate;
    }

    /**
     *
     * @return
     */
    public String getIn_reply_to_screen_name()
    {
        return in_reply_to_screen_name;
    }

    /**
     *
     * @param in_reply_to_screen_name
     */
    public void setIn_reply_to_screen_name(String in_reply_to_screen_name)
    {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    /**
     *
     * @return
     */
    public Place getPlace()
    {
        return place;
    }

    /**
     *
     * @param place
     */
    public void setPlace(Place place)
    {
        this.place = place;
    }

    /**
     *
     * @return
     */
    public String getSource()
    {
        return source;
    }

    /**
     *
     * @param source
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * Get the value of in_reply_to_status_id
     *
     * @return the value of in_reply_to_status_id
     */
    public Integer getIn_reply_to_status_id()
    {
        return in_reply_to_status_id;
    }

    /**
     * Set the value of in_reply_to_status_id
     *
     * @param in_reply_to_status_id new value of in_reply_to_status_id
     */
    public void setIn_reply_to_status_id(Integer in_reply_to_status_id)
    {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    /**
     * Get the value of created_at
     *
     * @return the value of created_at
     */
    public String getCreated_at()
    {
        return created_at;
    }

    /**
     * Set the value of created_at
     *
     * @param created_at new value of created_at
     */
    public void setCreated_at(String created_at)
    {
        this.created_at = created_at;
    }

    /**
     * Get the value of truncated
     *
     * @return the value of truncated
     */
    public Boolean getTruncated()
    {
        return truncated;
    }

    /**
     * Set the value of truncated
     *
     * @param truncated new value of truncated
     */
    public void setTruncated(Boolean truncated)
    {
        this.truncated = truncated;
    }

    /**
     * Get the value of geo
     *
     * @return the value of geo
     */
    public Coordinate getGeo()
    {
        return geo;
    }

    /**
     * Set the value of geo
     *
     * @param geo new value of geo
     */
    public void setGeo(Coordinate geo)
    {
        this.geo = geo;
    }
}
