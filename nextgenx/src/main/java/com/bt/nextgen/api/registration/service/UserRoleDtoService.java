package com.bt.nextgen.api.registration.service;

import com.bt.nextgen.api.registration.model.UserRoleDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;


public interface UserRoleDtoService extends SubmitDtoService<JobProfileIdentifier, UserRoleDto>
{

}
