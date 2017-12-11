package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceExclusion;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.trxservice.rebal.v1_0.RebalReq;
import com.btfin.abs.trxservice.rebaldet.v1_0.RebalDetReq;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("avaloqModelPortfolioRebalanceIntegrationService")
@SuppressWarnings("squid:S1200")
public class ModelPortfolioRebalanceIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements ModelPortfolioRebalanceIntegrationService {

    @Autowired
    private ModelRebalanceConverter rebalanceConverter;

    @Autowired
    private ModelPortfolioExclusionConverter exclusionConverter;

    @Autowired
    private ModelPortfolioSubmitConverter submitConverter;

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Override
    public List<ModelPortfolioRebalance> loadModelPortfolioRebalances(BrokerKey investmentManagerKey,
            ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelPortfolioRebalanceTemplate.REBALANCE_SUMMARY)
                .forParam(ModelPortfolioRebalanceParams.PARAM_INVESTMENT_MANAGER_ID, investmentManagerKey.getId());
        ModelPortfolioRebalanceResponseImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                ModelPortfolioRebalanceResponseImpl.class, serviceErrors);
        if (response.getModelPortfolioRebalances() != null) {
            return response.getModelPortfolioRebalances();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public ModelPortfolioRebalance updateModelPortfolioRebalance(BrokerKey broker, final IpsKey ipsKey,
            final RebalanceAction action, final ServiceErrors serviceErrors) {
        RebalReq req = rebalanceConverter.toSubmitRequest(ipsKey, action);
        ModelRebalanceUpdateResponseImpl rsp = webserviceClient.sendToWebService(req, AvaloqOperation.REBAL_REQ,
                ModelRebalanceUpdateResponseImpl.class, serviceErrors);
        rebalanceConverter.processErrors(rsp);
        List<ModelPortfolioRebalance> rebalances = loadModelPortfolioRebalances(broker, serviceErrors);
        for (ModelPortfolioRebalance rebalance : rebalances) {
            if (ipsKey.equals(rebalance.getIpsKey())) {
                return rebalance;
            }
        }
        return null;
    }

    @Override
    public ModelPortfolioRebalance submitModelPortfolioRebalance(BrokerKey broker, IpsKey ipsKey, ServiceErrors serviceErrors) {
        List<RebalanceAccount> accounts = loadModelPortfolioRebalanceAccounts(ipsKey, serviceErrors);
        RebalDetReq req = submitConverter.toSubmitRequest(accounts);
        ModelPortfolioSubmitResponseImpl rsp = webserviceClient.sendToWebService(req, AvaloqOperation.REBAL_DET_REQ,
                ModelPortfolioSubmitResponseImpl.class, serviceErrors);
        submitConverter.processErrors(rsp);
        List<ModelPortfolioRebalance> rebalances = loadModelPortfolioRebalances(broker, serviceErrors);
        for (ModelPortfolioRebalance rebalance : rebalances) {
            if (ipsKey.equals(rebalance.getIpsKey())) {
                return rebalance;
            }
        }
        return null;
    }

    @Override
    public void updateRebalanceExclusions(BrokerKey broker, final IpsKey ipsKey, List<RebalanceExclusion> exclusions,
            final ServiceErrors serviceErrors) {
        List<RebalanceAccount> accounts = loadModelPortfolioRebalanceAccounts(ipsKey, serviceErrors);
        RebalDetReq req = exclusionConverter.toExcludeRequest(accounts, exclusions);
        ModelPortfolioExclusionResponseImpl rsp = webserviceClient.sendToWebService(req, AvaloqOperation.REBAL_DET_REQ,
                ModelPortfolioExclusionResponseImpl.class, serviceErrors);
        exclusionConverter.processErrors(rsp);
    }

    @Override
    public List<RebalanceAccount> loadModelPortfolioRebalanceAccounts(IpsKey ipsKey, ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelPortfolioRebalanceTemplate.REBALANCE_ACCOUNTS)
                .forParam(ModelPortfolioRebalanceParams.PARAM_IPS_ID, ipsKey.getId());
        return avaloqService.executeReportRequestToDomain(avaloqRequest, RebalanceAccountsResponseImpl.class, serviceErrors)
                .getAccountRebalances();
    }

    @Override
    public List<RebalanceOrderGroup> loadRebalanceOrdersForIps(IpsKey ipsKey, ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelPortfolioRebalanceTemplate.REBALANCE_ORDERS).forParam(
                ModelPortfolioRebalanceParams.PARAM_IPS_ID, ipsKey.getId());
        return avaloqService.executeReportRequestToDomain(avaloqRequest, RebalanceOrdersResponseImpl.class, serviceErrors)
                .getRebalanceOrders();
    }

    @Override
    public List<RebalanceOrderGroup> loadRebalanceOrders(List<String> docDetIds, ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelPortfolioRebalanceTemplate.REBALANCE_ORDERS).forParam(
                ModelPortfolioRebalanceParams.PARAM_DOC_DET_LIST, docDetIds);
        return avaloqService.executeReportRequestToDomain(avaloqRequest, RebalanceOrdersResponseImpl.class, serviceErrors)
                .getRebalanceOrders();
    }
}
