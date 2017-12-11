package com.bt.nextgen.web.model;

import com.bt.nextgen.config.JsonObjectMapper;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.session.SessionUtils;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.on;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class ClientLogger {

    private final Logger logger = LoggerFactory.getLogger(ClientLogger.class);
    private static final String CLIENT_LOG = "CLIENT_LOG";
    private static final String CLIENT_PERF = "CLIENT_PERF";
    private static final String NO_DG_AVAILABLE = "Not available";
    private static final String SEPARATOR = "~";

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    /**
     * Logs the information sent from UI, used for analysis
     *
     * @param clientLogInformation - Client log information object
     */
    public void logClientInfo(ClientLogInformation clientLogInformation) throws JsonProcessingException {
        final String clientType = clientLogInformation.getClientType();
        final String clientVersion = clientLogInformation.getClientVersion();
        final String originatingSystem = clientLogInformation.getOriginatingSystem();

        logValues(CLIENT_LOG, clientType, clientVersion, originatingSystem, clientLogInformation.getErrorLogs());
        logValues(CLIENT_PERF, clientType, clientVersion, originatingSystem, clientLogInformation.getPerformanceLogs());
    }

    /**
     * @param logType           - Type of the log [Performance/Error]
     * @param clientType        - Client type [web/nw/mobile]
     * @param clientVersion     - client version info
     * @param originatingSystem - user's originating system
     * @param clientLogs        - UI logs [error/performance]
     * @throws JsonProcessingException - Exception thrown when log serialization to JSON fails
     */
    private void logValues(String logType, String clientType, String clientVersion, String originatingSystem,
                           List<? extends ClientLog> clientLogs) throws JsonProcessingException {
        if (isNotEmpty(clientLogs)) {
            final JsonObjectMapper mapper = new JsonObjectMapper();
            long start=System.currentTimeMillis();
            logger.debug("Client Logger calling getActive Profile at ");
            final UserProfile profile = profileService.getActiveProfile();
            logger.debug("Client Logger calling getActive Profile method end with duration - {} ",System.currentTimeMillis()-start);
            String dealerGroup = getDealerGroup(profile);
            for (final ClientLog clientLog : clientLogs) {
                logger.info(MarkerFactory.getMarker(logType),
                        logType + ", {}, {}, {}, {}, {}, {}, {}",
                        clientLog.getLocation(),
                        clientType,
                        clientVersion,
                        originatingSystem,
                        profile.getJobRole().name(),
                        dealerGroup,
                        mapper.writeValueAsString(clientLog));
            }
        }
    }

    /**
     * Gets the dealer group(s) for the active user
     *
     * @param profile - Active user profile
     */
    private String getDealerGroup(JobProfile profile) {
        String dealerGroupName = NO_DG_AVAILABLE;
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        long start=System.currentTimeMillis();
        logger.debug("getDealerGroup in Client Logger at{} ",start);
        if (JobRole.INVESTOR.equals(profile.getJobRole())) {
            // There may be more than one dealer groups for the user, so logging all
            final Set<Broker> dealerGroups = brokerHelperService.getDealerGroupsforInvestor(serviceErrors);
            dealerGroupName = join(collect(dealerGroups, on(Broker.class).getPositionName()), SEPARATOR);
        } else {
            final Broker dealerGroup = brokerHelperService.getDealerGroupForIntermediary(profile, serviceErrors);
            if (dealerGroup != null) {
                dealerGroupName = dealerGroup.getPositionName();
            }

        }
        logger.debug("getDealerGroup in Client Logger completed in {}s ",(System.currentTimeMillis()-start)/1000);
        return dealerGroupName;
    }
}
