package com.bt.nextgen.api.verifylinkedaccount.service;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.verifylinkedaccount.model.LinkedAccountDetailsDto;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.linkedaccountverification.LinkedAccountVerificationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.LinkedAccountStatus;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountIntegrationService;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountStatus;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.btfin.panorama.core.security.encryption.EncodedString;

/**
 * Created by l078480 on 22/08/2017.
 */
@Service
public class VerifyLinkedDtoServiceImpl implements VerifyLinkedDtoService {

    @Autowired
    VerifyLinkedAccountIntegrationService verifyLinkedAccountIntegrationService;

    @Autowired
    protected StaticIntegrationService staticIntegrationService;

    private static final String VERIFY_CODE = "verifyCode";

    private static final String GENERATE_CODE = "generateCode";

    private static String GEN_CODE="can_gen_code";

    private static String VEFY_CODE="can_vfy_code";

    private static String VERIFY_CODE_SATATUS="+";


    public LinkedAccountDetailsDto submit(LinkedAccountDetailsDto linkedAccountDetailsDto , ServiceErrors serviceErrors){
        LinkedAccountVerificationImpl verifyLinkedAccountDetails =new LinkedAccountVerificationImpl();
        VerifyLinkedAccountStatus verifyLinkedAccountStatus=null;
        verifyLinkedAccountDetails.setAccountNumber(linkedAccountDetailsDto.getAccountNumber());
        verifyLinkedAccountDetails.setBsb(linkedAccountDetailsDto.getBsb());
        verifyLinkedAccountDetails.setVerificationCode(linkedAccountDetailsDto.getVerificationCode());
        String unencodedAccountId = EncodedString.toPlainText(linkedAccountDetailsDto.getKey().getAccountId());
        verifyLinkedAccountDetails.setAccountKey(AccountKey.valueOf(unencodedAccountId));
        if(VERIFY_CODE.equals(linkedAccountDetailsDto.getVerificationAction())){
            verifyLinkedAccountStatus = verifyLinkedAccountIntegrationService.getVerifyLinkedAccount(verifyLinkedAccountDetails,serviceErrors);
        }
        else if(GENERATE_CODE.equals(linkedAccountDetailsDto.getVerificationAction())){
            verifyLinkedAccountStatus = verifyLinkedAccountIntegrationService.generateCodeForLinkedAccount(verifyLinkedAccountDetails,serviceErrors);
        }

        return populateVerifyLinkedAccountStatus(verifyLinkedAccountStatus,serviceErrors);


    }


    private LinkedAccountDetailsDto populateVerifyLinkedAccountStatus(VerifyLinkedAccountStatus verifyLinkedAccountStatus,ServiceErrors serviceErrors){
        LinkedAccountDetailsDto linkedAccountDetailsDto =new LinkedAccountDetailsDto();
        if(null!=verifyLinkedAccountStatus){
            IdFld linkedIdField = new IdFld();
            String linkedAccountStatus =  verifyLinkedAccountStatus.getLinkedAccountStatus()!=null ? verifyLinkedAccountStatus.getLinkedAccountStatus() : "2";
            linkedIdField.setVal(linkedAccountStatus);
            Code linkedAccountStatusCode = staticIntegrationService.loadCode(CodeCategory.LINKED_ACCOUNT_STATUS,
                    AvaloqGatewayUtil.asString(linkedIdField),
                    serviceErrors);
            LinkedAccountStatusDto linkedaccountStatus = new LinkedAccountStatusDto();
            linkedaccountStatus.setLinkedAccountStatus(LinkedAccountStatus.forIntlId(linkedAccountStatusCode.getIntlId()));
            for(Field field: linkedAccountStatusCode.getFields()){
                if(GEN_CODE.equals(field.getName())){
                    boolean genCode=  VERIFY_CODE_SATATUS.equals(field.getValue()) ? true: false;
                    linkedaccountStatus.setGenCode(genCode);
                }
                else if(VEFY_CODE.equals(field.getName())) {
                    boolean vefyCode = VERIFY_CODE_SATATUS.equals(field.getValue()) ? true : false;
                    linkedaccountStatus.setVfyCode(vefyCode);
                }

            }
            linkedAccountDetailsDto.setLinkedAccountStatus(linkedaccountStatus);
        }
        return linkedAccountDetailsDto;
    }

}
