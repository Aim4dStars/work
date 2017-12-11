package com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.MaintainIPToIPRelationshipsRequest;

import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.GroupEsbRelationshipManagementRequestV1Builder;

public class GroupEsbIpToIpManagementRequestV1Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbRelationshipManagementRequestV1Builder.class);

    private GroupEsbIpToIpManagementRequestV1Builder() {
    }

    public static MaintainIPToIPRelationshipsRequest maintainIpToIpRelation(IpToIpRelationshipRequest input) {
    	au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.ObjectFactory of = 
    			new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.ObjectFactory();
    	
    	MaintainIPToIPRelationshipsRequest request = of.createMaintainIPToIPRelationshipsRequest();
    	
    	request = setRequestParameters(request, input);
    	
        return request;
    }
    
    /**
     * This method is used to set request parameters received from the input by user.
     * @param req
     * @param request
     */
    private static MaintainIPToIPRelationshipsRequest setRequestParameters(
    		MaintainIPToIPRelationshipsRequest request,
    		IpToIpRelationshipRequest input) {

        @SuppressWarnings("rawtypes")
		Class sClass = request.getClass();

        Field f1;
        try {
            f1 = sClass.getDeclaredField("involvedParty");
           //f1.setAccessible(true);
            
            FieldUtils.writeField(f1, request,input.getInvolvedParty(), true);

            //f1.set(request, input.getInvolvedParty());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error("Error setting request parameters", e);
        }

        return request;

    }
}
