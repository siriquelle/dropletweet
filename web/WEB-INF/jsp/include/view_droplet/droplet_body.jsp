<%@ page contentType="text/html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>

<div class="user_info">
    <div class="name">
        <c:out value="${modelMap.user.name}" />
    </div>

</div>

<div class="actions">

    <div class="action" id="home"><a href="##" title="Tweets from People you Follow"></a></div>
    <div class="action" id="replies"><a href="##" title="Replies and Mentions"></a></div>
    <div class="action" id="dms"><a href="##" title="Direct Messages"></a></div>
    <div class="action" id="favourites"><a href="##" title="Your Favourite Tweets"></a></div>
    <div class="action" id="retweets"><a href="##" title="Your Sent Tweets"></a></div>
    <div class="action_search" id="search"><a href="##" title="Search" id="search_a"></a></div>
    <div id="action_search_container_outer">
        <div class="action_search_container_inner">
            <input type="text" id="search_txt"/>
            <a href="##" id="search_btn"></a>
        </div>
    </div>
    <div class="action_conversations" id="conversations"><a href="##" title="List Conversations Being Tracked By You" ></a></div>
    <div class="action" id="logout"><a href="./signin.htm?logout" title="Logout of Dropletweet"></a></div>

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
