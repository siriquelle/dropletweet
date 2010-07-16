/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.domain;

import com.dropletweet.model.Single;
import java.util.Date;
import twitter4j.Status;

/**
 * This class represents a single tweet
 * @author Siriquelle
 */
public class Tweet {

    protected Long id;
    protected Long in_reply_to_id;
    protected String from_user;
    protected Integer from_user_id;
    protected String to_user;
    protected Integer to_user_id;
    protected String source;
    protected String profile_image_url;
    protected String text;
    protected String created_at;
    protected String iso_language_code;
    protected String location;
    protected Date updated;

    /**
     * The default constructor can be used to create a tweet without any default values.
     */
    public Tweet()
    {
    }

    /**
     * This constructor takes in a Single object, a class that represents a single tweet query and parses
     * a single tweet object form it.
     * @param seed
     */
    public Tweet(Single seed)
    {
        this.profile_image_url = seed.getUser().getProfile_image_url();
        this.created_at = seed.getCreated_at();
        this.from_user = seed.getUser().getScreen_name();
        this.to_user_id = seed.getIn_reply_to_user_id();
        this.text = seed.getText();
        this.id = seed.getId();
        this.from_user_id = seed.getUser().getId();
        this.to_user = seed.getIn_reply_to_screen_name();
        this.iso_language_code = seed.getUser().getLang();
        this.source = seed.getSource();
        this.location = seed.getUser().getLocation();
    }

    public Tweet(Status status)
    {
        this.id = status.getId();
        this.in_reply_to_id = status.getInReplyToStatusId();
        this.from_user = status.getUser().getScreenName();
        this.from_user_id = status.getUser().getId();
        this.to_user = status.getInReplyToScreenName();
        this.to_user_id = status.getInReplyToUserId();
        this.source = status.getSource();
        this.profile_image_url = status.getUser().getProfileImageURL().toString();
        this.text = status.getText();
        this.created_at = status.getCreatedAt().toString();
        this.iso_language_code = status.getUser().getLang();
        this.location = status.getUser().getLocation();
    }

    /**
     * Get the value of location
     *
     * @return the value of location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Set the value of location
     *
     * @param location new value of location
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Get the value of source
     *
     * @return the value of source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Set the value of source
     *
     * @param source new value of source
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * Get the value of iso_language_code
     *
     * @return the value of iso_language_code
     */
    public String getIso_language_code()
    {
        return iso_language_code;
    }

    /**
     * Set the value of iso_language_code
     *
     * @param iso_language_code new value of iso_language_code
     */
    public void setIso_language_code(String iso_language_code)
    {
        this.iso_language_code = iso_language_code;
    }

    /**
     * Get the value of to_user
     *
     * @return the value of to_user
     */
    public String getTo_user()
    {
        return to_user;
    }

    /**
     * Set the value of to_user
     *
     * @param to_user new value of to_user
     */
    public void setTo_user(String to_user)
    {
        this.to_user = to_user;
    }

    /**
     * Get the value of from_user_id
     *
     * @return the value of from_user_id
     */
    public Integer getFrom_user_id()
    {
        return from_user_id;
    }

    /**
     * Set the value of from_user_id
     *
     * @param from_user_id new value of from_user_id
     */
    public void setFrom_user_id(Integer from_user_id)
    {
        this.from_user_id = from_user_id;
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
     * Get the value of in_reply_to_id
     *
     * @return the value of in_reply_to_id
     */
    public Long getIn_reply_to_id()
    {
        return in_reply_to_id;
    }

    /**
     * Set the value of in_reply_to_id
     *
     * @param in_reply_to_id new value of in_reply_to_id
     */
    public void setIn_reply_to_id(Long in_reply_to_id)
    {
        this.in_reply_to_id = in_reply_to_id;
    }

    /**
     * Get the value of to_user_id
     *
     * @return the value of to_user_id
     */
    public Integer getTo_user_id()
    {
        return to_user_id;
    }

    /**
     * Set the value of to_user_id
     *
     * @param to_user_id new value of to_user_id
     */
    public void setTo_user_id(Integer to_user_id)
    {
        this.to_user_id = to_user_id;
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
     * Get the value of from_user
     *
     * @return the value of from_user
     */
    public String getFrom_user()
    {
        return from_user;
    }

    /**
     * Set the value of from_user
     *
     * @param from_user new value of from_user
     */
    public void setFrom_user(String from_user)
    {
        this.from_user = from_user;
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
     * Get the value of profile_image_url
     *
     * @return the value of profile_image_url
     */
    public String getProfile_image_url()
    {
        return profile_image_url;
    }

    /**
     * Set the value of profile_image_url
     *
     * @param profile_image_url new value of profile_image_url
     */
    public void setProfile_image_url(String profile_image_url)
    {
        this.profile_image_url = profile_image_url;
    }

    /**
     * Get the value of updated
     *
     * @return the value of updated
     */
    public Date getUpdated()
    {
        return updated;
    }

    /**
     * Set the value of updated
     *
     * @param updated new value of updated
     */
    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }
}
