package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Super Personal Tax Deduction Capture and Vary Details - Submit
 */
@Service
public class SavePersonalTaxDeductionDtoServiceImpl implements SavePersonalTaxDeductionDtoService {
    @Autowired
    private PersonalTaxDeductionIntegrationService personalTaxDeductionIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;


    /**
     * Submit - save super personal tax deduction
     *
     * @param keyedObject
     * @param serviceErrors
     *
     * @return
     */
    @Override
    public PersonalTaxDeductionNoticeTrxnDto submit(PersonalTaxDeductionNoticeTrxnDto keyedObject, ServiceErrors serviceErrors) {
        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(keyedObject.getKey().getAccountId()));
        final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        final Date date = new DateTime(keyedObject.getDate()).toDate();
        final DateTime financialYearStartDate = ApiFormatter.parseDate(DateUtil.getFinYearStartDate(date));
        final DateTime financialYearEndDate = ApiFormatter.parseDate(DateUtil.getFinYearEndDate(date));
        final String originalDocId = keyedObject.getDocId();

        if (originalDocId == null) {
            return personalTaxDeductionIntegrationService.createTaxDeductionNotice(account.getAccountNumber(),
                    financialYearStartDate, financialYearEndDate, keyedObject.getAmount());
        }
        else {
            return personalTaxDeductionIntegrationService.varyTaxDeductionNotice(account.getAccountNumber(),
                    originalDocId, financialYearStartDate, financialYearEndDate, keyedObject.getAmount());
        }
    }
}
