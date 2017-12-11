package com.bt.nextgen.service.avaloq.userprofile;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.userinformation.UserInformationEnumTemplate;
import com.bt.nextgen.service.integration.userprofile.AvailableProfiles;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service will provide the available profiles for the logged in user. The output from this service will be further used for
 * fetching the applicable roles.
 */
@Service
public class AvaloqProfileIntegrationServiceImpl implements ProfileIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(AvaloqProfileIntegrationServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    /**
     * This service loads the available job profiles of the logged in user. It is integrated against task template -
     * BTFG$UI_SEC_USER_LIST.MY#JOB_USER
     * 
     * @return List <JobProfile>
     */
    @Override
    public List<JobProfile> loadAvailableJobProfiles(ServiceErrors serviceErrors) {
        logger.debug("loadAvailableJobProfiles for user");
        try {
            AvaloqRequest avaloqReportRequest = new AvaloqReportRequestImpl(UserInformationEnumTemplate.JOB_PROFILE_LIST);
            AvailableProfiles availableProfiles = avaloqService.executeReportRequestToDomain(avaloqReportRequest.forBootstrap(),
                    AvailableProfilesImpl.class, serviceErrors);
            return getOpenProfiles(availableProfiles);
        } catch (Exception e) {
            logger.error("Error loading available job profiles", e);
            throw e;
        }
    }

    /**
     * Loads the available job profiles for a given user. It is integrated against task template -
     * BTFG$UI_SEC_USER_LIST.LOOKUP#JOB_USER
     * 
     * @param identifier
     *            GCM_id of the user to load
     * @param serviceErrors
     *            errors DTO to communicate issues
     * @return List <JobProfile>
     */
    @Override
    public List<JobProfile> loadAvailableJobProfilesForUser(BankingCustomerIdentifier identifier, ServiceErrors serviceErrors) {
        try {
            AvaloqRequest avaloqReportRequest = new AvaloqReportRequestImpl(
                    UserInformationEnumTemplate.JOB_PROFILE_LIST_FOR_USER);
            AvailableProfiles availableProfiles = avaloqService.executeReportRequestToDomain(
                    avaloqReportRequest.forParam(com.btfin.panorama.core.security.avaloq.userprofile.UserInformationParams.PARAM_AUTH_KEY, identifier.getBankReferenceKey().getId()),
                    AvailableProfilesImpl.class, serviceErrors);
            return getOpenProfiles(availableProfiles);
        } catch (Exception e) {
            logger.error("Error loading available job profiles for user", e);
            throw e;
        }
    }

    protected List<JobProfile> getOpenProfiles(AvailableProfiles availableProfiles) {
        List<JobProfile> openProfiles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(availableProfiles.getJobProfiles())) {
            for (JobProfile job : availableProfiles.getJobProfiles()) {
                if (job.getCloseDate() == null || job.getCloseDate().isAfter(LocalDate.now().toDateTimeAtStartOfDay())) {
                    openProfiles.add(job);
                }
            }
        }
        return openProfiles;
    }
}
