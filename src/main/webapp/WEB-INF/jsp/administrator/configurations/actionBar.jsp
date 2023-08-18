<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand" style="color:black">
                <c:choose>
                    <c:when test="${param['page'] == 'listConfigs'}">
                        Configurations
                    </c:when>
                    <c:when test="${param['page'] == 'configDetails'}">
                        Configuration - Details
                    </c:when>
                    <c:when test="${param['page'] == 'transport'}">
                        Configuration - Transport Method
                    </c:when>
                    <c:when test="${param['page'] == 'mappings'}">
                        Configuration - Field Settings
                    </c:when>
                    <c:when test="${param['page'] == 'ERGCustomize'}">
                        Configuration - ERG Customization
                    </c:when>
                    <c:when test="${param['page'] == 'translations'}">
                       Configuration - Data Translations
                    </c:when>
                    <c:when test="${param['page'] == 'specs'}">
                        Configuration - Message Specs
                    </c:when>
                    <c:when test="${param['page'] == 'schedule'}">
                        Configuration - Schedule
                    </c:when>  
                    <c:when test="${param['page'] == 'connections'}">
                       Configuration Connections
                    </c:when> 
                    <c:when test="${param['page'] == 'connectiondetails'}">
                       Configuration Connection Details
                    </c:when>    
                    <c:when test="${param['page'] == 'HL7'}">
                        Configuration - HL7 Customization
                    </c:when>  
                    <c:when test="${param['page'] == 'CCD'}">
                        Configuration - CCD/XML/JSON Customization
                    </c:when>     
                    <c:when test="${param['page'] == 'preprocessing'}">
                        Configuration - Pre-Process Macros
                    </c:when>   
                    <c:when test="${param['page'] == 'postprocessing'}">
                        Configuration - Post-Process Macros
                    </c:when>   
                    <c:when test="${param['page'] == 'notes'}">
                        Configuration Notes
                    </c:when>       
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions">
            <c:choose>
                <c:when test="${param['page'] == 'listConfigs'}">
                    <c:if test="${not empty allowConfigImport}">
                        <li role="menuitem" class="importConfigBtn"><a href="#configFileUploadModal" data-toggle="modal" class="importConfig" title="Import Configuration" role="button"><span class="glyphicon icon-stacked glyphicon glyphicon-import"></span>Import Configuration</a></li>
                    </c:if>
                    <li role="menuitem"><a href="/administrator/configurations/create" title="Create New Configuration" role="button"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>
                <c:when test="${param['page'] == 'connections'}">
                    <c:if test="${not empty allowConnectionImport}">
                        <li role="menuitem" class="importConnectionBtn"><a href="#connectionFileUploadModal" data-toggle="modal" class="importConnection" title="Import Connection" role="button"><span class="glyphicon icon-stacked glyphicon glyphicon-import"></span>Import Connection</a></li>
                    </c:if>
                    <li><a href="/administrator/configurations/connections/details" data-toggle="modal" role="button" title="Create Configuration Connection"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>  
		<c:when test="${param['page'] == 'connectiondetails'}">
                    <c:if test="${not empty allowExport}">
                        <li role="menuitem" class="exportConnectionnBtn"> <a href="#" class="exportConnection" rel="${connectionId}" title="Export this Connection"><span class="glyphicon icon-stacked glyphicon glyphicon-export"></span>Export Connection</a></li>
                    </c:if>
                    <li role="menuitem">
			<a href="#" class="printConfig" title="Print this Connection" rel="${connectionId}" role="button"><span class="glyphicon glyphicon-print icon-stacked"></span> Print </a>
		    </li>
                    <li role="menuitem"><a href="#" id="saveDetails" title="Save this Connection" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                    <li role="menuitem"><a href="#" id="saveCloseDetails" title="Save &amp; Close" role="button"><span class="glyphicon glyphicon-floppy-disk icon-stacked"></span> Save &amp; Close</a></li>
                    <li role="menuitem"><a href="<c:url value='/administrator/configurations/connections' />" title="Cancel" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span>Cancel</a></li>
                </c:when>     
                <c:when test="${param['page'] == 'CCD'}">
                    <li><a href="#ccdElementModal" id="createNewCCDElement" data-toggle="modal" role="button" title="Create CCD Element"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                </c:when>    
                <c:otherwise>
                    <c:if test="${configurationDetails.allowExport}">
                        <li>
                            <a href="#" class="exportConfig" rel="${configurationDetails.id}" title="Export this Configuration" role="button"><span class="glyphicon glyphicon-export icon-stacked"></span>Export</a>
                         </li> 
                    </c:if>
                    <li>
			<a href="#" class="printConfig" title="Print this Configuration" rel="${configurationDetails.id}" role="button"><span class="glyphicon glyphicon-print icon-stacked"></span> Print </a>
		    </li>
                    <c:if test="${param['page'] != 'notes'}">
                        <li>
                            <a href="#" id="saveDetails" title="Save this Configuration initial setup" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a>
                        </li>
                    </c:if>
		    <c:if test="${configurationDetails.configurationType == 1 || (configurationDetails.configurationType == 2 && param['page'] != 'schedule')}">
			 <c:if test="${param['page'] != 'postprocessing' && param['page'] != 'notes'}">
			    <li><a href="#" id="next" title="Save and Proceed to the Next Step"><span class="glyphicon glyphicon-forward icon-stacked" role="button"></span>Next Step</a></li>
			</c:if>
		    </c:if>
                    <%--<c:if test="${not empty id}"><li><a href="#confirmationOrgDelete" data-toggle="modal" rel="${id}" title="Delete this Configuration"><span class="glyphicon glyphicon-remove icon-stacked"></span>Delete</a></li></c:if>--%>
                    <c:if test="${param['page'] != 'notes'}">
                        <li>
                            <a href="<c:url value='/administrator/configurations/list' />" title="Cancel" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span>Cancel</a>
                        </li>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>
