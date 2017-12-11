package com.bt.nextgen.serviceops.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.EmployeeIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.GroupSystemIdentifier;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Agent;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.AgentRoleType;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ArrangementAccessCondition;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.ArrangementChannelAccessCondition;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Brand;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.Individual;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyArrangementRole;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyNetwork;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.InvolvedPartyRoleType;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.MFADeviceArrangement;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.common.xsd.v1.MaintenanceTransaction;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementRequest;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.MaintainMFADeviceArrangementResponse;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.ObjectFactory;
import au.com.westpac.gn.resourceitemmanagement.services.devicemanagement.xsd.maintainmfadevicearrangement.v1.svc0276.UpdateAction;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.SafiObjectFactory;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;

import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
public class UpdateDeviceArrangementServiceImpl implements UpdateDeviceArrangementService 
{
    Logger logger = LoggerFactory.getLogger(UpdateDeviceArrangementServiceImpl.class);
    @Autowired
    private WebServiceProvider serviceProvider;
    @Autowired
    @Qualifier("marshaller")
    private Jaxb2Marshaller marshaller;
	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;
	
	private static final String UNBLOCK_TYPE_NETWORK = "NETWORK";
	private static final String UNBLOCK_TYPE_DEVICE = "DEVICE";
	
	
	private static final String BRAND_CODE = "BTNG";

    @Override
    public String unBlockMobile(String p, ServiceOpsModel serviceOpsModel, String employeeId) 
    {
        try
        {
            MaintainMFADeviceArrangementRequest request = makeRequest(p, serviceOpsModel, employeeId, UNBLOCK_TYPE_NETWORK);            
            MaintainMFADeviceArrangementResponse response = (MaintainMFADeviceArrangementResponse) 
            		serviceProvider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken() ,Attribute.GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS, request);
                        
            MaintainMFADeviceArrangementRequest request2 = makeRequest(p, serviceOpsModel, employeeId, UNBLOCK_TYPE_DEVICE);            
            MaintainMFADeviceArrangementResponse response2 = (MaintainMFADeviceArrangementResponse) 
            		serviceProvider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken() ,Attribute.GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS, request2);            
            
            return "";
        } catch (Exception e) {
            logger.error("Error in unblocking mobile: ", e);
        }
        return null;
    }

    private MaintainMFADeviceArrangementRequest makeRequest(String clientId, ServiceOpsModel serviceOpsModel, String employeeId, String unblockType) 
    {

    	if (!unblockType.equalsIgnoreCase(UNBLOCK_TYPE_NETWORK) && !unblockType.equalsIgnoreCase(UNBLOCK_TYPE_DEVICE))
    	{
    		throw new IllegalArgumentException("unblockType: " + unblockType + " is not one of " + UNBLOCK_TYPE_NETWORK + " or " + UNBLOCK_TYPE_DEVICE);
    	}
    	
        //TODO These values are coming from client service (Use userDetailsService)
        String customerNumber = serviceOpsModel.getUserId();
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
        internalId.setArrangementId(serviceOpsModel.getSafiDeviceId());
        details.setInternalIdentifier(internalId);
        
        request.setArrangement(details);        
        return request;
    }

}
