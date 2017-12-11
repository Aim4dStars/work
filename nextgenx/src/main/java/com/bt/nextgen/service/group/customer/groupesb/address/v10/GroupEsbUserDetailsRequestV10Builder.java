package com.bt.nextgen.service.group.customer.groupesb.address.v10;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.ArrangementRelationshipStatus;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.InvolvedPartyEntityFilter;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.InvolvedPartyRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationContext;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("squid:S1200")
public final class GroupEsbUserDetailsRequestV10Builder
{

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbUserDetailsRequestV10Builder.class);

    private GroupEsbUserDetailsRequestV10Builder(){}

    public static RetrieveDetailsAndArrangementRelationshipsForIPsRequest createRetrieveDetailsAndArrangementRelationships(CustomerManagementRequest customerManagementRequest) {
        LOGGER.info("Creating request to retrieve customer details and arrangements.");
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.ObjectFactory of = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.ObjectFactory();

        InvolvedPartyEntityFilter involvedPartyEntityFilter = of.createInvolvedPartyEntityFilter();
        involvedPartyEntityFilter = createInvolvedParty(involvedPartyEntityFilter, customerManagementRequest);

        InvolvedPartyRole involvedPartyRole = of.createInvolvedPartyRole();
        involvedPartyRole.setRoleType(customerManagementRequest.getInvolvedPartyRoleType().getDescription());

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier  = factory.createInvolvedPartyIdentifier();
        involvedPartyIdentifier = createInvolvedPartyIdentifier(involvedPartyIdentifier, customerManagementRequest);

        RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = of.createRetrieveDetailsAndArrangementRelationshipsForIPsRequest();
        request.setInvolvedPartyEntityFilter(involvedPartyEntityFilter);

        if (RoleType.INDIVIDUAL.equals(customerManagementRequest.getInvolvedPartyRoleType())) {
            request.setIndividualInvolvedPartyRole(involvedPartyRole);
        } else {
            request.setOrganisationInvolvedPartyRole(involvedPartyRole);
        }

        List<InvolvedPartyIdentifier> identifiers = request.getInvolvedPartyIdentifier();
        identifiers.add(involvedPartyIdentifier);
        LOGGER.info("Finished creating request to retrieve customer details and arrangements.");
        return request;
    }

    private static InvolvedPartyEntityFilter createInvolvedParty(InvolvedPartyEntityFilter involvedPartyEntityFilter, CustomerManagementRequest customerManagementRequest){
        addInvolvedPartyEntity(involvedPartyEntityFilter, customerManagementRequest.getOperationTypes());
        addRemainingFlagsInRequest(involvedPartyEntityFilter, customerManagementRequest.getOperationTypes());
        return involvedPartyEntityFilter;
    }

    private static InvolvedPartyIdentifier createInvolvedPartyIdentifier(InvolvedPartyIdentifier involvedPartyIdentifier, CustomerManagementRequest customerManagementRequest){
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem(ServiceConstants.UCM);
        involvedPartyIdentifier.setInvolvedPartyId(customerManagementRequest.getCISKey().getId());

        return involvedPartyIdentifier;
    }

    private static InvolvedPartyEntityFilter addInvolvedPartyEntity(InvolvedPartyEntityFilter involvedPartyEntityFilter, List<CustomerManagementOperation> operations) {
        LOGGER.info("Setting involvedPartyEntityFilters based on operations provided.");
        if(operations.contains(CustomerManagementOperation.ADDRESS_UPDATE)){
            involvedPartyEntityFilter.setIncludePostalAddresses(true);
            involvedPartyEntityFilter.setIPPostalAddressActiveFlag(true);
        }

        if(operations.contains(CustomerManagementOperation.PREFERRED_NAME_UPDATE)){
            involvedPartyEntityFilter.setIncludeDemographics(true);
            involvedPartyEntityFilter.setIncludeAlternateNames(true);
            involvedPartyEntityFilter.setAlternateNamesActiveFlag(true);
        }

        if(operations.contains(CustomerManagementOperation.CONTACT_DETAILS_UPDATE)){
            involvedPartyEntityFilter.setIncludeCommunicationAddresses(true);
            involvedPartyEntityFilter.setIPCommAddressActiveFlag(true);
        }

        if(operations.contains(CustomerManagementOperation.ARRANGEMENTS)){
            involvedPartyEntityFilter.setIncludeArrangements(true);
            involvedPartyEntityFilter.setArrangementRelationshipStatus(ArrangementRelationshipStatus.ACTIVE);
        }

        if(operations.contains(CustomerManagementOperation.REGISTRATION_STATE)){
            involvedPartyEntityFilter.setIncludeDemographics(true);
        }

        if (operations.contains(CustomerManagementOperation.INDIVIDUAL_DETAILS)) {
            involvedPartyEntityFilter.setIncludeDemographics(true);
        }
        LOGGER.info("Finished setting involvedPartyEntityFilters.");
        return involvedPartyEntityFilter;
    }

    private static InvolvedPartyEntityFilter addRemainingFlagsInRequest(InvolvedPartyEntityFilter involvedPartyEntityFilter, List<CustomerManagementOperation> operations){
        LOGGER.info("Setting default involvedPartyEntityFilters based on operations not provided.");
        if(!operations.contains(CustomerManagementOperation.ADDRESS_UPDATE)){
            involvedPartyEntityFilter.setIncludePostalAddresses(false);
            involvedPartyEntityFilter.setIPPostalAddressActiveFlag(false);
        }

        if(!operations.contains(CustomerManagementOperation.PREFERRED_NAME_UPDATE)){
            if(!operations.contains(CustomerManagementOperation.REGISTRATION_STATE)) {
                involvedPartyEntityFilter.setIncludeDemographics(false);
            }
            involvedPartyEntityFilter.setIncludeAlternateNames(false);
            involvedPartyEntityFilter.setAlternateNamesActiveFlag(false);
        }

        if(!operations.contains(CustomerManagementOperation.CONTACT_DETAILS_UPDATE)){
            involvedPartyEntityFilter.setIncludeCommunicationAddresses(false);
            involvedPartyEntityFilter.setIPCommAddressActiveFlag(false);
        }

        if(!operations.contains(CustomerManagementOperation.ARRANGEMENTS)){
            involvedPartyEntityFilter.setIncludeArrangements(false);
        }

        //Mark all other flags as false because we don't have operations to handle this requirement
        involvedPartyEntityFilter.setIncludeCharacteristics(false);
        involvedPartyEntityFilter.setIncludePosition(false);
        involvedPartyEntityFilter.setIncludeCrossReferences(false);
        involvedPartyEntityFilter.setIncludeAMLCharacteristic(false);
        LOGGER.info("Finished setting default involvedPartyEntityFilters based on operations not provided.");
        return involvedPartyEntityFilter;
    }

    public static RetrieveDetailsAndArrangementRelationshipsForIPsRequest createPaginatedRetrieveDetailsAndArrangementRelationships(
        CustomerManagementRequest customerManagementRequest, PaginationInstruction arrangementPaginationInstruction) {
        LOGGER.info("Creating paginated request to retrieve customer details and arrangements.");
        RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = createRetrieveDetailsAndArrangementRelationships(customerManagementRequest);
        request.setArrangementPaginationInstruction(getPaginationInstruction(arrangementPaginationInstruction.getPaginationContext()));
        LOGGER.info("Finished creating paginated request to retrieve customer details and arrangements.");
        return request;
    }

    private static PaginationInstruction getPaginationInstruction(List<PaginationContext> paginationContext) {
        LOGGER.info("{} to retrieve: {}", paginationContext.get(0).getName(), paginationContext.get(0).getValue());
        PaginationInstruction paginationInstruction = new PaginationInstruction();
        paginationInstruction.getPaginationContext().addAll(paginationContext);
        return paginationInstruction;
    }
}
