package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.transaction.TransactionErrorDetailsImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class TransferGroupDetailsImpl extends TransactionErrorDetailsImpl implements TransferGroupDetails, TransactionResponse {
    public static final String XML_HEADER = "//data/";

    @ServiceElement(xpath = XML_HEADER + "doc/val")
    private String transferId;

    @ServiceElement(xpath = XML_HEADER + "trade_date/val", converter = DateTimeConverter.class)
    private DateTime transferDate;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "order_type_id/val", staticCodeCategory = "ORDER_TYPE")
    private OrderType orderType;

    @ServiceElement(xpath = XML_HEADER + "pause_drawdown/val")
    private Integer drawdownDelayDays;

    @ServiceElement(xpath = XML_HEADER + "bp/src_bp_id/val")
    private String sourceAccountId;

    @ServiceElement(xpath = XML_HEADER + "bp/src_cont/full_close/val")
    private boolean closeAfterTransfer;

    @ServiceElement(xpath = XML_HEADER + "bp/src_cont/cont_id/val")
    private String sourceContainerId;

    @ServiceElement(xpath = XML_HEADER + "bp/trg_bp_id/val")
    private String targetAccountId;

    // Transfer target is an existing container (RMP or TMP).
    @ServiceElement(xpath = XML_HEADER + "bp/trg_cont/cont_id/val")
    private String destContainerId;

    // Destination of the transfer is a NEW container (RMP or TMP).
    @ServiceElement(xpath = XML_HEADER + "bp/trg_cont_id/mp_asset_id/val")
    private String destAssetId;

    @ServiceElement(xpath = XML_HEADER + "xfer_type/in_specie_xfer_type_id/val", staticCodeCategory = "INSPECIE_TRANSFER_TYPE")
    private TransferType externalTransferType;

    @ServiceElement(xpath = XML_HEADER + "xfer_type/xfer_chg_benef_id/val", staticCodeCategory = "CHANGE_BENEFICIAL_OWNERSHIP")
    private BeneficialOwnerChangeStatus changeOfBeneficialOwnership;

    @ServiceElement(xpath = XML_HEADER + "income_pref_id/val", staticCodeCategory = "INCOME_PREFERENCE")
    private IncomePreference incomePreference;

    @ServiceElementList(xpath = XML_HEADER + "asset_list/asset", type = TransferAssetImpl.class)
    private List<TransferAsset> transferAssets;

    @ServiceElementList(xpath = XML_HEADER + "mp_pref_list/mp_pref_item", type = ModelPreferenceImpl.class)
    private List<ModelPreferenceAction> preferenceList;

    @ServiceElement(xpath = "//rsp/valid/err_list/err | //rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> warnings;

    private List<ValidationError> validationErrors;

    public TransferGroupDetailsImpl() {
        super();
    }

    /**
     * Constructor for external transfer.
     * 
     * @param transferId
     * @param transferType
     * @param changeOfBeneficialOwnership
     */
    public TransferGroupDetailsImpl(String transferId, TransferType transferType,
            BeneficialOwnerChangeStatus changeOfBeneficialOwnership) {
        this.transferId = transferId;
        this.externalTransferType = transferType;
        this.changeOfBeneficialOwnership = changeOfBeneficialOwnership;
    }

    /**
     * Constructor for external-transfer.
     * 
     * @param transferId
     * @param transferType
     * @param isCbo
     */
    public TransferGroupDetailsImpl(String transferId, TransferType transferType, boolean isCbo) {
        this.transferId = transferId;
        this.externalTransferType = transferType;
        this.changeOfBeneficialOwnership = BeneficialOwnerChangeStatus.YES;
        if (!isCbo) {
            this.changeOfBeneficialOwnership = BeneficialOwnerChangeStatus.NO;
        }
    }

    public AccountKey getSourceAccountKey() {
        return AccountKey.valueOf(sourceAccountId);
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public AccountKey getTargetAccountKey() {
        return AccountKey.valueOf(targetAccountId);
    }

    public String getTargetAccountId() {
        return this.targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getDestContainerId() {
        return destContainerId;
    }

    public void setDestContainerId(String destContainerId) {
        this.destContainerId = destContainerId;
    }

    public String getDestAssetId() {
        return destAssetId;
    }

    public void setDestAssetId(String destAssetId) {
        this.destAssetId = destAssetId;
    }

    public List<ModelPreferenceAction> getPreferenceList() {
        return preferenceList;
    }

    public void setPreferenceList(List<ModelPreferenceAction> preferenceList) {
        this.preferenceList = preferenceList;
    }

    public void setExternalTransferType(TransferType transferType) {
        this.externalTransferType = transferType;
    }

    public BeneficialOwnerChangeStatus getChangeOfBeneficialOwnership() {
        return changeOfBeneficialOwnership;
    }

    public void setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus changeOfBeneficialOwnership) {
        this.changeOfBeneficialOwnership = changeOfBeneficialOwnership;
    }

    public void setTransferAssets(List<TransferAsset> transferAssets) {
        this.transferAssets = transferAssets;
    }

    @Override
    public TransferType getExternalTransferType() {
        return externalTransferType;
    }

    @Override
    public List<TransferAsset> getTransferAssets() {
        return transferAssets;
    }

    @Override
    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public List<TransactionValidation> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<TransactionValidation> warnings) {
        this.warnings = warnings;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public boolean isChangeOfBeneficialOwnership() {
        if (this.changeOfBeneficialOwnership != null) {
            switch (changeOfBeneficialOwnership) {
                case YES:
                    return true;
                case NO:
                default:
                    return false;
            }
        }
        return false;
    }

    public DateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(DateTime transferDate) {
        this.transferDate = transferDate;
    }

    public String getLocListItem(Integer index) {
        if (transferAssets != null) {
            TransferAsset asset = transferAssets.get(index);
            return asset.getAssetId();
        }
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        int i = 1;
        if (transferAssets != null) {
            for (TransferAsset asset : transferAssets) {
                if (asset.getAssetId().equals(itemId)) {
                    return BigInteger.valueOf(i);
                }
                i++;
            }
        }
        return null;
    }

    @Override
    public String getSourceContainerId() {
        return sourceContainerId;
    }

    public void setSourceContainerId(String sourceContainerId) {
        this.sourceContainerId = sourceContainerId;
    }

    @Override
    public boolean getCloseAfterTransfer() {
        return closeAfterTransfer;
    }

    public void setCloseAfterTransfer(boolean closeAfterTransfer) {
        this.closeAfterTransfer = closeAfterTransfer;
    }

    public Integer getDrawdownDelayDays() {
        return drawdownDelayDays;
    }

    public void setDrawdownDelayDays(Integer drawdownDelayDays) {
        this.drawdownDelayDays = drawdownDelayDays;
    }

    public IncomePreference getIncomePreference() {
        return incomePreference;
    }

    public void setIncomePreference(IncomePreference incomePreference) {
        this.incomePreference = incomePreference;
    }
}
