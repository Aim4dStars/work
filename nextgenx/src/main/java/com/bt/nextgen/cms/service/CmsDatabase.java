package com.bt.nextgen.cms.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class CmsDatabase
{
	public Map<String, CmsEntryJaxb> entries;

	public CmsDatabase()
	{
	}

}
