package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.service.avaloq.account.TaxLiability;
import com.bt.nextgen.service.integration.account.OnboardingDetails;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class WrapAccountDetailDto extends WrapAccountDto {

    public WrapAccountDetailDto() {
    }

    public WrapAccountDetailDto(AccountKey key, DateTime accountStartDate, DateTime closureDate) {
        super(key, accountStartDate, closureDate);
    }

    private String accountType; // accountStructureType

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

    private DateTime registeredSinceDate;

    private List<OnboardingDetails> onboardingDetails;

    private List<InitialInvestmentAssetDto> initialInvestmentAssets;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

    public List<InitialInvestmentAssetDto> getInitialInvestmentAssets() {
        return initialInvestmentAssets;
    }

    public void setInitialInvestmentAssets(List<InitialInvestmentAssetDto> initialInvestmentAssets) {
        this.initialInvestmentAssets = initialInvestmentAssets;
    }

    public List<OnboardingDetails> getOnboardingDetails() {
        return onboardingDetails;
    }

    public void setOnboardingDetails(List<OnboardingDetails> onboardingDetails) {
        this.onboardingDetails = onboardingDetails;
    }
}
