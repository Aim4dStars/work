package com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.MaintainArrangementAndIPArrangementRelationshipsRequest;

@SuppressWarnings("squid:S1200")
public final class GroupEsbRelationshipManagementRequestV1Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbRelationshipManagementRequestV1Builder.class);

    private GroupEsbRelationshipManagementRequestV1Builder() {
    }

    public static MaintainArrangementAndIPArrangementRelationshipsRequest createMaintainAndArrangementRelationshipRequest(
            ArrangementAndRelationshipManagementRequest input) {

        final au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ObjectFactory factory =
                new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ObjectFactory();
        MaintainArrangementAndIPArrangementRelationshipsRequest req =
                factory.createMaintainArrangementAndIPArrangementRelationshipsRequest();
        req = setRequestParameters(req, input);

        return req;
    }

    /**
     * This method is used to set request parameters received from the input by user.
     * @param req
     * @param request
     */
    private static MaintainArrangementAndIPArrangementRelationshipsRequest setRequestParameters(
            MaintainArrangementAndIPArrangementRelationshipsRequest request,
            ArrangementAndRelationshipManagementRequest input) {

        Class sClass = request.getClass();

        Field f1;
        try {
            f1 = sClass.getDeclaredField("arrangement");
            FieldUtils.writeField(f1, request,input.getArrangement(), true);
            //f1.setAccessible(true);

            //f1.set(request, input.getArrangement());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error("Error setting request parameters", e);
        }

        request.setRequestedAction(input.getRequestedAction());

        return request;

    }
}
