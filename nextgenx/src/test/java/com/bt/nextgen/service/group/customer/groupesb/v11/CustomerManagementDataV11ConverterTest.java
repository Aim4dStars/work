package com.bt.nextgen.service.group.customer.groupesb.v11;

import au.com.westpac.gn.common.xsd.identifiers.v1.AccountArrangementIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.AlternateName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Assessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmailAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MoneyLaunderingAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ProductArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress;
import com.bt.nextgen.service.avaloq.account.BankAccountImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.IndividualDetails;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.phone.v11.CustomerPhoneV11Converter;
import com.bt.nextgen.service.integration.user.CISKey;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 18/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class CustomerManagementDataV11ConverterTest {
    public static final String CUSTOMER_NUMBER = "12312345";
    @Mock
    private RetrieveDetailsAndArrangementRelationshipsForIPsResponse response;

    @Mock
    private Organisation org;
    @Mock
    private CustomerManagementRequest request;
    @Mock
    private Individual individual;
    @Mock
    private IndividualName individualName;

    @Test
    public void convertResponseInAddressModelShouldConvertAddressIntoTitleCase() {

        CISKey cisKey = CISKey.valueOf("12345");
        List<PostalAddressContactMethod> postalAddressContactMethods = new ArrayList<>();
        PostalAddressContactMethod addressContactMethod = new PostalAddressContactMethod();
        MaintenanceAuditContext au = new MaintenanceAuditContext();
        au.setIsActive(Boolean.TRUE);
        addressContactMethod.setAuditContext(au);
        addressContactMethod.setUsage("REGISTERED ADDRESS");
        StandardPostalAddress hasAddress = new StandardPostalAddress();
        hasAddress.setCountry("AUSTRALIA");
        hasAddress.setState("NSW");
        hasAddress.setBuildingName("BUILDING NAME");
        hasAddress.setCity("CITY");
        hasAddress.setStreetName("STREET NAME");
        hasAddress.setUnitNumber("UNIT 58");
        addressContactMethod.setHasAddress(hasAddress);
        postalAddressContactMethods.add(addressContactMethod);

        when(request.getCISKey()).thenReturn(cisKey);
        when(response.getOrganisation()).thenReturn(org);
        when(response.getOrganisation().getHasPostalAddressContactMethod()).thenReturn(postalAddressContactMethods);
        when(request.getInvolvedPartyRoleType()).thenReturn(RoleType.ORGANISATION);

        CustomerData cusData = CustomerManagementDataV11Converter.convertResponseInAddressModel(response, request);

        assertThat(cusData.getAddress().getCountry(), is("AUSTRALIA"));
        assertThat(cusData.getAddress().getState(), is("NSW"));
        assertThat(cusData.getAddress().getBuilding(), is("Building Name"));
        assertThat(cusData.getAddress().getCity(), is("City"));
        assertThat(cusData.getAddress().getStreetName(), is("Street Name"));
        assertThat(cusData.getAddress().getUnit(), is("Unit 58"));
    }

    @Test
    public void convertResponseInAddressModelShouldCheckObsoleteFlag() {

        CISKey cisKey = CISKey.valueOf("12345");
        List<PostalAddressContactMethod> postalAddressContactMethods = new ArrayList<>();
        PostalAddressContactMethod addressContactMethod = new PostalAddressContactMethod();
        MaintenanceAuditContext au = new MaintenanceAuditContext();
        au.setIsActive(Boolean.TRUE);
        addressContactMethod.setAuditContext(au);
        addressContactMethod.setUsage("REGISTERED ADDRESS");
        StandardPostalAddress hasAddress = new StandardPostalAddress();
        hasAddress.setCountry("AUSTRALIA");
        hasAddress.setState("NSW");
        hasAddress.setBuildingName("BUILDING NAME");
        hasAddress.setCity("CITY");
        hasAddress.setStreetName("STREET NAME");
        hasAddress.setUnitNumber("UNIT 58");

        addressContactMethod.setHasAddress(hasAddress);
        addressContactMethod.setValidityStatus(CustomerPhoneV11Converter.OBSOLETE);
        addressContactMethod.setUsage("R");
        postalAddressContactMethods.add(addressContactMethod);

        when(request.getCISKey()).thenReturn(cisKey);
        when(response.getIndividual()).thenReturn(individual);
        when(response.getIndividual().getHasPostalAddressContactMethod()).thenReturn(postalAddressContactMethods);
        when(request.getInvolvedPartyRoleType()).thenReturn(RoleType.INDIVIDUAL);

        CustomerData cusData = CustomerManagementDataV11Converter.convertResponseInAddressModel(response, request);

        assertNull(cusData.getAddress());
    }

    @Test
    public void convertResponseInEmailModelShouldCheckObsoleteFlag() {

        CISKey cisKey = CISKey.valueOf("12345");
        List<EmailAddressContactMethod> emailAddressContactMethods = new ArrayList<>();
        EmailAddressContactMethod emailContactMethod = new EmailAddressContactMethod();
        MaintenanceAuditContext au = new MaintenanceAuditContext();
        au.setIsActive(Boolean.TRUE);
        emailContactMethod.setAuditContext(au);
        emailContactMethod.setUsage("R");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("test@test.com");


        emailContactMethod.setHasAddress(emailAddress);
        emailContactMethod.setValidityStatus(CustomerPhoneV11Converter.OBSOLETE);
        emailAddressContactMethods.add(emailContactMethod);

        when(request.getCISKey()).thenReturn(cisKey);
        when(response.getIndividual()).thenReturn(individual);
        when(response.getIndividual().getHasEmailAddressContactMethod()).thenReturn(emailAddressContactMethods);
        when(request.getInvolvedPartyRoleType()).thenReturn(RoleType.INDIVIDUAL);

        CustomerData cusData = CustomerManagementDataV11Converter.convertResponseInEmailModel(response);

        assertThat(cusData.getEmails().size(), Is.is(0));
    }

    @Test
    public void convertResponseToPreferredNameModelShouldReturnThePrefNameInTitleCase() {

        List<AlternateName> names = new ArrayList<>();
        AlternateName alternateName = new AlternateName();
        alternateName.setName("SOME PREFERRED NAME IN CAPS");
        alternateName.setIsPreferred("Y");
        names.add(alternateName);

        when(response.getIndividual()).thenReturn(individual);
        when(response.getIndividual().getHasForName()).thenReturn(individualName);
        when(response.getIndividual().getHasForName().getHasAlternateName()).thenReturn(names);

        CustomerData cusData = CustomerManagementDataV11Converter.convertResponseToPreferredNameModel(response);
        assertThat(cusData.getPreferredName(), is("Some Preferred Name In Caps"));
    }

    @Test
    public void testIndividualDetailsConverter_withAllDetails() {
        when(response.getIndividual()).thenReturn(getIndividual("Mr", "Homer", "Jay", "Simpson", "Male", 1, 5, 1980, "Yes", "WBC", "N"));
        IndividualDetails individual = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response).getIndividualDetails();
        assertThat(individual.getTitle(), is("Mr"));
        assertThat(individual.getFirstName(), is("Homer"));
        assertThat(individual.getLastName(), is("Simpson"));
        assertThat(individual.getGender(), is("Male"));
        assertThat(individual.getDateOfBirth(), is("01/05/1980"));
        assertThat(individual.getUserName(), is(CUSTOMER_NUMBER));
        assertTrue(individual.getIdVerified());
        assertThat(individual.getMiddleNames(), contains("Jay"));
    }

    @Test
    public void testIdvStatusForIndividualPreAmlWbc() {
        when(response.getIndividual()).thenReturn(getIndividualWithIdvStatusOnly("Yes", "WBC", "Y"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }

    @Test
    public void testIdvStatusForIndividualPreAmlNonWbc() {
        when(response.getIndividual()).thenReturn(getIndividualWithIdvStatusOnly("Yes", "Non WBC", "Y"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }


    @Test
    public void testIdvStatusForIndividualPostAmlWbc() {
        when(response.getIndividual()).thenReturn(getIndividualWithIdvStatusOnly("Yes", "WBC", "N"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }

    @Test
    public void testIdvStatusForIndividualPostAmlNonWbc() {
        when(response.getIndividual()).thenReturn(getIndividualWithIdvStatusOnly("Yes", "Non WBC", "N"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }

    @Test
    public void testIdvStatusForIndividualPostAmlWbcNop() {
        when(response.getIndividual()).thenReturn(getIndividualWithIdvStatusOnly("Yes", "WBC NOP", "N"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(false));
    }


    @Test
    public void testIndividualDetailsConverter_withWbcIdvMethod() {
        when(response.getIndividual()).thenReturn(getIndividual("Yes", "WBC"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }

    @Test
    public void testIndividualDetailsConverter_withNonWbcIdvMethod() {
        when(response.getIndividual()).thenReturn(getIndividual("Yes", "Non WBC"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }

    @Test
    public void testIndividualDetailsConverter_withNonWbcIdvMethodIgnoreCase() {
        when(response.getIndividual()).thenReturn(getIndividual("Yes", "non wbc"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(true));
    }

    @Test
    public void testIndividualDetailsConverter_withInvalidIdvMethod() {
        when(response.getIndividual()).thenReturn(getIndividual("Yes", "WBC NOP"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(false));
    }

    @Test
    public void testIndividualDetailsConverter_withNoIdvVerification() {
        when(response.getIndividual()).thenReturn(getIndividual(null, null));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIdVerified(), is(false));
    }

    @Test
    public void testIndividualDetailsConverter_withNoForeignRegistration() {
        when(response.getIndividual()).thenReturn(getIndividual(null, null));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIsForeignRegistered(), is("N"));
    }

    @Test
    public void testIndividualDetailsConverter_withForeignRegistration() {
        when(response.getIndividual()).thenReturn(getIndividual("Mr", "FirstName","MiddleName", "LastName", "Male", 5, 12, 1990, "Y", "WBC", "Y"));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertThat(customerData.getIndividualDetails().getIsForeignRegistered(), is("Y"));
    }

    @Test
    public void testIndividualDetailsConverter_withNoCRSData() {
        when(response.getIndividual()).thenReturn(getIndividual("Mr", "FirstName","MiddleName", "LastName", "Male", 5, 12, 1990, "Y", "WBC", null));
        CustomerData customerData = CustomerManagementDataV11Converter.convertResponseToIndividualDetailsModel(response);
        assertNull(customerData.getIndividualDetails().getIsForeignRegistered());
    }

    private Individual getIndividual(String title, String firstName, String middleName, String surname, String gender,
                                     int day, int month, int year, String assessmentStatus, String assessmentMethod, String isForeignRegistered) {
        Individual individual = new Individual();
        individual.setHasForName(getHasForNameDetails(title, firstName, middleName, surname));
        individual.setGender(gender);

        individual.setBirthDate(getBirthDate(day, month, year));
        individual.setHasIdentityVerificationAssessment(getIdentityVerificationAssessment(assessmentStatus, assessmentMethod));
        individual.setCustomerIdentifier(getCustomerIdentifier(CUSTOMER_NUMBER));
        individual.setIsForeignRegistered(isForeignRegistered);
        return individual;
    }

    private CustomerIdentifier getCustomerIdentifier(String customerNumber) {
        CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        customerIdentifier.setCustomerNumber(customerNumber);
        return customerIdentifier;
    }

    private Individual getIndividualWithIdvStatusOnly(String assessmentStatus, String assessmentMethod, String preCommencementCategory) {
        Individual individual = new Individual();
        individual.setCustomerIdentifier(getCustomerIdentifier(CUSTOMER_NUMBER));
        individual.setHasAntiMoneyLaunderingAssessment(getAntiMoneyLaunderingAssessment(preCommencementCategory));
        individual.setHasIdentityVerificationAssessment(getIdentityVerificationAssessment(assessmentStatus, assessmentMethod));
        return individual;
    }

    private MoneyLaunderingAssessment getAntiMoneyLaunderingAssessment(String value) {
        MoneyLaunderingAssessment moneyLaunderingAssessment = new MoneyLaunderingAssessment();
        moneyLaunderingAssessment.setPreCommencementCategory(value);
        return moneyLaunderingAssessment;
    }

    private Individual getIndividual(String assessmentStatus, String assessmentMethod) {
        return getIndividual(null, null, null, null, null, 1, 1, 1, assessmentStatus, assessmentMethod, "N");
    }

    private JAXBElement<XMLGregorianCalendar> getBirthDate(int day, int month, int year) {
        ObjectFactory factory = new ObjectFactory();
        XMLGregorianCalendar birthdate = new XMLGregorianCalendarImpl();
        birthdate.setDay(day);
        birthdate.setMonth(month);
        birthdate.setYear(year);
        return factory.createIndividualBirthDate(birthdate);
    }

    private Assessment getIdentityVerificationAssessment(String assessmentStatus, String assessmentMethod) {
        Assessment assessment = new Assessment();
        assessment.setAssessmentStatus(assessmentStatus);
        assessment.setAssessmentMethod(assessmentMethod);
        return assessment;
    }

    private IndividualName getHasForNameDetails(String prefix, String firstName, String middleName, String surname) {
        IndividualName nameDetails = new IndividualName();
        nameDetails.setPrefixTitle(prefix);
        nameDetails.setFirstName(firstName);
        nameDetails.setLastName(surname);
        if (isNotBlank(middleName)) {
            nameDetails.getMiddleNames().add(middleName);
        }
        return nameDetails;
    }

    @Test
    public void testBankAccountDetails_withNickName() {
        InvolvedPartyArrangementRole account = getAccount("062100", "111111", "8f3978d0cd1243d996e9fe4c969e8946", "My own account");
        BankAccountImpl result = CustomerManagementDataV11Converter.convertResponseToBankAccountModel(account, "Product Name1");
        assertThat(result.getBsb(), is("062100"));
        assertThat(result.getAccountNumber(), is("111111"));
        assertThat(result.getName(), is("My own account"));
    }

    @Test
    public void testBankAccountDetails_withProductName() {
        InvolvedPartyArrangementRole account = getAccount("062100", "111111", "8f3978d0cd1243d996e9fe4c969e8946", "");
        BankAccountImpl result = CustomerManagementDataV11Converter.convertResponseToBankAccountModel(account, "Product Name1");
        assertThat(result.getBsb(), is("062100"));
        assertThat(result.getAccountNumber(), is("111111"));
        assertThat(result.getName(), is("Product Name1"));
    }

    @Test
    public void testBankAccountDetails_withEmptyDetails() {
        InvolvedPartyArrangementRole account = getAccount(null, null, null, null);
        BankAccountImpl result = CustomerManagementDataV11Converter.convertResponseToBankAccountModel(account, "Product Name1");
        assertNull(result);
    }

    @Test
    public void testBankAccountDetails_withNullInput() {
        InvolvedPartyArrangementRole account = null;
        BankAccountImpl result = CustomerManagementDataV11Converter.convertResponseToBankAccountModel(account, "Product Name1");
        assertNull(result);
    }

    @Test
    public void testBankAccountDetails_withNoProductName() {
        InvolvedPartyArrangementRole account = getAccount("062100", "111111", "8f3978d0cd1243d996e9fe4c969e8946", null);
        BankAccountImpl result = CustomerManagementDataV11Converter.convertResponseToBankAccountModel(account, null);
        assertThat(result.getBsb(), is("062100"));
        assertThat(result.getAccountNumber(), is("111111"));
        assertNull(result.getName());
    }

    private InvolvedPartyArrangementRole getAccount(String bsb, String accountNumber, String cpc, String nickName) {
        InvolvedPartyArrangementRole account = new InvolvedPartyArrangementRole();
        if (bsb != null) {
            ProductArrangement context = new ProductArrangement();
            AccountArrangementIdentifier accountArrangement = new AccountArrangementIdentifier();
            accountArrangement.setAccountNumber(accountNumber);
            accountArrangement.setBsbNumber(bsb);
            accountArrangement.setCanonicalProductCode(cpc);
            context.setAccountArrangementIdentifier(accountArrangement);
            account.setHasForContext(context);
            account.setNickName(nickName);
        }
        return account;
    }
}