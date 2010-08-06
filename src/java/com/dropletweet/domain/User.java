/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.domain;

import com.dropletweet.util.DLog;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Twitter User.
 *
 * @author Siriquelle
 */
public class User {

    protected Integer id;
    protected Integer statuses_count;
    protected Integer favourites_count;
    protected Integer followers_count;
    protected Integer utc_offset;
    protected Integer friends_count;
//
    protected Long latest_tweet_id;
//
    protected boolean contributors_enabled;
    protected boolean following;
    protected boolean geo_enabled;
    protected boolean profile_background_tile;
    protected boolean verified;
    protected boolean _protected;
//
    protected String created_at;
    protected String description;
    protected String profile_background_image_url;
    protected String profile_link_color;
    protected String profile_sidebar_fill_color;
    protected String url;
    protected String notifications;
    protected String profile_image_url;
    protected String profile_sidebar_border_color;
    protected String location;
    protected String screen_name;
    protected String time_zone;
    protected String profile_background_color;
    protected String name;
    protected String lang;
    protected String profile_text_color;
    protected String access_token;
    protected String access_token_secret;

    public User()
    {
    }

    public User(Tweet reply)
    {
        screen_name = reply.getFrom_user();
        id = reply.getFrom_user_id();
        location = reply.getLocation();
        profile_image_url = reply.getProfile_image_url();
        latest_tweet_id = reply.getId();
    }

    public User(twitter4j.User user)
    {
        this.id = user.getId();
        this.statuses_count = user.getStatusesCount();
        this.favourites_count = user.getFavouritesCount();
        this.followers_count = user.getFollowersCount();
        this.utc_offset = user.getUtcOffset();
        this.friends_count = user.getFriendsCount();
        this.contributors_enabled = user.isContributorsEnabled();
        this.geo_enabled = user.isContributorsEnabled();
        this.profile_background_tile = user.isProfileBackgroundTiled();
        this.verified = user.isVerified();
        this._protected = user.isProtected();
        this.created_at = user.getCreatedAt().toString();
        this.description = user.getDescription();
        this.profile_background_image_url = user.getProfileBackgroundImageUrl();
        this.profile_link_color = user.getProfileLinkColor();
        this.profile_sidebar_fill_color = user.getProfileSidebarFillColor();
        if (user.getURL() != null)
        {
            this.url = user.getURL().toString();
        }
        this.profile_image_url = user.getProfileImageURL().toString();
        this.profile_sidebar_border_color = user.getProfileSidebarBorderColor();
        this.location = user.getLocation();
        this.screen_name = user.getScreenName();
        this.time_zone = user.getTimeZone();
        this.profile_background_color = user.getProfileBackgroundColor();
        this.name = user.getName();
        this.lang = user.getLang();
        this.profile_text_color = user.getProfileTextColor();
    }

    /**
     *
     * @return the value of _protected
     */
    public boolean get_protected()
    {
        return _protected;
    }

    /**
     *
     * @param _protected
     */
    public void set_protected(boolean _protected)
    {
        this._protected = _protected;
    }

    /**
     *
     * @return the value of contributors_enabled
     */
    public boolean isContributors_enabled()
    {
        return contributors_enabled;
    }

    /**
     *
     * @param contributors_enabled
     */
    public void setContributors_enabled(boolean contributors_enabled)
    {
        this.contributors_enabled = contributors_enabled;
    }

    /**
     *
     * @return the value of created_at
     */
    public String getCreated_at()
    {
        return created_at;
    }

    /**
     *
     * @param created_at
     */
    public void setCreated_at(String created_at)
    {
        this.created_at = created_at;
    }

    /**
     *
     * @return the value of description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     *
     * @return the value of favourites_count
     */
    public Integer getFavourites_count()
    {
        return favourites_count;
    }

    /**
     *
     * @param favourites_count
     */
    public void setFavourites_count(Integer favourites_count)
    {
        this.favourites_count = favourites_count;
    }

    /**
     *
     * @return the value of followers_count
     */
    public Integer getFollowers_count()
    {
        return followers_count;
    }

    /**
     *
     * @param followers_count
     */
    public void setFollowers_count(Integer followers_count)
    {
        this.followers_count = followers_count;
    }

    /**
     *
     * @return the value of following
     */
    public boolean isFollowing()
    {
        return following;
    }

    /**
     *
     * @param following
     */
    public void setFollowing(boolean following)
    {
        this.following = following;
    }

    /**
     *
     * @return the value of friends_count
     */
    public Integer getFriends_count()
    {
        return friends_count;
    }

    /**
     *
     * @param friends_count
     */
    public void setFriends_count(Integer friends_count)
    {
        this.friends_count = friends_count;
    }

    /**
     *
     * @return the value of geo_enabled
     */
    public boolean isGeo_enabled()
    {
        return geo_enabled;
    }

    /**
     *
     * @param geo_enabled
     */
    public void setGeo_enabled(boolean geo_enabled)
    {
        this.geo_enabled = geo_enabled;
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
     * Get the value of latest_tweet_id
     *
     * @return the value of latest_tweet_id
     */
    public Long getLatest_tweet_id()
    {
        return latest_tweet_id;
    }

    /**
     * Set the value of latest_tweet_id
     *
     * @param latest_tweet_id new value of latest_tweet_id
     */
    public void setLatest_tweet_id(Long latest_tweet_id)
    {
        this.latest_tweet_id = latest_tweet_id;
    }

    /**
     *
     * @return the value of lang
     */
    public String getLang()
    {
        return lang;
    }

    /**
     *
     * @param lang
     */
    public void setLang(String lang)
    {
        this.lang = lang;
    }

    /**
     *
     * @return the value of location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     *
     * @param location
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     *
     * @return the value of name
     */
    public String getName()
    {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     *
     * @return the value of notifications
     */
    public String getNotifications()
    {
        return notifications;
    }

    /**
     *
     * @param notifications
     */
    public void setNotifications(String notifications)
    {
        this.notifications = notifications;
    }

    /**
     *
     * @return the value of profile_background_color
     */
    public String getProfile_background_color()
    {
        return profile_background_color;
    }

    /**
     *
     * @param profile_background_color
     */
    public void setProfile_background_color(String profile_background_color)
    {
        this.profile_background_color = profile_background_color;
    }

    /**
     *
     * @return the value of profile_background_image_url
     */
    public String getProfile_background_image_url()
    {
        return profile_background_image_url;
    }

    /**
     *
     * @param profile_background_image_url
     */
    public void setProfile_background_image_url(String profile_background_image_url)
    {
        this.profile_background_image_url = profile_background_image_url;
    }

    /**
     *
     * @return the value of profile_background_tile
     */
    public boolean isProfile_background_tile()
    {
        return profile_background_tile;
    }

    /**
     *
     * @param profile_background_tile
     */
    public void setProfile_background_tile(boolean profile_background_tile)
    {
        this.profile_background_tile = profile_background_tile;
    }

    /**
     *
     * @return the value of profile_image_url
     */
    public String getProfile_image_url()
    {
        return profile_image_url;
    }

    /**
     *
     * @param profile_image_url
     */
    public void setProfile_image_url(String profile_image_url)
    {
        this.profile_image_url = profile_image_url;
    }

    /**
     *
     * @return the value of profile_link_color
     */
    public String getProfile_link_color()
    {
        return profile_link_color;
    }

    /**
     *
     * @param profile_link_color
     */
    public void setProfile_link_color(String profile_link_color)
    {
        this.profile_link_color = profile_link_color;
    }

    /**
     *
     * @return the value of profile_sidebar_border_color
     */
    public String getProfile_sidebar_border_color()
    {
        return profile_sidebar_border_color;
    }

    /**
     *
     * @param profile_sidebar_border_color
     */
    public void setProfile_sidebar_border_color(String profile_sidebar_border_color)
    {
        this.profile_sidebar_border_color = profile_sidebar_border_color;
    }

    /**
     *
     * @return the value of profile_sidebar_fill_color
     */
    public String getProfile_sidebar_fill_color()
    {
        return profile_sidebar_fill_color;
    }

    /**
     *
     * @param profile_sidebar_fill_color
     */
    public void setProfile_sidebar_fill_color(String profile_sidebar_fill_color)
    {
        this.profile_sidebar_fill_color = profile_sidebar_fill_color;
    }

    /**
     *
     * @return the value of profile_text_color
     */
    public String getProfile_text_color()
    {
        return profile_text_color;
    }

    /**
     *
     * @param profile_text_color
     */
    public void setProfile_text_color(String profile_text_color)
    {
        this.profile_text_color = profile_text_color;
    }

    /**
     *
     * @return the value of screen_name
     */
    public String getScreen_name()
    {
        return screen_name;
    }

    /**
     *
     * @param screen_name
     */
    public void setScreen_name(String screen_name)
    {
        this.screen_name = screen_name;
    }

    /**
     *
     * @return the value of statuses_count
     */
    public Integer getStatuses_count()
    {
        return statuses_count;
    }

    /**
     *
     * @param statuses_count
     */
    public void setStatuses_count(Integer statuses_count)
    {
        this.statuses_count = statuses_count;
    }

    /**
     *
     * @return the value of time_zone
     */
    public String getTime_zone()
    {
        return time_zone;
    }

    /**
     *
     * @param time_zone
     */
    public void setTime_zone(String time_zone)
    {
        this.time_zone = time_zone;
    }

    /**
     *
     * @return the value of url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     *
     * @return the value of utc_offset
     */
    public Integer getUtc_offset()
    {
        return utc_offset;
    }

    /**
     *
     * @param utc_offset
     */
    public void setUtc_offset(Integer utc_offset)
    {
        this.utc_offset = utc_offset;
    }

    /**
     *
     * @return the value of verified
     */
    public boolean isVerified()
    {
        return verified;
    }

    /**
     *
     * @param verified
     */
    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }

    /**
     * Get the value of accessToken
     *
     * @return the value of accessToken
     */
    public String getAccess_token()
    {
        return access_token;
    }

    /**
     * Set the value of accessToken
     *
     * @param access_token
     */
    public void setAccess_token(String access_token)
    {
        this.access_token = access_token;
    }

    /**
     * Get the value of access_token_secret
     *
     * @return the value of access_token_secret
     */
    public String getAccess_token_secret()
    {
        return access_token_secret;
    }

    /**
     * Set the value of access_token_secret
     *
     * @param access_token_secret new value of access_token_secret
     */
    public void setAccess_token_secret(String access_token_secret)
    {
        this.access_token_secret = access_token_secret;
    }
}
