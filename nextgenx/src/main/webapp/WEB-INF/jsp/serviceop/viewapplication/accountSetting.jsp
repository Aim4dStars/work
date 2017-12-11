<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Account settings
    </h2>
	<div class="formBlock">
     <span class="emphasis">
    <c:choose>
    <c:when test="${clientApplication.investorAccountType == 'individual' || clientApplication.investorAccountType == 'joint' || clientApplication.investorAccountType == 'company' || clientApplication.investorAccountType == 'superPension'}">
           Investor(s)
    </c:when>
    <c:when test="${clientApplication.investorAccountType == 'individualSMSF' || clientApplication.investorAccountType == 'individualTrust'|| clientApplication.investorAccountType == 'newIndividualSMSF'}">
			Trustee(s)        
   </c:when>
	<c:when test="${clientApplication.investorAccountType == 'corporateSMSF' || clientApplication.investorAccountType == 'corporateTrust' || clientApplication.investorAccountType == 'newCorporateSMSF'}">
			Director(s)
	</c:when>
</c:choose>
	</span>
		</div>

    <c:forEach var="setting" items="${clientApplication.accountSettings.personRelations}">
        <c:if test="${!setting.adviser}">
            <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">${setting.name}<br>
                        <c:if test="${setting.primaryContactPerson}"> (Primary Contact Person)<br> </c:if>
                        <c:if test="${setting.approver}"> (Approver)<br> </c:if>
                        <c:if test="${not empty setting.personRoles}">
                            <span>(<c:forEach var="role" items="${setting.personRoles}">
                                ${role}&nbsp;
                            </c:forEach>)</span>
                        </c:if>
                    </label>
                    <span>${setting.permissions}</span>
                </span>
            </div>
        </c:if>
    </c:forEach>

    <div class="formBlock">
        <span class="emphasis">
           Professionals
        </span>
    </div>
    <c:forEach var="setting" items="${clientApplication.accountSettings.personRelations}">
        <c:if test="${setting.adviser}">
            <div class="formBlock">
                <span>
                <label class="formLabelAppDetail formLabelStyleOne">${setting.name} <c:if test="${setting.primaryContactPerson}"> (Primary Contact Person) </c:if>
                </label>
                <span>${setting.permissions}</span>
                </span>
            </div>
        </c:if>
    </c:forEach>

  <c:if test="${!(clientApplication.investorAccountType == 'superAccumulation'|| clientApplication.investorAccountType == 'superPension')}">
     <c:if test="${clientApplication.accountSettings.powerOfAttorney != null}">
          <div class="formBlock">
                  <span class="emphasis">
                    Power of attorney
                  </span>
          </div>
             <div class="formBlock">
         <span>
             <label class="formLabelAppDetail formLabelStyleOne">Adviser holds power of attorney
             </label>
             <span>${clientApplication.accountSettings.powerOfAttorney}</span>
         </span>
             </div>
     </c:if>
  </c:if>


</div>