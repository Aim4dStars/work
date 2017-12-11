package com.bt.nextgen.core.web.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;

public class EnumPropertyEditor<T extends Enum> extends PropertyEditorSupport
{
	private static Logger logger = LoggerFactory.getLogger(EnumPropertyEditor.class);
	private final Class<T> genericType;

	public EnumPropertyEditor(Class<T> genericType)
	{
		this.genericType = genericType;
	}

	@Override
	public void setAsText(String text)
	{
		Enum<?> instance = null;

		try
		{
			instance = Enum.valueOf(genericType, text.toUpperCase().trim());
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage());
		}
		setValue(instance);
	}
}
