<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:if test="${modelMap.latestTweet != null}">
    Latest: <c:out value="${modelMap.latestTweet.text}" escapeXml="false" />
    <c:if test="${modelMap.latestTweet.source != null}" >
        via <c:out value="${modelMap.latestTweet.source}" escapeXml="false" />
    </c:if>

    <c:if test="${modelMap.latestTweet.in_reply_to_id} != null">
        in reply to <c:out value="${modelMap.latestTweet.to_user}" />
    </c:if>


    <c:if test="${modelMap.latestTweet == null}">
        Twitter is Error, :( boo!
    </c:if>
</c:if>