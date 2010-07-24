var loadingImage;

function createLoadingImage(){
    loadingImage =document.createElement("img");
    loadingImage.setAttribute('src', './assets/img/load.gif');
    loadingImage.setAttribute('alt', 'Loading Conversation');
    loadingImage.setAttribute('height', '15px');
    loadingImage.setAttribute('width', '128px');
    loadingImage.setAttribute('id', 'img_loading');

}

var dropletCommonError = "dropletweet is error, boo!";

var seedURL = "http://twitter.com/PhillyD/status/19382288629";//Philly D/
    //"http://twitter.com/reimarie/status/19373181518";//Playing Games
    //"http://twitter.com/markrock/status/19087326268";//Its wet in dublin
//Laporte Doesn't Know Google Docs Works "http://twitter.com/leolaporte/status/19034374335";
// Welcome Tweet "http://twitter.com/dropletweet/status/18859376985";



/*****************************************************************************/
function updateConversation(seedURL){
    $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {
            initialp($.parseJSON(data));
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            $("#message_out").empty().append(dropletCommonError);
        }
    });

}

/******************************************************************************/


function addEvent(obj, type, fn) {
    if (obj.addEventListener) obj.addEventListener(type, fn, false);
    else obj.attachEvent('on' + type, fn);
}

