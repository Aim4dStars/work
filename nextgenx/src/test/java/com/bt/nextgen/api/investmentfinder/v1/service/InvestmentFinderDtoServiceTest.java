package com.bt.nextgen.api.investmentfinder.v1.service;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAsset;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAssetEntity;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAssetQuery;
import com.btfin.panorama.service.integration.investmentfinder.service.InvestmentFinderAssetService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentFinderDtoServiceTest {

    @InjectMocks
    InvestmentFinderDtoServiceImpl investmentFinderDtoService;

    @Mock
    private InvestmentFinderAssetService investmentFinderAssetService;

    ServiceErrors serviceErrors;

    @Before
    public void init() {
        serviceErrors = new ServiceErrorsImpl();
        InvestmentFinderAssetEntity investmentFinderAssetEntity = new InvestmentFinderAssetEntity();
        investmentFinderAssetEntity.setAssetCode("Code-Red");
        investmentFinderAssetEntity.setAsx200(true);
        investmentFinderAssetEntity.setMarketCapitalisation(new BigDecimal("2000"));
        Mockito.when(
                investmentFinderAssetService.findInvestmentFinderAssetsByQuery(Mockito.any(InvestmentFinderAssetQuery.class)))
                .thenReturn(Collections.singletonList((InvestmentFinderAsset) investmentFinderAssetEntity));
    }

    @Test
    public void testSearch_whenProvidedNoCriteria_thenServiceErrorsHasErrors() {
        assertThat(serviceErrors.hasErrors(), equalTo(false));
        List<InvestmentFinderAssetDto> investmentFinderAssets = investmentFinderDtoService
                .search(Collections.<ApiSearchCriteria> emptyList(), serviceErrors);
        assertThat(investmentFinderAssets, Matchers.empty());
        assertThat(serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void testSearch_whenProvidedAnInvalidQueryName_thenServiceErrorsHasErrors() {
        assertThat(serviceErrors.hasErrors(), equalTo(false));
        List<InvestmentFinderAssetDto> investmentFinderAssets = investmentFinderDtoService.search(
                Collections.singletonList(
                        new ApiSearchCriteria(InvestmentFinderDtoServiceImpl.QUERY_NAME_CRITERIA, "I dont exisit")),
                serviceErrors);
        assertThat(investmentFinderAssets, Matchers.empty());
        assertThat(serviceErrors.hasErrors(), equalTo(true));
    }

    @Test
    public void testSearch_whenProvidedAnValidQueryName_thenItReturnsTheQueryResults() {
        assertThat(serviceErrors.hasErrors(), equalTo(false));
        List<InvestmentFinderAssetDto> investmentFinderAssets = investmentFinderDtoService.search(
                Collections.singletonList(
                        new ApiSearchCriteria(InvestmentFinderDtoServiceImpl.QUERY_NAME_CRITERIA, "findShareOrderByDividend")),
                serviceErrors);
        assertThat(investmentFinderAssets, Matchers.notNullValue());
        assertThat(investmentFinderAssets, Matchers.hasSize(1));
        assertThat(investmentFinderAssets.get(0).getAssetCode(), equalTo("Code-Red"));
        assertThat(investmentFinderAssets.get(0).getMarketCapitalisation(), equalTo(new BigDecimal("2000")));
        assertThat(serviceErrors.hasErrors(), equalTo(false));
    }
}
