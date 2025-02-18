/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


jQuery(function ($) {
    
    $(document).ready(function () {

        getOrganizations();

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        $("input:text,form").attr("autocomplete", "off");

        $(document).on('click', '.orgRow', function () {
            window.location.href = $(this).attr('rel') + '/';
        });

        $('#searchOrgBtn').click(function () {
            $('#searchForm').submit();
        });
    });
    
    function getOrganizations() {

        $('#organization-table').DataTable().destroy();
        
        $('#organization-table').DataTable({
            bProcessing: true,
            bServerSide: true,
            deferRender: true,
            aaSorting: [[0,'desc']],
            sPaginationType: "bootstrap", 
            oLanguage: {
               sSearch: "_INPUT_",
               sSearchPlaceholder: 'Filter Organizations',
               sLengthMenu: '<select class="form-control" style="width:150px">' +
                    '<option value="10">10 Records</option>' +
                    '<option value="20">20 Records</option>' +
                    '<option value="30">30 Records</option>' +
                    '<option value="40">40 Records</option>' +
                    '<option value="50">50 Records</option>' +
                    '<option value="-1">All</option>' +
                    '</select>',
                sProcessing: "<div style='background-color:#64A5D4; text-align:center; width:100%; height:50px; margin-top:100px; position:absolute'><p style='color:white; font-weight:bold; padding-top:15px;' class='bolder'>Retrieving Results. Please wait...</p></div>"
            },
            sAjaxSource: "/administrator/organizations/ajax/getOrganizations",
            aoColumns: [
                {
                    "mData": "id", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "5%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    },
                    'createdCell':  function (td, cellData, rowData, row, col) {
                        $(td).attr('rel', rowData.cleanURL); 
                        $(td).addClass('orgRow');
                     }
                },
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "20%",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    },
                    'createdCell':  function (td, cellData, rowData, row, col) {
                        $(td).attr('rel', rowData.cleanURL); 
                        $(td).addClass('orgRow');
                     }
                },
                {
                    "mData": "helRegistry", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "15%",
                    "render": function ( data, type, row, meta ) {
                        return data;
                    },
                    'createdCell':  function (td, cellData, rowData, row, col) {
                        $(td).attr('rel', rowData.cleanURL);
                        $(td).addClass('orgRow');
                     }
                },
                {
                    "mData": "address", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "20%",
                    "render": function ( data, type, row, meta ) {
                        var contactInfo;

                        if(data == '') {
                            contactInfo = "N/A";
                        }
                        else {
                            contactInfo = data;

                            if(row.address2 !== '') {
                                contactInfo = contactInfo + '<br />' + row.address2;
                            }
                            if(row.city !== '') {
                                contactInfo = contactInfo + '<br />' + row.city;
                            }
                            if(row.state !== '') {
                                contactInfo = contactInfo + '&nbsp;' + row.state;
                            }
                            if(row.postalCode !== '') {
                                contactInfo = contactInfo + '&nbsp;' + row.postalCode;
                            }
                        }
                        return contactInfo;
                    },
                    'createdCell':  function (td, cellData, rowData, row, col) {
                        $(td).attr('rel', rowData.cleanURL); 
                        $(td).addClass('orgRow');
                     }
                },
                {
                    "mData": "dateCreated", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        var dateC = new Date(row.dateCreated);
                        var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear();
                        return myDateFormatted;
                    },
                    'createdCell':  function (td, cellData, rowData, row, col) {
                        $(td).attr('rel', rowData.cleanURL); 
                        $(td).addClass('orgRow');
                     }
                },
                {
                    "mData": "orgName", 
                    "defaultContent": "",
                    "bSortable":true,
                    "sWidth": "10%",
                    "className": "center-text",
                    "render": function ( data, type, row, meta ) {
                        var editLink = '<a href="#" class="btn btn-link" title="Edit this organization" role="button"><span class="glyphicon glyphicon-edit"></span> Edit</a>';
                        return editLink;
                    },
                    'createdCell':  function (td, cellData, rowData, row, col) {
                        $(td).attr('rel', rowData.cleanURL); 
                        $(td).addClass('orgRow');
                     }
                }
             ]
        }); 
    }
    
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sSortAsc": "tableheader headerSortDown",
        "sSortDesc": "tableheader headerSortUp",
        "sSortable": "tableheader"
    });


    $.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings)
    {
       return {
            "iStart": oSettings._iDisplayStart,
            "iEnd": oSettings.fnDisplayEnd(),
            "iLength": oSettings._iDisplayLength,
            "iTotal": oSettings.fnRecordsTotal(),
            "iFilteredTotal": oSettings.fnRecordsDisplay(),
            "iPage": Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
            "iTotalPages": Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
        };
    }

    /* Bootstrap style pagination control */
    $.extend($.fn.dataTableExt.oPagination, {

        "bootstrap": {
            "fnInit": function (oSettings, nPaging, fnDraw) {

                var oLang = oSettings.oLanguage.oPaginate;
                var fnClickHandler = function (e) {
                    e.preventDefault();
                    if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
                        fnDraw(oSettings);
                    }
                };

                $(nPaging).append(
                    '<ul class="pagination pull-right">' +
                    '<li class="prev disabled"><a href="#">&laquo;</a></li>' +
                    '<li class="next disabled"><a href="#">&raquo;</a></li>' +
                    '</ul>'
                );
                var els = $('a', nPaging);
                $(els[0]).bind('click.DT', {action: "previous"}, fnClickHandler);
                $(els[1]).bind('click.DT', {action: "next"}, fnClickHandler);
            },
            "fnUpdate": function (oSettings, fnDraw) {
                var iListLength = 5;
                var oPaging = oSettings.oInstance.fnPagingInfo();
                var an = oSettings.aanFeatures.p;
                var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);

                if (oPaging.iTotalPages < iListLength) {
                    iStart = 1;
                    iEnd = oPaging.iTotalPages;
                } else if (oPaging.iPage <= iHalf) {
                    iStart = 1;
                    iEnd = iListLength;
                } else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
                    iStart = oPaging.iTotalPages - iListLength + 1;
                    iEnd = oPaging.iTotalPages;
                } else {
                    iStart = oPaging.iPage - iHalf + 1;
                    iEnd = iStart + iListLength - 1;
                }

                for (i = 0, iLen = an.length; i < iLen; i++) {
                    // Remove the middle elements
                    $('li:gt(0)', an[i]).filter(':not(:last)').remove();

                    // Add the new list items and their event handlers
                    for (j = iStart; j <= iEnd; j++) {
                        sClass = (j == oPaging.iPage + 1) ? 'class="active"' : '';
                        $('<li ' + sClass + '><a href="#">' + j + '</a></li>')
                        .insertBefore($('li:last', an[i])[0])
                        .bind('click', function (e) {
                            e.preventDefault();
                            oSettings._iDisplayStart = (parseInt($('a', this).text(), 10) - 1) * oPaging.iLength;
                            fnDraw(oSettings);
                        });
                    }

                    // Add / remove disabled classes from the static elements
                    if (oPaging.iPage === 0) {
                        $('li:first', an[i]).addClass('disabled');
                    } else {
                        $('li:first', an[i]).removeClass('disabled');
                    }

                    if (oPaging.iPage === oPaging.iTotalPages - 1 || oPaging.iTotalPages === 0) {
                        $('li:last', an[i]).addClass('disabled');
                    } else {
                        $('li:last', an[i]).removeClass('disabled');
                    }
                }
            }
        }
    });
});