/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
        
    $("input:text,form").attr("autocomplete", "off");

    //This function will launch the soap message overlay with the select wsId 
    $(document).on('click', '.viewSoapMessage', function(event) {
            var wsId = $(this).attr('rel'); 
        $.ajax({
            url: 'wsmessage/viewSoapMessage.do',
            type: "POST",
            data: {'wsId': wsId},
            success: function(data) {
                $("#soapModal").html(data);
            }
        });
    });

  //This function will launch the soap message overlay with the select wsId 
    $(document).on('click', '.viewSoapResponse', function(event) {
            var wsId = $(this).attr('rel'); 
        $.ajax({
            url: 'wsmessage/viewSoapResponse.do',
            type: "POST",
            data: {'wsId': wsId},
            success: function(data) {
                $("#soapModal").html(data);
            }
        });
    });

    //This function will launch the soap message overlay with the select wsId 
    $(document).on('click', '.viewBtachDLDetail', function(event) {
            var batchId = $(this).attr('rel'); 
        $.ajax({
            url: 'viewBatchDLDetails.do',
            type: "POST",
            data: {'batchId': batchId},
            success: function(data) {
                $("#soapModal").html(data);
            }
        });
    });

    //This will change between inbound and outbound
    $(document).on('change', '#wsDirection', function(event) {
        window.location.href = "/administrator/processing-activity/invalidIn";  
    });

    var searchTerm = $('#invalidOutbound-table').attr('term');

    $('#invalidOutbound-table').DataTable().destroy();

    $('#invalidOutbound-table').DataTable({
        bServerSide: false,
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
	}
    });
    
    $(document).ready(function() {
         var isDST = $('#DTS').val();
         
         if(isDST === '') {
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
               //isDST = 1;
            }
            $('#DTS').val(isDST);
            searchByDateRange();
         }
    });
        
});


function searchByDateRange() {
   var fromDate = $('.daterange span').attr('rel');
   var toDate = $('.daterange span').attr('rel2');
   
   if(!fromDate) {
       fromDate = $('#fromDate').attr('rel');
   }
   
   if(!toDate) {
       toDate = $('#toDate').attr('rel');
   }
    
   $('#fromDate').val(fromDate);
   $('#toDate').val(toDate);
   
   $('body').overlay({
        glyphicon : 'floppy-disk',
        message : 'Processing...'
    });
   
   $('#searchForm').submit();

}
