


jQuery(function ($) {
    
    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");
        
        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
        
        var toolTipEl = document.getElementById('toolTipBTN');
        if(toolTipEl != null) {
            var tooltip = new bootstrap.Tooltip(toolTipEl, {
              boundary: document.body // or document.querySelector('#boundary')
            });
        }
    });
   
    
    $(document).on('click','.printConfig',function() {
        
        $('body').overlay({
           glyphicon : 'print',
           message : 'Gathering Details...'
         });
        
         var configId = $(this).attr('rel');

         $.ajax({
             url: '/administrator/configurations/createConfigPrintPDF.do',
             data: {
                 'configId': configId
             },
             type: "GET",
             dataType : 'text',
             contentType : 'application/json;charset=UTF-8',
             success: function(data) {
                 if(data !== '') {
                     window.location.href = '/administrator/configurations/printConfig/'+ data;
                     $('.overlay').css('display','none');
                 }
                 else {
                     $('#errorMsg').show();
                 }
             }
         });
         
         return false;
   });

   $(document).on('click', '.exportConfig', function() {
            
        var configId = $(this).attr('rel');

        if(confirm("Are you sure you want to export this configuration?")) {

            $.ajax({
                url: 'createConfigExportFile.do',
                data: {
                    'configId': configId
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/printConfigExport/'+ data;
                        //$('#dtDownloadModal').modal('toggle');
                    }
                    else {
                        $('#exportErrorMsg').show();
                    }
                }
            });
        }
    });
   
});

