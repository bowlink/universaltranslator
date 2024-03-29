

require(['./main'], function () {
    
    $("input:text,form").attr("autocomplete", "off");
    populateCrosswalks(1,1);
    populateExistingTranslations();

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(1000);
    }
    
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
    
    $(document).on('change','#delimiter', function() {
        if($("#cwFileLink").length > 0) {
            var cwFileLink = $('#cwFileLink').attr('href');

            cwFileLink = cwFileLink.replace('&delim='+$('#delimiter').data("delim"),'&delim='+$(this).val());
            $('#delimiter').data("delim",$(this).val());
            $('#cwFileLink').attr('href',cwFileLink);
        }
    });
    
    $(document).on('click', '.showMore', function() {
       var macroId = $(this).attr('rel');
       
       if(!$('#macro-'+macroId).is(":visible")) {
           $(this).html("Hide Details");
           $('#macro-'+macroId).show();
       }
       else {
           $(this).html("Show More Details");
           $('#macro-'+macroId).hide();
       }
    });
    
    $(document).on('click','.deleteCrosswalk',function() {
        var dtsId = $(this).attr('rel2');
        var cwId = $(this).attr('rel');
        
        if((dtsId*1) > 0) {
            alert("The crosswalk you are trying to delete is associated to a field within the data translations section of one or more of your configurations. \n\nIn order to modify the crosswalks existing values you can click 'view' and upload a new crosswalk file. \n\nTo delete the crosswalk you must first remove its association with any of your configuration fields.");
            location.reload();
        }
        else {
            if(confirm("Are you sure you want to remove this crosswalk?")) {
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Deleting...'
                });

                $.ajax({
                    url: 'deleteCrosswalk.do',
                    data: {
                        'cwId': cwId
                    },
                    type: 'POST',
                    success: function(data) {
                      location.reload();
                    }
                });
            }
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
    });

    //This function will launch the new crosswalk overlay with a blank form
    $(document).on('click', '.macroDefinitions', function () {
        $.ajax({
            url: 'macroDefinitions',
            type: "GET",
            data: {
                'macroCategory': 1
            },
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });

    $(document).on('click', '.useMacro', function() {
        var macroId = $(this).attr('rel');
        $('#macro').val(macroId);
        $('#macro').trigger( "change" );
    });

    $(document).on('click', '.createCrosswalkDownload', function() {
       $.ajax({
            url: '/administrator/configurations/createCrosswalkDownload',
            data: {
               'configId':$(this).attr('rel')
            },
            type: "GET",
            success: function(data) {
                $("#cwDownloadModal").html(data);
            }
        });
    });

    $(document).on('click', '#generateCWButton', function() {

        var errorFound = 0;
       
        var whichView = $('.showCrosswalks').attr('rel');
       
        if(whichView == 0) {
            whichView = 1;
        }
        else {
            whichView = 0;
        }

       //Makes sure name is entered and entity is selected
       if($('#fileName').val() === "") {
           $('#cwnameDiv').addClass("has-error");
           errorFound = 1;
       }
       
       if(errorFound == 0) {
           
           var fileType = $("input[name='fileType']:checked").val();
           
           $('body').overlay({
                glyphicon : 'floppy-disk',
                message : 'Generating Excel File!'
            });
             $('.overlay').css('display','block');
           
           $.ajax({
                //url: '/administrator/configurations/crosswalksDownload',
                url: '/administrator/configurations/crosswalksExcelFileDownload',
                data: {
                    'configId':$(this).attr('rel'),
                    'fileName': $('#fileName').val(),
                    'fileType': fileType,
                    'inUseOnly':whichView
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/downloadDTCWExcelFile/'+ data;
                        $('#successMsg').show();
                        $('.overlay').css('display','none');
                    }
                    else {
                        $('#errorMsg').show();
                    }
                }
            });
       }
    });

    $(document).on('click', '.createDataTranslationDownload', function() {
       $.ajax({
            url: '/administrator/configurations/createDataTranslationDownload',
            data: {
               'configId':$(this).attr('rel')
            },
            type: "GET",
            success: function(data) {
                $("#dtDownloadModal").html(data);
            }
        });
    });

    $(document).on('click', '#generateDTButton', function() {

        var errorFound = 0;

       //Makes sure name is entered and entity is selected
       if($('#fileName').val() === "") {
           $('#dtnameDiv').addClass("has-error");
           errorFound = 1;
       }

       if(errorFound == 0) {
           $.ajax({
                url: '/administrator/configurations/dataTranslationsDownload',
                data: {
                    'configId':$(this).attr('rel'),
                    'fileName': $('#fileName').val()
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/downloadDTCWFile/'+ data;
                        $('#successMsg').show();
                        //$('#dtDownloadModal').modal('toggle');
                    }
                    else {
                        $('#errorMsg').show();
                    }
                }
            });
       }
    });

    //Function that will check the selected macro and determine if a module
    //should be launched to ask questions.
    $('#macro').change(function () {
        var selMacro = $(this).val();
        var list = $('#macroLookUpList').val();

        if (selMacro > 0) {
            if (list.indexOf(selMacro) !== -1) {
                $.ajax({
                    url: 'getMacroDetails.do',
                    type: "GET",
                    data: {'macroId': selMacro},
                    success: function (data) {
                        $('#macroModal').html(data);
                        $('#macroModal').modal('toggle');
                        $('#macroModal').modal('show');
                    }
                });
            }
        }
    });

    //Function that will take in the macro details
    $(document).on('click', '.submitMacroDetailsButton', function () {
        var noErrors = 1;
        
        var fieldA = $('#fieldAQuestion').val();
        var fieldB = $('#fieldBQuestion').val();
        var con1 = $('#Con1Question').val();
        var con2 = $('#Con2Question').val();

        if(noErrors == 1) {
            //Clear all fields
            $('#fieldA').val("");
            $('#fieldB').val("");
            $('#constant1').val("");
            $('#constant2').val("");
            
            if($('#Con1QuestionSelect').val() !== 'undefined') {
                $('#constant1').attr('rel', $('#Con1Question option:selected').html());
            }
            if($('#Con2QuestionSelect').val() !== 'undefined') {
                $('#constant2').attr('rel', $('#Con2Question option:selected').html());
            }
            if($('#fieldAQuestionSelect').val() !== 'undefined') {
                $('#fieldA').attr('rel', $('#fieldAQuestion option:selected').html());
            }
            if($('#fieldBQuestionSelect').val() !== 'undefined') {
                $('#fieldB').attr('rel', $('#fieldBQuestion option:selected').html());
            }
            
            if (fieldA) {
                $('#fieldA').val(fieldA);
            }
            if (fieldB) {
                $('#fieldB').val(fieldB);
            }
            if (con1) {
                $('#constant1').val(con1);
            }
            if (con2) {
                $('#constant2').val(con2);
            }

            //Close the modal window
            $('#macroModal').modal('toggle');
            $('#macroModal').modal('hide'); 
        }
        
    });

    //This function will get the next/prev page for the crosswalk list
    $(document).on('click', '.nextPage', function () {
        var page = $(this).attr('rel');
        var whichView = $('.showCrosswalks').attr('rel');
        
        if(whichView == 0) {
            whichView = 1;
        }
        else {
            whichView = 0;
        }
        
        populateCrosswalks(page,whichView);
    });
    
    $(document).on('click', '.showCrosswalks', function () {
      var whichView = $(this).attr('rel');
        
      populateCrosswalks(1,whichView);
      
      if(whichView == 0) {
        $(this).attr('rel',1);
        $(this).html('Show Only In Use Crosswalks');
        $('#crosswalkTitle').html("Available");
      }
      else {
        $(this).attr('rel',0);
        $(this).html('Show All Available Crosswalks');
        $('#crosswalkTitle').html("In Use");
      }
    });
    
    //The function that will be called when the "Save" button is clicked
    $('#saveDetails').click(function () {
        if(!$(this).hasClass("disabled")) {
            $('#next').addClass( "disabled" );
            $('#saveDetails').addClass( "disabled" );
            $.ajax({
                url: 'translations',
                type: "POST",
                data: {'categoryId': 1},
                success: function (data) {
                    window.location.href = "translations?msg=updated";
                }
            });
        }
        else {
            alert("Data translations are still loading. You can save the page once they are fully loaded.");
        }
    });
     
    //The function that will be called when the "Next Step" button is clicked
    $('#next').click(function () {
        var configType = $('#configtype').attr('rel');
        var mappings = $('#configtype').attr('rel2');
        
        if(!$(this).hasClass("disabled")) {
            $('#saveDetails').addClass( "disabled" );
            $('#next').addClass( "disabled" );
            $.ajax({
                url: 'translations',
                type: "POST",
                data: {'categoryId': 1},
                success: function (data) {
                    window.location.href = "scheduling?msg=updated";
                }
            });
        }
        else {
            alert("Data translations are still loading. You can proceed to the next step once they are fully loaded.");
        }
    });

    $(document).on('click', '.cwClose', function() {
        location.reload();
    });

    //This function will launch the crosswalk overlay with the selected
    //crosswalk details
    $(document).on('click', '.viewCrosswalk', function () {
        $.ajax({
            url: 'viewCrosswalk' + $(this).attr('rel'),
            type: "GET",
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });

    //This function will launch the new crosswalk overlay with a blank form
    $(document).on('click', '#createNewCrosswalk', function () {
        var orgId = $('#orgId').val();

        $.ajax({
            url: 'newCrosswalk',
            type: "GET",
            data: {'orgId': orgId},
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });
    
    //This function will launch the new crosswalk form to upload multiple crosswalks at once
    $(document).on('click', '#uploadMultiCrosswalks', function () {
        var orgId = $('#orgId').val();
        var cwId = $(this).data('cwid');

        $.ajax({
            url: 'multipleCrosswalkUpload',
            type: "GET",
            data: {
                'orgId': orgId,
                'cwId': cwId
            },
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });

    //The function to submit the new crosswalk
    $(document).on('click', '#submitCrosswalkButton', function (event) {
        $('.uploadError').hide();
        $('.uploadSuccess').hide();
        $('#crosswalkNameDiv').removeClass("has-error");
        $('#crosswalkNameMsg').removeClass("has-error");
        $('#crosswalkNameMsg').html('');
        $('#crosswalkDelimDiv').removeClass("has-error");
        $('#crosswalkDelimMsg').removeClass("has-error");
        $('#crosswalkDelimMsg').html('');
        $('#crosswalkFileDiv').removeClass("has-error");
        $('#crosswalkFileMsg').removeClass("has-error");
        $('#crosswalkFileMsg').html('');

        var errorFound = 0;
        var actionValue = $(this).attr('rel').toLowerCase();

        //Make sure a title is entered
        if ($('#name').val() == '') {
            $('#crosswalkNameDiv').addClass("has-error");
            $('#crosswalkNameMsg').addClass("has-error");
            $('#crosswalkNameMsg').html('The crosswalk name is a required field!');
            errorFound = 1;
        }

        //Need to make sure the crosswalk name doesn't already exist.
        var orgId = $('#orgId').val();

        if(actionValue === "create") {

            $.ajax({
                url: 'checkCrosswalkName.do',
                type: "POST",
                async: false,
                data: {'name': $('#name').val(), 'orgId': orgId},
                success: function (data) {
                    if (data == 1) {
                        $('#crosswalkNameDiv').addClass("has-error");
                        $('#crosswalkNameMsg').addClass("has-error");
                        $('#crosswalkNameMsg').html('The name entered is already associated with another crosswalk in the system!');
                        errorFound = 1;
                    }
                }
            });
        }

        //Make sure a delimiter is selected
        if ($('#delimiter').val() == '') {
            $('#crosswalkDelimDiv').addClass("has-error");
            $('#crosswalkDelimMsg').addClass("has-error");
            $('#crosswalkDelimMsg').html('The file delimiter is a required field!');
            errorFound = 1;
        }

        //Make sure a file is selected and is a text file
        if ($('#crosswalkFile').val() == '' || $('#crosswalkFile').val().indexOf('.txt') == -1) {
            $('#crosswalkFileDiv').addClass("has-error");
            $('#crosswalkFileMsg').addClass("has-error");
            $('#crosswalkFileMsg').html('The crosswalk file must be a text file!');
            errorFound = 1;
        }

        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {

            //check and submit form
            var form = $('#crosswalkdetailsform')[0];
            var formData = new FormData(form);
            $.ajax({
                url: '/administrator/configurations/'+actionValue+'Crosswalk',
                type: "POST",
                enctype: 'multipart/form-data',
                processData: false,  // Important!
                contentType: false,
                cache: false,
                data: formData,
                success: function(data) {
                   if(data > 0) {
                       $.ajax({
                            url: 'viewCrosswalk?i=' + data,
                            type: "GET",
                            success: function(data) {
                                data = data.replace('close', 'close cwClose');
                                data = data.replace('uploadSuccess" role="alert" style="display:none;"', 'uploadSuccess" role="alert" style="display:block;"');
                                $("#crosswalkModal").html(data);
                            }
                        });
                   }
                   else {
                       $('.uploadError').show();
                   }
                }
            });
        }
    });
    
    $(document).on('click', '#clearTranslationButton', function() {
        $('#field').val("");
        $('#crosswalk').val("");
        $('#macro').val("");
        $('#fieldA').val("");
        $('#fieldB').val("");
        $('#constant1').val("");
        $('#constant2').val("");
        $('#fieldDiv').removeClass("has-error");
        $('#fieldMsg').removeClass("has-error");
        $('#fieldMsg').html("");
        $('#crosswalkDiv').removeClass("has-error");
        $('#crosswalkMsg').removeClass("has-error");
        $('#crosswalkMsg').html("");
        $('#macroDiv').removeClass("has-error");
        $('#macroMsg').removeClass("has-error");
        $('#macroMsg').html("");
    });

    //This function will handle populating the data translation table
    //The trigger will be when a crosswalk is selected along with a
    //field
    $(document).on('click', '#submitTranslationButton', function () {
        var selectedField = $('#field').val();
        var selectedFieldText = $('#field').find(":selected").text();
        var selectedCW = $('#crosswalk').val();
        var selectedCWText = $('#crosswalk').find(":selected").text();
        var selectedMacro = $('#macro').val();
        var selectedMacroText = $('#macro').find(":selected").text();
        
        if (typeof $('#constant1').attr('rel') !== 'undefined') {
            selectedCWText = $('#constant1').attr('rel');
        }
        if (typeof $('#constant2').attr('rel') !== 'undefined') {
            selectedCWText = $('#constant2').attr('rel');
        }
        if (typeof $('#constant1').attr('rel') !== 'undefined') {
            selectedCWText = $('#constant1').attr('rel');
        }
        if (typeof $('#fieldA').attr('rel') !== 'undefined') {
            selectedCWText = $('#fieldA').attr('rel');
        }
        if (typeof $('#fieldB').attr('rel') !== 'undefined') {
            selectedCWText = $('#fieldB').attr('rel');
        }
        
        //Remove all error classes and error messages
        $('div').removeClass("has-error");
        $('span').html("");

        var errorFound = 0;

        if (selectedField == "") {
            $('#fieldDiv').addClass("has-error");
            $('#fieldMsg').addClass("has-error");
            $('#fieldMsg').html('A field must be selected!');
            errorFound = 1;
        }
        if (selectedCW == "" && selectedMacro == "") {
            $('#crosswalkDiv').addClass("has-error");
            $('#crosswalkMsg').addClass("has-error");
            $('#crosswalkMsg').html('Either a macro or crosswalk must be selected!');
            $('#macroDiv').addClass("has-error");
            $('#macroMsg').addClass("has-error");
            $('#macroMsg').html('Either a macro or crosswalk must be selected!');
            errorFound = 1;
        }
        
        if (errorFound == 0) {
            $.ajax({
                url: "setTranslations",
                type: "GET",
                data: {
                    'f': selectedField, 
                    'fText': selectedFieldText, 
                    'cw': selectedCW, 
                    'CWText': selectedCWText, 
                    'macroId': selectedMacro, 
                    'macroName': selectedMacroText, 
                    'fieldA': $('#fieldA').val(), 
                    'fieldB': $('#fieldB').val(), 
                    'constant1': $('#constant1').val(), 
                    'constant2': $('#constant2').val(),
                    'passClear': $('.passclear:checked').val()
                },
                success: function (data) {
                    location.reload();
                }
            });
        }
    });

    //Function that will handle changing a process order and
    //making sure another field does not have the same process 
    //order selected. It will swap display position
    //values with the requested position.
    $(document).on('change', '.processOrder', function () {
        
        var translationId = $(this).data('id');
        var newDspPos = $(this).val();
        
        if(confirm("Are you sure you want to move this configuration translation process position?")) {

            $.ajax({
                url: 'updateTranslationProcessOrder.do',
                type: "POST",
                data: {
                    'newProcessOrder': newDspPos,
                    'translationId': translationId
                },
                success: function (data) {
                     location.reload();
                }
            });
        }
    });

    //Function that will handle removing a line item from the
    //existing data translations. Function will also update the
    //processing orders for each displayed.
    $(document).on('click', '.removeTranslation', function () {
        var translationId = $(this).data('id');
        
        if(confirm("Are you sure you want to remove this configuration translation?")) {
            //Need to remove the translation
            $.ajax({
                url: 'removeTranslations.do',
                type: "POST",
                data: {
                    'translationId': translationId
                },
                success: function (data) {
                     location.reload();
                }
            });
        }
    });
    
    
    $(document).on('click', '.reloadMultiForm', function (event) {
        $('#crosswalkDelimDiv').removeClass("has-error");
        $('#crosswalkDelimMsg').removeClass("has-error");
        $('#crosswalkDelimMsg').html('');
        $('#crosswalkFileDiv').removeClass("has-error");
        $('#crosswalkFileMsg').removeClass("has-error");
        $('#crosswalkFileMsg').html('');
        
        $('.cwUploadForm').show();
        $('.uploadResults').hide();
        $('#crosswalkFile').val('');
        $('.uploadResults').html('');
    });
    
    //The function to upload all the selected crosswalks
    $(document).on('click', '#submitMultiCrosswalkButton', function (event) {
        $('.uploadError').hide();
        $('.uploadSuccess').hide();
        $('#crosswalkDelimDiv').removeClass("has-error");
        $('#crosswalkDelimMsg').removeClass("has-error");
        $('#crosswalkDelimMsg').html('');
        $('#crosswalkFileDiv').removeClass("has-error");
        $('#crosswalkFileMsg').removeClass("has-error");
        $('#crosswalkFileMsg').html('');
        
        var errorFound = 0;

        //Need to make sure the crosswalk name doesn't already exist.
        var orgId = $('#orgId').val();

        //Make sure a delimiter is selected
        if ($('#delimiter').val() == '') {
            $('#crosswalkDelimDiv').addClass("has-error");
            $('#crosswalkDelimMsg').addClass("has-error");
            $('#crosswalkDelimMsg').html('The file delimiter is a required field!');
            errorFound = 1;
        }

        //Make sure a file is selected and is a text file
        if ($('#crosswalkFile').val() == '') {
            $('#crosswalkFileDiv').addClass("has-error");
            $('#crosswalkFileMsg').addClass("has-error");
            $('#crosswalkFileMsg').html('At least one crosswalk file must be selected!');
            errorFound = 1;
        }
        else {
            var selection = document.getElementById('crosswalkFile');
            for (var i=0; i<selection.files.length; i++) {
                var ext = selection.files[i].name.substr(-3);
                if(ext!== "txt" )  {
                    $('#crosswalkFileDiv').addClass("has-error");
                    $('#crosswalkFileMsg').addClass("has-error");
                    $('#crosswalkFileMsg').html('All selected crosswalk files must be a txt file!');
                    errorFound = 1;
                    return false;
                }
            } 
        }
        
        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {

            //check and submit form
            var form = $('#multiCWForm')[0];
            var formData = new FormData(form);
            
            $.ajax({
                url: '/administrator/configurations/submitMultiCrosswalks',
                type: "POST",
                enctype: 'multipart/form-data',
                processData: false,  // Important!
                contentType: false,
                cache: false,
                data: formData,
                success: function(data) {
                   if(data != '1') {
                       $('.cwUploadForm').hide();
                       $('.uploadResults').show();
                       $('.uploadResults').html(data);
                   }
                   else {
                       location.reload();
                   }
                }
            });
        }
    });
});


function populateExistingTranslations() {
    
    //Disable the save buttons (will be reactivated when the existing DTS are loaded)
    $('#saveDetails').addClass( "disabled" );
    $('#next').addClass( "disabled" );
   
    $.ajax({
        url: 'getTranslations.do',
        type: "GET",
        success: function (data) {
            $("#existingTranslations").html(data);
	    $('.dtDownloadLink').show();
            $('#saveDetails').removeClass( "disabled" );
            $('#next').removeClass( "disabled" );
        }
    });
}

function populateCrosswalks(page,inuseonly) {
    var orgId = $('#orgId').val();
    var configId = $('#configId').val();
    
    $.ajax({
        url: 'getCrosswalks.do',
        type: "GET",
        data: {
            'page': page, 
            'orgId': orgId, 
            'maxCrosswalks': 8, 
            'configId': configId,
            'inUseOnly':inuseonly
        },
        success: function (data) {
            
            $("#crosswalksTable").html(data);
            
            $("#crosswalksTable").find('#cwDataTable').DataTable({
                bServerSide: false,
                bProcessing: false, 
                deferRender: true,
                aaSorting: [[3,'desc']],
                 "columns": [
                    { "width": "10%" },
                    { "width": "30%" },
                    { "width": "20%", "type": "date" },
                    { "width": "20%", "type": "date" },
                    { "width": "10%" }
                 ],
                sPaginationType: "bootstrap", 
                searching: true,
                bLengthChange: false,
                oLanguage: {
                   sEmptyTable: "No Crosswalks are in use for this configuration.", 
                   sSearch: "Filter: ",
                   sLengthMenu: '<select class="form-control" style="width:150px">' +
                        '<option value="10">10 Records</option>' +
                        '<option value="20">20 Records</option>' +
                        '<option value="30">30 Records</option>' +
                        '<option value="40">40 Records</option>' +
                        '<option value="50">50 Records</option>' +
                        '<option value="-1">All</option>' +
                        '</select>'
                }
            });
            
            $('.dataTables_filter').addClass('pull-left');
        }
    });
}

function removeVariableFromURL(url_string, variable_name) {
    var URL = String(url_string);
    var regex = new RegExp("\\?" + variable_name + "=[^&]*&?", "gi");
    URL = URL.replace(regex, '?');
    regex = new RegExp("\\&" + variable_name + "=[^&]*&?", "gi");
    URL = URL.replace(regex, '&');
    URL = URL.replace(/(\?|&)$/, '');
    regex = null;
    return URL;
}


function populateFieldA() {
    if ($("#field option:selected").val() != '') {
        var idForOption = "o" + $("#field option:selected").val();
        var fieldAVal = $("#" + idForOption).attr('rel');
        $('#fieldAQuestion').val(fieldAVal);
    }
}
