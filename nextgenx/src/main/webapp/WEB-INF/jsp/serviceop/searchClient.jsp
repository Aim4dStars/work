<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form id="intermediariesSearch" method="GET" class="jq-intermediariesAndClientsSearchForm" action="">
    <legend>Clients search via CIS Key</legend>
     <table >
         <tr id="cisKeyP">
            <td class="subHeaderTextItem verticalMiddle"><label for="cisKey" class="formLabel">Enter CIS key</label></td>
            <td class="subHeaderTextItem">
        <span class="inputWrapper iconWrapper" >
            <input id="intermediaryName" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="cisKey"
            data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]"
            data-min-length="11"
            data-max-length="11"
            data-validation-required-error="Please enter CIS key"
            data-validation-minLength-error="Please enter at least 11 digit CIS Key"
            data-validation-searchNumber-error="Please enter numbers"
            data-ng-key="searchCriteria"
            type="text" maxlength="11" value="<c:out value='${param.searchCriteria}' />" autocomplete="off">
        </span>
           </td>
        </tr>
        <tr>
            <td class="subHeaderTextItem verticalMiddle"><label for="silo" class="formLabel">Silo</label></td>
            <td class="subHeaderTextItem">
        <select name="silo" id="selectSilo" class="jq-serviceOperatorSelectAction">
            <option value="WPAC">WPAC</option>
            <option value="BTPL">BTPL</option>
        </select>
           </td>
        </tr>
    

       <tr>
            <td class="subHeaderTextItem verticalMiddle"> <label for="roleType" class="formLabel">Role</label></td>
        <td class="subHeaderTextItem">
        <select name="roleType" id="selectRole" class="jq-serviceOperatorSelectAction">
            <c:forEach items="${roleTypes}" var="roleType">
                <option value="${roleType}">${roleType}</option>
            </c:forEach>
        </select>
           </td>
        </tr>
</table>
    <p class="formBlock clearFix">
        <label for="intermediaryName" class="formLabel">Filters for Request</label>
        <span class="noDisplay formFieldMessageError jq-termCondCheckboxErrorFix">Please select at least one Operation Type</span>
         <table>
        <tr>
                <td><input type="checkbox" id="selectAll" value="selectAll" style="display:block" class="jq-formTcCheckBox" /><label class="formLabel" for="preferredName" rel="external">SelectAll</label></td>
            </tr>  
            <tr>
                <td><input type="checkbox" name="operationTypes" value="ID" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Demographics</label></td>
                <td><input type="checkbox" name="operationTypes" value="IP" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Postal Addresses</label></td>
                <td><input type="checkbox" name="operationTypes" value="IPF" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Postal Address Active Flag</label></td>
            </tr> 
            <tr>
                <td><input type="checkbox" name="operationTypes" value="IC" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Characteristics</label></td>
                <td><input type="checkbox" name="operationTypes" value="ICA" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Communication Addresses</label></td>
                <td><input type="checkbox" name="operationTypes" value="ICAF" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Communication Address Active Flag</label></td>
            </tr>
            <tr>
                <td><input type="checkbox" name="operationTypes" value="IPOS" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Position</label></td>
                <td><input type="checkbox" name="operationTypes" value="IA" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Arrangements</label></td>
                <td><input type="checkbox" name="operationTypes" value="AAF" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Arrangements Active Flag</label></td>
            </tr>
            <tr>

                <td><input type="checkbox" name="operationTypes" value="ICR" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Cross References</label></td>
                <td><input type="checkbox" name="operationTypes" value="IAN" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Alternate Names</label></td>
                <td><input type="checkbox" name="operationTypes" value="ANAF" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">Alternate Names Active Flag</label></td>
            </tr>
            <tr>
                <td><input type="checkbox" name="operationTypes" value="AMLC" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">AML Characteristic</label></td>
                <td><input type="checkbox" name="operationTypes" value="ACAF" style="display:block" class="jq-formTcCheckBox checkbox" /><label class="formLabelCheckBox" for="preferredName" rel="external">AML Characteristic Active Flag</label></td>
            </tr>
        </table> 
    </p>

    <p class="formBlock clearFix">
        <input value="Search" type="submit" id="jq-IntermediariesAndClientsSearchButton" class="noDisplay" tabindex="-1"/>
        <input value="" name="selection" type="hidden" class="jq-IntermediariesAndClientsSearchSelection"/>
        <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">Search<em class="iconsearch"></em></a>
    </p>

</form>
