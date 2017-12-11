package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.inspecietransfer.v3.model.SponsorDetailsDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transfer.TaxParcelImpl;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.SponsorDetailsImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SponsorDetailsConverter.class, TaxParcelConverter.class })
public class TransferAssetConverterTest {

    @InjectMocks
    private TransferAssetConverter transferAssetConverter;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Mock
    Map<String, Asset> assetMap;

    @Mock
    Map<String, AssetDto> assetDtoMap;

    @Test
    public void testFromDtoList() {

        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(assetDto.getAssetId()).thenReturn("assetId");
        Mockito.when(assetDto.getAssetName()).thenReturn("assetName");
        Mockito.when(assetDto.getAssetType()).thenReturn(AssetType.MANAGED_FUND.name());

        // Mock static converters
        PowerMockito.mockStatic(SponsorDetailsConverter.class);
        SponsorDetails sponsorDetails = Mockito.mock(SponsorDetails.class);
        PowerMockito.when(SponsorDetailsConverter.fromDto(Mockito.any(SponsorDetailsDto.class), Mockito.any(String.class)))
                .thenReturn(sponsorDetails);

        PowerMockito.mockStatic(TaxParcelConverter.class);
        TaxParcel taxParcel = Mockito.mock(TaxParcel.class);
        PowerMockito.when(TaxParcelConverter.fromDtoList(Mockito.anyListOf(TaxParcelDto.class))).thenReturn(
                Arrays.asList(taxParcel));

        TransferAssetDto mockDto = Mockito.mock(TransferAssetDto.class);
        Mockito.when(mockDto.getAsset()).thenReturn(assetDto);
        Mockito.when(mockDto.getQuantity()).thenReturn(BigDecimal.TEN);

        TransferAssetDto mockDto2 = Mockito.mock(TransferAssetDto.class);

        List<TransferAsset> models = transferAssetConverter.fromDtoList(Arrays.asList(mockDto, mockDto2), "accountId",
                TransferType.LS_BROKER_SPONSORED);

        Assert.assertEquals(1, models.size());

        TransferAsset model = models.get(0);
        Assert.assertEquals("assetId", model.getAssetId());
        Assert.assertEquals("assetName", model.getName());
        Assert.assertEquals(AssetType.MANAGED_FUND, model.getType());
        Assert.assertEquals(BigDecimal.TEN, model.getQuantity());
        Assert.assertNotNull(model.getSponsorDetails());
        Assert.assertNotNull(model.getTaxParcels());
    }

    @Test
    public void testToDtoList() {

        // Mock assetDtoConverter
        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetId()).thenReturn("assetId");
        Mockito.when(assetMap.get(Mockito.any(String.class))).thenReturn(asset);

        Mockito.when(assetService.loadAssets(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class))).thenReturn(
                assetMap);

        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(assetDto.getAssetId()).thenReturn("assetId");
        Mockito.when(assetDtoMap.get(Mockito.any(String.class))).thenReturn(assetDto);

        Mockito.when(
                assetDtoConverter.toAssetDto(Mockito.anyMapOf(String.class, Asset.class),
                        Mockito.anyMapOf(String.class, TermDepositAssetDetail.class))).thenReturn(assetDtoMap);

        // Mock static converters
        PowerMockito.mockStatic(SponsorDetailsConverter.class);
        SponsorDetailsDto sponsorDetails = Mockito.mock(SponsorDetailsDto.class);
        PowerMockito.when(SponsorDetailsConverter.toDto(Mockito.any(SponsorDetails.class), Mockito.any(TransferType.class)))
                .thenReturn(sponsorDetails);

        TransferAsset mockModel = Mockito.mock(TransferAsset.class);
        Mockito.when(mockModel.getAssetId()).thenReturn("assetId");
        Mockito.when(mockModel.getName()).thenReturn("assetName");
        Mockito.when(mockModel.getType()).thenReturn(AssetType.MANAGED_FUND);
        Mockito.when(mockModel.getQuantity()).thenReturn(BigDecimal.TEN);
        Mockito.when(mockModel.getSponsorDetails()).thenReturn(new SponsorDetailsImpl());

        TaxParcelImpl tp = new TaxParcelImpl("assetId", new DateTime(), new DateTime(), BigDecimal.ONE, BigDecimal.TEN,
                BigDecimal.ZERO, null);
        List<TaxParcel> tpList = new ArrayList<>();
        tpList.add(tp);
        Mockito.when(mockModel.getTaxParcels()).thenReturn(tpList);

        TransferAsset mockModel2 = Mockito.mock(TransferAsset.class);

        List<TransferAssetDto> dtos = transferAssetConverter.toDtoList(Arrays.asList(mockModel, mockModel2),
                TransferType.LS_BROKER_SPONSORED, new FailFastErrorsImpl());

        Assert.assertEquals(1, dtos.size());

        TransferAssetDto dto = dtos.get(0);
        Assert.assertEquals("assetId", dto.getAsset().getAssetId());
        Assert.assertEquals(BigDecimal.TEN, dto.getQuantity());
        Assert.assertNotNull(dto.getSponsorDetails());

        // Verify tax parcel
        verifyTaxParcelDto(tp, dto.getTaxParcels().get(0));

    }

    private void verifyTaxParcelDto(TaxParcelImpl tp, TaxParcelDto tpDto) {
        Assert.assertEquals(tp.getAssetId(), tpDto.getAssetId());
        Assert.assertEquals(tp.getCostBase(), tpDto.getCostBase());
        Assert.assertEquals(tp.getIndexedCostBase(), tpDto.getIndexedCostBase());
        Assert.assertEquals(tp.getQuantity(), tpDto.getQuantity());
        Assert.assertEquals(tp.getReducedCostBase(), tpDto.getReducedCostBase());
    }
}
