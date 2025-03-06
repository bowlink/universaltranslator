/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery(function ($) {
    
    $(document).ready(function () {
        
        $('#badfilepathtable').DataTable({
            bAutoWidth: false,
            bStateSave: false,
            aaSorting: [[1,'desc']],
            "oLanguage": {
                "sSearch": "_INPUT_",
                sSearchPlaceholder: 'Filter Bad File Paths',
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
                { "sWidth": "45%" },
                { "sWidth": "20%" },
                { "sWidth": "20%" },
                { "sWidth": "15%","bSortable":false }
            ]
        });

        //Function to delete the message 
        $(document).on('click', '.deleteFilePath', function() {

            var confirmed = confirm("Are you sure you want to delete this path?");
            
            if (confirmed) {

                var pathId = $(this).attr('rel');

                $.ajax({
                    url: '/administrator/sysadmin/moveFilePaths',
                    type: 'POST',
                    data: {
                        'pathId': pathId
                    },
                    success: function(data) {
                        window.location.href='/administrator/sysadmin/badFilePaths?msg=deleted'
                    }
                });
            }
        });
    });
});