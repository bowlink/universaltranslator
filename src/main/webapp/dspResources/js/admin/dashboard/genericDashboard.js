
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
                success: function(data) {
                    window.location.reload();
                }
            });

        });
    });
});
