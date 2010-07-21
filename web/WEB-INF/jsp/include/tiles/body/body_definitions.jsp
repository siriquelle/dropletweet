<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<tiles:definition id="body.shell" page="/WEB-INF/jsp/include/tiles/body/body.jsp" />

<tiles:definition id="body.landing" page="/WEB-INF/jsp/include/tiles/body/body.jsp"  >
    <tiles:put name="body" value="/WEB-INF/jsp/include/view_landing/landing_body.jsp" beanScope="request" />
</tiles:definition>

<tiles:definition id="body.droplet" page="/WEB-INF/jsp/include/tiles/body/body.jsp" >
    <tiles:put name="body" value="/WEB-INF/jsp/include/view_droplet/droplet_body.jsp" beanScope="request" />
</tiles:definition>