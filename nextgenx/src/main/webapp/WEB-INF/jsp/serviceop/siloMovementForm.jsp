<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<form name="serviceOps_form"
	id="maintainIpToIpRelationshipBulkUploadForm" method="POST"
	action="/ng/secure/page/serviceOps/siloMovement"
	class="jq-clientDetailActionForm" commandName="reqModel">
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>"
		name="cssftoken" />
	<table>
		<tr id="fromSiloP">
			<td class="subHeaderTextItem verticalMiddle"><label for="silo"
				class="formLabel">From Silo</label></td>
			<td class="subHeaderTextItem"><select name="fromSilo"
				id="fromSilo" class="inputStyleEight">
					<option value="WPAC">WPAC</option>
					<option value="BTPL">BTPL</option>
			</select></td>
		</tr>
		<tr id="toSiloP">
			<td class="subHeaderTextItem verticalMiddle"><label for="silo"
				class="formLabel">To Silo</label></td>
			<td class="subHeaderTextItem"><select name="toSilo" id="toSilo"
				class="inputStyleEight">
					<option value="WPAC">WPAC</option>
					<option value="BTPL">BTPL</option>
			</select></td>
		</tr>
		<tr id="cisKeyP">
			<td class="subHeaderTextItem verticalMiddle"><label for="cisKey"
				class="formLabel">CIS key</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="cisKey"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="key"
					data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]"
					data-min-length="11" data-max-length="11"
					data-validation-required-error="Please enter CIS key"
					data-validation-minLength-error="Please enter at least 11 digit CIS Key"
					data-validation-searchNumber-error="Please enter numbers"
					data-ng-key="searchCriteria" type="text" maxlength="11" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="personTypeP">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="personType" class="formLabel">Person Type</label></td>
			<td class="subHeaderTextItem"><select name="personType"
				id="personType" class="inputStyleEight">
					<option value="INDIVIDUAL">Individual</option>
					<option value="ORGANISATION">Organisation</option>
			</select></td>
		</tr>
	</table>
	<input type="submit" class="primaryButton jq-formSubmit" value="Submit"
		id="submitBtnBulkUpload">
</form>
<script language="javascript" type="text/javascript"
	src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/downloadFailedApplication.js'/>"></script>