package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.account.BillerImpl;
import com.bt.nextgen.service.avaloq.account.UpdateAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayAnyOneImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.account.PayeeRequest;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PayeeDtoServiceTest {
    @InjectMocks
    private PayeeDtoServiceImpl payeeDtoService;

    @Mock
    BpayBillerCodeRepository bpayBillerCodeRepository;

    @Mock
    PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    AccountIntegrationService avaloqAccountIntegrationServiceImpl;

    @Mock
    private BsbCodeRepository bsbCodeRepository;

    @Mock
    private MovemoneyDtoErrorMapper movemoneyErrorMapper;

    @Mock
    private PrmService prmService;

    @Mock
    private HttpSession httpSession;

    PaymentDto paymentDtoKeyedObj;
    List<Biller> billerList = null;
    PayeeDetails payeeDetails;
    UpdateAccountDetailResponse updateAccountDetailResponse = null;
    PayeeDto payeeDto;
    BpayBiller bpayBiller = null;

    @Before
    public void setup() throws Exception {
        payeeDetails = Mockito.mock(PayeeDetails.class);
        Mockito.when(payeeDetails.getModifierSeqNumber()).thenReturn(new BigDecimal(123));
        payeeDto = new PayeeDto();
        updateAccountDetailResponse = new UpdateAccountDetailResponseImpl();
        updateAccountDetailResponse.setUpdatedFlag(true);

        paymentDtoKeyedObj = new PaymentDto(new AccountKey("123456"));

        payeeDto.setAccountName("Denis Beecham");
        payeeDto.setAccountId("123456");
        payeeDto.setNickname("Nickname1");
        payeeDto.setCode("234567");
        payeeDto.setCrn("345678");
        payeeDto.setSaveToList("Yes");
        payeeDto.setLimit("1000");

        paymentDtoKeyedObj.setPrimary(true);
        paymentDtoKeyedObj.setToPayeeDto(payeeDto);
        paymentDtoKeyedObj.setKey(new AccountKey("123456"));

        bpayBiller = new BpayBiller();
        bpayBiller.setBillerName("Denis Beecham");

        BillerImpl biller = new BillerImpl();
        biller.setBillerCode("012006");
        biller.setCRN("123456789");
        biller.setName("Denis Beecham");
        biller.setNickName("Nickname1");
        billerList = new ArrayList<Biller>();
        billerList.add(biller);

        List<PayAnyOne> payanyoneList = new ArrayList<PayAnyOne>();
        PayAnyOneImpl payanyone = new PayAnyOneImpl();
        payanyone.setName("Denis Beecham");
        payanyone.setBsb("012006");
        payanyone.setAccountNumber("120061455");
        payanyone.setNickName("Nickname1");
        payanyoneList.add(payanyone);

        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        payeeDetailsImpl.setBpayBillerPayeeList(billerList);
        payeeDetailsImpl.setPayanyonePayeeList(payanyoneList);
        Mockito.when(payeeDetailsIntegrationService.loadPayeeDetails(Mockito.any(WrapAccountIdentifier.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(payeeDetailsImpl);

        final List<DomainApiErrorDto> domainErrors = Arrays.asList(new DomainApiErrorDto());
        when(movemoneyErrorMapper.map(Mockito.anyList())).thenReturn(domainErrors);

        Bsb bsb = new Bsb();
        bsb.setBsbCode("012006");
        Mockito.when(bsbCodeRepository.load(Mockito.anyString())).thenReturn(bsb);
    }

    @Test
    public void shouldReturnLinkedAccRequestTest() {
        LinkedAccRequest request = payeeDtoService.makeLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(request);
        assertEquals(request.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(request.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(request.getLinkedAccount());
        assertEquals(request.getLinkedAccount().getAccountNumber(), paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        assertEquals(request.getLinkedAccount().getBsb(), paymentDtoKeyedObj.getToPayeeDto().getCode());
        assertEquals(request.getLinkedAccount().getName(), paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        assertEquals(request.getLinkedAccount().getNickName(), paymentDtoKeyedObj.getToPayeeDto().getNickname());
        assertEquals(request.getLinkedAccount().getLimit(), paymentDtoKeyedObj.getLimit());
        assertFalse(request.getLinkedAccount().isPrimary());
        assertEquals(request.getLinkedAccount().getCurrency(), CurrencyType.AustralianDollar);
    }

    @Test
    public void shouldReturnPayeeRequestTest() {
        PayeeRequest payeeRequest = payeeDtoService.makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(payeeRequest);
        assertEquals(payeeRequest.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(payeeRequest.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(payeeRequest.getBankAccount());
        assertEquals(payeeRequest.getBankAccount().getAccountNumber(), paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        assertEquals(payeeRequest.getBankAccount().getName(), paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        assertEquals(payeeRequest.getBankAccount().getNickName(), paymentDtoKeyedObj.getToPayeeDto().getNickname());
        assertEquals(payeeRequest.getBankAccount().getBsb(), paymentDtoKeyedObj.getToPayeeDto().getCode());
    }

    @Test
    public void shouldReturnBillerRequestTest() {
        BpayBiller bpayBiller = new BpayBiller();
        bpayBiller.setBillerName("Test123");
        Mockito.when(bpayBillerCodeRepository.load(paymentDtoKeyedObj.getToPayeeDto().getCode())).thenReturn(bpayBiller);
        BillerRequest biller = payeeDtoService.makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(biller);
        assertEquals(biller.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(biller.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(biller.getBillerDetail());
        assertEquals(biller.getBillerDetail().getName(), paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        assertEquals(biller.getBillerDetail().getNickName(), paymentDtoKeyedObj.getToPayeeDto().getNickname());
        assertEquals(biller.getBillerDetail().getBillerCode(), paymentDtoKeyedObj.getToPayeeDto().getCode());
        assertEquals(biller.getBillerDetail().getCRN(), paymentDtoKeyedObj.getToPayeeDto().getCrn());
    }

    @Test
    public void shouldReturnDeleteLinkedAccRequestTest() {
        DeleteLinkedAccRequest request = payeeDtoService.makeDeleteLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(request);
        assertEquals(request.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(request.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(request.getBankAccount());
        assertEquals(request.getBankAccount().getAccountNumber(), paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        assertEquals(request.getBankAccount().getBsb(), paymentDtoKeyedObj.getToPayeeDto().getCode());
        assertEquals(request.getBankAccount().getName(), paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        assertEquals(request.getBankAccount().getNickName(), paymentDtoKeyedObj.getToPayeeDto().getNickname());
    }

    @Test
    public void testValidate_whenBPayAndDuplicateCRN_thenErrorAdded() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);

        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        PaymentDto returnedPaymentDto = payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        assertEquals(1, returnedPaymentDto.getErrors().size());
    }

    @Test
    public void testValidate_whenBPayAndDuplicateNickname_thenErrorAdded() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);
        paymentDto.getToPayeeDto().setCrn("notaduplicate");

        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        PaymentDto returnedPaymentDto = payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        assertEquals(1, returnedPaymentDto.getErrors().size());
    }

    @Test
    public void testValidate_whenPayAnyoneAndIsDuplicateAccount_thenErrorAdded() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        PaymentDto returnedPaymentDto = payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        assertEquals(1, returnedPaymentDto.getErrors().size());
    }

    @Test
    public void testValidate_whenLinkedAccountAndIsDuplicateAccount_thenErrorAdded() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);
        PaymentDto returnedPaymentDto = payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        assertEquals(1, returnedPaymentDto.getErrors().size());
    }

    @Test
    public void testValidate_whenPayAnyoneAndIsNotDuplicateAccount_thenValidBsb() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        paymentDto.getToPayeeDto().setCode("notaduplicate");
        PaymentDto returnedPaymentDto = payeeDtoService.validate(paymentDto, new ServiceErrorsImpl());
        assertTrue(returnedPaymentDto.isValidBsb());
    }

    @Test
    public void testSubmit_whenNoOperation_thenNoMethodsCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        payeeDtoService.submit(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl, Mockito.times(0)).addNewRegPayeeDetail(any(PayeeRequest.class),
                any(ServiceErrorsImpl.class));
        verify(avaloqAccountIntegrationServiceImpl, Mockito.times(0)).updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));
        verify(avaloqAccountIntegrationServiceImpl, Mockito.times(0)).deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public void testSubmit_whenUnknownOperation_thenNoMethodsCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        paymentDto.setOpType("HACK");
        payeeDtoService.submit(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl, Mockito.times(0)).addNewRegPayeeDetail(any(PayeeRequest.class),
                any(ServiceErrorsImpl.class));
        verify(avaloqAccountIntegrationServiceImpl, Mockito.times(0)).updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));
        verify(avaloqAccountIntegrationServiceImpl, Mockito.times(0)).deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public void testSubmit_whenAdd_thenAddNewRegPayeeDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        paymentDto.setOpType(Attribute.ADD);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.addNewRegPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        payeeDtoService.submit(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).addNewRegPayeeDetail(any(PayeeRequest.class), any(ServiceErrorsImpl.class));
    }

    @Test
    public void testSubmit_whenUpdate_thenUpdateExistingPayeeDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        paymentDto.setOpType(Attribute.UPDATE);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        payeeDtoService.submit(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public void testSubmit_whenDelete_thenDeleteExistingPayeeDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);
        paymentDto.setOpType(Attribute.DELETE);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        payeeDtoService.submit(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public void testAddPayee_whenBPay_thenAddNewBillerDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));
        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        Mockito.when(avaloqAccountIntegrationServiceImpl.addNewBillerDetail(Mockito.any(BillerRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.addPayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).addNewBillerDetail(any(BillerRequest.class), any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.BPAY.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testAddPayee_whenPayAnyone_thenAddNewRegPayeeDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.addNewRegPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.addPayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).addNewRegPayeeDetail(any(PayeeRequest.class), any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.PAY_ANYONE.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testAddPayee_whenLinkedAccount_thenAddLinkedAccountCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.addLinkedAccount(Mockito.any(LinkedAccRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.addPayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).addLinkedAccount(any(LinkedAccRequest.class), any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.LINKED.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testAddPayee_whenNotSaveToAddressBookAndSessionAttribute_thenSessionAttributeRemoved() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);
        paymentDto.getToPayeeDto().setSaveToList(null);

        Mockito.when(httpSession.getAttribute(Mockito.anyString())).thenReturn(new TwoFactorRuleModel());
        Mockito.doNothing().when(httpSession).removeAttribute(Mockito.anyString());

        payeeDtoService.addPayee(paymentDto, new ServiceErrorsImpl());
        verify(httpSession).removeAttribute(Mockito.anyString());
    }

    @Test
    public void testAddPayee_whenNotSaveToAddressBookAndNoSessionAttribute_thenSessionAttributeAdded() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);
        paymentDto.getToPayeeDto().setSaveToList(null);

        Mockito.when(httpSession.getAttribute(Mockito.anyString())).thenReturn(null);

        TwoFactorRuleModel ruleModel = new TwoFactorRuleModel();
        TwoFactorAccountVerificationKey accountVerificationKey = new TwoFactorAccountVerificationKey(
                paymentDto.getToPayeeDto().getAccountId(), paymentDto.getToPayeeDto().getCode());
        ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(null, true));

        PaymentDto returnedPaymentDto = payeeDtoService.addPayee(paymentDto, new ServiceErrorsImpl());
        verify(httpSession).setAttribute(Mockito.anyString(), Mockito.anyObject());
        assertEquals("120061455", EncodedString.toPlainText(returnedPaymentDto.getToPayeeDto().getAccountKey()));
    }

    @Test
    public void testUpdatePayee_whenBPay_thenUpdateExistingBillerDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));
        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        Mockito.when(avaloqAccountIntegrationServiceImpl.updateExistingBillerDetail(Mockito.any(BillerRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.updatePayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).updateExistingBillerDetail(Mockito.any(BillerRequest.class),
                Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.BPAY.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testUpdatePayee_whenPayAnyone_thenUpdateExistingPayeeDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.updatePayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.PAY_ANYONE.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testUpdatePayee_whenLinkedAccount_thenUpdateLinkedAccountCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.updateLinkedAccount(Mockito.any(LinkedAccRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.updatePayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).updateLinkedAccount(Mockito.any(LinkedAccRequest.class),
                Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.LINKED.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testMakeBillerRequest_whenDeleteOp_thenBpayBillerCodeRepoNotCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);
        paymentDto.setOpType("DELETE");

        BillerRequest billerReq = payeeDtoService.makeBillerRequest(paymentDto, payeeDetails);
        verify(bpayBillerCodeRepository, Mockito.times(0)).load(Mockito.anyString());

        assertNotNull(billerReq);
        assertEquals("012006", billerReq.getBillerDetail().getBillerCode());
        assertEquals("123456789", billerReq.getBillerDetail().getCRN());
    }

    @Test
    public void testMakeBillerRequest_whenNotDeleteOp_thenBpayBillerCodeRepoCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);

        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        BillerRequest billerReq = payeeDtoService.makeBillerRequest(paymentDto, payeeDetails);
        verify(bpayBillerCodeRepository, atLeastOnce()).load(Mockito.anyString());

        assertNotNull(billerReq);
        assertEquals("012006", billerReq.getBillerDetail().getBillerCode());
        assertEquals("123456789", billerReq.getBillerDetail().getCRN());
        assertEquals(bpayBiller.getBillerName(), billerReq.getBillerDetail().getName());
    }

    @Test
    public void testDeletePayee_whenBPay_thenDeleteExistingBillerDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));
        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        Mockito.when(avaloqAccountIntegrationServiceImpl.deleteExistingBillerDetail(Mockito.any(BillerRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.deletePayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).deleteExistingBillerDetail(Mockito.any(BillerRequest.class),
                Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.BPAY.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testDeletePayee_whenPayAnyone_thenDeleteExistingPayeeDetailCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.PAY_ANYONE);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.deletePayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.PAY_ANYONE.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testDeletePayee_whenLinkedAccount_thenDeleteLinkedAccountCalled() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);

        Mockito.doNothing().when(prmService).triggerPayeeEvents(Mockito.any(PaymentDto.class));

        Mockito.when(avaloqAccountIntegrationServiceImpl.deleteLinkedAccount(Mockito.any(DeleteLinkedAccRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        PaymentDto returnedPaymentDto = payeeDtoService.deletePayee(paymentDto, new ServiceErrorsImpl());
        verify(avaloqAccountIntegrationServiceImpl).deleteLinkedAccount(Mockito.any(DeleteLinkedAccRequest.class),
                Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(returnedPaymentDto);
        assertEquals("Denis Beecham", returnedPaymentDto.getToPayeeDto().getAccountName());
        assertEquals("120061455", returnedPaymentDto.getToPayeeDto().getAccountId());
        assertEquals("Nickname1", returnedPaymentDto.getToPayeeDto().getNickname());
        assertEquals("012006", returnedPaymentDto.getToPayeeDto().getCode());
        assertEquals("123456789", returnedPaymentDto.getToPayeeDto().getCrn());
        assertEquals(false, returnedPaymentDto.getToPayeeDto().isPrimary());
        assertEquals(PayeeType.LINKED.toString(), returnedPaymentDto.getToPayeeDto().getPayeeType());
    }

    @Test
    public void testIsDuplicateAccount_whenNoPayees_thenReturnFalse() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.LINKED);
        boolean result = payeeDtoService.isDuplicateAccount(null, paymentDto);
        assertFalse(result);
    }

    @Test
    public void testIsDuplicateAccount_whenExistingPayee_thenReturnTrue() {
        boolean result = payeeDtoService.isDuplicateAccount(getPayeeDetails().getPayanyonePayeeList(),
                getPaymentDto(PayeeType.LINKED));
        assertTrue(result);
    }

    @Test
    public void testIsDuplicateCrn_whenNoBillers_thenReturnFalse() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);
        boolean result = payeeDtoService.isDuplicateCRN(null, paymentDto);
        assertFalse(result);
    }

    @Test
    public void testIsDuplicateCrn_whenExistingBiller_thenReturnTrue() {
        boolean result = payeeDtoService.isDuplicateCRN(getPayeeDetails().getBpayBillerPayeeList(),
                getPaymentDto(PayeeType.BPAY));
        assertTrue(result);
    }

    @Test
    public void testIsDuplicateNickname_whenNoBillers_thenReturnFalse() {
        PaymentDto paymentDto = getPaymentDto(PayeeType.BPAY);
        boolean result = payeeDtoService.isDuplicateNickname(null, paymentDto);
        assertFalse(result);
    }

    @Test
    public void testIsDuplicateNickname_whenExistingBiller_thenReturnTrue() {
        boolean result = payeeDtoService.isDuplicateNickname(getPayeeDetails().getBpayBillerPayeeList(),
                getPaymentDto(PayeeType.BPAY));
        assertTrue(result);
    }

    @Test
    public void testValidateBSB_whenNullObject_thenReturnFalse() {
        Mockito.when(bsbCodeRepository.load(Mockito.anyString())).thenReturn(null);
        boolean result = payeeDtoService.validateBSB("bsb");
        assertFalse(result);
    }

    @Test
    public void testValidateBSB_whenNullBsb_thenReturnFalse() {
        Bsb bsb = new Bsb();
        Mockito.when(bsbCodeRepository.load(Mockito.anyString())).thenReturn(bsb);
        boolean result = payeeDtoService.validateBSB("bsb");
        assertFalse(result);
    }

    @Test
    public void testValidateBSB_whenNotNullBsb_thenReturnTrue() {
        boolean result = payeeDtoService.validateBSB("bsb");
        assertTrue(result);
    }

    private PayeeDetailsImpl getPayeeDetails() {
        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        MoneyAccountIdentifierImpl maccId = new MoneyAccountIdentifierImpl();
        maccId.setMoneyAccountId("76697");
        payeeDetailsImpl.setMaxDailyLimit("200000");
        payeeDetailsImpl.setMoneyAccountIdentifier(maccId);
        payeeDetailsImpl.setModifierSeqNumber(new BigDecimal("120"));

        List<PayAnyOne> payanyoneList = new ArrayList<PayAnyOne>();
        PayAnyOneImpl payanyone = new PayAnyOneImpl();
        payanyone.setName("Denis Beecham");
        payanyone.setBsb("012006");
        payanyone.setAccountNumber("120061455");
        payanyone.setNickName("Nickname1");
        payanyoneList.add(payanyone);
        payeeDetailsImpl.setPayanyonePayeeList(payanyoneList);

        List<Biller> billerList = new ArrayList<Biller>();
        BillerImpl biller = new BillerImpl();
        biller.setBillerCode("012006");
        biller.setCRN("123456789");
        biller.setName("Denis Beecham");
        biller.setNickName("Nickname1");
        billerList.add(biller);
        payeeDetailsImpl.setBpayBillerPayeeList(billerList);

        return payeeDetailsImpl;
    }

    private PaymentDto getPaymentDto(PayeeType payeeType) {
        PaymentDto paymentDto = new PaymentDto();
        com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString());
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();
        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        PayeeDto toDto = new PayeeDto();
        toDto.setCode("012006");
        toDto.setAccountId("120061455");
        toDto.setAccountName("Denis Beecham");
        toDto.setNickname("Nickname1");
        toDto.setCrn("123456789");
        toDto.setAccountKey(EncodedString.fromPlainText("120061455").toString());
        toDto.setSaveToList("save");
        toDto.setLimit("1000");
        toDto.setPayeeType(payeeType.toString());

        paymentDto.setFromPayDto(fromDto);
        paymentDto.setToPayeeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.getWithdrawalType(); //
        paymentDto.setTransactionDate(new DateTime(2015, 1, 30, 0, 0));

        return paymentDto;
    }
}
