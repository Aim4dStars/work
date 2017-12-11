package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequest;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequestModel;
import com.bt.nextgen.service.onboarding.ProvisionMFAMobileDeviceResponse;
import com.bt.nextgen.serviceops.model.ProvisionMFARequestData;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L069552 on 6/11/17.
 */
@Service
public class ProvisionMFADeviceServiceImpl implements ProvisionMFADeviceService {

    @Autowired
    private OnboardingIntegrationService btEsbService;

    @Override
    public boolean provisionMFADevice(ProvisionMFARequestData provisionMFARequestData,ServiceErrors serviceErrors) {
        ProvisionMFADeviceRequest provisionMFADeviceRequestModel = new ProvisionMFADeviceRequestModel();
        provisionMFADeviceRequestModel.setCanonicalProductCode(provisionMFARequestData.getCanonicalProductName());
        provisionMFADeviceRequestModel.setCustomerIdentifiers(buildCustomerIdentifiers(provisionMFARequestData));
        provisionMFADeviceRequestModel.setPrimaryMobileNumber(provisionMFARequestData.getPrimaryMobileNumber());
        ProvisionMFAMobileDeviceResponse provisionMFAMobileDeviceResponse = btEsbService.provisionMFADevice(provisionMFADeviceRequestModel);
        return null != provisionMFAMobileDeviceResponse && null != provisionMFAMobileDeviceResponse.getMFAArrangementId();
    }

    private Map<CustomerNoAllIssuerType, String> buildCustomerIdentifiers(ProvisionMFARequestData request) {
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, request.getGcmId());
        return customerIdentifiers;
    }
}