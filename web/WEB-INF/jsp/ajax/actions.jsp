<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
Latest: <c:out value="${modelMap.tweet.text}" escapeXml="false" /> from <c:out value="${modelMap.tweet.source}" escapeXml="false" />
<c:if test="${modelMap.tweet.in_reply_to_id} != null">
    in reply to <c:out value="${modelMap.tweet.to_user}" />
</c:if>