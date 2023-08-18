<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<div class="main clearfix full-width" role="main">
    <div class="col-md-12">
        <div class="alert alert-danger" id="exportErrorMsg" style="display:none;">
            <strong>Export Error!</strong> 
            An error occurred while trying to export the connection.
        </div>
        <c:if test="${not empty param['msg']}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
		<c:choose>
		    <c:when test="${param['msg'] == 'updated'}">The configuration connection status has been updated!</c:when>
                    <c:when test="${param['msg'] == 'deleted'}">The configuration connection has been successfully removed!</c:when>
                    <c:when test="${param['msg'] == 'saved'}">The configuration connection has been successfully deleted!</c:when>
                </c:choose>
            </div>
        </c:if>
        <section class="panel panel-default">
            <div class="panel-body">
                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty connections}">id="connectiondataTable"</c:if>>
                            <thead>
                                <tr>
				    <th scope="col">Id</th>
                                    <th scope="col">Source Details</th>
                                    <th scope="col">Target Details</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col" class="center-text">Status</th>
                                    <th scope="col" class="center-text"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty connections}">
                                    <c:forEach var="connection" items="${connections}">
                                        <tr>
					     <td scope="row">${connection.id}</td>
                                            <td scope="row">
                                                Organization: <strong>${connection.sourceOrgName}</strong>
						<br />
						Configuration: <strong>${connection.sourceConfigName} (Id: ${connection.sourceConfigId})</strong>
						<br />
						Transport Method: <strong>${connection.sourceTransportMethod}</strong>
                                            </td>
					    <td scope="row">
                                                Organization: <strong>${connection.targetOrgName}</strong>
						<br />
						Configuration: <strong>${connection.targetConfigName} (Id: ${connection.targetConfigId})</strong>
						<br />
						Transport Method: <strong>${connection.targetTransportMethod}</strong>
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${connection.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                            <td class="center-text actions-col">
                                                <c:choose>
                                                    <c:when test="${connection.status == true}">
                                                        <a href="#a" class="changeStatus" rel="${connection.id}" rel2="0" title="Make this connection inactive">Active</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="#a" class="changeStatus" rel="${connection.id}" rel2="1" title="Make this connection active">Inactive</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
					    <td>
						<div class="dropdown pull-left">
						    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							<i class="fa fa-cog"></i>
						    </button>
						    <ul class="dropdown-menu pull-right">
                                                        <li>
                                                            <a href="#" class="printConfig" rel="${connection.id}" title="Print this Connection">
                                                                <span class="glyphicon glyphicon-print"></span>
                                                                Print
                                                            </a>
                                                        </li>  
                                                        <li class="divider"></li> 
							<li>
							    <a href="/administrator/configurations/connections/details?i=${connection.id}" data-toggle="modal" rel="${connection.id}" class="connectionEdit" title="Edit this connection">
								<span class="glyphicon glyphicon-edit"></span>
								Edit Connection
							    </a>
							</li>
                                                        <c:if test="${connection.allowExport}">
                                                            <li class="divider"></li>
                                                            <li>
                                                                <a href="#" class="exportConnection" rel="${connection.id}" title="Export this Connection">
                                                                    <span class="glyphicon glyphicon-export"></span>
                                                                    Export Connection
                                                                </a>
                                                             </li> 
                                                        </c:if>
							<li class="divider"></li>
							<li>
							    <a href="#" class="deleteConnection" rel="${connection.id}" title="Delete this connection">
								<span class="glyphicon glyphicon-remove-circle"></span>
								Delete
							    </a>
							</li>
						    </ul>
						</div>
					    </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="8" class="center-text">There are currently no configuration connections set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="connectionFileUploadModal" role="dialog" tabindex="-1" aria-labeledby="Connection File Upload" aria-hidden="true" aria-describedby="Connection File Upload"></div>