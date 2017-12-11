package com.bt.nextgen.reports.modelportfolio;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.MultiDataReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@MultiDataReport("modelPortfolioBulkReport")
public class ModelPortfolioBulkReport extends BaseReport
{
	private static final String RESULT_SORTING_ORDER = "modelName,asc";

	@Autowired
	private ModelPortfolioDtoService modelPortfolioService;

    public List<ModelPortfolioDto> getModelPortfolios()
	{
		ApiResponse response = new Sort <>(new FindAll <>(ApiVersion.CURRENT_VERSION, modelPortfolioService),
			RESULT_SORTING_ORDER).performOperation();
		List <ModelPortfolioDto> modelPortfolioDtos = ((ResultListDto <ModelPortfolioDto>)response.getData()).getResultList();
		return modelPortfolioDtos;

	}

	@Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections)
	{
        return getModelPortfolios();
	}

	@Override
    public Collection<String> getReportPageNames(Collection<?> data)
	{
        List<ModelPortfolioDto> modelPortfolioDtos = getModelPortfolios();
		List <String> modelPageNames = new ArrayList <>();
		if ((modelPortfolioDtos != null) && (!modelPortfolioDtos.isEmpty()))
		{
			for (ModelPortfolioDto modelPortfolio : modelPortfolioDtos)
			{
				String pageName = modelPortfolio.getModelName();
				if (pageName == null || StringUtils.isBlank(pageName))
				{
					pageName = modelPortfolio.getKey().getModelId();
				}

				modelPageNames.add(pageName);
			}
		}

		return modelPageNames;
	}
}
