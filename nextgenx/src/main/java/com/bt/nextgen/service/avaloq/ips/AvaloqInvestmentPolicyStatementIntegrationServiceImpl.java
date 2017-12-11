package com.bt.nextgen.service.avaloq.ips;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsProductAssociationInterface;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class AvaloqInvestmentPolicyStatementIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        InvestmentPolicyStatementIntegrationService {
    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private CacheIPSIntegrationServiceImpl cacheIPSIntegrationServiceImpl;

    @Override
    public Map<IpsKey, IpsProductAssociationInterface> getAsscociatedProductAndIpsIds(final ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(IpsTemplate.IPS_INVST_OPT);
        IpsProductListHolder response = avaloqService.executeReportRequestToDomain(avaloqRequest, IpsProductListHolder.class,
                serviceErrors);

        Map<IpsKey, List<ProductKey>> ipsProducts = new HashMap<IpsKey, List<ProductKey>>();
        for (IpsProductImpl ipsProduct : response.getIpsList()) {
            List<ProductKey> products = ipsProducts.get(ipsProduct.getIpsKey());
            if (products == null) {
                products = new ArrayList<>();
                ipsProducts.put(ipsProduct.getIpsKey(), products);
            }
            products.add(ipsProduct.getProductKey());
        }
        Map<IpsKey, IpsProductAssociationInterface> result = new HashMap<>();
        for (Entry<IpsKey, List<ProductKey>> entry : ipsProducts.entrySet()) {
            result.put(entry.getKey(), new IpsProductAssociationImpl(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @Override
    public InvestmentPolicyStatementInterface getIPSDetail(IpsIdentifier identifier, ServiceErrors serviceErrors) {
        IpsKey ipsKey = identifier.getIpsKey();
        return cacheIPSIntegrationServiceImpl.loadInvestmentPolicyStatements(serviceErrors).get(ipsKey);
    }

    @Override
    public Map<IpsKey, InvestmentPolicyStatementInterface> getInvestmentPolicyStatements(List<IpsKey> ipsIdList,
            ServiceErrors serviceErrors) {
        Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = cacheIPSIntegrationServiceImpl
                .loadInvestmentPolicyStatements(serviceErrors);
        if (!ipsMap.keySet().containsAll(ipsIdList)) {
            cacheIPSIntegrationServiceImpl.clearCache(serviceErrors);
            ipsMap = cacheIPSIntegrationServiceImpl.loadInvestmentPolicyStatements(serviceErrors);
        }
        return ipsMap;
    }

    @Override
    public Map<IpsKey, InvestmentPolicyStatementInterface> getInvestmentPolicyStatements(ServiceErrors serviceErrors) {
        return cacheIPSIntegrationServiceImpl.loadInvestmentPolicyStatements(serviceErrors);
    }

    @Override
    public Map<IpsKey, InvestmentPolicyStatementInterface> getSelectiveInvestmentPolicyStatements(List<IpsKey> ipsIdList,
            ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(IpsTemplate.IPS_LIST_SELECTIVE).forParam(
                IpsParams.PARAM_IPS_LIST, Lambda.extractProperty(ipsIdList, "id"));
        IpsListHolder response = avaloqService.executeReportRequestToDomain(avaloqRequest, IpsListHolder.class, serviceErrors);
        return Lambda.index(response.getIpsList(), Lambda.on(InvestmentPolicyStatementInterface.class).getIpsKey());
    }

    @Override
    public Map<IpsKey, ModelPortfolioDetail> getModelDetails(List<IpsKey> ipsIdList, ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(IpsTemplate.IPS_LIST_SELECTIVE).forParam(
                IpsParams.PARAM_IPS_LIST, Lambda.extractProperty(ipsIdList, "id"));
        IpsDetailList response = avaloqService.executeReportRequestToDomain(avaloqRequest, IpsDetailList.class, serviceErrors);
        return Lambda.index(response.getIpsList(), Lambda.on(IpsDetails.class).getIpsKey());
    }

    @Override
    public Map<IpsKey, InvestmentPolicyStatementInterface> refreshInvestmentPolicyStatementsCache(ServiceErrors serviceErrors) {
        cacheIPSIntegrationServiceImpl.clearCache(serviceErrors);
        return cacheIPSIntegrationServiceImpl.loadInvestmentPolicyStatements(serviceErrors);
    }

    @Override
    public List<IpsSummaryDetails> getDealerGroupIpsSummary(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(IpsTemplate.IPS_SUMMARY_LIST).forParam(
                IpsParams.PARAM_INVESTMENT_MANAGER_ID, brokerKey.getId());
        IpsSummaryList response = avaloqService.executeReportRequestToDomain(avaloqRequest, IpsSummaryList.class, serviceErrors);
        return response.getSummaryDetailsList();
    }

    @Override
    public List<BrokerKey> getInvestmentManagerFromModel(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        List<IpsSummaryDetails> results = getDealerGroupIpsSummary(brokerKey, serviceErrors);
        List<BrokerKey> brokerList = new ArrayList<>();
        if (results != null) {
            for (IpsSummaryDetails details : results) {
                if (!brokerList.contains(details.getInvestmentManagerId())) {
                    brokerList.add(details.getInvestmentManagerId());
                }
            }
        }
        return brokerList;
    }


    @Override
    public List <InvestmentPolicyStatementInterface> loadInvestmentPolicyStatement(final ServiceErrors serviceErrors)
    {
        final List <InvestmentPolicyStatementInterface> result = new ArrayList <InvestmentPolicyStatementInterface>();
        new IntegrationOperation("loadInvestmentPolicyStatement", serviceErrors)
        {
            @Override
            public void performOperation()
            {
                Map<IpsKey, InvestmentPolicyStatementInterface> impls = getInvestmentPolicyStatements(serviceErrors);
                result.addAll(impls.values());
            }
        }.run();
        return result;

    }

    @Override
    /**
     * {@inheritDoc}
     */
    public InvestmentPolicyStatementInterface loadInvestmentPolicyStatement(String ipsId, ServiceErrors serviceErrors)
    {
        Map<IpsKey, InvestmentPolicyStatementInterface> cached = getInvestmentPolicyStatements(serviceErrors);

        if (cached != null)
        {
            return cached.get(IpsKey.valueOf(ipsId));
        }

        return null;
    }
}