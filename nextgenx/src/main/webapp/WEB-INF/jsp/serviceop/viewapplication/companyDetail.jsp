<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        <c:choose><c:when test="${clientApplication.investorAccountType == 'company'}">Company details</c:when><c:otherwise>Company acting as trustee</c:otherwise></c:choose>
    </h2>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Company name
            </label>
        <span>${company.fullName}</span>
    </div>
    <c:if test="${not empty company.asicName}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">ASIC registered name
                </label>
                <span>${company.asicName}</span>
            </span>
        </div>
    </c:if>
    <c:if test="${not empty company.acn}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Company ACN
                </label>
                <span>${company.acn}</span>
            </span>
        </div>
    </c:if>
    <c:if test="${not empty company.abn}">
        <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Company ABN
            </label>
            <span>${company.abn}</span>
        </span>
        </div>
    </c:if>
    <c:if test="${not empty company.industry}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Industry
                </label>
                <span>${company.industry}</span>
            </span>
        </div>
    </c:if>

    <c:set var="crs" value ="${company}"/>
    <%@ include file="crsDetails.jsp" %>

    <c:if test="${clientApplication.investorAccountType == 'company'}">
      <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Registered for GST
            </label>
            <span><c:choose><c:when test="${company.registrationForGst}">YES</c:when><c:otherwise>NO</c:otherwise></c:choose></span>
        </span>
	    </div>
	     <div class="formBlock">
	        <span>
	            <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption
	            </label>
	            <span>
	            <c:choose>
	                <c:when test="${company.tfnProvided}"> TFN supplied</c:when>
	                <c:when test="${(not empty company.exemptionReason) && (company.exemptionReason ne 'No exemption')}"> Exempt - ${company.exemptionReason}</c:when>
	                <c:otherwise> Not Supplied</c:otherwise>
	            </c:choose>
	            </span>
	        </span>
	    </div>
    </c:if>
    <c:if test="${not empty company.occupierName}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Occupier of the company office</label>
                <span>${company.occupierName}</span>
            </span>
        </div>
    </c:if>

    <c:choose>
          <c:when test="${company.addressesV2 != null && not empty company.addressesV2}">
              <c:forEach var="addressv2" items="${company.addressesV2}">
                <div class="formBlock">
                    <span> <c:choose>
                            <c:when test="${not empty addressv2.addressType && addressv2.addressType == 'REGISTERED'}">
                                <label class="formLabelAppDetail formLabelStyleOne">Registered company Office </label>
                            </c:when>
                            <c:when test="${not empty addressv2.addressType && addressv2.addressType == 'PLACEOFBUSINESS'}">
                                <label class="formLabelAppDetail formLabelStyleOne">Principal place Of business </label>
                            </c:when>
                        </c:choose> <span>${addressv2.addressDisplayText}</span>
                    </span>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
             <c:forEach var="address" items="${company.addresses}">
                        <div class="formBlock">
                            <span> <c:choose>
                                    <c:when test="${address.domicile && not address.mailingAddress}">
                                        <label class="formLabelAppDetail formLabelStyleOne">Registered company Office </label>
                                    </c:when>
                                    <c:when test="${not address.domicile &&  address.mailingAddress}">
                                        <label class="formLabelAppDetail formLabelStyleOne">Principal place Of business </label>
                                    </c:when>
                                </c:choose> <span>${address.careOf} ${address.unitNumber}
                                    ${address.floor}${address.streetNumber} ${address.streetName} ${address.poBoxPrefix} ${address.poBox}
                                    ${address.suburb} ${address.state} ${address.postcode}
                                    ${address.country}</span>
                            </span>
                        </div>
             </c:forEach>
        </c:otherwise>
    </c:choose>
    <c:if test="${clientApplication.investorAccountType != 'newCorporateSMSF'}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Company verification
                </label>
                <span><c:choose><c:when test="${company.idvs =='Verified'}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
            </span>
        </div>
    </c:if>

    <c:if test="${clientApplication.investorAccountType == 'company'}">
         <c:if test="${company.personalInvestmentEntity != null}">
                 <div class="formBlock">
                        <span>
                            <label class="formLabelAppDetail formLabelStyleOne">Personal investment entity
                            </label>
                            <span>${company.personalInvestmentEntity}</span>
                        </span>
                 </div>
         </c:if>
     </c:if>
</div>
