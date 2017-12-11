<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class=" jq-uploadDocumentPopupBlock" id="uploadDocumentPopupBlock" style="display:none;">
    <div class="modalContentMod3">
    <h2 class="formHeaderModal">
        <span class="baseGama"> Upload</span> document</span>
    </h2>
    <div style="margin:0 auto;">
        <div class="noticeBox noDisplay warningBox jq-uploadMessageBox">
            <ul class="noticeBoxWrapper">
                <li>
                    <span class="messageIcon"><em class="iconItem"></em></span>
                </li>
                <li class="noticeBoxText noticeBoxTextSmallBox">
                    <p class="emphasis jq-uploadMessage"></p>
                </li>
            </ul>
        </div>
    <form id="uploadForm" name="uploadForm" enctype="multipart/form-data" method="post">
    <ul class="formBlockContainer formBlockContainerMod6">
        <li class="formBlock formBlockMod1">
            <div id="selectedDocumentInfo" class="doc-wrapper" >
                <div class="margin-right-half" style="float: left;">
                    <span class="icons-secondary-sizing">
                        <span class="icon icon-document" title="document" />
                    </span>
                </div>
                <div id="docInfo">
                    <span class="heading-seven documentbar-info text-overflow jq-SelectedDocumentName" />
                </div>
                <span class="jq-SelectedDocumentSize deemphasis" >0Kb</span>
            </div>
        </li>
        <li class="formBlock formBlockMod1">
            <span class="inputStyleAlignOne jsUploadDocumentType">
            <label for="uploadDocumentType" class="formLabel">Document type</label>
                <select id="uploadDocumentType" class="jq-uploadDocumentType" name="uploadDocumentType">
                    <option value="Any" selected="selected">Any</option>
                 </select>
            </span>
            <span class="inputStyleAlignOne jsDocumentSubType">
                <div id="documentSubTypeContainer" class="noDisplay" >
                <label for="documentSubType" class="formLabel">Document sub type <span style="font-weight : normal;">(Optional)</span></label>
                <select id="documentSubType" class="jq-documentSubType" name="documentSubType">
               </select>
               </div>
            </span>
        </li>
        <li class="formBlock formBlockMod1">
            <span class="inputStyleAlignOne">
                <label for="uploadFinancialYear" class="formLabel">Financial year <span style="font-weight : normal;">(Optional)</span></label>
                <select id="uploadFinancialYear" class="jq-uploadFinancialYear" name="uploadFinancialYear">
                           <option value="Any" selected="selected">Any</option>
               </select>
           </span>
            <span class="inputStyleAlignOne">
                <div id="docTitleCodesContainer" class="noDisplay" >
                <label for="documentTitleCodes" class="formLabel">Document title code</label>
                <select id="documentTitleCodes" class="jq-documentTitleCodes" name="documentTitleCodes">
               </select>
               </div>
            </span>
             <span class="inputStyleAlignOne">
                            <div id="docTitleCodesContainerForFA" class="noDisplay" >
                            <label for="documentTitleCodesForFA" class="formLabel">Document title code</label>
                            <select id="documentTitleCodesForFA" class="jq-documentTitleCodesForFA" name="documentTitleCodesForFA">
                           </select>
                           </div>
                        </span>
        </li>
        <li class="formBlock formBlockMod1">
           <span class="inputStyleAlignOne">
            <div id="abcOrderIdContainer" class="noDisplay">
               <label for="updateAbcOrderId" class="formLabel">ABS Order ID <span style="font-weight : normal;">(Optional)</label>
               <input
                 id="updateAbcOrderId"
                 name="updateAbcOrderId"
                 class="formTextInput inputStyleEight jq-updateAbcOrderIdInput"
                 data-ng-key="updateAbcOrderId"
                 type="text"
                 size="21">
             </div>
             </span>
           <span class="inputStyleAlignOne">
           <div id="externalIdContainer" class="noDisplay">
             <label for="updateExternalId" class="formLabel">External ID</label>
             <input
               id="updateExternalId"
               name="updateExternalId"
               class="formTextInput inputStyleEight jq-uploadExternalIdInput"
               data-ng-key="updateExternalId"
               type="text"
               size="21">
           </div>
           </span>
           <span class="inputStyleAlignOne">
           <div id="documentSubSubTypeContainer" class="noDisplay" >
            <label for="documentSubSubType" class="formLabel">Document Sub Type(2)</label>
             <select id="documentSubSubType" class="jq-documentSubSubType" name="documentSubSubType">
             </select>
           </div>
           </span>
        </li>
        <li class="formBlock formBlockMod1">
            <span class="inputStyleAlignOne" style="width: 223px; margin: 0 auto; margin-top: 5px;">
                <input type="checkbox" class="jq-auditFlagInput auditFlag" data-ng-key="auditFlagInput" name="auditFlagInput" style="display:block;" />
                <label for="auditFlagInput" class="formLabel"><em class="icon-notification"></em> Flagged for audit</label>
            </span>
            <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-top: 5px; margin-bottom: 10px;">
                <input type="checkbox" class="jq-uploadPrivacyFlag uploadPrivacyFlag" data-ng-key="uploadPrivacyFlag" name="uploadPrivacyFlag" style="display:block;" />
                <label for="uploadPrivacyFlag" class="formLabel"> Private document</label>
            </span>
        </li>
        </ul>
    </fieldset>
    </form>
    </div>
    <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
    <li>
    <a href="#nogo" class="primaryButton jq-documentUploadBtn" title="Upload" >
        <span>Upload</span>
        <em class="icon-document-upload"></em>
    </a>
    </li>
    <li>
    <a href="#nogo" class="baseLink baseLinkClear jq-cancelUploadDocumentPopupBlock" title="Cancel">
        <em class="iconlink"></em>
        <span class="iconLinkLabel">Cancel</span>
    </a>
    </li>
    </ul>

</div>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/uploadDocument.js'/>"></script>