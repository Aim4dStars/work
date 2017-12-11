<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<form name="serviceOps_form" id="maintainArrangementForm" method="POST" action="/ng/secure/page/serviceOps/maintainArrangementAndrelationship" class="jq-clientDetailActionForm" commandName="reqModel">
	<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
	<table>
	<tr class="jq-useCaseName">
		<td class="subHeaderTextItem verticalMiddle"><label for="useCaseName" class="formLabel">Use Case Name</label></td>
		<td class="subHeaderTextItem"><select name="useCase" id="useCaseName" class="">
			<option value="notselect">--Select---</option>
			<option value="ipar">Create IP_AR (RoleType SOL)</option>
			<option value="ipsar">Create IP_SAR</option>
			<option value="iparthirdparty">Create IP_AR (RoleType Third Party)</option>
			<option value="enddateiparsol">End Date IP_AR (RoleType SOL)</option>
			<option value="enddateipsar">End Date IP_SAR</option>
			<option value="enddateiparthirdparty">End Date IP_AR (RoleType Third Party)</option>
		</select></td>
	</tr>
	<tr class="jq-personType noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="personType" class="formLabel">Person Type</label></td>
		<td class="subHeaderTextItem"><select name="personType" id="personType" class="">
			<option value="Individual">Individual</option>
			<option value="Organisation">Organisation</option>
		</select></td>
	</tr>
	<tr class="jq-silo noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="silo" class="formLabel">Silo</label></td>
		<td class="subHeaderTextItem"><select name="silo" id="silo" class="">
			<option value="WPAC">WPAC</option>
			<option value="BTPL">BTPL</option>
		</select></td>
	</tr>
	<tr id="productCpc1" class="jq-productCPC noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="product" class="formLabel">Product/CPC</label></td>
		<td class="subHeaderTextItem"><select name="productCpc" id="productCpc" class="">
			<option value="35d1b65704184ae3b87799400f7ab93c">BT Invest</option>
			<option value="457eba2b65ca4c2f937d0deae9866312">BT Panorama Service</option>
			<option value="797475d1e1b246528c49ef8a75a9315e">BT Panorama Super</option>
			<option value="b38a555bc9bc43e88b776219057a67b8">BT Invest Direct</option>
		</select></td>
	</tr>
	<tr id="versionNumberArP" class="jq-versionAR noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="versionNumberAr" class="formLabel">Version Number @AR</label></td>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input id="versionNumberAr" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="versionNumberAr"
			data-validation="validate[custom[customFunction]]" 
			data-validation-searchNumber-error="Please enter numbers"
			data-ng-key="searchCriteria"
			type="text" value="" autocomplete="off" >
		</span></td>
	</tr>
	<tr id="versionNumberIpArP" class="jq-versionIPAR noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="versionNumberIpAr" class="formLabel">Version Number @IP_AR</label></td>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input id="versionNumberIpAr" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="versionNumberIpAr"
		    data-validation="validate[custom[customFunction]]"
			data-validation-searchNumber-error="Please enter numbers"
			data-ng-key="searchCriteria"
			type="text" value="" autocomplete="off" >
		</span></td>
	</tr>
	<tr id="lifecycleStatusReasonP" class="jq-lifeCycleStatus noDisplay">
        <td class="subHeaderTextItem verticalMiddle"><label for="lifecycleStatusReason" class="formLabel">Life Cycle Status Reason</label></td>
        <td class="subHeaderTextItem"><select name="lifecycleStatusReason" id="lifecycleStatusReason" class="">
            <option value="Unrestricted">Unrestricted</option>
            <option value="Restricted">Restricted</option>
        </select></td>
    </tr>
	<tr id="cisKeyP" class="jq-cisKey noDisplay">
	    <td class="subHeaderTextItem verticalMiddle"><label for="cisKey" class="formLabel">CIS key</label></td>
	    <td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
	        <input id="cisKey" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="cisKey"
                    data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]"
                    data-min-length="11"
                    data-max-length="11"
                    data-validation-required-error="Please enter CIS key"
                    data-validation-minLength-error="Please enter at least 11 digit CIS Key"
                    data-validation-searchNumber-error="Please enter numbers"
                    data-ng-key="searchCriteria"
			        type="text" maxlength="11" value="" autocomplete="off">
			</input>
		</span></td>
	</tr>
	<tr id="accountNumberP" class="jq-accNumber noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="accountNumber" class="formLabel">Account Number</label>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input  id="accountNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="accountNumber"
			        data-validation="validate[custom[searchNumber], custom[customFunction]]"
			        data-validation-searchNumber-error="Please enter numbers"
			        data-ng-key="searchCriteria"
			        type="text" value="" autocomplete="off">
			</input>
		</span></td>
	</tr>
	<tr id="panNumberP" class="jq-panNumber noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="panNumber" class="formLabel">PAN Number</label>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input id="panNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="panNumber"
                data-validation="validate[custom[searchNumber], custom[customFunction]]"
                data-min-length="9"
                data-max-length="9"
                data-validation-required-error="Please enter PAN Number"
                data-validation-minLength-error="Please enter 9 digit PAN Number"
                data-validation-searchNumber-error="Please enter numbers"
                data-ng-key="searchCriteria"
                type="text" maxlength="9" value="" autocomplete="off">
			</input>
		</span></td>
	</tr>
	<tr id="bsbNumberP" class="jq-bsbNumber noDisplay">
		<td class="subHeaderTextItem verticalMiddle"><label for="bsbNumber" class="formLabel">BSB Number</label></td>
		<td class="subHeaderTextItem"><span class="inputWrapper iconWrapper">
		    <input id="bsbNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="bsbNumber"
			data-validation="validate[custom[searchNumber], custom[customFunction]]"
			data-min-length="6" 
			data-max-length="6"
			data-validation-required-error="Please enter BSB Number"
			data-validation-minLength-error="Please enter 6 digit BSB Number"
			data-validation-searchNumber-error="Please enter numbers"
			data-ng-key="searchCriteria"
			type="text" maxlength="6" value="" autocomplete="off"></input>
		</span></td>
	</tr>
 <tr id="startDateP" class="jq-startDateIPAR noDisplay">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="startDate" class="formLabel">Start Date @ IP_AR_AUTH</label></td>
			<td class="subHeaderTextItem">
			    <span class="calendarPlaceHolder jq-effectiveStartDateCalendarPlaceHolder"></span>
			    <span class="iconWrapper">
			        <input id="startDate" name="startDate" data-calendar=".jq-effectiveStartDateCalendarPlaceHolder"
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
			    <span>
                    <input id='hour' name="hour" class="formTextInput inputStyleOne" placeholder="hrs"
                        data-validation="validate[max,min]"
                        data-min="1" data-max="24"
                        data-validation-min-error ="Please enter valid time"
                        data-validation-max-error="Please enter valid time">
                        </input> :
                    <input id='min' name="min" class="formTextInput inputStyleOne" placeholder="mins"
                        data-validation="validate[max,min]"
                        data-min="0" data-max="59"
                        data-validation-min-error ="Please enter valid time"
                        data-validation-max-error="Please enter valid time">
                        </input> :
                    <input id='sec' name="sec" class="formTextInput inputStyleOne" placeholder="secs"
                        data-validation="validate[max,min]"
                        data-min="0" data-max="59"
                        data-validation-min-error ="Please enter valid time"
                        data-validation-max-error="Please enter valid time">
                    </input>
                </span>
			</td>
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
            <tr>
                <th id="tabHeadUseCase">Use Case</th>
                <td id="selectedUsecase"></td>               
            </tr>
                <tr style="margin-top:10px;">
                <th id="tabHeadPersonType">Person Type</th>
                <td id="tabDataPersonType"></td>
                </tr>
               
                <tr style="margin-bottom:5px">
                <th id="tabHeadSilo">Silo</th>
                <td id="tabDataSilo"></td>
                </tr>
                <tr>
                <th id="tabHeadCpc">Product/CPC</th>
                <td id="tabDataCPC"></td>
                </tr>
                <tr>
                <th id="tabHeadVersionNoAr">Version Number @AR</th>
                <td id="tabDataVersionNoAr"></td>
                </tr>
                <tr>
                <th id="tabHeadVersionNoIpAr">Version Number @IP_AR</th>
                <td id="tabDataVersionNoIpAr"></td>
                </tr>                  
                <tr>
                <th id="tabHeadLifeCycleReason">Life cycle status reason</th>
                <td id="tabDataLifeCycleReason"></td>
                </tr>
                <tr>
                <th id="tabHeadCis">CIS Key</th>
                <td id="tabDataCisKey"></td>
                </tr>
                <tr>
                <th id="tabHeadAccNo">Account number</th>
                <td id="tabDataAccNo"></td>
                </tr>        
                <tr>
                <th id="tabHeadPanNo">Pan number</th>
                <td id="tabDataPanNo"></td>
                </tr>
                <tr>
                <th id="tabHeadBsbNo">BSB Number</th>
                <td id="tabDataBsbNo"></td>
                </tr> 
                <tr>
                <th id="tabHeadStartDate">Start Date</th>
                <td id="tabDataStartDate"></td>
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