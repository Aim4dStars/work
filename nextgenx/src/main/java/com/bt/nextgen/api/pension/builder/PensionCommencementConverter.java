package com.bt.nextgen.api.pension.builder;

import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.trxservice.ausapens.v1_0.ActionType;
import com.btfin.abs.trxservice.ausapens.v1_0.AuSaPensReq;
import com.btfin.abs.trxservice.ausapens.v1_0.AuSaPensRsp;
import com.btfin.abs.trxservice.base.v1_0.Req;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by L067218 on 13/09/2016.
 */
public class PensionCommencementConverter {
    private static final Logger LOG = LoggerFactory.getLogger(PensionCommencementConverter.class);

    private static final String COMMENCE_STATUS = "Processed";
    private static final String SAVED = "saved";
    private static final String NOT_SAVED = "notSaved";


    // Set PensionTrxnDto
    public PensionTrxnDto toPensionDetailsResponseDto(AuSaPensRsp pensionResponse, ErrorConverter errorConverter) {
        final PensionTrxnDto pensionTrxDto = new PensionTrxnDto();
        final String result = pensionResponse.getData().getPrcInfo().getVal();

        pensionTrxDto.setTransactionStatus(StringUtils.isNotEmpty(result) && result.equals(COMMENCE_STATUS) ? SAVED : NOT_SAVED);
        if (pensionTrxDto.getTransactionStatus().equals(NOT_SAVED)) {
            logErrors(pensionResponse);
        }
        if (hasErrors(pensionResponse)) {
            pensionTrxDto.setErrors(errorConverter.processErrorList(pensionResponse.getRsp().getExec().getErrList()));
        }
        return pensionTrxDto;
    }


    //Make commence pension request
    public AuSaPensReq makePensionCommencementRequest(String accountNumber) {
        final AuSaPensReq pensReq = AvaloqObjectFactory.getAuSaPensObjectFactory().createAuSaPensReq();
        pensReq.setHdr(AvaloqGatewayUtil.createHdr());

        final com.btfin.abs.trxservice.ausapens.v1_0.Data data = AvaloqObjectFactory.getAuSaPensObjectFactory().createData();
        data.setPensBpId(AvaloqGatewayUtil.createExtlId(accountNumber, "bp_nr"));
        data.setAction(ActionType.COMMENCE);
        pensReq.setData(data);

        final Req req = AvaloqUtils.createTransactionServiceExecuteReq();
        pensReq.setReq(req);
        return pensReq;
    }


    private void logErrors(AuSaPensRsp pensionResponse) {
        if (LOG.isDebugEnabled()) {
            if (hasErrors(pensionResponse)) {
                final String bpId = pensionResponse.getData().getPensBpId() == null ? "(unknown)" : pensionResponse.getData().getPensBpId().getVal();
                LOG.debug("Pension commencement errors: bpId = " + bpId);
                for (Err error : pensionResponse.getRsp().getExec().getErrList().getErr()) {
                    LOG.debug("\t" + error.getErrMsg());
                }
            }
        }
    }

    private boolean hasErrors(AuSaPensRsp pensionResponse) {
        return pensionResponse.getRsp() != null && pensionResponse.getRsp().getExec() != null
            && pensionResponse.getRsp().getExec().getErrList() != null
            && pensionResponse.getRsp().getExec().getErrList().getErr() != null;
    }

}
