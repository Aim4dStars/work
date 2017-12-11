package com.bt.nextgen.api.client.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.IndividualTo;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.InvolvedPartyRelationshipQuery;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrieveiptoiprelationships.v4.svc0260.OrganisationTo;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;
import com.bt.nextgen.service.groupesb.iptoiprelationships.v4.IPToIPRelationshipsIntegrationService;
import com.bt.nextgen.serviceops.controller.ServiceOpsController;
import com.bt.nextgen.serviceops.model.RetriveIpToIpRelationshipReqModel;

@Service("retriveIPToIPRelationshipDtoService")
@SuppressWarnings("squid:S1200")
public class IPToIPRelationshipsDataDtoServiceImpl implements IPToIPRelationshipsDataDtoService {
    private static final Logger logger = LoggerFactory
            .getLogger(ServiceOpsController.class);
    private static final String SOURCE_SYSTEM = "CIS";

    private static final String PERSON_TYPE = "Individual";

    @Autowired
    @Qualifier("iptoiprelationshipsintegrationservice")
    private IPToIPRelationshipsIntegrationService iptoiprelationshipsintegrationservice;

    @Override
    public CustomerRawData retrieve(RetriveIpToIpRelationshipReqModel reqModel, ServiceErrors serviceErrors) {
        RetriveIPToIPRequest request = createretriveIPToIPRequest(reqModel);
        logger.info("Calling IPToIPRelationshipsIntegrationService.retrieve");
        CustomerRawData customerData =
                iptoiprelationshipsintegrationservice.retrieveIpToIpRelationshipsInformation(request, serviceErrors);

        return customerData;
    }

    private RetriveIPToIPRequest createretriveIPToIPRequest(RetriveIpToIpRelationshipReqModel reqModel) {
        logger.info("Calling IPToIPRelationshipsIntegrationService.createretriveIPToIPRequest");
        RetriveIPToIPRequest retriveIPToIPRequest = new RetriveIPToIPRequest();
        List<InvolvedPartyRelationshipQuery> involvedPartyRelationshipQueryList =
                new ArrayList<InvolvedPartyRelationshipQuery>();
        InvolvedPartyRelationshipQuery involvedPartyRelationshipQuery = new InvolvedPartyRelationshipQuery();
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem(SOURCE_SYSTEM);
        involvedPartyIdentifier.setInvolvedPartyId(reqModel.getCisKey());
        if (PERSON_TYPE.equalsIgnoreCase(reqModel.getRoleType())) {
            IndividualTo individualTo = new IndividualTo();
            individualTo.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
            involvedPartyRelationshipQuery.setIndividual(individualTo);
        } else {
            OrganisationTo organisationTo = new OrganisationTo();
            organisationTo.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
            involvedPartyRelationshipQuery.setOrganisation(organisationTo);
        }

        involvedPartyRelationshipQueryList.add(involvedPartyRelationshipQuery);
        retriveIPToIPRequest.setInvolvedPartyRelationshipQuery(involvedPartyRelationshipQueryList);

        return retriveIPToIPRequest;
    }
}
