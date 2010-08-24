//**                                                                        **//
var listType = "friendsList";
//
var ajaxActionIntervalId;
var searchRepliesIntervalId;
//
var tempHeight;
var screenName;
var pageTitle;
//
var loadingImageSmall;
var audioElement;
var messageElement;
//
var charCount = 0;
//
var mentionsCount = -1;
var newMentionsCount = 0;

//
$(document).ready(function() {
    createLoadingImage();
    createLoadingImageSmall();
    createAudioElement();
    createMessageElement();
    
    //
    setup();
    initialize($);
    //
    tweetStreamHooks();
    tweetHooks();
    //
    startAutoUpdate();
    setScreenNameFromDocument();
    setPageTitleFromDocument();
    doc = document;
});

//**                                                                        **//


function stopAutoUpdate(){
    if(ajaxActionIntervalId != null || searchRepliesIntervalId != null){
        clearInterval(ajaxActionIntervalId);
        clearInterval(searchRepliesIntervalId);
    }
}
function startAutoUpdate(){

    stopAutoUpdate();
    
    ajaxActionIntervalId = setInterval("ajaxAction('"+ listType +"')", 175000);
    searchRepliesIntervalId = setInterval("getNewReplyCount()", 185000);
}
//**                                                                        **//
var tweetStreamAjaxHooks;
function tweetStreamHooks(){
    /************************************************************************/
    /************************************************************************/
    $(".action").click(function(){
        var action = $(this).attr("id");
        listType = action;
        ajaxAction(action);
        resetTweetInput();
        resetPageTitle();
        resetNewMentionsCount();
    });
    /************************************************************************/
    /************************************************************************/
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
    /************************************************************************/
    /************************************************************************/
    $("#conversationList").click(function(){
        listType = "conversationList";
        ajaxAction(listType);
    });
    /************************************************************************/
    /************************************************************************/
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
    /************************************************************************/
    /************************************************************************/
    $("#new_tweet_submit_btn").mousedown(function(){
        $(this).addClass("new_tweet_submit_active");
    });

    $("#new_tweet_submit_btn").blur(function(){
        $(this).removeClass("new_tweet_submit_active");
    });
    $("#new_tweet_submit_btn").mouseup(function(){
        startLoading();
        stopAutoUpdate();
        $(this).removeClass("new_tweet_submit_active");
        $("#message_out").empty().append(loadingImage);
        var tweet_text = $("#new_tweet_text_txt").val();
        tweet_text = encodeURIComponent(tweet_text);
        var in_reply_to_id = $("#new_tweet_in_reply_to_id").val();
        
        abortAjax(tweetStreamAjaxHooks);

        tweetStreamAjaxHooks = $.ajax({
            url: "./tweet.ajax?action=post&in_reply_to_id=" + in_reply_to_id +"&tweet_text=" + tweet_text,
            success: function(data) {
                stopLoading();
                resetTweetInput();
                $("#message_out").empty().append(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                resetLoading();
                $("#message_out").empty().append(getMessageElement(dropletCommonError));
            }
        });
    });
    
    /************************************************************************/
    $("#user_info_action_hide_show").click(function(){
        $(this).toggleClass("user_info_show");
        $(this).toggleClass("user_info_hide");
        var height = $("#user_info_container").height();
        if(height != 0){
            $("#user_info_container").animate({
                height:0
            }, 250, "linear", function(){
                tempHeight = height;
            });
        } else{
            $("#user_info_container").animate({
                height:tempHeight
            }, 250, "linear");
        }
    });
    $("#user_info_action_reset").click(function(){
        showUserDetails(screenName);
    });

/************************************************************************/
/************************************************************************/
}

//**                                                                        **//
function resetTweetInput(){
    $("#new_tweet_in_reply_to_id").val("");
    $("#new_tweet_text_txt").val("");
    startAutoUpdate();
}

var ajaxActionAjax;
//**                                                                        **//
function ajaxAction(action){
    $("#"+action).children("a").addClass("loading_small");
    startLoading();
    stopAutoUpdate();
    abortAjax(ajaxActionAjax);
    ajaxActionAjax = $.ajax({
        url: "./statuslist.ajax?action=" + action,
        success: function(data) {
            stopLoading();
            $("#tweetUpdatePanel").empty().append(data);
            tweetHooks();
            startAutoUpdate();
            decrementHitsCounter();
            $("#"+action).children("a").removeClass("loading_small");
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
            $("#"+action).children("a").removeClass("loading_small");
        }
    });
}

var searchAjaxActionAjax;
//**                                                                        **//
function searchAjaxAction(query){
    startLoading();
    abortAjax(searchAjaxActionAjax);
    searchAjaxActionAjax = $.ajax({
        url: "./statuslist.ajax?action=search&q=" + encodeURIComponent(query),
        success: function(data) {
            stopLoading();
            listType = "search_" + query;
            $("#tweetUpdatePanel").empty().append(data);
            tweetHooks();
            decrementHitsCounter();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}

//**                                                                        **//
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

//**                                                                        **//
function replyHook(){
    $(".reply").unbind();
    $(".reply").click(function(){
        var id = $(this).attr("id").toString();
        id = id.substr("reply".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        replyTweet(tweetId, from_user);
    });
}

//**                                                                        **//
function retweetHook(){
    $(".retweet").unbind();
    $(".retweet").click(function(){
        $(this).children("a").toggleClass("isRetweet");
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("retweet".length, id.length);
        retweetTweet(tweetId);
    });
}

//**                                                                        **//
function favouriteHook(){
    $(".favourite").unbind();
    $(".favourite").click(function(){
        $(this).children("a").toggleClass("isFavourite");
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("favourite".length, id.length);
        favouriteTweet(tweetId);
    });
}

//**                                                                        **//
function deleteHook(){
    $(".delete").unbind();
    $(".delete").click(function(){
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("delete".length, id.length);
        deleteTweet(tweetId);
    });
}

//**                                                                        **//
function spamHook(){
    $(".spam").unbind();
    $(".spam").click(function(){
        var id = $(this).attr("id").toString();
        var tweetId = id.substr("spam".length, id.length);
        spamTweet(tweetId);
    });
}

var trackHookAjax;
//**                                                                        **//
function trackHook(){
    $(".track").unbind();
    $(".track").click(function(){
        stopAutoUpdate();
        $(this).children("a").toggleClass("isTracked");
        
        var id = $(this).attr("id").toString();
        id = id.substr("track".length, id.length);
        //
        var tweetId = id.substr(0, id.indexOf("_", 0));
        var from_user = id.substr(id.indexOf("_", 0)+1, id.length);
        //
        seedURL = "http://twitter.com/" + from_user +"/status/" + tweetId;        
        //
        abortAjax(trackHookAjax);
        trackHookAjax = $.ajax({
            url: "./tweet.ajax?action=track&tweetId=" + tweetId +"&listType=" +listType,
            success: function(data) {
                $("#message_out").empty().append(data).append(loadingImage);
                reloadConversation(seedURL);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                $("#message_out").empty().append(getMessageElement(dropletCommonError));
                resetLoading();
            }
        });
    });
}

//**                                                                        **//
function reloadHook(){
    $(".reload").unbind();
    $(".reload").click(function(){
        stopAutoUpdate();

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

//**                                                                        **//
function followHook(){
    $(".follow").unbind();
    $(".follow").click(function(){
        stopAutoUpdate();

        var id = $(this).attr("id").toString();
        var screen_name = id.substr("follow".length, id.length);
        //
        followUser(screen_name);
    });
}

//**                                                                        **//
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

//**                                                                        **//
function searchForFromUser(){
    $(".person").unbind();
    $(".person").click(function(){
        stopAutoUpdate();
        var user = $(this).text();
        user = user.substring(user.indexOf("@")+1, user.length);
        showUserDetails(user);
        query = "from:"+user;
        searchAjaxAction(query);
    });
}

//**                                                                        **//
var moreTweetsHookAjax;
function moreTweetsHook(){
    $("#more_tweet_submit_btn").unbind();
    $("#more_tweet_submit_btn").mousedown(function(){
        $(this).addClass("more_tweet_submit_active");
    });
    $("#more_tweet_submit_btn").blur(function(){
        $(this).removeClass("more_tweet_submit_active");
    });
    $("#more_tweet_submit_btn").mouseup(function(){
        $("#more_tweet_submit_btn").empty().append(loadingImageSmall);
        startLoading();
        stopAutoUpdate();
        var hrpoint = $("#more_tweet_submit_btn").attr("name");
        hrpoint = hrpoint.toString().substring(1, hrpoint.length);
        $(this).removeClass("more_tweet_submit_active");

        abortAjax(moreTweetsHookAjax);
        moreTweetsHookAjax =  $.ajax({
            url: ".statuslist.ajax?action=more&listType="+listType,
            success: function(data) {
                stopLoading();
                $("#tweetUpdatePanel").empty().append(data);
                
                $("#"+hrpoint).after("<hr class=\"page_break\"/>");
                tweetHooks();
                startAutoUpdate();
                $("#more_tweet_submit_btn").empty().append("more");
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                resetLoading();
                $("#more_tweet_submit_btn").empty().append("more");
                $("#message_out").empty().append(getMessageElement(dropletCommonError));
                
            }
        });
    });
}

//**                                                                        **//
function replyTweet(tweetId, from_user){
    startLoading();
    $("#new_tweet_in_reply_to_id").val(tweetId);
    $("#new_tweet_text_txt").val("@" +from_user + " ");
    $("#new_tweet_text_txt").focus();
    stopLoading();
}

//**                                                                        **//
var retweetTweetAjax;
function retweetTweet(tweetId){
    startLoading();
    abortAjax(retweetTweetAjax);
    retweetTweetAjax = $.ajax({
        url: "./tweet.ajax?action=retweet&tweetId=" + tweetId,
        success: function(data) {
            stopLoading();
            $("#message_out").empty().append(getMessageElement(data));
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
            
        }
    });
}

//**                                                                        **//
var favouriteTweetAjax;
function favouriteTweet(tweetId){
    startLoading();
    abortAjax(favouriteTweetAjax);
    favouriteTweetAjax = $.ajax({
        url: "./tweet.ajax?action=favourite&tweetId=" + tweetId,
        success: function(data) {
            stopLoading();
            $("#message_out").empty().append(getMessageElement(data));

        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
            
        }
    });
}

//**                                                                        **//
var deleteTweetAjax;
function deleteTweet(tweetId){
    startLoading();
    abortAjax(deleteTweetAjax);
    deleteTweetAjax = $.ajax({
        url: "./tweet.ajax?action=delete&tweetId=" + tweetId,
        success: function(data) {
            stopLoading();
            if(!data.toString().match("Error")){
                $("#dt"+tweetId).fadeOut("slow");
            }
            
            $("#message_out").empty().append(getMessageElement(data));
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));

        }
    });
}

//**                                                                        **//
var spamTweetAjax;
function spamTweet(userId){
    startLoading();
    abortAjax(spamTweetAjax);
    spamTweetAjax = $.ajax({
        url: "./tweet.ajax?action=spam&userId=" + userId,
        success: function(data) {
            stopLoading();
            $("#message_out").empty().append(getMessageElement(data));
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}

//**                                                                        **//
var followUserAjax;
function followUser(screen_name){
    startLoading();
    abortAjax(followUserAjax);
    followUserAjax = $.ajax({
        url: "./tweet.ajax?action=follow&screen_name=" + screen_name,
        success: function(data) {
            stopLoading();
            $("#message_out").empty().append(getMessageElement(data));
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}

//**                                                                        **//
function getDomElement(node){
    var source = node.data.source;
    while(source.match("&lt;") || source.match("&gt;") || source.match("&quot;")){
        source = source.replace("&lt;", "<");
        source = source.replace("&gt;", ">");
        source = source.replace("&quot;", "\"");
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
                                    <a href=\"#"+ node.data.from_user +"\" class=\"outlink b person\" target=\"_self\">"+ node.data.from_user +"</a> "+ node.data.text +"\
                                </div>\
                                <div class=\"tweet_info\">\
                                    <a href=\"http://twitter.com/"+ node.data.from_user +"/status/"+ node.data.id +"\" target=\"_blank\"/>"+ node.data.created_at +"</a>\
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

//**                                                                        **//
function getCharCountElement(){
    var charCount = document.createElement("div");
    charCount.setAttribute('id', 'char_count');
    charCount.setAttribute('class', 'simple_message');
    return charCount;
}

//**                                                                        **//
var reloadConversationAjax;
function reloadConversation(seedURL){
    startInfoVisLoading();
    startConversationLogging();
    abortAjax(reloadConversationAjax);
    reloadConversationAjax = $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {
            stopConversationLogging();
            stopInfoVisLoading();
            $("#infovis0 .node").remove();
            currentConversation = $.parseJSON(data);
            initialp($.parseJSON(data));
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            stopConversationLogging();
            stopInfoVisLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
    
}

//**                                                                        **//
function afterCompute(){
    $(".node").draggable();
    tweetHooks();
    statFilterSetup($);
}

//**                                                                        **//
var showUserDetailsAjax;
function showUserDetails(user){
    startLoading();
    abortAjax(showUserDetailsAjax);
    showUserDetailsAjax = $.ajax({
        url: "./user.ajax?action=get_user_info&screen_name=" + user,
        success: function(data) {
            stopLoading();
            if(tempHeight>0){
                $("#user_info_container").animate({
                    height:"100%"
                }, 250, "linear");
            }
            user = $.parseJSON(data);
            var animationSpeed = 100;
            $("#screen_name").fadeOut(animationSpeed).empty().append(user.screen_name).fadeIn(animationSpeed);
            animationSpeed+=100;
            $("#description").fadeOut(animationSpeed).empty().append(user.description).fadeIn(animationSpeed);
            animationSpeed-=100;
            $("#profile_image_url").fadeOut(animationSpeed).attr("src", user.profile_image_url);
            $("#profile_image_url").fadeIn(animationSpeed);
            animationSpeed+=100;
            $("#location").fadeOut(animationSpeed).empty().append(user.location).fadeIn(animationSpeed);
            animationSpeed-=100;
            $("#friends_count").fadeOut(animationSpeed).empty().append(user.following).fadeIn(animationSpeed);
            animationSpeed+=100;
            $("#followers_count").fadeOut(animationSpeed).empty().append(user.followers).fadeIn(animationSpeed);
            animationSpeed-=100;
            $("#statuses_count").fadeOut(animationSpeed).empty().append(user.tweets).fadeIn(animationSpeed);
            animationSpeed+=100;
            $("#hits_count").empty().append(user.hits);
            
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}

//**                                                                        **//
function decrementHitsCounter(){
    hitsCount = $("#hits_count").text().trim();
    $("#hits_count").text(--hitsCount);
    if(hitsCount<2){
        showUserDetails(screenName);
    }
}

//**                                                                        **//
function createLoadingImageSmall(){
    loadingImageSmall =document.createElement("img");
    loadingImageSmall.setAttribute('src', './assets/img/load_small.gif');
    loadingImageSmall.setAttribute('alt', 'Loading Conversation');
    loadingImageSmall.setAttribute('height', '20px');
    loadingImageSmall.setAttribute('width', '20px');
    loadingImageSmall.setAttribute('id', 'img_loading_small');
}

//**                                                                        **//
var getNewReplyCountAjax;
function getNewReplyCount(){
    abortAjax(getNewReplyCountAjax);
    getNewReplyCountAjax = $.ajax({
        url: "./statuslist.ajax?action=replyList",
        success: function(data) {
            var initial = data.toString().trim().length;
            var post = data.toString().replaceAll("tweet_container", "").trim().length;
            var tweet = initial - post;
            var eventual = (tweet/("tweet_container".length));
            if(eventual!= null && eventual > mentionsCount){
                var count = eventual - mentionsCount;
                if(mentionsCount > -1){
                    newMentionsCount += count;
                    var replyText = (newMentionsCount>1)? "replies": "reply";
                    $("#message_out").empty().append(getMessageElement(newMentionsCount + " new " + replyText));
                    document.getElementById("audio_sound").play();
                    $("title").empty().append("("+newMentionsCount+") " + pageTitle);
                }
                mentionsCount = eventual;
            }
            decrementHitsCounter();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}

//**                                                                        **//
function createAudioElement(){
    audioElement = document.createElement("audio");
    audioElement.setAttribute('id', 'audio_sound');
    audioElement.setAttribute('src', './assets/aud/action/action_reply_found.wav');
    audioElement.setAttribute('autobuffer', 'autobuffer');
    $("body").append(audioElement);
}



//**                                                                        **//
function setScreenNameFromDocument(){
    screenName = $("#screen_name").text().trim();
}

//**                                                                        **//
function setPageTitleFromDocument(){
    pageTitle = document.title;
}

//**                                                                        **//
function resetPageTitle(){
    document.title = pageTitle;
}

function resetNewMentionsCount(){
    newMentionsCount = 0;
}

