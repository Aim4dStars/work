package com.bt.nextgen.api.dashboard.service;

import com.bt.nextgen.api.dashboard.model.AdviserPerformanceDto;
import com.bt.nextgen.api.dashboard.model.AdviserPerformanceSummaryDto;
import com.bt.nextgen.api.dashboard.model.PeriodKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.dashboard.AdviserPerformanceImpl;
import com.bt.nextgen.service.avaloq.dashboard.PerformanceDataImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;
import com.bt.nextgen.service.integration.dashboard.PerformanceData;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AdviserDashboardDtoServiceTest
{

	private static final String LAST_30_DAYS = "LAST_30_DAYS";

	@InjectMocks
	private AdviserDashboardDtoServiceImpl dashboardDtoService;

	@Mock
	AdviserPerformanceIntegrationService adviserPerformanceService;

	@Mock
	UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerService;

	BrokerKey brokerKey = BrokerKey.valueOf("34343");
    UserProfile profile = null;

    @Before
	public void setup() throws Exception
	{
        profile = Mockito.mock(UserProfile.class);
	}

	@Test
	public void testGetDashboard_WhenPeriodHasLast30Days_thenPerformanceReturnedAsExpected()
	{
		AdviserPerformanceImpl adviserPerformance = new AdviserPerformanceImpl();
		List <PerformanceData> performances = new ArrayList <>();
		List <PerformanceData> periodPerformances = new ArrayList <>();
		PerformanceDataImpl performance = new PerformanceDataImpl();
		PerformanceDataImpl perPerformance = new PerformanceDataImpl();
		performance.setFee(BigDecimal.valueOf(20));
		performance.setInflows(BigDecimal.valueOf(30));
		performance.setNetFlows(BigDecimal.valueOf(40));
		performance.setFua(BigDecimal.valueOf(50));
		performance.setObjCount(Integer.valueOf(60));
		performance.setOutflows(BigDecimal.valueOf(20));
		performance.setPeriodSop(DateTime.now());
		performance.setPeriodEop(DateTime.now());
		performances.add(performance);
		adviserPerformance.setDailyPerformanceData(performances);

		perPerformance.setFee(BigDecimal.valueOf(20));
		perPerformance.setInflows(BigDecimal.valueOf(30));
		perPerformance.setNetFlows(BigDecimal.valueOf(40));
		perPerformance.setFua(BigDecimal.valueOf(50));
		perPerformance.setObjCount(Integer.valueOf(60));
		perPerformance.setOutflows(BigDecimal.valueOf(20));
		periodPerformances.add(perPerformance);
		adviserPerformance.setPeriodPerformanceData(periodPerformances);

		Mockito.when(adviserPerformanceService.loadCurrentMonthPerformanceData(Mockito.any(BrokerKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(adviserPerformance);

		PeriodKey periodKey = PeriodKey.valueOf(LAST_30_DAYS);
		AdviserPerformanceSummaryDto performanceSummaryDto = dashboardDtoService.find(periodKey, new ServiceErrorsImpl());
		List <AdviserPerformanceDto> advsrPerformances = performanceSummaryDto.getPerformance();
		AdviserPerformanceDto performanceDto = advsrPerformances.get(0);
		Assert.assertEquals(1, advsrPerformances.size());
		Assert.assertEquals(BigDecimal.valueOf(20), performanceDto.getFee());
		Assert.assertEquals(BigDecimal.valueOf(30), performanceDto.getInflow());
		Assert.assertEquals(BigDecimal.valueOf(20), performanceDto.getOutflow());
		Assert.assertEquals(BigDecimal.valueOf(50), performanceDto.getFua());
		Assert.assertEquals(Integer.valueOf(60), performanceDto.getAccounts());
		Assert.assertEquals(BigDecimal.valueOf(40), performanceDto.getNetflow());

		Assert.assertEquals(BigDecimal.valueOf(20), performanceSummaryDto.getTotalFee());
		Assert.assertEquals(BigDecimal.valueOf(30), performanceSummaryDto.getTotalInflow());
		Assert.assertEquals(BigDecimal.valueOf(40), performanceSummaryDto.getTotalNetflow());
		Assert.assertEquals(BigDecimal.valueOf(50), performanceSummaryDto.getTotalFua());
		Assert.assertEquals(Integer.valueOf(60), performanceSummaryDto.getTotalAccounts());
		Assert.assertEquals(BigDecimal.valueOf(20), performanceSummaryDto.getTotalOutflow());
	}

    @Test
    public void testFindOne_WhenLogedIn_thenReturnFuaAndAccounts() {
        List<Broker> brokerList = new ArrayList<>();
        BrokerKey brokerKey = BrokerKey.valueOf("34343");
        BrokerImpl broker1 =  new BrokerImpl(brokerKey, BrokerType.ADVISER);
        broker1.setFua(new BigDecimal(10000));
        broker1.setNumberOfAccounts(23);
        brokerList.add(broker1);
        BrokerImpl broker2 =  new BrokerImpl(brokerKey, BrokerType.ADVISER);
        broker2.setFua(new BigDecimal(10000));
        brokerList.add(broker2);
        BrokerImpl broker3 =  new BrokerImpl(brokerKey, BrokerType.ADVISER);
        brokerList.add(broker3);

        Mockito.when(userProfileService.getActiveProfile()).thenReturn(profile);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Mockito.when(brokerService.getBrokersForJob(profile, serviceErrors)).thenReturn(brokerList);
        AdviserPerformanceSummaryDto summaryDto = dashboardDtoService.findOne(serviceErrors);
        Assert.assertThat(summaryDto.getTotalFua(), Is.is(new BigDecimal(20000)));
        Assert.assertThat(summaryDto.getTotalAccounts(), Is.is(23));
    }
}
