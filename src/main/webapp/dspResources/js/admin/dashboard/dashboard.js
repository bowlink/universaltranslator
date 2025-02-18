/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery(function ($) {
    
    $(document).ready(function () {
        
        getGenericMessages();
        getInboundMessages();
        getOutboundMessages();
        setInterval(function(){getGenericMessages()}, 50000);
        setInterval(function(){getInboundMessages()}, 50000);
        setInterval(function(){getOutboundMessages()}, 50000);

        $("input:text,form").attr("autocomplete", "off");

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
            type: "POST",
            success: function(data) {
               data = $(data);
               $('.genericMessages').html(data);
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
        
        $('#inbounddataTable').DataTable().destroy();
        
        $('#inbounddataTable').DataTable({
            bProcessing: true,
            bServerSide: true,
            deferRender: true,
            aaSorting: [[1,'desc']],
            sPaginationType: "bootstrap", 
            fnDrawCallback: function() {
                $('[data-bs-toggle="popover"]').popover();
            },
            oLanguage: {
               sEmptyTable: "There were no files submitted for the selected date range.", 
               sSearch: "Filter Results: ",
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
                    "mData": "dateSubmitted", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "13%",
                    "render": function ( data, type, row, meta ) {
                        var dateC = new Date(data);
                        var minutes = dateC.getMinutes();
                        var hours = dateC.getHours();
                        var ampm =  hours >= 12 ? 'pm' : 'am';
                        hours = hours % 12;
                        hours = hours ? hours : 12;
                        minutes = minutes < 10 ? '0'+minutes : minutes;
                        var myDateFormatted = 'Received: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;

                        if(row.startDateTime != null) {
                            var dateS = new Date(row.startDateTime);
                            minutes = dateS.getMinutes();
                            if(isDST == 1) {
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
                            }
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
                            if(isDST == 1) {
                                hours = dateC.getHours()-1;
                                if(hours < 0) {
                                    hours = 11;
                                }
                                else if(hours == 0) {
                                    hours = 12;
                                }
                            }
                            else {
                                hours = dateC.getHours();
                            }
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
                    "mData": "utBatchName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '';

                        if(row.uploadType === 'Watch List Entry') {
                            returnData = 'N/A';
                        }
                        else {
                            returnData = '<a href="/administrator/processing-activity/inbound/'+data+'" class="dashboard-link" title="View Inbound Batch" role="button">'+data+'</a>';
                        }
                        return returnData;
                    }
                },
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "15%",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    }
                },
                {
                    "mData": "configName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "20%",
                    "render": function ( data, type, row, meta ) {
                       return data;
                    }
                },
                {
                    "mData": "transportMethod", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    }
                },
                {
                    "mData": "totalRecordCount", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '';

                        if(row.uploadType === 'Watch List Entry') {
                            returnData = 'N/A';
                        }
                        else {
                            returnData = commaSeparateNumber(data);
                        }
                        return returnData;
                    }
                },
                {
                    "mData": "errorRecordCount", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        var returnData = '';

                        if(row.uploadType === 'Watch List Entry') {
                            returnData = 'N/A';
                        }
                        else {
                            returnData = commaSeparateNumber(data);
                        }
                        return returnData;
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
            aaSorting: [[1,'desc']],
            sPaginationType: "bootstrap", 
             drawCallback: function() {
                $('[data-bs-toggle="popover"]').popover();
            },
            oLanguage: {
               sEmptyTable: "There were no files sent out for the selected date range.", 
               sSearch: "Filter Results: ",
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
                    if(data.errorRecordCount == data.totalRecordCount) {
                        $(row).addClass('table-danger');
                        $(row).attr('data-bs-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
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
                    "mData": "id", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "8%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        return "On Demand";
                    }
                },
                {
                    "mData": "dateCreated", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "12%",
                    "render": function ( data, type, row, meta ) {
                        var dateC = new Date(data);
                        var minutes = dateC.getMinutes();
                        var hours = dateC.getHours();
                        var ampm =  hours >= 12 ? 'pm' : 'am';
                        hours = hours % 12;
                        hours = hours ? hours : 12;
                        minutes = minutes < 10 ? '0'+minutes : minutes;

                        var myDateFormatted = '';



                        if(row.startDateTime != null) {
                            dateC = new Date(row.startDateTime);
                            minutes = dateC.getMinutes();
                            if(isDST == 1) {
                                hours = dateC.getHours()-1;
                                if(hours < 0) {
                                    hours = 11;
                                }
                                else if(hours == 0) {
                                    hours = 12;
                                }
                            }
                            else {
                                hours = dateC.getHours();
                            }
                            ampm =  hours >= 12 ? 'pm' : 'am';
                            hours = hours % 12;
                            hours = hours ? hours : 12;
                            minutes = minutes < 10 ? '0'+minutes : minutes;

                            myDateFormatted += 'Start: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                        }

                        if(row.endDateTime != null) {
                            dateC = new Date(row.endDateTime);
                            minutes = dateC.getMinutes();
                            if(isDST == 1) {
                                hours = dateC.getHours()-1;
                                if(hours < 0) {
                                    hours = 11;
                                }
                                else if(hours == 0) {
                                    hours = 12;
                                }
                            }
                            else {
                                hours = dateC.getHours();
                            }
                            ampm =  hours >= 12 ? 'pm' : 'am';
                            hours = hours % 12;
                            hours = hours ? hours : 12;
                            minutes = minutes < 10 ? '0'+minutes : minutes;

                            myDateFormatted += '<br />End: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                        }

                         myDateFormatted += '<br />Sent: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;


                        return myDateFormatted;
                    }
                },
                {
                    "mData": "utBatchName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "render": function ( data, type, row, meta ) {
                        return '<a href="/administrator/processing-activity/outbound/'+row.utBatchName+'" class="dashboard-link" title="View Outbound Batch" role="button">'+row.utBatchName+'</a>';
                    }
                },
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "15%",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    }
                },
                {
                    "mData": "configName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "20%",
                    "render": function ( data, type, row, meta ) {
                       return data;
                    }
                },
                {
                    "mData": "transportMethod", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    }
                },
                {
                    "mData": "totalRecordCount", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        return commaSeparateNumber(data);
                    }
                },
                {
                    "mData": "errorRecordCount", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                         return commaSeparateNumber(data);
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