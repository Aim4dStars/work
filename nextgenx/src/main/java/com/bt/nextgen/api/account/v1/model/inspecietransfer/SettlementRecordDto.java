package com.bt.nextgen.api.account.v1.model.inspecietransfer;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @deprecated Use V2
 */
@Deprecated
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = SettlementRecordDtoImpl.class, name = "SettlementRecordDtoImpl"),
        @JsonSubTypes.Type(value = TransferAssetDtoImpl.class, name = "TransferAssetDtoImpl") })
public interface SettlementRecordDto {

    public String getTransferId();

    public String getAssetId();

    public String getAssetCode();

    public BigDecimal getQuantity();

    public BigDecimal getAmount();

    public String getTransferStatus();

    public DateTime getTransferDate();
}
