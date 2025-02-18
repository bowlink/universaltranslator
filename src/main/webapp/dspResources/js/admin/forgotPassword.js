/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

jQuery(function ($) {
    
    $(document).ready(function () {
        
        $("input:text,form").attr("autocomplete", "off");

        $('#retrievePasswordForm').submit(function(event) {
            $('#passwordFound').hide();
            $('#passwordNotFound').hide();

            $.ajax({
                url: '/forgotPassword.do',
                type: "Post",
                data: {
                    'identifier': $('#identifier').val()
                },
                success: function (data) {
                    if (data > 0) {
                        sendEmail(data);
                    } else {
                        $('#noResults').show();
                        $('.alert').delay(2000).fadeOut(1000);
                    }
                }
            });

           event.preventDefault(); 
        });
    });
    
    function sendEmail(userId) {
        $.ajax({
            url: '/sendPassword.do',
            type: "Post",
            data: {
                'userId': userId
            },
            success: function(response) {
                $('#noResults').hide();
                $('#emailSent').show();
            }
        });
    }
});