package com.bt.nextgen.api.pension.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.ausapens.v1_0.ActionType;
import com.btfin.abs.trxservice.ausapens.v1_0.AuSaPensReq;
import com.btfin.abs.trxservice.ausapens.v1_0.AuSaPensRsp;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.base.v1_0.RspExec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by L067218 on 15/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PensionCommencementConverterTest {
    private LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    @InjectMocks
    private PensionCommencementConverter pensionCommencementConverter;

    String accountNumber = "";
    AuSaPensRsp pensionResponse = new AuSaPensRsp();
    com.btfin.abs.trxservice.ausapens.v1_0.Data data;

    @Mock
    private ErrorConverter errorConverter;
    private List<ValidationError> validations = new ArrayList<ValidationError>();
    private Logger rootLogger = loggerContext.getLogger(PensionCommencementConverter.class.getName());
    private Level beforeLevelValue = rootLogger.getLevel();

    @Before
    public void init() {
        accountNumber = "400002374";
    }

    @After
    public void afterRun() {
        rootLogger.setLevel(beforeLevelValue);
    }

    @Test
    public void testMakePensionCommencementRequest(){
       AuSaPensReq pensRequest = pensionCommencementConverter.makePensionCommencementRequest(accountNumber);
       assertThat("PensionRequest is not null", pensRequest, is(notNullValue()));
       assertThat("Pension BP Id is:", pensRequest.getData().getPensBpId().getExtlVal().getVal(), is("400002374"));
       assertThat("Pension Action:", pensRequest.getData().getAction().value(), is(ActionType.COMMENCE.value()));
    }

    @Test
    public void pensionDetailsResponseDtoWhenPensionCommenceProcessed(){
        data = AvaloqObjectFactory.getAuSaPensObjectFactory().createData();
        data.setPrcInfo(AvaloqGatewayUtil.createTextVal("Processed"));
        pensionResponse.setData(data);
        PensionTrxnDto pensionDto = pensionCommencementConverter.toPensionDetailsResponseDto(pensionResponse, errorConverter );
        assertThat("PensionTrxnDto is not null", pensionDto, is(notNullValue()));
        assertThat("Pension Commence Status is saved:", pensionDto.getTransactionStatus(), is("saved"));

    }

    @Test
    public void pensionDetailsResponseDtoWhenPensionCommenceRejected(){
        PensionTrxnDto pensionDto = new PensionTrxnDto();
        data = AvaloqObjectFactory.getAuSaPensObjectFactory().createData();
        data.setPrcInfo(AvaloqGatewayUtil.createTextVal("Rejected"));

        pensionResponse.setData(data);
        pensionCommencementWithNoResponseOrErrors(pensionResponse, pensionDto);

        pensionResponse.setRsp(new Rsp());
        pensionCommencementWithNoResponseOrErrors(pensionResponse, pensionDto);

        pensionResponse.getRsp().setExec(new RspExec());
        rootLogger.setLevel(Level.DEBUG);
        pensionCommencementWithNoResponseOrErrors(pensionResponse, pensionDto);

        ErrList errList = new ErrList();
        Err err = new Err();
        err.setErrMsg("FAILED");
        err.setExtlKey("btfg$chk_dod_pens_comm");
        errList.getErr().add(err);
        pensionResponse.getRsp().getExec().setErrList(errList);
        validations.add(new ValidationError("btfg$chk_dod_pens_comm", null, "Pension commencement not allowed because of Date of Death!", ValidationError.ErrorType.ERROR));
        Mockito.when(errorConverter.processErrorList(errList)).thenReturn(validations);
        pensionCommencementResponseWithErrors(pensionResponse, pensionDto);

        pensionResponse.getData().setPensBpId(new IdFld() );
        pensionResponse.getData().getPensBpId().setVal( "PensBpValue" );
        pensionCommencementResponseWithErrors(pensionResponse, pensionDto);
    }

    private void pensionCommencementWithNoResponseOrErrors(AuSaPensRsp pensionResponse, PensionTrxnDto pensionDto) {
        pensionDto = pensionCommencementConverter.toPensionDetailsResponseDto(pensionResponse, errorConverter );
        assertThat("PensionTrxnDto is not null", pensionDto, is(notNullValue()));
        assertThat("Pension Commence Status is not saved:", pensionDto.getTransactionStatus(), is("notSaved"));
        assertThat(pensionDto.getErrors(), nullValue());
    }

    private void pensionCommencementResponseWithErrors(AuSaPensRsp pensionResponse, PensionTrxnDto pensionDto) {
        pensionDto = pensionCommencementConverter.toPensionDetailsResponseDto(pensionResponse, errorConverter );
        assertThat("PensionTrxnDto is not null", pensionDto, is(notNullValue()));
        assertThat("Pension Commence Status is not saved:", pensionDto.getTransactionStatus(), is("notSaved"));
        assertThat(pensionDto.getErrors(), notNullValue());
        assertThat(pensionDto.getErrors().size(), greaterThanOrEqualTo(1));
    }

}
