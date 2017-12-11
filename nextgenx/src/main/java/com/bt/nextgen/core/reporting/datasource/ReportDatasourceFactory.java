package com.bt.nextgen.core.reporting.datasource;

import java.util.List;
import java.util.Map;

import com.bt.nextgen.core.reporting.ReportIdentity;

public interface ReportDatasourceFactory
{
	//ReportDatasource createDataSource(ReportIdentity reportKey, Map <String, Object> allRequestParams);

	List <ReportDatasource> createDataSources(ReportIdentity reportKey, Map <String, Object> params);
}