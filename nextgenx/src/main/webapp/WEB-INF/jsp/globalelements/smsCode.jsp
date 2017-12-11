<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<security:authorize ifAnyGranted="ROLE_SERVICE_OP">
	<c:set var="disableGetSMSBts" value ="TRUE"/>
</security:authorize>
<div class="jq-smsCodeWrapContainer smsCodeWrapContainer clearBoth">
	<span class="label sms-label">SMS code for your security</span>
	<span class="iconHelpContainer" id="smsCodeHelp">
		<span class="icon-support-help"></span>
		<div class="inlineHelpTooltip noDisplay">
			<span class="inlineHelpTooltipArrow"></span>
			<div class="helpContent">The security code will be sent to the mobile number you registered with.</div>
		</div>
	</span>
	<div data-view-component="button" data-component-name="secactivehref" data-directive-processed="true" class="view-secactivehref SMSCodebuttonContainer">
		<a href="#nogo" title="Get SMS Code" class="btn-action-primary jq-getSMSCodeButton jq-formSubmit">
	       	<span class="button-inner">
	           	<span class="label-content jq-smsButtonTextHolder">Get SMS Code</span>
	            <span class="icon-wrapper">
	            	<span class="icon icon-mobile-send"></span>
	            </span>
	        </span>
		</a>
	</div>
	<div data-view-component="inputtext" data-component-name="smsCode" data-directive-processed="true" class="jq-appendErrorAfter smsCodeWrap">
		<div class="validation-container jq-smsValidation">
			<span class="input-wrap  margin-bottom-1" data-symbol="">
				<input id="smsCode" type="text" data-length="6" name="smsCode" data-min-length="6"  ${disableGetSMSBts == 'TRUE'? ' data-service-ops="true" ' : ''}
					   data-validation="validate[disabled, required,custom[signedInteger],custom[minLength]]" data-validation-signedInteger-error="<cms:content name="Err.IP-0289"/>"
					   data-validation-minLength-error="<cms:content name="Err.IP-0289"/>" class="text-input formTextInput inputStyleTwo jq-smsCode floatLeft getSMSInput jq-skip"
					   maxlength="6" data-validation-required-error="<cms:content name="Err.IP-0289"/>" data-validation-disabled-error="<cms:content name="Err.IP-0289"/>" data-stickit-id="stickit_36">
			</span>
		</div>
	</div>
</div>
