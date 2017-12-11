package com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships.MaintainArrangementAndRelationshipIntegrationService;

@Service("maintainArrangementAndRelationshipIntegrationService")
public class GroupEsbRelationshipManagementImpl implements MaintainArrangementAndRelationshipIntegrationService {

    @Autowired
    @Qualifier("maintainArrangementAndRelationshipIntegrationServiceV1")
    private MaintainArrangementAndRelationshipIntegrationService integrationServiceV1;

    @Override
    public CustomerRawData createArrangementAndRelationShip(ArrangementAndRelationshipManagementRequest input,
            ServiceErrors serviceError) {
        CustomerRawData customerRawData = integrationServiceV1.createArrangementAndRelationShip(input, serviceError);
        return customerRawData;
    }

}
