package com.bt.nextgen.service.group.customer.groupesb.state.v10;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.GeographicArea;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ModifyOrganisationCustomerRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;

@SuppressWarnings("squid:S1200")
public final class GroupEsbRegStateRequestV10Builder {
    
    private GroupEsbRegStateRequestV10Builder(){}


    public static ModifyOrganisationCustomerRequest createStateModificationRequest(CustomerData customerData, RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse) {

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ObjectFactory objectFactoryOrgCustomer =
                new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ObjectFactory();

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier  = factory.createInvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem("UCM");
        involvedPartyIdentifier.setInvolvedPartyId(customerData.getRequest().getCISKey().getId());
        Organisation organisation = objectFactoryOrgCustomer.createOrganisation();
        organisation.setInvolvedPartyIdentifier(involvedPartyIdentifier);

        MaintenanceAuditContext auditContextForOrg = objectFactoryOrgCustomer.createMaintenanceAuditContext();
        auditContextForOrg.setIsActive(Boolean.TRUE);
        String cachedVersionNo = getCachedVersionNo(cachedResponse.getOrganisation());
        if (null != cachedVersionNo) {
            auditContextForOrg.setVersionNumber(cachedVersionNo);
        }
        organisation.setAuditContext(auditContextForOrg);

        RegistrationArrangement registrationArrangement = objectFactoryOrgCustomer.createRegistrationArrangement();
        GeographicArea geographicArea = objectFactoryOrgCustomer.createGeographicArea();
        if (null != customerData.getRegisteredState()) {
            geographicArea.setCountry(customerData.getRegisteredState().getCountry());
            geographicArea.setState(customerData.getRegisteredState().getRegistrationState());
        }
        registrationArrangement.setIsIssuedAt(geographicArea);
        organisation.setHasRegistration(registrationArrangement);

        ModifyOrganisationCustomerRequest modifyOrgCustomerRequest = objectFactoryOrgCustomer.createModifyOrganisationCustomerRequest();
        modifyOrgCustomerRequest.setOrganisation(organisation);
        return modifyOrgCustomerRequest;
    }

    private static String getCachedVersionNo(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.
xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.Organisation organisation) {
        if (null != organisation && null != organisation.getAuditContext() && null != organisation.getAuditContext().getVersionNumber()) {
            return organisation.getAuditContext().getVersionNumber();
        }
        return null;
    }

}

