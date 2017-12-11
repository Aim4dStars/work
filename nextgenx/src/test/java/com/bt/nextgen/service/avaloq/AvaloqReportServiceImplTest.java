package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btfin.panorama.core.security.profile.UserProfileService;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqReportServiceImplTest
{
	
	
    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    UserProfileService userProfileService;

	@Test
	public void testLoadAllTermDepositProducts() throws Exception
	{

	}
}
