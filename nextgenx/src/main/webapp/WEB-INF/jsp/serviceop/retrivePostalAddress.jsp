<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="noDisplay"  id="addressVaidationApi">${addressValidationQasApi }</div>
<script>var addressQasApi = $("#addressVaidationApi").html();</script>

<form name="serviceOps_form" id="serviceOps" method="GET"
	action="/ng/secure/page/serviceOps/retrivePostalAddress"
	class="jq-clientDetailActionForm" commandName="reqModel">
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
	<table>
		<tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Search Address</label></td>
			<td class="subHeaderTextItem verticalMiddle" width="79%"><input
				id="address"
				class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
				name="address" placeholder="Please enter Address"
				data-validation="validate[required,custom[customFunction]]"
				data-validation-required-error="Please enter Address"
				data-ng-key="searchCriteria" type="text" maxlength="11" value=""
				autocomplete="off"></input></td>
		</tr>
		<tr id="addressKeyP">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="addressKey" class="formLabel">Address key</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="addressKey"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="key"
					type="text" 
					autocomplete="off" readonly="readonly"/>
			</span></td>
		</tr>
		<tr id="addressTypeP">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="addressType" class="formLabel">Address Type</label></td>
			<td class="subHeaderTextItem"><select name="addressType"
				id="addressType" class="">
					<c:forEach items="${addressType}" var="address">
						<c:choose>
							<c:when test="${address=='S'}">
								<option value="${address}">Standard Postal Address</option>
							</c:when>
							<c:when test="${address == 'N'}">
								<option value="${address}">Non Standard Postal Address
								</option>
							</c:when>
							<c:when test="${address == 'P'}">
								<option value="${address}">Provider Postal Address</option>
							</c:when>
							<c:otherwise>
								<option value="${address}">All formats of Postal
									Address</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
			</select></td>
		</tr>
	</table>
	<input type="submit" class="primaryButton jq-formSubmit" value="Submit">
</form>