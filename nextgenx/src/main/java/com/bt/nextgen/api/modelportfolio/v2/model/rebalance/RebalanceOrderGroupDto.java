package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;

public class RebalanceOrderGroupDto extends BaseDto {

    private String modelName;
    private String modelSymbol;
    private BrokerUser adviser;
    private DateTime rebalanceDate;
    private List<RebalanceOrderDetailsDto> orderDetails;

    public RebalanceOrderGroupDto(RebalanceOrderGroupDto rebalanceOrderGroup, List<RebalanceOrderDetailsDto> orderDetails) {
        super();
        this.modelName = rebalanceOrderGroup.modelName;
        this.modelSymbol = rebalanceOrderGroup.modelSymbol;
        this.adviser = rebalanceOrderGroup.adviser;
        this.rebalanceDate = rebalanceOrderGroup.rebalanceDate;
        this.orderDetails = orderDetails;
    }

    public RebalanceOrderGroupDto(BrokerUser adviser, RebalanceOrderGroup orders, List<RebalanceOrderDetailsDto> orderDetails) {
        super();
        this.modelName = orders.getModelName();
        this.modelSymbol = orders.getModelSymbol();
        this.adviser = adviser;
        this.rebalanceDate = orders.getRebalanceDate();
        this.orderDetails = orderDetails;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelSymbol() {
        return modelSymbol;
    }

    public String getAdviserName() {
        StringBuilder builder = new StringBuilder();
        if (adviser != null) {
            if (!StringUtils.isBlank(adviser.getFirstName())) {
                builder.append(adviser.getFirstName());
            }
            if (!StringUtils.isBlank(adviser.getMiddleName())) {
                builder.append(" ");
                builder.append(adviser.getMiddleName());
            }
            if (!StringUtils.isBlank(adviser.getLastName())) {
                builder.append(" ");
                builder.append(adviser.getLastName());
            }
        }
        return builder.toString();
    }

    public String getAdviserNumber() {
        if (adviser != null) {
            return adviser.getBankReferenceId();
        }
        return null;
    }

    public DateTime getRebalanceDate() {
        return rebalanceDate;
    }

    public List<RebalanceOrderDetailsDto> getOrderDetails() {
        return orderDetails;
    }

}
