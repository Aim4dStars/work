package com.bt.nextgen.service.integration.rollover;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface CashRolloverService {

    public List<SuperfundDetails> loadAvailableSuperfunds(ServiceErrors serviceErrors);

    public RolloverDetails submitRolloverInDetails(RolloverDetails rolloverDetails, ServiceErrors serviceErrors);

    public RolloverDetails saveRolloverDetails(RolloverDetails rolloverDetails, ServiceErrors serviceErrors);

    public RolloverDetails discardRolloverDetails(String rolloverId, ServiceErrors serviceErrors);

    public RolloverDetails loadRolloverDetails(String rolloverId, ServiceErrors serviceErrors);

}
