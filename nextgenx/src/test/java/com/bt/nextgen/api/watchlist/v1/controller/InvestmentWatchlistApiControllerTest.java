package com.bt.nextgen.api.watchlist.v1.controller;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistDto;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistKey;
import com.bt.nextgen.api.watchlist.v1.service.InvestmentWatchlistDtoService;
import com.bt.nextgen.api.watchlist.v1.service.InvestmentWatchlistDtoServiceImpl.AddAssetCodesPartialUpdate;
import com.bt.nextgen.api.watchlist.v1.service.InvestmentWatchlistDtoServiceImpl.RemoveAssetCodesPartialUpdate;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentWatchlistApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.watchlist.v1.controller" })
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/watchlist/v1/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "InvestmentWatchlistDtoService")
        InvestmentWatchlistDtoService investmentWatchlistDtoService() {
            return investmentWatchlistDtoService;
        }

    }

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    InvestmentWatchlistApiController investmentWatchlistApiController;

    @Mock
    static InvestmentWatchlistDtoService investmentWatchlistDtoService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        InvestmentWatchlistDto watchlist = new InvestmentWatchlistDto("idme56", "ownme31", "watchme27", new HashSet<String>(),
                new HashSet<InvestmentFinderAssetDto>());
        Mockito.when(investmentWatchlistDtoService.findAll(Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(watchlist));
        Mockito.when(
                investmentWatchlistDtoService.find(Mockito.any(InvestmentWatchlistKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(watchlist);
        Mockito.when(
                investmentWatchlistDtoService.create(Mockito.any(InvestmentWatchlistDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(watchlist);
        Mockito.doNothing().when(investmentWatchlistDtoService).delete(Mockito.any(InvestmentWatchlistKey.class),
                Mockito.any(ServiceErrors.class));
        Mockito.when(investmentWatchlistDtoService.partialUpdate(Mockito.any(InvestmentWatchlistKey.class),
                Mockito.any(Map.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new InvestmentWatchlistDto("idme56", "ownme31", "watchme73", new HashSet<String>(),
                        new HashSet<InvestmentFinderAssetDto>()));
        Mockito.when(investmentWatchlistDtoService.getAddAssetCodesPartialUpdateService())
                .thenReturn(Mockito.mock(AddAssetCodesPartialUpdate.class));
        Mockito.when(investmentWatchlistDtoService.getRemoveAssetCodesPartialUpdateService())
                .thenReturn(Mockito.mock(RemoveAssetCodesPartialUpdate.class));
        Mockito.when(investmentWatchlistDtoService.getAddAssetCodesPartialUpdateService().partialUpdate(
                Mockito.any(InvestmentWatchlistKey.class), Mockito.any(Map.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(watchlist);
        Mockito.when(investmentWatchlistDtoService.getRemoveAssetCodesPartialUpdateService().partialUpdate(
                Mockito.any(InvestmentWatchlistKey.class), Mockito.any(Map.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(watchlist);
    }

    /**
     * Test method for {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#getWatchlists()}
     */
    @Test
    public final void testGetWatchlists() throws Exception {
        this.mockMvc.perform(get("/secure/api/watchlists/v1_0/").accept(MediaType.ALL)).andExpect(status().isOk())
                .andExpect(content().mimeType("application/json")).andExpect(jsonPath("$.data.resultList").exists())
                .andExpect(jsonPath("$.data.resultList[0].watchlistName").value("watchme27")).toString();
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#getWatchlist(java.lang.String)}
     */
    @Test
    public final void testGetWatchlist() throws Exception {
        this.mockMvc.perform(get("/secure/api/watchlists/v1_0/idme56/").accept(MediaType.ALL)).andExpect(status().isOk())
                .andExpect(content().mimeType("application/json")).andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.watchlistName").value("watchme27"));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#createWatchlist(java.lang.String)}
     */
    @Test
    public final void testCreateWatchlist() throws Exception {
        this.mockMvc.perform(post("/secure/api/watchlists/v1_0/").param("watchlistName", "watchme27").accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.watchlistName").value("watchme27"));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#updateWatchlistName(String, String)}
     */
    @Test
    public final void testUpdateWatchlistName() throws Exception {
        this.mockMvc
                .perform(post("/secure/api/watchlists/v1_0/idme56/watchlistname").param("watchlistName", "watchme73")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.watchlistName").value("watchme73"));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#deleteWatchlist(java.lang.String)}
     */
    @Test
    public final void testDeletetWatchlist() throws Exception {
        this.mockMvc.perform(post("/secure/api/watchlists/v1_0/idme56/delete").accept(MediaType.ALL)).andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#addAssets(String, java.util.Set)}
     */
    @Test
    public final void testAddAssets() throws Exception {
        this.mockMvc
                .perform(
                        post("/secure/api/watchlists/v1_0/idme56/addassets").param("assetCodes", "213,433").accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.watchlistName").value("watchme27"));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.watchlist.v1.controller.InvestmentWatchlistApiController#removeAssets(String, java.util.Set)}
     */
    @Test
    public final void testRemoveAssets() throws Exception {
        this.mockMvc
                .perform(post("/secure/api/watchlists/v1_0/idme56/removeassets").param("assetCodes", "433").accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.watchlistName").value("watchme27"));
    }

}
