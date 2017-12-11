package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioRebalanceSubmitDtoServiceTest {
    @InjectMocks
    private final ModelPortfolioRebalanceDtoServiceImpl dtoService = new ModelPortfolioRebalanceDtoServiceImpl();

    @Mock
    private ModelPortfolioRebalanceIntegrationService modelPortfolioRebalanceIntegrationService;

    @Mock
    private InvestmentPolicyStatementIntegrationService investmentPolicyStatementIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioSummaryIntegrationService modelPortfolioIntegrationService;

    @Mock
    private ModelPortfolioHelper helper;

    private Map<IpsKey, InvestmentPolicyStatementInterface> ispMap;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Before
    public void setup() {
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);
        Mockito.when(userProfileService.isEmulating()).thenReturn(true);

        ispMap = new HashMap<IpsKey, InvestmentPolicyStatementInterface>();
        ispMap.put(IpsKey.valueOf("111746"), Mockito.mock(InvestmentPolicyStatementInterface.class));
        ispMap.put(IpsKey.valueOf("loadedKey"), Mockito.mock(InvestmentPolicyStatementInterface.class));

        Code code = Mockito.mock(Code.class);

        when(staticIntegrationService.loadCode(eq(CodeCategory.IPS_STATUS), any(String.class), any(ServiceErrors.class)))
                .thenReturn(code);
        Mockito.when(helper.getCurrentBroker(Mockito.any(ServiceErrors.class)))
                .thenReturn(BrokerKey.valueOf("brokerKey"));

    }

    @Test
    public void whenValidRequest_thenUpdateSentToIntegrationTier() {

        ModelPortfolioRebalanceDto dto = new ModelPortfolioRebalanceDto(new ModelPortfolioKey("key"), "scan");
        Mockito.when(modelPortfolioRebalanceIntegrationService.updateModelPortfolioRebalance(Mockito.any(BrokerKey.class),
                Mockito.any(IpsKey.class), Mockito.any(RebalanceAction.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioRebalance>() {

                    @Override
                    public ModelPortfolioRebalance answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(BrokerKey.valueOf("brokerKey"), invocation.getArguments()[0]);
                        Assert.assertEquals(IpsKey.valueOf("key"), invocation.getArguments()[1]);
                        Assert.assertEquals(RebalanceAction.SCAN, invocation.getArguments()[2]);

                        ModelPortfolioRebalance result = Mockito.mock(ModelPortfolioRebalance.class);
                        Mockito.when(result.getIpsKey()).thenReturn(IpsKey.valueOf("loadedKey"));
                        return result;
                    }
                });

        when(investmentPolicyStatementIntegrationService.getInvestmentPolicyStatements(Mockito.anyList(),
                any(ServiceErrors.class))).thenReturn(ispMap);

        ModelPortfolioSummary summary = Mockito.mock(ModelPortfolioSummary.class);
        Mockito.when(summary.getModelKey()).thenReturn(IpsKey.valueOf("key"));
        Mockito.when(summary.getAccountType()).thenReturn(ModelType.INVESTMENT);
        List<ModelPortfolioSummary> summaries = new ArrayList<>();
        summaries.add(summary);
        when(modelPortfolioIntegrationService.loadModels(Mockito.any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(summaries);

        dto = dtoService.submit(dto, null);
        Assert.assertEquals(dto.getKey().getModelId(), "loadedKey");

    }

    private Code buildStaticCode(final String id, final String name) {
        final Code code = Mockito.mock(Code.class);
        Mockito.when(code.getCodeId()).thenReturn(id);
        Mockito.when(code.getName()).thenReturn(name);
        return code;
    }

}
