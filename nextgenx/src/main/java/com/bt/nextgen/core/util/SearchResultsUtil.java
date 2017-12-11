package com.bt.nextgen.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.web.model.SearchCriteria;
import com.bt.nextgen.core.web.model.SearchCriteriaProvider;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.core.web.model.SearchParametersImpl;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.service.avaloq.Template;

public class SearchResultsUtil
{
	private static final Logger logger = LoggerFactory.getLogger(SearchResultsUtil.class);

	/**
	 * Builds a Search Query by setting the parameters in SearchParameter from the request Map
	 * @param searchParametersMap
	 * @param searchFor
	 * @return
	 */
	public static SearchParameters buildSearchQueryFor(Map <String, String[]> searchParametersMap, Template searchFor)
	{
		SearchParametersImpl searchParameter = new SearchParametersImpl();
		List <SearchCriteria> searchCriterias = new ArrayList <SearchCriteria>();
		searchParameter.setSearchFor(searchFor);

		for (Entry <String, String[]> entrySet : searchParametersMap.entrySet())
		{
			if (isValidSearchKey(entrySet.getKey()))
			{
				SearchCriteriaProvider searchCriteria = new SearchCriteriaProvider();
				searchCriteria.setSearchKey(SearchParams.valueOf(entrySet.getKey()));
				for (String value : entrySet.getValue())
				{
					searchCriteria.setSearchValue(value);
					break;
				}

				searchCriterias.add(searchCriteria);
			}
			else
			{
				logger.warn("The Search Key : {} sent from the UI is not a valid key, it is not present in Search Param ENUM.",
					entrySet.getKey());
			}
		}
		searchParameter.setSearchCriterias(searchCriterias);
		return searchParameter;
	}

	/**
	 * Builds a Search Query by setting the parameters in SearchParameter from an API search criteria using a supplied Mapper
	 * @param searchParametersMap
	 * @param searchFor
	 * @return
	 */
	public static SearchParameters buildSearchQueryFor(List <ApiSearchCriteria> criteriaList, SearchParameterMapper mapper,
		Template searchFor)
	{
		SearchParameters searchParameter = buildSearchQueryFor(criteriaList, mapper);
		((SearchParametersImpl)searchParameter).setSearchFor(searchFor);
		return searchParameter;

	}

	/**
	 * Builds a Search Query by setting the parameters in SearchParameter from an API search criteria using a supplied Mapper
	 * @param searchParametersMap
	 * @return
	 */
	public static SearchParameters buildSearchQueryFor(List <ApiSearchCriteria> criteriaList, SearchParameterMapper mapper)
	{
		SearchParametersImpl searchParameter = new SearchParametersImpl();

		List <SearchCriteria> searchCriterias = new ArrayList <SearchCriteria>();

		for (ApiSearchCriteria criteria : criteriaList)
		{
			String key = mapper.getSearchKey(criteria);

			if (key != null && isValidSearchKey(key))
			{
				SearchCriteriaProvider searchCriteria = new SearchCriteriaProvider();
				searchCriteria.setSearchKey(SearchParams.valueOf(key));
				searchCriteria.setSearchValue(mapper.getSearchValue(criteria));
				searchCriterias.add(searchCriteria);
			}
			else
			{
				throw new BadRequestException("Unsupported search request " + criteria.getProperty() + ":"
					+ criteria.getOperation());
			}
		}
		searchParameter.setSearchCriterias(searchCriterias);
		return searchParameter;
	}

	/**
	 * fetches the Avaloq Parameter name from the properties file for that template 
	 * @param templateName
	 * @param param
	 * @return
	 */
	public static String getParamName(String templateName, String param)
	{
		return Properties.get(templateName + "." + param + "." + "PARAM_NAME");
	}

	/**
	 * fetches the Avaloq Parameter Type from the properties file for that template
	 * @param templateName
	 * @param param
	 * @return
	 */
	public static String getParamType(String templateName, String param)
	{
		return Properties.get(templateName + "." + param + "." + "PARAM_TYPE");
	}

	/**
	 * checks whether the search key send from UI is present in the SearchParam ENUM
	 * @param searchKey
	 * @param templateName
	 * @return
	 */
	private static boolean isValidSearchKey(String searchKey)
	{
		try
		{
			SearchParams.valueOf(searchKey);
		}
		catch (IllegalArgumentException ex)
		{
			logger.warn("The key is not present in the SearchParams ENUM");
			return false;
		}
		return true;
	}

}
