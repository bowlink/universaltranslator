
jQuery(function ($) {
    
    $(document).ready(function () {
        
        $('#macroListtable').DataTable({
            bAutoWidth: false,
            bStateSave: false,
            aaSorting: [[1,'asc']],
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
                { "sWidth": "25%" },
                { "sWidth": "30%" },
                { "sWidth": "30%" },
                { "sWidth": "5%" },
            ]
        });
    
        $(document).on('click','.printMacros',function() {
           $('body').overlay({
               glyphicon : 'print',
               message : 'Gathering Details...'
           });
           $('.overlay').show();

           $.ajax({
                url: '/administrator/sysadmin/createMacroExcelFile.do',
                data: {},
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    $('.overlay').hide();
                    if(data !== '') {
                        window.location.href = '/administrator/sysadmin/printMacroExcelFile/'+ data;
                    }
                    else {
                        alert("An error occurred creating the macro list file. A Health-e-Link system administrator has been notified.");
                    }
                }
            });
       });

        //This function will remove the macro is clicked 
        $(document).on('click', '.marcoDelete', function () {
            var confirmed = confirm("Are you sure you want to remove this macro?");
            if (confirmed) {
                var id = $(this).attr('rel');
                window.location.href = "/administrator/sysadmin/macros/delete?i=" + id;
            }
        });

        //This function will launch the new macro overlay with a blank screen
        $(document).on('click', '.createNewMacro', function () {

            $.ajax({
                url: '/administrator/sysadmin/macros/create',
                type: "GET",
                success: function (data) {
                    $("#macroModalContent").html(data);
                }
            });
        });

        //This function will launch the edit macro overlay populating the fields
        $(document).on('click', '.macroEdit', function () {

            var macroDetailsAction = "/administrator/sysadmin/macros/view?i=" + $(this).attr('rel');

            $.ajax({
                url: macroDetailsAction,
                type: "GET",
                success: function (data) {
                    $("#macroModalContent").html(data);
                }
            });
         });
         
         $(document).on('click', '#submitButton', function (event) {
            var formData = $("#macroform").serialize();
            var actionValue = "macros/" + $(this).attr('rel').toLowerCase();
            
            $.ajax({
                url: actionValue,
                data: formData,
                type: "POST",
                async: false,
                success: function (data) {
                    
                    if (data.indexOf('macroUpdated') != -1) {
                        var goToUrl = "macros?msg=updated";
                        window.location.href = goToUrl;
                    } 
                    else if (data.indexOf('macroCreated') != -1) {
                        var goToUrl = "macros?msg=created";
                        window.location.href = goToUrl;
                    } 
                    else {
                        $("#macroModalContent").html(data);
                    }
                }
            });
            event.preventDefault();
            return false;
        });

        //This function will remove the macro is clicked 
        $(document).on('click', '.runTestMacroFile', function () {

            $.ajax({
                url: "/administrator/sysadmin/macros/runTestFile",
                type: "POST",
                async: false,
                success: function (data) {
                    window.location.href = "/administrator/processing-activity/inbound";
                }

            });
            event.preventDefault();
            return false;
        });
    }); 
});