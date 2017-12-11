package com.bt.nextgen.api.profile.v1.model;

import java.util.List;

public class UnderlyingRoleDto {

    private static final int BROKER_NAME_LIMIT = 5;

    private String profileId;
    private String dealerGroupName;
    private List<String> brokerNames;
    private boolean active;

    public UnderlyingRoleDto(String profileId, String dealerGroupName, List<String> brokerNames, boolean active) {
        this.profileId = profileId;
        this.dealerGroupName = dealerGroupName;
        this.brokerNames = brokerNames;
        this.active = active;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public List<String> getBrokerNames() {
        if (brokerNames.size() > BROKER_NAME_LIMIT) {
            return brokerNames.subList(0, BROKER_NAME_LIMIT);
        }
        return brokerNames;
    }

    public int getBrokerCount() {
        return brokerNames.size();
    }

    public boolean isActive() {
        return active;
    }
}
