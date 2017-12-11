package com.bt.nextgen.api.account.v2.model.allocation;

import com.bt.nextgen.core.api.model.Dto;

@Deprecated
public interface AllocationDto extends Dto {

    String getName();

    Boolean getIsExternal();

    String getSource();

}
