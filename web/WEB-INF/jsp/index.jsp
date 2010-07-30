<%@ page contentType="text/html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/base/base_definitions.jsp" %>
<%@include file="/WEB-INF/jsp/include/tiles/body/body_definitions.jsp" %>

<tiles:insert beanName="base.bare" beanScope="request" >
    <tiles:put name="title" type="string" value="Dropletweet - A conversation tracking tool for twitter" beanScope="request"/>
    <tiles:put name="meta" value="/WEB-INF/jsp/include/view_landing/landing_meta.jsp" beanScope="request"/>
    <tiles:put name="body" beanName="body.landing" beanScope="request"/>
</tiles:insert>
