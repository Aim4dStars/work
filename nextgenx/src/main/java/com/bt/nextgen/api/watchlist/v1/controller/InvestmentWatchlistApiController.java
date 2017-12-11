package com.bt.nextgen.api.watchlist.v1.controller;

import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistDto;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistKey;
import com.bt.nextgen.api.watchlist.v1.service.InvestmentWatchlistDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.Delete;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.PartialUpdate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides services to support the Investment Finder.
 */
@Controller("InvestmentWatchlistApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(value = "Provides services to support the investment watchlists/favourites.")
public class InvestmentWatchlistApiController {

    @Autowired
    private InvestmentWatchlistDtoService investmentWatchlistDtoService;

    /**
     * Gets all of the watchlists for the currently authenticated user.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Gets all of the watchlists for the currently authenticated user.", notes = "This operation will not populate the assets attribute of the InvestmentWatchlist", response = InvestmentWatchlistDto.class, responseContainer = "Set")
    @RequestMapping(value = "${api.watchlist.v1.uri}", method = RequestMethod.GET)
    public @ResponseBody ApiResponse getWatchlists() {
        return new FindAll<InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION, investmentWatchlistDtoService).performOperation();
    }

    /**
     * Gets all of the watchlists for the currently authenticated user.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Gets the watchlist identified by the supplied id.", notes = "This operation will populate the assets attribute of the InvestmentWatchlist", response = InvestmentWatchlistDto.class, responseContainer = "Set")
    @RequestMapping(value = "${api.watchlist.v1.uri.single}", method = RequestMethod.GET)
    public @ResponseBody ApiResponse getWatchlist(@PathVariable String watchlistId) {
        return new FindByKey<InvestmentWatchlistKey, InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION,
                investmentWatchlistDtoService, InvestmentWatchlistKey.valueOf(watchlistId)).performOperation();
    }

    /**
     * Creates a new watchlist for the currently authenticated user.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Creates a new wathlist with the supplied name.", response = InvestmentWatchlistDto.class)
    @RequestMapping(value = "${api.watchlist.v1.uri}", method = RequestMethod.POST)
    public @ResponseBody ApiResponse createWatchlist(
            @ApiParam(value = "The name of the new watchlist", required = true) @RequestParam String watchlistName) {
        return new Create<InvestmentWatchlistKey, InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION,
                investmentWatchlistDtoService, new InvestmentWatchlistDto(watchlistName)).performOperation();
    }

    /**
     * Creates a new watchlist for the currently authenticated user.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Deletes the watchlist identified by the supplied id.", response = InvestmentWatchlistDto.class)
    @RequestMapping(value = "${api.watchlist.v1.uri.single.delete}", method = RequestMethod.POST)
    public @ResponseBody ApiResponse deleteWatchlist(@PathVariable String watchlistId) {
        return new Delete<InvestmentWatchlistKey, InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION,
                investmentWatchlistDtoService, InvestmentWatchlistKey.valueOf(watchlistId)).performOperation();
    }

    /**
     * Changes the name of the watchlist.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Changes the name of the watchlist.", response = InvestmentWatchlistDto.class)
    @RequestMapping(value = "${api.watchlist.v1.uri.single.updateName}", method = RequestMethod.POST)
    public @ResponseBody ApiResponse updateWatchlistName(@PathVariable String watchlistId,
            @ApiParam(value = "The new name of the watchlist", required = true) @RequestParam String watchlistName) {
        Map<String, String> nameUpdate = new HashMap<>(1);
        nameUpdate.put("watchlistName", watchlistName);
        return new PartialUpdate<InvestmentWatchlistKey, InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION,
                investmentWatchlistDtoService, InvestmentWatchlistKey.valueOf(watchlistId), nameUpdate,
                InvestmentWatchlistDto.class, null).performOperation();
    }

    /**
     * Adds the provided asset codes to the watchlist.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Adds the provided asset codes to the watchlist.", response = InvestmentWatchlistDto.class)
    @RequestMapping(value = "${api.watchlist.v1.uri.single.addAssets}", method = RequestMethod.POST)

    public @ResponseBody ApiResponse addAssets(@PathVariable String watchlistId,
            @ApiParam(value = "The asset codes of the assets to be added.", name = "assetCodes", required = true) @RequestParam Set<String> assetCodes) {
        Map<String, Set<String>> assetCodesUpdate = new HashMap<>(1);
        assetCodesUpdate.put("assetCodes", assetCodes);
        return new PartialUpdate<InvestmentWatchlistKey, InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION,
                investmentWatchlistDtoService.getAddAssetCodesPartialUpdateService(), InvestmentWatchlistKey.valueOf(watchlistId),
                assetCodesUpdate, InvestmentWatchlistDto.class, null).performOperation();
    }

    /**
     * Removes the provided asset codes to the watchlist.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Adds the provided asset codes to the watchlist.", response = InvestmentWatchlistDto.class)
    @RequestMapping(value = "${api.watchlist.v1.uri.single.removeAssets}", method = RequestMethod.POST)
    public @ResponseBody ApiResponse removeAssets(@PathVariable String watchlistId,
            @ApiParam(value = "The asset codes of the assets to be removed.", name = "assetCodes", required = true) @RequestParam Set<String> assetCodes) {
        Map<String, Set<String>> assetCodesUpdate = new HashMap<>(1);
        assetCodesUpdate.put("assetCodes", assetCodes);
        return new PartialUpdate<InvestmentWatchlistKey, InvestmentWatchlistDto>(ApiVersion.CURRENT_VERSION,
                investmentWatchlistDtoService.getRemoveAssetCodesPartialUpdateService(),
                InvestmentWatchlistKey.valueOf(watchlistId), assetCodesUpdate, InvestmentWatchlistDto.class, null)
                        .performOperation();
    }

}
