<%@page isErrorPage="true" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<security:authorize ifAnyGranted="ROLE_SERVICE_OP">
    <c:set var="role" value ="ROLE_SERVICE_OP"/>
</security:authorize>

 <div class="noteBoxWrap noteBox">
     <div class="noticeBox">

         <h3 class="headerTextItem">
              <span class="betaC1">Sorry, we could not find the page you were looking for.</span>
         </h3>

         <p class="noticeBoxText noticeBoxTextMod1">Please use the navigation on the left or select one of the links below:</p>

          <ul class="listWicon">
          <li>
            <a href="javascript:history.go(-1)" class="baseLink baseLinkMod1" title="Back to previous page">
                <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">Back to previous page</span>
            </a>
          </li>
          <li>
             <c:if  test="${role ne 'ROLE_SERVICE_OP'}">
                <a href="${pageContext.request.contextPath}/secure/page/home" class="baseLink baseLinkMod1" title="Go to your dashboard">
                    <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">Go to your dashboard</span>
                </a>
             </c:if>
          </li>
          <li>
             <security:authorize ifAnyGranted="ROLE_INVESTOR">
                  <a href="${pageContext.request.contextPath}/secure/app/#ng/messages/your" class="baseLink baseLinkMod1" title="See your messages">
                     <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">See your messages</span>
                 </a>
             </security:authorize>
              <security:authorize ifAnyGranted="ROLE_ADVISER">
                   <a href="${pageContext.request.contextPath}/secure/app/#ng/messages/client" class="baseLink baseLinkMod1" title="See your messages">
                      <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">See your messages</span>
                  </a>
             </security:authorize>
           </li>
        </ul>
   </div>
 </div>
