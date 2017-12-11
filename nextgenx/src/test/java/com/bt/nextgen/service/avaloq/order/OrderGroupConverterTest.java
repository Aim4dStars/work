package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PreferenceAction;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.abs.trxservice.base.v1_0.Ovr;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxItem;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxList;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class OrderGroupConverterTest {
    @InjectMocks
    private OrderGroupConverter orderGroupConverter;

    private OrderGroupImpl newGroup;
    private OrderGroupImpl existingGroup;
    private List<ValidationError> warnings;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private CmsService cmsService;

    @Before
    public void setup() {
        newGroup = new OrderGroupImpl();
        newGroup.setReference("reference");
        newGroup.setOwner(ClientKey.valueOf("owner"));
        newGroup.setTransactionSeq(new BigInteger("123456"));

        warnings = new ArrayList<>();
        warnings.add(new ValidationError("avaloqErrorId", "field", "message", ErrorType.WARNING));
        warnings.add(new ValidationError("avaloqErrorId2", "field2", "message2", ErrorType.WARNING));
        newGroup.setWarnings(warnings);

        existingGroup = new OrderGroupImpl();
        existingGroup.setReference("existing reference");
        existingGroup.setOwner(ClientKey.valueOf("existing owner"));
        existingGroup.setOrderGroupId("1234");
        existingGroup.setTransactionSeq(new BigInteger("5687"));
        existingGroup.setWarnings(warnings);

        Pair<String, BigDecimal> allocation = new ImmutablePair<String, BigDecimal>("cashAccount", BigDecimal.ONE);
        List<OrderItem> orderItems = new ArrayList<>();

        OrderItemImpl item = new OrderItemImpl();
        OrderItemSummaryImpl summary = new OrderItemSummaryImpl();

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641960", "buy", AssetType.MANAGED_PORTFOLIO, "1234", summary,
                Collections.singletonList(allocation));
        item.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        ModelPreferenceAction mpa = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.CASH,
                PreferenceAction.SET);
        item.setPreferences(Arrays.asList(mpa));
        List<FeesComponents> components = new ArrayList<>();
        components.add(new FlatPercentFeesComponent(BigDecimal.valueOf(0.1)));
        Map<FeesType, List<FeesComponents>> fees = new HashMap<>();
        fees.put(FeesType.PORTFOLIO_MANAGEMENT_FEE, components);
        item.setFees(fees);
        item.setIncomePreference(IncomePreference.TRANSFER);
        orderItems.add(item);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641961", "buy", AssetType.MANAGED_PORTFOLIO, "2345", summary,
                Collections.singletonList(allocation));
        item.setSubAccountKey(null);
        ModelPreferenceAction mpa2 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.CASH,
                PreferenceAction.SET);
        item.setPreferences(Arrays.asList(mpa2));
        item.setIncomePreference(IncomePreference.REINVEST);
        orderItems.add(item);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641961", "sell", AssetType.MANAGED_PORTFOLIO, "4256", summary,
                Collections.singletonList(allocation));
        item.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        ModelPreferenceAction mpa3 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.PRORATA,
                PreferenceAction.REMV);
        item.setPreferences(Arrays.asList(mpa3));
        orderItems.add(item);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641961", "sell", AssetType.MANAGED_PORTFOLIO, "5678", summary,
                Collections.singletonList(allocation));
        item.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        ModelPreferenceAction mpa4 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.PRORATA,
                PreferenceAction.REMV);
        item.setPreferences(Arrays.asList(mpa4));
        orderItems.add(item);

        // mf orders
        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, "Cash", null, null, null, null);
        item = new OrderItemImpl("641960", "buy", AssetType.MANAGED_FUND, "4567", summary, Collections.singletonList(allocation));
        ModelPreferenceAction mpa5 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.CASH,
                PreferenceAction.SET);
        item.setPreferences(Arrays.asList(mpa5));
        orderItems.add(item);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        item = new OrderItemImpl("641961", "sell", AssetType.MANAGED_FUND, "5678", summary, Collections.singletonList(allocation));
        ModelPreferenceAction mpa6 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.PRORATA,
                PreferenceAction.REMV);
        item.setPreferences(Arrays.asList(mpa6));
        orderItems.add(item);

        // ls orders
        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, "Cash", BigInteger.valueOf(10), BigDecimal.valueOf(10),
                "GFD", PriceType.MARKET);
        item = new OrderItemImpl("641960", "buy", AssetType.SHARE, "4567", summary, Collections.singletonList(allocation));
        ModelPreferenceAction mpa7 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.CASH,
                PreferenceAction.SET);
        item.setPreferences(Arrays.asList(mpa7));
        orderItems.add(item);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, BigInteger.valueOf(10), BigDecimal.valueOf(10),
                "GTC", PriceType.LIMIT);
        item = new OrderItemImpl("641961", "sell", AssetType.SHARE, "5678", summary, Collections.singletonList(allocation));
        ModelPreferenceAction mpa8 = new ModelPreferenceActionImpl(AccountKey.valueOf("110523"), Preference.PRORATA,
                PreferenceAction.REMV);
        item.setPreferences(Arrays.asList(mpa8));
        orderItems.add(item);

        newGroup.setOrders(orderItems);
        existingGroup.setOrders(orderItems);

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

        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                if (CodeCategory.TRX_ORDER_TYPE.equals(args[0])) {
                    if ("101".equals(args[1])) {
                        return new CodeImpl("101", "BUY", "Buy", "buy");
                    } else if ("102".equals(args[1])) {
                        return new CodeImpl("102", "SELL", "Sell", "sell");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.DISTRIBUTION_METHOD.equals(args[0])) {
                    if ("41".equals(args[1])) {
                        return new CodeImpl("41", "DIV_REVST_YES", "Reinvest", "div_revst_yes");
                    } else if ("42".equals(args[1])) {
                        return new CodeImpl("42", "DIV_REVST_NO", "Cash", "div_revst_no");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.EXPIRY_METHOD.equals(args[0])) {
                    if ("1".equals(args[1])) {
                        return new CodeImpl("1", "GFD", "Good for day", "good_for_day");
                    } else if ("2".equals(args[1])) {
                        return new CodeImpl("2", "GTC", "Good till cancelled", "good_till_canc");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.PRICE_TYPE.equals(args[0])) {
                    if ("1".equals(args[1])) {
                        return new CodeImpl("1", "M", "Market", "mkt");
                    } else if ("2".equals(args[1])) {
                        return new CodeImpl("2", "L", "Limit", "lim");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.PREFERENCE_TYPE.equals(args[0])) {
                    if ("1".equals(args[1])) {
                        return new CodeImpl("1", "DO_NOT_HOLD", "Do not hold", "do_not_hold");
                    } else if ("21".equals(args[1])) {
                        return new CodeImpl("21", "CASH", "Cash", "cash");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.PREFERENCE_ACTION.equals(args[0])) {
                    if ("1".equals(args[1])) {
                        return new CodeImpl("1", "SET", "Set Preference", "set");
                    } else if ("2".equals(args[1])) {
                        return new CodeImpl("2", "REMV", "Remove Preference", "remv");
                    } else {
                        return null;
                    }
                } else if (CodeCategory.INCOME_PREFERENCE.equals(args[0])) {
                    if ("660464".equals(args[1])) {
                        return new CodeImpl("660464", "REINVEST", "Reinvest", "reinvest");
                    } else if ("660469".equals(args[1])) {
                        return new CodeImpl("660469", "TRANSFER", "Transfer", "transfer");
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
    public void toOrderValidateRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderValidateRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(newGroup.getTransactionSeq(), AvaloqGatewayUtil.asBigInteger(req.getReq().getValid().getTransSeqNr()));
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
    }

    @Test
    public void toOrderSubmitRequest_whenSuppliedWithRequest_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderSubmitRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.DO, req.getReq().getExec().getAction().getGenericAction());
        Assert.assertEquals(newGroup.getTransactionSeq(), AvaloqGatewayUtil.asBigInteger(req.getReq().getExec().getTransSeqNr()));
    }

    @Test
    public void toOrderValidateRequest_whenSuppliedWithRequestForExistingOrder_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderValidateRequest(existingGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, existingGroup);
        Assert.assertEquals(existingGroup.getTransactionSeq(), AvaloqGatewayUtil.asBigInteger(req.getReq().getValid().getTransSeqNr()));
        Assert.assertEquals(Constants.VALIDATE_EXISTING, req.getReq().getValid().getAction().getWfcAction());
    }

    @Test
    public void toOrderSubmitRequest_whenSuppliedWithRequestForExistingOrder_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderSubmitRequest(existingGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, existingGroup);
        Assert.assertEquals(existingGroup.getTransactionSeq(), AvaloqGatewayUtil.asBigInteger(req.getReq().getExec().getTransSeqNr()));
        Assert.assertEquals(Constants.VALIDATE_EXISTING, req.getReq().getExec().getAction().getWfcAction());
    }

    @Test
    public void toOrderSubmitRequest_whenSuppliedWithWarnings_thenReqExecMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderSubmitRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        List<Ovr> ovr = req.getReq().getExec().getOvrList().getOvr();
        Assert.assertEquals(newGroup.getWarnings().size(), ovr.size());
        int i = 0;
        for (ValidationError warning : newGroup.getWarnings()) {
            Assert.assertEquals(warning.getErrorId(), AvaloqGatewayUtil.asExtlString(ovr.get(i).getOvrId()));
            i++;
        }
    }

    @Test
    public void toOrderSaveRequest_whenSuppliedWithNewRequest_thenBdlReqMatches_andActionIsSaveNew() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderSaveRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.SAVE_NEW, req.getReq().getExec().getAction().getWfcAction());
        Assert.assertEquals(null, req.getReq().getExec().getTransSeqNr());
    }

    @Test
    public void toOrderSaveRequest_whenSuppliedWithExistingRequest_thenBdlReqMatches_andActionIsSaveExisting() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        newGroup.setOrderGroupId("1");
        TrxBdlReq req = orderGroupConverter.toOrderSaveRequest(newGroup, serviceErrors);
        newGroup.setOrderGroupId(null);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.SAVE_EXISTING, req.getReq().getExec().getAction().getWfcAction());
        Assert.assertEquals(newGroup.getTransactionSeq(), AvaloqGatewayUtil.asBigInteger(req.getReq().getExec().getTransSeqNr()));
    }

    @Test
    public void toOrderLoadRequest_whenSuppliedOrderId_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        TrxBdlReq req = orderGroupConverter.toOrderLoadRequest("1234", serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals("1234", AvaloqGatewayUtil.asString(req.getReq().getGet().getDoc()));
    }

    @Test
    public void toOrderDeleteRequest_whenSuppliedOrderId_thenBdlReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        TrxBdlReq req = orderGroupConverter.toOrderDeleteRequest("1234", serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals("1234", AvaloqGatewayUtil.asString(req.getReq().getExec().getDoc()));
        Assert.assertEquals(Constants.DELETE, req.getReq().getExec().getAction().getWfcAction());
        Assert.assertEquals(null, req.getReq().getExec().getTransSeqNr());
    }

    @Test
    public void toTrxList_whenSuppliedWithRequest_thenTrxListReqMatches() throws Exception {
        TrxList req = orderGroupConverter.toTrxList(newGroup.getOrders());
        Assert.assertNotNull(req);
        Assert.assertEquals(8, req.getTrxItem().size());
        int i = 0;
        for (OrderItem item : newGroup.getOrders()) {
            Assert.assertEquals(item.getOrderType(), AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getDirId()));
            Assert.assertEquals(item.getAmount().doubleValue(), AvaloqGatewayUtil.asBigDecimal(req.getTrxItem().get(i).getQty())
                    .doubleValue(), 0.005);
            Assert.assertEquals(item.getAssetId(), AvaloqGatewayUtil.asString(req.getTrxItem().get(i).getAssetId()));

            if (item.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                if (item.getSubAccountKey() != null) {
                    Assert.assertEquals(item.getSubAccountKey().getId(),
                            AvaloqGatewayUtil.asString(req.getTrxItem().get(i).getContId()));
                } else {
                    Assert.assertEquals(item.getFundsSource().get(0).getKey(),
                            AvaloqGatewayUtil.asString(req.getTrxItem().get(i).getContId()));
                }
                Assert.assertEquals("mp", AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getTypeId()));
                Assert.assertEquals(item.getIsFull(), AvaloqGatewayUtil.asBoolean(req.getTrxItem().get(i).getIsFull()));

                if (item.getIncomePreference() != null) {
                    Assert.assertEquals(item.getIncomePreference().getIntlId(),
                            AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getIncomePrefId()));
                } else {
                    Assert.assertEquals(IncomePreference.REINVEST.getIntlId(),
                            AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getIncomePrefId()));
                }

            } else if (item.getAssetType() == AssetType.MANAGED_FUND) {
                Assert.assertEquals(item.getFundsSource().get(0).getKey(),
                        AvaloqGatewayUtil.asString(req.getTrxItem().get(i).getContId()));
                Assert.assertEquals("fd", AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getTypeId()));
                Assert.assertEquals(item.getIsFull(), AvaloqGatewayUtil.asBoolean(req.getTrxItem().get(i).getIsFull()));
                if (!orderGroupConverter.isRedemption(item.getOrderType())) {
                    Assert.assertEquals(
                            item.getDistributionMethod(),
                            DistributionMethod.getDistributionMethod(
                                    AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getSinstrId())).getDisplayName());
                }
            } else if (item.getAssetType() == AssetType.TERM_DEPOSIT) {
                Assert.assertEquals(item.getFundsSource().get(0).getKey(),
                        AvaloqGatewayUtil.asString(req.getTrxItem().get(i).getContId()));
                Assert.assertEquals("fidd", AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getTypeId()));
            } else if (item.getAssetType() == AssetType.SHARE) {
                Assert.assertEquals(item.getFundsSource().get(0).getKey(),
                        AvaloqGatewayUtil.asString(req.getTrxItem().get(i).getContId()));
                Assert.assertEquals("ls", AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getTypeId()));
                Assert.assertEquals(item.getUnits(), AvaloqGatewayUtil.asBigInteger(req.getTrxItem().get(i).getQty()));
                Assert.assertEquals(item.getExpiry(),
                        ExpiryMethod.getExpiryMethod(AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getExpirTypeId())).name());
                Assert.assertEquals(item.getPriceType().getIntlId(),
                        PriceType.getPriceType(AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getExecTypeId())).getIntlId());

                if (!orderGroupConverter.isRedemption(item.getOrderType())) {
                    Assert.assertEquals(
                            item.getDistributionMethod(),
                            DistributionMethod.getDistributionMethod(
                                    AvaloqGatewayUtil.asExtlString(req.getTrxItem().get(i).getSinstrId())).getDisplayName());
                }
            }

            i++;
        }
    }

    private void assertBdlReqMatches(TrxBdlReq req, OrderGroupImpl orderGroup) {
        Assert.assertEquals(orderGroup.getReference(), AvaloqGatewayUtil.asString(req.getData().getDescription()));
        Assert.assertEquals(orderGroup.getOwner().getId(), AvaloqGatewayUtil.asString(req.getData().getOwnerId()));
        int i = 0;
        for (OrderItem item : orderGroup.getOrders()) {
            Assert.assertEquals(item.getFundsSource().get(0).getKey(), AvaloqGatewayUtil.asString(req.getData().getTrxContId()));
            Assert.assertEquals(item.getOrderType(),
                    AvaloqGatewayUtil.asExtlString(req.getData().getTrxList().getTrxItem().get(i).getDirId()));
            Assert.assertEquals(item.getAmount().doubleValue(),
                    AvaloqGatewayUtil.asBigDecimal(req.getData().getTrxList().getTrxItem().get(i).getQty()).doubleValue(), 0.005);
            i++;
        }
    }

    @Test
    public void toOrderValidateResponse_whenSuppliedWithResponse_thenModelMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderValidateRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
    }

    @Test
    public void toOrderValidateResponse_whenResponseHasUIErrors_thenValidationException() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderValidateRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
    }

    @Test
    public void toOrderValidateResponse_whenSuppliedWithResponse_t() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderValidateRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
    }

    @Test
    public void toOrderValidateResponse_whenResponseHasAppErrors_thenValidationException() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlReq req = orderGroupConverter.toOrderValidateRequest(newGroup, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertBdlReqMatches(req, newGroup);
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
    }

    @Test
    public void testToValidateOrderResponse_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        TrxBdlRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderGroupResponse_UT.xml", TrxBdlRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        OrderGroupImpl result = orderGroupConverter.toValidateOrderResponse("1125", new BigInteger("123456"),
                AccountKey.valueOf("accountKey"), rsp, serviceErrors);
        Assert.assertNotNull(result);
        Assert.assertFalse(serviceErrors.hasErrors());
        assertOrderGroupMatches(result, 2);
    }

    @Test
    public void testToSubmitOrderResponse_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        TrxBdlRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderGroupSubmitResponse_UT.xml", TrxBdlRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        OrderGroupImpl result = orderGroupConverter.toSubmitOrderResponse("1125", new BigInteger("123456"),
                AccountKey.valueOf("accountKey"), rsp, serviceErrors);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTransactionSeq(), new BigInteger("2"));
        Assert.assertFalse(serviceErrors.hasErrors());
        assertOrderGroupMatches(result, 2);
    }

    @Test
    public void testToDeleteOrderResponse_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        TrxBdlRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderGroupResponse_UT.xml", TrxBdlRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        orderGroupConverter.processDeleteResponse(rsp, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void toOrderLoadResponse_whenSuppliedWithResponse_thenOrderGroupMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        TrxBdlRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderGroupTrxLoadResponse_UT.xml", TrxBdlRsp.class);
        OrderGroupImpl result = orderGroupConverter.toLoadOrderResponse("1125", null, AccountKey.valueOf("accountKey"), rsp,
                serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(result.getTransactionSeq(), new BigInteger("2"));
        assertOrderGroupMatches(result, 0);
    }

    @Test
    public void testToValidateOrderResponse_whenSuppliedWithValidationError_thenObjectThenValidationError() throws Exception {
        TrxBdlRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderGroupResponseErr_UT.xml", TrxBdlRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        try {
            orderGroupConverter.toValidateOrderResponse("1125", new BigInteger("123456"), AccountKey.valueOf("accountKey"), rsp,
                    serviceErrors);
            Assert.assertEquals(rsp.getRsp().getValid().getErrList().getErr().get(0).getErrMsg(),
                    "The client has not enough cash for trading !");
            Assert.fail();
        } catch (ValidationException e) {
        }
    }

    private void assertOrderGroupMatches(OrderGroupImpl group, int expectedWarnings) {
        group.getLastUpdateDate();
        Assert.assertEquals("1125", group.getOrderGroupId());
        Assert.assertEquals("29502", group.getOwner().getId());
        Assert.assertNotNull(group.getReference());
        Assert.assertEquals(expectedWarnings, group.getWarnings().size());
        Assert.assertEquals("Cash", group.getOrders().get(0).getDistributionMethod());
    }

    @Test
    public void toOrderItem_whenSuppliedWithRequest_thenTrxListReqMatches() throws Exception {
        TrxBdlRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderGroupResponse_UT.xml", TrxBdlRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Assert.assertFalse(serviceErrors.hasErrors());

        List<OrderItem> orderItems = new ArrayList<>();
        for (TrxItem item : rsp.getData().getTrxList().getTrxItem()) {
            orderItems.add(orderGroupConverter.toOrderItem(item, serviceErrors));
        }

        Assert.assertEquals(3, orderItems.size());
        Assert.assertEquals("buy", orderItems.get(0).getOrderType());
        Assert.assertEquals("41876", orderItems.get(0).getAssetId());
        Assert.assertEquals("Cash", orderItems.get(0).getDistributionMethod());
        Assert.assertEquals(Preference.CASH, orderItems.get(0).getPreferences().get(0).getPreference());
        Assert.assertEquals(PreferenceAction.SET, orderItems.get(0).getPreferences().get(0).getAction());
        Assert.assertEquals(IncomePreference.REINVEST, orderItems.get(0).getIncomePreference());

        Assert.assertEquals("GFD", orderItems.get(2).getExpiry());
        Assert.assertEquals(PriceType.LIMIT, orderItems.get(2).getPriceType());
        Assert.assertEquals(BigDecimal.valueOf(13.8), orderItems.get(2).getPrice());
        Assert.assertEquals(BigInteger.valueOf(1000), orderItems.get(2).getUnits());
        Assert.assertEquals(Preference.PRORATA, orderItems.get(2).getPreferences().get(0).getPreference());
        Assert.assertEquals(PreferenceAction.REMV, orderItems.get(2).getPreferences().get(0).getAction());
        Assert.assertEquals("013040", orderItems.get(2).getBankClearNumber());
        Assert.assertEquals("213456789", orderItems.get(2).getPayerAccount());
        Assert.assertEquals(IncomePreference.TRANSFER, orderItems.get(2).getIncomePreference());

    }

}
