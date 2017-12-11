    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
        <%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>


        <!-- Investor -->
        <security:authorize ifAnyGranted="ROLE_INVESTOR">

        </security:authorize>

         <!-- Adviser -->
        <security:authorize ifAnyGranted="ROLE_ADVISER">
            <h1>*********</h1>
             <jsp:include page="../detailedview/detailedViewAdvisorExpand.jsp"/>
        </security:authorize>
        <br/>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/shared/globalElements.js'/>"></script>