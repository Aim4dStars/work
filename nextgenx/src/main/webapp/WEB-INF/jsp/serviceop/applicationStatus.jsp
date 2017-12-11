<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form name="serviceOps_form" id="serviceOps" method="GET" action="/ng/secure/page/serviceOps/OnBoardingStatus" class="jq-clientDetailActionForm">
<input id="token" type="hidden" value="<c:out value='${cssftoken}'/>" name="cssftoken" />
    <table>
        <tr id="cisKeyP">
            <td class="subHeaderTextItem verticalMiddle"><label for="cisKey" class="formLabel">Application ID</label></td>
            <td class="subHeaderTextItem">
                <span class="inputWrapper iconWrapper">
                    <input  id="cisKey" class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput" name="cisKey"
                           data-validation="validate[required,custom[searchNumber],custom[minLength],custom[customFunction]]" 
			data-min-length="1" 
			data-max-length="20"
			data-validation-required-error="Please enter Application Id"
			data-validation-minLength-error="Please enter at least 1 digit Appid"
			data-validation-searchNumber-error="Please enter numbers"
			class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
			data-ng-key="searchCriteria" 
			type="text" 
			maxlength="20"
			value="" 
			autocomplete="off"></input>
		</span>
                </span>
            </td>
        </tr>
    </table>
    <input type="submit" class="primaryButton jq-formSubmit" value="Submit">
</form>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/downloadFailedApplication.js'/>"></script>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOperatorClientSearch.js'/>"></script>