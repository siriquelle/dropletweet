var Log = {
    elem: false,
    write: function(text){
        if (!this.elem)
            this.elem = document.getElementById('log');
        this.elem.innerHTML = text;
        this.elem.style.left = (200) + 'px';
    }
};

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
            update(seedURL);
        }
    });
    $("#btn_UpdateConversation").click(function(){
        update(seedURL);
    });

});

function update(seedURL){
    Log.write("<img src=\"./assets/img/load.gif\" />");

    document.getElementById('infovis').innerHTML = "";
    $.ajax({
        url: "./jit.json?q=" + seedURL,
        success: function(data) {

            try{
                var json = $.parseJSON(data);

                initialp(json);
            }catch(e){
                Log.write(e.toString());
            }
        }
    });

}

function initialp(json){
    //end
    var infovis = document.getElementById('infovis');
    var w = infovis.offsetWidth , h = infovis.offsetHeight+100;

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
            color: "#a00"
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
            Log.write("Loading..");
        },
        //Attach event handlers and add text to the
        //labels. This method is only triggered on label
        //creation
        onCreateLabel: function(domElement, node){
            domElement.innerHTML = node.name + ":<br /> " + node.data.tweet;
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
                style.fontSize = "1.2em";
                style.opacity = "1";
                style.color = "#ddd";
                style.zIndex = "3000";
                style.textAlign = "left";
                style.marginLeft = "20px";
                style.marginTop = "-80px";

                domElement.innerHTML = node.name + " :<br /> " + node.data.tweet;

            } else if(node._depth == 1){
                style.fontSize = "0.8em";
                style.opacity = "0.8";
                style.color = "#ddd";
                style.zIndex = "2000";
                style.marginTop = "-40px";
                domElement.innerHTML = node.name + " :<br />  " + node.data.tweet;
            } else if(node._depth == 2){
                style.fontSize = "0.2em";
                style.color = "#ddd";
                style.opacity = "0.6";
                style.zIndex = "1000";
                style.marginTop = "-20px";
                domElement.innerHTML = node.name;
            }
             else if(node._depth == 3){
                style.fontSize = "0.2em";
                style.color = "#ddd";
                style.opacity = "0.4";
                style.zIndex = "1000";
                style.marginTop = "0";
                domElement.innerHTML = node.name;
            }
            else {
                style.display = 'none';
            }

            var left = parseInt(style.left);
            var w = domElement.offsetWidth;
            style.left = (left - w / 2) + 'px';
        },

        onAfterCompute: function(){
            Log.write("Complete");
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
