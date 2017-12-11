<c:choose>
    <c:when test="${role =='Beneficiary'}"><span>Beneficiary</span><br/></c:when>
    <c:when test="${role =='BeneficialOwner'}">
        <span>
            <c:choose>
                <c:when test="${featureAML}">
                   Shareholder / controller <c:if test="${clientApplication.investorAccountType != 'corporateSMSF' && clientApplication.investorAccountType != 'company'}">of the company</c:if>
                </c:when>
                <c:otherwise>
                   Responsible person
                </c:otherwise>
            </c:choose>
        </span><br/>
    </c:when>
    <c:when test="${role =='ControllerOfTrust'}">
        <c:choose>
            <c:when test="${clientApplication.investorAccountType == 'individualTrust'}">
                <span>Controller</span><br/>
            </c:when>
            <c:otherwise><span>Controller of the trust</span><br/></c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${role =='Member' && additionalMember == 'true'}">
        <span>Member under 18/legally disabled</span><br/>
    </c:when>
    <c:when test="${role =='Shareholder'}">
        <span>
            <c:choose>
                <c:when test="${featureAML}">
                   Shareholder
                </c:when>
                <c:otherwise>
                   ${role}
                </c:otherwise>
            </c:choose>
        </span><br/>
    </c:when>
    <c:otherwise><span>${role}</span><br/></c:otherwise>
</c:choose>