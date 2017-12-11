package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AccountDto extends WrapAccountDto {

    /** The account id. */
    private String accountId;

    /** The account name. */
    private String accountName;

    /** The account number. */
    private String accountNumber;

    /** The account type. */
    private String accountType;

    /** The account status. */
    private String accountStatus;

    /** The product. */
    private String product;

    /** The product id. */
    private String productId;

    /** The available cash. */
    private BigDecimal availableCash;

    /** The portfolio value. */
    private BigDecimal portfolioValue;

    /** The primary contact name. */
    private String primaryContactName;

    /** The primary contact number. */
    private String primaryContactNumber;

    /** The primary contact address. */
    private String primaryContactAddress;

    /** The adviser id. */
    private String adviserId;

    /** The adviser name. */
    private String adviserName;

    /** The adviser permission. */
    private String adviserPermission;

    /** The adviser mobile number. */
    private String adviserMobileNumber;

    /** The adviser dealer group. */
    private String adviserDealerGroup;

    /** The clients. */
    private List<ClientIdentificationDto> clients;

    /** Open Date for the Account */
    private DateTime openDate;

    /** The account type description, this may hold different values than accountType, particularly for Super accounts. */
    private String accountTypeDescription;

    /**
     * Instantiates a new account dto.
     *
     * @param key
     *            the key
     */
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

    /**
     * Gets the account name.
     *
     * @return the account name
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the account name.
     *
     * @param accountName
     *            the new account name
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Gets the account number.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the account number.
     *
     * @param accountNumber
     *            the new account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the account type.
     *
     * @return the account type
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * Sets the account type.
     *
     * @param accountType
     *            the new account type
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * Gets the account status.
     *
     * @return the account status
     */
    public String getAccountStatus() {
        return accountStatus;
    }

    /**
     * Sets the account status.
     *
     * @param accountStatus
     *            the new account status
     */
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    /**
     * Gets the adviser id.
     *
     * @return the adviser id
     */
    public String getAdviserId() {
        return adviserId;
    }

    /**
     * Sets the adviser id.
     *
     * @param adviserId
     *            the new adviser id
     */
    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    /**
     * Gets the adviser name.
     *
     * @return the adviser name
     */
    public String getAdviserName() {
        return adviserName;
    }

    /**
     * Sets the adviser name.
     *
     * @param adviserName
     *            the new adviser name
     */
    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    /**
     * Gets the adviser permission.
     *
     * @return the adviser permission
     */
    public String getAdviserPermission() {
        return adviserPermission;
    }

    /**
     * Sets the adviser permission.
     *
     * @param adviserPermission
     *            the new adviser permission
     */
    public void setAdviserPermission(String adviserPermission) {
        this.adviserPermission = adviserPermission;
    }

    /**
     * Gets the product.
     *
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * Sets the product.
     *
     * @param product
     *            the new product
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * Gets the available cash.
     *
     * @return the available cash
     */
    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    /**
     * Sets the available cash.
     *
     * @param availableCash
     *            the new available cash
     */
    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    /**
     * Gets the portfolio value.
     *
     * @return the portfolio value
     */
    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    /**
     * Sets the portfolio value.
     *
     * @param portfolioValue
     *            the new portfolio value
     */
    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    /**
     * Gets the primary contact name.
     *
     * @return the primary contact name
     */
    public String getPrimaryContactName() {
        return primaryContactName;
    }

    /**
     * Sets the primary contact name.
     *
     * @param primaryContactName
     *            the new primary contact name
     */
    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    /**
     * Gets the primary contact number.
     *
     * @return the primary contact number
     */
    public String getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    /**
     * Sets the primary contact number.
     *
     * @param primaryContactNumber
     *            the new primary contact number
     */
    public void setPrimaryContactNumber(String primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    /**
     * Gets the primary contact address.
     *
     * @return the primary contact address
     */
    public String getPrimaryContactAddress() {
        return primaryContactAddress;
    }

    /**
     * Sets the primary contact address.
     *
     * @param primaryContactAddress
     *            the new primary contact address
     */
    public void setPrimaryContactAddress(String primaryContactAddress) {
        this.primaryContactAddress = primaryContactAddress;
    }

    /**
     * Gets the adviser mobile number.
     *
     * @return the adviser mobile number
     */
    public String getAdviserMobileNumber() {
        return adviserMobileNumber;
    }

    /**
     * Sets the adviser mobile number.
     *
     * @param adviserMobileNumber
     *            the new adviser mobile number
     */
    public void setAdviserMobileNumber(String adviserMobileNumber) {
        this.adviserMobileNumber = adviserMobileNumber;
    }

    /**
     * Gets the adviser dealer group.
     *
     * @return the adviser dealer group
     */
    public String getAdviserDealerGroup() {
        return adviserDealerGroup;
    }

    /**
     * Sets the adviser dealer group.
     *
     * @param adviserDealerGroup
     *            the new adviser dealer group
     */
    public void setAdviserDealerGroup(String adviserDealerGroup) {
        this.adviserDealerGroup = adviserDealerGroup;
    }

    /**
     * Gets the product id.
     *
     * @return the product id
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the product id.
     *
     * @param productId
     *            the new product id
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Gets the clients.
     *
     * @return the clients
     */
    public List<ClientIdentificationDto> getClients() {
        return clients;
    }

    /**
     * Sets the clients.
     *
     * @param clients
     *            the new clients
     */
    public void setClients(List<ClientIdentificationDto> clients) {
        this.clients = clients;
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
