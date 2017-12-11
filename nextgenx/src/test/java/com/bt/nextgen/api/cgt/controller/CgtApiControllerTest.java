package com.bt.nextgen.api.cgt.controller;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.service.RealisedCgtDtoService;
import com.bt.nextgen.api.cgt.service.UnrealisedCgtDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class CgtApiControllerTest {

    @InjectMocks
    private CgtApiController cgtApiController;

    @Mock
    private RealisedCgtDtoService realisedCgtDtoService;

    @Mock
    private UnrealisedCgtDtoService unrealisedCgtDtoService;

    String accountId;
    DateTime startDate;
    DateTime endDate;
    String groupByAssetType;
    String groupBySecurity;

    CgtKey assetTypeKey;
    CgtKey securityKey;

    @Before
    public void setup() {
        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        startDate = new DateTime();
        endDate = new DateTime().plusDays(10);
        groupByAssetType = "ASSET_TYPE";
        groupBySecurity = "SECURITY";

        assetTypeKey = new CgtKey(accountId, startDate, endDate, groupByAssetType);
        securityKey = new CgtKey(accountId, startDate, endDate, groupBySecurity);
    }

    @Test
    public void testGetRealisedCgtAssetTypes() throws Exception {
        CgtDto mockCgtDto = new CgtDto(assetTypeKey, new ArrayList<CgtGroupDto>());

        Mockito.when(realisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                mockCgtDto);

        ApiResponse response = cgtApiController.getRealisedCgtAssetTypes(accountId, startDate.toString(), endDate.toString(),
                groupByAssetType);

        CgtDto cgtDto = (CgtDto) response.getData();

        Assert.assertNotNull(cgtDto);
        Assert.assertEquals(cgtDto.getKey().getGroupBy(), groupByAssetType);
        Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 0);
    }

    @Test
    public void testGetRealisedCgtSecurities() throws Exception {
        CgtDto mockCgtDto = new CgtDto(securityKey, new ArrayList<CgtGroupDto>());

        Mockito.when(realisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                mockCgtDto);

        ApiResponse response = cgtApiController.getRealisedCgtSecurities(accountId, startDate.toString(), endDate.toString(),
                groupBySecurity);

        CgtDto cgtDto = (CgtDto) response.getData();

        Assert.assertNotNull(cgtDto);
        Assert.assertEquals(cgtDto.getKey().getGroupBy(), groupBySecurity);
        Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 0);
    }

    @Test
    public void testGetUnrealisedCgtAssetTypes() throws Exception {
        CgtDto mockCgtDto = new CgtDto(assetTypeKey, new ArrayList<CgtGroupDto>());

        Mockito.when(unrealisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                mockCgtDto);

        ApiResponse response = cgtApiController.getUnrealisedCgtAssetTypes(accountId, startDate.toString(), groupByAssetType);

        CgtDto cgtDto = (CgtDto) response.getData();

        Assert.assertNotNull(cgtDto);
        Assert.assertEquals(cgtDto.getKey().getGroupBy(), groupByAssetType);
        Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 0);
    }

    @Test
    public void testGetUnrealisedCgtSecurities() throws Exception {
        CgtDto mockCgtDto = new CgtDto(securityKey, new ArrayList<CgtGroupDto>());

        Mockito.when(unrealisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                mockCgtDto);

        ApiResponse response = cgtApiController.getUnrealisedCgtSecurities(accountId, startDate.toString(), groupBySecurity);

        CgtDto cgtDto = (CgtDto) response.getData();

        Assert.assertNotNull(cgtDto);
        Assert.assertEquals(cgtDto.getKey().getGroupBy(), groupBySecurity);
        Assert.assertEquals(cgtDto.getCgtGroupDtoList().size(), 0);
    }
}
