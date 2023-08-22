


require(['./main'], function () {
    
    $(document).on('click', '.checkFTP', function() {
        $.ajax({
             url: '/administrator/configurations/configFTPCheck',
             data: {
                 'configId': $(this).attr('rel'),
                 'transportId': $(this).attr('rel2')
             },
             type: "GET",
             success: function(data) {
                 $("#configFTPCheckdModal").html(data);
             }
         });
    });
    
    $("#configFTPCheckdModal").on('shown.bs.modal', function () {
        
        var transportId = 0;
        if (typeof $('input[id=transportId]').val() !== 'undefined') {
            transportId = $('input[id=transportId]').val();
        }
        
        if(transportId > 0) {
            $('#returnMsg').html("");
            $.ajax({
                url: '/administrator/configurations/runConfigFTPCheck',
                data: {
                    'transportId': transportId
                },
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
                timeout: 30000
            });
        }
    });
    
    $(document).on('click', '.importConfig', function() {
        $.ajax({
             url: '/administrator/configurations/configImportUpload',
             data: {},
             type: "GET",
             success: function(data) {
                 $("#configFileUploadModal").html(data);
             }
         });
    });
    
    //The function to submit the import file
    $(document).on('click', '#submitImportFileButton', function (event) {
        var errorFound = 0;

        $('#importConfigFileDiv').removeClass("has-error");
        $('#importConfigFileMsg').removeClass("has-error");
        $('#importConfigFileMsg').html('');
        $('#errorMsg').hide();

        var expectedExtension = $('#expectedExt').val();

        if($('#importConfigFile').val() !== '') {
            var file = $('#importConfigFile').val();
            var uploadedFileExt = file.substr( (file.lastIndexOf('.') +1) );
        }

        //Make sure a file is selected and is a text file
        if ($('#importConfigFile').val() === '') {
            $('#importConfigFileDiv').addClass("has-error");
            $('#importConfigFileMsg').addClass("has-error");
            $('#importConfigFileMsg').html('The file is a required field.');
            errorFound = 1;
        }
        else if (uploadedFileExt != expectedExtension) {
            $('#importConfigFileDiv').addClass("has-error");
            $('#importConfigFileMsg').addClass("has-error");
            $('#importConfigFileMsg').html('The configruation import file must have a .' + $('#expectedExt').val() + ' extension.');
            errorFound = 1;
        }

        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {
            //check and submit form
            var form = $('#importConfigFileForm')[0];
            var formData = new FormData(form);
            $.ajax({
                url: '/administrator/configurations/submitConfigurationImportFile',
                type: "POST",
                enctype: 'multipart/form-data',
                processData: false,  // Important!
                contentType: false,
                cache: false,
                data: formData,
                success: function(data) {
                   
                   if(data == 1) {
                       window.location.href = '/administrator/configurations/list';
                   }
                   else if(data == 2) {
                       $('#importConfigFile').val("");
                       $('#importError').html("The file uploaded was not a correct configuration import script.");
                       $('#errorMsg').show();
                   }
                   else {
                       $('#importConfigFile').val("");
                       $('#errorMsg').show();
                   }
                }
            });
        }
     });
        
    $(document).on('click', '.uploadFile', function() {
        var fileDropLocation = $(this).attr('rel2');
        var configId = $(this).attr('rel');

        $.ajax({
             url: '/administrator/configurations/configFileUpload',
             data: {
                'configId':configId,
                'fileDropLocation':fileDropLocation
             },
             type: "GET",
             success: function(data) {
                 $("#configFileUploadModal").html(data);
             }
         });
    });

    //The function to submit the uploaded file
    $(document).on('click', '#submitFileButton', function (event) {
        var errorFound = 0;

        $('#configFileDiv').removeClass("has-error");
        $('#configFileMsg').removeClass("has-error");
        $('#configFileMsg').html('');
        $('#successMsg').hide();
        $('#errorMsg').hide();

        var expectedExtension = $('#expectedExt').val();

        if($('#configFile').val() !== '') {
            var file = $('#configFile').val();
            var uploadedFileExt = file.substr( (file.lastIndexOf('.') +1) );
        }

        //Make sure a file is selected and is a text file
        if ($('#configFile').val() === '') {
            $('#configFileDiv').addClass("has-error");
            $('#configFileMsg').addClass("has-error");
            $('#configFileMsg').html('The file is a required field.');
            errorFound = 1;
        }
        else if (uploadedFileExt != expectedExtension) {
            $('#configFileDiv').addClass("has-error");
            $('#configFileMsg').addClass("has-error");
            $('#configFileMsg').html('According to the configruation the file must have a .' + $('#expectedExt').val() + ' extension.');
            errorFound = 1;
        }

        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {

            //check and submit form
            var form = $('#configFileForm')[0];
            var formData = new FormData(form);
            $.ajax({
                url: '/administrator/configurations/submitConfigFileForProcessing',
                type: "POST",
                enctype: 'multipart/form-data',
                processData: false,  // Important!
                contentType: false,
                cache: false,
                data: formData,
                success: function(data) {
                   if(data == 1) {
                       $('#configFile').val("");
                       $('#submitFileButton').hide();
                       $('#successMsg').show();
                   }
                   else {
                       $('#configFile').val("");
                       $('#errorMsg').show();
                   }
                }
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
             url: 'createConfigPrintPDF.do',
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

    $('#myTabContent a[href="#source-config"]').tab('show');

    $("a[data-toggle=\"tab\"]").on("shown.bs.tab", function (e) {
        $($.fn.dataTable.tables( true ) ).css('width', '100%');
        $($.fn.dataTable.tables( true ) ).DataTable().columns.adjust().draw();
    });

     try {
         /* Table initialisation */
         var sourceconfigdatatable = $('#sourceconfigdatatable').dataTable({
             "bStateSave": false,
             "sPaginationType": "bootstrap",
             columnDefs: [ { type: 'date', 'targets': [4,5] } ],
             "oLanguage": {
                 "sSearch": "_INPUT_",
                 "sLengthMenu": '<select class="form-control" style="width:150px">' +
                         '<option value="10">10 Records</option>' +
                         '<option value="20">20 Records</option>' +
                         '<option value="30">30 Records</option>' +
                         '<option value="40">40 Records</option>' +
                         '<option value="50">50 Records</option>' +
                         '<option value="-1">All</option>' +
                         '</select>'
             }
         });
         sourceconfigdatatable.fnSort([[0, 'desc']]);
     }
     catch(err) {}

     try {
         /* Table initialisation */
         var targetconfigdatatable = $('#targetconfigdatatable').dataTable({
             "bStateSave": false,
             "sPaginationType": "bootstrap",
              columnDefs: [ { type: 'date', 'targets': [4,5] } ],
             "oLanguage": {
                 "sSearch": "_INPUT_",
                 "sLengthMenu": '<select class="form-control" style="width:150px">' +
                         '<option value="10">10 Records</option>' +
                         '<option value="20">20 Records</option>' +
                         '<option value="30">30 Records</option>' +
                         '<option value="40">40 Records</option>' +
                         '<option value="50">50 Records</option>' +
                         '<option value="-1">All</option>' +
                         '</select>'
             }
         });
         targetconfigdatatable.fnSort([[0, 'desc']]);
     }
     catch(err) {}

     $.ajaxSetup({
         cache: false
     });

     //Fade out the updated/created message after being displayed.
     if ($('.alert').length > 0) {
         $('.alert').delay(2000).fadeOut(1000);
     }

     $("input:text,form").attr("autocomplete", "off");

     $(document).on('click', '.editConfig', function () {
         window.location.href = "details?i=" + $(this).attr('rel');
     });

     $(document).on('click', '.deleteConfig', function() {

         var configId = $(this).attr('rel');

         if(confirm("Are you sure you want to delete this configuration?")) {

             $('body').overlay({
                 glyphicon : 'floppy-disk',
                 message : 'Deleting...'
             });

             $.ajax({
                 url: 'deleteConfiguration.do',
                 data: {
                     'configId': configId
                 },
                 type: 'POST',
                 success: function(data) {
                   window.location.href = "list?msg=deleted";
                 }
             });
         }
     });

     $(document).on('click', '.copyConfig', function() {

         var configId = $(this).attr('rel');

         if(confirm("Are you sure you want to copy this configuration?")) {

             $('body').overlay({
                 glyphicon : 'floppy-disk',
                 message : 'Copying...'
             });

             $.ajax({
                 url: 'copyConfiguration.do',
                 data: {
                     'configId': configId
                 },
                 type: 'POST',
                 success: function(data) {
                   window.location.href = "details?i=" + data;
                 }
             });

         }

     });

     $('#searchConfigBtn').click(function () {
         $('#searchForm').submit();
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


