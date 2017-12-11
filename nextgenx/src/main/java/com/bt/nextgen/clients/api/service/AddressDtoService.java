package com.bt.nextgen.clients.api.service;

import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.clients.api.model.AddressKey;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;

public interface AddressDtoService extends SearchByKeyDtoService<AddressKey, AddressDto> {
    public AddressDto validateAustralianAddress(AddressDto addressDto);
}
