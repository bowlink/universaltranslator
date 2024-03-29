

require(['./main'], function () {
    $("input:text,form").attr("autocomplete", "off");
    
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
    
    $(document).on('click','.createNewTemplate',function() {
      
        var configId = $(this).attr('rel');

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
                }
                else {
                    alert("An error occurred creating your template file. A Health-e-Link system administrator has been notified.");
                }
            }
        });
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

    //Fade out the updated/created message after being displayed.
    if ($('.alert-success').length > 0) {
        $('.alert-success').delay(2000).fadeOut(1000);
    }

    //If any field changes need to show the message in red that nothign
    //will be saved unless teh "Saved" button is pressed
    $(document).on('change', '.formField', function () {
        $('#saveMsgDiv').show();
    });

    var sourceConfigurationId = $('.templateFields').attr('rel');

    if(sourceConfigurationId) {
        loadSourceConfigurationFields(sourceConfigurationId);
    }

    $('#loadConfigurationFields').click(function(event) {
       loadSourceConfigurationFields($(this).attr('sourceConfigId'));
    });


    $('.useField').change(function() {
        if(!$(this).prop( "checked" )) {
            var fieldNo = $(this).attr('fieldNo');
            $('#validation_'+fieldNo).val(1);
            $('#match_'+fieldNo).val(0);
        }
    });


    //This function will save the messgae type field mappings
    $('#saveDetails').click(function (event) {
        $('#action').val('save');

        //Need to make sure all required fields are marked if empty.
        var hasErrors = 0;
        $('.alert-danger').hide();

        if (hasErrors == 0) {

            var formData = $("#formFields").serialize();

            $.ajax({
                url: 'saveFields',
                data: formData,
                type: "POST",
                async: false,
                success: function (data) {
                    $('.fieldsUpdated').show();
                    $('.alert').delay(2000).fadeOut(1000);
                }
            });
            event.preventDefault();
            return false;

        }
    });

    $('#next').click(function (event) {
        $('#action').val('next');
        $('.alert-danger').hide();

        var hasErrors = 0;

        if (hasErrors == 0) {
            var formData = $("#formFields").serialize();

            $.ajax({
                url: 'saveFields',
                data: formData,
                type: "POST",
                async: false,
                success: function (data) {
                    window.location.href = 'translations';
                }
            });
            event.preventDefault();
            return false;
        }
    });

    //Reload Configuration Fields
    $('#reloadConfigurationFields').click(function (event) {
        $.ajax({
            url: 'reloadConfigurationFields',
            data: {
                'configurationId' : $(this).attr('rel'),
                'transportDetailsId': $(this).attr('rel2')
            },
            type: "GET",
            success: function (data) {
                window.location.href = 'mappings';
            }
        });
    });

    $('#appendConfigurationFields').click(function(event) {
        $.ajax({
            url: '/administrator/configurations/appendConfigurationFields',
            data: {
                'configId' : $(this).attr('rel'),
                'configTransportId': $(this).attr('rel2')
            },
            type: "GET",
            success: function (data) {
                $("#appendNewFieldsModal").html(data);
            }
        });
    });
});

function loadSourceConfigurationFields(sourceConfigId) {
    
    $('.matchingField').each(function() {
	 $(this).find('option').remove().end().append('<option value="0">-</option>').val('');
    });
    
    $.ajax({
	url: 'getSourceConfigurationFields',
	type: "GET",
	data: {
	    'sourceConfigId': sourceConfigId
	},
	success: function (data) {
	    //Load the available fields
	    $('#availableFields').html(data);
	   
	    var data = $(data);
	    
	    //find the element name
	    var fieldRows = data.find('.fieldRow');
	    
	    if(fieldRows.length > 0) {
		
		$('.matchingField').each(function() {
		    var selectObj = $(this);
		    var currVal = $(this).attr('currval');
		    
		    fieldRows.each(function(){
			var rowValues = $(this).attr('rel');
			var rowValuesSplit = rowValues.split('-');
			var validationId = rowValuesSplit[3];
			
			if((rowValuesSplit[0]*1) == (currVal*1)) {
			    selectObj.append($('<option selected></option>').val(rowValues).html(rowValuesSplit[2]));
			}
			else {
			    selectObj.append($('<option></option>').val(rowValues).html(rowValuesSplit[2]));
			}
			
		    });
		});
	    }
	}
    });
}

