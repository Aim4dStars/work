package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.btfin.abs.trxservice.rlovin.v1_0.RlovInReq;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RolloverConverterTest {

    @InjectMocks
    private RolloverConverter rolloverConverter;

    private static final String WFC_ACTION_SAVE_NEW = "opn_hold";
    private static final String WFC_ACTION_SAVE_EXISTING = "hold_store";
    private static final String WFC_ACTION_SUBMIT_EXISTING = "hold_prc";
    private static final String WFC_ACTION_DISCARD_EXISTING = "hold_discd";

    @Test
    public void testToLoadRolloverRequest() {
        RlovInReq req = rolloverConverter.toLoadRolloverRequest("rolloverId");

        Assert.notNull(req);
        assertEquals("rolloverId", req.getReq().getGet().getDoc().getVal());
    }

    @Test
    public void testToDiscardRolloverRequest() {
        RlovInReq req = rolloverConverter.toDiscardRolloverRequest("123456");

        Assert.notNull(req);
        assertEquals(WFC_ACTION_DISCARD_EXISTING, req.getReq().getExec().getAction().getWfcAction());
        assertEquals(new BigDecimal(123456), req.getReq().getExec().getDoc().getVal());
    }

    @Test
    public void testToSubmitExistingRolloverRequest() {
        RolloverDetails rolloverDetails = mockRolloverDetails();
        RlovInReq req = rolloverConverter.toRolloverInRequest(rolloverDetails);

        Assert.notNull(req);
        assertEquals(WFC_ACTION_SUBMIT_EXISTING, req.getReq().getExec().getAction().getWfcAction());
        assertEquals(new BigDecimal(123456), req.getReq().getExec().getDoc().getVal());
        assertEquals(BigDecimal.ONE, req.getReq().getExec().getTransSeqNr().getVal());

        validateRolloverDetails(req);
    }

    @Test
    public void testToSubmitExistingRolloverRequest_withNullOrEmptyFundId() {
        RolloverDetails rolloverDetails = mockRolloverDetails();
        Mockito.when(rolloverDetails.getFundId()).thenReturn(null);
        RlovInReq req = rolloverConverter.toRolloverInRequest(rolloverDetails);

        Assert.notNull(req);
        Assert.isNull(req.getData().getFundId());

        // Empty string
        Mockito.when(rolloverDetails.getFundId()).thenReturn("");
        req = rolloverConverter.toRolloverInRequest(rolloverDetails);
        Assert.notNull(req);
        Assert.isNull(req.getData().getFundId());

    }

    @Test
    public void testToSaveExistingRolloverRequest() {
        RolloverDetails rolloverDetails = mockRolloverDetails();
        RlovInReq req = rolloverConverter.toSaveRolloverRequest(rolloverDetails);

        Assert.notNull(req);
        assertEquals(WFC_ACTION_SAVE_EXISTING, req.getReq().getExec().getAction().getWfcAction());
        assertEquals(new BigDecimal(123456), req.getReq().getExec().getDoc().getVal());
        assertEquals(BigDecimal.ONE, req.getReq().getExec().getTransSeqNr().getVal());

        validateRolloverDetails(req);
    }

    @Test
    public void testToSubmitNewRolloverRequest() {
        RolloverDetails rolloverDetails = mockNewRolloverDetails();
        RlovInReq req = rolloverConverter.toRolloverInRequest(rolloverDetails);

        Assert.notNull(req);
        assertEquals(Constants.DO, req.getReq().getExec().getAction().getGenericAction());

        validateRolloverDetails(req);
    }

    @Test
    public void testToSaveNewRolloverRequest() {
        RolloverDetails rolloverDetails = mockNewRolloverDetails();
        RlovInReq req = rolloverConverter.toSaveRolloverRequest(rolloverDetails);

        Assert.notNull(req);
        assertEquals(WFC_ACTION_SAVE_NEW, req.getReq().getExec().getAction().getWfcAction());

        validateRolloverDetails(req);
    }

    private void validateRolloverDetails(RlovInReq req) {
        assertEquals("accountId", req.getData().getBpId().getVal());
        assertEquals("fundId", req.getData().getFundId().getExtlVal().getVal());
        assertEquals("fundName", req.getData().getFundName().getVal());
        assertEquals("fundAbn", req.getData().getFundAbn().getVal());
        assertEquals("fundUsi", req.getData().getFundUsi().getVal());
        assertEquals(BigDecimal.TEN, req.getData().getFundEstimAmt().getVal());
        assertEquals(true, req.getData().getSsRlovIn().isVal());
        assertEquals("accountNumber", req.getData().getMbrAccNr().getVal());
        assertEquals(RolloverOption.FULL.getCode(), req.getData().getRlovOptId().getExtlVal().getVal());
        assertEquals(RolloverType.CASH_ROLLOVER.getCode(), req.getData().getRlovTypeId().getExtlVal().getVal());
        assertEquals(false, req.getData().getInclInsur().isVal());
    }

    private RolloverDetails mockRolloverDetails() {
        RolloverDetails detail = mockNewRolloverDetails();
        Mockito.when(detail.getRolloverId()).thenReturn("123456");
        Mockito.when(detail.getLastTransSeqId()).thenReturn("1");
        return detail;
    }

    private RolloverDetails mockNewRolloverDetails() {
        RolloverDetails detail = Mockito.mock(RolloverDetails.class);
        Mockito.when(detail.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(detail.getFundId()).thenReturn("fundId");
        Mockito.when(detail.getFundName()).thenReturn("fundName");
        Mockito.when(detail.getFundAbn()).thenReturn("fundAbn");
        Mockito.when(detail.getFundUsi()).thenReturn("fundUsi");
        Mockito.when(detail.getAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(detail.getPanInitiated()).thenReturn(Boolean.TRUE);
        Mockito.when(detail.getRequestDate()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(detail.getAccountNumber()).thenReturn("accountNumber");
        Mockito.when(detail.getRolloverOption()).thenReturn(RolloverOption.FULL);
        Mockito.when(detail.getRolloverType()).thenReturn(RolloverType.CASH_ROLLOVER);
        Mockito.when(detail.getIncludeInsurance()).thenReturn(Boolean.FALSE);
        return detail;
    }
}