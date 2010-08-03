<%@ page contentType="text/html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<div class="user_info_action">
    <a id="user_info_hide_show" class="user_info_show" href="##" title="Show/Hide user info."></a>
</div>
<div class="user_info" id="user_info_container">
    <div class="user_info_profile_image">
        <img alt="<c:out value="${modelMap.user.screen_name}" />"  src="<c:out value="${modelMap.user.profile_image_url}" />" width="48px" height="48px"/>
    </div>
    <div class="user_info_screen_name" id="screen_name">
        <c:out value="${modelMap.user.screen_name}" />
    </div>
    <div class="user_info_details_container">
        <div class="user_info_details_item">
            <c:out value="${modelMap.user.description}" />
        </div>
        <div class="user_info_details_item">
            <c:out value="${modelMap.user.location}" />
        </div>
    </div>


    <div class="user_info_count_container">
        <div class="user_info_count" >
            <div class="user_info_count_head">Following</div>
            <div class="user_info_count_number"  id="friends_count"><c:out value="${modelMap.user.friends_count}" /></div>
        </div>
        <div class="user_info_count" >
            <div class="user_info_count_head">Followers</div>
            <div class="user_info_count_number" id="followers_count"><c:out value="${modelMap.user.followers_count}" /></div>
        </div>
        <div class="user_info_count" >
            <div class="user_info_count_head">Tweets</div>
            <div class="user_info_count_number" id="statuses_count"><c:out value="${modelMap.user.statuses_count}" /></div>
        </div>
    </div>
</div>
<div class="actions">

    <div class="action" id="friendsList"><a href="##" title="Tweets from People you Follow"></a></div>
    <div class="action" id="replyList"><a href="##" title="Replies and Mentions"></a></div>
    <div class="action" id="dmList"><a href="##" title="Direct Messages"></a></div>
    <div class="action" id="favouritesList"><a href="##" title="Your Favourite Tweets"></a></div>
    <div class="action" id="sentList"><a href="##" title="Your Sent Tweets"></a></div>
    <div class="action" id="discussionList"><a href="##" title="Tweets from your followers in conversation."></a></div>
    <div class="action_search" id="search"><a href="##" title="Search" id="search_a"></a></div>
    <div id="action_search_container_outer">
        <div class="action_search_container_inner">
            <input type="text" id="search_txt"/>
            <a href="##" id="search_btn"></a>
        </div>
    </div>
    <div class="action_conversations" id="conversationList"><a href="##" title="List Conversations Being Tracked By You" ></a></div>


</div>

<div class="new_tweet_container">
    <textarea cols="1" rows="1" class="new_tweet_text" id="new_tweet_text_txt"></textarea>
    <input type="hidden" value="" id="new_tweet_in_reply_to_id"/>

    <button class="new_tweet_submit"id="new_tweet_submit_btn" >Tweet</button>
    <div class="message" id="message_out"></div>
</div>

<div class="tweet_stream">
    <div id="tweetUpdatePanel">
        <tiles:insert page="/WEB-INF/jsp/ajax/statuslist.jsp" >
            <tiles:put name="modelMap" value="${modelMap.tweetList}" />
        </tiles:insert>
    </div>

</div>
