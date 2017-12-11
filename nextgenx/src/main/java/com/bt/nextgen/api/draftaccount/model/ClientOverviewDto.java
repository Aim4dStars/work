package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.core.security.encryption.EncodedString;

import java.math.BigDecimal;

public class ClientOverviewDto extends BaseDto {

    private final String accountId;
    private final String encodedAccountNumber;
    private final String accountName;
    private final String accountNumber;
    private String superAccountSubType;
    private final String accountType;
    private final Status accountStatus;
    private final String product;
    private final BigDecimal availableCash;
    private final BigDecimal portfolioValue;
    private final String clientId;
    private final boolean direct;
    private final String userExperience;
    /**
     * This is the feature key to be used on UI for the toggles
     */
     private final String typeId;

    public ClientOverviewDto(WrapAccount account, String accountType, Status accountStatus, String product, BigDecimal availableCash,
                             BigDecimal portfolioValue, String encodedClientId, UserExperience userExperience, String typeId) {
        this.accountId = EncodedString.fromPlainText(account.getAccountKey().getId()).toString();
        this.encodedAccountNumber = EncodedString.fromPlainText(account.getAccountNumber()).toString();
        this.accountName = account.getAccountName();
        this.accountNumber = account.getAccountNumber();
        this.accountType = accountType;
        this.superAccountSubType = account.getSuperAccountSubType() != null ? account.getSuperAccountSubType().getIntlId() : null;
        this.accountStatus = accountStatus;
        this.product = product;
        this.availableCash = availableCash;
        this.portfolioValue = portfolioValue;
        this.clientId = encodedClientId;
        this.direct = UserExperience.DIRECT.equals(userExperience);
        this.userExperience = userExperience == null ? "" : userExperience.getDisplayName();
        this.typeId = typeId;
    }

    public static enum Status {
        PENDING_APPROVAL,
        PENDING_YOUR_APPROVAL,
        CLOSED,
        ACTIVE,
        FUND_ESTABLISHMENT_IN_PROGRESS,
        UNKNOWN
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEncodedAccountNumber() {
        return encodedAccountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getSuperAccountSubType() {
        return superAccountSubType;
    }

    public void setSuperAccountSubType(String superAccountSubType) {
        this.superAccountSubType = superAccountSubType;
    }

    public String getAccountStatus() {
        return accountStatus.toString();
    }

    public String getProduct() {
        return product;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isDirect() {
        return direct;
    }

    public String getUserExperience() {
        return userExperience;
    }

    public String getTypeId() {
        return typeId;
    }
}
