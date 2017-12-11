package com.bt.nextgen.api.movemoney.v2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import junit.framework.Assert;
import org.joda.time.DateTime;
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

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.addressbook.web.model.GenericPayee;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.ConfirmDepositConversation;
import com.bt.nextgen.payments.web.model.DepositInterface;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequestModel;
import com.bt.nextgen.service.avaloq.gateway.businessunit.Payee;
import com.bt.nextgen.service.avaloq.movemoney.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.CashAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeLimitImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class DepositDtoServiceTest {

    @InjectMocks
    private DepositDtoServiceImpl depositDtoServiceImpl;

    @Mock
    PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    DepositIntegrationService depositIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    CmsService cmsService;

    @Mock
    private MovemoneyDtoErrorMapper movemoneyGroupDtoErrorMapper;

    @Mock
    private TransactionReceiptHelper transactionReceiptHelper;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private static String PAYMENT_LIMITS = "PAYMENT_LIMITS";

    MoveMoneyModel conversation;

    CashAccountModel cashAccountModel;
    List<PayeeModel> payeeModelList;

    List<PhoneModel> phoneModelList;

    List<Payee> payeeList = null;

    PayeeDetails payeeDetails = null;
    RecurringDepositDetailsImpl recurringDepositDetails = null;
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockHttpSession session;
    private PortfolioRequest portfolioRequest;
    private AccountKey accountKey;
    private DepositDetailsImpl depositDetails = null;
    private MoneyAccountIdentifierImpl moneyAcc = new MoneyAccountIdentifierImpl();
    private PayAnyoneAccountDetails payAnyone = new PayAnyoneAccountDetailsImpl();
    private CodeImpl statusCode;

    @Before
    public void setup() throws Exception {
        accountKey = new AccountKey(EncodedString.fromPlainText("36846").toString());
        payeeModelList = new ArrayList<PayeeModel>();
        cashAccountModel = new CashAccountModel();
        cashAccountModel.setPersonName("Test Account");
        cashAccountModel.setBsb("012020");
        cashAccountModel.setAccountId("123654789");
        cashAccountModel.setMaccId("123");

        statusCode = new CodeImpl("1", "Verified","vfy");
        statusCode.setIntlId("vfy");
        statusCode.addField("can_vfy_code","-");

        PayeeModel payeeModel = new PayeeModel();
        // payeeModel.setId("1");
        payeeModel.setCode("0120");
        payeeModel.setName("Test");
        payeeModel.setPayeeType(PayeeType.LINKED);
        payeeModel.setReference("123456789");
        payeeModelList.add(payeeModel);

        payeeList = new ArrayList<Payee>();
        GenericPayee payee = new GenericPayee();
        payee.setName("Test");
        payee.setCode("0120");
        payee.setPayeeType(PayeeType.LINKED);
        payee.setReference(payeeModel.getReference());
        payeeList.add(payee);

        payeeDetails = getPayeeDetailsObj();

        phoneModelList = new ArrayList<PhoneModel>();
        PhoneModel phoneModel = new PhoneModel();
        phoneModel.setPhoneNumber("1256290873");
        phoneModel.setPrimary(true);
        phoneModelList.add(phoneModel);

        moneyAcc.setMoneyAccountId("moneyAccountId");
        payAnyone.setAccount("123456789");
        payAnyone.setBsb("123456");
        DateTime transactionDate = new DateTime("2014-09-02");

        depositDetails = new DepositDetailsImpl(moneyAcc, payAnyone, new BigDecimal("1234"), CurrencyType.AustralianDollar,
                "Test", transactionDate, "121234", null, ContributionType.SPOUSE);

        recurringDepositDetails = new RecurringDepositDetailsImpl(moneyAcc, payAnyone, new BigDecimal("1234"),
                CurrencyType.AustralianDollar, "Test", transactionDate, "121234", null, ContributionType.SPOUSE,
                RecurringFrequency.Monthly, new DateTime(), null, null, null);

        portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId("36846");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        request.setSession(session);
    }

    @Test
    public void testSearch() {
        Mockito.when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.LINKED_ACCOUNT_STATUS),
                Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(statusCode);
        List<DepositDto> depositDtoList = depositDtoServiceImpl.loadPayeesForDeposits(accountKey);
        DepositDto depositDto =   depositDtoList.get(0);
        Assert.assertEquals("Verified", depositDto.getFromPayDto().getLinkedAccountStatus().getLinkedAccountStatus().getDescription());
        assertNotNull(depositDtoList);
        assertTrue(depositDtoList.size() == 1);
    }

    @Test
    public void testSearch_NoLinkedAccounts() {
        Mockito.when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class))).thenReturn(null);
        List<DepositDto> depositDtoList = depositDtoServiceImpl.loadPayeesForDeposits(accountKey);
        assertNotNull(depositDtoList);
        assertEquals(depositDtoList.size(), 0);
    }

    @Test
    public void testValidateDeposit() {
        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError("btfg$chk_fund_cap", null, null, ValidationError.ErrorType.ERROR));
        depositDetails.setErrors(errors);

        List<DomainApiErrorDto> apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("btfg$chk_fund_cap", "domain", "reason", null, DomainApiErrorDto.ErrorType.ERROR));
        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.validateDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.setIsRecurring(false);
        depositDtoFinal.setErrors(apiErrors);
        DepositDto finalDepositDto = depositDtoServiceImpl.validate(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        assertEquals(finalDepositDto.getErrors().get(0).getErrorId(), "btfg$chk_fund_cap");
    }

    @Test
    public void testValidateRecurringDeposit() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.validateDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        DepositDto finalDepositDto = depositDtoServiceImpl.validate(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
    }

    @Test
    public void testSubmitDeposit() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setIsRecurring(false);
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        verify(transactionReceiptHelper, times(1)).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void testSubmitDeposit_invalid() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("12345679");
        depositDtoFinal.setIsRecurring(false);
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        verify(transactionReceiptHelper, never()).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void testSubmitDeposit_WithWarnings() {
        List<ValidationError> warnings = new ArrayList<>();
        warnings.add(new ValidationError("btfg$chk_contr_cap_exce", null, null, ValidationError.ErrorType.WARNING));
        depositDetails.setWarnings(warnings);

        List<DomainApiErrorDto> apiWarnings = new ArrayList<>();
        apiWarnings.add(
                new DomainApiErrorDto("btfg$chk_contr_cap_exce", "domain", "reason", null, DomainApiErrorDto.ErrorType.WARNING));

        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiWarnings);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setIsRecurring(false);
        depositDtoFinal.setWarnings(apiWarnings);
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        assertEquals(finalDepositDto.getWarnings().get(0).getErrorId(), "btfg$chk_contr_cap_exce");
        verify(transactionReceiptHelper, times(1)).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void testSubmitRecurringDeposit() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setPayeeType(PayeeType.PAY_ANYONE.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        verify(transactionReceiptHelper, times(1)).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void testSubmitRecurringDeposit_WithWarning() {
        List<ValidationError> warnings = new ArrayList<>();
        warnings.add(new ValidationError("btfg$chk_contr_cap_exce", null, null, ValidationError.ErrorType.WARNING));
        depositDetails.setWarnings(warnings);

        List<DomainApiErrorDto> apiWarnings = new ArrayList<>();
        apiWarnings.add(
                new DomainApiErrorDto("btfg$chk_contr_cap_exce", "domain", "reason", null, DomainApiErrorDto.ErrorType.WARNING));
        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiWarnings);

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setPayeeType(PayeeType.PAY_ANYONE.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setWarnings(apiWarnings);
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        assertEquals(finalDepositDto.getWarnings().get(0).getErrorId(), "btfg$chk_contr_cap_exce");
        verify(transactionReceiptHelper, times(1)).storeReceiptData(Matchers.anyObject());
    }

    @Test
    public void testCreateRecurringDeposit() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.createDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setPayeeType(PayeeType.PAY_ANYONE.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        DepositDto finalDepositDto = depositDtoServiceImpl.create(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
    }

    @Test
    public void testUpdateDeposit() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.createDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setIsRecurring(false);
        DepositDto finalDepositDto = depositDtoServiceImpl.update(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
    }

    @Test
    public void shouldFailDepositTx_WhenLinkedAccount_WithOtherAccount() {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);
        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        DepositDto response = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals(response.getErrors().get(0).getErrorId(), Constants.ACCT_NOT_IN_PAYEE_LIST);
    }

    public DepositInterface getDepositInterfaceObj() {
        DepositDto depositDto = new DepositDto();
        depositDto.setAmount(new BigDecimal(100));
        depositDto.setDescription("Test Deposit");
        depositDto.setTransactionDate("2014-09-02");
        depositDto.setEndRepeat("setDate");
        depositDto.setIsRecurring(true);
        depositDto.setKey(accountKey);
        depositDto.setPaymentId("139D83A1CB75C1CF72D147DD52033573AC4EE45A0F0D56C919350C9EDA537610");
        depositDto.setFrequency("monthly");

        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setCrn("1002");
        payeeDto.setPayeeType("LINKED");
        payeeDto.setAccountId("36846");
        payeeDto.setNickname("linkedNick");
        payeeDto.setAccountName("linkedAcc");

        depositDto.setFromPayDto(payeeDto);

        DepositInterface confirmDepositConversation = new ConfirmDepositConversation();
        confirmDepositConversation.setAmount(new BigDecimal(12));
        confirmDepositConversation.setDate(depositDto.getTransactionDate());
        confirmDepositConversation.setFrequency(depositDto.getFrequency());
        confirmDepositConversation.setMaccId(cashAccountModel.getMaccId());
        confirmDepositConversation.setPayeeDescription(depositDto.getDescription());
        confirmDepositConversation.setToName(cashAccountModel.getPersonName());
        confirmDepositConversation.setToBsb(cashAccountModel.getBsb());
        confirmDepositConversation.setToAccount(cashAccountModel.getAccountId());

        return confirmDepositConversation;
    }

    public DepositDto getDepositDto(String payeeType) {
        DepositDto depositDto = new DepositDto();
        depositDto.setKey(accountKey);

        PayeeDto fromDto = new PayeeDto();
        fromDto.setCode("262-786");
        fromDto.setAccountId("12345677");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(payeeType);
        depositDto.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();
        toDto.setCode("12006");
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(payeeType);
        depositDto.setToPayeeDto(toDto);

        depositDto.setAmount(new BigDecimal(12));
        depositDto.setDepositType("Spouse");
        depositDto.setDescription("Test Payment");
        depositDto.setEndRepeat("setEndDate");
        depositDto.setIsRecurring(true);
        depositDto.setFrequency(RecurringFrequency.Monthly.name());
        depositDto.setTransactionDate("02 Sep 2014");
        depositDto.setRepeatEndDate("02 Oct 2014");

        return depositDto;
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
        cashAcc.setAccountName("Test Account");
        cashAcc.setBsb("012020");
        cashAcc.setAccountNumber("123654789");
        cashAcc.setBillerCode("123");
        cashAccList.add(cashAcc);
        payeeDetailsImpl.setCashAccount(cashAccList);
        List<LinkedAccount> linkAccDetList = new ArrayList<>();

        LinkedAccount linkedAccountDetails = mock(LinkedAccount.class);
        when(linkedAccountDetails.getAccountNumber()).thenReturn("1234567");
        when(linkedAccountDetails.getBsb()).thenReturn("262-786");

        linkAccDetList.add(linkedAccountDetails);
        payeeDetailsImpl.setLinkedAccountList(linkAccDetList);

        List<PayAnyOne> payAnyOnes = new ArrayList<>();
        PayAnyOne payAnyOne = mock(PayAnyOne.class);
        when(payAnyOne.getAccountNumber()).thenReturn("1234567");
        when(payAnyOne.getBsb()).thenReturn("262-786");

        payAnyOnes.add(payAnyOne);
        payeeDetailsImpl.setPayanyonePayeeList(payAnyOnes);

        List<Biller> billers = new ArrayList<>();
        Biller biller = mock(Biller.class);
        when(biller.getBillerCode()).thenReturn("262-786");
        when(biller.getCRN()).thenReturn("1234567");

        billers.add(biller);
        payeeDetailsImpl.setBpayBillerPayeeList(billers);
        return payeeDetailsImpl;
    }

}
