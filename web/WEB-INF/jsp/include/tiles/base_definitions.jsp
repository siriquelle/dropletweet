<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<tiles:definition id="base.preloaded" page="/WEB-INF/jsp/include/tiles/base.jsp" >
    <tiles:put name="meta" value="/WEB-INF/jsp/include/tiles/meta.jsp" />
    <tiles:put name="header" value="/WEB-INF/jsp/include/tiles/header.jsp" />
    <tiles:put name="navigation" value="/WEB-INF/jsp/include/tiles/navigation.jsp"/>
    <tiles:put name="footer" value="/WEB-INF/jsp/include/tiles/footer.jsp" />
</tiles:definition>

<tiles:definition id="base.bare" page="/WEB-INF/jsp/include/tiles/base.jsp" >
    <tiles:put name="header" value="/WEB-INF/jsp/include/tiles/header.jsp" />
    <tiles:put name="footer" value="/WEB-INF/jsp/include/tiles/footer.jsp" />
</tiles:definition>





