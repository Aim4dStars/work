package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.util.SamlUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractUserCachedAvaloqIntegrationServiceTest
{

	public class TestCacheKeyIntegrationService extends AbstractUserCachedAvaloqIntegrationService
	{

	}

	private SamlToken adviserToken;


	@Before
	public void setup()
	{
		adviserToken = new SamlToken(SamlUtil.loadSaml());
	}

	@Mock
	AvaloqBankingAuthorityService avaloqBankingAuthorityService;

	@InjectMocks
	private final TestCacheKeyIntegrationService underTest= new TestCacheKeyIntegrationService();

	private final TestCacheKeyIntegrationService underTestNoMocksInjected = new TestCacheKeyIntegrationService();


	@Test
	public void testGetAvaloqId()
	{
		when(avaloqBankingAuthorityService.getSamlToken()).thenReturn(adviserToken);

		assertThat(underTest.getAvaloqId(), is(not(nullValue())));

		assertThat(underTest.getAvaloqId(),is("217082760"));

	}

	@Test
	public void testGetAvaloqId_whenNoSaml()
	{
		when(avaloqBankingAuthorityService.getSamlToken()).thenReturn(null);

		assertThat(underTest.getAvaloqId(), is(not(nullValue())));

		assertThat(underTest.getAvaloqId(),is(""));
	}

	@Test
	public void testGetAvaloqId_noBankingAuthService()
	{
		assertThat(underTestNoMocksInjected.getAvaloqId(), is(not(nullValue())));

		assertThat(underTestNoMocksInjected.getAvaloqId(),is(""));
	}


	@Test
	public void testGetActiveProfile_noBankingAuthService()
	{
		assertThat(underTestNoMocksInjected.getActiveProfileCacheKey(), is(not(nullValue())));

		assertThat(underTestNoMocksInjected.getActiveProfileCacheKey(),is(""));
	}

	@Test
	public void testGetActiveProfile()
	{
        JobProfile job = Mockito.mock(JobProfile.class);
        when(job.getJobRole()).thenReturn(JobRole.ADVISER);
        when(job.getJob()).thenReturn(JobKey.valueOf("jobId"));
        when(job.getProfileId()).thenReturn("profileId");
        when(avaloqBankingAuthorityService.getActiveJobProfile()).thenReturn(job);

		assertThat(underTest.getActiveProfileCacheKey(), is(not(nullValue())));

		assertThat(underTest.getActiveProfileCacheKey(),is("profileId"));
	}
}
