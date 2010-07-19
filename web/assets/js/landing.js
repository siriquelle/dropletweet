/**********************************************************************/
var loadingImage;

$(document).ready(function() {
    //var seedURL = "https://twitter.com/dropletweet/status/18859376985";
    var seedURL = "http://twitter.com/ConanOBrien/status/18769159235";
    createLoadingImage();
    updateConversation(seedURL);
    tweetStreamHooks();
    updateHooks();
});

/**********************************************************************/

function createLoadingImage(){
    loadingImage =document.createElement("img");
    loadingImage.setAttribute('src', './assets/img/load.gif');
    loadingImage.setAttribute('alt', 'Loading Conversation');
    loadingImage.setAttribute('height', '15px');
    loadingImage.setAttribute('width', '128px');
    loadingImage.setAttribute('id', 'img_loading');

}

function updateHooks(){    
    tweetHooks();
}

/******************************************************************************/

function tweetStreamHooks(){
    $(".action").click(function(){
        $("#message_out").append(loadingImage);
        var action = $(this).attr("id").toString();
        $.ajax({
            url: "./statuslist.ajax?action=" + action,
            success: function(data) {
                $("#tweetUpdatePanel").empty();
                $("#tweetUpdatePanel").append(data);              
                updateHooks();
                $("#message_out").empty();
            }
        });
    });

    $("#new_tweet_submit_btn").click(function(){
        $("#new_tweet_submit_btn").addClass("new_tweet_submit_active");
        var tweet_text = $("#new_tweet_text_txt").val();
        var in_reply_to_id = $("#new_tweet_in_reply_to_id").val();

        $.ajax({
            url: "./tweet.ajax?action=post&tweet_text="+tweet_text+"&in_reply_to_id=" + in_reply_to_id,
            success: function(data) {
                $("#message_out").empty().append(data);
                $("#new_tweet_in_reply_to_id").val("");
                $("#new_tweet_text_txt").val("");
                $("#new_tweet_submit_btn").removeClass("new_tweet_submit_active");
            }
        });

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
    $("#message_out").append(loadingImage);
    $(".retweet").click(function(){
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("retweet".length, id.length);
        retweetTweet(tweetId);
    });
}

function favouriteHook(){
    $("#message_out").append(loadingImage);
    $(".favourite").click(function(){
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("favourite".length, id.length);
        favouriteTweet(tweetId);
    });
}

function deleteHook(){
    $("#message_out").append(loadingImage);
    $(".delete").click(function(){
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("delete".length, id.length);
        deleteTweet(tweetId);
    });
}

function spamHook(){
    $("#message_out").append(loadingImage);
    $(".spam").click(function(){
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("spam".length, id.length);
        spamTweet(tweetId);
    });
}

function trackHook(){
    $("#message_out").append(loadingImage);
    $(".track").click(function(){
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
    alert("delete : " + tweetId);
    $("#message_out").empty();
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
    $("#mycanvas").fadeOut("fast").empty().fadeIn("fast");

    $("#message_out").append(loadingImage);
 
    $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {

            var json = $.parseJSON(data);
            initialp(json);

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
            domElement.innerHTML = node.name + " " + node.data.tweet;
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

                domElement.innerHTML = node.name + " :<br /> " + node.data.tweet;

            } else if(node._depth == 1){
                style.fontSize = "0.8em";
                style.opacity = "0.8";
                style.zIndex = "2000";
                style.marginTop = "-40px";
                domElement.innerHTML = node.name + " :<br />  " + node.data.tweet;
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
                style.opacity = "0.2";
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
        }
    });


    //load JSON data.
    ht.loadJSON(json);
    //compute positions and plot.
    ht.refresh();
    //end
    ht.controller.onAfterCompute();

}
