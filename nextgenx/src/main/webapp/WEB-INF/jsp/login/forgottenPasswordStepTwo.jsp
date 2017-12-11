<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
    	<div class="noDisplay jq-FormErrorMessage customFormError errorStyle"></div>
    	<c:if test="${userType!=undefined && userType!=null && userType == 'WPL_USER'}">
            <div class="globalErrorMessage">
                <div data-view-component="messagealert" data-directive-processed="true" class="view-messagealert_2 noticeBox informationBox" data-ng-key="messageBox">
                    <div class="response-message alert  message-alert-dynamic" role="alert">
                        <span class="icon-container-outer">
                            <span class="icon-container information">
                                <span class="icon icon-support-information"></span>
                            </span>
                        </span>
                        <span class="message infoMessage">
                            <p><cms:content name="UIM-IP-0101"/></p>
                        </span>
                    </div>
                </div>
            </div>
        </c:if>
    	<div class="panoramaLogoWrapper">
            <div class="panoramaLogo">&nbsp;</div>
        </div>
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
                  	
					
						<div class="jq-passwordResetStepTwo noDisplay" data-ng-key="passwordResetStepTwo">
						  
							<form name="form" data-ajax="false" action="registerUser"
								method='POST' class="jq-passwordResetStepTwoForm"
								data-ajax-submit-url="" data-action="">
								<div class="noDisplay jq-FormErrorMessage"></div>
						<fieldset>
							<legend>Forgotten password</legend>
							<h2 class="header-statement heading-two">
								<span class="color-white">Forgotten password</span>
							</h2>
							<h3 class="heading-seven">Step 2 of 2</h3>
						<ul>
							<li class="margin-bottom-1">
								<div class="bubbleContainer">
									<div class="helpToolBox noDisplay helpToolBoxPos3 jq-passwordResetPasswordPolicyHintsContainer"
										id="passwordCheckReset"	data-ng-key="passwordResetPasswordPolicyHints">
										<em class="iconArrowHelpLeft"></em>
										<div>
											<div class="heading-ten jq-mustbeText">
												<span></span>
											</div>
											<ul class="helpBox">
												<li class="jq-alphabeticCharacter">
													<em class="iconbullet"></em>
													<span>One letter</span>
												</li>
												<li class="jq-number">
                                                    <em class="iconbullet"></em>
                                                    <span>One number</span>
                                                </li>
												<li class="jq-nonAlphabeticCharacter">
													<em class="iconbullet"></em>
													<span>One number or special	character</span>
												</li>
												<li class="jq-minLength">
													<em class="iconbullet"></em>
													<span></span>
												</li>
												<li class="jq-upperCase">
                                                    <em class="iconbullet"></em>
                                                    <span>Only uppercase letters</span>
                                                </li>
											</ul>
											<div class="heading-ten sub-heading">
												<span>Cannot Include</span>
											</div>
											<ul>
												<li class="jq-userName">
													<em class="iconbullet"></em> 
													<span>Your username</span>
												</li>
												<li class="jq-maxLength">
                                                    <em class="iconbullet"></em>
                                                    <span></span>
                                                </li>
                                                <li class="jq-lowerCase">
                                                    <em class="iconbullet"></em>
                                                    <span>Lowercase letters</span>
                                                </li>
											</ul>
										</div>
									</div>
								</div>
								<div data-view-component="inputtext" data-component-name="passwordResetNewPassword"
									data-directive-processed="true"
									class="jq-appendErrorAfter createpassword">
									<div class="validation-container">										
										<input type="password" id="passwordResetNewPassword" name="newpassword"
										class="formTextInput jq-passwordResetNewPassword"
										maxlength="32" placeholder="Create password"
										data-validation="validate[required,customFunction]"
										data-validation-required-error="<cms:content name="Err.IP-0298"/>"
										data-validation-customFunction-error="<cms:content name="Err.IP-0326"/>"
                                		data-validation-minLength-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-maxLength-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-validChars-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-spaces-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-consecutiveCharacters-error="<cms:content name="Err.IP-0325"/>"
                                		data-validation-consecutiveNumbers-error="<cms:content name="Err.IP-0325"/>"
                                		data-validation-userNameInString-error="<cms:content name="Err.IP-0319"/>"
                                		data-validation-nonAlphabeticCharacter-error="<cms:content name="Err.IP-0326"/>"
                                		data-validation-alphabeticCharacter-error="<cms:content name="Err.IP-0326"/>"
                                		data-validation-highlight-on-success="true"	
										data-ng-key="passwordResetNewPassword"
										data-stickit-id="stickit_23" data-view-value=""
										data-event="blur"
								 		"/>
										<span class="icon-notification-success noDisplay"></span>								
									</div>
								</div>
					</li>
					<li class="margin-bottom-2">
						<div data-view-component="inputtext" data-component-name="passwordResetConfirmPassword"
							data-directive-processed="true"
							class="jq-appendErrorAfter confirmpassword">
							<div class="validation-container">
								<input aria-describedby="passwordCheckReset" type="password"
									id="passwordResetConfirmPassword" name="confirmPassword"
									class="formTextInput jq-passwordResetConfirmPassword"
									maxlength="32" placeholder="Repeat password" data-validation="validate[required, custom]" data-validation-required-error="<cms:content name="Err.IP-0320"/>"
									data-validation-custom-error="<cms:content name="Err.IP-0321"/>"									
									data-validation-highlight-on-success="true"
									data-ng-key="passwordResetConfirmPassword"									
									data-stickit-id="stickit_23" data-view-value=""
									data-event="blur" /> 
									<!--  data-validation-required-error="${errors.err00033}"
									data-validation-custom-error="${errors.err00033}"-->
									
								<span class="icon-notification-success noDisplay"></span>
							</div>
						</div>
					</li>					
					<li>
						<span data-view-component="button" data-component-name="primaryaction" data-directive-processed="true" class="view-primaryaction">
    						<button type="submit" class="btn-action-primary jq-formSubmit jq-passwordResetStepTwoSubmitButton disabled" data-ng-key="passwordResetStepTwoSubmitButton">
        						<span class="button-inner">
                            		<span class="label-content">Sign in</span>
                            		<span class="icon-wrapper"><span class="icon iconWLoader noDisplay"></span></span>
        						</span>
    						</button>
						</span>													
						<span data-view-component="button" data-component-name="terhref"
							data-directive-processed="true" class="view-terhref">
							<a href="../../public/page/logon?TAM_OP=login" title="Cancel" class="btn-action-tertiary forgotPasswordStepTwoCancelBtn">
								<span class="button-inner">									
									<span class="label-content">Cancel</span>
									<span class="icon-wrapper">
										<span class="icon icon-close"></span>
									</span>
								</span>
							</a>
						</span>																
					</li>
				</ul>
			</fieldset>
			<input type="hidden" name="${halgmFieldName}" value="" class="jq-halgmField"/>
            <input type="hidden" name="${brandFieldName}" value="${logonBrand}" class="jq-brandField"/>
            <input type="hidden" name="token" value="" class="jq-tokenField"/>
		</form>
		<div class="doNotContainer">
			<h5 class="heading-six">You must follow these security
				requirements in relation to your Password:
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
</aside>
</div>
<!--<div class="bottomFooter">
    <span>Powered by BT Panorama</span>
</div>
<div class="jq-tmpPasswordBusyDialog logonBusyDialog">
    <h3 class="heading-two">Signing you in... <em class="iconLoader"></em></h3>
</div>-->
<script language="javascript" type="text/javascript">
    org.bt.cryptoUrl = '${obfuscationUrl}';
    $('.panoramaLogo').css('right', '250px').css('transition', 'all 0.2s linear');
    $('.jq-passwordResetStepTwo').css('right', '230px').css('transition', 'all 0.2s linear');
    setTimeout(function(){
        $('.jq-passwordResetStepTwo').removeClass('noDisplay');
   	},400);
</script>

<input type="hidden" class="jq-passwordResetUsername" value="<c:out value="${userId}"/>">
<script>var paramUserName ="<c:out value="${userId}"/>"</script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/client/desktop/pages/forgottenPassword.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/liveperson/le-mtagconfig.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/liveperson/livepersonvendor.js'/>"></script>
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/vendors/adobe/omniture/analytics_btpanorama.js'/>"></script>
<script language="javascript" type="text/javascript">
    var pageDetails;
     pageDetails = {
         "pageName": "forgotten password:step 2",
         "pageType": "selfservice",
         "formName": "forgotten password",
         "pageStep": "forgotpasswordcomplete"
     };
     if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1) {
        window.wa && wa('page', pageDetails);
     }
</script>
</body>

