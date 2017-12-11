package com.bt.nextgen.content.api.service;

import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface ContentDtoService extends FindByKeyDtoService <ContentKey, ContentDto>, FindAllDtoService <ContentDto>
{

}
