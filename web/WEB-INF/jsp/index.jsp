<%@ page contentType="text/html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/base_definitions.jsp" %>

<tiles:insert beanName="base.bare" beanScope="request" >
    <tiles:put name="title" type="string" value="Dropletweet - A conversation tracking tool for twitter" />
    <tiles:put name="meta" value="/WEB-INF/jsp/include/view_landing/landing_meta.jsp" />
    <tiles:put name="body" value="/WEB-INF/jsp/include/view_landing/landing_body.jsp" />
</tiles:insert>
