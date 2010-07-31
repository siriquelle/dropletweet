<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<tiles:definition id="header.shell" page="/WEB-INF/jsp/include/tiles/header/header.jsp" />

<tiles:definition id="header.droplet" page="/WEB-INF/jsp/include/tiles/header/header.jsp" >
    <tiles:put name="menu" value="/WEB-INF/jsp/include/view_droplet/droplet_header_menu.jsp" beanScope="request" />
</tiles:definition>
