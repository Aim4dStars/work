package com.bt.nextgen.api.ips.service;

import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementDto;
import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementKey;
import com.bt.nextgen.api.ips.model.IpsFeeDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.ips.InvestmentPolicyStatementImpl;
import com.bt.nextgen.service.avaloq.ips.IpsFeeImpl;
import com.bt.nextgen.service.avaloq.ips.IpsTariffBoundaryImpl;
import com.bt.nextgen.service.avaloq.ips.IpsTariffImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsTariff;
import com.bt.nextgen.service.integration.ips.IpsTariffBoundary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentPolicyStatementDtoServiceTest {

    @InjectMocks
    private final InvestmentPolicyStatementDtoServiceImpl ipsDtoService = new InvestmentPolicyStatementDtoServiceImpl();

    @Mock
    InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryService;

    InvestmentPolicyStatementImpl ipsImpl1 = new InvestmentPolicyStatementImpl();
    InvestmentPolicyStatementImpl ipsImpl2 = new InvestmentPolicyStatementImpl();
    Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap1 = new HashMap<>();
    Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap2 = new HashMap<>();

    @Before
    public void setup() throws Exception {

        IpsTariffImpl tariff = new IpsTariffImpl();
        tariff.setTariffFactor(new BigDecimal(0.5));
        List<IpsTariff> tariffList = new ArrayList<>();
        tariffList.add(tariff);

        IpsFeeImpl feeImpl = new IpsFeeImpl();
        feeImpl.setMasterBookKind(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        feeImpl.setBookKind(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        feeImpl.setTariffList(tariffList);
        List<IpsFee> feeList = new ArrayList<>();
        feeList.add(feeImpl);

        ipsImpl1.setApirCode("apirCode");
        ipsImpl1.setCode("flatFee");
        ipsImpl1.setInvestmentName("Inv Name Flat Fee");
        ipsImpl1.setFeeList(feeList);
        ipsImpl1.setInvestmentManagerPersonId("invstPersonId1");

        IpsTariffImpl tariff2 = new IpsTariffImpl();

        IpsTariffBoundaryImpl tariffBnd1 = new IpsTariffBoundaryImpl();
        tariffBnd1.setBoundFrom(BigDecimal.ZERO);
        tariffBnd1.setBoundTo(BigDecimal.ONE);
        tariffBnd1.setTariffFactor(new BigDecimal(0.5));

        IpsTariffBoundaryImpl tariffBnd2 = new IpsTariffBoundaryImpl();
        tariffBnd2.setBoundFrom(BigDecimal.ONE);
        tariffBnd2.setBoundTo(BigDecimal.TEN);
        tariffBnd2.setTariffFactor(BigDecimal.ONE);

        List<IpsTariffBoundary> tariffBndList = new ArrayList<>();
        tariffBndList.add(tariffBnd1);
        tariffBndList.add(tariffBnd2);
        tariff2.setTariffBndList(tariffBndList);

        List<IpsTariff> tariffList2 = new ArrayList<>();
        tariffList2.add(tariff2);

        IpsFeeImpl feeImpl2 = new IpsFeeImpl();
        feeImpl2.setMasterBookKind(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        feeImpl2.setBookKind(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        feeImpl2.setTariffList(tariffList2);
        List<IpsFee> feeList2 = new ArrayList<>();
        feeList2.add(feeImpl2);

        ipsImpl2.setApirCode("apirCode");
        ipsImpl2.setCode("tieredFee");
        ipsImpl2.setInvestmentName("Inv Name");
        ipsImpl2.setFeeList(feeList2);
        ipsImpl2.setInvestmentManagerPersonId("invstPersonId2");

        ipsMap1.put(IpsKey.valueOf("121"), ipsImpl1);
        ipsMap2.put(IpsKey.valueOf("122"), ipsImpl2);

        InvestmentPolicyStatementImpl ipsImpl3 = Mockito.mock(InvestmentPolicyStatementImpl.class);
        Mockito.when(ipsImpl3.getIpsKey()).thenReturn(IpsKey.valueOf("123"));
        Mockito.when(ipsImpl3.getApirCode()).thenReturn("apirCode");
        Mockito.when(ipsImpl3.getCode()).thenReturn("tieredFee");
        Mockito.when(ipsImpl3.getInvestmentName()).thenReturn("Inv Name");
        Mockito.when(ipsImpl3.getFeeList()).thenReturn(null);
        Mockito.when(ipsImpl3.getInvestmentManagerPersonId()).thenReturn("invstPersonId3");
        ipsMap2.put(IpsKey.valueOf("123"), ipsImpl3);
    }

    @Test
    public void testModelFlatFee() {
        Mockito.when(
                ipsIntegrationService.getSelectiveInvestmentPolicyStatements(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(ipsMap1);
        InvestmentPolicyStatementDto ipsDto = ipsDtoService
                .find(new InvestmentPolicyStatementKey("121"), new ServiceErrorsImpl());
        Assert.assertNotNull(ipsDto);

        IpsFeeDto ipsFeeDto = (IpsFeeDto) ipsDto.getFeeList().get(0);
        Assert.assertTrue(ipsFeeDto.getFee().equals(new BigDecimal(50).setScale(2)));

    }

    @Test
    public void testModelTieredFee() {
        Mockito.when(
                ipsIntegrationService.getSelectiveInvestmentPolicyStatements(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenReturn(ipsMap2);
        InvestmentPolicyStatementDto ipsDto = ipsDtoService
                .find(new InvestmentPolicyStatementKey("122"), new ServiceErrorsImpl());
        Assert.assertNotNull(ipsDto);

        IpsFeeDto ipsFeeDto = (IpsFeeDto) ipsDto.getFeeList().get(0);
        Assert.assertTrue(ipsFeeDto.getFee().equals(new BigDecimal(50).setScale(2)));
        Assert.assertTrue(ipsFeeDto.getBoundFrom().equals(BigDecimal.ZERO));
        Assert.assertTrue(ipsFeeDto.getBoundTo().equals(BigDecimal.ONE));
        IpsFeeDto ipsFeeDto2 = (IpsFeeDto) ipsDto.getFeeList().get(1);
        Assert.assertTrue(ipsFeeDto2.getFee().equals(new BigDecimal(100).setScale(2)));
        Assert.assertTrue(ipsFeeDto2.getBoundFrom().equals(BigDecimal.ONE));
        Assert.assertTrue(ipsFeeDto2.getBoundTo().equals(BigDecimal.TEN));

    }

    @Test
    public void testSearch_withDifferentInvestmentManagerId_thenNoResultsReturn() {
        Mockito.when(ipsIntegrationService.getInvestmentPolicyStatements(Mockito.any(ServiceErrors.class))).thenReturn(ipsMap1);

        Mockito.when(userProfileService.getPositionId()).thenReturn("invstPersonId2");
        Mockito.when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        List<InvestmentPolicyStatementDto> ipsDto = ipsDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(ipsDto);
        Assert.assertTrue(ipsDto.isEmpty());
    }

    @Test
    public void testSearch_withDealerGroupManager_thenValidResultsReturn() {
        Mockito.when(ipsIntegrationService.getInvestmentPolicyStatements(Mockito.any(ServiceErrors.class))).thenReturn(ipsMap2);

        Mockito.when(userProfileService.getPositionId()).thenReturn("invstPersonId1");
        Mockito.when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("invstPersonId3"));
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        ModelPortfolioSummary ps = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(ps.getModelKey()).thenReturn(IpsKey.valueOf("123"));
        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(ps));

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        List<InvestmentPolicyStatementDto> ipsDto = ipsDtoService.search(criteriaList, new ServiceErrorsImpl());

        Assert.assertNotNull(ipsDto);
        Assert.assertTrue(ipsDto.size() == 1);
        Assert.assertTrue(ipsDto.get(0).getId().equals("123"));
    }
}
