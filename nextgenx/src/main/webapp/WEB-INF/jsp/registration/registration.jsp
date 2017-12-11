<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/WEB-INF/taglib/cms.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div id="jq-register" class="jq-registerContainer">
	<div class="jq-registerStepOne" data-ng-key="registerStepOne">
		<form name="form" data-ajax="false" action="" method='POST' class="jq-registerStepOneForm jq-preventSubmit"	data-ajax-submit-url="" data-action="">
			<div class="noDisplay jq-FormErrorMessage customFormError"></div>
			<fieldset>
				<legend>Register</legend>
				<h3 class="header-statement heading-two">
					<span class="color-white registerPlaceholder"></span>
				</h3>
				<h4 class="heading-seven">Step 1 of 2</h4>
				<ul>
					<li class="margin-bottom-1">
						<div data-view-component="inputtext"
							 data-component-name="registrationno"
							 data-directive-processed="true" class="jq-appendErrorAfter registrationSuccess">
							<div class="validation-container">
							    <span class="icon-notification-success noDisplay"></span>
								<span class="registrationCodeWrap" id="registrationCodeParent" data-symbol="">
									<input class="text-input jq-registrationCode jq-registrationFields" name="userCode"
										   id="registrationCode" value="" type="text"
										   data-validation="validate[required,minLength,maxLength]"
										   data-validation-required-error="<cms:content name="Err.IP-0300"/>"
										   data-validation-minLength-error="<cms:content name="Err.IP-0300"/>"
										   data-validation-maxLength-error="<cms:content name="Err.IP-0300"/>"
										   data-ng-key="registrationCode" autocomplete="off"
										   placeholder="Registration number" maxlength="50" data-stickit-id="stickit_23"
										   data-min-length="8" data-max-length="50"
										   data-view-value="" data-event="blur">
									<span class="iconHelpContainer" id="registrationNumberHelp">
										<div class="inlineHelpTooltip noDisplay">
											<span class="inlineHelpTooltipArrow"></span>
											<div class="helpContent">Enter your unique 12 character registration number that was emailed to you. Include all letters,
                                            numbers and dashes, eg: abcd-1234-ef
											</div>
										</div>
									</span>
								</span>
							</div>
						</div>
					</li>
					<li class="margin-bottom-1">
						<div data-view-component="inputtext"
							 data-component-name="lastname" data-directive-processed="true"
							 class="jq-appendErrorAfter registrationSuccess">
							<div class="validation-container">
							    <span class="icon-notification-success noDisplay"></span>
							    <span id="lastNameParent">
								    <input class="text-input jq-registrationFields" name="lastName"
									   id="lastName" value="" type="text"
									   data-validation="validate[required,custom[lastname]]"
									   data-validation-required-error="<cms:content name="Err.IP-0301"/>"
									   data-validation-lastname-error="<cms:content name="Err.IP-0301"/>"
									   data-ng-key="registerLastName" autocomplete="off"
									   placeholder="Your last name" maxlength="256" data-stickit-id="stickit_23"
									   data-view-value="" data-event="blur">
                                    <span class="iconHelpContainer" id="lastNameHelp">
                                        <div class="inlineHelpTooltip noDisplay">
                                            <span class="inlineHelpTooltipArrow"></span>
                                            <div class="helpContent">Enter your last name only. It must match the application form.</div>
                                        </div>
                                    </span>
                                </span>
							</div>
						</div>
					</li>
					<li class="margin-bottom-2">
						<div data-view-component="inputtext"
							 data-component-name="postcode" data-directive-processed="true"
							 class="jq-appendErrorAfter registrationSuccess">
							<div class="validation-container">
							    <span class="icon-notification-success noDisplay"></span>
								<span class="input-wrapper" data-symbol="" id="postCodeParent">
								    <input class="text-input jq-registrationFields"
									   id="postcode" name="postcode" maxlength="10"
									   data-validation="validate[required,custom[alphaNumeric]]"
									   data-validation-required-error="<cms:content name="Err.IP-0302"/>"
									   data-validation-alphaNumeric-error="<cms:content name="Err.IP-0302"/>"
									   data-ng-key='registerPostcode' type="text" autocomplete="off"
									   placeholder="Your postcode" data-stickit-id="stickit_36">
                                    <span class="iconHelpContainer" id="postcodeHelp">
                                    <div class="inlineHelpTooltip noDisplay">
                                    <span class="inlineHelpTooltipArrow"></span>
                                        <div class="helpContent">Enter your registered business postcode.</div>
                                        </div>
                                    </span>
                                </span>
							</div>
						</div>
					</li>

					<li class="smsCodeBody">
						<%@include file="../globalelements/smsCode.jsp"%>
						<div class="clearBoth jq-registerSmsNumberMessage smsNumberMessage">
                            <div class="mobileIconWrap">
                                <span class="icon-mobile-receive"></span>
                            </div>
                            <div class="codeSentText">
                                Code sent. If the code is not received in a few minutes,
                                <a href="#nogo" title="Try Again" class="jq-RegisterTryAgain jq-formSubmit">try again</a> .
                            </div>
                        </div>
					</li>
					<li class="verifyCancelBtn margin-bottom-1">
						<span data-view-component="button" data-component-name="disabledprimaryaction" data-directive-processed="true" class="view-disabledprimaryaction ">
    						<button type="submit" class="btn-action-primary  jq-formSubmit jq-registerStepOneSubmitButton" data-ng-key="registerStepOneSubmitButton" >
        						<span class="button-inner">
                            		<span class="label-content">Next</span>
                            		<span class="icon-wrapper">
                            			<span class="icon iconWLoader noDisplay"></span>
                            		</span>
        						</span>
    						</button>
						</span>
						<span data-view-component="button" data-component-name="terhref"
							  data-directive-processed="true" class="view-terhref">
							<a href="../../public/page/logon?TAM_OP=login" title="Cancel" class="btn-action-tertiary registrationCancelBtn">
								<span class="button-inner">
									<span class="label-content">Cancel</span>
									<span class="icon-wrapper">
                                        <span class="icon icon-close"></span>
                                    </span>
								</span>

							</a>
						</span>
					</li>
					<li class="forgotusername-contact">
						<div>Need help?</div> <span>Call <cms:content name="CONTACT_NUMBER"/><%--Phone# updated with US1199--%></span>
					</li>
				</ul>
			</fieldset>
			<input name="action" value="REGISTRATION" type="hidden" />
			<input type="hidden" name="deviceToken" value="" class="jq-deviceToken" />
		</form>
		<div class="notificationPanel">
			<div class="registerBoxWrapper">
				<div class="registerBoxWrapper-title">
					Already registered?
				</div>
				<div class="registerBoxWrapper-text">
					Sign in to access your account.
				</div>
				<a href="../../public/page/logon?TAM_OP=login" class="btn-action-primary">
					<span class="button-inner">
						<span>Sign in</span>
					</span>
				</a>
			</div>
		</div>
		<div class="noDisplay" role="presentation">
			<form method="post" action="" class="jq-authWithEamForm">
				<input type="hidden" name="RelayState" value="" />
				<input type="hidden" name="SAMLResponse" value="" />
				<input type="hidden" name="deviceToken" value="" />
				<input type="submit" name="auth" value="auth" />
			</form>
		</div>
	</div>
	<div class="jq-registerStepTwo noDisplay" data-ng-key="registerStepTwo">
		<form name="form" data-ajax="false"
			  action="com.bt.nextgen.login.web.model.RegistrationModel"
			  method='POST' class="jq-registerStepTwoForm" data-ajax-submit-url="" data-action="">
			<fieldset>
				<legend>Register</legend>
				<h3 class="header-statement heading-two">
					<span class="color-secondary">Register</span>
				</h3>
				<h4 class="heading-seven">Step 2 of 2</h4>
				<ul class="loginForm">
					<li class="margin-bottom-1">
						<span class="bubbleContainer">
							<div class="helpToolBox helpToolBoxPos1 noDisplay jq-usernamePolicyHintsContainer"
								 id="nameCheck" data-ng-key="usernamePolicyHint">
								<em class="iconArrowHelpLeft"></em>
								<div>
									<div class="heading-ten">
										<span class="color-highlight">Must be</span>
									</div>
									<ul class="helpBox">
										<li class="jq-minLength jq-maxLength">
											<em class="iconbullet"></em> <span>Between 8-50 characters</span>
										</li>
										<li class="jq-alphabeticCharacter">
											<em class="iconbullet"></em> <span>A combination of letters and numbers</span>
										</li>
									</ul>
									<div class="heading-ten">
										<span>Can be or Include</span>
									</div>
									<ul class="">
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
											<span>One of these characters '&^%$#@!</span>
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
									<span class="label">Create username</span>
									 <input	class="text-input jq-registerUsername" name="userCode"
											   id="username" value="" type="text"
											   data-validation-ajax-error="<cms:content name="Err.IP-0304"/>"
											   data-validation-highlight-on-success="true"
											   data-ng-key="registerUsername" autocomplete="off"
											   placeholder="" maxlength="50" data-stickit-id="stickit_23"
											   data-view-value=""
											   data-validation="validate[required]"
											   data-validation-required-error="<cms:content name="Err.IP-0297"/>"> <span
										class="icon-notification-success noDisplay"></span>
									<!--  data-validation="validate[required,custom[ajax]]"
    									data-validation-required-error="${errors.err00031}" -->
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
										<span class="color-highlight">Must be at least</span>
									</div>
									<ul class="helpBox">
										<li class="jq-alphabeticCharacter">
											<em class="iconbullet"></em>
											<span>One letter</span>
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
									<div class="heading-ten">
										<span>Cannot Include</span>
									</div>
									<ul class="">
										<li>
											<em class="iconbullet"></em>
											<span>You user name</span>
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
									<span class="label">Create password</span> <input
										class="formTextInput jq-registerPassword" name="password"
										id="password" value="" type="password"
										data-validation="validate[required,custom]"
										data-validation-required-error="<cms:content name="Err.IP-0298"/>"
										data-validation-custom-error="<cms:content name="Err.IP-0298"/>"
										data-validation-highlight-on-success="true"
										data-ng-key="registerNewPassword" autocomplete="off"
										placeholder="" maxlength="50" /> <span
										class="icon-notification-success noDisplay"></span>
									<!--  data-validation="validate[required]" data-validation-required-error="${errors.err00032}" -->
								</div>
							</div>
						</span>
					</li>
					<li class="margin-bottom-2">
						<span>
							<div data-view-component="inputtext" data-component-name="name"
								 data-directive-processed="true"
								 class="jq-appendErrorAfter confirmpassword">
								<div class="validation-container">
									<span class="label">Repeat password</span> <input
										class="formTextInput jq-registerConfirmPassword"
										name="confirmPassword" id="confirmPassword" value=""
										type="password" data-validation="validate[required]"
										data-validation-required-error="<cms:content name="Err.IP-0305"/>"
										data-validation-highlight-on-success="true"
										data-ng-key="registerConfirmPassword" autocomplete="off"
										placeholder="" maxlength="250" data-stickit-id="stickit_23"
										data-view-value=""> <span
										class="icon-notification-success noDisplay"></span>
								</div>
							</div>
						</span>
					</li>
					<li class="margin-bottom-2">
						<span data-view-component="button" data-component-name="disabledprimaryaction" data-directive-processed="true" class="view-disabledprimaryaction disabled">
    						<button type="submit" class="btn-action-primary disabled" tabindex="-1">
        						<span class="button-inner">
                            		<span class="label-content">Sign in</span>
                            		<span class="icon-wrapper">
                            			<span class="icon" aria-hidden="true"></span>
                            		</span>
        						</span>
    						</button>
						</span>
					</li>
				</ul>
			</fieldset>
		</form>
		<div class="doNotContainer color-white">
			<h5 class="header-statement heading-five">
				<span class="color-white">You must follow these security requirements in relation to your Password:</span>
			</h5>
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
<script language="javascript" type="text/javascript" src="../static/vendors/adobe/omniture/analytics_btpanorama.js"></script>
<script language="javascript" type="text/javascript">
	var pageDetails;
	pageDetails = {
		"pageName": "register:step 1",
		"pageType": "registration",
		"formName": "register",
		"pageStep": "start"
	};
	if (window.org.bt.utils.location.getBaseUrl().indexOf('localhost') <= -1 && window.location.href.indexOf('#jq-register') > -1) {
		window.wa && wa('page', pageDetails);
	}
</script>