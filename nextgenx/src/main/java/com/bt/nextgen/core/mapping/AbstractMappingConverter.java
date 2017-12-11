package com.bt.nextgen.core.mapping;

import java.net.URL;

import com.btfin.panorama.core.mapping.Mapper;
import com.btfin.panorama.core.mapping.MapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.exception.ApplicationConfigurationException;

public class AbstractMappingConverter
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractMappingConverter.class);

	private Mapper mapper = null;

	protected Mapper getMapper()
	{
		if (mapper == null)
		{
			Class <? extends AbstractMappingConverter> clazz = this.getClass();
			String mappingFile = getResourceName(clazz);
			URL mappingUrl = clazz.getResource(mappingFile);
			if (mappingUrl == null)
			{
				String message = "Cannot get resource " + mappingFile;
				logger.error(message);
				throw new ApplicationConfigurationException(message);
			}
			mapper = new MapperImpl(mappingUrl.toString());
		}
		return mapper;
	}

	private String getResourceName(Class <? extends AbstractMappingConverter> clazz)
	{
		String className = clazz.getName();
		int dot = className.lastIndexOf(".");
		if (dot != -1)
		{
			className = className.substring(dot + 1);
		}
		return className + ".mapping.xml";
	}

}
