<%@ page session="false" autoFlush="true"%>
<%@page contentType="text/plain" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>

<c:out value="${modelMap.jit}" default="ERROR" escapeXml="false"/>
