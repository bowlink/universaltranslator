<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<table class="table table-hover table-default" <c:if test="${availableCrosswalks.size() > 0}">id="cwDataTable"</c:if>>
    <thead>
        <tr>
	    <th scope="col" class="center-text">ID</th>
            <th scope="col">Name</th>
            <th scope="col" class="center-text">Date Created</th>
            <th scope="col" class="center-text">Last Updated</th>
            <th scope="col"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${availableCrosswalks.size() > 0}">
                <c:forEach items="${availableCrosswalks}" var="crosswalk" varStatus="pStatus">
                    <tr>
                        <td scope="row" class="center-text">${availableCrosswalks[pStatus.index].id}</td>
			<td>
                            ${availableCrosswalks[pStatus.index].name}
                            <c:choose>
                                <c:when test="${availableCrosswalks[pStatus.index].orgId == 0}"> 
                                    (generic)
                                </c:when>
                                <c:otherwise> (Org Specific)</c:otherwise>
                            </c:choose>
                        </td>
                        <td class="center-text"><fmt:formatDate value="${availableCrosswalks[pStatus.index].dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                        <td class="center-text"><fmt:formatDate value="${availableCrosswalks[pStatus.index].lastUpdated}" type="date" pattern="M/dd/yyyy" /></td>
                        <td>
                            <div class="dropdown pull-left">
                                <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
                                    <i class="fa fa-cog"></i>
                                </button>
                                <ul class="dropdown-menu pull-right">
                                    <li>
                                        <a href="#crosswalkModal" data-toggle="modal" class="btn btn-link viewCrosswalk" rel="?i=${availableCrosswalks[pStatus.index].id}" title="View this Crosswalk">
                                            <span class="glyphicon glyphicon-edit"></span> View
                                        </a>
                                    </li>  
                                    <c:if test="${availableCrosswalks[pStatus.index].orgId > 0}">
                                        <li>
                                            <a href="#!" class="btn btn-link deleteCrosswalk" rel2="${crosswalk.dtsId}" rel="${availableCrosswalks[pStatus.index].id}" title="Delete this Crosswalk">
                                                <span class="glyphicon glyphicon-remove"></span> Delete
                                            </a>
                                        </li> 
                                    </c:if>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td scope="row" colspan="5" style="text-align:center">No Crosswalks are in use for this configuration.</td>
                </tr>
            </c:otherwise>
            </c:choose>
    </tbody>
</table>