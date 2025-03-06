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

        //This function will release the batch
        $(document).on('click', '.releaseOutboundBatch', function () {

            var confirmed = confirm("Are you sure you want to release this outbound batch?");

            if (confirmed) {
                $("#actionRowBottom").hide();
                $("#actionRowTop").hide();
                $.ajax({
                    url: '/administrator/processing-activity/outboundBatchOptions',
                    data: {
                        'batchOption': $(this).attr('rel'), 
                        'batchId': $(this).attr('rel2')
                    },
                    type: "POST",
                    success: function (data) {
                        window.location.href = '/administrator/processing-activity/outbound';
                    }
                });
            }
        });
    });
    
    function populateMessages(fromDate,toDate) {
    
        //CHeck if daylight savings time
        Date.prototype.stdTimezoneOffset = function () {
            var jan = new Date(this.getFullYear(), 0, 1);
            var jul = new Date(this.getFullYear(), 6, 1);
            return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
        }

        Date.prototype.isDstObserved = function () {
            return this.getTimezoneOffset() < this.stdTimezoneOffset();
        }

        var today = new Date();
        var isDST = 0;
        if (today.isDstObserved()) { 
           isDST = 1;
        }

        var batchName = $('#batchName').val();

        var userRole = $('#userRole').val();

        var searchTerm = $('#batchdownloads-table').attr('term');

        $('#batchdownloads-table').DataTable().destroy();
        
        var deferRender = false;
        
        var date1 = new Date(fromDate);
        var date2 = new Date(toDate);
        
        const timeDiff = Math.abs(date2.getTime() - date1.getTime());
        const daysDiff = Math.ceil(timeDiff / (1000*3600*24));
        
        if(daysDiff > 60) {
            deferRender = true;
        } 
        
        $('#batchdownloads-table').DataTable({
            bServerSide: true,
            bProcessing: true, 
            deferRender: deferRender,
            aaSorting: [[5,'desc']],
            "oSearch": {"sSearch": searchTerm },
            sPaginationType: "bootstrap", 
            oLanguage: {
               sEmptyTable: "There were no files sent out for the selected date range.", 
               sSearch: "_INPUT_",
               sSearchPlaceholder: 'Filter Outbound Batches',
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
            sAjaxSource: "/administrator/processing-activity/ajax/getBatchDownloads?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
            aoColumns: [
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "15%",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '<strong>' + data+'</strong><br />Org Id: '+row.orgId;
                        
                        return returnData;
                    }
                },
                {
                    "mData": "configName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "26%",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '';
                        
                        if(data !== '') {
                            returnData = '<strong>' + row.utBatchName + '</strong></br>';
                            returnData += 'Batch Id: ' + row.id + '</br>';

                            if(row.outputFileName !== '' && (row.statusId == 28 || row.statusId == 58 || row.statusId == 30)) {
                                returnData += '<a href="/FileDownload/downloadFile.do?fromPage=outbound&filename='+encodeURIComponent(row.outputFileName)+'&utBatchName='+row.utBatchName+'&foldername=archivesOut&orgId='+row.orgId+'" title="'+row.outputFileName+'">Download Outbound File</a></br>';
                            }
                            returnData += '</br><a href="/administrator/configurations/details?i='+row.configId+'" title="View Source Configuration">Config Name: '+data+'</a><br />Config Id: ' + row.configId + '<br />';
                        }
                        else {
                            returnData = '<strong>Invalid File</strong><br />';
                        }
                       return returnData;
                    }
                },
                {
                    "mData": "fromBatchName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "21%",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '<strong>'+row.srcOrgName + '</strong><br />';
                        returnData += '<a href="/administrator/processing-activity/inbound/'+data+'" title="View Inbound Batch" role="button">Batch Name: '+data+'</a><br />';
                        returnData += 'Batch Id: ' + row.batchUploadId +'<br />';

                        if(row.originalFileName !== '') {
                            returnData += '<a href="/FileDownload/downloadFile.do?fromPage=outbound&filename='+row.originalFileName+'&foldername=archivesIn&orgId=0" title="'+row.originalFileName+'">Download Submitted File</a>';
                        }

                       return returnData;
                    }
                },
                /*{
                    "mData": "transportMethod", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "12%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '';

                        if(data === 'File Upload') {
                            returnData = 'File Download';
                        }
                        else if(row.transportMethodId == 6) {
                            returnData = '<a href="/administrator/processing-activity/wsmessageOut/'+row.utBatchName+'" title="View Web Services Status">'+data+'</a>';
                        }
                        else if(row.transportMethodId == 9) {
                            returnData = '<a href="/administrator/processing-activity/apimessagesOut/'+row.utBatchName+'" title="View Rest API Message">'+data+'</a>';
                        }
                        else {
                            return data;
                        }
                    }
                },*/
                {
                    "mData": "statusValue", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                       return '<a href="#batchInfoModal" data-bs-toggle="modal" class="viewStatus" rel="'+row.statusId+'" title="View this Status">'+data+'</a>';
                    }
                },
                {
                    "mData": "totalRecordCount", 
                    "defaultContent": "",
                    "bSortable":false,
                    "sWidth": "15%",
                    "render": function ( data, type, row, meta ) {
                        var returnData = 'Total Rows in File: <strong>';
                        returnData += commaSeparateNumber(data) + '</strong><br />';
                        returnData += 'Total Errors: <strong>'+ commaSeparateNumber(row.totalErrorCount) + '</strong>';
                       return returnData;
                    }
                },
                {
                    "mData": "dateCreated", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "18%",
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

                        if(row.startDateTime != null) {
                            dateC = new Date(row.startDateTime);
                            minutes = dateC.getMinutes();
                            /*if(isDST == 1) {
                               if(hours < 0) {
                                   hours = 11;
                               }
                               else if(hours == 0) {
                                   hours = 12;
                               }
                            }
                            else {
                                hours = dateC.getHours();
                            }*/
                            hours = dateC.getHours();
                            ampm =  hours >= 12 ? 'pm' : 'am';
                            hours = hours % 12;
                            hours = hours ? hours : 12;
                            minutes = minutes < 10 ? '0'+minutes : minutes;

                            myDateFormatted += '<br />Start: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                        }

                        if(row.endDateTime != null) {
                            dateC = new Date(row.endDateTime);
                            minutes = dateC.getMinutes();
                            /*if(isDST == 1) {
                               if(hours < 0) {
                                   hours = 11;
                               }
                               else if(hours == 0) {
                                   hours = 12;
                               }
                            }
                            else {
                                hours = dateC.getHours();
                            }*/
                             hours = dateC.getHours();
                            ampm =  hours >= 12 ? 'pm' : 'am';
                            hours = hours % 12;
                            hours = hours ? hours : 12;
                            minutes = minutes < 10 ? '0'+minutes : minutes;

                            myDateFormatted += '<br />End: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                        }

                        return myDateFormatted;
                    }
                },
                {
                    "mData": "transportMethodId", 
                    "defaultContent": "",
                    "bSortable":false,
                    "sWidth": "5%",
                    "className": "center-text actions-col",
                    "render": function ( data, type, row, meta ) {
                       var returnData = '<div class="dropdown"><button class="btn btn-sm btn-default dropdown-toggle" type="button" data-bs-toggle="dropdown"><i class="fa fa-cog"></i></button><ul class="dropdown-menu pull-right">';

                       if(data != 2) {
                           returnData += '<li><a href="/administrator/processing-activity/outbound/batchActivities/'+row.utBatchName+'" class="viewBatchActivities" title="View Batch Activities"><span class="glyphicon glyphicon-edit"></span>View Batch Activities</a></li>';
                           returnData += '<li class="divider"></li>';
                           returnData += '<li><a href="/administrator/processing-activity/outbound/auditReport/'+row.utBatchName+'" title="View Audit Report"><span class="glyphicon glyphicon-edit"></span> View Audit Report</a></li>';
                       }

                       if(row.statusId == 64 || row.statusId == 59) {
                           returnData += '<li class="divider"></li>';
                           returnData += '<li><a href="#!" id="release" class="releaseOutboundBatch" rel="releaseBatch" rel2="'+row.id+'"><span class="glyphicon glyphicon-ok-sign"></span> Process Now</a></li>';
                       }

                       return returnData;
                    }
                }
             ]
        });  
    }

    function commaSeparateNumber(val){
        while (/(\d+)(\d{3})/.test(val.toString())){
          val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
        }
        return val;
     }

    function searchByDateRange() {
        var fromDate = $('.daterange span').attr('rel');
        var toDate = $('.daterange span').attr('rel2');

        $('#fromDate').val(fromDate);
        $('#toDate').val(toDate);

        populateMessages(fromDate,toDate);

    }
    
});