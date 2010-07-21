$(document).ready(function(){
    $('#background_clouds_text').click(function() {
        $('#background_clouds_text_dropletweet').fadeOut('slow');
        $('#background_clouds_text_coming').fadeOut('slow');
    });
    $('#btn_signup').click(function() {
        var email = $("#txt_email").val();
        $('#email_success').css("display", "none");
        $('#email_fail').css("display", "none");
        $.ajax({
            url: "./signup.json?email=" + email,
            success: function(data) {
               
                try{
                    var json = jQuery.parseJSON(data);
                    if(json.success == "true"){
                        $('#email_success').fadeIn('fast');
                    } else{
                        $('#email_fail').fadeIn('fast');
                    }
                }catch(e){
                    $('#email_fail').fadeIn('fast');
                }
            }
        });

    });

});
