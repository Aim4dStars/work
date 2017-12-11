<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<div class="jq-serviceDetailPageWrap">
   <div class="setBottomGutter">
    <a href="../home" class="baseLink baseLinkClear">
        <em class="mod180 iconlink"></em> <span class="iconLinkLabel"> Back to search</span> </a>
    </div>
    <div>
    <h1 class="pageHeaderItem">Account record for ${serviceOpsModel.wrapAccountDetail.accountName} </h1>
    <div class="sectionContentItem sectionContentItemStyleOne clearFix">
    <!-- First half DETAILS -->
    <div class="setHalf setHalfMod3 lastBorder">
        <div>
        <c:choose>
            <c:when test="${serviceOpsModel.wrapAccountDetail.accountType == 'Individual'}">
            <h3 class="sectionGroupHeader">Client</h3>
            </c:when>
            <c:when test="${serviceOpsModel.wrapAccountDetail.accountType == 'Joint'}">
            <h3 class="sectionGroupHeader">Clients</h3>
            </c:when>
            <c:when test="${serviceOpsModel.wrapAccountDetail.accountType == 'SUPER'}">
            <h3 class="sectionGroupHeader">Investor</h3>
            </c:when>
            <c:otherwise>
            <h3 class="sectionGroupHeader">Linked clients</h3>
            </c:otherwise>
        </c:choose>

        <dl class="dataSummary">
            <dt class="listContentItemHeader clearBoth  emphasis">Primary contact</dt>
                <dd>${serviceOpsModel.primaryContactPerson}</dd>
        </dl>

        <c:forEach items="${serviceOpsModel.linkedClients}" var="client">
            <c:if test="${!client.adviserFlag}">
                <h3 class="sectionGroupHeader"><a href="${client.detailPageUrl}" title="${client.firstName} ${client.lastName}" class="baseLink" data-ng-key="accountName">${client.firstName} ${client.lastName}</a></h3>
                <dl class="dataSummary">
                <c:if test="${not empty client.roles}">
                    <dt class="listContentItemHeader clearBoth  emphasis">Role(s)</dt>
                    <dd>${client.roles}</dd>
                </c:if>
                    <dt class="listContentItemHeader clearBoth  emphasis">Payment setting</dt>
                    <dd>${client.paymentSetting}</dd>

                    <dt class="listContentItemHeader clearBoth  emphasis">Postal address</dt>
                    <dd>${client.postalAddress}</dd>
                </dl>
            </c:if>
        </c:forEach>

        <h3 class="sectionGroupHeader">Account details</h3>

        <dl class="dataSummary">
            <dt class="listContentItemHeader clearBoth  emphasis">BSB</dt>
            <dd>${serviceOpsModel.wrapAccountDetail.bsb}</dd>
            <dt class="listContentItemHeader clearBoth  emphasis">Account no.</dt>
            <dd>${serviceOpsModel.wrapAccountDetail.accountNumber}</dd>
            <dt class="listContentItemHeader clearBoth  emphasis">Registered since</dt>
            <dd>${serviceOpsModel.registeredSince}</dd>
            <dt class="listContentItemHeader clearBoth  emphasis">Tax preference</dt>
            <dd>${serviceOpsModel.wrapAccountDetail.cGTLMethod}</dd>
            <c:if test="${not empty serviceOpsModel.wrapAccountDetail.migrationDetails.migrationDate}">
                <dt class="listContentItemHeader clearBoth  emphasis">Upgraded to Panorama on</dt>
                <dd>
                    <joda:format var="formattedMigrationdateDate" value="${serviceOpsModel.wrapAccountDetail.migrationDetails.migrationDate}" pattern="dd MMM yyyy" />
                    <p>${formattedMigrationdateDate}</p>
                </dd>
            </c:if>
            <c:if test="${not empty serviceOpsModel.wrapAccountDetail.migrationDetails.accountId}">
                <dt class="listContentItemHeader clearBoth  emphasis">Original account no.</dt>
                <dd>${serviceOpsModel.wrapAccountDetail.migrationDetails.accountId}</dd>
            </c:if>
        </dl>
        <h3 class="sectionGroupHeader">Adviser details</h3>
        <c:if test="${serviceOpsModel.directInvestorFlag}">
            <h3 class="descriptionHeader">Not applicable</h3>
        </c:if>
        <c:forEach items="${serviceOpsModel.linkedClients}" var="client">
            <c:if test="${client.adviserFlag}">
                <dl class="dataSummary">
                    <dt class="listContentItemHeader clearBoth  emphasis">Name</dt>
                    <dd><a href="${client.detailPageUrl}" title="${client.firstName} ${client.lastName}" class="baseLink" data-ng-key="accountName">${client.fullName}</a>, ${client.postalAddress}</dd>

                    <dt class="listContentItemHeader clearBoth  emphasis">Payment setting</dt>
                    <dd>${client.paymentSetting}</dd>
                </dl>
            </c:if>
        </c:forEach>
    </div>
    </div>

    <!-- Second half ACTION-->
    <div class="setHalf setHalfMod4">
        <div class="sectionContentItemContainer jq-clientDetailActionWrap">
            <form:form name="serviceOps_form" id="serviceOps" method="POST" action="submitAccountDetailAction" commandName="serviceOpsModel" class="jq-clientDetailActionForm">
            	<form:input id="userName" path="userName" type="hidden" value='${serviceOpsModel.userName}' name="userName"/>
            	<form:input id="role" path="role" type="hidden" value='${serviceOpsModel.role}' name="role"/>
            	<form:input id="userId" path="userId" type="hidden" value='${serviceOpsModel.userId}' name="userId"/>
            	<form:input id="primaryMobileNumber" path="primaryMobileNumber" type="hidden" value='${serviceOpsModel.primaryMobileNumber}' name="primaryMobileNumber"/>
            	<form:input id="firstName" path="firstName" type="hidden" value='${serviceOpsModel.firstName}' name="firstName"/>
                <form:input id="lastName" path="lastName" type="hidden" value='${serviceOpsModel.lastName}' name="lastName"/>
            	<form:input id="action" path="action" type="hidden" value='${serviceOpsModel.action}' name="action"/>
                <form:input id="accountNumber" path="accountNumber" type="hidden" value='${serviceOpsModel.wrapAccountDetail.key.accountId}' name="accountNumber"/>
            	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken"/>
                <h3 class="sectionGroupHeader">Actions</h3>
                <c:if test="${not empty STATUS && STATUS == 'ERROR'}">
                    <c:set var="messageBoxClass" value ="warningBox"/>
                    <c:set var="message" value ="${errorMessage}"/>
                </c:if>
               <c:if test="${not empty STATUS && STATUS == 'SUCCESS'}">
                  <c:set var="messageBoxClass" value ="successBox"/>
                  <c:set var="message" value ="${SUCCESS}"/>
              </c:if>
                <c:if test="${not empty message}">
                    <div class="noticeBox noticeBoxTextSmallMod2 ${messageBoxClass}">
                        <ul class="noticeBoxWrapper">
                            <li>
                                <span class="messageIcon"><em class="iconItem"></em></span>
                            </li>
                            <li class="noticeBoxText noticeBoxTextSmallBox">
                                <p class="emphasis jq-actionStatus">
                                   ${message}
                                </p>
                            </li>
                        </ul>
                    </div>
                </c:if>
                <ul class="formBlockContainer setSelectContainerGutter">
                    <li class="formBlock">
                        <label for="selectAnAction" class="formLabel">Select an action</label>
                        <select name="clientId" id="selectAnAction" class="jq-serviceOperatorSelectAction">
                        <c:forEach items="${serviceOpsModel.linkedClients}" var="client">
                            <option value="${client.clientId}">${client.actionMessage}</option>
                        </c:forEach>
                        </select>
                    </li>
                    <li class="setTopGutter clearBoth"><a href="#nogo" class="primaryButton jq-formSubmit jq-detailPageActionSubmitButton ${not empty actionPerformed ? 'jq-cloak' : ''}  ${not empty serviceOpsModel.canPerformAction ? 'primaryButtonDisabled jq-disabled' : ''}">Go</a></li>
                </ul>
                </form:form>
            </div>
    </div>
    </div>
    </div>
<%--use by js module internally do not remove--%>
<input type="hidden" value="clientDetailPage" name="_page" class="jq-serviceOperatorDetailPage"/>
<input type="hidden" value="${not empty defaultAction ? defaultAction : ''}" name="_defaultAction" class="jq-serviceOperatorDetailDefaultAction"/>
<input type="hidden" value="${not empty actionPerformed ? actionPerformed : ''}" name="_actionPerformed" class="jq-serviceOperatorDetailActionPerformed"/>
<p aria-live="assertive" aria-atomic="true" class="ui-helper-hidden-accessible jq-actionStatusMessage"></p>
</div>

<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOperator.js'/>"></script>

<div class ="modalContent jq-cloak jq-updateStatementPrefModal">
<form method="POST" action="updateStatementPref" class="jq-updateStatementPref">
<input type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken"/>
        <div class="modalContentMod3">
         <h1 class="formHeaderModal">
            <span class="baseGama"> Change Correspondence Preference
        </h1>

        <div><input type="radio" value="ONLINE" name= "preference" style="display:inline;" />   Online </div>
        <div><input type="radio" value="PAPER" name= "preference" style ="display:inline;" />   Paper </div>

        <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
            <li>
                <button type="submit" class="primaryButton jq-formSubmit jq-correspondence" title="Update">Update</button>
            </li>
            <li>
                <a href="updateStatementPrefModal" class="baseLink baseLinkClear" title="Cancel">
                    <em class="iconlink"></em>
                    <span class="iconLinkLabel">Cancel</span>
                </a>
            </li>
        </ul>
        </div>
    </form>

 </div>