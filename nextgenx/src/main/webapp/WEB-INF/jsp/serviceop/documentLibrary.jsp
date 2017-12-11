<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
 <div class="setBottomGutter">
    <a href="<c:url value='/secure/page/serviceOps/accountSearch'/>" class="baseLink baseLinkClear">
        <em class="mod180 iconlink"></em> <span class="iconLinkLabel"> Back to search</span> </a>
    </div>
<h2 class="pageHeaderItem" style=" display: inline;">Documents for ${filters.relationshipType == 'ACCT' ? ' Account ':' Person ' } </h2>
<h6 class="" style="font-family: Helvetica, Arial, sans-serif;">
    <span class="baseGama"><c:out value='${filters.name}'/></span> <em class="icon-divider"></em> <c:out value='${accountKey}'/>
</h6>
<div class="noticeBox noDisplay jq-messageBox">
    <ul class="noticeBoxWrapper">
        <li>
            <span class="messageIcon"><em class="iconItem"></em></span>
        </li>
        <li class="noticeBoxText noticeBoxTextSmallBox">
            <p class="emphasis jq-message"></p>
        </li>
    </ul>
</div>
<form id="documentSearch" method="GET" class="jq-documentSearchForm" action="" data-ajax-submit-url="">
   <input type="hidden" name="documentTypeFilter" id="documentTypeFilter" class="jq-documentTypeFilter"  value='${filters.documentType}'/>
   <input type="hidden" name="documentSubTypeFilter" id="documentSubTypeFilter" class="jq-documentSubTypeFilter"  value='${filters.documentSubType}'/>
   <input type="hidden" name="financialYearFilter" id="financialYearFilter" class="jq-financialYearFilter"  value='${filters.financialYear}' />
   <input type="hidden" name="uploadByFilter" id="uploadByFilter" class="jq-uploadByFilter"  value='${filters.uploadedBy}'/>
   <input type="hidden" name="relationshipTypeFilter" id="relationshipTypeFilter" class="jq-relationshipTypeFilter"  value='${filters.relationshipType}'/>
   <input type="hidden" name="dateFromFilter" id="dateFromFilter" class="jq-dateFromFilter"  value='${filters.fromDate}' />
   <input type="hidden" name="dateToFilter" id="dateToFilter" class="jq-dateToFilter"  value='${filters.toDate}'/>
   <input type="hidden" name="documentStatusFilter" id="documentStatusFilter" class="jq-documentStatusFilter"  value='${filters.documentStatus}'/>
   <input type="hidden" name="isAuditFilter" id="isAuditFilter" class="jq-isAuditFilter"  value='${filters.auditFlag}'/>
   <input type="hidden" name="nameFilter" id="nameFilter" class="jq-nameFilter"  value='${filters.name}'/>
   <input type="hidden" name="nameSearchFilter" id="nameSearchFilter" class="jq-nameSearchFilter"  value='${filters.nameSearchToken}'/>
   <input type="hidden" name="softDeletedFilter" id="softDeletedFilter" class="jq-softDeletedFilter"  value='${filters.softDeleted}'/>
   <input type="hidden" name="documentSubSubTypeFilter" id="documentSubSubTypeFilter" class="jq-documentSubSubTypeFilter"  value='${filters.documentSubSubType}'/>
    <fieldset>
    <ul class="formBlockContainer formBlockContainerMod6">
        <li class="formBlock formBlockMod1">
        <span class="inputStyleAlignOne">
            <input id="documentNameFilter"
              class="formTextInput inputStyleEight jq-documentNameFilter"
              name="documentNameFilter"
              type="text"
              size="40"
              placeholder="Search document name"
              autocomplete="off"
              maxlength="200">
        </span>
       </li>
        <li class="formBlock formBlockMod1">
        <span class="inputStyleAlignOne jsDocumentType">
            <label for="documentType" class="formLabel">Document type</label>
            <select id="documentTypeInput" class="jq-documentTypeInput" name="documentTypeInput">
                <option value="Any" selected="selected">Any</option>
             </select>
         </span>
        <span class="inputStyleAlignOne jsSubDocumentType">
            <div id="documentSubCategoryContainer" class="noDisplay" >
            <label for="documentSubCategory" class="formLabel">Document sub type</label>
            <select id="documentSubCategory" class="jq-documentSubCategory" name="documentSubCategory">
           </select>
           </div>
            </span>
        <span class="inputStyleAlignOne ">
            <div id="documentSubSubCategoryContainer" class="noDisplay" >
            <label for="documentSubSubCategory" class="formLabel">Document sub type(2)</label>
            <select id="documentSubSubCategory" class="jq-documentSubSubCategory" name="documentSubSubCategory">
            </select>
            </div>
            </span>
        </li>
        <li class="formBlock formBlockMod1">
        <span class="inputStyleAlignOne">
            <label for="searchFinancialYear" class="formLabel">Financial year</label>
            <select id="searchFinancialYear" class="jq-searchFinancialYear" name="searchFinancialYear">
               <option value="Any" selected="selected">Any</option>
           </select>
        </span>
        <span class="inputStyleAlignOne">
        <label for="uploadedBy" class="formLabel">Uploaded by</label>
            <select id="uploadedBy" class="jq-uploadedBy" name="uploadedBy">
               <option value="Any" selected="selected">Any</option>
               <option value="Accountant">Accountant</option>
               <option value="Adviser">Adviser</option>
               <option value="Investor">Investor</option>
               <option value="Panorama">Panorama</option>
            </select>
        </span>
        <span class="inputStyleAlignOne" style="margin: 0 auto; margin-top: 5px;">
        <table>
            <tr>
                <td>
                <label for="fromDate" class="formLabel">From</label>
                  <span class="calendarPlaceHolder jq-SearchFromDateCalendarPlaceHolder"></span>
                      <span class="iconWrapper">
                       <input id="fromDate" name="fromDate" data-calendar="jq-SearchFromDateCalendarPlaceHolder"
                                class="formTextInput jq-SearchFromDate inputStyleThree" readonly value="" data-placeholder=""/>
                      <a class="iconActionButton jq-failedappDownloadFromDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                            <em class="iconcalendar"><span>Select Date</span></em>
                            <em class="iconarrowfulldown"></em>
                        </a>
                     </span>
                </td>
                <td>
                 <label for="toDate" class="formLabel">To</label>
                  <span class="calendarPlaceHolder jq-SearchToDateCalendarPlaceHolder"></span>
                      <span class="iconWrapper">
                      <input id="toDate" name="toDate" data-calendar=".jq-SearchToDateCalendarPlaceHolder"
                               class="formTextInput jq-SearchToDate inputStyleThree" readonly value="" data-placeholder=""/>
                      <a class="jq-failedappDownloadToDateCalendarIcon jq-appendErrorAfter calendarIconLink" title="Date Picker" href="#nogo">
                            <em class="iconcalendar"><span>Select date</span></em>
                            <em class="iconarrowfulldown"></em>
                        </a>
                     </span>
                </td>
            </tr>
         </table>
        <span>
        </li>
        <li class="formBlock formBlockMod1">
        <span class="inputStyleAlignOne" style="width: 220px; margin: 0 auto; margin-bottom: 10px;">
            <input type="checkbox" class="jq-auditFilterFlag auditFlag" data-ng-key="auditFilterFlag" name="auditFilterFlag" style="display:block;" />
            <label for="auditFilterFlag" class="formLabel"><em class="icon-notification"></em> Flagged for audit</label>
        </span>
        <span class="inputStyleAlignOne" style="width: 220px; margin: 0 auto; margin-bottom: 10px;">
            <input type="checkbox" class="jq-privacyFlag privacyFlag" data-ng-key="privacyFlag" name="privacyFlag" style="display:block;" />
            <label for="privacyFlag" class="formLabel"> Private document</label>
        </span>
        <span class="inputStyleAlignOne" style="width: 220px; margin: 0 auto; margin-bottom: 10px;">
            <input type="checkbox" class="jq-softDeletedFlag softDeletedFlag" data-ng-key="softDeletedFlag" name="softDeletedFlag" style="display:block;" />
            <label for="softDeletedFlag" class="formLabel"> Marked as soft deleted</label>
        </span>
        </li>
        <table>
            <tr><td>
            <input id="accountNumber" class="formTextInput inputStyleEight jq-accountNumber noDisplay"
                name="accountNumber" disabled data-ng-key="accountNumber" type="text"
                value="<c:out value='${filters.accountNumber}' />"
                autocomplete="off"
                size="40">
            <input value="Search" type="submit" id="jq-IntermediariesAndClientsSearchButton" class="noDisplay" tabindex="-1"/>
                 <a href="#nogo" class="actionButtonIcon jq-formSubmit" title="Search">
                 <span>Search</span>
                 <em class="iconsearch"></em>
                 </a>
            <input value="Clear" type="" id="" class="noDisplay" tabindex="-1"/>
                <a href="#nogo" class="actionButtonIcon jq-formClear" title="Clear">
                <span>Reset to default</span>
                </a>
            </td><td style="text-align: right;">
                <input id="fileInput" type="file" name="uploadFile" style="display:none;">
                <span class="inputWrapper iconWrapper  noDisplay" >
                <input id="selectedDocument"
                        class="formTextInput inputStyleEight jq-selectedDocument"
                        name="selectedDocument"
                        data-ng-key="selectedDocument"
                        type="text">
                </span>
                <a href="#nogo" class="actionButtonIcon jq-documentUploadAction" title="Upload">
                    <span>Upload</span>
                    <em class="icon-document-upload" title="Upload document"></em>
                </a>
            </td>
            </tr>
        </table>
    </ul>
</fieldset>
</form>
<div id="jq-documentsSearch" data-type="documents" class="jq-tabContainer">
<input type="hidden" id="accountId" value='${accountKey}'/>
<input type="hidden" id="selectedDocumentId" value=''/>
<span id="jq-ErrorMsg" style="color:red"></span>
<c:choose>
<c:when test="${fn:length(documents) > 0}">
    <div class="tableContainer jq-searchResultTableContainer">
    <table id="documentResultTable" class="dataTable jq-documentResultTable" data-table="searchResult" >
        <caption class="caption">DOCUMENT RESULTS FOR <c:out value="${accountKey}" /></caption>
        <thead>
            <tr class="tableHeader tableHeaderFull tableHeaderMod2" >
                <td colspan="4" class="toolHeader">
                    <p class="textTransTable textTransTableMod2">Showing 1 - ${fn:length(documents)} of ${fn:length(documents)}</p>
                </td>
            </tr>
            <tr class="tableHeader tableHeaderFull tableHeaderMod6">
                <th class="dataTableHeader dataTableHeaderFirst alphaAlign">File Name </th>
                <th class="dataTableHeader alphaAlign">Uploaded On</th>
                <th class="dataTableHeader alphaAlign">Uploaded By</th>
                <th class="dataTableHeader alphaAlign">Action</th>
            </tr>
       </thead>
      <tbody class="clientSearchResult jq-searchResult">
      <c:forEach items="${documents}" var="document">
        <tr  class="dataTableRow dataTableRowActiveMod2 dataTableRowBg" data-id="${document.key.documentId}" data-type="document">
            <td class="dataTableCell nameWrap" style="width:50%">
                <a href="#?docId=${document.key.documentId}" class="jq-documentVersions" title="Document versions">
                    <img id="documentVersionsImg" src="/ng/public/static/images/icon_expand_plus.gif" width="15" height="15">
                </a>
                <c:choose>
                  <c:when test="${document.softDeleted}">
                    <img id="softDeletedDocImg" src="/ng/public/static/images/soft_deleted_document.png" title="Soft deleted document" width="15" height="18">
                   </c:when>
                  <c:otherwise>
                    <img id="DocumentImg" src="/ng/public/static/images/doc_document.png" width="15" height="18">
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${document.audit}">
                    <em class="icon-notification red" title="Flagged for audit"></em>
                  </c:when>
                  <c:otherwise>
                    <em class="icon-notification" title="No flagged for audit"></em>
                  </c:otherwise>
                </c:choose>
              <strong> ${document.documentName}</strong>
              ${document.status eq 'Draft' ? 'Private' : ''} </br>
              ${document.documentTypeLabel}${not empty document.documentSubType ? ' > ':'' }${not empty document.documentSubType ? document.documentSubType :'' } ${not empty document.documentSubType2 ? ' > ':'' }${not empty document.documentSubType2Label ? document.documentSubType2Label :'' }- ${document.size/1024}kb</br>
              ${document.financialYear}
            </td>
            <td class="dataTableCell cdWrap" style="width:10%">
                <joda:format var="formattedDate" value="${document.uploadedDate}" style="M-" />
              <div class="pnWrap">${formattedDate}</div>
            </td>
            <td class="dataTableCell cdWrap" style="width:25%">
              <div class="pnWrap">${fn:trim(document.addedByName)} ${not empty fn:trim(document.addedByName) ? '</br>':'' }
              ${not empty document.uploadedBy ? document.uploadedBy:''} ${ not empty fn:trim(document.uploadedBy) ? '-' : ''} ${document.uploadedRole}
              </div>
            </td>
            <td class="dataTableCell cdWrap" style="width:15%">
               <a href="#nogo" class="jq-editDocument" data-id="${document.key.documentId}">
               <span class="icon-wrapper">
                    <em class="icon-edit" title="Edit document details"></em>
               </span>
               </a>
               <a href="${pageContext.request.contextPath}/secure/page/serviceOps/documents/${document.key.documentId}">
                <span class="icon-wrapper">
                    <em class="icon-document-download" title="Download document"></em>
                <span>
               </a>
               <c:if test="${not document.softDeleted}">
                   <a href="#nogo" class="jq-newDocVersionUpload" data-id="${document.key.documentId}" data-info="${document.documentType}-${document.documentSubType}-${document.documentTitleCode}">
                    <em class="icon-document-upload" title="Upload document"></em>
                   </a>
                </c:if>
               <c:if test="${document.documentType ne 'SCANNED' && document.documentType ne 'POBOX' && document.documentType ne 'EMAIL' && document.documentType ne 'FAX' }">
               <a href="#nogo" class="jq-deleteDocument" data-id="${document.key.documentId}">
                <em class="icon-trash" title="Delete document"></em>
               </a>
               </c:if>
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
<c:when test="${fn:length(documents) == 0}">
    <p>Sorry, we can&rsquo;t find any document for selected ${filters.relationshipType == 'ACCT' ? ' account ':' person ' }. Please try again.</p>
</c:when>
</c:choose>
</div>
<div id="disableLayer" class="disableLayer"></div>
<div class="jq-spinnerDiv disableLayer">
    <span><em class="iconLoader"></em></span>
</div>

<div class="jq-confirmationPopup" id="confirmationPopup" style="display:none;">
    <input type="hidden" id="selectedDocumentId" value="" name="selectedDocumentId"/>
    <div style="height: 10px;clear:both;"></div>
    <div class="modalContentMod3">
    <div style="height: 10px;clear:both;"></div>
        <h1 class="formHeaderModal">
            <span class="baseGama"> Confirm</span> delete document</span>
        </h1>
        <div style="padding: 20px;">
            <h2 class="mainHeaderItemMod9">Are you sure you want to delete this document?</h2>
        </div>
        <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
            <li>
                <a href="#nogo" class="primaryButton jq-confirmDeletion" title="Confirm">Confirm</a>
            </li>
            <li>
                <a href="#nogo" class="baseLink baseLinkClear jq-cancelDeleteDocument" title="Cancel">
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
              <li class="noticeBoxText emphasis"><p>Document once deleted can not be retrieved.</p></li>
          </ul>
        </div>
    </div>
</div>
<jsp:include page="uploadModal.jsp" />
<jsp:include page="editDocumentModal.jsp" />
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/documentLibrary.js'/>"></script>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/referenceData.js'/>"></script>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/docLibUtils.js'/>"></script>
