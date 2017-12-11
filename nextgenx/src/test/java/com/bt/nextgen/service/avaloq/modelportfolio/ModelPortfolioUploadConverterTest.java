package com.bt.nextgen.service.avaloq.modelportfolio;

import com.avaloq.abs.bb.fld_def.ExtlIdVal;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.btfin.abs.trxservice.mp_cton.v1_0.Asset;
import com.btfin.abs.trxservice.mp_cton.v1_0.AssetList;
import com.btfin.abs.trxservice.mp_cton.v1_0.MpCtonReq;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioUploadConverterTest {

    @InjectMocks
    private ModelPortfolioUploadConverter uploadConverter;

    private ModelPortfolioUploadImpl modelUpload;
    private List<ModelPortfolioAssetAllocation> allocations;
    private ModelPortfolioAssetAllocationImpl allocation;

    @Mock
    private CmsService cmsService;

    @Mock
    private BankDateIntegrationService bankDateService;

    @Before
    public void setup() {
        allocation = new ModelPortfolioAssetAllocationImpl();
        allocation.setAssetCode("BOQ");
        allocation.setAssetAllocation(new BigDecimal("100.00"));
        allocation.setTradePercent(new BigDecimal("50.00"));
        allocation.setAssetTolerance(new BigDecimal("20.10"));
        allocations = new ArrayList<>();
        allocations.add(allocation);
        modelUpload = new ModelPortfolioUploadImpl();
        modelUpload.setModelCode("EQR_CORE_EQ");
        modelUpload.setModelName("EQR Core Equities");
        modelUpload.setCommentary("Lorem ipsum dolor sit amet");
        modelUpload.setAssetAllocations(allocations);
        when(cmsService.getDynamicContent(any(String.class), any(String[].class))).thenReturn("Invalid asset code");
        when(bankDateService.getBankDate(any(ServiceErrors.class))).thenReturn(new DateTime());
    }

    @Test
    public void toToModelUploadRequest_whenSuppliedWithRequest_thenReqExecMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        MpCtonReq req = uploadConverter.toModelUploadRequest(modelUpload, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(Constants.DO, req.getReq().getExec().getAction().getGenericAction());
    }

    @Test
    public void toToModelUploadRequest_whenSuppliedWithRequest_thenReqValidMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        MpCtonReq req = uploadConverter.toModelValidateRequest(modelUpload, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());
    }

    @Test
    public void toToGenericModelRequest_whenSuppliedWithRequest_thenReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        MpCtonReq req = uploadConverter.toGenericModelRequest(modelUpload, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(modelUpload.getModelCode(), AvaloqGatewayUtil.asExtlString(req.getData().getMpDoc().getMpKey()));
        Assert.assertEquals(modelUpload.getCommentary(), AvaloqGatewayUtil.asString(req.getData().getMpDoc().getRemark()));
        List<Asset> assets = req.getData().getAssetList().getAsset();
        Assert.assertEquals(modelUpload.getAssetAllocations().size(), assets.size());
    }

    @Test
    public void testToAssetList_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        AssetList assetList = uploadConverter.toAssetList(allocations);
        Assert.assertEquals(allocations.size(), assetList.getAsset().size());
        Asset asset = assetList.getAsset().get(0);
        Assert.assertEquals(allocations.get(0).getAssetCode(), AvaloqGatewayUtil.asExtlString(asset.getAssetKey()));
        Assert.assertEquals(allocations.get(0).getAssetAllocation(), AvaloqGatewayUtil.asBigDecimal(asset.getAssetWgt()));
        Assert.assertEquals(allocations.get(0).getTradePercent(), AvaloqGatewayUtil.asBigDecimal(asset.getTrade()));
        Assert.assertEquals(allocations.get(0).getAssetTolerance(), AvaloqGatewayUtil.asBigDecimal(asset.getAssetTolrc()));
    }

    @Test
    public void toModelUploadRequest_whenSuppliedWithMpCash_ExtIdKeyValueIsEmpty() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        ModelPortfolioAssetAllocationImpl alloc = new ModelPortfolioAssetAllocationImpl();
        alloc.setAssetCode("MPCASH");
        alloc.setAssetAllocation(new BigDecimal("100.00"));
        alloc.setTradePercent(new BigDecimal("50.00"));
        List<ModelPortfolioAssetAllocation> allocs = new ArrayList<>();
        allocs.add(alloc);

        ModelPortfolioUploadImpl uploadModel = new ModelPortfolioUploadImpl();
        uploadModel.setModelCode("EQR_CORE_EQ");
        uploadModel.setModelName("EQR Core Equities");
        uploadModel.setCommentary("Lorem ipsum dolor sit amet");
        uploadModel.setAssetAllocations(allocs);
        MpCtonReq req = uploadConverter.toModelUploadRequest(uploadModel, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(Constants.DO, req.getReq().getExec().getAction().getGenericAction());

        ExtlIdVal extIdVal = req.getData().getAssetList().getAsset().get(0).getAssetKey().getExtlVal();
        UploadAssetCodeEnum assetCode = UploadAssetCodeEnum.fromValue(extIdVal.getVal());
        Assert.assertEquals(extIdVal.getKey(), "");
        Assert.assertEquals(assetCode, UploadAssetCodeEnum.MPCASH);
    }

    @Test
    public void toAssetList() {
        List<ModelPortfolioAssetAllocation> allocations = new ArrayList<>();
        AssetList list = uploadConverter.toAssetList(allocations);
        Assert.assertTrue(list.getAsset().size() == 0);

        ModelPortfolioAssetAllocation a0 = mockModelPortfolioAssetAllocation("assetCode", BigDecimal.ONE);
        allocations.add(a0);

        ModelPortfolioAssetAllocation a1 = mockModelPortfolioAssetAllocation(null, BigDecimal.ONE);
        allocations.add(a1);

        ModelPortfolioAssetAllocation a2 = mockModelPortfolioAssetAllocation(UploadAssetCodeEnum.MPCASH.value(), BigDecimal.ONE);
        allocations.add(a2);

        ModelPortfolioAssetAllocation a3 = mockModelPortfolioAssetAllocation(UploadAssetCodeEnum.TMP_CASH.value(), BigDecimal.ONE);
        when(a3.getTradePercent()).thenReturn(BigDecimal.TEN);
        allocations.add(a3);

        ModelPortfolioAssetAllocation a4 = mockModelPortfolioAssetAllocation(UploadAssetCodeEnum.SUPER_TMP_CASH.value(),
                BigDecimal.ONE);
        allocations.add(a4);

        ModelPortfolioAssetAllocation a5 = mockModelPortfolioAssetAllocation(UploadAssetCodeEnum.ADVISER_MODEL_CASH.value(),
                BigDecimal.ONE);
        allocations.add(a5);

        list = uploadConverter.toAssetList(allocations);
        Assert.assertTrue(list.getAsset().size() == 6);
    }

    private ModelPortfolioAssetAllocation mockModelPortfolioAssetAllocation(String assetCode, BigDecimal allocation) {
        ModelPortfolioAssetAllocation a = mock(ModelPortfolioAssetAllocation.class);
        when(a.getAssetCode()).thenReturn(assetCode);
        when(a.getAssetAllocation()).thenReturn(allocation);

        return a;
    }
}
