



jQuery(function ($) {
    
    $(document).ready(function () {
        
        $.ajaxSetup({
            cache: false
        });
        
        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
        
        $("input:text,form").attr("autocomplete", "off");

        if ($('#watchListdatatable').length) {
            
            $('#watchListdatatable').DataTable({
                bAutoWidth: false,
                bStateSave: false,
                aaSorting: [[6,'asc']],
                "oLanguage": {
                    "sSearch": "_INPUT_",
                    sSearchPlaceholder: 'Filter Watch List Entries',
                    "sLengthMenu": '<select class="form-control" style="width:150px">' +
                            '<option value="10">10 Records</option>' +
                            '<option value="20">20 Records</option>' +
                            '<option value="30">30 Records</option>' +
                            '<option value="40">40 Records</option>' +
                            '<option value="50">50 Records</option>' +
                            '<option value="-1">All</option>' +
                            '</select>'
                },
               "aoColumns" : [
                    { "sWidth": "15%" },
                    { "sWidth": "20%" },
                    { "sWidth": "20%" },
                    { "sWidth": "10%" },
                    { "sWidth": "10%" },
                    { "sWidth": "10%" },
                    { "sWidth": "10%" },
                    { "sWidth": "5%" }
                ]
            });
        }

        //This function will launch the new watch entry overlay with a blank screen
        $(document).on('click', '#createNewWatchEntry', function () {
            $.ajax({
                url: '/watchlist/createWatchEntry',
                type: "GET",
                success: function (data) {
                    
                     data = $(data);
                    
                    var date = new Date();
                    var currentMonth = date.getMonth();
                    var currentDate = date.getDate();
                    var currentYear = date.getFullYear();

                    data.find('.expectFirstFile').daterangepicker({
                        singleDatePicker: true,
                        timePicker: false,
                        showDropdowns: true,
                        minDate: new Date(currentYear, currentMonth, currentDate),
                        startDate: moment(date),
                        minYear: parseInt(moment().format('YYYY')),
                        maxYear: parseInt(moment().format('YYYY'))
                    });
                    
                    $("#modalContent").html(data);
                }
            });
        });

        $(document).on('click', '.deleteWatchEntry', function() {

            var watchId = $(this).attr('rel');

            if(confirm("Are you sure you want to remove this watch entry?")) {
               
                $.ajax({
                    url: '/watchlist/deleteWatchEntry',
                    data: {
                        'watchId': watchId
                    },
                    type: 'POST',
                    success: function(data) {
                       location.reload();
                    }
                });

            }

        });

        //This function will launch the edit watch entry overlay
        $(document).on('click', '.watchEntryEdit', function () {
            var entryId = $(this).attr('rel');
            $.ajax({
                url: '/watchlist/editWatchEntry',
                type: "GET",
                data: {
                    'entryId': entryId
                },
                success: function (data) {

                    data = $(data);
                    
                    var orgId = data.find('.selOrganization');
                    
                    var date = new Date();
                    var currentMonth = date.getMonth();
                    var currentDate = date.getDate();
                    var currentYear = date.getFullYear();

                    data.find('.expectFirstFile').daterangepicker({
                        singleDatePicker: true,
                        timePicker: false,
                        showDropdowns: true,
                        minDate: new Date(currentYear, currentMonth, currentDate),
                        startDate: moment(date),
                        minYear: parseInt(moment().format('YYYY')),
                        maxYear: parseInt(moment().format('YYYY'))
                    });
                    
                    $("#modalContent").html(data);
                    
                     if(orgId.val() > 0) {
                         populateConfigurations(orgId.val());
                    }
                }
            });
        });

        //Go get the existing message types for the selected organization'
        $(document).on('change', '.selOrganization', function () {
            var selOrg = $(this).val();

            if (selOrg === '') {
                $('#orgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg);
            }
        });

        //This function will save the messgae type field mappings
        $(document).on('click', '#submitButton', function () {
            var hasErrors = 0;
            var msg = $('#entryMessage').val();
            var org = $('#organization').val();
            var config = $('#config').val();

            $('div.form-group').removeClass("has-error");
            $('span.control-label').removeClass("has-error");
            $('span.control-label').html("");
            $('.alert-danger').hide();

            if($('.tab-pane.active').attr("id") == 'generic') {
                if (msg === '') {
                    $('#entryMessageDiv').addClass("has-error");
                    hasErrors = 1;
                }
                if ($('#expectFirstFile').val() === '') {
                    $('#expectFirstFileDiv').addClass("has-error");
                    hasErrors = 1;
                }
            }
            else {
                if (org === '') {
                    $('#orgDiv').addClass("has-error");
                    hasErrors = 1;
                }

                if (config === '') {
                    $('#configDiv').addClass("has-error");
                    hasErrors = 1;
                }
                if ($('#expectFirstFileMT').val() === '') {
                    $('#expectFirstFileMTDiv').addClass("has-error");
                    hasErrors = 1;
                }
            }

            if (hasErrors == 0) {

                if($('.tab-pane.active').attr("id") == 'generic') {
                    //Check to see if a time has been selected
                    if($('#expectedTimeHour').val() != "" && 
                        $('#expectedTimeMinute').val() != "" && 
                        $('#expectedTimeHAMPM').val() != "") {

                        var selectedExpectedTime = $('#expectedTimeHour').val()+":"+$('#expectedTimeMinute option:selected').text()+" "+$('#expectedTimeAMPM').val();
                        $('#expectFirstFileTime').val(selectedExpectedTime);
                    }
                    $('#watchListGenericEntryForm').submit();
                }
                else {
                    //Check to see if a time has been selected
                    if($('#expectedTimeHourMT').val() != "" && 
                        $('#expectedTimeMinuteMT').val() != "" && 
                        $('#expectedTimeHAMPMMT').val() != "") {

                        var selectedExpectedTime = $('#expectedTimeHourMT').val()+":"+$('#expectedTimeMinuteMT option:selected').text()+" "+$('#expectedTimeAMPMMT').val();
                        $('#expectFirstFileTimeMT').val(selectedExpectedTime);
                    }
                    $('#watchListEntryForm').submit();
                }
            }

        });
    });    
   
    function populateConfigurations(orgId) {
    
        var currConfigId = $('#config').attr('rel');

        $.ajax({
            url: '/administrator/configurations/getAvailableConfigurations.do',
            type: "GET",
            data: {'orgId': orgId},
            success: function (data) {
                //get value of preselected col
                var html = '<option value="">- Select - </option>';
                var len = data.length;

                for (var i = 0; i < len; i++) {
                    if (data[i].id == currConfigId) {
                        html += '<option value="' + data[i].id + '" selected>' + data[i].configname +  '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                    } else {
                        html += '<option value="' + data[i].id + '">' + data[i].configname + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                    }
                }
                $('#config').html(html);
            }
        });
    }
});