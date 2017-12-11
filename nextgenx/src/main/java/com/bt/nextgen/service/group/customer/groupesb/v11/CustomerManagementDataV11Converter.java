package com.bt.nextgen.service.group.customer.groupesb.v11;

import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.AlternateName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.service.avaloq.account.BankAccountImpl;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.IndividualDetails;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.address.v11.AddressAdapterV11;
import com.bt.nextgen.service.group.customer.groupesb.address.v11.CustomerAddressManagementV11Converter;
import com.bt.nextgen.service.group.customer.groupesb.email.CustomerEmail;
import com.bt.nextgen.service.group.customer.groupesb.phone.v11.CustomerPhoneV11Converter;
import com.bt.nextgen.service.integration.IntegrationServiceUtil;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.util.CaseConverterUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

public class CustomerManagementDataV11Converter {

    private static String TIN_EXEMPTION_ID = "RC000004";

    private static final Logger logger = LoggerFactory.getLogger(CustomerManagementDataV11Converter.class);

    private CustomerManagementDataV11Converter() {
    }

    /**
     * Method to transform preferred name from gesb service response into
     * customerData Assuming only one Standard address to be in the response
     * with status "R" or "G" and in state "Active"
     *
     * @param response
     * @return customerData
     */
    public static CustomerData convertResponseInAddressModel(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response, CustomerManagementRequest request) {

        logger.info("Converting address response to model for user {}", request.getCISKey().getId());
        CustomerData customerData = new CustomerDataImpl();
        customerData.setRequest(request);

        if (request.getInvolvedPartyRoleType().equals(RoleType.INDIVIDUAL)) {
            if (response.getIndividual() != null && response.getIndividual().getHasPostalAddressContactMethod() != null) {
                logger.info("Address retrieved is for Individual {}", request.getCISKey().getId());
                PostalAddressContactMethod address = filterPostalAddressForIndividual(response);
                if (address != null) {
                    Address returnedAddress = CustomerAddressManagementV11Converter.convertAddressFromResponse(address);
                    // Usage 'R' indicates domicile. Residential and Mailing is considered as same.
                    ((AddressAdapterV11)returnedAddress).setDomicile(true);
                    ((AddressAdapterV11)returnedAddress).setMailingAddress(true);
                    customerData.setAddress(returnedAddress);
                }
            }
        } else {
            logger.info("Address retrieved is for Organization {}", request.getCISKey().getId());
            PostalAddressContactMethod address = filterPostalAddressForOrganization(response);
            if (address != null) {
                Address returnedAddress = CustomerAddressManagementV11Converter.convertAddressFromResponse(address);
                customerData.setAddress(returnedAddress);
            }
        }
        return customerData;
    }

    //TODO : Fix the usage status : Pending with GCM team
    public static PostalAddressContactMethod filterPostalAddressForIndividual(
            RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        for (PostalAddressContactMethod address : response.getIndividual().getHasPostalAddressContactMethod()) {
            if (!CustomerPhoneV11Converter.OBSOLETE.equalsIgnoreCase(address.getValidityStatus()) && address.getAuditContext().isIsActive() && (
                    ServiceConstants.INDIVIDUAL_ADDRESS_USAGE.equalsIgnoreCase(address.getUsage()) || "R".equalsIgnoreCase(address.getUsage())
            )
            /*
             * && (address.getPriorityLevel() != null ?
             * address.getPriorityLevel(
             * ).equals(au.com.westpac.gn.involvedpartymanagement
             * .services.involvedpartymanagement.xsd
             * .retrievedetailsandarrangementrelationshipsforips
             * .v11.svc0258.PriorityLevel.PRIMARY) : true)
             */) {
                return address;
            }
        }
        return null;
    }

    //TODO : Fix the usage status : Pending with GCM team
    public static PostalAddressContactMethod filterPostalAddressForOrganization(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        for (PostalAddressContactMethod address : response.getOrganisation().getHasPostalAddressContactMethod()) {
            if (address.getAuditContext().isIsActive() && (
                    address.getUsage().equalsIgnoreCase(ServiceConstants.ORG_ADDRESS_USAGE) || "G".equalsIgnoreCase(address.getUsage())
            )
            /*
             * && (address.getPriorityLevel() != null ?
             * address.getPriorityLevel(
             * ).equals(au.com.westpac.gn.involvedpartymanagement
             * .services.involvedpartymanagement.xsd
             * .retrievedetailsandarrangementrelationshipsforips
             * .v11.svc0258.PriorityLevel.PRIMARY) : true)
             */) {
                return address;
            }
        }
        return null;
    }

    /**
     * Method to transform preferred name from gesb service response into
     * customerData
     *
     * @param response
     * @return CustomerData
     */
    public static CustomerData convertResponseToPreferredNameModel(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        List<AlternateName> names = response.getIndividual().getHasForName().getHasAlternateName();
        CustomerData customerData = new CustomerDataImpl();

        logger.info("Transforming response from gesb for preferred name");
        for (AlternateName name : names) {
            if ("Y".equals(name.getIsPreferred().toString())) {
                customerData.setPreferredName(name.getName());
                CaseConverterUtil.convertToTitleCase(customerData);
            }
        }
        return customerData;
    }

    /**
     * Method to transform email addresses from gesb service response into
     * customerData
     *
     * @param response
     * @return CustomerData
     */
    public static CustomerData convertResponseInEmailModel(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        CustomerData customerData = new CustomerDataImpl();

        List<Email> emailsList = new ArrayList<>();
        List<EmailAddressContactMethod> emailAddressContactMethods = response.getIndividual().getHasEmailAddressContactMethod();
        for (EmailAddressContactMethod emailAddressContactMethod : emailAddressContactMethods) {
            if (null != emailAddressContactMethod && null != emailAddressContactMethod.getHasAddress() && !CustomerPhoneV11Converter.OBSOLETE.equalsIgnoreCase(emailAddressContactMethod.getValidityStatus())) {
                CustomerEmail email = new CustomerEmail();
                email.setEmail(emailAddressContactMethod.getHasAddress().getEmailAddress());
                emailsList.add(email);
            }
        }

        customerData.setEmails(emailsList);
        return customerData;
    }

    /**
     * Method to transform an account from gesb service response into a
     * BankAccount
     *
     * @param account
     * @param productName
     * @return CustomerData
     */
    public static BankAccountImpl convertResponseToBankAccountModel(InvolvedPartyArrangementRole account, String productName) {
        if (account != null && account.getHasForContext() != null && account.getHasForContext().getAccountArrangementIdentifier() != null) {
            final BankAccountImpl bankAccount = new BankAccountImpl();
            bankAccount.setAccountNumber(account.getHasForContext().getAccountArrangementIdentifier().getAccountNumber());
            bankAccount.setBsb(account.getHasForContext().getAccountArrangementIdentifier().getBsbNumber());
            bankAccount.setName(getBankAccountName(account, productName));
            return bankAccount;
        }
        return null;
    }

    /**
     * Get name of bank account and default to product name if none is returned
     *
     * @param account
     * @param productName
     * @return
     */
    private static String getBankAccountName(InvolvedPartyArrangementRole account, String productName) {
        return StringUtils.isNotBlank(account.getNickName()) ? account.getNickName() : productName;
    }

    /**
     * Method to transform the demographic details retrieved from GCM to the
     * personal information structure for CustomerData
     *
     * @param response
     * @return
     */
    public static CustomerData convertResponseToIndividualDetailsModel(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response) {
        final CustomerData customerData = new CustomerDataImpl();
        final IndividualDetails individualDetails = new IndividualDetails();
        if (response.getIndividual() != null) {
            final Individual individual = response.getIndividual();
            individualDetails.setDateOfBirth(getDobString(individual.getBirthDate()));
            individualDetails.setGender(individual.getGender());
            individualDetails.setIdVerified(getIdvStatus(individual));
            individualDetails.setUserName(individual.getCustomerIdentifier().getCustomerNumber());
            individualDetails.setIsForeignRegistered(individual.getIsForeignRegistered());
            final IndividualName names = individual.getHasForName();
            if (names != null) {
                individualDetails.setTitle(names.getPrefixTitle());
                individualDetails.setFirstName(names.getFirstName());
                individualDetails.setLastName(names.getLastName());
                individualDetails.setMiddleNames(names.getMiddleNames());
            }
        }
        customerData.setIndividualDetails(individualDetails);
        return customerData;
    }

    private static String getDobString(JAXBElement<XMLGregorianCalendar> dob) {
        final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        return fmt.print(IntegrationServiceUtil.convertToDateTime(dob));
    }

    /**
     * Determine the client's IDV status.
     *
     * @param individual
     *            individual being assessed.
     * @return whether this individual counts as being satisfactorily ID
     *         Verified.
     */
    private static boolean getIdvStatus(Individual individual) {
        if (individual.getHasIdentityVerificationAssessment() != null) {
            return "YES".equalsIgnoreCase(individual.getHasIdentityVerificationAssessment().getAssessmentStatus()) && isValidAssessmentMethodForPanorama(individual.getHasIdentityVerificationAssessment().getAssessmentMethod());
        }
        return false;
    }

    private static boolean isValidAssessmentMethodForPanorama(String assessmentMethod) {
        return "WBC".equalsIgnoreCase(assessmentMethod) || "Non WBC".equalsIgnoreCase(assessmentMethod);
    }
}
