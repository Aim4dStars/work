package com.bt.nextgen.api.watchlist.v1.model;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * This represents a users saved list of assets that form a watchlist or group of favourites.
 * 
 * Since share watchlists (along with alerts) will continue to be managed via MDH these watchlists will likely only contain MF, MP
 * and TD asset types.
 *
 */
@ApiModel(value = "InvestmentWatchlist")
public class InvestmentWatchlistDto extends BaseDto implements KeyedDto<InvestmentWatchlistKey> {

    @ApiModelProperty(value = "The id of the watchlist")
    private String watchlistId;
    @ApiModelProperty(value = "The id of the owner of the watchlist")
    private String ownerId;
    @ApiModelProperty(value = "The name of the watchlist")
    private String watchlistName;
    @ApiModelProperty(value = "The asset codes belonging to the watchlist", position = 1)
    private Set<String> assetCodes;
    @ApiModelProperty(value = "The assets belonging to the watchlist, for performance these assets are not always populated, refer to the api documentation", position = 2)
    private Set<InvestmentFinderAssetDto> assets;

    public InvestmentWatchlistDto(String watchlistName) {
        this.watchlistName = watchlistName;
    }

    public InvestmentWatchlistDto(String watchlistName, String ownerId) {
        this.watchlistName = watchlistName;
        this.ownerId = ownerId;
    }

    public InvestmentWatchlistDto(String watchlistName, Set<String> assetCodes) {
        this.watchlistName = watchlistName;
        this.assetCodes = assetCodes;
    }

    public InvestmentWatchlistDto(String watchlistId, String ownerId, String watchlistName, Set<String> assetCodes,
            Set<InvestmentFinderAssetDto> assets) {
        this.watchlistId = watchlistId;
        this.ownerId = ownerId;
        this.watchlistName = watchlistName;
        this.assetCodes = assetCodes;
        this.assets = assets;
    }

    @Override
    public InvestmentWatchlistKey getKey() {
        return InvestmentWatchlistKey.valueOf(watchlistId);
    }

    public String getWatchlistId() {
        return watchlistId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getWatchlistName() {
        return watchlistName;
    }

    public Set<String> getAssetCodes() {
        return assetCodes;
    }

    public Set<InvestmentFinderAssetDto> getAssets() {
        return assets;
    }

}
