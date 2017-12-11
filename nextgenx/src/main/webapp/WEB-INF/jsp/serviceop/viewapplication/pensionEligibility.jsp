<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<div class="sectionContentItem jq-contactDetailsWrap" xmlns:c="http://www.w3.org/1999/XSL/Transform">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Eligibility
    </h2>

    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne"> Pension eligibility
            </label>
            <span style="width:500px">
                <c:set var="pensionEligibility" value="${clientApplication.pensionEligibility.eligibilityCriteria}"/>
                <%=StringEscapeUtils.escapeHtml((String)pageContext.getAttribute("pensionEligibility"))%>
            </span>
        </span>
    </div>

    <c:if test="${not empty clientApplication.pensionEligibility.conditionRelease}">
    <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Condition of release
            </label>
            <span style="width:500px">${clientApplication.pensionEligibility.conditionRelease}</span>
        </span>
    </div>
    </c:if>
</div>