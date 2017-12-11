package com.bt.nextgen.service.avaloq.search;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.PersonSearchResult;

@ServiceBean(xpath="/")
public class PersonSearchResultImpl extends AvaloqBaseResponseImpl implements PersonSearchResult
{
	@ServiceElementList(xpath="//data/person_list/person", type=PersonResponseImpl.class)
	private List <PersonResponse> persons;
	
	@Override
	public List <PersonResponse> getPersonResponse()
	{
		return persons;
	}
}
	