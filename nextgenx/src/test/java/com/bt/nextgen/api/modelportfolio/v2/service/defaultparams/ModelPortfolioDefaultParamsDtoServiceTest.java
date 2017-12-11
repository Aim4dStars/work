package com.bt.nextgen.api.modelportfolio.v2.service.defaultparams;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.PreferredPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.TailoredPortfolioDefaultParamsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.dealergroupparams.DealerGroupParams;
import com.bt.nextgen.service.avaloq.dealergroupparams.DealerGroupParamsIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioDefaultParamsDtoServiceTest {

    @InjectMocks
    private ModelPortfolioDefaultParamsDtoServiceImpl defaultParamsDtoServiceImpl;

    @Mock
    private DealerGroupParamsIntegrationService dgParamsIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Test
    public void testFindTailoredPortfolioDefaults_whenParamsReturned_thenDtoConstructed() {
        DealerGroupParams params = Mockito.mock(DealerGroupParams.class);
        Mockito.when(params.getMinimumCashAllocationPercentage()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(params.getMinimumInitialInvestmentAmount()).thenReturn(BigDecimal.valueOf(5000));

        Mockito.when(
                dgParamsIntegrationService.loadCustomerAccountObjects(Mockito.any(DealerParameterKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(params));

        Broker dealerBroker = Mockito.mock(Broker.class);
        Mockito.when(dealerBroker.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        DealerParameterKey key = new DealerParameterKey(ModelType.INVESTMENT.getCode(),
                ModelPortfolioType.TAILORED.getIntlId(), dealerBroker);

        TailoredPortfolioDefaultParamsDto paramsDto = (TailoredPortfolioDefaultParamsDto) defaultParamsDtoServiceImpl.find(key,
                new ServiceErrorsImpl());

        Assert.assertNotNull(paramsDto);
        Assert.assertEquals(BigDecimal.valueOf(2), paramsDto.getMinimumCashAllocationPercentage());
        Assert.assertEquals(BigDecimal.valueOf(5000), paramsDto.getMinInvestmentAmount());
        Assert.assertEquals("dealerId", paramsDto.getKey().getBrokerKey().getId());
        Assert.assertEquals(ModelType.INVESTMENT.getCode(), paramsDto.getKey().getAccountType());
        Assert.assertEquals(ModelPortfolioType.TAILORED.getIntlId(), paramsDto.getKey().getPortfolioType());
    }

    @Test
    public void testFind_whenParamsReturned_thenDtoIsCreated() {
        DealerGroupParams params = Mockito.mock(DealerGroupParams.class);
        Mockito.when(params.getPPDefaultAssetTolerance()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(params.getPPMinimumInvestmentAmount()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(params.getPPMinimumTradeAmount()).thenReturn(BigDecimal.valueOf(500));

        Mockito.when(
                dgParamsIntegrationService.loadCustomerAccountObjects(Mockito.any(DealerParameterKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(params));

        Broker dealerBroker = Mockito.mock(Broker.class);
        Mockito.when(dealerBroker.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));

        DealerParameterKey key = new DealerParameterKey(ModelType.SUPERANNUATION.getCode(),
                ModelPortfolioType.PREFERRED.getIntlId(), dealerBroker);

        PreferredPortfolioDefaultParamsDto paramsDto = (PreferredPortfolioDefaultParamsDto) defaultParamsDtoServiceImpl.find(key,
                new ServiceErrorsImpl());

        Assert.assertNotNull(paramsDto);
        Assert.assertEquals(BigDecimal.valueOf(2), paramsDto.getDefaultAssetTolerance());
        Assert.assertEquals(BigDecimal.valueOf(1000), paramsDto.getMinInvestmentAmount());
        Assert.assertEquals(BigDecimal.valueOf(500), paramsDto.getMinTradeAmount());
        Assert.assertEquals("dealerId", paramsDto.getKey().getBrokerKey().getId());
        Assert.assertEquals(ModelType.SUPERANNUATION.getCode(), paramsDto.getKey().getAccountType());
        Assert.assertEquals(ModelPortfolioType.PREFERRED.getIntlId(), paramsDto.getKey().getPortfolioType());
    }

    @Test
    public void testFind_whenNoPortfolioTypeSpecified_thenNoDtoIsCreated() {
        DealerGroupParams params = Mockito.mock(DealerGroupParams.class);

        Mockito.when(
                dgParamsIntegrationService.loadCustomerAccountObjects(Mockito.any(DealerParameterKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(params));

        Broker dealerBroker = Mockito.mock(Broker.class);
        Mockito.when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerId"));

        DealerParameterKey key = new DealerParameterKey(ModelType.INVESTMENT.getCode(), null, null);

        ModelPortfolioDefaultParamsDto paramsDto = defaultParamsDtoServiceImpl.find(key, new ServiceErrorsImpl());

        Assert.assertNull(paramsDto);
    }

}
