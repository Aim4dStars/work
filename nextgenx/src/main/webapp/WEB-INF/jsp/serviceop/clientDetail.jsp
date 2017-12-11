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
    <a href="../home" class="baseLink baseLinkClear">
        <em class="mod180 iconlink"></em> <span class="iconLinkLabel"> Back to search</span> </a>
    </div>
    <div>
    <h1 class="pageHeaderItem">${serviceOpsModel.role == 'INVESTOR' ? 'Client' :'Intermediary'} record for ${serviceOpsModel.fullName} </h1>
    <c:if test="${not empty serviceOpsModel.westpacLive && !(not serviceOpsModel.westpacLive)}">
        <div class="noticeBox successBox"style="border:3px solid #0095c8; width:100%">
    		<ul class="messageWidthIcon">
    			<li>
    			<span class="iconSet iconSetFull iconSetFullStyleThree" style="background:#0095c8">
                  <em class="iconinformation"></em>
                 </span>
                 </li>
                 <c:if test="${not empty serviceOpsModel.wib && serviceOpsModel.wib}">
                    <li class="noticeBoxText " style="color:#0095c8;width:auto">&nbsp; &nbsp;&nbsp;This customer has Westpac Corporate Lending and Westpac Live access and any actions taken could affect on both.</li>
                 </c:if>
                 <c:if test="${not empty serviceOpsModel.wib && (not serviceOpsModel.wib)}">
                    <li class="noticeBoxText " style="color:#0095c8;width:auto">&nbsp; &nbsp;&nbsp;The Customer Has Westpac Live Access and Any actions taken could affect both Panorama and Westpac</li>
                 </c:if>

    		</ul>
    	</div>
    </c:if>
    <c:if test="${not empty serviceOpsModel.wib && !(not serviceOpsModel.wib) && (not serviceOpsModel.westpacLive )}">
            <div class="noticeBox successBox"style="border:3px solid #0095c8; width:100%">
        		<ul class="messageWidthIcon">
        			<li>
        			<span class="iconSet iconSetFull iconSetFullStyleThree" style="background:#0095c8">
                      <em class="iconinformation"></em>
                     </span>
                     </li>
                        <li class="noticeBoxText " style="color:#0095c8;width:auto">&nbsp; &nbsp;&nbsp;This customer has Westpac Corporate Lending access and any actions taken could affect them on both Panorama<br>&nbsp; &nbsp;&nbsp;and Westpac.</li>
        		</ul>
        	</div>
        </c:if>
    <div class="sectionContentItem sectionContentItemStyleOne clearFix">
    <!-- First half DETAILS -->
    <div class="setHalf setHalfMod3 lastBorder">
        <div>
        <h3 class="sectionGroupHeader">Details</h3>
        <dl class="dataSummary">
            <dt class="listContentItemHeader clearBoth  emphasis">Registered since</dt>
                <dd>${serviceOpsModel.registeredSince}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">Dealer Group/Company</dt>
                <dd>
                <c:forEach items="${serviceOpsModel.dealerGroupList}" var="dealerGroup">
                   ${dealerGroup}
                </c:forEach>
                <c:if test="${not empty serviceOpsModel.companyName}">
                   <br/>${serviceOpsModel.companyName}
                </c:if>
                </dd>
            <dt class="listContentItemHeader clearBoth emphasis">Practice</dt>
                <dd>${serviceOpsModel.practiceName}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">Date of birth</dt>
                <dd>${serviceOpsModel.dob}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">Username</dt>
                <dd>${serviceOpsModel.userName}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">PAN No./CIS Key/Z-Number</dt>
                <dd>${not empty serviceOpsModel.gcmId ? serviceOpsModel.gcmId : "-"}/${not empty serviceOpsModel.cisId ? serviceOpsModel.cisId : "-" }/${not empty serviceOpsModel.westpacCustomerNumber ? serviceOpsModel.westpacCustomerNumber : "-"}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">User ID</dt>
                <dd>${serviceOpsModel.userId}</dd>
            <dt class="listContentItemHeader clearBoth emphasis">PPID(EAM)</dt>
               <dd>${not empty serviceOpsModel.ppId ? serviceOpsModel.ppId : "-" }</dd>
            <dt class="listContentItemHeader clearBoth emphasis">PPID(Avaloq)</dt>
                <dd>${not empty serviceOpsModel.ppIdFromAvaloq ? serviceOpsModel.ppIdFromAvaloq : "-" }</dd>
            <c:if test="${serviceOpsModel.role != 'INVESTOR' }">
                <dt class="listContentItemHeader clearBoth emphasis">Role</dt>
                <dd>${serviceOpsModel.role}</dd>
            </c:if>
             <c:if test="${not empty serviceOpsModel.phone }">
              <dt class="listContentItemHeader clearBoth emphasis">Phone</dt>
                <dd class="listContentItemText"  >
                    <c:forEach items="${serviceOpsModel.phone}" var="landLine">
                       <span class="emphasis"><em class="iconprimary iconprimaryMod1 iconAlignMod1"><span>Primary contact method</span></em>
                       <span data-ng-key="phone"> ${landLine.number}</span><br />
                    </c:forEach>
                </dd>
             </c:if>

              <dt class="listContentItemHeader clearBoth emphasis">Mobile</dt>
              <dd class="listContentItemText listContentItemTextMod2">
                <c:choose>
                    <c:when test="${not empty serviceOpsModel.mobilePhones}">
                        <c:forEach items="${serviceOpsModel.mobilePhones}" var="mobilelist">
                        <!--Primary mobile number will be SmsIdentifier only-->
                          <c:if test="${(mobilelist.type == 'MOBILE_PHONE_PRIMARY')}">
                              <c:if test="${(fn:length(serviceOpsModel.mobilePhones) gt 1)}">

                                  <div class="listContentItemTextItem" data-ng-key="mobile"> <span class="emphasis">
                                  <c:if test="${mobilelist.preferred}"> <em
                                          class="iconprimary iconprimaryMod1 iconAlignMod1"><span>Primary contact method</span></em>
                                  </c:if>${mobilelist.number}</span><br/>

                                          <%--   <c:if test="${mobilelist.smsIdentifier}"> --%> (for SMS security
                                      codes) <%-- </c:if> --%>
                                  </div>
                              </c:if>

                              <c:if test="${(fn:length(serviceOpsModel.mobilePhones) eq 1)}">

                                  <div class="listContentItemTextItem" data-ng-key="mobile"> <span class="emphasis">
                                  <c:if test="${mobilelist.preferred}"> <em
                                          class="iconprimary iconprimaryMod1 iconAlignMod1"><span>Primary contact method</span></em>
                                  </c:if>${mobilelist.number}</span><br/>

                                          <%--  <c:if test="${mobilelist.smsIdentifier}"> --%> (for SMS security
                                      codes) <%-- </c:if> --%>
                                  </div>
                              </c:if>

                          </c:if>
                          <c:if test="${mobilelist.preferred && !(mobilelist.type == 'MOBILE_PHONE_PRIMARY')}">
                              <em class="iconprimary iconprimaryMod1"></em><span
                                  data-ng-key="mobile">${mobilelist.number}</span><br/>
                          </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="listContentItemTextItem" data-ng-key="mobile">
                            <span class="emphasis">
                                <em class="iconprimary iconprimaryMod1 iconwarningCircle">
                                    <span>Warning</span>
                                </em>
                                <span>Missing</span>
                            </span>
                        </div>
                    </c:otherwise>
                </c:choose>
              </dd>
              <dt class="listContentItemHeader clearBoth emphasis">Email</dt>
              <dd class="listContentItemText">
                <c:choose>
                    <c:when test="${not empty serviceOpsModel.email}">
                        <c:forEach items="${serviceOpsModel.email}" var="emailList">
                          <c:if test="${emailList.type == 'EMAIL_PRIMARY'}">
                            <div class="listContentItemTextItem" data-ng-key="email">
                              <c:if test="${(fn:length(serviceOpsModel.email) gt 1)}">
                                 <ul class="listContent listContentMod2 clearFix">
                                        <li>
                                             <c:if test="${emailList.preferred}"> <em class="iconprimary iconprimaryMod1 iconAlignMod1"><span>Primary contact method</span></em> </c:if>
                                             <a href="mailto:${emailList.email}" class="emphasis listContentItemTextItemWrap baseLink">${emailList.email}</a><br/>
                                              (Primary E-Mail ID)
                                        </li>
                                    </ul>

                              </c:if>
                              <c:if test="${(fn:length(serviceOpsModel.email) eq 1)}">
                              <c:if test="${emailList.preferred}"> <em class="iconprimary iconprimaryMod1 iconAlignMod1"><span>Primary contact method</span></em> </c:if>
                              <a href="mailto:${emailList.email}" class="emphasis listContentItemTextItemWrap baseLink">${emailList.email}</a><br/>
                              (Primary E-Mail ID)
                              </c:if>
                            </div>
                          </c:if>

                          <c:if test="${emailList.preferred && !(emailList.type == 'EMAIL_PRIMARY')}">

                            <div class="listContentItemTextItem" data-ng-key="email">
                                <em class="iconprimary iconprimaryMod1 iconAlignMod1"><span>Primary contact method</span></em>
                                <a href="mailto:${emailList.email}" class="emphasis listContentItemTextItemWrap baseLink">${emailList.email}</a><br/>
                            </div>
                          </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="listContentItemTextItem" data-ng-key="email">
                            <span class="emphasis">
                                <em class="iconprimary iconprimaryMod1 iconwarningCircle">
                                    <span>Warning</span>
                                </em>
                                <span>Missing</span>
                            </span>
                        </div>
                    </c:otherwise>
                </c:choose>
              </dd>

            <dt class="listContentItemHeader clearBoth emphasis">Business address</dt>
                <dd>
                <c:if test="${not empty serviceOpsModel.residentialAddress.floorNumber }">
                    FL ${serviceOpsModel.residentialAddress.floorNumber}
                </c:if>
                <c:if test="${not empty serviceOpsModel.residentialAddress.unitNumber }">
                    U ${serviceOpsModel.residentialAddress.unitNumber}
                </c:if>
	            ${serviceOpsModel.residentialAddress.addressLine1}

                <c:if test="${not empty serviceOpsModel.residentialAddress.buildingName }">
                    ${serviceOpsModel.residentialAddress.buildingName}
                </c:if>

                <c:choose>
                    <c:when test="${not empty serviceOpsModel.residentialAddress.city }">
                        ${serviceOpsModel.residentialAddress.city}
                    </c:when>
                    <c:otherwise>
                        ${serviceOpsModel.residentialAddress.suburb}
                    </c:otherwise>
                </c:choose>

	                ${serviceOpsModel.residentialAddress.state}
	                ${serviceOpsModel.residentialAddress.pin}
	                ${serviceOpsModel.residentialAddress.country}
                </dd>
            <dt class="listContentItemHeader last clearBoth emphasis">Postal<br/> address</dt>
                <dd>
                <c:if test="${not empty serviceOpsModel.postalAddress.floorNumber }">
                    FL ${serviceOpsModel.postalAddress.floorNumber}
                </c:if>
                <c:if test="${not empty serviceOpsModel.postalAddress.unitNumber }">
                    U ${serviceOpsModel.postalAddress.unitNumber}
                </c:if>
                    ${serviceOpsModel.postalAddress.addressLine1}

	                <c:if test="${not empty serviceOpsModel.postalAddress.buildingName }">
                        ${serviceOpsModel.postalAddress.buildingName}
                    </c:if>

	                <c:if test="${not empty serviceOpsModel.postalAddress.boxPrefix && not empty serviceOpsModel.postalAddress.poBoxNumber }" >
                    	    ${serviceOpsModel.postalAddress.boxPrefix}
                    	    ${serviceOpsModel.postalAddress.poBoxNumber}
                    </c:if>

	                <c:choose>
                        <c:when test="${not empty serviceOpsModel.postalAddress.city }">
                            ${serviceOpsModel.postalAddress.city}
                        </c:when>
                        <c:otherwise>
                            ${serviceOpsModel.postalAddress.suburb}
                        </c:otherwise>
                    </c:choose>

	                ${serviceOpsModel.postalAddress.state}
	                ${serviceOpsModel.postalAddress.pin}
	                ${serviceOpsModel.postalAddress.country}
                </dd>
        </dl>
    </div>
    </div>

    <%--get message from model attribute when it is implemented in controller and remove set message in each c when--%>
    <%--<c:set var="message" value="${message ? message :''}">--%>

     <c:choose>
            <c:when test="${serviceOpsModel.action == 'Account creation incomplete'}">
                <c:set var="messageBoxClass" value ="informationBox"/>
                <c:set var="statusTextClass" value ="info"/>
                <c:set var="defaultMessage"><cms:content name="uim0132"/></c:set>
                <c:set var="message" value ="${not empty message ? message : defaultMessage}"/>
                <c:if test="${empty serviceOpsModel.canCreateAccount}"><c:set var="message"><cms:content name="uim99132"/></c:set></c:if>
                <c:set var="defaultAction" value =""/>
            </c:when>
            <c:when test="${serviceOpsModel.action == 'Unregistered'}">
                <c:set var="messageBoxClass" value ="informationBox"/>
                <c:set var="statusTextClass" value ="info"/>
                <c:set var="defaultMessage"><cms:content name="uim0132"/></c:set>
                <c:set var="message" value ="${not empty message ? message : defaultMessage}"/>
                <c:set var="defaultAction" value ="RESEND_REGISTRATION_EMAIL"/>
            </c:when>
            <c:when test="${serviceOpsModel.loginStatus.group == 'BLOCKED'}">
                <c:set var="messageBoxClass" value ="warningBox"/>
                <c:set var="statusTextClass" value ="warning"/>
                <c:set var="defaultMessage"><cms:content name="uim0127"/></c:set>
                <c:set var="message" value ="${not empty message ? message : defaultMessage}"/>
                <c:set var="defaultAction" value ="UNBLOCK_ACCESS"/>
            </c:when>
             <c:when test="${serviceOpsModel.action == 'Has Temporary Password'}">
                <c:set var="messageBoxClass" value ="successBox"/>
                <c:set var="statusTextClass" value ="positive"/>
                <c:set var="defaultMessage"><cms:content name="uim0126"/></c:set>
                <c:set var="message" value ="${not empty message ? message : serviceOpsModel.message}"/>
                <c:set var="defaultAction" value =""/>
            </c:when>
            <c:when test="${serviceOpsModel.loginStatus.group == 'ACTIVE'}">
                <c:choose>
                    <c:when test="${not empty serviceOpsModel.mandatoryDetailMissing && serviceOpsModel.mandatoryDetailMissing}">
                    <c:set var="messageBoxClass" value ="informationBox"/>
                    <c:set var="defaultMessage"><cms:content name="uim0140"/></c:set>
                    <c:set var="statusTextClass" value ="info"/>
                    <c:set var="message" value ="${not empty message ? message : defaultMessage}"/>
                    </c:when>
                <c:otherwise>
                    <c:set var="messageBoxClass" value ="successBox"/>
                    <c:set var="statusTextClass" value ="positive"/>
                    <c:set var="message" value ="${not empty message ? message : ''}"/>
                </c:otherwise>
                </c:choose>
                 <c:set var="defaultAction" value ="SIGN_IN_AS_USER"/>
            </c:when>
            <c:when test="${serviceOpsModel.loginStatus.group == 'SUSPENDED'}">
	            <c:set var="messageBoxClass" value ="warningBox"/>
	            <c:set var="statusTextClass" value ="warning"/>
	            <c:set var="defaultMessage"><cms:content name="uim0127"/></c:set>
	            <c:set var="message" value ="${not empty message ? message : defaultMessage}"/>
	            <c:set var="defaultAction" value =""/>
            </c:when>
            <c:when test="${(serviceOpsModel.loginStatus.group == 'LOCKED')}">
	            <c:set var="messageBoxClass" value ="warningBox"/>
	            <c:set var="statusTextClass" value ="warning"/>
	            <c:set var="defaultMessage"><cms:content name="uim0127"/></c:set>
	            <c:set var="message" value ="${not empty message ? message : defaultMessage}"/>
	            <c:set var="defaultAction" value =""/>
            </c:when>
            <c:otherwise>
	            <c:set var="messageBoxClass" value ="informationBox"/>
	            <c:set var="statusTextClass" value ="info"/>
	            <c:set var="message" value ="${not empty message ? message :serviceOpsModel.message}"/>
	            <c:set var="defaultAction" value =""/>
            </c:otherwise>
        </c:choose>

    <!-- Second half ACTION-->
    <div class="setHalf setHalfMod4">
        <div class="sectionContentItemContainer jq-clientDetailActionWrap">
            <form:form name="serviceOps_form" id="serviceOps" method="POST" action="submitAction" commandName="serviceOpsModel" class="jq-clientDetailActionForm">
            	<form:input id="userName" path="userName" type="hidden" value='${serviceOpsModel.userName}' name="userName"/>
            	<form:input id="role" path="role" type="hidden" value='${serviceOpsModel.role}' name="role"/>
            	<form:input id="userId" path="userId" type="hidden" value='${serviceOpsModel.userId}' name="userId"/>
            	<form:input id="primaryMobileNumber" path="primaryMobileNumber" type="hidden" value='${serviceOpsModel.primaryMobileNumber}' name="primaryMobileNumber"/>
            	<form:input id="firstName" path="firstName" type="hidden" value='${serviceOpsModel.firstName}' name="firstName"/>
                <form:input id="lastName" path="lastName" type="hidden" value='${serviceOpsModel.lastName}' name="lastName"/>
            	<form:input id="action" path="action" type="hidden" value='${serviceOpsModel.action}' name="action"/>
            	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken"/>
                <h3 class="sectionGroupHeader">Actions</h3>
                <dl class="dataSummary endBorder dataSummaryExtended">
                    <dt class="emphasis">Smartclient status:</dt>
                    <dd>
	                     <c:if test="${serviceOpsModel.avaloqStatusReg}"><span class="positive emphasis">Registered</span> </c:if>
	                     <c:if test="${!(serviceOpsModel.avaloqStatusReg)}"><span class="warning emphasis">Unregistered</span></c:if>
	                     <c:if test="${serviceOpsModel.terminatedFlag}"><span class="warning emphasis"> (Terminated)</span> </c:if>
                    </dd>

                    <dt class="emphasis">On-line status:</dt>
                    <dd><span class="${statusTextClass} emphasis">${serviceOpsModel.action}</span></dd>

                    <c:if test="${serviceOpsModel.role == 'INVESTOR' && not empty serviceOpsModel.onboardingStatus}">
                    	 <dt class="emphasis">Onboarding status:</dt>
                    	 <dd><span class="positive emphasis">${serviceOpsModel.onboardingStatus}</span></dd>

                    	 <c:if test="${not empty serviceOpsModel.onboardingFailureReason}">
                    	 	<dt class="emphasis">Reason for failure:</dt>
                    	 	<dd><span class="warning emphasis">${serviceOpsModel.onboardingFailureReason}</span></dd>
                    	 </c:if>
                    </c:if>
                </dl>
                   <p class="clearFix"></p>
<!--Below Condition will change the message box text and colour when get an error response from UI -->
                   <c:if test="${not empty STATUS && STATUS == 'ERROR'}">
	                    <c:set var="messageBoxClass" value ="warningBox"/>
	                    <c:set var="message" value ="${errorMessage}"/>
                   </c:if>
                    <c:if test="${message != ''}">
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
                        <c:choose>
                            <c:when test="${serviceOpsModel.action == 'Account creation incomplete'}">
                                <li class="setTopGutter">
                                    <input type="hidden" name="actionValue" value="CREATE_ACCOUNT"/>
					 					<c:choose>
											<c:when test="${serviceOpsModel.role == 'INVESTOR'}">
												<a href="#nogo"
													class="primaryButton jq-formSubmit ${not empty actionPerformed? 'primaryButtonDisabled jq-disabled' : ''} ${serviceOpsModel.canCreateAccount == true ? '' : 'primaryButtonDisabled jq-disabled'}">Create
													account</a>
											</c:when>
											<c:otherwise>
												<a href="#nogo"
													class="primaryButton jq-formSubmit ${not empty actionPerformed? 'primaryButtonDisabled jq-disabled' : ''} ${serviceOpsModel.canCreateAccount == true ? '' : 'primaryButtonDisabled jq-disabled'}">Create
													account</a>
										 	</c:otherwise>
										</c:choose>
									</li>
                            </c:when>
                        <c:otherwise>
                            <li class="formBlock">
                                <label for="selectAnAction" class="formLabel">Select an action</label>
                                <select name="actionValue" id="selectAnAction" class="jq-serviceOperatorSelectAction">
                                    <c:forEach items="${serviceOpsModel.actionValues}" var="actionValue">
                                        <option value="${actionValue.key}">${actionValue.value}</option>
                                    </c:forEach>
                                </select>
                            </li>
                            <li class="setTopGutter clearBoth"><a href="#nogo" class="primaryButton jq-formSubmit jq-detailPageActionSubmitButton ${not empty actionPerformed ? 'jq-cloak' : ''}  ${not empty serviceOpsModel.canPerformAction ? 'primaryButtonDisabled jq-disabled' : ''}">Go</a></li>
                        </c:otherwise>
                    </c:choose>
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

<div class="modalContent jq-cloak jq-confirmMobileNumberModal">
    <form method="POST" action="submitConfirmMobile" class="jq-confirmMobileNumberForm">
    <%-- <input type="hidden" value="<c:out value='${serviceOpsModel.secretKey}'/>" name="secret"/> --%>
    <input type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken"/>
    <input type="hidden" value="<c:out value='${serviceOpsModel.mobileNumber}'/>" name="mobile"/>
   <%--  <input type="hidden" value="<c:out value='${serviceOpsModel.userId}'/>" name="userId"/> --%>
    <%-- hard coded safiDeviceId now .. later on will grab it from avaloq .. from serviceOpsModel.safiDeviceId--%>
   <!--  <input type="hidden" value="0012345822" name="safiDeviceId"/> -->
        <div class="modalContentMod3">
         <h1 class="formHeaderModal">
            <span class="baseGama"> Confirm mobile number</span> to be used for security
        </h1>
        <h2 class="mainHeaderItemMod9">${serviceOpsModel.mobileNumber}</h2>
        <p>(this is the primary number entered in Smart Client)</p>
        <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
            <li>
                <a href="#nogo" class="primaryButton jq-formSubmit" title="Confirm">Confirm</a>
            </li>
            <li>
                <a href="requireMobileConfirmation?mobile=${serviceOpsModel.mobileNumber}" class="baseLink baseLinkClear" title="Cancel">
                    <em class="iconlink"></em>
                    <span class="iconLinkLabel">Cancel</span>
                </a>
            </li>
        </ul>
        <div class="noticeBox infoBoxMod3">
            <ul class="noticeBoxWrapper">
                <li>
                    <span class="iconSet iconSetFull iconSetFullStyleFour">
                        <em class="iconinformation"></em>
                    </span>
                </li>
                <li class="noticeBoxText emphasis"><p><cms:content name="uim0129"/></p></li>
            </ul>
          </div>
        </div>
    </form>
 </div>

 <div class ="modalContent jq-cloak jq-updatePPIDModal">
<form method="POST" action="updateppid" class="jq-updatePPID">
<input type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken"/>
        <div class="modalContentMod3">
         <h1 class="formHeaderModal">
            <span class="baseGama"> Update PPID for adviser
        </h1>
        <h2 class="mainHeaderItemMod9">${serviceOpsModel.mobileNumber}</h2>
         <span class="inputWrapper" >
        <input id="PPIDText" name="ppid" type="text" class="formTextInput inputStyleNine"
        style="width:400px"
        maxLength="20"
        data-validation="validate[required],custom[signedInteger]"
        data-validation-required-error="Please Enter PPID"
        data-validation-signedInteger-error="Please Enter Only Numbers"/>
        </span>
        <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
            <li>
                <a href="#nogo" class="primaryButton jq-formSubmit" title="Update">Update</a>
            </li>
            <li>
                <a href="updatePPIDModal" class="baseLink baseLinkClear" title="Cancel">
                    <em class="iconlink"></em>
                    <span class="iconLinkLabel">Cancel</span>
                </a>
            </li>
        </ul>
        </div>
    </form>

 </div>