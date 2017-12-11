package com.bt.nextgen.api.profile.v1.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.bt.nextgen.api.profile.v1.model.AggregatedRoleDto;
import com.bt.nextgen.api.profile.v1.model.JobRoleConverter;
import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.bt.nextgen.api.profile.v1.model.ProfileRoles;
import com.bt.nextgen.api.profile.v1.model.UnderlyingRoleDto;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.sort;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AggregatedRoleUtil {

    private static final Logger logger = getLogger(AggregatedRoleUtil.class);
    private static final Comparator<AggregatedRoleDto> BY_GROUP_ROLE = new Comparator<AggregatedRoleDto>() {
        @Override
        public int compare(AggregatedRoleDto group1, AggregatedRoleDto group2) {
            return group1.getRole().compareTo(group2.getRole());
        }
    };

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    /**
     * Group the current user's available jobs by role
     * 
     * @param profileDto
     * @param activeProfile
     * @param serviceErrors
     */
    protected void setAggregatedRoles(ProfileDetailsDto profileDto, UserProfile activeProfile, ServiceErrors serviceErrors) {
        if (!Properties.getSafeBoolean("feature.oeContextSwitchingUpdate")) {
            return;
        }

        List<AggregatedRoleDto> aggregatedRoles = new ArrayList<>();

        List<JobProfile> availableProfiles = profileService.getAvailableProfiles();
        Group<JobProfile> groupedJobs = Lambda.group(availableProfiles, Lambda.by(Lambda.on(JobProfile.class).getJobRole()));
        Iterator<String> iter = groupedJobs.keySet().iterator();

        while (iter.hasNext()) {
            JobRole role = JobRole.forCode(iter.next());
            List<JobProfile> jobsWithRole = groupedJobs.find(role.toString());

            aggregatedRoles.addAll(getAggregatedRoleDtos(role, jobsWithRole, profileDto, activeProfile, serviceErrors));
        }

        sort(aggregatedRoles, BY_GROUP_ROLE);
        profileDto.setAggregatedRoles(aggregatedRoles);

        logger.info("Aggregated role(s) into {} group(s)", aggregatedRoles.size());
    }

    /**
     * Perform subgrouping by user experience to separate the Adviser role into two - Adviser and ASIM Adviser. Other role types
     * are unaffected.
     * 
     * @param role
     * @param jobsWithRole
     * @param profileDto
     * @param activeProfile
     * @param serviceErrors
     * @return
     */
    private List<AggregatedRoleDto> getAggregatedRoleDtos(JobRole role, List<JobProfile> jobsWithRole, ProfileDetailsDto profileDto,
            UserProfile activeProfile, ServiceErrors serviceErrors) {
        List<AggregatedRoleDto> aggregatedRoles = new ArrayList<>();
        List<List<JobProfile>> jobsList;
        
        if (JobRole.ADVISER.equals(role)) {
            jobsList = groupByUserExperience(jobsWithRole);
        } else {
            jobsList = Collections.singletonList(jobsWithRole);
        }

        for (List<JobProfile> jobs : jobsList) {
            aggregatedRoles.add(getAggregatedRoleDto(role, jobs, profileDto, activeProfile, serviceErrors));
        }

        return aggregatedRoles;
    }
    
    /**
     * Group a given list of jobs by user experience
     * 
     * @param jobsWithRole
     * @return
     */
    private List<List<JobProfile>> groupByUserExperience(List<JobProfile> jobsWithRole) {
        List<List<JobProfile>> jobsByUserExperience = new ArrayList<>();

        Group<JobProfile> groupedJobs = Lambda.group(jobsWithRole, Lambda.by(Lambda.on(JobProfile.class).getUserExperience()));
        Iterator<String> iter = groupedJobs.keySet().iterator();

        while (iter.hasNext()) {
            jobsByUserExperience.add(groupedJobs.find(iter.next()));
        }

        return jobsByUserExperience;
    }

    /**
     * Create aggregatedRoleDto for the given role, based on the list of jobs provided.
     * 
     * @param role
     * @param jobsWithRole
     * @param profileDto
     * @param activeProfile
     * @param serviceErrors
     * @return
     */
    private AggregatedRoleDto getAggregatedRoleDto(JobRole role, List<JobProfile> jobsWithRole, ProfileDetailsDto profileDto, UserProfile activeProfile, ServiceErrors serviceErrors) {
        List<UnderlyingRoleDto> underlyingJobs = new ArrayList<>(jobsWithRole.size());
        String roleDisplayName = JobRoleConverter.valueOf(role.name()).toString();
        String userExperienceDisplayName = null;
        
        for (JobProfile job : jobsWithRole) {
            String encodedProfileId = getEncodedProfileIdFromRole(profileDto, job.getProfileId());
            boolean roleIsActive = activeProfile.getProfileId().equalsIgnoreCase(job.getProfileId());

            List<Broker> brokers = brokerService.getBrokersForJob(job, serviceErrors);
            String dealerGroupName = getDealerGroupName(brokers, serviceErrors);
            List<String> brokerNames = getBrokerNames(role, brokers, serviceErrors);
            
            if (job.getUserExperience() != null) {
                // Capture the first available user experience to store at the aggregated level
                userExperienceDisplayName = job.getUserExperience().getDisplayName();
            }

            underlyingJobs.add(new UnderlyingRoleDto(encodedProfileId, dealerGroupName, brokerNames, roleIsActive));
        }

        return new AggregatedRoleDto(roleDisplayName, userExperienceDisplayName, underlyingJobs);
    }

    /**
     * Given a profileDto with the 'roles' field set, find the encoded profile ID for each role and reuse it so that profile IDs
     * in the 'aggregatedRoles' field match those in 'roles'.
     * 
     * @param profileDto
     * @param profileId
     * @return
     */
    private String getEncodedProfileIdFromRole(ProfileDetailsDto profileDto, String profileId) {
        for (ProfileRoles role : profileDto.getRoles()) {
            String decodedId = EncodedString.toPlainText(role.getProfileId());
            if (decodedId.equals(profileId)) {
                return role.getProfileId();
            }
        }
        return null;
    }

    /**
     * Given a list of brokers linked to a job, find the dealer group name on the first broker.
     * 
     * @param brokers
     * @param serviceErrors
     * @return
     */
    private String getDealerGroupName(List<Broker> brokers, ServiceErrors serviceErrors) {
        for (Broker broker : brokers) {
            if (broker.getDealerKey() != null) {
                Broker dealer = brokerService.getBroker(broker.getDealerKey(), serviceErrors);
                return dealer.getPositionName();
            }
        }
        return null;
    }

    /**
     * Given a list of brokers linked to a job, find names of all brokers.
     * 
     * @param role
     * @param brokers
     * @param serviceErrors
     * @return
     */
    private List<String> getBrokerNames(JobRole role, List<Broker> brokers, ServiceErrors serviceErrors) {
        List<String> brokerNames = new ArrayList<>();
        boolean isSupportRole = JobRole.PARAPLANNER.equals(role) || JobRole.ASSISTANT.equals(role);

        for (Broker broker : brokers) {
            if (isSupportRole) {
                brokerNames.add(getBrokerNameForSupportRole(broker, serviceErrors));
            } else {
                brokerNames.add(broker.getPositionName());
                break;
            }

        }
        return brokerNames;
    }

    /**
     * Given a broker, find the appropriate name to display to support users
     * 
     * @param broker
     * @param serviceErrors
     * @return
     */
    private String getBrokerNameForSupportRole(Broker broker, ServiceErrors serviceErrors) {
        if (broker.getBrokerType() == BrokerType.ADVISER) {
            BrokerUser brokerUser = brokerService.getAdviserBrokerUser(broker.getKey(), serviceErrors);
            return brokerUser.getFirstName() + " " + brokerUser.getLastName();
        }
        return broker.getPositionName();
    }
}
