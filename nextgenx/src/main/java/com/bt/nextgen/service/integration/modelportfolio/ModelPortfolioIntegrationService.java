package com.bt.nextgen.service.integration.modelportfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;

import java.util.Collection;

/**
 * Interface to load all of the details of a model portfolio
 */
public interface ModelPortfolioIntegrationService {

    /**
     * Loads the model portfolios for an investment manager id.
     * 
     * @param investmentManager
     *            - the investment manager key.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the models from the end point.
     * @return The collection of model portfolios. If no models are found for
     *         the user then an empty list is returned.
     */
    Collection<ModelPortfolio> loadModels(final BrokerKey investmentManager, final ServiceErrors serviceErrors);

    /**
     * Validates a model portfolio. If warnings are present then the warnings
     * list of the model portofolio is populated. If errors or fatals are
     * present then ValidationExceptions are thrown and the model is not
     * uploaded.
     * 
     * @param model
     *            - the model portfolio to upload.
     * @param serviceErrors
     *            - Output parameter, stores all errors encountered.
     */
    public ModelPortfolioUpload validateModel(final ModelPortfolioUpload model, final ServiceErrors serviceErrors);

    /**
     * Submits a model portfolio. If warnings are present then the warnings list
     * of the model portofolio is populated. If errors or fatals are present
     * then ValidationExceptions are thrown and the model is not uploaded.
     * 
     * @param model
     *            - the model portfolio to upload.
     * @param serviceErrors
     *            - Output parameter, stores all errors encountered.
     */
    public ModelPortfolioUpload submitModel(final ModelPortfolioUpload model, final ServiceErrors serviceErrors);

    /**
     * Loads the model portfolio for a model id.
     * 
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the models from the end point.
     * @return The model portfolio. If the model is not found then null is
     *         returned.
     */
    ModelPortfolio loadModel(final IpsKey modelKey, final ServiceErrors serviceErrors);

    /**
     * Retrieve a previously created model based on the order-number.
     * 
     * @param modelKey
     * @param serviceErrors
     * @return
     */
    public ModelPortfolioUpload loadUploadedModel(final IpsKey modelKey, final ServiceErrors serviceErrors);

    /**
     * Retrieve the shadow portfolio model of the specified model key.
     * 
     * @param modelKey
     * @param serviceErrors
     * @return
     */
    public ShadowPortfolio loadShadowPortfolioModel(final IpsKey modelKey, final ServiceErrors serviceErrors);
}
