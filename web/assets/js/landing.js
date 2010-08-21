/**********************************************************************/
//
$(document).ready(function() {
    createLoadingImage();
    createMessageElement();
    $("#message_out").append(loadingImage);
    setup();
    initialize($);
});

/**********************************************************************/

function getDomElement(node){
    var source = node.data.source;
    while(source.match("&lt;") ||source.match("&rt;") || source.match("&quot;") ){
        source= source.replace("&lt;", "<");
        source= source.replace("&gt;", ">");
        source= source.replace("&quot;", "\"");
    }


    var domElement = "\
                            <div class=\"node_tweet_container\">\
                                <div class=\"tweet_profile_image_container\">\
                                    <a href=\"http://twitter.com/"+ node.data.from_user +"\" title=\""+ node.data.from_user +"\" target=\"_blank\">\
                                        <img src=\""+ node.data.profile_image_url +"\" alt=\""+ node.data.from_user +"\" height=\"48px\" width=\"48px\" />\
                                    </a>\
                                </div>\
                                \
                                <div class=\"tweet_text\">\
                                    <a href=\"http://twitter.com/"+ node.data.from_user +"\" class=\"outlink b\" target=\"_blank\">"+ node.data.from_user +"</a> "+ node.data.text +"\
                                </div>\
                                <div class=\"tweet_info\">\
                                    <a href=\"http://twitter.com/"+ node.data.from_user +"/status/"+ node.data.id +"\" target=\"_blank\"/>"+ node.data.created_at +" </a>\
                                    via\
                                    "+ source +"\
                                </div>";

    return domElement;
}

function afterCompute(){
    $(".node").draggable();
    statFilterSetup($);
}

