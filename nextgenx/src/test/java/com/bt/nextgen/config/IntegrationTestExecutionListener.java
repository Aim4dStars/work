package com.bt.nextgen.config;

import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.util.SamlUtil;

import static org.springframework.util.StringUtils.hasText;

public class IntegrationTestExecutionListener extends AbstractTestExecutionListener
{
	private static final Logger logger = LoggerFactory.getLogger(IntegrationTestExecutionListener.class);
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception
	{
		parseTestSecureContextAnnotation(testContext);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		if (testContext.getTestMethod().getAnnotation(SecureTestContext.class) != null) {
			logger.info("Clearing security context after somebody has messed with it");
			SecurityContextHolder.clearContext();
		}
	}

	protected void parseTestSecureContextAnnotation(TestContext testContext)
	{
		SecureTestContext secureTestContext = testContext.getTestMethod().getAnnotation(SecureTestContext.class);
		if (secureTestContext != null)
		{
			logger.info("Found a secure test context {}", ToStringBuilder.reflectionToString(secureTestContext));
			TestingAuthenticationToken authentication = new TestingAuthenticationToken(
					secureTestContext.username(),
					secureTestContext.password(),
					secureTestContext.authorities());

			Profile dummyProfile = new Profile(new SamlToken(
					SamlUtil.loadSaml(secureTestContext.username(), secureTestContext.authorities(), secureTestContext.customerId()
					)));
			dummyProfile.setCurrentProfileId(secureTestContext.profileId());

			if (hasText(secureTestContext.profileId()) && hasText(secureTestContext.jobRole()) && hasText(secureTestContext.jobId()))
			{
				final JobProfile jobProfile = createJobProfile(secureTestContext);
				dummyProfile.setActiveProfile(createUserProfile(secureTestContext, jobProfile));
				dummyProfile.setActiveJobProfile(jobProfile);
			}
			authentication.setDetails(dummyProfile);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	private JobProfile createJobProfile(SecureTestContext context)
	{
		return createJobProfile(context.profileId(), context.jobRole(), context.jobId(), context.userExperience());
	}

	private JobProfile createJobProfile(String profileId, String jobRole, String jobId, UserExperience experience)
	{
		JobProfileImpl jobProfile = new JobProfileImpl();
		JobRole role;
		try {
			role = JobRole.valueOf(jobRole.toUpperCase());
		} catch (Exception e) {
			role = JobRole.OTHER;
		}
		jobProfile.setProfileId(profileId);
		jobProfile.setJobRole(role);
		jobProfile.setJob(JobKey.valueOf(jobId));
		jobProfile.setUserExperience(experience);
		return jobProfile;
	}

	private UserProfile createUserProfile(SecureTestContext context, JobProfile jobProfile)
	{
		return createUserProfile(context.customerId(), jobProfile);
	}

	private UserProfile createUserProfile(String customerId, JobProfile jobProfile)
	{
		UserInformationImpl info = new UserInformationImpl();
		info.setProfileId(jobProfile.getProfileId());
		info.setCustomerId(customerId);
		info.setJobKey(jobProfile.getJob());
		info.setJobId(jobProfile.getJob().getId());
		return new UserProfileAdapterImpl(info, jobProfile);
	}
}
