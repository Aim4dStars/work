package com.bt.nextgen.service.integration.ips;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;

import java.util.List;
import java.util.Map;

/**
 * Integration service related to Investment Policy Statement
 */
public interface InvestmentPolicyStatementIntegrationService {

    /**
     * Returns a Map with Key as IFS Id and IFS Object containing List of Associated Product Ids.
     *
     * @param serviceErrors
     * @return
     */
    Map<IpsKey, IpsProductAssociationInterface> getAsscociatedProductAndIpsIds(ServiceErrors serviceErrors);

    /**
     * Returns the IPS details for a particular IPS Id
     *
     * @param ipsId
     * @param serviceErrors
     * @return
     */
    InvestmentPolicyStatementInterface getIPSDetail(IpsIdentifier identifier, ServiceErrors serviceErrors);

    Map<IpsKey, InvestmentPolicyStatementInterface> getInvestmentPolicyStatements(List<IpsKey> ipsIdList,
                                                                                  final ServiceErrors serviceErrors);

    Map<IpsKey, InvestmentPolicyStatementInterface> getInvestmentPolicyStatements(final ServiceErrors serviceErrors);

    Map<IpsKey, InvestmentPolicyStatementInterface> getSelectiveInvestmentPolicyStatements(List<IpsKey> ipsIdList,
                                                                                           final ServiceErrors serviceErrors);

    Map<IpsKey, ModelPortfolioDetail> getModelDetails(List<IpsKey> ipsIdList, ServiceErrors serviceErrors);

    Map<IpsKey, InvestmentPolicyStatementInterface> refreshInvestmentPolicyStatementsCache(ServiceErrors serviceErrors);

    List<IpsSummaryDetails> getDealerGroupIpsSummary(BrokerKey brokerKey, ServiceErrors serviceErrors);

    List<BrokerKey> getInvestmentManagerFromModel(BrokerKey brokerKey, ServiceErrors serviceErrors);


    List<InvestmentPolicyStatementInterface> loadInvestmentPolicyStatement(ServiceErrors serviceErrors);

    /**
     * This method will return the investment policy statement for a given ips id.
     *
     * @param ipsId
     * @param serviceErrors
     * @return InvestmentPolicyStatementInterface
     */
    InvestmentPolicyStatementInterface loadInvestmentPolicyStatement(String ipsId, ServiceErrors serviceErrors);
}
