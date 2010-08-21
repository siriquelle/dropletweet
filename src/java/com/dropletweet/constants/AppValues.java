/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.constants;

/**
 *
 * @author Siriquelle
 */
public class AppValues {

    private AppValues()
    {
    }
    //
    public static final String COOKIE_KEY_ACCESS_TOKEN = "accessToken";
    //
    public static final String VALUE_SEPARATOR_UNDERSCORE = "_";
    //
    public static final String VIEW_INDEX_NAME = "index.htm";
    public static final String VIEW_DROPLET_NAME = "droplet.htm";
    public static final String VIEW_SIGNIN_NAME = "signin.htm";
    //
    public static final String VIEW_VALUE_INDEX = "index";
    public static final String VIEW_VALUE_DROPLET = "droplet";
    //
    public static final String AJAX_STATUSLIST_NAME = "statuslist.ajax";
    public static final String AJAX_TWEET_NAME = "tweet.ajax";
    public static final String AJAX_USER_NAME = "user.ajax";
    public static final String AJAX_UTIL_NAME = "util.ajax";
    //
    public static final String AJAX_OUT_LOCATION_STATUSLIST = "ajax/statuslist";
    public static final String AJAX_OUT_LOCATION_ACTIONS = "ajax/actions";
    public static final String AJAX_OUT_LOCATION_UTIL = "ajax/util";
    public static final String AJAX_OUT_LOCATION_USER = "ajax/user";
    //
    public static final String MODELMAP_VIEW_VALUE_REDIRECT_PREFIX = "redirect:";
    public static final String MODELMAP_VIEW_VALUE_REDIRECT_INDEX = "redirect:/index.htm";
    public static final String MODELMAP_VIEW_VALUE_REDIRECT_DROPLET = "redirect:/droplet.htm";
    //
    public static final String MODELMAP_KEY_VIEW = "view";
    public static final String MODELMAP_KEY_USER = "user";
    public static final String MODELMAP_KEY_MODELMAP = "modelMap";
    public static final String MODELMAP_KEY_AJAX_TWEET_ACTION_BEAN = "ajaxTweetActionBean";
    public static final String MODELMAP_KEY_AJAX_UTIL_BEAN = "ajaxUtilBean";
    public static final String MODELMAP_KEY_AJAX_USER_BEAN = "ajaxUserBean";
    //
    public static final String SESSION_KEY_USER = "user";
    public static final String SESSION_KEY_REQUEST_TOKEN = "requestToken";
    public static final String SESSION_KEY_MODELMAP = "modelMap";
    public static final String SESSION_KEY_TWITTER = "twitter";
    //
    public static final String REQUEST_KEY_LOGOUT = "logout";
    public static final String REQUEST_KEY_ACTION = "action";
    public static final String REQUEST_KEY_Q = "q";
    public static final String REQUEST_KEY_LIST_TYPE = "listType";
    public static final String REQUEST_KEY_TWEET_ID = "tweetId";
    public static final String REQUEST_KEY_TWEET_TEXT = "tweet_text";
    public static final String REQUEST_KEY_IN_REPLY_TO_ID = "in_reply_to_id";
    public static final String REQUEST_KEY_USER_ID = "userId";
    public static final String REQUEST_KEY_SCREEN_NAME = "screen_name";
    //
    public static final String Q_VALUE_FROM = "from:";
    //
    public static final String ACTION_VALUE_MORE = "more";
    //
    public static final String LIST_NAME_FRIENDS_LIST = "friendsList";
    public static final String LIST_NAME_REPLY_LIST = "replyList";
    public static final String LIST_NAME_DM_LIST = "dmList";
    public static final String LIST_NAME_DM_SENT_LIST = "dmSentList";
    public static final String LIST_NAME_FAVOURITES_LIST = "favouritesList";
    public static final String LIST_NAME_SENT_LIST = "sentList";
    public static final String LIST_NAME_CONVERSATION_LIST = "conversationList";
    public static final String LIST_NAME_DISCUSSION_LIST = "discussionList";
    public static final String LIST_NAME_RETWEET_LIST = "retweetList";
    public static final String LIST_NAME_TWEET_LIST = "tweetList";
    public static final String LIST_NAME_SEARCH = "search";
    //
    public static final String TWEET_ACTION_VALUE_RETWEET = "retweet";
    public static final String TWEET_ACTION_VALUE_POST = "post";
    public static final String TWEET_ACTION_VALUE_DELETE = "delete";
    public static final String TWEET_ACTION_VALUE_FAVOURITE = "favourite";
    public static final String TWEET_ACTION_VALUE_SPAM = "spam";
    public static final String TWEET_ACTION_VALUE_TRACK = "track";
    public static final String TWEET_ACTION_VALUE_FOLLOW = "follow";
    //
    public static final String UTIL_ACTION_VALUE_GET_LATEST_URL = "get_latest_url";
    public static final String UTIL_ACTION_VALUE_GET_LOADING_STATUS = "get_loading_status";
    //
    public static final String USER_ACTION_VALUE_GET_USER_INFO = "get_user_info";
    //
    public static final String SERVLET_CONTEXT_KEY_LATEST_URL = "latest_url";
}
