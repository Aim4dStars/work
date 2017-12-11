package com.bt.nextgen.api.adviser.service;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.model.AdviserSearchDtoKey;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdviserSearchDtoServiceTest
{

	@InjectMocks
	AdviserSearchDtoServiceImpl dtoService;

	@Mock
	private BrokerIntegrationService brokerService;

	@Mock
	private StaticIntegrationService staticService;

	@Mock
	private UserProfileService profileService;

	ServiceErrors serviceErrors;
	Collection <Code> entityResultList;
	Collection <Code> stateResultList;
	AdviserSearchDtoKey key;

	@Before
	public void setup()
	{
		entityResultList = new ArrayList <Code>();
		entityResultList.add(new CodeImpl("660272", "SUPER_DG", "Super Dealer Group"));
		entityResultList.add(new CodeImpl("660228", "PRACTICE", "Practice"));
		stateResultList = new ArrayList <Code>();
		stateResultList.add(new CodeImpl("5004", "NSW", "New South Wales"));
		stateResultList.add(new CodeImpl("5002", "QLD", "Queensland"));
	}

	@Test
	public void testSearchCriteriaConsistent()
	{
		Collection <BrokerIdentifier> brokerList = new ArrayList <>();
		brokerList.add(getBrokerId("101843"));
		UserProfile activeProfile = getActiveProfile();
		when(profileService.getActiveProfile()).thenReturn(activeProfile);
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerList);

		BrokerUser brokerUser = new BrokerUserImpl(UserKey.valueOf("79260"));
		((BrokerUserImpl)brokerUser).addBroker(JobRole.ADVISER, BrokerKey.valueOf("79260"));

		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), brokerUser, true, ""));

		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);

		List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
		apiSearchCriterias.add(new ApiSearchCriteria(Attribute.CONSISTENT_ID_FLAG, "true"));
		when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getBroker(JobRole.ASSISTANT))
				.thenReturn(getBroker(JobRole.ADVISER));
		List <AdviserSearchDto> result = dtoService.search(apiSearchCriterias, serviceErrors);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getAdviserPositionId(),"BB6DE40238D29338");
	}

	@Test
	public void testSearchCriteriaWithoutConsistent()
	{
		Collection <BrokerIdentifier> brokerList = new ArrayList <>();
		brokerList.add(getBrokerId("101843"));
		UserProfile activeProfile = getActiveProfile();
		when(profileService.getActiveProfile()).thenReturn(activeProfile);
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerList);
		BrokerUser brokerUser = new BrokerUserImpl(UserKey.valueOf("79260"));
		((BrokerUserImpl)brokerUser).addBroker(JobRole.ADVISER, BrokerKey.valueOf("79260"));
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), brokerUser, true, ""));

		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
		List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
		when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getBroker(JobRole.ASSISTANT))
				.thenReturn(getBroker(JobRole.ADVISER));
		List <AdviserSearchDto> result = dtoService.search(apiSearchCriterias, serviceErrors);
		assertEquals(result.size(), 1);
		assertEquals(EncodedString.toPlainText(result.get(0).getAdviserPositionId()),"79260");
	}

	@Test
	public void testSearchSuccess()
	{
		Collection <BrokerIdentifier> brokerList = new ArrayList <>();
		brokerList.add(getBrokerId("id1"));
		UserProfile activeProfile = getActiveProfile();
		when(profileService.getActiveProfile()).thenReturn(activeProfile);
		key = new AdviserSearchDtoKey("Ho Si");
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerList);
		BrokerUser brokerUser = getBrokerUser();
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), brokerUser,true, ""));
		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
		when(staticService.loadCodes(CodeCategory.ENTITY_TYPE, serviceErrors)).thenReturn(entityResultList);
		when(staticService.loadCodes(CodeCategory.STATES, serviceErrors)).thenReturn(stateResultList);
		when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getBroker(JobRole.ASSISTANT))
			.thenReturn(getBroker(JobRole.ADVISER));
		List <AdviserSearchDto> result = dtoService.search(key, serviceErrors);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getState(), "NSW");
		assertEquals(result.get(0).getPracticeName(), "My Practice");
		assertEquals(EncodedString.toPlainText(result.get(0).getAdviserPositionId()),
			EncodedString.toPlainText(EncodedString.fromPlainText(null).toString()));
	}

	@Test
	public void testSearchNoMatchingResults()
	{
		Collection <BrokerIdentifier> brokerList = new ArrayList <>();
		brokerList.add(getBrokerId("id1"));
		key = new AdviserSearchDtoKey("Oth");
		UserProfile activeProfile = getActiveProfile();
		when(profileService.getActiveProfile()).thenReturn(activeProfile);
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerList);
		BrokerUser brokerUser = getBrokerUser();
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), brokerUser,true, ""));
		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
		when(staticService.loadCodes(CodeCategory.ENTITY_TYPE, serviceErrors)).thenReturn(entityResultList);
		when(staticService.loadCodes(CodeCategory.STATES, serviceErrors)).thenReturn(stateResultList);
		when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getBroker(JobRole.ASSISTANT))
			.thenReturn(getBroker(JobRole.ADVISER));
		List <AdviserSearchDto> result = dtoService.search(key, serviceErrors);
		assertEquals(result.size(), 0);
	}

	@Test
	public void testSearchNoAdvisersReturned()
	{
		Collection <BrokerIdentifier> brokerList = new ArrayList <>();
		brokerList.add(getBrokerId("id1"));
		key = new AdviserSearchDtoKey("Oth");
		UserProfile activeProfile = getActiveProfile();
		when(profileService.getActiveProfile()).thenReturn(activeProfile);
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(null);
		BrokerUser brokerUser = getBrokerUser();
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), brokerUser, true, ""));
		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
		when(staticService.loadCodes(CodeCategory.ENTITY_TYPE, serviceErrors)).thenReturn(entityResultList);
		when(staticService.loadCodes(CodeCategory.STATES, serviceErrors)).thenReturn(stateResultList);
		when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getBroker(JobRole.ASSISTANT))
			.thenReturn(getBroker(JobRole.ADVISER));
		List <AdviserSearchDto> result = dtoService.search(key, serviceErrors);
		assertEquals(result.size(), 0);
	}

	private BrokerIdentifier getBrokerId(final String id)
	{
		BrokerIdentifier brokerIdentifier = new BrokerIdentifier()
		{
			@Override
			public BrokerKey getKey()
			{
				return BrokerKey.valueOf(id);
			}
		};
		return brokerIdentifier;
	}

	@Test
	public void testFindByIdReturnsBrokerUserWithAddress()
	{
		BrokerUser user = createBrokerUser();
		String encodedId = EncodedString.fromPlainText("12345").toString();
		when(brokerService.getAdviserBrokerUser(Mockito.eq(BrokerKey.valueOf("12345")), any(ServiceErrors.class))).thenReturn(user);
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(getBrokerId("12345")));
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), user, true, ""));
		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
		AdviserSearchDto result = dtoService.find(new AdviserSearchDtoKey(encodedId), new ServiceErrorsImpl());
		assertEquals("12345", EncodedString.toPlainText(result.getAdviserPositionId()));
		assertEquals("Adviser", result.getPracticeName());
		assertEquals("Frank", result.getFirstName());
		assertEquals("Smith", result.getLastName());
		assertEquals("NSW", result.getState());

	}

	@Test
	public void testFindByIdReturnsBrokerUserWithoutAddress()
	{
		BrokerUser user = createBrokerUser();
		when(user.getAddresses()).thenReturn(null);
		String encodedId = EncodedString.fromPlainText("12345").toString();
		when(brokerService.getAdviserBrokerUser(Mockito.eq(BrokerKey.valueOf("12345")), any(ServiceErrors.class))).thenReturn(user);
		when(brokerService.getAdvisersForUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(getBrokerId("12345")));
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Broker broker = mock(Broker.class);
		brokerWrapperMap.put(BrokerKey.valueOf("79260"),new BrokerWrapperImpl(BrokerKey.valueOf("79260"), user, true, ""));
		when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
		AdviserSearchDto result = dtoService.find(new AdviserSearchDtoKey(encodedId), new ServiceErrorsImpl());
		assertEquals("12345", EncodedString.toPlainText(result.getAdviserPositionId()));
		assertEquals("Adviser", result.getPracticeName());
		assertEquals("Frank", result.getFirstName());
		assertEquals("Smith", result.getLastName());
		assertNull(result.getState());

	}


	@Test(expected= NotAllowedException.class)
	public void testFind_ShouldThrowAnException_WhenAdviserIsNotPresentInThePermittedAdviserList() {
		BrokerUser user = createBrokerUser();
		when(user.getAddresses()).thenReturn(null);
		String encodedId = EncodedString.fromPlainText("12345").toString();
		when(brokerService.getAdviserBrokerUser(Mockito.eq(BrokerKey.valueOf("12345")), any(ServiceErrors.class))).thenReturn(user);
		dtoService.find(new AdviserSearchDtoKey(encodedId), serviceErrors);
	}

		private BrokerUser createBrokerUser()
	{
		BrokerUser user = mock(BrokerUser.class);
		AddressImpl address = new AddressImpl();
		address.setState("NSW");
		when(user.getRoles()).thenReturn(getBroker(JobRole.ADVISER).getRoles());
		when(user.getPracticeName()).thenReturn("Adviser");
		when(user.getLastName()).thenReturn("Smith");
		when(user.getFirstName()).thenReturn("Frank");
		when(user.getAddresses()).thenReturn(Arrays.asList((Address)address));
		return user;
	}

	/*private PersonResponse getPersons(String userRole, String state)
	{
		PersonResponse person = new PersonResponseImpl();
		person.setDomiState(state);
		person.setDomiSuburb("Sydney");
		person.setFirstName("Test");
		person.setLastName("User");
		person.setClientKey(ClientKey.valueOf("12345"));
		person.setUserRole(userRole);
		return person;
	}*/

	private BrokerUser getBroker(final JobRole role)
	{
		BrokerUser brokerUser = new BrokerUser()
		{

			@Override
			public String getLastName()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public UserKey getBankReferenceKey()
			{
				// TODO Auto-generated method stub
				return null;
			}

			public JobKey getJob()
			{
				return null;
			}

			@Override
			public String getProfileId()
			{
				return null;
			}

			@Override
			public String getFirstName()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getMiddleName()
			{
				return null;
			}

			@Override
			public ClientKey getClientKey()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setClientKey(ClientKey personId)
			{

			}

			@Override
			public Collection <BrokerRole> getRoles()
			{
				BrokerRole br = new BrokerRole()
				{
					@Override
					public JobRole getRole()
					{
						return role;
					}

					@Override
					public JobAuthorizationRole getAuthorizationRole()
					{
						return null;
					}

					@Override
					public BrokerKey getKey()
					{
						return BrokerKey.valueOf("12345");
					}
				};
				return Arrays.asList(br);
			}

            @Override
			public boolean isRegisteredOnline()
			{
				return false;
			}

			@Override
			public String getPracticeName()
			{
				// TODO Auto-generated method stub
				return "My Practice";
			}

			@Override
			public String getEntityId()
			{
				// TODO Auto-generated method stub
				return "660228";
			}

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
			public String getBankReferenceId()
			{
				return null;
			}

			@Override
			public String getFullName()
			{
				return null;
			}

			@Override
			public Collection <AccountKey> getWrapAccounts()
			{
				return null;
			}

			@Override
			public ClientType getClientType()
			{
				return null;
			}

			@Override
			public Collection <ClientDetail> getRelatedPersons()
			{
				return null;
			}

			@Override
			public List <Address> getAddresses()
			{
				return null;
			}

			@Override
			public List <Email> getEmails()
			{
				return null;
			}

			@Override
			public List <Phone> getPhones()
			{
				return null;
			}

			@Override
			public int getAge() {
				return 0;
			}

			@Override
			public Gender getGender() {
				return null;
			}

			@Override
			public DateTime getDateOfBirth() {
				return null;
			}

			@Override
			public boolean isRegistrationOnline() {
				return false;
			}

			@Override
			public String getTitle() {
				return null;
			}

			@Override
			public String getSafiDeviceId() {
				return null;
			}

			@Override
			public String getModificationSeq() {
				return null;
			}

			@Override
			public String getGcmId() {
				return null;
			}

			@Override
			public DateTime getOpenDate() {
				return null;
			}

			@Override
			public DateTime getCloseDate()
			{
				return null;
			}

			@Override
			public com.bt.nextgen.service.integration.domain.InvestorType getLegalForm()
			{
				return null;
			}

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                // TODO Auto-generated method stub
                return null;
            }
        };
		return brokerUser;
	}

	public BrokerUser getBrokerUser()
	{
		BrokerUser brokerUser = new BrokerUser()
		{
			@Override
			public Collection <BrokerRole> getRoles()
			{
				return null;
			}

            @Override
			public boolean isRegisteredOnline()
			{
				return false;
			}

			@Override
			public String getPracticeName()
			{
				return "My Practice";
			}

			@Override
			public String getEntityId()
			{
				return "660228";
			}

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
			public String getFirstName()
			{
				return "Homer";
			}

			@Override
			public String getMiddleName()
			{
				return null;
			}

			@Override
			public String getLastName()
			{
				return "Simpson";
			}

			@Override
			public String getBankReferenceId()
			{
				return null;
			}

			@Override
			public UserKey getBankReferenceKey()
			{
				return null;
			}

			@Override
			public Collection <AccountKey> getWrapAccounts()
			{
				return null;
			}

			@Override
			public Collection <ClientDetail> getRelatedPersons()
			{
				return null;
			}

			@Override
			public List <Email> getEmails()
			{
				return null;
			}

			@Override
			public List <Phone> getPhones()
			{
				return null;
			}

			@Override
			public int getAge() {
				return 0;
			}

			@Override
			public Gender getGender() {
				return null;
			}

			@Override
			public DateTime getDateOfBirth() {
				return null;
			}

			@Override
			public boolean isRegistrationOnline() {
				return false;
			}

			@Override
			public String getTitle() {
				return null;
			}

			@Override
			public String getSafiDeviceId() {
				return null;
			}

			@Override
			public String getModificationSeq() {
				return null;
			}

			@Override
			public String getGcmId() {
				return null;
			}

			@Override
			public DateTime getOpenDate() {
				return null;
			}

			@Override
			public DateTime getCloseDate()
			{
				return null;
			}

			@Override
			public String getFullName()
			{
				return null;
			}

			@Override
			public ClientType getClientType()
			{
				return null;
			}

			@Override
			public List <Address> getAddresses()
			{
				return Arrays.asList(getAddress());
			}

			@Override
			public InvestorType getLegalForm()
			{
				return null;
			}

			@Override
			public ClientKey getClientKey()
			{
				return null;
			}

			@Override
			public void setClientKey(ClientKey personId)
			{

			}

			@Override
			public JobKey getJob()
			{
				return null;
			}

			@Override
			public String getProfileId()
			{
				return null;
			}

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                // TODO Auto-generated method stub
                return null;
            }
        };
		return brokerUser;
	}

	public Address getAddress()
	{
		Address address = new Address()
		{
			@Override
			public String getAddressLine1() {
				return null;
			}

			@Override
			public String getAddressLine2() {
				return null;
			}

			@Override
			public String getAddressLine3() {
				return null;
			}

			@Override
			public boolean isInternationalAddress() {
				return false;
			}

			@Override
			public String getOccupierName() {
				return null;
			}

			@Override
			public AddressKey getAddressKey()
			{
				return null;
			}

			@Override
			public String getCareOf()
			{
				return null;
			}

			@Override
			public String getUnit()
			{
				return null;
			}

			@Override
			public String getFloor()
			{
				return null;
			}

			@Override
			public String getStreetNumber()
			{
				return null;
			}

			@Override
			public String getStreetName()
			{
				return null;
			}

			@Override
			public String getStreetType()
			{
				return null;
			}

            @Override
            public String getStreetTypeId() {
                return null;
            }

            @Override
			public String getStreetTypeUserId()
			{
				return null;
			}

			@Override
			public String getBuilding()
			{
				return null;
			}

			@Override
			public String getSuburb()
			{
				return "Sydney";
			}

			@Override
			public String getStateAbbr()
			{
				return null;
			}

			@Override
			public String getState()
			{
				return "NSW";
			}

			@Override
			public String getPoBox()
			{
				return null;
			}

			@Override
			public String getCity()
			{
				return "Sydney";
			}

			@Override
			public boolean isDomicile()
			{
				return true;
			}

			@Override
			public String getStateCode()
			{
				return null;
			}

			@Override
			public String getPostCode()
			{
				return null;
			}

			@Override
			public String getCountryAbbr()
			{
				return null;
			}

			@Override
			public String getCountryCode()
			{
				return null;
			}

			@Override
			public String getCountry()
			{
				return null;
			}

			@Override
			public String getModificationSeq()
			{
				return null;
			}

			@Override
			public boolean isMailingAddress()
			{
				return false;
			}

			@Override
			public int getCategoryId()
			{
				return 0;
			}

			@Override
			public boolean isPreferred()
			{
				return false;
			}

			@Override
			public String getElectronicAddress()
			{
				return null;
			}

			@Override
			public AddressMedium getAddressType()
			{
				return null;
			}

			@Override
			public AddressType getPostAddress()
			{
				return null;
			}

			@Override
			public String getPoBoxPrefix()
			{
				return null;
			}

			@Override
			public String getStateOther()
			{
				return null;
			}
		};
		return address;
	}

	public UserProfile getActiveProfile()
	{
		UserInformation userInfo = new UserInformationImpl();
		userInfo.setClientKey(ClientKey.valueOf("id1"));
		UserProfile activeProfile = new UserProfileAdapterImpl(userInfo, new JobProfileImpl());
		return activeProfile;
	}
}
