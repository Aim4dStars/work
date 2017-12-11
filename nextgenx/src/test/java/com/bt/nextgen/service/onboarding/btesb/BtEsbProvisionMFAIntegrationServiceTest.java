package com.bt.nextgen.service.onboarding.btesb;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequest;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequestModel;
import com.bt.nextgen.service.onboarding.ProvisionMFAMobileDeviceResponse;
import com.bt.nextgen.service.onboarding.ProvisionMFAMobileDeviceResponseAdapter;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L069552 on 14/11/17.
 */
public class BtEsbProvisionMFAIntegrationServiceTest extends BaseSecureIntegrationTest{

    @Autowired
    BtEsbOnboardingIntegrationServiceImpl btEsbOnboardingIntegrationService;

    ProvisionMFADeviceRequest provisionMFADeviceRequest;

    @Before
    public void setUp(){

        provisionMFADeviceRequest = new ProvisionMFADeviceRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA,"201654479");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY,null);
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, null);
        provisionMFADeviceRequest.setCustomerIdentifiers(customerIdentifiers);
        provisionMFADeviceRequest.setCanonicalProductCode("3fb3e732d5c5429d97af392ab18e998b");
        provisionMFADeviceRequest.setPrimaryMobileNumber("61444888448");

    }

    @Test
    @SecureTestContext(authorities = {"ROLE_SERVICE_OP"}, username = "CS057462", jobId = "1231224112", customerId = "133331113", profileId = "144412321", jobRole = "SERVICE_AND_OPERATION")
    public void testProvisionMFADevice(){

        ProvisionMFAMobileDeviceResponse provisionMFAMobileDeviceResponse = btEsbOnboardingIntegrationService.provisionMFADevice(provisionMFADeviceRequest);
        assertNotNull(provisionMFAMobileDeviceResponse);
        assertNotNull(provisionMFAMobileDeviceResponse.getMFAArrangementId());

    }

}