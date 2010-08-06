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

    public AjaxUserBean()
    {
    }

    public AjaxUserBean(User user)
    {
        screenName = user.getScreenName();
        profileImageUrl = user.getProfileImageURL();
        description = user.getDescription();
        location = user.getLocation();
        following = user.getFriendsCount();
        followers = user.getFollowersCount();
        tweets = user.getStatusesCount();
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
}
