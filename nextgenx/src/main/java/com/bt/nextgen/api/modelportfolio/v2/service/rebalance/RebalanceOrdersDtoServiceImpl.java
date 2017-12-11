package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrdersDto;
import com.bt.nextgen.api.modelportfolio.v2.service.TailorMadePortfolioDtoService;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.util.rebalance.RebalanceOrdersSortingHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("RebalanceOrdersDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200")
// Concurrency
public class RebalanceOrdersDtoServiceImpl implements RebalanceOrdersDtoService {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private TailorMadePortfolioDtoService tmpDtoService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private ModelPortfolioHelper helper;

    @Autowired
    private RebalanceOrdersSortingHelper sortingHelper;

    @Autowired
    private ModelPortfolioRebalanceIntegrationService rebalanceIntegrationService;

    private static final String FULL_SELL_TEXT = "Sell all";

    @Override
    public RebalanceOrdersDto find(IpsKey ipsKey, ServiceErrors serviceErrors) {

        List<RebalanceOrderGroupDto> orderGroupDtoList = new ArrayList<>();

        Concurrent.when(loadAccounts(serviceErrors), loadRebalanceOrdersForIps(ipsKey, serviceErrors))
                .done(processResults(orderGroupDtoList, serviceErrors)).execute();

        return new RebalanceOrdersDto(ipsKey, orderGroupDtoList);
    }

    @Override
    public RebalanceOrdersDto findByDocIds(IpsKey ipsKey, List<String> rebalDocDetIds, ServiceErrors serviceErrors) {

        List<RebalanceOrderGroupDto> orderGroupDtoList = new ArrayList<>();

        Concurrent.when(loadAccounts(serviceErrors), loadRebalanceOrders(rebalDocDetIds, serviceErrors))
                .done(processResults(orderGroupDtoList, serviceErrors)).execute();

        return new RebalanceOrdersDto(ipsKey, orderGroupDtoList);
    }

    private ConcurrentCallable<Map<AccountKey, WrapAccount>> loadAccounts(final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Map<AccountKey, WrapAccount>>() {

            @Override
            public Map<AccountKey, WrapAccount> call() {
                return accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors);
            }
        };
    }

    private ConcurrentCallable<List<RebalanceOrderGroup>> loadRebalanceOrdersForIps(final IpsKey ipsKey,
            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<RebalanceOrderGroup>>() {

            @Override
            public List<RebalanceOrderGroup> call() {
                return rebalanceIntegrationService.loadRebalanceOrdersForIps(ipsKey, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<List<RebalanceOrderGroup>> loadRebalanceOrders(final List<String> rebalDocDetIds,
            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<RebalanceOrderGroup>>() {

            @Override
            public List<RebalanceOrderGroup> call() {
                return rebalanceIntegrationService.loadRebalanceOrders(rebalDocDetIds, serviceErrors);
            }
        };
    }

    private ConcurrentComplete processResults(final List<RebalanceOrderGroupDto> results, final ServiceErrors serviceErrors) {
        return new AbstractConcurrentComplete() {

            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                Map<AccountKey, WrapAccount> accountMap = (Map<AccountKey, WrapAccount>) r.get(0).getResult();
                List<RebalanceOrderGroup> orderGroupList = (List<RebalanceOrderGroup>) r.get(1).getResult();
                results.addAll(buildOrderGroupDtos(accountMap, orderGroupList, serviceErrors));
            }
        };
    }

    private List<RebalanceOrderGroupDto> buildOrderGroupDtos(Map<AccountKey, WrapAccount> accountMap,
            List<RebalanceOrderGroup> orderGroupList, ServiceErrors serviceErrors) {

        if (orderGroupList != null && !orderGroupList.isEmpty()) {

            List<RebalanceOrderGroupDto> orderGroupDtoList = new ArrayList<>();
            Map<String, Asset> assetMap = getAssetMap(orderGroupList, serviceErrors);
            Map<BrokerKey, BrokerUser> brokerMap = getBrokerMap(orderGroupList, serviceErrors);

            for (RebalanceOrderGroup orderGroup : orderGroupList) {
                List<RebalanceOrderDetailsDto> orderDetailsDtoList = buildOrderDetailsDtos(orderGroup.getOrderDetails(),
                        assetMap, accountMap, serviceErrors);

                orderGroupDtoList.add(new RebalanceOrderGroupDto(brokerMap.get(orderGroup.getAdviser()), orderGroup,
                        orderDetailsDtoList));
            }

            orderGroupDtoList = groupByAdviser(orderGroupDtoList);
            sortingHelper.basicSort(orderGroupDtoList);

            return orderGroupDtoList;
        }

        return Collections.emptyList();
    }

    private Map<String, Asset> getAssetMap(List<RebalanceOrderGroup> orderGroupList, ServiceErrors serviceErrors) {

        Set<String> assetIds = new HashSet<String>();
        for (RebalanceOrderGroup orderGroup : orderGroupList) {
            for (RebalanceOrderDetails orderDetails : orderGroup.getOrderDetails()) {
                if (orderDetails != null) {
                    assetIds.add(orderDetails.getAsset());
                }
            }
        }
        return assetService.loadAssets(assetIds, serviceErrors);
    }

    private Map<BrokerKey, BrokerUser> getBrokerMap(List<RebalanceOrderGroup> orderGroupList, ServiceErrors serviceErrors) {

        Map<BrokerKey, BrokerUser> brokerMap = new HashMap<>();
        for (RebalanceOrderGroup orderGroup : orderGroupList) {
            if (orderGroup.getAdviser() != null && brokerMap.get(orderGroup.getAdviser()) == null) {
                BrokerUser brokerUser = brokerService.getAdviserBrokerUser(orderGroup.getAdviser(), serviceErrors);
                brokerMap.put(orderGroup.getAdviser(), brokerUser);
            }
        }
        return brokerMap;
    }

    private List<RebalanceOrderDetailsDto> buildOrderDetailsDtos(List<RebalanceOrderDetails> orderDetailsList,
            Map<String, Asset> assetMap, Map<AccountKey, WrapAccount> accountMap, ServiceErrors serviceErrors) {

        List<RebalanceOrderDetailsDto> orderDetailsDtoList = new ArrayList<>();

        AssetDto tmpAssetDto = tmpDtoService.findOne(serviceErrors);

        Map<String, Boolean> fullRedemptionMap = storeFullRedemption(orderDetailsList, assetMap, tmpAssetDto);

        for (RebalanceOrderDetails orderDetails : orderDetailsList) {
            BigDecimal estimatedPrice = null;
            if (showPrice(orderDetails)) {
                estimatedPrice = orderDetails.getCurrentValue().divide(orderDetails.getCurrentQuantity(),
                        new MathContext(6, RoundingMode.HALF_UP));
            }

            String comments = orderDetails.getReasonForExclusion();
            if (comments == null && orderDetails.getIsSellAll()) {
                comments = FULL_SELL_TEXT;
            }

            WrapAccount account = accountMap.get(AccountKey.valueOf(orderDetails.getAccount()));
            Asset asset = assetMap.get(orderDetails.getAsset());

            Boolean isTMPAsset = asset.getAssetId() != null && asset.getAssetId().equals(tmpAssetDto.getAssetId());

            RebalanceOrderDetailsDto orderDetailsDto = new RebalanceOrderDetailsDto(account, asset, estimatedPrice, comments,
                    orderDetails, fullRedemptionMap.get(orderDetails.getAccount()), isTMPAsset);
            orderDetailsDtoList.add(orderDetailsDto);
        }

        return orderDetailsDtoList;
    }

    private Map<String, Boolean> storeFullRedemption(List<RebalanceOrderDetails> orderDetails, Map<String, Asset> assetMap,
            AssetDto tmpAssetDto) {
        Map<String, Boolean> fullRedemptionMap = new HashMap<String, Boolean>();

        Group<RebalanceOrderDetails> groupedSubTypes = Lambda.group(orderDetails,
                Lambda.by(Lambda.on(RebalanceOrderDetails.class).getAccount()));
        List<Group<RebalanceOrderDetails>> subGroups = groupedSubTypes.subgroups();
        boolean sellAllAssets = true;
        for (Group<RebalanceOrderDetails> subGroup : subGroups) {
            List<RebalanceOrderDetails> rebalanceOrders = subGroup.findAll();
            for (RebalanceOrderDetails rebalanceOrder : rebalanceOrders) {
                Asset rebalanceAsset = assetMap.get(rebalanceOrder.getAsset());
                if (!(rebalanceAsset.getAssetId() != null && rebalanceAsset.getAssetId().equals(tmpAssetDto.getAssetId()))
                        && !(rebalanceOrder.getTargetWeight().compareTo(BigDecimal.ZERO) == 0)) {
                    sellAllAssets = false;
                    break;
                }
            }

            fullRedemptionMap.put(rebalanceOrders.get(0).getAccount(), sellAllAssets);

        }
        return fullRedemptionMap;
    }



    private boolean showPrice(RebalanceOrderDetails orderDetails) {
        boolean notExcluded = orderDetails.getReasonForExclusion() != null;
        boolean notNullOrZero = orderDetails.getCurrentValue() != null && orderDetails.getCurrentQuantity() != null
                && !orderDetails.getCurrentQuantity().equals(BigDecimal.ZERO);

        return notExcluded && notNullOrZero;
    }

    private List<RebalanceOrderGroupDto> groupByAdviser(List<RebalanceOrderGroupDto> orderGroupDtoList) {

        Map<String, RebalanceOrderGroupDto> groupMap = new HashMap<>();

        for (RebalanceOrderGroupDto orderGroupDto : orderGroupDtoList) {
            RebalanceOrderGroupDto group = groupMap.get(orderGroupDto.getAdviserName());
            if (group == null) {
                groupMap.put(orderGroupDto.getAdviserName(), orderGroupDto);
            } else {
                group.getOrderDetails().addAll(orderGroupDto.getOrderDetails());
            }
        }

        return new ArrayList<RebalanceOrderGroupDto>(groupMap.values());
    }

}
