<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<script>

}
</script>

<form name="serviceOps_form" id="serviceOps" method="GET"
	action="/ng/secure/page/serviceOps/retrieveIDVDetails"
	class="jq-clientDetailActionForm" commandName="reqModel">
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
<p id="cisKeyP"  class="formBlock clearFix">
		<label for="cisKey" class="formLabel">CIS key</label> <span
			class="inputWrapper iconWrapper"> <input id="cisKey"
			data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]" 
			data-min-length="11" 
			data-max-length="11"
			data-validation-required-error="Please enter CIS key"
			data-validation-minLength-error="Please enter at least 11 digit CIS Key"
			data-validation-searchNumber-error="Please enter numbers"
			class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
			name="cisKey" 
			data-ng-key="searchCriteria" 
			type="text" 
			maxlength="11"
			value="" 
			autocomplete="off"></input>
		</span>
	</p>
	<p class="formBlock clearFix">
		<label for="personType" class="formLabel">Role Type</label> <select
			name="personType" id="personType"
			class="">
			<option value="Individual">Individual</option>
			<option value="Organisation">Organisation</option>
		</select>
	</p>
	<p class="formBlock clearFix">
		<label for="silo" class="formLabel">Silo</label> <select name="silo"
			id="silo" class="">
			<option value="WPAC">WPAC</option>
			<option value="BTPL">BTPL</option>
		</select>
	</p>
	
	

	
	<input type="submit" class="primaryButton jq-formSubmit" value="Submit">
</form>
<script language="javascript" type="text/javascript"
	src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/downloadFailedApplication.js'/>"></script>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOperatorClientSearch.js'/>"></script>