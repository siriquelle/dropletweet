<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="modelMap" value="${modelMap}"/>

<c:if test="${modelMap.tweetList != null}">
    <c:set var="listLength" value="${fn:length(modelMap.tweetList)}"/>

    <c:forEach items="${modelMap.tweetList}" var="tweet" >

        <div class="tweet_container" id="dt<c:out value="${tweet.id}" />">
            <c:if test="${tweet.profile_image_url != null}">
                <div class="tweet_profile_image_container">
                    <a href="http://twitter.com/<c:out value="${tweet.from_user}" />" title="<c:out value="${tweet.from_user}" />" target="_blank">
                        <img src="<c:out value="${tweet.profile_image_url}" />" alt="<c:out value="${tweet.from_user}" />" height="48px" width="48px" />
                    </a>

                    <div class="tweet_profile_action_container" >
                        <div class="tweet_profile_action follow fl" id="follow<c:out value="${tweet.from_user}" />" ><a href="##" title="Follow @<c:out value="${tweet.from_user}" />"></a></div>
                    </div>
                </div>

            </c:if>

            <div class="tweet_text">
                <a href="http://twitter.com/<c:out value="${tweet.from_user}" />" class="outlink b" target="_blank"><c:out value="${tweet.from_user}" escapeXml="false" /></a> <c:out value="${tweet.prettyText}" escapeXml="false"/>
            </div>
            <div class="tweet_info">
                <a href="http://twitter.com/<c:out value="${tweet.from_user}" />/status/<c:out value="${tweet.id}" />" target="_blank"><c:out value="${tweet.prettyTime}" /></a>
                via
                <c:out value="${tweet.source}" escapeXml="false"/>
                <c:if test="${tweet.in_reply_to_id > -1}">
                    <a href="http://twitter.com/<c:out value="${tweet.to_user}" />/status/<c:out value="${tweet.in_reply_to_id}" />" target="_blank" >in reply to
                        <c:out value="${tweet.to_user}" />
                    </a>
                </c:if>
            </div>
            <div class="tweet_actions">
                <div class="tweet_action reply" id="reply<c:out value="${tweet.id}" />_<c:out value="${tweet.from_user}" />"><a href="##" title="Reply to this tweet"></a></div>

                <div class="tweet_action retweet" id="retweet<c:out value="${tweet.id}" />">
                    <c:if test="${tweet.retweet == true}" >
                        <a href="##" class="isRetweet" title="Retweet this tweet"></a>
                    </c:if>
                    <c:if test="${tweet.retweet == false }" >
                        <a href="##" title="Retweet by you" ></a>
                    </c:if>
                </div>

                <div class="tweet_action favourite" id="favourite<c:out value="${tweet.id}" />">
                    <c:if test="${tweet.favourite == true}" >
                        <a href="##" class="isFavourite" title="Favourite this tweet"></a>
                    </c:if>
                    <c:if test="${tweet.favourite == false}" >
                        <a href="##" title="Your favoured tweet" ></a>
                    </c:if>
                </div>

                <c:if test="${modelMap.user.screen_name == tweet.from_user}" >
                    <div class="tweet_action delete" id="delete<c:out value="${tweet.id}" />"><a href="##" title="Delete this tweet"></a></div>
                </c:if>

                <c:if test="${modelMap.user.screen_name != tweet.from_user}">
                    <div class="tweet_action spam" id="spam<c:out value="${tweet.id}" />"><a href="##" title="Report this tweet for spam"></a></div>
                </c:if>


                <div class="tweet_action track" id="track<c:out value="${tweet.id}" />_<c:out value="${tweet.from_user}" />">

                    <c:if test="${tweet.tracked == true}" >

                        <a href="##" class="isTracked" title="This tweet is being tracked by you"></a>

                    </c:if>

                    <c:if test="${tweet.tracked == false}" >

                        <a href="##" title="Track and map this tweet" ></a>
                    </c:if>
                </div>
                <c:if test="${tweet.tracked == true}" >
                    <div class="tweet_action reload" id="reload<c:out value="${tweet.id}" />_<c:out value="${tweet.from_user}" />">
                        <a href="##" title="Map this tweet" ></a>
                    </div>
                </c:if>
            </div>
        </div>
        <c:set var="lastId" value="${tweet.id}"/>
    </c:forEach>
    <c:if test="${listLength > 40}" >
        <button id="more_tweet_submit_btn" name="ldt<c:out value="${lastId}" />"class="more_tweet_submit">more</button>
    </c:if>

    <c:if test="${fn:length(modelMap.tweetList) == 0}" >
        <div class="message" >nothing in here yet :(</div>
    </c:if>
</c:if>

