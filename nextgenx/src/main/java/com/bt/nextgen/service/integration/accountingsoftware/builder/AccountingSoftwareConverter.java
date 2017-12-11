package com.bt.nextgen.service.integration.accountingsoftware.builder;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareImpl;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareType;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrType;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import com.btfin.abs.trxservice.cont.v1_0.ContRsp;
import com.btfin.panorama.core.conversion.CodeCategory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("squid:S1200")
public final class AccountingSoftwareConverter {

    private AccountingSoftwareConverter() {

    }
    /**
     *
     * @param software
     * @return
     */
    public static ContReq createRequest(AccountingSoftware software) {
        ContReq feedReq = new ContReq();
        feedReq.setHdr(AvaloqGatewayUtil.createHdr());
        com.btfin.abs.trxservice.cont.v1_0.Data data = AvaloqObjectFactory.getContObjectFactory().createData();
        //data.setCont(AvaloqUtils.createIdVal(software.getContainerId()));
        com.btfin.abs.trxservice.cont.v1_0.ExtlHold  extlHold = new com.btfin.abs.trxservice.cont.v1_0.ExtlHold();
        extlHold.setBp(AvaloqGatewayUtil.createIdVal(software.getKey().getId()));
        //extlHold.setClazz(AvaloqUtils.createIdVal(software.getSoftware().getValue()));
        extlHold.setStatus(AvaloqGatewayUtil.createExtlIdVal(software.getSoftwareFeedStatus().getValue()));
        data.setExtlHold(extlHold);
        feedReq.setData(data);
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);
        Req request = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        request.setExec(reqExec);
        feedReq.setReq(request);
        return feedReq;

    }

    /**
     *
     * @param contRspRsp
     * @param serviceErrors
     * @return
     */
    public static AccountingSoftware  convertToDomain(ContRsp contRspRsp, StaticIntegrationService staticService, ServiceErrors serviceErrors) {
        AccountingSoftwareImpl software = new AccountingSoftwareImpl();
        com.btfin.abs.trxservice.cont.v1_0.Data data = contRspRsp.getData();
        AccountKey key = AccountKey.valueOf(AvaloqGatewayUtil.asString(data.getExtlHold().getBp()));
        software.setKey(key);
        //software.setContainerId(AvaloqUtils.asString(data.getCont()));

        AccountingSoftwareType softwareType = AccountingSoftwareType.fromValue(staticService.loadCode(CodeCategory.EXT_HOLDING_SRC,
                AvaloqGatewayUtil.asString(data.getExtlHold().getClazz()), serviceErrors).getIntlId());
        software.setSoftwareName(softwareType);

        SoftwareFeedStatus status = SoftwareFeedStatus.getDisplayValueFor(staticService.loadCode(CodeCategory.EXT_HOLDING_STATUS,
                AvaloqGatewayUtil.asString(data.getExtlHold().getStatus()), serviceErrors).getIntlId());
        software.setSoftwareFeedStatus(status);
        toErrorList(software, null, contRspRsp.getRsp());
        return software;
    }

    /**
     *
     * @param software
     * @param field
     * @param rsp
     */
    public static void toErrorList(AccountingSoftwareImpl software, String field, Rsp rsp)
    {
        List<ValidationError> validation = new ArrayList<>();
        if ( checkValidResponse(rsp) || checkExecResponse(rsp) )
        {
            List <Err> errs = checkErrors(rsp);

            for (Err err : errs)
            {
                //TODO: This needs to be confirmed with BAs
                ErrType errType = err.getType();
                ValidationError.ErrorType errorType;
                if (err.getType() != null)
                {
                    switch (errType)
                    {
                        case FA:
                            errorType = ValidationError.ErrorType.FATAL;
                            break;
                        case OVR:
                            errorType = ValidationError.ErrorType.WARNING;
                            break;
                        default:
                            errorType = ValidationError.ErrorType.ERROR;
                            break;
                    }
                    validation.add(new ValidationError(field,
                            errorType == ValidationError.ErrorType.FATAL ? "Fatal Error" : err.getExtlKey(),
                            errorType));
                }
            }
        }
        software.setValidationErrors(validation);
    }

    public static boolean checkExecResponse(Rsp rsp) {
        boolean status = false;
        if (rsp.getExec() != null && rsp.getExec().getErrList() != null && !rsp.getExec().getErrList().getErr().isEmpty()) {
            status =  true;
        }
        return status;
    }

    public static boolean checkValidResponse(Rsp rsp) {
        boolean status = false;
        if (rsp.getValid() != null && rsp.getValid().getErrList() != null && !rsp.getValid().getErrList().getErr().isEmpty()) {
            status =  true;
        }
        return status;
    }

    public static List<Err> checkErrors(Rsp rsp) {
        List<Err> errs = null;

        if (rsp.getValid() != null && rsp.getValid().getErrList()!= null){
            errs = rsp.getValid().getErrList().getErr();
        }
        if (rsp.getExec() != null && rsp.getExec().getErrList()!= null){
            errs = rsp.getExec().getErrList().getErr();
        }
        return errs;
    }
}