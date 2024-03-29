<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix full-width" role="main">
    
    
   <input type="hidden" name="connectionId" id="connectionId" value="${connectionId}" />
    <div class="row-fluid">
	 <c:if test="${not empty param['msg']}" >
	     <div class="col-md-12">
		 <div class="alert alert-success">
		    <strong>Success!</strong> 
		    <c:choose>
			<c:when test="${param['msg'] == 'saved'}">The configuration connection has been successfully saved!</c:when>
		    </c:choose>
		</div>
	     </div>
        </c:if>
    
	<div class="col-md-6">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <h3 class="panel-title">Source Organization</h3>
		</div>
		<div class="panel-body">
		    <div class="form-container scrollable">
			<div id="srcorgDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="organization">Organization *</label>
                            <c:set var="helRegName" value="" />
			    <select id="organization" class="form-control selSendingOrganization" <c:if test="${connectionId > 0}">disabled="true"</c:if>>
				<option value="">- Select -</option>
				<c:forEach items="${sourceOrganizations}" var="org" varStatus="oStatus">
                                    <c:if test="${helRegName == '' || helRegName != sourceOrganizations[oStatus.index].helRegistry}">
                                        <c:if test="${helRegName != ''}">
                                            </optgroup>
                                        </c:if>
                                        <optgroup label="${sourceOrganizations[oStatus.index].helRegistry}">    
                                    </c:if>
                                    <option value="${sourceOrganizations[oStatus.index].id}" <c:if test="${sourceOrganizations[oStatus.index].id == sourceOrgId}">selected</c:if>>${sourceOrganizations[oStatus.index].orgName} </option>
                                    <c:set var="helRegName" value="${sourceOrganizations[oStatus.index].helRegistry}" />
                                </c:forEach>
			    </select>
			    <span id="srcOrgMsg" class="control-label"></span>
			</div>  
			<div id="srcConfigDiv" class="form-group ${status.error ? 'has-error' : '' }">
			    <label class="control-label" for="messageTypeId">Configuration *</label>
			    <select id="srcConfig" rel="${sourceConfigId}" class="form-control" <c:if test="${connectionId > 0}">disabled="true"</c:if>>
				<option value="">- Select -</option>
			    </select>  
			    <span id="srcConfigMsg" class="control-label"></span>
			</div>  
		    </div>
                </div>
            </section>
        </div>
	<div class="col-md-6">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <h3 class="panel-title">Target Organization</h3>
		</div>
		<div class="panel-body">
		    <div id="tgtorgDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="organization">Organization *</label>
                        <c:set var="helRegName" value="" />
			<select id="organization" class="form-control seltgtOrganization" <c:if test="${connectionId > 0}">disabled="true"</c:if>>
			    <option value="">- Select -</option>
			    <c:forEach items="${targetOrganizations}" var="org" varStatus="oStatus">
                                <c:if test="${helRegName == '' || helRegName != targetOrganizations[oStatus.index].helRegistry}">
                                    <c:if test="${helRegName != ''}">
                                        </optgroup>
                                    </c:if>
                                    <optgroup label="${targetOrganizations[oStatus.index].helRegistry}">    
                                </c:if>
				<option value="${targetOrganizations[oStatus.index].id}" <c:if test="${targetOrganizations[oStatus.index].id == targetOrgId}">selected</c:if>>${targetOrganizations[oStatus.index].orgName} </option>
                                <c:set var="helRegName" value="${targetOrganizations[oStatus.index].helRegistry}" />
                            </c:forEach>
			</select>
			<span id="tgtOrgMsg" class="control-label"></span>
		    </div>   
		    <div id="tgtConfigDiv" class="form-group ${status.error ? 'has-error' : '' }">
			<label class="control-label" for="messageTypeId">Configuration *</label>
			<select  id="tgtConfig" rel="${targetConfigId}" class="form-control" <c:if test="${connectionId > 0}">disabled="true"</c:if>>
			    <option value="">- Select -</option>
			</select>
			<span id="tgtConfigMsg" class="control-label"></span>
		    </div> 
                </div>
            </section>
        </div>
    </div>	    
    <div class="row-fluid dataElementDivs" style="display:none">
	<div class="col-md-6" >
	    <section class="panel panel-default sourceDataElementDiv" style="display:none">
		<div class="panel-heading">
		    <h3 class="panel-title">Source Configuration Data Elements</h3>
		</div>
		 <div class="panel-body">
		     <div class="form-container scrollable sourceDataElements"></div>
		 </div>
	    </section>
	</div>
	<div class="col-md-6">
	    <section class="panel panel-default targetDataElementDiv" style="display:none">
		<div class="panel-heading">
		    <h3 class="panel-title">Target Configuration Data Elements</h3>
		</div>
		 <div class="panel-body">
		     <div class="form-container scrollable targetDataElements"></div>
		 </div>
	    </section>
	</div>
    </div>
</div>