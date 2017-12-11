package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.api.profile.v1.service.ProfileUtil;
import com.bt.nextgen.core.security.UserRole;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.model.ProductToggleEnum;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Properties.class})
public class PermissionBaseDtoServiceTest {
	@InjectMocks
	private PermissionBaseDtoServiceImpl service;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private BrokerIntegrationService brokerService;

	@Mock
	private UserInformationIntegrationService userInformationService;

	@Mock
	private StaticIntegrationService staticIntegrationService;

	@Mock
	private ProductIntegrationService productService;

	@Mock
	private FeatureTogglesService featureTogglesService;

	@Mock
	private BrokerHelperService brokerHelperService;

	@Mock
	private ProfileUtil profileUtil;

	private Broker broker;
	private BrokerUser brokerUser;
	private BrokerUser brokerUserReadonly;
	private UserProfile user;
	private ServiceErrors serviceErrors;

	private FeatureToggles toggles;

	private List<Product> productList;

	@Before
	public void setup() {
		serviceErrors = new ServiceErrorsImpl();

		broker = getBroker();
		brokerUser = getBrokerUser();
		brokerUserReadonly = getBrokerUserReadOnly();
        productList = Arrays.asList(getProduct("DUMMY_WL_PRODUCT"));

		Collection<Code> userFrs = new ArrayList<>();
		CodeImpl code = new CodeImpl();
		code.setCategory("btfg$user_role");
		code.setUserId("$FR_CLT_LIST_UI_REP");
		code.setName("FR View Client List");
		code.setIntlId("9747");
		code.setCodeId("9747");
		userFrs.add(code);
		when(staticIntegrationService.loadCodes(((CodeCategory) Mockito.anyObject()), ((ServiceErrors) Mockito.anyObject())))
				.thenReturn(userFrs);
		when(userProfileService.isExistingAvaloqUser()).thenReturn(true);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		toggles = new FeatureToggles();
		when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(toggles);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);


		List<Broker> brokerList = new ArrayList<>();
		BrokerImpl multiAdvisersOne = new BrokerImpl(BrokerKey.valueOf("1"), BrokerType.SUPER_DEALER);
		brokerList.add(multiAdvisersOne);
		BrokerImpl multiAdvisersTwo = new BrokerImpl(BrokerKey.valueOf("1"), BrokerType.ACCOUNTANT);
		brokerList.add(multiAdvisersTwo);
		BrokerImpl multiAdvisersThree = new BrokerImpl(BrokerKey.valueOf("1"), BrokerType.ADVISER);
		multiAdvisersThree.setCanViewMarketData(Boolean.TRUE);
		final BrokerKey marketDataDgKey = BrokerKey.valueOf("123");
		multiAdvisersThree.setDealerKey(marketDataDgKey);
		brokerList.add(multiAdvisersThree);

		final BrokerImpl marketDataDg = new BrokerImpl(marketDataDgKey, BrokerType.DEALER);
		marketDataDg.setCanViewMarketData(Boolean.TRUE);

		final Broker noMarketDataDg = new BrokerImpl(BrokerKey.valueOf("noMarketData"), BrokerType.DEALER);

		when(brokerHelperService.getAdviserListForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
				.thenReturn(brokerList);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
				.thenReturn(multiAdvisersThree);

		when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenAnswer(new Answer<Broker>() {
			@Override
			public Broker answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				BrokerKey key = (BrokerKey) args[0];

				if (key != null && key.equals(marketDataDgKey)) {
					return marketDataDg;
				} else {
					return noMarketDataDg;
				}
			}
		});

		//productList = Arrays.asList(getProduct("PROD.OFFER.60f52dc6d17421eaf1632ac9e"), getProduct("PROD.OFFER.d1b65704184ae3b87799400f7ab"), getProduct("PROD.OFFER.60f52dc6d17421eaf1632ac9"), getProduct("PROD.OFFER.d1b65704184ae3b87799400f7a"));
		when(userProfileService.getSamlToken()).thenReturn(new SamlToken(SamlUtil.loadSaml()));
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);

	}

	private Product getProduct(final String shortName) {
		Product product = mock(Product.class);
		when(product.getShortName()).thenReturn(shortName);
		when(product.getProductLevel()).thenReturn(ProductLevel.WHITE_LABEL);
		return product;
	}

	private Product getSuperProduct(final String shortName) {
		Product product = mock(Product.class);
		when(product.getShortName()).thenReturn(shortName);
		when(product.getProductLevel()).thenReturn(ProductLevel.WHITE_LABEL);
		when(product.isSuper()).thenReturn(true);
		return product;
	}

	private Broker getBroker() {
		Broker broker = mock(Broker.class);
		when(broker.canViewMarketData()).thenReturn(Boolean.TRUE);
		return broker;
	}

	private JobProfile getJobProfile(final JobRole jobRole) {
		JobProfile jobProfile = mock(JobProfile.class);
		when(jobProfile.getJobRole()).thenReturn(jobRole);
		return jobProfile;
	}

	public UserProfile getUser(JobRole jobRole, FunctionalRole... functionalRoles) {
		UserInformation user = new UserInformationImpl();
		user.setClientKey(ClientKey.valueOf("client1"));
		user.setFunctionalRoles(getRoles());

		if (functionalRoles.length == 0) {
			user.setFunctionalRoles(Arrays.asList(FunctionalRole.Finalise_a_deceased_estate,
					FunctionalRole.Make_a_Pay_Anyone_Payment_to_a_new_payee, FunctionalRole.View_Client_Orders,
					FunctionalRole.Trade_entry, FunctionalRole.Products_and_news));
		} else {
			user.setFunctionalRoles(Arrays.asList(functionalRoles));
		}
		UserProfile profile = new UserProfileAdapterImpl(user, getJobProfile(jobRole));

		return profile;
	}

	public UserProfile getUserProfile(JobRole jobRole, List<FunctionalRole> functionalRoles, List<String> userRoles) {
		UserInformation userInfo =  mock(UserInformation.class);

		when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf("client1"));
		when(userInfo.getFunctionalRoles()).thenReturn(functionalRoles);
		when(userInfo.getUserRoles()).thenReturn(userRoles);

		return new UserProfileAdapterImpl(userInfo, getJobProfile(jobRole));
	}

	public UserProfile getDirectInvestor() {
		UserInformation user = new UserInformationImpl();
		user.setClientKey(ClientKey.valueOf("client1"));
		user.setFunctionalRoles(getRoles());
		user.setFunctionalRoles(Arrays.asList(FunctionalRole.Make_a_payment_linked_accounts, FunctionalRole.Trade_entry,
				FunctionalRole.View_Client_Orders, FunctionalRole.Submit_trade_to_executed,
				FunctionalRole.Purchase_Term_Deposits, FunctionalRole.Submit_RIP));
		UserProfile profile = new UserProfileAdapterImpl(user, getJobProfile(JobRole.INVESTOR));

		return profile;
	}

	public UserProfile getUserWithNoInvestmentOrderPermission(JobRole jobRole) {
		UserInformation user = new UserInformationImpl();
		user.setFunctionalRoles(getRoles());
		user.setFunctionalRoles(Arrays.asList(FunctionalRole.Make_a_BPAYPay_Anyone_Payment,
				FunctionalRole.Finalise_a_deceased_estate, FunctionalRole.Make_a_Pay_Anyone_Payment_to_a_new_payee));
		UserProfile profile = new UserProfileAdapterImpl(user, getJobProfile(jobRole));

		return profile;
	}

	public List<FunctionalRole> getRoles() {
		List<FunctionalRole> roles = new ArrayList<FunctionalRole>();
		roles.add(FunctionalRole.Make_a_BPAYPay_Anyone_Payment);
		roles.add(FunctionalRole.Person_requests);
		roles.add(FunctionalRole.ACCPAY_FUND_Maker);
		return roles;
	}

	public BrokerUser getBrokerUser() {
		BrokerUser brokerUser = mock(BrokerUser.class);
		when(brokerUser.getRoles()).thenReturn(
				Arrays.asList(getBrokerRole("broker1", JobAuthorizationRole.Support_With_Cash, JobRole.PARAPLANNER),
						getBrokerRole("broker2", JobAuthorizationRole.Support_Without_Cash, JobRole.PARAPLANNER),
						getBrokerRole("broker4", JobAuthorizationRole.Supervisor_With_Cash, JobRole.PARAPLANNER),
						getBrokerRole("broker5", JobAuthorizationRole.Supervisor_Without_Cash, JobRole.PARAPLANNER)));
		return brokerUser;
	}

	public BrokerUser getBrokerUserReadOnly() {
		BrokerUser brokerUser = mock(BrokerUser.class);
		when(brokerUser.getRoles()).thenReturn(
				Arrays.asList(getBrokerRole("broker1", JobAuthorizationRole.Support_With_Cash, JobRole.PARAPLANNER),
						getBrokerRole("broker3", JobAuthorizationRole.Supervisor_ReadOnly, JobRole.PARAPLANNER)));
		return brokerUser;
	}

	private BrokerRole getBrokerRole(final String brokerKey, final JobAuthorizationRole authorization, final JobRole role) {
		BrokerRole brokerRole = new BrokerRole() {
			@Override
			public JobRole getRole() {
				return role;
			}

			@Override
			public JobAuthorizationRole getAuthorizationRole() {
				return authorization;
			}

			@Override
			public BrokerKey getKey() {
				return BrokerKey.valueOf(brokerKey);
			}
		};
		return brokerRole;
	}

	@Test
	public void testSearch_containsAllPermissions() throws Exception {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		toggles.setFeatureToggle("projectOne", true);
		toggles.setFeatureToggle("projectTwo", false);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.payment.anyone.create"), false);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("dummy.permission"), false);
		assertEquals(permission.hasPermission("emulating"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
		assertEquals(permission.hasPermission("feature.global.projectOne"), true);
		assertEquals(permission.hasPermission("feature.global.projectTwo"), false);
		assertEquals(permission.hasPermission("feature.global.unknownProject"), false);
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

	@Test
	public void testSearch_containsNoPermissions() throws Exception {
		user = getUserWithNoInvestmentOrderPermission(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		toggles.setFeatureToggle("projectOne", false);
		toggles.setFeatureToggle("projectTwo", true);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);

		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), false);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
		assertEquals(permission.hasPermission("feature.global.projectOne"), false);
		assertEquals(permission.hasPermission("feature.global.projectTwo"), true);
		assertEquals(permission.hasPermission("feature.global.unknownProject"), false);
	}

	@Test
	public void testSearch_whenUpdateAccountRole_thenContainsPermissions() throws Exception {
		user = getUser(JobRole.INVESTOR, FunctionalRole.BP_requests);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(false, permission.hasPermission("account.investmentpreferences.view"));
	}

	@Test
	public void testSearchAdviserNonFunctionalRole() throws Exception {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("user.detail.intermediary.view"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), true);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
		assertEquals(permission.hasPermission("clientlist.adviser.view"), true);
		assertEquals(permission.hasPermission("products.termdeposit.csv.view"), true);
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

	@Test
	public void testSearchAccountantNonFunctionalRole() throws Exception {
		user = getUser(JobRole.ACCOUNTANT);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(null);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("clientlist.adviser.view"), true);
		assertEquals(permission.hasPermission("clientlist.accountant.view"), true);
		assertEquals(permission.hasPermission("messages.accountant.view"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), true);
		assertEquals(permission.hasPermission("products.termdeposit.csv.view"), true);
		assertEquals(permission.hasPermission("products.btcash.view"), false);
		assertEquals(permission.hasPermission("products.smsf.view"), false);
	}

	@Test
	public void testSearchInvestorNonFunctionalRole() throws Exception {
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("user.detail.investor.view"), true);
		assertEquals(permission.hasPermission("investor.view"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), false);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), false);
		assertEquals(permission.hasPermission("products.termdeposit.csv.view"), true);
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

	@Test
	public void testWestpactAdvisedInvestorViewTrue() {
		BrokerImpl broker = null;
		broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.ADVISER);
		broker.setDealerKey(BrokerKey.valueOf("45677"));
		broker.setExternalBrokerKey(ExternalBrokerKey.valueOf("DG.PBP"));
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("products.termdeposit.csv.view"), false);

	}

	@Test
	public void testWestpactAdvisedInvestorViewFalse() {
		BrokerImpl broker = null;
		broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.ADVISER);
		broker.setDealerKey(BrokerKey.valueOf("45677"));
		broker.setExternalBrokerKey(ExternalBrokerKey.valueOf("DG.HANADV"));
		Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("products.termdeposit.csv.view"), true);

	}

	@Test
	public void testSearchDirectInvestorNonFunctionalRole() throws Exception {
		// currently using investor role, when there will be a separate role for direct investor - use that.
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(false, permission.hasPermission("account.details.update"));
		assertEquals(false, permission.hasPermission("default"));
		assertEquals(true, permission.hasPermission("emulating"));
		assertEquals(true, permission.hasPermission("user.detail.investor.view"));
		assertEquals(true, permission.hasPermission("investor.view"));
		assertEquals(false, permission.hasPermission("client.alerts.unread.view"));
		assertEquals(false, permission.hasPermission("account.investmentorders.menu.view"));
		assertEquals(false, permission.hasPermission("account.investmentpreferences.view"));
		assertEquals(false, permission.hasPermission("recent.accounts.view"));
		assertEquals(false, permission.hasPermission("products.termdeposit.menu.view"));
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

	@Test
	public void testTradeFunctionalRoleNotAvailableForInvestors() throws Exception {
		user = getDirectInvestor();
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("account.trade.entry"), false);
		assertEquals(permission.hasPermission("account.trade.create"), false);
		assertEquals(permission.hasPermission("account.trade.submit"), false);
		assertEquals(permission.hasPermission("emulating"), false);
		assertEquals(permission.hasPermission("account.payment.linked.create"), true);
		assertEquals(permission.hasPermission("account.deposit.linked.create"), true);
	}

	@Test
	public void testSearchAssistantNonFunctionalRoleReadOnlyLinkedAdviser() throws Exception {
		user = getUser(JobRole.ASSISTANT);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
				brokerUserReadonly);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("account.trade.create"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), false);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
	}

	@Test
	public void testSearchAssistantNonFunctionalRoleNotReadOnlyLinkedAdviser() throws Exception {
		user = getUser(JobRole.ASSISTANT);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
				brokerUser);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("account.trade.create"), true);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
	}

	@Test
	public void testSearchPracticeManagerNonFunctionalRole() throws Exception {
		user = getUser(JobRole.PRACTICE_MANAGER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), false);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
	}

	@Test
	public void testPracticeManagerNonReadOnlyPermission() throws Exception {
		when(brokerService.getBrokerUser(any(UserProfile.class), any(ServiceErrorsImpl.class))).thenReturn(brokerUser);
		user = getUser(JobRole.PRACTICE_MANAGER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);

	}

	@Test
	public void testSearchParaplannerNotReadOnlyRoleLinkedToAdviser() throws Exception {
		user = getUser(JobRole.PARAPLANNER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
				brokerUser);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.Initiate_a_complaint, FunctionalRole.Generate_Quarterly_TFN_ABN_Report,
						FunctionalRole.Create_a_complaint_feedback));
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("account.trade.create"), true);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
	}

	@Test
	public void testSearchParaplanner_noBrokerFound() throws Exception {
		user = getUser(JobRole.PARAPLANNER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
				null);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.Initiate_a_complaint, FunctionalRole.Generate_Quarterly_TFN_ABN_Report,
						FunctionalRole.Create_a_complaint_feedback));
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("account.trade.create"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), false);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
	}

	@Test
	public void testSearchParaplannerReadOnlyRoleLinkedToAdviser() throws Exception {
		user = getUser(JobRole.PARAPLANNER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
				brokerUserReadonly);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("account.trade.create"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), false);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
	}

	@Test
	public void testSearchDealerGroupManagerNonFunctionalRole() throws Exception {
		user = getUser(JobRole.DEALER_GROUP_MANAGER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		BrokerUser user = mock(BrokerUser.class);
		when(user.getRoles()).thenReturn(
				Collections.singletonList(getBrokerRole("dgm1", JobAuthorizationRole.Supervisor_Update,
						JobRole.DEALER_GROUP_MANAGER)));
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
				user);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), true);
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

	@Test
	public void testSearchInvestmentManagerNonFunctionalRole() throws Exception {
		user = getUser(JobRole.INVESTMENT_MANAGER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.Finalise_a_deceased_estate, FunctionalRole.Make_a_Pay_Anyone_Payment_to_a_new_payee,
						FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry));
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.details.update"), false);
		assertEquals(permission.hasPermission("default"), false);
		assertEquals(permission.hasPermission("emulating"), true);
		assertEquals(permission.hasPermission("client.alerts.unread.view"), false);
		assertEquals(permission.hasPermission("account.investmentorders.menu.view"), true);
		assertEquals(permission.hasPermission("account.investmentpreferences.view"), false);
		assertEquals(permission.hasPermission("recent.accounts.view"), false);
		assertEquals(permission.hasPermission("products.btcash.view"), false);
		assertEquals(permission.hasPermission("products.smsf.view"), false);
	}

	@Test
	public void testAdviser_withNewIndividualSmsfProductToggle() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getProduct(ProductToggleEnum.NEW_SMSF_INDIVIDUAL.getProductShortNameList().get(0)));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.newsmsf.individual.create"), true);
	}


	@Test
	public void testAdviser_withToggleON_withUserExperience_ADVISED() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(Properties.getSafeBoolean("feature.superforASIM")).thenReturn(true);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getActiveProfile().getUserExperience()).thenReturn(UserExperience.ADVISED);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.super.create"), true);
	}

	@Test
	public void testAdviser_withToggleON_withUserExperience_ASIM() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(Properties.getSafeBoolean("feature.superforASIM")).thenReturn(true);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getActiveProfile().getUserExperience()).thenReturn(UserExperience.ASIM);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.super.create"), true);
	}

	@Test
	public void testAdviser_withToggleOFF_withUserExperience_ASIM() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(Properties.getSafeBoolean("feature.superforASIM")).thenReturn(false);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getActiveProfile().getUserExperience()).thenReturn(UserExperience.ASIM);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.super.create"), false);
	}

	@Test
	public void testParaplanner_withToggleOFF_withUserExperience_ADVISED() throws Exception {
		user = getUser(JobRole.PARAPLANNER);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(Properties.getSafeBoolean("feature.superforASIM")).thenReturn(false);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getActiveProfile().getUserExperience()).thenReturn(UserExperience.ADVISED);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.super.create"), true);
	}

	@Test
	public void testAdviser_withInsuranceToggle() throws Exception {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports));
		productList = Arrays.asList(getProduct(ProductToggleEnum.INSURANCE.getProductShortNameList().get(0)),
				getProduct(ProductToggleEnum.INSURANCE.getProductShortNameList().get(1)));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.insurance.businessreport.view"), true);
		assertEquals(permission.hasPermission("account.insurance.applications.view"), true);
	}

	@Test
	public void testAdviser_withoutDGWithInsuranceAPLLinked() throws Exception {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports));
		//productList = Arrays.asList(getProduct(ProductToggleEnum.INSURANCE.getProductShortNameList().get(0)), getProduct(ProductToggleEnum.INSURANCE.getProductShortNameList().get(1)));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.insurance.businessreport.view"), false);
		assertEquals(permission.hasPermission("account.insurance.applications.view"), false);
	}

	@Test
	public void testParaplanner_withInsuranceToggle() throws Exception {
		user = getUser(JobRole.PARAPLANNER, FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports));
		productList = Arrays.asList(getProduct(ProductToggleEnum.INSURANCE.getProductShortNameList().get(0)),
				getProduct(ProductToggleEnum.INSURANCE.getProductShortNameList().get(1)));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.insurance.businessreport.view"), true);
		assertEquals(permission.hasPermission("account.insurance.applications.view"), true);
	}

	@Test
	public void testParaplanner_withoutDGWithInsuranceAPLLinked() throws Exception {
		user = getUser(JobRole.PARAPLANNER, FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports);
		when(userInformationService.getFunctionalRoleList(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(
				Arrays.asList(FunctionalRole.View_Insurance_Account, FunctionalRole.View_intermediary_reports));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.insurance.businessreport.view"), false);
		assertEquals(permission.hasPermission("account.insurance.applications.view"), false);
	}

	@Test
	public void testAdviser_withoutNewIndividualSmsfProductToggle() throws Exception {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.newsmsf.individual.create"), false);
	}

	@Test
	public void testAdviser_withNewCorporateSmsfProductToggle() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getProduct(ProductToggleEnum.NEW_SMSF_CORPORATE.getProductShortNameList().get(0)));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.newsmsf.corporate.create"), true);
	}

	@Test
	public void testAdviser_withoutNewCorporateSmsfProductToggle() throws Exception {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(true);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		//when(productService.getDealerGroupForProfile(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(productList);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("account.application.newsmsf.corporate.create"), false);
	}

	@Test
	public void testMarketViewDataPermissionsForInvestor() {
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertTrue(permission.hasPermission("marketinformation.view"));
	}

	@Test
	public void testMarketViewDataPermissionsForAdvisers() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertTrue(permission.hasPermission("marketinformation.view"));
	}
	
	@Test
    public void testMarketViewDataPermissionsForDealerGroupManager() {
        user = getUser(JobRole.DEALER_GROUP_MANAGER);
        when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
        when(userProfileService.isEmulating()).thenReturn(false);

        PermissionsDto permission = service.findOne(serviceErrors);
        assertNotNull(permission);
        assertTrue(permission.hasPermission("marketinformation.view"));
    }

	@Test
	public void marketDataShouldBeProvidedForAccountRole() {
		user = getUser(JobRole.ACCOUNTANT);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertTrue(permission.hasPermission("marketinformation.view"));
		assertTrue(permission.hasPermission("marketinformation.realtimeprice.view"));
	}

	@Test
	public void marketDataShouldBeProvidedForAccountSupportStaffRole() {
		user = getUser(JobRole.ACCOUNTANT_SUPPORT_STAFF);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertTrue(permission.hasPermission("marketinformation.view"));
		assertTrue(permission.hasPermission("marketinformation.realtimeprice.view"));
	}

	@Test
	public void marketDataShouldNotBeProvidedForInvestmentManagerRole() {
		user = getUser(JobRole.INVESTMENT_MANAGER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertFalse(permission.hasPermission("marketinformation.view"));
	}

	@Test
	public void test_permissionForExistingAvaloqUser() {
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		toggles.setFeatureToggle("projectOne", true);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertTrue(permission.hasPermission("user.detail.investor.view"));
		assertTrue(permission.hasPermission("investor.view"));
		assertTrue(permission.hasPermission("feature.global.projectOne"));
	}

	@Test
	public void test_permissionForUser_notInAvaloq() {
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.isExistingAvaloqUser()).thenReturn(false);
		toggles.setFeatureToggle("projectOne", true);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertFalse(permission.hasPermission("user.detail.investor.view"));
		assertFalse(permission.hasPermission("investor.view"));
		assertTrue(permission.hasPermission("feature.global.projectOne"));
	}

	@Test
	public void testSetMarketInformationRealtimePricePermission_whenInvestorRoleIsProvided_thenTheRealtimePricePermissionIsNotSet() {
		user = getUser(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertFalse("service.hasBasicPermission(\"marketinformation.realtimeprice.view\") should return false for investors.",
				service.hasBasicPermission("marketinformation.realtimeprice.view"));
	}

	@Test
	public void testSetMarketInformationRealtimePricePermission_whenAnyOtherRoleIsProvided_thenTheRealtimePricePermissionIsBasedOnAccountTradeCreate() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"marketinformation.realtimeprice.view\") should true false for other roles that have account.trade.create.",
				service.hasBasicPermission("marketinformation.realtimeprice.view"));

		user.setFunctionalRoles(Arrays.asList(FunctionalRole.Finalise_a_deceased_estate,
				FunctionalRole.Make_a_Pay_Anyone_Payment_to_a_new_payee, FunctionalRole.View_Client_Orders));

		assertTrue(
				"service.hasBasicPermission(\"marketinformation.realtimeprice.view\") should return false for other roles that do not have account.trade.create.",
				service.hasBasicPermission("marketinformation.realtimeprice.view"));
	}

	@Test
	public void testSetCorporateActionPermission_whenInvestorRoleWithoutTradeCreateIsProvided_thenTheCorporateActionInvestorIsNotSet() {
		user = getUserWithNoInvestmentOrderPermission(JobRole.INVESTOR);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertFalse(
				"service.hasBasicPermission(\"corporateactions.investor\") should return false for investors without account.trade.create.",
				service.hasBasicPermission("corporateactions.investor"));
	}

	@Test
	public void testSetCorporateActionPermission_whenAdviserRoleIsProvided_thenTheCorporateActionInvestorIsNotSet() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertFalse("service.hasBasicPermission(\"account.corporateactions.investor\") should return false for advisers.",
				service.hasBasicPermission("corporateactions.investor"));
	}

	@Test
	public void testSetLCPermission() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		FeatureToggles featureToggles = new FeatureToggles();
		featureToggles.setFeatureToggle("LifeCentralOptionView", true);
		when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertTrue(permission.hasPermission("LC.view"));
	}

	@Test
	public void testSetLCPermissionForMissingPPId() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		when(userProfileService.getSamlToken()).thenReturn(new SamlToken(SamlUtil.loadSamlwithoutPPId()));
		FeatureToggles featureToggles = new FeatureToggles();
		featureToggles.setFeatureToggle("LifeCentralOptionView", true);
		when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertFalse(permission.hasPermission("LC.view"));
	}

	@Test
	public void testSetLCPermissionWhenFeatureToggleOff() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		when(userProfileService.getSamlToken()).thenReturn(new SamlToken(SamlUtil.loadSamlwithoutPPId()));
		FeatureToggles featureToggles = new FeatureToggles();
		featureToggles.setFeatureToggle("LifeCentralOptionView", false);
		when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertFalse(permission.hasPermission("LC.view"));
	}

	@Test
	public void testSetCorporateActionPermission_whenAdviserRoleWithoutTradeCreateIsProvided_thenTheCorporateActionTransactIsNotSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.Unknown_Role);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertFalse(
				"service.hasBasicPermission(\"corporateactions.transact\") should return false for advisers without account.trade.create.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenAdviserRoleWithTradeCreateOrTradeSubmitIsProvided_thenTheCorporateActionViewAndTransactAreSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.Trade_entry);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for advisers with account.trade.create.",
				service.hasBasicPermission("corporateactions.view"));
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.transact\") should return true for advisers with account.trade.create.",
				service.hasBasicPermission("corporateactions.transact"));

		user = getUser(JobRole.ADVISER, FunctionalRole.Submit_trade_to_executed);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for advisers with account.trade.submit.",
				service.hasBasicPermission("corporateactions.view"));
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.transact\") should return true for advisers with account.trade.submit.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenAdviserRoleWithPortfolioUploadOrCreateIsProvided_thenTheCorporateActionViewAndTransactAreSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.Start_upload_model_file);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for advisers with account.trade.create.",
				service.hasBasicPermission("corporateactions.view"));
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.transact\") should return true for advisers with account.trade.create.",
				service.hasBasicPermission("corporateactions.transact"));

		user = getUser(JobRole.ADVISER, FunctionalRole.Create_model_portfolios);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for advisers with account.trade.create.",
				service.hasBasicPermission("corporateactions.view"));
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.transact\") should return true for advisers with account.trade.create.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenAdviserRoleWithIntermediaryReportsIsProvided_thenTheCorporateActionViewIsSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_intermediary_reports);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for advisers with intermediary.adviser.report.",
				service.hasBasicPermission("corporateactions.view"));
		assertFalse(
				"service.hasBasicPermission(\"corporateactions.transact\") should return false for advisers with intermediary.adviser.report.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenRoleWithModelPortfolioViewIsProvided_thenTheCorporateActionViewIsSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_model_portfolios);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for user with modelportfolios.view.",
				service.hasBasicPermission("corporateactions.view"));
		assertFalse(
				"service.hasBasicPermission(\"corporateactions.transact\") should return true for user with modelportfolios.view.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenRoleWithModelPortfolioUploadIsProvided_thenTheCorporateActionViewAndTransactAreSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.Start_upload_model_file);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for user with modelportfolios.upload.",
				service.hasBasicPermission("corporateactions.view"));
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.transact\") should return false for user with modelportfolios.upload.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenRoleWithModelPortfolioCreateIsProvided_thenTheCorporateActionViewAndTransactIsSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.Create_model_portfolios);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.view\") should return true for user with modelportfolios.create.",
				service.hasBasicPermission("corporateactions.view"));
		assertTrue(
				"service.hasBasicPermission(\"corporateactions.transact\") should return true for user with modelportfolios.create.",
				service.hasBasicPermission("corporateactions.transact"));
	}

	@Test
	public void testSetCorporateActionPermission_whenDealerGroup_thenTheCorporateActionDealerGroupViewIsSet() {
		user = getUser(JobRole.DEALER_GROUP_MANAGER, FunctionalRole.Unknown_Role);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);
		assertTrue(service.hasBasicPermission("corporateactions.dealergroup.view"));
		assertFalse(service.hasBasicPermission("corporateactions.investmentmanager.view"));
	}

	@Test
	public void testSetCorporateActionPermission_whenInvestmentManager_thenTheCorporateActionInvestmentManagerViewIsSet() {
		user = getUser(JobRole.INVESTMENT_MANAGER, FunctionalRole.Unknown_Role);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);
		assertFalse(service.hasBasicPermission("corporateactions.dealergroup.view"));
		assertTrue(service.hasBasicPermission("corporateactions.investmentmanager.view"));
	}

	@Test
	public void testSetTrackingPermission_whenIntermediaryAdviserReportIsNotProvided_thenTheTrackingViewPermissionIsNotSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.Unknown_Role);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertFalse(
				"service.hasBasicPermission(\"tracking.view\") should return false for users without intermediary.adviser.report.",
				service.hasBasicPermission("tracking.view"));
	}

	@Test
	public void testSetTrackingPermission_whenIntermediaryAdviserReportIsProvided_thenTheTrackingViewPermissionIsSet() {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_intermediary_reports);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(
				"service.hasBasicPermission(\"tracking.view\") should return true for users with intermediary.adviser.report.",
				service.hasBasicPermission("tracking.view"));
	}

	@Test
	public void testAdviserBeneficiaryList_withToggleON_withUserExperience_ASIM() throws Exception {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_intermediary_reports);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(Properties.getSafeBoolean("feature.superforASIM")).thenReturn(true);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getActiveProfile().getUserExperience()).thenReturn(UserExperience.ASIM);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("business.beneficiaries.view"), true);
	}

	@Test
	public void testAdviserBeneficiaryList_withNOSUPER_ToggleON_withUserExperience_ASIM() throws Exception {
		user = getUser(JobRole.ADVISER, FunctionalRole.View_intermediary_reports);
		productList = Arrays.asList(getProduct("DUMMY_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(Properties.getSafeBoolean("feature.superforASIM")).thenReturn(true);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.switchActiveProfile(anyString()).getUserExperience()).thenReturn(UserExperience.ASIM);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("business.beneficiaries.view"), false);
	}

	@Test
	public void testSetMenuHeaderPermission_whenNotTrusteeOrIrgUserRoles_thenTheMenuHeaderOptionsAreSetToTrue() {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		assertTrue(service.hasBasicPermission("messages.menu.view"));
		assertTrue(service.hasBasicPermission("help.menu.view"));
		assertFalse(service.hasBasicPermission("trustee.view"));
	}

	@Test
	public void testSetCorporateActionApprovalPermissions_whenIsServiceOpsAndTrusteeRole_thenTheCorrectPermissionsAreSet() {
		UserProfile userProfile = getUserProfile(JobRole.SERVICE_AND_OPERATION, Arrays.asList(FunctionalRole
						.View_security_events_approval),
				Arrays.asList(UserRole.TRUSTEE_READ_ONLY.getRole()));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(userProfile);

		// This flag switches off "I'd like to" menu in UI kernel
		assertTrue(service.hasBasicPermission("trustee.view"));
		assertTrue(service.hasBasicPermission("corporateactions.approval.view"));
		assertTrue(service.hasBasicPermission("corporateactions.trustee.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.trustee.approval.transact"));
		assertFalse(service.hasBasicPermission("corporateactions.irg.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.irg.approval.transact"));
		assertFalse(service.hasBasicPermission("user.detail.intermediary.view"));
		assertFalse(service.hasBasicPermission("recent.accounts.view"));

		userProfile = getUserProfile(JobRole.SERVICE_AND_OPERATION_LIMITED, Arrays.asList(FunctionalRole.Transact_security_events_approval),
				Arrays.asList(UserRole.TRUSTEE_BASIC.getRole()));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(userProfile);

		assertTrue(service.hasBasicPermission("trustee.view"));
		assertTrue(service.hasBasicPermission("corporateactions.approval.view"));
		assertTrue(service.hasBasicPermission("corporateactions.trustee.approval.view"));
		assertTrue(service.hasBasicPermission("corporateactions.trustee.approval.transact"));
		assertFalse(service.hasBasicPermission("corporateactions.irg.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.irg.approval.transact"));
		assertFalse(service.hasBasicPermission("user.detail.intermediary.view"));
		assertFalse(service.hasBasicPermission("recent.accounts.view"));
	}

	@Test
	public void testSetCorporateActionApprovalPermissions_whenIsServiceOpsAndIrgRole_thenTheCorrectPermissionsAreSet() {
		UserProfile userProfile = getUserProfile(JobRole.SERVICE_AND_OPERATION, Arrays.asList(FunctionalRole
						.View_security_events_approval),
				Arrays.asList(UserRole.IRG_READ_ONLY.getRole()));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(userProfile);

		assertTrue(service.hasBasicPermission("trustee.view"));
		assertTrue(service.hasBasicPermission("corporateactions.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.trustee.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.trustee.approval.transact"));
		assertTrue(service.hasBasicPermission("corporateactions.irg.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.irg.approval.transact"));
		assertFalse(service.hasBasicPermission("user.detail.intermediary.view"));
		assertFalse(service.hasBasicPermission("recent.accounts.view"));

		userProfile = getUserProfile(JobRole.SERVICE_AND_OPERATION_LIMITED, Arrays.asList(FunctionalRole.Transact_security_events_approval),
				Arrays.asList(UserRole.IRG_BASIC.getRole()));
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(userProfile);

		assertTrue(service.hasBasicPermission("trustee.view"));
		assertTrue(service.hasBasicPermission("corporateactions.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.trustee.approval.view"));
		assertFalse(service.hasBasicPermission("corporateactions.trustee.approval.transact"));
		assertTrue(service.hasBasicPermission("corporateactions.irg.approval.view"));
		assertTrue(service.hasBasicPermission("corporateactions.irg.approval.transact"));
		assertFalse(service.hasBasicPermission("user.detail.intermediary.view"));
		assertFalse(service.hasBasicPermission("recent.accounts.view"));
	}

	@Test
	public void testPermissions_SuperOnlyProduct() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"));
		PowerMockito.mockStatic(Properties.class);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("products.btcash.view"), false);
		assertEquals(permission.hasPermission("products.smsf.view"), false);
	}

	@Test
	public void testPermissions_MultipleProducts() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT"), getProduct("DUMMY_WHITE_LABEL"));
		PowerMockito.mockStatic(Properties.class);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

	@Test
	public void testAdviserPermissions_SingleNonSuperProduct() throws Exception {
		user = getUser(JobRole.ADVISER);
		productList = Arrays.asList(getProduct("DUMMY_WHITE_LABEL"));
		PowerMockito.mockStatic(Properties.class);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.getActiveProfile()).thenReturn(user);
		when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
		when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);

		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("products.btcash.view"), true);
		assertEquals(permission.hasPermission("products.smsf.view"), true);
	}

    @Test
    public void testAdviserPermissions_MultipleSuperProduct() throws Exception {
        user = getUser(JobRole.ADVISER);
        productList = Arrays.asList(getSuperProduct("DUMMY_SUPER_PRODUCT1"), getSuperProduct("DUMMY_SUPER_PRODUCT2"));
        PowerMockito.mockStatic(Properties.class);
        when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
        when(userProfileService.getActiveProfile()).thenReturn(user);
        when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);

        PermissionsDto permission = service.findOne(serviceErrors);
        assertNotNull(permission);
        assertEquals(permission.hasPermission("products.btcash.view"), false);
        assertEquals(permission.hasPermission("products.smsf.view"), false);
    }

	@Test
	public void testAdviser_withoutSupportStaffClientListPermission() throws Exception {
		user = getUser(JobRole.ADVISER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
			brokerUserReadonly);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("clientlist.supportstaff.view"), false);
	}

	@Test
	public void testParaplanner_withoutSupportStaffClientListPermission() throws Exception {
		user = getUser(JobRole.PARAPLANNER);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		when(brokerService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(
			brokerUserReadonly);
		Broker dgBroker = mock(Broker.class);
		when(dgBroker.getParentEBIKey()).thenReturn(ExternalBrokerKey.valueOf("BT"));
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(dgBroker);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("clientlist.supportstaff.view"), false);
	}

	@Test
	public void testAssistant_withSupportStaffClientListPermission() throws Exception {
		user = getUser(JobRole.ASSISTANT);
		when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
		when(userProfileService.isEmulating()).thenReturn(false);
		Broker dgBroker = mock(Broker.class);
		when(dgBroker.getParentEBIKey()).thenReturn(ExternalBrokerKey.valueOf("WPAC"));
		when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(dgBroker);
		PermissionsDto permission = service.findOne(serviceErrors);
		assertNotNull(permission);
		assertEquals(permission.hasPermission("clientlist.supportstaff.view"), true);
	}

    @Test
    public void testDealerGroup_withTMPSuperProductEnabled() throws Exception {
        user = getUser(JobRole.DEALER_GROUP_MANAGER);
        when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
        when(userProfileService.isEmulating()).thenReturn(false);
        when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        when(broker.isTmpSuperProductEnabled()).thenReturn(Boolean.FALSE);
        when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                productList);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(null);
        PermissionsDto permission = service.findOne(serviceErrors);
        // by default, permission is set to true.
        assertEquals(permission.hasPermission("modelportfolio.tailored.super.edit"), true);
        assertEquals(permission.hasPermission("mda.intermediary.report.view"), false);

        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(broker);
        permission = service.findOne(serviceErrors);
        assertEquals(permission.hasPermission("modelportfolio.tailored.super.edit"), false);
    }

    @Test
    public void testPortfolioManager_withCorrectPermissionsEnabled() throws Exception {
        user = getUser(JobRole.PORTFOLIO_MANAGER);
        when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
        when(userProfileService.isEmulating()).thenReturn(false);
        when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

        when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                productList);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(null);
        PermissionsDto permission = service.findOne(serviceErrors);
        // by default, permission is set to true.
        assertEquals(permission.hasPermission("mda.intermediary.report.view"), true);
        assertEquals(permission.hasPermission("modelportfolio.tailored.view"), true);
        assertEquals(permission.hasPermission("tracking.view"), true);
    }

    @Test
    public void testDealerGroup_withModelConstructionTypesEnabled() throws Exception {
        user = getUser(JobRole.DEALER_GROUP_MANAGER);
        when(userProfileService.switchActiveProfile(anyString())).thenReturn(user);
        when(userProfileService.isEmulating()).thenReturn(false);
        when(broker.isTmpFixedConstructionEnabled()).thenReturn(Boolean.FALSE);
        when(broker.isTmpFloatingConstructionEnabled()).thenReturn(Boolean.FALSE);
        when(productService.getDealerGroupProductList(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                productList);
        PermissionsDto permission = service.findOne(serviceErrors);
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(null);
        // true by default
        assertEquals(true, permission.hasPermission("modelportfolio.tailored.fixed.edit"));
        assertEquals(true, permission.hasPermission("modelportfolio.tailored.floating.edit"));

        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(broker);
        permission = service.findOne(serviceErrors);
        assertEquals(false, permission.hasPermission("modelportfolio.tailored.fixed.edit"));
        assertEquals(false, permission.hasPermission("modelportfolio.tailored.floating.edit"));
    }
}
