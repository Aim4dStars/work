<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form id="intermediariesSearch" method="GET" class="jq-intermediariesAndClientsSearchForm" action="">
  <fieldset>
    <legend>Intermediaries and clients search</legend>
      <ul class="formBlockContainer formBlockContainerMod6">
        <li class="formBlock formBlockMod3">
          <label for="intermediaryName" class="formLabel visuallyHidden">Enter intermediary or client name</label>
          <span class="inputWrapper iconWrapper" >
            <input
              id="intermediaryName"
              data-validation="validate[required,custom[searchText],custom[minLength],custom[customFunction]]"
              data-min-length="2"
              data-validation-required-error="${errors.err00094}"
              data-validation-minLength-error="${errors.err00094}"
              data-validation-searchText-error="${errors.err00084}"
              class="formTextInput inputStyleEight jq-intermediariesAndClientsSearchInput"
              name="searchCriteria"
              data-ng-key="searchCriteria"
              type="text"
              value="<c:out value='${param.searchCriteria}' />"
              autocomplete="off">
          </span>
        </li>
        <li class="formBlock formBlockMod6">
          <input value="Search" type="submit" id="jq-IntermediariesAndClientsSearchButton" class="noDisplay" tabindex="-1"/>
          <input value="" name="selection" type="hidden" class="jq-IntermediariesAndClientsSearchSelection"/>
          <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">
                <span>Search</span>
                <em class="iconsearch"></em>
          </a>
        </li>
      </ul>
 </fieldset>
</form>

 <script id="jq-autoCompleteTemplate" type="text/x-handlebars-template">
    <a>
    <dl class="accontSelectContainer accontSelectContainerMod1">
        <dt class="accountDef accountDefMod1 accountDefTitleItem">

            <div class="floatLeft">
                <p data-ng-key="name">{{lastName}}, {{firstName}}</p>
                <p class="normalWeight accountDefText" data-ng-key="userID">{{id}}</p>
            </div>
        </dt>
        <dd class="floatRight accountDefTextItem">
            <span data-ng-key="location">{{city}}<br/>{{state}}</span>
        </dd>
    </dl>
    </a>
</script>