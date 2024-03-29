<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <c:choose>
                <c:when test="${not empty savedStatus}" >
                    <c:choose>
                        <c:when test="${savedStatus == 'updated'}"><div class="alert alert-success"><strong>Success!</strong> The configuration data translations have been successfully updated!</div></c:when>
                        <c:when test="${savedStatus == 'created'}"><div class="alert alert-success"><strong>Success!</strong> The crosswalk has been successfully created!</div></c:when>
                        <c:when test="${savedStatus == 'error'}"><div class="alert alert-danger"><strong>Error!</strong> The uploaded crosswalk did not have the correct delimiter!</div></c:when>
                    </c:choose>
                </c:when>
                <c:when test="${not empty param.msg}" >
                    <div class="alert alert-success">
                        <strong>Success!</strong> 
                        <c:choose>
                            <c:when test="${param.msg == 'updated'}">The data translations have been successfully updated!</c:when>
                            <c:when test="${param.msg == 'created'}">The crosswalk has been successfully added!</c:when>
                        </c:choose>
                    </div>
                </c:when>
            </c:choose>
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Configuration Summary</h3>
                </div>
                <div class="panel-body">
                    <p><strong>Organization:</strong> ${configurationDetails.orgName}</p>
                    <p><strong>Configuration Name:</strong> ${configurationDetails.configName} (Id: ${configurationDetails.id})</p>
                    <p><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></p>
                    <p><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></p>
                    <c:if test="${not empty lastConfigUpdate}"><p><strong>Template file last updated on:</strong> <fmt:formatDate value="${lastConfigUpdate}" type="date" pattern="M/dd/yyyy h:mm a" /></p></c:if>
                </div>
            </section>
            <c:if test="${not empty error}" >
                <div class="alert alert-danger" role="alert">
                    The selected file was not found.
                </div>
            </c:if>
	</div>
    </div>
    
    <div class="row-fluid">
        
	<div class="col-md-6">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <h3 class="panel-title">New Data Translation</h3>
		</div>
		<div class="panel-body">
		    <div class="form-container">
			<div id="fieldDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldNumber">Field</label>
                            <select id="field" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${fields}" var="field" varStatus="fStatus">
                                    <c:if test="${fields[fStatus.index].useField == true}">
					<option value="${fields[fStatus.index].id}" rel="${fields[fStatus.index].fieldNo}" id="o${fields[fStatus.index].id}">${fields[fStatus.index].fieldDesc} - F${fields[fStatus.index].fieldNo} </option>
				    </c:if>
				</c:forEach>
                            </select>
                            <span id="fieldMsg" class="control-label"></span>
                        </div>
                        <div id="crosswalkDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldNumber">Crosswalk</label>
                            <select id="crosswalk" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${crosswalks}" var="cwalk" varStatus="cStatus">
                                    <option value="${crosswalks[cStatus.index].id}">${crosswalks[cStatus.index].name} <c:choose><c:when test="${crosswalks[cStatus.index].orgId == 0}"> (generic)</c:when><c:otherwise> (Org Specific)</c:otherwise></c:choose></option>
                                </c:forEach>
                            </select>
                            <span id="crosswalkMsg" class="control-label"></span>
                        </div>
                        <div id="macroDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="macro">Macro <a href="#crosswalkModal" data-toggle="modal" class="macroDefinitions">(View Macro Definitions)</a></label>
                            <select id="macro" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${macros}" var="macro" varStatus="mStatus">
                                    <option value="${macro.id}">${macro.macroName}</option>
                                </c:forEach>
                            </select>
                            <span id="macroMsg" class="control-label"></span>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="passclear">Pass/Clear Error</label>
                            <div>
                                <label class="radio-inline">
                                    <input type="radio" name="passClear" class="passclear" value="1" checked />Pass Error 
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="passClear" class="passclear" value="2" />Clear Error
                                </label>
                            </div>
                        </div>
                        <div id="fieldADiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldA">Field A</label>
                            <input path="fieldA" id="fieldA" class="form-control" type="text" maxLength="45" readonly="" />
                            <span id="fieldAMsg" class="control-label"></span>
                        </div>
                        <div id="fieldBDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldB">Field B</label>
                            <input path="fieldB" id="fieldB" class="form-control" type="text" maxLength="45" readonly="" />
                            <span id="fieldBMsg" class="control-label"></span>
                        </div>
                        <div id="constant1Div" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="constant1">Constant 1</label>
                            <input path="constant1" id="constant1" class="form-control" type="text" maxLength="45" readonly="" />
                            <span id="constant1Msg" class="control-label"></span>
                        </div>
                        <div id="constant2Div" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="constant2">Constant 2</label>
                            <input path="constant2" id="constant2" class="form-control" type="text" maxLength="45" readonly="" />
                            <span id="constant2Msg" class="control-label"></span>
                        </div>
                        <div class="form-group">
                            <input type="button" id="submitTranslationButton"  class="btn btn-primary" value="Add Translation"/>
                            <input type="button" id="clearTranslationButton"  class="btn btn-danger" value="Clear Values"/>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <%-- Existing Crosswalks --%>
        <div class="col-md-6">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <div class="pull-right">
                        <div class="pull-left">
                            <a href="#!" rel="0" class="btn btn-success btn-xs btn-action showCrosswalks" title="Show All Available Crosswalks">Show All Available Crosswalks</a>
                        </div>
                        <div class="dropdown pull-left" style="padding-left:20px;">
                            <button class="btn btn-xs btn-default btn-action dropdown-toggle" type="button" data-toggle="dropdown">
                                <i class="fa fa-cog"></i>
                            </button>
                            <ul class="dropdown-menu pull-right">
                                <li>
                                    <a href="#cwDownloadModal" rel="${configurationDetails.id}" data-toggle="modal" class="createCrosswalkDownload" title="Download Existing Crosswalks">
                                        Download Existing Crosswalks
                                    </a>
                                </li>
                                <li>
                                    <a href="#crosswalkModal" data-toggle="modal" class="" id="createNewCrosswalk" title="Add New Crosswalk">
                                        Add New Single Crosswalk
                                    </a>
                                </li>
                                <li>
                                    <a href="#crosswalkModal" data-cwid="${configurationDetails.id}" data-toggle="modal" class="" id="uploadMultiCrosswalks" title="Upload Multiple Crosswalks">
                                        Upload Multiple Crosswalk Files
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <h3 class="panel-title"><span id="crosswalkTitle">In Use</span> Crosswalks</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container scrollable">
                        <div id="crosswalksTable"></div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>

<div class="main clearfix" role="main">	
    <%-- Existing Translations --%>
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-heading">
		<div class="pull-right dtDownloadLink" style="display:none">
		    <a href="#dtDownloadModal" rel="${configurationDetails.id}" data-toggle="modal" class="btn btn-primary btn-xs btn-action createDataTranslationDownload" title="Download Existing Translations">Download Existing Translations</a>
		</div>
                <h3 class="panel-title">Existing Data Translations</h3>
            </div>
            <div class="panel-body">
                <div id="translationMsgDiv"  rel="${id}" class="alert alert-danger" style="display:none;">
                    <strong>You must click SAVE above to submit the data translations listed below!</strong>
                </div>
                <div class="form-container scrollable" id="existingTranslations"></div>
            </div>
        </section>
    </div>
</div>
<input type="hidden" id="orgId" value="${orgId}" />
<input type="hidden" id="configId" value="${configurationDetails.id}" />
<input type="hidden" id="macroLookUpList" value="${macroLookUpList}" />

<%-- Provider Address modal --%>
<div class="modal fade" id="crosswalkModal" role="dialog" tabindex="-1" aria-labeledby="Message Crosswalks" aria-hidden="true" aria-describedby="Message Crosswalks"></div>
<div class="modal fade" id="macroModal" role="dialog" tabindex="-1" aria-labeledby="Macro Details" aria-hidden="true" aria-describedby="Macro Details"></div>
<div class="modal fade" id="dtDownloadModal" role="dialog" tabindex="-1" aria-labeledby="Data Translations Download" aria-hidden="true" aria-describedby="Data Translations Download"></div>
<div class="modal fade" id="cwDownloadModal" role="dialog" tabindex="-1" aria-labeledby="Crosswalks Download" aria-hidden="true" aria-describedby="Crosswalks Download"></div>
<div class="modal fade" id="loadingModal" role="dialog" tabindex="-1" aria-labeledby="Data Translations Loading" aria-hidden="true" aria-describedby="Data Translations Loading"></div>