package com.bt.nextgen.service.integration.userinformation;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserInformationIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	UserInformationIntegrationService userService;

	@Test
	@SecureTestContext(username="adviser")
	public void testUserInformationService() throws Exception
	{
		String profileId = "";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		JobProfile profile = new JobProfileImpl();
		UserInformation user = userService.loadUserInformation(profile, serviceErrors);
		assertNotNull(user);

		assertEquals("71819", user.getJob().getId());
		assertNotNull(user.getFunctionalRoles());
		assertEquals("45278", user.getClientKey().getId());
		assertEquals("1531", user.getProfileId());

	}


    @Test
    @SecureTestContext(username="safiCheckUserInformation")
    public void testUserInformationServiceSafiCheck() throws Exception
    {
        String profileId = "";
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        JobProfile profile = new JobProfileImpl();
        UserInformation user = userService.loadUserInformation(profile, serviceErrors);
        assertNotNull(user);

        assertEquals("71819", user.getJob().getId());
        assertNotNull(user.getFunctionalRoles());
        assertEquals("45278", user.getClientKey().getId());
        assertEquals("1531", user.getProfileId());
        assertEquals("person-120_3030", user.getFirstName());
        assertEquals("person-120_last", user.getLastName());
        assertEquals("Nicole Smith", user.getFullName());
        assertEquals("4aadd0aa-3a6f-4a56-ba9b-f9b6c08f78d5", user.getSafiDeviceId());
    }
	@Test
	@SecureTestContext(username="adviser")
	public void testLoggedInPerson() throws Exception
	{
		String profileId = "";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		JobProfile profile = new JobProfileImpl();
		ClientIdentifier person = userService.getLoggedInPerson(profile, serviceErrors);
		assertNotNull(person);

		assertEquals("45278", person.getClientKey().getId());
	}

	@Test
	@SecureTestContext(username="adviser")
	public void testUserRole() throws Exception
	{
		String profileId = "";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		JobProfile profile = new JobProfileImpl();
		JobPermission role = userService.getAvailableRoles(profile, serviceErrors);
		assertNotNull(role);
	}

	@Test
	@SecureTestContext(username="adviser")
	public void testUserIdentifier() throws Exception
	{
		String profileId = "";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		JobProfile profile = new JobProfileImpl();
		UserInformation identifier = userService.getUserIdentifier(profile, serviceErrors);
		assertNotNull(identifier);

		assertEquals("1531", identifier.getProfileId());
	}

    @Test
	
    @SecureTestContext(username = "explode", customerId = "201101101")
    public void testUserRoleError() throws Exception
    {
        String profileId = "";
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        JobProfile profile = new JobProfileImpl();
        try {
            JobPermission role = userService.getAvailableRoles(profile, serviceErrors);
        }
        catch(Exception e)
        {}

        assertThat(serviceErrors.hasErrors(), Is.is(true));
    }

}
