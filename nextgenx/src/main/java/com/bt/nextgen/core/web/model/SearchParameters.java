package com.bt.nextgen.core.web.model;

import java.util.List;

import com.bt.nextgen.service.avaloq.Template;

//DESCOPED FOR BETA5: SEARCH SPIKE 
public interface SearchParameters
{

	Template getSearchFor();

	List<SearchCriteria> getSearchCriterias();

}