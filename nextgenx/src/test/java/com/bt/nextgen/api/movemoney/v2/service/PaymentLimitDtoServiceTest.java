package com.bt.nextgen.api.movemoney.v2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeLimitImpl;
import com.bt.nextgen.service.prm.service.PrmService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.DailyLimitModel;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.UpdateAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.account.UpdatePaymentLimitRequest;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

@RunWith(MockitoJUnitRunner.class)
public class PaymentLimitDtoServiceTest {
    @InjectMocks
    private PaymentLimitDtoServiceImpl paymentLimitDtoServiceImpl;

    @Mock
    PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    DailyLimitDto linkedDailyLimitDto;

    DailyLimitDto bpayDailyLimitDto;

    DailyLimitDto payanyOneDailyLimitDto;

    DailyLimitModel linkedDailyLimitModel;

    DailyLimitModel bpayDailyLimitModel;

    DailyLimitModel payanyOneDailyLimitModel;

    EncodedString accountId = EncodedString.fromPlainText("36846");

    AccountKey accountKey = new AccountKey("36846");

    UpdateAccountDetailResponse updateAccountDetailResponse = null;

    DailyLimitDto dailyLimitDtoKeyedObj;

    @Mock
    AccountIntegrationService avaloqAccountIntegrationServiceImpl;
    @Mock
    PrmService prmService;

    @Before
    public void setUp() throws Exception {

        updateAccountDetailResponse = new UpdateAccountDetailResponseImpl();
        updateAccountDetailResponse.setUpdatedFlag(true);

        dailyLimitDtoKeyedObj = new DailyLimitDto();

        /*
         * linkedDailyLimitDto = new DailyLimitDto(); linkedDailyLimitDto.setAmount("100");
         * linkedDailyLimitDto.setPayeeType("LINKED");
         * 
         * bpayDailyLimitDto = new DailyLimitDto(); bpayDailyLimitDto.setAmount("100"); bpayDailyLimitDto.setPayeeType("BPAY");
         * 
         * payanyOneDailyLimitDto = new DailyLimitDto(); payanyOneDailyLimitDto.setAmount("100");
         * payanyOneDailyLimitDto.setPayeeType("PAY_ANYONE");
         * 
         * linkedDailyLimitModel = new DailyLimitModel();
         * 
         * linkedDailyLimitModel.setLinkedLimit("$3,000"); List <String> linkedLimits = new ArrayList <String>();
         * linkedLimits.add("1,0000.00"); linkedLimits.add("$25,000.00"); linkedLimits.add("$5,0000.00");
         * linkedLimits.add("$100,000"); linkedLimits.add("$200,000"); linkedDailyLimitModel.setLinkedLimits(linkedLimits);
         * 
         * bpayDailyLimitModel = new DailyLimitModel();
         * 
         * bpayDailyLimitModel.setBpayLimit("$3,000"); List <String> bpayLimits = new ArrayList <String>();
         * bpayLimits.add("1,0000.00"); bpayLimits.add("$25,000.00"); bpayLimits.add("$5,0000.00"); bpayLimits.add("$100,000");
         * bpayLimits.add("$200,000"); bpayDailyLimitModel.setBpayLimits(bpayLimits);
         * 
         * payanyOneDailyLimitModel = new DailyLimitModel();
         * 
         * payanyOneDailyLimitModel.setPayAnyoneLimit("3,000");
         * 
         * List <String> payAnyOneLimits = new ArrayList <String>(); payAnyOneLimits.add("1,0000.00");
         * payAnyOneLimits.add("$25,000.00"); payAnyOneLimits.add("$5,0000.00"); payAnyOneLimits.add("$100,000");
         * payAnyOneLimits.add("$200,000"); payanyOneDailyLimitModel.setPayAnyoneLimits(payAnyOneLimits);
         */

    }

    @Test
    public void testCheckLimitsLinked() throws Exception {

        /*
         * DailyLimitDto dailyLimitDto = new DailyLimitDto();
         * Mockito.when(paymentLimitService.checkPaymentLimit(Mockito.anyString(), Mockito.anyString(),
         * Mockito.any(PayeeType.class))) .thenReturn(linkedDailyLimitModel);
         * 
         * DailyLimitDto dailyLimitDtoResult = paymentLimitDtoServiceImpl.checkLimits("36846", linkedDailyLimitDto, new
         * ServiceErrorsImpl());
         * 
         * assertNotNull(dailyLimitDtoResult); assertNotNull(dailyLimitDtoResult.getLinkedLimit());
         * assertNotNull(dailyLimitDtoResult.getLinkedLimits()); assertTrue(dailyLimitDtoResult.getLinkedLimits().size() > 0);
         */

    }

    @Test
    public void testupdateLimitsLinked() throws Exception {
        /*
         * linkedDailyLimitDto.setLimit(new BigDecimal(3001)); linkedDailyLimitDto.setKey(accountKey);
         * Mockito.when(paymentLimitService.updatePaymentLimit(linkedDailyLimitDto.getKey().getAccountId(),
         * linkedDailyLimitDto.getLimit().toString(), PayeeType.LINKED)).thenReturn(true);
         * 
         * DailyLimitDto dailyLimitDto = paymentLimitDtoServiceImpl.updateDailyLimit(linkedDailyLimitDto, new
         * ServiceErrorsImpl());
         * 
         * assertNotNull(dailyLimitDto); assertNotNull(dailyLimitDto.getLimit());
         */
    }

    @Test
    public void testCheckLimitsBPAY() throws Exception {

        /*
         * DailyLimitDto dailyLimitDto = new DailyLimitDto();
         * Mockito.when(paymentLimitService.checkPaymentLimit(Mockito.anyString(), Mockito.anyString(),
         * Mockito.any(PayeeType.class))) .thenReturn(bpayDailyLimitModel);
         * 
         * DailyLimitDto dailyLimitDtoResult = paymentLimitDtoServiceImpl.checkLimits("36846", bpayDailyLimitDto, new
         * ServiceErrorsImpl());
         * 
         * assertNotNull(dailyLimitDtoResult); assertNotNull(dailyLimitDtoResult.getBpayLimit());
         * assertNotNull(dailyLimitDtoResult.getBpayLimits()); assertTrue(dailyLimitDtoResult.getBpayLimits().size() > 0);
         */

    }

    @Test
    public void testupdateLimitsBPAY() throws Exception {
        /*
         * bpayDailyLimitDto.setLimit(new BigDecimal(3001)); bpayDailyLimitDto.setKey(accountKey);
         * Mockito.when(paymentLimitService.updatePaymentLimit(bpayDailyLimitDto.getKey().getAccountId(),
         * bpayDailyLimitDto.getLimit().toString(), PayeeType.BPAY)).thenReturn(true);
         * 
         * DailyLimitDto dailyLimitDto = paymentLimitDtoServiceImpl.updateDailyLimit(bpayDailyLimitDto, new ServiceErrorsImpl());
         * 
         * assertNotNull(dailyLimitDto); assertNotNull(dailyLimitDto.getLimit());
         */
    }

    @Test
    public void testCheckLimitsPayAnyOne() throws Exception {

        /*
         * DailyLimitDto dailyLimitDto = new DailyLimitDto();
         * Mockito.when(paymentLimitService.checkPaymentLimit(Mockito.anyString(), Mockito.anyString(),
         * Mockito.any(PayeeType.class))) .thenReturn(payanyOneDailyLimitModel);
         * 
         * DailyLimitDto dailyLimitDtoResult = paymentLimitDtoServiceImpl.checkLimits("36846", payanyOneDailyLimitDto, new
         * ServiceErrorsImpl());
         * 
         * assertNotNull(dailyLimitDtoResult); assertNotNull(dailyLimitDtoResult.getPayAnyoneLimit());
         * assertNotNull(dailyLimitDtoResult.getPayAnyoneLimits()); assertTrue(dailyLimitDtoResult.getPayAnyoneLimits().size() >
         * 0);
         */
    }

    @Test
    public void testupdateLimitsPayAnyOne() throws Exception {

        /*
         * payanyOneDailyLimitDto.setLimit(new BigDecimal(3001)); payanyOneDailyLimitDto.setKey(accountKey);
         * Mockito.when(paymentLimitService.updatePaymentLimit(payanyOneDailyLimitDto.getKey().getAccountId(),
         * payanyOneDailyLimitDto.getLimit().toString(), PayeeType.PAY_ANYONE)).thenReturn(true);
         * 
         * DailyLimitDto dailyLimitDto = paymentLimitDtoServiceImpl.updateDailyLimit(payanyOneDailyLimitDto, new
         * ServiceErrorsImpl());
         * 
         * assertNotNull(dailyLimitDto); assertNotNull(dailyLimitDto.getLimit());
         */

    }

    @Test
    public void testUpdateDailyLimit() {
        dailyLimitDtoKeyedObj.setLimit(new BigDecimal(50000));
        dailyLimitDtoKeyedObj.setKey(new AccountKey("123456"));
        dailyLimitDtoKeyedObj.setPayeeType(PayeeType.BPAY.name());

        PayeeLimitImpl payeeLimit = new PayeeLimitImpl();
        payeeLimit.setCurrency("AUD");
        payeeLimit.setLimitAmount("1000");
        payeeLimit.setOrderType(TransactionOrderType.BPAY);


        PayeeLimitImpl payeeLimit2 = new PayeeLimitImpl();
        payeeLimit2.setCurrency("AUD");
        payeeLimit2.setLimitAmount("1000");
        payeeLimit2.setOrderType(TransactionOrderType.PAY_ANYONE);




        ArrayList<PayeeLimit> tempArray = new ArrayList<>();

        tempArray.add(payeeLimit);
        tempArray.add(payeeLimit2);

        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        payeeDetailsImpl.setPayeeLimits(tempArray);



        payeeDetailsImpl.setModifierSeqNumber(new BigDecimal(8));

        payeeDetailsImpl.setMaxDailyLimit("0.00");

        Mockito.when(payeeDetailsIntegrationService.loadPayeeDetails(Mockito.any(WrapAccountIdentifier.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(payeeDetailsImpl);

        DailyLimitDto dailyLimitDto;

        Mockito.when(avaloqAccountIntegrationServiceImpl.updatePaymentLimit(Mockito.any(UpdatePaymentLimitRequest.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);

        dailyLimitDto = paymentLimitDtoServiceImpl.updateDailyLimit(dailyLimitDtoKeyedObj, new ServiceErrorsImpl());

        assertNotNull(dailyLimitDto);

        assertEquals("true", dailyLimitDto.getIsLimitUpdated());


        payeeDetailsImpl.setPayeeLimits(null);

        dailyLimitDto = paymentLimitDtoServiceImpl.updateDailyLimit(dailyLimitDtoKeyedObj, new ServiceErrorsImpl());

        assertNotNull(dailyLimitDto);



        PayeeLimitImpl payeeLimit1 = new PayeeLimitImpl();
        payeeLimit.setCurrency("AUD");
        payeeLimit.setLimitAmount("1000");

        tempArray = new ArrayList<PayeeLimit>();
        tempArray.add(payeeLimit1);

        payeeDetailsImpl = new PayeeDetailsImpl();
        payeeDetailsImpl.setPayeeLimits(tempArray);



        payeeDetailsImpl.setModifierSeqNumber(new BigDecimal(8));

        payeeDetailsImpl.setMaxDailyLimit("0.00");

        Mockito.when(payeeDetailsIntegrationService.loadPayeeDetails(Mockito.any(WrapAccountIdentifier.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(payeeDetailsImpl);


        dailyLimitDto = paymentLimitDtoServiceImpl.updateDailyLimit(dailyLimitDtoKeyedObj, new ServiceErrorsImpl());

        assertNotNull(dailyLimitDto);






    }

}
