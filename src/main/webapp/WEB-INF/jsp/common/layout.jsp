<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="utf-8">	
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <meta http-equiv="Content-Language" content="en">
        <title>${title}</title>
        <link rel="shortcut icon" href="#">
        <link rel="stylesheet" href="/dspResources/css/admin/main.css?v=2">
        <link rel="stylesheet" href="/dspResources/@fortawesome/fontawesome-pro/css/regular.min.css">
	<link rel="stylesheet" href="/dspResources/@fortawesome/fontawesome-pro/css/solid.min.css">
	<link rel="stylesheet" href="/dspResources/@fortawesome/fontawesome-pro/css/fontawesome.min.css">
        <link rel="stylesheet" href="/dspResources/css/admin/bootstrap-wysihtml5.css">
        <jsp:text><![CDATA[<!--[if lte IE 9]>]]></jsp:text>
                <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/admin/ie.css">
        <jsp:text><![CDATA[<![endif]-->]]></jsp:text>
        <script type="text/javascript" src="/dspResources/js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
        <script data-main="/dspResources/js/admin/main" src="/dspResources/js/vendor/require.js"></script>
    </head>
    <body id="${page-id}" class="${page-section}">
        <jsp:text><![CDATA[<!--[if lte IE 7]>]]></jsp:text>
        <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <jsp:text><![CDATA[<![endif]-->]]></jsp:text>
        <div class="wrap">
            <c:if test="${not empty header}"><jsp:include page="/WEB-INF/jsp/${header}.jsp" /></c:if>
            <div>
                <c:if test="${not empty actions}"><jsp:include page="/WEB-INF/jsp/${actions}.jsp" /></c:if>
                <div class="container-fluid">
                    <div class="row-fluid contain">${pageContext.request.servletPath}ffff
                        <c:if test="${not empty menu}"><jsp:include page="/WEB-INF/jsp/${menu}.jsp" /></c:if>
                        <%--<c:if test="${not empty body}"><jsp:include page="/WEB-INF/jsp/${body}.jsp" /></c:if>--%>
                    </div>
                </div>
                <c:if test="${not empty footer}"><jsp:include page="../${footer}.jsp" /></c:if>  
            </div>
        </div>
    </body>
    <c:if test="${not empty footer}"><jsp:include page="/WEB-INF/jsp/${footer}.jsp" /></c:if>  
    <%-- <c:if test="${not empty jscript}"><jsp:include page="${pageContext.request.contextPath}/dspResources/js/${jscript}" /></c:if>
   <c:if test="${not empty script}">
        <script type="text/javascript" nonce="9483TZ393HIE383">
        require(["<%=request.getContextPath()%><tiles:getAsString name='jscript' ignore='true' />"]);
        </script>
    </c:if>--%>
</html>