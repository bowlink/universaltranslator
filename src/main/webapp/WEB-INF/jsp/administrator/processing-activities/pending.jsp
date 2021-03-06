<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<div class="main clearfix" role="main">

    <c:if test="${not empty param.msg}" >
        <div class="col-md-12">
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${param.msg == 'processed'}">
                        The transactions for the selected organization and message type are now being processed.
                    </c:when>
                    <c:when test="${param.msg == 'notprocessed'}">
                        The transactions for the selected organization and message type were all updated to Do Not Process.
                    </c:when>
                </c:choose>
            </div>
        </div>
    </c:if>
    <%--<div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                        <dt>System Summary:</dt>
                        <dd><strong>Total Transactions to be Processed:</strong> <fmt:formatNumber value="${summaryDetails.batchesToProcess}" /></dd>
                    </dt>
                </div>
            </section>
        </div>
    </div>--%>
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-body">
                <form id="showMessageTypes" action="/administrator/processing-activity/pending/messageTypes" method="post">
                    <input type="hidden" name="orgId" id="orgId" value="0" />  
                </form>
                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty transactions}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Target Organization</th>
                                    <th scope="col" class="center-text">Total Pending Deliveries</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty transactions}">
                                    <c:forEach var="transaction" items="${transactions}">
                                        <tr  style="cursor: pointer">
                                            <td scope="row">
                                                ${transaction.orgDetails}
                                            </td>
                                            <td class="center-text">
                                                ${transaction.totalPending}
                                            </td>
                                            <td class="actions-col">
                                                <a href="javascript:void(0);" rel="${transaction.orgId}" class="btn btn-link viewLink" title="View Transactions" role="button">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    View
                                                </a>
                                                <a href="javascript:void(0);" class="btn btn-link processAll" rel="${transaction.orgId}" title="Process All" role="button">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Process All
                                                </a>
                                                <a href="javascript:void(0);" class="btn btn-link donotprocess" rel="${transaction.orgId}" title="Do Not Process" role="button">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Do Not Process
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>     
                                </c:when>   
                                <c:otherwise>
                                    <tr><td colspan="3" class="center-text">There are currently no pending deliveries.</td></tr>
                                </c:otherwise>
                            </c:choose>           
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>