package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.abs.trxservice.ips.v1_0.IpsReq;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Properties.class })
public class ModelPortfolioDetailConverterTest {

    @InjectMocks
    ModelPortfolioDetailConverter modelPortfolioConverter;

    @Mock
    TransactionValidationConverter validationConverter;

    ModelPortfolioDetailImpl model;
    ModelPortfolioDetailImpl emptyModel;

    @Before
    public void setup() {
        // This feature is switch off by default.
        // Switch on when it is required to remove offer from TMP.
        PowerMockito.mockStatic(Properties.class);
        Mockito.when(Properties.getSafeBoolean("feature.model.tmpofferRemoval")).thenReturn(false);

        model = new ModelPortfolioDetailImpl();
        model.setId("modelId");
        model.setName("name");
        model.setSymbol("symbol");
        model.setOpenDate(new DateTime("2016-01-01"));
        model.setInvestmentStyle("style");
        model.setMinimumInvestment(BigDecimal.valueOf(25000));
        model.setModelAssetClass("class");
        model.setModelConstruction(ConstructionType.FLOATING);
        model.setModelStructure("structure");
        model.setModelType("type");
        model.setPortfolioConstructionFee(BigDecimal.valueOf(0.2));
        model.setStatus(ModelPortfolioStatus.NEW);
        model.setInvestmentManagerId(BrokerKey.valueOf("invst"));
        model.setMpSubType(ModelPortfolioType.TAILORED.getIntlId());
        model.setAccountType(ModelType.INVESTMENT.getCode());

        TargetAllocationImpl taa = new TargetAllocationImpl();
        taa.setAssetClass("assetClass");
        taa.setIndexAssetId("assetId");
        taa.setMaximumWeight(BigDecimal.valueOf(50));
        taa.setMinimumWeight(BigDecimal.valueOf(20));
        taa.setNeutralPos(BigDecimal.valueOf(30));

        List<TargetAllocation> taaList = new ArrayList<>();
        taaList.add(taa);

        model.setTargetAllocations(taaList);

        emptyModel = Mockito.mock(ModelPortfolioDetailImpl.class);
    }

    @Test
    public void whenToValidateRequestCalled_thenCorrectRequestReturned() {

        IpsReq req = modelPortfolioConverter.toValidateRequest(model);

        Assert.assertNotNull(req);

        Assert.assertEquals("modelId", req.getData().getIpsId().getVal());
        Assert.assertEquals("name", req.getData().getName().getVal());
        Assert.assertEquals("symbol", req.getData().getIpsSym().getVal());
        Assert.assertEquals("2016-01-01", req.getData().getOpenDate().getVal().toString());
        Assert.assertEquals(BigDecimal.valueOf(25000), req.getData().getMinInitInvst().getVal());
        Assert.assertEquals("class", req.getData().getAssetClass().getExtlVal().getVal());
        Assert.assertEquals("flo", req.getData().getCtonType().getExtlVal().getVal());
        Assert.assertEquals("structure", req.getData().getModelStruct().getExtlVal().getVal());
        Assert.assertEquals("type", req.getData().getMpType().getExtlVal().getVal());
        Assert.assertEquals(BigDecimal.valueOf(0.2), req.getData().getPortfCtonFee().getVal());
        Assert.assertEquals(ModelPortfolioStatus.NEW,
                ModelPortfolioStatus.forIntlId(req.getData().getStatus().getExtlVal().getVal()));
        Assert.assertEquals("invst", req.getData().getInvstMgr().getVal());
        Assert.assertEquals("tmp", req.getData().getMpSubType().getExtlVal().getVal());
        Assert.assertEquals(model.getAccountType(), req.getData().getAccType().getExtlVal().getVal());

        Assert.assertEquals("assetClass", req.getData().getTaaList().getTaa().get(0).getAssetClassCat().getExtlVal().getVal());
        Assert.assertEquals("assetId", req.getData().getTaaList().getTaa().get(0).getIdxAsset().getVal());
        Assert.assertEquals(BigDecimal.valueOf(50), req.getData().getTaaList().getTaa().get(0).getMaxWgt().getVal());
        Assert.assertEquals(BigDecimal.valueOf(20), req.getData().getTaaList().getTaa().get(0).getMinWgt().getVal());
        Assert.assertEquals(BigDecimal.valueOf(30), req.getData().getTaaList().getTaa().get(0).getNeutralPos().getVal());
    }

    @Test
    public void whenToValidateRequestCalledWithEmptyModel_thenRequestReturnedWithEmptyFields() {

        IpsReq req = modelPortfolioConverter.toValidateRequest(emptyModel);

        Assert.assertNotNull(req);

        Assert.assertNull(req.getData().getIpsId());
        Assert.assertNull(req.getData().getName());
        Assert.assertNull(req.getData().getIpsSym());
        Assert.assertNull(req.getData().getOpenDate());
        Assert.assertNull(req.getData().getMinInitInvst());
        Assert.assertNull(req.getData().getAssetClass());
        Assert.assertNull(req.getData().getCtonType());
        Assert.assertNull(req.getData().getModelStruct());
        Assert.assertNull(req.getData().getMpType());
        Assert.assertNull(req.getData().getPortfCtonFee());
        Assert.assertNull(req.getData().getStatus());
        Assert.assertNull(req.getData().getInvstMgr());
        Assert.assertNull(req.getData().getAccType());
        Assert.assertNull(req.getData().getMpSubType());
    }

    @Test
    public void whenToSubmitRequestCalled_thenCorrectRequestReturned() {

        model.setModelDescription("modelDescription");
        model.setMinimumTradeAmount(BigDecimal.ONE);
        IpsReq req = modelPortfolioConverter.toSubmitRequest(model);

        Assert.assertNotNull(req);

        // Adviser model's attributes will not be set
        Assert.assertNull(req.getData().getPpPar());
        Assert.assertNull(req.getData().getPpDescn());

        Assert.assertEquals("modelId", req.getData().getIpsId().getVal());
        Assert.assertEquals("name", req.getData().getName().getVal());
        Assert.assertEquals("symbol", req.getData().getIpsSym().getVal());
        Assert.assertEquals("2016-01-01", req.getData().getOpenDate().getVal().toString());
        Assert.assertEquals(BigDecimal.valueOf(25000), req.getData().getMinInitInvst().getVal());
        Assert.assertEquals("class", req.getData().getAssetClass().getExtlVal().getVal());
        Assert.assertEquals("flo", req.getData().getCtonType().getExtlVal().getVal());
        Assert.assertEquals("structure", req.getData().getModelStruct().getExtlVal().getVal());
        Assert.assertEquals("type", req.getData().getMpType().getExtlVal().getVal());
        Assert.assertEquals(BigDecimal.valueOf(0.2), req.getData().getPortfCtonFee().getVal());
        Assert.assertEquals(ModelPortfolioStatus.NEW,
                ModelPortfolioStatus.forIntlId(req.getData().getStatus().getExtlVal().getVal()));
        Assert.assertEquals("invst", req.getData().getInvstMgr().getVal());
        Assert.assertEquals("tmp", req.getData().getMpSubType().getExtlVal().getVal());

        Assert.assertEquals("assetClass", req.getData().getTaaList().getTaa().get(0).getAssetClassCat().getExtlVal().getVal());
        Assert.assertEquals("assetId", req.getData().getTaaList().getTaa().get(0).getIdxAsset().getVal());
        Assert.assertEquals(BigDecimal.valueOf(50), req.getData().getTaaList().getTaa().get(0).getMaxWgt().getVal());
        Assert.assertEquals(BigDecimal.valueOf(20), req.getData().getTaaList().getTaa().get(0).getMinWgt().getVal());
        Assert.assertEquals(BigDecimal.valueOf(30), req.getData().getTaaList().getTaa().get(0).getNeutralPos().getVal());

    }

    @Test
    public void whenToLoadRequestCalled_thenCorrectRequestReturned() {

        ModelPortfolioKey modelKey = new ModelPortfolioKey("modelId");

        IpsReq req = modelPortfolioConverter.toLoadRequest(modelKey);

        Assert.assertNotNull(req);
        Assert.assertEquals("modelId", req.getReq().getGet().getDoc().getVal());

    }

    @Test
    public void test_processErrors() {
        List<ValidationError> errorList = new ArrayList<>();
        List<TransactionValidation> txnErrorList = new ArrayList<>();
        ModelPortfolioDetailImpl model = mock(ModelPortfolioDetailImpl.class);
        when(model.getWarnings()).thenReturn(txnErrorList);
        when(model.getSymbol()).thenReturn("symbol");

        // No errors, empty list is returned.
        Assert.assertTrue(modelPortfolioConverter.processErrors(model).isEmpty());

        ValidationError err1 = getValidationError("errId1", "field", "err messasge", ErrorType.WARNING);
        errorList.add(err1);
        errorList.add(getValidationError("10781", "field", "err messasge", ErrorType.ERROR));
        when(validationConverter.toValidationError(any(TransactionResponse.class), Mockito.anyList())).thenReturn(errorList);
        Assert.assertEquals(2, modelPortfolioConverter.processErrors(model).size());

        errorList.clear();
        errorList.add(getValidationError("10", "field", "message", ErrorType.WARNING));
        errorList.add(getValidationError("10", "field", "something symbol", ErrorType.WARNING));
        when(validationConverter.toValidationError(any(TransactionResponse.class), Mockito.anyList())).thenReturn(errorList);
        Assert.assertEquals("Err.IP-0640", modelPortfolioConverter.processErrors(model).get(1).getErrorId());
    }

    @Test
    public void test_processErrorWithErrorException_validationExceptionThrown() {
        List<ValidationError> errorList = new ArrayList<>();
        List<TransactionValidation> txnErrorList = new ArrayList<>();
        ModelPortfolioDetailImpl model = mock(ModelPortfolioDetailImpl.class);
        when(model.getWarnings()).thenReturn(txnErrorList);
        when(model.getSymbol()).thenReturn("symbol");

        errorList.add(getValidationError("errId1", "field", "err messasge", ErrorType.WARNING));
        errorList.add(getValidationError("errorId", "field", "err messasge", ErrorType.ERROR));
        when(validationConverter.toValidationError(any(TransactionResponse.class), Mockito.anyList())).thenReturn(errorList);
        List<ValidationError> errorResults = modelPortfolioConverter.processErrors(model);
        Assert.assertNotNull(errorResults);
    }
    
    @Test
    public void whenToSubmitRequestCalledForAdviserModel_thenCorrectRequestReturned() {

        model.setMpSubType("pp");
        model.setModelDescription("modelDescription");
        model.setMinimumTradeAmount(BigDecimal.ONE);
        IpsReq req = modelPortfolioConverter.toSubmitRequest(model);

        Assert.assertNotNull(req);
        Assert.assertNotNull(req.getData().getPpPar());
        Assert.assertEquals(BigDecimal.ONE, req.getData().getPpPar().getMinTradeAmount().getVal());
        Assert.assertEquals(model.getModelDescription(), req.getData().getPpDescn().getVal());
    }

    @Test
    public void whenToSubmitRequestCalledForAdviserModel_withNullValues_thenCorrectRequestReturned() {

        model.setMpSubType("pp");
        model.setModelDescription("modelDescription");
        model.setMinimumTradeAmount(null);
        model.setMinimumTradePercent(null);
        IpsReq req = modelPortfolioConverter.toSubmitRequest(model);
        // No parameter set for nulls
        Assert.assertNotNull(req);
        Assert.assertNull(req.getData().getPpPar());

        // Only minTradeAmount value is set.
        model.setMinimumTradeAmount(BigDecimal.ONE);
        req = modelPortfolioConverter.toSubmitRequest(model);
        Assert.assertEquals(BigDecimal.ONE, req.getData().getPpPar().getMinTradeAmount().getVal());
        Assert.assertNull(req.getData().getPpPar().getMinTradePct());

        // Only minTradePct is set.
        model.setMinimumTradeAmount(null);
        model.setMinimumTradePercent(BigDecimal.TEN);
        req = modelPortfolioConverter.toSubmitRequest(model);
        Assert.assertEquals(BigDecimal.TEN, req.getData().getPpPar().getMinTradePct().getVal());
        Assert.assertNull(req.getData().getPpPar().getMinTradeAmount());
    }

    private ValidationError getValidationError(String errId, String field, String message, ErrorType type) {
        ValidationError err1 = mock(ValidationError.class);
        when(err1.getErrorId()).thenReturn(errId);
        when(err1.getField()).thenReturn(field);
        when(err1.getMessage()).thenReturn(message);
        when(err1.getType()).thenReturn(type);

        return err1;

    }
}
