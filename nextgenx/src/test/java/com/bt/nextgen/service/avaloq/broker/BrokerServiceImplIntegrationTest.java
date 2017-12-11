package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.BrokerService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BrokerServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	private static final Logger log = LoggerFactory.getLogger(BrokerServiceImplIntegrationTest.class);

	/*@Autowired
	@Qualifier("userBrokerHolderService")
	private BrokerService userBrokerHolderService;*/

	@Autowired
	@Qualifier("brokerCacheService")
	private BrokerService brokerCacheService;


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



    @Test
	public void getBrokerUsersWithNameAndRole() throws Exception
	{
		final String testName = "getBrokerUsersWithNameAndRole";
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Timer timer;
		JobRole jobRole;
		String firstNamePrefix;
		String lastNamePrefix;
		List<BrokerUser> brokerUsers;

		jobRole = JobRole.PARAPLANNER;
		firstNamePrefix = "P";
		lastNamePrefix = "P";

        /************* userBrokerHolderService ************/
        timer = new Timer("userBrokerHolderService - " + testName + " (jobRole=" + jobRole.name() + ", firstNamePrefix=" + firstNamePrefix
        				+ ", lastNamePrefix=" + lastNamePrefix + ")");
        timer.start();
	/*	brokerUsers = userBrokerHolderService.getBrokerUsersWithNameAndRole(jobRole, firstNamePrefix, lastNamePrefix,
							serviceErrors);
		timer.end();
        assertThat("BrokerUsers count", brokerUsers.size(), equalTo(95));

        timer.start();
		brokerUsers = userBrokerHolderService.getBrokerUsersWithNameAndRole(jobRole, firstNamePrefix, lastNamePrefix,
							serviceErrors);
		timer.end();
        assertThat("BrokerUsers count", brokerUsers.size(), equalTo(95));

        timer.start();
		brokerUsers = userBrokerHolderService.getBrokerUsersWithNameAndRole(jobRole, firstNamePrefix, lastNamePrefix,
							serviceErrors);
		timer.end();
        assertThat("BrokerUsers count", brokerUsers.size(), equalTo(95));
*/
        /************* brokerCacheService ************/
        timer = new Timer("brokerCacheService - " + testName + " (jobRole=" + jobRole.name() + ", firstNamePrefix=" + firstNamePrefix
        				+ ", lastNamePrefix=" + lastNamePrefix + ")");
        timer.start();
		brokerUsers = brokerCacheService.getBrokerUsersWithNameAndRole(jobRole, firstNamePrefix, lastNamePrefix,
							serviceErrors);
		timer.end();
        assertThat("BrokerUsers count", brokerUsers.size(), equalTo(95));

        timer.start();
		brokerUsers = brokerCacheService.getBrokerUsersWithNameAndRole(jobRole, firstNamePrefix, lastNamePrefix,
							serviceErrors);
		timer.end();
        assertThat("BrokerUsers count", brokerUsers.size(), equalTo(95));

        timer.start();
		brokerUsers = brokerCacheService.getBrokerUsersWithNameAndRole(jobRole, firstNamePrefix, lastNamePrefix,
							serviceErrors);
		timer.end();
        assertThat("BrokerUsers count", brokerUsers.size(), equalTo(95));
		
		log.debug("brokerUsers = " + brokerUsers);
	}
}
