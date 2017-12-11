package com.bt.nextgen.api.payments.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.*;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.*;


import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import static org.mockito.Mockito.mock;
import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.account.v1.service.PaymentDtoServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentMethod;
import com.bt.nextgen.payments.web.model.PaymentInterface;
import com.bt.nextgen.payments.web.model.PaymentModel;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequestModel;
import com.bt.nextgen.service.avaloq.payeedetails.CashAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeLimitImpl;
import com.bt.nextgen.service.avaloq.payments.RecurringPaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.bt.nextgen.service.integration.payments.PaymentIntegrationService;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class PaymentDtoServiceImplTest {

    @InjectMocks
    private PaymentDtoServiceImpl paymentDtoServiceImpl;

    @Mock
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    PaymentIntegrationService paymentService;

    @Mock
    CmsService cmsService;

    @Mock
    MoneyAccountIdentifierImpl identifier = new MoneyAccountIdentifierImpl();

    @Mock
    WrapAccountIdentifierImpl wrapidentifier = new WrapAccountIdentifierImpl();

    @Mock
    StaticIntegrationService staticIntegrationService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    Profile userProfile;

    @Mock
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;


    RecurringPaymentDetailsImpl recurPayment = new RecurringPaymentDetailsImpl();

    private static String PAYMENT_LIMITS = "PAYMENT_LIMITS";

    List<PayeeModel> payeeModelList;

    List<PhoneModel> phoneModelList;

    PortfolioRequest portfolioRequest;

    PayeeDetails payeeDetails = null;

    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockHttpSession session;

    private SamlToken token = null;

    @Before
    public void setup() throws Exception {
        payeeModelList = new ArrayList<PayeeModel>();
        PayeeModel payeeModel = new PayeeModel();

        payeeModel.setCode("0120");
        payeeModel.setName("Test");
        payeeModel.setPayeeType(PayeeType.LINKED);
        payeeModel.setReference("123456789");
        payeeModelList.add(payeeModel);

        payeeDetails = getPayeeDetailsObj();

        wrapidentifier.setBpId("1234");

        phoneModelList = new ArrayList<PhoneModel>();
        PhoneModel phoneModel = new PhoneModel();
        phoneModel.setPhoneNumber("1256290873");
        phoneModel.setPrimary(true);
        phoneModelList.add(phoneModel);

        portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId("36846");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        request.setSession(session);

    }

    @Test
    public void testSearch() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        payeeDetails = payee;
        String model = null;

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);

        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("36846", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertTrue(paymentDtoList.size() == 1);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }

    @Test
    public void testAssociatedAccounts_forInvestors() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("123456789"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNotNull(paymentDtoList.get(0).getAssociatedAccounts());
    }
    @Test
    public void testAssociatedAccounts_forInvestorsNullBankAccounts() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("123456789"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);
        CustomerData customerData = getCustomerData();
        customerData.setBankAccounts(null);
        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(customerData);
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
    }
    @Test
    public void testAssociatedAccounts_forInvestorsEmptyBankAccounts() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("123456789"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);
        CustomerData customerData = getCustomerData();
        customerData.setBankAccounts(new ArrayList<BankAccount>());
        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(customerData);
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
    }
    @Test
    public void testAssociatedAccounts_forInvestors_noMovemoneyModel() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = null;
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("123456789"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }
    @Test
    public void testAssociatedAccounts_forNonInvestors() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = null;
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(false);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }
    @Test
    public void testAssociatedAccounts_forNullCISKey() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(null);

        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }

    @Test
    public void testAssociatedAccounts_forNullCISKeyAndNonIndividual() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(null);

        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }

    @Test
    public void testAssociatedAccounts_forEmulation () {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(null);

        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);
        when(profileService.isEmulating()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }

    @Test
    public void testAssociatedAccounts_forEmulationNonInvestor () {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(null);

        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(false);
        when(profileService.isEmulating()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }

    @Test
    public void testAssociatedAccounts_forEmulationNonInvestorPaymentsScreen () {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "false";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(null);

        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(false);
        when(profileService.isEmulating()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertNull(paymentDtoList.get(0).getAssociatedAccounts());
    }


    @Test
    public void testAssociatedAccounts_forNonIndividual() {
        when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        PayeeDetailsImpl payee = new PayeeDetailsImpl();
        payee.setMaxDailyLimit("200000");
        identifier.setMoneyAccountId("76697");
        payee.setMoneyAccountIdentifier(identifier);
        wrapidentifier.setBpId("1234");
        String model = "true";
        Code code = mock(Code.class);

        when(accountIntegrationService.loadAvailableCash(any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(new AvailableCashImpl());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(staticIntegrationService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrorsImpl.class))).thenReturn(code);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.loadPayees("123456789", model, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
    }

    private CustomerData getCustomerData() {
        List<BankAccount>bankAccountList = new ArrayList<>();
        BankAccountImpl bankAccount1 = new BankAccountImpl();
        BankAccountImpl bankAccount2 = new BankAccountImpl();
        bankAccount1.setBsb("012345");
        bankAccount1.setAccountNumber("789456123");
        bankAccount1.setName("Westpac Account 1");
        bankAccountList.add(bankAccount1);

        bankAccount2.setBsb("456123");
        bankAccount2.setAccountNumber("753951456");
        bankAccount2.setName("Westpac Account 2");
        bankAccountList.add(bankAccount2);

        CustomerData response = new CustomerDataImpl();
        response.setBankAccounts(bankAccountList);

        return response;
    }


    @Test
    public void testValidatePayment() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        RecurringPaymentDetailsImpl recurPayment = new RecurringPaymentDetailsImpl();
        PaymentInterface payment = new PaymentModel();
        payment.setPaymentId("OTY90867HJU");
        payment.setPaymentMethod(PaymentMethod.Method.DIRECT_DEBIT.name());
        payment.setAmount("$12.00");
        payment.setDescription("Test Payment");
        payment.setMaccId("1002927871");

        PayeeModel from = new PayeeModel();
        from.setReference("1001");
        from.setCode("262-786");
        from.setName("Adrian Demo Smith");
        from.setPayeeType(PayeeType.PAY_ANYONE);
        from.setPayeeType(PayeeType.PAY_ANYONE);
        payment.setFrom(from);

        PayeeModel to = new PayeeModel();
        to.setReference("");
        to.setCode("12006");
        to.setName("linkedAcc");
        to.setPayeeType(PayeeType.LINKED);
        to.setDescription("Test Payment");
        payment.setTo(to);

        payment.setDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");

        PaymentDto paymentDto = new PaymentDto();
        com.bt.nextgen.api.account.v1.model.AccountKey key = new com.bt.nextgen.api.account.v1.model.AccountKey("36846");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        paymentDto.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountKey(EncodedString.fromPlainText("456213375").toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        paymentDto.setToPayteeDto(toDto);

        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
            paymentDto.setTransactionDate(new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(date));
            recurPayment.setTransactionDate(date);
        } catch (ParseException e) {
            fail();
        }

        when(paymentService.validatePayment(any(RecurringPaymentDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurPayment);

        PaymentDto paymentDo = paymentDtoServiceImpl.validatePayment(paymentDto, new ServiceErrorsImpl());

        assertNotNull(paymentDo);

    }

    @Test
    public void testMakePayment() {

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        PaymentInterface payment = new PaymentModel();
        payment.setPaymentId("OTY90867HJU");
        payment.setPaymentMethod(PaymentMethod.Method.DIRECT_DEBIT.name());
        payment.setAmount("$12.00");
        payment.setDescription("Test Payment");
        payment.setMaccId("1002927871");

        PayeeModel from = new PayeeModel();
        from.setReference("1001");
        from.setCode("262-786");
        from.setName("Adrian Demo Smith");
        from.setPayeeType(PayeeType.PAY_ANYONE);
        from.setPayeeType(PayeeType.PAY_ANYONE);
        payment.setFrom(from);

        PayeeModel to = new PayeeModel();
        to.setReference("");
        to.setCode("12006");
        to.setName("linkedAcc");
        to.setPayeeType(PayeeType.LINKED);
        to.setDescription("Test Payment");
        payment.setTo(to);

        payment.setDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        PaymentDto paymentDto = new PaymentDto();
        com.bt.nextgen.api.account.v1.model.AccountKey key = new com.bt.nextgen.api.account.v1.model.AccountKey("36846");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        paymentDto.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountKey(EncodedString.fromPlainText("234234234234").toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        paymentDto.setToPayteeDto(toDto);

        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        paymentDto.setBusinessChannel("Mobile");
        paymentDto.setClientIp("0.0.0.1");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
            paymentDto.setTransactionDate(new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(date));
            recurPayment.setTransactionDate(date);
        } catch (ParseException e) {
            fail();
        }
        when(paymentService.submitPayment(any(RecurringPaymentDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurPayment);
        PaymentDto paymentDo = paymentDtoServiceImpl.makePayment(paymentDto);

        assertNotNull(paymentDo);
    }

    @Test
    public void shouldFailPaymentTx_WhenBPay_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        recurPayment.setTransactionDate(getDateObj());
        when(paymentService.submitPayment(any(RecurringPaymentDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurPayment);
        PaymentInterface payment = this.getPaymentModel();

        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY.name());
        paymentDto.setTransactionDate(getDateObj().toString());
        PaymentDto response = paymentDtoServiceImpl.makePayment(paymentDto);

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals(response.getErrors().get(0).getId(), Constants.ACCT_NOT_IN_PAYEE_LIST);
    }

    @Test
    public void shouldFailPaymentTx_WhenLinkedAccount_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        recurPayment.setTransactionDate(getDateObj());
        when(paymentService.submitPayment(any(RecurringPaymentDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurPayment);
        PaymentInterface payment = this.getPaymentModel();

        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED.name());
        paymentDto.setTransactionDate(getDateObj().toString());
        PaymentDto response = paymentDtoServiceImpl.makePayment(paymentDto);

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals(response.getErrors().get(0).getId(), Constants.ACCT_NOT_IN_PAYEE_LIST);
    }

    @Test
    public void shouldFailPaymentTx_WhenPayAnyOne_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        recurPayment.setTransactionDate(getDateObj());
        when(paymentService.submitPayment(any(RecurringPaymentDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurPayment);
        PaymentInterface payment = this.getPaymentModel();

        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE.name());
        paymentDto.setTransactionDate(getDateObj().toString());
        PaymentDto response = paymentDtoServiceImpl.makePayment(paymentDto);

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals(response.getErrors().get(0).getId(), Constants.ACCT_NOT_IN_PAYEE_LIST);
    }

    @Test
    public void submitAvaloqErrorTest() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        serviceErrors.addError(new ServiceErrorImpl("Its an error"));
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE.name());
        try{
            paymentDtoServiceImpl.submit(paymentDto, serviceErrors);
        } catch (Exception e) {
            Assert.fail("submit payment should not fail in case avaloq returns errors");
        }
    }

    public PaymentInterface getPaymentModel() {
        PaymentInterface payment = new PaymentModel();
        payment.setPaymentId("OTY90867HJU");
        payment.setPaymentMethod(PaymentMethod.Method.DIRECT_DEBIT.name());
        payment.setAmount("$12.00");
        payment.setDescription("Test Payment");
        payment.setMaccId("1002927871");

        PayeeModel from = new PayeeModel();
        from.setReference("1001");
        from.setCode("262-786");
        from.setName("Adrian Demo Smith");
        from.setPayeeType(PayeeType.PAY_ANYONE);
        payment.setFrom(from);

        PayeeModel to = new PayeeModel();
        to.setReference("");
        to.setCode("12006");
        to.setName("linkedAcc");
        to.setPayeeType(PayeeType.LINKED);
        to.setDescription("Test Payment");
        payment.setTo(to);
        payment.setDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");

        return payment;
    }

    public PaymentDto getPaymentDto(String payeeType) {
        PaymentDto paymentDto = new PaymentDto();
        com.bt.nextgen.api.account.v1.model.AccountKey key = new com.bt.nextgen.api.account.v1.model.AccountKey("36846");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();
        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        PayeeDto toDto = new PayeeDto();
        toDto.setCode("12006");
        toDto.setAccountId("120061455");
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(payeeType);
        toDto.setCrn("123456789");
        toDto.setAccountKey(EncodedString.fromPlainText("120061455").toString());
        toDto.setSaveToList("save");

        paymentDto.setFromPayDto(fromDto);
        paymentDto.setToPayteeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");

        return paymentDto;
    }

    public PayeeDetails getPayeeDetailsObj() {
        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        MoneyAccountIdentifierImpl maccId = new MoneyAccountIdentifierImpl();
        // maccId.setMoneyAccountId("12345");
        maccId.setMoneyAccountId("76697");
        payeeDetailsImpl.setMaxDailyLimit("200000");
        payeeDetailsImpl.setMoneyAccountIdentifier(maccId);
        payeeDetailsImpl.setModifierSeqNumber(new BigDecimal("120"));
        List<PayeeLimit> payeeLimitList = new ArrayList<>();
        PayeeLimitImpl payLim = new PayeeLimitImpl();
        payLim.setCurrency("aud");
        payLim.setMetaType(TransactionType.PAY);
        payLim.setOrderType(TransactionOrderType.PAY_ANYONE);
        payeeLimitList.add(payLim);
        payeeDetailsImpl.setPayeeLimits(payeeLimitList);
        List<CashAccountDetailsImpl> cashAccList = new ArrayList<CashAccountDetailsImpl>();
        CashAccountDetailsImpl cashAcc = new CashAccountDetailsImpl();
        cashAccList.add(cashAcc);
        payeeDetailsImpl.setCashAccount(cashAccList);
        List<LinkedAccount> linkAccDetList = new ArrayList<>();
        LinkedAccount linkedAccountDetails = new LinkedAccount() {
            @Override
            public boolean isPrimary() {
                return false;
            }

            @Override
            public String getCurrencyId() {
                return null;
            }

            @Override
            public BigDecimal getLimit() {
                return null;
            }

            @Override
            public CurrencyType getCurrency() {
                return null;
            }

            @Override
            public BigDecimal getRemainingLimit() {
                return null;
            }

            @Override
            public boolean isPensionPayment() {
                return false;
            }



            @Override
            public String getAccountNumber() {
                return "1234567";
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getNickName() {
                return null;
            }

            @Override
            public String getLinkedAccountStatus() { return null; }

            @Override
            public String getBsb() {
                return "262-786";
            }
        };
        linkAccDetList.add(linkedAccountDetails);
        payeeDetailsImpl.setLinkedAccountList(linkAccDetList);

        List<PayAnyOne> payAnyOnes = new ArrayList<>();
        PayAnyOne payAnyOne = new PayAnyOne() {
            @Override
            public String getAccountNumber() {
                return "1234567";
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getNickName() {
                return null;
            }

            @Override
            public String getBsb() {
                return "262-786";
            }
        };
        payAnyOnes.add(payAnyOne);
        payeeDetailsImpl.setPayanyonePayeeList(payAnyOnes);

        List<Biller> billers = new ArrayList<>();
        Biller biller = new Biller() {
            @Override
            public String getCRN() {
                return "1234567";
            }

            @Override
            public String getCRNType() {
                return null;
            }

            @Override
            public void setCRNType(String crnType) {

            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getNickName() {
                return null;
            }

            @Override
            public String getBillerCode() {
                return "262-786";
            }
        };
        billers.add(biller);
        payeeDetailsImpl.setBpayBillerPayeeList(billers);
        return payeeDetailsImpl;
    }

    public Date getDateObj() {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
        } catch (ParseException e) {
            fail();
        }
        return date;
    }

}
