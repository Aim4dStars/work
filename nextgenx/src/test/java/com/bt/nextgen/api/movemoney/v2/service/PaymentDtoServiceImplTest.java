package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.movemoney.v2.model.EndPaymentDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.account.BillerImpl;
import com.bt.nextgen.service.avaloq.account.LinkedAccountImpl;
import com.bt.nextgen.service.avaloq.movemoney.PaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayAnyOneImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.btfin.panorama.service.integration.RecurringFrequency;

import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentActionType;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PaymentIntegrationService;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.btfin.abs.err.v1_0.ErrType;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentDtoServiceImplTest {
    private static final String PAY_ANYONE_PERMISSION = "account.payment.anyone.create";

    @InjectMocks
    private PaymentDtoServiceImpl paymentDtoService;

    @Mock
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    private PaymentIntegrationService paymentIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private PermissionAccountDtoService acctPermissionService;

    @Mock
    private MovemoneyDtoErrorMapper movemoneyDtoErrorMapper;

    @Mock
    private HttpSession httpSession;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private PaymentDetails mockPaymentDetails;

    @Mock
    private TransactionReceiptHelper transactionReceiptHelper;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Before
    public void setup() {
        WrapAccountDetail wrapAccountDetails = mock(WrapAccountDetail.class);
        when(wrapAccountDetails.getModificationSeq()).thenReturn("10");
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any (ServiceErrors.class))).thenReturn(wrapAccountDetails);
    }

    @Test
    public void validateWithNoErrorsAndWarnings() {
        final PaymentDto paymentDto = getPaymentDto();
        final PaymentDetails paymentDetails = paymentDetails();
        final PaymentDto result;

        // avoid using PayeeType.LINKED to by pass 2FA
        paymentDto.getToPayeeDto().setPayeeType(PayeeType.BPAY.toString());

        when(paymentIntegrationService.validatePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails);
        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(null);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        result = paymentDtoService.validate(paymentDto, serviceErrors);

        verify(paymentIntegrationService).validatePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(httpSession).getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertThat("errors", result.getErrors(), nullValue());
        assertThat("warnings", result.getWarnings(), nullValue());
    }

    @Test
    public void validateWithEmptyErrorsAndWarnings() {
        final PaymentDto paymentDto = getPaymentDto();
        final PaymentDto result;
        final List<ValidationError> errors = Arrays.asList(new ValidationError("errorId1", "field2", "my error1", ErrorType.ERROR));
        final List<ValidationError> warnings = Arrays.asList(new ValidationError("warnId1", "field3", "my warning2", ErrorType.WARNING));
        final List<DomainApiErrorDto> domainErrors = Arrays.asList(new DomainApiErrorDto());
        final List<DomainApiErrorDto> domainWarnings = Arrays.asList(new DomainApiErrorDto());

        // avoid using PayeeType.LINKED to by pass 2FA
        paymentDto.getToPayeeDto().setPayeeType(PayeeType.BPAY.toString());

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());
        when(paymentIntegrationService.validatePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(mockPaymentDetails);
        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(null);
        when(mockPaymentDetails.getErrors()).thenReturn(errors);
        when(mockPaymentDetails.getWarnings()).thenReturn(warnings);
        when(movemoneyDtoErrorMapper.map(errors)).thenReturn(domainErrors);
        when(movemoneyDtoErrorMapper.map(warnings)).thenReturn(domainWarnings);

        result = paymentDtoService.validate(paymentDto, serviceErrors);

        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(paymentIntegrationService).validatePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));

        assertThat("errors", result.getErrors(), equalTo(domainErrors));
        assertThat("warnings", result.getWarnings(), equalTo(domainWarnings));
    }


    @Test
    public void update_regularWithFatalServiceError() {
        final ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        final PaymentDto paymentDto = getPaymentDto();
        final ServiceError[] filteredErrors;

        when(paymentIntegrationService.savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .then(new Answer<PaymentDetails>() {
                    @Override
                    public PaymentDetails answer(InvocationOnMock invocation) throws Throwable {
                        ServiceErrors errors = (ServiceErrors) invocation.getArguments()[1];

                        errors.addError(makeServiceError(ErrType.OVR.value(), "override error 1"));
                        errors.addError(makeServiceError(ErrType.FA.value(), "fatal error 2"));
                        errors.addError(makeServiceError(ErrType.UI.value(), "ui error 3"));

                        return paymentDetails();
                    }
                });
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, serviceErrors);

        verify(paymentIntegrationService).savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertThat("response DTO not null", responsePaymentDto, notNullValue());
        filteredErrors = serviceErrors.getErrors().values().toArray(new ServiceError[serviceErrors.getErrors().size()]);
        assertThat("has filtered errors", filteredErrors.length, equalTo(1));
        assertThat("Error type is fatal", filteredErrors[0].getType(), equalTo("fa"));
        assertThat("Error message", filteredErrors[0].getMessage(), equalTo("fatal error 2"));
    }


    //New regular pension payment
    @Test
    public void update_newRegular() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("savenewregular");
        paymentDto.setWithdrawalType("Regular Pension Payment");
        paymentDto.setReceiptNumber(null);
        paymentDto.setTransSeqNo(null);
        paymentDto.setTransactionId(null);

        when(paymentIntegrationService.savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).savePayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.SAVE_NEW_REGULAR);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.REGULAR_PENSION_PAYMENT);
        assertNull(paymentCaptor.getValue().getPositionId());
        assertNull(paymentCaptor.getValue().getTransactionSeqNo());
        assertNull(paymentCaptor.getValue().getDocId());
    }

    //new regular pension payment from existing Active
    @Test
    public void update_newRegularEditingActive() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("savenewregular");
        paymentDto.setWithdrawalType("Regular Pension Payment");
        paymentDto.setReceiptNumber(null);
        paymentDto.setTransSeqNo(null);
        paymentDto.setTransactionId("84700F8D63D18D12EEA9A48AABF471D5DF5B7BF992FF5B91"); //existing active pension storPosId

        when(paymentIntegrationService.savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(paymentIntegrationService).savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).savePayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.SAVE_NEW_REGULAR);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.REGULAR_PENSION_PAYMENT);
        assertEquals(paymentCaptor.getValue().getPositionId(), "853917");
        assertNull(paymentCaptor.getValue().getTransactionSeqNo());
        assertNull(paymentCaptor.getValue().getDocId());
    }

    //edit saved regular pension payment
    @Test
    public void update_EditSavedRegular() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("saveregular");
        paymentDto.setWithdrawalType("Regular Pension Payment");
        paymentDto.setReceiptNumber("65454");
        paymentDto.setTransSeqNo("2");
        paymentDto.setTransactionId("84700F8D63D18D12EEA9A48AABF471D5DF5B7BF992FF5B91"); //existing saved regular pension storPosId

        when(paymentIntegrationService.savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).savePayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.SAVE_REGULAR);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.REGULAR_PENSION_PAYMENT);
        assertEquals(paymentCaptor.getValue().getPositionId(), "853917");
        assertEquals(paymentCaptor.getValue().getTransactionSeqNo(), "2");
        assertEquals(paymentCaptor.getValue().getDocId(), "65454");
    }


    //cancel saved regular pension payment
    @Test
    public void update_CancelSavedRegular() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("cancelregular");
        paymentDto.setWithdrawalType("Regular Pension Payment");
        paymentDto.setReceiptNumber("65454");
        paymentDto.setTransSeqNo("3");
        paymentDto.setTransactionId(null);

        when(paymentIntegrationService.endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(paymentIntegrationService).endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).endPayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.CANCEL_REGULAR);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.REGULAR_PENSION_PAYMENT);
        assertNull(paymentCaptor.getValue().getPositionId());
        assertEquals(paymentCaptor.getValue().getTransactionSeqNo(), "3");
        assertEquals(paymentCaptor.getValue().getDocId(), "65454");
    }

    //edit saved oneoff pension payment
    @Test
    public void update_EditSavedOneOff() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("modifyoneoff");
        paymentDto.setWithdrawalType("Pension payment");
        paymentDto.setReceiptNumber("65454");
        paymentDto.setTransSeqNo("2");
        paymentDto.setTransactionId(null); //existing saved oneoff pension storPosId

        when(paymentIntegrationService.savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).savePayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.MODIFY_ONEOFF);
        assertEquals("modifyoneoff", PaymentActionType.MODIFY_ONEOFF.getLabel());
        assertEquals(PaymentActionType.MODIFY_ONEOFF, PaymentActionType.fromAction("hold_pay_hold_pay"));
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        assertNull(paymentCaptor.getValue().getPositionId());
        assertEquals(paymentCaptor.getValue().getTransactionSeqNo(), "2");
        assertEquals(paymentCaptor.getValue().getDocId(), "65454");
    }

    //cancel saved oneoff pension payment
    @Test
    public void update_CancelSavedOneoff() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("canceloneoff");
        paymentDto.setWithdrawalType("Pension Payment");
        paymentDto.setReceiptNumber("65454");
        paymentDto.setTransSeqNo("3");
        paymentDto.setTransactionId(null);

        when(paymentIntegrationService.endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(paymentIntegrationService).endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).endPayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.CANCEL_ONEOFF);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        assertNull(paymentCaptor.getValue().getPositionId());
        assertEquals(paymentCaptor.getValue().getTransactionSeqNo(), "3");
        assertEquals(paymentCaptor.getValue().getDocId(), "65454");
    }

    //Submit saved LumpSum pension payment
    @Test
    public void update_EditSavedLumpSum() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("submitoneoff");
        paymentDto.setWithdrawalType("Lump sum withdrawal");
        paymentDto.setReceiptNumber("65454");
        paymentDto.setTransSeqNo("2");
        paymentDto.setTransactionId(null); //existing saved oneoff pension storPosId
        when(paymentIntegrationService.savePayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).savePayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.SUBMIT_ONEOFF);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.LUMP_SUM_WITHDRAWAL);
        assertNull(paymentCaptor.getValue().getPositionId());
        assertEquals(paymentCaptor.getValue().getTransactionSeqNo(), "2");
        assertEquals(paymentCaptor.getValue().getDocId(), "65454");
    }


    //cancel saved lumpsum pension payment
    @Test
    public void update_CancelSavedLumpSum() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setPaymentAction("canceloneoff");
        paymentDto.setWithdrawalType("Lump sum withdrawal");
        paymentDto.setReceiptNumber("65454");
        paymentDto.setTransSeqNo("3");
        paymentDto.setTransactionId(null);

        when(paymentIntegrationService.endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails());

        PaymentDto responsePaymentDto = paymentDtoService.update(paymentDto, new ServiceErrorsImpl());

        verify(paymentIntegrationService).endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));

        assertNotNull(responsePaymentDto);
        ArgumentCaptor<PaymentDetails> paymentCaptor = ArgumentCaptor.forClass(PaymentDetails.class);
        verify(paymentIntegrationService).endPayment(paymentCaptor.capture(), any(ServiceErrors.class));
        assertEquals(paymentCaptor.getValue().getPaymentAction(), PaymentActionType.CANCEL_ONEOFF);
        assertEquals(paymentCaptor.getValue().getWithdrawalType(), WithdrawalType.LUMP_SUM_WITHDRAWAL);
        assertNull(paymentCaptor.getValue().getPositionId());
        assertEquals(paymentCaptor.getValue().getTransactionSeqNo(), "3");
        assertEquals(paymentCaptor.getValue().getDocId(), "65454");
    }

    @Test
    public void submitEndPayment() {
        submitEndPayment("cancelregular", null, false);
        submitEndPayment("cancelregular", false, false);
        submitEndPayment("cancelregular", true, true);
    }

    @Test
    public void submitWithInvalidTransaction() {
        final PaymentDetailsImpl paymentDetails = paymentDetails();
        final PaymentDto paymentDto = getPaymentDto();
        final String accountId = paymentDto.getKey().getAccountId();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final PaymentDto result;

        reset(paymentIntegrationService);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(getPayeeDetailsObj());
        when(acctPermissionService.canTransact(accountId, PAY_ANYONE_PERMISSION)).thenReturn(true);

        result = paymentDtoService.submit(paymentDto, serviceErrors);

        verify(paymentIntegrationService, never()).endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));

        assertThat("result not null", result, notNullValue());
        assertThat("result errors not null", result.getErrors(), notNullValue());
        assertThat("result contains errors", result.getErrors().size(), equalTo(1));
    }

    @Test
    public void submitValidLinkedAccountTransaction() {
        final PaymentDetailsImpl paymentDetails = paymentDetails();
        final PaymentDto paymentDto = getPaymentDto();
        final String accountId = paymentDto.getKey().getAccountId();
        final PayeeDto payeeDto = paymentDto.getToPayeeDto();
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final LinkedAccountImpl linkedAccount = new LinkedAccountImpl();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final PaymentDto result;

        linkedAccount.setBsb(payeeDto.getCode());
        linkedAccount.setAccountNumber(payeeDto.getAccountId());
        payeeDetails.setLinkedAccountList(Arrays.asList((LinkedAccount) linkedAccount));

        when(paymentIntegrationService.submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(acctPermissionService.canTransact(accountId, PAY_ANYONE_PERMISSION)).thenReturn(true);

        result = paymentDtoService.submit(paymentDto, serviceErrors);

        verify(paymentIntegrationService).submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(acctPermissionService, atLeastOnce()).canTransact(accountId, PAY_ANYONE_PERMISSION);

        assertThat("result not null", result, notNullValue());
        assertThat("result errors null", result.getErrors(), nullValue());
    }

    @Test
    public void submitValidPayAnyoneAccountTransaction() {
        final PaymentDetailsImpl paymentDetails = paymentDetails();
        final PaymentDto paymentDto = getPaymentDto();
        final String accountId = paymentDto.getKey().getAccountId();
        final PayeeDto payeeDto = paymentDto.getToPayeeDto();
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final PayAnyOneImpl payAnyOne = new PayAnyOneImpl();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final PaymentDto result;

        payAnyOne.setBsb(payeeDto.getCode());
        payAnyOne.setAccountNumber(payeeDto.getAccountId());
        payeeDetails.setPayanyonePayeeList(Arrays.asList((PayAnyOne) payAnyOne));
        payeeDto.setPayeeType(PayeeType.PAY_ANYONE.toString());

        when(paymentIntegrationService.submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(acctPermissionService.canTransact(accountId, PAY_ANYONE_PERMISSION)).thenReturn(true);

        result = paymentDtoService.submit(paymentDto, serviceErrors);

        verify(paymentIntegrationService).submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(acctPermissionService, atLeastOnce()).canTransact(accountId, PAY_ANYONE_PERMISSION);

        assertThat("result not null", result, notNullValue());
        assertThat("result errors null", result.getErrors(), nullValue());
    }

    @Test
    public void submitValidBpayAccountTransaction() {
        final PaymentDetailsImpl paymentDetails = paymentDetails();
        final PaymentDto paymentDto = getPaymentDto();
        final String accountId = paymentDto.getKey().getAccountId();
        final PayeeDto payeeDto = paymentDto.getToPayeeDto();
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final BillerImpl biller = new BillerImpl();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final PaymentDto result;

        biller.setBillerCode(payeeDto.getCode());
        biller.setCRN(payeeDto.getCrn());
        payeeDetails.setBpayBillerPayeeList(Arrays.asList((Biller) biller));
        payeeDto.setPayeeType(PayeeType.BPAY.toString());

        when(paymentIntegrationService.submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(acctPermissionService.canTransact(accountId, PAY_ANYONE_PERMISSION)).thenReturn(true);

        result = paymentDtoService.submit(paymentDto, serviceErrors);

        verify(paymentIntegrationService).submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(acctPermissionService, atLeastOnce()).canTransact(accountId, PAY_ANYONE_PERMISSION);

        assertThat("result not null", result, notNullValue());
        assertThat("result errors null", result.getErrors(), nullValue());
    }

    @Test
    public void submitValidWithTwoFactorAuthentication() {
        final PaymentDetailsImpl paymentDetails = paymentDetails();
        final PaymentDto paymentDto = getPaymentDto();
        final String accountId = paymentDto.getKey().getAccountId();
        final PayeeDto payeeDto = paymentDto.getToPayeeDto();
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final TwoFactorRuleModel twoFactorRuleModel = new TwoFactorRuleModel();
        final TwoFactorAccountVerificationKey authenticationKey;
        final AccountVerificationStatus accountVerificationStatus = new AccountVerificationStatus("rule1", true);
        final LinkedAccountImpl linkedAccount = new LinkedAccountImpl();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final PaymentDto result;

        linkedAccount.setBsb(payeeDto.getCode());
        linkedAccount.setAccountNumber(payeeDto.getAccountId());
        payeeDetails.setLinkedAccountList(Arrays.asList((LinkedAccount) linkedAccount));

        authenticationKey = new TwoFactorAccountVerificationKey(paymentDto.getToPayeeDto().getAccountId(),
                paymentDto.getToPayeeDto().getCode());
        twoFactorRuleModel.addVerificationStatus(authenticationKey, accountVerificationStatus);

        when(paymentIntegrationService.submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails);
        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModel);
        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class)))
                .thenReturn(payeeDetails);
        when(acctPermissionService.canTransact(accountId, PAY_ANYONE_PERMISSION)).thenReturn(true);

        result = paymentDtoService.submit(paymentDto, serviceErrors);

        verify(paymentIntegrationService).submitPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));
        verify(payeeDetailsIntegrationService).loadPayeeDetails(any(WrapAccountIdentifierImpl.class),
                any(ServiceErrorsImpl.class));
        verify(acctPermissionService, atLeastOnce()).canTransact(accountId, PAY_ANYONE_PERMISSION);

        assertThat("result not null", result, notNullValue());
        assertThat("result errors null", result.getErrors(), nullValue());
    }

    @Test
    public void testGetSafiAuthResult_whenNotSaveToAddressBook_thenSessionCheckedForAuthentication() {
        final TwoFactorRuleModel twoFactorRuleModel = new TwoFactorRuleModel();
        final TwoFactorAccountVerificationKey authenticationKey;
        final AccountVerificationStatus accountVerificationStatus = new AccountVerificationStatus(null, true);

        PayeeDto toDto = new PayeeDto();
        toDto.setCode("12006");
        toDto.setAccountId("120061455");
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType("PAY_ANYONE");
        toDto.setAccountKey(EncodedString.fromPlainText("120061455").toString());
        authenticationKey = new TwoFactorAccountVerificationKey(toDto.getAccountId(), toDto.getCode());
        twoFactorRuleModel.addVerificationStatus(authenticationKey, accountVerificationStatus);

        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModel);

        boolean result = paymentDtoService.getSafiAuthResult(toDto);
        assertTrue(result);
    }

    @Test
    public void testIsValidTransaction_whenNullPayee_thenNotValid() {
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        boolean result = paymentDtoService.isValidTransaction("accountId", new PayeeDetailsImpl(), null);
        assertFalse(result);
    }

    @Test
    public void testIsValidTransaction_whenNullPayeeDetails_thenNotValid() {
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        boolean result = paymentDtoService.isValidTransaction("accountId", null, new PayeeDto());
        assertFalse(result);
    }

    @Test
    public void testIsValidTransaction_whenNullPayeeAndNullPayeeDetails_thenNotValid() {
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        boolean result = paymentDtoService.isValidTransaction("accountId", null, null);
        assertFalse(result);
    }

    @Test
    public void testIsValidTransaction_whenPayAnyoneAndNoTransactAccess_thenNotValid() {
        final PaymentDto paymentDto = getPaymentDto();
        paymentDto.getToPayeeDto().setPayeeType(PayeeType.PAY_ANYONE.toString());
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final TwoFactorRuleModel twoFactorRuleModel = new TwoFactorRuleModel();
        final TwoFactorAccountVerificationKey authenticationKey;
        final AccountVerificationStatus accountVerificationStatus = new AccountVerificationStatus(null, true);

        authenticationKey = new TwoFactorAccountVerificationKey(paymentDto.getToPayeeDto().getAccountId(),
                paymentDto.getToPayeeDto().getCode());
        twoFactorRuleModel.addVerificationStatus(authenticationKey, accountVerificationStatus);

        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModel);
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        boolean result = paymentDtoService.isValidTransaction(paymentDto.getKey().getAccountId(), payeeDetails,
                paymentDto.getToPayeeDto());
        assertFalse(result);
    }

    @Test
    public void testIsValidTransaction_whenBPayAndNoTransactAccess_thenNotValid() {
        final PaymentDto paymentDto = getPaymentDto();
        paymentDto.getToPayeeDto().setPayeeType(PayeeType.BPAY.toString());
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final TwoFactorRuleModel twoFactorRuleModel = new TwoFactorRuleModel();
        final TwoFactorAccountVerificationKey authenticationKey;
        final AccountVerificationStatus accountVerificationStatus = new AccountVerificationStatus(null, true);

        authenticationKey = new TwoFactorAccountVerificationKey(paymentDto.getToPayeeDto().getAccountId(),
                paymentDto.getToPayeeDto().getCode());
        twoFactorRuleModel.addVerificationStatus(authenticationKey, accountVerificationStatus);

        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModel);
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        boolean result = paymentDtoService.isValidTransaction(paymentDto.getKey().getAccountId(), payeeDetails,
                paymentDto.getToPayeeDto());
        assertFalse(result);
    }

    @Test
    public void testIsValidTransaction_whenPayAnyoneAndSafiAuthenticated_thenValid() {
        final PaymentDto paymentDto = getPaymentDto();
        paymentDto.getToPayeeDto().setPayeeType(PayeeType.PAY_ANYONE.toString());
        paymentDto.getToPayeeDto().setSaveToList(null);
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final TwoFactorRuleModel twoFactorRuleModel = new TwoFactorRuleModel();
        final TwoFactorAccountVerificationKey authenticationKey;
        final AccountVerificationStatus accountVerificationStatus = new AccountVerificationStatus(null, true);

        authenticationKey = new TwoFactorAccountVerificationKey(paymentDto.getToPayeeDto().getAccountId(),
                paymentDto.getToPayeeDto().getCode());
        twoFactorRuleModel.addVerificationStatus(authenticationKey, accountVerificationStatus);

        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModel);
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        boolean result = paymentDtoService.isValidTransaction(paymentDto.getKey().getAccountId(), payeeDetails,
                paymentDto.getToPayeeDto());
        assertTrue(result);
    }

    @Test
    public void testIsValidTransaction_whenBPayAndSafiAuthenticated_thenValid() {
        final PaymentDto paymentDto = getPaymentDto();
        paymentDto.getToPayeeDto().setPayeeType(PayeeType.BPAY.toString());
        paymentDto.getToPayeeDto().setSaveToList(null);
        final PayeeDetailsImpl payeeDetails = getPayeeDetailsObj();
        final TwoFactorRuleModel twoFactorRuleModel = new TwoFactorRuleModel();
        final TwoFactorAccountVerificationKey authenticationKey;
        final AccountVerificationStatus accountVerificationStatus = new AccountVerificationStatus(null, true);

        authenticationKey = new TwoFactorAccountVerificationKey(paymentDto.getToPayeeDto().getAccountId(),
                paymentDto.getToPayeeDto().getCode());
        twoFactorRuleModel.addVerificationStatus(authenticationKey, accountVerificationStatus);

        when(httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModel);
        when(acctPermissionService.canTransact(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        boolean result = paymentDtoService.isValidTransaction(paymentDto.getKey().getAccountId(), payeeDetails,
                paymentDto.getToPayeeDto());
        assertTrue(result);
    }

    @Test
    public void testToPaymentDetails() {
        PaymentDto paymentDto = getPaymentDto();
        paymentDto.setClientIp("10.0.0.1");
        paymentDto.setBusinessChannel("1");
        MoneyAccountIdentifierImpl maccId = new MoneyAccountIdentifierImpl();
        maccId.setMoneyAccountId("76697");
        PaymentDetails paymentDetails = paymentDtoService.toPaymentDetails(maccId, paymentDto, null);
        assertThat(paymentDetails.getClientIp(), equalTo("10.0.0.1"));
        assertThat(paymentDetails.getBusinessChannel(), equalTo("1"));
    }

    private void submitEndPayment(final String paymentAction, final Boolean drawdownInProgress,
                                  final boolean warningExpected) {
        final EndPaymentDto endPaymentDto = getEndPaymentDto(paymentAction, "abc", drawdownInProgress);
        final PaymentDetailsImpl paymentDetails = paymentDetails();
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final PaymentDto result;

        reset(paymentIntegrationService);
        when(paymentIntegrationService.endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDetails);

        result = paymentDtoService.submit(endPaymentDto, serviceErrors);

        verify(paymentIntegrationService).endPayment(any(PaymentDetails.class), any(ServiceErrorsImpl.class));

        assertThat("result not null", result, notNullValue());
    }

    private EndPaymentDto getEndPaymentDto(String paymentAction, String transactionId, Boolean drawdownInProgress) {
        final EndPaymentDto retval = (EndPaymentDto) getPaymentDto(new EndPaymentDto());

        retval.setPaymentAction(paymentAction);
        retval.setTransactionId(EncodedString.fromPlainText(transactionId).toString());
        retval.setHasDrawdownInprogress(drawdownInProgress);

        return retval;
    }


    private PaymentDto getPaymentDto() {
        return getPaymentDto(new PaymentDto());
    }


    private PaymentDto getPaymentDto(PaymentDto paymentDto) {
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("36846").toString());
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();
        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        PayeeDto toDto = new PayeeDto();
        toDto.setCode("12006");
        toDto.setAccountId("120061455");
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType("LINKED");
        toDto.setCrn("123456789");
        toDto.setAccountKey(EncodedString.fromPlainText("120061455").toString());
        toDto.setSaveToList("save");

        paymentDto.setFromPayDto(fromDto);
        paymentDto.setToPayeeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.getWithdrawalType(); //
        paymentDto.setTransactionDate(new DateTime(2015, 1, 30, 0, 0));

        return paymentDto;
    }

    private PayeeDetailsImpl getPayeeDetailsObj() {
        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        MoneyAccountIdentifierImpl maccId = new MoneyAccountIdentifierImpl();
        maccId.setMoneyAccountId("76697");
        payeeDetailsImpl.setMaxDailyLimit("200000");
        payeeDetailsImpl.setMoneyAccountIdentifier(maccId);
        payeeDetailsImpl.setModifierSeqNumber(new BigDecimal("120"));
        return payeeDetailsImpl;
    }

    private PaymentDetailsImpl paymentDetails() {
        PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
        paymentDetails.setTransactionDate(new Date());
        paymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        paymentDetails.setAmount(new BigDecimal("4134"));
        paymentDetails.setBenefeciaryInfo("cash transfer");
        paymentDetails.setIndexationType(IndexationType.DOLLAR);
        paymentDetails.setIndexationAmount(new BigDecimal("10"));
        paymentDetails.setRecurringFrequency(RecurringFrequency.Monthly);
        MoneyAccountIdentifierImpl moneyAccount = new MoneyAccountIdentifierImpl();
        moneyAccount.setMoneyAccountId("red45");
        paymentDetails.setMoneyAccount(moneyAccount);
        //paymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        //paymentDetails.setPaymentAction(PaymentActionType.SAVE_NEW_REGULAR);
        paymentDetails.setAccountKey(AccountKey.valueOf("1234555"));
        return paymentDetails;
    }

    private ServiceError makeServiceError(final String type, final String message) {
        return new ServiceError() {
            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public void setMessage(String s) {

            }

            @Override
            public String getReason() {
                return null;
            }

            @Override
            public void setReason(String s) {

            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public void setType(String s) {

            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public void setId(String s) {

            }

            @Override
            public String getErrorCode() {
                return null;
            }

            @Override
            public void setErrorCode(String s) {

            }

            @Override
            public Throwable getException() {
                return null;
            }

            @Override
            public void setException(Throwable throwable) {

            }

            @Override
            public String getCorrelationId() {
                return type + '#' + message;
            }

            @Override
            public void setCorrelationId(String s) {

            }

            @Override
            public void setService(String s) {

            }

            @Override
            public String getService() {
                return null;
            }

            @Override
            public void setOriginatingSystem(String s) {

            }

            @Override
            public String getOriginatingSystem() {
                return null;
            }

            @Override
            public String getErrorMessageForScreenDisplay() {
                return null;
            }
        };
    }
}