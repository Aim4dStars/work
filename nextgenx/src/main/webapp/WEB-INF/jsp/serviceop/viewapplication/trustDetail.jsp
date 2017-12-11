<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Trust details
    </h2>
    <c:if test="${not empty clientApplication.trust.businessClassificationDesc}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Trust description
                </label>
            <span class="emphasis">
               ${clientApplication.trust.businessClassificationDesc}
            </span>
        </div>
    </c:if>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Trust name
            </label>
            <span>${clientApplication.accountName}</span>
        </span>
    </div>
     <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Trust business name
            </label>
            <span>${clientApplication.trust.businessName}</span>
        </span>
    </div>
     <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Trust ABN
            </label>
            <span>${clientApplication.trust.abn}</span>
        </span>
    </div>
    
    <c:if test="${not empty clientApplication.trust.trustReguName}">
	    <div class="formBlock">
	    <span>
	        <label class="formLabelAppDetail formLabelStyleOne">Name of regulator
	     </label>
	        <span>${clientApplication.trust.trustReguName}</span>
	    </span>
	    </div>
    </c:if>
    
    <c:if test="${not empty clientApplication.trust.licencingNumber}">
	    <div class="formBlock">
	    <span>
	        <label class="formLabelAppDetail formLabelStyleOne">Licensing number
	     </label>
	        <span>${clientApplication.trust.licencingNumber}</span>
	    </span>
	    </div>
    </c:if>
    
    <c:if test="${not empty clientApplication.trust.arsn}">
	    <div class="formBlock">
	    <span>
	        <label class="formLabelAppDetail formLabelStyleOne">Australian Registered Scheme Number (ARSN)
	     </label>
	        <span>${clientApplication.trust.arsn}</span>
	    </span>
	    </div>
    </c:if>
    
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Date of Registration
            </label>
            <span><fmt:formatDate pattern="dd MMM yyyy"
                                  value="${clientApplication.trust.registrationDate}" /></span>
        </span>
    </div>
    <div class="formBlock">
    <span>
        <label class="formLabelAppDetail formLabelStyleOne">Registration State
        </label>
        <span>${clientApplication.trust.registrationState}</span>
    </span>
    </div>
    
    <c:if test="${not empty clientApplication.trust.legEstFund}">
	    <div class="formBlock">
	    <span>
	        <label class="formLabelAppDetail formLabelStyleOne">Name of legislation establishing the fund
	     </label>
	        <span>${clientApplication.trust.legEstFund}</span>
	    </span>
	    </div>
    </c:if>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Industry
            </label>
            <span>${clientApplication.trust.industry}</span>
        </span>
    </div>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Registered for GST
            </label>
            <span><c:choose><c:when test="${clientApplication.trust.registrationForGst}">YES</c:when><c:otherwise>NO</c:otherwise></c:choose></span>
        </span>
    </div>

    <c:set var="crs" value ="${clientApplication.trust}"/>
    <%@ include file="crsDetails.jsp" %>

     <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption
            </label>
            <span>
            <c:choose>
                <c:when test="${clientApplication.trust.tfnProvided}"> TFN supplied</c:when>
                <c:when test="${(not empty clientApplication.trust.exemptionReason) && (clientApplication.trust.exemptionReason ne 'No exemption')}"> Exempt - ${clientApplication.trust.exemptionReason}</c:when>
                <c:otherwise> Not Supplied</c:otherwise>
            </c:choose>
            </span>
        </span>
    </div>

    <c:choose>
        <c:when test="${clientApplication.trust.addressesV2 != null && not empty clientApplication.trust.addressesV2}">
                  <c:forEach var="addressv2" items="${clientApplication.trust.addressesV2}">
                                    <div class="formBlock">
                                                            <c:if test="${not empty addressv2.addressType && addressv2.addressType == 'REGISTERED'}">
                                                                <label class="formLabelAppDetail formLabelStyleOne">Trust Address </label>
                                                             <span>${addressv2.addressDisplayText}</span>
                                                    </c:if>
                                   </div>
                            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:forEach var="address" items="${clientApplication.trust.addresses}">
                    <div class="formBlock">
                                            <c:if test="${address.domicile && not address.mailingAddress}">
                                                <label class="formLabelAppDetail formLabelStyleOne">Trust Address </label>
                                             <span>${address.careOf} ${address.unitNumber}
                                            ${address.floor}${address.streetNumber} ${address.streetName} ${address.poBoxPrefix} ${address.poBox}
                                            ${address.suburb} ${address.state} ${address.postcode}
                                            ${address.country}</span>
                                    </c:if>
                   </div>
            </c:forEach>
        </c:otherwise>
	</c:choose>
	 <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Trust Verification
            </label>
            <span><c:choose><c:when test="${clientApplication.trust.idvs =='Verified'}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
        </span>
    </div>

    <c:if test="${clientApplication.trust.personalInvestmentEntity != null}">
         <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">Personal investment entity
                    </label>
                    <span>${clientApplication.trust.personalInvestmentEntity}</span>
                </span>
         </div>
    </c:if>
</div>
