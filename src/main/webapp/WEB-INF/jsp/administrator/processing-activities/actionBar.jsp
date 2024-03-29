<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand">
               <c:choose>
                    <c:when test="${param['page'] == 'waiting'}">
                        <a href="#" title="Transactions Waiting to be Processed" class="unstyled-link">Transactions Waiting to be Processed</a>
                    </c:when>
                    <c:when test="${param['page'] == 'inbound' || page == 'inbound'}">
                        <a href="#" title="Inbound Batches" class="unstyled-link">Inbound Batches</a>
                    </c:when>
                    <c:when test="${param['page'] == 'auditReport' && page == 'rejected'}">
                        <a href="#" title="Inbound Batches with Rejected Transactions / Audit Report" class="unstyled-link">Inbound Batches with Rejected Transactions / Audit Report</a>
                    </c:when>       
                    <c:when test="${param['page'] == 'rejected' || page == 'rejected'}">
                        <a href="#" title="Inbound Batches with Rejected Transactions" class="unstyled-link">Inbound Batches with Rejected Transactions</a>
                    </c:when>    
                    <c:when test="${param['page'] == 'outbound' || page == 'outbound'}">
                        <a href="#" title="Outbound Batches" class="unstyled-link">Outbound Batches</a>
                    </c:when>
                    <c:when test="${param['page'] == 'edit'}">
                        <a href="#" title="Edit Batch Transaction" class="unstyled-link">Edit Batch Transaction</a>
                    </c:when>
                    <c:when test="${param['page'] == 'refActivityExport'}">
                        <a href="#" title="Referral Activity Export" class="unstyled-link">Referral Activity Export</a>
                    </c:when>  
                    <c:when test="${param['page'] == 'auditReport' && page == 'invalid'}">
                        <a href="#" title="Invalid Inbound Batches / Audit Report" class="unstyled-link">Invalid Inbound Batches / Audit Report</a>
                    </c:when>    
		    <c:when test="${param['page'] == 'auditReport'}">
                        <a href="#" title="Inbound Batches / Audit Report" class="unstyled-link">Inbound Batches / Audit Report</a>
                    </c:when>
                    <c:when test="${param['page'] == 'auditReportOutbound'}">
                        <a href="#" title="Outbound Batches / Audit Report" class="unstyled-link">Outbound Batches / Audit Report</a>
                    </c:when>    
		    <c:when test="${param['page'] == 'wsmessage'}">
                        <a href="#" title="Web Service Messages" class="unstyled-link">Web Service Messages</a>
                    </c:when>
		    <c:when test="${param['page'] == 'apimessages'}">
                        <a href="#" title="Rest API Messages In" class="unstyled-link">Rest API Messages In</a>
                    </c:when>
		   <c:when test="${param['page'] == 'apimessagesout'}">
                        <a href="#" title="Rest API Messages Out" class="unstyled-link">Rest API Messages Out</a>
                    </c:when>
		    <c:when test="${param['page'] == 'report'}">
                        <a href="#" title="Activity Report" class="unstyled-link">Activity Report</a>
                    </c:when>
		    <c:when test="${param['page'] == 'invalidin' || page == 'invalidIn'}">
                        <a href="#" title="Invalid Inbound Batches" class="unstyled-link">Invalid Inbound Batches</a>
                    </c:when>
		    <c:when test="${param['page'] == 'invalidout'}">
                        <a href="#" title="Invalid Outbound Batches" class="unstyled-link">Invalid Outbound Batches</a>
                    </c:when>
		    <c:when test="${param['page'] == 'directmessages'}">
                        <a href="#" title="Direct Messages In" class="unstyled-link">Direct Messages In</a>
                    </c:when>
		   <c:when test="${param['page'] == 'directmessagesout'}">
                        <a href="#" title="Direct Messages Out" class="unstyled-link">Direct Messages Out</a>
                    </c:when>
                    <c:when test="${param['page'] == 'generateReport'}">
                        <a href="#" title="Generate Activity Report" class="unstyled-link">Generate Activity Report</a>
                    </c:when>
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions" role="menu">
            <c:choose>
                <c:when test="${param['page'] == 'report'}">
                    <c:if test="${checkforFTP}">
                        <li role="menuitem"><a href="#configFTPCheckdModal" data-toggle="modal" class="checkFTP" title="Check All FTP locations for new files to process" role="button"><span class="glyphicon glyphicon-play-circle icon-stacked"></span> Check ALL FTP Locations</a></li>
                    </c:if>
                    <li role="menuitem"><a href="/administrator/processing-activity/generateReport" class="" title="Generate Activity Report" role="button"><span class="glyphicon glyphicon-list-alt icon-stacked"></span> Generate Report</a></li>
                </c:when>
                <c:when test="${param['page'] == 'generateReport'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/activityReport" class="" title="Back to Activity Report" role="button"><span class="glyphicon glyphicon-backward icon-stacked"></span> Back</a></li>
                </c:when>
                <c:when test="${param['page'] == 'edit'}">
                    <li role="menuitem"><a href="#" id="saveCloseDetails" class="submitMessage" title="Save &amp; Close" role="button"><span class="glyphicon glyphicon-floppy-disk icon-stacked"></span> Save &amp; Close</a></li>
                </c:when>
                <c:when test="${param['page'] == 'auditReport' && page == 'invalid'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/invalidIn" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>  
                <c:when test="${param['page'] == 'auditReport' && page == 'rejected'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/rejected" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>      
		<c:when test="${param['page'] == 'auditReport'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/inbound" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>  
                <c:when test="${param['page'] == 'auditReportOutbound'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/outbound" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>     
            </c:choose>
        </ul>
    </div>
</nav>
