package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.service.AccountDtoService;
import com.bt.nextgen.api.asset.model.AssetHoldersDto;
import com.bt.nextgen.api.asset.util.AssetConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.AccountPositionHolder;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;

@Service
public class AssetHoldersDtoServiceImpl implements AssetHoldersDtoService {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private AccountDtoService accountDtoService;

    /**
     * Gets the list of all the accounts and holdings based on the Asset Id and the date criteria
     * Also, filters the account list based on the input filters
     *
     * @param criteriaList  - Criteria list to filter the Asset holdings
     * @param serviceErrors - Service errors
     * @return - List<AssetHoldersDto>
     */
    @Override
    public List<AssetHoldersDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final List<String> assetIds = asList(getCriteriaValue(criteriaList, AssetConstants.ASSET_IDS).split(","));
        final Map<String, Asset> assetsMap = assetIntegrationService.loadAssets(assetIds, new ServiceErrorsImpl());

        // Collect all unique IPS Ids
        final List<String> ipsIds = new ArrayList<>(new HashSet<>(collect(select(assetsMap.values(),
                having(on(Asset.class).getIpsId(), not(isEmptyOrNullString()))), on(Asset.class).getIpsId())));

        // The service supports only AND of asset Ids and IPS Ids.
        final List<AccountPositionHolder> accountPositionHolders = assetIntegrationService.getAssetAccountHolders(
                assetIds, ipsIds, new DateTime(getCriteriaValue(criteriaList, AssetConstants.PRICE_DATE)), serviceErrors);

        return convertToDto(getAccountsMap(criteriaList, serviceErrors), accountPositionHolders);
    }

    /*
    * Converts the Asset holdings & accounts list responses to AssetHoldersDto
    */
    private List<AssetHoldersDto> convertToDto(Map<String, AccountDto> accountDtosMap, List<AccountPositionHolder> accountPositionHolders) {
        final List<AssetHoldersDto> assetHoldersDtoList = new ArrayList<>();
        if (isNotEmpty(accountPositionHolders)) {
            for (AccountPositionHolder accountPositionHolder : accountPositionHolders) {
                final String accountId = accountPositionHolder.getAccountKey().getId();
                if (accountDtosMap.containsKey(accountId)) {
                    assetHoldersDtoList.add(new AssetHoldersDto(accountDtosMap.get(accountId),
                            accountPositionHolder.getPrice(), accountPositionHolder.getPriceDate(),
                            accountPositionHolder.getUnits(), accountPositionHolder.getMarketValue()));
                }
            }
        }
        return assetHoldersDtoList;
    }

    /*
    * Gets the filtered response from accountDtoService.search() and then creates a map indexed on AccountId
    */
    private Map<String, AccountDto> getAccountsMap(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final List<ApiSearchCriteria> criteriaForAccountFilters = getAccountFilterCriteria(criteriaList);
        final List<AccountDto> accountList = accountDtoService.search(criteriaForAccountFilters, serviceErrors);
        return convertToMap(accountList);
    }

    private Map<String, AccountDto> convertToMap(List<AccountDto> accountList) {
        final Map<String, AccountDto> accountsMap = new HashMap<>();
        for (AccountDto accountDto : accountList) {
            accountsMap.put(EncodedString.toPlainText(accountDto.getEncodedAccountKey()), accountDto);
        }
        return accountsMap;
    }

    /**
     * Filters out the criterion for the account filter/search
     *
     * @param criteriaList - The (original) criteria list provided as input
     * @return
     */
    private List<ApiSearchCriteria> getAccountFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        final List<ApiSearchCriteria> accountFilterCriterion = new ArrayList<>();
        for (ApiSearchCriteria criteria : criteriaList) {
            if (!asList(AssetConstants.ASSET_IDS, AssetConstants.PRICE_DATE).contains(criteria.getProperty())) {
                accountFilterCriterion.add(new ApiSearchCriteria(
                        criteria.getProperty().replace("account.", ""), criteria.getOperation(),
                        criteria.getValue(), criteria.getOperationType()));
            }
        }
        return accountFilterCriterion;
    }

    private String getCriteriaValue(List<ApiSearchCriteria> criteriaList, String property) {
        final ApiSearchCriteria criteria = selectFirst(criteriaList, having(on(ApiSearchCriteria.class).getProperty(), equalTo(property)));
        return criteria != null ? criteria.getValue() : null;
    }
}
