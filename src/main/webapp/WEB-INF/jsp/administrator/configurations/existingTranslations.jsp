<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<table class="table responsive table-bordered table-hover">
    <thead>
        <tr style="background-color:#f5f5f5">
            <th scope="col">Field</th>
            <th scope="col">Macro Name</th>
            <th scope="col">Crosswalk Name</th>
            <th scope="col" class="center-text">Pass/Clear</th>
            <th scope="col" class="center-text">Field A</th>
            <th scope="col" class="center-text">Field B</th>
            <th scope="col" class="center-text">Constant 1</th>
            <th scope="col" class="center-text">Constant 2</th>
            <th scope="col" class="center-text">Process Order</th>
            <th scope="col" class="center-text"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${dataTranslations.size() > 0}">
                <c:forEach items="${dataTranslations}" var="translation" varStatus="tStatus">
                    <tr>
                        <td scope="row">
                            ${translation.fieldName} <c:if test="${translation.fieldNo > 0}">- F${translation.fieldNo}</c:if>
                        </td>
                        <td>
                            ${translation.macroName}
                        </td>
                        <td>
                            ${translation.crosswalkName}
                        </td>
                        <td class="center-text">
                            <c:choose><c:when test="${translation.passClear == 1}">Pass</c:when><c:otherwise>Clear</c:otherwise></c:choose>
			</td>
			<td class="center-text">
                            ${translation.fieldA} 
                        </td>
                        <td class="center-text">
                            ${translation.fieldB} 
                        </td>
                        <td class="center-text">
                            ${translation.constant1} 
                        </td>
                        <td class="center-text">
                            ${translation.constant2} 
                        </td>
                        <td class="center-text">
                            <select data-id="${translation.id}" name="processOrder" class="processOrder">
                                <option value="">- Select -</option>
                                <c:forEach begin="1" end="${dataTranslations.size()}" var="i">
                                    <option value="${i}" <c:if test="${translation.processOrder  == i}">selected</c:if>>${i}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="center-text">
                            <a href="javascript:void(0);" class="btn btn-link removeTranslation" data-id="${translation.id}" title="Remove this field translation.">
                                <span class="glyphicon glyphicon-edit"></span>
                                Remove
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise><tr><td scope="row" colspan="10" class="center-text">No Existing Translations Found</td></tr></c:otherwise>
            </c:choose>
    </tbody>
</table>


