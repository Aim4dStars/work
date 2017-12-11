package com.bt.nextgen.api.branch.service;

import com.bt.nextgen.api.branch.model.BranchKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.model.KeyedDto;

public interface BranchDtoService extends FindByKeyDtoService<BranchKey,KeyedDto<BranchKey>> {
}
