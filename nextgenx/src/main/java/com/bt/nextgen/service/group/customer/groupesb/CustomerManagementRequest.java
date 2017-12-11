package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.service.integration.user.CISIdentifier;
import com.bt.nextgen.service.integration.user.CISKey;

import java.util.List;

/**
 * Created by F057654 on 24/07/2015.
 */
public interface CustomerManagementRequest extends CISIdentifier{

    public void setCISKey(CISKey cisKey);

    public List<CustomerManagementOperation> getOperationTypes();

    public void setOperationTypes(List<CustomerManagementOperation> operationTypes);

    public RoleType getInvolvedPartyRoleType();

    public void setInvolvedPartyRoleType(RoleType involvedPartyRoleType);
}
