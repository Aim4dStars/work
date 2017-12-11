<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<form name="serviceOps_form" id="maintainIpContactForm"
	method="POST"
	action="/ng/secure/page/serviceOps/maintainIpContactMethod"
	class="jq-clientDetailActionForm" commandName="maintainIPContactMethodModel">
	
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>"
		name="cssftoken" />
	<table>
	<tr id="siloP" class=".jq-silo">
			<td class="subHeaderTextItem verticalMiddle"><label for="silo"
				class="formLabel">Silo</label></td>
			<td class="subHeaderTextItem"><select name="silo" id="silo"
				class="inputStyleEight">
					<option value="WPAC">WPAC</option>
					<option value="BTPL">BTPL</option>
			</select></td>
		</tr>
		
		<tr id="roleP" class="jq-personType">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Person Type</label></td>
			<td class="subHeaderTextItem"><select name="personType" id="personType"
				class="inputStyleEight">
					<option value="Individual">Individual</option>
					<option value="Organisation">Organisation</option>
			</select></td>
		</tr>
		
		<tr id="agentCisKeyP" class="jq-cisKey">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceAgentCISKey" class="formLabel">CIS Key</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="cisKey"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="cisKey"
					placeholder="Please enter cisKey"
					data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]"
					data-min-length="11" data-max-length="11"
					data-validation-required-error="Please enter cisKey"
					data-validation-minLength-error="Please enter at least 11 digit cisKey"
					data-validation-searchNumber-error="Please enter numbers"
					data-ng-key="searchCriteria" type="text" maxlength="11" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		
	
		<tr id="roleP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Requested Action</label></td>
			<td class="subHeaderTextItem"><select name="requestedAction" id="requestedAction"
				class="inputStyleEight">
					<option value="Add">Add</option>
					<option value="Modify">Modify</option>
					<option value="Delete">Delete</option>
			</select></td>
		</tr>
		
		
		<tr id="roleP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Address Type</label></td>
			<td class="subHeaderTextItem"><select name="addressType" id="addressType"
				class="inputStyleEight">
					<option value="EMAIL">Email</option>
					<option value="MOBILE">Mobile</option>
			</select></td>
		</tr>
		<tr id="roleP" class="jq-usageId">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Usage Id</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="usageId"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="usageId"
					placeholder="Please enter usageId"
					data-validation="validate[required]"
					data-validation-required-error="Please enter usageId"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		
		<tr id="roleP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Validity Status</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="validityStatus"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="validityStatus"
					placeholder="Please enter Validity Status"
					data-validation="validate[required]"
					data-validation-required-error="Please enter Validity Status"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
	
	<tr id="roleP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Priority Level</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="priorityLevel"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="priorityLevel"
					placeholder="Please enter Priority Level"
					data-validation="validate[required]"
					data-validation-required-error="Please enter Priority Level"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		
		
		<tr id="roleP" class="jq-emailId">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Email Address</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="emailAddress"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="emailAddress"
					placeholder="Please enter Email Address"
					data-validation="validate[custom[customFunction]]"
					data-validation-required-error="Please enter Email Address"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="roleP" class="jq-countryCode noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Country Code</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="countryCode"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="countryCode"
					placeholder="Please enter Country Code"
					data-validation="validate[custom[customFunction]]"
					data-validation-required-error="Please enter Country Code"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		<tr id="roleP" class="jq-areaCode noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Area Code</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="areaCode"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="areaCode"
					placeholder="Please enter Area Code"
					data-validation="validate[custom[searchNumber], custom[customFunction]]"
					data-validation-required-error="Please enter Area Code"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		
		<tr id="roleP" class="jq-localNo noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Local Number</label></td>
			<td class="subHeaderTextItem"><span
				class="inputWrapper iconWrapper"> <input id="localNumber"
					class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
					name="localNumber"
					placeholder="Please enter Local Number"
					data-validation="validate[custom[searchNumber], custom[customFunction]]"
					data-validation-required-error="Please enter Local Number"
					data-ng-key="searchCriteria" type="text" value=""
					autocomplete="off"></input>
			</span></td>
		</tr>
		
		<tr id="roleP" class="jq-contactMedium noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourcePersonType" class="formLabel">Contact Medium</label></td>
			<td class="subHeaderTextItem"><select name="contactMedium" id="contactMedium"
				class="inputStyleEight">
					<option value="MOBILE">Mobile</option>
					<option value="PHONE">Phone</option>
			</select></td>
		</tr>
		</table>
	 <input type="submit" class="primaryButton jq-formSubmit" value="Submit" id="submitBtn">	
	</form>
	
	    <div class="modalContent jq-cloak jq-confirmModal">
    
        <div class="modalContentMod3">
        <h1 class="formHeaderModal">
        <span class="baseGama" id="usecaseSpan"></span> using below details ?
    </h1>
        <table class="" style="text-align:left;line-height:1.5">
        <thead>
           
                
               
                <tr style="margin-bottom:5px">
                <th id="tabHeadSilo">Silo</th>
                <td id="tabDataSilo"></td>
                </tr>
                
                <tr style="margin-top:10px;">
                <th id="tabHeadPersonType">Person Type</th>
                <td id="tabDataPersonType"></td>
                </tr>
                
                <tr>
                <th id="tabHeadcisKey">CIS Key</th>
                <td id="tabDatacisKey"></td>
                </tr>
                <tr>
                <th id="tabHeadrequestedAction">Requested Action</th>
                <td id="tabDatarequestedAction"></td>
                </tr>
                <tr>
                <th id="tabHeadaddressType">Address Type</th>
                <td id="tabDataaddressType"></td>
                </tr>                  
                <tr>
                <th id="tabHeadusageId">Usage Id</th>
                <td id="tabDatausageId"></td>
                </tr>
                <tr>
                <th id="tabHeadvalidityStatus">Validity Status</th>
                <td id="tabDatavalidityStatus"></td>
                </tr>
                <tr>
                <th id="tabHeadpriorityLevel">Priority Level</th>
                <td id="tabDatapriorityLevel"></td>
                </tr>        
                <tr>
                <th id="tabHeademailAddress">Email Address</th>
                <td id="tabDataemailAddress"></td>
                </tr>
                <tr>
                <th id="tabHeadcountryCode">Country Code</th>
                <td id="tabDatacountryCode"></td>
                </tr> 
                <tr>
                <th id="tabHeadareaCode">Area Code</th>
                <td id="tabDataareaCode"></td>
                </tr>
                 <tr>
                <th id="tabHeadlocalNumber">Local Number</th>
                <td id="tabDatalocalNumber"></td>
                </tr>
                 <tr>
                <th id="tabHeadcontactMedium">Contact Medium</th>
                <td id="tabDatacontactMedium"></td>
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