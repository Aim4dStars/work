package com.bt.nextgen.serviceops.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.serviceops.model.ServiceOpsModel;

import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementRequest;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementResponse;

/**
 * Created with IntelliJ IDEA.
 * User: l053474
 * Date: 16/08/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateDeviceArrangementServiceImplTest {
    @InjectMocks
    UpdateDeviceArrangementServiceImpl service;

    @Mock
    private com.bt.nextgen.core.webservice.provider.WebServiceProvider serviceProvider;

    @Test
    public void testUnBlockMobile() throws Exception {
    	ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
    	serviceOpsModel.setAction("UNBLOCK_MOBILE_DEVICE");
    	serviceOpsModel.setSafiDeviceId("2343s-23423-s654-e23424-gr4444");
    	
        MaintainMFADeviceArrangementResponse response = new MaintainMFADeviceArrangementResponse();
        when(serviceProvider.sendWebService(anyString(), any(MaintainMFADeviceArrangementRequest.class))).thenReturn(response);
		Assert.assertThat(service.unBlockMobile("123",serviceOpsModel, "service"), IsNull.nullValue());
    }
}
