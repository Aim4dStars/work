<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<security:authorize ifAnyGranted="ROLE_ADVISER">
 <jsp:include page="advisercontactus.jsp" flush="true" />
</security:authorize>
<security:authorize ifAnyGranted="ROLE_INVESTOR">
 <jsp:include page="investorcontactus.jsp" flush="true" />
</security:authorize>
