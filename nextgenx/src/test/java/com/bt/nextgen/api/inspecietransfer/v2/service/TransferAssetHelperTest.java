package com.bt.nextgen.api.inspecietransfer.v2.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.TransferAssetDtoImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferItemImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.TransferItem;
import com.bt.nextgen.service.integration.transfer.TransferType;

@RunWith(MockitoJUnitRunner.class)
public class TransferAssetHelperTest {

    @InjectMocks
    private TransferAssetHelper assetHelper;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    CmsService cmsService;

    private List<InspecieAsset> transferAssets;

    private List<Asset> assets;

    private Map<String, Asset> results;

    InspecieTransferDtoImpl transferDto = null;
    List<String[]> taxParcels = new ArrayList<>();

    @Before
    public void setup() throws Exception {

        assets = new ArrayList<>();
        assets.add(createAsset("assetId1", "assetname1", "assetcode1"));
        assets.add(createAsset("assetId2", "assetname2", "assetcode2"));
        assets.add(createAsset("assetId3", "assetname3", "assetcode3"));

        transferAssets = new ArrayList<>();
        InspecieAsset ia1 = new InspecieAsset(assets.get(0).getAssetId(), new BigDecimal(10d));
        transferAssets.add(ia1);
        InspecieAsset ia2 = new InspecieAsset(assets.get(1).getAssetId(), new BigDecimal(11d));
        transferAssets.add(ia2);
        InspecieAsset ia3 = new InspecieAsset(assets.get(2).getAssetId(), new BigDecimal(12d));
        transferAssets.add(ia3);

        results = new HashMap<>();
        results.put(assets.get(0).getAssetId(), assets.get(0));
        results.put(assets.get(1).getAssetId(), assets.get(1));
        results.put(assets.get(2).getAssetId(), assets.get(2));

        SettlementRecordDtoImpl asset1 = new SettlementRecordDtoImpl("121", "BHP", new BigDecimal("1234.00"));
        SettlementRecordDtoImpl asset3 = new SettlementRecordDtoImpl("121", "ABC", new BigDecimal("1235"));

        List<SettlementRecordDto> assets = new ArrayList<>();
        assets.add(asset1);
        assets.add(asset3);
        transferDto = new InspecieTransferDtoImpl(TransferType.LS_BROKER_SPONSORED.getDisplayName(), new SponsorDetailsDtoImpl(),
                assets, "123456", new InspecieTransferKey("124", "214"), Boolean.TRUE, new ArrayList<DomainApiErrorDto>());

        Mockito.when(cmsService.getContent(Mockito.any(String.class))).thenReturn("BT");
        Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class))).thenReturn("BT");
    }

    @Test
    public void testToTransferAssetDto() {

        Mockito.when(assetService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(results);

        List<SettlementRecordDto> dtoList = assetHelper.toTransferAssetsDto(transferAssets);
        Assert.assertTrue(dtoList.size() == 3);

        for (int i = 0; i < dtoList.size(); i++) {
            Assert.assertEquals(assets.get(i).getAssetCode(), dtoList.get(i).getAssetCode());
            Assert.assertEquals(transferAssets.get(i).getQuantity(), dtoList.get(i).getQuantity());
        }
    }

    @Test
    public void testToTransferItemDto() {

        List<TransferItem> items = new ArrayList<>();
        items.add(createTransferItem("item1", assets.get(0).getAssetId()));
        items.add(createTransferItem("item2", assets.get(1).getAssetId()));
        items.add(createTransferItem("item3", assets.get(2).getAssetId()));

        Mockito.when(assetService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(results);

        List<SettlementRecordDto> dtoList = assetHelper.toTransferItemDto(items);
        Assert.assertTrue(dtoList.size() == 3);
        for (int i = 0; i < dtoList.size(); i++) {
            TransferAssetDtoImpl recDto = (TransferAssetDtoImpl) dtoList.get(i);
            Assert.assertEquals(assets.get(i).getAssetId(), recDto.getAssetId());
            Assert.assertEquals(assets.get(i).getAssetName(), recDto.getAssetName());
        }
    }

    private TransferItemImpl createTransferItem(String id, String assetId) {

        TransferItemImpl item = new TransferItemImpl();
        item.setSettlementId(id);
        item.setAssetId(assetId);
        return item;
    }

    private Asset createAsset(String id, String name, String code) {
        AssetImpl a1 = new AssetImpl();
        a1.setAssetId(id);
        a1.setAssetCode(code);
        a1.setAssetName(name);

        return a1;
    }

    @Test
    public void testValidateFile_assetCodesNotMatching_expect0521() {
        String[] taxParcelField1 = { "ABC", "10/12/1990", "1235", "100", "200" };
        String[] taxParcelField2 = { "BHP", "12/12/2016", "1236", "1234", "200" };
        String[] taxParcelField3 = { "XXX", "12/12/2014", "1236", "100", "200" };
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);
        taxParcels.add(taxParcelField3);

        List<DomainApiErrorDto> errors = new ArrayList<DomainApiErrorDto>();
        assetHelper.assetRelatedCumulativeValidations(taxParcels, transferDto, errors);
        Assert.assertFalse(errors.isEmpty());
        List<DomainApiErrorDto> warningsWithAssetErrorCode = Lambda.filter(
                Lambda.having(Lambda.on(DomainApiErrorDto.class).getErrorId(), Matchers.equalTo("Err.IP-0521")), errors);
        Assert.assertFalse(warningsWithAssetErrorCode.isEmpty());

    }

    @Test
    public void testValidateFile_assetCodesNotMatching_expect0520() {
        String[] taxParcelField1 = { "abc", "10/12/1990", "1235", "1000", "200" };
        taxParcels.add(taxParcelField1);

        List<DomainApiErrorDto> errors = new ArrayList<DomainApiErrorDto>();
        assetHelper.assetRelatedCumulativeValidations(taxParcels, transferDto, errors);
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(errors.size(), 2);
        List<DomainApiErrorDto> warningsWithAssetErrorCode = Lambda.filter(
                Lambda.having(Lambda.on(DomainApiErrorDto.class).getErrorId(), Matchers.equalTo("Err.IP-0520")), errors);
        Assert.assertFalse(warningsWithAssetErrorCode.isEmpty());

    }

    @Test
    public void testValidateFile_assetCodesNotMatching_expect0522() {
        String[] taxParcelField1 = { "ABC", "10/12/2014", "1235", "100", "200" };
        String[] taxParcelField2 = { "abc", "10/12/2012", "1235", "100", "200" };
        String[] taxParcelField3 = { "BHP", "10/12/2014", "1234.00", "100", "200" };
        String[] taxParcelField4 = { "bhp", "10/12/2014", "1234.00", "100", "200" };
        taxParcels.add(taxParcelField1);
        taxParcels.add(taxParcelField2);
        taxParcels.add(taxParcelField3);
        taxParcels.add(taxParcelField4);

        List<DomainApiErrorDto> errors = new ArrayList<DomainApiErrorDto>();
        assetHelper.assetRelatedCumulativeValidations(taxParcels, transferDto, errors);
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(errors.size(), 2);

        List<DomainApiErrorDto> warningsWithAssetErrorCode = Lambda.filter(
                Lambda.having(Lambda.on(DomainApiErrorDto.class).getErrorId(), Matchers.equalTo("Err.IP-0522")), errors);
        Assert.assertFalse(warningsWithAssetErrorCode.isEmpty());

    }
}
