<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="ng" uri="/WEB-INF/taglib/core.tld" %>

<div id="jq-passwordReset1">
	<div class="jq-passwordResetStepOne" data-ng-key="passwordRestStepOne">
		<form name="form" data-ajax="false" action="" method='POST'
			class="jq-passwordResetStepOneForm jq-preventSubmit"
			data-ajax-submit-url="" data-action="">
			<div class="noDisplay jq-FormErrorMessage customFormError"></div>
			<fieldset>
				<h1 class="header-statement heading-two">
					<span class="color-white">Forgotten password</span>
				</h1>
				<h1 class="heading-seven">Step 1 of 2</h1>
				<ul>
					<li class="margin-bottom-1">
						<div data-view-component="inputtext" data-component-name="passwordResetUsername"
							data-directive-processed="true" class="jq-appendErrorAfter">
							<div class="validation-container">
								<!--<span class="label">Username</span>-->
								<input class="text-input jq-passwordResetUsername"
									name="userCode" id="passwordResetUsername" value=""
									type="text" data-validation="validate[required]"
									data-validation-required-error="<cms:content name="Err.IP-0297"/>"
									data-ng-key="login_username"
									autocomplete="off" placeholder="Username" maxlength="250"
									data-stickit-id="stickit_23" data-view-value=""
									data-event="blur">
							</div>
						</div>
					</li>
					<li class="margin-bottom-1">
						<div data-view-component="inputtext" data-component-name="passwordResetLastName"
							data-directive-processed="true" class="jq-appendErrorAfter">
							<div class="validation-container">
								<!--<span class="label">Last name</span>-->
								<input class="text-input" name="lastName"
									type="text" autocomplete="off" placeholder="Your last name" id="passwordResetLastName"
									maxlength="256"	data-validation="validate[required,custom[lastname]]"
									data-validation-required-error="<cms:content name="Err.IP-0301"/>"
									data-validation-lastname-error="<cms:content name="Err.IP-0301"/>"
									data-ng-key="passwordResetLastName" />
							</div>
						</div>
					</li>
					<li class="margin-bottom-2">
						<div data-view-component="inputtext" data-component-name="passwordResetPostcode"
							data-directive-processed="true" class="jq-appendErrorAfter">
							<div class="validation-container">
								<!--<span class="label">Postcode</span>-->
								<span class="input-wrap" data-symbol="">
									<input class="text-input"
									id="passwordResetPostcode" name="postcode" maxlength="10"
									data-validation="validate[required,custom[alphaNumeric]]"
									data-validation-required-error="<cms:content name="Err.IP-0302"/>"
									data-validation-alphaNumeric-error="<cms:content name="Err.IP-0302"/>"
									data-ng-key='passwordResetPostcode' type="text"
									autocomplete="off" placeholder="Your postcode" data-stickit-id="stickit_36">
									<span class="iconHelpContainer" id="postCodeHelp">
										<span class="icon-support-help"></span>
										<div class="inlineHelpTooltip noDisplay">
											<span class="inlineHelpTooltipArrow"></span>
											<div class="helpContent">Enter the postcode that was used to register your account.</div>
										</div>
									</span>
								</span>
							</div>
						</div>
					</li>
					<li class="smsCodeBody">
						<div class="smsCodeWrapContainer clearBoth">
                         	<span class="label sms-label">SMS code for your security</span>
                         	<span class="iconHelpContainer" id="smsCodeHelp">
								<span class="icon-support-help"></span>
								<div class="inlineHelpTooltip noDisplay">
									<span class="inlineHelpTooltipArrow"></span>
									<div class="helpContent">The security code will be sent to the mobile number you registered with.</div>
								</div>
							</span>
                            <div data-view-component="button" data-component-name="secactivehref" data-directive-processed="true" class="view-secactivehref">
                                <a href="#nogo" class="btn-action-primary jq-passwordResetGetSMSCodeButton jq-getSMSCodeButton jq-formSubmit">
                                    <span class="button-inner">
                                        <span class="label-content jq-smsButtonTextHolder">Get SMS Code</span>
                                        <span class="icon-wrapper">
                                            <span class="icon icon-mobile-send"></span>
                                        </span>
                                    </span>
                                </a>
                            </div>
                            <div data-view-component="inputtext"
                                data-component-name="smsCode" data-directive-processed="true"
                                class="jq-appendErrorAfter smsCodeWrap">
                                <div class="validation-container">
                                    <span class="input-wrap margin-bottom-1" data-symbol="">
                                        <input id="passwordResetSmsCode" type="text" data-length="6" name="smsCode" data-min-length="6"
                                        data-validation="validate[disabled, required,custom[signedInteger],custom[minLength]]" data-validation-signedInteger-error="<cms:content name="Err.IP-0289"/>"
                                        data-validation-minLength-error="<cms:content name="Err.IP-0289"/>" class="text-input jq-passwordResetSmsCode noDisplay"
                                        maxlength="6" data-validation-required-error="<cms:content name="Err.IP-0289"/>" data-validation-disabled-error="<cms:content name="Err.IP-0289"/>" data-stickit-id="stickit_36" data-ng-key="smsCode">
                                    </span>
                                </div>
                            </div>
                            <span class="jq-passwordResetSmsBusyDialogContainer noDisplay">
                                <em class="ajaxLine logonBusyDialogAjax"></em>
                            </span>
							<div class="clearBoth jq-passwordResetSmsNumberMessage smsNumberMessage">
								<div class="mobileIconWrap">
									<span class="icon-mobile-receive"></span>
							</div>
							<div class="codeSentText">
								Code sent to registered mobile number. If not received in a few minutes,
								<a href="#nogo" class="jq-forgottenpasswordTryAgain jq-formSubmit">try again</a> .
							</div>
						</div>
					</li>
					<li>
						<span data-view-component="button"
						data-component-name="disabledprimaryhref"
						data-directive-processed="true"
						class="view-disabledprimaryhref disabled">
							<a href="#" data-ng-key="passwordResetStepOneSubmitButton" class="btn-action-primary disabled jq-inactive jq-formSubmit jq-passwordResetStepOneSubmitButton" tabindex="-1">
								<span class="button-inner">
									<span class="label-content">Next</span>
									<span class="icon-wrapper">
									<span class="icon iconWLoader noDisplay"></span></span>
								</span>
							</a>
						</span>
						<span data-view-component="button" data-component-name="terhref"
								data-directive-processed="true" class="view-terhref">
							<a href="../../public/page/logon?TAM_OP=login" class="btn-action-tertiary forgotPasswordCancelBtn">
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
			<input name="action" value="FORGOT_PASSWORD" type="hidden" />
			<input name="deviceToken" value="" type="hidden" />
		</form>
		<!--  <footer class="footerDisclaimerWrap">
         	  <!--<c:if test="${env ne PROD and env ne UAT}">[build: ${buildNumber}]</c:if>
		     	<div class="disclaimer jq-disclaimer footerDisclaimer">
                  	<%--<div class="emphasis">Disclaimer</div>--%>
		        	<div><cms:content name="loginDisclaimer"/></div>
		    	</div>
        </footer>-->
	</div>
	<div class="noDisplay" role="presentation">
  		<form method="post" action="" class="jq-authWithEamForgotPasswordForm">
        	<input type="hidden" name="RelayState" value="" />
            <input type="hidden" name="SAMLResponse" value=""/>
			<input type="hidden" name="deviceToken" value=""/>
            <input type="submit" name="auth" value="auth"/>
       	</form>
  	</div>
	<div class="jq-passwordResetStepTwo noDisplay" data-ng-key="passwordResetStepTwo">
		<form name="form" data-ajax="false" action="registerUser"
			method='POST' class="jq-passwordResetStepTwoForm"
			data-ajax-submit-url="" data-action="">
			<fieldset>
				<h1 class="header-statement heading-two">
					<span class="color-secondary">Forgotten password</span>
				</h1>
				<h1 class="heading-seven">Step 2 of 2</h1>
				<ul>
					<li class="margin-bottom-1">
						<div class="bubbleContainer">
							<div class="helpToolBox helpToolBoxPos3 noDisplay jq-passwordResetPasswordPolicyHintsContainer"
								id="passwordCheckReset"
								data-ng-key="passwordResetPasswordPolicyHints">
								<em class="iconArrowHelpLeft"></em>
								<div>
									<div class="heading-ten">
										<span class="color-highlight">Must be at least</span>
									</div>
									<ul class="helpBox">
										<li class="jq-alphabeticCharacter">
											<em class="iconbullet"></em>
											<span>One letter</span>
										</li>
										<li class="jq-nonAlphabeticCharacter">
											<em class="iconbullet"></em>
											<span>One number or special	character</span>
										</li>
										<li class="jq-minLength">
											<em class="iconbullet"></em>
											<span>8	characters</span>
										</li>
									</ul>
									<div class="heading-ten">
										<span>Cannot Include</span>
									</div>
									<ul class="">
										<li>
											<em class="iconbullet"></em>
											<span>Your username</span>
										</li>
									</ul>
								</div>
							</div>
						</div>
						<div data-view-component="inputtext" data-component-name="passwordResetNewPassword"
							data-directive-processed="true"
							class="jq-appendErrorAfter createpassword">
							<div class="validation-container">
								<span class="label">Create password</span>
								<input type="password" id="passwordResetNewPassword" name="newPassword"
									class="formTextInput jq-passwordResetNewPassword"
									maxlength="50"
									data-validation="validate[required,customFunction]"
									data-validation-required-error="<cms:content name="Err.IP-0298"/>"
									data-validation-customFunction-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-minLength-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-maxLength-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-validChars-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-spaces-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-consecutiveCharacters-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-consecutiveNumbers-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-userNameInString-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-nonAlphabeticCharacter-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-alphabeticCharacter-error="<cms:content name="Err.IP-0319"/>"
                                	data-validation-highlight-on-success="true"
									data-ng-key="passwordResetNewPassword"
									data-stickit-id="stickit_23" data-view-value=""
									data-event="blur"/>
								<span class="icon-notification-success noDisplay"></span>
							</div>
						</div>

					</li>
					<li class="margin-bottom-2">
						<div data-view-component="inputtext" data-component-name="passwordResetConfirmPassword"
							data-directive-processed="true"
							class="jq-appendErrorAfter confirmpassword">
							<div class="validation-container">
								<span class="label">Repeat password</span>
								<input aria-describedby="passwordCheckReset" type="password"
									id="passwordResetConfirmPassword" name="confirmPassword"
									class="formTextInput jq-passwordResetConfirmPassword"
									maxlength="50" data-validation="validate[required, custom]"
									data-validation-required-error="<cms:content name="Err.IP-0320"/>"
									data-validation-custom-error="<cms:content name="Err.IP-0321"/>"
									data-validation-highlight-on-success="true"
									data-ng-key="passwordResetConfirmPassword"
									data-stickit-id="stickit_23" data-view-value=""
									data-event="blur" />
									<!--  data-validation-required-error="${errors.err00033}"
									data-validation-custom-error="${errors.err00033}" -->

								<span class="icon-notification-success noDisplay"></span>
							</div>
						</div>
					</li>
					<li class="margin-bottom-2">
						<span data-view-component="button" data-component-name="primaryhref" data-directive-processed="true" class="view-primaryhref">
    						<a href="#" class="btn-action-primary jq-formSubmit jq-passwordResetStepTwoSubmitButton" data-ng-key="passwordResetStepTwoSubmitButton">
        						<span class="button-inner">
                					<span class="label-content">Sign in</span>
                					<span class="icon-wrapper"><span class="icon " aria-hidden="true"></span></span>
        						</span>
    						</a>
						</span>
						<span data-view-component="button" data-component-name="terhref"
							data-directive-processed="true" class="view-terhref">
							<a href="../../public/page/logon?TAM_OP=login" class="btn-action-tertiary">
								<span class="button-inner">
									<span class="icon-wrapper">
										<span class="icon icon-cta-link"></span>
									</span>
									<span class="label-content">Cancel</span>
								</span>
							</a>
						</span>
					</li>
				</ul>
			</fieldset>
		</form>
		<div class="doNotContainer color-white">
			<h1 class="header-statement heading-five">
				<span class="color-white">You must follow these security
					requirements in relation to your Password:</span>
			</h1>
			<ul class="unordered-list">
				<li class="color-white">You must not disclose your Password to
					anyone, including a family member or a friend.</li>
				<li class="color-white">You must not keep a written record of
					the Password without making a reasonable attempt to protect its
					security (either by disguising it or preventing unauthorised access
					to it).</li>
				<li class="color-white">You must avoid acting with extreme
					carelessness in failing to protect the security of the Password.</li>
				<li class="color-white">You must not select a numeric Password
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
<script language="javascript" type="text/javascript" src="<ng:hashurl src='/public/static/js/client/desktop/pages/tempPassword.js'/>"></script>
<script language="javascript" type="text/javascript" src="../static/vendors/adobe/omniture/analytics_btpanorama.js"></script>
<script language="javascript" type="text/javascript">
     var pageDetails;
     pageDetails = {
        "pageName": "forgotten password:step 1",
        "pageType": "selfservice",
        "formName": "forgotten password",
        "pageStep": "forgotpasswordstart"
     };
     if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1 && window.location.href.indexOf('#jq-passwordReset1') > -1) {
         window.wa && wa('page', pageDetails);
     }
</script>
