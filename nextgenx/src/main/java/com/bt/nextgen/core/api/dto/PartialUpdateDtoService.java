package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceErrors;

import java.util.Map;

public interface PartialUpdateDtoService<K, T extends KeyedDto<K>> extends KeyDtoService<K, T> {
    T partialUpdate(K key, Map<String, ? extends Object> partialUpdates, ServiceErrors serviceErrors);
}
