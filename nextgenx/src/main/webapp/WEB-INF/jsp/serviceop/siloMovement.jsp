<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var = "key"  value = "${key}"/>
   <c:if test="${empty  key}">
     <h1 class="pageHeaderItem">GCM SILO movement</h1>
    <jsp:include page="siloMovementForm.jsp" />
    <script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/siloMovement.js'/>"></script> 
    </c:if>
    <c:if test="${not empty  key}">
    <jsp:include page="siloResponse.jsp" /> <br><br>
    <p class="emphasis">Do you want another SILO movement,<a href="<c:url value='/secure/page/serviceOps/siloMovementReq'/>"> <span style="color:blue; font-style: italic;">click here</span> </a></p> 
</c:if>

