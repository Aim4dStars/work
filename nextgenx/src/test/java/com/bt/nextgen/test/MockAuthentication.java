package com.bt.nextgen.test;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.util.SamlUtil;
import org.joda.time.DateTime;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class MockAuthentication
{

	public void mockAuthentication(String role){
		TestingAuthenticationToken authentication = new TestingAuthenticationToken("", "", role);
		Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
		authentication.setDetails(dummyProfile);
        dummyProfile.setActiveJobProfile(getJobProfile(JobRole.ADVISER, "job id 1"));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

    private JobProfile getJobProfile(final JobRole role, final String jobId)
    {
        JobProfile job = new JobProfile()
        {
            @Override public JobRole getJobRole()
            {
                return role;
            }

            @Override public String getPersonJobId()
            {
                return null;
            }

            @Override public JobKey getJob()
            {
                return JobKey.valueOf(jobId);
            }

            @Override public String getProfileId()
            {
                return null;
            }

            @Override public DateTime getCloseDate(){
                return null;
            }

            @Override
            public UserExperience getUserExperience() {
                return null;
            }
        };
        return job;
    }

}
