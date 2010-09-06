/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model.bean;

import java.net.URL;
import twitter4j.User;

/**
 *
 * @author Siriquelle
 */
public class AjaxUserBean {

    private String screenName;
    private URL profileImageUrl;
    private String description;
    private String location;
    private Integer following;
    private Integer followers;
    private Integer tweets;
    private Integer hits;
    private Integer dmCount;
    private Integer mentionsCount;

    public AjaxUserBean()
    {
    }

    public AjaxUserBean(User user)
    {
        this.screenName = user.getScreenName().replaceAll("\\n*\\r*", "");
        this.profileImageUrl = user.getProfileImageURL();
        this.description = user.getDescription().replaceAll("\\n*\\r*", "");
        this.location = user.getLocation().replaceAll("\\n*\\r*", "");
        this.following = user.getFriendsCount();
        this.followers = user.getFollowersCount();
        this.tweets = user.getStatusesCount();
        this.hits = user.getRateLimitStatus().getRemainingHits();
    }

    public Integer getTweets()
    {
        return tweets;
    }

    public void setTweets(Integer Tweets)
    {
        this.tweets = Tweets;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getFollowers()
    {
        return followers;
    }

    public void setFollowers(Integer followers)
    {
        this.followers = followers;
    }

    public Integer getFollowing()
    {
        return following;
    }

    public void setFollowing(Integer following)
    {
        this.following = following;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public URL getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(URL profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public String getScreenName()
    {
        return screenName;
    }

    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }

    /**
     * Get the value of hits
     *
     * @return the value of hits
     */
    public Integer getHits()
    {
        return hits;
    }

    /**
     * Set the value of hits
     *
     * @param hits new value of hits
     */
    public void setHits(Integer hits)
    {
        this.hits = hits;
    }

    /**
     * Get the value of mentionsCount
     *
     * @return the value of mentionsCount
     */
    public Integer getMentionsCount()
    {
        return mentionsCount;
    }

    /**
     * Set the value of mentionsCount
     *
     * @param mentionsCount new value of mentionsCount
     */
    public void setMentionsCount(Integer mentionsCount)
    {
        this.mentionsCount = mentionsCount;
    }

    /**
     * Get the value of dmCount
     *
     * @return the value of dmCount
     */
    public Integer getDmCount()
    {
        return dmCount;
    }

    /**
     * Set the value of dmCount
     *
     * @param dmCount new value of dmCount
     */
    public void setDmCount(Integer dmCount)
    {
        this.dmCount = dmCount;
    }

    public void setUser(twitter4j.User user)
    {
        this.screenName = (user.getScreenName() != null) ? user.getScreenName().replaceAll("\\n*\\r*", "") : null;
        this.profileImageUrl = user.getProfileImageURL();
        this.description = (user.getDescription() != null) ? user.getDescription().replaceAll("\\n*\\r*", "") : null;
        this.location = (user.getLocation() != null) ? user.getLocation().replaceAll("\\n*\\r*", "") : null;
        this.following = user.getFriendsCount();
        this.followers = user.getFollowersCount();
        this.tweets = user.getStatusesCount();
        this.hits = user.getRateLimitStatus().getRemainingHits();
    }
}
