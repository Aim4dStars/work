<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form name="serviceOps_form" id="maintainIPtoIPRelationship" method="POST" action="/ng/secure/page/serviceOps/maintainIpToIpRelationship" class="jq-clientDetailActionForm" commandName="reqModel">
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
	<table>
	<tr>
		<td class="subHeaderTextItem verticalMiddle"><label for="useCaseName" class="formLabel">Please select Use Case</label></td>
		<td class="subHeaderTextItem">
            <select name="useCase" id="useCaseName" class="inputStyleEight">
                <option value="notselect">--Select--</option>
                <option value="Add">Add Relationship</option>
                <option value="Modify">Modify Relationship</option>
            </select>
		</td>
	</tr>
	<tr id="siloP" class="noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="silo" class="formLabel">Silo</label></td>
		<td class="subHeaderTextItem">
		    <select name="silo" id="silo" class="inputStyleEight">
			    <option value="WPAC">WPAC</option>
			    <option value="BTPL">BTPL</option>
		    </select>
		</td>
	</tr>
	<tr id="sourcePersonTypeP" class="noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="sourcePersonType" class="formLabel">Source Person Type</label></td>
		<td class="subHeaderTextItem">
            <select name="sourcePersonType" id="sourcePersonType" class="inputStyleEight">
                <option value="Individual">Individual</option>
                <option value="Organisation">Organisation</option>
            </select>
		</td>
	</tr>
	<tr id="sourceCisKeyP"  class="noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="sourceCISKey" class="formLabel">Source CIS key</label></td>
		<td class="subHeaderTextItem">
            <span class="inputWrapper iconWrapper">
            <input id="sourceCISKey" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="sourceCISKey"
                data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]"
                data-min-length="11"
                data-max-length="11"
                data-validation-required-error="Please enter CIS key"
                data-validation-minLength-error="Please enter at least 11 digit CIS Key"
                data-validation-searchNumber-error="Please enter numbers"
                data-ng-key="searchCriteria"
                type="text" maxlength="11" value="" autocomplete="off"></input>
            </span>
		</td>
	</tr>
	<tr id="targetPersonTypeP" class="noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="targetPersonType" class="formLabel">Target Person Type</label></td>
		<td class="subHeaderTextItem"><select name="targetPersonType" id="targetPersonType" class="inputStyleEight">
			<option value="Individual">Individual</option>
			<option value="Organisation">Organisation</option>
		</select></td>
	</tr>
	<tr id="targetCisKeyP"  class="noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="targetCISKey" class="formLabel">Target CIS key</label></td>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input id="targetCISKey" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="targetCISKey"
			data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]" 
			data-min-length="11" 
			data-max-length="11"
			data-validation-required-error="Please enter CIS key"
			data-validation-minLength-error="Please enter at least 11 digit CIS Key"
			data-validation-searchNumber-error="Please enter numbers"
			data-ng-key="searchCriteria"
			type="text" maxlength="11" value="" autocomplete="off"></input>
		</span></td>
	</tr>
	<tr id="partyRelTypeP" class="noDisplay">
        <td class="subHeaderTextItem verticalMiddle"><label for="partyRelType" class="formLabel">Party Rel Type</label></td>
        <td class="subHeaderTextItem"><select name="partyRelType" id="partyRelType" class="inputStyleEight">
            <option value="ST">Settler of Trust</option>
            <option value="DIR">Director</option>
            <option value="SC">Secretary</option>
            <option value="SO">Signing Officer</option>
            <option value="SH">Shareholder</option>
            <option value="MB">Member</option>
            <option value="BEN">Beneficiary</option>
            <option value="TRST">Trustee</option>
            <option value="BEO">Beneficial Owner</option>
            <option value="REN">Responsible Entity</option>
        </select></td>
    </tr>
	<tr id="partyRelStatusP" class="noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="partyRelStatus" class="formLabel">Party Rel Status</label></td>
		<td class="subHeaderTextItem"><select name="partyRelStatus" id="partyRelStatus" class="inputStyleEight">
			<option value="Active">Active</option>
			<option value="Inactive">Inactive</option>
		</select></td>
	</tr>
	
	<tr id="partyRelStartDateP" class="noDisplay">
            <td class="subHeaderTextItem verticalMiddle"><label
                for="partyRelStartDate" class="formLabel">Party Rel Start Dat</label></td>
            <td class="subHeaderTextItem">
                <span class="calendarPlaceHolder jq-dateOfBirthPickerHolder"></span>
                <span class="iconWrapper">
                    <input id="partyRelStartDate" name="partyRelStartDate" data-calendar=".jq-dateOfBirthPickerHolder"
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
        <tr  id="partyRelEndDateP" class="noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="partyRelEndDate" class="formLabel">Party Rel End Date</label></td>
			<td class="subHeaderTextItem">
			    <span class="calendarPlaceHolder jq-effectiveStartDateCalendarPlaceHolder"></span>
			    <span class="iconWrapper">
			        <input id="partyRelEndDate" name="partyRelEndDate" data-calendar=".jq-effectiveStartDateCalendarPlaceHolder"
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
	
	<tr id="versionNumberP" class="jq-version noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="versionNumber" class="formLabel" >Version Number</label></td>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input id="versionNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="versionNumber"
			data-validation="validate[custom[customFunction]]" 
			data-validation-searchNumber-error="Please enter numbers"
			data-ng-key="searchCriteria"
			type="text" value="" autocomplete="off" >
		</span></td>
	</tr>
	</table>

	<input type="submit" class="primaryButton jq-formSubmit" value="Submit" disabled>
</form>
 <div class="modalContent jq-cloak jq-confirmModal">
    
        <div class="modalContentMod3">
        <h1 class="formHeaderModal">
        <span class="baseGama" id="usecaseContent"></span> using below details ?
    </h1>
    <table class="" style="text-align:left;line-height:1.5">
        <thead>
            <tr>
                <th id="tabHeadUseCase">Use Case</th>
                <td id="selectedUsecase"></td>               
            </tr>
                <tr style="margin-top:10px;">
                <th id="tabHeadSilo">Silo</th>
                <td id="tabDataSilo"></td>
                </tr>
               
                <tr style="margin-bottom:5px">
                <th id="tabHeadSPersonType">Source Person Type</th>
                <td id="tabDataSPersonType"></td>               
                </tr>
             
                <tr>
                <th id="tabHeadSCis">Source CIS Key</th>
                <td id="tabDataSCisKey"></td>
                </tr>
                <tr>
                <th id="tabHeadTPersonType">Target Person Type</th>
                <td id="tabDataTPersonType"></td>
                </tr>        
                <tr>
                <th id="tabHeadTCis">Target CIS Key</th>
                <td id="tabDataTCisKey"></td>
                </tr>
                <tr>
                <th id="tabHeadPartyRelType">Party Rel Type</th>
                <td id="tabDataPartyRelType"></td>
                </tr> 
                <tr>
                <th id="tabHeadPartyRelStatus">Party Rel Status</th>
                <td id="tabDataPartyRelStatus"></td>
                </tr>  
                 <tr>
                <th id="tabHeadPartyRelStartDt">Party Rel Start Date</th>
                <td id="tabDataPartyRelStartDt"></td>
                </tr>  
                 <tr>
                <th id="tabHeadPartyRelEndDt">Party Rel End Date</th>
                <td id="tabDataPartyRelEndDt"></td>
                </tr>  
                <tr>
                <th id="tabHeadVersionNo">Version Number</th>
                <td id="tabDataVersionNo"></td>                   
        </thead> 
     
    </table>
        <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
        <li>
            <a href="#nogo" class="primaryButton jq-formSubmit" title="Confirm" id="confirmButton" align="right" margin-top="50px">Confirm</a>
        </li>
        <li>
            <a href="#" class="baseLink baseLinkClear" title="Cancel" id="cancelButton">
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
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOpsCalender.js'/>"></script>    
