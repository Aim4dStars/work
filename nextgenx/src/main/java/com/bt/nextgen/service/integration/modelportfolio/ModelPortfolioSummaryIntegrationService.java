package com.bt.nextgen.service.integration.modelportfolio;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;

/**
 * Interface to load the basic details of a model portfolio
 */
public interface ModelPortfolioSummaryIntegrationService
{
	/**
	 * Loads the basic model portfolio details for an investment manager id. 
	 * @param investmentManager - the investment manager key.
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the models from the end point.
	 * @return The collection of model portfolios. If no models are found for the user then an empty list is returned.
	 */
	List <ModelPortfolioSummary> loadModels(final BrokerKey investmentManager, final ServiceErrors serviceErrors);
}
