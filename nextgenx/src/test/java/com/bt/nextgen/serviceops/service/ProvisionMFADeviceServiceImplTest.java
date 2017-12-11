package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.collection.Collection;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequest;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequestModel;
import com.bt.nextgen.service.onboarding.ProvisionMFAMobileDeviceResponse;
import com.bt.nextgen.serviceops.model.ProvisionMFARequestData;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorCodeType;
import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by L069552 on 9/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProvisionMFADeviceServiceImplTest {

    @InjectMocks
    ProvisionMFADeviceServiceImpl provisionMFADeviceServiceImpl;

    @Mock
    private OnboardingIntegrationService btEsbService;

    ProvisionMFARequestData provisionMFADeviceRequestModel;

    ProvisionMFAMobileDeviceResponse provisionMFAMobileDeviceResponse;

    private void mockProvisionMFAMobileDeviceResponse_Success(){

        provisionMFAMobileDeviceResponse = mock(ProvisionMFAMobileDeviceResponse.class);
        when(provisionMFAMobileDeviceResponse.getMFAArrangementId()).thenReturn("1234526222222");
        when(btEsbService.provisionMFADevice(any(ProvisionMFADeviceRequest.class))).thenReturn(provisionMFAMobileDeviceResponse);
    }

    private void mockProvisionMFAMobileDeviceResponse_Failure(){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ServiceError serviceError = mock(ServiceError.class);
        when(serviceError.getErrorCode()).thenReturn("ERR1234");
        serviceErrors.addErrors(Collections.singletonList(serviceError));
        provisionMFAMobileDeviceResponse = mock(ProvisionMFAMobileDeviceResponse.class);
        when(provisionMFAMobileDeviceResponse.getServiceErrors()).thenReturn(serviceErrors);
        when(btEsbService.provisionMFADevice(any(ProvisionMFADeviceRequest.class))).thenReturn(provisionMFAMobileDeviceResponse);
    }


    private void mockProvisionMFAMobileDeviceResponse_Invalid(){

        provisionMFAMobileDeviceResponse = mock(ProvisionMFAMobileDeviceResponse.class);
        when(provisionMFAMobileDeviceResponse.getMFAArrangementId()).thenReturn(null);
        when(btEsbService.provisionMFADevice(any(ProvisionMFADeviceRequest.class))).thenReturn(provisionMFAMobileDeviceResponse);
    }

    @Before
    public void initialise(){
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA,"201645524");
        provisionMFADeviceRequestModel = new ProvisionMFARequestData();
        provisionMFADeviceRequestModel.setCanonicalProductName("673jbjhded912321fdg45");
        provisionMFADeviceRequestModel.setGcmId("20165542234");
    }

    @Test
    public void provisionMFADevice_Success(){

        mockProvisionMFAMobileDeviceResponse_Success();
        boolean isUpdated = provisionMFADeviceServiceImpl.provisionMFADevice(provisionMFADeviceRequestModel,new ServiceErrorsImpl());
        assertTrue(isUpdated);
    }

    @Test
    public void provisionMFADevice_Failure(){

        mockProvisionMFAMobileDeviceResponse_Failure();
        boolean isUpdated = provisionMFADeviceServiceImpl.provisionMFADevice(provisionMFADeviceRequestModel,new ServiceErrorsImpl());
        assertFalse(isUpdated);

    }

    @Test
    public void provisionMFADevice_Invalid(){
        mockProvisionMFAMobileDeviceResponse_Invalid();
        boolean isUpdated = provisionMFADeviceServiceImpl.provisionMFADevice(provisionMFADeviceRequestModel,new ServiceErrorsImpl());
        assertFalse(isUpdated);
    }
}