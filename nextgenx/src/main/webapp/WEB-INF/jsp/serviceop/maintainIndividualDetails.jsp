<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<form name="serviceOps_form" id="maintainIndividualIdv" method="POST" action="/ng/secure/page/serviceOps/maintainIdvDetails" class="jq-clientDetailActionForm" commandName="reqModel">
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
	<table>
	    <tr id="idvTypeP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label for="silo"
				class="formLabel">IDV Type</label></td>
			<td class="subHeaderTextItem"><select name="idvType" id="idvType"
				class="inputStyleEight">
					<option value="Individual">Individual</option>
					<option value="Organisation">Organisation</option>
			</select></td>
		</tr>
		<tr id="roleP" class="">
            <td class="subHeaderTextItem verticalMiddle"><label
                for="sourcePersonType" class="formLabel">Requested Action</label></td>
            <td class="subHeaderTextItem"><select name="requestedAction" id="requestedAction"
                class="inputStyleEight">
                <option value="select">Select one</option>
                    <option value="update">Update</option>
                    <option value="update and validate">Update and Validate</option>
                    <option value="update and set IDV status">Update and set IDV status</option>
                    <option value="delete">Delete</option>
            </select></td>
        </tr>
		<tr id="siloP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label for="silo"
				class="formLabel">Silo</label></td>
			<td class="subHeaderTextItem"><select name="silo" id="silo"
				class="inputStyleEight">
					<option value="WPAC">WPAC</option>
					<option value="BTPL">BTPL</option>
			</select></td>
		</tr>
		<tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Person Type</label></td>
			<td class="subHeaderTextItem"><select name="personType" id="personType"
				class="inputStyleEight">
					<option value="INDV">Individual</option>
					<option value="COMD">Company</option>
					<option value="TRST">Trust</option>
			</select></td>
		</tr>
		<tr id="cisKeyP" class="">
            <td class="subHeaderTextItem verticalMiddle"><label
                for="sourceCISKey" class="formLabel">Customer CIS key</label></td>
            <td class="subHeaderTextItem"><span
                class="inputWrapper iconWrapper"> <input id="cisKey"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="cisKey"
                    placeholder="Customer CIS key"
                    data-validation=""
                    data-min-length="" data-max-length=""
                    data-validation-required-error="Please enter CIS key"
                    data-validation-minLength-error="Please enter at least 11 digit CIS Key"
                    data-validation-searchNumber-error="Please enter numbers"
                    data-ng-key="searchCriteria" type="text" maxlength="11" value=""
                    autocomplete="off"></input>
            </span></td>
        </tr>
	    <tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceCISKey" class="formLabel">Full Name</label></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="fullName"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="fullName"
					placeholder="Full name"
					data-validation=""
					data-validation-required-error="Please enter First Name"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceCISKey" class="formLabel">Name Details</label>
			</td>
			<td class="subHeaderTextItem" width="10%">
			    <span class="inputWrapper">
			        <input id="firstName" class="formTextInput inputStyleSeven"
					name="firstName"
					placeholder="First name"
					data-validation=""
					data-validation-required-error="Please enter First Name"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>

			        <input id="middleName" class="formTextInput inputStyleSeven"
                    name="middleName"
                    placeholder="Middle name"
                    data-validation=""
                    data-validation-required-error="Please enter Last Name"
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>

                    <input id="lastName" class="formTextInput inputStyleSeven"
                    name="lastName"
                    placeholder="Last name"
                    data-validation=""
                    data-validation-required-error="Please enter Last Name"
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                </span>
			</td>
		</tr>
		<tr>
            <td class="subHeaderTextItem verticalMiddle"><label
                for="sourceextIdvDate" class="formLabel">Date Of Birth</label></td>
            <td class="subHeaderTextItem">
                <span class="calendarPlaceHolder jq-dateOfBirthPickerHolder"></span>
                <span class="iconWrapper">
                    <input id="dateOfBirth" name="dateOfBirth" data-calendar=".jq-dateOfBirthPickerHolder"
                      class="formTextInput jq-dateOfBirth inputStyleThree" value="" data-placeholder=""
                      data-validation=""
                      data-past="2006-01-01" data-future="2015-03-30"
                      data-validation-required-error="${errors.err00053}"
                      data-validation-date-error="${errors.err00060}"
                      data-validation-future-error="${errors.err00053}"
                      data-validation-customFunction-error="${errors.err00016}"/>
                    <a class="iconActionButton jq-birthDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                        <em class="iconcalendar"><span>Select Date</span></em>
                        <em class="iconarrowfulldown"></em>
                    </a>
             </span></td>
        </tr>
		<tr>
			<td class="subHeaderTextItem verticalMiddle" width="15%"><label
				for="sourceEmployerName" class="formLabel">Employer Name</label></td>
			<td class="subHeaderTextItem verticalMiddle" width="38%"><span
				class="inputWrapper iconWrapper"> <input id="employerName"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="employerName"
					placeholder="Employer name"
					data-validation=""
					data-validation-required-error="Please enter Employer Name"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr>
            <td class="subHeaderTextItem verticalMiddle"><label
                for="sourceAgentCISKey" class="formLabel">Agent Details</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper"> <input id="agentCisKey"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="agentCisKey"
                    placeholder="Agent CIS Key"
                    data-validation=""
                    data-min-length="" data-max-length=""
                    data-validation-required-error="Please enter agentCisKey"
                    data-validation-minLength-error="Please enter at least 11 digit agentCisKey"
                    data-validation-searchNumber-error="Please enter numbers"
                    data-ng-key="searchCriteria" type="text" maxlength="11" value=""
                    autocomplete="off"></input>
                </span>
                <span class="inputWrapper iconWrapper"> <input id="agentName"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="agentName"
                    placeholder="Agent name"
                    data-validation=""
                    data-validation-required-error="Please enter Agent Name"
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                </span>
            </td>
        </tr>
	    <tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceextIdvDate" class="formLabel">External IDV Date</label></td>
			<td class="subHeaderTextItem">
			    <span class="calendarPlaceHolder jq-effectiveStartDateCalendarPlaceHolder"></span>
			    <span class="iconWrapper">
			        <input id="extIdvDate" name="extIdvDate" data-calendar=".jq-effectiveStartDateCalendarPlaceHolder"
                          class="formTextInput jq-effectiveStartDate inputStyleThree" value="" data-placeholder=""
                          data-validation=""
                          data-past="2006-01-01" data-future="2015-03-30"
                          data-validation-required-error="${errors.err00053}"
                          data-validation-date-error="${errors.err00060}"
                          data-validation-future-error="${errors.err00053}"
                          data-validation-customFunction-error="${errors.err00016}"/>
			        <a class="iconActionButton jq-effectiveStartDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                          <em class="iconcalendar"><span>Select Date</span></em>
                          <em class="iconarrowfulldown"></em>
                    </a>
			    </span>
			</td>
		</tr>
		<tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Registration Details</label><span class="italicFont">(optional)</span>
			</td>
			<td class="subHeaderTextItem">
			    <select name="registrationNumberType" id="registrationNumberType" class="inputStyleFour">
				    <option value="select">Registration Type</option>
					<option value="ARBN">ARBN</option>
					<option value="ACN">ACN</option>
					<option value="ABN">ABN</option>
					<option value="FOREIGN">FOREIGN</option>
			    </select>
			    <span class="inputWrapper iconWrapper"> <input id="registrationNumber"
                    class="formTextInput inputStyleFour jq-intermediariesAndClientsSearchInput"
                    name="registrationNumber"
                    placeholder="Registration number"
                    data-validation=""
                    data-validation-required-error="Please enter First Name"
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                </span>
			</td>
		</tr>
		<tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceCISKey" class="formLabel">Customer Number</label><span class="italicFont">(optional)</span></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="customerNumber"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="customerNumber"
					placeholder="Customer number"
					data-validation=""
					data-validation-required-error="Please enter First Name"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr>
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Is Sole Trader</label><span class="italicFont">(optional)</span></td>
			<td class="subHeaderTextItem"><select name="isSoleTrader" id="isSoleTrader"
				class="inputStyleEight">
				<option value="select">Select one</option>
					<option value="Y">Y</option>
					<option value="N">N</option>
			</select></td>
		</tr>

		<tr id="involvedPartyNameTypeP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceinvolvedPartyNameType" class="formLabel">InvolvedParty Name Type</label></td>
			<td class="subHeaderTextItem"><select name="involvedPartyNameType" id="involvedPartyNameType"
				class="inputStyleEight">
				<option value="select">Select one</option>
					<option value="RegisteredName">RegisteredName</option>
					<option value="TradingName">TradingName</option>
					<option value="OtherName">OtherName</option>
			</select></td>
		</tr>
		<tr id="isOtherNameP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceisOtherName" class="formLabel">Is Other Name</label></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="isOtherName"
					class="formTextInput inputStyleEight"
					name="isOtherName"
					placeholder="Please enter "
					data-validation=""
					data-validation-required-error="Please enter "
					data-ng-key="searchCriteria" type="text"  value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="hfcpFullNameP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcehfcpFullName" class="formLabel">Contact Person Full Name</label></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="hfcpFullName"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="hfcpFullName"
					placeholder="Please enter "
					data-validation=""
					data-validation-required-error="Please enter "
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="isRegulatedByP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceisRegulatedBy" class="formLabel">Is Regulated By</label></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="isRegulatedBy"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="isRegulatedBy"
					placeholder="Please enter "
					data-validation=""
					data-validation-required-error="Please enter "
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="isIssuedAtCountryP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceisIssuedAtCountry" class="formLabel">Is IssuedAt-Country</label></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="isIssuedAtCountry"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="isIssuedAtCountry"
					placeholder="Please enter "
					data-validation=""
					data-validation-required-error="Please enter "
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="isIssuedAtStateP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceisIssuedAtState" class="formLabel">Is IssuedAt-State</label></td>
			<td class="subHeaderTextItem" width="26%"><span
				class="inputWrapper iconWrapper"> <input id="isIssuedAtState"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="isIssuedAtState"
					placeholder="Please enter "
					data-validation=""
					data-validation-required-error="Please enter "
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="isForeignRegisteredP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Is ForeignRegistered</label></td>
			<td class="subHeaderTextItem"><select name="isForeignRegistered" id="isForeignRegistered"
				class="inputStyleEight">
				<option value="select">Select one</option>
					<option value="Y">Y</option>
					<option value="N">N</option>
			</select></td>
		</tr>

		<tr id="iparDetailsP" class="noDisplay">
		    <td class="subHeaderTextItem verticalMiddle">
                <label for="sourcePersonType" class="formLabel">IPAR Details</label>
            </td>
            <td>
		    <p class="inputWrapper inputStyleFive">
		        <input id="iparAddressLine1"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparAddressLine1"
                    placeholder="Address Line 1"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text"  value=""
                    autocomplete="off"></input>
                <input id="iparCity"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparCity"
                    placeholder="City"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                <input id="iparState"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparState"
                    placeholder="State"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                <input id="iparPostCode"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparPostCode"
                    placeholder="Postcode"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
		        <input id="iparCountry"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparCountry"
                    placeholder="Country"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                <input id="iparRoleType"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparType"
                    placeholder="Role type"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                <input id="iparCisKey"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparCisKey"
                    placeholder="CIS key"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
                <input id="iparRoleId"
                    class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
                    name="iparId"
                    placeholder="Role id"
                    data-validation=""
                    data-validation-required-error="Please enter "
                    data-ng-key="searchCriteria" type="text" value=""
                    autocomplete="off"></input>
		    </p>
		    </td>
		</tr>
		<tr id="roleP" class="">
            <td class="subHeaderTextItem verticalMiddle"><label
                for="sourcePersonType" class="formLabel">Document Type</label></td>
            <td class="subHeaderTextItem"><select name="documentType" id="documentType"
                class="inputStyleEight">
                    <option value="LICENCEDRIVER-NONWBC">LICENCEDRIVER-NONWBC</option>
                    <option value="LICENSEDRIVERFRGN-NONWBC">LICENSEDRIVERFRGN-NONWBC</option>
                    <option value="AUSPASSPORT-NONWBC">AUSPASSPORT-NONWBC</option>
                    <option value="FOREIGNPASSPORT-NONWBC">FOREIGNPASSPORT-NONWBC</option>
                    <option value="18PLUS-NONWBC">18PLUS-NONWBC</option>
                    <option value="FOREIGNNATIONIDENTCARD-NONWBC">FOREIGNNATIONIDENTCARD-NONWBC</option>
                    <option value="BIRTHCERTAUS-NONWBC">BIRTHCERTAUS-NONWBC</option>
                    <option value="AUSCITIZENCERT-NONWBC">AUSCITIZENCERT-NONWBCC</option>
                    <option value="PENSIONCARDCLINK-NONWBC">PENSIONCARDCLINK-NONWBC</option>
                    <option value="HEALTHCARDCLINK-NONWBC">HEALTHCARDCLINK-NONWBC</option>
                    <option value="ATONOTICE-NONWBC">ATONOTICE-NONWBC</option>
                    <option value="UTILNOTICE-NONWBC">UTILNOTICE-NONWBC</option>
            </select></td>
        </tr>
        <tr data-duplicate="addressVerification" data-duplicate-max="6" data-duplicate-min="1">
            <td class="subHeaderTextItem verticalMiddle">
                <label for="sourcePersonType" class="formLabel">Document Verification</label>
            </td>
		    <td>
                <select id="optAttrName" name="optAttrName">
                    <option value="select">Document name</option>
                    <c:forEach var="attr" items="${optAttribute}">
                        <option value= ${attr}> ${attr}</option>
                    </c:forEach>
		        </select>
		        <p class="inputWrapper inputStyleFive"> <input id="optAttrVal"
                        class="formTextInput"
                        name="optAttrVal"
                        placeholder="Document value"
                        data-validation=""
                        data-validation-required-error="Please enter Document value"
                        data-ng-key="searchCriteria" type="text" value=""
                        autocomplete="off"></input>
                </p>
		    </td>
        </tr>
        <tr>
            <td/>
            <td colspan="2">
                <button class="primaryButtonAddRemove" id="add" data-duplicate-add="addressVerification" >+</button>
                <button class="primaryButtonAddRemove" id="add" data-duplicate-remove="addressVerification">-</button>
            </td>
        </tr>
    </table>
    <br>
    <table>
        <tr data-duplicate="address" data-duplicate-max="6" data-duplicate-min="1" style="padding-bottom:2em;">
            <td class="subHeaderTextItem verticalMiddle">
                <label for="sourcePersonType" class="formLabel" style="padding-right:10em;">Address Details</label>
            </td>
            <td/>
            <td>
                <select name="usage" id="usage">
                    <option value="MAIN BUSINESS PREMISES">Main Business Premises</option>
                    <option value="REGISTERED BUSINESS ADDRESS">Registered Business Address</option>
                    <option value="RESIDENTIAL ADDRESS">Residential Address</option>
                </select>
                <p class="inputWrapper inputStyleFive">
                    <input id="postalAddressType"
                          name="postalAddressType"
                          placeholder="Postal address type"
                          data-validation=""
                          data-validation-required-error="Please enter postal address type"
                          data-ng-key="searchCriteria" type="text" value=""
                          autocomplete="off"></input>
                    <input id="addressline1"
                          name="addressline1"
                          placeholder="Address line 1"
                          data-validation=""
                          data-validation-required-error="Please enter address"
                          data-ng-key="searchCriteria" type="text" value=""
                          autocomplete="off"></input>
                    <input id="addressline2"
                          name="addressline2"
                          placeholder="Address line 2"
                          data-validation=""
                          data-validation-required-error="Please enter address"
                          data-ng-key="searchCriteria" type="text" value=""
                          autocomplete="off"></input>
                    <input id="city"
                        name="city"
                        placeholder="City"
                        data-validation=""
                        data-validation-required-error="Please enter City"
                        data-ng-key="searchCriteria" type="text" value=""
                        autocomplete="off"></input>
                    <input id="state"
                         name="state"
                         placeholder="State"
                         data-validation=""
                         data-validation-required-error="Please enter State"
                         data-ng-key="searchCriteria" type="text" value=""
                         autocomplete="off"></input>
                    <input id="pincode"
                          name="pincode"
                          placeholder="Pincode"
                          data-min-length="" data-max-length=""
                          data-validation=""
                          data-validation-required-error="Please enter Pincode"
                          data-ng-key="searchCriteria" type="text" value=""
                          autocomplete="off"></input>
                    <input id="country"
                            name="country"
                            placeholder="Country"
                            data-validation=""
                            data-validation-required-error="Please enter Country"
                            data-ng-key="searchCriteria" type="text" value=""
                            autocomplete="off"></input>
                </p>
            </td>
        </tr>
        <tr id="primaryP" class="noDisplay">
            <td/>
            <td colspan="2">
                <button class="primaryButtonAddRemove" id="add" data-duplicate-add="address" >+</button>
                <button class="primaryButtonAddRemove" id="add" data-duplicate-remove="address" >-</button>
            </td>
        </tr>
    </table>

		
	<br>
	<input type="submit" class="primaryButton jq-formSubmit" value="Submit" id="indIdvSubmit">
</form>



<div class="modalContent jq-cloak jq-confirmModal">
        <div class="modalContentMod3">
        <h1 class="formHeaderModal">
        <span class="baseGama" id="usecaseSpan"></span> using below details ?
    </h1>
        <table class="" style="text-align:left;line-height:1.5;table-layout: fixed;">
        <thead>
            <tr>
                <th id="tabHeadcustomerCisKey">Customer CIS key</th>
                <td id="tabDataCustomerCisKey"></td>               
            </tr>
                <tr style="margin-top:10px;">
                <th id="tabHeadPersonType">Person Type</th>
                <td id="tabDataPersonType"></td>
                </tr>
               <tr style="margin-top:10px;">
                <th id="tabHeadSilo">Silo</th>
                <td id="tabDataSilo"></td>
                </tr>
                <tr style="margin-bottom:5px">
                <th id="tabFullName">Full Name</th>
                <td id="tabDataFullName"></td>
                </tr>
                <tr>
                <th id="tabHeadFirstName">First Name</th>
                <td id="tabDataFirstName"></td>
                </tr>
                <tr>
                <th id="tabHeadLastName">Last Name</th>
                <td id="tabDataLastName"></td>
                </tr>
                <tr>
                <th id="tabHeadMiddleName">Middle Name</th>
                <td id="tabDataMiddleName"></td>
                </tr>                  
                <tr>
                <th id="tabHeadAgentCISKey">Agent CIS Key</th>
                <td id="tabDataAgentCISKey"></td>
                </tr>
                <tr>
                <th id="tabHeadAgentName">AgentName</th>
                <td id="tabDataAgentName"></td>
                </tr>
                <tr>
                <th id="tabHeadEmployerName">Employer Name</th>
                <td id="tabDataEmployerName"></td>
                </tr>        
                <tr>
                <th id="tabHeadDateOfBirth">Date Of Birth</th>
                <td id="tabDataDateOfBirth"></td>
                </tr>
                <tr>
                <th id="tabHeadextIdvDate">External IDV Date</th>
                <td id="tabDataextIdvDate"></td>
                </tr> 
                <tr>
                <th id="tabHeadAddressline1">Address line 1</th>
                <td id="tabDataAddressline1"></td>
                </tr>  
                <tr>
                <th id="tabHeadAddressline2">Address line 2</th>
                <td id="tabDataAddressline2"></td>
                </tr>  
				<tr>
                <th id="tabHeadCity">City</th>
                <td id="tabDataCity"></td>
                </tr>
				<tr>
                <th id="tabHeadState">State</th>
                <td id="tabDataState"></td>
                </tr>        
                <tr>
                <th id="tabHeadPincode">Pincode</th>
                <td id="tabDataPincode"></td>
                </tr>
                <tr>
                <th id="tabHeadCountry">Country</th>
                <td id="tabDataCountry"></td>
                </tr> 
                <tr>
                <th id="tabHeadUsage">Usage</th>
                <td id="tabDataUsage"></td>
                </tr>  
                <tr>
                <th id="tabHeadDocumentType">Document Type</th>
                <td id="tabDataDocumentType"></td>
                </tr>  
				<tr>
                <th id="tabHeadRegistrationNumberType">Registration Number Type</th>
                <td id="tabDataRegistrationNumberType"></td>
                </tr>
				<tr>
                <th id="tabHeadRegistrationNumber">Registration Number</th>
                <td id="tabDataRegistrationNumber"></td>
                </tr>
                <tr>
                <th id="tabHeadCustomerNumber">Customer Number</th>
                <td id="tabDataCustomerNumber"></td>
                </tr> 
               <!--  <tr>
                <th id="tabHeadEVRoleType">EV Role Type</th>
                <td id="tabDataEVRoleType"></td>
                </tr>  
                <tr>
                <th id="tabHeadEVBusinessEntityName">EV Business Entity Name</th>
                <td id="tabDataEVBusinessEntityName"></td>
                </tr>  
				<tr>
                <th id="tabHeadEVRecepientEmail1">EV Recepient Email1</th>
                <td id="tabDataEVRecepientEmail1"></td>
                </tr>
				<tr>
                <th id="tabHeadEVRecepientEmail2">EV Recepient Email2</th>
                <td id="tabDataEVRecepientEmail2"></td>
                </tr> -->
				<tr>
                <th id="tabHeadIsSoleTrader">Is Sole Trader</th>
                <td id="tabDataIsSoleTrader"></td>
                </tr>
				<tr>
                <th id="tabHeadAttributeName" >Attribute Name</th>
                <td id="tabDataAttributeName" style="word-wrap: break-word;"></td>
                </tr>
				<tr>
                <th id="tabHeadAttributeValue" >Attribute Value</th>
                <td id="tabDataAttributeValue" style="word-wrap: break-word;"></td>
                </tr>
                               <tr>
                <th id="tabHeadRequestedAction">Requested Action</th>
                <td id="tabDataRequestedAction"></td>
                </tr>
				 <tr>
                <th id="tabHeadInvolvedPartyNameType">InvolvedParty Name Type</th>
                <td id="tabDataInvolvedPartyNameType"></td>
                </tr>
				<tr>
                <th id="tabHeadIsOtherName">Is Other Name</th>
                <td id="tabDataIsOtherName"></td>
                </tr>
				<tr>
                <th id="tabHeadhfcpFullName">Has For Contact Person – Full Name</th>
                <td id="tabDataHfcpFullName"></td>
                </tr>
				<tr>
                <th id="tabHeadIsRegulatedBy">Is Regulated By</th>
                <td id="tabDataIsRegulatedBy"></td>
                </tr>
				<tr>
				<th id="tabHeadIsIssuedAtCountry">Is IssuedAt-Country</th>
                <td id="tabDataIsIssuedAtCountry"></td>
                </tr>
				<tr>
				<th id="tabHeadIsIssuedAtState">Is IssuedAt-State</th>
                <td id="tabDataIsIssuedAtState"></td>
                </tr>
				<tr>
				<th id="tabHeadIsForeignRegistered">Is ForeignRegistered</th>
                <td id="tabDataIsForeignRegistered"></td>
                </tr>
				<tr>
				<th id="tabHeadiparAddressLine1">InvolvedPartyAssociationRole AddressLine1</th>
                <td id="tabDataiparAddressLine1"></td>
                </tr>
				<tr>
				<th id="tabHeadiparCity">InvolvedPartyAssociationRole city</th>
                <td id="tabDataiparCity"></td>
                </tr>
				<tr>
				<th id="tabHeadiparState">InvolvedPartyAssociationRole state</th>
                <td id="tabDataiparState"></td>
                </tr>
				<tr>
				<th id="tabHeadiparPostCode">InvolvedPartyAssociationRole postcode</th>
                <td id="tabDataiparPostCode"></td>
                </tr>
				<tr>
				<th id="tabHeadiparCountry">InvolvedPartyAssociationRole country</th>
                <td id="tabDataiparCountry"></td>
                </tr>
				<tr>
				<th id="tabHeadiparRoleType">InvolvedPartyAssociationRole roleType</th>
                <td id="tabDataiparRoleType"></td>
                </tr>
				<tr>
				<th id="tabHeadiparCisKey">InvolvedPartyAssociationRole CISKey</th>
                <td id="tabDataiparCisKey"></td>
                </tr>
				<tr>
				<th id="tabHeadiparRoleId">InvolvedPartyAssociationRole RoleID</th>
                <td id="tabDataiparRoleId"></td>
                </tr>
        </thead> 
     
    </table>
        <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
        <li>
            <a href="#nogo" class="primaryButton jq-formSubmit" title="Confirm" id="confirmBtn" align="right" margin-top="50px">Confirm</a>
        </li>
        <li>
            <a href="#" class="baseLink baseLinkClear" title="Cancel" id="cancelBtn">
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
            <li class="noticeBoxText emphasis"><p><cms:content name="uim0129"/>Optional - Message for User</p></li>
        </ul>
      </div>
       </div> 
      </div>  
        
  
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOpsCalender.js'/>"></script>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/duplicate.js'/>"></script> 
 