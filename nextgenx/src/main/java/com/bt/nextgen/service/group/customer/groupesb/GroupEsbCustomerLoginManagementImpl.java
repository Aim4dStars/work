package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.ObjectFactory;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RequestedAction;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserAlternateAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserNameAliasCredentialDocument;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialServiceImpl;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by m035652 on 6/02/14.
 */
@Service
@SuppressWarnings({"squid:S1200","squid:S00116","findbugs:SS_SHOULD_BE_STATIC"})
public class GroupEsbCustomerLoginManagementImpl implements CustomerLoginManagementIntegrationService 
{
	private static final Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);
	private static final String SVC_311_V4_ENABLED = "svc.311.v4.enabled";
	
    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;
    
    @Autowired
   	private UserProfileService profileService;
    
    @Resource(name = "serverAuthorityService")
    private BankingAuthorityService applicationSamlService;

    @Override
    public CustomerCredentialInformation getCustomerInformation(CredentialRequest credentialRequest, ServiceErrors serviceErrors) 
    {
        String gcmId = "";
        
        //if credentialrequest is not null then use that gcmId to fetch the username 
        //else use the profileservice gcmId
        if(credentialRequest != null && credentialRequest.getBankReferenceId() != null)
        {
                gcmId = credentialRequest.getBankReferenceId();
        }
        else
        {
                logger.info("GroupEsbCustomerLoginManagementImpl.getCustomerInformation(): Is service operator:{}", profileService.isServiceOperator());
                gcmId = profileService.getGcmId();
        }
        logger.info("GroupEsbCustomerLoginManagementImpl.getCustomerInformation(): Gcm Id:{}", gcmId);
                if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
                    logger.info("SVC_311_V4_ENABLED");
                        RetrieveChannelAccessCredentialRequest request = createRetrieveChannelAccessCredentialRequest(gcmId);

                        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                                        userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), request, serviceErrors);

                        RetrieveChannelAccessCredentialResponse response = (RetrieveChannelAccessCredentialResponse) correlatedResponse.getResponseObject();

                        CustomerCredentialInformation result = new GroupEsbCustomerCredentialAdapter(response, gcmId, serviceErrors);
                        if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_311, correlatedResponse.getCorrelationIdWrapper());
                        }
                        return result;
                }else{
                        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialRequest request = createRetrieveChannelAccessCredentialRequestV3(gcmId);

                        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                                        userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), request, serviceErrors);

                        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialResponse response = (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialResponse) correlatedResponse.getResponseObject();

                        CustomerCredentialInformation result = new GroupEsbCustomerCredentialAdapter(response, gcmId, serviceErrors);
                        if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_311, correlatedResponse.getCorrelationIdWrapper());
                        }
                        return result;
                }
    }
    
    @Override
    public CustomerCredentialInformation getDirectCustomerInformation(CredentialRequest credentialRequest, ServiceErrors serviceErrors) 
    {
        logger.info("GroupEsbCustomerLoginManagementImpl.getCustomerInformation(): PAN NUmber:{}", credentialRequest.getBankReferenceId());
                if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
                    logger.info("SVC_311_V4_ENABLED");
                        RetrieveChannelAccessCredentialRequest request = createRetrieveChannelAccessCredentialRequest(credentialRequest.getBankReferenceId());

                        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                                applicationSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), request, serviceErrors);

                        RetrieveChannelAccessCredentialResponse response = (RetrieveChannelAccessCredentialResponse) correlatedResponse.getResponseObject();

                        CustomerCredentialInformation result = new GroupEsbCustomerCredentialAdapter(response, credentialRequest.getBankReferenceId(), serviceErrors);
                        if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_311, correlatedResponse.getCorrelationIdWrapper());
                        }
                        return result;
                }else{
                        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialRequest request = createRetrieveChannelAccessCredentialRequestV3(credentialRequest.getBankReferenceId());

                        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                                applicationSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), request, serviceErrors);

                        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialResponse response = (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialResponse) correlatedResponse.getResponseObject();

                        CustomerCredentialInformation result = new GroupEsbCustomerCredentialAdapter(response, credentialRequest.getBankReferenceId(), serviceErrors);
                        if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
                                ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_311, correlatedResponse.getCorrelationIdWrapper());
                        }
                        return result;
                }
    }

    @Override
    public List<Roles> getCredentialGroups(CredentialRequest credentialRequest, ServiceErrors serviceErrors) {
        List<Roles> roles=new ArrayList<>();
            CustomerCredentialInformation customerInformation =
                    this.getCustomerInformation(credentialRequest, serviceErrors);
            if (serviceErrors.hasErrors()) {
                logger.error("Error occurred while trying to fetch credential groups for client - GCM ID {}", credentialRequest.getBankReferenceId());
            } else {
                roles = customerInformation.getCredentialGroups();
            }
        return roles;
    }

    @Override
    public String getPPID(CredentialRequest credentialRequest, ServiceErrors serviceErrors) {
        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialRequest request =
                createRetrieveChannelAccessCredentialRequestV5(credentialRequest.getBankReferenceId());

        CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
                applicationSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL_V5.getConfigName(), request, serviceErrors);

        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialResponse response =
                (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialResponse) correlatedResponse.getResponseObject();

        CustomerCredentialInformation result = new GroupEsbCustomerCredentialAdapter(response, credentialRequest.getBankReferenceId(), serviceErrors);
        if (!response.getServiceStatus().getStatusInfo().get(0).getLevel().value().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_311, correlatedResponse.getCorrelationIdWrapper());
        }
        return result.getPpId();
    }

    public au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialRequest createRetrieveChannelAccessCredentialRequestV5(String gcmId) {
        logger.info("GroupEsbCustomerLoginManagementImpl.createRetrieveChannelAccessCredentialRequest(): Gcm Id:{}", gcmId);
        logger.info("GroupEsbCustomerLoginManagementImpl.createRetrieveChannelAccessCredentialRequest(): Is service operator:{}", profileService.isServiceOperator());

        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.ObjectFactory of = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.ObjectFactory();
        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialRequest request = of.createRetrieveChannelAccessCredentialRequest();
        request.getRequestedAction().add(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RequestedAction.RETRIEVE_ONLINE_BANKING_STATUS);
        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserCredentialDocument userCredentialDocument = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserCredentialDocument();
        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserNameAliasCredentialDocument userNameAliasCredentialDocument = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserNameAliasCredentialDocument();
        userNameAliasCredentialDocument.setUserId(gcmId);
        userCredentialDocument.setUserName(userNameAliasCredentialDocument);
        request.setUserCredential(userCredentialDocument);

        return request;
    }
    
/*    @Override
    public CustomerCredentialInformation getCustomerInformation(ServiceErrors serviceErrors) 
    {
        RetrieveChannelAccessCredentialRequest request = createRetrieveChannelAccessCredentialRequest(null);

        CorrelatedResponse correlatedResponse =  provider.sendWebServiceWithSecurityHeaderAndResponseCallback(
        							   userSamlService.getSamlToken(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL.getConfigName(), request, serviceErrors);
        
        RetrieveChannelAccessCredentialResponse response = (RetrieveChannelAccessCredentialResponse)correlatedResponse.getResponseObject();

        CustomerCredentialInformation result = new GroupEsbCustomerCredentialAdapter(response, profileService.getBankReferenceId());
        if(!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
		{
			ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_311, correlatedResponse.getCorrelationIdWrapper());
		}
        return result;
    }*/
    
    public RetrieveChannelAccessCredentialRequest createRetrieveChannelAccessCredentialRequest(String gcmId)
    {
    	logger.info("GroupEsbCustomerLoginManagementImpl.createRetrieveChannelAccessCredentialRequest(): Gcm Id:{}", gcmId);

		ObjectFactory of = new ObjectFactory();
		RetrieveChannelAccessCredentialRequest request = of.createRetrieveChannelAccessCredentialRequest();
    	request.getRequestedAction().add(RequestedAction.RETRIEVE_ONLINE_BANKING_STATUS);
		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();
		UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
        UserAlternateAliasCredentialDocument hasAlternateAlias = new UserAlternateAliasCredentialDocument();
        hasAlternateAlias.setUserId(gcmId);
        userNameAliasCredentialDocument.setHasAlternateUserNameAlias(hasAlternateAlias);
    	userCredentialDocument.setUserName(userNameAliasCredentialDocument);
    	request.setUserCredential(userCredentialDocument);

    	return request;

    }

	public au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialRequest createRetrieveChannelAccessCredentialRequestV3(String gcmId)
	{
		logger.info("GroupEsbCustomerLoginManagementImpl.createRetrieveChannelAccessCredentialRequest(): Gcm Id:{}", gcmId);
		logger.info("GroupEsbCustomerLoginManagementImpl.createRetrieveChannelAccessCredentialRequest(): Is service operator:{}", profileService.isServiceOperator());

		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.ObjectFactory of = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.ObjectFactory();
		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialRequest request = of.createRetrieveChannelAccessCredentialRequest();
		request.getRequestedAction().add(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RequestedAction.RETRIEVE_ONLINE_BANKING_STATUS);
		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument userCredentialDocument = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument();
		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserNameAliasCredentialDocument userNameAliasCredentialDocument = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserNameAliasCredentialDocument();
		userNameAliasCredentialDocument.setUserId(gcmId);
		userCredentialDocument.setUserName(userNameAliasCredentialDocument);
		request.setUserCredential(userCredentialDocument);

		return request;

	}

	@Override
	public String getCustomerUserName(String customerId, ServiceErrors errors) {
		String userName = Constants.EMPTY_STRING;
		if (customerId != null) {
			CredentialRequestModel request = new CredentialRequestModel();
			request.setBankReferenceId(customerId);
			CustomerCredentialInformation customerInformation =
					this.getCustomerInformation(request, errors);
			if (errors.hasErrors()) {
				logger.error("Error occurred while trying to fetch username for client - GCM ID {}", customerId);
			} else {
				userName = customerInformation.getUsername();
			}
		}
		return userName;
	}
}
