<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page isErrorPage="true" %>
<spring:eval var="env" expression="@environment.getProperty('environment')" />

 <div class="noteBoxWrap noteBox">
         <div class="noticeBox">
             <h3 class="headerTextItem">
                  <span>We are currently experiencing technical difficulties.</span>
             </h3>

             <p class="noticeBoxText noticeBoxTextMod1">Sorry for the inconvenience. Please try again later.</p>
         </div>
 </div>

<c:if test="${!(env == 'PROD' || env == 'UAT')}">
 <div class="${errorClass}">
    <c:if test="${subTemplate == 'base'}">
       <%@ include file="base.jsp" %>
    </c:if>
    <c:if test="${subTemplate == 'unmarshall'}">
       <%@ include file="unmarshall.jsp" %>
    </c:if>
 </div>
</c:if>
