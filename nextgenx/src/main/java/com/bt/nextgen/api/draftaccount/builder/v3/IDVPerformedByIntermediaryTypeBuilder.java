package com.bt.nextgen.api.draftaccount.builder.v3;

import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.onboarding.helper.PartyHelper;
import ns.btfin_com.party.v3_0.IDVPerformedByIntermediaryType;
import org.springframework.stereotype.Service;

/**
 * Build instances of IDV performance intermediaries.
 */
@Service
public class IDVPerformedByIntermediaryTypeBuilder {

    public IDVPerformedByIntermediaryType intermediary(BrokerUser user, Broker dealer) {
        return PartyHelper.intermediary(user.getBankReferenceId(), user.getFirstName(), user.getLastName(), dealer.getPositionName());
    }
}
