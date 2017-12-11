package com.bt.nextgen.service.avaloq.dashboard;

import java.util.List;

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

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.dashboard.PerformanceData;
import com.avaloq.abs.screen_rep.hira.btfg$ui_avsr_dshbrd_prd_prddet.Rep;

@RunWith(MockitoJUnitRunner.class)
public class PerformanceDataConverterTest
{
	@InjectMocks
	PerformanceDataConverter converter = new PerformanceDataConverter();

	@Mock
	private StaticIntegrationService staticIntegrationService;

	@Before
	public void setup()
	{
		Mockito.when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class),
			Mockito.anyString(),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <Object>()
		{
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				if ("8".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("8", "PERIOD", "Period");
					c.setIntlId("period");
					return c;
				}
				else if ("1".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("1", "DAY", "day");
					c.setIntlId("day");
					return c;
				}
				else if ("2".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("2", "WEEK", "Week");
					c.setIntlId("week");
					return c;
				}
				else if ("3".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("3", "MTH", "Month");
					c.setIntlId("mth");
					return c;
				}
				else if ("4".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("4", "Quar", "Quarterly");
					c.setIntlId("quar");
					return c;
				}
				else if ("6".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("6", "YEAR", "Yearly");
					c.setIntlId("year");
					return c;
				}
				else if ("59".equals(args[1]))
				{
					CodeImpl c = new CodeImpl("59", "SINCE_INCEPTION", "Since Inception");
					c.setIntlId("since_incept");
					return c;
				}
				else
				{
					return null;
				}
			}
		});

	}

	@Test
	public void testToModelPerformance_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/AdviserDashboardCurrMonthResponse_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		AdviserPerformanceImpl performance = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(performance);
		//Assert.assertFalse(serviceErrors.hasErrors());

		//List <PerformanceData> dailyData = performance.getDailyPerformanceData();
		List <PerformanceData> periodData = performance.getPeriodPerformanceData();
		//Assert.assertTrue(dailyData != null);
		Assert.assertTrue(periodData != null);


	}

	@Test
	public void testToModelPerformance_whenSuppliedWithEmptyResponseWithData() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/AdviserDashboardCurrYearResponse_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		AdviserPerformanceImpl performance = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(performance);
		Assert.assertFalse(serviceErrors.hasErrors());

		List <PerformanceData> periodData = performance.getPeriodPerformanceData();

		Assert.assertTrue(periodData != null);

	}

	@Test
	public void testToModelPerformance_whenSuppliedWithEmptyResponseWithNoData() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/AdviserDashboardCurrYearEmptyResponse_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		AdviserPerformanceImpl performance = converter.toModel(report, serviceErrors);
		Assert.assertNull(performance.getDailyPerformanceData());
		Assert.assertNull(performance.getMonthlyPerformanceData());
		Assert.assertNull(performance.getYearlyPerformanceData());
	}

	@Test
	public void testToModelPerformance_whenSuppliedWithEmptyResponseWithNoPeriodData() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/AdviserDashboardCurrYearEmptyPeriodResponse_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		AdviserPerformanceImpl performance = converter.toModel(report, serviceErrors);
		Assert.assertNull(performance.getDailyPerformanceData());
		Assert.assertNull(performance.getMonthlyPerformanceData());
		Assert.assertNull(performance.getYearlyPerformanceData());
	}
}
