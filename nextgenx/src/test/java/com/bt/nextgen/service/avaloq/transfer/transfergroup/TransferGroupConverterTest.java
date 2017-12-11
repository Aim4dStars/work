package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.btfin.abs.trxservice.xferbdl.v1_0.XferBdlReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TransferGroupConverterTest {

    @InjectMocks
    private TransferGroupConverter transferGroupConverter;

    @Mock
    private GroupTransferAssetHelper groupTransferAssetHelper;

    @Mock
    private TransactionValidationConverter transactionValidationConverter;

    @Test
    public void testToLoadTransferRequest() {
        XferBdlReq req = transferGroupConverter.toLoadTransferRequest("transferId");

        Assert.notNull(req);
        assertEquals("transferId", req.getReq().getGet().getDoc().getVal());
    }

    @Test
    public void testToValidateTransferRequest_whenTransferDetailsMissing_thenFieldsAreNotSetInRequest() {
        TransferGroupDetails transferDetails = mockMinimalTransferDetails();
        XferBdlReq req = transferGroupConverter.toValidateTransferRequest(transferDetails);

        Assert.notNull(req);
        assertEquals(OrderType.INTRA_ACCOUNT_TRANSFER.getCode(), req.getData().getOrderTypeId().getExtlVal().getVal());
        assertEquals(Origin.WEB_UI.getCode(), req.getData().getMediumId().getExtlVal().getVal());
        assertEquals("targetAccountId", req.getData().getBp().getTrgBpId().getVal());
        assertEquals(BigDecimal.ZERO, req.getData().getPauseDrawdown().getVal());
        assertEquals(IncomePreference.REINVEST.getIntlId(), req.getData().getIncomePrefId().getExtlVal().getVal());

        assertNull(req.getData().getBp().getSrcBpId());
        assertNull(req.getData().getBp().getTrgCont().getContId());
        assertNull(req.getData().getBp().getTrgCont().getMpAssetId());
        assertNull(req.getData().getBp().getSrcCont());
    }

    @Test
    public void testToValidateTransferRequest_whenTransferDetailsPresent_thenFieldsAreSetInRequest() {
        TransferGroupDetails transferDetails = mockTransferDetails();
        XferBdlReq req = transferGroupConverter.toValidateTransferRequest(transferDetails);

        Assert.notNull(req);
        assertEquals(OrderType.IN_SPECIE_TRANSFER.getCode(), req.getData().getOrderTypeId().getExtlVal().getVal());
        assertEquals(Origin.WEB_UI.getCode(), req.getData().getMediumId().getExtlVal().getVal());
        assertEquals(BigDecimal.valueOf(30), req.getData().getPauseDrawdown().getVal());
        assertEquals("targetAccountId", req.getData().getBp().getTrgBpId().getVal());
        assertEquals("destContainer", req.getData().getBp().getTrgCont().getContId().getVal());
        assertEquals("destAsset", req.getData().getBp().getTrgCont().getMpAssetId().getVal());
        assertEquals("sourceContainer", req.getData().getBp().getSrcCont().getContId().getVal());
        assertEquals(true, req.getData().getBp().getSrcCont().getFullClose().isVal());
        assertEquals(IncomePreference.REINVEST.getIntlId(), req.getData().getIncomePrefId().getExtlVal().getVal());

        assertNull(req.getData().getBp().getSrcBpId()); // Not set for inspecie transfers
    }

    @Test
    public void testToSubmitTransferRequest_whenTransferDetailsMissing_thenFieldsAreNotSetInRequest() {
        TransferGroupDetails transferDetails = mockMinimalTransferDetails();
        XferBdlReq req = transferGroupConverter.toSubmitTransferRequest(transferDetails);

        Assert.notNull(req);
        assertEquals(OrderType.INTRA_ACCOUNT_TRANSFER.getCode(), req.getData().getOrderTypeId().getExtlVal().getVal());
        assertEquals(Origin.WEB_UI.getCode(), req.getData().getMediumId().getExtlVal().getVal());
        assertEquals("targetAccountId", req.getData().getBp().getTrgBpId().getVal());
        assertEquals(BigDecimal.ZERO, req.getData().getPauseDrawdown().getVal());
        assertEquals(IncomePreference.REINVEST.getIntlId(), req.getData().getIncomePrefId().getExtlVal().getVal());

        assertNull(req.getData().getBp().getSrcBpId());
        assertNull(req.getData().getBp().getTrgCont().getContId());
        assertNull(req.getData().getBp().getTrgCont().getMpAssetId());
        assertNull(req.getData().getBp().getSrcCont());
    }

    @Test
    public void testToSubmitTransferRequest_whenTransferDetailsPresent_thenFieldsAreSetInRequest() {
        TransferGroupDetails transferDetails = mockTransferDetails();
        XferBdlReq req = transferGroupConverter.toSubmitTransferRequest(transferDetails);

        Assert.notNull(req);
        assertEquals(BigDecimal.valueOf(12345), req.getReq().getExec().getDoc().getVal());
        assertEquals(OrderType.IN_SPECIE_TRANSFER.getCode(), req.getData().getOrderTypeId().getExtlVal().getVal());
        assertEquals(Origin.WEB_UI.getCode(), req.getData().getMediumId().getExtlVal().getVal());
        assertEquals(BigDecimal.valueOf(30), req.getData().getPauseDrawdown().getVal());
        assertEquals("targetAccountId", req.getData().getBp().getTrgBpId().getVal());
        assertEquals("destContainer", req.getData().getBp().getTrgCont().getContId().getVal());
        assertEquals("destAsset", req.getData().getBp().getTrgCont().getMpAssetId().getVal());
        assertEquals("sourceContainer", req.getData().getBp().getSrcCont().getContId().getVal());
        assertEquals(true, req.getData().getBp().getSrcCont().getFullClose().isVal());
        assertEquals(IncomePreference.REINVEST.getIntlId(), req.getData().getIncomePrefId().getExtlVal().getVal());

        assertNull(req.getData().getBp().getSrcBpId()); // Not set for inspecie transfers
    }

    private TransferGroupDetails mockTransferDetails() {
        TransferGroupDetails details = Mockito.mock(TransferGroupDetailsImpl.class);

        Mockito.when(details.getOrderType()).thenReturn(OrderType.IN_SPECIE_TRANSFER);
        Mockito.when(details.getExternalTransferType()).thenReturn(TransferType.LS_BROKER_SPONSORED);
        Mockito.when(details.getChangeOfBeneficialOwnership()).thenReturn(BeneficialOwnerChangeStatus.YES);
        Mockito.when(details.getDrawdownDelayDays()).thenReturn(30);
        Mockito.when(details.getSourceAccountKey()).thenReturn(AccountKey.valueOf("sourceAccountId"));
        Mockito.when(details.getTargetAccountKey()).thenReturn(AccountKey.valueOf("targetAccountId"));
        Mockito.when(details.getDestContainerId()).thenReturn("destContainer");
        Mockito.when(details.getDestAssetId()).thenReturn("destAsset");
        Mockito.when(details.getSourceContainerId()).thenReturn("sourceContainer");
        Mockito.when(details.getCloseAfterTransfer()).thenReturn(true);
        Mockito.when(details.getIncomePreference()).thenReturn(IncomePreference.REINVEST);
        Mockito.when(details.getTransferId()).thenReturn("12345");

        // TODO: Test Transfer asset list in GroupTransferAssetHelperTest
        // TODO: Test Preference list in GroupTransferAssetHelperTest
        // Warning list tested in TransactionValidationConverterTest

        return details;
    }

    private TransferGroupDetails mockMinimalTransferDetails() {
        TransferGroupDetails details = Mockito.mock(TransferGroupDetailsImpl.class);

        Mockito.when(details.getExternalTransferType()).thenReturn(TransferType.LS_BROKER_SPONSORED);
        Mockito.when(details.getTargetAccountKey()).thenReturn(AccountKey.valueOf("targetAccountId"));

        return details;
    }

}
