package com.bt.nextgen.service.integration.supermatch;

import com.bt.nextgen.service.btesb.supermatch.model.AtoMoney;

import java.util.List;

/**
 * Interface for the Super match details retrieved from the super search
 */
public interface SuperMatchDetails {

    /**
     * Gets the status summary details for the search
     */
    StatusSummary getStatusSummary();

    /**
     * Gets the list of monies(with categories) held by ATO
     */
    List<AtoMoney> getAtoMonies();

    /**
     * Gets the super fund accounts from the search
     */
    List<SuperFundAccount> getSuperFundAccounts();
}
