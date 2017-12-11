package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.GeographicArea;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.ModifyIndividualIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualip.v5.svc0338.RegistrationArrangement;
import ch.lambdaj.Lambda;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil.convertToXMLGregorianCalendar;

/**
 * This class would build the request xml for modifying Individual TIN details
 * Created by M035995 on 9/01/2017.
 */
public class GroupEsbIndividualTaxResidenceUpdateRequestBuilder {

    private static final String REGI_NUMBER_TYPE_FOREIGN = "FOREIGN";

    private GroupEsbIndividualTaxResidenceUpdateRequestBuilder() {

    }

    /**
     * This method creates an instance of {@link ModifyIndividualIPRequest} to be sent as request to Service 338 from Group ESB
     *
     * @param customerData instance of {@link CustomerData}
     *
     * @return object of {@link ModifyIndividualIPRequest}
     */
    public static ModifyIndividualIPRequest createModifyIndividualIPRequest(CustomerData customerData) {
        final ObjectFactory objectFactory = new ObjectFactory();
        final ModifyIndividualIPRequest individualIPRequest = objectFactory.createModifyIndividualIPRequest();
        final Individual individual = objectFactory.createIndividual();

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier = factory.createInvolvedPartyIdentifier();
        involvedPartyIdentifier = createInvolvedPartyIdentifier(involvedPartyIdentifier,
                customerData.getRequest().getCISKey().getId());
        final List<InvolvedPartyIdentifier> involvedPartyIdentifierList = new ArrayList<>();
        involvedPartyIdentifierList.add(involvedPartyIdentifier);

        individual.getInvolvedPartyIdentifier().addAll(involvedPartyIdentifierList);
        // We can get a maximum of 2 FOREIGN blocks; retrieve it from the list of residence countries and then validate
        final List<TaxResidenceCountry> taxResidenceCountryList = Lambda.select(customerData.getTaxResidenceCountries(),
                Lambda.having(Lambda.on(TaxResidenceCountry.class).getResidenceCountry(), Matchers.equalTo(REGI_NUMBER_TYPE_FOREIGN)));
        final TaxResidenceCountry taxResidenceCountry = Lambda.selectFirst(taxResidenceCountryList,
                Lambda.having(Lambda.on(TaxResidenceCountry.class).getTin(), Matchers.equalTo("N")));
        individual.setIsForeignRegistered(taxResidenceCountry == null || taxResidenceCountry.getEndDate() != null ? "Y" : "N");
        individual.getHasForeignRegistration().addAll(createHasForeignRegistrations(customerData));

        individualIPRequest.setIndividual(individual);

        return individualIPRequest;
    }

    private static InvolvedPartyIdentifier createInvolvedPartyIdentifier(InvolvedPartyIdentifier involvedPartyIdentifier,
                                                                         String cisKey) {
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setSourceSystem(ServiceConstants.CIS);
        involvedPartyIdentifier.setInvolvedPartyId(cisKey);
        return involvedPartyIdentifier;
    }

    private static List<RegistrationArrangement> createHasForeignRegistrations(CustomerData customerData) {
        final List<RegistrationArrangement> foreignRegistrationList = new ArrayList<>();
        RegistrationArrangement registrationArrangement = null;
        RegistrationIdentifier registrationIdentifier = null;
        GeographicArea geographicArea = null;
        MaintenanceAuditContext auditContext = null;
        for (TaxResidenceCountry taxResidenceCountry : customerData.getTaxResidenceCountries()) {
            registrationArrangement = new RegistrationArrangement();
            auditContext = new MaintenanceAuditContext();

            if (taxResidenceCountry.getExemptionReason() != null) {
                registrationArrangement.setNoteText(taxResidenceCountry.getExemptionReason());
            }
            registrationArrangement.setStartDate(convertToXMLGregorianCalendar(taxResidenceCountry.getStartDate()));
            registrationArrangement.setEndDate(convertToXMLGregorianCalendar(taxResidenceCountry.getEndDate()));

            //If end date is null then set the value to true else false
            auditContext.setIsActive(taxResidenceCountry.getEndDate() == null);

            registrationIdentifier = new RegistrationIdentifier();
            registrationIdentifier.setRegistrationNumber(taxResidenceCountry.getTin());

            if (REGI_NUMBER_TYPE_FOREIGN.equalsIgnoreCase(taxResidenceCountry.getResidenceCountry())) {
                registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.FOREIGN);
            }
            else {
                registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.TIN);
                geographicArea = new GeographicArea();
                geographicArea.setCountry(taxResidenceCountry.getResidenceCountry());
                registrationArrangement.setIsIssuedAt(geographicArea);
            }
            registrationArrangement.setRegistrationIdentifier(registrationIdentifier);

            // Set the version number in the Audit context only
            if (StringUtils.isNotEmpty(taxResidenceCountry.getVersionNumber())) {
                auditContext.setVersionNumber(taxResidenceCountry.getVersionNumber());
            }
            registrationArrangement.setAuditContext(auditContext);
            foreignRegistrationList.add(registrationArrangement);
        }
        return foreignRegistrationList;
    }
}
