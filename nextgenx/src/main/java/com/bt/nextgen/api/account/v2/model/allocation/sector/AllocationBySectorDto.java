package com.bt.nextgen.api.account.v2.model.allocation.sector;

import com.bt.nextgen.api.account.v2.model.allocation.AllocationDto;

import java.math.BigDecimal;

@Deprecated
public interface AllocationBySectorDto extends AllocationDto {
    String getAssetSector();

    String getName();

    BigDecimal getUnits();

    BigDecimal getBalance();

    BigDecimal getInternalBalance();

    BigDecimal getExternalBalance();

    BigDecimal getAllocationPercentage();

    Boolean getPending();

    Boolean getIsExternal();

    String getSource();
}
