/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    $("input:text,form").attr("autocomplete", "off");
    
    $(document).on('click', '.close', function() {
       window.location.reload(); 
    });
    
    $(document).on('click', '.checkFTP', function() {
        $.ajax({
             url: '/administrator/processing-activity/configFTPCheck',
             data: {},
             type: "GET",
             success: function(data) {
                 $("#configFTPCheckdModal").html(data);
             }
         });
    });
    
    $("#configFTPCheckdModal").on('shown.bs.modal', function () {
        
        $('#returnMsg').html("");
        $.ajax({
            url: '/administrator/processing-activity/runConfigFTPCheck',
            data: {},
            type: "GET",
            error: function() {
               $('#spinnerSymbol').removeClass('fa-spinner fa-spin').addClass('fa-times');
               $('#spinnerSymbol').css('color','red');
               $('#returnMsg').html("The FTP check ran into an issue.");
            },
            success: function(data) {
                if(data.indexOf('connection error') > 0) {
                    $('#spinnerSymbol').removeClass('fa-spinner fa-spin').addClass('fa-times');
                    $('#spinnerSymbol').css('color','red');
                    $('#returnMsg').html(data);
                }
                else {
                    $('#spinnerSymbol').removeClass('fa-spinner fa-spin').addClass('fa-check-circle');
                    $('#spinnerSymbol').css('color','green');
                    $('#returnMsg').html(data);

                    if(data.indexOf('successfully') > 0) {
                        $('#successMsg').show();
                    }
                }
            },
            timeout: 50000
        });
    });
});


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('body').overlay({
        glyphicon: 'floppy-disk',
        message: 'Processing...'
    });

    $('#searchForm').submit();

}