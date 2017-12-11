package com.bt.nextgen.api.userpreference.service;

import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

public interface UserPreferenceDtoService extends SearchByKeyDtoService<UserPreferenceDtoKey, UserPreferenceDto>,
    UpdateDtoService<UserPreferenceDtoKey, UserPreferenceDto> {

}
