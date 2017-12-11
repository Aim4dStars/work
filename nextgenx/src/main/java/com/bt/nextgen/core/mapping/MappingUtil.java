package com.bt.nextgen.core.mapping;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;

public class MappingUtil
{
	private static final Logger logger = LoggerFactory.getLogger(MappingUtil.class);

	public static <T> T singleItem(Collection <T> items, ServiceErrors serviceErrors)
	{
		T result = null;
		if (items.size() == 1)
		{
			result = items.iterator().next();
		}
		else if (items.size() > 1)
		{
			ServiceError error = new ServiceErrorImpl();
			error.setReason("Expected a single value but received " + items.size());
			serviceErrors.addError(error);
			logger.error("Expected a single value but received " + items.size());
			result = items.iterator().next();
		}
		else
		{
			ServiceError error = new ServiceErrorImpl();
			error.setReason("Expected a single value but received none ");
			serviceErrors.addError(error);
			logger.error("Expected a single value but received none ");
		}
		return result;
	}

	public static boolean isEmpty(Object report, ServiceErrors serviceErrors)
	{
		try
		{
			if (report != null && PropertyUtils.getProperty(report, "metadata") != null
				&& PropertyUtils.getProperty(report, "metadata.emptyRep") != null
				&& PropertyUtils.getProperty(report, "metadata.emptyRep.val") != null)
			{
				Object obj = PropertyUtils.getProperty(report, "metadata.emptyRep.val");
				if (obj instanceof String)
				{
					return Boolean.parseBoolean((String)obj);
				}
				else if (obj instanceof Boolean)
				{
					return (Boolean)obj;
				}
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			logger.error("Error getting the metadata from the report", e);
			ServiceError error = new ServiceErrorImpl();
			error.setException(e);
			error.setReason("Error getting the metadata from the report");
			serviceErrors.addError(error);
		}
		return false;
	}

}
