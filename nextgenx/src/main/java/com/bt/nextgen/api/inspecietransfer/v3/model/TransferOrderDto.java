package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.transfer.TransferOrderImpl;
import com.bt.nextgen.service.integration.transfer.TransferOrder;
import org.joda.time.DateTime;

import java.util.List;

public class TransferOrderDto extends BaseDto {

    private InspecieTransferKey key;
    private DateTime transferDate;
    private String transferType;
    private String transferStatus;
    private Boolean isCBO;
    private Boolean initiatedOnline;
    private TransferDest dest;
    private SponsorDetailsDto sponsorDetails;
    private List<TransferItemDto> transferItems;

    public TransferOrderDto() {
        super();
    }

    public TransferOrderDto(TransferOrder transferOrder, String transferStatus, TransferDest transferDest,
            SponsorDetailsDto sponsorDetails, List<TransferItemDto> transferItems) {
        super();

        this.key = new InspecieTransferKey(transferOrder.getAccountId(), transferOrder.getTransferId());
        this.transferDate = transferOrder.getTransferDate();
        this.transferType = transferOrder.getTransferType() == null ? null : transferOrder.getTransferType().name();
        this.transferStatus = transferStatus;
        this.isCBO = ((TransferOrderImpl) transferOrder).isChangeOfBeneficialOwnership();
        this.initiatedOnline = ((TransferOrderImpl) transferOrder).initiatedOnline();
        this.dest = transferDest;
        this.sponsorDetails = sponsorDetails;
        this.transferItems = transferItems;
    }

    public InspecieTransferKey getKey() {
        return key;
    }

    public DateTime getTransferDate() {
        return transferDate;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public Boolean getIsCBO() {
        return isCBO;
    }

    public Boolean getInitiatedOnline() {
        return initiatedOnline;
    }

    public TransferDest getDest() {
        return dest;
    }

    public SponsorDetailsDto getSponsorDetails() {
        return sponsorDetails;
    }

    public List<TransferItemDto> getTransferItems() {
        return transferItems;
    }

}