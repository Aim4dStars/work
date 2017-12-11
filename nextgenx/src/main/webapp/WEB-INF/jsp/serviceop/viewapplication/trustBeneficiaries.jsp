<div class="sectionContentItem jq-contactDetailsWrap">
                <h2 class="mainHeaderItem mainHeaderItemMod3">
                    Beneficiaries
                </h2>
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
                <div class="formBlock">
                    <span class="emphasis">
                       Nominated Beneficiaries
                    </span>
                </div>
                <div class="formBlock">
                    <label class="formLabelAppDetail formLabelStyleOne emphasis">Name</label>
                    <span class="emphasis">Beneficiary</span>
                </div>
                 <c:forEach var="beneficiary" items="${clientApplication.trust.beneficiaries}">
                        <c:set var="personRole" value="Account_isNotBeneficiary" />
                        <c:forEach var="role" items="${beneficiary.personRoles}">
                            <c:if test="${role == 'Beneficiary'}">
                                <c:set var="personRole" value="Beneficiary" />
                            </c:if>
                        </c:forEach>

                        <c:if test="${beneficiary.primaryRole =='TRUSTEE'}">
                            <div class="formBlock">
                                <span>
                                    <label class="formLabelAppDetail formLabelStyleOne">${beneficiary.title} ${beneficiary.fullName}
                                    </label>
                                    <c:choose>
                                        <c:when test="${personRole == 'Beneficiary'}">
                                        <span>Yes</span>
                                        </c:when>
                                        <c:otherwise>
                                        <span>No</span>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </c:if>
                 </c:forEach>
                 <div class="formBlock">
                                 <span class="emphasis">
                                    Additional Beneficiaries
                                 </span>
                 </div>
                  <c:forEach var="beneficiary" items="${clientApplication.trust.beneficiaries}">
                         <c:if test="${beneficiary.primaryRole =='BENEFICIARY'}">
                             <div class="formBlock">
                                 <span>
                                     <label class="formLabelAppDetail formLabelStyleOne">${beneficiary.title} ${beneficiary.fullName}
                                     </label>
                                 </span>
                             </div>
                         </c:if>
                  </c:forEach>
</div>