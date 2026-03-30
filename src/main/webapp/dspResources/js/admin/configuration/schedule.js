

jQuery(function ($) {
    
    var formUpdated = false;
    
    $(document).ready(function () {
        if ($('.configWasUpdated').length > 0) {
           printAfterSnapshot($('#configIdForSnapshot').val(),$('.configWasUpdated').data('module'));
        }
        
        //Log a change happened
        $('#schedulingSpecs :input').on('change input', function () {
            formUpdated = true;
        });
        
        $('.processMethod').on('change input', function () {
            formUpdated = true;
        });
        
        showScheduleForm();

        //function that will get the field mappings for the selected transport method
        $('.changeTransportMethod').click(function () {
            var selTransportMethod = $('#transportMethod').val();

            if (selTransportMethod === "") {
                $('#transportMethodDiv').addClass("has-error");
            } else {
                window.location.href = 'scheduling?i=' + selTransportMethod;
            }
        });

        //Toggle Scheduling Specs when process configuration is changed
        $('.processMethod').change(function () {
            showScheduleForm();
        });

        //Toggle check how often
        $('.processingType').change(function () {

            if ($(this).val() === "1") {
                $('#processingTimeDiv').show();
                $('#newfilecheckDiv').hide();
            } else {
                $('#newfilecheckDiv').show();
                $('#processingTimeDiv').hide();
            }

        });

        //This function will save the schedule mappings
        $('#saveDetails').click(function () {
            
            if(formUpdated) {
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Saving Changes'
                });
                $('.overlay').css('display','block');
                printBeforeSnapshot($('#configIdForSnapshot').val(),'schedulingSpecs','schedule',null);
            }
            else {
               $('#schedulingSpecs').submit();
            }
        });

        $('#next').click(function () {
            $('#action').val("next");
            
            if(formUpdated) {
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Saving Changes'
                });
                $('.overlay').css('display','block');
                printBeforeSnapshot($('#configIdForSnapshot').val(),'schedulingSpecs','schedule',null);
            }
            else {
               $('#schedulingSpecs').submit();
            }
        });
    });
})


function showScheduleForm() {
    var type = $('.processMethod:checked').val();
    $('#type').val(type);
    if (type !== "1" && type !== "5") {
        $('#specs').show();

        //Hide all fields
        $('.specFormFields').hide();

        //Daily
        if (type === "2") {
            $('#processingTypeDiv').show();

            if ($('.processingType:checked').val() === "1") {
                $('#processingTimeDiv').show();
            } else {
                $('#newfilecheckDiv').show();
            }
        }

        //Weekly
        if (type === "3") {
            $('#processingDayDiv').show();
            $('#processingTimeDiv').show();
        }

        //Monthly
        if (type === "4") {
            $('#processingTimeDiv').show();
        }
    } else {
        $('#specs').hide();
    }
}

