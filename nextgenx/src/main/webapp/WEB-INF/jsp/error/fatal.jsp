<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<%@page isErrorPage="true" %>


<div class="messageType2Wrap">   
    <div class="messageIconWrap">
        <em class="iconwarning"><span class="screenReaderText">We're currently experiencing technical difficulties.</span></em>
    </div>
    <div class="messageBody">
        <h2>We're currently experiencing technical difficulties.</h2>
    </div>
    <div class="messageText jq-fatalWrap">Please call our support staff on 
    		<security:authorize ifAnyGranted="ROLE_INVESTOR">1300 881 716</security:authorize>
			<security:authorize ifAnyGranted="ROLE_ADVISER">1300 784 207</security:authorize> and quote the following error code. 
    	<span class="jq-fatalCollapseView"><a href="#nogo" class="baseLink jq-fatalToggleButton">Show error code</a><em class="iconarrowfulldown"></em></span>
    	<span class="jq-fatalExpandView noDisplay"><a href="#nogo" class="baseLink jq-fatalToggleButton">Hide error code</a><em class="iconarrowfullup"></em></span>
    </div>
    <div class="messageText jq-fatalExpandView noDisplay">Error code: <c:out value="${errorcode}"/></div>
</div>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/client/desktop/pages/fatalErrors.js'/>"></script>