package com.bt.nextgen.api.account.v2.model.allocation.exposure;

import com.bt.nextgen.api.account.v2.model.allocation.AllocationDto;

import java.math.BigDecimal;
import java.util.Map;

@Deprecated
public interface AllocationByExposureDto extends AllocationDto {

    String getName();

    BigDecimal getBalance();

    BigDecimal getInternalBalance();

    BigDecimal getExternalBalance();

    BigDecimal getAccountPercent();

    Boolean getIsExternal();

    Map<String, BigDecimal> getAllocationDollar();

    Map<String, BigDecimal> getAssetAllocationPercentage();

    Map<String, BigDecimal> getAccountAllocationPercentage();

    String getSource();

}
