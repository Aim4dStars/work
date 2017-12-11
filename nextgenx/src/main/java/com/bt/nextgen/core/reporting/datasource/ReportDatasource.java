package com.bt.nextgen.core.reporting.datasource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface covers what input is required when rendering a report. For static reports an 'EMPTY' datasource
 * has been provided.
 */
public interface ReportDatasource
{
	/**
	 * For report that require no customisation
	 */
	public static final ReportDatasource EMPTY_DATASOURCE = new ReportDatasource()
	{
		private Map <String, Object> parameters = new HashMap <>(0);
		private Collection <? > beans = Collections.emptyList();

		@Override
		public Map <String, Object> getParameters()
		{
			return parameters;
		}

		@Override
		public Collection <? > getBeans()
		{
			return beans;
		}

		@Override
		public String getName()
		{
			return null;
		}
	};

	/**
	 * Called to get parameters for the report
	 * @return
	 */
	public Map <String, Object> getParameters();

	/**
	 * Called to get the beans for the report, these form the main 'dataset' for a report
	 * @return
	 */
	public Collection <? > getBeans();

	public String getName();
}
