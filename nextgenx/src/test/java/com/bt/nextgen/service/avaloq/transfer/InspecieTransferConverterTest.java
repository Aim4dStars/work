package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.btfin.abs.trxservice.base.v1_0.Ovr;
import com.btfin.abs.trxservice.base.v1_0.OvrList;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.masssettle.v1_0.MassSettleReq;
import com.btfin.abs.trxservice.masssettle.v1_0.SettleRec;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class InspecieTransferConverterTest {
    @InjectMocks
    private InspecieTransferConverter transferConverter;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    protected TransactionValidationConverter validationConverter;

    private TransferDetailsImpl td1;

    @Before
    public void setup() throws ParseException {

        td1 = new TransferDetailsImpl();
        td1 = new TransferDetailsImpl();
        td1.setDestContainerId("92569");
        td1.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.YES);
        td1.setTransferType(TransferType.LS_BROKER_SPONSORED);

        // sponsor details
        SponsorDetailsImpl sponsor = new SponsorDetailsImpl();
        sponsor.setSponsorId("98170");
        sponsor.setInvestmentId("123456");
        td1.setSponsorDetails(sponsor);

        InspecieAsset asset = new InspecieAsset();
        asset.setAssetId("110760");
        asset.setQuantity(BigDecimal.TEN);
        List<InspecieAsset> assets = new ArrayList<>();
        assets.add(asset);
        td1.setTransferAssets(assets);

        // tax parcels
        TaxParcelImpl tax1 = new TaxParcelImpl();
        tax1.setCostBase(BigDecimal.valueOf(11.11d));
        tax1.setQuantity(BigDecimal.TEN);

        TaxParcelImpl tax2 = new TaxParcelImpl();
        tax2.setCostBase(BigDecimal.valueOf(11.11d));
        tax2.setQuantity(BigDecimal.TEN);

        List<TaxParcel> taxList = new ArrayList<>();
        taxList.add(tax1);
        taxList.add(tax2);
        td1.setTaxParcels(taxList);

    }

    @Test
    public void toValidateTransferRequest_whenSuppliedWithRequest_thenReqMatches() throws Exception {
        MassSettleReq req = transferConverter.toValidateTransferRequest(td1);
        Assert.assertNotNull(req);
        Assert.assertEquals(td1.getDestContainerId(), AvaloqGatewayUtil.asString(req.getData().getContainer().getContId()));
        Assert.assertEquals(td1.getTransferType().getCode(),
                AvaloqGatewayUtil.asExtlString(req.getData().getXferType().getInSpecieXferType()));

        // Sponsor details
        validateSponsorDetails(td1, req);

        SettleRec settleRec = req.getData().getSettleRecList().getSettleRec().get(0);
        Assert.assertEquals(td1.getTransferAssets().get(0).getAssetId(), AvaloqGatewayUtil.asString(settleRec.getAsset()));
        Assert.assertEquals(td1.getTransferAssets().get(0).getQuantity(), AvaloqGatewayUtil.asBigDecimal(settleRec.getQty()));

        Assert.assertEquals(Constants.DO, req.getReq().getValid().getAction().getGenericAction());

    }

    @Test
    public void toSubmitTransferRequest_WithWarning() throws Exception {
        TransferDetailsImpl td2 = new TransferDetailsImpl();
        td2.setDestContainerId("113859");
        td2.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.YES);
        td2.setTransferType(TransferType.LS_BROKER_SPONSORED);
        td2.setSponsorDetails(td1.getSponsorDetails());
        InspecieAsset asset2 = new InspecieAsset();
        asset2.setAssetId("110695");
        asset2.setQuantity(BigDecimal.TEN);
        List<InspecieAsset> assets2 = new ArrayList<>();
        assets2.add(asset2);
        td2.setTransferAssets(assets2);

        String errorId = "5080";
        ValidationError valErr = new ValidationError(errorId, "5080",
                "Container status will be changed to Pending Opening. Continue?", ErrorType.WARNING);

        List<ValidationError> errList = new ArrayList<>();
        errList.add(valErr);
        td2.setValidationErrors(errList);

        Ovr o1 = new Ovr();
        o1.setOvrId(AvaloqGatewayUtil.createIdVal(errorId));
        OvrList ovrList = new OvrList();
        ovrList.getOvr().add(o1);
        Mockito.when(validationConverter.toWarningList(Mockito.any(TransactionResponse.class))).thenReturn(ovrList);

        MassSettleReq req = transferConverter.toSubmitTransfer(td2);
        Ovr ovr = req.getReq().getExec().getOvrList().getOvr().get(0);
        Assert.assertNotNull(ovr);
    }

    @Test
    public void toLoadTransfer() throws Exception {
        String transferId = "transferId";
        MassSettleReq req = transferConverter.toLoadTransferDetails(transferId);
        Assert.assertNotNull(req);

        ReqGet reqGet = req.getReq().getGet();
        Assert.assertNotNull(reqGet);
        Assert.assertEquals(transferId, AvaloqGatewayUtil.asString(reqGet.getDoc()));
    }

    private void validateSponsorDetails(TransferDetails td, MassSettleReq req) {
        // Sponsor details: dest_bank_bp_id
        Assert.assertTrue(stringCompare(td.getSponsorDetails().getSponsorId(),
                AvaloqGatewayUtil.asString(req.getData().getSponsor().getDestBankBp())));

        // Sponsor details: dest_benef_text
        Assert.assertTrue(stringCompare(td1.getSponsorDetails().getInvestmentId(),
                AvaloqGatewayUtil.asString(req.getData().getSponsor().getDestBenefText())));

        // Sponsor details: dest_bank_bp_text
        Assert.assertTrue(stringCompare(td1.getSponsorDetails().getPlatformId(),
                AvaloqGatewayUtil.asString(req.getData().getSponsor().getDestBankBpText())));

        // Sponsor details: reg_det
        Assert.assertTrue(stringCompare(td1.getSponsorDetails().getRegistrationDetails(),
                AvaloqGatewayUtil.asString(req.getData().getSponsor().getRegDet())));
    }

    private boolean stringCompare(String strValue1, String strValue2) {
        if (StringUtils.isNotBlank(strValue1)) {
            return strValue1.equals(strValue2);
        }

        return StringUtils.isBlank(strValue2);
    }
}
