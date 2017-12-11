package com.bt.nextgen.core.security;

import com.bt.nextgen.api.profile.model.ProfileRoles;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.core.security.profile.SafiDeviceIdentifier;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.ASIM;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UserProfileServiceSpringIntegrationTest extends BaseSecureIntegrationTest
{

    @Autowired
    UserProfileService userProfileService;

    @SecureTestContext(username="adviserTest", customerId = "201601388")
    public void testActiveProfile_forAdviser()
    {
        //staticIntegrationService.loadCodes(new FailFastErrorsImpl());

        UserProfile info = userProfileService.getActiveProfile();
        assertThat(info,is(notNullValue()));
        assertThat(info.getProfileId(), is(notNullValue()));

        assertThat(info.getClientKey(), is(notNullValue()));
        assertThat(info.getClientKey().getId(), is("47175"));

        assertThat(info.getJob(), is(notNullValue()));
        assertThat(info.getJob().getId(), is("71817"));

        assertThat(info.getBankReferenceKey(), is(notNullValue()));
        assertThat(info.getBankReferenceKey().getId(),is("201601388"));

        assertThat(info.getBankReferenceId(), is(notNullValue()));
        assertThat(info.getBankReferenceId(),is("201601388"));


        //assertThat(info.getBankReferenceId(),is("297104519"));
		/*List<JobProfile> profiles = userProfileService.getAvailableProfiles();
		assertThat(profiles,is(notNullValue()));
		assertThat(profiles.size(),is(1));
		assertThat(profiles.get(0), is(notNullValue()));
		assertThat(profiles.get(0).getJobAuthLevel(), is(notNullValue()));
		assertThat(profiles.get(0).getJobRole(), is(notNullValue()));*/

	/*	Collection<Broker> brokers = brokerIntegrationService.getBrokersForJob(userProfileService.getActiveProfile(),new ServiceErrorsImpl());

		Broker foundBroker = null;

		if(brokers!=null &&brokers.size()>0)
		{

			for(Broker broker:brokers)
				if(broker.getBrokerType().equals(BrokerType.ADVISER))
					foundBroker=broker;
			Broker dealerGroup = brokerIntegrationService.getBroker(foundBroker.getDealerKey(),new FailFastErrorsImpl());
			assertThat(dealerGroup,is(notNullValue()));
			System.out.println(dealerGroup.getPositionName());

			assertThat(foundBroker, is(notNullValue()));
			System.out.println(foundBroker.getKey().getId());
			BrokerUser advisersBroker = brokerIntegrationService.getAdviserBrokerUser(foundBroker.getKey(),new ServiceErrorsImpl());
			assertThat(advisersBroker,is(notNullValue()));

			System.out.println(advisersBroker.getClientKey().getId());
		}else
			throw new RuntimeException("Everything broke");

	*/
    }

    //TODO : Need to discuss as for working  of this we need to change jobRole to PARAPLANNER
    @Test
    @SecureTestContext(username = "PARAPLANNER", customerId = "201615502", profileId = "3373", jobRole = "PARAPLANNER", jobId = "70313")
    public void testIsParaPlanner()
    {
        boolean paraplanner = userProfileService.isParaPlanner();
        assertTrue(paraplanner);
    }

    @Test
    @SecureTestContext(username = "PARAPLANNER", customerId = "201615502", profileId = "3373", jobRole = "PARAPLANNER", jobId = "70313")
    public void testAllOtherRolesExceptParaPlannerFalse()
    {
        boolean dealerGroup = userProfileService.isDealerGroup();
        assertFalse(dealerGroup);

        boolean adminAssistant = userProfileService.isAdminAssistant();
        assertFalse(adminAssistant);

        boolean investor = userProfileService.isInvestor();
        assertFalse(investor);

        boolean adviser = userProfileService.isAdviser();
        assertFalse(adviser);
    }

    @Test
    @SecureTestContext(username = "Assistant", customerId = "201631171", profileId = "8243", jobRole = "ASSISTANT", jobId = "73625")
    public void testIsAdminAssistant()
    {
        boolean adminAssistant = userProfileService.isAdminAssistant();
        assertTrue(adminAssistant);
    }

    @Test
    @SecureTestContext(username = "Assistant", customerId = "201631171", profileId = "8243", jobRole = "ASSISTANT", jobId = "73625")
    public void testAllOtherRolesExceptAssistantFalse()
    {
        boolean paraplanner = userProfileService.isParaPlanner();
        assertFalse(paraplanner);

        boolean dealerGroup = userProfileService.isDealerGroup();
        assertFalse(dealerGroup);

        boolean investor = userProfileService.isInvestor();
        assertFalse(investor);

        boolean adviser = userProfileService.isAdviser();
        assertFalse(adviser);
    }

    @Test
    @SecureTestContext(username = "investor", customerId = "20165571", profileId = "8243", jobRole = "INVESTOR", jobId = "73625")
    public void testIsInvestor()
    {
        boolean investor = userProfileService.isInvestor();
        assertTrue(investor);
    }

    @Test
    @SecureTestContext(username = "investor", customerId = "20165571", profileId = "8243", jobRole = "INVESTOR", jobId = "73625")
    public void testAllOtherRolesExceptInvestorFalse()
    {
        boolean paraplanner = userProfileService.isParaPlanner();
        assertFalse(paraplanner);

        boolean adminAssistant = userProfileService.isAdminAssistant();
        assertFalse(adminAssistant);

        boolean dealerGroup = userProfileService.isDealerGroup();
        assertFalse(dealerGroup);

        boolean adviser = userProfileService.isAdviser();
        assertFalse(adviser);
    }

    @Test
    @SecureTestContext(username = "adviser", profileId = "677", jobRole = "ADVISER", customerId = "201601408", jobId = "71793", userExperience = ASIM)
    public void testIsAdviser()
    {
        assertTrue(userProfileService.isAdviser());
        final UserProfile active = userProfileService.getActiveProfile();
        assertThat(active.getProfileId(), is("677"));
        assertThat(active.getUsername(), is("adviser"));
        assertThat(active.getUserExperience(), is(ASIM));
    }

    @Test
    @SecureTestContext(username = "adviser", profileId = "677", jobRole = "ADVISER", customerId = "201601408", jobId = "71793")
    public void testAllOtherRolesExceptAdviseralse()
    {
        boolean paraplanner = userProfileService.isParaPlanner();
        assertFalse(paraplanner);

        boolean adminAssistant = userProfileService.isAdminAssistant();
        assertFalse(adminAssistant);

        boolean investor = userProfileService.isInvestor();
        assertFalse(investor);

        boolean dealerGroup = userProfileService.isDealerGroup();
        assertFalse(dealerGroup);
    }

    @Test
    @SecureTestContext(username = "individualclient", customerId = "201606040", profileId = "2352", jobRole = "ADVISER")
    public void testGetSafiDeviceIdentifier()
    {
        SafiDeviceIdentifier safiDeviceIdentifier = userProfileService.getSafiDeviceIdentifier();
        assertThat(safiDeviceIdentifier, is(notNullValue()));
        assertThat(safiDeviceIdentifier.getSafiDeviceId(), is(notNullValue()));

    }

    @Test
    @SecureTestContext(username = "DealeGrpManager", customerId = "300000000", profileId = "10000", jobRole = "ADVISER", jobId = "93000")
    public void testGetDealerGroupUserForDealerGroupManager() {
        Broker dealerGroupTest = userProfileService.getDealerGroupBroker();
        assertThat(dealerGroupTest, is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is("collect--1_136 (Dealer Group)"));

    }

    @Test
    @SecureTestContext(username = "DealeGrpManager", customerId = "DGMGR_BAS2", profileId = "8147", jobRole = "DEALER_GROUP_MANAGER", jobId = "73220")
    public void testIsDealerGroup() {
        boolean dealerGroup = userProfileService.isDealerGroup();
        assertTrue(dealerGroup);

    }

    @Test
    @SecureTestContext(username = "DealeGrpManager", customerId = "DGMGR_BAS2", profileId = "8147", jobRole = "DEALER_GROUP_MANAGER", jobId = "73220")
    public void testAllOtherRolesExceptDGFalse() {
        boolean paraplanner = userProfileService.isParaPlanner();
        assertFalse(paraplanner);

        boolean adminAssistant = userProfileService.isAdminAssistant();
        assertFalse(adminAssistant);

        boolean investor = userProfileService.isInvestor();
        assertFalse(investor);

        boolean adviser = userProfileService.isAdviser();
        assertFalse(adviser);
    }

    /*
     * @Test
     *
     * @SecureTestContext(username = "investor", customerId = "20165571",
     * profileId = "8243", jobRole = "INVESTOR", jobId = "73625") public void
     * testClientList() {
     *
     * Broker dealerGroupTest = userProfileService.getDealerGroupBroker();
     * assertThat(dealerGroupTest.getPositionName(),
     * is("collect--1_882 (Dealer Group)"));
     *
     * }
     */

    /*
     * @Test
     *
     * @SecureTestContext(username = "investor", customerId = "20165571",
     * profileId = "8243", jobRole = "INVESTOR", jobId = "73625") public void
     * testAdviserForDirectInvestor() {
     *
     * Broker dealerGroupTest = ((InvestorProfileService)
     * userProfileService).getAdviserForLoggedInInvestor();
     * assertThat(dealerGroupTest.getPositionName(),
     * is("collect--1_473 (Adviser Position)"));
     *
     * }
     */

    @Test
    @SecureTestContext(username = "PARAPLANNER", customerId = "300000001", profileId = "10001", jobRole = "ADVISER", jobId = "93001")
    public void testGetDealerGroupParaplanner() {
        Broker dealerGroupTest = userProfileService.getDealerGroupBroker();
        assertThat(dealerGroupTest, is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is("collect--1_136 (Dealer Group)"));

    }

    @Test
    @SecureTestContext(username = "Assistant", customerId = "300000002", profileId = "10002", jobRole = "ADVISER", jobId = "93002")
    public void testGetDealerGroupUserForAssistant() {
        Broker dealerGroupTest = userProfileService.getDealerGroupBroker();
        assertThat(dealerGroupTest, is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is("collect--1_136 (Dealer Group)"));

    }

    @Test
    @SecureTestContext(username = "investorTest", customerId = "201602428")
    public void testActiveProfile_forInvestor() {
        UserProfile activeProfile = userProfileService.getActiveProfile();

        JobRole activeRole = activeProfile.getJobRole();

        assertThat(activeRole, is(notNullValue()));

        List<ProfileRoles> roles = new ArrayList<>();

        for (JobProfile profile : userProfileService.getAvailableProfiles()) {
            boolean active = false;
            if (activeRole.equals(profile.getJobRole())) {
                active = true;
            }
            roles.add(new ProfileRoles(profile.getJobRole().name(), EncodedString.fromPlainText(profile.getJob().getId())
                    .toString(), active));
        }

        /*
         * assertThat(info.getProfileId(), is(notNullValue()));
         * assertThat(info.getProfileId(), is("1179"));
         * assertThat(info.getClientKey(), is(notNullValue()));
         * assertThat(info.getClientKey().getId(), is("111111"));
         * assertThat(info.getBankReferenceKey(), is(notNullValue()));
         * assertThat(info.getBankReferenceKey().getId(), is("201602121"));
         * assertThat(info.getBankReferenceId(), is("201602121"));
         */
    }

    @Test
    @SecureTestContext(username = "adviser", profileId = "677", jobRole = "ADVISER", customerId = "201601408", jobId = "83489")
    public void testGetDealerGroup() {
        Broker dealerGroupTest = userProfileService.getDealerGroupBroker();
        assertThat(dealerGroupTest, is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is(notNullValue()));
        assertThat(dealerGroupTest.getPositionName(), is("collect--1_136 (Dealer Group)"));

    }
}
