package com.bt.nextgen.deposit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.deposit.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.deposit.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.deposit.DepositConverter;
import com.bt.nextgen.service.integration.deposit.DepositDetails;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class DepositConverterTest {
    ServiceErrors serviceErrors;

    /*
     * @Mock private AvaloqStaticIntegrationService avaloqStaticIntegrationService;
     */
    @Before
    public void setUp() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToValidateDepositRequest() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();

        InpayReq depositReq = DepositConverter.toValidateDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitDepositRequest() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();

        InpayReq depositReq = DepositConverter.toSubmitDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToGenericDepositRequest() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();

        InpayReq depositReq = DepositConverter.toGenericDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getHdr());
        assertNotNull(depositReq.getData());
        assertThat(depositReq.getData().getAmount().getVal(), equalTo(deposit.getDepositAmount()));
        assertThat(depositReq.getData().getCredMacc().getVal(), equalTo(deposit.getMoneyAccountIdentifier().getMoneyAccountId()));
        assertThat(depositReq.getData().getPayerAcc().getVal(), equalTo(deposit.getPayAnyoneAccountDetails().getAccount()));
        assertThat(depositReq.getData().getBsb().getVal(), equalTo(deposit.getPayAnyoneAccountDetails().getBsb()));
        assertThat(depositReq.getData().getBenefRefNr().getVal(), equalTo(deposit.getDescription()));
        assertNotNull(depositReq.getData().getCurry().getExtlVal());

    }

    @Test
    public void testToValidateRecurringDepositRequest() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();

        InpayReq depositReq = DepositConverter.toValidateRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitRecurringDepositRequest() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();

        InpayReq depositReq = DepositConverter.toSubmitRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToGenericDepositResponse() throws Exception {
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositPayOnceResponse.xml", InpayRsp.class);
        DepositDetails response = DepositConverter.toGenericDepositResponse(createDepositDetailsObject(), report, serviceErrors);
        assertThat(report.getData().getAmount().getVal(), equalTo(response.getDepositAmount()));
        assertThat(report.getData().getCredMacc().getVal(), equalTo(response.getMoneyAccountIdentifier().getMoneyAccountId()));
        assertThat(report.getData().getPayerAcc().getVal(), equalTo(response.getPayAnyoneAccountDetails().getAccount()));
        assertThat(report.getData().getBsb().getVal(), equalTo(response.getPayAnyoneAccountDetails().getBsb()));
        assertThat(report.getData().getBenefRefNr().getVal(), equalTo(response.getDescription()));
    }

    @Test
    public void testToRecursiveDepositResponse() throws Exception {
        // mockStaticCode();
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositRecurringResponse.xml", InpayRsp.class);
        RecurringDepositDetails response = DepositConverter.toRecursiveDepositResponse(createDepositDetailsObject_Recurring(),
                report, serviceErrors);
        // assertThat(response.getRecurringFrequency().toString(), equalTo("Monthly"));
        assertNotNull(response.getStartDate());
        assertNotNull(response.getEndDate());
        /*
         * assertThat(response.getStartDate().toString(), equalTo(report.getData() .getContr() .getContrPeriodStart() .getVal()
         * .toString()));
         * 
         * assertThat(response.getEndDate().toString(),
         * equalTo(report.getData().getContr().getContrPeriodEnd().getVal().toString()));
         */

    }

    @Test
    public void testToSubmitDepositResponse() throws Exception {
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositPayOnceResponse.xml", InpayRsp.class);
        DepositDetails response = DepositConverter.toSubmitDepositResponse(createDepositDetailsObject(), report, serviceErrors);
        assertThat(response.getRecieptNumber(), equalTo(report.getData().getDoc().getVal().toString()));

    }

    @Test
    public void testToSubmitRecurringDepositResponse() throws Exception {
        // mockStaticCode();
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositRecurringResponse.xml", InpayRsp.class);
        RecurringDepositDetails response = DepositConverter
                .toSubmitRecurringDepositResponse(createDepositDetailsObject_Recurring(), report, serviceErrors);
        assertThat(response.getRecieptNumber(), equalTo(report.getData().getDoc().getVal().toString()));
        assertNotNull(response.getStartDate());
        assertNotNull(response.getEndDate());
    }

    @Test
    public void testToStopDepositRequest() throws Exception {
        InpayReq depositReq = DepositConverter.toStopDepositRequest("12345", serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getHdr());
        assertNotNull(depositReq.getData());
        assertThat(depositReq.getData().getPos().getVal(), is("12345"));
        assertNotNull(depositReq.getReq());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.CANCEL));
    }

    @Test
    public void testToStopDepositResponse() throws Exception {
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositPayOnceResponse.xml", InpayRsp.class);
        TransactionStatus status = DepositConverter.toStopDepositResponse(report, serviceErrors);
        assertThat(status.isSuccessful(), is(true));
    }

    DepositDetails createDepositDetailsObject() throws ParseException {
        DepositDetails deposit = new DepositDetailsImpl();
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("64083");
        deposit.setMoneyAccountIdentifier(moneyAccId);

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");
        deposit.setPayAnyoneAccountDetails(accDetails);

        deposit.setDepositAmount(new BigDecimal(123));
        CurrencyType currency = CurrencyType.AustralianDollar;
        deposit.setCurrencyType(currency);
        deposit.setDescription("Rent 123");
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-01-27");
        deposit.setTransactionDate(date);
        return deposit;
    }

    RecurringDepositDetails createDepositDetailsObject_Recurring() throws ParseException {
        RecurringDepositDetails deposit = new RecurringDepositDetailsImpl();
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("64083");
        deposit.setMoneyAccountIdentifier(moneyAccId);

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");
        deposit.setPayAnyoneAccountDetails(accDetails);

        deposit.setDepositAmount(new BigDecimal(123));
        CurrencyType currency = CurrencyType.AustralianDollar;
        deposit.setCurrencyType(currency);
        deposit.setDescription("Rent 123");
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-01-27");
        deposit.setTransactionDate(date);

        deposit.setRecurringFrequency(RecurringFrequency.Monthly);
        deposit.setStartDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-01-28"));
        deposit.setEndDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2016-01-26"));

        return deposit;
    }

    RecurringDepositDetails createInRspObject_Recurring() throws ParseException {
        RecurringDepositDetails deposit = new RecurringDepositDetailsImpl();
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("64083");
        deposit.setMoneyAccountIdentifier(moneyAccId);

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");
        deposit.setPayAnyoneAccountDetails(accDetails);

        deposit.setDepositAmount(new BigDecimal(123));
        CurrencyType currency = CurrencyType.AustralianDollar;
        deposit.setCurrencyType(currency);
        deposit.setDescription("Rent 123");
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-01-27");
        deposit.setTransactionDate(date);

        deposit.setRecurringFrequency(RecurringFrequency.Monthly);
        deposit.setStartDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-01-28"));
        deposit.setEndDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2016-01-26"));

        return deposit;
    }
}
