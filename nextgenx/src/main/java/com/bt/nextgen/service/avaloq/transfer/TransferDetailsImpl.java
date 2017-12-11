package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.bt.nextgen.service.integration.transfer.TransferType;

import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class TransferDetailsImpl implements TransferDetails, TransactionResponse {
    public static final String XML_HEADER = "//data/";

    @ServiceElement(xpath = "//data/container/cont_id/annot/ctx/id")
    private String destContainerId;

    private String destAssetId;

    @ServiceElement(xpath = XML_HEADER + "xfer_type/in_specie_xfer_type/val", staticCodeCategory = "INSPECIE_TRANSFER_TYPE")
    private TransferType transferType;

    @ServiceElement(xpath = XML_HEADER + "xfer_type/xfer_chg_benef_id/val", staticCodeCategory = "CHANGE_BENEFICIAL_OWNERSHIP")
    private BeneficialOwnerChangeStatus changeOfBeneficialOwnership;

    @ServiceElementList(xpath = "//data/settle_rec_list/settle_rec", type = InspecieAsset.class)
    private List<InspecieAsset> transferAssets;

    @ServiceElement(xpath = "//data/tax_parcel_list/tax_parcel", type = TaxParcelImpl.class)
    private List<TaxParcel> taxParcels;

    @ServiceElement(xpath = "//data/sponsor", type = SponsorDetailsImpl.class)
    private SponsorDetails sponsorDetails;

    private AccountKey accountKey;

    @ServiceElement(xpath = "//data/doc/val")
    private String transferId;

    @ServiceElement(xpath = XML_HEADER + "ui_wf_status/val", staticCodeCategory = "ORDER_STATUS")
    private OrderStatus status;

    @ServiceElement(xpath = "//rsp/valid/err_list/err | //rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> warnings;

    private List<ValidationError> validationErrors;

    public TransferDetailsImpl() {

    }

    public TransferDetailsImpl(String transferId, TransferType transferType,
            BeneficialOwnerChangeStatus changeOfBeneficialOwnership, List<InspecieAsset> transferAssets) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.changeOfBeneficialOwnership = changeOfBeneficialOwnership;
        this.transferAssets = transferAssets;
    }

    public TransferDetailsImpl(String transferId, TransferType transferType, boolean isCbo, List<InspecieAsset> transferAssets) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.changeOfBeneficialOwnership = BeneficialOwnerChangeStatus.YES;
        if (!isCbo) {
            this.changeOfBeneficialOwnership = BeneficialOwnerChangeStatus.NO;
        }
        this.transferAssets = transferAssets;
    }

    // public void setContainer(ContainerGroup container) {
    // this.container = container;
    // }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public BeneficialOwnerChangeStatus getChangeOfBeneficialOwnership() {
        return changeOfBeneficialOwnership;
    }

    public void setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus changeOfBeneficialOwnership) {
        this.changeOfBeneficialOwnership = changeOfBeneficialOwnership;
    }

    public void setTaxParcels(List<TaxParcel> taxParcels) {
        this.taxParcels = taxParcels;
    }

    public void setTransferAssets(List<InspecieAsset> transferAssets) {
        this.transferAssets = transferAssets;
    }

    // @Override
    // public ContainerGroup getContainer() {
    // return container;
    // }

    @Override
    public TransferType getTransferType() {
        return transferType;
    }

    @Override
    public List<TaxParcel> getTaxParcels() {
        return taxParcels;
    }

    @Override
    public List<InspecieAsset> getTransferAssets() {
        return transferAssets;
    }

    public SponsorDetails getSponsorDetails() {
        return sponsorDetails;
    }

    public void setSponsorDetails(SponsorDetails sponsorDetails) {
        this.sponsorDetails = sponsorDetails;
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

    public String getLocListItem(Integer index) {
        if (transferAssets != null) {
            InspecieAsset inAsset = transferAssets.get(index);
            return inAsset.getAssetId();
        }
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        int i = 1;
        if (transferAssets != null) {
            for (InspecieAsset asset : transferAssets) {
                if (asset.getAssetId().equals(itemId)) {
                    return BigInteger.valueOf(i);
                }
                i++;
            }
        }
        return null;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
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

    @Override
    public String getDestContainerId() {
        return destContainerId;
    }

    @Override
    public String getDestAssetId() {
        return destAssetId;
    }

    public void setDestContainerId(String destContainerId) {
        this.destContainerId = destContainerId;
    }

    public void setDestAssetId(String destAssetId) {
        this.destAssetId = destAssetId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}
