<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<div class="page">
    <div id="leftColumn">

        <div id="container">
            <div id="infovis0" class="infovis" ></div>
        </div>
        <details class="infovis_stats_container" id="infovis_stats">
            <div class="infovis_stat" >
                <div class="infovis_stat_head">Tweets</div>
                <div class="infovis_stat_text" id="tweets_out">0</div>
            </div>
            <div class="infovis_stat" >
                <div class="infovis_stat_head">Contributers</div>
                <div class="infovis_stat_text" id="peeps_out">0</div>
            </div>
            <div class="infovis_stat" >
                <div class="infovis_stat_head">Key Terms</div>
                <div class="infovis_stat_text" id="terms_out">0</div>
            </div>
        </details>

    </div>
    <aside id="rightColumn">

        <div class="rightPanel">

            <tiles:insert attribute="body" ignore="true" />

        </div>

        <tiles:insert attribute="addition" ignore="true" />

    </aside>
</div>