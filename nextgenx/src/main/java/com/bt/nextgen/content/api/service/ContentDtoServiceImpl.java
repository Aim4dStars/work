package com.bt.nextgen.content.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.cms.CmsEntry;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.core.util.SETTINGS;
import com.bt.nextgen.service.ServiceErrors;

@Service
class ContentDtoServiceImpl implements ContentDtoService
{
	@Autowired
	private CmsService cmsService;

	@Override
	public ContentDto find(ContentKey key, ServiceErrors serviceErrors)
	{
		ContentDto contentDto = null;
		CmsEntry content = cmsService.getRawContent(key.getContentId());
		if (!CmsService.MISSING.equals(content))
		{
			contentDto = new ContentDto(key, content);
		}
		return contentDto;
	}

	@Override
	public List <ContentDto> findAll(ServiceErrors serviceErrors)
	{
		final Collection<String> indexes = cmsService.getRawContentIndex();
		final List<ContentDto> result = new ArrayList<>(indexes.size());
		final boolean indexAsValues = SETTINGS.CONTENT_INDEX_VALUES.isTrue();
		for (String index : indexes)
		{
			final CmsEntry content = cmsService.getRawContent(index);
			if (indexAsValues && content.getMetaData("contentType") == null)
			{
				result.add(new ContentDto(index, "Content " + index));
			}
			else if (!CmsService.MISSING.equals(content))
			{
				result.add(new ContentDto(index, content));
			}
		}
		return result;
	}
}
