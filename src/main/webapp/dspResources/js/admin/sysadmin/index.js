
require(['./main'], function () {

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(5000);
    }

    $("input:text,form").attr("autocomplete", "off");
});



