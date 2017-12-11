package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.tdmaturities.model.TDMaturitiesDto;
import com.bt.nextgen.api.tdmaturities.service.TDMaturitiesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.ControllerOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Report("termDepositMaturitiesReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
public class TermDepositMaturitiesReport
{
	private static final String QUERY = "query";
	private static final String STATUS = "status";

	@Autowired
	private TDMaturitiesDtoService tdmaturitiesDtoService;

	@ReportBean("tdMaturities")
	public Collection <TDMaturitiesDto> getTDMaturities(Map <String, String> params)
	{
		List <ApiSearchCriteria> criteriaForTDTypes = new ArrayList <ApiSearchCriteria>();
		List <ApiSearchCriteria> searchCriterias = getApiSearchCriteria(params);
		if (null != searchCriterias)
		{
			for (ApiSearchCriteria apiSearchCriteria : searchCriterias)
			{
				if (apiSearchCriteria.getProperty().equalsIgnoreCase(STATUS))
				{
					criteriaForTDTypes.add(apiSearchCriteria);
				}
			}
		}
		ControllerOperation controllerOperation = new BeanFilter(ApiVersion.CURRENT_VERSION,
			new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, tdmaturitiesDtoService, searchCriterias),
			criteriaForTDTypes);
		String orderBy = params.get(Sort.SORT_PARAMETER);
		ApiResponse response = new Sort <>(controllerOperation, orderBy).performOperation();
		ResultListDto <TDMaturitiesDto> resultList = (ResultListDto <TDMaturitiesDto>)response.getData();
		return resultList.getResultList();
	}

	private List <ApiSearchCriteria> getApiSearchCriteria(Map <String, String> params)
	{
		String searchCriteria = params.get(QUERY);
		List <ApiSearchCriteria> searchCriterias = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, searchCriteria);
		return searchCriterias;
	}
}
