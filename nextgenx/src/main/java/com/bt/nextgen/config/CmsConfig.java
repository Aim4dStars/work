package com.bt.nextgen.config;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.cms.service.CmsServiceXmlResourceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.util.SETTINGS.CMS_INDEX;

@Configuration
public class CmsConfig
{
	private static final Logger logger = LoggerFactory.getLogger(CmsConfig.class);

	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	public CmsService createCmsService()
	{
        final String fileNamesStr = CMS_INDEX.value();
        String[] fileNames = StringUtils.split(fileNamesStr, ',');
        List<Resource> resources = new ArrayList<>();
        for (String fileName : fileNames) {
            if (StringUtils.isNotEmpty(fileName)) {
                resources.add(resourceLoader.getResource(fileName));
            }
        }
        logger.info("Bootstrapping the cms service with {} resource {}",
                new Object[] { CmsServiceXmlResourceImpl.class.getName(), fileNamesStr });
        return new CmsServiceXmlResourceImpl(resources);
	}
}
