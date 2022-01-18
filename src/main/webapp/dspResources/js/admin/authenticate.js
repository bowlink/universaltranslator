/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");
        
        $(document).on('click', '.resendCode', function() {
            $.ajax({
                url: '/resendCode',
                type: "POST",
                success: function(data) {

                    var data = $(data);

                    //Check if the session has expired.
                    if(data.find('.username').length > 0) {
                       top.location.href = '/logout';
                    }
                    else if(data[0] == 0) {
                        top.location.href = '/logout';
                    }
                    else {
                        $('.newCodeMsg').addClass('alert-success').removeClass('alert-danger').html('A new authentication code has been sent.');
                        $('.newCodeMsg').show();
                        $('.newCodeMsg').delay(2000).fadeOut(3000);
                    }
                }
            });
        });
        
        $('.verifyCode').keydown(function(event){ 
            var keyCode = (event.keyCode ? event.keyCode : event.which);   
            if (keyCode == 13) {
                $('.verifyCode').trigger('click');
            }
        });
        
        $('.verificationCode').keydown(function(event){ 
            var keyCode = (event.keyCode ? event.keyCode : event.which);   
            if (keyCode == 13) {
                $('.verifyCode').trigger('click');
            }
        });
        
        $(document).on('click', '.verifyCode', function() {
            
            if($('.verificationCode').val() === '') {
                $('.newCodeMsg').addClass('alert-danger').removeClass('alert-success').html('The authentication code is required.');
                $('.newCodeMsg').show();
                $('.newCodeMsg').delay(2000).fadeOut(4000);
            }
            else {
                $.ajax({
                    url: '/validateCode',
                    type: "POST",
                    data: {
                      'vericationCode':  $('.verificationCode').val()
                    },
                    success: function(data) {
                       
                        if(data == 0) {
                            $('.newCodeMsg').addClass('alert-danger').removeClass('alert-success').html('The authentication code entered does not match what was sent.');
                            $('.newCodeMsg').show();
                            $('.newCodeMsg').delay(2000).fadeOut(4000);
                        }
                        else if(data == 1) {
                            top.location.href = '/administrator';
                        }
                        else {
                            top.location.href = '/logout';
                        }
                    }
                });
            }
        });
    });
});