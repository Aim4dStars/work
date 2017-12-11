<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="jq-serviceDetailPageWrap">
   <div class="setBottomGutter">
    <a href="searchApplication" class="baseLink baseLinkClear">
        <em class="mod180 iconlink"></em> <span class="iconLinkLabel"> Back to search</span> </a>
    </div>
    
    <div>
    <c:choose>
    <c:when test="${not empty onBoardingStatusModel.onboardingPartyList }">
    <h1 class="pageHeaderItem">On Boarding record for Application Id ${onBoardingStatusModel.onBoardingApplication.key.id} </h1>
    
    <div class="sectionContentItem sectionContentItemStyleOne clearFix">
    <!-- First half DETAILS -->
    <div class="setHalf setHalfMod3 lastBorder">
        <div>
        <h3 class="sectionGroupHeader">Application Details</h3>
        <dl class="dataSummary">
            <dt class="listContentItemHeader clearBoth  emphasis">Application Type</dt>
                <dd>${onBoardingStatusModel.onBoardingApplication.applicationType}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">Avaloq Order Id</dt>
                <dd>
                ${onBoardingStatusModel.onBoardingApplication.avaloqOrderId}
                </dd>
            <dt class="listContentItemHeader clearBoth emphasis">Offline Approval</dt>
                <dd>${onBoardingStatusModel.onBoardingApplication.offline}</dd>
                  
        </dl>
        <h3 class="sectionGroupHeader">Party Details</h3>
        <HR>
         <c:if test="${not empty onBoardingStatusModel.onboardingPartyList }">
          <c:forEach items="${onBoardingStatusModel.onboardingPartyList}" var="partyList">
           <dl class="dataSummary">
            <dt class="listContentItemHeader clearBoth  emphasis">Party Id</dt>
                <dd>${partyList.partyId}</dd>
              <dt class="listContentItemHeader clearBoth  emphasis">GCM Pan</dt>
                <dd>${partyList.gcmPan}</dd>  
                <dt class="listContentItemHeader clearBoth emphasis">Application Id</dt>
                <dd> ${partyList.onBaordingId}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">Party Status</dt>
                <dd>${partyList.status}</dd>
          </dl>
          <HR>
        </c:forEach>
        </c:if>
    </div>
    </div>
   
    <!-- Second half ACTION-->
    <div class="setHalf setHalfMod4">
        <div class="sectionContentItemContainer jq-clientDetailActionWrap">
            <form name="serviceOps_form" id="serviceOps" method="POST" action="/ng/secure/page/serviceOps/changeStatus"  class="jq-clientDetailActionForm">
            	
            	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken"/>
            	<input id="appId" type="hidden" value="<c:out value='${onBoardingStatusModel.onBoardingApplication.key.id}'/>" name="appId"/>
            	 <c:if test="${not empty onBoardingStatusModel.statusMessage }">
            	 <span class="positive emphasis" style="color: green;">You have successfully updated the status to ${onBoardingStatusModel.applicationStatus}</span>
            	 </c:if>
                <h3 class="sectionGroupHeader">Actions</h3>
                <dl class="dataSummary endBorder dataSummaryExtended">
                      	 <dt class="emphasis">Onboarding status:</dt>
                    	 <dd><span class="positive emphasis">${onBoardingStatusModel.applicationStatus}</span></dd>

                </dl>
                   <p class="clearFix"></p>

                    <ul class="formBlockContainer setSelectContainerGutter">
                        
                            <li class="formBlock">
                                <label for="selectAnAction" class="formLabel">Select a Status</label>
                                <select name="status" id="status" class="jq-serviceOperatorSelectAction">
                                <c:forEach items="${statusType}" var="status">
                                    <option value="${status}">${status}</option>
                                    </c:forEach>
                                </select>
                            </li>
                            <li class="setTopGutter clearBoth"><a href="#nogo" class="primaryButton jq-formSubmit jq-detailPageActionSubmitButton ">Go</a></li>
                    </ul>
                </form>
            </div>
    </div>
    </div>
    </c:when>
    <c:otherwise>
    <h1 class="pageHeaderItem">No Data Found Please try with valid ID </h1>
    </c:otherwise>
    </c:choose>
    </div>
    
<%--use by js module internally do not remove--%>
<input type="hidden" value="clientDetailPage" name="_page" class="jq-serviceOperatorDetailPage"/>
<input type="hidden" value="${not empty defaultAction ? defaultAction : ''}" name="_defaultAction" class="jq-serviceOperatorDetailDefaultAction"/>
<input type="hidden" value="${not empty actionPerformed ? actionPerformed : ''}" name="_actionPerformed" class="jq-serviceOperatorDetailActionPerformed"/>
<p aria-live="assertive" aria-atomic="true" class="ui-helper-hidden-accessible jq-actionStatusMessage"></p>
</div>


<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOperator.js'/>"></script> 
