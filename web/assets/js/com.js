var loadingImage;
var infovisLoadElement;
//
var current = null;
var infovis = null;
//
var w = null , h = null;
var canvas = null;
var ht = null;
//
var currentConversation;
var nodeCurrentPositionLeft;
var nodeCurrentPositionRight;
var nodeCurrentId;
//
var firstLoad = true;
var loadingComplete = 0;
var doc = document;
//
function createLoadingImage(){
    loadingImage =doc.createElement("img");
    loadingImage.setAttribute('src', './assets/img/load.gif');
    loadingImage.setAttribute('alt', 'Loading Conversation');
    loadingImage.setAttribute('height', '15px');
    loadingImage.setAttribute('width', '128px');
    loadingImage.setAttribute('id', 'img_loading');

}
var dropletCommonError = "dropletweet is error, boo!";

var seedURL;// = "http://twitter.com/dropletweet/status/19821003664";

//"http://twitter.com/Nonomadic/status/20099105077"//MINIROLLS
//"http://twitter.com/conoro/status/19943612580"; // Show Review

//"http://twitter.com/dropletweet/status/19821003664";//Bug Tracking
//"http://twitter.com/damienmulley/status/19605819998"; //Largest To Date
////http://twitter.com/DerrenBrown/status/19483147595";//Don't sit down
//http://twitter.com/PhillyD/status/19382288629";//Philly D/
//"http://twitter.com/reimarie/status/19373181518";//Playing Games
//"http://twitter.com/markrock/status/19087326268";//Its wet in dublin
//Laporte Doesn't Know Google Docs Works "http://twitter.com/leolaporte/status/19034374335";
// Welcome Tweet "http://twitter.com/dropletweet/status/18859376985";

/******************************************************************************/

function addEvent(obj, type, fn) {
    if (obj.addEventListener) obj.addEventListener(type, fn, false);
    else obj.attachEvent('on' + type, fn);
}
//end
/*****************************************************************************/
function setup(){
    current = "infovis0";
    infovis = document.getElementById(current);
    w = infovis.offsetWidth , h = infovis.offsetHeight;

    //init canvas
    //Create a new canvas instance.
    canvas = new Canvas('mycanvas', {
        'injectInto': current,
        'width': w,
        'height': h
    });
    //end

    ht = new Hypertree(canvas, {
        //Change node and edge styles such as
        //color, width and dimensions.
        levelDistance:100,
        radius:120,
        offset:1,
        Node: {
            dim: 12,
            type: "circle",
            width: 300,
            height: 120,
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
        onBeforeCompute: function(node){
            
        },
        //Attach event handlers and add text to the
        //labels. This method is only triggered on label
        //creation
        onCreateLabel: function(domElement, node){
            

            $("#"+node.id).mousedown(function(){
                nodeCurrentPositionLeft = $("#"+node.id).css("left");
                nodeCurrentPositionRight = $("#"+node.id).css("right");
                
            });

            $("#"+node.id).mouseup(function(){
                if(nodeCurrentPositionLeft == $("#"+node.id).css("left") && nodeCurrentPositionRight == $("#"+node.id).css("right") && nodeCurrentId != node.id){
                    ht.onClick(node.id);
                    nodeCurrentId = node.id;
                }

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
            }
            else if(node._depth == 2){
                style.fontSize = "0.2em";
                style.opacity = "0.6";
                style.zIndex = "1000";
                style.marginTop = "-20px";
                domElement.innerHTML = getFarConversationElement(node);
            }
            else if(node._depth == 3){
                style.fontSize = "0.2em";
                style.opacity = "0.4";
                style.zIndex = "1000";
                style.marginTop = "0";
                domElement.innerHTML = getFarConversationElement(node);
            }
            else {
                style.fontSize = "0.2em";
                style.opacity = ".2";
                style.zIndex = "500";
                style.marginTop = "0";
                domElement.innerHTML = getFarConversationElement(node);
            }

            var left = parseInt(style.left);
            var w = domElement.offsetWidth;
            style.left = (left - w / 2) + 'px';
        },
        onAfterCompute:function(){
            afterCompute();
        }

    });
}

/*****************************************************************************/
var updateConversationAjax;
function updateConversation(seedURL){
    abortAjax(updateConversationAjax);
    startInfoVisLoading();
    startConversationLogging();
    updateConversationAjax = $.ajax({
        url: "./jit.json",
        data: "q=" + seedURL,
        method: "GET",
        success: function(data) {
            stopInfoVisLoading();
            stopConversationLogging();
            currentConversation = $.parseJSON(data);
            initialp(currentConversation);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            stopConversationLogging();
            stopInfoVisLoading();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });

}
/*****************************************************************************/
function initialp(json){
    $("#infovis_stats").fadeOut(350);
    //
    ht.fx.clearLabels(true);
    //load JSON data.
    ht.loadJSON(json);
    nodeCurrentId = json.id;
    //compute positions and plot.
    ht.refresh();
    //
    calculateStatistics();
    //end
    afterCompute();

}


function calculateStatistics(){
    $("#tweets_out").empty().append(currentConversation.data.stats.tweetCount);
    
    var peeps = currentConversation.data.stats.peepCount;
    peeps = peeps.substring(1, peeps.length-1).split(",");
    $("#peeps_out").empty().append(peeps.length);
    
    var terms = currentConversation.data.stats.terms.split("|");
    doMaxMinDist(terms);
    $("#terms_out").empty();
    for(var j=0; j < terms.length; j++){

        var termElement = getTermsElement(terms[j]);

        $("#terms_out").append(termElement);
        $("#terms_out").append(" ");
    }
   
    $("#infovis_stats").fadeIn(350);
}

var min = 0;
var max = 0;
var distribution = 0;
function doMaxMinDist(terms){
    var count = new Array();
    for(var i = 0; i<terms.length; i++){
        var term = terms[i].split(",");
        count[i] = term[1];
    }

    min = count[0];
    max = count[0];
    for(var j = 0; j<count.length; j++){
        if(count[j]<=min){
            min = count[j];
        }
        
        if(count[j]>=max){
            max = count[j];
        }
    }
    distribution = (max-min)/3;

}

var initializeAjax;
function initialize($){
    abortAjax(initializeAjax);
    initializeAjax = $.ajax({
        url: "./util.ajax",
        data: "action=get_latest_url",
        method: "GET",
        timeout: 60000,
        success: function(data) {
            updateConversation(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}
function startLoading(){
    $("#message_out").empty().append(loadingImage);
    loadingComplete++;
}


function stopLoading(){
    if(--loadingComplete == 0){
        $("#img_loading").remove();
    }
}

function resetLoading(){
    $("#message_out").empty();
    loadingComplete = 0;
}

String.prototype.replaceAll=function(s1, s2) {
    return this.replace(new RegExp(s1,"g"), s2);
}

function getTermsElement(termString){
    var termArray = termString.split(",");
    var term = termArray[0];
    var count = termArray[1];
    var infovis_stat_text_size = "infovis_stat_text_medium";
    if(term.toString().match("#")){
        infovis_stat_text_size ="infovis_stat_text_medium";
    }else if(count == max){
        infovis_stat_text_size ="infovis_stat_text_largest";
    } else if(count == min){
        infovis_stat_text_size ="infovis_stat_text_smallest";
    }else if(count > (min + (distribution*2))){
        infovis_stat_text_size ="infovis_stat_text_large";
    }else if(count > (min + distribution)){
        infovis_stat_text_size ="infovis_stat_text_medium";
    }else{
        infovis_stat_text_size ="infovis_stat_text_medium";
    }
    var termLink = document.createElement("a");
    termLink.setAttribute("href", "##");
    termLink.setAttribute("class", "infovis_stat_filter " + infovis_stat_text_size);
    termLink.setAttribute("id", term);
    termLink.textContent = term;
    return termLink;
}

function statFilterSetup($){
    $(".infovis_stat_filter").unbind();
    $(".infovis_stat_filter").click(function(){
        var term = $(this).attr("id").toString();
        var termContainer = $(this);
        termContainer.toggleClass("infovis_stat_filter_term_highlight");
        $(".node").each(function(){
            var nodeText = $(this).attr("id").toString();
            nodeText = $("#" + nodeText + " > .node_tweet_container > .tweet_text:first");
            if(nodeText.length>0){
                var tweet_text = nodeText.get(0).innerHTML;
                var t = term.trim().toString();
                tweet_text = tweet_text.toLowerCase();
                if(tweet_text.match("[^@#]("+t+")")){
                    if(termContainer.hasClass("infovis_stat_filter_term_highlight")){
                        $(this).addClass("infovis_stat_filter_node_highlight");
                    }else if (!termContainer.hasClass("infovis_stat_filter_term_highlight")){
                        $(this).removeClass("infovis_stat_filter_node_highlight");
                    }
                }
            }
        });
        
    });
   
}

function getFarConversationElement(node){

    var domElement = "\
                        <div class=\"node_tweet_container\">\
                                "+ node.data.from_user +"\
                            <div class=\"tweet_text\" style=\"display:none\">\
                                "+ node.data.text +"\
                            </div>\
                        </div>";
    return domElement;
}

function startConversationLogging(){
    loggingIntervalID = setInterval("conversationLoadingLogger()", 1200);
}

var conversationAjaxLoadingLoggerAjax;
function conversationLoadingLogger(){
    abortAjax(conversationAjaxLoadingLoggerAjax);
    conversationAjaxLoadingLoggerAjax = $.ajax({
        url: "./util.ajax",
        data: "action=get_loading_status",
        method: "GET",
        timeout: 60000,
        success: function(data) {
            if(data.toString().length > 0){
                data = data.toString().replaceAll("\\|", "<br />")
                $("#infovis_message_out").empty().append(getMessageElement(data));
            }else if(data.toString().trim() == ("Complete")){
                $("#infovis_message_out").empty();
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            stopConversationLogging();
            $("#message_out").empty().append(getMessageElement(dropletCommonError));
        }
    });
}

var loggingIntervalID;

function stopConversationLogging(){
    while(loggingIntervalID!= null){
        $("#message_out").empty();
        loggingIntervalID = clearInterval(loggingIntervalID);
        
    }
}

function abortAjax(ajax){
    if(ajax!= null){
        ajax.abort();
    }
}

//**                                                                        **//
function createMessageElement(){
    messageElement = doc.createElement("div");
    messageElement.setAttribute("class", "simple_message");
}

//**                                                                        **//
function getMessageElement(message){
    messageElement.innerHTML = message;
    return messageElement;
}


function startInfoVisLoading(){
    $("#infovis0").append(infovisLoadElement);
    $(".infovis_load").fadeIn("fast");
}

function stopInfoVisLoading(){
    $(".infovis_load").fadeOut("fast", function(){
        $(".infovis_load").remove();
    });
}

function createInfoVisLoadElement(){
    infovisLoadElement = document.createElement("div");
    infovisLoadElement.setAttribute("class", "infovis_load");
    infovisLoadElement.innerHTML = "<img src=\"./assets/img/load.gif\" />"
+"<div id=\"infovis_message_out\" class=\"infovis_simple_message\"><div class=\"simple_message\" />Charging..</div></div>";
}

