
function addEvent(obj, type, fn) {
    if (obj.addEventListener) obj.addEventListener(type, fn, false);
    else obj.attachEvent('on' + type, fn);
}

var url = "";
$(document).ready(function() {
    $("#btn_LoadSeedURL").click(function(){
        var seedURL = $("#txt_SeedURL").val();
        
        if(seedURL != url){           
            url = seedURL;
            updateConversation(seedURL);
        }
    });
    $("#btn_UpdateConversation").click(function(){
        updateConversation(seedURL);
    });

});