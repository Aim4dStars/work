package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.text.ParseException;

@RunWith(MockitoJUnitRunner.class)
public class RipDepositConverterTest {
    @InjectMocks
    private RipDepositConverter ripDepositConverter;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Before
    public void setup() throws ParseException {
        Mockito.when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        if (CodeCategory.DD_PERIOD.equals(args[0])) {

                            // Once("btfg$once"), Quarterly("rq"), Weekly("weekly"),
                            // Yearly("ry")
                            if ("23".equals(args[1])) {
                                return new CodeImpl("23", "RM", "Monthly (Rolling)", "rm");
                            } else if ("66".equals(args[1])) {
                                return new CodeImpl("66", "WEEKLY_2", "bi-weekly", "weekly_2");
                            } else if ("5000".equals(args[1])) {
                                return new CodeImpl("5000", "ONCE", "Once", "btfg@once");
                            } else if ("24".equals(args[1])) {
                                return new CodeImpl("24", "RQ", "Quaterly (Rolling)", "rq");
                            } else if ("65".equals(args[1])) {
                                return new CodeImpl("65", "WEEKLY", "weekly", "weekly");
                            } else if ("26".equals(args[1])) {
                                return new CodeImpl("26", "RY", "Yearly (Rolling)", "ry");
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                });
    }

    @Test
    public void toLoadDepositRequest() throws Exception {
        String linkedDDRef = "linkedDDRef";
        InpayReq payReq = ripDepositConverter.toLoadDDRequest(linkedDDRef);
        Assert.assertTrue(payReq != null);
        Assert.assertEquals(linkedDDRef, AvaloqGatewayUtil.asString(payReq.getReq().getGet().getDoc()));

    }

    @Test
    public void toLoadDepositResponse() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        InpayRsp inpayRsp = JaxbUtil.unmarshall("/webservices/response/RipDepositResponse.xml", InpayRsp.class);
        String receiptNumber = "receiptNumber";
        RecurringDepositDetails deposit = ripDepositConverter.toLoadDDResponse(receiptNumber, inpayRsp, serviceErrors);
        Assert.assertNotNull(deposit);
        Assert.assertEquals(RecurringFrequency.Monthly, deposit.getRecurringFrequency());
        Assert.assertEquals("176160", deposit.getPositionId());
        Assert.assertEquals(CurrencyType.AustralianDollar, deposit.getCurrencyType());
        Assert.assertNotNull(deposit.getDepositDate());
        DateTime depositDate = new DateTime(deposit.getDepositDate());
        Assert.assertEquals(9, depositDate.getDayOfMonth());
        Assert.assertEquals(9, depositDate.getMonthOfYear());
        Assert.assertEquals(2015, depositDate.getYear());

    }

}
