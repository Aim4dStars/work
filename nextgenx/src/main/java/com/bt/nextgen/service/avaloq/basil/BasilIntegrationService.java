package com.bt.nextgen.service.avaloq.basil;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;
import java.util.Set;

/**
 * Integration service for getting Basil document details
        * Created by M035995 on 26/09/2016.
        */
public interface BasilIntegrationService {

    /**
     * Retrieve list of documents from Basil service
     *
     * @param policyNumberList
     * @param portfolioNumberList
     *
     * @return List<ImageDetails> - List of documents
     */
    List<ImageDetails> getInsuranceDocuments(List<String> policyNumberList, Set<String> portfolioNumberList, ServiceErrors serviceErrors);

}
