/**
 * 
 */
package com.bt.nextgen.service.groupesb.createorganisationip.v5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.CreateOrganisationIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.ObjectFactory;

/**
 * @author L081050
 */
public class CreateOrganisationIPRequestBuilderV5 {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrganisationIPIntegrationServiceImplv5.class);

    private CreateOrganisationIPRequestBuilderV5() {

    }

    public static CreateOrganisationIPRequest createOrganisationIpRequest(CreateOrganisationIPReq request) {
        LOGGER.info("Creating RetrieveIPToIPRelationshipsRequest inside CreateOrganisationIPRequestBuilderV5");
        final ObjectFactory factory = new ObjectFactory();
        CreateOrganisationIPRequest req = factory.createCreateOrganisationIPRequest();
        req.setOrganisation(request.getOragnaisation());
        return req;
    }
}
