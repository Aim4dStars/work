package com.bt.nextgen.api.watchlist.v1.service;

import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistDto;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.investmentfinder.service.InvestmentFinderAssetService;
import com.btfin.panorama.service.integration.wachlist.service.InvestmentWatchlistService;
import com.btfin.panorama.service.integration.watchlist.model.InvestmentWatchlist;
import com.btfin.panorama.service.integration.watchlist.model.InvestmentWatchlistEntity;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentWatchlistDtoServiceImplTest {

    @InjectMocks
    InvestmentWatchlistDtoServiceImpl investmentWatchlistDtoService;

    @Mock
    private InvestmentWatchlistService investmentWatchlistService;

    @Mock
    private InvestmentFinderAssetService investmentFinderAssetService;

    @Mock
    private UserProfileService userProfileService;

    @Before
    public void setup() {
        InvestmentWatchlist watchlist = new InvestmentWatchlistEntity("idme34", "watchme21", "ownme56", null);
        Mockito.when(investmentWatchlistService.saveWatchlist(Mockito.any(InvestmentWatchlist.class))).thenReturn(watchlist);
        Mockito.when(investmentWatchlistService.findWatchlistByOwner(Mockito.any(String.class)))
                .thenReturn(Collections.singletonList(watchlist));
        Mockito.when(investmentWatchlistService.findWatchlist(Mockito.any(String.class))).thenReturn(watchlist);
        Mockito.when(investmentWatchlistService.updateWatchlistName(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(watchlist);
        Mockito.when(investmentWatchlistService.addAssetCodes(Mockito.any(String.class), Mockito.any(Set.class)))
                .thenReturn(watchlist);
        Mockito.when(investmentWatchlistService.removeAssetCodes(Mockito.any(String.class), Mockito.any(Set.class)))
                .thenReturn(watchlist);
    }

    @Test
    public void testCreate_whenAWatchlistDtoIsProvided_thenAWatchlistDtoShouldBeReturned() {
        InvestmentWatchlistDto watchlist = new InvestmentWatchlistDto("watchme21", "ownme56");

        InvestmentWatchlistDto created = investmentWatchlistDtoService.create(watchlist, new ServiceErrorsImpl());
        assertThat(created.getOwnerId(), Matchers.equalTo(watchlist.getOwnerId()));
    }

    @Test
    public void testFindAll_whenCalled_thenAListShouldBeReturned() {
        List<InvestmentWatchlistDto> found = investmentWatchlistDtoService.findAll(new ServiceErrorsImpl());
        assertThat(found, Matchers.hasSize(1));
    }

    @Test
    public void testFind_whenCalledWithAnId_thenAWatchlistDtoShouldBeReturned() {
        InvestmentWatchlistDto found = investmentWatchlistDtoService.find(InvestmentWatchlistKey.valueOf("idme34"),
                new ServiceErrorsImpl());
        assertThat(found.getWatchlistId(), Matchers.equalTo("idme34"));
    }

    @Test
    public void testPartialUpdate_whenCalledWithAnUpdate_thenAWatchlistDtoShouldBeReturned() {
        InvestmentWatchlistDto found = investmentWatchlistDtoService.partialUpdate(InvestmentWatchlistKey.valueOf("idme34"),
                Collections.singletonMap("watchlistName", "watchme66"), new ServiceErrorsImpl());
        assertThat(found.getWatchlistId(), Matchers.equalTo("idme34"));
    }

    @Test
    public void testDelete_whenAWatchlistIdIsProvided_thenDeleteIsCalledWithTheId() {
        investmentWatchlistDtoService.delete(InvestmentWatchlistKey.valueOf("idme37"), new ServiceErrorsImpl());
        Mockito.verify(investmentWatchlistService).deleteWatchlist("idme37");
    }

    @Test
    public void testAddAssets_whenAListOfAssetCodesIsProvided_thenAddIsCalledWithTheId() {
        investmentWatchlistDtoService.getAddAssetCodesPartialUpdateService().partialUpdate(
                InvestmentWatchlistKey.valueOf("idme37"), Collections.singletonMap("assetCodes", Collections.singleton("123")),
                new ServiceErrorsImpl());
        Mockito.verify(investmentWatchlistService).addAssetCodes("idme37", Collections.singleton("123"));
    }

    @Test
    public void testDeleteAssets_whenAListOfAssetCodesIsProvided_thenDeleteIsCalledWithTheId() {
        investmentWatchlistDtoService.getRemoveAssetCodesPartialUpdateService().partialUpdate(
                InvestmentWatchlistKey.valueOf("idme37"), Collections.singletonMap("assetCodes", Collections.singleton("123")),
                new ServiceErrorsImpl());
        Mockito.verify(investmentWatchlistService).removeAssetCodes("idme37", Collections.singleton("123"));
    }

}
