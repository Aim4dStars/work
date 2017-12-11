package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v1.service.DepositDtoService;
import com.bt.nextgen.api.account.v1.service.DepositDtoServiceImpl;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.addressbook.web.model.GenericPayee;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.ConfirmDepositConversation;
import com.bt.nextgen.payments.web.model.DepositInterface;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequestModel;
import com.bt.nextgen.service.avaloq.deposit.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.gateway.businessunit.Payee;
import com.bt.nextgen.service.avaloq.payeedetails.CashAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeLimitImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.deposit.DepositIntegrationService;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DepositReceiptTest
{

    @InjectMocks
    private DepositDtoServiceImpl depositDtoServiceImpl;

    @InjectMocks
    private DepositReceipt depositReceipt;

    @Mock
    private DepositDtoService depositService;

    @Mock
    PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    DepositIntegrationService depositIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    protected StaticIntegrationService staticIntegrationService;

    @Mock
    CmsService cmsService;

    private static String PAYMENT_LIMITS = "PAYMENT_LIMITS";

    List <PayeeModel> payeeModelList;

    List <PhoneModel> phoneModelList;

    PortfolioRequest portfolioRequest;

    PersonInterface personInterface;

    List <Payee> payeeList = null;

    PayeeDetails payeeDetails = null;
    RecurringDepositDetails recurringDepositDetails = null;
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static MockHttpSession session;




    Map <String, DepositDto> depositMap = null;
    private CodeImpl statusCode;

    @Before
    public void setup() throws Exception
    {

        personInterface = mock(PersonInterface.class);

        payeeModelList = new ArrayList <PayeeModel>();
        PayeeModel payeeModel = new PayeeModel();
        //payeeModel.setId("1");
        payeeModel.setCode("0120");
        payeeModel.setName("Test");
        payeeModel.setPayeeType(PayeeType.LINKED);
        payeeModel.setReference("123456789");
        payeeModelList.add(payeeModel);
        statusCode = new CodeImpl("1", "Verified","vfy");
        statusCode.setIntlId("vfy");
        statusCode.addField("can_vfy_code","-");
        payeeList = new ArrayList <Payee>();
        GenericPayee payee = new GenericPayee();
        payee.setName("Test");
        payee.setCode("0120");
        payee.setPayeeType(PayeeType.LINKED);
        payee.setReference(payeeModel.getReference());
        payeeList.add(payee);

        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        MoneyAccountIdentifierImpl maccId = new MoneyAccountIdentifierImpl();
        maccId.setMoneyAccountId("12345");
        payeeDetailsImpl.setMoneyAccountIdentifier(maccId);
        payeeDetailsImpl.setModifierSeqNumber(new BigDecimal("120"));
        List <PayeeLimit> payeeLimitList = new ArrayList <>();
        PayeeLimitImpl payLim = new PayeeLimitImpl();
        payLim.setCurrency("aud");
        payLim.setMetaType(null);
        payLim.setOrderType(null);
        payeeLimitList.add(payLim);
        payeeDetailsImpl.setPayeeLimits(payeeLimitList);
        List <CashAccountDetailsImpl> cashAccList = new ArrayList <CashAccountDetailsImpl>();
        CashAccountDetailsImpl cashAcc = new CashAccountDetailsImpl();
        cashAccList.add(cashAcc);
        payeeDetailsImpl.setCashAccount(cashAccList);
        List <LinkedAccount> linkAccDetList = new ArrayList <>();
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

        List<PayAnyOne> payAnyOnes  = new ArrayList <>();
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
        payeeDetails = payeeDetailsImpl;

        phoneModelList = new ArrayList <PhoneModel>();
        PhoneModel phoneModel = new PhoneModel();
        phoneModel.setPhoneNumber("1256290873");
        phoneModel.setPrimary(true);
        phoneModelList.add(phoneModel);

        RecurringDepositDetailsImpl recurringDepositDetailsImpl = new RecurringDepositDetailsImpl();
        recurringDepositDetailsImpl.setDepositAmount(new BigDecimal("1234"));
        recurringDepositDetailsImpl.setRecurringFrequency(RecurringFrequency.Monthly);
        recurringDepositDetailsImpl.setDescription("Test");
        recurringDepositDetailsImpl.setStartDate(new Date());
        recurringDepositDetailsImpl.setRecieptNumber("121234");
        recurringDepositDetailsImpl.setTransactionDate(new Date("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)"));

        recurringDepositDetails = recurringDepositDetailsImpl;
        portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId("36846");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        request.setSession(session);

    }

    @Test
    public void testSearch()
    {
        Mockito.when(cmsService.getContent(PAYMENT_LIMITS)).thenReturn("10000, 25000, 50000, 100000, 200000");

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class))).thenReturn(payeeDetails);
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.LINKED_ACCOUNT_STATUS),
                Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(statusCode);

        List <DepositDto> depositDtoList = depositDtoServiceImpl.loadPayeesForDeposits(portfolioRequest);

        assertNotNull(depositDtoList);

        assertTrue(depositDtoList.size() == 1);

    }

    @Test
    public void testValidateDeposit()
    {

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class))).thenReturn(payeeDetails);
        when(depositIntegrationService.validateDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurringDepositDetails);
        DepositDto depositDto = new DepositDto();
        depositDto.setAmount(new BigDecimal(100));
        depositDto.setDescription("Test Deposit");
        depositDto.setTransactionDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        depositDto.setEndRepeat("setDate");
        depositDto.setIsRecurring(true);
        depositDto.setKey(new AccountKey("36846"));
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
        confirmDepositConversation.setPayeeDescription(depositDto.getDescription());
        DepositDto depositDtoFinal = new DepositDto();
        AccountKey key = new AccountKey("36846");
        depositDtoFinal.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountId("12351");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        depositDtoFinal.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        depositDtoFinal.setToPayteeDto(toDto);

        depositDtoFinal.setAmount(new BigDecimal(12));
        depositDtoFinal.setDescription("Test Payment");
        depositDtoFinal.setEndRepeat("setEndDate");
        depositDtoFinal.setIsRecurring(true);
        depositDtoFinal.setFrequency(RecurringFrequency.Monthly.name());
        depositDtoFinal.setTransactionDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        depositDtoFinal.setRepeatEndDate("Tue Oct 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");

        DepositDto finalDepositDto = depositDtoServiceImpl.validateDeposit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);

    }

    @Test
    public void testSubmitDeposit()
    {
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class), any(ServiceErrorsImpl.class))).thenReturn(payeeDetails);
        when(depositIntegrationService.submitDeposit(any(RecurringDepositDetailsImpl.class), any(ServiceErrorsImpl.class))).thenReturn(recurringDepositDetails);
        DepositDto depositDto = new DepositDto();
        depositDto.setAmount(new BigDecimal(100));
        depositDto.setDescription("Test Deposit");
        depositDto.setTransactionDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        depositDto.setEndRepeat("setDate");
        depositDto.setIsRecurring(true);
        depositDto.setKey(new AccountKey("36846"));
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
        confirmDepositConversation.setPayeeDescription(depositDto.getDescription());

        DepositDto depositDtoFinal = new DepositDto();
        AccountKey key = new AccountKey("36846");
        depositDtoFinal.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountId("1234567");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        depositDtoFinal.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        depositDtoFinal.setToPayteeDto(toDto);

        depositDtoFinal.setAmount(new BigDecimal(12));
        depositDtoFinal.setDescription("Test Payment");
        depositDtoFinal.setEndRepeat("setEndDate");
        depositDtoFinal.setIsRecurring(true);
        depositDtoFinal.setFrequency(RecurringFrequency.Monthly.name());
        depositDtoFinal.setTransactionDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        depositDtoFinal.setRepeatEndDate("Tue Oct 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");

        DepositDto finalDepositDto = depositDtoServiceImpl.submitDeposit(depositDtoFinal, new ServiceErrorsImpl());

        assertNotNull(finalDepositDto);

        assertNotNull(finalDepositDto.getRecieptNumber());
        assertNotNull(finalDepositDto.getFromPayDto().getCode());
        assertNotNull(finalDepositDto.getToPayteeDto().getCode());

    }

}
