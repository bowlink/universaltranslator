/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery(function ($) {
    
    $(document).ready(function () {
        
        var fromDate = $('#fromDate').attr('rel');
        var toDate = $('#toDate').attr('rel');

        populateMessages(fromDate,toDate);

        //Function to display the details of the selected batch received from a direct HISP
        $(document).on('click', '.viewDirectDetails', function () {
            $.ajax({
                url: '/administrator/processing-activity/viewDirectDetailsOutById' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#modalContent").html(data);
                }
            });
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
    });
    
    function populateMessages(fromDate,toDate) {
    
        var batchName = $('#batchName').val();

        $('#directmessagesout-table').DataTable().destroy();

         $('#directmessagesout-table').DataTable({
            bServerSide: true,
            bProcessing: true, 
            deferRender: true,
            aaSorting: [[4,'desc']],
            sPaginationType: "bootstrap", 
            oLanguage: {
               sEmptyTable: "There were no outbound direct messages for the selected date range.", 
               sSearch: "_INPUT_",
               sSearchPlaceholder: 'Filter Direct Batches',
               sLengthMenu: '<select class="form-control" style="width:150px">' +
                    '<option value="10">10 Records</option>' +
                    '<option value="20">20 Records</option>' +
                    '<option value="30">30 Records</option>' +
                    '<option value="40">40 Records</option>' +
                    '<option value="50">50 Records</option>' +
                    '<option value="-1">All</option>' +
                    '</select>',
                 sProcessing: "<div style='background-color:#64A5D4; text-align:center; width:100%; height:50px; margin-top:100px; position:absolute'><p style='color:white; font-weight:bold; padding-top:15px;' class='bolder'>Retrieving Results. Please wait...</p></div>"
            },
            sAjaxSource: "/administrator/processing-activity/ajax/getDirectMessagesOut?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
            aoColumns: [
                {
                    "mData": "id", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    }
                },
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "20%",
                    "render": function ( data, type, row, meta ) {
                       return data;
                    }
                },
                {
                    "mData": "batchDownloadId", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "15%",
                    "render": function ( data, type, row, meta ) {
                        if(data > 0) {
                            return '<a href="/administrator/processing-activity/outbound/' + row.batchName + '" title="View Outbound Batch">' + row.batchName + '</a>';
                        }
                        else {
                            return "N/A";
                        }
                    }
                },
                {
                    "mData": "statusName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                       return data;
                    }
                },
                {
                    "mData": "dateCreated", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "15%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        var dateC = new Date(data);
                        var minutes = dateC.getMinutes();
                        var hours = dateC.getHours();
                        var ampm =  hours >= 12 ? 'pm' : 'am';
                        hours = hours % 12;
                        hours = hours ? hours : 12;
                        minutes = minutes < 10 ? '0'+minutes : minutes;
                        var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                        return myDateFormatted;
                    }
                },
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text actions-col",
                    "render": function ( data, type, row, meta ) {
                       return '<a href="#payloadModal" data-bs-toggle="modal" class="viewDirectDetails" rel="'+row.id+'" title="View Direct Message Details"><span class="glyphicon glyphicon-edit"></span> View Details</a>';
                    }
                }
             ]
        });   
    }

    function searchByDateRange() {
        var fromDate = $('.daterange span').attr('rel');
        var toDate = $('.daterange span').attr('rel2');

        $('#fromDate').val(fromDate);
        $('#toDate').val(toDate);

        populateMessages(fromDate,toDate);
    }
});
