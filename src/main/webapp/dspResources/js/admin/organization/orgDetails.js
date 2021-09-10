

require(['./main'], function () {
    $("input:text,form").attr("autocomplete", "off");

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(1000);
    }

    var isHELRegistry = $('#isHELRegistry').val();

    if(isHELRegistry == 1) {
        populateHELRegistries(1);
    }
    else if(isHELRegistry == 0) {
        $('#orgDetails').show();
    }

    //Check to see if the organization is a HEL Registry or not
    $(document).on('change','#isHELRegistry', function() {
       populateHELRegistries($(this).val());
    });

    var helRegistry = $('#helRegistry').attr('rel');

    if(helRegistry != 0 && helRegistry !== "0-") {
        populateHELRegistryOrgs(helRegistry);
    }

    //Registry is selected we need to go get the organizations set up for that organization
    $(document).on('change','#helRegistry', function() {
        populateHELRegistryOrgs($(this).val());
    });


    //A registry organization was selected need to get the details to populate the fields
    $(document).on('change','#helRegistryOrgId', function() {

        var selRegistryOrg = $(this).val();
        var selRegistrySchemaName = $(this).attr('schema');
        
        var selRegistry = $('#helRegistry').val();
        var selRegistryType = selRegistry.split("-")[2];
        
        var currRegistryOrg = $(this).attr('rel');
        
        if(selRegistryOrg != 0) {
            $.ajax({
                url: 'getHELRegistryOrganizationDetails?tenantId='+selRegistrySchemaName,
                type: "GET",
                data: {
                    'selRegistryOrgId': selRegistryOrg,
                    'registryType':selRegistryType
                },
                dataType: 'json',
                success: function (data) {
                    $('#orgDetails').show();

                    var data = $(data);

                    //set the org name
                    $('#orgName').val(data[0].name);
                    
                    var strippedorgName = data[0].name.replace(/ +/g, '');
                    $('#cleanURL').val(strippedorgName);

                    //set the org address
                    $('#address').val(data[0].address);

                    //set the org address 2
                    $('#address2').val(data[0].address2);

                    //set the org city
                    $('#city').val(data[0].city);

                    //set the org state
                    $('#state').val(data[0].state);

                    //set the org postal code
                    $('#postalCode').val(data[0].zipCode);

                    //set the org phone Number
                    $('#phone').val(data[0].phoneNumber);

                    //set the org website
                    $('#infoURL').val(data[0].website);

                    //set the org pirmary contact email
                    $('#primaryContactName').val(data[0].primaryContactName);
                    
                    //set the org pirmary contact email
                    $('#primaryContactEmail').val(data[0].primaryContactEmail);
                    
                    //set the org pirmary contact email
                    $('#primaryTechContactName').val(data[0].technicalContactName);
                    
                    //set the org pirmary contact email
                    $('#primaryTechContactEmail').val(data[0].technicalContactEmail);
                }
            });
        }
        else {
            $('#orgDetails').show();
        }
    });

    //Make sure the two values equal before the delete function is allowed
    $('#confirmDeleteOrg').click(function (event) {
        if ($('#realUsername').val() !== $('#username').val()) {
            $('#confirmDiv').addClass("has-error");
            $('#confirmMsg').html('The username entered does not match the username you logged in with!');
        } else {
            $('#confirmOrgDelete').submit();
        }
    });

    $('#saveDetails').click(function (event) {
        $('#action').val('save');

        //Need to make sure all required fields are marked if empty.
        var hasErrors = 0;
        hasErrors = checkFormFields();

        if (hasErrors == 0) {
            var selRegistry = $('#helRegistry').val();

            if(selRegistry != 0) {

                var selRegistrySchemaName = selRegistry.split("-")[1];

                //Set the schema name
                $('#helRegistrySchemaName').val(selRegistrySchemaName);
                $('#helRegistryId').val(selRegistry.split("-")[0]);
            }
            $("#organization").submit();
        }
    });

    $('#saveCloseDetails').click(function (event) {
        $('#action').val('close');

        var hasErrors = 0;
        hasErrors = checkFormFields();

        if (hasErrors == 0) {

            var selRegistry = $('#helRegistry').val();

            if(typeof selRegistry !== "undefined" && selRegistry != 0) {

                var selRegistrySchemaName = selRegistry.split("-")[1];

                //Set the schema name
                $('#helRegistrySchemaName').val(selRegistrySchemaName);
                $('#helRegistryId').val(selRegistry.split("-")[0]);
            }
            
            //Resave the clean url;
            $('#cleanURL').val('');
            var orgName = $('#orgName').val();
            var strippedorgName = orgName.replace(/ +/g, '');
            $('#cleanURL').val(strippedorgName);
            
            $("#organization").submit();
        }
    });

    //Need to set the organization clean url based off of the organization name
    /*$('#orgName').keyup(function (event) {
        var orgName = $(this).val();
        var strippedorgName = orgName.replace(/ +/g, '');
        $('#cleanURL').val(strippedorgName);
        //$('#nameChange').val(1);
    });*/
});

function populateHELRegistries(isHELRegistry) {
    
    if(isHELRegistry == 1) {
	$('#orgDetails').hide();

	var selRegistry = $('#helRegistry').attr('rel');

	$.ajax({
	     url: 'getHELRegistries?tenantId=registries',
	     type: "GET",
	     data: {},
	     dataType: 'json',
	     success: function (data) {
		 $('#HELRegistryDetails').show();
		 
		 var selRegistryId = selRegistry.split("-")[0];

		 var helRegistrySelect = $('#helRegistry');

		 $.each(data, function(index) {
		    if(data[index].id == selRegistryId) {
			helRegistrySelect.append($('<option selected></option>').val(data[index].id+'-'+data[index].dbschemaname+'-'+data[index].registryType).html(data[index].registryName));
		    }
		    else {
			helRegistrySelect.append($('<option></option>').val(data[index].id+'-'+data[index].dbschemaname+'-'+data[index].registryType).html(data[index].registryName));
		    }
		 });
	     }
	 });
    }
    else {
	$('#helRegistryId').val(0);
	$('#helRegistryOrgId').val(0);
	$('#helRegistrySchemaName').val("");
	$('#orgDetails').show();
	$('#helRegistry').find('option').remove().end().append('<option value="0">- Select -</option>').val('');
	$('#helRegistryOrgId').find('option').remove().end().append('<option value="0">- Select -</option>').val('');
	$('#HELRegistryDetails').hide();
    }
}

function populateHELRegistryOrgs(selRegistry) {
    
    if(selRegistry != 0) {
		
	var selRegistrySchemaName = selRegistry.split("-")[1];
        var selRegistryId = selRegistry.split("-")[0];
        var selRegistryType = selRegistry.split("-")[2];
        
	if(selRegistryId != 0 && selRegistrySchemaName !== "") {
            
            $('#helRegistryOrgId').empty();
            
            var tierLevel = 3;
            
            if(selRegistryType == 2) {
                tierLevel = 2;
            }
            
	    $.ajax({
		url: 'getHELRegistryOrganizations?tenantId='+selRegistrySchemaName,
		type: "GET",
		data: {
                    'registryType': selRegistryType,
                    'tierLevel': tierLevel
                },
		dataType: 'json',
		success: function (data) {
		    $('#orgDetails').show();
		    $('#HELRegistryOrgsDiv').show();
		    $('#helRegistryOrgId').attr('schema', selRegistrySchemaName);

		    var helRegistryOrgSelect = $('#helRegistryOrgId');
		    
		    var selRegistryOrgId = helRegistryOrgSelect.attr('rel');
		    
		    if(data.length > 0) {
                        
                        helRegistryOrgSelect.append($('<option></option>').val(0).html('N/A'));
                        
                        if(selRegistryType == 2) {
                            $('<optgroup/>').attr('label', 'Tier 2 - Subrecipients').appendTo(helRegistryOrgSelect);
                        }
                        
			$.each(data, function(index) {
			     if(data[index].id == selRegistryOrgId) {
				 helRegistryOrgSelect.append($('<option selected></option>').val(data[index].id).html(data[index].name + ' (Display Id: ' + data[index].displayId + ')'));
			     }
			     else {
				 helRegistryOrgSelect.append($('<option></option>').val(data[index].id).html(data[index].name + ' (Display Id: ' + data[index].displayId + ')'));
			     }
			});
		    }
                    else {
                        helRegistryOrgSelect.empty();
                    }
                    
                    //Also get tier 3 for family planning registries
                    if(selRegistryType == 2) {
                        $.ajax({
                            url: 'getHELRegistryOrganizations?tenantId='+selRegistrySchemaName,
                            type: "GET",
                            data: {
                                'registryType': selRegistryType,
                                'tierLevel': 3
                            },
                            dataType: 'json',
                            success: function (data) {
                                if(data.length > 0) {

                                    if(selRegistryType == 2) {
                                        $('<optgroup/>').attr('label', 'Tier 3 - Sites').appendTo(helRegistryOrgSelect);
                                    }

                                    $.each(data, function(index) {
                                         if(data[index].id == selRegistryOrgId) {
                                             helRegistryOrgSelect.append($('<option selected></option>').val(data[index].id).html(data[index].name + ' (Display Id: ' + data[index].displayId + ')'));
                                         }
                                         else {
                                             helRegistryOrgSelect.append($('<option></option>').val(data[index].id).html(data[index].name + ' (Display Id: ' + data[index].displayId + ')'));
                                         }
                                    });
                                }
                            }
                        });
                    }
		}
	    });
            
	}
	else {
	    //$('#orgDetails').hide();
	    $('#HELRegistryOrgsDiv').hide();
	    $('#helRegistryOrgId').find('option').remove().end().append('<option value="0">- Select -</option>').val('');
	}
    }
    else {
	//$('#orgDetails').hide();
	$('#HELRegistryOrgsDiv').hide();
	$('#helRegistryOrgId').find('option').remove().end().append('<option value="0">- Select -</option>').val('');
    }
}
	   
	
function checkFormFields() {
    var hasErrors = 0;
    
    $('#orgNameDiv').removeClass("has-error");
    $('#orgNameErrorMsg').removeClass("has-error");
    $('#orgNameErrorMsg').html('');
    
    var orgName = $('#orgName').val();
    
    if($.trim(orgName) === '') {
        $('#orgNameDiv').addClass("has-error");
        $('#orgNameErrorMsg').addClass("has-error");
        $('#orgNameErrorMsg').html('The organization name is a required field.');
        hasErrors = 1;
    }
    else if(/^[a-zA-Z0-9 ]*$/.test(orgName) === false) {
        $('#orgNameDiv').addClass("has-error");
        $('#orgNameErrorMsg').addClass("has-error");
        $('#orgNameErrorMsg').html('The organization name contains illegal characters. Only letters, numbers and spaces are accepted.');
        hasErrors = 1;
    }

    if ($('#file').length > 1) {
        var filename = $('#file').val();
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

    return hasErrors;
}

