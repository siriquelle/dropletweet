<%@ page contentType="text/html" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@include file="/WEB-INF/jsp/include/tiles/base_definitions.jsp" %>
<div class="page">
    <div id="leftColumn">

        <div id="container">
            <div id="infovis"></div>
        </div>
    </div>
    <div id="rightColumn">

        <div class="rightPanel">

            <!--PRESENT THE AUTHORIZATION URL TO AN ANONYMOUS USER TO USE-->

            <div class="link_twitter_sign_in"><a href="./signin.htm" ></a></div>
            <div class="message" id="message_out"></div>

        </div>


    </div>
</div>