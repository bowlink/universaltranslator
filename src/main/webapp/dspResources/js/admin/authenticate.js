/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {
        
        var totalInvalid = 0;

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
                        totalInvalid = 0;
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
            
            $('.newCodeMsg').hide();
            
            if($('.verificationCode').val() === '') {
                $('.newCodeMsg').addClass('alert-danger').removeClass('alert-success').html('The authentication code is required.');
                $('.newCodeMsg').show();
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
                            totalInvalid+=1;
                            $('.newCodeMsg').addClass('alert-danger').removeClass('alert-success').html('The authentication code entered does not match what was sent.');
                            $('.newCodeMsg').show();
                            
                            if(totalInvalid == 3) {
                                disableUser();
                            }
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
        
        function disableUser() {
        
            $('.newCodeMsg').addClass('alert-danger').removeClass('alert-success').html('Your user account has been disabled due to three successive invalid attempts to login. Please contact the help desk to enable your account.');
            $('.newCodeMsg').show();

            $.ajax({
                url: '/disableUser',
                type: "POST",
                success: function(data) {
                    setTimeout(function () {
                        top.location.href= '/logout'; 
                     }, 6000);
                }
            });
        }
    });
});