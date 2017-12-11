package com.bt.nextgen.api.inspecietransfer.v2.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @deprecated Use V3
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
