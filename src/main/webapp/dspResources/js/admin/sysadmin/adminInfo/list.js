
jQuery(function ($) {
    
    $(document).ready(function () {
        
        $('#sysadmindataTable').DataTable({
            bAutoWidth: false,
            bStateSave: false,
            aaSorting: [[3,'desc']],
            "oLanguage": {
                "sSearch": "_INPUT_",
                sSearchPlaceholder: 'Filter System Administrators',
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
                { "sWidth": "15%" },
                { "sWidth": "15%" },
                { "sWidth": "5%", "bSortable":false }
            ]
        });
    
        //This function will launch the new dataItem overlay with a blank screen
        $(document).on('click', '.administratorLogins', function () {

            $.ajax({
                url: '/administrator/sysadmin/systemAdminLogins',
                data: {
                    'adminId': $(this).attr('rel')
                },
                type: "GET",
                success: function (data) {
                    $("#adminModalContent").html(data);
                    
                    $("#adminModalContent").find('#loginDataTable').dataTable({
                        "bAutoWidth": false,
                        "bStateSave": false,
                        "bLengthChange": false,
                        "sDom": '<"leftcolumn"><"rightcolumn"<"H"lfr>t><"bottombar"<"F"ip>>',
                        "oLanguage": {
                            "sSearch": "_INPUT_",
                            sSearchPlaceholder: 'Filter Logins',
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
                            { "sWidth": "50%", "sType": "date" },
                            { "sWidth": "50%"}
                        ],
                       "aaSorting" : [[0, "desc"]]
                    });
                }
            });
        });

        //This function will launch the new dataItem overlay with a blank screen
        $(document).on('click', '.createNewSystemAdministrator', function () {

            $.ajax({
                url: '/administrator/sysadmin/adminInfo',
                data: {
                    'adminId': 0
                },
                type: "GET",
                success: function (data) {
                    $("#adminModalContent").html(data);
                }
            });
        });

        //This function will launch the new dataItem overlay with a blank screen
        $(document).on('click', '.administratorEdit', function () {

            $.ajax({
                url: '/administrator/sysadmin/adminInfo',
                data: {
                    'adminId': $(this).attr('rel')
                },
                type: "GET",
                success: function (data) {
                    $("#adminModalContent").html(data);
                }
            });
        });

        //Function to submit the changes to an admin user
        $(document).on('click', '#submitButton', function (event) {
            var buttonVal = $(this).attr('rel');
            
            var passwordVal = $('#newPassword').val();
            var confirmPasswordVal = $('#confirmPassword').val();
            var firstName = $('#firstName').val();
            var lastName = $('#lastName').val();
            var username = $('#username').val();
            var email = $('#email').val();
            var proceed = true;

            $('div.form-group').removeClass("has-error");
            $('span.control-label').removeClass("has-error");
            $('span.control-label').html("");

            if (firstName.trim() === '') {
                $('#firstNameDiv').addClass("has-error");
                $('#firstNameMsg').addClass("has-error");
                $('#firstNameMsg').html('First name is required.');
                event.preventDefault();
                proceed = false;
            }

            if (lastName.trim() === '') {
                $('#lastNameDiv').addClass("has-error");
                $('#lastNameMsg').addClass("has-error");
                $('#lastNameMsg').html('Last name is required.');
                event.preventDefault();
                proceed = false;
            }

            if (username.trim() === '') {
                $('#usernameDiv').addClass("has-error");
                $('#usernameMsg').addClass("has-error");
                $('#usernameMsg').html('Username is required.');
                event.preventDefault();
                proceed = false;
            }

            if (!isEmail(email)) {
                $('#emailDiv').addClass("has-error");
                $('#emailMsg').addClass("has-error");
                $('#emailMsg').html('Please enter a valid email.');
                event.preventDefault();
                proceed = false;
            }

            if (passwordVal !== confirmPasswordVal) {
                $('#confirmPasswordDiv').addClass("has-error");
                $('#newPasswordDiv').addClass("has-error");
                $('#confimPasswordMsg').addClass("has-error");
                $('#confimPasswordMsg').html('The two passwords do not match.');
                event.preventDefault();
                proceed = false;
            }

            if (proceed) {
                var formData = $("#userdetailsform").serialize();

                var actionValue = 'adminInfo';

                $.ajax({
                    url: actionValue,
                    data: formData,
                    type: "POST",
                    async: false,
                    success: function (data) {
                        $("#adminModalContent").html(data);
                    }
                });
                event.preventDefault();
                return false;
            }
        });

        function isEmail(email) {
            var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            return regex.test(email);
        }
    });
});