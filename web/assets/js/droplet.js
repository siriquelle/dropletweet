/**********************************************************************/
var currentAction = "home";
var intervalID = null;
var isLoggedIn = false;
var screen_name = "";
//
$(document).ready(function() {
    var seedURL = "http://twitter.com/leolaporte/status/19034374335";
    //"http://twitter.com/dropletweet/status/18859376985";

    createLoadingImage();
    updateConversation(seedURL);
    tweetStreamHooks();
    updateHooks();
});

/**********************************************************************/

function updateHooks(){

    if(intervalID != null){
        clearInterval(intervalID);
    }

    tweetHooks();
    intervalID = setInterval("ajaxAction('"+ currentAction +"')", 180000);
}

/******************************************************************************/

function tweetStreamHooks(){
    $(".action").click(function(){
        var action = $(this).attr("id").toString();
        currentAction = action;
        ajaxAction(action);
        resetTweetInput();
    });

    $("#new_tweet_submit_btn").click(function(){
        $("#message_out").append(loadingImage);
        var tweet_text = $("#new_tweet_text_txt").val();
        var in_reply_to_id = $("#new_tweet_in_reply_to_id").val();

        $.ajax({
            url: "./tweet.ajax?action=post&tweet_text="+tweet_text+"&in_reply_to_id=" + in_reply_to_id,
            success: function(data) {
                $("#message_out").empty().append(data);
                resetTweetInput();
            }
        });

    });

    $("#new_tweet_submit_btn").mousedown(function(){
        $(this).addClass("new_tweet_submit_active");
    });
    
    $("#new_tweet_submit_btn").mouseup(function(){
        $(this).removeClass("new_tweet_submit_active");
    });
    
}

function resetTweetInput(){
    $("#new_tweet_in_reply_to_id").val("");
    $("#new_tweet_text_txt").val("");
}

function ajaxAction(action){
    $("#message_out").append(loadingImage);
    $.ajax({
        url: "./statuslist.ajax?action=" + action,
        success: function(data) {
            $("#tweetUpdatePanel").empty();
            $("#tweetUpdatePanel").append(data);
            updateHooks();
            $("#message_out").empty();
        }
    });
}
/******************************************************************************/

function tweetHooks(){
    replyHook();
    retweetHook();
    favouriteHook();
    deleteHook();
    spamHook();
    trackHook();
}

/******************************************************************************/

function replyHook(){
    $(".reply").click(function(){
        $("#message_out").append(loadingImage);
        var id = $(this).attr("id").toString();
        id = id.substr("reply".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        replyTweet(tweetId, from_user);
    });
}

function retweetHook(){
    
    $(".retweet").click(function(){
        $("#message_out").append(loadingImage);
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("retweet".length, id.length);
        retweetTweet(tweetId);
    });
}

function favouriteHook(){
    
    $(".favourite").click(function(){
        $("#message_out").append(loadingImage);
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("favourite".length, id.length);
        favouriteTweet(tweetId);
    });
}

function deleteHook(){
    
    $(".delete").click(function(){
        $("#message_out").append(loadingImage);
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("delete".length, id.length);
        deleteTweet(tweetId);
    });
}

function spamHook(){
    
    $(".spam").click(function(){
        $("#message_out").append(loadingImage);
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("spam".length, id.length);
        spamTweet(tweetId);
    });
}

function trackHook(){
    $(".track").click(function(){
        $("#message_out").append(loadingImage);
        var id = $(this).attr("id").toString();
        id = id.substr("track".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        //
        trackTweet(tweetId, from_user);
    });
}

/******************************************************************************/

function replyTweet(tweetId, from_user){
    $("#new_tweet_in_reply_to_id").val(tweetId);
    $("#new_tweet_text_txt").val("@" +from_user + " ");
    $("#new_tweet_text_txt").focus();
    $("#message_out").empty();
}
function retweetTweet(tweetId){
    $.ajax({
        url: "./tweet.ajax?action=retweet&tweetId=" + tweetId,
        success: function(data) {
            $("#message_out").empty().append(data);
        }
    });
}
function favouriteTweet(tweetId){
    alert("favourite : " + tweetId);
    $("#message_out").empty();
}

function deleteTweet(tweetId){
    $.ajax({
        url: "./tweet.ajax?action=delete&tweetId=" + tweetId,
        success: function(data) {
            if(!data.toString().match("Error")){
                $("#dt"+tweetId).fadeOut("slow");
            }
            $("#message_out").empty().append(data);
        }
    });
}

function spamTweet(tweetId){
    alert("spam : " + tweetId);
    $("#message_out").empty();
}
function trackTweet(tweetId, from_user){
    seedURL = "http://twitter.com/" + from_user +"/status/" + tweetId;
    updateConversation(seedURL);
}

/******************************************************************************/

function updateConversation(seedURL){
    $("#infovis").empty();
    $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {
            initialp($.parseJSON(data));
        }
    });

}

/******************************************************************************/

function addEvent(obj, type, fn) {
    if (obj.addEventListener) obj.addEventListener(type, fn, false);
    else obj.attachEvent('on' + type, fn);
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
            $("#message_out").empty();         
            $(".node").draggable();
            updateHooks();
        }
    });


    //load JSON data.
    ht.loadJSON(json);
    //compute positions and plot.
    ht.refresh();
    //end
    ht.controller.onAfterCompute();

}

function getDomElement(node){
    var source = node.data.source;
    while(source.match("&lt;") || source.match("&gt;") || source.match("&quote;")){
        source = source.replace("&lt;", "<");
        source = source.replace("&gt;", ">");
        source = source.replace("&quote;", "\"");
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
                                </div>\
                                    <div class=\"tweet_actions\">\
                                    <div class=\"tweet_action reply\" id=\"reply"+ node.data.id +"_"+ node.data.from_user +"\"><a href=\"##\" ></a></div>\
                                    <div class=\"tweet_action retweet\" id=\"retweet"+ node.data.id +"\"><a href=\"##\"></a></div>\
                                    <div class=\"tweet_action favourite\" id=\"favourite"+ node.data.id +"\"><a href=\"##\"></a></div>\
                                </div>\
                            </div>";

    return domElement;
}
