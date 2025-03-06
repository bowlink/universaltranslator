/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery(function ($) {
    
    $(document).ready(function () {
        
         $("input:text,form").attr("autocomplete", "off");
        
        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
        
        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewStatus', function () {
            $.ajax({
                url: '/administrator/processing-activity/viewStatus' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#modalContent").html(data);
                }
            });
        });
        
        getGenericMessages();
        getInboundMessages();
        getOutboundMessages();
        //setInterval(function(){getGenericMessages()}, 50000);
        //setInterval(function(){getInboundMessages()}, 50000);
        //setInterval(function(){getOutboundMessages()}, 50000);

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
                getGenericMessages();
                getInboundMessages();
                getOutboundMessages();
            }
        );
    });

    function getGenericMessages() {

        var fromDate = $('.daterange span').attr('rel');
        var toDate = $('.daterange span').attr('rel2');

        if(fromDate == null) {
            fromDate = $('#fromDate').attr('rel');
        }

        if(toDate == null) {
            toDate = $('#toDate').attr('rel');
        }

        $.ajax({
            url: '/administrator/processing-activity/dashboardGenericBatches',
            data: {
                'fromDate': fromDate, 
                'toDate': toDate
            },
            type: "GET",
            success: function(data) {
                
               $('.genericMessages').html(data);
                
               $('#genericdataTable').DataTable({
                    "bAutoWidth": false,
                    "bStateSave": true,
                    "iCookieDuration": 60,
                    "sPaginationType": "bootstrap",
                    "oLanguage": {
                        "sSearch": "_INPUT_",
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
                        { "sWidth": "5%" },
                        { "sWidth": "10%" },
                        { "sWidth": "85%" }
                    ],
                   "aaSorting" : [[1, "desc"]]
                });
            }
        });
    }

    function getInboundMessages() {

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

        var fromDate = $('.daterange span').attr('rel');
        var toDate = $('.daterange span').attr('rel2');

        if(fromDate == null) {
            fromDate = $('#fromDate').attr('rel');
        }

        if(toDate == null) {
            toDate = $('#toDate').attr('rel');
        }
        
        var isEAH = $('#inbounddataTable').attr('rel');
        
        $('#inbounddataTable').DataTable().destroy();
        
        $('#inbounddataTable').DataTable({
            bProcessing: true,
            bServerSide: true,
            deferRender: true,
            aaSorting: [[5,'desc']],
            sPaginationType: "bootstrap", 
            fnDrawCallback: function() {
                $('[data-bs-toggle="popover"]').popover();
            },
            oLanguage: {
               sEmptyTable: "There were no files received for the selected date range.", 
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
            sAjaxSource: "/administrator/processing-activity/dashboardInBoundBatches?fromDate="+fromDate+"&toDate="+toDate,
            createdRow: function(row, data, index) {
                $(row).addClass('batchRow');
                
                if(data.uploadType === 'Watch List Entry') {
                    $(row).addClass('table-primary');
                }
                else {
                    $(row).attr('data-bs-trigger', 'hover');
                    $(row).attr('data-bs-toggle', 'popover');
                    $(row).attr('data-bs-placement', 'top');
                    $(row).attr('data-bs-html', 'true');
                    $(row).attr('data-bs-animation', 'false');
                    $(row).attr('data-bs-title', 'File Status');
                    
                    if(data.statusId == 23 || data.statusId == 24) {
                        if(data.errorRecordCount == data.totalRecordCount) {
                            $(row).addClass('table-danger');
                            $(row).attr('data-bs-data-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
                        }
                        else if(data.errorRecordCount > 0) {
                            //< .5 green .5 | threshold = yellow | >= threshold = red
                            var percent = Math.round((data.totalErrorRows / data.totalRecordCount) * 100);
                            var thresholdHalf = Math.round((data.threshold / 2));

                            if(percent < thresholdHalf) {
                                $(row).addClass('table-success');
                                $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                            }
                            else if(percent/data.threshold >= thresholdHalf && percent < data.threshold) {
                                $(row).addClass('table-warning');
                                $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                            }
                            else {
                                $(row).addClass('table-danger');
                                $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                            }
                        }
                        else {
                           $(row).addClass("table-success"); 
                           $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully</b>" + "<br />" + "<b>Record Error Percent:</b> 0%");
                        }
                    }
                    else if(data.statusId == 58 || data.statusId == 7|| data.statusId == 1 || data.statusId == 41 || data.statusId == 39 || data.statusId == 30 || data.statusId == 29) {
                        $(row).addClass('table-danger');
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Failed to Process</b>");
                    }
                    else if(data.errorRecordCount > 0) {
                         //< .5 green .5 | threshold = yellow | >= threshold = red
                        var percent = Math.round((data.totalErrorRows / data.totalRecordCount) * 100);
                        var thresholdHalf = Math.round((data.threshold / 2));

                        if(percent < thresholdHalf) {
                            $(row).addClass('table-success');
                            $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                        }
                        else if(percent/data.threshold >= thresholdHalf && percent < data.threshold) {
                            $(row).addClass('table-warning');
                            $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                        }
                        else {
                            $(row).addClass('table-danger');
                            $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                        }
                    }
                }
            },
            aoColumns: [
                {
                    "mData": "uploadType", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "8%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    }
                },
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
                            
                            var returnData = ''
                            
                            if(row.utBatchName != 'N/A') {
                                returnData = '<a href="/administrator/processing-activity/inbound/'+row.utBatchName+'"><strong>Batch Name: ' + row.utBatchName + '</strong></a><br />';
                                returnData += 'Batch Id: ' + row.id+ '<br />';
                            }
                            else {
                                returnData = '<strong>Batch Name: ' + row.utBatchName + '</strong><br />';
                            }
                            
                            if(row.transportMethodId != 2 && row.utBatchName != 'N/A') {

                                if(row.transportMethodId == 9 || row.transportMethodId == 12 ) {
                                   returnData += '<a href="/FileDownload/downloadFile.do?fromPage=dashboard&filename='+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />';
                                }
                                else if (row.transportMethodId == 6) {
                                    returnData += '<a href="/FileDownload/downloadFile.do?fromPage=dashboard&filename='+row.utBatchName+'_dec.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />';
                                }
                                else if(row.transportMethodId == 13 ) {
                                   returnData += '<a href="/FileDownload/downloadFile.do?fromPage=dashboard&filename=archive_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn&orgId='+row.orgId+'" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />';
                                }
                                else {
                                   returnData += '<a href="/FileDownload/downloadFile.do?fromPage=dashboard&filename=encoded_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=input files&orgId='+row.orgId+'" title="Download Submitted File">Submitted File - '+row.originalFileName+'</a><br />'; 
                                }

                                if(row.originalFileName.split('.')[1].toString().toLowerCase() != 'txt' || row.fileDelimiter == 13) {
                                    if(row.inboundBatchConfigurationType == 1 && (row.transportMethodId == 10 || row.transportMethodId == 13)) {
                                        if(row.transportMethod.indexOf("Direct") > 0 || row.transportMethod === 'File Drop') {
                                            returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=dashboard&filename='+row.utBatchName+'.txt&foldername=loadFiles" title="View Pipe File">Internal File - '+row.utBatchName+'.txt</a><br />';
                                        }
                                        else {
                                            returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=dashboard&filename=archive_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="Download Pipe File">Internal File - '+row.utBatchName+'</a><br />';
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
                        if(data != 'N/A') {
                            return '<a href="#batchInfoModal" data-bs-toggle="modal" class="viewStatus" rel="'+row.statusId+'" title="View this Status">'+data+'</a>'; 
                        }
                        else {
                            return data;
                        }
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
                        
                        if (type === 'display') {
                            return myDateFormatted;
                        }

                        return row.dateAsInteger;
                    }
                }
             ]
        }); 
    }
    
    function getOutboundMessages() {

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

        var fromDate = $('.daterange span').attr('rel');
        var toDate = $('.daterange span').attr('rel2');

        if(fromDate == null) {
            fromDate = $('#fromDate').attr('rel');
        }

        if(toDate == null) {
            toDate = $('#toDate').attr('rel');
        }

        $('#outbounddataTable').DataTable().destroy();

         $('#outbounddataTable').DataTable({
            bServerSide: true,
            bProcessing: true, 
            deferRender: true,
            aaSorting: [[5,'desc']],
            sPaginationType: "bootstrap", 
             drawCallback: function() {
                $('[data-bs-toggle="popover"]').popover();
            },
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
            sAjaxSource: "/administrator/processing-activity/dashboardOutBoundBatches?fromDate="+fromDate+"&toDate="+toDate,
            createdRow: function(row, data, index) {
                $(row).addClass('outboundbatchRow');
                $(row).attr('data-bs-trigger', 'hover');
                $(row).attr('data-bs-toggle', 'popover');
                $(row).attr('data-bs-placement', 'top');
                $(row).attr('data-bs-html', 'true');
                $(row).attr('data-bs-animation', 'false');
                $(row).attr('data-bs-title', 'File Status');

                if(data.statusId == 28) {
                    if(data.totalErrorCount == data.totalRecordCount) {
                        $(row).addClass('table-danger');
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
                    }
                    else if(data.totalErrorCount > 0) {
                       //< .5 green .5 | threshold = yellow | >= threshold = red
                        var percent = Math.round((data.totalErrorRows / data.totalRecordCount) * 100);
                        var thresholdHalf = Math.round((data.threshold / 2));

                        if(percent < thresholdHalf) {
                            $(row).addClass('table-success');
                            $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                        }
                        else if(percent/data.threshold >= thresholdHalf && percent < data.threshold) {
                            $(row).addClass('table-warning');
                            $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                        }
                        else {
                            $(row).addClass('table-danger');
                            $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                        }
                    }
                    else {
                        $(row).addClass("table-success"); 
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully</b>" + "<br />" + "<b>Record Error Percent:</b> 0%");
                    }
                }
                else if(data.statusId == 58 || data.statusId == 7|| data.statusId == 1 || data.statusId == 41 || data.statusId == 39 || data.statusId == 30 || data.statusId == 29) {
                     //< .5 green .5 | threshold = yellow | >= threshold = red
                    var percent = Math.round((data.totalErrorRows / data.totalRecordCount) * 100);
                    var thresholdHalf = Math.round((data.threshold / 2));

                    if(percent < thresholdHalf) {
                        $(row).addClass('table-success');
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                    }
                    else if(percent/data.threshold >= thresholdHalf && percent < data.threshold) {
                        $(row).addClass('table-warning');
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                    }
                    else {
                        $(row).addClass('table-danger');
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully with Errors</b>" + "<br />" + "<b>Record Error Percent: </b>" + percent + '%');
                    }
                }
            },
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
                            returnData = '<a href="/administrator/processing-activity/outbound/'+row.utBatchName+'"><strong>Batch Name: ' + row.utBatchName + '</strong></a><br />';
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
                }
             ]
        }); 
    }
    
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sSortAsc": "tableheader headerSortDown",
        "sSortDesc": "tableheader headerSortUp",
        "sSortable": "tableheader"
    });
        
    
    $.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings)
    {
       return {
            "iStart": oSettings._iDisplayStart,
            "iEnd": oSettings.fnDisplayEnd(),
            "iLength": oSettings._iDisplayLength,
            "iTotal": oSettings.fnRecordsTotal(),
            "iFilteredTotal": oSettings.fnRecordsDisplay(),
            "iPage": Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
            "iTotalPages": Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
        };
    }

    /* Bootstrap style pagination control */
    $.extend($.fn.dataTableExt.oPagination, {
       
        "bootstrap": {
            "fnInit": function (oSettings, nPaging, fnDraw) {
               
                var oLang = oSettings.oLanguage.oPaginate;
                var fnClickHandler = function (e) {
                    e.preventDefault();
                    if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
                        fnDraw(oSettings);
                    }
                };

                $(nPaging).append(
                    '<ul class="pagination pull-right">' +
                    '<li class="prev disabled"><a href="#">&laquo;</a></li>' +
                    '<li class="next disabled"><a href="#">&raquo;</a></li>' +
                    '</ul>'
                );
                var els = $('a', nPaging);
                $(els[0]).bind('click.DT', {action: "previous"}, fnClickHandler);
                $(els[1]).bind('click.DT', {action: "next"}, fnClickHandler);
            },
            "fnUpdate": function (oSettings, fnDraw) {
                var iListLength = 5;
                var oPaging = oSettings.oInstance.fnPagingInfo();
                var an = oSettings.aanFeatures.p;
                var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);
                
                if (oPaging.iTotalPages < iListLength) {
                    iStart = 1;
                    iEnd = oPaging.iTotalPages;
                } else if (oPaging.iPage <= iHalf) {
                    iStart = 1;
                    iEnd = iListLength;
                } else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
                    iStart = oPaging.iTotalPages - iListLength + 1;
                    iEnd = oPaging.iTotalPages;
                } else {
                    iStart = oPaging.iPage - iHalf + 1;
                    iEnd = iStart + iListLength - 1;
                }
                
                for (i = 0, iLen = an.length; i < iLen; i++) {
                    // Remove the middle elements
                    $('li:gt(0)', an[i]).filter(':not(:last)').remove();

                    // Add the new list items and their event handlers
                    for (j = iStart; j <= iEnd; j++) {
                        sClass = (j == oPaging.iPage + 1) ? 'class="active"' : '';
                        $('<li ' + sClass + '><a href="#">' + j + '</a></li>')
                        .insertBefore($('li:last', an[i])[0])
                        .bind('click', function (e) {
                            e.preventDefault();
                            oSettings._iDisplayStart = (parseInt($('a', this).text(), 10) - 1) * oPaging.iLength;
                            fnDraw(oSettings);
                        });
                    }

                    // Add / remove disabled classes from the static elements
                    if (oPaging.iPage === 0) {
                        $('li:first', an[i]).addClass('disabled');
                    } else {
                        $('li:first', an[i]).removeClass('disabled');
                    }

                    if (oPaging.iPage === oPaging.iTotalPages - 1 || oPaging.iTotalPages === 0) {
                        $('li:last', an[i]).addClass('disabled');
                    } else {
                        $('li:last', an[i]).removeClass('disabled');
                    }
                }
            }
        }
    });

    function commaSeparateNumber(val){
        while (/(\d+)(\d{3})/.test(val.toString())){
          val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
        }
        return val;
    }
});