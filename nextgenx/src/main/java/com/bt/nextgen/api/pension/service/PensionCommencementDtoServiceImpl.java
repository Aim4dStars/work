package com.bt.nextgen.api.pension.service;

import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.pension.PensionCommencementIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by L067218 on 12/09/2016.
 */
@Service
public class PensionCommencementDtoServiceImpl implements PensionCommencementDtoService {

    @Autowired
    private PensionCommencementIntegrationService pensionCommencementIntegrationService;


    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    /**
     * Submit - commence pension
     *
     * @param pensionTrxnDto DTO for pension commencement.
     * @param serviceErrors  Object to add service errors to.
     *
     * @return DTO containing the result of pension commencement.
     */
    @Override
    public PensionTrxnDto submit(PensionTrxnDto pensionTrxnDto, ServiceErrors serviceErrors) {
        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(pensionTrxnDto.getKey().getAccountId()));
        final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        final PensionTrxnDto retval = pensionCommencementIntegrationService.commencePension(account.getAccountNumber());

        // clear cache of account details so that the next retrieval gets the updated pension details
        if (retval.getTransactionStatus() != null && "saved".equalsIgnoreCase(retval.getTransactionStatus())) {
            accountService.clearWrapAccountDetail(accountKey);
        }

        return retval;
    }
}
