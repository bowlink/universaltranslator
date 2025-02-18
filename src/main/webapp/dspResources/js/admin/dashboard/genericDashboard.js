
jQuery(function ($) {
    
    $(document).ready(function () {
        $(document).on('click', '.complateGenericEntry', function () {
            var entryId = $(this).attr('rel');

            if(this.checked) {
                $("#genericRow_"+entryId).removeClass("table-primary");
                $("#genericRow_"+entryId).addClass("table-success");
            }
            else {
                $("#genericRow_"+entryId).removeClass("table-success");
                $("#genericRow_"+entryId).addClass("table-primary");
            }

            $.ajax({
                url: '/administrator/processing-activity/completeGenericWatchList',
                data: {
                    'entryId': entryId, 
                    'isChecked': this.checked
                },
                type: "POST",
                success: function(data) {}
            });

        });

        $('#genericdataTable').dataTable({
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
    });
});
