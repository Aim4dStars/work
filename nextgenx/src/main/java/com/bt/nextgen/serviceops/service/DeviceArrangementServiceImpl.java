package com.bt.nextgen.serviceops.service;

import au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.MFADeviceArrangement;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.retrievemfadevicearrangements.v1.svc0272.ObjectFactory;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.retrievemfadevicearrangements.v1.svc0272.RetrieveMFADeviceArrangementsRequest;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.retrievemfadevicearrangements.v1.svc0272.RetrieveMFADeviceArrangementsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerDeviceManagementIntegrationService;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @see 'DeviceArrangementServiceResponse.xml' soap response
 * @see 'UpdateDeviceArrangementResponse.xml' soap response update modile device'
 */

@Service
public class DeviceArrangementServiceImpl implements DeviceArrangementService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceArrangementServiceImpl.class);

    @Autowired
    private WebServiceProvider serviceProvider;

    /**
     * Device provision status is "ACTIVE"
     */
    private static final String PROVISIONING_STATUS_ACTIVE = "ACT";

    private final String STATUS_INFO_SUCCESS = "Success";

    @Autowired
    private CustomerDeviceManagementIntegrationService customerDeviceManagementIntegrationService;

    @Override
    public boolean isDeviceDetailsFound(String deviceIdentifier) {

        ObjectFactory of = new ObjectFactory();
        RetrieveMFADeviceArrangementsRequest request = of.createRetrieveMFADeviceArrangementsRequest();
        MFADeviceArrangement mfaDeviceArrangement = new MFADeviceArrangement();
        ProductArrangementIdentifier productArrangementIdentifier = new ProductArrangementIdentifier();
        productArrangementIdentifier.setArrangementId(deviceIdentifier);
        mfaDeviceArrangement.setInternalIdentifier(productArrangementIdentifier);
        request.setArrangement(mfaDeviceArrangement);

        RetrieveMFADeviceArrangementsResponse response = (RetrieveMFADeviceArrangementsResponse) serviceProvider.sendWebService(
                Attribute.SAFI_KEY, request);

        List<StatusInfo> statusInfoList = response.getServiceStatus().getStatusInfo();
        for (StatusInfo statusInfo : statusInfoList) {
            if (statusInfo.getLevel().value().equalsIgnoreCase(STATUS_INFO_SUCCESS)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Confirms a mobile number for a customer which is comprised of the following ICC internal steps:<p>
     * <ul>
     * <li>Takes the updated mobile number from avaloq and creates the corresponding safi device id in SAFI</li>
     * <li>Updates the safi device id from LINKED to ACTIVE</li>
     * </ul><p>
     * The device is subsequently ready for 2FA activities.
     *
     * @param mobileNumber
     * @param customerId
     * @param safiDeviceId
     * @param employeeId
     * @param customerEAMStatus
     * @return
     */
    @Override
    public boolean confirmMobileNumber(String mobileNumber, String customerId, String safiDeviceId, String employeeId, String clientId,
                                       UserAccountStatusModel customerEAMStatus, ServiceErrors serviceErrors) {

        logger.info("Confirm mobile number to {}, for user {}, and safi_deviceId {}", mobileNumber, customerId, safiDeviceId);
        logger.info("Updating mobile number ...");
        boolean updateStatus = updateUserMobileNumber(mobileNumber, customerId, safiDeviceId, employeeId, clientId, null, serviceErrors);

        // If we have updated the mobile correctly (including creating the new safi device id)
        // Then we call the same method again
        if (updateStatus) {
            logger.info("Updating safi device id status to ACTIVE");
            updateStatus = updateUserMobileNumber(mobileNumber,
                    customerId,
                    safiDeviceId,
                    employeeId,
                    clientId,
                    customerEAMStatus,
                    serviceErrors);
        }

        return updateStatus;
    }


    /**
     * <p>Updates a mobile number that has been entered in Avaloq and creates the corresponding device_id entry in SAFI</p>
     * <p>Note that this call will leave the safi device_id in LINKED state, which will cause challenge failure for any registered customer.
     * The updateMobileProvisioningStatus method should be called to change the device_id back into ACTIVE for registered customers</p>
     *
     * @param mobileNumber      Mobile number to be confirmed
     * @param customerId        Avaloq customer number
     * @param safiDeviceId      Device id
     * @param gcmId             GCM id of the customer
     * @param clientId          Client Id of the customer
     * @param customerEAMStatus EAM status indicating whether the user is registered or not
     * @return true if mobile number confirmed to safi successfully, false otherwise.
     */
    @Override
    public boolean updateUserMobileNumber(String mobileNumber, String customerId, String safiDeviceId, String gcmId, String clientId,
                                          UserAccountStatusModel customerEAMStatus, ServiceErrors serviceErrors) {

        logger.info("DeviceArrangementServiceImpl.updateUserMobileNumber() : Updating the User Mobile Number");
        String deviceProvisioningStatus = "";

        if (customerEAMStatus != null) {
            deviceProvisioningStatus = PROVISIONING_STATUS_ACTIVE;
            logger.info("The Device Provisioning Status in EAM is {} and the group is unregistered", deviceProvisioningStatus);
        }

        CustomerCredentialManagementInformation response = customerDeviceManagementIntegrationService.updateUserMobileNumber(mobileNumber,
                safiDeviceId,
                gcmId,
                deviceProvisioningStatus,
                serviceErrors);

        if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(response.getServiceLevel())) {
            logger.info("Response From GroupEsb Method update User Mobile Number {}", Boolean.TRUE);
            return true;

        }

        return false;
    }

    @Override
    public String unBlockMobile(ServiceOpsModel serviceOpsModel, String employeeId, ServiceErrors serviceErrors) {
        logger.info("DeviceArrangementServiceImpl.unBlockMobile() : Updating the User Mobile Number");

        CustomerCredentialManagementInformation response = customerDeviceManagementIntegrationService.unBlockMobile(serviceOpsModel.getUserId(),
                serviceOpsModel.getSafiDeviceId(),
                employeeId,
                serviceErrors);

        if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(response.getServiceLevel())) {
            return Constants.EMPTY_STRING;
        }

        return null;
    }
}
