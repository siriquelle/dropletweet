<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/base/base_definitions.jsp" %>
<%@include file="/WEB-INF/jsp/include/tiles/body/body_definitions.jsp" %>
<%@include file="/WEB-INF/jsp/include/tiles/header/header_definitions.jsp" %>

<tiles:insert beanName="base.bare" beanScope="request" >
    <tiles:put name="title" type="string" value="dropletweet - a conversation tracking tool for twitter" beanScope="request" />
    <tiles:put name="header" beanName="header.droplet" beanScope="request" />
    <tiles:put name="meta" value="/WEB-INF/jsp/include/view_droplet/droplet_meta.jsp" beanScope="request" />
    <tiles:put name="body"beanName="body.droplet" beanScope="request" />
</tiles:insert>
