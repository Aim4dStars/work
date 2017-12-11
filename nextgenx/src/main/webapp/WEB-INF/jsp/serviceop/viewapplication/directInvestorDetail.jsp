<div class="sectionContentItem jq-contactDetailsWrap">
  <h2 class="mainHeaderItem mainHeaderItemMod3">
    Investor(s)
  </h2>
  <c:forEach var="investor" items="${clientApplication.investors}">

      <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Country for tax purposes
            </label>
            <span>${investor.resiCountryforTax}</span>
        </span>
      </div>
      <div class="formBlock">
	        <span>
	            <label class="formLabelAppDetail formLabelStyleOne">TFN / exemption
                </label>
	            <span>
	            <c:choose>
	                <c:when test="${investor.tfnProvided}"> TFN supplied</c:when>
	                <c:when test="${(not empty investor.exemptionReason) && (investor.exemptionReason ne 'No exemption')}"> Exempt - ${investor.exemptionReason}</c:when>
	                <c:otherwise> Not Supplied</c:otherwise>
	            </c:choose>
	            </span>
	        </span>
      </div>

      <c:set var="investorPhones"  scope="request" value="${investor.phones}" />
      <c:set var="investorEmails"  scope="request" value="${investor.emails}" />
      <jsp:include page="investorDetails.jsp" />

  </c:forEach>
</div>
