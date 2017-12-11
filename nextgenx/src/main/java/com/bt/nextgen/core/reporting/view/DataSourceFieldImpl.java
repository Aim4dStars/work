package com.bt.nextgen.core.reporting.view;

public class DataSourceFieldImpl implements DataSourceField
{
	private final String fieldName;
	private final Class <? > fieldClass;

	public DataSourceFieldImpl(String fieldName, Class <? > fieldClass)
	{
		this.fieldName = fieldName;
		this.fieldClass = fieldClass;
	}

	@Override
	public String getFieldName()
	{
		return fieldName;
	}

	@Override
	public Class <? > getFieldClass()
	{
		return fieldClass;
	}

}
