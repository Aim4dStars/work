package com.bt.nextgen.service.integration.transfer;

import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.order.OrderStatus;
import org.joda.time.DateTime;

import java.util.List;

public interface TransferOrder {

    public String getTransferId();

    public String getAccountId();

    public String getDestContainerId();

    public TransferType getTransferType();

    public SponsorDetails getSponsorDetails();

    public BeneficialOwnerChangeStatus getChangeOfBeneficialOwnership();

    public OrderStatus getStatus();

    public List<TransferItem> getTransferItems();

    public DateTime getTransferDate();

    public Origin getMedium();

}
