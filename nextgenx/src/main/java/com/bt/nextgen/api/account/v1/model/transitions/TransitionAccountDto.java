package com.bt.nextgen.api.account.v1.model.transitions;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.Set;


/**
 * Created by L069552 on 16/09/2015.
 */
public class TransitionAccountDto extends BaseDto {

    /** Account Id  */
    private String accountId;

    /** Account Name. */
    private String accountName;

    /** Account Number. */
    private String accountNumber;

    /** Account Type */
    private String accountType;

    /** Product Name */
    private String product;

    /** Product Id */
    private String productId;

    /** Available Cash */
    private BigDecimal availableCash;

    /** Portfolio Value */
    private BigDecimal portfolioValue;

    /** Expected Cash Value */
    private BigDecimal expectedCash;

    /** Expected Asset Value */
    private BigDecimal expectedAssetValue;

    /** Account Status*/
    private String accountStatus;

    /** Transition Status */
    private String transitionStatus;


    /** Transfer Type */
    private String transferType;

    /** Broker Id  */
    private String brokerId;

    /** Broker Name  */
    private String brokerName;

    /** Adviser List  */
    private Set<String> adviserList;


    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getProduct() {
        return product;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public AccountKey getKey() {
        return new AccountKey(accountId);
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }


    public BigDecimal getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(BigDecimal expectedCash) {
        this.expectedCash = expectedCash;
    }

    public BigDecimal getExpectedAssetValue() {
        return expectedAssetValue;
    }

    public void setExpectedAssetValue(BigDecimal expectedAssetValue) {
        this.expectedAssetValue = expectedAssetValue;
    }

    public AccountKey getAccountkey() {
        return accountkey;
    }

    private AccountKey accountkey;


    public TransitionAccountDto(AccountKey key) {

        this.accountkey = key;
    }

    public String getTransitionStatus() {
        return transitionStatus;
    }

    public void setTransitionStatus(String transitionStatus) {
        this.transitionStatus = transitionStatus;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getBrokerName() {
        return brokerName;
    }
    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }


    public Set<String> getAdviserList() {return adviserList;}
    public void setAdviserList(Set<String> adviserList) {this.adviserList = adviserList;}

}
