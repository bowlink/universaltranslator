<%-- 
    Document   : newjspentityCWForm
    Created on : May 7, 2018, 8:30:25 AM
    Author     : chadmccue
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">FTP File Check</h3>
        </div>
        <div class="modal-body">
            <form id="ftpFileCheckForm" method="post" role="form">
                <input type="hidden" id="transportId" value="${transportId}" />
                
                <div class="form-container">
                    <div style="margin-bottom:10px;">
                        <i id="spinnerSymbol" class="fa fa-spin fa-spinner fa-1x"></i>
                        The system is checking the <strong>${ftpLocation}</strong> location for a new file to process.
                    </div>
                    <p id="returnMsg" style="font-weight:bold"></p>
                </div>
                <div id="successMsg" style="display:none;">
                    <div class="alert alert-success">
                        The above file(s) have been submitted for processing. <br /> Click <a href="/administrator/processing-activity/inbound">here</a> to go to view processing activities.
                    </div>
                </div>    
            </form>
        </div>
    </div>
</div>


