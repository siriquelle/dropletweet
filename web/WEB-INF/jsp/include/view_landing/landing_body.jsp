<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/base_definitions.jsp" %>
<div class="page">
    <div id="leftColumn">
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

                <div class="tweet">
                    <textarea cols="1" rows="1" class="tweet_text"></textarea>
                </div>

                <div class="statusStream">
                    <div id="statusUpdatePanel">
                        <c:forEach items="${modelMap.tweetList}" var="tweet" >
                            <div class="status">
                                <div class="status_text"><c:out value="${tweet.id}" /><c:out value="${tweet.text}" escapeXml="false" /> </div>
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