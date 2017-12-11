package com.bt.nextgen.service.avaloq.corporateaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avaloq.abs.bb.fld_def.NrFld;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.err.v1_0.LocList;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.base.v1_0.RspValid;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Data;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Secevt2ApplyDecsnReq;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Secevt2ApplyDecsnRsp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.corporateaction.service.converter.AbstractCorporateActionConverterTest;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionElectionConverterTest extends AbstractCorporateActionConverterTest {
    private CorporateActionElectionConverter converter = new CorporateActionElectionConverter();

    @Before
    public void setup() {
    }

    @Test
    public void testToSaveElectionGroupRequest() {
        CorporateActionPosition position1 = mock(CorporateActionPosition.class);
        CorporateActionPosition position2 = mock(CorporateActionPosition.class);
        CorporateActionPosition position3 = mock(CorporateActionPosition.class);

        when(position1.getId()).thenReturn("1");
        when(position2.getId()).thenReturn("2");
        when(position3.getId()).thenReturn("3");
        when(position3.getContainerType()).thenReturn(ContainerType.DIRECT);

        List<CorporateActionOption> decisions = new ArrayList<>();

        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(1), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(2), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(3), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(4), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(5), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(6), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(7), "100"));

        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(2), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(3), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(4), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(5), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(6), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(7), "100"));
        decisions.add(createOptionMock(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), "Y"));

        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        CorporateActionSelectedOptionDto selectedOptionDto = mock(CorporateActionSelectedOptionDto.class);

        when(electionGroup.getOrderNumber()).thenReturn("0");
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(position1, position2, position3));
        when(electionGroup.getOptions()).thenReturn(decisions);
        when(selectedOptionDto.getOptionId()).thenReturn(1);

        Secevt2ApplyDecsnReq req = converter.toSaveElectionGroupRequest(electionGroup);

        assertNotNull(req);
        assertNotNull(req.getHdr());
        assertNotNull(req.getData());
        assertNotNull(req.getData().getDocId());
        assertNotNull(req.getData().getDecsn());

        assertTrue(req.getData().getDocId().getVal().longValue() == 0);
        assertEquals("1", req.getData().getPosList().getPos().get(0).getPosId().getVal());

        assertEquals(electionGroup.getOptions().get(0).getKey(), req.getData().getDecsn().getKey1().getVal());
        assertEquals(electionGroup.getOptions().get(0).getValue(), req.getData().getDecsn().getVal1().getVal());
        assertEquals(electionGroup.getOptions().get(1).getKey(), req.getData().getDecsn().getKey2().getVal());
        assertEquals(electionGroup.getOptions().get(1).getValue(), req.getData().getDecsn().getVal2().getVal());
        assertEquals(electionGroup.getOptions().get(2).getKey(), req.getData().getDecsn().getKey3().getVal());
        assertEquals(electionGroup.getOptions().get(2).getValue(), req.getData().getDecsn().getVal3().getVal());
        assertEquals(electionGroup.getOptions().get(3).getKey(), req.getData().getDecsn().getKey4().getVal());
        assertEquals(electionGroup.getOptions().get(3).getValue(), req.getData().getDecsn().getVal4().getVal());
        assertEquals(electionGroup.getOptions().get(4).getKey(), req.getData().getDecsn().getKey5().getVal());
        assertEquals(electionGroup.getOptions().get(4).getValue(), req.getData().getDecsn().getVal5().getVal());
        assertEquals(electionGroup.getOptions().get(5).getKey(), req.getData().getDecsn().getKey6().getVal());
        assertEquals(electionGroup.getOptions().get(5).getValue(), req.getData().getDecsn().getVal6().getVal());
        assertEquals(electionGroup.getOptions().get(6).getKey(), req.getData().getDecsn().getKey7().getVal());
        assertEquals(electionGroup.getOptions().get(6).getValue(), req.getData().getDecsn().getVal7().getVal());
        assertEquals(electionGroup.getOptions().get(7).getKey(), req.getData().getDecsn().getKey8().getVal());
        assertEquals(electionGroup.getOptions().get(7).getValue(), req.getData().getDecsn().getVal8().getVal());
        assertEquals(electionGroup.getOptions().get(8).getKey(), req.getData().getDecsn().getKey9().getVal());
        assertEquals(electionGroup.getOptions().get(8).getValue(), req.getData().getDecsn().getVal9().getVal());
        assertEquals(electionGroup.getOptions().get(9).getKey(), req.getData().getDecsn().getKey10().getVal());
        assertEquals(electionGroup.getOptions().get(9).getValue(), req.getData().getDecsn().getVal10().getVal());
        assertEquals(electionGroup.getOptions().get(10).getKey(), req.getData().getDecsn().getKey11().getVal());
        assertEquals(electionGroup.getOptions().get(10).getValue(), req.getData().getDecsn().getVal11().getVal());
        assertEquals(electionGroup.getOptions().get(11).getKey(), req.getData().getDecsn().getKey12().getVal());
        assertEquals(electionGroup.getOptions().get(11).getValue(), req.getData().getDecsn().getVal12().getVal());
        assertEquals(electionGroup.getOptions().get(12).getKey(), req.getData().getDecsn().getKey13().getVal());
        assertEquals(electionGroup.getOptions().get(12).getValue(), req.getData().getDecsn().getVal13().getVal());
        assertEquals(electionGroup.getOptions().get(13).getKey(), req.getData().getDecsn().getKey14().getVal());
        assertEquals(electionGroup.getOptions().get(13).getValue(), req.getData().getDecsn().getVal14().getVal());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), req.getData().getDecsn().getKey15().getVal());
        assertEquals("Y", req.getData().getDecsn().getVal15().getVal());
    }

    @Test
    public void testToSaveElectionResponse() {
        Secevt2ApplyDecsnRsp response = new Secevt2ApplyDecsnRsp();
        Data data = new Data();
        NrFld numberField = new NrFld();
        numberField.setVal(new BigDecimal(0));

        data.setDocId(numberField);
        response.setData(data);

        CorporateActionElectionGroup electionGroup = converter.toSaveElectionResponse(response);

        assertEquals("0", electionGroup.getOrderNumber());
    }

    @Test
    public void testToSaveElectionResponse_withErrors() {
        Secevt2ApplyDecsnRsp response = new Secevt2ApplyDecsnRsp();
        Data data = new Data();
        NrFld numberField = new NrFld();
        numberField.setVal(new BigDecimal(0));

        data.setDocId(numberField);
        response.setData(data);

        Rsp rsp = new Rsp();
        response.setRsp(rsp);
        RspValid rspValid = new RspValid();
        rsp.setValid(rspValid);
        ErrList errList = new ErrList();

        rspValid.setErrList(errList);
        Err error = new Err();
        LocList locList = new LocList();
        locList.getLoc().add("XPath:pos_list/pos/pos_id[138197]");
        error.setLocList(locList);
        error.setExtlKey("not_enough_money");
        error.setErrMsg("Error");
        errList.getErr().add(error);

        CorporateActionElectionGroup electionGroup = converter.toSaveElectionResponse(response);

        assertTrue(!electionGroup.getValidationErrors().isEmpty());
        assertEquals("138197", electionGroup.getValidationErrors().get(0).getPositionId());
        assertEquals("not_enough_money", electionGroup.getValidationErrors().get(0).getErrorCode());
        assertEquals("Error", electionGroup.getValidationErrors().get(0).getErrorMessage());

        errList.getErr().clear();
        electionGroup = converter.toSaveElectionResponse(response);
        assertNotNull(electionGroup);

        rspValid.setErrList(null);
        electionGroup = converter.toSaveElectionResponse(response);
        assertNotNull(electionGroup);

        rsp.setValid(null);
        electionGroup = converter.toSaveElectionResponse(response);
        assertNotNull(electionGroup);

        response.setRsp(null);
        electionGroup = converter.toSaveElectionResponse(response);
        assertNotNull(electionGroup);
    }

    @Test
    public void testToSaveElectionResponse_withErrorsButMissingPositionId() {
        Secevt2ApplyDecsnRsp response = new Secevt2ApplyDecsnRsp();
        Data data = new Data();
        NrFld numberField = new NrFld();
        numberField.setVal(new BigDecimal(0));

        data.setDocId(numberField);
        response.setData(data);

        Rsp rsp = new Rsp();
        response.setRsp(rsp);
        RspValid rspValid = new RspValid();
        rsp.setValid(rspValid);
        ErrList errList = new ErrList();

        rspValid.setErrList(errList);
        Err error = new Err();
        LocList locList = new LocList();
        locList.getLoc().add("");
        error.setLocList(locList);
        error.setExtlKey("not_enough_money");
        error.setErrMsg("Error");
        errList.getErr().add(error);

        CorporateActionElectionGroup electionGroup = converter.toSaveElectionResponse(response);

        assertEquals("", electionGroup.getValidationErrors().get(0).getPositionId());
        assertEquals("not_enough_money", electionGroup.getValidationErrors().get(0).getErrorCode());
        assertEquals("Error", electionGroup.getValidationErrors().get(0).getErrorMessage());
    }

    @Test
    public void testCorporateActionDecisionKey() {
        assertEquals(CorporateActionDecisionKey.QUANTITY, CorporateActionDecisionKey.forCode("qty"));
        assertEquals(CorporateActionDecisionKey.PERCENT, CorporateActionDecisionKey.forCode("perc"));
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION, CorporateActionDecisionKey.forCode("do_block_on_appl_decsn"));
        assertNull(CorporateActionDecisionKey.forCode("xxx"));
        assertEquals("perc", CorporateActionDecisionKey.PERCENT.getCode());
    }

    @Test
    public void testCorporateActionOptionKey() {
        assertEquals(CorporateActionOptionKey.OFFERED_PRICE, CorporateActionOptionKey.forCode("pay"));
        assertNull(CorporateActionOptionKey.forCode("xxx"));
        assertEquals("pay", CorporateActionOptionKey.OFFERED_PRICE.getCode());
    }

    @Test
    public void testToSaveElectionResponseForIm() {
        Secevt2ApplyDecsnRsp response = mock(Secevt2ApplyDecsnRsp.class);

        Rsp rsp = mock(Rsp.class);
        RspValid rspValid = mock(RspValid.class);
        ErrList errList = mock(ErrList.class);
        Err error = mock(Err.class);
        List<Err> errors = new ArrayList<>();
        errors.add(error);

        LocList locList = mock(LocList.class);
        List<String> loc = new ArrayList<>();
        loc.add("Some String");

        when(error.getLocList()).thenReturn(locList);
        when(error.getLocList().getLoc()).thenReturn(loc);

        when(response.getRsp()).thenReturn(rsp);
        when(response.getRsp().getValid()).thenReturn(rspValid);
        when(response.getRsp().getValid().getErrList()).thenReturn(errList);
        when(response.getRsp().getValid().getErrList().getErr()).thenReturn(errors);

        Data data = mock(Data.class);
        NrFld docId = mock(NrFld.class);

        when(response.getData()).thenReturn(data);
        when(response.getData().getDocId()).thenReturn(docId);
        when(response.getData().getDocId().getVal()).thenReturn(BigDecimal.TEN);

        CorporateActionElectionGroup electionGroup = converter.toSaveElectionResponseForIm(response);

        assertNotNull(electionGroup);
        assertNotNull(electionGroup.getOrderNumber());
        assertEquals("10", electionGroup.getOrderNumber());

        when(response.getRsp()).thenReturn(null);
        electionGroup = converter.toSaveElectionResponseForIm(response);
        assertNotNull(electionGroup);

        when(response.getRsp()).thenReturn(rsp);
        when(response.getRsp().getValid()).thenReturn(null);
        electionGroup = converter.toSaveElectionResponseForIm(response);
        assertNotNull(electionGroup);

        when(response.getRsp().getValid()).thenReturn(rspValid);
        when(response.getRsp().getValid().getErrList()).thenReturn(null);
        electionGroup = converter.toSaveElectionResponseForIm(response);
        assertNotNull(electionGroup);

        locList = mock(LocList.class);
        loc = new ArrayList<>();
        loc.add("[123]");
        when(response.getRsp().getValid().getErrList()).thenReturn(errList);
        when(error.getLocList()).thenReturn(locList);
        when(error.getLocList().getLoc()).thenReturn(loc);
        electionGroup = converter.toSaveElectionResponseForIm(response);
        assertNotNull(electionGroup);
        assertNotNull(electionGroup.getValidationErrors());
        assertNotNull(electionGroup.getValidationErrors().get(0));
        assertEquals("123", electionGroup.getValidationErrors().get(0).getPositionId());

        when(response.getRsp().getValid().getErrList()).thenReturn(errList);
        errors = new ArrayList<>();
        when(response.getRsp().getValid().getErrList().getErr()).thenReturn(errors);
        electionGroup = converter.toSaveElectionResponseForIm(response);
        assertNotNull(electionGroup);
    }
}
