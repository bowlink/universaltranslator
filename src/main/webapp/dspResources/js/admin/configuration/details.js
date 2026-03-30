


jQuery(function ($) {
    
     var formUpdated = false;
    
    //Check to see the page was updated so you can create the after snapshot
    $(document).ready(function () {

        if ($('.configWasUpdated').length > 0) {
            printAfterSnapshot($('#configIdForSnapshot').val(),'Details');
        }
        
        //Log a change happened
        $('#configuration :input').on('change input', function () {
            formUpdated = true;
        });
    });
    
   //Hide the Configuration Type if a target configuration type
   $('.type').change(function(event) {

      if($(this).val() == 2) {
          $('#configurationTypeDiv').hide();
      } 
      else {
          $('#configurationTypeDiv').show();
      }
   });


    $('#saveDetails').click(function (event) {
        $('#action').val('save');
        var hasErrors = 0;
        hasErrors = checkform();
        if (hasErrors == 0) {
            if(formUpdated) {
                $('body').overlay({
                   glyphicon : 'floppy-disk',
                   message : 'Saving Changes'
                });
                $('.overlay').css('display','block');
                printBeforeSnapshot($('#configIdForSnapshot').val(),'configuration','Details',null);
            }
            else {
                $("#configuration").submit();
            }
       }
    });

    $('#next').click(function (event) {
       $('#action').val('next');
       var hasErrors = 0;
       hasErrors = checkform();
       if (hasErrors == 0) {
            if(formUpdated) {
                $('body').overlay({
                   glyphicon : 'floppy-disk',
                   message : 'Saving Changes'
                });
                $('.overlay').css('display','block');
                printBeforeSnapshot($('#configIdForSnapshot').val(),'configuration','Details',null);
            }
            else {
                $("#configuration").submit();
            }
       }
    });
   
    function populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName) {
        $('#messageTypeDiv').hide();

        $.ajax({
            url: 'getHELRegistryConfigurations?tenantId='+helRegistrySchemaName,
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

    function checkform() {
        var errorFound = 0;

        var selectedType = $('.type:checked').val();

        //Check to make sure an organization is selected
        if ($('#organization').val() === '') {
            $('#orgDiv').addClass("has-error");
            $('#configOrgMsg').addClass("has-error");
            $('#configOrgMsg').html('The organization is a required field.');
            errorFound = 1;
        }

        //Check to make sure a configuration name is entered
        if ($('#configName').val() === '') {
            $('#configNameDiv').addClass("has-error");
            $('#configNameMsg').addClass("has-error");
            $('#configNameMsg').html('The configuration name is a required field.');
            errorFound = 1;
        }
        else {
            //Check to see if organization name contains any special characters
            var configName = $('#configName').val();
            if(/^[a-zA-Z0-9- ]*$/.test(configName) == false) {
                $('#configNameDiv').addClass("has-error");
                $('#configNameMsg').addClass("has-error");
                $('#configNameMsg').html('The configuration name contains illegal characters. Only letters and numbers are accepted.');
                errorFound = 1;
            }
        }

        return errorFound;
    }

});

