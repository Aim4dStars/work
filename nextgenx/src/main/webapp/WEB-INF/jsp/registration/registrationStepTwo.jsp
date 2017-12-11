<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>


<body class="bodyContainer flexContainer bg-blurred" data-liveperson="true">
	<!--<header></header>-->
    <spring:eval var="env" expression="@environment.getProperty('environment')" />
    <spring:eval var="buildNumber" expression="@environment.getProperty('nextgen.version')" />
    <div class="layoutContainer BlueContainer">
    	<div class="panoramaLogoWrapper">
            <div class="panoramaLogo">&nbsp;</div>
        </div> 
    <div class="noDisplay jq-FormErrorMessage customFormError errorStyle"></div>	  
   		<aside class="signinContainerWrap">
        	<div class="signinContainer">
            	<div class="jq-mainContent">	           
				 	<div class="noDisplay">
 						<ul>
              				<li class="noDisplay">
                             	<em class="iconlink"></em><a href="#jq-logon" title="Sign in" class="" data-ng-key="signin">Sign in</a>
                      		</li>
                        	<li>
                            	<em class="iconlink"></em><a href="#jq-register" title="Register" class="" data-ng-key="register">Register</a>
                        	</li>
							<li>
								<em class="iconlink"></em><a href="#jq-passwordReset1" title="Password Reset" class="" data-ng-key="signin">Password Reset</a>
							</li>						
                    	</ul>
                  	</div>
					<div id="jq-register" class="jq-registerContainer">	
						<div class="jq-registerStepTwo noDisplay" data-ng-key="registerStepTwo">
							<form name="form" data-ajax="false"
								action="com.bt.nextgen.login.web.model.RegistrationModel"
								method='POST' class="jq-registerStepTwoForm jq-preventSubmit" data-ajax-submit-url="" data-action="">
								<input type="hidden" name="jq-registrationCode" id="registrationCode" value="${username}"/>
							<fieldset>
								<legend>Register</legend>
								<h3 class="header-statement heading-two">
									<span class="color-white registerPlaceholder"></span>
								</h3>
								<h4 class="heading-seven">Step 2 of 2</h4>
								<div data-ng-key="messageBox" class="noDisplay noticeBox warningBox jq-noticeBoxTC">
                        <ul class="noticeBoxWrapper">
                            <li><span class="messageIcon"><em class="iconItem"></em></span></li>
                            <li class="noticeBoxText" role="alert">
                                <span class="noticeDescTxt jq-termsCondErrMessage">message</span>
                            </li>
                        </ul>
                    </div>
						<ul>
							<li class="margin-bottom-1">
								<span class="bubbleContainer">
									<div class="helpToolBox helpToolBoxPos1 noDisplay jq-usernamePolicyHintsContainer"
										id="nameCheck" data-ng-key="usernamePolicyHint">
										<em class="iconArrowHelpLeft"></em>
										<div>
											<div class="heading-ten">
												<span>Must be</span>
											</div>
											<ul class="helpBox">
												<li class="jq-minLength jq-maxLength">
													<em class="iconbullet"></em> <span>Between 8-50 characters, but cannot just be numbers</span>
												</li>
											</ul>
											<div class="heading-ten sub-heading">
												<span>Can be or include</span>
											</div>
											<ul>
												<li>
													<em class="iconbullet"></em> 
													<span>Your own name (no spaces)</span>
												</li>
												<li>
													<em class="iconbullet"></em>
													<span>An email address</span>
												</li>
												<li>
													<em class="iconbullet"></em> 
													<span>A combination of letters, numbers or special characters # @ ! ^ $ or *</span>
												</li>
											</ul>
										</div>
									</div>
								</span>
							<span>
							<div data-view-component="inputtext" data-component-name="username"
								data-directive-processed="true"
								class="jq-appendErrorAfter createusername">
								<div class="validation-container">
									<!--<span class="label">Create username</span>-->
									<input class="text-input jq-registerUsername " name="newUserName"
										id="username" placeholder="Create username" value="" type="text"
										data-validation-ajax-error="<cms:content name="Err.IP-0304"/>"
										data-validation-highlight-on-success="true"
										data-ng-key="registerUsername" autocomplete="off"
										placeholder="" maxlength="50" data-stickit-id="stickit_23"
										data-view-value=""
										data-validation="validate[required,customFunction,custom[ajax]]"
										data-validation-customFunction-error="<cms:content name="Err.IP-0339"/>"
										data-validation-required-error="<cms:content name="Err.IP-0297"/>"
										data-validation-minLength-error="<cms:content name="Err.IP-0339"/>"
										data-validation-maxLength-error="<cms:content name="Err.IP-0339"/>"
										data-validation-nonAlphaNumericCharacter-error="<cms:content name="Err.IP-0339"/>">
									<span class="icon-notification-success noDisplay"></span>
								</div>
							</div>
						</span>
					</li>
					<li class="margin-bottom-1">
						<span class="bubbleContainer">
							<div class="helpToolBox helpToolBoxPos2 noDisplay jq-passwordPolicyHintsContainer"
								id="passwordCheck" data-ng-key="passwordPolicyHint">
								<em class="iconArrowHelpLeft"></em>
								<div>
									<div class="heading-ten">
										<span>Must be at least</span>
									</div>
									<ul class="helpBox">
										<li class="jq-alphabeticCharacter">
											<em class="iconbullet"></em>
											<span>One Letter</span>
										</li>
										<li class="jq-nonAlphabeticCharacter">
											<em class="iconbullet"></em> 
											<span>One number or special character</span>
										</li>
										<li class="jq-minLength">
											<em class="iconbullet"></em>
										 	<span>8 Characters</span>
										</li>
									</ul>
									<div class="heading-ten sub-heading">
										<span>Cannot include</span>
									</div>
									<ul class="">
										<li>
											<em class="iconbullet"></em>
											<span>Your user name</span>
										</li>
									</ul>
								</div>
							</div>
						</span>
						<span>
							<div data-view-component="inputtext" data-component-name="password"
								data-directive-processed="true"
								class="jq-appendErrorAfter createpassword">
								<div class="validation-container">
									<!--<span class="label">Create password</span>-->
									<input class="formTextInput jq-registerPassword" name="password"
										id="password" placeholder="Create password" value="" type="password"
										data-validation="validate[required,customFunction]"	
										data-validation-required-error="<cms:content name="Err.IP-0298"/>"
										data-validation-customFunction-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-minLength-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-maxLength-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-validChars-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-spaces-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-consecutiveCharacters-error="<cms:content name="Err.IP-0325"/>"
                                		data-validation-consecutiveNumbers-error="<cms:content name="Err.IP-0325"/>"
                                		data-validation-userNameInString-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-nonAlphabeticCharacter-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-alphabeticCharacter-error="<cms:content name="Err.IP-0319"/>"
										data-validation-highlight-on-success="true"										
										data-ng-key="registerNewPassword" autocomplete="off"
										placeholder="" maxlength="32" /> 
										<span class="icon-notification-success noDisplay"></span>									
								</div>
							</div>
						</span>
					</li>
					<li class="margin-bottom-1">						
							<div data-view-component="inputtext" data-component-name="confirmPassword"
								data-directive-processed="true"
								class="jq-appendErrorAfter confirmpassword">
								<div class="validation-container">
									<!--<span class="label">Repeat password</span>-->
									<input class="formTextInput jq-registerConfirmPassword"
										name="confirmPassword" id="confirmPassword" placeholder="Repeat password" value="" type="password" data-validation="validate[required,custom]"
										data-validation-required-error="<cms:content name="Err.IP-0305"/>"
										data-validation-custom-error="<cms:content name="Err.IP-0321"/>"
										data-validation-highlight-on-success="true"
										data-ng-key="registerConfirmPassword" autocomplete="off"
										placeholder="" maxlength="32" data-stickit-id="stickit_23"
										data-view-value="">
									<span class="icon-notification-success noDisplay"></span>
								</div>
							</div>						
					</li>
                    <c:choose>
                    <c:when test="${showTerms == 'true'}">
                       <li class="margin-bottom-1">
                    </c:when>
                    <c:otherwise>
                        <li class="margin-bottom-1">
                    </c:otherwise>
                    </c:choose>
                    	<div>
                    		<c:if test="${showTerms == 'true'}">
                    				<div class="jq-adviserTermsCondWrapper">
				                       	<input type="checkbox" data-ng-key="reg-termsAndCondition" id="regStepTC1" name="tncAccepted" class="jq-formTcCheckBox" />
				                       	<label class="formLabelCheckBox" for="regStepTC1" rel="external">I agree to the
				                       	<c:if test="${empty userRole}">
    							  			<a href="<cms:content name="AdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
    							  		</c:if>
    							  		<c:if test="${userRole == null}">
    							  			<a href="<cms:content name="AdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
    							  		</c:if>
    							  		<c:if test="${userRole == 'Adviser'}">
    							  			<a href="<cms:content name="AdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
    							  		</c:if>
    							  		<c:if test="${userRole == 'Paraplanner'}">
    							  			<a href="<cms:content name="AdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
    							  		</c:if>
    							  		<c:if test="${userRole == 'Assistant'}">
    							  			<a href="<cms:content name="AdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
    							  		</c:if>
										<c:if test="${userRole == 'Investment_manager'}">
    							  			<a href="<cms:content name="InvestmentAdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
    							  		</c:if>
    							  		<c:if test="${userRole == 'Practice_manager'}">
                                            <a href="<cms:content name="AdviserTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
                                        </c:if>
                                        <c:if test="${userRole == 'Dealer_group_manager'}">
                                            <a href="<cms:content name="DealerGroupTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
                                        </c:if>
                                        <c:if test="${userRole == 'Accountant'}">
											<a href="<cms:content name="AccountantTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
										</c:if>
										<c:if test="${userRole == 'Accountant_support_staff'}">
                                        	<a href="<cms:content name="AccountantTermsAndConditions"/>" title="Terms and Conditions" class="text-link" target="_blank">
                                        </c:if>
                                        terms and conditions
    							  		</a>
    							  		</label>
				                       	<span class="noDisplay jq-termCondCheckboxErrorFix"></span>
			                       </div>
			                       <span>
			                       		<input type="hidden" name="termAndConditionHidden" class="jq-regTermsAndCondHidden" data-validation-required-error="${errors.err00052}" value="false" />
			                       </span> 
							</c:if>
                    	</div>
                    </li>
					<li>
						<span data-view-component="button" data-component-name="disabledprimaryaction" data-directive-processed="true" class="view-disabledprimaryaction disabled">
    						<button type="submit" class="btn-action-primary disabled jq-registerStepTwoSubmitButton jq-inactive jq-formSubmit" tabindex="-1" data-ng-key="saveAndSignin">
        						<span class="button-inner">
                            		<span class="label-content">Sign in</span>
                            		<span class="icon-wrapper">
                            			<span class="icon iconWLoader noDisplay"></span>
                            		</span>
        						</span>
    						</button>
						</span>												
					</li>
				</ul>
			</fieldset>
			<input type="hidden" name="deviceToken" value="" class="jq-deviceToken"/>
            <input type="hidden" name="${halgmFieldName}" value="" class="jq-halgmField"/>
            <input type="hidden" name="${brandFieldName}" value="${logonBrand}" class="jq-brandField"/>
		</form>
		<div class="doNotContainer">
			<h5 class="heading-six">
				You must follow these security requirements in relation to your Password:
			</h5>
			<ul class="unordered-list">
				<li>You must not disclose your Password to
					anyone, including a family member or a friend.</li>
				<li>You must not keep a written record of
					the Password without making a reasonable attempt to protect its
					security (either by disguising it or preventing unauthorised access
					to it).</li>
				<li>You must avoid acting with extreme
					carelessness in failing to protect the security of the Password.</li>
				<li>You must not select a numeric Password
					that represents your birth date, or an alphabetical Password that
					is a recognisable part of your name.</li>
			</ul>
			<p>If you do not follow these requirements, you will be liable
				for loss in additional circumstances as set out in the Westpac
				Payment Services Terms and Conditions.</p>

		</div>
		<div class="clearBoth"></div>		
	</div>
</div>
</div>
</div>
</aside>
</div>
 	<!--<div class="bottomFooter">
    	<span>Powered by BT Panorama</span>
 	</div> -->
<input type="hidden" name="isAdvisorHidden" class="jq-isAdvisorHidden" value="${showTerms}" />
<input type="hidden" name="isUserRole" class="jq-isUserRole" value="${userRole}" />
<!--<div class="jq-registerBusyDialog logonBusyDialog">
    <h3 class="heading-two">Signing you in... <em class="iconLoader"></em></h3>
</div>-->
<script language="javascript" type="text/javascript">
	org.bt.cryptoUrl = '${obfuscationUrl}';
	$('.panoramaLogo').css('right', '250px').css('transition', 'all 0.2s linear');
    $('.jq-registerStepTwo').css('right', '230px').css('transition', 'all 0.2s linear');
    setTimeout(function(){
        $('.jq-registerStepTwo').removeClass('noDisplay');
   	},400);
</script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/client/desktop/pages/registrationStepTwo.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/liveperson/le-mtagconfig.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/liveperson/livepersonvendor.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/adobe/omniture/analytics_btpanorama.js'/>"></script>
<script language="javascript" type="text/javascript">
    var pageDetails;
     pageDetails = {
         "pageName": "register:step 2",
         "pageType": "registration",
         "formName": "register",
         "pageStep": "complete"
     };
     if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1) {
        window.wa && wa('page', pageDetails);
     }
</script>
</body>

