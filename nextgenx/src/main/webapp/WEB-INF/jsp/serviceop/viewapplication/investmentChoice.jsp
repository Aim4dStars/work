<div class="sectionContentItem jq-contactDetailsWrap">
  <h2 class="mainHeaderItem mainHeaderItemMod3">
    Investment choice
  </h2>
  <div class="formBlock">
        <span>
            <label class="formLabelAppDetail formLabelStyleOne">Managed Portfolio</label>
            <span>${clientApplication.investmentChoice.managedPortfolio}</span>
           </span>
  </div>
<c:choose>
    <c:when test="${clientApplication.investmentChoice.initialInvestmentAmount != null}">
          <div class="formBlock">
                <span>
                    <label class="formLabelAppDetail formLabelStyleOne">Initial investment amount</label>
                  <span><fmt:formatNumber value="${clientApplication.investmentChoice.initialInvestmentAmount}" currencySymbol="$" type="currency"/></span>
                </span>
          </div>
        </c:when>
    </c:choose>
</div>

