package com.bt.nextgen.service.avaloq.fees;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.ips.IpsKey;
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
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class FeesScheduleConverterTest {
    @InjectMocks
    private FeesScheduleConverter feesScheduleConverter = new FeesScheduleConverter();

    @InjectMocks
    private FeeScheduleTrxConverter feeScheduleTrxConverter = new FeeScheduleTrxConverter();

    @Mock
    private StaticIntegrationService staticIntegrationService;

    List<SlidingScaleTiers> slidingScaleTiers;

    @Before
    public void setup() {
        Mockito.when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        if (CodeCategory.FEE_TYPE.equals(args[0])) {
                            if ("5826".equals(args[1])) {
                                return new CodeImpl("5826", "AVSR_ADVCONG", "Ongoing advice fee", "btfg$avsr_advcong");
                            } else if ("5832".equals(args[1])) {
                                return new CodeImpl("5832", "DG_ADVCONG", "Licensee advice fee", "btfg$dg_advcong");
                            } else if ("5834".equals(args[1])) {
                                return new CodeImpl("5834", "PLATFORM_ADM", "Administration fee", "btfg$platform_adm");
                            } else if ("562".equals(args[1])) {
                                return new CodeImpl("5834", "TRUSTEE", "Trustee fee", "btfg$trustee_fee");
                            } else if ("551".equals(args[1])) {
                                return new CodeImpl("551", "PORTFOLIO", "Portfolio fee", "btfg$tmp_mngt");
                            } else {
                                return null;
                            }
                        } else if (CodeCategory.COMPONENT_TYPE.equals(args[0])) {
                            if ("1".equals(args[1])) {
                                return new CodeImpl("1", "COMPO_AMT", "Dollar fee component", "compo_amt");
                            } else if ("2".equals(args[1])) {
                                return new CodeImpl("2", "COMPO_PCT", "Percentage fee component", "compo_pct");
                            } else if ("3".equals(args[1])) {
                                return new CodeImpl("3", "COMPO_SLIDESCL", "Sliding scale fee component", "compo_slidescl");
                            } else if ("4".equals(args[1])) {
                                return new CodeImpl("4", "COMPO_MINMAX", "Min/Max fee component", "compo_minmax");
                            } else {
                                return null;
                            }
                        } else if (CodeCategory.FEE_MISC.equals(args[0])) {
                            if ("1".equals(args[1])) {
                                return new CodeImpl("1", "AMT", "Fixed Dollar Amount", "btfg$fixed_amt");
                            } else if ("41".equals(args[1])) {
                                return new CodeImpl("41", "FUA_TAILORED", "Tailored", "btfg$fua_tailored");
                            } else if ("7".equals(args[1])) {
                                return new CodeImpl("7", "FUA_MA", "Managed portfolios", "btfg$fua_clust_ma");
                            } else if ("4".equals(args[1])) {
                                return new CodeImpl("4", "FUA_TD", "Term Deposits", "btfg$fua_clust_td");
                            } else if ("3".equals(args[1])) {
                                return new CodeImpl("3", "FUA_CASH", "Cash", "btfg$fua_clust_cash");
                            } else if ("5".equals(args[1])) {
                                return new CodeImpl("5", "FUA_MF", "Managed Funds", "btfg$fua_clust_mf");
                            } else if ("6".equals(args[1])) {
                                return new CodeImpl("6", "FUA_LS", "Listed Securities", "btfg$fua_clust_ls");
                            } else {
                                return null;
                            }
                        } else if (CodeCategory.ASSET_TYPE.equals(args[0]) || CodeCategory.ADMIN_ASSET_TYPE.equals(args[0])) {
                            if ("505260".equals(args[1])) {
                                return new CodeImpl("505260", "CASH", "Cash", "cash");
                            } else if ("505300".equals(args[1])) {
                                return new CodeImpl("505300", "CASH", "Cash", "cash");
                            } else if ("505261".equals(args[1])) {
                                return new CodeImpl("505261", "TD", "Term deposits", "td");
                            } else if ("505301".equals(args[1])) {
                                return new CodeImpl("505301", "TD", "Term deposits", "td");
                            } else if ("505262".equals(args[1])) {
                                return new CodeImpl("505262", "MF", "Managed funds", "mf");
                            } else if ("505302".equals(args[1])) {
                                return new CodeImpl("505302", "MF", "Managed funds", "mf");
                            } else if ("505264".equals(args[1])) {
                                return new CodeImpl("505264", "MA", "Managed portfolios", "ma");
                            } else if ("505304".equals(args[1])) {
                                return new CodeImpl("505304", "MA", "Managed portfolios", "ma");
                            } else if ("505266".equals(args[1])) {
                                return new CodeImpl("505266", "LS", "Listed Securities", "ls");
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                });
        SlidingScaleTiers slidingScaleTier;
        slidingScaleTiers = new ArrayList<SlidingScaleTiers>();
        slidingScaleTier = new SlidingScaleTiers();
        slidingScaleTier.setLowerBound(new BigDecimal("0"));
        slidingScaleTier.setUpperBound(new BigDecimal("10"));
        slidingScaleTier.setPercent(new BigDecimal(0.5));
        slidingScaleTiers.add(slidingScaleTier);
        slidingScaleTier = new SlidingScaleTiers();
        slidingScaleTier.setLowerBound(new BigDecimal("11"));
        slidingScaleTier.setUpperBound(new BigDecimal("40"));
        slidingScaleTier.setPercent(new BigDecimal(0.10));
        slidingScaleTiers.add(slidingScaleTier);
        slidingScaleTier = new SlidingScaleTiers();
        slidingScaleTier.setLowerBound(new BigDecimal("41"));
        slidingScaleTier.setUpperBound(new BigDecimal("50"));
        slidingScaleTier.setPercent(new BigDecimal(0.7));
        slidingScaleTiers.add(slidingScaleTier);

    }

    @Test
    public void testvalidateFeeSchedule() throws JAXBException, ParserConfigurationException {
        DollarFeesComponent dollarFeeComponent;
        PercentageFeesComponent percentageFeesComponent;
        SlidingScaleFeesComponent slidingScaleFeesComponent;
        List<FeesMiscType> transactionTypes;
        // Setting the request object
        List<FeesSchedule> feesInterfaceList = new ArrayList<FeesSchedule>();

        // Advice fees - Book kind
        FeesSchedule feesInterface = new FeesScheduleImpl();
        feesInterface.setAccountId("55922");
        feesInterface.setType(FeesType.ONGOING_FEE);
        // Ongoing
        List<FeesComponents> feesComponents = new ArrayList<FeesComponents>();
        // Ongoing - Dollar
        dollarFeeComponent = new DollarFeesComponent();
        dollarFeeComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        dollarFeeComponent.setDollar(new BigDecimal(100));
        dollarFeeComponent.setCpiindex(true);
        feesComponents.add(dollarFeeComponent);
        // Ongoing - Percentage
        percentageFeesComponent = new PercentageFeesComponent();
        Map<FeesMiscType, BigDecimal> percentageFeesMap = new HashMap<FeesMiscType, BigDecimal>();
        percentageFeesMap.put(FeesMiscType.PERCENT_TERM_DEPOSIT, new BigDecimal("00.6"));
        percentageFeesComponent.setPercentMap(percentageFeesMap);
        feesComponents.add(percentageFeesComponent);
        // Add ongoing advice fee component to advice fee
        feesInterface.setFeesComponents(feesComponents);
        feesInterface.setType(FeesType.ONGOING_FEE);
        feesInterfaceList.add(feesInterface);

        // Licence fees - Book kind
        feesInterface = new FeesScheduleImpl();
        feesInterface.setType(FeesType.LICENSEE_FEE);
        // Ongoing
        feesComponents = new ArrayList<FeesComponents>();
        // Ongoing - Dollar
        dollarFeeComponent = new DollarFeesComponent();
        dollarFeeComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        dollarFeeComponent.setDollar(new BigDecimal(200));
        dollarFeeComponent.setCpiindex(false);
        feesComponents.add(dollarFeeComponent);
        // Ongoing - Sliding Scale
        slidingScaleFeesComponent = new SlidingScaleFeesComponent();
        slidingScaleFeesComponent.setFeesComponentType(FeesComponentType.SLIDING_SCALE_FEE);
        slidingScaleFeesComponent.setMinFees(new BigDecimal("100"));
        slidingScaleFeesComponent.setMaxFees(new BigDecimal("500"));
        // Sliding Scale Tiers
        slidingScaleFeesComponent.setTiers(slidingScaleTiers);
        // Transaction Types
        transactionTypes = new ArrayList<FeesMiscType>();
        transactionTypes.add(FeesMiscType.PERCENT_CASH);
        transactionTypes.add(FeesMiscType.PERCENT_MANAGED_FUND);
        transactionTypes.add(FeesMiscType.PERCENT_MANAGED_PORTFOLIO);
        transactionTypes.add(FeesMiscType.PERCENT_TERM_DEPOSIT);
        transactionTypes.add(FeesMiscType.PERCENT_SHARE);
        slidingScaleFeesComponent.setTransactionType(transactionTypes);
        feesComponents.add(slidingScaleFeesComponent);
        // Add ongoing advice fee component to advice fee
        feesInterface.setFeesComponents(feesComponents);
        feesInterfaceList.add(feesInterface);
        feesInterfaceList.add(getPortfolioFees());

        com.btfin.abs.trxservice.mdffee.v1_0.MdfFeeReq mdfFeeReq = feeScheduleTrxConverter
                .validateFeeScheduleRequest(feesInterfaceList);

        JAXBContext jaxbContext = JAXBContext.newInstance(mdfFeeReq.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();// dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        marshaller.marshal(mdfFeeReq, doc);

        com.btfin.abs.trxservice.mdffee.v1_0.Data data = mdfFeeReq.getData();
        Assert.assertEquals(data.getBookKindList().getBookKind().get(0).getBookKind().getExtlVal().getVal(), "btfg$avsr_advcong");
        Assert.assertEquals(data.getBookKindList().getBookKind().get(1).getBookKind().getExtlVal().getVal(), "btfg$dg_advcong");
        Assert.assertEquals(data.getBookKindList().getBookKind().get(2).getBookKind().getExtlVal().getVal(), "btfg$tmp_mngt");
        Assert.assertEquals(data.getBookKindList().getBookKind().get(3).getBookKind().getExtlVal().getVal(), "btfg$tmp_mngt");

        Assert.assertEquals(
                data.getBookKindList().getBookKind().get(0).getTariffList().getTariff().get(0).getMisc().getExtlVal().getVal(),
                "btfg$fixed_amt");
        Assert.assertEquals(
                data.getBookKindList().getBookKind().get(0).getTariffList().getTariff().get(1).getFactor().getValue().getVal(),
                new BigDecimal("0.6"));
        Assert.assertEquals(data.getBookKindList().getBookKind().get(1).getTariffList().getTariff().get(1).getBoundList()
                .getTariffBound().get(0).getQty2BoundTo().getVal(), new BigDecimal("10"));

        Assert.assertEquals(data.getBookKindList().getBookKind().get(2).getCont().getVal(), "subaccountId");
        Assert.assertEquals(
                data.getBookKindList().getBookKind().get(2).getTariffList().getTariff().get(0).getFactor().getValue().getVal(),
                BigDecimal.ONE);

        Assert.assertEquals(data.getBookKindList().getBookKind().get(3).getCont().getVal(), "subaccountId2");
        Assert.assertEquals(data.getBookKindList().getBookKind().get(3).getTariffList().getTariff().get(0).getBoundList()
                .getTariffBound().get(0).getQty2BoundTo().getVal(), new BigDecimal("10"));

    }

    private FeesSchedule getPortfolioFees() {
        FeesScheduleImpl fees = new FeesScheduleImpl();
        fees.setFeesType(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        List<FeesComponents> components = new ArrayList<>();
        components.add(new ProductFlatPercentFeesComponent(SubAccountKey.valueOf("subaccountId"), IpsKey.valueOf("ipsId"),
                BigDecimal.ONE));
        components.add(new ProductSlidingScaleFeesComponent(SubAccountKey.valueOf("subaccountId2"), IpsKey.valueOf("ipsId"),
                slidingScaleTiers));
        fees.setFeesComponents(components);
        return fees;

    }

    public void testValidateFeeScheduleResponse() {
        DollarFeesComponent dollarFeeComponent;
        PercentageFeesComponent percentageFeesComponent;
        SlidingScaleFeesComponent slidingScaleFeesComponent;
        Map<FeesMiscType, BigDecimal> percentageFeesMap = new HashMap<FeesMiscType, BigDecimal>();
        com.btfin.abs.trxservice.mdffee.v1_0.MdfFeeRsp mdfFeeRsp = JaxbUtil.unmarshall(
                "/webservices/response/FeeScheduleTransactionResponse_UT.xml",
                com.btfin.abs.trxservice.mdffee.v1_0.MdfFeeRsp.class);
        FeesScheduleTransactionImpl feesScheduleTransactionImpl = feeScheduleTrxConverter.validateFeeScheduleResponse(mdfFeeRsp,
                new ServiceErrorsImpl());
        List<FeesSchedule> feesInterfaceListResponse = feesScheduleTransactionImpl.getFeesScheduleInterfaceList();
        Assert.assertEquals(feesInterfaceListResponse.get(0).getType(), "5825");
        Assert.assertEquals(feesInterfaceListResponse.get(1).getType(), "5850");

        dollarFeeComponent = (DollarFeesComponent) feesInterfaceListResponse.get(0).getFeesComponents().get(0);
        Assert.assertEquals(dollarFeeComponent.getFeesComponentType(), FeesComponentType.DOLLAR_FEE);
        Assert.assertEquals(dollarFeeComponent.getDollar(), new BigDecimal("1"));

        percentageFeesComponent = (PercentageFeesComponent) feesInterfaceListResponse.get(0).getFeesComponents().get(1);
        percentageFeesMap = percentageFeesComponent.getPercentMap();
        Assert.assertEquals(percentageFeesMap.size(), 3);

        slidingScaleFeesComponent = (SlidingScaleFeesComponent) feesInterfaceListResponse.get(1).getFeesComponents().get(1);
        Assert.assertEquals(slidingScaleFeesComponent.getTiers().get(1).getLowerBound(), new BigDecimal("11"));
        Assert.assertEquals(slidingScaleFeesComponent.getTiers().get(2).getUpperBound(), new BigDecimal("50"));
        Assert.assertEquals(slidingScaleFeesComponent.getTransactionType().get(0), "td");
        Assert.assertEquals(slidingScaleFeesComponent.getTransactionType().get(3), "ls");
    }

    @Test
    public void toFeesScheduleModel_whenSuppliedWithRequest_thenReqMatches() throws Exception {
        com.avaloq.abs.screen_rep.hira.btfg$ui_fee_bp_costp.Rep rsp = JaxbUtil.unmarshall(
                "/webservices/response/FeeScheduleDetails_UT.xml", com.avaloq.abs.screen_rep.hira.btfg$ui_fee_bp_costp.Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Assert.assertFalse(serviceErrors.hasErrors());

        List<FeesSchedule> fees = feesScheduleConverter.toFeesScheduleModel(rsp, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(fees);
        Assert.assertEquals(5, fees.size());
        Assert.assertEquals(FeesType.ONGOING_FEE, fees.get(0).getFeesType());
        Assert.assertEquals(FeesType.LICENSEE_FEE, fees.get(1).getFeesType());
        Assert.assertEquals(FeesType.ADMIN_FEE, fees.get(2).getFeesType());
        Assert.assertEquals(FeesType.TRUSTEE_FEE, fees.get(3).getFeesType());
        Assert.assertEquals(FeesType.PORTFOLIO_MANAGEMENT_FEE, fees.get(4).getFeesType());

        for (FeesSchedule fee : fees) {
            for (FeesComponents comp : fee.getFeesComponents()) {
                assertFeeComponentMatches(comp);
            }
        }
    }

    private void assertFeeComponentMatches(FeesComponents comp) {
        if (comp instanceof DollarFeesComponent) {
            Assert.assertNotNull(((DollarFeesComponent) comp).getFeesComponentType());
        } else if (comp instanceof PercentageFeesComponent) {
            Assert.assertNotNull(((PercentageFeesComponent) comp).getPercentMap());
        } else if (comp instanceof SlidingScaleFeesComponent) {
            Assert.assertNotNull(((SlidingScaleFeesComponent) comp).getTiers());
        } else if (comp instanceof FlatPercentFeesComponent) {
            Assert.assertNotNull(((FlatPercentFeesComponent) comp).getRate());
        }

    }
}
