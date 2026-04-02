


jQuery(function ($) {
    
    $(document).ready(function () {
      
        $('#auditLogs').dataTable({
           "bAutoWidth": false,
            "bStateSave": false,
            "bLengthChange": true,
            "oLanguage": {
                "sSearch": "_INPUT_",
                sSearchPlaceholder: 'Filter Notes',
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
                { "sWidth": "40%", "bSortable":false },
                { "sWidth": "30%", "bSortable":false },
                { "sWidth": "25%", "sType": "date" },
                { "sWidth": "5%", "bSortable":false}
            ],
           "aaSorting" : [[3, "desc"]]
        });
    });
    
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sSortAsc": "tableheader headerSortDown",
        "sSortDesc": "tableheader headerSortUp",
        "sSortable": "tableheader"
    });
});
