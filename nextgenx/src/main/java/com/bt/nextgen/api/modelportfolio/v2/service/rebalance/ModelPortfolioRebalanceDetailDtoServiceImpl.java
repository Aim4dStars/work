package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ExclusionStatus;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceAccountDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ModelPortfolioRebalanceAccountsDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200") // Concurrency
public class ModelPortfolioRebalanceDetailDtoServiceImpl implements ModelPortfolioRebalanceDetailDtoService {
    @Autowired
    private ModelPortfolioRebalanceIntegrationService rebalanceIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    @Autowired
    private ModelPortfolioHelper helper;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private StaticIntegrationService codeService;

    @Override
    public ModelPortfolioRebalanceDetailDto find(ModelPortfolioKey key, ServiceErrors serviceErrors) {
        List<ModelPortfolioRebalanceAccountDto> rebalAccountList = new ArrayList<>();
        IpsKey ipsKey = IpsKey.valueOf(key.getModelId());

        Concurrent.when(loadAccounts(serviceErrors), loadModelPortfolios(ipsKey, serviceErrors))
                .done(processResults(key, rebalAccountList, serviceErrors)).execute();

        InvestmentPolicyStatementInterface ips = ipsIntegrationService
                .getInvestmentPolicyStatements(Collections.singletonList(ipsKey), serviceErrors).get(ipsKey);

        ModelPortfolioRebalance rebalance = getRebalance(ipsKey, serviceErrors);
        ModelPortfolioRebalanceDetailDto result = new ModelPortfolioRebalanceDetailDto(key, ips.getInvestmentName(),
                ips.getCode(), rebalance == null ? null : rebalance.getTotalAccountsCount(), rebalAccountList);

        return result;
    }

    private ModelPortfolioRebalance getRebalance(IpsKey key, ServiceErrors serviceErrors) {
        BrokerKey broker = helper.getCurrentBroker(serviceErrors);
        List<ModelPortfolioRebalance> summaries = rebalanceIntegrationService.loadModelPortfolioRebalances(broker, serviceErrors);

        for (ModelPortfolioRebalance summary : summaries) {
            if (summary.getIpsKey().equals(key)) {
                return summary;
            }
        }
        return null;
    }

    private ConcurrentCallable<List<RebalanceAccount>> loadModelPortfolios(final IpsKey ipsKey,
            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<RebalanceAccount>>() {
            @Override
            public List<RebalanceAccount> call() {
                return rebalanceIntegrationService.loadModelPortfolioRebalanceAccounts(ipsKey,
                        serviceErrors);
            }
        };
    }

    private ConcurrentCallable<Map<AccountKey, WrapAccount>> loadAccounts(final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Map<AccountKey, WrapAccount>>() {
            @Override
            public Map<AccountKey, WrapAccount> call() {
                return accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors);
            }
        };
    }

    private ConcurrentComplete processResults(final ModelPortfolioKey key, final List<ModelPortfolioRebalanceAccountDto> results,
            final ServiceErrors serviceErrors) {

        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                Map<AccountKey, WrapAccount> accounts = (Map<AccountKey, WrapAccount>) r.get(0).getResult();
                List<RebalanceAccount> rebalances = (List<RebalanceAccount>) r.get(1).getResult();
                results.addAll(buildDtoList(key.getModelId(), accounts, rebalances, serviceErrors));
            }
        };
    }

    private List<ModelPortfolioRebalanceAccountDto> buildDtoList(String modelId, Map<AccountKey, WrapAccount> accounts,
            List<RebalanceAccount> rebalances, ServiceErrors serviceErrors) {
        List<ModelPortfolioRebalanceAccountDto> dtoList = new ArrayList<>();
        Map<BrokerKey, BrokerUser> brokerMap = getBrokerMap(rebalances, serviceErrors);
        for (RebalanceAccount rebalance : rebalances) {
            dtoList.add(buildDto(modelId, rebalance, accounts.get(rebalance.getAccount()), brokerMap.get(rebalance.getAdviser()),
                    serviceErrors));
        }
        return dtoList;
    }

    private ModelPortfolioRebalanceAccountDto buildDto(String modelId, RebalanceAccount rebalance, WrapAccount account,
            BrokerUser adviser, ServiceErrors serviceErrors) {
        ExclusionStatus exclusionStatus = rebalance.getUserExcluded() ? ExclusionStatus.USER_EXCLUDED : ExclusionStatus.INCLUDED;
        String exclusionReason = rebalance.getUserExclusionReason();
        Code code = codeService.loadCode(CodeCategory.MP_REBAL_EXCLUSION_REASON, rebalance.getSystemExclusionReason(), serviceErrors);
        if (code != null) {
            exclusionReason = code.getName();
            exclusionStatus = ExclusionStatus.SYSTEM_EXCLUDED;
        }
        Product product = productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors);
        return new ModelPortfolioRebalanceAccountDto(modelId, rebalance, account, product, adviser, exclusionStatus,
                exclusionReason);
    }

    private Map<BrokerKey, BrokerUser> getBrokerMap(List<RebalanceAccount> rebalances, ServiceErrors serviceErrors) {
        Map<BrokerKey, BrokerUser> brokerMap = new HashMap<>();
        for (RebalanceAccount rebalance : rebalances) {
            if (brokerMap.get(rebalance.getAdviser()) == null) {
                BrokerUser brokerUser = brokerService.getAdviserBrokerUser(rebalance.getAdviser(), serviceErrors);
                brokerMap.put(rebalance.getAdviser(), brokerUser);
            }
        }
        return brokerMap;

    }

}
