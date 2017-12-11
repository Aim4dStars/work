package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.btfin.panorama.core.validation.Validator;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqImCorporateActionIntegrationServiceImplTest {

	@InjectMocks
	private AvaloqImCorporateActionIntegrationServiceImpl avaloqImCorporateActionIntegrationService;

	@Mock
	private AvaloqExecute avaloqExecute;

	@Mock
	private Validator validator;

	private ServiceErrors serviceErrors;

	private CorporateActionResponseImpl response;

	@Before
	public void setup() {
		CorporateAction corporateAction = mock(CorporateAction.class);

		List<CorporateAction> corporateActions = new ArrayList<>();
		corporateActions.add(corporateAction);

		response = mock(CorporateActionResponseImpl.class);

		when(response.getCorporateActions()).thenReturn(corporateActions);

		when(avaloqExecute
				.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(CorporateActionResponseImpl.class),
						any(ServiceErrors.class))).thenReturn(response);

		serviceErrors = mock(ServiceErrors.class);
	}

	@Test
	public void test_loadVoluntaryCorporateActions()
	{
		List<CorporateAction> result = avaloqImCorporateActionIntegrationService.loadVoluntaryCorporateActions("", new DateTime(), new DateTime(), "", serviceErrors);
		Assert.assertNotNull(result);

		result = avaloqImCorporateActionIntegrationService.loadVoluntaryCorporateActions("", null, new DateTime(), "", serviceErrors);
		Assert.assertNotNull(result);

		result = avaloqImCorporateActionIntegrationService.loadVoluntaryCorporateActions("", new DateTime(),null, "", serviceErrors);
		Assert.assertNotNull(result);

		when(response.getCorporateActions()).thenReturn(null);

		result = avaloqImCorporateActionIntegrationService.loadVoluntaryCorporateActions("", new DateTime(),null, "123", serviceErrors);
		Assert.assertNull(result);
	}

	@Test
	public void test_loadMandatoryCorporateActions()
	{
		List<CorporateAction> result = avaloqImCorporateActionIntegrationService.loadMandatoryCorporateActions("", new DateTime(), new DateTime(), "", serviceErrors);
		Assert.assertNotNull(result);

		result = avaloqImCorporateActionIntegrationService.loadMandatoryCorporateActions("", null, new DateTime(), "", serviceErrors);
		Assert.assertNotNull(result);

		result = avaloqImCorporateActionIntegrationService.loadMandatoryCorporateActions("", new DateTime(), null, "", serviceErrors);
		Assert.assertNotNull(result);

		result = avaloqImCorporateActionIntegrationService.loadMandatoryCorporateActions("", new DateTime(), new DateTime(), "", serviceErrors);
		Assert.assertNotNull(result);

		result = avaloqImCorporateActionIntegrationService.loadMandatoryCorporateActions("", new DateTime(), null, "123", serviceErrors);
		Assert.assertNotNull(result);
	}
}
