/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
	
    //setInterval(function(){searchByDateRange()}, 30000);

    $("input:text,form").attr("autocomplete", "off");
    
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

    //This function will launch the status detail overlay with the selected
    //status
    $(document).on('click', '.viewStatus', function () {
        $.ajax({
            url: '/administrator/processing-activity/viewStatus' + $(this).attr('rel'),
            type: "GET",
            success: function (data) {
                $("#statusModal").html(data);
            }
        });
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

    $(document).ready(function() {

        var fromDate = $('#fromDate').attr('rel');
        var toDate = $('#toDate').attr('rel');

        populateMessages(fromDate,toDate);

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
    
    var searchTerm = $('#batchuploads-table').attr('term');
    
    $('#batchuploads-table').DataTable().destroy();
    
    $('#batchuploads-table').DataTable({
	bServerSide: true,
	bProcessing: true, 
	deferRender: true,
	aaSorting: [[5,'desc']],
        "oSearch": {"sSearch": searchTerm },
	sPaginationType: "bootstrap", 
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
	sAjaxSource: "/administrator/processing-activity/ajax/getBatchUploads?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
	aoColumns: [
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
		"mData": "configName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "20%",
		"render": function ( data, type, row, meta ) {
		    var returnData = '';
		    if(data !== '') {
			returnData = '<strong>'+data+'</strong><br />';
		    }
		    else {
			returnData = '<strong>Invalid File</strong><br />';
		    }
		    returnData += row.utBatchName;
		    
		    if(row.transportMethodId != 2) {
		    
			if(row.transportMethodId == 9 || row.transportMethodId == 12 ) {
			   returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename='+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="View Submitted File">Submitted File - '+row.originalFileName+'</a>';
			}
			else if (row.transportMethodId == 6) {
			    returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename='+row.utBatchName+'_dec.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="View Submitted File">Submitted File - '+row.originalFileName+'</a>';
			}
                        else if(row.transportMethodId == 13 ) {
			   returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename=archive_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn&orgId='+row.orgId+'" title="View Submitted File">Submitted File - '+row.originalFileName+'</a>';
			}
			else {
			   returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename=encoded_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=input files&orgId='+row.orgId+'" title="View Submitted File">Submitted File - '+row.originalFileName+'</a>'; 
			}
			
                        if(row.originalFileName.split('.')[1].toString().toLowerCase() != 'txt' || row.fileDelimiter == 13) {
                            if(row.inboundBatchConfigurationType == 1 && (row.transportMethodId == 10 || row.transportMethodId == 13)) {
                                if(row.transportMethod.indexOf("Direct") > 0 || row.transportMethod === 'File Drop') {
                                    returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename='+row.utBatchName+'.txt&foldername=loadFiles" title="View Pipe File">Internal File - '+row.utBatchName+'.txt</a>';
                                }
                                else {
                                    returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=inbound&filename=archive_'+row.utBatchName+'.'+row.originalFileName.split('.')[1].toString().toLowerCase()+'&foldername=archivesIn" title="View Pipe File">Internal File - '+row.utBatchName+'</a>';
                                }
                            }
                        }
		    }
                    return returnData;
		}
	    },
	    {
		"mData": "transportMethod", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "18%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    if(data.indexOf("Rest") > 0) {
			return '<a href="/administrator/processing-activity/apimessages/'+row.utBatchName+'" title="View Rest API Message">'+data+'</a>';
		    }
		    else if(data.indexOf("Direct") > 0) {
			return '<a href="#directModal" data-toggle="modal" class="viewDirectDetails" rel="'+row.id+'" title="View Direct Message Details">File Drop (Direct)</a>';
		    }
		    else {
			return data;
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
		   return '<a href="#statusModal" data-toggle="modal" class="viewStatus" rel="'+row.statusId+'" title="View this Status">'+data+'</a>';
		}
	    },
	    {
		"mData": "totalRecordCount", 
		"defaultContent": "",
		"bSortable":false,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
		    var returnData = 'Total Transactions: <strong>';
		    returnData += commaSeparateNumber(data) + '</strong><br />';
		    returnData += 'Total Errors: <strong>'+ commaSeparateNumber(row.errorRecordCount) + '</strong>';
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
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text actions-col",
		"render": function ( data, type, row, meta ) {
		   var returnData = '<div class="dropdown pull-left"><button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown"><i class="fa fa-cog"></i></button><ul class="dropdown-menu pull-right">';
		   
		    if(row.statusValue === 'MAN') {
                       returnData += '<li><a href="#!" id="release" class="releaseBatch" rel="releaseBatch" rel2="'+row.id+'" title="Process Inbound Batch"><span class="glyphicon glyphicon-ok-sign"></span></span> Process Inbound Batch</a></li>';
		       returnData += '<li class="divider"></li>';
                    }
                    
                    if(data != 2) {
		       returnData += '<li><a href="/administrator/processing-activity/inbound/batchActivities/'+row.utBatchName+'" class="viewBatchActivities" title="View Batch Activities"><span class="glyphicon glyphicon-edit"></span> View Batch Activities</a></li>';
		       returnData += '<li class="divider"></li>';
		       returnData += '<li><a href="/administrator/processing-activity/inbound/auditReport/'+row.utBatchName+'" title="View Audit Report"><span class="glyphicon glyphicon-edit"></span> View Audit Report</a></li>';
		   }
		   
		   if(userRole == 1) {
		       returnData += '<li class="divider"></li>';
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
