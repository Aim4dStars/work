package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnnotatedBrokerIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	AnnotatedBrokerIntegrationServiceImpl brokerIntegrationService;

    @Autowired
    private AvaloqExecute avaloqExecute;

	@Test
    @SecureTestContext
	public void testLoadBrokers() throws Exception
	{
		BrokerHolderAnnotationImpl holder = brokerIntegrationService.loadBrokers();
		assertThat(holder, is(notNullValue()));
		assertThat(holder.getBrokers(), is(notNullValue()));
		assertThat(holder.getBrokers().size(), is(not(0)));
		assertThat(holder.getBrokers().get(0), is(notNullValue()));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(0))).getBrokerId(), is(notNullValue()));
		assertThat(holder.getBrokers().get(0).getKey(), is(notNullValue()));
		assertThat(holder.getBrokers().get(0).getKey().getId(), is("80400"));
		assertThat(holder.getBrokers().get(0).getFua(), is(new BigDecimal("0")));
		assertThat(holder.getBrokers().get(0).getNumberOfAccounts().intValue(), is(0));
		assertThat(holder.getBrokers().get(0).getBrokerType(), is(BrokerType.OTHER));
		assertThat(holder.getBrokers().get(0).getPositionName(), is("BT Operations"));

		//Check JobList
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList(), is(notNullValue()));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().size(), is(not(0)));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getJob(), is(notNullValue()));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getJob().getId(), is("83569"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getProfileId(), is("791"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getProfileId(), is("791"));
		assertTrue(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).isRegisteredOnline());

		assertThat(holder.getBrokers().get(26).getBrokerType(), is(BrokerType.ADVISER));
		assertThat(holder.getBrokers().get(26).getPositionName(), is("collect--1_981 (Adviser Position)"));
		assertThat(holder.getBrokers().get(26).getParentKey().getId(), is("78747"));
		assertThat(holder.getBrokers().get(26).getKey().getId(), is("80069"));
		assertThat(holder.getBrokers().get(26).getBankReferenceKey().getId(), is("101135145"));
		assertThat(holder.getBrokers().get(26).getBrokerStartDate().toLocalDate().toString(), is("2013-12-19"));
		assertThat(holder.getBrokers().get(26).isLicenseeFeeActive(), is(false));

		assertThat(holder.getBrokers().get(26).getKey().getId(), is("80069"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getFirstName(),
			is("person-120_1230"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getMiddleName(),
			is("person-120_1230"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getLastName(),
			is("person-120_1230"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getBankReferenceKey().getId(),
			is("201601720"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList()
			.get(0)
			.getReferenceStartDate()
			.toLocalDate()
			.toString(), is("2013-12-19"));

		//Check the address fields
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses(), is(notNullValue()));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.size(),
			is(not(0)));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getAddressKey(),
			is(notNullValue()));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getPoBox(),
			is("addr--1_2962"));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getStreetNumber(),
			is("addr--1_2962"));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getStreetName(),
			is("addr--1_2962"));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getBuilding(),
			is(nullValue()));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getSuburb(),
			is("addr--1_2962"));
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getCity(),
			is("addr--1_2962"));
		assertFalse((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.isDomicile());
		assertThat((((JobProfileAnnotatedHolder)((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0))).getFullAddress()
			.get(0)
			.getPostCode(),
			is("addr--1_2962"));

		/*//assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(1).getAddresses().get(0).getCareOf(),is(null));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getStreetType(), is("Access"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getStateAbbr(), is("ACT"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getState(),is("Australia Capital Territory"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getStateCode(), is("5007"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getCountryCode(), is("2061"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getCountry(),is("Australia"));
		assertTrue(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).isMailingAddress());
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getCategoryId(), is(1));
		assertTrue(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(1).isPreferred());
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(1).getElectronicAddress(), is("09169843995"));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getPostAddress(), is(AddressType.POSTAL));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(1).getPostAddress(), is(AddressType.ELECTRONIC));

		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getAddressType(),
		        is(AddressMedium.POSTAL));
		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).getPostAddress(), is(AddressType.POSTAL));
		assertTrue(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).isMailingAddress());
		assertFalse(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(0).isPreferred());

		assertThat(((BrokerAnnotationHolder)(holder.getBrokers().get(26))).getJobList().get(0).getAddresses().get(1).getAddressType(),
		        is(AddressMedium.MOBILE_PHONE_PRIMARY));*/
	}

	@Test
    @SecureTestContext
	public void testgetBrokerMap() throws Exception
	{
		BrokerHolderAnnotationImpl holder = brokerIntegrationService.loadBrokers();
		Map <BrokerKey, Broker> map = holder.getBrokerMap();
		assertThat(holder.getBrokers().size(), is(1640));
		assertThat(map.size(), is(1640));
		BrokerKey key = BrokerKey.valueOf("79872");
		Broker broker = map.get(key);
		assertThat(broker.getBrokerType(), is(BrokerType.ADVISER));
		assertThat(broker.getPositionName(), is("collect--1_1378 (Adviser Position)"));
		assertThat(broker.getParentKey().getId(), is("78344"));
		assertThat(broker.getDealerKey().getId(), is("78344"));
		assertThat(broker.getBankReferenceKey().getId(), is("101141699"));
		assertThat(broker.getBrokerType(), is(BrokerType.ADVISER));
		assertThat(broker.getBrokerStartDate().toLocalDate().toString(), is("2014-08-28"));
		assertThat(broker.getFua().intValue(), is(0));
		assertFalse(broker.isLicenseeFeeActive());
		assertFalse(broker.isPayableParty());
		assertThat(broker.getBankReferenceId(), is("101141699"));
		assertThat(broker.getExternalBrokerKey().getId(), is("AVSR_POS.3088"));
		assertThat(broker.getParentEBIKey().getId(), is("PROFESS"));
		Assert.assertNull(broker.getPracticeKey());
        assertTrue(broker.canViewMarketData());

		BrokerKey brokerKey = BrokerKey.valueOf("78344");
		broker = map.get(brokerKey);
		Assert.assertNotNull(broker.getDealerKey());
		Assert.assertThat(broker.getDealerKey().getId().toString(), Is.is("78344"));

	}

	@Test
    @SecureTestContext
	public void generateJobBrokerHolder() throws Exception
	{
		BrokerHolderAnnotationImpl holder = brokerIntegrationService.loadBrokers();
		Map <UserKey, Collection <JobKey>> jobKeyMap = holder.getUserJobMap();
		Map <JobKey, BrokerUser> mapJob = holder.getJobMap();
		JobBrokerHolder jobBrokerHolder = holder.getUserMap();
		assertThat(jobKeyMap.size(), is(1603));
		assertThat(mapJob.size(), is(2105));
		UserKey userKey = UserKey.valueOf("201604869");
		JobKey jobKey = JobKey.valueOf("81064");

		BrokerUser brokerUser = mapJob.get(jobKey);
		Assert.assertNotNull(brokerUser);
		Assert.assertNotNull(brokerUser.getRoles());
		assertThat(brokerUser.getFirstName(), is("person-120_3691"));
		assertThat(brokerUser.getMiddleName(), is("person-120_3691"));
		assertFalse(brokerUser.isRegisteredOnline());

		userKey = UserKey.valueOf("201603847");
		Collection <JobKey> jobKeys = jobKeyMap.get(userKey);
		Assert.assertNotNull(jobKeys);
		Iterator iterator = jobKeys.iterator();
		brokerUser = mapJob.get(iterator.next());
		Assert.assertThat(brokerUser.getReferenceStartDate().toLocalDate().toString(), Is.is("2014-04-28"));
		Assert.assertThat(brokerUser.getLastName(), Is.is("person-120_3691"));
		Assert.assertThat(brokerUser.getPracticeName(), Is.is("collect--1_999 (Adviser Position)"));
		Assert.assertThat(brokerUser.getFullName(), Is.is("person-120_3691 person-120_3691 person-120_3691"));
		Assert.assertThat(brokerUser.getBankReferenceId(), Is.is("201603847"));
		Assert.assertThat(brokerUser.getCISKey().getId(), Is.is("10926120072"));
		Assert.assertThat(brokerUser.getClientType(), Is.is(ClientType.OE));
		Set <BrokerRole> roleSet = (HashSet)brokerUser.getRoles();
		Assert.assertThat(roleSet.size(), Is.is(1));
		for (BrokerRole brokerRole : roleSet)
		{
			Assert.assertThat(brokerRole.getAuthorizationRole(), Is.is(JobAuthorizationRole.Supervisor_Transact));
			Assert.assertThat(brokerRole.getRole(), Is.is(JobRole.ADVISER));

		}
		Assert.assertThat(brokerUser.getEntityId(), Is.is("660230"));
	}

    @Test
	
    @SecureTestContext(username = "explode", customerId = "201101101")
    public void getErrorForBrokerHiearchy() throws Exception {

        ServiceErrors serviceErrors =  new FailFastErrorsImpl();
        try {
            BrokerHolderAnnotationImpl holder = avaloqExecute.executeReportRequestToDomain(
                    new AvaloqReportRequest(Template.BROKER_HIERARCHY.getName()).asApplicationUser(),
                    PartialInvalidationBrokerHolderImpl.class, serviceErrors);
        }
        catch(Exception e)
        {

        }
        assertThat(serviceErrors.hasErrors(), Is.is(true));

    }


}
