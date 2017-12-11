<c:set var="isNewCorporate" value="${clientApplication.investorAccountType == 'newCorporateSMSF'}" scope="request"  />
<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Director(s)
    </h2>
    <c:forEach var="director" items="${clientApplication.directors}">
    <div class="formBlock">
        <span class="emphasis">
           ${director.title} ${director.fullName}
        </span>
    </div>
    <div class="formBlock">
        <label class="formLabelAppDetail formLabelStyleOne">Preferred name</label>
        <span>${director.preferredName}</span>
    </div>

    <c:if test ="${isNewCorporate && director.formerName != null}">
        <div class="formBlock">
            <label class="formLabelAppDetail formLabelStyleOne">Former names</label>
            <span>${director.formerName}</span>
        </div>
    </c:if>

    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Gender</label>
            <span>${director.gender}</span>
        </span>
    </div>

    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Date of Birth</label>
            <span>${director.dateOfBirth}</span>
        </span>
    </div>

    <c:set var="crs" value ="${director}"/>
    <%@ include file="crsDetails.jsp" %>

    <c:if test ="${isNewCorporate}">

        <div class="formBlock">
                <label class="formLabelAppDetail formLabelStyleOne">Place of birth</label>
                <span>${director.placeOfBirthSuburb} ${director.placeOfBirthState} ${director.placeOfBirthCountry}</span>
        </div>

        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption</label>
                <span>
				<c:choose>
				    <c:when test="${director.tfnProvided}"> TFN supplied</c:when>
				    <c:when test="${(not empty director.exemptionReason) && (director.exemptionReason ne 'No exemption')}"> Exempt - ${director.exemptionReason}</c:when>
				    <c:otherwise> Not Supplied</c:otherwise>
				</c:choose>
                </span>
            </span>
        </div>

    </c:if>

    <c:set var="investorAddressesV2"  scope="request" value="${director.addressesV2}" />
    <c:set var="investorAddresses"  scope="request" value="${director.addresses}" />

    <c:set var="investorPhones"  scope="request" value="${director.phones}" />
    <c:set var="investorEmails"  scope="request" value="${director.emails}" />
    <jsp:include page="investorDetails.jsp" />

    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Proof of identity</label>
            <span><c:choose><c:when test="${director.idvs =='Verified'}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
        </span>
    </div>
    <br/>
    </c:forEach>
</div>
