package com.bt.nextgen.service.avaloq.logout;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.abs.trxservice.reguser.v1_0.RegUserReq;
import com.btfin.abs.trxservice.reguser.v1_0.RegUserRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.bt.nextgen.service.avaloq.AvaloqUtils.makeLogoutUserRequest;

/**
 * This service is used to notify avaloq about logged out user
 */

@Service
@SuppressWarnings("squid:S1188")
public class LogoutUserIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements LogoutUserIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutUserIntegrationServiceImpl.class);

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Resource(name = "userDetailsService")
    private AvaloqBankingAuthorityService userSamlService;

    /**
     * This method notifies Avaloq upon user logs out to prevent sending unread notification count cache invalidation message
     * for logged out user
     */
    @Override
    public UserLogoutDetail notifyUserLogout(final ServiceErrors serviceErrors) throws Exception {
        LOGGER.debug("Entered notifyUserLogout..");
        if (userSamlService.getSamlToken() == null) {
            LOGGER.info("User SAML is not available. Will not deregister the user from Avaloq.");
            return null;
        }
        return new IntegrationSingleOperation<UserLogoutDetail>("notifyUserLogout", serviceErrors) {
            @Override
            public UserLogoutDetail performOperation() {

                String respDateVal;
                RegUserReq regUserReq = makeLogoutUserRequest();
                RegUserRsp regUserRsp = webserviceClient.sendToWebService(regUserReq, AvaloqOperation.REG_USER_REQ, serviceErrors);
                UserLogoutDetail userLogoutDetail = new UserLogoutDetailImpl();
                if (null != regUserRsp.getData().getSecUser().getVal()) {
                    userLogoutDetail.setSecUser(regUserRsp.getData().getSecUser().getVal());
                }
                if (null != regUserRsp.getData().getLastActionTime()) {
                    respDateVal = regUserRsp.getData().getLastActionTime().getVal().toString();
                    respDateVal = respDateVal.substring(0, respDateVal.indexOf("+")) ;
                    DateTime lastActionTime = DateTime.parse(respDateVal,
                            DateTimeFormat.forPattern(Constants.AVALOQ_RESPONSE_DATE_FORMAT));
                    userLogoutDetail.setLastActionTime(lastActionTime);
                }
                if (null != regUserRsp.getData().getLoginTime()) {
                    respDateVal = regUserRsp.getData().getLoginTime().getVal().toString();
                    respDateVal = respDateVal.substring(0, respDateVal.indexOf("+"));
                    DateTime loginTime = DateTime.parse(respDateVal,
                            DateTimeFormat.forPattern(Constants.AVALOQ_RESPONSE_DATE_FORMAT));
                    userLogoutDetail.setLoginTime(loginTime);
                }
                return userLogoutDetail;
            }
        }.run();
    }

}