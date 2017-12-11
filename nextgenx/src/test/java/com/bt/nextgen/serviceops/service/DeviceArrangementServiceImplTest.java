package com.bt.nextgen.serviceops.service;

import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerDeviceManagementIntegrationService;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceArrangementServiceImplTest {

    @InjectMocks
    private DeviceArrangementServiceImpl deviceArrangementService;

    @Mock
    private CustomerDeviceManagementIntegrationService customerDeviceManagementIntegrationService;

    @Test
    public void testUpdateUserMobileNumber() {
        MaintainMFADeviceArrangementResponse response = mock(MaintainMFADeviceArrangementResponse.class);
        ServiceStatus serviceStatus = createServiceStatus(Level.ERROR);
        when(response.getServiceStatus()).thenReturn(serviceStatus);

        UserAccountStatusModel userAcctStatusModel = new UserAccountStatusModel();
        userAcctStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);
        ServiceErrors serviceError = new ServiceErrorsImpl();
        CustomerCredentialManagementInformation credentialInformation = getSuccessObject();
        Mockito.when(customerDeviceManagementIntegrationService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(), any(ServiceErrorsImpl.class))).thenReturn(credentialInformation);

        boolean result = deviceArrangementService.confirmMobileNumber("0431167001", "customerId", "safiDeviceId", "employeeId", "f", userAcctStatusModel, serviceError);
        Assert.assertThat(result, Is.is(true));

        verify(customerDeviceManagementIntegrationService, times(1)).updateUserMobileNumber(anyString(), anyString(), anyString(), eq(""), any(ServiceErrorsImpl.class));
        verify(customerDeviceManagementIntegrationService, times(1)).updateUserMobileNumber(anyString(), anyString(), anyString(), eq("ACT"), any(ServiceErrorsImpl.class));
    }

    @Test
    public void testUpdateUserMobileNumberFeatureOnActive() {
        MaintainMFADeviceArrangementResponse response = mock(MaintainMFADeviceArrangementResponse.class);
        ServiceStatus serviceStatus = createServiceStatus(Level.ERROR);
        when(response.getServiceStatus()).thenReturn(serviceStatus);

        UserAccountStatusModel userAcctStatusModel = new UserAccountStatusModel();
        userAcctStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);
        ServiceErrors serviceError = new ServiceErrorsImpl();
        CustomerCredentialManagementInformation credentialInformation = getSuccessObject();
        Mockito.when(customerDeviceManagementIntegrationService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(), any(ServiceErrorsImpl.class))).thenReturn(credentialInformation);

        boolean result = deviceArrangementService.confirmMobileNumber("0431167001", "customerId", "safiDeviceId", "employeeId", "f", userAcctStatusModel, serviceError);
        Assert.assertThat(result, Is.is(true));

        verify((customerDeviceManagementIntegrationService), times(1)).updateUserMobileNumber(anyString(), anyString(), anyString(), eq(""), any(ServiceErrorsImpl.class));
        verify((customerDeviceManagementIntegrationService), times(1)).updateUserMobileNumber(anyString(), anyString(), anyString(), eq("ACT"), any(ServiceErrorsImpl.class));
    }

    @Test
    public void testUpdateUserMobileNumberDGOnActive() throws Exception {
        MaintainMFADeviceArrangementResponse response = mock(MaintainMFADeviceArrangementResponse.class);
        ServiceStatus serviceStatus = createServiceStatus(Level.ERROR);
        when(response.getServiceStatus()).thenReturn(serviceStatus);

        UserAccountStatusModel userAcctStatusModel = new UserAccountStatusModel();
        userAcctStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);
        ServiceErrors serviceError = new ServiceErrorsImpl();
        CustomerCredentialManagementInformation credentialInformation = getSuccessObject();
        Mockito.when(customerDeviceManagementIntegrationService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(), any(ServiceErrorsImpl.class))).thenReturn(credentialInformation);

        boolean result = deviceArrangementService.confirmMobileNumber("0431167001", "customerId", "safiDeviceId", "employeeId", "f", userAcctStatusModel, serviceError);
        Assert.assertThat(result, Is.is(true));

        verify((customerDeviceManagementIntegrationService), times(1)).updateUserMobileNumber(anyString(), anyString(), anyString(), eq(""), any(ServiceErrorsImpl.class));
        verify((customerDeviceManagementIntegrationService), times(1)).updateUserMobileNumber(anyString(), anyString(), anyString(), eq("ACT"), any(ServiceErrorsImpl.class));
    }

    private ServiceStatus createServiceStatus(Level level) {
        ServiceStatus serviceStatus = mock(ServiceStatus.class);
        List<StatusInfo> statusInfoList = new ArrayList<StatusInfo>();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(level);
        statusInfo.setCode("00000");
        statusInfoList.add(statusInfo);
        when(serviceStatus.getStatusInfo()).thenReturn(statusInfoList);

        return serviceStatus;
    }

    private CustomerCredentialManagementInformation getSuccessObject() {
        CustomerCredentialManagementInformation credentialInformation = new CustomerCredentialManagementInformation() {
            @Override
            public String getServiceLevel() {
                return "SUCCESS";
            }

            @Override
            public String getServiceStatusErrorCode() {
                return null;
            }

            @Override
            public String getServiceStatusErrorDesc() {
                return null;
            }

            @Override
            public String getServiceStatus() {
                return null;
            }

            @Override
            public String getServiceNegativeResponse() {
                return null;
            }

            @Override
            public String getNewPassword() {
                return null;
            }
        };
        return credentialInformation;
    }
}
