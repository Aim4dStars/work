package com.bt.nextgen.api.termdeposit.controller;

import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.service.TermDepositCalculatorDtoService;
import com.bt.nextgen.api.termdeposit.service.TermDepositRateCalculatorDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 1/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositsCalculatorApiControllerTest {

    @InjectMocks
    private TermDepositsCalculatorApiController controller;

    @Mock
    private TermDepositCalculatorDtoService termDepositCalculatorDtoService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private TermDepositRateCalculatorDtoService termDepositRateCalculatorService;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private Broker broker;

    @Mock
    private FeatureToggles features;

    @Mock
    private TermDepositCalculatorDto calculatorDto;

    @Before
    public void setUp() throws Exception {
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("123456"));
        when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(features);
        when(termDepositCalculatorDtoService.find(any(TermDepositCalculatorKey.class), any(ServiceErrors.class)))
                .thenReturn(calculatorDto);
        when(termDepositRateCalculatorService.find(any(TermDepositCalculatorKey.class), any(ServiceErrors.class)))
                .thenReturn(calculatorDto);
    }

    @Test
    public void getOldTermDespoistWithoutBadgeAndAccountId() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(false);
        ApiResponse response = controller.getTermDepositCalculator(null, "10000.00", null,null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getOldTermDespoistWithoutBadge() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(false);
        ApiResponse response = controller.getTermDepositCalculator(null, "10000.00", "123321",null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getOldTermDespoistWithoutAccountId() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(false);
        ApiResponse response = controller.getTermDepositCalculator("D671036F60C34DDD", "10000.00", null,null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getOldTermDespoist() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(false);
        ApiResponse response = controller.getTermDepositCalculator("D671036F60C34DDD", "10000.00", "123321",null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getNewTermDespoistWithoutBadgeAndAccountId() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(true);
        ApiResponse response = controller.getTermDepositCalculator(null, "10000.00", null,null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getNewTermDespoistWithoutBadge() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(true);
        ApiResponse response = controller.getTermDepositCalculator(null, "10000.00", "123321",null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getNewTermDespoistWithoutAccountId() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(true);
        ApiResponse response = controller.getTermDepositCalculator("D671036F60C34DDD", "10000.00", null,null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
    @Test
    public void getNewTermDespoist() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(true);
        ApiResponse response = controller.getTermDepositCalculator("D671036F60C34DDD", "10000.00", "123321",null);
        assertThat(response.getApiVersion(), is(ApiVersion.CURRENT_VERSION));
    }
}