package com.bt.nextgen.api.movemoney.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.BsbValidationDto;
import com.bt.nextgen.api.movemoney.v2.model.EndPaymentDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.service.PayeeDtoService;
import com.bt.nextgen.api.movemoney.v2.service.PaymentDtoService;
import com.bt.nextgen.api.movemoney.v2.service.SavedPaymentDtoServiceImpl;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAnalyzeRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.safi.model.SafiChallengeRequest;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.rsa.csd.ws.IdentificationData;
import org.apache.struts.mock.MockHttpSession;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Created by L072457 on 21/01/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentsApiControllerTest {

    // MockHttpServletRequest request;
    //
    // MockHttpServletResponse response;

    @Mock
    RedirectAttributes redirectAttributes;
    @Mock
    WebDataBinder dataBinder;
    @InjectMocks
    private PaymentsApiController paymentsApiController;
    @Mock
    PayeeDtoService payeeDtoService;
    @Mock
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationService;
    @Mock
    private CmsService cmsService;
    @Mock
    private PaymentDtoService paymentDtoService;

    @Mock
    private SavedPaymentDtoServiceImpl pensionPaymentDtoService;

    @Mock
    BsbCodeRepository bsbCodeRepository;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> listArgumentCaptor;

    private String accountKey = "FE5A9D833B86241F4767886F6D5ED0FB6E62F96DC31A6DDD";
    private PaymentDto paymentDto;
    private TransactionDto transactionDto;

    @Before
    public void setUp() {
        Mockito.doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "CMS-DATA";
            }

        }).when(cmsService).getContent(anyString());
    }

    @Test
    public final void testGetPayeesForAccount_null_model() throws Exception {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        paymentDto = new PaymentDto();
        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        paymentDtos.add(paymentDto);

        Mockito.when(paymentDtoService.search(Mockito.any(AccountKey.class), Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(paymentDtos);

        ApiResponse apiResponse = paymentsApiController.getPayeesForAccount(accountKey, null);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(paymentDtoService).search(Mockito.any(AccountKey.class), Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testGetPayeesForAccount_model() throws Exception {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        paymentDto = new PaymentDto();
        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        paymentDtos.add(paymentDto);

        Mockito.when(paymentDtoService.search(Mockito.any(AccountKey.class), Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(paymentDtos);

        ApiResponse apiResponse = paymentsApiController.getPayeesForAccount(accountKey, "moveMoneyModel");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(paymentDtoService).search(Mockito.any(AccountKey.class), Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testConfirmPayment() throws Exception {
        paymentDto = new PaymentDto();
        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");

        Mockito.when(paymentDtoService.validate(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDto);

        KeyedApiResponse<AccountKey> apiResponse = paymentsApiController.confirmPayment(accountKey, paymentDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(paymentDtoService).validate(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSubmitPayment() throws Exception {
        // MockHttpSession session = new MockHttpSession();
        // session.setAttribute("analyze-result-d8cb6cc3-5e8d-451b-842c-196e7afafa78", createSafiAnalyzeResult());
        //MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("User-Agent")).thenReturn("mobile user agent");
        Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        paymentDto = new PaymentDto();
        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptId(EncodedString.fromPlainText("123").toString());
        paymentDto.setReceiptNumber(EncodedString.fromPlainText("456").toString());

        Mockito.when(staticIntegrationService.loadCodeByAvaloqId(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl("1","Mobile","Mobile"));
        Mockito.when(paymentDtoService.submit(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDto);

        ApiResponse apiResponse = paymentsApiController.submitPayment(accountKey, paymentDto, request);

        assertThat(apiResponse, is(notNullValue()));
        Assert.assertEquals(((PaymentDto) apiResponse.getData()).getBusinessChannel(), "1");
        Assert.assertEquals(((PaymentDto) apiResponse.getData()).getClientIp(), "10.0.0.1");
        Mockito.verify(paymentDtoService).submit(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSubmitPaymentOnline() throws Exception {
        // MockHttpSession session = new MockHttpSession();
        // session.setAttribute("analyze-result-d8cb6cc3-5e8d-451b-842c-196e7afafa78", createSafiAnalyzeResult());
        //MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("User-Agent")).thenReturn("web online user agent");
        Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        paymentDto = new PaymentDto();
        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptId(EncodedString.fromPlainText("123").toString());
        paymentDto.setReceiptNumber(EncodedString.fromPlainText("456").toString());

        Mockito.when(staticIntegrationService.loadCodeByAvaloqId(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(new CodeImpl("2","Online","Online"));
        Mockito.when(paymentDtoService.submit(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDto);

        ApiResponse apiResponse = paymentsApiController.submitPayment(accountKey, paymentDto, request);

        assertThat(apiResponse, is(notNullValue()));
        Assert.assertEquals(((PaymentDto) apiResponse.getData()).getBusinessChannel(), "2");
        Assert.assertEquals(((PaymentDto) apiResponse.getData()).getClientIp(), "10.0.0.1");
        Mockito.verify(paymentDtoService).submit(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSavePayment() throws Exception {

        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber(EncodedString.fromPlainText("456").toString());
        paymentDto.setPaymentAction("savenewregular");

        Mockito.when(paymentDtoService.update(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDto);
        ApiResponse apiResponse = paymentsApiController.savePayment(accountKey, paymentDto);
        assertThat(apiResponse, is(notNullValue()));
        Mockito.verify(paymentDtoService).update(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testCancelSavePayment() throws Exception {
        paymentDto = new PaymentDto();
        paymentDto.setPaymentAction("canceloneoff");
        paymentDto.setReceiptNumber("650517");
        paymentDto.setTransSeqNo("3");

        Mockito.when(paymentDtoService.update(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDto);
        ApiResponse apiResponse = paymentsApiController.cancelPayment(accountKey, paymentDto);
        assertThat(apiResponse, is(notNullValue()));
        Mockito.verify(paymentDtoService).update(Mockito.any(EndPaymentDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testEndPayment() throws Exception {
        EndPaymentDto endPaymentDto = new EndPaymentDto();
        endPaymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        endPaymentDto.setHasDrawdownInprogress(Boolean.TRUE);

        Mockito.when(paymentDtoService.submit(Mockito.any(EndPaymentDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(endPaymentDto);

        ApiResponse apiResponse = paymentsApiController.endPayment(accountKey, endPaymentDto);

        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(paymentDtoService).submit(Mockito.any(EndPaymentDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public void shouldValidateBSB() throws Exception {
        String bsbCode = "MY_BSB_CODE";
        String bsbName = "MY_BSB_NAME";
        Bsb bsb = new Bsb();
        bsb.setBsbCode(bsbCode);
        bsb.setBankName(bsbName);
        Mockito.when(bsbCodeRepository.load(bsbCode)).thenReturn(bsb);
        BsbValidationDto bsbValidationDto = paymentsApiController.validateBSB(accountKey, bsbCode);
        assertThat(bsbValidationDto.isValid(), is(true));
        assertThat(bsbValidationDto.getBankName(), is(bsbName));
    }

    @Test
    public void shouldValidateBSB_Invalid() throws Exception {
        String bsbCode = "INVALID_BSB_CODE";
        Bsb bsb = new Bsb();
        bsb.setBsbCode(bsbCode);
        Mockito.when(bsbCodeRepository.load(bsbCode)).thenReturn(null);
        BsbValidationDto bsbValidationDto = paymentsApiController.validateBSB(accountKey, bsbCode);
        assertThat(bsbValidationDto.isValid(), is(false));
    }

    @Test
    public void shouldValidateBSB_bsbCodeNull() throws Exception {
        String bsbCode = "INVALID_BSB_CODE";
        Bsb bsb = new Bsb();
        bsb.setBsbCode(null);
        Mockito.when(bsbCodeRepository.load(bsbCode)).thenReturn(bsb);
        BsbValidationDto bsbValidationDto = paymentsApiController.validateBSB(accountKey, bsbCode);
        assertThat(bsbValidationDto.isValid(), is(false));
    }

    public SafiAnalyzeAndChallengeResponse createSafiAnalyzeResult() {
        SafiAnalyzeAndChallengeResponse result = new SafiAnalyzeAndChallengeResponse();

        IdentificationData identificationData = new IdentificationData();
        identificationData.setClientSessionId("7c8f422:1449b7fb819:-6f56");
        identificationData.setTransactionId("TRX_7c8f422:1449b7fb819:-6f55");

        result.setIdentificationData(identificationData);
        result.setActionCode(true);
        result.setDeviceId("159f9da6-42b5-4aaa-84c6-2335f8fab2b3");
        result.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        result.setDevicePrint(
                "version%3D1%26pm%5Ffpua%3Dmozilla%2F5%2E0%20%28windows%20nt%206%2E1%3B%20wow64%29%20applewebkit%2F537%2E36%20%28khtml%2C%20like%20gecko%29%20chrome%2F33%2E0%2E1750%2E146%20safari%2F537%2E36%7C5%2E0%20%28Windows%20NT%206%2E1%3B%20WOW64%29%20AppleWebKit%2F537%2E36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F33%2E0%2E1750%2E146%20Safari%2F537%2E36%7CWin32%26pm%5Ffpsc%3D24%7C1280%7C800%7C760%26pm%5Ffpsw%3D%7Cqt1%7Cqt2%7Cqt3%7Cqt4%7Cqt5%7Cdsw%26pm%5Ffptz%3D10%26pm%5Ffpln%3Dlang%3Den%2DUS%7Csyslang%3D%7Cuserlang%3D%26pm%5Ffpjv%3D1%26pm%5Ffpco%3D1");
        result.setDeviceToken("PMV60we%2BjHxYaQvS7uvXa5L0ipGvnMEoQXniuQAI4oK%2F295Ao3NTZg3HXhEybPW1ZDayhA");

        return result;
    }

    public HttpRequestParams getHttpRequestParams() {
        HttpRequestParams requestParams = new HttpRequestParams();
        requestParams.setHttpAccept(
                "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        requestParams.setHttpAcceptChars("ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        requestParams.setHttpAcceptEncoding("gzip,deflate");
        requestParams.setHttpAcceptLanguage("en-ua,en;q=0.5");
        requestParams.setHttpReferrer("http://www.cba.com.uk");
        requestParams.setHttpUserAgent(
                "Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.04506.30)");
        requestParams.setHttpOriginatingIpAddress("127.0.0.1");

        return requestParams;
    }

    @Test
    public void testAddPayee_Invalid_SmsCode() throws Exception {
        Mockito.doAnswer(new Answer<SafiAuthenticateResponse>() {
            @Override
            public SafiAuthenticateResponse answer(InvocationOnMock invocation) throws Throwable {
                SafiAuthenticateResponse safiAuthenticateResponse = new SafiAuthenticateResponse();
                safiAuthenticateResponse.setSuccessFlag(false);
                return safiAuthenticateResponse;
            }
        }).when(twoFactorAuthenticationService).authenticate(any(SafiAuthenticateRequest.class));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("analyze-result-d8cb6cc3-5e8d-451b-842c-196e7afafa78", createSafiAnalyzeResult());
        MockHttpServletRequest request = new MockHttpServletRequest();
        PayeeDto payeeDto =new PayeeDto();
        payeeDto.setPayeeType("LINKED");
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setToPayeeDto(payeeDto);

        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa78");
        Mockito.when(payeeDtoService.validate(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDto);

        SafiAuthenticateResponse safiAuthenticateResponse = Mockito.mock(SafiAuthenticateResponse.class);
        safiAuthenticateResponse.setSuccessFlag(false);
        Mockito.when(twoFactorAuthenticationService.authenticate(Mockito.any(SafiAuthenticateRequest.class)))
                .thenReturn(safiAuthenticateResponse);

        KeyedApiResponse keyedApiResponse = paymentsApiController.addPayee(accountKey, paymentDto, "223423", session, request);
        assertThat(keyedApiResponse.getStatus(), Is.is(1));
    }

    @Test
    public void testAddLinkedPayee_VerificationDone() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LINKED_2FA_VERIFIED", true);
        MockHttpServletRequest request = new MockHttpServletRequest();

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setErrors(Arrays.asList(new DomainApiErrorDto()));
        Mockito.when(payeeDtoService.validate(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDto);

        KeyedApiResponse keyedApiResponse = paymentsApiController.addLinkedPayee(accountKey, paymentDto, session, request);
        assertThat(keyedApiResponse.getStatus(), Is.is(1));
    }

    @Test
    public void testAddLinkedPayee_VerificationNotDone() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LINKED_2FA_VERIFIED", false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        PaymentDto paymentDto = new PaymentDto();
        /*paymentDto.setErrors(Arrays.asList(new DomainApiErrorDto()));
        Mockito.when(payeeDtoService.validate(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDto);*/

        KeyedApiResponse keyedApiResponse = paymentsApiController.addLinkedPayee(accountKey, paymentDto, session, request);
        assertThat(keyedApiResponse.getStatus(), Is.is(1));
    }


    @Test
    public void testAddPayee_Invalid_TransactionId() throws Exception {
        Mockito.doAnswer(new Answer<SafiAuthenticateResponse>() {
            @Override
            public SafiAuthenticateResponse answer(InvocationOnMock invocation) throws Throwable {
                SafiAuthenticateResponse safiAuthenticateResponse = new SafiAuthenticateResponse();
                safiAuthenticateResponse.setSuccessFlag(false);
                return safiAuthenticateResponse;
            }
        }).when(twoFactorAuthenticationService).authenticate(any(SafiAuthenticateRequest.class));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("analyze-result-d8cb6cc3-5e8d-451b-842c-196e7afafa88", createSafiAnalyzeResult());
        MockHttpServletRequest request = new MockHttpServletRequest();

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setTransactionId("d8cb6cc3-5e8d-451b-842c-196e7afafa99");
        Mockito.when(payeeDtoService.validate(Mockito.any(PaymentDto.class), Mockito.any(ServiceErrorsImpl.class)))
                .thenReturn(paymentDto);

        SafiAuthenticateResponse safiAuthenticateResponse = Mockito.mock(SafiAuthenticateResponse.class);
        safiAuthenticateResponse.setSuccessFlag(false);
        Mockito.when(twoFactorAuthenticationService.authenticate(Mockito.any(SafiAuthenticateRequest.class)))
                .thenReturn(safiAuthenticateResponse);

        KeyedApiResponse keyedApiResponse = paymentsApiController.addPayee(accountKey, paymentDto, "223423", session, request);
        assertThat(keyedApiResponse.getStatus(), Is.is(1));
    }

    private void mockSafiAnalyzeAndChallengeResponseForChallengeReq() {
        Mockito.doAnswer(new Answer<SafiAnalyzeAndChallengeResponse>() {
            @Override
            public SafiAnalyzeAndChallengeResponse answer(InvocationOnMock invocation) throws Throwable {
                SafiAnalyzeAndChallengeResponse safiAnalyzeAndChallengeResponse = new SafiAnalyzeAndChallengeResponse();
                safiAnalyzeAndChallengeResponse.setActionCode(true);
                return safiAnalyzeAndChallengeResponse;
            }

        }).when(twoFactorAuthenticationService).challenge(any(SafiChallengeRequest.class), any(ServiceErrorsImpl.class));
    }

    private void mockSafiAnalyzeAndChallengeResponse() {
        Mockito.doAnswer(new Answer<SafiAnalyzeAndChallengeResponse>() {
            @Override
            public SafiAnalyzeAndChallengeResponse answer(InvocationOnMock invocation) throws Throwable {
                SafiAnalyzeAndChallengeResponse safiAnalyzeAndChallengeResponse = new SafiAnalyzeAndChallengeResponse();
                safiAnalyzeAndChallengeResponse.setActionCode(true);
                return safiAnalyzeAndChallengeResponse;
            }

        }).when(twoFactorAuthenticationService).analyze(any(SafiAnalyzeRequest.class), any(ServiceErrorsImpl.class));
    }

    private void mockSafiAuthenticateResponse() throws Exception {
        Mockito.doAnswer(new Answer<SafiAuthenticateResponse>() {
            @Override
            public SafiAuthenticateResponse answer(InvocationOnMock invocation) throws Throwable {
                SafiAuthenticateResponse safiAuthenticateResponse = new SafiAuthenticateResponse();
                safiAuthenticateResponse.setUsername("username");
                safiAuthenticateResponse.setSuccessFlag(true);
                return safiAuthenticateResponse;
            }

        }).when(twoFactorAuthenticationService).authenticate(any(SafiAuthenticateRequest.class));
    }


    @Test
    public final void testRetrievePayments() throws Exception {
        List<TransactionDto> transactionList = new ArrayList<>();
        transactionList.add(transactionDto);
        Mockito.when(pensionPaymentDtoService.search(anyList(), any(ServiceErrorsImpl.class)))
                .thenReturn(new ArrayList<TransactionDto>());

        ApiResponse apiResponse = paymentsApiController.retrieveSavedPayments(accountKey);
        assertThat(apiResponse, is(notNullValue()));

        verify(pensionPaymentDtoService).search(anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class));
        verify(pensionPaymentDtoService).search(listArgumentCaptor.capture(), any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList = listArgumentCaptor.getValue();
        ApiSearchCriteria apiSearchCriteria1 = apiSearchCriteriaList.get(0);
        assertThat(apiSearchCriteria1.getProperty(), is("account-id"));
        assertThat(apiSearchCriteria1.getValue(), is(new EncodedString("FE5A9D833B86241F4767886F6D5ED0FB6E62F96DC31A6DDD").plainText().toString()));
        assertThat(apiSearchCriteriaList.size(), is(1));

    }

    @Test(expected = IllegalArgumentException.class)
    public void retrievePaymentsWithEmptyAccountId() {
        paymentsApiController.retrieveSavedPayments("");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCancelPaymentInvalidPaymentAction() {
        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber("123");
        paymentDto.setPaymentAction("test");
        paymentsApiController.cancelPayment("1234", paymentDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelPaymentEmptyPaymentAction() {
        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber("23872");
        paymentDto.setPaymentAction("");
        paymentsApiController.cancelPayment("1234", paymentDto);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCancelPaymentInvalidAccountID() {
        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber("23872");
        paymentDto.setPaymentAction("savenewregular");
        paymentsApiController.cancelPayment("", paymentDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSavePaymentInvalidPaymentAction() {
        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber("123");
        paymentDto.setPaymentAction("test");
        paymentsApiController.savePayment("1234", paymentDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSavePaymentEmptyPaymentAction() {
        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber("23872");
        paymentDto.setPaymentAction("");
        paymentsApiController.savePayment("1234", paymentDto);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSavePaymentInvalidAccountID() {
        paymentDto = new PaymentDto();
        paymentDto.setAmount(new BigDecimal(1.00));
        paymentDto.setReceiptNumber("23872");
        paymentDto.setPaymentAction("savenewregular");
        paymentsApiController.savePayment("", paymentDto);
    }


}
