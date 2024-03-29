
require(['./main'], function () {

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(5000);
    }

    $("input:text,form").attr("autocomplete", "off");
    
    $(document).on('change','#delimiter', function() {
        var cwFileLink = $('#cwFileLink').attr('href');
        
        cwFileLink = cwFileLink.replace('&delim='+$('#delimiter').data("delim"),'&delim='+$(this).val());
        $('#delimiter').data("delim",$(this).val());
        $('#cwFileLink').attr('href',cwFileLink);
    });


     //This function will launch the crosswalk overlay with the selected
    //crosswalk details
    $(document).on('click', '.viewCrosswalk', function () {
        $.ajax({
            url: '/administrator/configurations/viewCrosswalk' + $(this).attr('rel'),
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
            url: '/administrator/configurations/newCrosswalk',
            type: "GET",
            data: {'orgId': orgId},
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });

    //The function to submit the new crosswalk
    $(document).on('click', '#submitCrosswalkButton', function (event) {
        $('.uploadError').hide();
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
                url: '/administrator/configurations/checkCrosswalkName.do',
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
                            url: '/administrator/configurations/viewCrosswalk?i=' + data,
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

        //$('#crosswalkdetailsform').attr('action', actionValue + 'Crosswalk');
        //$('#crosswalkdetailsform').submit();

    });
    
    $(document).on('click','.deleteCrosswalk',function() {
        var cwId = $(this).attr('rel');
        
        if(confirm("Are you sure you want to remove this crosswalk?")) {
            $.ajax({
                url: '/administrator/configurations/checkIfCWInUse.do',
                data: {
                    'cwId': cwId
                },
                type: 'POST',
                success: function(data) {
                    console.log("IN");
                    console.log(data);
                    if(data === "0") {
                        $('body').overlay({
                            glyphicon : 'floppy-disk',
                            message : 'Deleting...'
                        });

                        $.ajax({
                            url: '/administrator/configurations/deleteCrosswalk.do',
                            data: {
                                'cwId': cwId
                            },
                            type: 'POST',
                            success: function(data) {
                              location.reload();
                            }
                        });
                    }
                    else {
                        alert("This crosswalk is being used in the follwing configurations (" + data + ").");
                    }
                }
            });
        }
        
        /*if((dtsId*1) > 0) {
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
                    url: '/administrator/configurations/deleteCrosswalk.do',
                    data: {
                        'cwId': cwId
                    },
                    type: 'POST',
                    success: function(data) {
                      location.reload();
                    }
                });
            }
        }*/
    });
});