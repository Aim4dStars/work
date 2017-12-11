package com.bt.nextgen.api.user.v1.service;


import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.model.UserNoticesDtoKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

/**
 * This service finds all the updates available to the user
 */
public interface UserNoticesDtoService extends FindAllDtoService<UserNoticesDto>, UpdateDtoService<UserNoticesDtoKey, UserNoticesDto> {
}
