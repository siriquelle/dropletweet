<%@ page contentType="text/html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>

<div class="user_info">
    <div class="name">
        <c:out value="${modelMap.user.name}" />
    </div>

</div>

<div class="actions">

    <div class="action" id="home"><a href="##" ></a></div>
    <div class="action" id="replies"><a href="##" ></a></div>
    <div class="action" id="dms"><a href="##" ></a></div>
    <div class="action" id="favourites"><a href="##" ></a></div>
    <div class="action" id="retweets"><a href="##" ></a></div>
    <div class="action" id="search"><a href="##" ></a></div>
    <div class="action" id="conversations"><a href="##" ></a></div>


</div>

<div class="new_tweet_container">
    <textarea cols="1" rows="1" class="new_tweet_text" id="new_tweet_text_txt"></textarea>
    <input type="hidden" value="" id="new_tweet_in_reply_to_id"/>

    <button class="new_tweet_submit"id="new_tweet_submit_btn" >Tweet</button>
    <div class="message" id="message_out"></div>
</div>

<div class="tweet_stream">
    <div id="tweetUpdatePanel">
        <c:forEach items="${modelMap.tweetList}" var="tweet" >
            <div class="tweet_container" id="dt<c:out value="${tweet.id}" />">
                <c:if test="${tweet.profile_image_url != null}">
                    <div class="tweet_profile_image_container">
                        <a href="http://twitter.com/<c:out value="${tweet.from_user}" />" title="<c:out value="${tweet.from_user}" />" target="_blank">
                            <img src="<c:out value="${tweet.profile_image_url}" />" alt="<c:out value="${tweet.from_user}" />" height="48px" width="48px" />
                        </a>
                    </div>
                </c:if>

                <div class="tweet_text">
                    <a href="http://twitter.com/<c:out value="${tweet.from_user}" />" class="outlink b" target="_blank"><c:out value="${tweet.from_user}" escapeXml="false" /></a> <c:out value="${tweet.text}" escapeXml="false"/>
                </div>
                <div class="tweet_info">
                    <a href="http://twitter.com/<c:out value="${tweet.from_user}" />/status/<c:out value="${tweet.id}" />" target="_blank"><c:out value="${tweet.created_at}" /></a>
                    via
                    <c:out value="${tweet.source}" escapeXml="false"/>
                    <c:if test="${tweet.in_reply_to_id > -1}">
                        <a href="http://twitter.com/<c:out value="${tweet.to_user}" />/status/<c:out value="${tweet.in_reply_to_id}" />" target="_blank" >in reply to
                            <c:out value="${tweet.to_user}" />
                        </a>
                    </c:if>
                </div>
                <div class="tweet_actions">
                    <div class="tweet_action reply" id="reply<c:out value="${tweet.id}" />_<c:out value="${tweet.from_user}" />"><a href="##"></a></div>
                    <div class="tweet_action retweet" id="retweet<c:out value="${tweet.id}" />"><a href="##"></a></div>
                    <div class="tweet_action favourite" id="favourite<c:out value="${tweet.id}" />"><a href="##"></a></div>

                    <c:if test="${modelMap.user.screen_name == tweet.from_user}" >
                        <div class="tweet_action delete" id="delete<c:out value="${tweet.id}" />"><a href="##"></a></div>
                    </c:if>
                    <c:if test="${modelMap.user.screen_name != tweet.from_user}">
                        <div class="tweet_action spam" id="spam<c:out value="${tweet.id}" />"><a href="##"></a></div>
                    </c:if>
                    <div class="tweet_action track" id="track<c:out value="${tweet.id}" />_<c:out value="${tweet.from_user}" />"><a href="##"></a></div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
