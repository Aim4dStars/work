package com.bt.nextgen.api.country.service;

import com.bt.nextgen.api.country.model.CountryCode;
import com.bt.nextgen.api.country.model.CountryDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * Service interface for fetching countries (usually from Avaloq static data).
 * Created by M013938 on 15/10/2015.
 */
public interface CountryDtoService extends FindAllDtoService<CountryDto>, FindByKeyDtoService<CountryCode, CountryDto> {
}
