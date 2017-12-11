<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Investor(s)
    </h2>
    <c:forEach var="investor" items="${clientApplication.investors}">
    <div class="formBlock">
        <span class="emphasis">
           ${investor.title} ${investor.fullName}
        </span>
    </div>

    <c:if test="${clientApplication.accountKey != null && clientApplication.investorAccountType == 'company'}">
         <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Role
                </label>
                <c:forEach var="role" items="${investor.personRoles}">
                    <span>${role}</span>
                </c:forEach>
            </span>
         </div>
    </c:if>

    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Preferred name
            </label>
            <span>${investor.preferredName}</span>
        </span>
    </div>
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Date of Birth
            </label>
            <span>${investor.dateOfBirth}</span>
        </span>
    </div>
    <div class="formBlock">
    <span>
        <label class="formLabelAppDetail formLabelStyleOne">Gender
        </label>
        <span>${investor.gender}</span>
    </span>
    </div>

    <c:set var="crs" value ="${investor}"/>
    <%@ include file="crsDetails.jsp" %>

    <c:choose>
        <c:when test="${clientApplication.investorAccountType == 'individual' || clientApplication.investorAccountType == 'joint' ||
        clientApplication.investorAccountType == 'superAccumulation' || clientApplication.investorAccountType == 'superPension'}" >
        <div class="formBlock">
                <span>
                    <c:choose>
                     <c:when test="${clientApplication.investorAccountType == 'superAccumulation'}">
                         <label class="formLabelAppDetail formLabelStyleOne">Tax file number </label>
                     </c:when>
                     <c:otherwise>
                         <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption </label>
                     </c:otherwise>
                    </c:choose>
                </span>
                <span>
                    <c:choose>
                        <c:when test="${investor.tfnProvided}">TFN supplied</c:when>
                        <c:when test="${(not empty investor.exemptionReason) && (investor.exemptionReason ne 'No exemption')}"> Exempt - ${investor.exemptionReason}</c:when>
                        <c:otherwise>Not Supplied</c:otherwise>
                    </c:choose>
                </span>
            </div>

	    </c:when>
    </c:choose>

    <c:set var="investorAddressesV2"  scope="request" value="${investor.addressesV2}" />
    <c:set var="investorAddresses"  scope="request" value="${investor.addresses}" />
    <c:set var="investorPhones"  scope="request" value="${investor.phones}" />
    <c:set var="investorEmails"  scope="request" value="${investor.emails}" />
    <jsp:include page="investorDetails.jsp" />

    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Proof of identity
            </label>
            <span><c:choose><c:when test="${investor.idvs =='Verified'}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
        </span>
    </div>
    </c:forEach>
</div>
