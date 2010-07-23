/**********************************************************************/

//
$(document).ready(function() {
    createLoadingImage();
    $("#message_out").append(loadingImage);
    updateConversation(seedURL);
});

/**********************************************************************/

function getDomElement(node){
    var source = node.data.source;
    while(source.match("&lt;") ||source.match("&rt;") || source.match("&quot") ){
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
function initialp(json){
    //end

    var infovis = document.getElementById('infovis');
    var w = infovis.offsetWidth , h = infovis.offsetHeight;

    //init canvas
    //Create a new canvas instance.
    var canvas = new Canvas('mycanvas', {
        'injectInto': 'infovis',
        'width': w,
        'height': h
    });
    //end

    //init Hypertree
    var ht = new Hypertree(canvas, {
        //Change node and edge styles such as
        //color, width and dimensions.
        Node: {
            dim: 12,
            type: "circle",
            width: 400,
            height: 400,
            color: "#0F4853"
        },

        Edge: {
            lineWidth: 3,
            color: "#088"
        },
        duration: 600,
        fps: 50,
        clearCanvas: true,
        transition: Trans.Expo.easeInOut,
        levelDistance: 100,
        onBeforeCompute: function(node){
        },
        //Attach event handlers and add text to the
        //labels. This method is only triggered on label
        //creation
        onCreateLabel: function(domElement, node){
            addEvent(domElement, 'click', function () {
                ht.onClick(node.id);
            });
        },
        //Change node styles when labels are placed
        //or moved.
        onPlaceLabel: function(domElement, node){
            var style = domElement.style;
            style.display = '';
            style.cursor = 'pointer';
            if (node._depth == 0) {
                style.fontSize = "1em";
                style.opacity = "1";
                style.zIndex = "3000";
                style.textAlign = "left";
                style.marginLeft = "0px";
                style.marginTop = "-60px";

                domElement.innerHTML = getDomElement(node);

            } else if(node._depth == 1){
                style.fontSize = "0.8em";
                style.opacity = "0.8";
                style.zIndex = "2000";
                style.marginTop = "-40px";
                domElement.innerHTML = getDomElement(node);
            } else if(node._depth == 2){
                style.fontSize = "0.2em";
                style.opacity = "0.6";
                style.zIndex = "1000";
                style.marginTop = "-20px";
                domElement.innerHTML = node.name;
            }
            else if(node._depth == 3){
                style.fontSize = "0.2em";
                style.opacity = "0.4";
                style.zIndex = "1000";
                style.marginTop = "0";
                domElement.innerHTML = node.name;
            }
            else {
                style.fontSize = "0.2em";
                style.opacity = "1";
                style.zIndex = "500";
                style.marginTop = "0";
                domElement.innerHTML = node.name;
            }

            var left = parseInt(style.left);
            var w = domElement.offsetWidth;
            style.left = (left - w / 2) + 'px';
        },

        onAfterCompute: function(){
            $("#infovis").fadeIn(300);
            $("#message_out").empty();
            $(".node").draggable();
        }
    });


    //load JSON data.
    ht.loadJSON(json);
    //compute positions and plot.
    ht.refresh();
    //end
    ht.controller.onAfterCompute();

}