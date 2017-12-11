<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        SMSF details
    </h2>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">SMSF name
            </label>
        <span class="emphasis">
           ${clientApplication.accountName}
        </span>
    </div>
    <!-- If there is not ABN, its a new SMSF, waiting until we have a flag to indicate this instead -->
    <c:if test="${not empty clientApplication.smsf.abn}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">SMSF ABN
                </label>
                <span>${clientApplication.smsf.abn}</span>
            </span>
        </div>
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Date of Registration
                </label>
                <span>${clientApplication.smsf.registrationDate}</span>
            </span>
        </div>
        <c:if test="${not empty clientApplication.smsf.registrationState}">
            <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">Registration State
                    </label>
                    <span>${clientApplication.smsf.registrationState}</span>
                </span>
            </div>
        </c:if>
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Industry
                </label>
                <span>${clientApplication.smsf.industry}</span>
            </span>
        </div>
    </c:if>

    <c:set var="crs" value ="${clientApplication.smsf}"/>
    <%@ include file="crsDetails.jsp" %>

    <c:if test="${clientApplication.investorAccountType == 'individualSMSF' || clientApplication.investorAccountType == 'corporateSMSF' }">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Registered for GST
                </label>
                <span><c:choose><c:when test="${clientApplication.smsf.registrationForGst}">YES</c:when><c:otherwise>NO</c:otherwise></c:choose></span>
            </span>
        </div>
    </c:if>
    <c:if test="${not empty clientApplication.smsf.abn}">
     <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption
            </label>
            <span>
            <c:choose>
                <c:when test="${clientApplication.smsf.tfnProvided}"> TFN supplied</c:when>
                <c:when test="${(not empty clientApplication.smsf.exemptionReason) && (clientApplication.smsf.exemptionReason ne 'No exemption')}"> Exempt - ${clientApplication.smsf.exemptionReason}</c:when>
                <c:otherwise> Not Supplied</c:otherwise>
            </c:choose>
            </span>
        </span>
    </div>
    </c:if>
    <c:choose>
         <c:when test="${clientApplication.smsf.addressesV2 != null && not empty clientApplication.smsf.addressesV2}">
            <c:forEach var="addressv2" items="${clientApplication.smsf.addressesV2}">
                                       <div class="formBlock">
                                                   <c:if test="${addressv2.addressType == 'REGISTERED'}">
                                                       <label class="formLabelAppDetail formLabelStyleOne">SMSF Address </label>
                                                    <span>${addressv2.addressDisplayText}</span>
                                                   </c:if>
                                       </div>
                        </c:forEach>
        </c:when>
        <c:otherwise>
           <c:forEach var="address" items="${clientApplication.smsf.addresses}">
                    <div class="formBlock">
                         <c:if test="${address.domicile && not address.mailingAddress}">
                             <label class="formLabelAppDetail formLabelStyleOne">SMSF Address </label>
                             <span>${address.careOf} ${address.unitNumber}
                             ${address.floor}${address.streetNumber} ${address.streetName} ${address.poBoxPrefix} ${address.poBox}
                             ${address.suburb} ${address.state} ${address.postcode}
                             ${address.country}</span>
                         </c:if>
                    </div>
           </c:forEach>
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty clientApplication.smsf.abn}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">SMSF verification
                </label>
                <span><c:choose><c:when test="${clientApplication.smsf.idvs =='Verified'}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
            </span>
        </div>
    </c:if>
</div>
