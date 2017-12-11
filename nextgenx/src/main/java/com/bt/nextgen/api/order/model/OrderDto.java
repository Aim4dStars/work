package com.bt.nextgen.api.order.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface OrderDto extends KeyedDto<OrderKey> {

    String getDisplayOrderId();

    DateTime getSubmitDate();

    String getOrigin();

    String getAccountKey();

    String getAccountName();

    String getOrderType();

    BigDecimal getAmount();

    String getStatus();

    String getAccountNumber();

    AssetDto getAsset();

    /**
     * @deprecated assetCode is referenced from the asset.
     *
     */
    @Deprecated
    String getAssetCode();

    Boolean getCancellable();

    String getLastTranSeqId();

    BigDecimal getQuantity();

    BigDecimal getPrice();

    @Override
    OrderKey getKey();

    Boolean getAmendable();

    Boolean getContractNotes();

    Boolean getExternal();

    /* Gets the brokerage for the investment*/
    BigDecimal getBrokerage();
}
