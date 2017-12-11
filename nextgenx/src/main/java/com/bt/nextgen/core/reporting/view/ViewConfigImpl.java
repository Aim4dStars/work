package com.bt.nextgen.core.reporting.view;

import java.util.ArrayList;
import java.util.List;

public class ViewConfigImpl implements ViewConfig
{
	private List <DataSourceField> dataSourceFields = new ArrayList <DataSourceField>();

    private List<ViewColumnConfig> viewColumnConfigs = new ArrayList<ViewColumnConfig>();

	@Override
	public List <DataSourceField> getDataSourceFields()
	{
		return dataSourceFields;
	}

    @Override
    public List<ViewColumnConfig> getViewColumnConfigs() {
        return viewColumnConfigs;
    }

    public void addViewColumnConfig(ViewColumnConfig viewColumnConfig) {
        viewColumnConfigs.add(viewColumnConfig);
    }
}
