package com.bt.nextgen.service.group.customer.groupesb.address.v10;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.util.CaseConverterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by F057654 on 10/08/2015.
 */
@SuppressWarnings("squid:S1200")
public final class CustomerAddressManagementV10Converter {

    private CustomerAddressManagementV10Converter() {
    }

    private static final Logger logger = LoggerFactory.getLogger(CustomerAddressManagementV10Converter.class);

    private static final String AU_COUNTRY_CODE = "AU";

    /**
     * Method to convert the response of svc0258 into Address interface which will be sent to the dtoService for display.
     *
     * @param address - PostalAddress retrieved from svc 0258
     * @return Address
     */
    public static Address convertAddressFromResponse(PostalAddressContactMethod address) {
        if (address == null) {
            return new CustomerAddress();
        }
        PostalAddress postalAddress = address.getHasAddress();
        AddressAdapterV10 addressAdapter = null;
        CaseConverterUtil.convertToTitleCase(postalAddress, "state", "country");

        if (postalAddress instanceof StandardPostalAddress) {
            logger.info("Processing Standard Postal Address details for user");
            StandardPostalAddress standardPostalAddress = (StandardPostalAddress) postalAddress;
            addressAdapter = new AddressAdapterV10(standardPostalAddress);
            addressAdapter.setInternationalAddress(!AU_COUNTRY_CODE.equalsIgnoreCase(standardPostalAddress.getCountry()));
            addressAdapter.setStandardAddressFormat(true);
        } else if (postalAddress instanceof NonStandardPostalAddress) {
            logger.info("Processing NonStandardPostalAddress details for user");
            NonStandardPostalAddress nonStandardPostalAddress = (NonStandardPostalAddress) postalAddress;
            addressAdapter = new InternationalAddressV10Adapter(nonStandardPostalAddress);
            addressAdapter.setInternationalAddress(!AU_COUNTRY_CODE.equalsIgnoreCase(nonStandardPostalAddress.getCountry()));
            addressAdapter.setStandardAddressFormat(false);
        } else {
            logger.error("Address different than StandardPostalAddress/NonStandardPostalAddress is marked as current : Not transforming");
        }
        return addressAdapter;
    }

    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod convertResponseInAddressModel(
            RetrieveDetailsAndArrangementRelationshipsForIPsResponse response, CustomerManagementRequest request) {
        if (request.getInvolvedPartyRoleType().equals(RoleType.INDIVIDUAL)) {
            if (response.getIndividual() != null && response.getIndividual().getHasPostalAddressContactMethod() != null) {
                return CustomerManagementDataV10Converter.filterPostalAddressForIndividual(response);
            }
        } else {
            return CustomerManagementDataV10Converter.filterPostalAddressForOrganization(response);
        }
        return null;
    }
}
