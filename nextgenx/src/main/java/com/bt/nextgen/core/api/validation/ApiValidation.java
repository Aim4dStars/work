package com.bt.nextgen.core.api.validation;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.util.ReflectionUtils;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;

public class ApiValidation
{
	/**
	 * @TODO replace these with content strings?
	 */
	private static final String PROPERTY_START = "%(";
	private static final String PROPERTY_END = ")";
	private static final String MISSING_INPUT_ERROR_PROPERTY = "property";
	private static final String MISSING_INPUT_ERROR = "Input parameter " + PROPERTY_START + MISSING_INPUT_ERROR_PROPERTY
		+ PROPERTY_END + " must be provided";
	private static final String MISSING_KEY_ERROR = "Input parameter must be provided";

	private static final String NOT_FOUND_ERROR = "Requested resource could not be found";

	private static final String KEY_INPUT_PARAMETER_ERROR = "Key should contain atleast one not null input";
	
	private static final String UPDATE_FIELD_NONEXISTANT_ERROR = "Update fields should exist on the target class";

	private static final String CLASS = "class";

	private ApiValidation()
	{}

	public static void preconditionNotNull(String apiVersion, Object o, String beanProperty)
	{
		try
		{
			Object propertyValue = BeanUtils.getProperty(o, beanProperty);
			if (propertyValue == null)
			{
				StrSubstitutor sub = new StrSubstitutor(Collections.singletonMap(MISSING_INPUT_ERROR_PROPERTY, beanProperty),
					PROPERTY_START,
					PROPERTY_END);
				throw new BadRequestException(apiVersion, sub.replace(MISSING_INPUT_ERROR));
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new ApiException(apiVersion, e);
		}
	}

	public static void preconditionNotNull(String apiVersion, Object o)
	{
		if (o == null)
		{
			throw new BadRequestException(apiVersion, MISSING_KEY_ERROR);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void preConditionCompleteKey(String apiVersion, Object o)
	{
		preconditionNotNull(apiVersion, o);
		try
		{
			Map describe = BeanUtils.describe(o);
			for (Iterator i = describe.keySet().iterator(); i.hasNext();)
			{
				String propertyName = (String)i.next();
				if (!CLASS.equals(propertyName))
				{
					preconditionNotNull(apiVersion, o, propertyName);
				}
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new ApiException(apiVersion, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void preConditionPartialKey(String apiVersion, Object o)
	{
		preconditionNotNull(apiVersion, o);
		try
		{
			Map describe = BeanUtils.describe(o);
			for (Iterator i = describe.keySet().iterator(); i.hasNext();)
			{
				String propertyName = (String)i.next();
				// Ignore the "class" property, as ALL beans will have this,
				// and it's guaranteed to be non-null!
				if (!CLASS.equals(propertyName))
				{
					Object propertyValue = BeanUtils.getProperty(o, propertyName);
					if (propertyValue != null)
						return;
				}
			}
			throw new BadRequestException(apiVersion, KEY_INPUT_PARAMETER_ERROR);
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new ApiException(apiVersion, e);
		}
	}

    public static void preConditionFieldsExistOnTarget(String apiVersion, Set<String> fieldNames, Class targetClass) {
        preconditionNotNull(apiVersion, fieldNames);
        for (String fieldName : fieldNames) {
            if (ReflectionUtils.findField(targetClass, fieldName) == null) {
                throw new BadRequestException(apiVersion, UPDATE_FIELD_NONEXISTANT_ERROR);
            }
        }
    }

	public static void postConditionNotNull(String apiVersion, Object o, String beanProperty)
	{
		try
		{
			Object propertyValue = BeanUtils.getProperty(o, beanProperty);
			if (propertyValue == null)
			{
				throw new NotFoundException(apiVersion, NOT_FOUND_ERROR);
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new ApiException(apiVersion, e);
		}
	}

	public static void postConditionDataNotNull(String apiVersion, Object o)
	{
		if (o == null)
		{
			throw new NotFoundException(apiVersion, NOT_FOUND_ERROR);
		}
	}

	public static void postConditionDataNotNull(String apiVersion, ApiResponse o)
	{
		postConditionDataNotNull(apiVersion, o.getData());
	}

	@SuppressWarnings("rawtypes")
	public static void postConditionListNotEmpty(String apiVersion, List l)
	{
		if (isEmpty(l))
		{
			throw new NotFoundException(apiVersion, NOT_FOUND_ERROR);
		}
	}

	public static void postConditionNoServiceErrors(String apiVersion, ServiceErrors serviceErrors)
	{
		if (serviceErrors != null && serviceErrors.hasErrors())
		{
			throw new ServiceException(apiVersion, serviceErrors);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void postConditionCompleteKey(String apiVersion, Object o)
	{
		postConditionDataNotNull(apiVersion, o);
		try
		{
			Map describe = BeanUtils.describe(o);
			for (Iterator i = describe.keySet().iterator(); i.hasNext();)
			{
				String propertyName = (String)i.next();
				if (!CLASS.equals(propertyName))
				{
					postConditionNotNull(apiVersion, o, propertyName);
				}
			}
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new ApiException(apiVersion, e);
		}
	}
}
