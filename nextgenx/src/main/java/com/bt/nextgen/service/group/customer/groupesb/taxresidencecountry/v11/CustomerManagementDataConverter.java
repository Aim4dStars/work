package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerManagementDataConverter {

    private static String TIN_EXEMPTION_ID = "RC000004";

    private CustomerManagementDataConverter() {
    }

    /**
     * Method to transform tax residence country details from gesb service response into for individual
     * customerData
     *
     * @param response
     *
     * @return CustomerData
     */
    public static CustomerData convertResponseToTaxResidenceCountryModelForIndividual(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        final CustomerData customerData = new CustomerDataImpl();
        final List<RegistrationArrangement> hasForeignRegistration = response.getIndividual().getHasForeignRegistration();
        customerData.setTaxResidenceCountries(getTaxResidenceCountryDetails(hasForeignRegistration));
        return customerData;
    }

    /**
     * Method to transform tax residence country details from gesb service response into for organisation
     * customerData
     *
     * @param response
     *
     * @return CustomerData
     */
    public static CustomerData convertResponseToTaxResidenceCountryModelOrganisation(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        final CustomerData customerData = new CustomerDataImpl();
        final List<RegistrationArrangement> hasForeignRegistration = response.getOrganisation().getHasForeignRegistration();
        customerData.setTaxResidenceCountries(getTaxResidenceCountryDetails(hasForeignRegistration));
        return customerData;
    }

    public static List<TaxResidenceCountry> getTaxResidenceCountryDetails(List<RegistrationArrangement> hasForeignRegistration) {
        final List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();
        if (hasForeignRegistration != null) {
            for (RegistrationArrangement registrationArrangement : hasForeignRegistration) {
                final TaxResidenceCountry taxResidenceCountry = new TaxResidenceCountry();
                if (registrationArrangement.getNoteText() != null && !TIN_EXEMPTION_ID.equals(registrationArrangement.getNoteText())) {
                    taxResidenceCountry.setExemptionReason(registrationArrangement.getNoteText());
                }
                if (CollectionUtils.isNotEmpty(registrationArrangement.getRegistrationIdentifier())) {
                    for (RegistrationIdentifier registrationIdentifier : registrationArrangement.getRegistrationIdentifier()) {
                        if (RegistrationNumberType.TIN.equals(registrationIdentifier.getRegistrationNumberType())) {
                            taxResidenceCountry.setTin(registrationIdentifier.getRegistrationNumber());
                        }
                        else if (RegistrationNumberType.FOREIGN.equals(registrationIdentifier.getRegistrationNumberType())) {
                            taxResidenceCountry.setResidenceCountry(registrationIdentifier.getRegistrationNumberType().value());
                            taxResidenceCountry.setTin(registrationIdentifier.getRegistrationNumber());
                        }
                    }
                }
                taxResidenceCountry.setStartDate(XMLGregorianCalendarUtil.convertToDateTime(registrationArrangement.getStartDate().getValue()));
                taxResidenceCountry.setVersionNumber(registrationArrangement.getAuditContext().getVersionNumber());
                if (registrationArrangement.getIsIssuedAt() != null) {
                    taxResidenceCountry.setResidenceCountry(registrationArrangement.getIsIssuedAt().getCountry());
                }
                taxResidenceCountries.add(taxResidenceCountry);
            }
        }
        return taxResidenceCountries;
    }
}
