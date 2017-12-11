package com.bt.nextgen.api.transactioncategorisation.model;

import java.util.List;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.core.api.model.BaseDto;

public class TransactionCategoryDto extends BaseDto
{
	private String intlId;
	private String label;
	private String categorisationLevel;
	//private String type;
	private String transactionMetaType;
	private List <StaticCodeDto> subCategories;

	public String getIntlId()
	{
		return intlId;
	}

	public void setIntlId(String intlId)
	{
		this.intlId = intlId;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getCategorisationLevel()
	{
		return categorisationLevel;
	}

	public void setCategorisationLevel(String categorisationLevel)
	{
		this.categorisationLevel = categorisationLevel;
	}

	

	public String getTransactionMetaType()
	{
		return transactionMetaType;
	}

	public void setTransactionMetaType(String transactionMetaType)
	{
		this.transactionMetaType = transactionMetaType;
	}

	public List <StaticCodeDto> getSubCategories()
	{
		return subCategories;
	}

	public void setSubCategories(List <StaticCodeDto> subCategories)
	{
		this.subCategories = subCategories;
	}

}
