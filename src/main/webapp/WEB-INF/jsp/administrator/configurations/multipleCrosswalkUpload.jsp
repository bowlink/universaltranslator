<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close cwClose" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Upload Multiple Crosswalk Files ${success}</h3>
        </div>
        <div class="modal-body">
            <div class="cwUploadForm">
                <form:form id="multiCWForm" enctype="multipart/form-data" method="post" role="form">
                    <input type="hidden" name="orgId" value="${orgId}" />
                    <input type="hidden" name="cwId" value="${cwId}" />
                    <div class="form-container">
                        <div id="crosswalkDelimDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="name">Select the Delimiter all the crosswalk files will have *</label>
                            <select name="fileDelimiter" id="delimiter" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${delimiters}" var="cwalk" varStatus="dStatus">
                                    <option value="${delimiters[dStatus.index][0]}">${delimiters[dStatus.index][1]} </option>
                                </c:forEach>
                            </select>
                            <span id="crosswalkDelimMsg" class="control-label"></span>
                        </div>
                        <div id="crosswalkFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="crosswalkFile">Select all Crosswalk Files to upload (Must be .txt files)*</label>
                            <input class="form-control" name="crosswalkFile" type="file" id="crosswalkFile" multiple />
                            <span id="crosswalkFileMsg" class="control-label"></span>
                        </div>
                        <div class="form-group">
                            <input type="button" id="submitMultiCrosswalkButton"  class="btn btn-primary" value="Upload Crosswalk Files"/>
                        </div>
                    </div>
                </form:form>
            </div>    
            <div class="uploadResults" style="display:none; height:200px; overflow: auto;"></div>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");


    });

</script>