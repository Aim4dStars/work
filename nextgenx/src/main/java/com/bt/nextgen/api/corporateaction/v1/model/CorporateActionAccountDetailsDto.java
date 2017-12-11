package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;


/**
 * Corporate action account details Dto object.
 */
public class CorporateActionAccountDetailsDto extends BaseDto implements KeyedDto<CorporateActionDtoKey> {
    private String portfolio;

    @JsonView(JsonViews.Write.class)
    private String positionId;

    private String clientId;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String clientAddress;

    @JsonView(JsonViews.Write.class)
    private String accountId;

    @JsonView(JsonViews.Write.class)
    private String accountKey;

    private String accountName;
    private String accountType;
    private String adviserName;
    private CorporateActionAccountParticipationStatus electionStatus;
    private Integer holding;
    private Integer originalHolding;
    private BigDecimal cash;

    private CorporateActionAccountElectionsDto savedElections;
    private CorporateActionAccountElectionsDto submittedElections;

    @JsonView(JsonViews.Write.class)
    private CorporateActionSelectedOptionsDto selectedElections;

    private BigDecimal portfolioValue;

    // Fields for the Participation report
    private Integer transactionNumber;
    private String transactionDescription;
    private CorporateActionTransactionStatus transactionStatus;
    private boolean pendingSell;
    private boolean trusteeApproval;

    // ROA notification
    private CorporateActionNotification notification;

    public CorporateActionAccountDetailsDto() {
        // Empty constructor
    }

    public CorporateActionAccountDetailsDto(String dgImId, CorporateActionAccountDetailsDtoParams params) {
        super();
        this.portfolio = params.getPortfolioName();
        this.clientId = params.getClientId();
        this.clientName = params.getClientName();
        this.clientPhone = params.getClientPhone();
        this.clientEmail = params.getClientEmail();
        this.clientAddress = params.getClientAddress();
        this.accountId = params.getAccountId();
        this.accountName = params.getAccountName();
        this.accountType = params.getAccountType();
        this.adviserName = params.getAdviserName();
        this.electionStatus = params.getElectionStatus();
        this.holding = params.getHolding();
        this.originalHolding = params.getOriginalHolding();
        this.cash = params.getCash();
        this.savedElections = params.getSavedElections();
        this.submittedElections = params.getSubmittedElections();
        this.transactionNumber = params.getTransactionNumber();
        this.transactionDescription = params.getTransactionDescription();
        this.transactionStatus = params.getTransactionStatus();
        this.positionId = params.getPositionId();
        this.accountKey = params.getAccountKey();
        this.portfolioValue = params.getPortfolioValue();
        this.pendingSell = params.isPendingSell();
        this.notification = params.getNotification();
        this.trusteeApproval = params.isTrusteeApproval();
    }

    // Used by JSON mapper in API controller
    public CorporateActionAccountDetailsDto(String clientId, String accountId, String positionId,
                                            CorporateActionSelectedOptionsDto selectedElections) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.positionId = positionId;
        this.selectedElections = selectedElections;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public String getPositionId() {
        return positionId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public CorporateActionAccountParticipationStatus getElectionStatus() {
        return electionStatus;
    }

    public Integer getHolding() {
        return holding;
    }

    public Integer getOriginalHolding() {
        return originalHolding;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public CorporateActionAccountElectionsDto getSavedElections() {
        return savedElections;
    }

    public CorporateActionAccountElectionsDto getSubmittedElections() {
        return submittedElections;
    }

    public CorporateActionSelectedOptionsDto getSelectedElections() {
        return selectedElections;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public Integer getTransactionNumber() {
        return transactionNumber;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public CorporateActionTransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public boolean isPendingSell() {
        return pendingSell;
    }

    public boolean isTrusteeApproval() {
        return trusteeApproval;
    }

    public CorporateActionNotification getNotification() {
        return notification;
    }

    @Override
    public CorporateActionDtoKey getKey() {
        return null;
    }
}
