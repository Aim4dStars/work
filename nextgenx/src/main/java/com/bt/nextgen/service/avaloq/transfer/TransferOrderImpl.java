package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TransferItem;
import com.bt.nextgen.service.integration.transfer.TransferOrder;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.util.List;

@ServiceBean(xpath = "doc", type = ServiceBeanType.CONCRETE)
public class TransferOrderImpl implements TransferOrder {

    // @NotNull
    private final static String XML_HEADER = "doc_head_list/doc_head/";

    // @NotNull
    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String transferId;

    @ServiceElement(xpath = XML_HEADER + "bp_id/val")
    private String accountId;

    @ServiceElement(xpath = XML_HEADER + "cont_id/val")
    private String destContainerId;

    @ServiceElement(xpath = XML_HEADER + "xfer_type_id/val", staticCodeCategory = "INSPECIE_TRANSFER_TYPE")
    private TransferType transferType;

    @ServiceElement(xpath = XML_HEADER + "xfer_cbo_type_id/val", staticCodeCategory = "CHANGE_BENEFICIAL_OWNERSHIP")
    private BeneficialOwnerChangeStatus changeOfBeneficialOwnership;

    // @NotNull
    @ServiceElement(xpath = XML_HEADER + "ui_wfs_id/val", staticCodeCategory = "ORDER_STATUS")
    private OrderStatus status;

    // @NotNull
    @ServiceElementList(xpath = XML_HEADER + "xfer_list/xfer", type = TransferItemImpl.class)
    private List<TransferItem> transferItems;

    @ServiceElement(xpath = XML_HEADER + "xfer_chess_pid/val")
    private String sponsorId;

    @ServiceElement(xpath = XML_HEADER + "xfer_cust/val")
    private String platformId;

    @ServiceElement(xpath = XML_HEADER + "xfer_srn_hin/val")
    private String investmentId;

    @ServiceElement(xpath = XML_HEADER + "order_date/val", converter = DateTimeTypeConverter.class)
    private DateTime transferDate;

    @ServiceElement(xpath = XML_HEADER + "medium_id/val", staticCodeCategory = "MEDIUM")
    private Origin medium;

    public TransferOrderImpl() {
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getDestContainerId() {
        return destContainerId;
    }

    public void setDestContainerId(String destContainerId) {
        this.destContainerId = destContainerId;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public BeneficialOwnerChangeStatus getChangeOfBeneficialOwnership() {
        return changeOfBeneficialOwnership;
    }

    public void setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus changeOfBeneficialOwnership) {
        this.changeOfBeneficialOwnership = changeOfBeneficialOwnership;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<TransferItem> getTransferItems() {
        return transferItems;
    }

    public void setTransferItems(List<TransferItem> transferItems) {
        this.transferItems = transferItems;
    }

    public SponsorDetails getSponsorDetails() {
        SponsorDetailsImpl s = new SponsorDetailsImpl();
        s.setInvestmentId(investmentId);
        s.setPlatformId(platformId);
        s.setSponsorId(sponsorId);

        return s;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public DateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(DateTime transferDate) {
        this.transferDate = transferDate;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }

    public Origin getMedium() {
        return medium;
    }

    public void setMedium(Origin medium) {
        this.medium = medium;
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

    public boolean initiatedOnline() {
        return this.medium != null && this.medium.equals(Origin.WEB_UI);
    }
}
