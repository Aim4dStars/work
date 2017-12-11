<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h1 class="pageHeaderItem">Approved Applications</h1>

<c:choose>
  <c:when test="${not empty approvedApplicationList}">
<table class="dataTable">
  <thead>
    <tr class="tableHeader tableHeaderFull tableHeaderMod2">
      <th class="dataTableHeader alphaAlign ">Account Type</td>
      <th class="dataTableHeader alphaAlign ">Account Name</td>
      <th class="dataTableHeader alphaAlign "></td>
    </tr>
  </thead>
  <tbody class="clientSearchResult jq-searchResult">
    <c:forEach items="${approvedApplicationList}" var="approvedApplication" varStatus="itemCount">
      <tr class="dataTableRow dataTableRowActiveMod2 dataTableRowBg">
        <td class="dataTableCell nameWrap"><c:out value='${approvedApplication.investorAccountType}' /></td>
        <td class="dataTableCell nameWrap"><c:out value='${approvedApplication.accountName}' /></td>
        <td class="dataTableCell nameWrap">
          <a href="/ng/secure/page/serviceOps/applicationDetails/customer/${cisKey}" >
            <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">Link to details</span>
          </a>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>
  </c:when>
  <c:otherwise>
    <div class="noSearchResult">No result was found. Please go back to previous page and try a different search.</div>
  </c:otherwise>
</c:choose>


<br><br><br>
<a href="javascript:history.go(-1)" class="baseLink baseLinkMod1" title="Back to previous page">
    <em class="iconlink"></em> <span class="iconLinkLabel iconLinkLabelMod1">Back to previous page</span>
</a>

