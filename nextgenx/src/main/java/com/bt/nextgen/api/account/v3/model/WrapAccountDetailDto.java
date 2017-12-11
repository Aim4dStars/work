package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.service.avaloq.account.TaxLiability;
import com.bt.nextgen.service.integration.account.OnboardingDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WrapAccountDetailDto extends WrapAccountDto {

    private String accountType; // accountStructureType

    private String superAccountSubType;

    private DateTime signDate;

    private String accountStatus;

    private String accountName;

    private String accountNumber;

    private String cGTLMethod;

    private String cGTLMethodId;

    private InvestorDto primaryContact;

    private String loggedInClientId;

    private BrokerDto adviser;

    private String bsb;

    private String billerCode;

    private String modificationSeq;

    private String taxLiability;

    private MigrationDetailsDto migrationDetails;

    private TaxAndPreservationDetailsDto taxAndPreservationDetails;

    private PensionDetailsDto pensionDetails;

    private BigDecimal adminFeeRate;

    /**
     * This is also called badges
     */
    private ProductDto product;

    private List<LinkedAccountDto> linkedAccounts;

    private List<PersonRelationDto> settings;

    private List<InvestorDto> owners;

    private BigDecimal minCashAmount;

    private boolean hasMinCash;

    private String subscriptionType;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<InitialInvestmentDto> initialInvestments;

    private DateTime registeredSinceDate;

    private List<OnboardingDetails> onboardingDetails;

    private AccountantDto accountant;

    private String personalBillerCode;

    private String spouseBillerCode;

    private boolean inTransition;

    private boolean hasIhin;

    private String ihin;

    private String statementPref;

    private String cmaStatementPref;


    /**
     * This is the feature key to be used on UI for the toggles
     */
    private String typeId;

    public WrapAccountDetailDto() {
        // for use by json mapping
    }

    public WrapAccountDetailDto(AccountKey key, DateTime accountStartDate, DateTime closureDate) {
        super(key, accountStartDate, closureDate);
    }

    public String getPersonalBillerCode() {
        return personalBillerCode;
    }

    public void setPersonalBillerCode(String personalBillerCode) {
        this.personalBillerCode = personalBillerCode;
    }

    public String getSpouseBillerCode() {
        return spouseBillerCode;
    }

    public void setSpouseBillerCode(String spouseBillerCode) {
        this.spouseBillerCode = spouseBillerCode;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getSuperAccountSubType() {
        return superAccountSubType;
    }

    public void setSuperAccountSubType(String superAccountSubType) {
        this.superAccountSubType = superAccountSubType;
    }

    public DateTime getSignDate() {
        return signDate;
    }

    public void setSignDate(DateTime signDate) {
        this.signDate = signDate;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getcGTLMethod() {
        return cGTLMethod;
    }

    public void setcGTLMethod(String cGTLMethod) {
        this.cGTLMethod = cGTLMethod;
    }

    public InvestorDto getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(InvestorDto primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getLoggedInClientId() {
        return loggedInClientId;
    }

    public void setLoggedInClientId(String loggedInClient) {
        this.loggedInClientId = loggedInClient;
    }

    public BrokerDto getAdviser() {
        return adviser;
    }

    public void setAdviser(BrokerDto adviser) {
        this.adviser = adviser;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public List<LinkedAccountDto> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccountDto> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public List<InvestorDto> getOwners() {
        return owners;
    }

    public void setOwners(List<InvestorDto> owners) {
        this.owners = owners;
    }

    public String getBsb() {
        return bsb;
    }

    public void setBsb(String bsb) {
        this.bsb = bsb;
    }

    public String getBillerCode() {
        return billerCode;
    }

    public void setBillerCode(String billerCode) {
        this.billerCode = billerCode;
    }

    public List<PersonRelationDto> getSettings() {
        return settings;
    }

    public void setSettings(List<PersonRelationDto> settings) {
        this.settings = settings;
    }

    public String getModificationSeq() {
        return modificationSeq;
    }

    public void setModificationSeq(String modificationSeq) {
        this.modificationSeq = modificationSeq;
    }

    public String getcGTLMethodId() {
        return cGTLMethodId;
    }

    public void setcGTLMethodId(String cGTLMethodId) {
        this.cGTLMethodId = cGTLMethodId;
    }

    public String getTaxLiability() {
        return taxLiability;
    }

    public void setTaxLiability(String taxLiability) {
        this.taxLiability = taxLiability;
    }

    public MigrationDetailsDto getMigrationDetails() {
        return migrationDetails;
    }

    public void setMigrationDetails(MigrationDetailsDto migrationDetails) {
        this.migrationDetails = migrationDetails;
    }

    public TaxAndPreservationDetailsDto getTaxAndPreservationDetails() {
        return taxAndPreservationDetails;
    }

    public void setTaxAndPreservationDetails(TaxAndPreservationDetailsDto taxAndPreservationDetails) {
        this.taxAndPreservationDetails = taxAndPreservationDetails;
    }

    public PensionDetailsDto getPensionDetails() {
        return pensionDetails;
    }

    public void setPensionDetails(PensionDetailsDto pensionDetails) {
        this.pensionDetails = pensionDetails;
    }

    public BigDecimal getAdminFeeRate() {
        return adminFeeRate;
    }

    public void setAdminFeeRate(BigDecimal adminFeeRate) {
        this.adminFeeRate = adminFeeRate;
    }

    public Boolean getReinvestmentsAllowed() {
        return !TaxLiability.TFN_LIABLE.getName().equals(taxLiability)
                && !TaxLiability.NON_RESIDENT_LIABLE.getName().equals(taxLiability);
    }

    public BigDecimal getMinCashAmount() {
        return minCashAmount;
    }

    public void setMinCashAmount(BigDecimal minCashAmount) {
        this.minCashAmount = minCashAmount;
    }

    public boolean isHasMinCash() {
        return hasMinCash;
    }

    public void setHasMinCash(boolean hasMinCash) {
        this.hasMinCash = hasMinCash;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public DateTime getRegisteredSinceDate() {
        return registeredSinceDate;
    }

    public void setRegisteredSinceDate(DateTime registeredSinceDate) {
        this.registeredSinceDate = registeredSinceDate;
    }

    public AccountantDto getAccountant() {
        return accountant;
    }

    public void setAccountant(AccountantDto accountant) {
        this.accountant = accountant;
    }

    public List<OnboardingDetails> getOnboardingDetails() {
        return onboardingDetails;
    }

    public void setOnboardingDetails(List<OnboardingDetails> onboardingDetails) {
        this.onboardingDetails = onboardingDetails;
    }

    public List<InitialInvestmentDto> getInitialInvestments() {
        return initialInvestments;
    }

    public void setInitialInvestments(List<InitialInvestmentDto> initialInvestments) {
        this.initialInvestments = initialInvestments;
    }

    public boolean getInTransition() {
        return inTransition;
    }

    public void setInTransition(boolean inTransition) {
        this.inTransition = inTransition;
    }

    public boolean getHasIhin() {
        return hasIhin;
    }

    public void setHasIhin(boolean hasIhin) {
        this.hasIhin = hasIhin;
    }

    public String getIhin() {
        return ihin;
    }

    public void setIhin(String ihin) {
        this.ihin = ihin;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }


    public String getStatementPref() {
        return statementPref;
    }

    public void setStatementPref(String statementPref) {
        this.statementPref = statementPref;
    }

    public String getCmaStatementPref() {
        return cmaStatementPref;
    }

    public void setCmaStatementPref(String cmaStatementPref) {
        this.cmaStatementPref = cmaStatementPref;
    }
}
