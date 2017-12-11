package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public interface RolloverInDto extends KeyedDto<RolloverKey> {

    public List<RolloverDetailsDto> getRolloverDetails();

    public String getRolloverType();

    public List<DomainApiErrorDto> getWarnings();

}
