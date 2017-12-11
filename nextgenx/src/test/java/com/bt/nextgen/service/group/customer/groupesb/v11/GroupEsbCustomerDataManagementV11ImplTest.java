package com.bt.nextgen.service.group.customer.groupesb.v11;

import au.com.westpac.gn.common.xsd.identifiers.v1.AccountArrangementIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.GeographicArea;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ProductArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.core.repository.WestpacProduct;
import com.bt.nextgen.core.repository.WestpacProductsRepository;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.address.v11.AddressAdapterV11;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 17/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerDataManagementV11ImplTest {

    /*
     * @InjectMocks private GroupEsbCustomerDataManagementImpl
     * groupEsbCustomerDataManagement;
     */

    @InjectMocks
    private GroupEsbCustomerDataManagementV11Impl groupEsbCustomerDataManagementV11;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private WestpacProductsRepository westpacProductsRepository;

    @Mock
    private RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject;

    @Mock
    Individual individual;

    @Mock
    private ServiceStatus serviceStatus;

    @Mock
    private PaginationInstruction paginationInstruction;

    private void runCommonMockServices() {
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);

        CorrelatedResponse correlatedResponse = new CorrelatedResponse( new CorrelationIdWrapper(), responseObject);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(), anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(responseObject.getServiceStatus()).thenReturn(serviceStatus);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));
        when(responseObject.getIndividual()).thenReturn(individual);
    }

    private StatusInfo getStatus(Level level) {
        StatusInfo status = new StatusInfo();
        status.setLevel(level);
        return status;
    }

    @Test
    public void test_retrieveBankDetails_success() {
        runCommonMockServices();
        List<InvolvedPartyArrangementRole> accountList = Arrays.asList(getAccount("123456789", "062100", "13d46777ec304"));
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(accountList);

        when(westpacProductsRepository.load("13d46777ec304")).thenReturn(getWestpacProduct("Westpac Choice", "Consumer Transaction Accounts", "CHOICE", "Y"));

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(1));
        assertThat(response.getBankAccounts().get(0).getAccountNumber(), is("123456789"));
        assertThat(response.getBankAccounts().get(0).getName(), is("Westpac Choice"));
        assertNull(response.getEmails());
        assertNull(response.getPhoneNumbers());
        assertNull(response.getAddress());
        assertNull(response.getIndividualDetails());
    }

    @Test
    public void test_retrieveBankDetails_filterResults_byCategory() {
        runCommonMockServices();

        List<InvolvedPartyArrangementRole> accountList = Arrays.asList(getAccount("1111111111", "062100", "13d46777ec304"),
                getAccount("222222222", "062100", "9823n23u40231"));
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(accountList);

        when(westpacProductsRepository.load("13d46777ec304")).thenReturn(getWestpacProduct("Westpac Choice", "Consumer Transaction Accounts", "CHOICE", "Y"));
        when(westpacProductsRepository.load("9823n23u40231")).thenReturn(getWestpacProduct("Westpac Mortgage", "Mortgage", "CHOICE", "Y"));

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(1));
        assertThat(response.getBankAccounts().get(0).getAccountNumber(), is("1111111111"));
        assertThat(response.getBankAccounts().get(0).getName(), is("Westpac Choice"));
    }

    @Test
    public void test_retrieveBankDetails_filterResults_byCategoryProductSystem() {
        runCommonMockServices();

        List<InvolvedPartyArrangementRole> accountList = Arrays.asList(getAccount("1111111111", "062100", "13d46777ec304"),
                getAccount("222222222", "062100", "9823n23u40231"));
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(accountList);

        when(westpacProductsRepository.load("13d46777ec304")).thenReturn(getWestpacProduct("Westpac Choice", "Consumer Transaction Accounts", "CHOICE", "Y"));
        when(westpacProductsRepository.load("9823n23u40231")).thenReturn(getWestpacProduct("Westpac Mortgage", "Consumer Transaction Accounts", "EVERGREEN", "Y"));

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(1));
        assertThat(response.getBankAccounts().get(0).getAccountNumber(), is("1111111111"));
        assertThat(response.getBankAccounts().get(0).getName(), is("Westpac Choice"));
    }

    @Test
    public void test_retrieveBankDetails_filterResults_byFundsTransferFrom() {
        runCommonMockServices();

        List<InvolvedPartyArrangementRole> accountList = Arrays.asList(getAccount("1111111111", "062100", "13d46777ec304"),
                getAccount("222222222", "062100", "9823n23u40231"));
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(accountList);

        when(westpacProductsRepository.load("13d46777ec304")).thenReturn(getWestpacProduct("Westpac Choice", "Consumer Transaction Accounts", "CHOICE", "Y"));
        when(westpacProductsRepository.load("9856g45068456")).thenReturn(getWestpacProduct("Westpac Choice", "Consumer Transaction Accounts", "CHOICE", "N"));

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(1));
        assertThat(response.getBankAccounts().get(0).getAccountNumber(), is("1111111111"));
        assertThat(response.getBankAccounts().get(0).getName(), is("Westpac Choice"));
    }

    @Test
    public void test_retrieveBankDetails_noResults() {
        runCommonMockServices();
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(null);

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(0));
    }

    @Test
    public void test_retrieveBankDetails_Error() {
        runCommonMockServices();
        ServiceErrors serviceErrors = mock(ServiceErrorsImpl.class);
        when(serviceErrors.hasErrors()).thenReturn(true);
        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), serviceErrors);

        assertThat(response.getBankAccounts().size(), is(0));
    }
    @Test
    public void test_retrieveBankDetails_NonBankAccount() {
        runCommonMockServices();
        ServiceErrors serviceErrors = mock(ServiceErrorsImpl.class);
        when(serviceErrors.hasErrors()).thenReturn(true);
        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("XYZ"), serviceErrors);

        assertThat(response.getBankAccounts().size(), is(0));
    }

    @Test
    public void test_retrieve_detailsOtherThanBank() {
        runCommonMockServices();

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ADDRESS_UPDATE,
                CustomerManagementOperation.REGISTRATION_STATE);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request, null,
                new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(0));
    }

    @Test
    public void test_retrieveBankDetails_noCpc() {
        runCommonMockServices();
        List<InvolvedPartyArrangementRole> accountList = Arrays.asList(getAccount("123456789", "062100", null));
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(accountList);

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());

        assertThat(response.getBankAccounts().size(), is(0));
    }

    @Test
    public void test_retrieveBankDetails_noPaginatedResults() {
        runCommonMockServices();
        when(responseObject.getArrangementPaginationInstruction()).thenReturn(paginationInstruction);
        when(paginationInstruction.isIsMoreRecordsAvailable()).thenReturn(false);
        List<InvolvedPartyArrangementRole> accountList = Arrays.asList(getAccount("123456789", "062100", "13d46777ec304"));
        when(individual.getIsPlayingRoleInArrangement()).thenReturn(accountList);

        when(westpacProductsRepository.load("13d46777ec304")).thenReturn(getWestpacProduct("Westpac Choice", "Consumer Transaction Accounts", "CHOICE", "Y"));

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ARRANGEMENTS);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());
        assertThat(response.getBankAccounts().size(), is(1));
        assertThat(response.getBankAccounts().get(0).getAccountNumber(), is("123456789"));
        assertThat(response.getBankAccounts().get(0).getName(), is("Westpac Choice"));
        assertNull(response.getEmails());
        assertNull(response.getPhoneNumbers());
        assertNull(response.getAddress());
        assertNull(response.getIndividualDetails());
    }

    @Test
    public void testFilterPostalAddress() {
        runCommonMockServices();
        List<PostalAddressContactMethod> addresses = getPostalAddressContactMethods();
        when(individual.getHasPostalAddressContactMethod()).thenReturn(addresses);
        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.ADDRESS_UPDATE);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                Arrays.asList("BANK_ACCOUNT"), new ServiceErrorsImpl());
        Address address = response.getAddress();
        assertNotNull(address);
        assertThat(address, instanceOf(AddressAdapterV11.class));
        assertTrue(((AddressAdapterV11) address).isStandardAddressFormat());
        assertFalse(address.isInternationalAddress());
    }

    @Test
    public void testRetrieveTaxDetails_noResults(){
        runCommonMockServices();

        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                null, new ServiceErrorsImpl());
        assertThat(response.getTaxResidenceCountries().size(), is(0));
    }

    @Test
    public void testRetrieveTaxDetails_withResults(){
        runCommonMockServices();
        List<RegistrationArrangement> hasForeignRegistration = new ArrayList<>();

        RegistrationArrangement arrangement = getRegistrationArrangement();

        hasForeignRegistration.add(arrangement);
        when(individual.getHasForeignRegistration()).thenReturn(hasForeignRegistration);
        CustomerManagementRequest request = createCustomerManagementRequest(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        CustomerData response = groupEsbCustomerDataManagementV11.retrieveCustomerInformation(request,
                null, new ServiceErrorsImpl());

        assertThat(response.getTaxResidenceCountries().size(), is(1));
        TaxResidenceCountry taxResidenceCountry = response.getTaxResidenceCountries().get(0);
        assertNull(taxResidenceCountry.getExemptionReason());
        assertThat(taxResidenceCountry.getResidenceCountry(), is("IND"));
        assertThat(taxResidenceCountry.getTin(), is("TIN"));

    }

    private RegistrationArrangement getRegistrationArrangement() {
        RegistrationArrangement arrangement = new RegistrationArrangement();
        GeographicArea geographicArea = new GeographicArea();
        geographicArea.setCountry("IND");
        arrangement.setIsIssuedAt(geographicArea);
        arrangement.setNoteText("RC000004");
        arrangement.setStartDate(new JAXBElement<XMLGregorianCalendar>(new QName("XMLGregorianCalendar"), XMLGregorianCalendar.class, XMLGregorianCalendarUtil.getXMLGregorianCalendarNow()));
        arrangement.setAuditContext(new MaintenanceAuditContext());
        RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
        registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.TIN);
        registrationIdentifier.setRegistrationNumber("TIN");
        arrangement.getRegistrationIdentifier().add(registrationIdentifier);
        return arrangement;
    }

    private List<PostalAddressContactMethod> getPostalAddressContactMethods() {
        PostalAddressContactMethod address = new PostalAddressContactMethod();
        MaintenanceAuditContext auditContext = new MaintenanceAuditContext();
        auditContext.setIsActive(true);
        address.setAuditContext(auditContext);
        address.setUsage("R");
        StandardPostalAddress standardAddress = new StandardPostalAddress();
        standardAddress.setCountry("AU");
        address.setHasAddress(standardAddress);
        return Arrays.asList(address);
    }

    private WestpacProduct getWestpacProduct(String name, String category, String categoryProductSystem, String fundsTransferFrom) {
        WestpacProduct westpacProduct = new WestpacProduct();
        westpacProduct.setName(name);
        westpacProduct.setCategory(category);
        westpacProduct.setCategoryProductSystem(categoryProductSystem);
        westpacProduct.setFundsTransferFrom(fundsTransferFrom);
        return westpacProduct;
    }

    private InvolvedPartyArrangementRole getAccount(String number, String bsb, String cpc) {
        InvolvedPartyArrangementRole account = new InvolvedPartyArrangementRole();
        ProductArrangement hasForContext = new ProductArrangement();
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setAccountNumber(number);
        accountArrangementIdentifier.setBsbNumber(bsb);
        accountArrangementIdentifier.setCanonicalProductCode(cpc);
        hasForContext.setAccountArrangementIdentifier(accountArrangementIdentifier);
        account.setHasForContext(hasForContext);
        return account;
    }

    private CustomerManagementRequest createCustomerManagementRequest(CustomerManagementOperation... operations) {
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setOperationTypes(Arrays.asList(operations));
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        request.setCISKey(CISKey.valueOf("123456789"));
        return request;
    }
}