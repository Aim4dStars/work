package com.bt.nextgen.cms.service;

import com.bt.nextgen.cms.CmsEntry;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * This JAXB class will contain entries from the cms-index.xml file.
 */
public class CmsEntryJaxb implements CmsEntry
{
	@XmlAnyAttribute
	public Map<QName,String> meta;

	@XmlValue
	public String value;

	public CmsEntryJaxb()
	{
	}

	@Override public String getMetaData(String name)
	{
		if(meta != null)
		{
			return meta.get(new QName(name));
		}
		else
		{
			return null;
		}
	}

	@Override public String getValue()
	{
		return value;
	}

	@Override public InputStream getStream() throws IOException
	{
		return new ByteArrayInputStream(value.getBytes());
	}
}
