package com.bt.nextgen.api.movemoney.v3.service;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.addressbook.web.model.GenericPayee;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.api.movemoney.v3.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v3.model.RecurringDepositKey;
import com.bt.nextgen.api.movemoney.v3.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.ConfirmDepositConversation;
import com.bt.nextgen.payments.web.model.DepositInterface;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.bt.nextgen.service.ServiceErrors;
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
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.movemoney.DepositStatus;
import com.bt.nextgen.service.integration.movemoney.OrderType;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DepositDtoServiceTest {

    @InjectMocks
    private DepositDtoServiceImpl depositDtoServiceImpl;

    @Mock
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    private DepositIntegrationService depositIntegrationService;

    @Mock
    private MovemoneyDtoErrorMapper movemoneyGroupDtoErrorMapper;

    private CashAccountModel cashAccountModel;
    private List<PayeeModel> payeeModelList;

    private List<Payee> payeeList;

    private PayeeDetails payeeDetails;
    private RecurringDepositDetailsImpl recurringDepositDetails;
    private DepositDetailsImpl depositDetails;
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockHttpSession session;
    private PortfolioRequest portfolioRequest;
    private AccountKey accountKey;
    private DepositDetailsImpl deposit1;
    private DepositDetailsImpl deposit2;
    private RecurringDepositDetailsImpl deposit3;
    private RecurringDepositDetailsImpl emptyRecurringDeposit;
    private List<DepositDetails> deposits;
    private DepositDto depositDto;
    private DepositDto recurringDepositMaxRepeatsDto;
    private DepositDto recurringDepositEndDateDto;
    private DepositDto emptyDepositDto;
    private DepositDto emptyRecurringDepositDto;
    private DepositDetailsImpl deposit;
    private DepositDetailsImpl emptyDeposit;
    private RecurringDepositDetailsImpl recurringDeposit;

    @Before
    public void setup() throws Exception {
        accountKey = new AccountKey(EncodedString.fromPlainText("36846").toString());
        payeeModelList = new ArrayList<PayeeModel>();
        cashAccountModel = new CashAccountModel();
        cashAccountModel.setPersonName("Test Account");
        cashAccountModel.setBsb("012020");
        cashAccountModel.setAccountId("123654789");
        cashAccountModel.setMaccId("123");

        PayeeModel payeeModel = new PayeeModel();
        // payeeModel.setId("1");
        payeeModel.setCode("0120");
        payeeModel.setName("Test");
        payeeModel.setPayeeType(PayeeType.LINKED);
        payeeModel.setReference("123456789");
        payeeModelList.add(payeeModel);

        PayeeDto fromDto = new PayeeDto();
        fromDto.setCode("262-786");
        fromDto.setAccountId("12345677");
        fromDto.setAccountName("Adrian Demo Smith");

        payeeList = new ArrayList<Payee>();
        GenericPayee payee = new GenericPayee();
        payee.setName("Test");
        payee.setCode("0120");
        payee.setPayeeType(PayeeType.LINKED);
        payee.setReference(payeeModel.getReference());
        payeeList.add(payee);

        payeeDetails = getPayeeDetailsObj();

        deposit1 = new DepositDetailsImpl();
        deposit1.setDepositId("abc");
        deposit1.setTransactionSeq("1");
        deposit1.setStatus(DepositStatus.ACTIVE);
        deposit1.setDepositAmount(new BigDecimal("1000"));
        deposit1.setDescription("desc1");
        deposit1.setTransactionDate(new DateTime("2014-09-01"));
        deposit1.setContributionType(ContributionType.PERSONAL);
        deposit1.setOrderType(OrderType.SUPER_ONE_OFF_CONTRIBUTION);
        deposit1.setPayerBsb("012345");
        deposit1.setPayerAccount("123456");
        deposit1.setPayerName("John Smith");
        deposit1.setPayeeMoneyAccount("456789");

        deposit2 = new DepositDetailsImpl();
        deposit2.setDepositId("def");
        deposit2.setTransactionSeq("2");
        deposit2.setStatus(DepositStatus.NOT_SUBMITTED);
        deposit2.setDepositAmount(new BigDecimal("2000"));
        deposit2.setDescription("desc2");
        deposit2.setTransactionDate(new DateTime("2014-09-02"));
        deposit2.setContributionType(ContributionType.PERSONAL);
        deposit2.setOrderType(OrderType.SUPER_ONE_OFF_CONTRIBUTION);
        deposit2.setPayerBsb("12345");
        deposit2.setPayerAccount("1234567");
        deposit2.setPayeeMoneyAccount("3456789");

        deposit3 = new RecurringDepositDetailsImpl();
        deposit3.setDepositId("ghi");
        deposit3.setTransactionSeq("3");
        deposit3.setStatus(DepositStatus.ACTIVE);
        deposit3.setDepositAmount(new BigDecimal("3000"));
        deposit3.setDescription("desc3");
        deposit3.setTransactionDate(new DateTime("2014-09-03"));
        deposit3.setRecurringFrequency(RecurringFrequency.Fortnightly);
        deposit3.setStartDate(new DateTime("2014-09-03"));
        deposit3.setEndDate(new DateTime("2014-10-03"));
        deposit3.setMaxCount(5);
        deposit3.setContributionType(ContributionType.PERSONAL);
        deposit3.setOrderType(OrderType.SUPER_RECURRING_CONTRIBUTION);
        deposit3.setPayerBsb("2345");
        deposit3.setPayerAccount("12345678");
        deposit3.setPayeeMoneyAccount("23456789");
        deposit3.setCurrencyType(CurrencyType.AustralianDollar);
        deposit3.setReceiptNumber("54678913");
        deposit3.setDepositDate(new DateTime("2014-10-03"));

        emptyRecurringDeposit = new RecurringDepositDetailsImpl();
        emptyRecurringDeposit.setDepositId("empty");
        emptyRecurringDeposit.setReceiptNumber("depositId");

        emptyDeposit = new DepositDetailsImpl();
        emptyDeposit.setDepositId("empty");
        emptyDeposit.setReceiptNumber("depositId");

        deposits = new ArrayList<>();
        deposits.add(deposit1);
        deposits.add(deposit2);
        deposits.add(deposit3);
        deposits.add(emptyRecurringDeposit);

        MoneyAccountIdentifierImpl moneyAcc = new MoneyAccountIdentifierImpl();
        moneyAcc.setMoneyAccountId("moneyAccountId");
        PayAnyoneAccountDetails payAnyone = new PayAnyoneAccountDetailsImpl();
        payAnyone.setAccount("123456789");
        payAnyone.setBsb("123456");
        DateTime transactionDate = new DateTime("2014-09-02");

        depositDetails = new DepositDetailsImpl(moneyAcc, payAnyone, new BigDecimal("1234"), CurrencyType.AustralianDollar,
                "Test", transactionDate, "121234", null, ContributionType.SPOUSE, RecurringFrequency.Once, null);

        recurringDepositDetails = new RecurringDepositDetailsImpl(moneyAcc, payAnyone, new BigDecimal("1234"),
                CurrencyType.AustralianDollar, "Test", transactionDate, "121234", null, ContributionType.SPOUSE,
                RecurringFrequency.Monthly, new DateTime(), null, null, null);

        emptyDepositDto = new DepositDto();
        emptyDepositDto.setKey(new DepositKey("depositId"));
        emptyDepositDto.setAccountKey(accountKey);
        emptyDepositDto.setFromPayDto(fromDto);

        emptyRecurringDepositDto = new DepositDto();
        emptyRecurringDepositDto.setKey(new RecurringDepositKey());
        emptyRecurringDepositDto.setKey(new RecurringDepositKey("depositId"));
        emptyRecurringDepositDto.setAccountKey(accountKey);
        emptyRecurringDepositDto.setFrequency("Monthly");
        emptyRecurringDepositDto.setFromPayDto(fromDto);

        depositDto = new DepositDto();
        depositDto.setKey(new DepositKey("depositId"));
        depositDto.setAmount(new BigDecimal(100));
        depositDto.setDescription("Test Deposit");
        depositDto.setTransactionDate(new DateTime("2014-08-02"));
        depositDto.setEndRepeat("setDate");
        depositDto.setIsRecurring(true);
        depositDto.setAccountKey(accountKey);
        depositDto.setFrequency("Monthly");
        depositDto.setOrderType(OrderType.SUPER_ONE_OFF_CONTRIBUTION.getName());
        depositDto.setStatus(DepositStatus.NOT_SUBMITTED.getDisplayName());
        depositDto.setCurrency(CurrencyType.AustralianDollar.getCurrency());
        depositDto.setFromPayDto(fromDto);

        recurringDepositMaxRepeatsDto = new DepositDto();
        recurringDepositMaxRepeatsDto.setKey(new DepositKey("depositId"));
        recurringDepositMaxRepeatsDto.setAmount(new BigDecimal(100));
        recurringDepositMaxRepeatsDto.setDescription("Test Deposit");
        recurringDepositMaxRepeatsDto.setTransactionDate(new DateTime("2014-08-02"));
        recurringDepositMaxRepeatsDto.setEndRepeat("setNumber");
        recurringDepositMaxRepeatsDto.setIsRecurring(true);
        recurringDepositMaxRepeatsDto.setAccountKey(accountKey);
        recurringDepositMaxRepeatsDto.setFrequency("Monthly");
        recurringDepositMaxRepeatsDto.setEndRepeatNumber("4");
        recurringDepositMaxRepeatsDto.setRepeatEndDate(new DateTime("2015-08-02"));
        recurringDepositMaxRepeatsDto.setFromPayDto(fromDto);

        recurringDepositEndDateDto = new DepositDto();
        recurringDepositEndDateDto.setKey(new DepositKey("depositId"));
        recurringDepositEndDateDto.setAmount(new BigDecimal(100));
        recurringDepositEndDateDto.setDescription("Test Deposit");
        recurringDepositEndDateDto.setTransactionDate(new DateTime("2014-08-02"));
        recurringDepositEndDateDto.setEndRepeat("setDate");
        recurringDepositEndDateDto.setIsRecurring(true);
        recurringDepositEndDateDto.setAccountKey(accountKey);
        recurringDepositEndDateDto.setFrequency("Monthly");
        recurringDepositEndDateDto.setRepeatEndDate(new DateTime("2015-08-02"));
        recurringDepositEndDateDto.setFromPayDto(fromDto);

        deposit = new DepositDetailsImpl();
        deposit.setDepositId("abc");
        deposit.setTransactionSeq("1");
        deposit.setStatus(DepositStatus.ACTIVE);
        deposit.setDepositAmount(new BigDecimal("1000"));
        deposit.setDescription("desc1");
        deposit.setTransactionDate(new DateTime("2014-09-01"));
        deposit.setContributionType(ContributionType.PERSONAL);
        deposit.setPayerBsb("012345");
        deposit.setPayerAccount("123456");
        deposit.setPayeeMoneyAccount("456789");
        deposit.setRecurringFrequency(RecurringFrequency.Once);

        recurringDeposit = new RecurringDepositDetailsImpl();
        recurringDeposit.setDepositId("ghi");
        recurringDeposit.setTransactionSeq("3");
        recurringDeposit.setStatus(DepositStatus.ACTIVE);
        recurringDeposit.setDepositAmount(new BigDecimal("3000"));
        recurringDeposit.setDescription("desc3");
        recurringDeposit.setTransactionDate(new DateTime("2014-09-03"));
        recurringDeposit.setRecurringFrequency(RecurringFrequency.Fortnightly);
        recurringDeposit.setStartDate(new DateTime("2014-09-03"));
        recurringDeposit.setEndDate(new DateTime("2014-10-03"));
        recurringDeposit.setMaxCount(5);
        recurringDeposit.setContributionType(ContributionType.PERSONAL);
        recurringDeposit.setPayerBsb("2345");
        recurringDeposit.setPayerAccount("12345678");
        recurringDeposit.setPayeeMoneyAccount("23456789");

        emptyRecurringDeposit = new RecurringDepositDetailsImpl();
        emptyRecurringDeposit.setDepositId("empty");
        emptyRecurringDeposit.setReceiptNumber("depositId");

        portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId("36846");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        request.setSession(session);

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
    }

    @Test
    public void testDepositDto_whenNullDepositId_thenIllegalArgumentException() {
        try {
            DepositKey nullId = new DepositKey(null);
        } catch (IllegalArgumentException exception) {
            assert (true);
            return;
        }
        fail("IllegalArgumentException Not Thrown");
    }

    @Test
    public void testDefaultConstructors() {
        DepositDto emptyDeposit = new DepositDto();
        assertNotNull(emptyDeposit);

        DepositKey emptyKey = new DepositKey();
        assertNotNull(emptyKey);
    }

    @Test
    // TODO: remove once payment dto service etc are moved to v3
    public void testPayeeDto() {
        PayeeDto payee = new PayeeDto();
        payee.setAccountId("id");
        payee.setAccountKey("accountkey");
        payee.setAccountName("name");
        payee.setCode("code");
        payee.setCrn("crn");
        payee.setDevieNumber("devie");
        payee.setFixedCRN(true);
        payee.setLimit("limit");
        payee.setNickname("nickname");
        payee.setPayeeType("payee type");
        payee.setPrimary(true);
        payee.setSaveToList("saveto");
        payee.setSmsCode("smscode");
        payee.setType("type");
        assertNotNull(payee);
        assertEquals("id", payee.getAccountId());
        assertEquals("accountkey", payee.getAccountKey());
        assertEquals("name", payee.getAccountName());
        assertEquals("code", payee.getCode());
        assertEquals("crn", payee.getCrn());
        assertEquals("devie", payee.getDevieNumber());
        assertEquals(true, payee.isFixedCRN());
        assertEquals("limit", payee.getLimit());
        assertEquals("nickname", payee.getNickname());
        assertEquals("payee type", payee.getPayeeType());
        assertEquals(true, payee.isPrimary());
        assertEquals("saveto", payee.getSaveToList());
        assertEquals("smscode", payee.getSmsCode());
        assertEquals("type", payee.getType());
    }

    @Test
    public void testSearch_whenValidRequest_thenListReturned() {
        DepositDto empty = new DepositDto();
        assertNotNull(empty);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();
        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                "139D83A1CB75C1CF72D147DD52033573AC4EE45A0F0D56C919350C9EDA537610", OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("unused search criteria", SearchOperation.EQUALS, "test", OperationType.STRING));

        when(depositIntegrationService.loadSavedDeposits(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(deposits);
        List<DepositDto> depositDtos = depositDtoServiceImpl.search(criteriaList, new ServiceErrorsImpl());
        DepositDto depositDto1 = depositDtos.get(0);
        assertEquals(deposit1.getDepositId(), depositDto1.getKey().getDepositId());
        assertEquals(deposit1.getTransactionSeq(), depositDto1.getTransactionSeq());
        assertEquals(deposit1.getStatus().getDisplayName(), depositDto1.getStatus());
        assertEquals(deposit1.getDepositAmount(), depositDto1.getAmount());
        assertEquals(deposit1.getDescription(), depositDto1.getDescription());
        assertEquals(deposit1.getTransactionDate(), depositDto1.getTransactionDate());
        assertEquals(deposit1.getContributionType().getDisplayName(), depositDto1.getDepositType());
        assertEquals(deposit1.getOrderType().getName(), depositDto1.getOrderType());
        assertEquals(deposit1.getPayerBsb(), depositDto1.getFromPayDto().getCode());
        assertEquals(deposit1.getPayerAccount(), depositDto1.getFromPayDto().getAccountId());
        assertEquals(deposit1.getPayerName(), depositDto1.getFromPayDto().getAccountName());

        DepositDto depositDto3 = depositDtos.get(2);
        assertEquals(deposit3.getDepositId(), depositDto3.getKey().getDepositId());
        assertEquals(deposit3.getTransactionSeq(), depositDto3.getTransactionSeq());
        assertEquals(deposit3.getStatus().getDisplayName(), depositDto3.getStatus());
        assertEquals(deposit3.getDepositAmount(), depositDto3.getAmount());
        assertEquals(deposit3.getDescription(), depositDto3.getDescription());
        assertEquals(deposit3.getStartDate(), depositDto3.getTransactionDate());
        assertEquals(deposit3.getEndDate(), depositDto3.getRepeatEndDate());
        assertEquals(deposit3.getMaxCount().toString(), depositDto3.getEndRepeatNumber());
        assertEquals(deposit3.getRecurringFrequency().name(), depositDto3.getFrequency());
        assertEquals(deposit3.getContributionType().getDisplayName(), depositDto3.getDepositType());
        assertEquals(deposit3.getOrderType().getName(), depositDto3.getOrderType());
        assertEquals(deposit3.getPayerBsb(), depositDto3.getFromPayDto().getCode());
        assertEquals(deposit3.getPayerAccount(), depositDto3.getFromPayDto().getAccountId());

        DepositDto depositDto4 = depositDtos.get(3);
        assertEquals(emptyRecurringDeposit.getDepositId(), depositDto4.getKey().getDepositId());
        assertEquals(emptyRecurringDeposit.getTransactionSeq(), depositDto4.getTransactionSeq());
        assertEquals("", depositDto4.getStatus());
        assertEquals(emptyRecurringDeposit.getDepositAmount(), depositDto4.getAmount());
        assertEquals(emptyRecurringDeposit.getDescription(), depositDto4.getDescription());
        assertEquals(null, depositDto4.getTransactionDate());
        assertEquals(null, depositDto4.getRepeatEndDate());
        assertEquals("", depositDto4.getEndRepeatNumber());
        assertEquals("", depositDto4.getFrequency());
        assertEquals("", depositDto4.getDepositType());
        assertEquals(emptyRecurringDeposit.getPayerBsb(), depositDto4.getFromPayDto().getCode());
        assertEquals(emptyRecurringDeposit.getPayerAccount(), depositDto4.getFromPayDto().getAccountId());
    }

    @Test
    public void testDepositConstructor_whenEmpty_thenPayerNotPopulated() {
        DepositDetailsImpl deposit = new DepositDetailsImpl(null, null, depositDto.getAmount(), CurrencyType.AustralianDollar,
                depositDto.getDescription(), null, null, ContributionType.PERSONAL, null, new ArrayList<ValidationError>(), null);
        assertNull(deposit.getPayeeMoneyAccount());
        assertNull(deposit.getPayerBsb());
        assertNull(deposit.getPayerAccount());

        deposit = new DepositDetailsImpl(null, null, depositDto.getAmount(), CurrencyType.AustralianDollar,
                depositDto.getDescription(), null, ContributionType.PERSONAL);
        assertNull(deposit.getPayeeMoneyAccount());
        assertNull(deposit.getPayerBsb());
        assertNull(deposit.getPayerAccount());
    }

    @Test
    public void testDelete_whenOneOff_thenDeleteOneOffCalled() {
        DepositKey key = new DepositKey("123456");
        Mockito.doNothing().when(depositIntegrationService).deleteDeposit(Mockito.anyString(), Mockito.any(ServiceErrors.class));
        depositDtoServiceImpl.delete(key, new ServiceErrorsImpl());
        Mockito.verify(depositIntegrationService).deleteDeposit(Mockito.anyString(), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testDelete_whenRecurring_thenDeleteRecurringCalled() {
        RecurringDepositKey key = new RecurringDepositKey("456789");
        Mockito.doNothing().when(depositIntegrationService).deleteRecurringDeposit(Mockito.anyString(),
                Mockito.any(ServiceErrors.class));
        depositDtoServiceImpl.delete(key, new ServiceErrorsImpl());
        Mockito.verify(depositIntegrationService).deleteRecurringDeposit(Mockito.anyString(), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void testValidate() {
        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError("btfg$chk_fund_cap", null, null, ValidationError.ErrorType.ERROR));
        depositDetails.setErrors(errors);

        List<DomainApiErrorDto> apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("btfg$chk_fund_cap", "domain", "reason", null, DomainApiErrorDto.ErrorType.ERROR));
        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);

        when(depositIntegrationService.validateDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.setIsRecurring(false);
        depositDtoFinal.setErrors(apiErrors);
        depositDtoFinal.setReceiptNumber("123");
        depositDtoFinal.setTransactionSeq("2");
        DepositDto finalDepositDto = depositDtoServiceImpl.validate(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        assertEquals(finalDepositDto.getErrors().get(0).getErrorId(), "btfg$chk_fund_cap");
    }

    @Test
    public void testValidateRecurringDeposit() {
        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError("btfg$chk_fund_cap", null, null, ValidationError.ErrorType.ERROR));
        recurringDepositDetails.setErrors(errors);

        List<DomainApiErrorDto> apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("btfg$chk_fund_cap", "domain", "reason", null, DomainApiErrorDto.ErrorType.ERROR));
        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);

        when(depositIntegrationService.validateDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.setErrors(apiErrors);
        depositDtoFinal.setReceiptNumber("123");
        depositDtoFinal.setTransactionSeq("2");
        DepositDto finalDepositDto = depositDtoServiceImpl.validate(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        assertEquals(finalDepositDto.getErrors().get(0).getErrorId(), "btfg$chk_fund_cap");
    }

    @Test
    public void testSubmit() {
        List<ValidationError> warnings = new ArrayList<>();
        warnings.add(new ValidationError("btfg$chk_contr_cap_exce", null, null, ValidationError.ErrorType.WARNING));
        depositDetails.setWarnings(warnings);

        List<DomainApiErrorDto> apiWarnings = new ArrayList<>();
        apiWarnings.add(
                new DomainApiErrorDto("btfg$chk_contr_cap_exce", "domain", "reason", null, DomainApiErrorDto.ErrorType.WARNING));
        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiWarnings);

        when(depositIntegrationService.submitDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setIsRecurring(false);
        depositDtoFinal.setReceiptNumber("123");
        depositDtoFinal.setTransactionSeq("2");
        depositDtoFinal.setWarnings(apiWarnings);
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
        assertEquals(finalDepositDto.getWarnings().get(0).getErrorId(), "btfg$chk_contr_cap_exce");
    }

    @Test
    public void testSubmitRecurringDeposit() {
        List<ValidationError> warnings = new ArrayList<>();
        warnings.add(new ValidationError("btfg$chk_contr_cap_exce", null, null, ValidationError.ErrorType.WARNING));
        depositDetails.setWarnings(warnings);

        List<DomainApiErrorDto> apiWarnings = new ArrayList<>();
        apiWarnings.add(
                new DomainApiErrorDto("btfg$chk_contr_cap_exce", "domain", "reason", null, DomainApiErrorDto.ErrorType.WARNING));
        when(movemoneyGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiWarnings);

        when(depositIntegrationService.submitDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setPayeeType(PayeeType.PAY_ANYONE.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setReceiptNumber("123");
        depositDtoFinal.setTransactionSeq("2");
        DepositDto finalDepositDto = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());
        assertNotNull(finalDepositDto);
    }

    @Test
    public void testCreate() {
        when(depositIntegrationService.createDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);
        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setIsRecurring(false);
        DepositDto finalDepositDto = depositDtoServiceImpl.create(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
    }

    @Test
    public void testCreateRecurringDeposit() {
        when(depositIntegrationService.createDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setPayeeType(PayeeType.PAY_ANYONE.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        DepositDto finalDepositDto = depositDtoServiceImpl.create(depositDtoFinal, new ServiceErrorsImpl());
        assertNotNull(finalDepositDto);
    }

    @Test
    public void testUpdate() {
        when(depositIntegrationService.updateDeposit(any(DepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(depositDetails);
        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setIsRecurring(false);
        depositDtoFinal.setReceiptNumber("123");
        depositDtoFinal.setTransactionSeq("2");
        DepositDto finalDepositDto = depositDtoServiceImpl.update(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);
    }

    @Test
    public void testUpdateRecurringDeposit() {
        when(depositIntegrationService.updateDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);

        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        depositDtoFinal.getFromPayDto().setPayeeType(PayeeType.PAY_ANYONE.name());
        depositDtoFinal.getFromPayDto().setAccountId("1234567");
        depositDtoFinal.setReceiptNumber("123");
        depositDtoFinal.setTransactionSeq("2");
        DepositDto finalDepositDto = depositDtoServiceImpl.update(depositDtoFinal, new ServiceErrorsImpl());
        assertNotNull(finalDepositDto);
    }

    @Test
    public void shouldFailDepositTx_WhenLinkedAccount_WithOtherAccount() {

        when(depositIntegrationService.submitDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class)))
                .thenReturn(recurringDepositDetails);
        DepositDto depositDtoFinal = getDepositDto(PayeeType.LINKED.name());
        DepositDto response = depositDtoServiceImpl.submit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(response);
        assertTrue(response.getErrors() != null);
        assertTrue(response.getErrors().size() > 0);
        assertEquals(response.getErrors().get(0).getErrorId(), Constants.ACCT_NOT_IN_PAYEE_LIST);
    }

    @Test
    public void testToRecurringDepositDetails_whenEmptyDto_thenMappedObject() throws Exception {
        RecurringDepositDetails deposit = depositDtoServiceImpl.toRecurringDepositDetails(emptyRecurringDepositDto,
                new ServiceErrorsImpl());
        assertNull(deposit.getMaxCount());
        assertNull(deposit.getEndDate());
        assertNull(deposit.getTransactionDate());
        assertNull(deposit.getContributionType());
    }

    @Test
    public void testToRecurringDepositDetails_whenMaxRepeatsDto_thenMappedObject() throws Exception {
        RecurringDepositDetails deposit = depositDtoServiceImpl.toRecurringDepositDetails(recurringDepositMaxRepeatsDto,
                new ServiceErrorsImpl());
        assertEquals(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId(), deposit.getPayeeMoneyAccount());
        assertEquals(recurringDepositMaxRepeatsDto.getFromPayDto().getAccountId(), deposit.getPayerAccount());
        assertEquals(recurringDepositMaxRepeatsDto.getFromPayDto().getCode(), deposit.getPayerBsb());
        assertEquals(recurringDepositMaxRepeatsDto.getAmount(), deposit.getDepositAmount());
        assertEquals(CurrencyType.AustralianDollar, deposit.getCurrencyType());
        assertEquals(recurringDepositMaxRepeatsDto.getDescription(), deposit.getDescription());
        assertEquals(recurringDepositMaxRepeatsDto.getTransactionDate(), deposit.getTransactionDate());
        assertEquals(ContributionType.forName(recurringDepositMaxRepeatsDto.getDepositType()), deposit.getContributionType());
        assertEquals(RecurringFrequency.valueOf(recurringDepositMaxRepeatsDto.getFrequency()), deposit.getRecurringFrequency());
        assertEquals(Integer.parseInt(recurringDepositMaxRepeatsDto.getEndRepeatNumber()), deposit.getMaxCount().intValue());
    }

    @Test
    public void testToRecurringDepositDetails_whenEndDateDto_thenMappedObject() throws Exception {
        RecurringDepositDetails deposit = depositDtoServiceImpl.toRecurringDepositDetails(recurringDepositEndDateDto,
                new ServiceErrorsImpl());
        assertEquals(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId(), deposit.getPayeeMoneyAccount());
        assertEquals(recurringDepositEndDateDto.getFromPayDto().getAccountId(), deposit.getPayerAccount());
        assertEquals(recurringDepositEndDateDto.getFromPayDto().getCode(), deposit.getPayerBsb());
        assertEquals(recurringDepositEndDateDto.getAmount(), deposit.getDepositAmount());
        assertEquals(CurrencyType.AustralianDollar, deposit.getCurrencyType());
        assertEquals(recurringDepositEndDateDto.getDescription(), deposit.getDescription());
        assertEquals(recurringDepositEndDateDto.getTransactionDate(), deposit.getTransactionDate());
        assertEquals(ContributionType.forName(recurringDepositEndDateDto.getDepositType()), deposit.getContributionType());
        assertEquals(RecurringFrequency.valueOf(recurringDepositEndDateDto.getFrequency()), deposit.getRecurringFrequency());
        assertEquals(recurringDepositEndDateDto.getRepeatEndDate(), deposit.getEndDate());
    }

    @Test
    public void testToDepositDetails_whenEmptyDto_thenMappedObject() throws Exception {
        DepositDetails deposit = depositDtoServiceImpl.toDepositDetails(emptyDepositDto, new ServiceErrorsImpl());
        assertNull(deposit.getTransactionDate());
        assertNull(deposit.getContributionType());
    }

    @Test
    public void testToDepositDetails_whenDto_thenMappedObject() throws Exception {
        DepositDetails deposit = depositDtoServiceImpl.toDepositDetails(depositDto, new ServiceErrorsImpl());
        assertEquals(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId(), deposit.getPayeeMoneyAccount());
        assertEquals(depositDto.getFromPayDto().getAccountId(), deposit.getPayerAccount());
        assertEquals(depositDto.getFromPayDto().getCode(), deposit.getPayerBsb());
        assertEquals(depositDto.getAmount(), deposit.getDepositAmount());
        assertEquals(CurrencyType.AustralianDollar, deposit.getCurrencyType());
        assertEquals(depositDto.getDescription(), deposit.getDescription());
        assertEquals(depositDto.getTransactionDate(), deposit.getTransactionDate());
        assertEquals(ContributionType.forName(depositDto.getDepositType()), deposit.getContributionType());
    }

    @Test
    public void testToDepositDto_whenNullDeposit_thenEmptyDto() throws Exception {
        DepositDto depositDto = depositDtoServiceImpl.toDepositDto(null, null);
        assertNull(depositDto.getKey());
    }

    @Test
    public void testToDepositDto_whenEmptyRecurringDeposit_thenMappedDto() throws Exception {
        DepositDto mappedDepositDto = depositDtoServiceImpl.toDepositDto(emptyRecurringDeposit, emptyDepositDto);
        assertEquals(depositDto.getKey().getDepositId(), mappedDepositDto.getKey().getDepositId());
        assertNull(mappedDepositDto.getFrequency());
        assertNull(mappedDepositDto.getTransactionDate());
        assertNull(mappedDepositDto.getRepeatEndDate());
        assertNull(mappedDepositDto.getDepositType());
    }

    @Test
    public void testToDepositDto_whenRecurringDeposit_thenMappedDto() throws Exception {
        recurringDeposit.setReceiptNumber("depositId");
        DepositDto mappedDepositDto = depositDtoServiceImpl.toDepositDto(recurringDeposit, depositDto);
        assertEquals(depositDto.getKey().getDepositId(), mappedDepositDto.getKey().getDepositId());
        assertEquals(recurringDeposit.getDepositAmount(), mappedDepositDto.getAmount());
        assertEquals(recurringDeposit.getRecurringFrequency().name(), mappedDepositDto.getFrequency());
        assertEquals(recurringDeposit.getDescription(), mappedDepositDto.getDescription());
        assertEquals(recurringDeposit.getStartDate(), mappedDepositDto.getTransactionDate());
        assertEquals(recurringDeposit.getEndDate(), mappedDepositDto.getRepeatEndDate());
        assertEquals(recurringDeposit.getContributionType().getDisplayName(), mappedDepositDto.getDepositType());
    }

    @Test
    public void testToDepositDto_whenEmptyDeposit_thenMappedDto() throws Exception {
        DepositDto mappedDepositDto = depositDtoServiceImpl.toDepositDto(emptyDeposit, emptyDepositDto);
        assertEquals(depositDto.getKey().getDepositId(), mappedDepositDto.getKey().getDepositId());
        assertNull(mappedDepositDto.getTransactionDate());
        assertNull(mappedDepositDto.getDepositType());
    }

    @Test
    public void testToDepositDto_whenDeposit_thenMappedDto() throws Exception {
        deposit.setReceiptNumber("depositId");
        DepositDto mappedDepositDto = depositDtoServiceImpl.toDepositDto(deposit, depositDto);
        assertEquals(depositDto.getKey().getDepositId(), mappedDepositDto.getKey().getDepositId());
        assertEquals(deposit.getDepositAmount(), mappedDepositDto.getAmount());
        assertEquals(deposit.getDescription(), mappedDepositDto.getDescription());
        assertEquals(deposit.getTransactionDate(), mappedDepositDto.getTransactionDate());
        assertEquals(deposit.getRecurringFrequency().name(), mappedDepositDto.getFrequency());
        assertEquals(deposit.getContributionType().getDisplayName(), mappedDepositDto.getDepositType());
    }

    @Test
    public void testFind_whenValidRequest_thenReturned() {
        DepositDetailsImpl deposit = new DepositDetailsImpl();
        deposit.setDepositId("abc");
        deposit.setTransactionSeq("1");
        deposit.setStatus(DepositStatus.ACTIVE);
        deposit.setDepositAmount(new BigDecimal("1000"));
        deposit.setDescription("desc1");
        deposit.setTransactionDate(new DateTime("2014-09-01"));
        deposit.setContributionType(ContributionType.PERSONAL);
        deposit.setOrderType(OrderType.SUPER_ONE_OFF_CONTRIBUTION);
        deposit.setPayerBsb("012345");
        deposit.setPayerAccount("123456");
        deposit.setPayerName("John Smith");
        deposit.setPayeeMoneyAccount("456789");

        when(depositIntegrationService.loadSavedDeposit(any(String.class), any(ServiceErrorsImpl.class))).thenReturn(deposit);

        DepositDto depositDto = depositDtoServiceImpl.find(new DepositKey("depositId"), new ServiceErrorsImpl());

        assertEquals(deposit.getDepositId(), depositDto.getKey().getDepositId());
        assertEquals(deposit.getTransactionSeq(), depositDto.getTransactionSeq());
        assertEquals(deposit.getStatus().getDisplayName(), depositDto.getStatus());
        assertEquals(deposit.getDepositAmount(), depositDto.getAmount());
        assertEquals(deposit.getDescription(), depositDto.getDescription());
        assertEquals(deposit.getTransactionDate(), depositDto.getTransactionDate());
        assertEquals(deposit.getContributionType().getDisplayName(), depositDto.getDepositType());
        assertEquals(deposit.getOrderType().getName(), depositDto.getOrderType());
        assertEquals(deposit.getPayerBsb(), depositDto.getFromPayDto().getCode());
        assertEquals(deposit.getPayerAccount(), depositDto.getFromPayDto().getAccountId());
        assertEquals(deposit.getPayerName(), depositDto.getFromPayDto().getAccountName());
    }

    public DepositInterface getDepositInterfaceObj() {
        DepositDto depositDto = new DepositDto();
        depositDto.setAmount(new BigDecimal(100));
        depositDto.setDescription("Test Deposit");
        depositDto.setTransactionDate(new DateTime("2014-09-02"));
        depositDto.setEndRepeat("setDate");
        depositDto.setIsRecurring(true);
        depositDto.setAccountKey(accountKey);
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
        confirmDepositConversation.setDate(depositDto.getTransactionDate().toString());
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
        depositDto.setAccountKey(accountKey);

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

        depositDto.setAmount(new BigDecimal(12));
        depositDto.setDepositType("Spouse");
        depositDto.setDescription("Test Payment");
        depositDto.setEndRepeat("setEndDate");
        depositDto.setIsRecurring(true);
        depositDto.setFrequency(RecurringFrequency.Monthly.name());
        depositDto.setTransactionDate(new DateTime("2014-09-02"));
        depositDto.setRepeatEndDate(new DateTime("2015-09-02"));

        return depositDto;
    }

    public PayeeDetails getPayeeDetailsObj() {
        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        MoneyAccountIdentifierImpl maccId = new MoneyAccountIdentifierImpl();
        // maccId.setMoneyAccountId("12345");
        maccId.setMoneyAccountId("moneyAccountId");
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
