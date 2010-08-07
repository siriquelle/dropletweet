<%@ page session="false" autoFlush="true"%>
<%@ page contentType="text/plain" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
{
"screen_name" :"<c:out value="${modelMap.ajaxUserBean.screenName}" />",

"profile_image_url" :"<c:out value="${modelMap.ajaxUserBean.profileImageUrl}" />",
"description" :"<c:out value="${modelMap.ajaxUserBean.description}" />",
"location" :"<c:out value="${modelMap.ajaxUserBean.location}" />",

"following" :"<c:out value="${modelMap.ajaxUserBean.following}" />",
"followers" :"<c:out value="${modelMap.ajaxUserBean.followers}" />",
"tweets" :"<c:out value="${modelMap.ajaxUserBean.tweets}" />",
"hits" :"<c:out value="${modelMap.ajaxUserBean.hits}" />"
}