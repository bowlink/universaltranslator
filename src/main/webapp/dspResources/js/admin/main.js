

jQuery(function ($) {
    
    $(document).ready(function () {
        
        // Log out confirmation
        $('.logout').on('click', function (e) {
            var confirmed = confirm('Are you sure you want to log out?');
            if (confirmed) {
                return true;
            } 
            else{
                e.preventDefault();
                return false;
            }
        });
        
    });
});