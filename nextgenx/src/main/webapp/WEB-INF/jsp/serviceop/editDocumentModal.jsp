<%@ taglib prefix="nextgen" uri="/WEB-INF/taglib/core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style>
.abcWrapper{}
</style>
<div id="editDocumentMetadataPopupBlock" class="jq-editDocumentMetadataPopupBlock" style="display:none;">
    <div class="modalContentMod3" >
    <h5 class="formHeaderModal">
        <span class="baseGama"> Update</span> document detail</span>
    </h5>
    <!-- Body div --->
    <div class="noticeBox noDisplay warningBox jq-updateMessageBox">
        <ul class="noticeBoxWrapper">
            <li>
                <span class="messageIcon"><em class="iconItem"></em></span>
            </li>
            <li class="noticeBoxText noticeBoxTextSmallBox">
                <p class="emphasis jq-updateMessage"></p>
            </li>
        </ul>
    </div>
    <div style="height: 650px; width: 800px; margin:0 auto; ">

        <div style="width: 650px; margin:0 auto;">
            <div style="margin-left: 1cm; margin-top: 10px;">
                <dl class="dataSummary">
                    <!--<dt class="listContentItemHeader clearBoth  emphasis">Business Area</dt><dd><span class="jq-businessArea"></span></dd>-->
                    <dt class="listContentItemHeader clearBoth  emphasis">Document Category</dt><dd><span class="jq-documentCategory"></span></dd>
                    <dt class="listContentItemHeader clearBoth  emphasis">Uploaded by</dt>
                    <dd>
                        <span class="jq-addedByName"></span> ( <span class="jq-addedByRole"></span> - <span class="jq-addedById"></span> )
                    </dd>
                    <dt class="listContentItemHeader clearBoth  emphasis">File</dt><dd><span class="jq-attachedFileInfo" style="text-overflow: ellipsis;"></span></dd>
                    <dt class="listContentItemHeader clearBoth  emphasis">Upload Date</dt><dd><span class="jq-checkInDate"></span></dd>
                </dl>
            </div>
        </div>
        <div style="width: 800px; margin:0 auto; float:left;">
            <ul class="tabs">
                <li><a href="#tab1">Document meta data</a></li>
                <li><a href="#tab2">Document audit history</a></li>
            </ul>
            <div class="clr"></div>
           <div id="tab1" style="border-top: black solid 1px;">
           <form id="documentMetaDataForm" name="documentMetaDataForm" method="post">
           <input type="hidden" name="changeToken" id="changeToken" class="jq-changeToken" />
           <input type="hidden" name="documentId" id="documentId" class="jq-documentId" />
           <input type="hidden" name="accountId" id="accountId" class="jq-accountId" />
           <input type="hidden" name="documentCategoryInput" id="documentCategoryInput" class="jq-documentCategoryInput" />
           <input type="hidden" name="documentSoftDeleted" id="documentSoftDeleted" class="jq-documentSoftDeleted" />
           <fieldset>
            <ul class="formBlockContainer formBlockContainerMod6" style="margin:0 auto;">
                <li class="formBlock formBlockMod1">
                  <span class="inputStyleAlignOne"  style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="relationshipId" class="formLabel">Relationship ID</label>
                    <input name="relationshipId"
                      id="relationshipId"
                      class="formTextInput jq-relationshipIdInput"
                      data-ng-key="relationshipId"
                      type="text"
                      size="30">
                  </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="documentName" class="formLabel">Document Name</label>
                    <input name="documentName"
                      id="documentName"
                      class="jq-documentNameInput"
                      data-ng-key="documentName"
                      type="text"
                      size="30">
                  </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                  <label for="externalId" class="formLabel">External ID</label>
                  <input
                    id="externalId"
                    name="externalId"
                    class="formTextInput inputStyleEight jq-externalIdInput"
                    data-ng-key="externalId"
                    type="text"
                    size="30">
                </span>
                </li>
                <li class="formBlock formBlockMod1">
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="relationshipType" class="formLabel">Relationship Type</label>
                    <select id="relationshipType" class="jq-relationshipType" name="relationshipType">
                    </select>
                  </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="documentTitleCode" class="formLabel">Document Title Code</label>
                     <select id="documentTitleCode" class="jq-documentTitleCode" name="documentTitleCode">
                        <option value="Any" selected="selected">Any</option>
                    </select>
                  </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="sourceId" class="formLabel">Source ID</label>
                        <input id="sourceId" name="sourceId"
                          class="formTextInput inputStyleEight jq-sourceIdInput "
                          data-ng-key="sourceId"
                          type="text"
                          size="30" />
                        <select id="sourceIdDropdown" class="jq-sourceIdDropdown noDisplay" name="sourceIdDropdown">
                        </select>
                </li>
                <li class="formBlock formBlockMod1">
                 <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="startDate" class="formLabel">Start Date</label>
                    <span class="calendarPlaceHolder jq-StartDateCalendarPlaceHolder"></span>
                      <span class="iconWrapper">
                       <input id="startDate" name="startDate" data-calendar="jq-StartDateCalendarPlaceHolder"
                                class="formTextInput jq-startDateInput inputStyleThree" value="" readonly data-placeholder=""/>
                      <a class="iconActionButton jq-startDateCalendarIcon jq-appendErrorAfter iconActionJointButton calendarIconLink" title="Date Picker" href="#nogo">
                            <em class="iconcalendar"><span>Select Date</span></em>
                            <em class="iconarrowfulldown"></em>
                        </a>
                     </span>
                 </span>
                 <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="endDate" class="formLabel">End Date</label>
                    <span class="calendarPlaceHolder jq-EndDateCalendarPlaceHolder"></span>
                      <span class="iconWrapper">
                      <input id="endDate" name="endDate" data-calendar="jq-EndDateCalendarPlaceHolder"
                               class="formTextInput jq-endDateInput inputStyleThree" value="" readonly data-placeholder=""/>
                      <a class="jq-endDateCalendarIcon jq-appendErrorAfter calendarIconLink" title="Date Picker" href="#nogo">
                            <em class="iconcalendar"><span>Select date</span></em>
                            <em class="iconarrowfulldown"></em>
                        </a>
                     </span>
                 </span>
                   <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                   <label for="expiryDate" class="formLabel">Expiry Date</label>
                   <span class="calendarPlaceHolder jq-expiryDateCalendarPlaceHolder"></span>
                     <span class="iconWrapper">
                     <input id="expiryDate" name="expiryDate" data-calendar="jq-expiryDateCalendarPlaceHolder"
                              class="formTextInput jq-expiryDateInput inputStyleThree" value="" readonly data-placeholder=""/>
                     <a class="jq-expiryDateCalendarIcon jq-appendErrorAfter calendarIconLink" title="Date Picker" href="#nogo">
                           <em class="iconcalendar"><span>Select date</span></em>
                           <em class="iconarrowfulldown"></em>
                       </a>
                    </span>
                 </span>

                </li>
                <li class="formBlock formBlockMod1">
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="editFinancialYear" class="formLabel">Financial Year</label>
                    <select id="editFinancialYear" class="jq-editFinancialYear" name="editFinancialYear">
                        <option value="Any" selected="selected">Any</option>
                    </select>
                  </span>
                  <span class="inputStyleAlignOne jsSubDocumentType" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="editDocumentSubCategory" class="formLabel">Document sub-category</label>
                    <select id="editDocumentSubCategory" class="jq-editDocumentSubCategory" name="editDocumentSubCategory">
                    </select>
                  </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="editDocumentSubCategory2" class="formLabel">Document sub-category 2</label>
                    <select id="editDocumentSubCategory2" class="jq-editDocumentSubCategory2" name="editDocumentSubCategory2">
                        <option value="Any" selected="selected">Any</option>
                    </select>
                  </span>
               </li>
                <li class="formBlock formBlockMod1">
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                    <label for="abcOrderId" class="formLabel">ABS Order ID</label>
                    <input
                      id="abcOrderId"
                      name="abcOrderId"
                      class="formTextInput inputStyleEight jq-abcOrderIdInput"
                      data-ng-key="abcOrderId"
                      type="text"
                      size="30">
                  </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px;">
                  <label for="batchId" class="formLabel">Batch ID</label>
                  <input
                    id="batchId"
                    name="batchId"
                    class="formTextInput inputStyleEight jq-batchIdInput"
                    data-ng-key="batchId"
                    type="text"
                    size="30">
                </span>
                  <span class="inputStyleAlignOne" style="width: 250px; margin-left: 5px;">
                    <label for="documentActivity" class="formLabel">Activity</label>
                    <input
                      id="documentActivity"
                      name="documentActivity"
                      class="formTextInput inputStyleEight jq-documentActivityInput"
                      data-ng-key="documentActivity"
                      type="text"
                    size="30">
                  </span>
                </li>
                <li class="formBlock formBlockMod1">
                 <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 1px;">
                    <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px; margin-top: 10px;" >
                    <input type="checkbox" class="jq-permanentEdit permanentEdit" data-ng-key="permanentEdit" name="permanentEdit" style="display:block;" />
                    <label for="permanentEdit" class="formLabel">Mark as permanent</label>
                    </span>
              </span>
              <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px; margin-top: 10px;" >
                <input type="checkbox" class="jq-auditFlag auditFlag" data-ng-key="auditFlag" name="auditFlag" style="display:block;" />
                <label for="auditFlag" class="formLabel">Flagged for audit</label>
                </span>
                 <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px; margin-top: 10px;">
                    <input type="checkbox" class="jq-editPrivacyFlag editPrivacyFlag" data-ng-key="editPrivacyFlag" name="editPrivacyFlag" style="display:block;" />
                    <label for="editPrivacyFlag" class="formLabel"> Private document</label>
                </span>
                </li>
                <li class="formBlock formBlockMod1">
                  <span class="inputStyleAlignOne" style="width: 250px; margin: 0 auto; margin-left: 5px; margin-top: 10px;" >
                    <input type="checkbox" class="jq-softDeleteEdit softDeleteEditFlag" data-ng-key="softDeleteEdit" name="softDeleteEdit" style="display:block;" />
                    <label for="softDeleteEdit" class="formLabel">Mark as soft deleted</label>
                    </span>
                </li>
            </ul>
            </fieldset>
           </form>
           </div>
            <div id="tab2" style="border-top: black solid 1px; margin:0 auto;" class="noDisplay">
				<div>
				  <div style="width : 450px; margin-top: 20px;">
					<dl class="dataSummary">
							  <dt class="listContentItemHeader clearBoth  emphasis">Updated by ID</dt><dd><span class="jq-updatedByIdText"></span></dd>
							  <dt class="listContentItemHeader clearBoth  emphasis">Updated by role</dt><dd><span class="jq-updatedByRoleText"></span></dd>
							  <dt class="listContentItemHeader clearBoth  emphasis">Updated by Name</dt><dd><span class="jq-UpdateByNameText"></span></dd>
							  <dt class="listContentItemHeader clearBoth  emphasis">Last modified on</dt><dd><span class="jq-lastModifiedOn"></span></dd>
					</dl>
					</div>
				  <div class="setHalf setHalfMod3 lastBorder" style="margin-top: 20px;">
					  <dl class="dataSummary">
						  <dt class="listContentItemHeader clearBoth  emphasis">Deleted by ID</dt><dd><span class="jq-deletedByIdText"></span></dd>
						  <dt class="listContentItemHeader clearBoth  emphasis">Deleted by role</dt><dd><span class="jq-deletedByRoleText"></span></dd>
						  <dt class="listContentItemHeader clearBoth  emphasis">Deleted by Name</dt><dd><span class="jq-deletedByNameText"></span></dd>
						  <dt class="listContentItemHeader clearBoth  emphasis">Deleted on</dt><dd><span class="jq-deletedOn"></span></dd>
					  </dl>
				  </div>
				  <div class="setHalf setHalfMod4" style=" margin-top: 20px;">
					  <dl class="dataSummary" style=" margin-left: 20px;">
						  <dt class="listContentItemHeader clearBoth  emphasis">Restored by ID</dt><dd><span class="jq-restoredByIdText"></span></dd>
						  <dt class="listContentItemHeader clearBoth  emphasis">Restored by role</dt><dd><span class="jq-restoredByRoleText"></span></dd>
						  <dt class="listContentItemHeader clearBoth  emphasis">Restored by Name</dt><dd><span class="jq-restoredByNameText"></span></dd>
						  <dt class="listContentItemHeader clearBoth  emphasis">Restored on</dt><dd><span class="jq-restoredOn"></span></dd>
					  </dl>
				  </div>
			  </div>
			</div> <!-- tab2 -->
        </div>
      </div>
      <!-- End Body div --->
    <ul class="actionWrapper actionWrapPrimary actionWrapperMod5">
        <li>
        <a href="#nogo" class="primaryButton jq-documentUpdateBtn" title="Update">
            <span>Update</span>
            <em class="icon-save"></em>
            </a>
        </li>
        <li>
        <a href="#nogo" class="baseLink baseLinkClear jq-cancelDocumentMetadataPopupBlock" title="Cancel">
            <em class="iconlink"></em>
            <span class="iconLinkLabel">Cancel</span>
        </a>
        </li>
    </ul>
    </div>
</div>
<script language="javascript" type="text/javascript" src="<nextgen:hashurl src='/public/static/js/client/desktop/pages/updateDocumentMetaData.js'/>"></script>