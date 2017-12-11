package com.bt.nextgen.api.watchlist.v1.service;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.api.investmentfinder.v1.service.InvestmentFinderAssetDtoConverter;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistDto;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistKey;
import com.bt.nextgen.core.api.dto.PartialUpdateDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAssetQuery;
import com.btfin.panorama.service.integration.investmentfinder.service.InvestmentFinderAssetService;
import com.btfin.panorama.service.integration.wachlist.service.InvestmentWatchlistService;
import com.btfin.panorama.service.integration.watchlist.model.InvestmentWatchlist;
import com.btfin.panorama.service.integration.watchlist.model.InvestmentWatchlistEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InvestmentWatchlistDtoServiceImpl implements InvestmentWatchlistDtoService {

    @Autowired
    private InvestmentWatchlistService investmentWatchlistService;

    @Autowired
    private InvestmentFinderAssetService investmentFinderAssetService;

    @Autowired
    private UserProfileService userProfileService;

    private final AddAssetCodesPartialUpdate addAssetCodesPartialUpdate = new AddAssetCodesPartialUpdate();

    private final RemoveAssetCodesPartialUpdate removeAssetCodesPartialUpdate = new RemoveAssetCodesPartialUpdate();

    @Override
    public List<InvestmentWatchlistDto> findAll(final ServiceErrors serviceErrors) {
        return toInvestmentWatchlistDtos(investmentWatchlistService.findWatchlistByOwner(userProfileService.getGcmId()),
                serviceErrors);
    }

    @Override
    public InvestmentWatchlistDto find(final InvestmentWatchlistKey key, final ServiceErrors serviceErrors) {
        return toInvestmentWatchlistDto(investmentWatchlistService.findWatchlist(key.getId()), true, serviceErrors);
    }

    @Override
    public InvestmentWatchlistDto create(final InvestmentWatchlistDto investmentWatchlistDto, final ServiceErrors serviceErrors) {
        final InvestmentWatchlistDto watchlistToCreate = new InvestmentWatchlistDto(investmentWatchlistDto.getWatchlistName(),
                userProfileService.getGcmId());
        return toInvestmentWatchlistDto(investmentWatchlistService.saveWatchlist(toInvestmentWatchlist(watchlistToCreate)), false,
                serviceErrors);
    }

    @Override
    public InvestmentWatchlistDto partialUpdate(final InvestmentWatchlistKey key,
            final Map<String, ? extends Object> partialUpdates, final ServiceErrors serviceErrors) {
        return updateWatchlistName(key.getId(), (String) partialUpdates.get("watchlistName"), serviceErrors);
    }

    @Override
    public void delete(final InvestmentWatchlistKey key, final ServiceErrors serviceErrors) {
        investmentWatchlistService.deleteWatchlist(key.getId());
    }

    private InvestmentWatchlist toInvestmentWatchlist(final InvestmentWatchlistDto investmentWatchlistDto) {
        return new InvestmentWatchlistEntity(investmentWatchlistDto.getWatchlistId(), investmentWatchlistDto.getWatchlistName(),
                investmentWatchlistDto.getOwnerId(), investmentWatchlistDto.getAssetCodes());
    }

    private List<InvestmentWatchlistDto> toInvestmentWatchlistDtos(final List<InvestmentWatchlist> investmentWatchlists,
            final ServiceErrors serviceErrors) {
        final List<InvestmentWatchlistDto> investmentWatchlistDtos = new ArrayList<>();
        for (final InvestmentWatchlist investmentWatchlist : investmentWatchlists) {
            investmentWatchlistDtos.add(toInvestmentWatchlistDto(investmentWatchlist, false, serviceErrors));
        }
        return investmentWatchlistDtos;
    }

    private InvestmentWatchlistDto toInvestmentWatchlistDto(final InvestmentWatchlist investmentWatchlist,
            final boolean fetchAssets, final ServiceErrors serviceErrors) {
        return new InvestmentWatchlistDto(investmentWatchlist.getWatchlistId(), investmentWatchlist.getOwnerId(),
                investmentWatchlist.getWatchlistName(), investmentWatchlist.getAssetCodes(),
                fetchAssets ? fetchAssets(investmentWatchlist.getAssetCodes(), serviceErrors) : null);
    }

    private Set<InvestmentFinderAssetDto> fetchAssets(final Set<String> assetCodes, final ServiceErrors serviceErrors) {
        if (CollectionUtils.isEmpty(assetCodes)) {
            return new HashSet<>();
        }
        return new HashSet<>(InvestmentFinderAssetDtoConverter.toInvestmentFinderAssetDto(
                investmentFinderAssetService.findInvestmentFinderAssetsByQuery(InvestmentFinderAssetQuery.FIND_BY_ASSET_CODE,
                        Collections.singletonList(assetCodes))));
    }

    private InvestmentWatchlistDto updateWatchlistName(final String watchlistId, final String watchlistName,
            final ServiceErrors serviceErrors) {
        return toInvestmentWatchlistDto(investmentWatchlistService.updateWatchlistName(watchlistId, watchlistName), false,
                serviceErrors);
    }

    private InvestmentWatchlistDto addAssetCodes(final String watchlistId, final Set<String> assetCodes,
            final ServiceErrors serviceErrors) {
        return toInvestmentWatchlistDto(investmentWatchlistService.addAssetCodes(watchlistId, assetCodes), false, serviceErrors);
    }

    private InvestmentWatchlistDto removeAssetCodes(final String watchlistId, final Set<String> assetCodes,
            final ServiceErrors serviceErrors) {
        return toInvestmentWatchlistDto(investmentWatchlistService.removeAssetCodes(watchlistId, assetCodes), false,
                serviceErrors);
    }

    @Override
    public AddAssetCodesPartialUpdate getAddAssetCodesPartialUpdateService() {
        return addAssetCodesPartialUpdate;
    }

    @Override
    public RemoveAssetCodesPartialUpdate getRemoveAssetCodesPartialUpdateService() {
        return removeAssetCodesPartialUpdate;
    }

    public class RemoveAssetCodesPartialUpdate
            implements PartialUpdateDtoService<InvestmentWatchlistKey, InvestmentWatchlistDto> {

        @SuppressWarnings("unchecked")
        @Override
        public InvestmentWatchlistDto partialUpdate(final InvestmentWatchlistKey key,
                final Map<String, ? extends Object> partialUpdates, final ServiceErrors serviceErrors) {
            return InvestmentWatchlistDtoServiceImpl.this.removeAssetCodes(key.getId(),
                    (Set<String>) partialUpdates.get("assetCodes"), serviceErrors);
        }

    }

    public class AddAssetCodesPartialUpdate implements PartialUpdateDtoService<InvestmentWatchlistKey, InvestmentWatchlistDto> {

        @SuppressWarnings("unchecked")
        @Override
        public InvestmentWatchlistDto partialUpdate(final InvestmentWatchlistKey key,
                final Map<String, ? extends Object> partialUpdates, final ServiceErrors serviceErrors) {
            return InvestmentWatchlistDtoServiceImpl.this.addAssetCodes(key.getId(),
                    (Set<String>) partialUpdates.get("assetCodes"), serviceErrors);
        }

    }

}
