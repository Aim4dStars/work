package com.bt.nextgen.service.avaloq.regularinvestment;

import com.avaloq.abs.bb.fld_def.NrFld;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.movemoney.DepositConverter;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RIPTransactionsIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class RegularInvestmentDelegateServiceImplTest {
    @InjectMocks
    RegularInvestmentDelegateServiceImpl regularInvestmentDelegateService;

    @Mock
    RIPTransactionsIntegrationServiceFactory ripTransactionsIntegrationServiceFactory;

    @Mock
    RegularInvestmentIntegrationService ripIntegrationService;

    @Mock
    RIPTransactionsIntegrationService ripTransactionIntegrationService;

    @Mock
    private AvaloqGatewayHelperService webService;

    @Mock
    private DepositConverter depositConverter;

    private RegularInvestmentImpl rip;

    private InpayReq inpayReq;

    private List<RegularInvestmentTransaction> ripTransactions;

    private RegularInvestmentTransactionImpl ripTransaction;

    @Before
    public void setup() {
        Mockito.when(ripTransactionsIntegrationServiceFactory.getInstance(anyString()))
                .thenReturn(ripTransactionIntegrationService);

        rip = new RegularInvestmentImpl();
        rip.setReference("reference try MF only");
        rip.setOwner(ClientKey.valueOf("29641"));
        RIPScheduleImpl ripSchedule = new RIPScheduleImpl(new DateTime(), null, RIPRecurringFrequency.Monthly);
        rip.setRIPSchedule(ripSchedule);

        Pair<String, BigDecimal> allocation = new ImmutablePair<String, BigDecimal>("137120", BigDecimal.ONE);
        List<OrderItem> orderItems = new ArrayList<>();

        OrderItemSummaryImpl summary = new OrderItemSummaryImpl(new BigDecimal("5000"), false, "Cash", BigInteger.ZERO,
                BigDecimal.ZERO, null, null);
        OrderItemImpl item = new OrderItemImpl(SubAccountKey.valueOf("137120"), null, "buy", AssetType.MANAGED_FUND, "111278",
                summary, Collections.singletonList(allocation));

        orderItems.add(item);

        OrderItemSummaryImpl summary1 = new OrderItemSummaryImpl(new BigDecimal("10000"), false, "Cash", BigInteger.ZERO,
                BigDecimal.ZERO, null, null);
        OrderItemImpl item1 = new OrderItemImpl(SubAccountKey.valueOf("151470"), null, "buy", AssetType.MANAGED_PORTFOLIO,
                "111780",
                summary1, Collections.singletonList(allocation));

        orderItems.add(item1);

        rip.setOrders(orderItems);

        ripTransaction = new RegularInvestmentTransactionImpl();
        ripTransaction.setAccountKey("accountKey");
        ripTransactions = new ArrayList<>();
        ripTransactions.add(ripTransaction);

        inpayReq = new InpayReq();
        inpayReq.setData(new com.btfin.abs.trxservice.inpay.v1_0.Data());
        inpayReq.getData().setDoc(new NrFld());
    }

    @Ignore
    @Test
    public void testValidateRIP_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        final RecurringDepositDetails dep = new RecurringDepositDetailsImpl();
        final ServiceErrors err = new ServiceErrorsImpl();
        Mockito.when(regularInvestmentDelegateService.validateRIPDeposit(dep, err)).thenReturn(null);
        Mockito.when(ripIntegrationService.validateRegularInvestment(Mockito.any(RegularInvestment.class),
                Mockito.any(ServiceErrors.class))).thenReturn(rip);
        RegularInvestment regularInvestment = regularInvestmentDelegateService.validateRegularInvestment(rip, null, null);
        Assert.assertNotNull(regularInvestment);

    }

    @Test
    public void testSubmitRIP_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        Mockito.when(ripIntegrationService.submitRegularInvestment(Mockito.any(RegularInvestment.class), anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(rip);

        RegularInvestment regularInvestment = regularInvestmentDelegateService.submitRegularInvestment(rip,
                new ServiceErrorsImpl(), new ServiceErrorsImpl());
        Assert.assertNotNull(regularInvestment);

    }

    @Test
    public void testSaveRIP_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        Mockito.when(ripIntegrationService.saveRegularInvestment(Mockito.any(RegularInvestment.class), anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(rip);

        RegularInvestment regularInvestment = regularInvestmentDelegateService.saveRegularInvestment(rip,
                new ServiceErrorsImpl(), new ServiceErrorsImpl());
        Assert.assertNotNull(regularInvestment);

    }



    @Test
    public void testLoadRIP_whenValidResponse_thenObjectLoadedAndNoServiceErrors() throws Exception {
        Mockito.when(ripIntegrationService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(rip);
        RegularInvestment regularInvestment = regularInvestmentDelegateService
                .loadRegularInvestment(AccountKey.valueOf("accountId"), "1234", null, null);
        Assert.assertNotNull(regularInvestment);
    }

    @Test
    public void testLoadRIPs_whenValidResponse_thenObjectLoadedAndNoServiceErrors() throws Exception {
        Mockito.when(ripTransactionIntegrationService.loadRegularInvestments(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(ripTransactions);
        List<RegularInvestmentTransaction> ripTransactions = regularInvestmentDelegateService
                .loadRegularInvestments(AccountKey.valueOf("12345"), null, null);
        Assert.assertNotNull(ripTransactions);
    }

    @Test
    public void testSuspendRIP_whenValidResponse_thenObjectLoadedAndNoServiceErrors() throws Exception {
        Mockito.when(ripIntegrationService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(rip);

        Mockito.when(regularInvestmentDelegateService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class), new ServiceErrorsImpl())).thenReturn(rip);

        Mockito.when(ripIntegrationService.suspendRegularInvestment(Mockito.any(RegularInvestment.class),
                Mockito.any(ServiceErrors.class))).thenReturn(rip);

        RegularInvestment regularInvestment = regularInvestmentDelegateService.suspendRegularInvestment(
                new OrderGroupKey(EncodedString.fromPlainText("accountKey").toString(), "1234"), new ServiceErrorsImpl());
        Assert.assertNotNull(regularInvestment);
    }

    @Test
    public void testSuspendActiveRIP_thenStopDepositIsCalled() throws Exception {
        RegularInvestment ripLocal = rip;
        RecurringDepositDetailsImpl dep = new RecurringDepositDetailsImpl();
        dep.setPositionId("positionId");
        dep.setReceiptNumber("receiptNumber");
        ripLocal.setDirectDebitDetails(dep);
        rip.setRIPStatus(RIPStatus.ACTIVE);

        Mockito.when(regularInvestmentDelegateService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class), new ServiceErrorsImpl())).thenReturn(ripLocal);

        Mockito.when(depositConverter.toStopDepositRequest(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(inpayReq);

        final List<String> list = new ArrayList<>(1);
        Mockito.doAnswer(new Answer<InpayRsp>() {
            @Override
            public InpayRsp answer(InvocationOnMock invocation) throws Throwable {
                // Stop INPAY_REQ has been posted.
                list.add("1");

                InpayRsp rsp = new InpayRsp();
                rsp.setData(new com.btfin.abs.trxservice.inpay.v1_0.Data());
                rsp.getData().setDoc(new NrFld());
                return rsp;
            }
        }).when(webService).sendToWebService(isA(InpayReq.class), isA(AvaloqOperation.class), isA(ServiceErrors.class));

        Mockito.when(ripIntegrationService.suspendRegularInvestment(Mockito.any(RegularInvestment.class),
                Mockito.any(ServiceErrors.class))).thenReturn(ripLocal);

        RegularInvestment regularInvestment = regularInvestmentDelegateService.suspendRegularInvestment(
                new OrderGroupKey(EncodedString.fromPlainText("accountKey").toString(), "1234"), new ServiceErrorsImpl());
        Assert.assertNotNull(regularInvestment);
        Assert.assertEquals("1", list.get(0));
    }

    @Test
    public void testSuspendNonActiveRIP_thenStopDepositIsSubmitted_OnlyWhenRIPIsActive() throws Exception {
        RegularInvestment ripLocal = rip;
        RecurringDepositDetails dep = new RecurringDepositDetailsImpl();
        rip.setDirectDebitDetails(dep);
        rip.setRIPStatus(RIPStatus.SUSPENDED);

        Mockito.when(regularInvestmentDelegateService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class), new ServiceErrorsImpl())).thenReturn(ripLocal);

        Mockito.when(ripIntegrationService.suspendRegularInvestment(Mockito.any(RegularInvestment.class),
                Mockito.any(ServiceErrors.class))).thenReturn(ripLocal);

        regularInvestmentDelegateService.suspendRegularInvestment(
                new OrderGroupKey(EncodedString.fromPlainText("accountKey").toString(), "1234"), new ServiceErrorsImpl());
        Assert.assertTrue("If Stop DD has been called, this test would have failed due to exception thrown.", true);
    }

    @Test
    public void testCancelActiveRIP_thenStopDepositIsCalled() throws Exception {
        RegularInvestment ripLocal = rip;
        RecurringDepositDetailsImpl dep = new RecurringDepositDetailsImpl();
        dep.setPositionId("positionId");
        dep.setReceiptNumber("receiptNumber");
        ripLocal.setDirectDebitDetails(dep);
        rip.setRIPStatus(RIPStatus.ACTIVE);

        Mockito.when(regularInvestmentDelegateService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class), new ServiceErrorsImpl())).thenReturn(ripLocal);

        Mockito.when(depositConverter.toStopDepositRequest(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(inpayReq);

        final List<String> list = new ArrayList<>(1);
        Mockito.doAnswer(new Answer<InpayRsp>() {
            @Override
            public InpayRsp answer(InvocationOnMock invocation) throws Throwable {
                // Stop INPAY_REQ has been posted.
                list.add("1");

                InpayRsp rsp = new InpayRsp();
                rsp.setData(new com.btfin.abs.trxservice.inpay.v1_0.Data());
                rsp.getData().setDoc(new NrFld());
                return rsp;
            }
        }).when(webService).sendToWebService(isA(InpayReq.class), isA(AvaloqOperation.class), isA(ServiceErrors.class));
        RegularInvestment regularInvestment = regularInvestmentDelegateService.cancelRegularInvestment(
                new OrderGroupKey(EncodedString.fromPlainText("accountKey").toString(), "1234"), new ServiceErrorsImpl());
        Assert.assertEquals("1", list.get(0));
    }

    @Test
    public void testResumeSuspendedRIP_DDIsRecreated() throws Exception {
        RegularInvestment ripLocal = rip;
        RecurringDepositDetailsImpl dep = new RecurringDepositDetailsImpl();
        dep.setPositionId("positionId");
        dep.setNextTransactionDate(DateTime.now().plusMonths(1));
        dep.setReceiptNumber("receiptNumber");
        dep.setDepositAmount(BigDecimal.ONE);

        MoneyAccountIdentifier ma = new MoneyAccountIdentifierImpl();
        ma.setMoneyAccountId("moneyAccountId");
        dep.setMoneyAccountIdentifier(ma);

        PayAnyoneAccountDetails payAnyone = new PayAnyoneAccountDetailsImpl();
        payAnyone.setAccount("account");
        payAnyone.setBsb("123456");
        dep.setPayAnyoneAccountDetails(payAnyone);
        dep.setDescription("description");
        dep.setCurrencyType(CurrencyType.AustralianDollar);
        dep.setRecurringFrequency(RecurringFrequency.Monthly);

        ripLocal.setDirectDebitDetails(dep);
        rip.setRIPStatus(RIPStatus.SUSPENDED);

        Mockito.when(regularInvestmentDelegateService.loadRegularInvestment(Mockito.any(AccountKey.class), anyString(),
                Mockito.any(ServiceErrors.class), new ServiceErrorsImpl())).thenReturn(ripLocal);

        Mockito.when(depositConverter.toSubmitRecurringDepositRequest(Mockito.any(RecurringDepositDetails.class),
                Mockito.any(ServiceErrors.class))).thenReturn(inpayReq);

        Mockito.when(
                depositConverter.toSubmitRecurringDepositResponse(Mockito.any(InpayRsp.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(dep);

        final List<String> list = new ArrayList<>(1);
        Mockito.doAnswer(new Answer<InpayRsp>() {
            @Override
            public InpayRsp answer(InvocationOnMock invocation) throws Throwable {
                // Submit INPAY_REQ.
                list.add("1");

                InpayRsp rsp = new InpayRsp();
                rsp.setData(new com.btfin.abs.trxservice.inpay.v1_0.Data());
                rsp.getData().setDoc(new NrFld());

                return rsp;
            }
        }).when(webService).sendToWebService(isA(InpayReq.class), isA(AvaloqOperation.class), isA(ServiceErrors.class));

        Mockito.when(ripIntegrationService.resumeRegularInvestment(Mockito.any(RegularInvestment.class),
                Mockito.any(ServiceErrors.class))).thenReturn(ripLocal);

        RegularInvestment regularInvestment = regularInvestmentDelegateService.resumeRegularInvestment(
                new OrderGroupKey(EncodedString.fromPlainText("accountKey").toString(), "1234"), new ServiceErrorsImpl(),
                new ServiceErrorsImpl());
        Assert.assertEquals("1", list.get(0));
    }
}
