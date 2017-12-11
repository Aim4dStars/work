package com.bt.nextgen.service.group.customer.groupesb;

import javax.annotation.Resource;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerUserNameManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerUsernameUpdateRequest;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.usermanagement.v5.GroupEsbUtils;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
@SuppressWarnings({"squid:S1200", "squid:S00116", "findbugs:SS_SHOULD_BE_STATIC", "squid:S00112", "checkstyle:com.puppycrawl.tools" + "" +
    ".checkstyle.checks.whitespace.TypecastParenPadCheck"})
public class GroupEsbCustomerUserNameManagementImpl implements CustomerUserNameManagementIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerUserNameManagementImpl.class);
    private static final String SVC_310_V5_ENABLED = "svc.310.v5.enabled";

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private CmsService cmsService;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;


    @Override
    public CustomerCredentialManagementInformation updateUsername(CustomerUsernameUpdateRequest usernameUpdateRequest, ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse = null;
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            ModifyChannelAccessCredentialRequest requestPayload = GroupEsbUtils.createModifyChannelAccessCredentialRequest(usernameUpdateRequest, true);
            try {
                correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                    WebServiceProviderConfig.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), requestPayload, serviceErrors);
            } catch (SoapFaultClientException sfe) {
                ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_310);
                return new GroupEsbCustomerCredentialManagementAdapter();
            }

            ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
            return result;
        } else {
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest requestPayload
                = com.bt.nextgen.service.group.customer.groupesb.usermanagement.v3.GroupEsbUtils.createModifyChannelAccessCredentialRequest(usernameUpdateRequest, true);
            try {
                correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                    WebServiceProviderConfig.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), requestPayload, serviceErrors);
            } catch (SoapFaultClientException sfe) {
                ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_310);
                return new GroupEsbCustomerCredentialManagementAdapter();
            }

            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse response
                = (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
            return result;
        }
    }

    @Override
    public CustomerCredentialManagementInformation createUsername(CustomerUsernameUpdateRequest usernameCreationRequest, ServiceErrors serviceErrors) {
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            //NOTE boolean true is submitted here because even creation of a user alias is still a modification of an existing user in EAM
            ModifyChannelAccessCredentialRequest requestPayload = GroupEsbUtils.createModifyChannelAccessCredentialRequest(usernameCreationRequest, true);

            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                WebServiceProviderConfig.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), requestPayload, serviceErrors);
            ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
            return result;
        } else {
            //NOTE boolean true is submitted here because even creation of a user alias is still a modification of an existing user in EAM
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest requestPayload
                = com.bt.nextgen.service.group.customer.groupesb.usermanagement.v3.GroupEsbUtils.createModifyChannelAccessCredentialRequest(usernameCreationRequest, true);

            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                WebServiceProviderConfig.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), requestPayload, serviceErrors);
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse response
                = (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper(), cmsService);
            }
            return result;
        }
    }

    @Override
    public CustomerCredentialManagementInformation blockUser(String credentialId, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerUserNameManagementImpl.blockUser(): Blocking credential ID- {}", credentialId);
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest(credentialId, ActionCode.LOCK_CREDENTIAL, false);
            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, request, serviceErrors);
            ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper());
            }
            return result;
        } else {
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest request
                = com.bt.nextgen.service.group.customer.groupesb.usermanagement.v3.GroupEsbUtils.createRequest(credentialId, au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ActionCode.LOCK_CREDENTIAL, false);
            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, request, serviceErrors);
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse response
                = (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper());
            }
            return result;
        }
    }

    @Override
    public CustomerCredentialManagementInformation unblockUser(String credentialId, boolean isResetPassword, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerUserNameManagementImpl.unblockUser(): UnBlocking credential ID- {}", credentialId);
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest(credentialId, ActionCode.REINSTATE_CREDENTIAL, isResetPassword);
            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, request, serviceErrors);
            ModifyChannelAccessCredentialResponse response = (ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper());
            }
            return result;
        } else {
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest request
                = com.bt.nextgen.service.group.customer.groupesb.usermanagement.v3.GroupEsbUtils.createRequest(credentialId, au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ActionCode.REINSTATE_CREDENTIAL, isResetPassword);
            CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL, request, serviceErrors);
            au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse response
                = (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse) correlatedResponse.getResponseObject();
            CustomerCredentialManagementInformation result = new GroupEsbCustomerCredentialManagementAdapter(response);
            if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_310, correlatedResponse.getCorrelationIdWrapper());
            }
            return result;
        }
    }
}
