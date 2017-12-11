package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import java.util.List;

public class InspecieTransferDtoImpl extends BaseDto implements InspecieTransferDto {

    @JsonView(JsonViews.Write.class)
    private InspecieTransferKey key;

    @JsonView(JsonViews.Write.class)
    private String orderType;

    @JsonView(JsonViews.Write.class)
    private String transferType;

    @JsonView(JsonViews.Write.class)
    private Boolean isCBO;

    @JsonView(JsonViews.Write.class)
    private DateTime transferDate;

    @JsonView(JsonViews.Write.class)
    private AccountKey sourceAccountKey;

    @JsonView(JsonViews.Write.class)
    private AccountKey targetAccountKey;

    @JsonView(JsonViews.Write.class)
    private String targetContainerId;

    @JsonView(JsonViews.Write.class)
    private String sourceContainerId;

    @JsonView(JsonViews.Write.class)
    private String targetAssetId;

    @JsonView(JsonViews.Write.class)
    private List<TransferAssetDto> transferAssets;

    @JsonView(JsonViews.Write.class)
    private List<TransferPreferenceDto> transferPreferences;

    @JsonView(JsonViews.Write.class)
    private String action;

    @JsonView(JsonViews.Write.class)
    private List<DomainApiErrorDto> warnings;

    @JsonView(JsonViews.Write.class)
    private boolean isFullClose;

    @JsonView(JsonViews.Write.class)
    private String incomePreference;

    public InspecieTransferDtoImpl() {
        super();
    }

    public InspecieTransferDtoImpl(String accountId, TransferGroupDetails details, List<TransferAssetDto> transferAssets,
            List<TransferPreferenceDto> transferPreferences, List<DomainApiErrorDto> warnings) {
        super();

        this.key = new InspecieTransferKey(accountId, details.getTransferId());
        this.orderType = details.getOrderType() == null ? "" : details.getOrderType().getDisplayName();
        this.transferType = details.getExternalTransferType() == null ? "" : details.getExternalTransferType().getDisplayName();
        this.isCBO = BeneficialOwnerChangeStatus.YES.equals(details.getChangeOfBeneficialOwnership());
        this.transferDate = details.getTransferDate();
        this.targetContainerId = details.getDestContainerId();
        this.targetAssetId = details.getDestAssetId();
        this.transferAssets = transferAssets;
        this.transferPreferences = transferPreferences;
        this.warnings = warnings;
        this.incomePreference = details.getIncomePreference() == null ? null : details.getIncomePreference().getIntlId();

        OrderType otype = details.getOrderType();
        this.orderType = otype != null ? otype.getDisplayName() : null;

        TransferType ttype = details.getExternalTransferType();
        this.transferType = ttype != null ? ttype.getDisplayName() : null;

        com.bt.nextgen.service.integration.account.AccountKey source = details.getSourceAccountKey();
        this.sourceAccountKey = new AccountKey(source != null ? source.getId() : null);

        com.bt.nextgen.service.integration.account.AccountKey target = details.getTargetAccountKey();
        this.targetAccountKey = new AccountKey(target != null ? target.getId() : null);
    }

    @Override
    public InspecieTransferKey getKey() {
        return key;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    @Override
    public String getTransferType() {
        return transferType;
    }

    @Override
    public DateTime getTransferDate() {
        return transferDate;
    }

    @Override
    public AccountKey getSourceAccountKey() {
        return sourceAccountKey;
    }

    @Override
    public AccountKey getTargetAccountKey() {
        return targetAccountKey;
    }

    @Override
    public String getSourceContainerId() {
        return sourceContainerId;
    }

    @Override
    public String getTargetContainerId() {
        return targetContainerId;
    }

    @Override
    public String getTargetAssetId() {
        return targetAssetId;
    }

    @Override
    public Boolean getIsCBO() {
        return isCBO;
    }

    @Override
    public List<TransferAssetDto> getTransferAssets() {
        return transferAssets;
    }

    @Override
    public List<TransferPreferenceDto> getTransferPreferences() {
        return transferPreferences;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    @Override
    public boolean getIsFullClose() {
        return isFullClose;
    }

    @Override
    public String getIncomePreference() {
        return incomePreference;
    }

    @Override
    public void setKey(InspecieTransferKey key) {
        this.key = key;
    }

    public void setFullClose(boolean isFullClose) {
        this.isFullClose = isFullClose;
    }

    public void setSourceAccountKey(AccountKey sourceAccountKey) {
        this.sourceAccountKey = sourceAccountKey;
    }

    public void setTargetAccountKey(AccountKey targetAccountKey) {
        this.targetAccountKey = targetAccountKey;
    }

    public void setTargetContainerId(String targetContainerId) {
        this.targetContainerId = targetContainerId;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public void setSourceContainerId(String sourceContainerId) {
        this.sourceContainerId = sourceContainerId;
    }

    @Override
    public boolean containsValidationWarningOnly() {
        if (this.getWarnings() != null) {
            for (DomainApiErrorDto errDto : this.getWarnings()) {
                String errType = errDto.getErrorType();
                if (DomainApiErrorDto.ErrorType.ERROR.toString().equals(errType)) {
                    return false;
                }
            }
        }
        return true;
    }
}
