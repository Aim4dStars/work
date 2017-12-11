package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceTriggerDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceTriggerDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.TriggerStatus;
import com.bt.nextgen.service.avaloq.modelportfolio.rebalance.ModelPortfolioRebalanceImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetailsImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerImpl;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTrigger;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelRebalanceStatus;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioRebalanceSummaryDtoServiceTest {

    @InjectMocks
    private final ModelPortfolioRebalanceDtoServiceImpl dtoServiceImpl = new ModelPortfolioRebalanceDtoServiceImpl();

    @Mock
    private ModelPortfolioRebalanceIntegrationService modelPortfolioRebalanceIntegrationService;

    @Mock
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryIntegrationService;

    @Mock
    private InvestmentPolicyStatementIntegrationService investmentPolicyStatementIntegrationService;

    @Mock
    private ModelPortfolioHelper helper;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap;

    private Code staticCodeIpsStatus;

    private Code staticCodeTriggerType;

    private TriggerStatus triggerStatus;

    private ModelPortfolioSummary model;

    @Before
    public void setup() throws Exception {
        ipsMap = new HashMap<IpsKey, InvestmentPolicyStatementInterface>();
        ipsMap.put(IpsKey.valueOf("111746"), buildIpsDetail("111746", "INVCODE", "Investment Name"));

        staticCodeIpsStatus = buildStaticCode("1", "Open");

        staticCodeTriggerType = buildStaticCode("1", "Inflows");

        triggerStatus = TriggerStatus.NEW;

        Mockito.when(
                staticIntegrationService.loadCodeByName(eq(CodeCategory.REBALANCE_TRIGGER_GROUP), any(String.class),
                        any(ServiceErrors.class))).thenReturn(staticCodeTriggerType);

        Mockito.when(staticIntegrationService.loadCode(eq(CodeCategory.IPS_STATUS), any(String.class), any(ServiceErrors.class)))
                .thenReturn(staticCodeIpsStatus);

        when(staticIntegrationService.loadCode(eq(CodeCategory.REBALANCE_TRIGGER_GROUP), any(String.class),
                any(ServiceErrors.class))).thenReturn(staticCodeTriggerType);

        Mockito.when(staticIntegrationService.loadCode(eq(CodeCategory.REBALANCE_TRIGGER_GROUP), any(String.class),
                any(ServiceErrors.class))).thenReturn(staticCodeTriggerType);

        Mockito.when(investmentPolicyStatementIntegrationService.getInvestmentPolicyStatements(Mockito.anyList(),
                        any(ServiceErrors.class))).thenReturn(ipsMap);

        Mockito.when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        Mockito.when(userProfileService.isAdviser()).thenReturn(true);

        model = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(model.getModelKey()).thenReturn(IpsKey.valueOf("111746"));
        Mockito.when(model.getStatus()).thenReturn(IpsStatus.OPEN);
        Mockito.when(model.getAccountType()).thenReturn(ModelType.INVESTMENT);
        Mockito.when(model.getLastRebalanceDate()).thenReturn(new DateTime("2017-01-01"));
        Mockito.when(model.getLastRebalanceUser()).thenReturn("John");
        Mockito.when(model.getNumAccounts()).thenReturn(10);
        Mockito.when(model.getHasScanTrigger()).thenReturn(true);

        ModelPortfolioSummary model2 = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(model2.getStatus()).thenReturn(IpsStatus.PENDING);

        ModelPortfolioSummary model3 = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(model3.getStatus()).thenReturn(IpsStatus.CLOSED_TO_NEW);

        ModelPortfolioSummary model4 = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(model4.getStatus()).thenReturn(IpsStatus.SUSPENDED);

        ModelPortfolioSummary model5 = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(model5.getStatus()).thenReturn(IpsStatus.TERMINATED);

        List<ModelPortfolioSummary> modelList = new ArrayList<ModelPortfolioSummary>();
        modelList.add(model);
        modelList.add(model2);
        modelList.add(model3);
        modelList.add(model4);
        modelList.add(model5);

        when(modelPortfolioSummaryIntegrationService.loadModels(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(modelList);
    }

    @Test
    public void testDealer() {

        ModelPortfolioRebalanceTriggerDetails rebalanceTriggerDetails1 = buildModelPortfolioRebalanceTriggerDetails("1",
                new DateTime("2015-05-04"), Integer.valueOf(10), Integer.valueOf(10));

        ModelPortfolioRebalanceTriggerDetails rebalanceTriggerDetails2 = buildModelPortfolioRebalanceTriggerDetails("2",
                new DateTime("2015-05-04"), Integer.valueOf(10), Integer.valueOf(10));

        List<ModelPortfolioRebalanceTriggerDetails> triggerDetailsList1 = new ArrayList<ModelPortfolioRebalanceTriggerDetails>();
        triggerDetailsList1.add(rebalanceTriggerDetails1);

        List<ModelPortfolioRebalanceTriggerDetails> triggerDetailsList2 = new ArrayList<ModelPortfolioRebalanceTriggerDetails>();
        triggerDetailsList2.add(rebalanceTriggerDetails2);

        ModelPortfolioRebalanceTrigger rebalanceTrigger1 = buildModelPortfolioRebalanceTrigger("1", "1",
                new DateTime("2015-08-20"), Integer.valueOf(10), Integer.valueOf(0), TriggerStatus.NEW, triggerDetailsList1);

        ModelPortfolioRebalanceTrigger rebalanceTrigger2 = buildModelPortfolioRebalanceTrigger("2", "1",
                new DateTime("2015-05-04"), Integer.valueOf(10), Integer.valueOf(10), TriggerStatus.ORDERS_READY,
                triggerDetailsList2);

        List<ModelPortfolioRebalanceTrigger> triggersList = new ArrayList<ModelPortfolioRebalanceTrigger>();
        triggersList.add(rebalanceTrigger1);
        triggersList.add(rebalanceTrigger2);

        ModelPortfolioRebalance rebalance = buildModelPortfolioRebalance("111746", "9512", new DateTime("2015-09-29"), "userName",
                Integer.valueOf(3), Integer.valueOf(0), triggersList, triggerStatus);

        ModelPortfolioRebalance uncachedIpsRebalance = buildModelPortfolioRebalance("12391239", "9512",
                new DateTime("2015-09-29"), "userName", Integer.valueOf(3), Integer.valueOf(0), triggersList, triggerStatus);

        List<ModelPortfolioRebalance> rebalanceList = new ArrayList<ModelPortfolioRebalance>();
        rebalanceList.add(rebalance);
        rebalanceList.add(uncachedIpsRebalance);

        Mockito.when(modelPortfolioRebalanceIntegrationService.loadModelPortfolioRebalances(any(BrokerKey.class),
                any(ServiceErrors.class))).thenReturn(rebalanceList);

        Mockito.when(helper.getCurrentBroker(Mockito.any(ServiceErrors.class)))
                .thenReturn(BrokerKey.valueOf("DealerID"));

        List<ModelPortfolioRebalanceDto> rebalanceDtos = dtoServiceImpl.findAll(null);
        assertNotNull(rebalanceDtos);
        assertEquals(1, rebalanceDtos.size());

        ModelPortfolioRebalanceDto rebalanceDto = rebalanceDtos.get(0);
        assertEquals(rebalance.getUserName(), rebalanceDto.getUserName());
        assertEquals(rebalance.getLastRebalanceDate(), rebalanceDto.getLastRebalanceDate());
        assertEquals(staticCodeIpsStatus.getName(), rebalanceDto.getStatus());
        assertEquals(rebalance.getTotalAccountsCount(), rebalanceDto.getTotalAccountsCount());
        assertEquals(rebalance.getTotalRebalancesCount(), rebalanceDto.getTotalRebalancesCount());
        assertEquals(ipsMap.get(IpsKey.valueOf("111746")).getCode(), rebalanceDto.getModelCode());
        assertEquals(ipsMap.get(IpsKey.valueOf("111746")).getInvestmentName(), rebalanceDto.getModelName());

        assertEquals("INVCODE", rebalanceDto.getModelCode());
        assertEquals("Investment Name", rebalanceDto.getModelName());
        assertEquals(ModelType.INVESTMENT.getDisplayValue(), rebalanceDto.getAccountType());

        ModelPortfolioRebalanceTriggerDto rebalanceTriggerDto1 = rebalanceDto.getRebalanceTriggers().get(0);

        assertEquals(rebalanceTrigger1.getMostRecentTriggerDate(), rebalanceTriggerDto1.getMostRecentTriggerDate());
        assertEquals(rebalanceTrigger1.getTotalAccountsCount(), rebalanceTriggerDto1.getTotalAccountsCount());
        assertEquals(rebalanceTrigger1.getTotalRebalancesCount(), rebalanceTriggerDto1.getTotalRebalancesCount());
        assertEquals(triggerStatus.getDescription(), rebalanceTriggerDto1.getTriggerStatus());
        assertEquals(staticCodeTriggerType.getName(), rebalanceTriggerDto1.getTriggerType());

        ModelPortfolioRebalanceTriggerDetailsDto rebalanceTriggerDetailsDto1 = rebalanceTriggerDto1.getRebalanceTriggerDetails()
                .get(0);

        assertEquals(rebalanceTriggerDetails1.getTotalAccountsCount(), rebalanceTriggerDetailsDto1.getImpactedAccountsCount());
        assertEquals(rebalanceTriggerDetails1.getTranasactionDate(), rebalanceTriggerDetailsDto1.getLastUpdateDate());
        assertEquals(rebalanceTriggerDetails1.getTrigger(), rebalanceTriggerDetailsDto1.getTrigger());

    }

    @Test
    public void testDealer_whenNoRebalanceAvailable_thenModelSummaryCreated() {

        List<ModelPortfolioRebalance> rebalanceList = new ArrayList<ModelPortfolioRebalance>();

        Mockito.when(
                modelPortfolioRebalanceIntegrationService.loadModelPortfolioRebalances(any(BrokerKey.class),
                        any(ServiceErrors.class))).thenReturn(rebalanceList);

        Mockito.when(helper.getCurrentBroker(Mockito.any(ServiceErrors.class)))
                .thenReturn(BrokerKey.valueOf("DealerID"));

        List<ModelPortfolioRebalanceDto> rebalanceDtos = dtoServiceImpl.findAll(null);
        assertNotNull(rebalanceDtos);
        assertEquals(1, rebalanceDtos.size());

        ModelPortfolioRebalanceDto rebalanceDto = rebalanceDtos.get(0);
        assertEquals(new ModelPortfolioKey(model.getModelKey().getId()), rebalanceDto.getKey());
        assertEquals(model.getLastRebalanceUser(), rebalanceDto.getUserName());
        assertEquals(model.getLastRebalanceDate(), rebalanceDto.getLastRebalanceDate());
        assertEquals(model.getStatus().getName(), rebalanceDto.getStatus());
        assertEquals(Integer.valueOf(10), rebalanceDto.getTotalAccountsCount());
        assertEquals(Integer.valueOf(0), rebalanceDto.getTotalRebalancesCount());
        assertEquals(ModelRebalanceStatus.COMPLETE, rebalanceDto.getRebalanceStatus());
        assertEquals(model.getAccountType(), ModelType.forName(rebalanceDto.getAccountType()));

        assertEquals(ipsMap.get(IpsKey.valueOf("111746")).getCode(), rebalanceDto.getModelCode());
        assertEquals(ipsMap.get(IpsKey.valueOf("111746")).getInvestmentName(), rebalanceDto.getModelName());

        assertEquals("INVCODE", rebalanceDto.getModelCode());
        assertEquals("Investment Name", rebalanceDto.getModelName());
    }

    @Test
    public void testInvestmentManager() {

        ModelPortfolioRebalance rebalance = buildModelPortfolioRebalance("111746", "9512", new DateTime("2015-09-29"), "userName",
                Integer.valueOf(3), Integer.valueOf(0), null, triggerStatus);
        List<ModelPortfolioRebalance> rebalanceList = new ArrayList<ModelPortfolioRebalance>();
        rebalanceList.add(rebalance);

        when(modelPortfolioRebalanceIntegrationService.loadModelPortfolioRebalances(any(BrokerKey.class),
                any(ServiceErrors.class))).thenReturn(rebalanceList);

        List<ModelPortfolioRebalanceDto> rebalanceDtos = dtoServiceImpl.findAll(null);
        assertNotNull(rebalanceDtos);

    }

    private ModelPortfolioRebalance buildModelPortfolioRebalance(String ipsId, String ipsStatus, DateTime lastRebalanceDate,
            String userName, Integer totalAccountsCount, Integer totalRebalancesCount,
            List<ModelPortfolioRebalanceTrigger> rebalanceTriggers, TriggerStatus rebalanceTriggerStatuses) {

        ModelPortfolioRebalanceImpl rebalanceImpl = new ModelPortfolioRebalanceImpl();
        rebalanceImpl.setIpsKey(IpsKey.valueOf(ipsId));
        rebalanceImpl.setIpsStatus(ipsStatus);
        rebalanceImpl.setLastRebalanceDate(lastRebalanceDate);
        rebalanceImpl.setUserName(userName);
        rebalanceImpl.setTotalAccountsCount(totalAccountsCount);
        rebalanceImpl.setTotalRebalancesCount(totalRebalancesCount);
        rebalanceImpl.setRebalanceTriggers(rebalanceTriggers);

        return rebalanceImpl;
    }

    private ModelPortfolioRebalanceTrigger buildModelPortfolioRebalanceTrigger(String rebalanceTriggerId, String triggerType,
            DateTime mostRecentTriggerDate, Integer totalAccountsCount, Integer totalRebalancesCount, TriggerStatus status,
            List<ModelPortfolioRebalanceTriggerDetails> rebalanceTriggerDocs) {

        ModelPortfolioRebalanceTriggerImpl rebalanceTrigger = new ModelPortfolioRebalanceTriggerImpl();
        rebalanceTrigger.setMostRecentTriggerDate(mostRecentTriggerDate);
        rebalanceTrigger.setRebalanceGroupDocs(rebalanceTriggerDocs);
        rebalanceTrigger.setTotalRebalancesCount(totalRebalancesCount);
        rebalanceTrigger.setTriggerType(triggerType);
        rebalanceTrigger.setStatus(status);
        return rebalanceTrigger;
    }

    private ModelPortfolioRebalanceTriggerDetails buildModelPortfolioRebalanceTriggerDetails(String trigger,
            DateTime tranasactionDate, Integer totalAccountsCount, Integer totalRebalancesCount) {

        ModelPortfolioRebalanceTriggerDetailsImpl rebalanceTriggerDetails = new ModelPortfolioRebalanceTriggerDetailsImpl();
        rebalanceTriggerDetails.setTrigger(trigger);
        rebalanceTriggerDetails.setTranasactionDate(tranasactionDate);
        rebalanceTriggerDetails.setTotalAccountsCount(totalAccountsCount);
        rebalanceTriggerDetails.setTotalRebalancesCount(totalRebalancesCount);
        return rebalanceTriggerDetails;
    }

    private Code buildStaticCode(final String id, final String name) {
        final Code code = Mockito.mock(Code.class);
        Mockito.when(code.getCodeId()).thenReturn(id);
        Mockito.when(code.getName()).thenReturn(name);
        return code;
    }

    private InvestmentPolicyStatementInterface buildIpsDetail(final String ipsId, final String symbolCode,
            final String investmentName) {
        InvestmentPolicyStatementInterface detail = new InvestmentPolicyStatementInterface() {

            @Override
            public IpsKey getIpsKey() {
                return IpsKey.valueOf(ipsId);
            }

            @Override
            public String getInvestmentName() {
                return investmentName;
            }

            @Override
            public String getCode() {
                return symbolCode;
            }

            @Override
            public void setIpsKey(IpsKey ipsId) {
            }

            @Override
            public String getApirCode() {
                return null;
            }

            @Override
            public String getAssetClassId() {
                return null;
            }

            @Override
            public String getInvestmentStyleId() {
                return null;
            }

            @Override
            public BigDecimal getPercentage() {
                return null;
            }

            @Override
            public BigDecimal getMinInitInvstAmt() {
                return null;
            }

            @Override
            public String getInvestmentManagerPersonId() {
                return null;
            }

            @Override
            public Boolean getTaxAssetDomicile() {
                return null;
            }

            @Override
            public List<IpsFee> getFeeList() {

                return null;
            }

            @Override
            public BigDecimal getWeightedIcr() {
                return null;
            }
        };
        return detail;
    }

}
