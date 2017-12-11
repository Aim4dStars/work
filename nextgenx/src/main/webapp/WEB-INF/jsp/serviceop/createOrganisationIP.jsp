<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form name="serviceOps_form" id="createOrganisationIpForm" method="POST" action="/ng/secure/page/serviceOps/createOrganisationIP" class="jq-clientDetailActionForm"  commandName="reqModel">
   <input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
    <table>
     <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="silo" class="formLabel">Silo</label></td>
            <td class="subHeaderTextItem">
                <select name="silo" id="silo" class="">
                    <option value="WPAC">WPAC</option>
                    <option value="BTPL">BTPL</option>
                </select>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="cisKey" class="formLabel">Full Name</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="fullName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="fullName"
                            data-validation=""
                            data-validation-required-error="Please enter full name"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="personType" class="formLabel">Role Type</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="personType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="personType"
                            data-validation=""
                            data-min-length="9" 
							data-max-length="9"
                            data-validation-required-error="Please enter role type"
                    data-validation-minLength-error="Please enter at least 9 digit role type"
                    data-validation-searchNumber-error="Please enter numbers"
                            data-ng-key="searchCriteria"
                            type="text"  value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
		<tr id="lastNameP" class="">
			<td class="subHeaderTextItem verticalMiddle"><label
				for="sourceextIdvDate" class="formLabel">Effective Start Date</label></td>
			<td class="subHeaderTextItem">
			 <span class="calendarPlaceHolder jq-effectiveStartDateCalendarPlaceHolder"></span>
			          <span class="iconWrapper">

			           <input id="effectiveStartDate" name="effectiveStartDate" data-calendar=".jq-effectiveStartDateCalendarPlaceHolder"
                              class="formTextInput jq-effectiveStartDate inputStyleThree" value="" data-placeholder=""
                              data-validation=""/>
			          <a class="iconActionButton jq-effectiveStartDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                          <em class="iconcalendar"><span>Select Date</span></em>
                          <em class="iconarrowfulldown"></em>
                      </a>
			         </span></td>
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="isForeignRegistered" class="formLabel">IsForeignRegistered</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="isForeignRegistered" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="isForeignRegistered"
                            data-validation=""
                            data-min-length="9" 
							data-max-length="9"
                            data-validation-required-error="Please enter IsForeignRegistered"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="registrationNumber" class="formLabel">Registration Number</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="registrationNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationNumber"
                            data-validation=""
                            data-validation-required-error="Please enter Registration Number"
                            data-validation-searchNumber-error="Please enter  Registration Number"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="registrationNumberType" class="formLabel">Registration Number Type</label></td>
            <td class="subHeaderTextItem">
                <select name="registrationNumberType" id="registrationNumberType"
                    class="">
                    <option value="select">Select One</option>
                    <option value="ACN">ACN</option>
                    <option value="ABN">ABN</option>
                     <option value="ARSN">ARSN</option>
                      <option value="FOREIGN">FOREIGN</option>
                       <option value="DRIVING_LICENCE">DRIVING_LICENCE</option>
                        <option value="UNKN">UNKN</option>
                         <option value="ASIC_ORG_ID">ASIC_ORG_ID</option>
                          <option value="Licence">Licence</option>
                           <option value="Certificate">Certificate</option>
                            <option value="Membership">Membership</option>
                             <option value="TFN">TFN</option>
                             <option value="TIN">TIN</option>
                             <option value="PPSR">PPSR</option>
                             
                </select>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="isIssuedAtC" class="formLabel">IsIssuedAt(country)</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="isIssuedAtC" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="isIssuedAtC"
                            data-validation=""
                            data-validation-required-error="Please enter IsIssuedAt Country"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="isIssuedAtS" class="formLabel">IsIssuedAt(state)</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="isIssuedAtS" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="isIssuedAtS"
                            data-validation=""
                            data-validation-required-error="Please enter IsIssuedAt State"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
    
     <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="industryCode" class="formLabel">Industry Code</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="industryCode" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="industryCode"
                            data-validation=""
                            data-validation-required-error="Please enter Industry Code"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="addrspriorityLevel" class="formLabel">Priority Level</label></td>
            <td class="subHeaderTextItem">
                <select name="priorityLevel" id="priorityLevel"
                    class="">
                    <option value="select">Select One</option>
                    <option value="Primary">Primary</option>
                    </select>
                    </td>
                    </tr>
                    
                    <tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="addrspriorityLevel" class="formLabel">Address Priority Level</label></td>
            <td class="subHeaderTextItem">
                <select name="addrspriorityLevel" id="addrspriorityLevel"
                    class="">
                    <option value="select">Select One</option>
                    <option value="Primary">Primary</option>
                    </select>
                    </td>
                    </tr>
                    
                    <tr>
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="usage" class="formLabel">Usage</label></td>
            <td class="subHeaderTextItem">
                <select name="usage" id="usage"
                    class="">
                    <option value="select">Select One</option>
                    <option value="G">G</option>
                    </select>
                    </td>
                    </tr>
                    
                    <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="addresseeNameText" class="formLabel">Addressee Name</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="addresseeNameText" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="addresseeNameText"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="addressType" class="formLabel">Address Type</label></td>
            <td class="subHeaderTextItem">
                <select name="addressType" id="addressType"
                    class="">
                    <option value="StandardPostalAddress">StandardPostalAddress</option>
                    
                    </select>
                    </td>
                    </tr>
                    
               <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="streetNumber" class="formLabel">Street Number</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="streetNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="streetNumber"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="streetName" class="formLabel">Street Name</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="streetName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="streetName"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="streetNumber" class="formLabel">Street Type</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="streetType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="streetType"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
       
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="city" class="formLabel">City</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="city" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="city"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="state" class="formLabel">State</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="state" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="state"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="postCode" class="formLabel">PostCode</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="postCode" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="postCode"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="country" class="formLabel">Country</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="country" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="country"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="organisationLegalStructureValue" class="formLabel">Organisation Legal Structure Value</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="organisationLegalStructureValue" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="organisationLegalStructureValue"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="purposeOfBusinessRelationship" class="formLabel">Purpose Of Business Relationship</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="purposeOfBusinessRelationship" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="purposeOfBusinessRelationship"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="sourceOfFunds" class="formLabel">Source Of Funds</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="sourceOfFunds" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="sourceOfFunds"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="sourceOfWealth" class="formLabel">Source Of Wealth</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="sourceOfWealth" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="sourceOfWealth"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="characteristicType" class="formLabel">Characteristic Type</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="characteristicType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="characteristicType"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
         <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="characteristicCode" class="formLabel">Characteristic Code</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="characteristicCode" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="characteristicCode"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="characteristicValue" class="formLabel">Characteristic Value</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="characteristicValue" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="characteristicValue"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="characteristicValue" class="formLabel">Foreign Registration Number</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="frn" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="frn"
                            data-ng-key="searchCriteria"
                            type="text" value="" autocomplete="off">
                    </input>
                </span>
            </td>
        </tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="registrationNumberType" class="formLabel">Foreign Registration Number Type</label></td>
            <td class="subHeaderTextItem">
                <select name="frntype" id="frntype"
                    class="">
                    <option value="select">Select One</option>
                    <option value="ACN">ACN</option>
                    <option value="ABN">ABN</option>
                     <option value="ARSN">ARSN</option>
                      <option value="FOREIGN">FOREIGN</option>
                       <option value="DRIVING_LICENCE">DRIVING_LICENCE</option>
                        <option value="UNKN">UNKN</option>
                         <option value="ASIC_ORG_ID">ASIC_ORG_ID</option>
                          <option value="Licence">Licence</option>
                           <option value="Certificate">Certificate</option>
                            <option value="Membership">Membership</option>
                             <option value="TFN">TFN</option>
                             <option value="TIN">TIN</option>
                             <option value="PPSR">PPSR</option>
                             
                </select>
            </td>
        </tr>
        
       
    </table>
    <input type="submit" class="primaryButton jq-formSubmit" value="Submit">
</form>


    <div class="modalContent jq-cloak jq-confirmModal">
    
        <div class="modalContentMod3">
        <h1 class="formHeaderModal">
        <span class="baseGama" id="usecaseSpan"></span> using below details ?
    </h1>
        <table class="" style="text-align:left;line-height:1.5">
        <thead>
            <tr>
                <th id="tabHeadSilo">Silo</th>
                <td id="tabDataSilo"></td>               
            </tr>
            
            <tr style="margin-bottom:5px">
                <th id="tabHeadFullName">Full Name</th>
                <td id="tabDataFullName"></td>
                </tr>
                
                <tr style="margin-top:10px;">
                <th id="tabHeadPersonType">Person Type</th>
                <td id="tabDataPersonType"></td>
                </tr>
               
                
                <tr>
                <th id="tabHeadHeadIsForeignRegistered">IsForeignRegistered</th>
                <td id="tabIsForeignRegistered"></td>
                </tr>
                <tr>
                <th id="tabHeadRegNo">Registration Number</th>
                <td id="tabRegNo"></td>
                </tr>
                <tr>
                <th id="tabHeadRegNoType">Registration Number Type</th>
                <td id="tabRegNoType"></td>
                </tr>                  
                <tr>
                <th id="tabHeadIsIssuedAtC">IsIssuedAt(Country)</th>
                <td id="tabIsIssuedAtC"></td>
                </tr>
                <tr>
                <th id="tabHeadIsIssuedAtS">IsIssuedAt(State)</th>
                <td id="tabIsIssuedAtS"></td>
                </tr>
                <tr>
                <th id="tabHeadIndustryCode">Industry Coder</th>
                <td id="tabIndustryCode"></td>
                </tr>        
                <tr>
                <th id="tabHeadPriorityLevel">Priority Level</th>
                <td id="tabPriorityLevel"></td>
                </tr>
                <tr>
                <th id="tabHeadAddressPriorityLevel">Address Priority Level</th>
                <td id="tabAddressPriorityLevel"></td>
                </tr> 
                <tr>
                <th id="tabHeadUsage">Usage</th>
                <td id="tabUsage"></td>
                </tr>  
                <tr>
                <th id="tabHeadAddresseeName">Addressee Name</th>
                <td id="tabAddresseeName"></td>
                </tr>  
                <tr>
                <th id="tabHeadAddressType">Address Type</th>
                <td id="tabAddressType"></td>
                </tr>  
                <tr>
                <th id="tabHeadStreetNumber">Street Number</th>
                <td id="tabStreetNumber"></td>
                </tr>  
                <tr>
                <th id="tabHeadStreetType">Street Type</th>
                <td id="tabStreetType"></td>
                </tr>  
                 <tr>
                <th id="tabHeadStreetName">Street Name</th>
                <td id="tabStreetName"></td>
                </tr>  
                 <tr>
                <th id="tabHeadCity">City</th>
                <td id="tabCity"></td>
                </tr> 
                <tr>
                <th id="tabHeadState">State</th>
                <td id="tabState"></td>
                </tr>
                <tr>
                <th id="tabHeadPostCode">PostCode</th>
                <td id="tabPostCode"></td>
                </tr>
                <tr>
                <th id="tabHeadCountry">Country</th>
                <td id="tabCountry"></td>
                </tr>
                <tr>
                <th id="tabHeadOrganisationLegalStructureValue">Organisation Legal Structure Value</th>
                <td id="tabOrganisationLegalStructureValue"></td>
                </tr> 
                <tr>
                <th id="tabHeadPurposeOfBusinessRelationship">Purpose Of Business Relationship</th>
                <td id="tabPurposeOfBusinessRelationship"></td>
                </tr> 
                <tr>
                <th id="tabHeadSourceOfFunds">Source Of Funds</th>
                <td id="tabSourceOfFunds"></td>
                </tr>
                 <tr>
                <th id="tabHeadSourceOfWealth">Source Of Wealth</th>
                <td id="tabSourceOfWealth"></td>
                </tr>  
                
                <tr>
                <th id="tabHeadCharacteristicType">Characteristic Type</th>
                <td id="tabCharacteristicType"></td>
                </tr>  
                 
                <tr>
                <th id="tabHeadCharacteristicCode">Characteristic Code</th>
                <td id="tabCharacteristicCode"></td>
                </tr>  
                <tr>
                <th id="tabHeadCharacteristicValue">Characteristic Value</th>
                <td id="tabCharacteristicValue"></td>
                </tr> 
                 <tr>
                <th id="tabHeadForeignRegistrationNumber">Foreign Registration Number</th>
                <td id="tabForeignRegistrationNumber"></td>
                </tr> 
                 <tr>
                <th id="tabHeadForeignRegistrationNumberType">Foreign Registration Number Type</th>
                <td id="tabForeignRegistrationNumberType"></td>
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
