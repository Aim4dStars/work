package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.core.web.model.Criterion;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Beneficiary request builder class
 */
public class BeneficiaryRequestBuilder {

    private static final String OE_ID = "oe_id";

    /**
     * Generate request criteria for retrieving beneficiary detail
     * @param brokerKey
     * @return List<Criterion>
     */
    public static List<Criterion> getBeneficiaryDetailsCriteriaRequest(BrokerKey brokerKey) {
        if (brokerKey != null && StringUtils.isBlank(brokerKey.getId())) {
            throw new IllegalArgumentException("Broker Key must be specified");
        }
        List<Criterion> criteria = new ArrayList<>();
        List<String> values = new ArrayList<String>(Arrays.asList(brokerKey.getId()));
        Criterion requestParam = new Criterion(OE_ID, values) {
            @Override
            public boolean isSingleValue() {
                return true;
            }
        };
        criteria.add(requestParam);
        return criteria;
    }
}
