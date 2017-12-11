package com.bt.nextgen.service.group.customer.groupesb;


import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.EmployeeIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.GroupSystemIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Agent;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.AgentRoleType;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ArrangementAccessCondition;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ArrangementChannelAccessCondition;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.AuthenticationAttemptAuditContext;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Brand;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ElectronicDeliveryDevice;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Individual;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyArrangementRole;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyNetwork;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyRoleType;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.MFADeviceArrangement;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.MaintenanceTransaction;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.SecurityDeviceParameter;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementRequest;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementResponse;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.ObjectFactory;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.UpdateAction;

import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.SafiObjectFactory;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerDeviceManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * 
 * @author L056616
 *
 */
@Service
public class GroupEsbCustomerDeviceManagementImpl implements CustomerDeviceManagementIntegrationService
{

	private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerDeviceManagementImpl.class);

	@Autowired
	private WebServiceProvider provider;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	private static final String UNBLOCK_TYPE_NETWORK = "NETWORK";
	private static final String UNBLOCK_TYPE_DEVICE = "DEVICE";
	private static final String BRAND_CODE = "BTNG";

	@Override
	public CustomerCredentialManagementInformation updateUserMobileNumber(String mobileNumber, String safiDeviceId, String gcmId,
																		  String deviceProvisioningStatus, ServiceErrors serviceErrors)
	{
		logger.info("GroupEsbCustomerDeviceManagementImpl.updateUserMobileNumber() : Updating the User Mobile Number");
		MaintainMFADeviceArrangementRequest request = createMFADeviceArrangementRequest(mobileNumber,
			safiDeviceId,
			gcmId,
			deviceProvisioningStatus);
		CorrelatedResponse correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
			WebServiceProviderConfig.GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS.getConfigName(),
			request,
			serviceErrors);
		MaintainMFADeviceArrangementResponse response = (MaintainMFADeviceArrangementResponse)correlatedResponse.getResponseObject();
		logger.info("GroupEsbCustomerDeviceManagementImpl.updateUserMobileNumber() : Successfully returning service status.");
		CustomerCredentialManagementInformation result = new GroupEsbCustomerDeviceAdapter(response);
		if (!result.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
		{
			ErrorHandlerUtil.parseErrors(response.getServiceStatus(),
				serviceErrors,
				ServiceConstants.SERVICE_276,
				correlatedResponse.getCorrelationIdWrapper());
		}
		return result;
	}

	/**
	 * This method is for UnblockMobile functionality of serviceOps.
	 */
	@Override
	public CustomerCredentialManagementInformation unBlockMobile(String userId, String safiDeviceId, String employeeId,
																 ServiceErrors serviceErrors)
	{
		logger.info("GroupEsbCustomerDeviceManagementImpl.unBlockMobile() : Updating the User Mobile Number");
		MaintainMFADeviceArrangementRequest unblockNetworkrequest = createMFADeviceArrangementRequestUnblockMobile(userId,
			safiDeviceId,
			employeeId,
			UNBLOCK_TYPE_NETWORK);
		CorrelatedResponse unblockNetworkCorrelatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
			WebServiceProviderConfig.GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS.getConfigName(),
			unblockNetworkrequest,
			serviceErrors);
		MaintainMFADeviceArrangementResponse unblockNetworkResponse = (MaintainMFADeviceArrangementResponse)unblockNetworkCorrelatedResponse.getResponseObject();
		logger.info("GroupEsbCustomerDeviceManagementImpl.unBlockMobile() : Successfully returning service status.");
		CustomerCredentialManagementInformation unblockNetworkResult = new GroupEsbCustomerDeviceAdapter(unblockNetworkResponse);

		if (!Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(unblockNetworkResult.getServiceLevel()))
		{
			ErrorHandlerUtil.parseErrors(unblockNetworkResponse.getServiceStatus(),
				serviceErrors,
				ServiceConstants.SERVICE_276,
				unblockNetworkCorrelatedResponse.getCorrelationIdWrapper());
		}

		MaintainMFADeviceArrangementRequest unblockDevicerequest = createMFADeviceArrangementRequestUnblockMobile(userId,
			safiDeviceId,
			employeeId,
			UNBLOCK_TYPE_DEVICE);
		CorrelatedResponse unblockDeviceCorrelatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
			WebServiceProviderConfig.GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS.getConfigName(),
			unblockDevicerequest,
			serviceErrors);
		MaintainMFADeviceArrangementResponse unblockDeviceResponse = (MaintainMFADeviceArrangementResponse)unblockDeviceCorrelatedResponse.getResponseObject();
		logger.info("GroupEsbCustomerDeviceManagementImpl.unBlockMobile() : Successfully returning service status.");
		CustomerCredentialManagementInformation unblockDeviceResult = new GroupEsbCustomerDeviceAdapter(unblockDeviceResponse);
		if (!Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(unblockDeviceResult.getServiceLevel()))
		{
			ErrorHandlerUtil.parseErrors(unblockDeviceResponse.getServiceStatus(),
				serviceErrors,
				ServiceConstants.SERVICE_276,
				unblockDeviceCorrelatedResponse.getCorrelationIdWrapper());
			return unblockDeviceResult;
		}

		return unblockNetworkResult;

	}

	/**
	 * Create the MFADeviceAt=raangementRequest for updateMobileNumberService.
	 * @param mobileNumber
	 * @param safiDeviceId
	 * @param gcmId
	 * @param deviceProvisioningStatus
	 * @return
	 */
	public MaintainMFADeviceArrangementRequest createMFADeviceArrangementRequest(String mobileNumber,
		String safiDeviceId, String gcmId, String deviceProvisioningStatus)
	{
		ObjectFactory of = new ObjectFactory();
		MaintainMFADeviceArrangementRequest request = of.createMaintainMFADeviceArrangementRequest();

		request.setRequestedAction(createUpdateAuthDeviceAction());

		MFADeviceArrangement mfaDeviceArrangement = new MFADeviceArrangement();
		mfaDeviceArrangement.setHasAuthenticationAuditContext(usingAuthenticationAttemptAuditContext());

		// Only set the provisioning status flag if not blank
		// If not set the device is set to "LINKED" status which is correct if the user has not registered yet
		// Post registration the status should always be "ACTIVE" so that the customer can receive SMS'
		// Note: Only one of the two parameters, privisioingStatus or isLinkedToSecurityDevice can be set as part of the call 
		// Otherwise, the call will fail.		
		if (!StringUtils.isEmpty(deviceProvisioningStatus))
		{
			mfaDeviceArrangement.setProvisioningStatus(deviceProvisioningStatus);
		}
		else
		{
			mfaDeviceArrangement.setIsLinkedToSecurityDevice(createElectronicDeliveryDevice(mobileNumber));
		}

		mfaDeviceArrangement.setInternalIdentifier(createProductArrangementIdentifier(safiDeviceId));
		mfaDeviceArrangement.setHasMaintenanceTransaction(createMaintenanceTransaction(gcmId));
		mfaDeviceArrangement.setHasBrand(createBrand(ServiceConstants.BRAND_CODE));

		request.setArrangement(mfaDeviceArrangement);
		return request;
	}

	public MaintainMFADeviceArrangementRequest createMFADeviceArrangementRequestUnblockMobile(String userId,
		String safiDeviceId, String employeeId, String unblockType)
	{
    	if (!unblockType.equalsIgnoreCase(UNBLOCK_TYPE_NETWORK) && !unblockType.equalsIgnoreCase(UNBLOCK_TYPE_DEVICE))
    	{
    		throw new IllegalArgumentException("unblockType: " + unblockType + " is not one of " + UNBLOCK_TYPE_NETWORK + " or " + UNBLOCK_TYPE_DEVICE);
    	}
    	
        //TODO These values are coming from client service (Use userDetailsService)
        String customerNumber = userId;
        String employerNumber = employeeId;

        ObjectFactory objectFactory = SafiObjectFactory.getMaintainDeviceObjectFactory();
        au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ObjectFactory deviceCommonObjectFactory = SafiObjectFactory.getDeviceCommonObjectFactory();
        au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory identifiersObjectFactory = SafiObjectFactory.getIdentifiesObjectFactory();
        MaintainMFADeviceArrangementRequest request = objectFactory.createMaintainMFADeviceArrangementRequest();

        request.setRequestedAction(UpdateAction.UPDATE_AUTHENTICATION_DEVICE);
        MFADeviceArrangement details = deviceCommonObjectFactory.createMFADeviceArrangement();
        InvolvedPartyArrangementRole arrangementRole = deviceCommonObjectFactory.createInvolvedPartyArrangementRole();
        arrangementRole.setRoleType(InvolvedPartyRoleType.USER);
        ArrangementChannelAccessCondition condition = deviceCommonObjectFactory.createArrangementChannelAccessCondition();
        ArrangementAccessCondition access = deviceCommonObjectFactory.createArrangementAccessCondition();

        Individual individual = deviceCommonObjectFactory.createIndividual();

        au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyArrangementRole role = deviceCommonObjectFactory.createInvolvedPartyArrangementRole();
        
        if (unblockType.equalsIgnoreCase(UNBLOCK_TYPE_DEVICE))
        {
        	details.setAuthenticationStatus("ACT");
        }
        
        role.setRoleType(InvolvedPartyRoleType.USER);
        CustomerIdentifier customerIdentifier = identifiersObjectFactory.createCustomerIdentifier();
        customerIdentifier.setCustomerNumber(customerNumber);
        individual.setExternalIdentifier(customerIdentifier);
        //TODO populate party with proper values
        access.setIsGrantedBy(individual);
        condition.getHasArrangementAuthority().add(access);
        arrangementRole.getHasAccessCondition().add(condition);
        //details.getHasArrangementRole().add(arrangementRole);

        MaintenanceTransaction transaction = deviceCommonObjectFactory.createMaintenanceTransaction();
        
        Agent rolePlayer = deviceCommonObjectFactory.createAgent();
        EmployeeIdentifier identifier = identifiersObjectFactory.createEmployeeIdentifier();        
        identifier.setEmployeeNumber(employerNumber);  	// Employee oracle number      
        rolePlayer.setExternalIdentifier(identifier);
        rolePlayer.setAgentRoleType(AgentRoleType.NON_FRAUD);
                
        
        
        // The following block to set the network authentication status to ACT (active)
        // SAFI escalates to a network level lock after the third incorrect authentication attempt.
        // Therefore we always need to perform network unlock.
        if (unblockType.equalsIgnoreCase(UNBLOCK_TYPE_NETWORK))
        {
	        InvolvedPartyArrangementRole involvedPartyArrangementRole = new InvolvedPartyArrangementRole();
	        involvedPartyArrangementRole.setRoleType(InvolvedPartyRoleType.USER);
	        
	        GroupSystemIdentifier groupSystemIdentifier = new GroupSystemIdentifier();
	        groupSystemIdentifier.setGroupId(customerNumber);
	                
	        InvolvedPartyNetwork involvedPartyNetwork = new InvolvedPartyNetwork();
	        involvedPartyNetwork.setInternalIdentifier(groupSystemIdentifier);
	                
	        ArrangementChannelAccessCondition arrangementChannelAccessCondition = new ArrangementChannelAccessCondition();
	        ArrangementAccessCondition arrangementAccessCondition = new ArrangementAccessCondition();
	        
	        details.getHasArrangementRole().add(involvedPartyArrangementRole);
	        details.getHasArrangementRole().get(0).getHasAccessCondition().add(arrangementChannelAccessCondition);
	        details.getHasArrangementRole().get(0).getHasAccessCondition().get(0).getHasArrangementAuthority().add(arrangementAccessCondition);
	        details.getHasArrangementRole().get(0).getHasAccessCondition().get(0).getHasArrangementAuthority().get(0).setLifecycleStatus("ACTIVE");
	        details.getHasArrangementRole().get(0).getHasAccessCondition().get(0).getHasArrangementAuthority().get(0).setIsResultOf(involvedPartyNetwork);
        }

        
        
        Brand brand = deviceCommonObjectFactory.createBrand();
        brand.setBrandCode(BRAND_CODE);
        details.setHasBrand(brand);

        transaction.setIsInitiatedBy(rolePlayer);
        //TODO verity these fields
        transaction.setMaintenanceTransactionType("ADO");
        transaction.setMaintenanceReasonCode("ADMINISTRATIVE_OTHER");
        transaction.setMaintenanceReasonText("Another reason applies to this request");
        details.setHasMaintenanceTransaction(transaction);
        au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier internalId = identifiersObjectFactory.createProductArrangementIdentifier();
        internalId.setArrangementId(safiDeviceId);
        details.setInternalIdentifier(internalId);
        
        request.setArrangement(details);        
        return request;

	}

	private UpdateAction createUpdateAuthDeviceAction()
	{
		UpdateAction updateAction = UpdateAction.UPDATE_AUTHENTICATION_DEVICE;
		return updateAction;
	}

	/**
	 * Sets the device_id for this request (which originates from Avaloq)
	 * @return
	 */
	private ProductArrangementIdentifier createProductArrangementIdentifier(String deviceId)
	{
		ProductArrangementIdentifier pArrangementIdentifier = new ProductArrangementIdentifier();

		pArrangementIdentifier.setArrangementId(deviceId);

		return pArrangementIdentifier;
	}

	private MaintenanceTransaction createMaintenanceTransaction(String employeeNumber)
	{
		MaintenanceTransaction maintenanceTransaction = new MaintenanceTransaction();
		maintenanceTransaction.setMaintenanceReasonCode("Administration");
		maintenanceTransaction.setMaintenanceTransactionType(ServiceConstants.MAINTENANCE_TRANSACTION_TYPE);
		maintenanceTransaction.setMaintenanceReasonText("Another reason applies to this device update request");
		maintenanceTransaction.setIsInitiatedBy(createAgent(employeeNumber));
		return maintenanceTransaction;
	}

	private Brand createBrand(String brandCode)
	{
		Brand brand = new Brand();
		brand.setBrandCode(brandCode);
		return brand;
	}

	private Agent createAgent(String employeeNumber)
	{
		Agent agent = new Agent();
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		AgentRoleType agentRoleType = AgentRoleType.NON_FRAUD;

		employeeIdentifier.setEmployeeNumber(employeeNumber);
		agent.setExternalIdentifier(employeeIdentifier);
		agent.setAgentRoleType(agentRoleType);

		return agent;
	}

	private ElectronicDeliveryDevice createElectronicDeliveryDevice(String mobileNumber)
	{
		ElectronicDeliveryDevice electroncDeliveryDevice = new ElectronicDeliveryDevice();

		SecurityDeviceParameter safiDevice = new SecurityDeviceParameter();
		safiDevice.setName(ServiceConstants.SMSOTP_PHONE_NUMBER);
		safiDevice.setValue(mobileNumber);
		electroncDeliveryDevice.getHasParameter().add(safiDevice);
		return electroncDeliveryDevice;
	}

	private AuthenticationAttemptAuditContext usingAuthenticationAttemptAuditContext()
	{
		AuthenticationAttemptAuditContext authAttemptAuditContext = new AuthenticationAttemptAuditContext();
		return authAttemptAuditContext;
	}

}
