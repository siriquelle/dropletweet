<%@ page contentType="text/html" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<div class="page">
    <div id="leftColumn">

        <div id="container">
            <div id="infovis"></div>
        </div>
    </div>
    <div id="rightColumn">

        <div class="rightPanel">

            <tiles:insert attribute="body" ignore="true" />

        </div>

    </div>
</div>