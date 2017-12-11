<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>
<spring:eval var="env" expression="@environment.getProperty('environment')" />
<spring:eval var="buildNumber" expression="@environment.getProperty('version')" />
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>

<c:set var="emulatingStyle" scope="request" value="" />
<security:authorize ifAnyGranted="ROLE_SERVICE_OP">
    <c:set var="emulatingStyle" scope="request" value="utilBarMod1" />
</security:authorize>

<!DOCTYPE html>
<html lang="en">
	<head>
		<title><tiles:insertAttribute name="title" /></title>
   		<tiles:insertAttribute name="pageHead" />
	</head>
	<body>
        <div class="layoutContainer">

            <aside class="layoutUtilBar">
                <div class="layoutUtilBarWrap">
                   <div class="utilBar ${emulatingStyle}" role="menu">
						 <jsp:include page="../globalelements/globalLeftNav.jsp"/>                                                
                   </div><!-- utilBar -->
<!--                    <em class="utilCopyright">
                   		<span class="noDisplay">BT Powered by NextGen</span>
                   </em> -->
                </div><!-- layoutUtilBarWrap -->
            </aside><!-- layoutUtilBar -->

            <div class="layoutContent">
                <!-- header -->
                <header class="layoutHeader">
                    <jsp:include page="../globalelements/globalTopNav.jsp"/>
                </header>
                <!-- /header -->
                <article class="layoutMain" data-role="content">

                     <c:if test="${person.firstTimeUser && person.ftueMessageStatus}">
                         <jsp:include page="../globalelements/ftueClientsBubble.jsp"/>
                      </c:if>

                    <div class="layoutMainWrap">
                        <tiles:insertAttribute name="detailView" />
                        <tiles:insertAttribute name="body" />
                    </div> <!-- layoutMainWrap -->
                </article><!-- layoutMain -->

				<footer class="layoutFooter layoutFooterMod1">
                  <jsp:include page="../globalelements/globalFooter.jsp"/>
                </footer> <!-- layoutFooter -->

            </div><!-- layoutContent -->

        </div><!-- layoutContainer -->
	</body>
</html>