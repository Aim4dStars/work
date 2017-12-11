package com.bt.nextgen.api.fundpayment.controller;

import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeDto;
import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeSearchDtoKey;
import com.bt.nextgen.api.fundpayment.service.FundPaymentNoticeSearchDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FundPaymentNoticeApiControllerTest
{
    @InjectMocks
    private FundPaymentNoticeApiController fundPaymentNoticeApiController;

    @Mock
    private FundPaymentNoticeSearchDtoService fundPaymentService;
    String sortBy = "distributionDate,desc;code,asc";
    String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
    List<FundPaymentNoticeDto> resultList = new ArrayList<>();

    @Test
    public void testGetFundPaymentNoticeNoSearchKey() throws Exception
    {
        resultList = Arrays.asList(getFundPaymentNoticeDto("code1", "name1", "cat1"),
            getFundPaymentNoticeDto("code2", "name2", "cat2"));
        Mockito.when(fundPaymentService.search(Mockito.any(FundPaymentNoticeSearchDtoKey.class), Mockito.anyList(), Mockito.any(
            ServiceErrors.class))).thenReturn(resultList);
        ApiResponse response = fundPaymentNoticeApiController.getFundPaymentNoticeList(null,
            null, null, "20 May 2014", "31 Jan 2015", sortBy, paging);
        Assert.assertNotNull(response);
        Assert.assertEquals(((ResultListDto<FundPaymentNoticeDto>)response.getData()).getResultList().size(), 2);
    }

    @Test
    public void testGetFundPaymentNoticeSearchByCode() throws Exception
    {
        resultList = Arrays.asList(getFundPaymentNoticeDto("code1", "name1", "cat1"));
        Mockito.when(fundPaymentService.search(Mockito.any(FundPaymentNoticeSearchDtoKey.class), Mockito.anyList(), Mockito.any(
            ServiceErrors.class))).thenReturn(resultList);
        ApiResponse response = fundPaymentNoticeApiController.getFundPaymentNoticeList("code1",
            null, null, "20 May 2014", "31 Jan 2015", sortBy, paging);
        Assert.assertNotNull(response);
        Assert.assertEquals(((ResultListDto<FundPaymentNoticeDto>)response.getData()).getResultList().size(), 1);
    }

    @Test
    public void testGetFundPaymentNoticeSearchByFundName() throws Exception
    {
        resultList = Arrays.asList(getFundPaymentNoticeDto("code1", "name1", "cat1"),
            getFundPaymentNoticeDto("code2", "name2", "cat2"));
        Mockito.when(fundPaymentService.search(Mockito.any(FundPaymentNoticeSearchDtoKey.class), Mockito.anyList(), Mockito.any(
            ServiceErrors.class))).thenReturn(resultList);
        ApiResponse response = fundPaymentNoticeApiController.getFundPaymentNoticeList(null,
            "nam", null, "20 May 2014", "31 Jan 2015", sortBy, paging);
        Assert.assertNotNull(response);
        Assert.assertEquals(((ResultListDto<FundPaymentNoticeDto>)response.getData()).getResultList().size(), 2);
    }

    @Test
    public void testGetFundPaymentNoticeSearchByFundManager() throws Exception
    {
        resultList = Arrays.asList(getFundPaymentNoticeDto("code2", "name2", "cat2"));
        Mockito.when(fundPaymentService.search(Mockito.any(FundPaymentNoticeSearchDtoKey.class), Mockito.anyList(), Mockito.any(
            ServiceErrors.class))).thenReturn(resultList);
        ApiResponse response = fundPaymentNoticeApiController.getFundPaymentNoticeList(null,
            null, "2", "20 May 2014", "31 Jan 2015", sortBy, paging);
        Assert.assertNotNull(response);
        Assert.assertEquals(((ResultListDto<FundPaymentNoticeDto>)response.getData()).getResultList().size(), 1);
    }

    @Test
    public void testGetFundPaymentNoticeNoResults() throws Exception
    {
        Mockito.when(fundPaymentService.search(Mockito.any(FundPaymentNoticeSearchDtoKey.class), Mockito.anyList(), Mockito.any(
            ServiceErrors.class))).thenReturn(resultList);
        ApiResponse response = fundPaymentNoticeApiController.getFundPaymentNoticeList(null,
            null, null, "20 May 2014", "31 Jan 2015", sortBy, paging);
        Assert.assertNotNull(response);
        Assert.assertEquals(((ResultListDto<FundPaymentNoticeDto>)response.getData()).getResultList().size(), 0);
    }

    @Test
    public void testGetFundPaymentNoticeNoPaging() throws Exception
    {
        Mockito.when(fundPaymentService.search(Mockito.any(FundPaymentNoticeSearchDtoKey.class), Mockito.anyList(), Mockito.any(
            ServiceErrors.class))).thenReturn(resultList);
        ApiResponse response = fundPaymentNoticeApiController.getFundPaymentNoticeList(null,
            null, null, "20 May 2014", "31 Jan 2015", sortBy, null);
        Assert.assertNotNull(response);
        Assert.assertEquals(((ResultListDto<FundPaymentNoticeDto>)response.getData()).getResultList().size(), 0);
    }

    private FundPaymentNoticeDto getFundPaymentNoticeDto(String code, String fundName, String fundManager)
    {
        FundPaymentNoticeDto dto = new FundPaymentNoticeDto();
        dto.setCode(code);
        dto.setFundName(fundName);
        dto.setFundManager(fundManager);
        return dto;
    }
}
