package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.transaction.ParListImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.transaction.ParList;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.btfin.abs.trxservice.base.v1_0.OvrList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TransactionValidationTest {

    @InjectMocks
    private TransactionValidationConverter validationConverter;

    @Mock
    private CmsService cmsService;

    private TransferDetailsImpl td1, td2, td3;

    @Before
    public void setup() throws ParseException {

        td1 = new TransferDetailsImpl();

        TransactionValidationImpl err = new TransactionValidationImpl();
        err.setErrorId("errorId");
        err.setErrorMessage("Container status will be changed to Pending Opening. Continue?");
        err.setErrorType("ovr");
        List<TransactionValidation> errList = new ArrayList<>();
        errList.add(err);
        td1.setWarnings(errList);

        // TransferDetails with asset-level error
        td2 = new TransferDetailsImpl();
        InspecieAsset asset1 = new InspecieAsset();
        asset1.setAssetId("assetId1");

        InspecieAsset asset2 = new InspecieAsset();
        asset2.setAssetId("assetId2");

        List<InspecieAsset> assetList = new ArrayList<>();
        assetList.add(asset1);
        assetList.add(asset2);
        td2.setTransferAssets(assetList);

        TransactionValidationImpl err1 = new TransactionValidationImpl();
        err1.setErrorType("ovr");
        err1.setErrorId("errorId");
        err1.setExternalKey("extKey");

        List<String> locList = new ArrayList<>();
        locList.add("XPath:trx_list/trx_item[1]");
        err1.setLocList(locList);
        List<TransactionValidation> errList1 = new ArrayList<>();
        errList1.add(err1);
        td2.setWarnings(errList1);

        td3 = new TransferDetailsImpl();

        TransactionValidationImpl err2 = new TransactionValidationImpl();
        err2.setErrorId("errorId");
        err2.setErrorMessage("Container status will be changed to Pending Opening. Continue?");
        err2.setErrorType("ui");
        err2.setExternalKey("btfg$cgt_stpld_sec");

        List<ParList> paramList = new ArrayList<>();
        ParListImpl param = new ParListImpl();
        param.setParam("SGP");
        paramList.add(param);
        err2.setParamList(paramList);
        List<TransactionValidation> errList2 = new ArrayList<>();
        errList2.add(err2);
        td3.setWarnings(errList2);

    }

    @Test
    public void testConvertAvaloqValidation_toValidationError() {
        List<ValidationError> errors = validationConverter.toValidationError(td1, td1.getWarnings());
        Assert.assertNotNull(errors);
        Assert.assertEquals("errorId", errors.get(0).getErrorId());
    }

    @Test
    public void testGetValidationError_fromTransactionResponse_withExtlWarning() {
        List<ValidationError> errors = validationConverter.toValidationError(td2, td2.getWarnings());
        Assert.assertNotNull(errors);
        Assert.assertEquals(td2.getWarnings().size(), errors.size());
        ValidationError validErr = errors.get(0);
        Assert.assertEquals(td2.getWarnings().get(0).getExternalKey(), validErr.getErrorId());
        Assert.assertEquals(td2.getTransferAssets().get(0).getAssetId(), validErr.getField());
        Assert.assertEquals(ErrorType.WARNING, validErr.getType());

    }

    @Test
    public void testGetOvrList_fromTransactionResponse_withExtlWarning() {
        if (td2.getValidationErrors() == null) {
            td2.setValidationErrors(validationConverter.toValidationError(td2, td2.getWarnings()));
        }
        OvrList ovrList = validationConverter.toWarningList(td2);
        Assert.assertNotNull(ovrList);
        Assert.assertEquals("extKey", AvaloqGatewayUtil.asExtlString(ovrList.getOvr().get(0).getOvrId()));
    }

    @Test
    public void testConstructOvr_fromTransactionResponse() {
        if (td1.getValidationErrors() == null) {
            td1.setValidationErrors(validationConverter.toValidationError(td1, td1.getWarnings()));
        }
        OvrList ovrList = validationConverter.toWarningList(td1);
        Assert.assertNotNull(ovrList);
        Assert.assertNull(AvaloqGatewayUtil.asString(ovrList.getOvr().get(0).getOvrId()));
    }

    @Test
    public void testGetErrorMessage_fromTransactionResponse_withErrParams() {
        Mockito.when(cmsService.getDynamicContent(Mockito.anyString(), Mockito.any(String[].class))).thenReturn("content");

        List<ValidationError> errors = validationConverter.toValidationError(td3, td3.getWarnings());

        Assert.assertNotNull(errors);
        Assert.assertEquals(td3.getWarnings().size(), errors.size());
        ValidationError validErr = errors.get(0);
        Assert.assertEquals(td3.getWarnings().get(0).getExternalKey(), validErr.getErrorId());
        Assert.assertEquals(ErrorType.ERROR, validErr.getType());
        Assert.assertTrue(validErr.getMessage() != null);
        Assert.assertTrue(validErr.getMessage().contains("content"));

    }
}
