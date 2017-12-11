package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.RolloverInDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.stereotype.Service;

@Service
public interface RolloverInDtoService extends FindByKeyDtoService<RolloverKey, RolloverInDto>,
        SubmitDtoService<RolloverKey, RolloverInDto> {

    public RolloverInDto save(RolloverInDto keyedObject, ServiceErrors serviceErrors);

    public RolloverInDto discard(RolloverKey key, ServiceErrors serviceErrors);
}
