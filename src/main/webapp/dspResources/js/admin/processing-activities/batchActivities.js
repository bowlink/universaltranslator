/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



jQuery(function ($) {
    
    $(document).ready(function () {
        $('#batchActivitiesDataTable').DataTable().destroy();

        $('#batchActivitiesDataTable').DataTable({
           bServerSide: false,
           bProcessing: false, 
           aaSorting: [[2,'asc']],
           sPaginationType: "bootstrap", 
           "pageLength": 50,
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
                   '</select>'
           },
           aoColumns: [
               {"width": "5%"},
               {"width": "80%",  "bSortable": false},
               {"width": "15%",  "bSortable": false}
           ]
        });
    });
});
