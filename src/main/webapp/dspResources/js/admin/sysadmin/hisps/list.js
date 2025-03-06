
jQuery(function ($) {
    
    $(document).ready(function () {
        
        $('#hispsListtable').DataTable({
            bAutoWidth: false,
            bStateSave: false,
            aaSorting: [[0,'asc']],
            "oLanguage": {
                "sSearch": "_INPUT_",
                sSearchPlaceholder: 'Filter System Macros',
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
                { "sWidth": "30%" },
                { "sWidth": "55%" },
                { "sWidth": "10%" },
                { "sWidth": "5%","bSortable":false }
            ]
        });

        //This function will launch the new HISP overlay with a blank screen
        $(document).on('click', '.createNewHisp', function () {

            $.ajax({
                url: '/administrator/sysadmin/hisps/create',
                type: "GET",
                success: function (data) {
                    $("#hispModalContent").html(data);
                }
            });
        });

        //This function will launch the edit HISP overlay populating the fields
        $(document).on('click', '.hispEdit', function () {

            var hispDetailsAction = "/administrator/sysadmin/hisps/view?i=" + $(this).attr('rel');

            $.ajax({
                url: hispDetailsAction,
                type: "GET",
                success: function (data) {
                    $("#hispModalContent").html(data);
                }
            });
        });


        $(document).on('click', '#submitButton', function (event) {
            var formData = $("#hispform").serialize();
            var actionValue = "/administrator/sysadmin/hisps/" + $(this).attr('rel').toLowerCase();

            $.ajax({
                url: actionValue,
                data: formData,
                type: "POST",
                async: false,
                success: function (data) {

                    if (data.indexOf('hispUpdated') != -1) {
                        var goToUrl = "/administrator/sysadmin/hisps?msg=updated";
                        window.location.href = goToUrl;
                    } else if (data.indexOf('hispCreated') != -1) {
                        var goToUrl = "/administrator/sysadmin/hisps?msg=created";
                        window.location.href = goToUrl;
                    } else {
                        $("#hispModalContent").html(data);
                    }
                }
            });
        });
     });    
});