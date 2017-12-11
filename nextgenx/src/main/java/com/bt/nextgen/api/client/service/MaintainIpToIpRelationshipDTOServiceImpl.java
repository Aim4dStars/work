package com.bt.nextgen.api.client.service;

import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.InvolvedParty;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.InvolvedPartyRelationshipRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.InvolvedPartyTo;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.MaintenanceAuditContext;

import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb.IpToIpRelationshipRequest;
import com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb.MaintainIpToIpRelationshipIntegrationService;
import com.bt.nextgen.serviceops.model.MaintainIpToIpRelationshipReqModel;

@Service("maintainIpToIpRelationship")
@SuppressWarnings("squid:S1200")
public class MaintainIpToIpRelationshipDTOServiceImpl implements MaintainIpToIpRelationshipDTOService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainIpToIpRelationshipDTOServiceImpl.class);

    private static final String USECASE_1="Add";
    
    private static final String USECASE_2="Modify";
    
    @Autowired
    @Qualifier("maintainIpToIpRelationshipIntegrationService")
    private MaintainIpToIpRelationshipIntegrationService integrationService;

    @Override
    public CustomerRawData maintainIpToIpRelationship(MaintainIpToIpRelationshipReqModel input, ServiceErrors serviceError) {
        LOGGER.info("Loading MaintainIpToIpRelationshipDTOServiceImpl.updateIpToIpRelationship()..");
        IpToIpRelationshipRequest req = createIpToIpRelationshipRequest(input);
        CustomerRawData customerData = integrationService.maintainIpToIpRelationship(req, serviceError);
        return customerData;
    }

	@SuppressWarnings("deprecation")
	private IpToIpRelationshipRequest createIpToIpRelationshipRequest(MaintainIpToIpRelationshipReqModel input) {
		
		InvolvedParty involvedParty = new InvolvedParty();
	   
		InvolvedPartyType sourceIPType= new InvolvedPartyType();
		sourceIPType.setValue(input.getSourcePersonType());
		involvedParty.setInvolvedPartyType(sourceIPType);

		InvolvedPartyIdentifier sourceIPIdentifier = new InvolvedPartyIdentifier();
		sourceIPIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		sourceIPIdentifier.setInvolvedPartyId(input.getSourceCISKey());
	    involvedParty.getInvolvedPartyIdentifier().add(sourceIPIdentifier);
		
	    InvolvedPartyRelationshipRole involvedPartyRelationshipRole = new InvolvedPartyRelationshipRole();
	    involvedPartyRelationshipRole.setLifecycleStatus(input.getPartyRelStatus());
	    Action action = new Action();
	    action.setValue(input.getUseCase());
	    involvedPartyRelationshipRole.setRequestedAction(action);
	    involvedPartyRelationshipRole.setRoleType(input.getPartyRelType());
	    involvedPartyRelationshipRole.setStartDate(XMLGregorianCalendarUtil.convertToXMLGregorianCalendar(new DateTime(new Date(input.getPartyRelStartDate()))));

	    InvolvedPartyTo hasForContext = new InvolvedPartyTo();
	    MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
	    if(USECASE_2.equalsIgnoreCase(input.getUseCase())){
	    	maintenanceAuditContext.setVersionNumber(input.getVersionNumber());
			involvedPartyRelationshipRole.setEndDate(XMLGregorianCalendarUtil.convertToXMLGregorianCalendar(new DateTime(new Date(input.getPartyRelEndDate()))));
	    }
	    hasForContext.setAuditContext(maintenanceAuditContext);

	    InvolvedPartyType targetInvolvedPartyType = new InvolvedPartyType();
		targetInvolvedPartyType.setValue(input.getTargetPersonType());
		hasForContext.setInvolvedPartyType(targetInvolvedPartyType);

		InvolvedPartyIdentifier targetIPIdentifier = new InvolvedPartyIdentifier();
		targetIPIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		targetIPIdentifier.setInvolvedPartyId(input.getTargetCISKey());
		hasForContext.getInvolvedPartyIdentifier().add(targetIPIdentifier);

	    involvedPartyRelationshipRole.setHasForContext(hasForContext);
	    involvedParty.getHasRelationshipRole().add(involvedPartyRelationshipRole);
	    
	    IpToIpRelationshipRequest request = new IpToIpRelationshipRequest();
		request.setInvolvedParty(involvedParty);
		return request;
	}

}
