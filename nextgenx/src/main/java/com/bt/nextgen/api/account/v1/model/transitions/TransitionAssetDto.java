package com.bt.nextgen.api.account.v1.model.transitions;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by L069552 on 25/09/2015.
 */
public class TransitionAssetDto extends BaseDto implements KeyedDto<AccountKey> {

    /** Gives the account key **/
    private AccountKey accountKey;

    /** Gives the order id for the transfer **/
    private String orderId;

    /** Gives the quantity of assets transferred **/
    private BigDecimal quantity;

    /** Gives the consideration amount  **/
    private BigDecimal considerationAmt;

    /** Gives the  transition status for the asset**/
    private String transitionStatus;

    /** Gives the asset cluster **/
    private String assetCluster;


    /** Gives the asset code **/
    private String assetCode;

    /** Gives the account name **/
    private String accountName;

    /** Gives the product name**/
    private String productName;

    /** Gives the account type **/
    private String accountType;

    /** Gives the account number **/
    private String accountNumber;


    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public DateTime getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getConsiderationAmt() {
        return considerationAmt;
    }

    public void setConsiderationAmt(BigDecimal considerationAmt) {
        this.considerationAmt = considerationAmt;
    }

    public String getTransitionStatus() {
        return transitionStatus;
    }

    public void setTransitionStatus(String transitionStatus) {
        this.transitionStatus = transitionStatus;
    }

    private String assetName;

    private DateTime submittedTimestamp;

    public String getAssetCluster() {
        return assetCluster;
    }

    public void setAssetCluster(String assetCluster) {
        this.assetCluster = assetCluster;
    }

    public TransitionAssetDto(AccountKey accountKey){

        this.accountKey = accountKey;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

}
