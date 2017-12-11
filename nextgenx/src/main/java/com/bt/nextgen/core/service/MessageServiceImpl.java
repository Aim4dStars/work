package com.bt.nextgen.core.service;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.ApplicationContextProvider;

public class MessageServiceImpl implements MessageService
{
	@Override
	public String lookup(String messageId)
	{
		CmsService cmsService=ApplicationContextProvider.getApplicationContext().getBean(CmsService.class);
		return cmsService.getContent(messageId);
	}
}
