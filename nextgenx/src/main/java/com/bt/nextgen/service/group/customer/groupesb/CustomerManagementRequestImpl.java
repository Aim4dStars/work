package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.service.integration.user.CISKey;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by F057654 on 27/07/2015.
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck","squid:S1142", "squid:MethodCyclomaticComplexity"})
public class CustomerManagementRequestImpl implements CustomerManagementRequest {

    private List<CustomerManagementOperation> operationTypes;
    private CISKey cisKey;
    private RoleType involvedPartyRoleType;

    public CustomerManagementRequestImpl(CISKey cisKey, RoleType involvedPartyRoleType, List<CustomerManagementOperation> operationTypes) {
        this.cisKey = cisKey;
        this.involvedPartyRoleType = involvedPartyRoleType;
        this.operationTypes = operationTypes;
    }

    public CustomerManagementRequestImpl(String cisKey, RoleType involvedPartyRoleType, CustomerManagementOperation... operationTypes) {
        this(CISKey.valueOf(cisKey), involvedPartyRoleType, asList(operationTypes));
    }

    public CustomerManagementRequestImpl() {
    }

    @Override
    public CISKey getCISKey() {
        return cisKey;
    }

    public void setCISKey(CISKey cisKey) {
        this.cisKey = cisKey;
    }

    @Override
    public List<CustomerManagementOperation> getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(List<CustomerManagementOperation> operationTypes) {
        this.operationTypes = operationTypes;
    }

    @Override
    public RoleType getInvolvedPartyRoleType() {
        return involvedPartyRoleType;
    }

    public void setInvolvedPartyRoleType(RoleType involvedPartyRoleType) {
        this.involvedPartyRoleType = involvedPartyRoleType;
    }

    @Override
    public String toString() {
        return "CustomerManagementRequestImpl{cisKey:" + cisKey + "; involvedPartyRoleType:" + involvedPartyRoleType
                + "; operationTypes:" + operationTypes + "}";
    }
}
