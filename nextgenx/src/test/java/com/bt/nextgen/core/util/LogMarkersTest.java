package com.bt.nextgen.core.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.core.Bank;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.DepositInterface;
import com.bt.nextgen.payments.web.model.PaymentInterface;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.bt.nextgen.portfolio.web.model.PortfolioInterface;
import com.bt.nextgen.termdeposit.web.model.TermDepositAccountModel;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.math.BigDecimal;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class LogMarkersTest {
    private static Logger logger = getLogger(LogMarkersTest.class);

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("5000.00");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("24888888.00");

    private static final Bank BOM = new Bank("TCP.CCC.BOM");
    private static final Bank BSA = new Bank("TCP.CCC.BSA");
    private static final Bank SGB = new Bank("TCP.CCC.SGB");
    private static final Bank WBC = new Bank("TCP.CCC.WBC");

    @Mock
    private UserProfileService userProfileService;

    @Mock
    PortfolioInterface portfolioModel;

    @InjectMocks
    private final LogMarkers logMarkers = new LogMarkers();

    private final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)
            getLogger(ROOT_LOGGER_NAME);

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    private Level originalRootLevel;

    @Before
    public void setup() {
        when(userProfileService.getUsername()).thenReturn("adviser");
        when(userProfileService.getFirstName()).thenReturn("Jennifer");
        when(userProfileService.getLastName()).thenReturn("Lopez");

        when(portfolioModel.getAccountName()).thenReturn("accountName");
        when(portfolioModel.getAccountType()).thenReturn("accountType");
        when(portfolioModel.getAccountId()).thenReturn("12345678");

        logMarkers.setUserDetailsService(userProfileService);
        when(mockAppender.getName()).thenReturn("AUDIT");
        root.addAppender(mockAppender);
        originalRootLevel = root.getLevel();
        root.setLevel(Level.INFO);
    }

    @After
    public void detachMockAppenderAndRestoreOriginalLoggingLevel() {
        root.detachAppender("AUDIT");
        root.setLevel(originalRootLevel);
    }

    @Test
    public void testResetUser() throws Exception {
        LogMarkers.audit_resetUser("username", LogMarkers.Status.SUCCESS, logger);
        verifyOutput("username [username] STATUS: [SUCCESS]");
    }

    @Test
    public void testChangeDailyLimit() throws Exception {

        LogMarkers.audit_changeDailyLimit(portfolioModel, LogMarkers.Status.SUCCESS, "$20000", "$50000", PayeeType.BPAY,
                logger);
        verifyOutput(
                "User name [adviser] First Name [Jennifer] Surname [Lopez] account name [accountName] Account type [accountType] Account ID [12345678] Changing payment limit for [BPAY] From amount [$20000] To amount [$50000] STATUS: [SUCCESS]");
    }

    //
    @Test
    public void testAddPayee() {
        PayeeModel payee = mock(PayeeModel.class);
        when(payee.getPayeeType()).thenReturn(PayeeType.BPAY);
        when(payee.getCode()).thenReturn("12345");
        when(payee.getCode()).thenReturn("827832");
        when(payee.getNickname()).thenReturn("nickName");
        when(payee.getName()).thenReturn("Name");
        LogMarkers.audit_add_payee(portfolioModel, payee, LogMarkers.Status.SUCCESS, logger, "");
        verifyOutput(
                "User name [adviser] First Name [Jennifer] Surname [Lopez] account name [accountName] Account type [accountType] Account ID [12345678] Adding a [BPAY] account, Biller code [827832] CRN [827832] Biller nickname [nickName] STATUS: [SUCCESS]");
        when(payee.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
        LogMarkers.audit_add_payee(portfolioModel, payee, LogMarkers.Status.SUCCESS, logger, "");
        verifyOutput(
                "User name [adviser] First Name [Jennifer] Surname [Lopez] account name [accountName] Account type [accountType] Account ID [12345678] Adding a [BPAY] account, Biller code [827832] CRN [827832] Biller nickname [nickName] STATUS: [SUCCESS]");
    }

    @Test
    public void testDeletePayee() {
        PayeeModel payee = mockPayeeModel();
        LogMarkers.audit_delete_payee(portfolioModel, payee, LogMarkers.Status.SUCCESS, logger);
        verifyOutput(
                "Deleting a [BPAY] account, Biller code [12345], CRN [827832], Biller nickname [nickName] STATUS: [SUCCESS]");
        when(payee.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
        LogMarkers.audit_delete_payee(portfolioModel, payee, LogMarkers.Status.SUCCESS, logger);
        verifyOutput(
                "Deleting a [PAY_ANYONE] account, Account name [Name], BSB [12345], Account number [827832], Nickname [nickName] STATUS: [SUCCESS]");
    }

    @Test
    public void testAuditLinkedAccount() {
        PayeeModel payee = mockPayeeModel();
        String operationType = "operationType";
        LogMarkers.audit_linkedAccount(portfolioModel, operationType, payee, LogMarkers.Status.SUCCESS, logger);
        verifyOutput(
                "operationType a [BPAY] account, Account name [Name], BSB [12345], Account number [827832], Nickname [nickName] STATUS: [SUCCESS]");
    }

    @Test
    public void testPayAnyone() {
        PayeeModel payee = mockPayeeModel();
        LogMarkers.audit_add_PayAnyone(portfolioModel, payee, LogMarkers.Status.SUCCESS, logger);
        verifyOutput(
                "Adding a [BPAY] account, Account name [Name], BSB [12345], Account number [827832], Nickname [nickName] STATUS: [SUCCESS]");
    }

    @Test
    public void testaudit_submitPayment_PayAnyOne() {
    	PaymentInterface payment = mock(PaymentInterface.class);
    	PayeeModel payee = mockPayeeModel();
    	when(payment.getTo()).thenReturn(payee);
    	when(payee.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
    	String failureReason = "testReason";
        LogMarkers.audit_submitPayment(portfolioModel, payment, LogMarkers.Status.SUCCESS, logger,failureReason);
        verifyOutput("payments: To (Account name [Name] BSB [12345] Account number [827832] Amount: [null]) STATUS: [SUCCESS]");
       }

    @Test
    public void testaudit_submitPayment_Bpay() {
    	PaymentInterface payment = mock(PaymentInterface.class);
    	PayeeModel payee = mockPayeeModel();
    	when(payment.getTo()).thenReturn(payee);
    	String failureReason = "testReason";
        LogMarkers.audit_submitPayment(portfolioModel, payment, LogMarkers.Status.SUCCESS, logger,failureReason);
        verifyOutput("payments: To (Biller Name [Name] Biller Code [12345] CRN [827832] Amount: [null]) STATUS: [SUCCESS]");
       }


    @Test
    public void testaudit_submitDeposit() {
    	DepositInterface deposit = mock(DepositInterface.class);
    	CashAccountModel cashAccount = mockCashAccountModel();
    	when(deposit.getAccount()).thenReturn(cashAccount);
    	when(deposit.getAmount()).thenReturn(new BigDecimal("70799.98"));
    	String failureReason = "testReason";
        LogMarkers.audit_submitDeposit(portfolioModel, deposit, LogMarkers.Status.SUCCESS, logger,failureReason);
        verifyOutput("Deposit: From (Account name [testUser] BSB [12345] Account number [testUser]) Amount: [70799.98] STATUS: [SUCCESS]");
       }

	@Test
    public void testaudit_withdraw_termDeposit() {
    	DepositInterface deposit = mock(DepositInterface.class);
    	CashAccountModel cashAccount = mockCashAccountModel();
    	when(deposit.getAccount()).thenReturn(cashAccount);
    	when(deposit.getAmount()).thenReturn(new BigDecimal("70799.98"));
    	String tdAccountId = "test001";
    	String failureReason = "testReason";
    	TermDepositAccountModel tdacc = mock(TermDepositAccountModel.class);
        LogMarkers.audit_withdraw_termDeposit(portfolioModel,LogMarkers.Status.SUCCESS,tdAccountId, tdacc,logger,failureReason);
        verifyOutput("Early withdrawal: Term Deposit: TD Id [test001] TD break date [null] Brand [null] Maturity amount [null] Tenure(term) [null] Rate [null], Amount invested [null], interest paid type [null] STATUS: [SUCCESS]");
       }


	@Test
    public void testaudit_changing_maturityInstruction() {
    	DepositInterface deposit = mock(DepositInterface.class);
    	CashAccountModel cashAccount = mockCashAccountModel();
    	when(deposit.getAccount()).thenReturn(cashAccount);
    	when(deposit.getAmount()).thenReturn(new BigDecimal("70799.98"));
    	String failureReason = "testReason";
    	String todayDate = ApiFormatter.asShortDate(new DateTime());
    	TermDepositAccountModel tdacc = mock(TermDepositAccountModel.class);
        LogMarkers.audit_changing_maturityInstruction(portfolioModel,LogMarkers.Status.SUCCESS, tdacc,logger,failureReason);
        verifyOutput("Changing maturity instructions: edit date [" + todayDate + "]  Tenure(term) [null] Amount invested [null] STATUS: [SUCCESS]");
       }

    private PayeeModel mockPayeeModel() {
        PayeeModel payee = mock(PayeeModel.class);
        when(payee.getPayeeType()).thenReturn(PayeeType.BPAY);
        when(payee.getCode()).thenReturn("12345");
        when(payee.getReference()).thenReturn("827832");
        when(payee.getNickname()).thenReturn("nickName");
        when(payee.getName()).thenReturn("Name");
        return payee;
    }

    private CashAccountModel mockCashAccountModel() {
    	CashAccountModel cashAccountModel = mock(CashAccountModel.class);
        when(cashAccountModel.getIdpsAccountName()).thenReturn("testUser");
        when(cashAccountModel.getBsb()).thenReturn("12345");
        return cashAccountModel;
    }

    private void verifyOutput(final String info) {
        verify(mockAppender).doAppend(argThat(containsMessage(info)));
    }

    public static ArgumentMatcher<ILoggingEvent> containsMessage(final String msg) {
        return new ArgumentMatcher<ILoggingEvent>() {
            @Override
            public boolean matches(Object argument) {
                return ((ILoggingEvent) argument).getFormattedMessage().contains(msg);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Message containing the text: [").appendText(msg).appendText("]");
            }
        };
    }
}
