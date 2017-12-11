package com.bt.nextgen.core.reporting.view;

import java.util.List;

public interface ViewConfig
{
	List <DataSourceField> getDataSourceFields();

	List <ViewColumnConfig> getViewColumnConfigs();

}
