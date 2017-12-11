package com.bt.nextgen.reports.asset;

import com.bt.nextgen.api.asset.service.AvailableAssetDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.reports.managedfunds.AvailableManagedFundReport;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING;

@RunWith(MockitoJUnitRunner.class)
public class AvailableManagedFundReportTest {
	@InjectMocks
	private AvailableManagedFundReport report;

	@Mock
	private ContentDtoService contentService;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private AvailableAssetDtoService availableAssetDtoService;

	@Before
	public void setup() {
	}

	@Test
	public void testGetDisclaimer() {
		ContentDto contentDto = new ContentDto("xxx", "Hello World!");

		Mockito.when(contentService.find(Mockito.any(ContentKey.class), Mockito.any(ServiceErrors.class))).thenReturn(contentDto);

		Assert.assertEquals(report.getDisclaimer(null), "Hello World!");
	}

	@Test
	public void testGetStartDate_whenThereIsNoStartDate_thenReturnCurrentDate() {
		DateTime effectiveDate = new DateTime(2016, 1, 1, 0, 0);
		Map<String, String> params = new HashMap<>();

		Assert.assertTrue(!report.getStartDate(params).equals(effectiveDate));
	}

	@Test
	public void testGetStartDate_whenThereIsStartDate_thenReturnStartDate() {
		DateTime effectiveDate = new DateTime(2016, 1, 1, 0, 0);
		Map<String, String> params = new HashMap<>();
		params.put(EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate.toString());

		Assert.assertTrue(report.getStartDate(params).equals(effectiveDate));
	}

	@Test
	public void testGetReportName() {
		Assert.assertEquals(report.getReportName(null), "Available funds list");
	}

	@Test
	public void testGetDealerGroupName() {
		BrokerImpl broker = new BrokerImpl(null, null);
		broker.setPositionName("Broker");
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

		Assert.assertEquals(report.getDelearGroupName(null), broker.getPositionName());
	}

	@Test
	public void testGetDealerGroupName_whenThereIsABracketInName_thenRemoveBracketInName() {
		BrokerImpl broker = new BrokerImpl(null, null);
		broker.setPositionName("Broker (Super)");
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

		Assert.assertEquals(report.getDelearGroupName(null), "Broker");
	}
}
