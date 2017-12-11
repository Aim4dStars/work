package com.bt.nextgen.service.avaloq.userprofile;

import com.bt.nextgen.core.exception.AvaloqConnectionException;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.InvestorImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqProfileIntegrationServiceImplTest {
    @InjectMocks
    AvaloqProfileIntegrationServiceImpl profileService;

    @Mock
    private AvaloqReportService avaloqService;

    private List<JobProfile> profiles;
    private AvailableProfilesImpl availableProfiles;

    @Before
    public void setup() {
        profiles = new ArrayList<JobProfile>();
        JobProfileImpl closedProfile = new JobProfileImpl();
        closedProfile.setJob(JobKey.valueOf("closedJobKey1"));
        closedProfile.setProfileId("closedProfileId1");
        closedProfile.setJobRole(JobRole.ACCOUNTANT);
        closedProfile.setCloseDate(DateTime.now().minusDays(10));
        closedProfile.setPersonJobId("closedPersonJobId1");
        closedProfile.setUserExperience(UserExperience.ASIM);
        JobProfileImpl profile = new JobProfileImpl();
        profile.setJob(JobKey.valueOf("fakeJobKey1"));
        profile.setProfileId("fakeProfileId1");
        profile.setJobRole(JobRole.ADVISER);
        profile.setCloseDate(null);
        profile.setPersonJobId("fakePersonJobId1");
        profile.setUserExperience(UserExperience.ADVISED);
        JobProfileImpl profile2 = new JobProfileImpl();
        profile2.setJob(JobKey.valueOf("fakeJobKey2"));
        profile2.setProfileId("fakeProfileId2");
        profile2.setJobRole(JobRole.ADVISER);
        profile2.setCloseDate(DateTime.now().plusDays(10));
        profile2.setPersonJobId("fakePersonJobId2");
        profile2.setUserExperience(UserExperience.ADVISED);
        profiles.add(closedProfile);
        profiles.add(profile);
        profiles.add(profile2);

        availableProfiles = Mockito.mock(AvailableProfilesImpl.class);
        Mockito.when(availableProfiles.getJobProfiles()).thenReturn(profiles);

        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(availableProfiles);
    }

    @Test
    public void testLoadAvailableJobProfiles_whenValid_thenProfilesReturned() {
        List<JobProfile> jobProfiles = profileService.loadAvailableJobProfiles(new ServiceErrorsImpl());
        assertThat(jobProfiles, not(nullValue()));
        assertThat(jobProfiles.size(), is(2));
        assertThat(jobProfiles.get(0).getProfileId(), is("fakeProfileId1"));
        assertThat(jobProfiles.get(1).getProfileId(), is("fakeProfileId2"));
    }

    @Test
    public void testLoadAvailableJobProfiles_whenException_thenExceptionThrown() {
        AvaloqConnectionException exception = new AvaloqConnectionException("intentional exception", new ServiceErrorsImpl());
        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenThrow(exception);
        try {
            profileService.loadAvailableJobProfiles(new ServiceErrorsImpl());
        } catch (AvaloqConnectionException e) {
            assert (true);
            return;
        }
        fail("AvaloqConnectionException Not Thrown");
    }

    @Test
    public void testLoadAvailableJobProfilesForUser_whenValid_thenProfilesReturned() {
        InvestorImpl key = new InvestorImpl();
        key.setGcmId("key");
        List<JobProfile> jobProfiles = profileService.loadAvailableJobProfilesForUser(key, new ServiceErrorsImpl());
        assertThat(jobProfiles, not(nullValue()));
        assertThat(jobProfiles.size(), is(2));
        assertThat(jobProfiles.get(0).getProfileId(), is("fakeProfileId1"));
        assertThat(jobProfiles.get(1).getProfileId(), is("fakeProfileId2"));
    }

    @Test
    public void testLoadAvailableJobProfilesForUser_whenException_thenExceptionThrown() {
        AvaloqConnectionException exception = new AvaloqConnectionException("intentional exception", new ServiceErrorsImpl());
        Mockito.when(avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenThrow(exception);
        try {
            InvestorImpl key = new InvestorImpl();
            key.setGcmId("key");
            profileService.loadAvailableJobProfilesForUser(key, new ServiceErrorsImpl());
        } catch (AvaloqConnectionException e) {
            assert (true);
            return;
        }
        fail("AvaloqConnectionException Not Thrown");
    }

    @Test
    public void testGetOpenProfiles_whenHasClosedProfiles_thenClosedProfilesExcluded() {
        List<JobProfile> jobProfiles = profileService.getOpenProfiles(availableProfiles);
        assertThat(jobProfiles, not(nullValue()));
        assertThat(jobProfiles.size(), is(2));
        assertThat(jobProfiles.get(0).getProfileId(), is("fakeProfileId1"));
        assertThat(jobProfiles.get(1).getProfileId(), is("fakeProfileId2"));
    }

    @Test
    public void testGetOpenProfiles_whenNoProfiles_thenEmptyListReturned() {
        AvailableProfilesImpl availableProfiles = new AvailableProfilesImpl();
        List<JobProfile> jobProfiles = profileService.getOpenProfiles(availableProfiles);
        assertThat(jobProfiles, not(nullValue()));
        assertThat(jobProfiles.size(), is(0));
    }
}
