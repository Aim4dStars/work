package com.bt.nextgen.api.profile.v1.service;

import com.bt.nextgen.api.profile.v1.model.AggregatedRoleDto;
import com.bt.nextgen.api.profile.v1.model.JobRoleConverter;
import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.bt.nextgen.api.profile.v1.model.ProfileRoles;
import com.bt.nextgen.api.profile.v1.model.UnderlyingRoleDto;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AggregatedRoleUtilTest {

    @InjectMocks
    private AggregatedRoleUtil aggregatedRoleUtil;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    private BrokerIntegrationService brokerService;

    private ProfileDetailsDto profileDto;
    private UserProfile activeProfile;
    private ProfileRoles role1;
    private ProfileRoles role2;
    private ProfileRoles role3;
    private ServiceErrors serviceErrors = new FailFastErrorsImpl();

    @Before
    public void setup() {
        activeProfile = mock(UserProfile.class);
        when(activeProfile.getProfileId()).thenReturn("job1");

        role1 = mock(ProfileRoles.class);
        when(role1.getProfileId()).thenReturn(EncodedString.fromPlainText("job1").toString());

        role2 = mock(ProfileRoles.class);
        when(role2.getProfileId()).thenReturn(EncodedString.fromPlainText("job2").toString());

        role3 = mock(ProfileRoles.class);
        when(role3.getProfileId()).thenReturn(EncodedString.fromPlainText("job3").toString());

        profileDto = new ProfileDetailsDto();
        profileDto.setRoles(Arrays.asList(role1, role2, role3));
    }

    @Test
    public void testGetAggregatedRoles_whenAdviserRolesPresent_thenJobsAreGroupedByRoleAndUserExperience() {
        JobProfile job1 = mock(JobProfile.class);
        when(job1.getJobRole()).thenReturn(JobRole.ADVISER);
        when(job1.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(job1.getProfileId()).thenReturn("job1");

        JobProfile job2 = mock(JobProfile.class);
        when(job2.getJobRole()).thenReturn(JobRole.ADVISER);
        when(job2.getUserExperience()).thenReturn(UserExperience.ASIM);
        when(job2.getProfileId()).thenReturn("job2");

        JobProfile job3 = mock(JobProfile.class);
        when(job3.getJobRole()).thenReturn(JobRole.ADVISER);
        when(job3.getUserExperience()).thenReturn(UserExperience.ADVISED);
        when(job3.getProfileId()).thenReturn("job3");

        when(profileService.getAvailableProfiles()).thenReturn(Arrays.asList(job1, job2, job3));

        Broker broker = mock(Broker.class);
        when(broker.getPositionName()).thenReturn("Broker name");
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey"));
        when(brokerService.getBrokersForJob(eq(job1), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));
        when(brokerService.getBrokersForJob(eq(job2), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));
        when(brokerService.getBrokersForJob(eq(job3), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));

        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getPositionName()).thenReturn("Dealer group name");
        when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerBroker);
        
        aggregatedRoleUtil.setAggregatedRoles(profileDto, activeProfile, serviceErrors);

        assertNotNull(profileDto.getAggregatedRoles());
        assertEquals(2, profileDto.getAggregatedRoles().size());

        AggregatedRoleDto aggregatedRole = profileDto.getAggregatedRoles().get(0);
        assertEquals(JobRoleConverter.ADVISER.toString(), aggregatedRole.getRole());
        assertEquals("Investor managed", aggregatedRole.getUserExperienceDisplay());
        assertEquals(false, aggregatedRole.isActive());
        assertEquals(1, aggregatedRole.getUnderlyingRoles().size());

        UnderlyingRoleDto underlyingRole = aggregatedRole.getUnderlyingRoles().get(0);
        assertEquals("job2", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role2.getProfileId(), underlyingRole.getProfileId());
        assertEquals(false, underlyingRole.isActive());
        assertEquals("Dealer group name", underlyingRole.getDealerGroupName());
        assertEquals(1, underlyingRole.getBrokerCount());
        assertEquals("Broker name", underlyingRole.getBrokerNames().get(0));

        aggregatedRole = profileDto.getAggregatedRoles().get(1);
        assertEquals(JobRoleConverter.ADVISER.toString(), aggregatedRole.getRole());
        assertEquals("Advised", aggregatedRole.getUserExperienceDisplay());
        assertEquals(true, aggregatedRole.isActive());
        assertEquals(2, aggregatedRole.getUnderlyingRoles().size());

        underlyingRole = aggregatedRole.getUnderlyingRoles().get(0);
        assertEquals("job1", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role1.getProfileId(), underlyingRole.getProfileId());
        assertEquals(true, underlyingRole.isActive());
        assertEquals("Dealer group name", underlyingRole.getDealerGroupName());
        assertEquals(1, underlyingRole.getBrokerCount());
        assertEquals("Broker name", underlyingRole.getBrokerNames().get(0));

        underlyingRole = aggregatedRole.getUnderlyingRoles().get(1);
        assertEquals("job3", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role3.getProfileId(), underlyingRole.getProfileId());
        assertEquals(false, underlyingRole.isActive());
        assertEquals("Dealer group name", underlyingRole.getDealerGroupName());
        assertEquals(1, underlyingRole.getBrokerCount());
        assertEquals("Broker name", underlyingRole.getBrokerNames().get(0));
    }

    @Test
    public void testGetAggregatedRoles_whenAssistantRolesPresent_thenJobsAreGroupedByRoleAndHaveBrokerNameList() {
        JobProfile job1 = mock(JobProfile.class);
        when(job1.getJobRole()).thenReturn(JobRole.ASSISTANT);
        when(job1.getProfileId()).thenReturn("job1");

        JobProfile job2 = mock(JobProfile.class);
        when(job2.getJobRole()).thenReturn(JobRole.PARAPLANNER);
        when(job2.getProfileId()).thenReturn("job2");

        when(profileService.getAvailableProfiles()).thenReturn(Arrays.asList(job1, job2));

        Broker broker = mock(Broker.class);
        when(broker.getPositionName()).thenReturn("Broker name");
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey"));

        Broker adviserBroker = mock(Broker.class);
        when(adviserBroker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        when(adviserBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey"));

        when(brokerService.getBrokersForJob(eq(job1), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));
        when(brokerService.getBrokersForJob(eq(job2), any(ServiceErrors.class))).thenReturn(
                Arrays.asList(adviserBroker, broker, broker, broker, broker, broker));

        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getPositionName()).thenReturn("Dealer group name");
        when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerBroker);

        BrokerUser brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFirstName()).thenReturn("FirstName");
        when(brokerUser.getLastName()).thenReturn("LastName");
        when(brokerService.getAdviserBrokerUser(broker.getKey(), serviceErrors)).thenReturn(brokerUser);

        aggregatedRoleUtil.setAggregatedRoles(profileDto, activeProfile, serviceErrors);

        assertNotNull(profileDto.getAggregatedRoles());
        assertEquals(2, profileDto.getAggregatedRoles().size());

        AggregatedRoleDto aggregatedRole = profileDto.getAggregatedRoles().get(0);
        assertEquals(JobRoleConverter.ASSISTANT.toString(), aggregatedRole.getRole());
        assertEquals(true, aggregatedRole.isActive());
        assertEquals(1, aggregatedRole.getUnderlyingRoles().size());

        UnderlyingRoleDto underlyingRole = aggregatedRole.getUnderlyingRoles().get(0);
        assertEquals("job1", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role1.getProfileId(), underlyingRole.getProfileId());
        assertEquals(true, underlyingRole.isActive());
        assertEquals("Dealer group name", underlyingRole.getDealerGroupName());
        assertEquals(1, underlyingRole.getBrokerCount());
        assertEquals("Broker name", underlyingRole.getBrokerNames().get(0));

        aggregatedRole = profileDto.getAggregatedRoles().get(1);
        assertEquals(JobRoleConverter.PARAPLANNER.toString(), aggregatedRole.getRole());
        assertEquals(false, aggregatedRole.isActive());
        assertEquals(1, aggregatedRole.getUnderlyingRoles().size());

        underlyingRole = aggregatedRole.getUnderlyingRoles().get(0);
        assertEquals("job2", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role2.getProfileId(), underlyingRole.getProfileId());
        assertEquals(false, underlyingRole.isActive());
        assertEquals("Dealer group name", underlyingRole.getDealerGroupName());
        assertEquals(6, underlyingRole.getBrokerCount());
        assertEquals(5, underlyingRole.getBrokerNames().size());
        assertEquals("FirstName LastName", underlyingRole.getBrokerNames().get(0));
    }

    @Test
    public void testGetAggregatedRoles_whenRolesOutsideHierarchyPresent_thenUnderlyingRolesOnlyHaveBrokerName() {
        JobProfile job1 = mock(JobProfile.class);
        when(job1.getJobRole()).thenReturn(JobRole.INVESTMENT_MANAGER);
        when(job1.getProfileId()).thenReturn("job1");

        JobProfile job2 = mock(JobProfile.class);
        when(job2.getJobRole()).thenReturn(JobRole.ACCOUNTANT);
        when(job2.getProfileId()).thenReturn("job2");

        when(profileService.getAvailableProfiles()).thenReturn(Arrays.asList(job1, job2));

        Broker broker = mock(Broker.class);
        when(broker.getPositionName()).thenReturn("Broker name");
        when(brokerService.getBrokersForJob(eq(job1), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));
        when(brokerService.getBrokersForJob(eq(job2), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));

        aggregatedRoleUtil.setAggregatedRoles(profileDto, activeProfile, serviceErrors);

        assertNotNull(profileDto.getAggregatedRoles());
        assertEquals(2, profileDto.getAggregatedRoles().size());

        AggregatedRoleDto aggregatedRole = profileDto.getAggregatedRoles().get(0);
        assertEquals(JobRoleConverter.ACCOUNTANT.toString(), aggregatedRole.getRole());
        assertEquals(false, aggregatedRole.isActive());
        assertEquals(1, aggregatedRole.getUnderlyingRoles().size());

        UnderlyingRoleDto underlyingRole = aggregatedRole.getUnderlyingRoles().get(0);
        assertEquals("job2", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role2.getProfileId(), underlyingRole.getProfileId());
        assertEquals(false, underlyingRole.isActive());
        assertNull(underlyingRole.getDealerGroupName());
        assertEquals(1, underlyingRole.getBrokerCount());
        assertEquals("Broker name", underlyingRole.getBrokerNames().get(0));

        aggregatedRole = profileDto.getAggregatedRoles().get(1);
        assertEquals(JobRoleConverter.INVESTMENT_MANAGER.toString(), aggregatedRole.getRole());
        assertEquals(true, aggregatedRole.isActive());
        assertEquals(1, aggregatedRole.getUnderlyingRoles().size());

        underlyingRole = aggregatedRole.getUnderlyingRoles().get(0);
        assertEquals("job1", EncodedString.toPlainText(underlyingRole.getProfileId()));
        assertEquals(role1.getProfileId(), underlyingRole.getProfileId());
        assertEquals(true, underlyingRole.isActive());
        assertNull(underlyingRole.getDealerGroupName());
        assertEquals(1, underlyingRole.getBrokerCount());
        assertEquals("Broker name", underlyingRole.getBrokerNames().get(0));
    }
}
