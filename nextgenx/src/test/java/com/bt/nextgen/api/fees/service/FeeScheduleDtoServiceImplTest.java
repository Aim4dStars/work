package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.fees.model.DollarFeeDto;
import com.bt.nextgen.api.fees.model.FeeScheduleDto;
import com.bt.nextgen.api.fees.model.FeesComponentDto;
import com.bt.nextgen.api.fees.model.FeesScheduleTrxnDto;
import com.bt.nextgen.api.fees.model.FeesTypeDto;
import com.bt.nextgen.api.fees.model.FeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.FlatPercentageFeeDto;
import com.bt.nextgen.api.fees.model.InvestmentMgmtFeesDto;
import com.bt.nextgen.api.fees.model.IpsFeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.PercentageFeeDto;
import com.bt.nextgen.api.fees.model.SlidingScaleFeeDto;
import com.bt.nextgen.api.fees.model.SlidingScaleFeeTierDto;
import com.bt.nextgen.api.fees.validation.FeesScheduleDtoErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.fees.AdminPercentageFeeComponent;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesMiscType;
import com.bt.nextgen.service.avaloq.fees.FeesScheduleImpl;
import com.bt.nextgen.service.avaloq.fees.FeesScheduleTransactionImpl;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.GlobalFeesComponent;
import com.bt.nextgen.service.avaloq.fees.PercentageFeeMetadata;
import com.bt.nextgen.service.avaloq.fees.PercentageFeesComponent;
import com.bt.nextgen.service.avaloq.fees.ProductSlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.fees.FeesScheduleIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.SubAccountIdentifier;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class FeeScheduleDtoServiceImplTest {
    @InjectMocks
    private FeeScheduleDtoServiceImpl dtoServiceImpl;

    @Mock
    private StaticIntegrationService staticService;

    @Mock
    private FeesScheduleIntegrationService feesScheduleIntegrationService;

    @Mock
    private FeesScheduleDtoErrorMapper feesScheduleDtoErrorMapper;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private InvestmentPolicyStatementIntegrationService investmentPolicyStatementIntegrationService;

    String accountId;
    ServiceErrors serviceErrors;

    List<FeesSchedule> interfaceList = new ArrayList<FeesSchedule>();
    DollarFeesComponent dollarComponent = new DollarFeesComponent();
    PercentageFeesComponent percentComponent = new PercentageFeesComponent();
    FlatPercentFeesComponent flatPercentComponent = new FlatPercentFeesComponent();
    SlidingScaleFeesComponent slidingComponent = new SlidingScaleFeesComponent();
    AdminPercentageFeeComponent percentAdminComponent = new AdminPercentageFeeComponent();
    List<FeesComponents> ongoingCompList = new ArrayList<FeesComponents>();
    List<FeesComponents> licenseeCompList = new ArrayList<FeesComponents>();
    List<FeesComponents> portfolioCompList = new ArrayList<FeesComponents>();
    List<FeesComponents> wrapAdvantageCompList = new ArrayList<FeesComponents>();
    List<FeesComponents> advanceMfCompList = new ArrayList<FeesComponents>();
    List<FeesComponents> contributionFeeList = new ArrayList<FeesComponents>();
    List<FeesComponents> adminFeesCompList = new ArrayList<FeesComponents>();
    List<ValidationError> warnings = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        accountId = "BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098";
        Mockito.when(
                staticService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<Object>() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();

                        if ("1".equals(args[1])) {
                            return new CodeImpl("1", "component", "Dollar fee component");
                        } else if ("2".equals(args[1])) {
                            return new CodeImpl("2", "component", "Percentage fee component");
                        } else if ("3".equals(args[1])) {
                            return new CodeImpl("3", "component", "Sliding-scale fee component");
                        } else if ("505260".equals(args[1])) {
                            return new CodeImpl("505260", "asset", "Cash");
                        } else if ("505261".equals(args[1])) {
                            return new CodeImpl("505261", "asset", "Term deposits");
                        } else if ("505262".equals(args[1])) {
                            return new CodeImpl("505262", "asset", "Managed funds");
                        } else if ("505264".equals(args[1])) {
                            return new CodeImpl("505264", "asset", "Managed portfolios");
                        } else if ("505265".equals(args[1])) {
                            return new CodeImpl("505265", "asset", "Listed Security");
                        } else if ("7".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("7", "percent", "Managed portfolios");
                            impl.setIntlId("btfg$fua_clust_ma");
                            return impl;
                        } else if ("4".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("4", "percent", "Term deposits");
                            impl.setIntlId("btfg$fua_clust_td");
                            return impl;
                        } else if ("3".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("3", "percent", "Cash");
                            impl.setIntlId("btfg$fua_clust_cash");
                            return impl;
                        } else if ("5".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("5", "percent", "Managed Funds");
                            impl.setIntlId("btfg$fua_clust_mf");
                            return impl;
                        } else if ("6".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("6", "percent", "listedSecurity");
                            impl.setIntlId("btfg$fua_clust_ls");
                            return impl;
                        } else if ("5826".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("5826", "AVSR_ADVCONG", "Ongoing advice fee");
                            impl.setIntlId("btfg$avsr_advcong");
                            return impl;
                        } else if ("5832".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("5832", "DG_ADVCONG", "Licensee advice fee");
                            impl.setIntlId("btfg$dg_advcong");
                            return impl;
                        } else if ("5834".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("5834", "PLATFORM_ADM", "Administration fee");
                            impl.setIntlId("btfg$platform_adm");
                            return impl;
                        } else if ("582".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("582", "WRAP_ADVTG", "Wrap Advantage - Client Rebate");
                            impl.setIntlId("btfg$wrap_advtg_mf_rebate");
                            return impl;
                        } else if ("583".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("583", "ADV_MNGD_FUND_REBATE", "Advance Managed Fund Rebate");
                            impl.setIntlId("btfg$advn_mf_rebate");
                            return impl;
                        } else if ("777".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("777", "CONTRIBUTION_FEE", "Contribution fee");
                            impl.setIntlId("btfg$cost_contri");
                            return impl;
                        } else if ("1".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("1", "COMPO_AMT", "Dollar fee component");
                            impl.setIntlId("compo_amt");
                            return impl;
                        } else if ("2".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("2", "COMPO_PCT", "Percentage fee component");
                            impl.setIntlId("compo_pct");
                            return impl;
                        } else if ("4".equals(args[1])) {
                            CodeImpl impl = new CodeImpl("4", "COMPO_MINMAX", "Min/Max fee component");
                            impl.setIntlId("compo_minmax");
                            return impl;
                        } else {
                            return null;
                        }
                    }
                });

        InvestmentPolicyStatementInterface ips = Mockito.mock(InvestmentPolicyStatementInterface.class);
        Mockito.when(ips.getCode()).thenReturn("code");
        Mockito.when(ips.getInvestmentName()).thenReturn("investmentName");
        Mockito.when(ips.getIpsKey()).thenReturn(IpsKey.valueOf("ipsId"));
        Mockito.when(investmentPolicyStatementIntegrationService.loadInvestmentPolicyStatement(Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(ips));

        accountId = "BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098";
        FeesSchedule ongoingFees = new FeesScheduleImpl();
        FeesSchedule licenseeFees = new FeesScheduleImpl();
        FeesSchedule portfolioFees = new FeesScheduleImpl();
        FeesSchedule wrapAdvantageFees = new FeesScheduleImpl();
        FeesSchedule advanceMfFees = new FeesScheduleImpl();
        FeesSchedule contributionFees = new FeesScheduleImpl();

        dollarComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        dollarComponent.setDollar(new BigDecimal("100"));
        dollarComponent.setCpiindex(true);
        dollarComponent.setIndexation(new Date());

        percentComponent.setFeesComponentType(FeesComponentType.PERCENTAGE_FEE);
        Map<FeesMiscType, BigDecimal> percentMap = new HashMap<FeesMiscType, BigDecimal>();
        percentMap.put(FeesMiscType.PERCENT_CASH, new BigDecimal("0.01"));
        percentMap.put(FeesMiscType.PERCENT_MANAGED_FUND, new BigDecimal("0.02"));
        percentMap.put(FeesMiscType.PERCENT_MANAGED_PORTFOLIO, new BigDecimal("0.03"));
        percentMap.put(FeesMiscType.PERCENT_TERM_DEPOSIT, new BigDecimal("0.04"));
        percentMap.put(FeesMiscType.PERCENT_SHARE, new BigDecimal("0.04"));
        percentComponent.setPercentMap(percentMap);

        slidingComponent.setFeesComponentType(FeesComponentType.SLIDING_SCALE_FEE);
        slidingComponent.setMinFees(new BigDecimal("1"));
        slidingComponent.setMaxFees(new BigDecimal("5"));

        List<FeesMiscType> transactionTypes = new ArrayList<FeesMiscType>();
        transactionTypes.add(FeesMiscType.PERCENT_CASH);
        transactionTypes.add(FeesMiscType.PERCENT_MANAGED_FUND);
        transactionTypes.add(FeesMiscType.PERCENT_MANAGED_PORTFOLIO);
        transactionTypes.add(FeesMiscType.PERCENT_TERM_DEPOSIT);
        transactionTypes.add(FeesMiscType.PERCENT_SHARE);
        slidingComponent.setTransactionType(transactionTypes);
        List<SlidingScaleTiers> tierList = new ArrayList<SlidingScaleTiers>();
        SlidingScaleTiers tier = new SlidingScaleTiers();
        tier.setLowerBound(new BigDecimal("10000"));
        tier.setUpperBound(new BigDecimal("100000"));
        tier.setPercent(new BigDecimal("0.05"));
        tierList.add(tier);
        slidingComponent.setTiers(tierList);

        ongoingCompList.add(dollarComponent);
        ongoingCompList.add(percentComponent);
        licenseeCompList.add(dollarComponent);
        licenseeCompList.add(slidingComponent);

        ProductSlidingScaleFeesComponent portfolioSlidingComponent = new ProductSlidingScaleFeesComponent(
                SubAccountKey.valueOf("subaccount"), IpsKey.valueOf("ipsId"), tierList);
        portfolioCompList.add(portfolioSlidingComponent);

        serviceErrors = new ServiceErrorsImpl();
        ongoingFees.setType(FeesType.ONGOING_FEE);
        ongoingFees.setFeesComponents(ongoingCompList);
        interfaceList.add(ongoingFees);
        licenseeFees.setType(FeesType.LICENSEE_FEE);
        licenseeFees.setFeesComponents(licenseeCompList);
        interfaceList.add(licenseeFees);

        portfolioFees.setType(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        portfolioFees.setFeesComponents(portfolioCompList);
        interfaceList.add(portfolioFees);

        flatPercentComponent.setRate(new BigDecimal("0.01"));
        wrapAdvantageCompList.add(flatPercentComponent);
        wrapAdvantageFees.setType(FeesType.WRAP_ADVANTAGE_REBATE);
        wrapAdvantageFees.setFeesComponents(wrapAdvantageCompList);
        interfaceList.add(wrapAdvantageFees);

        advanceMfCompList.add(flatPercentComponent);
        advanceMfFees.setType(FeesType.ADVANCE_MANAGED_FUND_REBATE);
        advanceMfFees.setFeesComponents(wrapAdvantageCompList);
        interfaceList.add(advanceMfFees);

        // contribution fees
        PercentageFeesComponent employerPercentComponent = new PercentageFeesComponent();
        Map<FeesMiscType, BigDecimal> employerPercentMap = new HashMap<FeesMiscType, BigDecimal>();
        employerPercentMap.put(FeesMiscType.EMPLOYER_CONTRIBUTION, new BigDecimal("0.01"));
        employerPercentComponent.setPercentMap(employerPercentMap);
        contributionFeeList.add(employerPercentComponent);
        PercentageFeesComponent personalPercentComponent = new PercentageFeesComponent();
        Map<FeesMiscType, BigDecimal> persponalPercentMap = new HashMap<FeesMiscType, BigDecimal>();
        persponalPercentMap.put(FeesMiscType.ONEOFF_PERSONAL_CONTRIBUTION, new BigDecimal("0.02"));
        personalPercentComponent.setPercentMap(persponalPercentMap);
        contributionFeeList.add(personalPercentComponent);
        PercentageFeesComponent spousePercentComponent = new PercentageFeesComponent();
        Map<FeesMiscType, BigDecimal> spousePercentMap = new HashMap<FeesMiscType, BigDecimal>();
        spousePercentMap.put(FeesMiscType.ONEOFF_SPOUSE_CONTRIBUTION, new BigDecimal("0.03"));
        spousePercentComponent.setPercentMap(spousePercentMap);
        contributionFeeList.add(spousePercentComponent);
        contributionFees.setType(FeesType.CONTRIBUTION_FEE);
        contributionFees.setFeesComponents(contributionFeeList);
        interfaceList.add(contributionFees);

        percentAdminComponent.setFeesComponentType(FeesComponentType.PERCENTAGE_FEE);
        Map<FeesMiscType, PercentageFeeMetadata> percentageFeesMap = new HashMap<>();
        PercentageFeeMetadata metaData = new PercentageFeeMetadata();
        metaData.setIsTailored(true);
        percentageFeesMap.put(FeesMiscType.PERCENT_MANAGED_FUND, metaData);
        percentAdminComponent.setPercentMap(percentageFeesMap);

        GlobalFeesComponent globalFeesComponent = new GlobalFeesComponent();
        globalFeesComponent.setFeesComponentType(FeesComponentType.GLOBAL_FEE);

        adminFeesCompList.add(dollarComponent);
        adminFeesCompList.add(percentAdminComponent);
        adminFeesCompList.add(slidingComponent);
        adminFeesCompList.add(globalFeesComponent);
    }

    @Test
    public void testGetFeesScheduleComponents() {
        Mockito.when(feesScheduleIntegrationService.getFees(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(interfaceList);

        FeeScheduleDto dtoList = dtoServiceImpl.find(new AccountKey(accountId), new FailFastErrorsImpl());

        assertNotNull(dtoList);
        assertNotNull(dtoList.getFees().get(0));
    }

    @Test
    public void testGetFeesScheduleComponents_whenAccount_returnIpsList() {
        WrapAccountDetail account = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(account.getAdviserKey()).thenReturn(BrokerKey.valueOf("adviser key"));

        Mockito.when(accountService.loadWrapAccountDetail(
                Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        Mockito.when(feesScheduleIntegrationService.getFees(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(interfaceList);

        FeeScheduleDto dtoList = dtoServiceImpl.find(new AccountKey(accountId), new FailFastErrorsImpl());

        assertNotNull(dtoList);
        assertNotNull(dtoList.getFees().get(0));
    }

    @Test
    public void testValidateFeeSchedule() {
        FeesScheduleTransactionImpl transactionImpl = new FeesScheduleTransactionImpl();
        transactionImpl.setFeesScheduleInterfaceList(interfaceList);
        transactionImpl.setValidationErrors(warnings);

        FeeScheduleDto feeScheduleDtoReq = new FeeScheduleDto();
        FeeScheduleDto feeScheduleDtoRes = new FeeScheduleDto();
        FeesScheduleTrxnDto trxDto = new FeesScheduleTrxnDto();

        FeesTypeTrxnDto onGoingFees = new FeesTypeTrxnDto();
        FeesTypeTrxnDto licenseeFees = new FeesTypeTrxnDto();
        List<FeesTypeTrxnDto> contributionFees = new ArrayList<FeesTypeTrxnDto>();

        DollarFeeDto dollarOngoing = new DollarFeeDto();
        PercentageFeeDto percentOngoing = new PercentageFeeDto();
        SlidingScaleFeeDto slidingOngoing = new SlidingScaleFeeDto();

        DollarFeeDto dollarLicensee = new DollarFeeDto();
        PercentageFeeDto percentLicensee = new PercentageFeeDto();
        SlidingScaleFeeDto slidingLicensee = new SlidingScaleFeeDto();
        FlatPercentageFeeDto percentContribution = new FlatPercentageFeeDto();

        dollarOngoing.setAmount(new BigDecimal("1"));
        dollarOngoing.setCpiindex(true);
        dollarOngoing.setName("ongoingadvicefeedollarfee");

        percentOngoing.setCash(new BigDecimal("1"));
        percentOngoing.setManagedFund(new BigDecimal("1"));
        percentOngoing.setManagedPortfolio(new BigDecimal("1"));
        percentOngoing.setTermDeposit(new BigDecimal("1"));
        percentOngoing.setShare(new BigDecimal("1"));
        percentOngoing.setName("ongoingadvicefeepercentagefee");

        slidingOngoing.setCash(true);
        slidingOngoing.setManagedFund(true);
        slidingOngoing.setManagedPortfolio(true);
        slidingOngoing.setShare(true);
        slidingOngoing.setTermDeposit(true);
        slidingOngoing.setMinimumFee(new BigDecimal("1"));
        slidingOngoing.setMaximumFee(new BigDecimal("5"));
        slidingOngoing.setName("ongoingadvicefeeslidingscalefee");

        List<SlidingScaleFeeTierDto> tierList = new ArrayList<SlidingScaleFeeTierDto>();
        SlidingScaleFeeTierDto tier = new SlidingScaleFeeTierDto();
        tier.setLowerBound(new BigDecimal("2"));
        tier.setUpperBound(new BigDecimal("3"));
        tier.setPercentage(new BigDecimal("1"));
        tierList.add(tier);
        slidingOngoing.setSlidingScaleFeeTier(tierList);

        onGoingFees.setDollarFee(dollarOngoing);
        onGoingFees.setPercentageFee(percentOngoing);
        onGoingFees.setSlidingScaleFee(slidingOngoing);

        dollarLicensee.setAmount(new BigDecimal("1"));
        dollarLicensee.setCpiindex(true);
        dollarLicensee.setName("licenseeadvicefeedollarfee");

        percentLicensee.setCash(new BigDecimal("1"));
        percentLicensee.setManagedFund(new BigDecimal("1"));
        percentLicensee.setManagedPortfolio(new BigDecimal("1"));
        percentLicensee.setShare(new BigDecimal("1"));
        percentLicensee.setTermDeposit(new BigDecimal("1"));
        percentLicensee.setName("licenseeadvicefeepercentagefee");

        slidingLicensee.setCash(true);
        slidingLicensee.setManagedFund(true);
        slidingLicensee.setManagedPortfolio(true);
        slidingLicensee.setShare(true);
        slidingLicensee.setTermDeposit(true);
        slidingLicensee.setMinimumFee(new BigDecimal("1"));
        slidingLicensee.setMaximumFee(new BigDecimal("5"));
        slidingLicensee.setName("licenseeadvicefeeslidingscalefee");

        List<SlidingScaleFeeTierDto> tierLicenseeList = new ArrayList<SlidingScaleFeeTierDto>();
        SlidingScaleFeeTierDto licTier = new SlidingScaleFeeTierDto();
        licTier.setLowerBound(new BigDecimal("2"));
        licTier.setUpperBound(new BigDecimal("3"));
        licTier.setPercentage(new BigDecimal("1"));
        tierLicenseeList.add(tier);
        slidingLicensee.setSlidingScaleFeeTier(tierLicenseeList);

        licenseeFees.setDollarFee(dollarLicensee);
        licenseeFees.setPercentageFee(percentLicensee);
        licenseeFees.setSlidingScaleFee(slidingLicensee);

        FeesTypeTrxnDto employerContribution = new FeesTypeTrxnDto();
        percentContribution.setRate(new BigDecimal("0.1"));
        percentContribution.setName("employercontribution");
        percentContribution.setLabel("Percentage fee component");
        employerContribution.setFlatPercentageFee(percentContribution);
        contributionFees.add(employerContribution);

        trxDto.setLicenseeFees(licenseeFees);
        trxDto.setOnGoingFees(onGoingFees);
        trxDto.setContributionFees(contributionFees);

        feeScheduleDtoReq.setKey(new AccountKey(accountId));
        feeScheduleDtoReq.setTransactionDto(trxDto);

        Mockito.when(feesScheduleIntegrationService.validateFeeSchedule(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(transactionImpl);

        feeScheduleDtoRes = dtoServiceImpl.executeAvaloqOperation(feeScheduleDtoReq, serviceErrors, Constants.VALIDATE);

        assertNotNull(feeScheduleDtoRes);
        assertNotNull(feeScheduleDtoRes.getFees());
    }

    @Test
    public void getPortfolioFees() {
        List<IpsFeesTypeTrxnDto> portfolioFees = new ArrayList<>();

        IpsFeesTypeTrxnDto percentFee = new IpsFeesTypeTrxnDto();
        percentFee.setIpsId("123");
        percentFee.setSubaccountId("8975340C52293ACA551D41B56123621391E8E00144A15AD3");
        percentFee.setPercentage(BigDecimal.valueOf(10));
        percentFee.setComponentType("PERCENTAGE_FEE");
        portfolioFees.add(percentFee);

        List<SlidingScaleFeeTierDto> tierList = new ArrayList<SlidingScaleFeeTierDto>();
        SlidingScaleFeeTierDto tier = new SlidingScaleFeeTierDto();
        tier.setLowerBound(new BigDecimal("2"));
        tier.setUpperBound(new BigDecimal("3"));
        tier.setPercentage(new BigDecimal("1"));
        tierList.add(tier);

        IpsFeesTypeTrxnDto slidingFee = new IpsFeesTypeTrxnDto();
        slidingFee.setIpsId("123");
        slidingFee.setSubaccountId("8975340C52293ACA551D41B56123621391E8E00144A15AD3");
        slidingFee.setSlidingScaleFeeTier(tierList);
        slidingFee.setComponentType("SLIDING_SCALE_FEE");
        portfolioFees.add(slidingFee);

        FeesSchedule schedule = dtoServiceImpl.getPortfolioFeesInterface(new AccountKey(accountId), portfolioFees);

        Assert.assertEquals(2, schedule.getFeesComponents().size());
        Assert.assertEquals(FeesComponentType.PERCENTAGE_FEE, schedule.getFeesComponents().get(0).getFeesComponentType());
        Assert.assertEquals(BigDecimal.valueOf(0.1).setScale(6),
                ((FlatPercentFeesComponent) schedule.getFeesComponents().get(0)).getRate());
        Assert.assertEquals("123", ((IpsIdentifier) schedule.getFeesComponents().get(0)).getIpsKey().getId());
        Assert.assertEquals("706405", ((SubAccountIdentifier) schedule.getFeesComponents().get(0)).getSubAccountKey().getId());

        Assert.assertEquals(FeesComponentType.SLIDING_SCALE_FEE, schedule.getFeesComponents().get(1).getFeesComponentType());
    }

    @Test
    public void testToFeesScheduleDto_valueMatches_whenFeesScheduleMap_passed() {
        List<FeesTypeDto> feeScheduleList = dtoServiceImpl.toFeesScheduleDto(interfaceList, new FailFastErrorsImpl());
        assertNotNull(feeScheduleList);
        Assert.assertEquals(5, feeScheduleList.size());
        Assert.assertEquals(2, feeScheduleList.get(0).getFeesComponent().size());
        Assert.assertEquals(2, feeScheduleList.get(1).getFeesComponent().size());

        Assert.assertEquals("Dollar fee component", feeScheduleList.get(0).getFeesComponent().get(0).getLabel());
        Assert.assertEquals("Percentage fee component", feeScheduleList.get(0).getFeesComponent().get(1).getLabel());
        Assert.assertEquals("Sliding scale fee component", feeScheduleList.get(1).getFeesComponent().get(1).getLabel());

        DollarFeesComponent dollarComp = (DollarFeesComponent) interfaceList.get(0).getFeesComponents().get(0);
        DollarFeeDto dollarDto = (DollarFeeDto) feeScheduleList.get(0).getFeesComponent().get(0);
        Assert.assertEquals(dollarComp.getDollar(), dollarDto.getAmount());

        PercentageFeesComponent perComp = (PercentageFeesComponent) interfaceList.get(0).getFeesComponents().get(1);
        PercentageFeeDto perDto = (PercentageFeeDto) feeScheduleList.get(0).getFeesComponent().get(1);
        Assert.assertEquals(perComp.getPercentMap().get(FeesMiscType.PERCENT_CASH).multiply(new BigDecimal(100)),
                perDto.getCash());
        Assert.assertEquals(perComp.getPercentMap().get(FeesMiscType.PERCENT_MANAGED_FUND).multiply(new BigDecimal(100)),
                perDto.getManagedFund());
        Assert.assertEquals(perComp.getPercentMap().get(FeesMiscType.PERCENT_MANAGED_PORTFOLIO).multiply(new BigDecimal(100)),
                perDto.getManagedPortfolio());
        Assert.assertEquals(perComp.getPercentMap().get(FeesMiscType.PERCENT_TERM_DEPOSIT).multiply(new BigDecimal(100)),
                perDto.getTermDeposit());
        Assert.assertEquals(perComp.getPercentMap().get(FeesMiscType.PERCENT_SHARE).multiply(new BigDecimal(100)),
                perDto.getShare());

        SlidingScaleFeesComponent slidingComp = (SlidingScaleFeesComponent) interfaceList.get(1).getFeesComponents().get(1);
        SlidingScaleFeeDto slidingDto = (SlidingScaleFeeDto) feeScheduleList.get(1).getFeesComponent().get(1);
        Assert.assertTrue(slidingDto.isManagedFund());
        Assert.assertTrue(slidingDto.isManagedPortfolio());
        Assert.assertTrue(slidingDto.isShare());
        Assert.assertEquals(1, slidingComp.getTiers().size());
        Assert.assertEquals(slidingComp.getTiers().get(0).getLowerBound(),
                slidingDto.getSlidingScaleFeeTier().get(0).getLowerBound());
        Assert.assertEquals(slidingComp.getTiers().get(0).getUpperBound(),
                slidingDto.getSlidingScaleFeeTier().get(0).getUpperBound());
        Assert.assertEquals(slidingComp.getTiers().get(0).getPercent().multiply(new BigDecimal(100)),
                slidingDto.getSlidingScaleFeeTier().get(0).getPercentage());

        Assert.assertEquals("Wrap advantage rebate",
                ((FlatPercentageFeeDto) feeScheduleList.get(2).getFeesComponent().get(0)).getName());
        Assert.assertEquals("Advance managed fund rebate",
                ((FlatPercentageFeeDto) feeScheduleList.get(3).getFeesComponent().get(0)).getName());
        Assert.assertEquals("Contribution fee", feeScheduleList.get(4).getType());
    }

    @Test
    public void testToFeesComponentDto_valueMatches_whenFeesComponentMap_passed() {
        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toFeesComponentDto(licenseeCompList,
                Constants.LICENSEE_FEE_LABEL, new ServiceErrorsImpl());
        Assert.assertEquals(2, componentDtoList.size());
    }

    @Test
    public void testToDollarFeeDto_valueMatches_whenDollarFeesComponentMap_passed() {
        DollarFeeDto dollarDto = new DollarFeeDto();
        dtoServiceImpl.toDollarFeeDto(dollarComponent, dollarDto);
        assertNotNull(dollarDto);
    }

    @Test
    public void testToPercentageFeeDto_valueMatches_whenPercentageFeesComponentMap_passed() {
        PercentageFeeDto percentDto = new PercentageFeeDto();
        dtoServiceImpl.toPercentageFeeDto(percentComponent, percentDto);
        assertNotNull(percentDto);
        Assert.assertEquals(percentComponent.getPercentMap().get(FeesMiscType.PERCENT_CASH).multiply(new BigDecimal(100)),
                percentDto.getCash());
        Assert.assertEquals(percentComponent.getPercentMap().get(FeesMiscType.PERCENT_MANAGED_FUND).multiply(new BigDecimal(100)),
                percentDto.getManagedFund());
        Assert.assertEquals(
                percentComponent.getPercentMap().get(FeesMiscType.PERCENT_MANAGED_PORTFOLIO).multiply(new BigDecimal(100)),
                percentDto.getManagedPortfolio());
        Assert.assertEquals(percentComponent.getPercentMap().get(FeesMiscType.PERCENT_TERM_DEPOSIT).multiply(new BigDecimal(100)),
                percentDto.getTermDeposit());
        Assert.assertEquals(percentComponent.getPercentMap().get(FeesMiscType.PERCENT_SHARE).multiply(new BigDecimal(100)),
                percentDto.getShare());
    }

    @Test
    public void testToSlidingScaleFeeDto_valueMatches_whenSlidingFeesComponentMap_passed() {
        SlidingScaleFeeDto slidingDto = new SlidingScaleFeeDto();
        dtoServiceImpl.toSlidingScaleFeeDto(slidingComponent, slidingDto, new ServiceErrorsImpl());
        assertNotNull(slidingDto);
        Assert.assertTrue(slidingDto.isManagedFund());
        Assert.assertTrue(slidingDto.isManagedPortfolio());
        Assert.assertTrue(slidingDto.isShare());
    }

    @Test
    public void testGetTiers() {
        List<SlidingScaleFeeTierDto> tiers = dtoServiceImpl.getTiers(slidingComponent);
        Assert.assertNotNull(tiers);
    }

    @Test
    public void testPortfolioManagementFee() {
        List<InvestmentMgmtFeesDto> result = dtoServiceImpl.getPortfolioMgmtFee(interfaceList, new FailFastErrorsImpl());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FeesType.PORTFOLIO_MANAGEMENT_FEE, result.get(0).getFeeType());
        Assert.assertEquals("investmentName", result.get(0).getInvestmentName());
        Assert.assertEquals("code", result.get(0).getCode());
    }

    @Test
    public void testToFlatPercentFeesComponentDto_WhenFlatPercentageFeeComponent_ThenFeesComponentDtoReturned() {
        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toFlatFeesComponentDto(wrapAdvantageCompList,
                Constants.WRAP_ADVANTAGE_REBATE, new ServiceErrorsImpl());
        Assert.assertEquals(1, componentDtoList.size());
        Assert.assertEquals("Wrap advantage rebate", ((FlatPercentageFeeDto) componentDtoList.get(0)).getName());
        Assert.assertEquals(new BigDecimal("1.00"), ((FlatPercentageFeeDto) componentDtoList.get(0)).getRate());
    }

    @Test
    public void testToFlatPercentFeesComponentDto_WhenNullFlatPercentageFeeComponent_ThenFeesComponentDtoReturned() {
        List<FeesComponents> advantageCompList = null;
        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toFlatFeesComponentDto(advantageCompList,
                Constants.WRAP_ADVANTAGE_REBATE, new ServiceErrorsImpl());
        Assert.assertEquals(0, componentDtoList.size());
    }

    @Test
    public void testToFlatPercentFeesComponentDto_WhenEmptyFlatPercentageFeeComponent_ThenFeesComponentDtoReturned() {
        List<FeesComponents> advantageCompList = new ArrayList<FeesComponents>();
        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toFlatFeesComponentDto(advantageCompList,
                Constants.WRAP_ADVANTAGE_REBATE, new ServiceErrorsImpl());
        Assert.assertEquals(0, componentDtoList.size());
    }

    @Test
    public void testToFlatPercentFeesComponentDto_WhenNotFlatPercentageFeeComponent_ThenFeesComponentDtoReturned() {
        List<FeesComponents> advantageCompList = new ArrayList<FeesComponents>();
        DollarFeesComponent advDollarComponent = new DollarFeesComponent();
        advDollarComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        advDollarComponent.setDollar(new BigDecimal("100"));
        advDollarComponent.setCpiindex(true);
        advDollarComponent.setIndexation(new Date());
        advantageCompList.add(advDollarComponent);

        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toFlatFeesComponentDto(advantageCompList,
                Constants.WRAP_ADVANTAGE_REBATE, new ServiceErrorsImpl());
        Assert.assertEquals(1, componentDtoList.size());
    }

    @Test
    public void testToFeesScheduleDto_whenNullContributions_thenFeesScheduleMapReturned() {
        interfaceList.remove(5);
        List<FeesTypeDto> feeScheduleList = dtoServiceImpl.toFeesScheduleDto(interfaceList, new FailFastErrorsImpl());
        assertNotNull(feeScheduleList);
        Assert.assertEquals(4, feeScheduleList.size());
    }

    @Test
    public void testValidateFeeSchedule_whenNullContributions_thenPassed() {
        FeesScheduleTransactionImpl transactionImpl = new FeesScheduleTransactionImpl();
        transactionImpl.setFeesScheduleInterfaceList(interfaceList);
        transactionImpl.setValidationErrors(warnings);
        FeeScheduleDto feeScheduleDtoReq = new FeeScheduleDto();
        FeeScheduleDto feeScheduleDtoRes = new FeeScheduleDto();
        FeesScheduleTrxnDto trxDto = new FeesScheduleTrxnDto();
        FeesTypeTrxnDto onGoingFees = new FeesTypeTrxnDto();
        DollarFeeDto dollarOngoing = new DollarFeeDto();
        dollarOngoing.setAmount(new BigDecimal("1"));
        dollarOngoing.setCpiindex(true);
        dollarOngoing.setName("ongoingadvicefeedollarfee");
        onGoingFees.setDollarFee(dollarOngoing);
        trxDto.setOnGoingFees(onGoingFees);

        feeScheduleDtoReq.setKey(new AccountKey(accountId));
        feeScheduleDtoReq.setTransactionDto(trxDto);

        Mockito.when(feesScheduleIntegrationService.validateFeeSchedule(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(transactionImpl);

        feeScheduleDtoRes = dtoServiceImpl.executeAvaloqOperation(feeScheduleDtoReq, serviceErrors, Constants.VALIDATE);

        assertNotNull(feeScheduleDtoRes);
        assertNotNull(feeScheduleDtoRes.getFees());
    }

    @Test
    public void testGetContributionFeesInterface_whenNullFlatPecentContribution_thenFeesScheduleMapReturned() {
        List<FeesTypeTrxnDto> contributionFees = new ArrayList<FeesTypeTrxnDto>();
        FeesTypeTrxnDto employerContribution = new FeesTypeTrxnDto();
        DollarFeeDto dollar = new DollarFeeDto();
        dollar.setAmount(new BigDecimal("100"));
        dollar.setName("employercontribution");
        dollar.setLabel("Dollar fee component");
        employerContribution.setDollarFee(dollar);
        FeesTypeTrxnDto personalContribution = new FeesTypeTrxnDto();
        contributionFees.add(personalContribution);

        contributionFees.add(employerContribution);
        AccountKey key = new AccountKey(EncodedString.fromPlainText("36846").toString());
        FeesSchedule feeSchedule = dtoServiceImpl.getContributionFeesInterface(key, contributionFees);
        assertNotNull(feeSchedule);
    }

    @Test
    public void testToDollarFeesModel_whenDollarContributionNameEmpty_thenNullNameReturned() {
        DollarFeeDto dollar = new DollarFeeDto();
        dollar.setAmount(new BigDecimal("100"));
        dollar.setLabel("Dollar fee component");
        DollarFeesComponent feeComp = dtoServiceImpl.toDollarFeesModel(dollar);
        assertNotNull(feeComp);
        assertNull(feeComp.getName());
    }

    @Test
    public void testToAdminFeesComponentDto_adminFeeToggleOff_thenFeesComponentsReturned() throws Exception {
        System.setProperty("feature.adminfeeslidingscale", "false");
        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toAdminFeesComponentDto(adminFeesCompList,
                Constants.ADMIN_FEE_LABEL, new ServiceErrorsImpl());
        Assert.assertEquals(4, componentDtoList.size());
    }

    @Test
    public void testToAdminFeesComponentDto_adminFeeToggleOn_thenFeesComponentsReturned1() throws Exception {
        System.setProperty("feature.adminfeeslidingscale", "true");
        List<FeesComponentDto> componentDtoList = dtoServiceImpl.toAdminFeesComponentDto(adminFeesCompList,
                Constants.ADMIN_FEE_LABEL, new ServiceErrorsImpl());
        Assert.assertEquals(4, componentDtoList.size());
    }
}
