package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RegularInvestmentConverterTest {
    @InjectMocks
    private RegularInvestmentConverter ripConverter;

    private RegularInvestmentImpl newRip;

    private RegularInvestmentImpl existingRip;

    private RegularInvestmentImpl existingHoldRip;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private OrderItemInitialiser orderItemInit;

    @Before
    public void setup() throws ParseException {
        newRip = new RegularInvestmentImpl();
        existingRip = new RegularInvestmentImpl();
        existingHoldRip = new RegularInvestmentImpl();
        newRip.setReference("reference");
        newRip.setOwner(ClientKey.valueOf("owner"));
        DateTime date = new DateTime();
        RIPScheduleImpl ripSchedule = new RIPScheduleImpl(date, date, RIPRecurringFrequency.Monthly);
        newRip.setRIPSchedule(ripSchedule);

        Pair<String, BigDecimal> allocation = new ImmutablePair<String, BigDecimal>("cashAccount", BigDecimal.ONE);
        List<OrderItem> orderItems = new ArrayList<>();

        OrderItemImpl item = new OrderItemImpl();
        OrderItemSummaryImpl summary = new OrderItemSummaryImpl();

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641960", "buy", AssetType.MANAGED_PORTFOLIO, "1234", summary,
                Collections.singletonList(allocation));
        item.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(item);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641961", "buy", AssetType.MANAGED_PORTFOLIO, "2345", summary,
                Collections.singletonList(allocation));
        item.setSubAccountKey(null);
        orderItems.add(item);

        // mf orders
        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, "Cash", null, null, null, null);
        item = new OrderItemImpl("641960", "buy", AssetType.MANAGED_FUND, "4567", summary, Collections.singletonList(allocation));
        orderItems.add(item);

        newRip.setOrders(orderItems);

        existingHoldRip.setRIPStatus(RIPStatus.HOLD);
        existingHoldRip.setOrders(orderItems);
        existingHoldRip.setOrderGroupId("1234");
        existingHoldRip.setOwner(ClientKey.valueOf("existing owner"));
        existingHoldRip.setTransactionSeq(new BigInteger("2"));
        existingHoldRip.setReference("reference");
        existingHoldRip.setRIPSchedule(ripSchedule);
        existingRip.setOrderGroupId("1234");
        existingRip.setTransactionSeq(new BigInteger("2"));
        existingRip.setRIPStatus(RIPStatus.ACTIVE);
        existingRip.setOwner(ClientKey.valueOf("existing owner"));
        existingRip.setOrders(orderItems);

        Asset mpAsset = Mockito.mock(Asset.class);
        Mockito.when(mpAsset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        Mockito.when(mpAsset.getAssetClass()).thenReturn(AssetClass.DIVERSIFIED);
        Mockito.when(assetIntegrationService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(mpAsset);

        Asset mfAsset = Mockito.mock(Asset.class);
        Mockito.when(mfAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        Mockito.when(mfAsset.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_SHARES);
        Mockito.when(assetIntegrationService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(mfAsset);

        Mockito.when(cmsService.getDynamicContent(Mockito.anyString(), Mockito.any(String[].class))).thenReturn("content");

        orderItemInit = Mockito.mock(OrderItemInitialiser.class);

        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                if (CodeCategory.ORDER_STATUS.equals(args[0])) {
                    if ("43".equals(args[1])) {
                        return new CodeImpl("43", "ACTIV", "Active", "activ");
                    } else if ("44".equals(args[1])) {
                        return new CodeImpl("44", "SUSP", "Suspended", "susp");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.TRX_BDL_PERIOD.equals(args[0])) {
                    if ("1".equals(args[1])) {
                        return new CodeImpl("1", "W", "Every week", "weekly");
                    } else if ("3".equals(args[1])) {
                        return new CodeImpl("3", "M", "Every month", "mth");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.TRX_ORDER_TYPE.equals(args[0])) {
                    if ("101".equals(args[1])) {
                        return new CodeImpl("101", "BUY", "Buy", "buy");
                    } else if ("102".equals(args[1])) {
                        return new CodeImpl("102", "SELL", "Sell", "sell");
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
    public void toValidateRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toValidateRIPRequest(newRip, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newRip);
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
        Assert.assertEquals(Constants.RIP_ORDER_TYPE, AvaloqGatewayUtil.asExtlString(req.getData().getOrderType()));
    }

    @Test
    public void toSubmitRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toSubmitRIPRequest(newRip, "1234", serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newRip);
        Assert.assertEquals("1234", AvaloqGatewayUtil.asString(req.getData().getRefDocId()));
        Assert.assertEquals(Constants.DO, req.getReq().getExec().getAction().getGenericAction());
        Assert.assertEquals(Constants.RIP_ORDER_TYPE, AvaloqGatewayUtil.asExtlString(req.getData().getOrderType()));
    }

    @Test
    public void tosaveRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toSaveRIPRequest(newRip, "1234", serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newRip);
        Assert.assertEquals("1234", AvaloqGatewayUtil.asString(req.getData().getRefDocId()));
        Assert.assertEquals(Constants.SAVE_RIP, req.getReq().getExec().getAction().getWfcAction());
        Assert.assertEquals(Constants.RIP_ORDER_TYPE, AvaloqGatewayUtil.asExtlString(req.getData().getOrderType()));
    }

    @Test
    public void toSubmitHoldRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toSubmitRIPRequest(existingHoldRip, "1234", serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, existingHoldRip);
        Assert.assertEquals("1234", AvaloqGatewayUtil.asString(req.getData().getRefDocId()));
        Assert.assertEquals(Constants.HOLD_RECUR, req.getReq().getExec().getAction().getWfcAction());

    }

    @Test
    public void toLoadRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toLoadRIPRequest("1234", serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals("1234", AvaloqGatewayUtil.asString(req.getReq().getGet().getDoc()));
    }

    @Test
    public void toSuspendRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toSuspendRIPRequest(existingRip, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(Constants.SUSPEND_RECURRING_TXN, req.getReq().getExec().getAction().getWfcAction());
    }

    @Test
    public void toResumeRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toResumeRIPRequest(existingRip, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(Constants.RESUME_RECURRING_TXN, req.getReq().getExec().getAction().getWfcAction());
    }

    @Test
    public void toCancelRIPRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = ripConverter.toCancelRIPRequest(existingRip, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(Constants.DISCARD_RECURRING_TXN, req.getReq().getExec().getAction().getWfcAction());
    }

    @Test
    public void toLoadRIPResponse_whenSuppliedWithRequest_thenBdlRspMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlRsp ripRsp = new TrxBdlRsp();
        com.btfin.abs.trxservice.trxbdl.v1_0.Data data = new com.btfin.abs.trxservice.trxbdl.v1_0.Data();
        com.btfin.abs.trxservice.base.v1_0.Rsp rsp = new com.btfin.abs.trxservice.base.v1_0.Rsp();
        com.btfin.abs.trxservice.base.v1_0.RspGet rspGet = new com.btfin.abs.trxservice.base.v1_0.RspGet();
        rspGet.setLastTransSeqNr(AvaloqGatewayUtil.createNumberVal(new BigInteger("2")));
        rsp.setGet(rspGet);

        data.setDoc(AvaloqGatewayUtil.createNumberVal("1234"));
        data.setThisExecFirstNtfcn(AvaloqGatewayUtil.createTextVal("Failed RIP due to insufficient funds"));
        data.setRefDocId(AvaloqGatewayUtil.createIdVal("1111"));
        data.setRedoPeriodId(AvaloqGatewayUtil.createIdVal("1"));
        data.setUiWfStatus(AvaloqGatewayUtil.createIdVal("43"));
        com.btfin.abs.trxservice.trxbdl.v1_0.TrxList trxList = new com.btfin.abs.trxservice.trxbdl.v1_0.TrxList();
        com.btfin.abs.trxservice.trxbdl.v1_0.TrxItem trxItem = new com.btfin.abs.trxservice.trxbdl.v1_0.TrxItem();
        trxItem.setAssetId(AvaloqGatewayUtil.createIdVal("111"));
        trxItem.setDirId(AvaloqGatewayUtil.createIdVal("101"));
        trxItem.setQty(AvaloqGatewayUtil.createNumberVal("1000"));
        trxItem.setThisExecFirstNtfcn(AvaloqGatewayUtil.createTextVal("Failed RIP Order Item due to insufficient funds"));
        trxList.getTrxItem().add(trxItem);
        data.setTrxList(trxList);

        ripRsp.setRsp(rsp);
        ripRsp.setData(data);
        RegularInvestmentImpl rip = ripConverter.toLoadRIPResponse("1234", new BigInteger("2"), AccountKey.valueOf("accountId"),
                ripRsp, serviceErrors);
        Assert.assertEquals("1234", rip.getOrderGroupId());
        Assert.assertEquals("1111", rip.getDirectDebitDetails().getReceiptNumber());
        Assert.assertEquals("Failed RIP due to insufficient funds", rip.getFirstNotification());
        Assert.assertEquals("Failed RIP Order Item due to insufficient funds", rip.getOrders().get(0).getFirstNotification());
        Assert.assertEquals(RIPStatus.ACTIVE, rip.getRIPStatus());
        Assert.assertEquals(RIPRecurringFrequency.Weekly, rip.getRIPSchedule().getRecurringFrequency());
    }

    @Test
    public void toLoadRIPResponse_whenFirstNotificationNull_thenBdlRspMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlRsp ripRsp = new TrxBdlRsp();
        com.btfin.abs.trxservice.trxbdl.v1_0.Data data = new com.btfin.abs.trxservice.trxbdl.v1_0.Data();
        com.btfin.abs.trxservice.base.v1_0.Rsp rsp = new com.btfin.abs.trxservice.base.v1_0.Rsp();
        com.btfin.abs.trxservice.base.v1_0.RspGet rspGet = new com.btfin.abs.trxservice.base.v1_0.RspGet();
        rspGet.setLastTransSeqNr(AvaloqGatewayUtil.createNumberVal(new BigInteger("2")));
        rsp.setGet(rspGet);

        data.setDoc(AvaloqGatewayUtil.createNumberVal("1234"));
        data.setThisExecFirstNtfcn(null);
        data.setRefDocId(AvaloqGatewayUtil.createIdVal("1111"));
        data.setRedoPeriodId(AvaloqGatewayUtil.createIdVal("1"));
        data.setUiWfStatus(AvaloqGatewayUtil.createIdVal("43"));
        com.btfin.abs.trxservice.trxbdl.v1_0.TrxList trxList = new com.btfin.abs.trxservice.trxbdl.v1_0.TrxList();
        com.btfin.abs.trxservice.trxbdl.v1_0.TrxItem trxItem = new com.btfin.abs.trxservice.trxbdl.v1_0.TrxItem();
        trxItem.setAssetId(AvaloqGatewayUtil.createIdVal("111"));
        trxItem.setDirId(AvaloqGatewayUtil.createIdVal("101"));
        trxItem.setQty(AvaloqGatewayUtil.createNumberVal("1000"));
        trxItem.setThisExecFirstNtfcn(null);
        trxList.getTrxItem().add(trxItem);
        data.setTrxList(trxList);

        ripRsp.setRsp(rsp);
        ripRsp.setData(data);
        RegularInvestmentImpl rip = ripConverter.toLoadRIPResponse("1234", new BigInteger("2"), AccountKey.valueOf("accountId"),
                ripRsp, serviceErrors);
        Assert.assertEquals("1234", rip.getOrderGroupId());
        Assert.assertEquals("1111", rip.getDirectDebitDetails().getReceiptNumber());
        Assert.assertNull(rip.getFirstNotification());
        Assert.assertNull(rip.getOrders().get(0).getFirstNotification());
        Assert.assertEquals(RIPStatus.ACTIVE, rip.getRIPStatus());
        Assert.assertEquals(RIPRecurringFrequency.Weekly, rip.getRIPSchedule().getRecurringFrequency());
    }

    private void assertBdlReqMatches(TrxBdlReq req, RegularInvestmentImpl rip) {
        Assert.assertEquals(rip.getReference(), AvaloqGatewayUtil.asString(req.getData().getDescription()));
        Assert.assertEquals(rip.getOwner().getId(), AvaloqGatewayUtil.asString(req.getData().getOwnerId()));
        Assert.assertEquals(rip.getRIPSchedule().getRecurringFrequency().getFrequency(),
                AvaloqGatewayUtil.asExtlString(req.getData().getRedoPeriodId()));
        int i = 0;
        for (OrderItem item : rip.getOrders()) {
            Assert.assertEquals(item.getFundsSource().get(0).getKey(), AvaloqGatewayUtil.asString(req.getData().getTrxContId()));
            Assert.assertEquals(item.getOrderType(),
                    AvaloqGatewayUtil.asExtlString(req.getData().getTrxList().getTrxItem().get(i).getDirId()));
            Assert.assertEquals(item.getAmount().doubleValue(),
                    AvaloqGatewayUtil.asBigDecimal(req.getData().getTrxList().getTrxItem().get(i).getQty()).doubleValue(), 0.005);
            i++;
        }
    }

}
