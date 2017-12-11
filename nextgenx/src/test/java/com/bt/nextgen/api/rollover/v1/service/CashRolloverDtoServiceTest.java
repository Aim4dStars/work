package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.SuperfundDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.rollover.CashRolloverService;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CashRolloverDtoServiceTest {

    @InjectMocks
    private CashRolloverDtoServiceImpl cashRolloverDtoService;

    @Mock
    private CashRolloverService cashRolloverService;

    @Test
    public void testFindAll() {

        SuperfundDetails details = Mockito.mock(SuperfundDetails.class);
        Mockito.when(details.getUsi()).thenReturn("usi");
        Mockito.when(details.getValidFrom()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(details.getValidTo()).thenReturn(new DateTime("2016-08-01"));
        Mockito.when(details.getAbn()).thenReturn("abn");
        Mockito.when(details.getOrgName()).thenReturn("orgName");
        Mockito.when(details.getProductName()).thenReturn("productName");

        SuperfundDetails details2 = Mockito.mock(SuperfundDetails.class);
        Mockito.when(details.getUsi()).thenReturn("usi");
        Mockito.when(details.getValidFrom()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(details.getValidTo()).thenReturn(new DateTime("2016-08-01"));
        Mockito.when(details.getAbn()).thenReturn("abn");
        Mockito.when(details.getOrgName()).thenReturn("orgName");
        Mockito.when(details.getProductName()).thenReturn("productName");

        Mockito.when(cashRolloverService.loadAvailableSuperfunds(Mockito.any(ServiceErrors.class))).thenReturn(
                Arrays.asList(details, details2));

        List<SuperfundDto> response = cashRolloverDtoService.findAll(new FailFastErrorsImpl());

        Assert.assertNotNull(response);
        Assert.assertEquals(2, response.size());

        SuperfundDto dto = response.get(0);
        Assert.assertEquals("usi", dto.getUsi());
        Assert.assertEquals(new DateTime("2016-01-01"), dto.getValidFrom());
        Assert.assertEquals(new DateTime("2016-08-01"), dto.getValidTo());
        Assert.assertEquals("abn", dto.getAbn());
        Assert.assertEquals("orgName", dto.getOrgName());
        Assert.assertEquals("productName", dto.getProductName());
    }

    @Test
    public void testFindAllNoneFound() {
        Mockito.when(cashRolloverService.loadAvailableSuperfunds(Mockito.any(ServiceErrors.class))).thenReturn(
                Collections.<SuperfundDetails> emptyList());

        List<SuperfundDto> response = cashRolloverDtoService.findAll(new FailFastErrorsImpl());

        Assert.assertNotNull(response);
        Assert.assertEquals(0, response.size());
    }

    @Test
    public void testSearch_withAbnUsiProductName() {
        SuperfundDetails details = Mockito.mock(SuperfundDetails.class);
        Mockito.when(details.getUsi()).thenReturn("usi1");
        Mockito.when(details.getValidFrom()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(details.getValidTo()).thenReturn(new DateTime("2016-08-01"));
        Mockito.when(details.getAbn()).thenReturn("abn1");
        Mockito.when(details.getOrgName()).thenReturn("orgName1");
        Mockito.when(details.getProductName()).thenReturn("productName");

        SuperfundDetails details2 = Mockito.mock(SuperfundDetails.class);
        Mockito.when(details2.getUsi()).thenReturn("longusi");
        Mockito.when(details2.getValidFrom()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(details2.getValidTo()).thenReturn(new DateTime("2016-08-01"));
        Mockito.when(details2.getAbn()).thenReturn("longabn");
        Mockito.when(details2.getOrgName()).thenReturn("orgName2");
        Mockito.when(details2.getProductName()).thenReturn("R B Bishop longproductName");

        Mockito.when(cashRolloverService.loadAvailableSuperfunds(Mockito.any(ServiceErrors.class))).thenReturn(
                Arrays.asList(details, details2));

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "usi", OperationType.STRING));
        List<SuperfundDto> results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(2, results.size());

        // Search using USI
        criteriaList.clear();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "usi1", OperationType.STRING));
        results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(details.getUsi(), results.get(0).getUsi());

        // Search using ABN
        criteriaList.clear();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "abn1", OperationType.STRING));
        results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(details.getAbn(), results.get(0).getAbn());

        // Search with Exact match
        criteriaList.clear();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS,
                "R B Bishop longproductName ABN: longabn USI: longusi", OperationType.STRING));
        results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(details2.getProductName(), results.get(0).getProductName());
    }

    @Test
    public void testSearch_withEmptyValues() {
        SuperfundDetails details = Mockito.mock(SuperfundDetails.class);
        Mockito.when(details.getUsi()).thenReturn(null);
        Mockito.when(details.getValidFrom()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(details.getValidTo()).thenReturn(new DateTime("2016-08-01"));
        Mockito.when(details.getAbn()).thenReturn("abn1");
        Mockito.when(details.getOrgName()).thenReturn("orgName1");
        Mockito.when(details.getProductName()).thenReturn("productName");

        Mockito.when(cashRolloverService.loadAvailableSuperfunds(Mockito.any(ServiceErrors.class))).thenReturn(
                Arrays.asList(details));

        // Search with USI value
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "usi", OperationType.STRING));
        List<SuperfundDto> results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(0, results.size());

        // Search with null query string
        criteriaList.clear();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "null", OperationType.STRING));
        results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(0, results.size());

        // Search with null Criteria.
        // Expected to return all available funds.
        criteriaList.clear();
        results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, results.size());

        // Search with non-null field (ABN)
        criteriaList.clear();
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "abn1", OperationType.STRING));
        results = cashRolloverDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(details.getAbn(), results.get(0).getAbn());
    }
}
