/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery(function ($) {
    
    $(document).ready(function () {
        
        var registryType = $('#registryType:checked').val();
        populateAgencies(registryType);

        $(document).on('change','#registryType', function() {
           populateAgencies($(this).val());
        });
        
        $('.date-range-picker-trigger').daterangepicker(
            {
                ranges: {
                    'See All': [$('#fromDate').attr('rel2'), moment()],
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
                    'Last 7 Days': [moment().subtract('days', 6), moment()],
                    'Last 30 Days': [moment().subtract('days', 29), moment()],
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
                },
                startDate: $('#fromDate').attr('rel'),
                endDate: $('#toDate').attr('rel')
            },
            function (start, end) {
                $('.daterange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
                $('.daterange span').attr('rel', start.format('MM/DD/YYYY'));
                $('.daterange span').attr('rel2', end.format('MM/DD/YYYY'));
                searchByDateRange();
            }
        );
    
        var pageRefreshRate = $('.refreshRate').attr('rel');
    
        if(pageRefreshRate > 0) {
            setTimeout(function(){
                window.location.reload(1);
            }, pageRefreshRate);
        }
        
        try {
            datatable.fnDestroy();

            datatable = $('#reportDataTable').dataTable({
                "aaSorting": [[ 4, 'desc' ]],
                "sPaginationType": "bootstrap",
                "bLengthChange": false,
                "bInfo": false,
                "bFilter":false
            });
        }
        catch(err) {}    
   
        $(document).on('click', '.setRefreshRate',function() {

            $.ajax({
                url: '/administrator/processing-activity/setPageRefreshRate.do',
                data: {
                    'refreshRate': $(this).attr('rel')
                },
                type: 'POST',
                success: function (data, date) {
                    window.location.reload(1);
                },
                error: function (error) {
                    console.log(error);
                }
            });
        });

        //Button to select/deselect all report checkboxes.
        $(document).on('click', '#checkAll', function() {
            $('input:checkbox[name=selAgency]').prop('checked', this.checked);   
        });
    
        $(document).on('click','#generateReport',function() {

            var agencies = $("input:checkbox[name=selAgency]:checked").map(function(){
                return $(this).val();
            }).get(); 

            if(agencies.length > 0) {
                $('body').overlay({
                    glyphicon: 'list-alt',
                    message: 'Generating Report...'
                }); 

                $.ajax({
                    url: '/administrator/processing-activity/generateReport',
                    data: {
                        'reportType': $('#reportType:checked').val(),
                        'registryType': $('#registryType:checked').val(),
                        'agencies': agencies.join(","),
                        'fromDate': $('#fromDate').val(),
                        'toDate': $('#toDate').val()
                    },
                    type: "POST",
                    success: function(data) {
                        window.location.href='/administrator/processing-activity/generateReport';
                    }
                });
            }
            else {
                $('#agencyDiv').addClass("has-error");
                $('#agencyMsg').addClass("has-error");
                $('#agencyMsg').html('You must select at least one agency to run the report.');
            }
        });

        $(document).on('click','.rerunReport',function() {
            $('body').overlay({
                glyphicon: 'list-alt',
                message: 'Generating Report...'
            }); 

            $.ajax({
                url: '/administrator/processing-activity/reRunActivityReport',
                data: {
                    'activityReportId': $(this).attr('rel')
                },
                type: "POST",
                success: function(data) {
                    window.location.href='/administrator/processing-activity/generateReport';
                }
            });
        });

        $(document).on('click','.deleteReport',function() {

            var reportId = $(this).attr('rel');

            if(confirm("Are you sure you want to remove this report?")) {

                $.ajax({
                    url: '/administrator/processing-activity/deleteActivityReport',
                    data: {
                        'activityReportId': $(this).attr('rel')
                    },
                    type: "POST",
                    success: function(data) {
                        window.location.href='/administrator/processing-activity/generateReport';
                    }
                });
            }
        });

        //This function will launch the new crosswalk overlay with a blank form
        $(document).on('click', '.showAgencies', function () {

            var activityReportId = $(this).attr('rel');

            $.ajax({
                url: '/administrator/processing-activity/displayActivityReportAgencies',
                type: "GET",
                data: {
                    'activityReportId': activityReportId
                },
                success: function (data) {
                    $("#modalContent").html(data);
                }
            });
        });
    });
    
    function populateAgencies(registryType) {
        $('#agencyList').html("");
        
        if(registryType == 1 || registryType == 2) {

            $.ajax({
                url: '/administrator/organizations/getAgenciesForReport',
                data: {
                    'registryType': registryType
                },
                type: "GET",
                success: function(data) {
                    var data = $(data);

                    //Check if the session has expired.
                    if(data.find('.username').length > 0) {
                       top.location.href = '/logout';
                    }
                    else {
                        $("#agencyList").html(data);
                    }
                }
            });
        }
        else {
           $('#agencyList').html("Please choose a valid registry type."); 
        }
    }

    function searchByDateRange() {
        var fromDate = $('.daterange span').attr('rel');
        var toDate = $('.daterange span').attr('rel2');

        $('#fromDate').val(fromDate);
        $('#toDate').val(toDate);
    }
});
