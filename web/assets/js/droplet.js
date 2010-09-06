//**                                                                        **//
var listType = "friendsList";
//
var ajaxActionIntervalId;
//
var tempHeight;
var screenName;
var pageTitle;
//
var loadingImageSmall;
var audioElement;
var messageElement;
var charCountElement;
//
var charCount = 0;
//
$(document).ready(function() {
    createLoadingImage();
    createLoadingImageSmall();
    createAudioElement();
    createMessageElement();
    createCharCountElement();
    createInfoVisLoadElement();
    //
    setup();
    initialize($);
    //
    ajaxAction(listType);
    //
    tweetStreamHooks();
    tweetHooks();
    userHooks();
    //
    startAutoUpdate();
    setScreenNameFromDocument();
    setPageTitleFromDocument();
    doc = document;
    //
    //getNewMentionsCount();
    setInterval("getNewMentionsCount()", 30000);
    //
    //getNewDmCount();
    setInterval("getNewDmCount()", 40000);
    //
    setInterval("showUserDetails('"+screenName+"')", 3600000);
});

//**                                                                        **//


function stopAutoUpdate(){
    if(ajaxActionIntervalId != null){
        ajaxActionIntervalId = clearInterval(ajaxActionIntervalId);
    }
}
function startAutoUpdate(){
    stopAutoUpdate();
    ajaxActionIntervalId = setInterval("ajaxAction('"+ listType +"')", 200000);
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
    });
    
    $("#dmList").click(function(){
        resetNewDmCount();
    });
    $("#replyList").click(function(){
        resetNewMentionsCount();
    });
    
    /************************************************************************/
    /************************************************************************/
    $("#search").click(function(){
        $("#search_a").toggleClass("search_a_toggle");
        $("#action_search_container_outer").toggle();
        $("#search_txt").val("");
        $("#search_txt").focus();
        $("#search_btn").click(function(){
            stopAutoUpdate();
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
        $("#message_out").empty().append(getCharCountElement());
    });
    
    $("#new_tweet_text_txt").keyup(function(){
        $("#message_out").empty().append(getCharCountElement());
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
        $(this).removeClass("new_tweet_submit_active");
        var tweet_text = $("#new_tweet_text_txt").val();
        tweet_text = encodeURIComponent(tweet_text);
        var in_reply_to_id = $("#new_tweet_in_reply_to_id").val();
        
        abortAjax(tweetStreamAjaxHooks);
        tweetStreamAjaxHooks = $.ajax({
            url: "./tweet.ajax",
            data: "action=post&in_reply_to_id=" + in_reply_to_id +"&tweet_text=" + tweet_text,
            type: "POST",
            timeout: 60000,
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
  
/************************************************************************/
/************************************************************************/
}

//**                                                                        **//
function resetTweetInput(){
    $("#new_tweet_in_reply_to_id").val("");
    $("#new_tweet_text_txt").val("");
}

var ajaxActionAjax;
//**                                                                        **//
function ajaxAction(action){

    $("#"+action).children("a").addClass("loading_small");
    startLoading();
    stopAutoUpdate();
    abortAjax(ajaxActionAjax);
    ajaxActionAjax = $.ajax({
        url: "./statuslist.ajax",
        type: "GET",
        dataType: "HTML",
        data: "action="+action,
        cache: false,
        timeout: 60000,
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
            startAutoUpdate();
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
        url: "./statuslist.ajax",
        data: "action=search&q=" + encodeURIComponent(query),
        type: "GET",
        dataType: "HTML",
        timeout: 60000,
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
            url: "./tweet.ajax",
            data: "action=track&tweetId=" + tweetId +"&listType=" +listType,
            type: "POST",
            timeout: 60000,
            success: function(data) {
                $("#message_out").empty().append(data);
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

        var hrpoint = $("#more_tweet_submit_btn").attr("name");
        hrpoint = hrpoint.toString().substring(1, hrpoint.length);
        $(this).removeClass("more_tweet_submit_active");

        abortAjax(moreTweetsHookAjax);
        moreTweetsHookAjax =  $.ajax({
            url: ".statuslist.ajax",
            data: "action=more&listType="+listType,
            type: "GET",
            timeout: 60000,
            success: function(data) {
                stopLoading();
                $("#tweetUpdatePanel").empty().append(data);  
                $("#"+hrpoint).after("<hr class=\"page_break\"/>");
                tweetHooks();
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
    var pre = "@";
    if(listType == "dmList"){
        if($("#dt"+tweetId).hasClass("tweet_container")){
            pre = "d ";
        }
    }
    $("#new_tweet_in_reply_to_id").val(tweetId);
    $("#new_tweet_text_txt").val(pre + from_user + " ");
    $("#new_tweet_text_txt").focus();
    stopLoading();
}

//**                                                                        **//

function retweetTweet(tweetId){
    if(confirm("Retweet this tweet to your followers?")){
        startLoading();
        $.ajax({
            url: "./tweet.ajax",
            data: "action=retweet&tweetId=" + tweetId,
            type: "POST",
            timeout: 60000,
            success: function(data) {
                stopLoading();
                $("#message_out").empty().append(data);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                resetLoading();
                $("#message_out").empty().append(getMessageElement(dropletCommonError));
            
            }
        });
    }
}

//**                                                                        **//

function favouriteTweet(tweetId){
    startLoading();    
    $.ajax({
        url: "./tweet.ajax",
        data: "action=favourite&tweetId=" + tweetId,
        type: "POST",
        timeout: 60000,
        success: function(data) {
            stopLoading();
            $("#message_out").empty().append(data);

        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
            
        }
    });
}

//**                                                                        **//

function deleteTweet(tweetId){
    
    if(confirm("Are you sure you want to delete this tweet?")){
        startLoading();
        $.ajax({
            url: "./tweet.ajax",
            data: "action=delete&tweetId=" + tweetId,
            type: "POST",
            timeout: 60000,
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
}

//**                                                                        **//

function spamTweet(userId){
    
    if(confirm("Are you sure you want to report this user for spamming?")){
        startLoading();
        $.ajax({
            url: "./tweet.ajax",
            data: "action=spam&userId=" + userId,
            type: "POST",
            timeout: 60000,
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
}

//**                                                                        **//

function followUser(screen_name){
    startLoading();
    
    $.ajax({
        url: "./tweet.ajax",
        data: "action=follow&screen_name=" + screen_name,
        type: "POST",
        timeout: 60000,
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
    charCountElement.textContent = (140 - $("#new_tweet_text_txt").val().length);
    return charCountElement;
}

function createCharCountElement(){
    charCountElement = doc.createElement("div");
    charCountElement.setAttribute('id', 'char_count');
    charCountElement.setAttribute('class', 'simple_message');
}

//**                                                                        **//
var reloadConversationAjax;
function reloadConversation(seedURL){
    startInfoVisLoading();
    startConversationLogging();
    abortAjax(reloadConversationAjax);
    reloadConversationAjax = $.ajax({
        url: "./jit.json",
        data: "q=" + seedURL,
        type: "GET",
        timeout: 60000,
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

//****************************************************************************//
//**                                                                        **//
function userHooks(){
    descriptionHook();
    userInfoActionResetHook();
    userInfoActionHideShowHook();
}
//**                                                                        **//
function descriptionHook(){
    var description;
    $("#description").focus(function(){
        description = $("#description").text();
        $("#description").addClass("user_info_details_editing");
    });

    $("#description").blur(function(){
        $("#description").removeClass("user_info_details_editing");
        if(description.trim() != $("#description").text().trim()){
            description = $("#description").text().trim();
            setUserDescription(description);
        }
    });
}
//**                                                                        **//
var setUserDescriptionAjax;
function setUserDescription(description){
    startLoading();
    abortAjax(setUserDescriptionAjax);
    setUserDescriptionAjax = $.ajax({
        url: "./user.ajax",
        data: "action=set_user_description&description=" + description,
        type: "POST",
        timeout: 60000,
        success: function(data) {
            stopLoading();
           
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}
//**                                                                        **//
var showUserDetailsAjax;
function showUserDetails(user){
    startLoading();
    abortAjax(showUserDetailsAjax);
    $("#description").attr("contentEditable", "false");
    showUserDetailsAjax = $.ajax({
        url: "./user.ajax",
        data: "action=get_user_info&screen_name=" + user,
        type: "GET",
        timeout: 60000,
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
function userInfoActionHideShowHook(){
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
}

function userInfoActionResetHook(){
    $("#user_info_action_reset").click(function(){
        showUserDetails(screenName);
        $("#description").attr("contentEditable", "true");
    });
}

//**                                                                        **//
//****************************************************************************//
function decrementHitsCounter(){
    hitsCount = $("#hits_count").text().trim();
    $("#hits_count").text(--hitsCount);
}

//**                                                                        **//
function createLoadingImageSmall(){
    loadingImageSmall =doc.createElement("img");
    loadingImageSmall.setAttribute('src', './assets/img/load_small.gif');
    loadingImageSmall.setAttribute('alt', 'Loading Conversation');
    loadingImageSmall.setAttribute('height', '20px');
    loadingImageSmall.setAttribute('width', '20px');
    loadingImageSmall.setAttribute('id', 'img_loading_small');
}

//****************************************************************************//
//**                                                                        **//
//****************************************************************************//
//****************************************************************************//
//** START GET_NEW_MENTIONS_COUNT                                           **//
//****************************************************************************//
var mentionsCount = -1;
var newMentionsCount = 0;
var getNewMentionsCountAjax;

function getNewMentionsCount(){
    abortAjax(getNewMentionsCountAjax);
    getNewMentionsCountAjax = $.ajax({
        url: "./user.ajax",
        data: "action=get_mentions_count",
        type: "GET",
        timeout: 60000,
        success: function(data) {
            var user = $.parseJSON(data);
            if(user!=null){
                var latest = user.mentions_count;
                if(latest!= null && latest > mentionsCount){
                    if(mentionsCount > -1){
                        newMentionsCount += (latest - mentionsCount);
                        $("#replyList_count_out").empty().append(newMentionsCount);
                        document.getElementById("audio_sound").play();
                        $("title").empty().append("("+ (newMentionsCount + newDmCount) +") " + pageTitle);
                    }
                    mentionsCount = latest;
                }
                decrementHitsCounter();
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
        }
    });
}

function resetNewMentionsCount(){
    newMentionsCount = 0;
    $("#replyList_count_out").empty();
}
//****************************************************************************//
//** END GET_NEW_MENTIONS_COUNT                                             **//
//****************************************************************************//
//****************************************************************************//
//** START GET_NEW_DM_COUNT                                                 **//
//****************************************************************************//
var dmCount = -1;
var newDmCount = 0;
var getNewDmCountAjax;

function getNewDmCount(){
    abortAjax(getNewDmCountAjax);
    getNewDmCountAjax = $.ajax({
        url: "./user.ajax",
        data: "action=get_dm_count",
        type: "GET",
        timeout: 60000,
        success: function(data) {
        
            var user = $.parseJSON(data);
            if(user != null){
                var latest = user.dm_count;

                if(latest!= null && latest > dmCount){
                    if(dmCount > -1){
                        newDmCount += (latest - dmCount);
                        $("#dmList_count_out").empty().append(newDmCount);
                        document.getElementById("audio_sound").play();
                        $("title").empty().append("("+ (newDmCount + newMentionsCount) + ") " + pageTitle);
                    }
                    dmCount = latest;
                }
            
                decrementHitsCounter();
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
        }
    });
}

function resetNewDmCount(){
    newDmCount = 0;
    $("#dmList_count_out").empty();
}
//****************************************************************************//
//**                                                                        **//
//****************************************************************************//
//**                                                                        **//
//****************************************************************************//
function createAudioElement(){
    audioElement = doc.createElement("audio");
    audioElement.setAttribute('id', 'audio_sound');
    audioElement.setAttribute('src', './assets/aud/action/action_reply_found.ogg');
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



