package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.api.movemoney.v2.model.EndPaymentDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentMethod;
import com.bt.nextgen.payments.web.model.PaymentInterface;
import com.bt.nextgen.payments.web.model.PaymentModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequestModel;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.AvailableCashImpl;
import com.bt.nextgen.service.avaloq.movemoney.PaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.CashAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeLimitImpl;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.rules.RuleCond;
import com.bt.nextgen.service.avaloq.rules.RuleImpl;
import com.bt.nextgen.service.avaloq.rules.RuleType;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PaymentIntegrationService;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentDtoServiceTest {

    @InjectMocks
    private PaymentDtoServiceImpl paymentDtoServiceImpl;

    @Mock
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private PaymentIntegrationService paymentService;

    @Mock
    private CmsService cmsService;

    @Mock
    private MoneyAccountIdentifierImpl identifier = new MoneyAccountIdentifierImpl();

    @Mock
    private WrapAccountIdentifierImpl wrapidentifier = new WrapAccountIdentifierImpl();

    @Mock
    private MovemoneyDtoErrorMapper movemoneyGroupDtoErrorMapper;

    @Mock
    private HttpSession httpSession;

    @Mock
    private AvaloqRulesIntegrationServiceImpl avaloqRulesIntegrationService;

    @Mock
    private PermissionAccountDtoService permissionAccountDtoService;

    @Mock
    private TransactionReceiptHelper transactionReceiptHelper;

    private PaymentDetailsImpl recurPayment = new PaymentDetailsImpl();

    private static String PAYMENT_LIMITS = "PAYMENT_LIMITS";

    private List<PayeeModel> payeeModelList;

    private List<PhoneModel> phoneModelList;

    private PortfolioRequest portfolioRequest;

    private PayeeDetails payeeDetails = null;

    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockHttpSession session;
    private  WrapAccountDetail wrapAccountDetails;

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

        when(permissionAccountDtoService.canTransact(anyString(), anyString())).thenReturn(Boolean.TRUE);

        wrapAccountDetails = mock(WrapAccountDetail.class);
        when(wrapAccountDetails.getModificationSeq()).thenReturn("10");
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any (ServiceErrors.class))).thenReturn(wrapAccountDetails);
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

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.MOVE_MONEY_MODEL, SearchOperation.EQUALS, model, OperationType.STRING));
        com.bt.nextgen.api.account.v3.model.AccountKey accountKey = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());

        List<PaymentDto> paymentDtoList = paymentDtoServiceImpl.search(accountKey, criteria, new ServiceErrorsImpl());

        assertNotNull(paymentDtoList);
        assertTrue(paymentDtoList.size() == 1);
    }

    @Test
    public void testValidatePayment() {
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
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
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

        paymentDto.setToPayeeDto(toDto);

        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
            paymentDto.setTransactionDate(new DateTime(date));
            recurPayment.setTransactionDate(date);
        } catch (ParseException e) {
            fail();
        }

        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn("SessionVar");
        when(avaloqRulesIntegrationService.retrieveTwoFaRule(RuleType.LINK_ACC, new HashMap<RuleCond, String>(), new FailFastErrorsImpl())).thenReturn(new RuleImpl());

        recurPayment = new PaymentDetailsImpl();
        recurPayment.setRecurringFrequency(RecurringFrequency.HalfYearly);
        when(paymentService.validatePayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);

        // recurring payment without end date
        PaymentDto paymentDo = paymentDtoServiceImpl.validate(paymentDto, new ServiceErrorsImpl());
        assertNotNull(paymentDo);
        assertNull(paymentDo.getRepeatEndDate());

        // recurring payment with end date
        recurPayment.setEndDate(new DateTime("2017-01-01").toDate());
        paymentDo = paymentDtoServiceImpl.validate(paymentDto, new ServiceErrorsImpl());
        assertNotNull(paymentDo);
        assertNotNull(paymentDo.getRepeatEndDate());
    }

    @Test
    public void testMakePayment_invalid() {

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
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
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

        paymentDto.setToPayeeDto(toDto);

        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
            paymentDto.setTransactionDate(new DateTime(date));
            recurPayment.setTransactionDate(date);
        } catch (ParseException e) {
            fail();
        }
        when(paymentService.submitPayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);
        PaymentDto paymentDo = paymentDtoServiceImpl.submit(paymentDto, new ServiceErrorsImpl());

        assertNotNull(paymentDo);
        verify(transactionReceiptHelper, never()).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void testMakePayment_valid() {
        PayeeDto toDto = new PayeeDto();
        toDto.setCode("12006");
        toDto.setAccountId("234234234234");
        toDto.setAccountKey(EncodedString.fromPlainText(toDto.getAccountId()).toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        PaymentDto paymentDto = new PaymentDto();
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
        paymentDto.setKey(key);
        paymentDto.setToPayeeDto(toDto);
        paymentDto.setTransactionDate(DateTime.parse("2016-01-01"));

        LinkedAccount linkedAccount = mock(LinkedAccount.class);
        when(linkedAccount.getBsb()).thenReturn(toDto.getCode());
        when(linkedAccount.getAccountNumber()).thenReturn(toDto.getAccountId());

        ((PayeeDetailsImpl) payeeDetails).setLinkedAccountList(Collections.singletonList(linkedAccount));
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(paymentService.submitPayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);
        PaymentDto paymentDo = paymentDtoServiceImpl.submit(paymentDto, new ServiceErrorsImpl());

        assertNotNull(paymentDo);
        verify(transactionReceiptHelper, times(1)).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void shouldFailPaymentTx_WhenBPay_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(cmsService.getContent("Err.IP-0315")).thenReturn("Payee not a linked account");
        recurPayment.setTransactionDate(getDateObj());
        when(paymentService.submitPayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);

        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY.name());
        paymentDto.setTransactionDate(new DateTime(getDateObj()));
        PaymentDto response = paymentDtoServiceImpl.submit(paymentDto, new ServiceErrorsImpl());

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals("Err.IP-0315", response.getErrors().get(0).getErrorId());
    }

    @Test
    public void shouldFailPaymentTx_WhenLinkedAccount_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(cmsService.getContent("Err.IP-0315")).thenReturn("Payee not a linked account");
        recurPayment.setTransactionDate(getDateObj());
        when(paymentService.submitPayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);

        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED.name());
        paymentDto.setTransactionDate(new DateTime(getDateObj()));
        PaymentDto response = paymentDtoServiceImpl.submit(paymentDto, new ServiceErrorsImpl());

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals("Err.IP-0315", response.getErrors().get(0).getErrorId());
    }

    @Test
    public void shouldFailPaymentTx_WhenPayAnyOne_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(cmsService.getContent("Err.IP-0315")).thenReturn("Payee not a linked account");
        recurPayment.setTransactionDate(getDateObj());
        when(paymentService.submitPayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);

        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE.name());
        paymentDto.setTransactionDate(new DateTime(getDateObj()));
        PaymentDto response = paymentDtoServiceImpl.submit(paymentDto, new ServiceErrorsImpl());

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals("Err.IP-0315", response.getErrors().get(0).getErrorId());
    }

    @Test
    public void testEndPayment() {
        EndPaymentDto endPaymentDto = new EndPaymentDto();
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
        endPaymentDto.setKey(key);
        endPaymentDto.setTransactionId(EncodedString.fromPlainText("123456").toString());
        endPaymentDto.setHasDrawdownInprogress(Boolean.TRUE);

        List<ValidationError> warnings = new ArrayList<>();
        warnings.add(new ValidationError("btfg$dd_occurred_close", null, null, ValidationError.ErrorType.WARNING));
        recurPayment.setWarnings(warnings);

        List<DomainApiErrorDto> apiWarnings = new ArrayList<>();
        apiWarnings.add(
                new DomainApiErrorDto("btfg$dd_occurred_close", "domain", "reason", null, DomainApiErrorDto.ErrorType.WARNING));
        Mockito.when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiWarnings);

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);

        when(paymentService.endPayment(any(PaymentDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurPayment);
        PaymentDto paymentDo = paymentDtoServiceImpl.submit(endPaymentDto, new ServiceErrorsImpl());

        assertNotNull(paymentDo);
        assertEquals(paymentDo.getWarnings().get(0).getErrorId(), "btfg$dd_occurred_close");
        verify(transactionReceiptHelper, never()).storeReceiptData(Matchers.anyObject());
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
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
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
        paymentDto.setToPayeeDto(toDto);
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
        payLim.setMetaType(null);
        payLim.setOrderType(null);
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

    @Test
    public void testToPaymentDto() {
        PaymentDto paymentDto = new PaymentDto();
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
        paymentDto.setKey(key);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountKey(EncodedString.fromPlainText("234234234234").toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        paymentDto.setToPayeeDto(toDto);

        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
            paymentDto.setTransactionDate(new DateTime(date));
            recurPayment.setTransactionDate(date);
        } catch (ParseException e) {
            fail();
        }

        // ensure that regular payments retain the transaction date and aren't nulled out by our one off payment logic
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        paymentDto.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT.getLabel());
        PaymentDetails paymentDetails = paymentDtoServiceImpl.toPaymentDetails(moneyAccountIdentifier, paymentDto, null);
        Assert.assertNotNull(paymentDetails.getTransactionDate());
        Assert.assertEquals("10", paymentDetails.getModificationSeq());

        // pension one off and lump sum should not pass transaction dates because it fails on avaloq side
        // we pass null for these and avaloq works out the dates correctly
        paymentDto.setIsRecurring(false);
        paymentDto.setFrequency(null);
        paymentDto.setWithdrawalType(WithdrawalType.PENSION_ONE_OFF_PAYMENT.getLabel());
        paymentDetails = paymentDtoServiceImpl.toPaymentDetails(moneyAccountIdentifier, paymentDto, null);
        assertNull(paymentDetails.getTransactionDate());

        paymentDto.setWithdrawalType(WithdrawalType.LUMP_SUM_WITHDRAWAL.getLabel());
        paymentDetails = paymentDtoServiceImpl.toPaymentDetails(moneyAccountIdentifier, paymentDto, null);
        assertNull(paymentDetails.getTransactionDate());

        // Max count & end date should be null for regular pension payments
        paymentDto.setIsRecurring(true);
        paymentDto.setEndRepeatNumber(BigInteger.ONE);
        paymentDto.setRepeatEndDate(DateTime.parse("2017-01-01"));
        when(wrapAccountDetails.getSuperAccountSubType()).thenReturn(AccountSubType.PENSION);
        when(wrapAccountDetails.getModificationSeq()).thenReturn("2");
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any (ServiceErrors.class))).thenReturn(wrapAccountDetails);
        paymentDetails = paymentDtoServiceImpl.toPaymentDetails(moneyAccountIdentifier, paymentDto, null);
        assertNull(paymentDetails.getMaxCount());
        assertNull(paymentDetails.getEndDate());

        // Max count & end date should NOT be null for non - regular pension payments
        when(wrapAccountDetails.getSuperAccountSubType()).thenReturn(null);
        when(wrapAccountDetails.getModificationSeq()).thenReturn("2");
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any (ServiceErrors.class))).thenReturn(wrapAccountDetails);
        paymentDetails = paymentDtoServiceImpl.toPaymentDetails(moneyAccountIdentifier, paymentDto, null);
        Assert.assertEquals(paymentDetails.getMaxCount(), BigInteger.ONE);
        Assert.assertNotNull(paymentDetails.getEndDate());

        // For 100% coverage
        paymentDto.setTransactionId(EncodedString.fromPlainText("123456").toString());
        List<DomainApiErrorDto> apiWarnings = new ArrayList<>();
        apiWarnings.add(
                new DomainApiErrorDto("btfg$dd_occurred_close", "domain", "reason", null, DomainApiErrorDto.ErrorType.WARNING));
        paymentDto.setWarnings(apiWarnings);
        Mockito.when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiWarnings);
        paymentDetails = paymentDtoServiceImpl.toPaymentDetails(moneyAccountIdentifier, paymentDto, null);
        Assert.assertNotNull(paymentDetails);
        Assert.assertNotNull(paymentDetails.getPositionId());
        Assert.assertNotNull(paymentDetails.getWarnings());
    }
}
