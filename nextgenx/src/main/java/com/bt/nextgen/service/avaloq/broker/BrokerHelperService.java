package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.service.integration.broker.Broker;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by L070815 on 13/01/2015.
 */
public interface BrokerHelperService {

    public Broker getAdviserForInvestor(WrapAccount account, ServiceErrors serviceErrors);

    public UserExperience getUserExperience(ApplicationDocumentDetail applicationDocument, ServiceErrors serviceErrors);

    public Broker getDealerGroupForInvestor(WrapAccount account, ServiceErrors serviceErrors);

    public Set<Broker> getDealerGroupsforInvestor(ServiceErrors serviceErrors);

    /**
     * @deprecated due to the fact that an investor may associated with more than one advisers, use
     * {@link #getAdviserForInvestor(BankingCustomerIdentifier,ServiceErrors)} method to obtain all the Brokers
     * associated with an Investor
     *
     * @param customerIdentifier
     * @param serviceErrors
     * @return
     */
    @Deprecated
    public Broker getAdviserForInvestor(BankingCustomerIdentifier customerIdentifier, ServiceErrors serviceErrors);

    public List<Broker> getAdviserListForInvestor(BankingCustomerIdentifier customerIdentifier, ServiceErrors serviceErrors);

    /**
     * This method should only be used for ServiceOps related logic.
     *
     * @param customerIdentifier
     * @param serviceErrors
     * @return
     */
    public List<Broker> getDealerGroupForIntermediary(BankingCustomerIdentifier customerIdentifier, ServiceErrors serviceErrors);

    public Broker getDealerGroupForIntermediary(JobProfileIdentifier userKey, ServiceErrors serviceErrors);

    /**
     * Returns true if investor is BT-Direct investor
     *
     * @return
     */

    public boolean isDirectInvestor(WrapAccount account, ServiceErrors serviceErrors);

    /**
     * Returns the user experience for the adviser of an account
     * 
     * @param account
     * @param serviceErrors
     * @return
     */
    public UserExperience getUserExperience(WrapAccount account, ServiceErrors serviceErrors);

    public Map<BrokerKey,Broker> loadBrokersByIdList(List<BrokerKey> brokerKeys, ServiceErrors serviceErrors);

    public String getBrandSiloForIntermediary(JobProfileIdentifier userKey, ServiceErrors serviceErrors);

    public String getBrandSiloForInvestor(ServiceErrors serviceErrors);
}
