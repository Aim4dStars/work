package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class PolicyTrackingDto extends BaseDto implements KeyedDto<BrokerKey> {

    private BrokerKey key;
    private String lastSelectedFNumber;
    private String lastSelectedAdviser;
    private List<String> fNumberList;

    public String getLastSelectedFNumber() {
        return lastSelectedFNumber;
    }

    public void setLastSelectedFNumber(String lastSelectedFNumber) {
        this.lastSelectedFNumber = lastSelectedFNumber;
    }

    public String getLastSelectedAdviser() {
        return lastSelectedAdviser;
    }

    public void setLastSelectedAdviser(String lastSelectedAdviser) {
        this.lastSelectedAdviser = lastSelectedAdviser;
    }

    public List<String> getFNumberList() {
        return fNumberList;
    }

    public void setFNumberList(List<String> fNumberList) {
        this.fNumberList = fNumberList;
    }

    public void setKey(BrokerKey key) {
        this.key = key;
    }

    @Override
    public BrokerKey getKey() {
        return key;
    }
}
