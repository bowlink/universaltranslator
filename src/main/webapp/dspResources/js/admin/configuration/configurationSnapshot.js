

function printBeforeSnapshot(configId, formName, module, callback) {
    
    $.ajax({
        url: '/administrator/configurations/createConfigPrintPDFForSnapshot.do',
        data: {
            'configId': configId,
            'snapShotType': 'before',
            'module': module
        },
        type: "GET",
        dataType : 'text',
        contentType : 'application/json;charset=UTF-8',
        success: function(data) {
            if(data !== '') {
                
                if (formName === 'formFields') {
                    var formData = $("#"+formName).serialize();

                    $.ajax({
                        url: '/administrator/configurations/saveFields',
                        data: formData,
                        type: "POST",
                        async: false,
                        success: function (data) {
                            $('.fieldsUpdated').show();
                            $('.alert').delay(2000).fadeOut(1000);
                            $('.overlay').remove();
                            printAfterSnapshot(configId,module);
                        }
                    });
                    event.preventDefault();
                    return false;
                }
                else if (formName === 'formFieldsNext') {
                    var formData = $("#formFields").serialize();

                    $.ajax({
                        url: '/administrator/configurations/saveFields',
                        data: formData,
                        type: "POST",
                        async: false,
                        success: function (data) {
                            window.location.href = 'translations?savedStatus=fieldsupdated';
                        }
                    });
                    event.preventDefault();
                    return false;
                }
                else if (typeof callback === "function") {
                    callback();
                }
                else {
                    //File created no need to call save
                    $("#"+formName).submit();
                }
            }
            else {
                $('#errorMsg').show();
            }
        }
    });
}

function printAfterSnapshot(configId, module) {
    
    $.ajax({
        url: '/administrator/configurations/createConfigPrintPDFForSnapshot.do',
        data: {
            'configId': configId,
            'snapShotType': 'after',
            'module': module
        },
        type: "GET",
        dataType : 'text',
        contentType : 'application/json;charset=UTF-8',
        success: function(data) {
            if(data !== '') {
            }
            else {
                $('#errorMsg').show();
            }
        }
    });
}
