package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.AMLCharacteristicFilter;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ArrangementRelationshipStatus;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ArrangementType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyEntityFilter;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PositionFilter;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.TaxResidencyFilter;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("squid:S1200")
public final class GroupEsbUserDetailsRequestBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbUserDetailsRequestBuilder.class);

    private GroupEsbUserDetailsRequestBuilder() {
    }

    public static RetrieveDetailsAndArrangementRelationshipsForIPsRequest createRetrieveDetailsAndArrangementRelationships(CustomerManagementRequest customerManagementRequest) {
        LOGGER.info("Creating request to retrieve customer details and arrangements.");
        final ObjectFactory objectFactory = new ObjectFactory();

        InvolvedPartyEntityFilter involvedPartyEntityFilter = objectFactory.createInvolvedPartyEntityFilter();
        involvedPartyEntityFilter = createInvolvedParty(involvedPartyEntityFilter, customerManagementRequest);

        InvolvedPartyRole involvedPartyRole = objectFactory.createInvolvedPartyRole();
        involvedPartyRole.setRoleType(customerManagementRequest.getInvolvedPartyRoleType().getDescription());

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier = factory.createInvolvedPartyIdentifier();
        involvedPartyIdentifier = createInvolvedPartyIdentifier(involvedPartyIdentifier, customerManagementRequest);

        final RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = objectFactory.createRetrieveDetailsAndArrangementRelationshipsForIPsRequest();
        request.setInvolvedPartyEntityFilter(involvedPartyEntityFilter);

        if (RoleType.INDIVIDUAL.equals(customerManagementRequest.getInvolvedPartyRoleType())) {
            request.setIndividualInvolvedPartyRole(involvedPartyRole);
        }
        else {
            request.setOrganisationInvolvedPartyRole(involvedPartyRole);
        }

        final List<InvolvedPartyIdentifier> identifiers = request.getInvolvedPartyIdentifier();
        identifiers.add(involvedPartyIdentifier);
        LOGGER.info("Finished creating request to retrieve customer details and arrangements.");
        return request;
    }

    private static InvolvedPartyEntityFilter createInvolvedParty(InvolvedPartyEntityFilter involvedPartyEntityFilter, CustomerManagementRequest customerManagementRequest) {
        addInvolvedPartyEntity(involvedPartyEntityFilter, customerManagementRequest.getOperationTypes());
        addRemainingFlagsInRequest(involvedPartyEntityFilter);
        return involvedPartyEntityFilter;
    }

    private static InvolvedPartyIdentifier createInvolvedPartyIdentifier(InvolvedPartyIdentifier involvedPartyIdentifier, CustomerManagementRequest customerManagementRequest) {
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem(ServiceConstants.CIS);
        involvedPartyIdentifier.setInvolvedPartyId(customerManagementRequest.getCISKey().getId());

        return involvedPartyIdentifier;
    }

    private static InvolvedPartyEntityFilter addInvolvedPartyEntity(InvolvedPartyEntityFilter involvedPartyEntityFilter, List<CustomerManagementOperation> operations) {
        LOGGER.info("Setting involvedPartyEntityFilters based on operations provided.");
        if (operations.contains(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE)) {
            involvedPartyEntityFilter.setIncludeCrossReferences(true);
            involvedPartyEntityFilter.setIncludeCommunicationAddresses(true);
            involvedPartyEntityFilter.setIncludeArrangements(true);
            involvedPartyEntityFilter.setIncludeAlternateNames(true);
            involvedPartyEntityFilter.setIncludeAMLCharacteristic(true);
            involvedPartyEntityFilter.setIncludeTaxResidency(true);
            involvedPartyEntityFilter.getCharacteristicsType().add("characteristicsType0");
            involvedPartyEntityFilter.setArrangementsActiveFlag(true);
            involvedPartyEntityFilter.getArrangementRoleType().add("arrangementRoleType1");
            involvedPartyEntityFilter.setArrangementRelationshipStatus(ArrangementRelationshipStatus.ACTIVE);
            involvedPartyEntityFilter.getArrangementType().add(ArrangementType.PRODUCT);
            involvedPartyEntityFilter.setIncludeCrossReferences(true);

            final PositionFilter positionFilter = new PositionFilter();
            positionFilter.setRelationshipType("RM");
            positionFilter.setRelationshipActiveFlag(true);
            positionFilter.setPositionType("RMGR");
            involvedPartyEntityFilter.getPositionFilter().add(positionFilter);

            involvedPartyEntityFilter.getCrossReferenceSourceSystem().add("LK-001");

            final AMLCharacteristicFilter amlCharacteristicFilter = new AMLCharacteristicFilter();
            amlCharacteristicFilter.setAMLCharacteristicActiveFlag(true);

            final TaxResidencyFilter taxResidencyFilter = new TaxResidencyFilter();
            taxResidencyFilter.setTaxResidencyActiveFlag(true);
            involvedPartyEntityFilter.setTaxResidencyFilter(taxResidencyFilter);
        }

        LOGGER.info("Finished setting involvedPartyEntityFilters.");
        return involvedPartyEntityFilter;
    }

    private static InvolvedPartyEntityFilter addRemainingFlagsInRequest(InvolvedPartyEntityFilter involvedPartyEntityFilter) {
        LOGGER.info("Setting default involvedPartyEntityFilters based on operations not provided.");
        involvedPartyEntityFilter.setIncludeDemographics(false);
        involvedPartyEntityFilter.setIncludePostalAddresses(false);
        involvedPartyEntityFilter.setIncludePosition(false);
        involvedPartyEntityFilter.setIPPostalAddressActiveFlag(false);
        involvedPartyEntityFilter.setIncludeCommunicationAddresses(false);
        involvedPartyEntityFilter.setIPCommAddressActiveFlag(false);
        involvedPartyEntityFilter.setIncludeCharacteristics(false);
        involvedPartyEntityFilter.setIncludeArrangements(false);
        LOGGER.info("Finished setting default involvedPartyEntityFilters based on operations not provided.");
        return involvedPartyEntityFilter;
    }
}
