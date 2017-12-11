<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<c:forEach var="address" items="${requestScope.investorAddresses}">
 <c:if test="${address != null &&  not empty address}">
        <div class="formBlock">
            <span>
                <c:choose>
                    <c:when test="${address.domicile && not address.mailingAddress}">
                        <label class="formLabelAppDetail formLabelStyleOne">Residential
                            Address </label>
                    </c:when>
                    <c:when test="${not address.domicile &&  address.mailingAddress}">
                        <label class="formLabelAppDetail formLabelStyleOne">Postal
                            Address </label>
                    </c:when>
                    <c:otherwise>
                        <label class="formLabelAppDetail formLabelStyleOne">Address </label>
                    </c:otherwise>
                </c:choose>
                <span>${address.careOf} ${address.poBoxPrefix} ${address.poBox} ${address.floor} ${address.unitNumber}
                    ${address.streetNumber} ${address.streetName} ${address.building}
                    ${address.suburb} ${address.state} ${address.postcode} ${address.country}
            </span>
        </div>
 </c:if>
</c:forEach>

<c:forEach var="addressV2" items="${requestScope.investorAddressesV2}">
 <c:if test="${addressV2 != null &&  not empty addressV2}">
        <div class="formBlock">
            <span>
                <c:choose>
                    <c:when test="${addressV2.addressType == 'RESIDENTIAL'}">
                    <label class="formLabelAppDetail formLabelStyleOne">Residential
                       Address </label>
                    </c:when>
                <c:when test="${addressV2.addressType == 'POSTAL'}">
                   <label class="formLabelAppDetail formLabelStyleOne">Postal
                        Address </label>
                </c:when>
                <c:otherwise>
                 <label class="formLabelAppDetail formLabelStyleOne">Address</label>
                 </c:otherwise>
               </c:choose>
            <span>${addressV2.addressDisplayText}</span>
            </span>
        </div>
 </c:if>
</c:forEach>

<c:forEach var="phone" items="${requestScope.investorPhones}">
        <div class="formBlock">
            <span>
                <c:choose>
                    <c:when test="${phone.phoneType == 'Home'}"><label class="formLabelAppDetail formLabelStyleOne">Home number</c:when>
                    <c:when test="${phone.phoneType == 'Work'}"><label class="formLabelAppDetail formLabelStyleOne">Work number</c:when>
                    <c:when test="${phone.phoneType == 'Other'}"><label class="formLabelAppDetail formLabelStyleOne">Other number</c:when>
                    <c:otherwise>
                        <label class="formLabelAppDetail formLabelStyleOne">Mobile <c:if test="${phone.phoneType =='Primary'}">- Primary</c:if>
                    </c:otherwise>
                </c:choose>
                </label>
                <span>
                    ${phone.countryCode} ${phone.areaCode} ${phone.number}<c:if test="${phone.preferred}"> (Preferred Contact)</c:if>
                </span>
            </span>
        </div>
</c:forEach>

<c:forEach var="investorEmail" items="${requestScope.investorEmails}">
        <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Email <c:if test="${investorEmail.emailType =='Primary'}">- Primary</c:if>
            </label>
            <span>${investorEmail.email}<c:if test="${investorEmail.preferred}"> (Preferred Contact)</c:if></span>
        </span>
        </div>
</c:forEach>

