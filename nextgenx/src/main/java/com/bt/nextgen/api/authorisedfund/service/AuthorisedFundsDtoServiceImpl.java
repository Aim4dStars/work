package com.bt.nextgen.api.authorisedfund.service;

import com.bt.nextgen.api.bgl.service.AccountingSoftwareConnectionService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;
import com.bt.nextgen.service.integration.authorisedfund.service.AuthorisedFundsIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by L067218 on 18/04/2016.
 */
@Service
public class AuthorisedFundsDtoServiceImpl implements AuthorisedFundsDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private AuthorisedFundsIntegrationService authorisedFundsIntegrationService;

    @Autowired
    private AccountingSoftwareConnectionService accountingSoftwareConnectionService;

    @Autowired
    UserProfileService userProfileService;


    public boolean isAccountAuthorised(AccountKey accountKey) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        WrapAccountDetailImpl accountDetail = (WrapAccountDetailImpl) accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        SmsfImpl smsfDetail = (SmsfImpl) accountDetail.getOwners().get(0);
        String abn = smsfDetail.getAbn();

        String accountantGcmId = accountingSoftwareConnectionService.getAccountantGcmIdForAccount(EncodedString.fromPlainText(accountKey.getId()).toString());
        List<AuthorisedFundDetail> fundList = authorisedFundsIntegrationService.loadAuthorisedFunds(accountantGcmId, TokenIssuer.BGL);

        for (AuthorisedFundDetail fundDetail : fundList) {
            if (fundDetail.getAbn() != null && fundDetail.getAbn().equals(abn)) {
                return true;
            }
        }
        return false;
    }
}
