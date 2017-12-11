package com.bt.nextgen.api.portfolio.v3.model.allocation;

import com.bt.nextgen.core.api.model.Dto;

public interface AllocationDto extends Dto {

    String getName();

    Boolean getIsExternal();

    String getSource();

}
