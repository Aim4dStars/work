package com.bt.nextgen.service.avaloq.movemoney;

import com.avaloq.abs.bb.fld_def.DateFld;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.avaloq.abs.bb.fld_def.NrFld;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositConverter;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.inpay.v1_0.Contr;
import com.btfin.abs.trxservice.inpay.v1_0.Data;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.Constants;
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

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DepositConverterTest {
    ServiceErrors serviceErrors;

    @InjectMocks
    private DepositConverter depositConverter;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    protected ErrorConverter errorConverter;

    @Before
    public void setup() throws ParseException {
        serviceErrors = new ServiceErrorsImpl();

        Mockito.when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        if (CodeCategory.DD_PERIOD.equals(args[0])) {
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
                        } else if (CodeCategory.SUPER_CONTRIBUTIONS_TYPE.equals(args[0]) && "11".equals(args[1])) {
                            return new CodeImpl("11", "SPOUSE", "Spouse Contributions", "spouse");
                        } else {
                            return null;
                        }
                    }
                });
    }

    @Test
    public void testToValidateDepositRequest_Null_TransactionDate_Not_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((DepositDetailsImpl) deposit).setContributionType(ContributionType.SPOUSE);

        InpayReq depositReq = depositConverter.toValidateDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
        assertThat(depositReq.getData().getContriTypeId().getExtlVal().getVal(),
                equalTo(deposit.getContributionType().getIntlId()));
    }

    @Test
    public void testToValidateDepositRequest_Null_TransactionDate_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(DateTime.now().plusMonths(6));
        ((DepositDetailsImpl) deposit).setContributionType(ContributionType.SPOUSE);

        InpayReq depositReq = depositConverter.toValidateDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
        assertThat(depositReq.getData().getContriTypeId().getExtlVal().getVal(),
                equalTo(deposit.getContributionType().getIntlId()));
    }

    @Test
    public void testToValidateDepositRequest_TransactionDate_Not_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((DepositDetailsImpl) deposit).setContributionType(ContributionType.SPOUSE);

        InpayReq depositReq = depositConverter.toValidateDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
        assertThat(depositReq.getData().getContriTypeId().getExtlVal().getVal(),
                equalTo(deposit.getContributionType().getIntlId()));
    }

    @Test
    public void testToValidateDepositRequest_TransactionDate_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((DepositDetailsImpl) deposit).setContributionType(ContributionType.SPOUSE);

        InpayReq depositReq = depositConverter.toValidateDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
        assertThat(depositReq.getData().getContriTypeId().getExtlVal().getVal(),
                equalTo(deposit.getContributionType().getIntlId()));
    }

    @Test
    public void testToValidateDepositRequest_Existing() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(DateTime.now().plusMonths(6));
        ((DepositDetailsImpl) deposit).setContributionType(ContributionType.SPOUSE);
        ((DepositDetailsImpl) deposit).setReceiptNumber("1");
        ((DepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toValidateDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
        assertThat(depositReq.getData().getContriTypeId().getExtlVal().getVal(),
                equalTo(deposit.getContributionType().getIntlId()));
    }

    @Test
    public void testToSubmitDepositRequest_Null_TransactionDate_Not_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitDepositRequest_Null_TransactionDate_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(DateTime.now().plusMonths(6));

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitDepositRequest_TransactionDate_Not_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitDepositRequest_TransactionDate_Future() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitDepositRequest() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitDepositRequest_Existing_Future_Deposit() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((DepositDetailsImpl) deposit).setReceiptNumber("1");
        ((DepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("hold_stcoll_done"));
    }

    @Test
    public void testToSubmitDepositRequest_Existing_Deposit() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((DepositDetailsImpl) deposit).setReceiptNumber("1");
        ((DepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("hold_inpay_done"));
    }

    @Test
    public void testToSubmitDepositRequest_Existing_Recurring() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        ((RecurringDepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((RecurringDepositDetailsImpl) deposit).setReceiptNumber("1");
        ((RecurringDepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toSubmitDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("hold_stcoll_done"));
    }

    @Test
    public void testToGenericDepositRequest_Null_ContributionType() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();

        InpayReq depositReq = depositConverter.toGenericDepositRequest(deposit, serviceErrors);
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
    public void testToGenericDepositRequest_ContributionType() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setContributionType(ContributionType.PERSONAL);

        InpayReq depositReq = depositConverter.toGenericDepositRequest(deposit, serviceErrors);
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

        InpayReq depositReq = depositConverter.toValidateRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getValid());
        assertThat(depositReq.getReq().getValid().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitRecurringDepositRequest_Null_MaxCount() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();

        InpayReq depositReq = depositConverter.toSubmitRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSubmitRecurringDepositRequest_MaxCount() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        ((RecurringDepositDetailsImpl) deposit).setMaxCount(10);
        ((RecurringDepositDetailsImpl) deposit).setEndDate(new DateTime("2015-01-26"));

        InpayReq depositReq = depositConverter.toSubmitRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getGenericAction(), is(Constants.DO));
    }

    @Test
    public void testToSaveDepositRequest_whenDepositIdNullAndTransactionDateFuture_thenCorrectActionSet() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(DateTime.now().plusMonths(6));

        InpayReq depositReq = depositConverter.toSaveDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq.getData().getContr());
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("store"));
    }

    @Test
    public void testToSaveDepositRequest_whenDepositIdNullAndTransactionDateNotFuture_thenCorrectActionSet() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));

        InpayReq depositReq = depositConverter.toSaveDepositRequest(deposit, false, serviceErrors);
        assertNull(depositReq.getData().getContr());
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("hold"));
    }

    @Test
    public void testToSaveDepositRequest_whenDepositIdNotNullAndTransactionDateNotFuture_thenCorrectActionSet() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(new DateTime("2015-01-27"));
        ((DepositDetailsImpl) deposit).setReceiptNumber("1");
        ((DepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toSaveDepositRequest(deposit, false, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("hold"));
    }

    @Test
    public void testToSaveDepositRequest_whenDepositIdNotNullAndTransactionDateFuture_thenCorrectActionSet() throws Exception {
        DepositDetails deposit = createDepositDetailsObject();
        ((DepositDetailsImpl) deposit).setTransactionDate(DateTime.now().plusMonths(6));
        ((DepositDetailsImpl) deposit).setReceiptNumber("1");
        ((DepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toSaveDepositRequest(deposit, true, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("store_stcoll"));
    }

    @Test
    public void testToSaveRecurringDepositRequest_whenDepositIdNull_thenCorrectActionSet() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();

        InpayReq depositReq = depositConverter.toSaveRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("store"));
    }

    @Test
    public void testToSaveRecurringDepositRequest_whenDepositIdNotNull_thenCorrectActionSet() throws Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        ((RecurringDepositDetailsImpl) deposit).setReceiptNumber("1");
        ((RecurringDepositDetailsImpl) deposit).setTransactionSeq("2");

        InpayReq depositReq = depositConverter.toSaveRecurringDepositRequest(deposit, serviceErrors);
        assertNotNull(depositReq);
        assertNotNull(depositReq.getReq());
        assertNotNull(depositReq.getReq().getExec());
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is("store_stcoll"));
    }

    @Test
    public void testToGenericDepositResponse() throws Exception {
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositPayOnceResponse.xml", InpayRsp.class);
        DepositDetails response = depositConverter.toDepositResponse(report, serviceErrors);
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
        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(report, serviceErrors);
        assertNotNull(response.getStartDate());
        assertNotNull(response.getEndDate());
    }

    @Test
    public void testToSubmitDepositResponse() throws Exception {
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositPayOnceResponse.xml", InpayRsp.class);
        DepositDetails response = depositConverter.toDepositResponse(report, serviceErrors);
        assertThat(response.getReceiptNumber(), equalTo(report.getData().getDoc().getVal().toString()));
    }

    @Test
    public void testToSubmitRecurringDepositResponse() throws Exception {
        // mockStaticCode();
        InpayRsp report = JaxbUtil.unmarshall("/webservices/response/DepositRecurringResponse.xml", InpayRsp.class);
        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(report, serviceErrors);
        assertThat(response.getReceiptNumber(), equalTo(report.getData().getDoc().getVal().toString()));
        assertNotNull(response.getStartDate());
        assertNotNull(response.getEndDate());
    }

    @Test
    public void testToStopDepositRequest() throws Exception {
        InpayReq depositReq = depositConverter.toStopDepositRequest("12345", serviceErrors);
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
        TransactionStatus status = depositConverter.toStopDepositResponse(report, serviceErrors);
        assertThat(status.isSuccessful(), is(true));
    }

    @Test
    public void testToStopDepositResponse_Null_Doc() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        inPayResponse.setData(data);
        TransactionStatus status = depositConverter.toStopDepositResponse(inPayResponse, serviceErrors);
        assertThat(status.isSuccessful(), is(false));
    }

    @Test
    public void testToRecursiveDepositResponse_FrequencyNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        data.setContr(contr);
        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertNull(response.getRecurringFrequency());
    }

    @Test
    public void testToRecursiveDepositResponse_FrequencyNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);
        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertThat(response.getRecurringFrequency().getDescription(), equalTo("Monthly"));
    }

    @Test
    public void testToRecursiveDepositResponse_TrxDateNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);
        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertNull(response.getTransactionDate());
    }

    @Test
    public void testToRecursiveDepositResponse_TrxDateNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);

        DateFld dateVal = AvaloqObjectFactory.getFlddefobjectfactory().createDateFld();
        data.setTrxDate(dateVal);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertNull(response.getTransactionDate());
    }

    @Test
    public void testToRecursiveDepositResponse_TrxDateValNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);

        XMLGregorianCalendar trxDate = null;
        DateFld dateVal = AvaloqObjectFactory.getFlddefobjectfactory().createDateFld();
        dateVal.setVal(trxDate);
        data.setTrxDate(dateVal);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertNull(response.getTransactionDate());
    }

    @Test
    public void testToRecursiveDepositResponse_TrxDateValNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);

        DateTime dateTime = new DateTime("2015-01-27");
        XMLGregorianCalendar trxDate = null;
        trxDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());

        DateFld dateVal = AvaloqObjectFactory.getFlddefobjectfactory().createDateFld();
        dateVal.setVal(trxDate);
        data.setTrxDate(dateVal);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertThat(response.getTransactionDate().getMillis(), equalTo(new DateTime("2015-01-27").getMillis()));
    }

    @Test
    public void testToRecursiveDepositResponse_ContrPeriodStartNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);

        DateTime dateTime = new DateTime("2015-01-28");
        XMLGregorianCalendar contrDate = null;
        contrDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());

        DateFld dateVal = AvaloqObjectFactory.getFlddefobjectfactory().createDateFld();
        dateVal.setVal(contrDate);
        data.getContr().setContrPeriodStart(dateVal);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertThat(response.getTransactionDate().getMillis(), equalTo(new DateTime("2015-01-28").getMillis()));
    }

    @Test
    public void testToRecursiveDepositResponse_ContributionNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);

        IdFld contributioIdField = new IdFld();
        contributioIdField.setVal("11");
        data.setContriTypeId(contributioIdField);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertThat(response.getContributionType(), equalTo(ContributionType.SPOUSE));
    }

    @Test
    public void testToRecursiveDepositResponse_MaxCountNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        NrFld nrField = new NrFld();
        nrField.setVal(new BigDecimal("5"));
        contr.setMaxPeriodCnt(nrField);
        data.setContr(contr);

        IdFld contributioIdField = new IdFld();
        contributioIdField.setVal("11");
        data.setContriTypeId(contributioIdField);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertThat(response.getMaxCount(), equalTo(5));
    }

    @Test
    public void testToRecursiveDepositResponse_TransactionSeqNotNull() throws Exception {
        InpayRsp inPayResponse = new InpayRsp();
        Data data = new Data();
        Rsp rsp = new Rsp();
        Contr contr = new Contr();
        IdFld idField = new IdFld();
        idField.setVal("23");
        contr.setContrPeriod(idField);
        data.setContr(contr);
        NrFld nrField = new NrFld();
        nrField.setVal(new BigDecimal("2"));
        data.setLastTransSeqNr(nrField);

        inPayResponse.setData(data);
        inPayResponse.setRsp(rsp);

        RecurringDepositDetails response = depositConverter.toRecursiveDepositResponse(inPayResponse, serviceErrors);
        assertThat(response.getTransactionSeq(), equalTo("2"));
    }

    @Test
    public void testToDeleteDepositRequest_whenValidRequest_thenInpayReqReturned() throws Exception {
        BigDecimal depositId = new BigDecimal("123564789");
        String actionType = "hold_inpay_discd";

        InpayReq depositReq = depositConverter.toDeleteDepositRequest(depositId.toPlainString(), actionType, serviceErrors);
        assertThat(depositReq.getReq().getExec().getAction().getWfcAction(), is(actionType));
        assertThat(depositReq.getReq().getExec().getDoc().getVal(), is(depositId));
    }

    @Test
    public void testProcessDeleteResponse_whenNoErrors_thenNoExceptionThrown() throws Exception {
        InpayRsp rsp = JaxbUtil.unmarshall("/webservices/response/SavedDepositDeleteResponse_UT.xml", InpayRsp.class);
        try {
            depositConverter.processDeleteResponse(rsp, serviceErrors);
        } catch (ValidationException e) {
            Assert.fail("should not throw any validation exceptions");
        }
    }

    @Test
    public void testProcessDeleteResponse_whenErrors_thenExceptionThrown() throws Exception {
        List<ValidationError> errors = new ArrayList<>();
        errors.add(new ValidationError("avaloqErrorId", "field", "Some error about deleting failing", ErrorType.ERROR));
        Mockito.when(errorConverter.processErrorList(Mockito.any(ErrList.class))).thenReturn(errors);
        InpayRsp rsp = JaxbUtil.unmarshall("/webservices/response/SavedDepositDeleteResponseErr_UT.xml", InpayRsp.class);
        try {
            depositConverter.processDeleteResponse(rsp, serviceErrors);
            Assert.fail("no validation exceptions thrown");
        } catch (ValidationException e) {
            Assert.assertEquals("Some error about deleting failing", e.getErrors().get(0).getMessage());
        }
    }

    DepositDetails createDepositDetailsObject() throws ParseException {
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("64083");

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");

        BigDecimal depositAmount = new BigDecimal(123);
        CurrencyType currency = CurrencyType.AustralianDollar;
        String description = "Rent 123";
        DateTime transactionDate = null;
        ContributionType contributionType = null;

        return new DepositDetailsImpl(moneyAccId, accDetails, depositAmount, currency, description, transactionDate, null, null,
                contributionType, null, null);
    }

    RecurringDepositDetails createDepositDetailsObject_Recurring() throws ParseException {
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("64083");

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");

        BigDecimal depositAmount = new BigDecimal(123);
        CurrencyType currency = CurrencyType.AustralianDollar;
        String description = "Rent 123";
        DateTime transactionDate = new DateTime("2015-01-27");
        DateTime startDate = new DateTime("2015-01-28");
        DateTime endDate = null;

        return new RecurringDepositDetailsImpl(moneyAccId, accDetails, depositAmount, currency, description, transactionDate,
                null, null, ContributionType.SPOUSE, RecurringFrequency.Monthly, startDate, endDate, null, null, null);
    }
}
