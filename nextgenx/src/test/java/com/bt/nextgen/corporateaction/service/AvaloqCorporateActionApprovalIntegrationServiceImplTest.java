package com.bt.nextgen.corporateaction.service;

import com.btfin.abs.trxservice.secevt.v1_0.Secevt2Rsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.AvaloqCorporateActionApprovalIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionApprovalDecisionConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionApprovalDecisionGroupImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecisionGroup;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqCorporateActionApprovalIntegrationServiceImplTest {
	@InjectMocks
	private AvaloqCorporateActionApprovalIntegrationServiceImpl avaloqTrusteeCorporateActionDecisionIntegrationService;

	@Mock
	private AvaloqGatewayHelperService webserviceClient;

	@Mock
	private CorporateActionApprovalDecisionConverter trusteeDecisionConverter;

	private ServiceErrors serviceErrors;

	private CorporateActionApprovalDecisionGroup decisionGroup;

	@Before
	public void setup() {
		serviceErrors = new ServiceErrorsImpl();

		decisionGroup = new CorporateActionApprovalDecisionGroupImpl(CorporateActionResponseCode.SUCCESS);
	}

	@Test
	public void test_submitDecisionGroup()
	{
		CorporateActionApprovalDecisionGroupImpl responseDecisionGroup =
				mock(CorporateActionApprovalDecisionGroupImpl.class);

		Secevt2Rsp securityEventResponse = mock(Secevt2Rsp.class);

		Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), Mockito.eq(AvaloqOperation.SECEVT2_REQ),
				Mockito.any(ServiceErrors.class))).thenReturn(securityEventResponse);

		when(trusteeDecisionConverter.toApprovalDecisionListDtoResponse(any(Secevt2Rsp.class), any(ServiceErrors.class))).
			thenReturn(responseDecisionGroup);

		CorporateActionApprovalDecisionGroup response =
			avaloqTrusteeCorporateActionDecisionIntegrationService.submitApprovalDecisionGroup(decisionGroup, serviceErrors);

		Assert.assertNotNull(response);
	}
}
