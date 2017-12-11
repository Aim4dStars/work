<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<h1 class="pageHeaderItem">Document Library</h1>
<div class="jq-serviceHomeTabWrap">
<form id="accountSearch" method="GET" class="jq-AccountSearchForm" action="">
  <fieldset>
      <legend>Account search</legend>
        <div class="formBlock clearFix">
        <span class="inputStyleAlignOne">
            <input type="hidden" id="roleSearchCriteria" value=''/>
            <label for="roleSearch" class="formLabel">Role</label>
            <select id="roleSearch" disabled class="jq-RoleSearch" name="roleSearch">
               <option value="Client" selected="selected">Investors</option>
               <option value="Intermediaries">Intermediaries</option>
               </select>
        </span>
        <span class="inputStyleAlignOne">
            <input type="hidden" id="searchForCriteria" value='${searchFor}'/>
            <label for="searchFor" class="formLabel">Search for</label>
            <select id="searchFor" class="jq-SearchFor" name="searchFor">
               <option value="Account" selected="selected">Account</option>
               <option value="Person">Person</option>
            </select>
        </span>
        <span class="inputStyleAlignOne">
            <input type="hidden" id="searchByCriteria" value='${searchType}'/>
            <label for="searchBy" class="formLabel">Search by</label>
            <select id="searchBy" class="jq-SearchBy" name="searchBy">
            </select>
        </span>
        </div>
        <ul class="formBlockContainer formBlockContainerMod6">
        <li class="formBlock formBlockMod3">
          <span class="inputWrapper iconWrapper" >
            <input
              id="searchCriteria"
              data-validation="validate[required,custom[searchText],custom[minLength],custom[customFunction]]"
              data-validation-minLength-error = "Please enter at least 2 letters or numbers before searching"
              data-validation-customFunction-error = "Please enter valid input"
              data-min-length="2"
              class="formTextInput inputStyleEight jq-AccountSearchInput"
              name="searchCriteria"
              data-ng-key="searchCriteria"
              type="text"
              size="40"
              value="<c:out value='${searchCriteria}' />"
              placeholder="Enter Account Number."
              autocomplete="off">
          </span>
        </li>
        <li class="formBlock formBlockMod6">
          <input value="Search" type="submit" id="jq-AccountSearchButton" class="noDisplay" tabindex="-1"/>
          <input value="" name="searchToken" type="hidden" class="jq-SearchToken"/>
          <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">
                <span>Search</span>
                <em class="iconsearch"></em>
          </a>
        </li>
      </ul>
 </fieldset>
</form>
<c:choose>
<c:when test="${not empty searchType && searchType eq 'name' || (not empty searchFor && searchFor eq 'Person' && searchType eq 'gcmId')}">
<div id="jq-clientsSearch" data-type="clients" class="jq-clientTabContainer">
<c:choose>
<c:when test="${fn:length(serviceOpsModel.clients) > 0}">
    <div class="tableContainer jq-searchResultTableContainer">
    <table class="dataTable" data-table="searchResult">
        <caption class="caption">CLIENT RESULTS FOR <c:out value="${searchCriteria}" /></caption>
        <thead>
            <tr class="tableHeader tableHeaderFull tableHeaderMod2" >
                <td colspan="3" class="toolHeader">
                    <p class="textTransTable textTransTableMod2">Showing 1 - ${fn:length(serviceOpsModel.clients)} of ${fn:length(serviceOpsModel.clients)}</p>
                </td>
            </tr>
            <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                <th class="dataTableHeader dataTableHeaderFirst alphaAlign">Name <em class="iconchevron"><span class="screenReaderText">Sorted</span></em></th>
                <th class="dataTableHeader alphaAlign">Primary contact details</th>
                <th class="dataTableHeader alphaAlign">Location</th>
            </tr>
       </thead>
      <tbody class="clientSearchResult jq-clientSearchResult">
      <c:forEach items="${serviceOpsModel.clients}" var="clients">
        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg" data-id="${clients.avaloqUserId}" data-info='${clients.lastName}, ${clients.firstName}' data-type="client">
            <td class="dataTableCell nameWrap">
              <a href="#nogo" title="${clients.lastName}, ${clients.firstName}" class="baseLink" data-ng-key="accountName">
              <strong> ${clients.lastName}, ${clients.firstName}</strong>
                  <div>  ${clients.avaloqUserId}</div>
              </a>

            </td>
            <td class="dataTableCell cdWrap">
              <div class="pnWrap">${clients.phone}</div>
              <div class="emWrap">${clients.email}</div>
            </td>
            <td class="dataTableCell loWrap">${clients.city}<div>${clients.state}</div></td>
        </tr>
        </c:forEach>
      </tbody>

        <tfoot>
          <tr>
            <td colspan="3">
            </td>
        </tr>
      </tfoot>
    </table>
  </div>
</c:when>
<c:when test="${not empty searchCriteria && fn:length(serviceOpsModel.clients) == 0}">
    <span class="jq-NotFoundMsg"><p>Sorry, we can&rsquo;t find that user. Please try again.</p></span>
</c:when>
</c:choose>
</div>
</c:when>
<c:otherwise>
<c:if test="${not empty searchType && searchType ne 'name' }">
<div id="jq-clientAccountSearch" data-type="clients" class="jq-accountTabContainer">
<input value="" name="selection" type="hidden" class="jq-AccountNumber"/>
<c:choose>
<c:when test="${fn:length(accounts) > 0}">
    <div class="tableContainer jq-searchResultTableContainer">
    <table class="dataTable" data-table="searchResult">
        <caption class="caption">SEARCH RESULTS FOR <c:out value="${searchCriteria}" /></caption>
        <thead>
            <tr class="tableHeader tableHeaderFull tableHeaderMod2" >
                <td colspan="4" class="toolHeader">
                    <p class="textTransTable textTransTableMod2">Showing 1 - ${fn:length(accounts)} of ${fn:length(accounts)}</p>
                </td>
            </tr>
            <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                <th class="dataTableHeader dataTableHeaderFirst alphaAlign">Account Name <em class="iconchevron"><span class="screenReaderText">Sorted</span></em></th>
                <th class="dataTableHeader alphaAlign">Account Number</th>
                <th class="dataTableHeader alphaAlign">Account Type</th>
                <th class="dataTableHeader alphaAlign">Status</th>
            </tr>
       </thead>
      <tbody class="clientSearchResult jq-searchResult">
      <c:forEach items="${accounts}" var="account">
        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg" data-id="${account.accountNumber}" data-info='${account.accountName}' data-type="account">
            <td class="dataTableCell nameWrap" style="width:40%">
              <a href="#nogo" title="${account.accountName}" class="baseLink" data-ng-key="accountName">
              <strong> ${account.accountName}</strong></a><br/>${account.product}
            </td>
            <td class="dataTableCell cdWrap" style="width:20%">
              <div class="pnWrap">${account.bsb} ${account.accountNumber}</div>
            </td>
            <td class="dataTableCell cdWrap" style="width:30%">
              <div class="pnWrap">${account.accountType}</div>
            </td>
            <td class="dataTableCell cdWrap" style="width:10%">
              <div class="pnWrap">${account.accountStatus}</div>
            </td>
        </tr>
        </c:forEach>
      </tbody>

        <tfoot>
          <tr>
            <td colspan="4">
            </td>
        </tr>
      </tfoot>
    </table>
  </div>
</c:when>
<c:when test="${not empty searchCriteria && fn:length(accounts) == 0}">
    <span class="jq-NotFoundMsg"><p>Sorry, we can&rsquo;t find that user. Please try again.</p></span>
</c:when>
</c:choose>
</div>
</c:if>
</div>
</c:otherwise>
</c:choose>

<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/accountSearchView.js'/>"></script>
