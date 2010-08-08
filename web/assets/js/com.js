var loadingImage;
var current = null;
var infovis = null;
var w = null , h = null;
var canvas = null;
var ht = null;
var currentConversation;
var nodeCurrentPositionLeft;
var nodeCurrentPositionRight;
var nodeCurrentId;
var firstLoad = true;
var loadingComplete = 0;

function createLoadingImage(){
    loadingImage =document.createElement("img");
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
            startLoading();
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
                style.opacity = ".2";
                style.zIndex = "500";
                style.marginTop = "0";
                domElement.innerHTML = node.name;
            }

            var left = parseInt(style.left);
            var w = domElement.offsetWidth;
            style.left = (left - w / 2) + 'px';
        },
        onAfterCompute:function(){
            stopLoading();
            afterCompute();
        }

    });
}

/*****************************************************************************/
function updateConversation(seedURL){
    startLoading();
    $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {
            stopLoading();
            currentConversation = $.parseJSON(data);
            initialp(currentConversation);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(dropletCommonError);
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
    
    var terms = currentConversation.data.stats.terms.split(" ");
    $("#terms_out").empty();
    for(var j=0; j < terms.length; j++){
        $("#terms_out").append(terms[j]);
        $("#terms_out").append("<br />");
    }
    $("#infovis_stats").fadeIn(350);
}

function initialize($){
    startLoading();
    $.ajax({
        url: "./util.ajax?action=get_latest_url",
        success: function(data) {
            stopLoading();
            updateConversation(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            resetLoading();
            $("#message_out").empty().append(dropletCommonError);
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