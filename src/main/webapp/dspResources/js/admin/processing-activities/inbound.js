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
    
        //This function will release the batch
        $(document).on('click', '.releaseBatch', function () {

            var confirmed = confirm("Are you sure you want to release this batch?");

            if (confirmed) {
                $.ajax({
                    url: '/administrator/processing-activity/inboundBatchOptions',
                    data: {
                        'batchOption': $(this).attr('rel'), 
                        'batchId': $(this).attr('rel2')
                    },
                    type: "POST",
                    success: function (data) {
                        window.location.href = '/administrator/processing-activity/inbound';
                    }
                });
            }
        });

        //Function to display the details of the selected batch received from a direct HISP
        $(document).on('click', '.viewDirectDetails', function () {
            $.ajax({
                url: '/administrator/processing-activity/viewDirectDetails' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#directModal").html(data);
                }
            });
        });

        $(document).on('click', '.deleteTransactions', function() {

            var batchName = $(this).attr('rel');

            if(confirm("Are you sure you want to remove this batch?")) {

                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Deleting...'
                });

                $.ajax({
                    url: 'deleteBatch.do',
                    data: {
                        'batchName': batchName
                    },
                    type: 'POST',
                    success: function(data) {
                       location.reload();
                    }
                });
            }
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

        var searchTerm = $('#term').val();
        
        var isEAH = $('#batchuploads-table').attr('rel');

        $('#batchuploads-table').DataTable().destroy();
        
        var deferRender = false;
        
        var date1 = new Date(fromDate);
        var date2 = new Date(toDate);
        
        const timeDiff = Math.abs(date2.getTime() - date1.getTime());
        const daysDiff = Math.ceil(timeDiff / (1000*3600*24));
        
        if(daysDiff > 60) {
            deferRender = true;
        } 

        $('#batchuploads-table').DataTable({
            bServerSide: true,
            bProcessing: true, 
            deferRender: deferRender,
            aaSorting: [[4,'desc']],
            "oSearch": {"sSearch": searchTerm },
            sPaginationType: "bootstrap", 
            oLanguage: {
               sEmptyTable: "There were no files submitted for the selected date range.", 
               sSearch: "_INPUT_",
               sSearchPlaceholder: 'Filter Inbound Batches',
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
            sAjaxSource: "/administrator/processing-activity/ajax/getBatchUploads?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
            aoColumns: [
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "25%",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '<strong>' + data+'</strong><br />Org Id: '+row.orgId;
                        
                        if(row.systemName != '' && isEAH === 'false') {
                            returnData += '<br />System: ' + row.systemName;
                        }
                        
                        return returnData;
                    }
                },
                {
                    "mData": "configName", 
                    "defaultContent": "",
                    "bSortable":false,
                    "sWidth": "25%",
                    "render": function ( data, type, row, meta ) {
                        
                        if(data !== '') {
                            
                            var returnData = '<strong>Batch Name: ' + row.utBatchName + '</strong><br />';
                            
                            returnData += 'Batch Id: ' + row.id+ '<br />';

                            if(row.transportMethodId != 2) {

                                if(row.transportMethodId == 9 || row.transportMethodId == 12 ) {
                                   returnData += '<a href="/FileDownload/downloadFile.do?fromPage=inbound&filename='+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />';
                                }
                                else if (row.transportMethodId == 6) {
                                    returnData += '<a href="/FileDownload/downloadFile.do?fromPage=inbound&filename='+row.utBatchName+'_dec.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />';
                                }
                                else if(row.transportMethodId == 13 ) {
                                   returnData += '<a href="/FileDownload/downloadFile.do?fromPage=inbound&filename=archive_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn&orgId='+row.orgId+'" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />';
                                }
                                else {
                                   returnData += '<a href="/FileDownload/downloadFile.do?fromPage=inbound&filename=encoded_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=input files&orgId='+row.orgId+'" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />'; 
                                }

                                if(row.originalFileName.split('.')[1].toString().toLowerCase() != 'txt' || row.fileDelimiter == 13) {
                                    if(row.inboundBatchConfigurationType == 1 && (row.transportMethodId == 10 || row.transportMethodId == 13)) {
                                        if(row.transportMethod.indexOf("Direct") > 0 || row.transportMethod === 'File Drop') {
                                            returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename='+row.utBatchName+'.txt&foldername=loadFiles" title="View Pipe File">Internal File - '+row.utBatchName+'.txt</a><br />';
                                        }
                                        else {
                                            returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename=archive_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="Download Pipe File">Internal File - '+row.utBatchName+'</a><br />';
                                        }
                                    }
                                }
                            }
                            
                            if(row.configId > 0) {
                                returnData += '<br /><a href="/administrator/configurations/details?i='+row.configId+'" title="View Source Configuration">Config Name: '+data+'</a><br />Config Id: ' + row.configId;
                            }
                            else {
                                returnData += '<br />Config Name: Not Found<br />Config Id: Not Found';
                            }

                            return returnData;
                        }
                        else {
                            return '<strong>Invalid File</strong>';
                        }
                    }
                },
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
                        returnData += 'Total Errors Found: <strong>'+ commaSeparateNumber(row.errorRecordCount) + '</strong>';
                        if((row.totalErrorRows*1) > 0 && (row.errorRecordCount*1) > 0) {
                            returnData += '<br />Total Rows with Errors: <strong>'+ commaSeparateNumber(row.totalErrorRows) + '</strong>';
                        }
                       return returnData;
                    }
                },
                {
                    "mData": "dateSubmitted", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "12%",
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

                            var dateS = new Date(row.startDateTime);
                            minutes = dateS.getMinutes();

                            /*if(isDST == 1) {
                                hours = dateS.getHours()-1;
                                if(hours < 0) {
                                    hours = 11;
                                }
                                else if(hours == 0) {
                                    hours = 12;
                                }
                            }
                            else {
                                hours = dateS.getHours();
                            }*/
                            hours = dateS.getHours();


                            ampm =  hours >= 12 ? 'pm' : 'am';
                            hours = hours % 12;
                            hours = hours ? hours : 12;
                            minutes = minutes < 10 ? '0'+minutes : minutes;

                            if((dateS.getMonth()*1)+1 != (dateC.getMonth()*1)+1 || (dateS.getDate() != dateC.getDate())) {
                                 myDateFormatted += '<br /><strong>Reprocessed: ' + ((dateS.getMonth()*1)+1)+'/'+dateS.getDate()+'/'+dateS.getFullYear() + '</strong>';
                            }

                            myDateFormatted += '<br />Start: ' + ((dateS.getMonth()*1)+1)+'/'+dateS.getDate()+'/'+dateS.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
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
                    "className": "",
                    "render": function ( data, type, row, meta ) {
                       var returnData = '<div class="dropdown"><button class="btn btn-sm btn-default dropdown-toggle" type="button" data-bs-toggle="dropdown"><i class="fa fa-cog"></i></button><ul class="dropdown-menu pull-right">';

                        if(row.statusValue === 'MAN') {
                           returnData += '<li><a href="#!" id="release" class="releaseBatch" rel="releaseBatch" rel2="'+row.id+'" title="Process Inbound Batch"><span class="glyphicon glyphicon-ok-sign"></span></span> Process Inbound Batch</a></li>';
                           returnData += '<li class="divider"></li>';
                        }

                        if(data != 2) {
                            returnData += '<li><a href="/administrator/processing-activity/inbound/batchActivities/'+row.utBatchName+'" class="viewBatchActivities" title="View Batch Activities"><span class="glyphicon glyphicon-edit"></span> View Batch Activities</a></li>';
                            if(row.configId > 0) {
                                returnData += '<li class="divider"></li>';
                                returnData += '<li><a href="/administrator/processing-activity/inbound/auditReport/'+row.utBatchName+'" title="View Audit Report"><span class="glyphicon glyphicon-edit"></span> View Audit Report</a></li>';
                            }
                        }

                       if(userRole == 1) {
                            returnData += '<li class="divider"></li>';
                            if(row.configId > 0) {
                                returnData += '<li><a href="/administrator/configurations/details?i='+row.configId+'" title="View Source Configuration"><span class="glyphicon glyphicon-edit"></span> View Source Configuration</a></li>';
                                returnData += '<li class="divider"></li>';
                            }
                            returnData += '<li><a href="javascript:void(0);" rel="'+row.utBatchName+'" class="deleteTransactions" title="Delete Batch Transactions"><span class="glyphicon glyphicon-remove"></span> Delete Batch</a></li>';
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