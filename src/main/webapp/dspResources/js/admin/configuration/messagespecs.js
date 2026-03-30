
jQuery(function ($) {
    
    var formUpdated = false;
    
    $(document).ready(function () {
        if ($('.configWasUpdated').length > 0) {
           printAfterSnapshot($('#configIdForSnapshot').val(),$('.configWasUpdated').data('module'));
        }
        
        //Log a change happened
        $('#messageSpecs :input').on('change input', function () {
            formUpdated = true;
        });
        
        var hasHeaderRow = $('.containsHeaderRow').val();
        if(hasHeaderRow == 1) {
            $('#totalHeaderRowsDiv').show();
        }
        else {
            $('#totalHeaderRowsDiv').hide();
        }
        
        $(document).on('change', '.containsHeaderRow', function() {
            if($(this).val() == 1) {
                $('#totalHeaderRows').val(1);
                $('#totalHeaderRowsDiv').show();
            }
            else {
                $('#totalHeaderRows').val(0);
                $('#totalHeaderRowsDiv').hide();
            }
        });

        $(document).on('click','.downloadNewTemplate',function() {

            var configId = $(this).attr('rel');

            $('body').overlay({
               glyphicon : 'floppy-disk',
               message : 'Creating Template...'
             });

            $.ajax({
                url: 'createNewFieldSettingsTemplate.do',
                data: {
                    'configId': configId
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/printNewFieldSettingsTemplate/'+ data;
                        $('.overlay').css('display','none');
                    }
                    else {
                        $('.overlay').css('display','none');
                        alert("An error occurred creating your template file. A Health-e-Link system administrator has been notified.");
                    }
                }
            });
        });

        //This function will save the messgae type field mappings
        $('#saveDetails').click(function () {
            $('#action').val('save');

            //Need to make sure all required fields are marked if empty.
            var hasErrors = 0;
            hasErrors = checkFormFields();

            if (hasErrors == 0) {
                if(formUpdated) {
                    $('body').overlay({
                        glyphicon : 'floppy-disk',
                        message : 'Saving Changes'
                    });
                    $('.overlay').css('display','block');
                    printBeforeSnapshot($('#configIdForSnapshot').val(),'messageSpecs','Message Specs',null);
                }
                else {
                    $("#messageSpecs").submit();
                }
            }
        });

        $('#next').click(function (event) {
            $('#action').val('next');

            var hasErrors = 0;
            hasErrors = checkFormFields();

            if (hasErrors == 0) {
                if(formUpdated) {
                    $('body').overlay({
                        glyphicon : 'floppy-disk',
                        message : 'Saving Changes'
                    });
                    $('.overlay').css('display','block');
                    printBeforeSnapshot($('#configIdForSnapshot').val(),'messageSpecs','Message Specs',null);
                }
                else {
                    $("#messageSpecs").submit();
                }
            }
        });
    });
});



function checkFormFields() {
    var hasErrors = 0;

    //Remove all has-error class
    $('div.form-group').removeClass("has-error");
    $('span.control-label').removeClass("has-error");
    $('span.control-label').html("");
    
    if ($('#parsingScriptFile').length > 1) {

        var filename = $('#parsingScriptFile').val();
        var extension = filename.replace(/^.*\./, '');

        if (extension == filename) {
            extension = '';
        } else {
            // if there is an extension, we convert to lower case
            // (N.B. this conversion will not effect the value of the extension
            // on the file upload.)
            extension = extension.toLowerCase();
        }

        if (extension != "jar") {
            $('#parsingTemplateDiv').addClass("has-error");
            $('#parsingTemplateMsg').addClass("has-error");
            $('#parsingTemplateMsg').html('The Parsing Script must be a jar file.');
            hasErrors = 1;
        }

    }
    
    //Check the start row value if contains header row
    var containsHeaderRow = $("input[name='containsHeaderRow']:checked").val();
    
    if(containsHeaderRow == 1) {
        var totalHeaderRows = $('#totalHeaderRows').val();
        
        if(totalHeaderRows < 1) {
            $('#totalHeaderRowsDiv').addClass("has-error");
            $('#totalHeaderRowsMsg').addClass("has-error");
            $('#totalHeaderRowsMsg').html('The number of header rows must be greater than 0. If there are no header rows select NO for "Will the submitted file have a header row?"');
            hasErrors = 1;
        }
        else if(!$.isNumeric(totalHeaderRows)) {
            $('#totalHeaderRowsDiv').addClass("has-error");
            $('#totalHeaderRowsMsg').addClass("has-error");
            $('#totalHeaderRowsMsg').html('The number of header rows must be a numeric value greater than 0.');
            hasErrors = 1;
        }
    }
    else {
        $('#startRow').val(0);
    }
    
     /*$(document).on('change', '.containsHeaderRow', function() {
        if($(this).val() == 1) {
            $('#startRow').val(1);
             $('.startRowDiv').show();
        }
        else {
            $('#startRow').val(0);
            $('.startRowDiv').hide();
        }
    });*/

    //Make sure at least one reportable field is selected
    /*var rptField1 = $('#rptField1').val();
    var rptField2 = $('#rptField2').val();
    var rptField3 = $('#rptField3').val();
    var rptField4 = $('#rptField4').val();

    if (rptField1 == 0 && rptField2 == 0 && rptField3 == 0 && rptField4 == 0) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('At least one reportable field must be selected.<br />');
        hasErrors = 1;
    }

    // Check to make sure there are different selected fields 
    if (hasErrors == 0 && rptField1 > 0 && (rptField1 == rptField2 || rptField1 == rptField3 || rptField1 == rptField4)) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('All reportable fields must be different.<br />');
        hasErrors = 1;
    }
    if (hasErrors == 0 && rptField2 > 0 && (rptField2 == rptField3 || rptField2 == rptField4)) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('All reportable fields must be different.<br />');
        hasErrors = 1;
    }
    if (hasErrors == 0 && rptField3 > 0 && (rptField3 == rptField4)) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('All reportable fields must be different.<br />');
        hasErrors = 1;
    }*/

    return hasErrors;
}


