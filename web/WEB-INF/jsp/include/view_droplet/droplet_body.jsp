<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<div class="user_info_action">
    <a id="user_info_hide_show" class="user_info_show" href="##" title="Show/Hide user info."></a>
</div>
<section class="user_info" id="user_info_container">
    <div class="user_info_profile_image">
        <img id="profile_image_url" alt="<c:out value="${modelMap.user.screenName}" />"  src="<c:out value="${modelMap.user.profileImageUrl}" />" width="48px" height="48px"/>
    </div>
    <div class="user_info_screen_name" id="screen_name">
        <c:out value="${modelMap.user.screeName}" />
    </div>
    <div class="user_info_details_container">
        <div class="user_info_details_item" id="description">
            <c:out value="${modelMap.user.description}" />
        </div>
        <div class="user_info_details_item" id="location">
            <c:out value="${modelMap.user.location}" />
        </div>
    </div>


    <details class="user_info_count_container">
        <div class="user_info_count" >
            <div class="user_info_count_head">Following</div>
            <div class="user_info_count_number"  id="friends_count"><c:out value="${modelMap.user.friendsCount}" /></div>
        </div>
        <div class="user_info_count" >
            <div class="user_info_count_head">Followers</div>
            <div class="user_info_count_number" id="followers_count"><c:out value="${modelMap.user.followersCount}" /></div>
        </div>
        <div class="user_info_count" >
            <div class="user_info_count_head">Tweets</div>
            <div class="user_info_count_number" id="statuses_count"><c:out value="${modelMap.user.statusesCount}" /></div>
        </div>
    </details>
</section>

<section class="actions">

    <div class="action" id="friendsList"><a href="##" title="Tweets from People you Follow"></a></div>
    <div class="action" id="replyList"><a href="##" title="Replies and Mentions"></a></div>
    <div class="action" id="dmList"><a href="##" title="Direct Messages"></a></div>
    <div class="action" id="favouritesList"><a href="##" title="Your Favourite Tweets"></a></div>
    <div class="action" id="sentList"><a href="##" title="Your Sent Tweets"></a></div>
    <div class="action" id="discussionList"><a href="##" title="Replies from People you Follow"></a></div>
    <div class="action_search" id="search"><a href="##" title="Search" id="search_a"></a></div>
    <div id="action_search_container_outer">
        <div class="action_search_container_inner">
            <input type="text" id="search_txt"/>
            <a href="##" id="search_btn"></a>
        </div>
    </div>
    <div class="action_conversations" id="conversationList"><a href="##" title="List Conversations Being Tracked By You" ></a></div>


</section>

<section class="new_tweet_container">
    <textarea cols="1" rows="1" class="new_tweet_text" id="new_tweet_text_txt"></textarea>
    <input type="hidden" value="" id="new_tweet_in_reply_to_id"/>

    <button class="new_tweet_submit"id="new_tweet_submit_btn" >Tweet</button>
    <div class="message" id="message_out"></div>
</section>

<section class="tweet_stream">
    <div id="tweetUpdatePanel">
        <tiles:insert page="/WEB-INF/jsp/ajax/statuslist.jsp" >
            <tiles:put name="modelMap" value="${modelMap.tweetList}" />
        </tiles:insert>
    </div>

</section>
