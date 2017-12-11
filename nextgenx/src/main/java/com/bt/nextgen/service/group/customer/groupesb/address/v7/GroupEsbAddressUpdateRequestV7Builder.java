package com.bt.nextgen.service.group.customer.groupesb.address.v7;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by F057654 on 28/07/2015.
 */
@SuppressWarnings("squid:S1200")
@Deprecated
public final class GroupEsbAddressUpdateRequestV7Builder {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbAddressUpdateRequestV7Builder.class);

    private GroupEsbAddressUpdateRequestV7Builder(){}

    public static MaintainIPContactMethodsRequest createUpdateIPContactMethods(CustomerData customerData, RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse){

        logger.info("Creating request for updating address in gcm for user {}",customerData.getRequest().getCISKey().getId());

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.ObjectFactory of =
                new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.ObjectFactory();

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier  = factory.createInvolvedPartyIdentifier();
        MaintainIPContactMethodsRequest request = of.createMaintainIPContactMethodsRequest();
        final List<InvolvedPartyIdentifier> identifiers = request.getInvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem(ServiceConstants.UCM);
        involvedPartyIdentifier.setInvolvedPartyId(customerData.getRequest().getCISKey().getId());
        identifiers.add(involvedPartyIdentifier);

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PostalAddressContactMethod cachedPostalAddress = null;
        if(customerData.getRequest().getInvolvedPartyRoleType().equals(RoleType.INDIVIDUAL)){
            request.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
            cachedPostalAddress
                    = CustomerAddressManagementV7Converter.convertResponseInAddressModel(cachedResponse, customerData.getRequest());
        }else{
            request.setInvolvedPartyType(InvolvedPartyType.ORGANISATION);
            cachedPostalAddress
                    = CustomerAddressManagementV7Converter.convertResponseInAddressModel(cachedResponse, customerData.getRequest());
        }

        PostalAddressContactMethod address = CustomerAddressUpdateV7Converter.createPostalAddressToBeSentForUpdate(cachedPostalAddress, customerData);
        List<PostalAddressContactMethod> addresses = request.getHasPostalAddressContactMethod();
        addresses.add(address);

        return request;

    }

}
