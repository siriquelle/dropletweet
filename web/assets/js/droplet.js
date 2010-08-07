/**********************************************************************/
var listType = "friendsList";
var intervalID = null;
var charCount = 0;
var temp_height;
var screenName;
//
$(document).ready(function() {
    createLoadingImage();
    setup();
    initialize($);
    tweetStreamHooks();
    tweetHooks();
    startAutoUpdate();
    setScreenNameFromDocument();
});

/**********************************************************************/


function stopAutoUpdate(){
    if(intervalID != null){
        clearInterval(intervalID);
    }    
}
function startAutoUpdate(){
    if(intervalID != null){
        clearInterval(intervalID);
    }
    intervalID = setInterval("ajaxAction('"+ listType +"')", 180000);
}
/******************************************************************************/

function tweetStreamHooks(){
    $(".action").click(function(){
        var action = $(this).attr("id").toString();
        listType = action;
        ajaxAction(action);
        resetTweetInput();
    });
    /**************************************************************************/
    /**************************************************************************/
    $("#search").click(function(){
        stopAutoUpdate();
        $("#search_a").toggleClass("search_a_toggle");
        $("#action_search_container_outer").toggle();
        $("#search_txt").val("");
        $("#search_txt").focus();
        $("#search_btn").click(function(){
            searchAjaxAction($("#search_txt").val());
        });
    });
    /**************************************************************************/
    /**************************************************************************/
    $("#conversationList").click(function(){
        listType = "conversationList";
        ajaxAction(listType);
    });

    /**************************************************************************/
    /**************************************************************************/

    /**************************************************************************/
    $("#new_tweet_text_txt").focus(function(){
        stopAutoUpdate();
        var charCount = getCharCountElement();
        charCount.textContent = (140 - $("#new_tweet_text_txt").val().length);
        $("#message_out").empty().append(charCount);

    });
    
    $("#new_tweet_text_txt").blur(function(){
        startAutoUpdate();
    });

    $("#new_tweet_text_txt").keyup(function(){
        stopAutoUpdate();
        var charCount = getCharCountElement();
        charCount.textContent = (140 - $("#new_tweet_text_txt").val().length);
        $("#message_out").empty().append(charCount);
    });
    /**************************************************************************/
    $("#new_tweet_submit_btn").mousedown(function(){
        $(this).addClass("new_tweet_submit_active");
    });

    $("#new_tweet_submit_btn").blur(function(){
        $(this).removeClass("new_tweet_submit_active");
    });
    $("#new_tweet_submit_btn").mouseup(function(){
        stopAutoUpdate();
        $(this).removeClass("new_tweet_submit_active");
        $("#message_out").empty().append(loadingImage);
        var tweet_text = $("#new_tweet_text_txt").val();
        tweet_text = encodeURIComponent(tweet_text);
        var in_reply_to_id = $("#new_tweet_in_reply_to_id").val();

        $.ajax({
            url: "./tweet.ajax?action=post&in_reply_to_id=" + in_reply_to_id +"&tweet_text=" + tweet_text,
            success: function(data) {
                $("#message_out").empty().append(data);
                resetTweetInput();
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                $("#message_out").empty().append(dropletCommonError);
            }
        });
    });
    /**************************************************************************/

    $("#user_info_hide_show").click(function(){
        $(this).toggleClass("user_info_show");
        $(this).toggleClass("user_info_hide");
        var height = $("#user_info_container").height();
        if(height != 0){
            $("#user_info_container").animate({
                height:0
            }, 250, "linear", function(){
                temp_height = height;
            });
        } else{
            showUserDetails(screenName);
            $("#user_info_container").animate({
                height:temp_height
            }, 250, "linear");
        }
    });

}
/**************************************************************************/
function resetTweetInput(){
    $("#new_tweet_in_reply_to_id").val("");
    $("#new_tweet_text_txt").val("");
    startAutoUpdate();
}
/**************************************************************************/
function ajaxAction(action){
    stopAutoUpdate();
    $("#message_out").empty().append(loadingImage);
    $.ajax({
        url: "./statuslist.ajax?action=" + action,
        success: function(data) {
            $("#tweetUpdatePanel").empty().append(data);
            $("#message_out").empty();
            tweetHooks();
            startAutoUpdate();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}
/**************************************************************************/
function searchAjaxAction(query){

    $("#message_out").empty().append(loadingImage);
    $.ajax({
        url: "./statuslist.ajax?action=search&q=" + encodeURIComponent(query),
        success: function(data) {
            listType = "search_" + query;
            $("#tweetUpdatePanel").empty().append(data);
            tweetHooks();
            $("#message_out").empty();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
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
    reloadHook();
    moreTweetsHook();
    followHook();
    searchForHashTagHook();
    searchForFromUser();
}

/******************************************************************************/

function replyHook(){
    $(".reply").unbind();
    $(".reply").click(function(){
        $("#message_out").empty().append(loadingImage);
        var id = $(this).attr("id").toString();
        id = id.substr("reply".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        replyTweet(tweetId, from_user);
    });
}
/**************************************************************************/
function retweetHook(){
    $(".retweet").unbind();
    $(".retweet").click(function(){
        $("#message_out").empty().append(loadingImage);
        $(this).children("a").toggleClass("isRetweet");
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("retweet".length, id.length);
        retweetTweet(tweetId);
    });
}
/**************************************************************************/
function favouriteHook(){
    $(".favourite").unbind();
    $(".favourite").click(function(){
        $("#message_out").empty().append(loadingImage);
        $(this).children("a").toggleClass("isFavourite");
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("favourite".length, id.length);
        favouriteTweet(tweetId);
    });
}
/**************************************************************************/
function deleteHook(){
    $(".delete").unbind();
    $(".delete").click(function(){
        $("#message_out").empty().append(loadingImage);
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("delete".length, id.length);
        deleteTweet(tweetId);
    });
}
/**************************************************************************/
function spamHook(){
    $(".spam").unbind();
    $(".spam").click(function(){
        $("#message_out").empty().append(loadingImage);
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("spam".length, id.length);
        spamTweet(tweetId);
    });
}
/**************************************************************************/
function trackHook(){
    $(".track").unbind();
    $(".track").click(function(){
        stopAutoUpdate();
        $("#message_out").empty().append(loadingImage);
        $(this).children("a").toggleClass("isTracked");
        
        var id = $(this).attr("id").toString();
        id = id.substr("track".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        //
        seedURL = "http://twitter.com/" + from_user +"/status/" + tweetId;        
        //
        $.ajax({
            url: "./tweet.ajax?action=track&tweetId=" + tweetId +"&listType=" +listType,
            success: function(data) {
                $("#message_out").empty().append(data).append(loadingImage);
                reloadConversation(seedURL);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                $("#message_out").empty().append(dropletCommonError);
            }
        });
    });
}
/**************************************************************************/
function reloadHook(){
    $(".reload").unbind();
    $(".reload").click(function(){
        stopAutoUpdate();
        $("#message_out").empty().append(loadingImage);
        $(this).children("a").toggleClass("isTracked");
        var id = $(this).attr("id").toString();
        id = id.substr("reload".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        //
        seedURL = "http://twitter.com/" + from_user +"/status/" + tweetId;
        //
   
        reloadConversation(seedURL);
        //
        $(this).children("a").toggleClass("isTracked");
    });
}
/**************************************************************************/
function followHook(){
    $(".follow").unbind();
    $(".follow").click(function(){
        stopAutoUpdate();
        $("#message_out").empty().append(loadingImage);

        var id = $(this).attr("id").toString();
        var screen_name = id.substr("follow".length, id.length);
        //
        followUser(screen_name);
    });
}
/**************************************************************************/
function searchForHashTagHook(){
    $(".hash").unbind();
    $(".hash").click(function(){
        stopAutoUpdate();
        var hashTag = $(this).text();
        $("#search_a").addClass("search_a_toggle");
        $("#action_search_container_outer").show();
        $("#search_txt").val(hashTag);
        $("#search_txt").focus();
        searchAjaxAction(hashTag);
    });
}
/**************************************************************************/
function searchForFromUser(){
    $(".person").unbind();
    $(".person").click(function(){
        stopAutoUpdate();
        var user = $(this).text();
        user = user.substring(user.indexOf("@")+1, user.length);
        showUserDetails(user);
        query = "from:"+user;
        $("#search_a").addClass("search_a_toggle");
        $("#action_search_container_outer").show();
        $("#search_txt").val(query);
        $("#search_txt").focus();
        searchAjaxAction(query);

    });
}
/**************************************************************************/
function moreTweetsHook(){
    $("#more_tweet_submit_btn").unbind();
    $("#more_tweet_submit_btn").mousedown(function(){
        $(this).addClass("more_tweet_submit_active");
    });
    $("#more_tweet_submit_btn").blur(function(){
        $(this).removeClass("more_tweet_submit_active");
    });
    $("#more_tweet_submit_btn").mouseup(function(){
        stopAutoUpdate();
        var hrpoint = $("#more_tweet_submit_btn").attr("name");
        hrpoint = hrpoint.toString().substring(1, hrpoint.length);
        $(this).removeClass("more_tweet_submit_active");
        $("#message_out").empty().append(loadingImage);

        $.ajax({
            url: ".statuslist.ajax?action=more&listType="+listType,
            success: function(data) {
                $("#tweetUpdatePanel").empty().append(data);
                $("#message_out").empty();
                $("#"+hrpoint).after("<hr class=\"page_break\"/>");
                tweetHooks();
                startAutoUpdate();
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                $("#message_out").empty().append(dropletCommonError);
            }
        });
    });
}
/******************************************************************************/

function replyTweet(tweetId, from_user){
    $("#new_tweet_in_reply_to_id").val(tweetId);
    $("#new_tweet_text_txt").val("@" +from_user + " ");
    $("#new_tweet_text_txt").focus();
    $("#message_out").empty();
}
/**************************************************************************/
function retweetTweet(tweetId){
    $.ajax({
        url: "./tweet.ajax?action=retweet&tweetId=" + tweetId,
        success: function(data) {
            $("#message_out").empty().append(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}
/**************************************************************************/
function favouriteTweet(tweetId){
    $.ajax({
        url: "./tweet.ajax?action=favourite&tweetId=" + tweetId,
        success: function(data) {
            $("#message_out").empty().append(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}
/**************************************************************************/
function deleteTweet(tweetId){
    $.ajax({
        url: "./tweet.ajax?action=delete&tweetId=" + tweetId,
        success: function(data) {
            if(!data.toString().match("Error")){
                $("#dt"+tweetId).fadeOut("slow");
            }
            $("#message_out").empty().append(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}
/**************************************************************************/
function spamTweet(userId){
    $.ajax({
        url: "./tweet.ajax?action=spam&userId=" + userId,
        success: function(data) {
            $("#message_out").empty().append(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}

/******************************************************************************/
function followUser(screen_name){
    $.ajax({
        url: "./tweet.ajax?action=follow&screen_name=" + screen_name,
        success: function(data) {
            $("#message_out").empty().append(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}
/******************************************************************************/

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
                                    <div class=\"tweet_profile_action_container\" >\
                                        <div class=\"tweet_profile_action follow fl\" id=\"follow"+node.data.from_user  +"\" ><a href=\"##\" title=\"Follow @"+node.data.from_user  +"\"></a></div>\
                                    </div>\
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

/**************************************************************************/
function getCharCountElement(){
    var charCount = document.createElement("div");
    charCount.setAttribute('id', 'char_count');
    charCount.setAttribute('class', 'simple_message');
    return charCount;
}

/*****************************************************************************/
function reloadConversation(seedURL){
    $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {
            $("#infovis0 .node").remove();
            currentConversation = $.parseJSON(data);
            initialp($.parseJSON(data));
        //perform morphing animation.
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
    
}
/*****************************************************************************/
function afterCompute(){
    $("#message_out").empty();
    $(".node").draggable();
    tweetHooks();
}
/*****************************************************************************/
function showUserDetails(user){
    
    $.ajax({
        url: "./user.ajax?action=get_user_info&screen_name=" + user,
        success: function(data) {

            if(temp_height>0){
                $("#user_info_container").animate({
                    height:"100%"
                }, 250, "linear");
            }
            user = $.parseJSON(data);
            var animationSpeed = 200;
            $("#screen_name").fadeOut(animationSpeed).empty().append(user.screen_name).fadeIn(animationSpeed);
            animationSpeed+=300;
            $("#description").fadeOut(animationSpeed).empty().append(user.description).fadeIn(animationSpeed);
            animationSpeed-=300;
            $("#profile_image_url").fadeOut(animationSpeed).attr("src", user.profile_image_url);
            $("#profile_image_url").fadeIn(animationSpeed);
            animationSpeed+=300;
            $("#location").fadeOut(animationSpeed).empty().append(user.location).fadeIn(animationSpeed);
            animationSpeed-=300;
            $("#friends_count").fadeOut(animationSpeed).empty().append(user.following).fadeIn(animationSpeed);
            animationSpeed+=300;
            $("#followers_count").fadeOut(animationSpeed).empty().append(user.followers).fadeIn(animationSpeed);
            animationSpeed-=300;
            $("#statuses_count").fadeOut(animationSpeed).empty().append(user.tweets).fadeIn(animationSpeed);
            animationSpeed+=300;
            $("#hits_count").empty().append(user.hits);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });
}

function setScreenNameFromDocument(){
    screenName = $("#screen_name").text().trim();
}
