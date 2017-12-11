package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.model.DirectTrackingDto;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;

public interface DirectTrackingDtoService extends FindByKeyDtoService<ClientApplicationKey, DirectTrackingDto> {
}
