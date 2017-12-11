package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ModifyChannelAccessCredentialRequest;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementIntegrationServiceV6;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.usermanagement.v6.GroupEsbUtils;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 * Created by L075208 on 19/07/2016.
 */
@Service
public class GroupEsbCustomerCredentialManagementImplV6 implements CustomerCredentialManagementIntegrationServiceV6 {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbCustomerCredentialManagementImplV6.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public boolean updatePPID(String PPID, String credentialId, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse;
        try {

            ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest(PPID, credentialId, ActionCode.MODIFY_EAM_EXTENDED_ATTRIBUTES);
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userProfileService.getSamlToken(),
                    Attribute.GROUP_ESB_UPDATE_PPID, request, serviceErrors);
            ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            boolean status = response != null && Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(response.getServiceStatus().getStatusInfo().get(0).getLevel().toString());

            if (!status) {
                ServiceError error = new ServiceErrorImpl();
                error.setErrorCode("Err.IP-0323");
                error.setReason("Failure received from EAM SVC_310.");
                serviceErrors.addError(error);
            }

            return status;

        } catch (SoapFaultClientException sfe) {
            LOGGER.info("Error trying to Update PPID. Error: {}", sfe.getFaultStringOrReason());
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_310);
            return false;
        }

    }
}
