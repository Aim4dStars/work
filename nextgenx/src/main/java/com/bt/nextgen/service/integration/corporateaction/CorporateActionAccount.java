package com.bt.nextgen.service.integration.corporateaction;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.broker.BrokerKey;

public interface CorporateActionAccount {

    /**
     * The CA Account number
     * 
     * @return account number
     */
    @NotNull
    String getPositionId();

    /**
     * The CA Account number
     * 
     * @return account number
     */
    @NotNull
    String getAccountId();

    /**
     * The logged in adviser id
     * 
     * @return adviser id
     */

    BrokerKey getAdviserId();

    /**
     * The portfolio id
     * 
     * @return product id
     */

    String getProductId();

    /**
     * The IPS id
     * 
     * @return IPS id
     */

    String getIpsId();

    /**
     * The Container Type id
     * 
     * @return Container Type id
     */
    ContainerType getContainerType();

    /**
     * The Account's current holding
     * 
     * @return eligible quantity
     */

    BigDecimal getEligibleQuantity();

    /**
     * The Account's current holding
     * 
     * @return available quantity
     */

    BigDecimal getAvailableQuantity();

    /**
     * The CA election status
     * 
     * @return election status
     */

    CorporateActionAccountParticipationStatus getElectionStatus();

    /**
     * The CA decisions
     * 
     * @return list of CorporateActionOption which is basically name-value pair
     */

    List<CorporateActionOption> getDecisions();

    /**
     * Available cash of the account.
     * 
     * @return
     */
    BigDecimal getAvailableCash();
}