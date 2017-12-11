<div class="sectionContentItem jq-contactDetailsWrap">
    <h2 class="mainHeaderItem mainHeaderItemMod3">
        Fees (optional)
    </h2>
    <p class="fees-message">
        All fees are GST inclusive.
    </p>
    <c:set var="containsLicenseeAdviceFee" value="false" />
    <c:forEach var="fees" items="${clientApplication.fees}">
        <c:choose>

            <c:when test="${fees.key =='estamount'}">
                <div class="formBlock">
						<span>
							<label class="emphasis formLabelAppDetail formLabelStyleOne">Adviser Establishment fee
                            </label>
							<span><fmt:formatNumber value="${fees.value}" currencySymbol="$" type="currency"/></span>
						</span>
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${fees.value.type == 'Licensee advice fee'}">
                    <c:set var="containsLicenseeAdviceFee" value="true" />
                </c:if>
                <c:choose>
                    <c:when test="${not empty fees.value.feesComponent}">

                        <div class="formBlock layoutMainHeaderItem">

                            <div class="formBlock">
                                <label class="emphasis formLabelAppDetail formLabelStyleOne">${fees.value.type}</label>
                            </div>

                            <c:forEach var="feeComponents" items= "${fees.value.feesComponent}">
                                <c:choose>
                                    <c:when test="${feeComponents.label =='Sliding scale fee component'}">
                                        <div class="formBlock"> <label class="formLabelAppDetail formLabelStyleOne">${feeComponents.label}</label></div>
                                        <div class="formBlock"> <label class="formLabelAppDetail formLabelStyleOne">Applies to:</label></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="formBlock"> <label class="formLabelAppDetail formLabelStyleOne">${feeComponents.label}</label></div>
                                    </c:otherwise>
                                </c:choose>

                                <c:forEach var="feeComponent" items= "${feeComponents}">
                                    <c:choose>
                                        <c:when test="${feeComponent.key !='slidingScaleFeeTier' and feeComponent.key !='label' and feeComponent.key !='valid'}">
                                            <c:if test="${feeComponent.value != null}">
                                                <label style="text-transform: capitalize;" class="labelText">${feeComponent.key} - ${feeComponent.value}<c:if test="${feeComponent.key =='deposit' or feeComponent.key =='employer' or feeComponent.key =='spouse' or feeComponent.key =='personal'}">%</c:if></label>
                                            </c:if>
                                        </c:when>
                                        <c:when test="${feeComponent.key =='slidingScaleFeeTier' && not empty feeComponent.value }">
                                            <div class="formBlock"> <label class="formLabelAppDetail formLabelStyleOne">Tiers/pa</label></div>
                                            <c:forEach var="slidingScale" items= "${feeComponent.value}">
                                                <c:choose>
                                                    <c:when test="${slidingScale.upperBound !=''}">
                                                        <label class="labelText">$${slidingScale.lowerBound} to $${slidingScale.upperBound} - ${slidingScale.percentage}%</label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label class="labelText">$${slidingScale.lowerBound} and above - ${slidingScale.percentage}%</label>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                            </c:forEach>

                        </div>

                    </c:when>
                    <c:when test="${fees.value.type == 'Adviser contribution fee'}">
                        <div class="formBlock">
                            <span>
                                <label class="emphasis formLabelAppDetail formLabelStyleOne">${fees.value.type}
                                </label>
                                <span>0.00%</span>
                            </span>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="formBlock">
                            <span>
                                <label class="emphasis formLabelAppDetail formLabelStyleOne">${fees.value.type}
                                </label>
                                <span><fmt:formatNumber value="0.00" currencySymbol="$" type="currency"/></span>
                            </span>
                        </div>
                    </c:otherwise>
                </c:choose>

            </c:otherwise>
        </c:choose>
        <div class="formBlock"></div>
    </c:forEach>

    <c:if test="${not containsLicenseeAdviceFee}">
        <div class="formBlock">
            <span>
                <label class="emphasis formLabelAppDetail formLabelStyleOne">Licensee advice fee</label>
                <span><fmt:formatNumber value="0.00" currencySymbol="$" type="currency"/></span>
            </span>
        </div>
        <div class="formBlock"></div>
    </c:if>

</div>