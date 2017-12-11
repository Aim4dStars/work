package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.usermanagement.v5.GroupEsbUtils;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class GroupEsbCustomerCredentialManagementImpl implements CustomerCredentialManagementIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbCustomerCredentialManagementImpl.class);

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public CustomerCredentialManagementInformation refreshCredential(String websealAppServerId, ServiceErrors serviceErrors) {
        LOGGER.info("Starting refreshCredential for user.");
        String credentialId = userProfileService.getCredentialId(serviceErrors);
        String westpacCustomerNumber = userProfileService.getSamlToken().getBankDefinedLogin();

        if (isBlank(credentialId) || isBlank(westpacCustomerNumber) || isBlank(websealAppServerId)) {
            LOGGER.error("details missing for refresh saml token. Credential id: {}; customer number: {}; websealServerId: {}.",
                credentialId, westpacCustomerNumber, websealAppServerId);
            return null;
        }

        CorrelatedResponse correlatedResponse;
        try {
            ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest(credentialId, ActionCode.REFRESH_CREDENTIAL, westpacCustomerNumber, websealAppServerId);
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userProfileService.getSamlToken(),
                Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, request, serviceErrors);
        } catch (SoapFaultClientException sfe) {
            LOGGER.info("Error trying to refreshCredential. Error: {}", sfe.getFaultStringOrReason());
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_310);
            return null;
        }

        ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
        CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
        if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310,
                correlatedResponse.getCorrelationIdWrapper());
        }
        LOGGER.info("Starting refreshCredential for user.");
        return result;
    }
}
