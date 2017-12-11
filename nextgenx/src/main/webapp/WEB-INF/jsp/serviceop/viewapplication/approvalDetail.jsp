<c:if test="${clientApplication.offlineApprovalAccess}">
    <div class="sectionContentItem jq-contactDetailsWrap">
        <h2 class="mainHeaderItem mainHeaderItemMod3">Application approval</h2>
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Method of approval</label>
                <span>${clientApplication.approvalType}</span>
            </span>
        </div>
    </div>
</c:if>
