<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/base_definitions.jsp" %>
<div class="page">
    <div id="leftColumn">
        <img id="img_loading" src="./assets/img/load.gif" alt="Loading Conversation"/>
        <div id="container">
            <div id="infovis"></div>
        </div>
    </div>
    <div id="rightColumn">

        <div class="rightPanel">
            <c:if test="${modelMap.user.name != null}">
                <div class="user_info">
                    <div class="name">
                        <c:out value="${modelMap.user.name}" />
                    </div>
                </div>

                <div class="actions">

                    <div class="action" id="home"><a href="#" ></a></div>
                    <div class="action" id="replies"><a href="#" ></a></div>
                    <div class="action" id="dms"><a href="#" ></a></div>
                    <div class="action" id="favourites"><a href="#" ></a></div>
                    <div class="action" id="retweets"><a href="#" ></a></div>
                    <div class="action" id="search"><a href="#" ></a></div>
                    <div class="action" id="conversations"><a href="#" ></a></div>


                </div>

                <div class="new_tweet_container">
                    <textarea cols="1" rows="1" class="new_tweet_text"></textarea>
                </div>

                <div class="tweet_stream">
                    <div id="tweetUpdatePanel">
                        <c:forEach items="${modelMap.tweetList}" var="tweet" >
                            <div class="tweet_container">
                                <div class="tweet_profile_image_container">
                                    <img src="<c:out value="${tweet.profile_image_url}" />" alt="" height="48px" width="48px" />
                                </div>
                                <div class="tweet_text">
                                    <c:out value="${tweet.text}" escapeXml="false" />
                                </div>
                                <div class="tweet_info">
                                    <c:out value="${tweet.created_at}" />
                                    via
                                    <c:out value="${tweet.source}" escapeXml="false"/>
                                    <c:if test="${tweet.in_reply_to_id > -1}">
                                        <a href="http://twitter.com/<c:out value="${tweet.to_user}" />/status/<c:out value="${tweet.in_reply_to_id}" />" >in reply to
                                            <c:out value="${tweet.to_user}" />
                                        </a>
                                    </c:if>
                                </div>
                                <div class="tweet_actions">
                                    <div class="tweet_action reply" id="<c:out value="${tweet.created_at}" />_<c:out value="${tweet.created_at}" />"><a href="#"></a></div>
                                    <div class="tweet_action retweet" id="retweet<c:out value="${tweet.id}" />"><a href="#"></a></div>
                                    <div class="tweet_action favourite" id="favourite<c:out value="${tweet.id}" />"><a href="#"></a></div>

                                    <div class="tweet_action delete" id="delete<c:out value="${tweet.id}" />"><a href="#"></a></div>
                                    <div class="tweet_action spam" id="spam<c:out value="${tweet.id}" />"><a href="#"></a></div>

                                    <div class="tweet_action track" id="track<c:out value="${tweet.id}" />_<c:out value="${tweet.from_user}" />"><a href="#"></a></div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!--PRESENT THE AUTHORIZATION URL TO AN ANONYMOUS USER TO USE-->
            <c:if test="${modelMap.user.name == null}">
                <div class="link_twitter_sign_in"><a href="./signin.htm" ></a></div>
            </c:if>
        </div>


    </div>
</div>