<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Country of residence for tax purposes</label>
            <span>
                <c:if test="${crs.resiCountryforTax == 'Australia'}">
                    ${crs.resiCountryforTax}
                    <c:if test="${crs.taxResidenceCountries != null}">
                        <c:if test="${ crs.taxResidenceCountries.getClass().isArray() ? crs.taxResidenceCountries.length > 0 : crs.taxResidenceCountries.size() > 0}"> - Used for tax calculations</c:if>
                    </c:if>
                    <br>
                </c:if>
                <c:if test="${crs.resiCountryforTax != 'Australia' && empty crs.taxResidenceCountries}">
                    ${crs.resiCountryforTax}
                    <br>
                </c:if>
                <c:forEach items="${crs.taxResidenceCountries}" var="country">
                    ${country.taxResidenceCountry} -
                    <c:if test="${country.taxExemptionReason != null}">
                        <c:if test="${country.taxExemptionReason == 'Under age'}">TIN exempt - under age</c:if>
                        <c:if test="${country.taxExemptionReason == 'TIN pending'}">TIN pending</c:if>
                        <c:if test="${country.taxExemptionReason == 'TIN not issued'}">TIN not issued</c:if>
                        <c:if test="${country.taxExemptionReason == 'Tax identification number'}">TIN</c:if>
                    </c:if>
                    <c:if test="${country.tin != null}"> - ${country.tin}</c:if>
                    <br>
                </c:forEach>
            </span>
        </span>
    </div>
    <c:if test="${crs.overseasTaxResident == false}">
        <div class="formBlock">
            <span>
                <label class="formLabelAppDetail formLabelStyleOne">Overseas resident for tax purposes</label>
                <span>No</span>
            </span>
        </div>
    </c:if>
    <c:if test="${crs.resiCountryforTax != 'Australia' && not empty crs.taxResidenceCountries }">
        <c:if test="${ crs.taxResidenceCountries.getClass().isArray() ? crs.taxResidenceCountries.length > 1 : crs.taxResidenceCountries.size() > 1}">
            <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">Country to use for tax calculations</label>
                    <span>${crs.resiCountryforTax}</span>
                </span>
            </div>
        </c:if>
    </c:if>