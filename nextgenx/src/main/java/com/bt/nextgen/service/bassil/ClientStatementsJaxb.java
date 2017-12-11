package com.bt.nextgen.service.bassil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.MatchingImageType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexDatePropValueType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexDateTimePropValueType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexDecimalPropValueType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexIntegerPropValueType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropValuesType;

/**
 * 
 *
 *
 */
public class ClientStatementsJaxb implements ClientStatements
{

	private MatchingImageType matchingImageType;

	private Map <String, List <String>> attributes;

	public ClientStatementsJaxb()
	{}

	public ClientStatementsJaxb(MatchingImageType matchingImageType)
	{
		this.matchingImageType = matchingImageType;
	}

	@Override
	public String getDocumentID()
	{
		return matchingImageType.getDocumentID();
	}

	@Override
	public String getDocumentEntryDate()
	{
		return matchingImageType.getDocumentEntryDate().toString();
	}

	@Override
	public Map <String, List <String>> getDocumentProperties()
	{
		if (attributes == null)
		{
			loadAttributes();
		}
		return attributes;
	}

	/**
	 * 
	 * @return Jaxb object of basil service type MatchingImageType
	 */
	public MatchingImageType getMatchingImageType()
	{
		return matchingImageType;
	}

	/**
	 * 
	 * @param matchingImageType
	 */
	public void setMatchingImageType(MatchingImageType matchingImageType)
	{
		this.matchingImageType = matchingImageType;
	}

	private void loadAttributes()
	{
		this.attributes = new HashMap <String, List <String>>();
		List <DocImageIndexPropType> properties = matchingImageType.getDocumentIndexProperties().getDocumentIndexProperty();
		for (DocImageIndexPropType docImageIndexPropType : properties)
		{
			String name = docImageIndexPropType.getDocumentIndexPropertyName();
			DocImageIndexPropValuesType values = docImageIndexPropType.getDocumentIndexPropertyValues();

			List <String> stringValue = values.getDocumentIndexStringPropertyValue();
			if (stringValue != null && !stringValue.isEmpty())
			{
				this.attributes.put(name, getAttributeValues(stringValue));
			}
			List <DocImageIndexDatePropValueType> dateValue = values.getDocumentIndexDatePropertyValue();
			if (dateValue != null && !dateValue.isEmpty())
			{
				this.attributes.put(name, getAttributeValues(dateValue));
			}
			List <DocImageIndexDateTimePropValueType> dateTimeValue = values.getDocumentIndexDateTimePropertyValue();
			if (dateTimeValue != null && !dateTimeValue.isEmpty())
			{
				this.attributes.put(name, getAttributeValues(dateTimeValue));
			}
			List <DocImageIndexDecimalPropValueType> decimalValue = values.getDocumentIndexDecimalPropertyValue();
			if (decimalValue != null && !decimalValue.isEmpty())
			{
				this.attributes.put(name, getAttributeValues(decimalValue));
			}
			List <DocImageIndexIntegerPropValueType> integerValue = values.getDocumentIndexIntegerPropertyValue();
			if (integerValue != null && !integerValue.isEmpty())
			{
				this.attributes.put(name, getAttributeValues(integerValue));
			}
		}
	}

	private List <String> getAttributeValues(List list)
	{
		List <String> values = new ArrayList <String>();

		for (Object object : list)
		{
			String value = null;
			if (object instanceof String)
			{
				value = (String)object;
				values.add(value);
			}
			else if (object instanceof DocImageIndexDatePropValueType)
			{
				value = ((DocImageIndexDatePropValueType)object).getDocumentIndexDateValue().toString();
				values.add(value);
				if (((DocImageIndexDatePropValueType)object).getDocumentIndexEndDateValue() != null)
				{
					value = ((DocImageIndexDatePropValueType)object).getDocumentIndexEndDateValue().toString();
					values.add(value);
				}
				//((DocImageIndexDatePropValueType)object).getDocumentIndexDateValue().toString();
			}
			else if (object instanceof DocImageIndexDateTimePropValueType)
			{
				value = ((DocImageIndexDateTimePropValueType)object).getDocumentIndexDateTimeValue().toString();
				values.add(value);
			}
			else if (object instanceof DocImageIndexDecimalPropValueType)
			{
				value = ((DocImageIndexDecimalPropValueType)object).getDocumentIndexDecimalValue().toPlainString();
				values.add(value);
			}
			else
			{
				value = ((DocImageIndexIntegerPropValueType)object).getDocumentIndexEndIntegerValue().toString();
				values.add(value);
			}

		}
		return values;
	}

	@Override
	public String getDocumentType()
	{
		List <String> documentType = getDocumentProperties().get("DocumentType");
		return documentType.get(0);
	}

	@Override
	public List <String> getDocumentProperties(String attributeName)
	{
		return getDocumentProperties().get(attributeName);
	}
}
