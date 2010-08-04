<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<c:if test="${modelMap.ajaxTweetActionBean.error == null}">
    Latest: <c:out value="${modelMap.ajaxTweetActionBean.text}" escapeXml="false" />
    <c:if test="${modelMap.ajaxTweetActionBean.link != null}" >
        via <c:out value="${modelMap.ajaxTweetActionBean.link}" escapeXml="false" />
    </c:if>

    <c:if test="${modelMap.ajaxTweetActionBean.id} != null">
        in reply to <c:out value="${modelMap.ajaxTweetActionBean.name}" />
    </c:if>

</c:if>


<c:if test="${modelMap.ajaxTweetActionBean.error != null}">
    Error: <c:out value="${modelMap.ajaxTweetActionBean.error}" />
</c:if>