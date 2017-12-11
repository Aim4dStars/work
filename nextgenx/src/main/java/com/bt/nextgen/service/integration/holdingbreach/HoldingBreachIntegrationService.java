package com.bt.nextgen.service.integration.holdingbreach;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Interface to load the holding breach report
 */
public interface HoldingBreachIntegrationService {
    /**
     * Loads the holding breach report
     * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the models from the end point.
     * @return The holding breach report including the affected clients/assets.
     */
    public HoldingBreachSummary loadHoldingBreaches(final ServiceErrors serviceErrors);
}
