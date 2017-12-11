package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.Person;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.btfin.panorama.service.integration.broker.BrokerType.ADVISER;
import static com.btfin.panorama.service.integration.broker.BrokerType.DEALER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


public class AvaloqBrokerIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	private static final Logger log = LoggerFactory.getLogger(AvaloqBrokerIntegrationServiceImplIntegrationTest.class);

	@Autowired
	private BrokerIntegrationService brokerService;



	private class Timer {
		private String infoStr;
		private long startTime;

		public Timer(String infoStr) {
			this.infoStr = infoStr;
		}

		public void start() {
			startTime = System.nanoTime();
		}

		public void end() {
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startTime;

			log.info("{}: Elapsed time = {} ms ({} us)", infoStr, elapsedTime / 1000000, elapsedTime / 1000);
		}
	}


    ///////////////////////////////////  getBroker()
    @Test
    public void getBroker_thenReturnNone() throws Exception
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        BrokerKey brokerKey;
        Broker broker;

        // adviser
        brokerKey = BrokerKey.valueOf("-1");
        broker = brokerService.getBroker(brokerKey, serviceErrors);
        assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
        assertThat("broker exists", broker, nullValue());
    }


	@Test
	public void getBroker_thenReturnOneBroker() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		String adviserKeyIdStr = "80091";
		String dealerKeyIdStr = "78450";
		BrokerKey brokerKey;
		Broker broker;

		// adviser
		brokerKey = BrokerKey.valueOf(adviserKeyIdStr);
		broker = brokerService.getBroker(brokerKey, serviceErrors);
		assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
		assertThat("broker exists", broker, notNullValue());
		assertThat("brokerType", broker.getBrokerType(), equalTo(ADVISER));
		assertThat("dealerKey", broker.getDealerKey().getId(), equalTo(dealerKeyIdStr));
        assertThat("101117206",equalTo(broker.getBankReferenceId()));
        assertThat("2014-06-04",equalTo(broker.getBrokerStartDate().toLocalDate().toString()));
        assertThat("AVSR_POS.2404",equalTo(broker.getExternalBrokerKey().getId()));
        assertThat("GRANTTHORN",equalTo(broker.getParentEBIKey().getId()));

        assertThat(false,equalTo(broker.isLicenseeFeeActive()));


        assertThat(false,equalTo(broker.isPayableParty()));
		// dealer
		brokerKey = BrokerKey.valueOf(dealerKeyIdStr);
		broker = brokerService.getBroker(brokerKey, serviceErrors);
		assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
		assertThat("broker exists", broker, notNullValue());
		assertThat("brokerType", broker.getBrokerType(), equalTo(DEALER));
		assertThat("dealerKey", broker.getDealerKey().getId(), equalTo(dealerKeyIdStr));
	}


	///////////////////////////////////  getBrokersForUser()
	@Test
	public void getBrokersForUser_cacheFetchTime() throws Exception
	{
		final String testName = "getBrokersForUser_cacheFetchTime";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		UserKey userKey = UserKey.valueOf("201601486");
		List<String> brokerIds;
		Timer timer;

		// user with multiple broker roles
		timer = new Timer("getBrokersForUser_cacheFetchTime() - multiple results");
		timer.start();
		Collection <Broker> brokers = brokerService.getBrokersForUser(userKey, serviceErrors);
		timer.end();
		logBrokers(testName + ": Brokers for userKey " + userKey.getId(), brokers);
		//brokerIds = Arrays.asList("68489", "68786");
        brokerIds = Arrays.asList("79964");
		assertThat("Number of broker roles for user " + userKey.getId(), brokers.size(), equalTo(brokerIds.size()));

		for (Broker broker : brokers) {
			assertThat("Broker key", broker.getKey().getId(), isIn(brokerIds));
		}

		// non-existent user
		userKey = UserKey.valueOf("non-existent-user");
		timer = new Timer("getBrokersForUser_cacheFetchTime() - no result");
		timer.start();
		brokers = brokerService.getBrokersForUser(userKey, serviceErrors);
		timer.end();
		logBrokers(testName + ": Brokers for userKey " + userKey.getId(), brokers);
		assertThat("Number of broker roles for user " + userKey.getId(), brokers.size(), equalTo(0));
	}

    @Test
	public void testLoadjobProfile_forSpecificRole() throws Exception
	{
		final String testName = "testLoadjobProfile_forSpecificRole()";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		/*UserKey userKey = UserKey.valueOf("201601388");*/
		List<JobProfileIdentifier> jobProfiles;
		JobProfileIdentifier jobProfile;
		JobRole userRole = JobRole.PARAPLANNER;
        BrokerIdentifierImpl brokerIdentifier = new BrokerIdentifierImpl();
		BrokerKey brokerKey;
		Collection <JobProfileIdentifier> userKeys;
		String[] expectedProfileIds;
		Timer timer;

		// first search
		brokerKey = BrokerKey.valueOf("79405");
        brokerIdentifier.setKey(brokerKey);
		timer = new Timer(testName + "(brokerKey=" + brokerKey.getId() + ", jobRole=" + userRole.name() + ") - one result");
		timer.start();
		userKeys = brokerService.getUserKeysForRole(brokerIdentifier, userRole, serviceErrors);
		timer.end();
		assertThat("result size", userKeys.size(), equalTo(1));
		jobProfiles = new ArrayList<>(userKeys);
		jobProfile = jobProfiles.get(0);
		assertThat("profileId", jobProfile.getProfileId(), equalTo("3561"));

		// second search for same broker
		timer = new Timer(testName + "(brokerKey=" + brokerKey.getId() + ", jobRole=" + userRole.name() + ") - one result");
		timer.start();
		userKeys = brokerService.getUserKeysForRole(brokerIdentifier, userRole, serviceErrors);
		timer.end();
		assertThat("result size", userKeys.size(), equalTo(1));
		jobProfiles = new ArrayList<>(userKeys);
		jobProfile = jobProfiles.get(0);
		assertThat("profileId", jobProfile.getProfileId(), equalTo("3561"));

		brokerKey = BrokerKey.valueOf("89668");
        brokerIdentifier.setKey(brokerKey);
        expectedProfileIds = new String[] { "7224", "7225", "7226"};
		timer = new Timer(testName + "(brokerKey=" + brokerKey.getId() + ", jobRole=" + userRole.name() + ") - "
						+ expectedProfileIds.length + " results");
		timer.start();
		userKeys = brokerService.getUserKeysForRole(brokerIdentifier, userRole, serviceErrors);
		timer.end();
		assertThat("result size", userKeys.size(), equalTo(expectedProfileIds.length));
		for (JobProfileIdentifier profile : userKeys) {
			assertThat("profileId", profile.getProfileId(), isOneOf(expectedProfileIds));
		}

		brokerKey = BrokerKey.valueOf("80157");
        brokerIdentifier.setKey(brokerKey);
		timer = new Timer(testName + "(brokerKey=" + brokerKey.getId() + ", jobRole=" + userRole.name() + ") - one result");
		timer.start();
		userKeys = brokerService.getUserKeysForRole(brokerIdentifier, userRole, serviceErrors);
		timer.end();
		assertThat("result size", userKeys.size(), equalTo(1));
		jobProfiles = new ArrayList<>(userKeys);
		jobProfile = jobProfiles.get(0);
		assertThat("profileId", jobProfile.getProfileId(), equalTo("3692"));
	}

	//Roles for a specific Natural Person

	@Test
	public void testgetBrokerUser() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		ClientKey clientKey = ClientKey.valueOf("34862");

		BrokerUser brokersUser = brokerService.getBrokerUser(clientKey, serviceErrors);

		Assert.assertNotNull(brokersUser);
		Assert.assertNotNull(brokersUser.getRoles());
	}

	//Dealer Group OE_ID for specific OE_ID

	@Test
	public void getBroker() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		BrokerKey brokerKey = BrokerKey.valueOf("79872");

		Broker broker = brokerService.getBroker(brokerKey, serviceErrors);

		assertThat("broker exists", broker, notNullValue());
		assertThat("dealerKey exists", broker.getDealerKey(), notNullValue());
		assertThat("dealerKey", broker.getDealerKey().getId(), equalTo("78344"));

	}

	@Test
	public void getAdviserBrokerUser() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		BrokerKey brokerKey = BrokerKey.valueOf("79872");
		BrokerUser brokerUser = brokerService.getAdviserBrokerUser(brokerKey, serviceErrors);

		assertThat("ServiceErrors", serviceErrors.hasErrors(), equalTo(false));
		assertThat("Broker user exists", brokerUser, notNullValue());
		assertThat("Broker user - userKey ", brokerUser.getBankReferenceId(), equalTo("201624883"));
		assertThat("Broker user - jobKey ", brokerUser.getProfileId(), equalTo("4452"));
	}

	//Parent for specific Natural Person

	@Test
	public void testgetAdvisersForUser() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

        UserKey userKey = UserKey.valueOf("201604869");
        JobKey jobKey = JobKey.valueOf("81064");
		BrokerUser jobProfileUser = new BrokerUserImpl(userKey, jobKey);
		List <BrokerIdentifier> brokers =(List<BrokerIdentifier>) brokerService.getAdvisersForUser(jobProfileUser, serviceErrors);

		assertThat("ServiceErrors", serviceErrors.hasErrors(), equalTo(false));
		assertThat("Brokers", brokers, notNullValue());
		assertThat("Number of broker roles for user " + userKey.getId() + ", job " + jobKey.getId(), brokers.size(), equalTo(1));
        assertThat(brokers.get(0).getKey().getId(), equalTo("80029"));


         userKey = UserKey.valueOf("201640731");
         jobKey = JobKey.valueOf("821699");
         jobProfileUser = new BrokerUserImpl(userKey, jobKey);
         List<BrokerIdentifier> brokerList = (List<BrokerIdentifier>)brokerService.getAdvisersForUser(jobProfileUser, serviceErrors);

        assertThat("ServiceErrors", serviceErrors.hasErrors(), equalTo(false));
        assertThat("Brokers", brokerList, notNullValue());
        assertThat("Number of broker roles for user " + userKey.getId() + ", job " + jobKey.getId(), brokerList.size(), equalTo(1));
        assertThat(brokerList.get(0).getKey().getId(), equalTo("80350"));

        userKey = UserKey.valueOf("201640732");
        jobKey = JobKey.valueOf("23423");
        jobProfileUser = new BrokerUserImpl(userKey, jobKey);
        List<BrokerIdentifier> emptyList = (List<BrokerIdentifier>)brokerService.getAdvisersForUser(jobProfileUser, serviceErrors);

        assertThat("ServiceErrors", serviceErrors.hasErrors(), equalTo(false));
        assertThat("Brokers", emptyList,is(empty()));
        assertThat("Number of broker roles for user " + userKey.getId() + ", job " + jobKey.getId(), emptyList.size(), equalTo(0));


    }

    @Test
    public void testgetBrokerUser_jobProfileIdentifier() throws Exception
    {
		UserKey userKey = UserKey.valueOf("201633716");
		JobKey jobKey = JobKey.valueOf("71810");
		BrokerUser jobProfileUser = new BrokerUserImpl(userKey, jobKey);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        BrokerUser brokersUser = brokerService.getBrokerUser(jobProfileUser, serviceErrors);

		assertThat("ServiceErrors", serviceErrors.hasErrors(), equalTo(false));
		assertThat("BrokerUser", brokersUser, notNullValue());
		assertThat("CustomerId", brokersUser.getBankReferenceId(), equalTo(userKey.getId()));
		assertThat("clientId", brokersUser.getClientKey().getId(), equalTo("64222"));
		assertThat("Roles", brokersUser.getRoles(), notNullValue());
		assertThat("Roles count", brokersUser.getRoles().size(), equalTo(1));
    }

    @Test
    public void testgetBrokersForJob() throws Exception
    {
        UserKey userKey = UserKey.valueOf("201604869");
		JobKey jobKey = JobKey.valueOf("81064");
        BrokerUser jobProfileUser = new BrokerUserImpl(userKey,jobKey);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<Broker> brokers = brokerService.getBrokersForJob(jobProfileUser, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(brokers);
        Assert.assertEquals(1,brokers.size());

    }
    @Test
    @SecureTestContext(username="adviser", jobRole = "PARAPLANNER" , customerId = "201641081", jobId="",  profileId = "")
    public void testAdvisersForUser()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserKey userKey = UserKey.valueOf("201604869");
        JobKey jobKey = JobKey.valueOf("78934");
        BrokerUser jobProfileUser = new BrokerUserImpl(userKey, jobKey);
        Collection <BrokerIdentifier> brokers = brokerService.getAdvisersForUser(jobProfileUser, serviceErrors);
        Assert.assertNotNull(brokers);
    }


	@Test
	public void getBrokersForUser_thenReturnNone() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		UserKey userKey = UserKey.valueOf("-1");
		Collection <Broker> brokers = brokerService.getBrokersForUser(userKey, serviceErrors);

		assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
		assertThat("brokers exist", brokers, notNullValue());
		assertThat("number of brokers", brokers.size(), equalTo(0));
	}


	@Test
	public void getBrokersForUser_thenReturnOneBroker() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		UserKey userKey = UserKey.valueOf("201608788");
		Collection <Broker> brokers = brokerService.getBrokersForUser(userKey, serviceErrors);
		Broker broker;

		assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
		assertThat("brokers exist", brokers, notNullValue());
		assertThat("number of brokers", brokers.size(), equalTo(1));

		broker = brokers.toArray(new Broker[brokers.size()]).clone()[0];
		assertThat("brokerType", broker.getBrokerType(), equalTo(ADVISER));
		assertThat("brokerKey", broker.getKey().getId(), equalTo("79597"));
		assertThat("dealerKey", broker.getDealerKey().getId(), equalTo("78990"));
	}


	///////////////////////////////////  getBrokerUser(UserKey)
	@Test
	public void getBrokerUserByUserKey_thenReturnNone() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		UserKey userKey = UserKey.valueOf("-1");
		BrokerUser brokerUser = brokerService.getBrokerUser(userKey, serviceErrors);

		assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
		assertThat("broker user exist", brokerUser, nullValue());
	}


	@Test
	public void getBrokerUserByUserKey_thenReturnResult() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		UserKey userKey = UserKey.valueOf("201608788");
		BrokerUser brokerUser = brokerService.getBrokerUser(userKey, serviceErrors);
		Collection<BrokerRole> roles;
		BrokerRole role;

		assertThat("broker user exist", brokerUser, notNullValue());
		assertThat("clientKey", brokerUser.getClientKey().getId(), equalTo("39069"));
		roles = brokerUser.getRoles();
		assertThat("role(s) exist", roles, notNullValue());
		assertThat("role numbers", roles.size(), equalTo(1));
		role = roles.toArray(new BrokerRole[roles.size()])[0];
		assertThat("role value", role.getRole(), equalTo(JobRole.ADVISER));
	}


	///////////////////////////////////  getBrokerUser(ClientKey)
	@Test
	public void getBrokerUserByClientKey_thenReturnNone() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ClientKey clientKey = ClientKey.valueOf("-1");
		BrokerUser brokerUser = brokerService.getBrokerUser(clientKey, serviceErrors);

		assertThat("no service error", serviceErrors.hasErrors(), equalTo(false));
		assertThat("broker user exist", brokerUser, nullValue());
	}


	@Test
	public void getBrokerUserByClientKey_thenReturnResult() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ClientKey clientKey = ClientKey.valueOf("34862");
		BrokerUser brokerUser = brokerService.getBrokerUser(clientKey, serviceErrors);
		Collection<BrokerRole> roles;
		BrokerRole role;

		assertThat("clientKey", brokerUser.getBankReferenceId(), equalTo("201604869"));
		roles = brokerUser.getRoles();
		assertThat("role(s) exist", roles, notNullValue());
		assertThat("role numbers", roles.size(), equalTo(1));
		role = roles.toArray(new BrokerRole[roles.size()])[0];
		assertThat("role value", role.getRole(), equalTo(JobRole.ADVISER));
	}


	@Test
    public void testgetPersonDetailsOfBrokerUser() throws Exception
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ClientKey clientKey = ClientKey.valueOf("34862");
        Person brokersUser = brokerService.getPersonDetailsOfBrokerUser(clientKey, serviceErrors);

        assertThat(brokersUser, notNullValue());
        assertThat(brokersUser.getFirstName(), is("person-120_2317"));
        assertThat(brokersUser.getLastName(), is("person-120_2317"));
        assertThat(brokersUser.getBankReferenceId(), is("201604869"));
        assertThat(brokersUser.getClientType(), is(ClientType.OE));
        assertThat(brokersUser.getClientType(), is(ClientType.OE));
        assertThat(brokersUser.getLegalForm(), nullValue());
    }

	private void logBrokers(String infoStr, Collection<Broker> brokers) {
		if (log.isDebugEnabled()) {
			int i;

			log.debug("");
			log.debug(infoStr);

			i = 0;
			for (Broker broker: brokers) {
				if (log.isDebugEnabled()) {
					log.debug(i + ". brokerKey = " + broker.getKey().getId()
							+ ", brokerType = " + broker.getBrokerType()
							+ ", dealerKey = " + (broker.getDealerKey() == null ? null : broker.getDealerKey().getId())
							/*+ ", firstName = " + broker.getFirstName()
							+ ", lastName = " + broker.getLastName()*/);
				}

				i++;
			}
		}
	}
}
