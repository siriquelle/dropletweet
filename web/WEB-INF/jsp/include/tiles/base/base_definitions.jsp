<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/header/header_definitions.jsp" %>
<%@include file="/WEB-INF/jsp/include/tiles/footer/footer_definitions.jsp" %>

<tiles:definition id="base.shell" page="/WEB-INF/jsp/include/tiles/base/base.jsp" />

<tiles:definition id="base.bare" page="/WEB-INF/jsp/include/tiles/base/base.jsp" >
    <tiles:put name="header" beanName="header.shell" beanScope="request" />
    <tiles:put name="footer" beanName="footer.shell" beanScope="request" />
</tiles:definition>