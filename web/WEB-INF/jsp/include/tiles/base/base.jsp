<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title><tiles:getAsString name="title" ignore="true"/></title>
        <link type="text/css" href="./assets/css/base.css?9" rel="stylesheet" />
        <script language="javascript" type="text/javascript" src="./assets/js/com.js?5"></script>
        <script language="javascript" type="text/javascript" src="./assets/js/lib/jquery.js"></script>
        <script language="javascript" type="text/javascript" src="./assets/js/lib/jquery-ui.js"></script>
        <script language="javascript" type="text/javascript" src="./assets/js/lib/jit.js"></script>
        <meta name="description" content="Track exciting twitter conversations in a single observable interactive format." />
        <link rel="shortcut icon" href="./assets/img/logo.png" />
        <link href='http://fonts.googleapis.com/css?family=Reenie+Beanie' rel='stylesheet' type='text/css'>
        <tiles:insert attribute="meta" ignore="true" />
    </head>

    <body>
        <div class="body">
            <tiles:insert attribute="header" ignore="true" />
            <tiles:insert attribute="body" ignore="true"/>
            <tiles:insert attribute="footer" ignore="true" />
        </div>
    </body>
</html>
