package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class AccountDto extends WrapAccountDto {
    private String accountId;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private String accountSubType;
    private String accountStatus;
    private String product;
    private String productId;

    private BigDecimal availableCash;
    private BigDecimal portfolioValue;

    private String primaryContactName;
    private String primaryContactNumber;
    private String primaryContactAddress;

    private String adviserId;
    private String adviserName;
    private String adviserPermission;
    private String adviserMobileNumber;
    private String adviserDealerGroup;
    private BigDecimal minCashAmount;
    private boolean hasMinCash;
    private List<ClientIdentificationDto> clients;
    private boolean direct;

    /** The account type description, this may hold different values than accountType, particularly for Super accounts. */
    private String accountTypeDescription;

    // Randomly encoded account key for hyperlink in account details UX page.

    private String encodedAccountKey;

    /** Open Date for the Account */
    private DateTime openDate;

    public AccountDto(AccountKey key) {
        super(key, null, null);
    }

    // TODO account id is not a public field, it should never be shown to a
    // user.
    // and should never be encoded on a dto. I think you mean account number.
    /**
     * Gets the account id.
     * 
     * @return the account id
     * @deprecated should not be used
     */
    @Deprecated
    public String getAccountId() {
        return accountId;
    }

    // TODO account id is not a public field, it should never be shown to a
    // user.
    // and should never be unencoded on a dto. I think you mean account number.
    /**
     * Sets the account id.
     * 
     * @param accountId
     *            the new account id
     * @deprecated should not be used
     */
    @Deprecated
    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountSubType() {
        return accountSubType;
    }

    public void setAccountSubType(String accountSubType) {
        this.accountSubType = accountSubType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public String getAdviserPermission() {
        return adviserPermission;
    }

    public void setAdviserPermission(String adviserPermission) {
        this.adviserPermission = adviserPermission;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    public void setPrimaryContactNumber(String primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    public String getPrimaryContactAddress() {
        return primaryContactAddress;
    }

    public void setPrimaryContactAddress(String primaryContactAddress) {
        this.primaryContactAddress = primaryContactAddress;
    }

    public String getAdviserMobileNumber() {
        return adviserMobileNumber;
    }

    public void setAdviserMobileNumber(String adviserMobileNumber) {
        this.adviserMobileNumber = adviserMobileNumber;
    }

    public String getAdviserDealerGroup() {
        return adviserDealerGroup;
    }

    public void setAdviserDealerGroup(String adviserDealerGroup) {
        this.adviserDealerGroup = adviserDealerGroup;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public List<ClientIdentificationDto> getClients() {
        return clients;
    }

    public void setClients(List<ClientIdentificationDto> clients) {
        this.clients = clients;
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

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public String getEncodedAccountKey() {
        return encodedAccountKey;
    }

    public void setEncodedAccountKey(String encodedAccountKey) {
        this.encodedAccountKey = encodedAccountKey;
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(DateTime openDate) {
        this.openDate = openDate;
    }

    public String getAccountTypeDescription() {
        return accountTypeDescription;
    }

    public void setAccountTypeDescription(String accountTypeDescription) {
        this.accountTypeDescription = accountTypeDescription;
    }
}
