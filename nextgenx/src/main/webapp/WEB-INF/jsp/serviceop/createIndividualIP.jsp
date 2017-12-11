<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form name="serviceOps_createIndividaulIP" id="createIndividaulIPForm" method="POST" action="/ng/secure/page/serviceOps/createIndividualIP" class="jq-clientDetailActionForm" commandName="reqModel">
    <input id="token" type="hidden" value="<c:out value='${cssftoken}'/>"
		name="cssftoken" />
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
     	<td class="subHeaderTextItem verticalMiddle"><label for="prefix" class="formLabel">Prefix</label></td>
        <td class="subHeaderTextItem">
        	<select name="prefix" id="prefix" class="">
	            <option value="MR">MR</option>
	            <option value="MRS">MRS</option>
            </select>
        </td>
     </tr>
     <tr>
         <td class="subHeaderTextItem verticalMiddle"><label for="firstName" class="formLabel">First Name</label></td>
         <td class="subHeaderTextItem">
            <span class="inputWrapper iconWrapper">
                <input  id="firstName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="firstName"
                 type="text" data-validation-required-error="Please enter First Name"
                 data-ng-key="searchCriteria"
                 type="text" maxlength="11" value="" autocomplete="off">
                </input>
            </span>
         </td>
      </tr>
      <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="lastName" class="formLabel">Last Name</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="lastName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="lastName"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
     </tr>
       
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="altName" class="formLabel">Alternate Name</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="altName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="altName"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>
      
       <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="gender" class="formLabel">Gender</label></td>
            <td class="subHeaderTextItem">
                <select name="gender" id="gender" class="">
                    <option value="M">Male</option>
                    <option value="F">Female</option>
                    <option value="U">Other</option>
                </select>
            </td>
        </tr>
        <tr>
        <td class="subHeaderTextItem verticalMiddle"><label for="birthDate" class="formLabel">Birth Date</label></td>
           <td class="subHeaderTextItem">
              <span class="iconWrapper">
                   <input  id="birthDate" class="formTextInput jq-failedappDownloadToDate inputStyleThree" data-calendar=".jq-failedappDownloadToDateCalendarPlaceHolder"
                           value="" data-placeholder=""
                           data-validation=""
                           data-validation-required-error="${errors.err00053}"
                           data-validation-date-error="${errors.err00060}"
                           data-validation-future-error="${errors.err00053}"
                           data-validation-customFunction-error="${errors.err00132}">
                   </input>
                   <a  class="jq-failedappDownloadToDateCalendarIcon jq-appendErrorAfter calendarIconLink"
                   title="Date Picker" href="#nogo">
                   <em class="iconcalendar"><span>Select date</span></em> <em class="iconarrowfulldown"></em>
               </a>
              </span>
           </td>
       </tr>
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="isForeignRegistered" class="formLabel">Is Foreign Registered</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="isForeignRegistered" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="isForeignRegistered"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="roleType" class="formLabel">Role Type</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="roleType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="roleType"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>
      <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="purposeOfBusinessRelationship" class="formLabel">Purpose Of Business Relationship</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="purposeOfBusinessRelationship" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="purposeOfBusinessRelationship"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>  
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="sourceOfFunds" class="formLabel">Source Of Funds</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="sourceOfFunds" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="sourceOfFunds"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>    
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="sourceOfWealth" class="formLabel">Source Of Wealth</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="sourceOfWealth" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="sourceOfWealth"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>   
       <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="usage" class="formLabel">Usage</label></td>
            <td class="subHeaderTextItem">
                <select name="usage" id="usage" class="">
                    <option value="R">R</option>
                </select>
            </td>
        </tr>  
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="addresseeNameText" class="formLabel">Addressee Name Text</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="addresseeNameText" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="addresseeNameText"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>  
       <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="addressType" class="formLabel">Address Type</label></td>
            <td class="subHeaderTextItem">
                <select name="addressType" id="addressType" class="">
                    <option value="StandardPostalAddress">StandardPostalAddress</option>
                    <option value="NonStandardPostalAddress">NonStandardPostalAddress</option>
                </select>
            </td>
        </tr> 
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="streetNumber" class="formLabel">Street Number</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="streetNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="streetNumber"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>  
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="streetName" class="formLabel">Street Name</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="streetName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="streetName"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="streetType" class="formLabel">Street Type</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="streetType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="streetType"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="city" class="formLabel">City</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="city" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="city"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>   
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="state" class="formLabel">State</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="state" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="state"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="postCode" class="formLabel">Post Code</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="postCode" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="postCode"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="country" class="formLabel">Country</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="country" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="country"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>      
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="registrationIdentifierNumber" class="formLabel">Registration Identifier Number</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="registrationIdentifierNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationIdentifierNumber"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>   
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="registrationIdentifierNumberType" class="formLabel">Registration Identifier Number Type</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="registrationIdentifierNumberType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationIdentifierNumberType"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>  
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="hasLoansWithOtherBanks" class="formLabel">Has Loans With Other Banks</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="hasLoansWithOtherBanks" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="hasLoansWithOtherBanks"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
        <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="middleNames" class="formLabel">MiddleName</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="middleNames" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="middleNames"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="preferredName" class="formLabel">PreferredName</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="preferredName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="preferredName"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="alternateName" class="formLabel">AlternateName</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="alternateName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="alternateName"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="isPreferred" class="formLabel">Is Preferred</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="isPreferred" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="isPreferred"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="employmentType" class="formLabel">Employment Type</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="employmentType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="employmentType"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>  
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="occupationCode" class="formLabel">Occupation Code</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="occupationCode" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="occupationCode"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="registrationArrangementsRegistrationNumber" class="formLabel">Registration Arrangements RegistrationNumber</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="registrationArrangementsRegistrationNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationArrangementsRegistrationNumber"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="registrationArrangementsRegistrationNumberType" class="formLabel">Registration Arrangements RegistrationNumberType</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="registrationArrangementsRegistrationNumberType" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationArrangementsRegistrationNumberType"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>    
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="registrationArrangementsCountry" class="formLabel">Registration Arrangements Country</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="registrationArrangementsCountry" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationArrangementsCountry"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="registrationArrangementsState" class="formLabel">Registration Arrangements State</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="registrationArrangementsState" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="registrationArrangementsState"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
          <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="addressLine1" class="formLabel">AddressLine1</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="addressLine1" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="addressLine1"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="addressLine2" class="formLabel">AddressLine2</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="addressLine2" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="addressLine2"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
           <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="addressLine3" class="formLabel">AddressLine2</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="addressLine3" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="addressLine3"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="floorNumber" class="formLabel">Floor Number</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="floorNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="floorNumber"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr>    
       <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="unitNumber" class="formLabel">UnitNumber</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="unitNumber" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="unitNumber"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
      <tr>
           <td class="subHeaderTextItem verticalMiddle"><label for="buildingName" class="formLabel">BuildingName</label></td>
           <td class="subHeaderTextItem">
              <span class="inputWrapper iconWrapper">
                   <input  id="buildingName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="buildingName"
                   type="text" maxlength="11" value="" autocomplete="off">
                   </input>
              </span>
           </td>
       </tr> 
     
    </table>
    <input type="submit" class="primaryButton jq-formSubmit" value="Submit">
</form>

<div class="modalContent jq-cloak jq-confirmModal">

<div class="modalContentMod3">
<h1 class="formHeaderModal">
<span class="baseGama" id="title"></span> using below details ?
</h1>
<table class="" style="text-align:left;line-height:1.5">
<thead>
    <tr>
        <th id="tabHeadSilo">Silo</th>
        <td id="selectedSilo"></td>               
    </tr>
        <tr style="margin-top:10px;">
        <th id="tabHeadPrefix>Prefix</th>
        <td id="tabDataPrefix"></td>
        </tr>
       
        <tr style="margin-bottom:5px">
        <th id="tabHeadFirstName">First Name</th>
        <td id="tabDataFirstName"></td>
        </tr>
        <tr>
        <th id="tabHeadLastName">Last Name</th>
        <td id="tabDataLastName"></td>
        </tr>
        <tr>
        <th id="tabHeadAltName">Alternate Name</th>
        <td id="tabDataAltName"></td>
        </tr>
        <tr>
        <th id="tabHeadGender">Gender</th>
        <td id="tabDataGender"></td>
        </tr>                  
        <tr>
        <th id="tabHeadBirthDate">Birth Date</th>
        <td id="tabDataBirthDate"></td>
        </tr>
        <tr>
        <th id="tabHeadIsForeignRegistered">Is Foreign Registered</th>
        <td id="tabDataIsForeignRegistered"></td>
        </tr>
        <tr>
        <th id="tabHeadRoleType">Role Type</th>
        <td id="tabDataRoleType"></td>
        </tr>        
        <tr>
        <th id="tabHeadPurpOfBusRelation">Purpose Of Bussiness Relation</th>
        <td id="tabDataPurpOfBusRelation"></td>
        </tr>
        <tr>
        <th id="tabHeadSourceOfFund">Source Of Funds</th>
        <td id="tabDataSourceOfFund"></td>
        </tr> 
        <tr>
        <th id="tabHeadSourceOfWealth">Source Of Wealth</th>
        <td id="tabDataSourceOfWealth"></td>
        </tr>    
        <tr>
        <th id="tabHeadUsage">Usage</th>
        <td id="tabDataUsage"></td>
        </tr>    
        <tr>
        <th id="tabHeadAddressee">Address Name Text</th>
        <td id="tabDataAddressee"></td>
        </tr>    
        <tr>
        <th id="tabHeadAddressType">Address Type</th>
        <td id="tabDataAddressType"></td>
        </tr>    
        <tr>
        <th id="tabHeadStreetNumber">Street Number</th>
        <td id="tabDataStreetNumber"></td>
        </tr>    
        <tr>
        <th id="tabHeadStreetName">Street Name</th>
        <td id="tabDataStreetName"></td>
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
        <th id="tabHeadPostCode">Post Code</th>
        <td id="tabDataPostCode"></td>
        </tr>    
        <tr>
        <th id="tabHeadCountry">Country</th>
        <td id="tabDataCountry"></td>
        </tr>    
        <tr>
        <th id="tabHeadRegNumber">Registration Identifier Number</th>
        <td id="tabDataRegNumber"></td>
        </tr>    
        <tr>
        <th id="tabHeadRegType">Registration Identifier Number Type</th>
        <td id="tabDataRegType"></td>
        </tr> 
         <tr>
        <th id="tabHeadHasLoansWithOtherBanks">Has Loans With Other Banks</th>
        <td id="tabDataHasLoansWithOtherBanks"></td>
        </tr> 
         <tr>
        <th id="tabHeadMiddleNames">MiddleNames</th>
        <td id="tabDataMiddleNames"></td>
        </tr> 
         <tr>
        <th id="tabHeadPreferredName">Preferred Name</th>
        <td id="tabDataPreferredName"></td>
        </tr> 
         <tr>
        <th id="tabHeadAlternateName">Alternate Name</th>
        <td id="tabDataAlternateName"></td>
        </tr> 
         <tr>
        <th id="tabHeadIsPreferred">Is Preferred</th>
        <td id="tabDataIsPreferred"></td>
        </tr> 
         <tr>
        <th id="tabHeadEmploymentType">Employment Type</th>
        <td id="tabDataEmploymentType"></td>
        </tr> 
         <tr>
        <th id="tabHeadOccupationCode">OccupationCode</th>
        <td id="tabDataOccupationCode"></td>
        </tr> 
         <tr>
        <th id="tabHeadRegArrRegistrationNumber">Registration Arrangements Registration Number</th>
        <td id="tabDataRegArrRegistrationNumber"></td>
        </tr> 
         <tr>
        <th id="tabHeadRegArrRegistrationNumberType">Registration Arrangements Registration Number Type</th>
        <td id="tabDataRegArrRegistrationNumberType"></td>
        </tr> 
        <tr>
        <th id="tabHeadRegisArrCountry">Registration Arrangements Country</th>
        <td id="tabDataRegisArrCountry"></td>
        </tr>  
         <tr>
        <th id="tabHeadAddressLine1">AddressLine1</th>
        <td id="tabDataAddressLine1"></td>
        </tr>  
         <tr>
        <th id="tabHeadAddressLine2">AddressLine2</th>
        <td id="tabDataAddressLine2"></td>
        </tr>  
         <tr>
        <th id="tabHeadAddressLine3">AddressLine3</th>
        <td id="tabDataAddressLine3"></td>
        </tr>  
         <tr>
        <th id="tabHeadisFloorNumber">FloorNumber</th>
        <td id="tabDataisFloorNumber"></td>
        </tr>  
        <tr>
        <th id="tabHeadUnitNumber">UnitNumber</th>
        <td id="tabDataUnitNumber"></td>
        </tr>      
        <tr>
        <th id="tabHeadBuildingName">BuildingName</th>
        <td id="tabDataBuildingName"></td>
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


<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/downloadFailedApplication.js'/>"></script>
