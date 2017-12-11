package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.AvaloqTransactionService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreferenceIntegrationService;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.request.AvaloqOperation;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import com.btfin.abs.trxservice.cont.v1_0.ContRsp;
import com.btfin.abs.trxservice.cont.v1_0.Data;
import com.btfin.abs.trxservice.cont.v1_0.MpPrefItem;
import com.btfin.abs.trxservice.cont.v1_0.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvaloqModelPreferenceIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements ModelPreferenceIntegrationService {
    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private AvaloqTransactionService avaloqTransactionService;

    @Autowired
    protected ErrorConverter errorConverter;

    @Override
    public AccountModelPreferences getPreferencesForAccount(final AccountKey accountKey, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<AccountModelPreferences>("getPreferencesForSubaccount", serviceErrors) {
            @Override
            public AccountModelPreferences performOperation() {
                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelPreferenceTemplate.ACCOUNT_PREFERENCES)
                        .forParam(ModelPreferenceParams.PARAM_ACCOUNT_LIST_ID, accountKey.getId());
                return avaloqService.executeReportRequestToDomain(avaloqRequest, AccountModelPreferencesImpl.class,
                        serviceErrors);
            }
        }.run();
    }

    @Override
    public List<ModelPreference> getPreferencesForSubaccount(final AccountKey accountKey, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<List<ModelPreference>>("getPreferencesForSubaccount", serviceErrors) {
            @Override
            public List<ModelPreference> performOperation() {
                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(ModelPreferenceTemplate.SUBACCOUNT_PREFERENCES)
                        .forParam(ModelPreferenceParams.PARAM_SUBACCOUNT_LIST_ID, accountKey.getId());
                SubaccountPreferencesHolder response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                        SubaccountPreferencesHolder.class, serviceErrors);
                return response.getPreferences();
            }
        }.run();
    }

    @Override
    public List<ModelPreference> updatePreferencesForSubaccount(final AccountKey subaccountKey,
            final List<ModelPreferenceAction> preferences, final ServiceErrors serviceErrors) {

        ObjectFactory objFactory = AvaloqObjectFactory.getContObjectFactory();
        final ContReq contReq = objFactory.createContReq();
        contReq.setHdr(AvaloqGatewayUtil.createHdr());
        Data data = objFactory.createData();
        data.setCont(AvaloqGatewayUtil.createIdVal(subaccountKey.getId()));

        data.setMpPrefList(objFactory.createMpPrefList());
        for (ModelPreferenceAction preference : preferences) {
            MpPrefItem item = objFactory.createMpPrefItem();
            item.setIssuerId(AvaloqGatewayUtil.createIdVal(preference.getIssuerKey().getId()));
            item.setPrefActionId(AvaloqGatewayUtil.createExtlIdVal(preference.getAction().toString()));
            item.setPrefTypeId(AvaloqGatewayUtil.createExtlIdVal(preference.getPreference().toString()));
            data.getMpPrefList().getMpPrefItem().add(item);
        }

        contReq.setData(data);
        contReq.setReq(AvaloqUtils.createTransactionServiceExecuteReq());

        new IntegrationOperation("updatePreferencesForSubaccount", serviceErrors) {
            @Override
            public void performOperation() {
                ContRsp contResp = avaloqTransactionService.executeTransactionRequest(contReq, AvaloqOperation.CONT_REQ,
                        serviceErrors);
                List<ValidationError> validations = new ArrayList<>();
                if (contResp.getRsp().getExec().getErrList() != null) {
                    ErrList errList = contResp.getRsp().getExec().getErrList();
                    validations = errorConverter.processErrorList(errList);
                }
                if (!validations.isEmpty()) {
                    throw new ValidationException(validations, "Update preference validation failed");
                }
            }
        }.run();
        return getPreferencesForSubaccount(subaccountKey, serviceErrors);
    }

}
