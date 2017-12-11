<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h1 class="pageHeaderItem">Search for intermediaries, clients or accounts</h1>
<div class="jq-serviceHomeTabWrap">
<div class="toggleMenuLineWrap toggleMenuLineWrapMod7">
<ul class="toggleMenuLine toggleMenuLineMod3">
    <li class="toggleMenuLineItem">
        <a href="#jq-intermediariesSearch" class="textLink" data-ng-key="intermediariesTab">Intermediaries</a>
    </li>
    <li class="toggleMenuLineItem">
        <a href="#jq-clientsSearch" class="textLink" data-ng-key="clientsTab">Clients</a>
    </li>
    <li class="toggleMenuLineItem">
        <a href="#jq-accountsSearch" class="textLink" data-ng-key="accountsTab">Accounts</a>
    </li>
</ul>
</div>
<jsp:include page="search.jsp" />
<div id="jq-intermediariesSearch" data-type="intermediaries" class="jq-tabContainer">
<c:choose>
<c:when test="${fn:length(serviceOpsModel.intermediaries) > 0}">
    <div class="tableContainer jq-searchResultTableContainer">
    <table class="dataTable" data-table="searchResult">
        <caption class="caption">INTERMEDIARY RESULTS FOR <c:out value="${param.searchCriteria}" /></caption>
        <thead>
            <tr class="tableHeader tableHeaderFull tableHeaderMod2" >
                <td colspan="4" class="toolHeader">
                    <p class="textTransTable textTransTableMod2">Showing 1 - ${fn:length(serviceOpsModel.intermediaries)} of ${fn:length(serviceOpsModel.intermediaries)}</p>
                </td>
            </tr>
            <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                <th class="dataTableHeader dataTableHeaderFirst alphaAlign">Name <em class="iconchevron"><span class="screenReaderText">Sorted</span></em></th>
                <th class="dataTableHeader alphaAlign ">Dealer group/Company & Role</th>
                <th class="dataTableHeader alphaAlign">Primary contact details</th>
                <th class="dataTableHeader alphaAlign">Location</th>
            </tr>
       </thead>
      <tbody class="clientSearchResult jq-searchResult">
      <c:forEach items="${serviceOpsModel.intermediaries}" var="intermediaries">
        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg" data-id="${intermediaries.clientId}" data-type="intermediary">
            <td class="dataTableCell nameWrap">
             <a href="#nogo" title="${intermediaries.lastName}, ${intermediaries.firstName}" class="baseLink" data-ng-key="accountName">
                <strong> ${intermediaries.lastName}, ${intermediaries.firstName},</strong>
                 <div>
                     ${intermediaries.avaloqUserId}
                  </div>
             </a>
            </td>
            <td class="dataTableCell dgWrap">
              ${intermediaries.dealerGroup}
                <c:if test="${not empty intermediaries.companyName }">
                    <div>${intermediaries.companyName}</div>
                </c:if>
                <div>${intermediaries.role}</div>
            </td>
            <td class="dataTableCell cdWrap">
              <div class="pnWrap">${intermediaries.phone}</div>
              <div class="emWrap">${intermediaries.email}</div>
            </td>
            <td class="dataTableCell loWrap">${intermediaries.city}<div>${intermediaries.state}</div></td>
        </tr>
        </c:forEach>
      </tbody>
      <tfoot>
          <tr>
            <td colspan="4"></td>
        </tr>
      </tfoot>
    </table>
  </div>
</c:when>
<c:when test="${not empty param.searchCriteria && fn:length(serviceOpsModel.intermediaries) == 0}">
    <p>Sorry, we can&rsquo;t find that user. Please try again.</p>
    <p aria-live="assertive" aria-atomic="true" class="ui-helper-hidden-accessible jq-searchResultMessage"></p>
</c:when>
</c:choose>
</div>
<div id="jq-clientsSearch" data-type="clients" class="jq-tabContainer">
<c:choose>
<c:when test="${fn:length(serviceOpsModel.clients) > 0}">
    <div class="tableContainer jq-searchResultTableContainer">
    <table class="dataTable" data-table="searchResult">
        <caption class="caption">CLIENT RESULTS FOR <c:out value="${param.searchCriteria}" /></caption>
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
      <tbody class="clientSearchResult jq-searchResult">
      <c:forEach items="${serviceOpsModel.clients}" var="clients">
        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg" data-id="${clients.clientId}" data-type="client">
            <td class="dataTableCell nameWrap">
              <a href="#nogo" title="${clients.lastName}, ${clients.firstName}" class="baseLink" data-ng-key="accountName">
              <strong> ${clients.lastName}, ${clients.firstName},</strong>
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
<c:when test="${not empty param.searchCriteria && fn:length(serviceOpsModel.clients) == 0}">
    <p>Sorry, we can&rsquo;t find that user. Please try again.</p>
</c:when>
</c:choose>
</div>
<div id="jq-accountsSearch" data-type="accounts" class="jq-tabContainer">
<c:choose>
<c:when test="${fn:length(serviceOpsModel.accounts) > 0}">
    <div class="tableContainer jq-searchResultTableContainer">
    <table class="dataTable" data-table="searchResult">
        <caption class="caption">ACCOUNT RESULTS FOR <c:out value="${param.searchCriteria}" /></caption>
        <thead>
            <tr class="tableHeader tableHeaderFull tableHeaderMod2" >
                <td colspan="3" class="toolHeader">
                    <p class="textTransTable textTransTableMod2">Showing 1 - ${fn:length(serviceOpsModel.accounts)} of ${fn:length(serviceOpsModel.accounts)}</p>
                </td>
            </tr>
            <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                <th class="dataTableHeader dataTableHeaderFirst alphaAlign">Name <em class="iconchevron"><span class="screenReaderText">Sorted</span></em></th>
                <th class="dataTableHeader alphaAlign">Account owner(s)</th>
                <th class="dataTableHeader alphaAlign">Adviser</th>
            </tr>
       </thead>
      <tbody class="accountsSearchResult jq-searchResult">
      <c:forEach items="${serviceOpsModel.accounts}" var="accounts">
        <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg" data-id="${accounts.accountId}" data-type="account">
            <td class="dataTableCell nameWrap">
                <a href="#nogo" title="${accounts.accountName}" class="baseLink" data-ng-key="accountName">
                    <strong>${accounts.accountName}</strong>
                    <div>${accounts.accountNumber}</div>
                    <div>${accounts.mNumber}</div>
                    <div>${accounts.accountType} . ${accounts.product}</div>
                </a>
            </td>
            <td class="dataTableCell loWrap">
            <c:forEach items="${accounts.owners}" var="owner">
                <div>${owner}</div>
            </c:forEach>
            </td>
            <td class="dataTableCell loWrap"><div>${accounts.adviserName}</div></td>
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
<c:when test="${not empty param.searchCriteria && fn:length(serviceOpsModel.accounts) == 0}">
    <p>Sorry, we can&rsquo;t find that account. Please try again.</p>
</c:when>
</c:choose>
</div>
</div>

<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/serviceOperator.js'/>"></script>
