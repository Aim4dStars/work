<spring:eval var="featureLinkedAccount" expression="@environment.getProperty('feature.onboardingLinkedAccounts')" />

<div class="sectionContentItem jq-contactDetailsWrap" xmlns:c="http://www.w3.org/1999/XSL/Transform">

    <c:set var = "compareAcctString" value = "${clientApplication.investorAccountType}:"/>
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Linked accounts <c:if test="${featureLinkedAccount && (fn:contains('newIndividualSMSF:individualSMSF:newCorporateSMSF:corporateSMSF:', compareAcctString) || (not empty clientApplication.parentProductName && fn:contains('Cash Management Account', clientApplication.parentProductName )))}"> <span> (optional)</span></c:if>
    </h2>
    <c:if test="${featureLinkedAccount && empty clientApplication.linkedAccounts && (fn:contains('newIndividualSMSF:individualSMSF:newCorporateSMSF:corporateSMSF:', compareAcctString) || (not empty clientApplication.parentProductName && fn:contains('Cash Management Account', clientApplication.parentProductName )))}">
        <div class="formBlock">
            <p>There is no linked bank account provided for this account.</p>
            <p>Supplying a linked bank account ensures you and your clients can move money into and out of this Panorama account. If a linked account is not supplied, only your client can supply one after registering.</p>
        </div>
    </c:if>
    <!-- before this feature linked accounts were mandatory-->
    <c:if test="${not empty clientApplication.linkedAccounts}">
        <c:forEach var="account" items="${clientApplication.linkedAccounts}">
             <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne emphasis">${account.name}
                    </label>
                    <span><c:if test="${account.primary}">Primary linked account</c:if></span>
                </span>
             </div>
            <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">BSB
                </label>
                <span>${account.bsb}</span>
            </span>
            </div>
            <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Account number
                </label>
                <span>${account.accountNumber}</span>
            </span>
            </div>

            <c:if test="${not empty account.nickName}">
                <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">Nickname
                    </label>
                    <span>${account.nickName}</span>
                </span>
                </div>
            </c:if>

            <c:if test="${clientApplication.applicationOriginType == 'BTPanorama'&& clientApplication.investorAccountType != 'superAccumulation' && clientApplication.investorAccountType != 'superPension'}">
                <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">Direct debit deposit
                    </label>
                    <span><fmt:formatNumber value="${account.directDebitAmount}" currencySymbol="$" type="currency"/></span>
                </span>
                </div>
            </c:if>
         </c:forEach>
     </c:if>
</div>