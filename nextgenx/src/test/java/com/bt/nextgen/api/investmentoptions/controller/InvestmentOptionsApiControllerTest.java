package com.bt.nextgen.api.investmentoptions.controller;


import com.bt.nextgen.api.investmentoptions.service.InvestmentOptionsSearchDtoServiceImpl;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentOptionsApiControllerTest {

    @InjectMocks
    InvestmentOptionsApiController investmentOptionsApiController;

    @Mock
    InvestmentOptionsSearchDtoServiceImpl investmentOptionsSearchDtoService;

    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> listArgumentCaptor;

    @Test
    public void testGetetInvestmentOptions() {

        ApiResponse apiResponse = investmentOptionsApiController.getInvestmentOptions("12345", "1232", "some", "name", null);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(investmentOptionsSearchDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(investmentOptionsSearchDtoService).search(listArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList = listArgumentCaptor.getValue();
        ApiSearchCriteria apiSearchCriteria1 = apiSearchCriteriaList.get(0);
        assertThat(apiSearchCriteria1.getProperty(), is("product-id"));
        assertThat(apiSearchCriteria1.getValue(), is("12345"));

        ApiSearchCriteria apiSearchCriteria2 = apiSearchCriteriaList.get(1);
        assertThat(apiSearchCriteria2.getProperty(), is("code"));
        assertThat(apiSearchCriteria2.getValue(), is("1232"));

        ApiSearchCriteria apiSearchCriteria3 = apiSearchCriteriaList.get(2);
        assertThat(apiSearchCriteria3.getProperty(), is("name"));
        assertThat(apiSearchCriteria3.getValue(), is("some"));

        assertThat(apiSearchCriteriaList.size(), is(3));
    }

    @Test
    public void testGetetInvestmentOptions_withNoProduct() {

        ApiResponse apiResponse = investmentOptionsApiController.getInvestmentOptions(null, "1232", "some", "name", null);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(investmentOptionsSearchDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(investmentOptionsSearchDtoService).search(listArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList = listArgumentCaptor.getValue();

        assertThat(apiSearchCriteriaList.size(), is(2));

        ApiSearchCriteria apiSearchCriteria1 = apiSearchCriteriaList.get(0);
        assertThat(apiSearchCriteria1.getProperty(), is("code"));
        assertThat(apiSearchCriteria1.getValue(), is("1232"));

        ApiSearchCriteria apiSearchCriteria2 = apiSearchCriteriaList.get(1);
        assertThat(apiSearchCriteria2.getProperty(), is("name"));
        assertThat(apiSearchCriteria2.getValue(), is("some"));
    }

    @Test
    public void testGetetInvestmentOptions_withNoParams() {
        ApiResponse apiResponse = investmentOptionsApiController.getInvestmentOptions(null, null, null, null, null);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(investmentOptionsSearchDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(investmentOptionsSearchDtoService).search(listArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList = listArgumentCaptor.getValue();

        assertThat(apiSearchCriteriaList.size(), is(0));
    }
}
