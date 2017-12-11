<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Trustee(s)
    </h2>
    <c:forEach var="trustee" items="${clientApplication.trustees}">
    <div class="formBlock">
        <span class="emphasis">
           ${trustee.title} ${trustee.fullName}
        </span>
    </div>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Preferred name
            </label>
            <c:if test="${trustee.preferredName != null}"><span>${trustee.preferredName}&nbsp;</span> </c:if>
        </span>
    </div>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Date of Birth
            </label>
            <span>${trustee.dateOfBirth}</span>
        </span>
    </div>
    <div class="formBlock">
    <span>
        <label class="formLabelAppDetail formLabelStyleOne">Gender
        </label>
        <span>${trustee.gender}</span>
    </span>

    <c:set var="crs" value ="${trustee}"/>
    <%@ include file="crsDetails.jsp" %>

    <c:if test ="${clientApplication.investorAccountType == 'newIndividualSMSF'}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption
                </label>
                <span>
                <c:choose>
                    <c:when test="${trustee.tfnProvided}"> TFN supplied</c:when>
                    <c:when test="${(not empty trustee.exemptionReason) && (trustee.exemptionReason ne 'No exemption')}"> Exempt - ${trustee.exemptionReason}</c:when>
                    <c:otherwise> Not Supplied</c:otherwise>
                </c:choose>
                </span>
            </span>
        </div>
    </c:if>
    </div>

      <c:set var="investorAddressesV2"  scope="request" value="${trustee.addressesV2}" />
      <c:set var="investorAddresses"  scope="request" value="${trustee.addresses}" />

        <c:set var="investorPhones"  scope="request" value="${trustee.phones}" />
        <c:set var="investorEmails"  scope="request" value="${trustee.emails}" />
        <jsp:include page="investorDetails.jsp" />

        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Proof of identity
                </label>
                <span><c:choose><c:when test="${(trustee.idvs =='Verified') || (trustee.idvs =='compl')}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
            </span>
        </div>
        <br/>
    </c:forEach>
</div>
