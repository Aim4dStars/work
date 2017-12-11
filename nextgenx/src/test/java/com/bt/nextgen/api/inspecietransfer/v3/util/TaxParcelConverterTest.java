package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.SponsorDetailsDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelRow;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelConverterTest {

    @Test
    public void testFromDtoList() {
        TaxParcelDto mockDto = Mockito.mock(TaxParcelDto.class);
        Mockito.when(mockDto.getAssetId()).thenReturn("assetId");
        Mockito.when(mockDto.getAssetCode()).thenReturn("assetCode");
        Mockito.when(mockDto.getTaxRelevanceDate()).thenReturn(new DateTime("2016-07-01"));
        Mockito.when(mockDto.getTaxVisibilityDate()).thenReturn(new DateTime("2016-07-02"));
        Mockito.when(mockDto.getQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(mockDto.getCostBase()).thenReturn(BigDecimal.valueOf(11));
        Mockito.when(mockDto.getReducedCostBase()).thenReturn(BigDecimal.valueOf(12));
        Mockito.when(mockDto.getIndexedCostBase()).thenReturn(BigDecimal.valueOf(13));

        TaxParcelDto mockDto2 = Mockito.mock(TaxParcelDto.class);

        List<TaxParcel> models = TaxParcelConverter.fromDtoList(Arrays.asList(mockDto, mockDto2));

        Assert.assertEquals(2, models.size());

        TaxParcel model = models.get(0);
        Assert.assertEquals("assetId", model.getAssetId());
        Assert.assertEquals(DateTime.parse("2016-07-01"), model.getRelevanceDate());
        Assert.assertEquals(DateTime.parse("2016-07-02"), model.getVisibilityDate());
        Assert.assertEquals(BigDecimal.TEN, model.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(11), model.getCostBase());
        Assert.assertEquals(BigDecimal.valueOf(12), model.getReducedCostBase());
        Assert.assertEquals(BigDecimal.valueOf(13), model.getIndexedCostBase());

        TaxParcel model2 = models.get(1);
        Assert.assertNotNull(model2);
    }

    @Test
    public void testToDtoList() {
        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(assetDto.getAssetCode()).thenReturn("assetCode");

        TaxParcel mockModel = Mockito.mock(TaxParcel.class);
        Mockito.when(mockModel.getAssetId()).thenReturn("assetId");
        Mockito.when(mockModel.getRelevanceDate()).thenReturn(new DateTime("2016-07-01"));
        Mockito.when(mockModel.getVisibilityDate()).thenReturn(new DateTime("2016-07-02"));
        Mockito.when(mockModel.getQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(mockModel.getCostBase()).thenReturn(BigDecimal.valueOf(11));
        Mockito.when(mockModel.getReducedCostBase()).thenReturn(BigDecimal.valueOf(12));
        Mockito.when(mockModel.getIndexedCostBase()).thenReturn(BigDecimal.valueOf(13));

        TaxParcel mockModel2 = Mockito.mock(TaxParcel.class);

        List<TaxParcelDto> dtos = TaxParcelConverter.toDtoList(Arrays.asList(mockModel, mockModel2), assetDto);

        Assert.assertEquals(2, dtos.size());

        TaxParcelDto dto = dtos.get(0);
        Assert.assertEquals("assetId", dto.getAssetId());
        Assert.assertEquals("assetCode", dto.getAssetCode());
        Assert.assertEquals(BigDecimal.TEN, dto.getQuantity());
        Assert.assertEquals(DateTime.parse("2016-07-01"), dto.getTaxRelevanceDate());
        Assert.assertEquals(DateTime.parse("2016-07-02"), dto.getTaxVisibilityDate());
        Assert.assertEquals(BigDecimal.valueOf(11), dto.getCostBase());
        Assert.assertEquals(BigDecimal.valueOf(12), dto.getReducedCostBase());
        Assert.assertEquals(BigDecimal.valueOf(13), dto.getIndexedCostBase());

        TaxParcelDto dto2 = dtos.get(1);
        Assert.assertNotNull(dto2);
    }

    @Test
    public void testConstructTransferAssetFromTaxParcels() {
        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getTransferType()).thenReturn(TransferType.OTHER_PLATFORM.getDisplayName());

        List<String> rowData = Arrays.asList("assetCode", "100", "X12341234", "Platform name", "2017-01-01", null, "100", null,
                "200", "300", "400");
        List<String> rowData2 = Arrays.asList("assetCode", "100", "X12341234", "Platform name", "2017-01-01", null, "100", null,
                "200", "300", "400");
        TaxParcelDetailedRow row1 = new TaxParcelDetailedRow(rowData, 1, null, null);
        TaxParcelDetailedRow row2 = new TaxParcelDetailedRow(rowData2, 2, null, null);

        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetName()).thenReturn("Asset name");
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.SHARE);
        row1.setAsset(asset);
        row2.setAsset(asset);

        List<TaxParcelRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        List<TransferAssetDto> result = TaxParcelConverter.constructTransferAssetFromTaxParcels(rows, "12345", transferDto);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        
        TransferAssetDto dto = result.get(0);
        Assert.assertEquals(BigDecimal.valueOf(200), dto.getQuantity());

        AssetDto assetDto = dto.getAsset();
        Assert.assertNotNull(assetDto);
        Assert.assertEquals("Asset name", assetDto.getAssetName());
        Assert.assertEquals(AssetType.SHARE.name(), assetDto.getAssetType());
        Assert.assertEquals("assetCode", assetDto.getAssetCode());

        Assert.assertEquals(2, dto.getTaxParcels().size());

        TaxParcelDto taxDto = dto.getTaxParcels().get(0);
        Assert.assertEquals("ASSETCODE", taxDto.getAssetCode());
        Assert.assertEquals(BigDecimal.valueOf(100), taxDto.getQuantity());
        Assert.assertEquals(new DateTime("2017-01-01"), taxDto.getTaxRelevanceDate());
        Assert.assertEquals(BigDecimal.valueOf(100), taxDto.getOriginalCostBase());
        Assert.assertEquals(BigDecimal.valueOf(200), taxDto.getCostBase());
        Assert.assertEquals(BigDecimal.valueOf(300), taxDto.getReducedCostBase());
        Assert.assertEquals(BigDecimal.valueOf(400), taxDto.getIndexedCostBase());

        SponsorDetailsDto sponsorDetails = dto.getSponsorDetails();
        Assert.assertNotNull(sponsorDetails);
        Assert.assertEquals("12345", sponsorDetails.getPid());
        Assert.assertEquals("X12341234", sponsorDetails.getHin());
        Assert.assertEquals("Platform name", sponsorDetails.getCustodian());
    }
}