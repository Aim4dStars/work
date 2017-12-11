package com.bt.nextgen.api.inspecietransfer.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @deprecated Use V3
 */
@Deprecated
public class InspecieTransferDtoImpl extends BaseDto implements InspecieTransferDto {

    private String transferType;
    private SponsorDetailsDtoImpl sponsorDetails;
    private List<SettlementRecordDto> settlementRecords;
    private List<TaxParcelDto> taxParcels;
    private TransferDest dest;
    private InspecieTransferKey key;
    private Boolean isCBO;
    private String action;
    private List<DomainApiErrorDto> warnings;
    private DateTime transferDate;
    private String transferStatus;

    public InspecieTransferDtoImpl() {
        // default constructor
    }

    public InspecieTransferDtoImpl(String transferType, SponsorDetailsDtoImpl sponsorDetails,
            List<SettlementRecordDto> settlementRecords, String destContainerId, InspecieTransferKey key, Boolean isCBO,
            List<DomainApiErrorDto> warnings) {
        super();
        this.transferType = transferType;
        this.sponsorDetails = sponsorDetails;
        this.settlementRecords = settlementRecords;
        dest = new TransferDest(destContainerId, null, null, null, null);
        this.key = key;
        this.isCBO = isCBO;
        this.warnings = warnings;

    }

    @Override
    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    @Override
    public SponsorDetailsDtoImpl getSponsorDetails() {
        return sponsorDetails;
    }

    public void setSponsorDetails(SponsorDetailsDtoImpl sponsorDetails) {
        this.sponsorDetails = sponsorDetails;
    }

    @Override
    public List<SettlementRecordDto> getSettlementRecords() {
        return settlementRecords;
    }

    public void setSettlementRecords(List<SettlementRecordDto> settlementRecords) {
        this.settlementRecords = settlementRecords;
    }

    @Override
    public List<TaxParcelDto> getTaxParcels() {
        return taxParcels;
    }

    public void setTaxParcels(List<TaxParcelDto> taxParcels) {
        this.taxParcels = taxParcels;
    }

    @Override
    public String getDestContainerId() {
        return dest.getDestContainerId();
    }

    @Override
    public String getDestAssetId() {
        return dest.getAssetId();
    }

    public void setDestContainerId(String destContainerId) {
        if (dest == null) {
            dest = new TransferDest();
        }
        dest.setDestContainerId(destContainerId);
    }

    public void setDestAssetId(String destAssetId) {
        if (dest == null) {
            dest = new TransferDest();
        }
        dest.setAssetId(destAssetId);
    }

    @Override
    public InspecieTransferKey getKey() {
        return key;
    }

    @Override
    public Boolean getIsCBO() {
        return isCBO;
    }

    public void setIsCBO(Boolean isCBO) {
        this.isCBO = isCBO;
    }

    @Override
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public DateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(DateTime transferDate) {
        this.transferDate = transferDate;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public String getTransferStatus() {
        return transferStatus;
    }

    public TransferDest getDest() {
        return dest;
    }

    public void setDest(TransferDest dest) {
        this.dest = dest;
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
