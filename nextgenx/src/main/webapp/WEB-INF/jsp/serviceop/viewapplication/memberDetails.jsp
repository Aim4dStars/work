<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:eval var="featureAML" expression="@environment.getProperty('feature.onboardingAmlChanges')" />

<div class="sectionContentItem jq-contactDetailsWrap">
  <c:if test="${not empty members}">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
      <c:choose>
        <c:when test="${clientApplication.investorAccountType == 'company'}">
            <c:choose>
                <c:when test="${featureAML}">
                   Additional Shareholder(s) / Controllers
                </c:when>
                <c:otherwise>
                   Additional Shareholder(s) / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'individualSMSF' || clientApplication.investorAccountType == 'newIndividualSMSF'}">
            Additional Member(s)
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'corporateSMSF'}">
            <c:choose>
                <c:when test="${featureAML}">
                   <c:set var="additionalMember"  scope="request" value="true" />
                   Additional Members,Shareholders and Controllers
                </c:when>
                <c:otherwise>
                   Additional Shareholder / Member(s) / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'newCorporateSMSF'}">
            Additional Shareholder / Member(s)
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'corporateTrust'}">
            <c:choose>
                <c:when test="${featureAML}">
                   Additional Beneficiaries, Shareholders  and Controllers
                </c:when>
                <c:otherwise>
                   Additional Shareholder / Beneficiary / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'individualTrust'}">
            <c:choose>
                <c:when test="${featureAML}">
                   Additional Beneficiaries
                </c:when>
                <c:otherwise>
                   Additional Beneficiaries / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
      </c:choose>
    </h2>

    <c:if test="${clientApplication.investorAccountType == 'corporateTrust' || clientApplication.investorAccountType == 'individualTrust'}">
      <c:if test="${not empty clientApplication.trust.trustMemberClass}">
        <div class="formBlock">
          <span class="emphasis">
            TRUST BENEFICIARY DETAILS
          </span>
        </div>
        <div class="formBlock">
          <span>
             ${clientApplication.trust.trustMemberClass}
          </span>
        </div>
      </c:if>
    </c:if>

    <c:forEach var="member" items="${members}">
      <div class="formBlock">
        <span class="emphasis">
           ${member.title} ${member.fullName}
        </span>
      </div>
      <div class="formBlock">
        <span> <label class="formLabelAppDetail formLabelStyleOne">Role </label>
          <span>
          <c:forEach var="role" items="${member.personRoles}">
             <%@ include file="investorRole.jsp" %>
          </c:forEach>
          <span>
        </span>
      </div>
      <c:if test="${not empty member.dateOfBirth}">
        <div class="formBlock">
          <span>
            <label class="formLabelAppDetail formLabelStyleOne">Date of Birth
            </label>
            <span>${member.dateOfBirth}</span>
          </span>
        </div>
      </c:if>

      <c:if test="${not empty member.gender}">
        <div class="formBlock">
          <span>
            <label class="formLabelAppDetail formLabelStyleOne">Gender
            </label>
            <span>${member.gender}</span>
          </span>
        </div>
      </c:if>

      <c:set var="personRolesList" value="${member.personRoles}" />
      <c:set var="isNotEligibleForTaxPurposes" value="false" />
      <c:set var="hasSingleRole" value="${fn:length(personRolesList) == 1}" />

       <c:if test="${hasSingleRole == 'true' && (personRolesList[0] eq 'Member' || personRolesList[0] eq 'Beneficiary')}">
              <c:set var="isNotEligibleForTaxPurposes" value="true" />
       </c:if>

      <c:if test="${isNotEligibleForTaxPurposes == 'false'}">
        <c:set var="crs" value ="${member}"/>
        <%@ include file="crsDetails.jsp" %>
      </c:if>

      <c:choose>
      <c:when test="${member.addresses != null && not empty member.addresses}">
          <c:forEach var="address" items="${member.addresses}">
            <div class="formBlock">
                  <c:if test="${address.domicile && not address.mailingAddress}">
                    <label class="formLabelAppDetail formLabelStyleOne">Residential
                      Address </label>
                   <span>${address.careOf} ${address.unitNumber}
                  ${address.floor}${address.streetNumber} ${address.streetName}
                  ${address.suburb} ${address.state} ${address.postcode}
                  ${address.country}</span>
              </c:if>
            </div>
          </c:forEach>
      </c:when>
      <c:otherwise>
          <c:forEach var="addressV2" items="${member.addressesV2}">
                     <div class="formBlock">
                           <c:if test="${addressV2.addressType == 'RESIDENTIAL'}">
                             <label class="formLabelAppDetail formLabelStyleOne">Residential
                               Address </label>
                            <span>${addressV2.addressDisplayText}</span>
                       </c:if>
                     </div>
          </c:forEach>
      </c:otherwise>
      </c:choose>
      <c:if test="${not empty member.idvs}">
        <div class="formBlock">
          <span>
            <label class="formLabelAppDetail formLabelStyleOne">Proof of identity
            </label>
            <span><c:choose><c:when test="${member.idvs =='Verified'}"> Verified </c:when><c:otherwise> Not Verified</c:otherwise></c:choose></span>
          </span>
        </div>
      </c:if>
    </c:forEach>
  </c:if>

  <c:if test="${clientApplication.containsNominatedInvestors}">
    <br/><h2 class="mainHeaderItem mainHeaderItemMod3">
      <c:choose>
         <c:when test="${clientApplication.investorAccountType == 'individualSMSF' || clientApplication.investorAccountType == 'newIndividualSMSF'}">
            Nominated Member(s)
         </c:when>
        <c:when test="${clientApplication.investorAccountType == 'corporateSMSF'}">
            <c:choose>
                <c:when test="${featureAML}">
                   <c:set var="additionalMember"  scope="request" value="false" />
                   Nominated Members,Shareholders and Controllers
                </c:when>
                <c:otherwise>
                   Nominated Shareholder / Member(s) / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'newCorporateSMSF'}">
            Nominated Shareholder / Member(s)
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'corporateTrust'}">
            <c:choose>
                <c:when test="${featureAML}">
                   Nominated Beneficiaries, Shareholders and Controllers
                </c:when>
                <c:otherwise>
                   Nominated Shareholder / Beneficiary / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'company'}">
            <c:choose>
                <c:when test="${featureAML}">
                   Nominated Shareholder(s) / Controllers
                </c:when>
                <c:otherwise>
                   Nominated Shareholder(s) / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${clientApplication.investorAccountType == 'individualTrust'}">
            <c:choose>
                <c:when test="${featureAML}">
                   Nominated Beneficiaries
                </c:when>
                <c:otherwise>
                   Nominated Beneficiaries / Responsible persons
                </c:otherwise>
            </c:choose>
        </c:when>
      </c:choose>
    </h2>
    <c:forEach var="nominatedMember" items="${nominatedMembers}">
      <c:if test="${nominatedMember.nominatedFlag}">
      <div class="formBlock">
        <span class="emphasis">
           ${nominatedMember.fullName}
        </span>
      </div>

       <div class="formBlock">
          <span> <label class="formLabelAppDetail formLabelStyleOne">Role </label>
            <span>
            <c:forEach var="role" items="${nominatedMember.personRoles}">
              <%@ include file="investorRole.jsp" %>
            </c:forEach>
            </span>
          </span>
        </div>
      </c:if>
    </c:forEach>
  </c:if>
</div>
