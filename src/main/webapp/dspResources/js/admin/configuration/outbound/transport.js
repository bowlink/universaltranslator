

require(['./main'], function () {
    
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

    $("input:text,form").attr("autocomplete", "off");

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(1000);
    }

    //Selected transport method
    var transportMethod = $('#transportMethod').val();
    var helRegistryId = $('#helRegistryId').val();
    var helSchemaName = $('#helSchemaName').val();
    var messageTypeId = $('#messageTypeId').val();
    var fileType = $('#fileType').val();

    showCorrectFieldsByTransportMethod(transportMethod);
    showCorrectFileDetails(fileType,0);

    //if the selected transport method is From a Health-e-Link Registry or going to a Health-e-Link registry
    //show the conifguration box
    if(transportMethod == 13 && messageTypeId == 1 && helRegistryId > 0 && helSchemaName !== "") {
        $('#helRegistryConfigDiv').show();
        $('#ergFileDownloadDiv').show();
        populateHELRegistryConfigs(helRegistryId,helSchemaName);
    }

    $('#transportMethod').change(function () {
        var methodId = $(this).val();
        var currMethod = $(this).attr('rel');

        if(methodId == 13 && messageTypeId == 1 && helRegistryId > 0 && helSchemaName !== "") {
            $('#ergFileDownloadDiv').show();
            $('#helRegistryConfigDiv').show();
            populateHELRegistryConfigs(helRegistryId,helSchemaName);

            //Set the file drop location
            //$('#directory2').val('/HELProductSuite/registries/'+$('#helRegistryFolderName').val()+'/loadFiles/');
            $('#directory2').val($('#helRegistryFolderName').val()+'/loadFiles/');

            //If method == 10 (Coming from a HEL Registry online form preset the values
            $('#fileType').val(2);
            $('#fileExt').val('txt');
        }
        else {
            $('#ergFileDownloadDiv').hide();
            $('#helRegistryConfigDiv').hide();
            $('#helRegistryConfigId').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');
        }
        
        showCorrectFieldsByTransportMethod(methodId);
    });


    $(document).on('change','#ergFileDownload',function() {
        var fileDropDir = $('#directory2').val();
        if($(this).val() == 1) {
            $('#directory2').val(fileDropDir.replace('loadFiles','importFiles'));
        }
        else {
            $('#directory2').val(fileDropDir.replace('importFiles','loadFiles'));
        }
    });

    $(document).on('change','#dmFindConfig',function() {
        if($(this).val() == 1) {
            $('.dmConfigKeywordDiv').show();
        }
        else {
            $('#dmConfigKeyword').val("");
            $('.dmConfigKeywordDiv').hide();
        }
    });

    $('#useSource').click(function () {
        if ($('#useSource').is(":checked")) {
            $('#targetFileName').val("USE SOURCE FILE");
        } else {
            $('#targetFileName').val("");
        }
    });

    //This function will save the messgae type field mappings
    $('#saveDetails').click(function () {
        $('#action').val('save');

        //Need to make sure all required fields are marked if empty.
        var hasErrors = 0;
        hasErrors = checkFormFields();

        if (hasErrors == 0) {
            $('#transportDetails').submit();
        }
    });

    $('#next').click(function (event) {
        $('#action').val('next');

        var hasErrors = 0;
        hasErrors = checkFormFields();

        if (hasErrors == 0) {
            $('#transportDetails').submit();
        }
    });

    //Set the default file extension when the file type is selected
    $('#fileType').change(function () {
        var fileType = $(this).val();
        showCorrectFileDetails(fileType,1);
    });

    $('.zipped').change(function () {
       if($(this).val() == 1) {
           $('#zipTypeTopDiv').show();
       }
       else {
           $('#zipTypeTopDiv').hide();
       }
    });
});

function showCorrectFieldsByTransportMethod(transportMethod) {
    $('#fileDetailsDiv').show();
    $('#ftpDetailsDiv').hide();
    $('#restDetailsDiv').hide();
    $('#fileDropDetailsDiv').hide();
   
    if(transportMethod == 13) {
        $('#fileDropDetailsDiv').show();
    }
    else if(transportMethod == 9) {
        $('#restDetailsDiv').show();
    }
    else if(transportMethod == 12) {
        $('#directMessageDetailsDiv').show();
        $('#ccdDetailsDiv').show();
    }
    else if(transportMethod == 3) {
        $('#ftpDetailsDiv').show();
    }
}

function showCorrectFileDetails(fileType,fileTypeChanged) {
    $('#ccdSampleDiv').hide();
    $('#hl7PDFSampleDiv').hide();
    $('#jsonWrapperElementDiv').hide();
    $('#fileDelimiterDiv').hide();
    $('#lineTerminatortDiv').hide();
    $('#encodingDiv').hide();
    $('#addTargetFileHeaderRowDiv').hide();

    $('#fileDelimiterDiv').show();
    $('#lineTerminatortDiv').show();

    if (fileType == 2) {
        $('#fileExt').val('txt');
        if(fileTypeChanged === 1) {
            $('#fileDelimiter').val('');
            $('#lineTerminator').val('\\n');
        }
        $('#fileDelimiterDiv').show();
        $('#lineTerminatortDiv').show();
        $('#fileExtDiv').hide();
        $('#encodingDiv').show();
		$('#addTargetFileHeaderRowDiv').show();
    } 
    else if (fileType == 3) {
        $('#fileExt').val('csv');
        if(fileTypeChanged === 1) {
            $('#fileDelimiter').val(1);
            $('#lineTerminator').val('\\n');
        }
        $('#fileDelimiterDiv').show();
        $('#lineTerminatortDiv').show();
        $('#fileExtDiv').hide();
        $('#encodingDiv').show();
        $('#addTargetFileHeaderRowDiv').show();
    } 
    else if (fileType == 4) {
        $('#fileExt').val('hr');
        $('#fileDelimiter').val(2);
        $('#lineTerminator').val('\\n');
        $('#fileDelimiterDiv').hide();
        $('#lineTerminatortDiv').hide();
        $('#fileExtDiv').hide();
        $('#encodingDiv').show();
        $("#addTargetFileHeaderRow2").prop("checked", true);
    } 
    else if (fileType == 8) {
        $('#fileExt').val('xls');
        $('#fileDelimiter').val(12);
        $('#lineTerminator').val('\\n');
        $('#fileDelimiterDiv').hide();
        $('#lineTerminatortDiv').hide();
        $('#fileExtDiv').hide();
        $('#encodingDiv').show();
        $('#addTargetFileHeaderRowDiv').show();
    } 
    else if (fileType == 9) {
        $('#fileExt').val('xml');
        $('#fileDelimiter').val(2);
        $('#lineTerminator').val('\\n');
        $('#fileDelimiterDiv').hide();
        $('#lineTerminatortDiv').hide();
        $('#fileExtDiv').hide();
        $('#encodingDiv').show();
        $('#addTargetFileHeaderRow').val(0);
        $("#addTargetFileHeaderRow2").prop("checked", true);
    }
    else if (fileType == 11) {
        $('#fileExt').val('xlsx');
        $('#fileDelimiter').val(12);
        $('#lineTerminator').val('\\n');
        $('#fileDelimiterDiv').hide();
        $('#lineTerminatortDiv').hide();
        $('#fileExtDiv').hide();
        $('#encodingDiv').show();
        $('#addTargetFileHeaderRowDiv').show();
    }
    else if (fileType == 12) {
        $('#jsonWrapperElementDiv').show();
        $('#fileExt').val('json');
        $('#fileDelimiterDiv').hide();
        $('#lineTerminatortDiv').hide();
        $('#encodingDiv').show();
        $('#addTargetFileHeaderRow').val(0);
        $("#addTargetFileHeaderRow2").prop("checked", true);
    }
}


function checkFormFields() {
    var hasErrors = 0;

    //Remove all has-error class
    $('div.form-group').removeClass("has-error");
    $('span.control-label').removeClass("has-error");
    $('span.control-label').html("");
    $('.alert-danger').hide();
    
    var type = $('#configType').attr('rel');
    
    var selMethodId = $('#transportMethod').val();
    var fileType = $('#fileType').val();
    
    //Make sure a transport method is chosen
    if ($('#transportMethod').val() === "") {
        $('#transportMethodDiv').addClass("has-error");
        $('#transportMethodMsg').addClass("has-error");
        $('#transportMethodMsg').html('The transport method is a required field.');
        hasErrors = 1;
    }
    
    //Make sure the file size is numeric and greate than 0
    if ($('#maxFileSize').val() <= 0 || !$.isNumeric($('#maxFileSize').val())) {
	$('#maxFileSizeDiv').addClass("has-error");
	$('#maxFileSizeMsg').addClass("has-error");
	$('#maxFileSizeMsg').html('The max file size is a required field and must be a numeric value.');
	hasErrors = 1;
    }

    //Make sure the file type is selected
    if ($('input[type="radio"][name="zipped"]:checked').val() === "1") {
	if($('#zipType').val() === "") {
	    $('#zipTypeDiv').addClass("has-error");
	    $('#zipTypeMsg').addClass("has-error");
	    $('#zipTypeMsg').html('The file will be zipped, the zip type is a required field.');
	    hasErrors = 1;
	}
    }

    //Make sure the file type is selected
    if ($('#fileType').val() === "") {
	$('#fileTypeDiv').addClass("has-error");
	$('#fileTypeMsg').addClass("has-error");
	$('#fileTypeMsg').html('The file type is a required field.');
	hasErrors = 1;
    }

    //Make sure the file ext is entered
    if ($('#fileExt').val() === "") {
	$('#fileExtDiv').addClass("has-error");
	$('#fileExtMsg').addClass("has-error");
	$('#fileExtMsg').html('The file extension is a required field.');
	hasErrors = 1;
    }

    //make sure encoding is selected
    if ($('#encodingId').val() === "") {
	$('#encodingDiv').addClass("has-error");
	$('#encodingMsg').addClass("has-error");
	$('#encodingMsg').html('Encoding is a required field.');
	hasErrors = 1;
    } 
    else {
	//Remove any '.' in the extension
	$('#fileExt').val($('#fileExt').val().replace('.', ''));
    }

    //Make sure the file delimiter is selected
    if ($('#fileDelimiter').val() === "") {
	$('#fileDelimiterDiv').addClass("has-error");
	$('#fileDelimiterMsg').addClass("has-error");
	$('#fileDelimiterMsg').html('The file delimiter is a required field.');
	hasErrors = 1;
    }


    if (selMethodId == 3) {
	var IPReg = /^(\d\d?)|(1\d\d)|(0\d\d)|(2[0-4]\d)|(2[0-5])\.(\d\d?)|(1\d\d)|(0\d\d)|(2[0-4]\d)|(2[0-5])\.(\d\d?)|(1\d\d)|(0\d\d)|(2[0-4]\d)|(2[0-5])$/;

	//Check FTP Get Fields
	var getFieldsEntered = 0;

	if ($('#ip2').val() !== "" || $('#username2').val() !== "" || $('#password2').val() !== "" || $('#directory2').val() !== "") {
	    getFieldsEntered = 1;
	}
	if (getFieldsEntered == 1) {
	    if ($('#ip2').val() === "") {
		$('#ip2Div').addClass("has-error");
		$('#ip2Msg').addClass("has-error");
		$('#ip2Msg').html('The host address is a required field.');
		hasErrors = 1;
	    } 
            /*else if (!IPReg.test($('#ip1').val())) {
		$('#ip1Div').addClass("has-error");
		$('#ip1Msg').addClass("has-error");
		$('#ip1Msg').html('The IP address entered is invalid.');
		hasErrors = 1;
	    }*/
	    if ($('#username2').val() === "") {
		$('#username2Div').addClass("has-error");
		$('#username2Msg').addClass("has-error");
		$('#username2Msg').html('The username is a required field.');
		hasErrors = 1;
	    }
            
	    if ($('#protocol2').val() === "SFTP") {
		/*if ($('#password2').val() === "" && $('#file2').val() === "" && $('#certification2').val() === "") {
		    $('#password2Div').addClass("has-error");
		    $('#password2Msg').addClass("has-error");
		    $('#password2Msg').html('The password or certification is a required field.');
		    hasErrors = 1;
		}*/
                if ($('#password2').val() === "" && $('#currPassword2').val() === "") {
                    $('#password2Div').addClass("has-error");
                    $('#password2Msg').addClass("has-error");
                    $('#password2Msg').html('The password is a required field.');
                    hasErrors = 1;
                }
	    } 
            else {
		if ($('#password2').val() === "" && $('#currPassword2').val() === "") {
		    $('#password2Div').addClass("has-error");
		    $('#password2Msg').addClass("has-error");
		    $('#password2Msg').html('The password is a required field.');
		    hasErrors = 1;
		}
	    }

	    if ($('#ftpdirectory2').val() === "") {
		$('#directory2Div').addClass("has-error");
		$('#directory2Msg').addClass("has-error");
		$('#directory2Msg').html('The directory is a required field.');
		hasErrors = 1;
	    }
            
            if ($('#port2').val() === "") {
		$('#port2Div').addClass("has-error");
		$('#port2Msg').addClass("has-error");
		$('#port2Msg').html('The port is a required field.');
		hasErrors = 1;
	    }
	}

	if (getFieldsEntered == 0) {
	    $('#FTPDanger').show();
	    hasErrors = 1;
	}
    }


    if (fileType == 4 && $('#configType').attr('rel') == 2) {
	if ($('#hl7PDFTemplatefile').val() != "") {

	    var filename = $('#hl7PDFTemplatefile').val();
	    var extension = filename.replace(/^.*\./, '');

	    if (extension == filename) {
		extension = '';
	    } else {
		// if there is an extension, we convert to lower case
		// (N.B. this conversion will not effect the value of the extension
		// on the file upload.)
		extension = extension.toLowerCase();
	    }

	    if (extension != "txt") {
		$('#HL7PDFTemplateDiv').addClass("has-error");
		$('#HL7PDFTemplateMsg').addClass("has-error");
		$('#HL7PDFTemplateMsg').html('The HL7 PDF Template file must be an txt file.');
		hasErrors = 1;
	    }
	} else {
	    $('#HL7PDFTemplateDiv').addClass("has-error");
	    $('#HL7PDFTemplateMsg').addClass("has-error");
	    $('#HL7PDFTemplateMsg').html('The HL7 PDF Template file must selected.');
	    hasErrors = 1;
	}
    }

    if (selMethodId == "9") {
	var apiURL = $('#restAPIURL').val();
	var apiUsername = $('#restAPIUsername').val();
	var apiPassword = $('#restAPIPassword').val();
	var apiType = $('#restAPIType').val();
	var apiFunction = $('#restAPIFunctionId').val();

	if(apiURL === "") {
	    $('#apiURLDiv').addClass("has-error");
	    $('#apiURLMsg').addClass("has-error");
	    $('#apiURLMsg').html('The Rest API URL is a required field.');
	    hasErrors = 1;
	}

	if(apiUsername === "") {
	    $('#apiUsernameDiv').addClass("has-error");
	    $('#apiUsernameMsg').addClass("has-error");
	    $('#apiUsernameMsg').html('The Rest API Username is a required field.');
	    hasErrors = 1;
	}
	else if(apiUsername.length < 5) {
	    $('#apiUsernameDiv').addClass("has-error");
	    $('#apiUsernameMsg').addClass("has-error");
	    $('#apiUsernameMsg').html('The Rest API Username must be at least 5 characters.');
	    hasErrors = 1;
	}

	if(apiPassword === "") {
	    $('#apiPasswordDiv').addClass("has-error");
	    $('#apiPasswordMsg').addClass("has-error");
	    $('#apiPasswordMsg').html('The Rest API Password is a required field.');
	    hasErrors = 1;
	}
	else if(apiPassword.length < 5) {
	    $('#apiPasswordDiv').addClass("has-error");
	    $('#apiPasswordMsg').addClass("has-error");
	    $('#apiPasswordMsg').html('The Rest API Password must be at least 5 characters.');
	    hasErrors = 1;
	}

	if(apiType === "") {
	    $('#restAPITypeDiv').addClass("has-error");
	    $('#restAPITypeMsg').addClass("has-error");
	    $('#restAPITypeMsg').html('The Rest API Type is a required field.');
	    hasErrors = 1;
	}

	if(apiFunction === "") {
	    $('#restAPIFunctionIdDiv').addClass("has-error");
	    $('#restAPIFunctionIdMsg').addClass("has-error");
	    $('#restAPIFunctionIdMsg').html('The Rest API Function is a required field.');
	    hasErrors = 1;
	}

	//Check for a duplicate url for rest api
	if(hasErrors != 1) {

	}

    }
    
    if(selMethodId == 12) {

	if(type == 1) {
	    if($('#directDomain').val() === "") {
		$('.directDomainDiv').addClass("has-error");
		$('#directDomainMsg').addClass("has-error");
		$('#directDomainMsg').html('This is a required field.');
		hasErrors = 1;
	    }

	    if($('#dmFindConfig').val() === "") {
		$('.dmFindConfigDiv').addClass("has-error");
		$('#dmFindConfigMsg').addClass("has-error");
		$('#dmFindConfigMsg').html('This is a required field.');
		hasErrors = 1;
	    }

	    if($('#dmFindConfig').val() == 1 && $('#dmConfigKeyword').val() === "") {
		$('.dmConfigKeywordDiv').addClass("has-error");
		$('#dmConfigKeywordMsg').addClass("has-error");
		$('#dmConfigKeywordMsg').html('This is a required field.');
		hasErrors = 1;
	    }
	}
	else {
	    //Make sure the file type is XML (CCD)
	    if ($('#fileType').val() === "" || $('#fileType').val() != 9) {
		$('#fileTypeDiv').addClass("has-error");
		$('#fileTypeMsg').addClass("has-error");
		$('#fileTypeMsg').html('The file type must be xml (CCD) when sending direct to a hisp.');
		hasErrors = 1;
	    }

	    //Make sure the file extension is XML
	    if ($('#fileExt').val() === "" || $('#fileExt').val().toLowerCase() !== "xml") {
		$('#fileTypeDiv').addClass("has-error");
		$('#fileTypeMsg').addClass("has-error");
		$('#fileTypeMsg').html('The file extension must be xml when sending direct to a hisp.');
		hasErrors = 1;
	    }


	    //Make sure the CCD Output template is uploaded
	    if($('#ccdSampleTemplate').length > 0) {
		if($('#ccdSampleTemplate').val() === "" && $("ccdTemplatefile").val() === "") {
		    $('#ccdDetailsDiv').addClass("has-error");
		    $('#ccdTemplateMsg').addClass("has-error");
		    $('#ccdTemplateMsg').html('The CCDA template file must be uploaded when sending direct to a hisp.');
		    hasErrors = 1;
		}
	    }
	    else {
		if($("#ccdTemplatefile").val() === "") {
		    $('#ccdDetailsDiv').addClass("has-error");
		    $('#ccdTemplateMsg').addClass("has-error");
		    $('#ccdTemplateMsg').html('The CCDA template file must be uploaded when sending direct to a hisp.');
		    hasErrors = 1;
		}
	    }
	}

    }

    return hasErrors;
}

function populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName) {
    
    $.ajax({
	url: '/administrator/configurations/getHELRegistryConfigurations?tenantId='+helRegistrySchemaName,
	type: "GET",
	data: {},
	dataType: 'json',
	success: function (data) {
	    $('#helRegistryConfigDiv').show();
	    $('#helRegistryConfigId').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');

	    var selHELRegistryConfigId = $('#helRegistryConfigId').attr('rel');

	    var helRegistryConfigSelect = $('#helRegistryConfigId');

	    $.each(data, function(index) {
	       if(data[index].id == selHELRegistryConfigId) {
		   helRegistryConfigSelect.append($('<option selected></option>').val(data[index].id).html(data[index].name));
	       }
	       else {
		   helRegistryConfigSelect.append($('<option></option>').val(data[index].id).html(data[index].name));
	       }
	    });
	}
    });
}
