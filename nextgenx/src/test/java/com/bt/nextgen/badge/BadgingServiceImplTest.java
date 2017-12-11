package com.bt.nextgen.badge;

import com.bt.nextgen.badge.model.Badge;
import com.bt.nextgen.badge.service.BadgingServiceImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class BadgingServiceImplTest {

    @InjectMocks
    private BadgingServiceImpl badgingService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private Configuration configuration;

    private BrokerKey unbadgedAdviserKey;
    private BrokerKey panoramaAdviserKey;
    private BrokerKey badgedAdviserKey;
    private BrokerKey unbadgedDealerKey;
    private BrokerKey panoramaDealerKey;
    private BrokerKey badgedDealerKey;

    private Broker unbadgedAdviser;
    private Broker panoramaAdviser;
    private Broker badgedAdviser;
    private Broker unbadgedDealer;
    private Broker panoramaDealer;
    private Broker badgedDealer;

    private final ServiceErrors serviceErrors = new FailFastErrorsImpl();

    @Before
    public void setup() {
        Mockito.when(configuration.getString(Mockito.anyString())).thenReturn("classpath:/cms/badge/badgeConfig.xml");

        unbadgedDealerKey = BrokerKey.valueOf("SecondBadgedBroker");
        unbadgedDealer = Mockito.mock(Broker.class);
        Mockito.when(unbadgedDealer.getKey()).thenReturn(unbadgedDealerKey);
        Mockito.when(unbadgedDealer.getPositionName()).thenReturn("Unbadged Dealer");
        Mockito.when(brokerService.getBroker(unbadgedDealerKey, serviceErrors)).thenReturn(unbadgedDealer);

        panoramaDealerKey = BrokerKey.valueOf("Panorama");
        panoramaDealer = Mockito.mock(Broker.class);
        Mockito.when(panoramaDealer.getKey()).thenReturn(panoramaDealerKey);
        Mockito.when(panoramaDealer.getPositionName()).thenReturn("Panorama");// needs to match test config xml
        Mockito.when(brokerService.getBroker(panoramaDealerKey, serviceErrors)).thenReturn(panoramaDealer);

        badgedDealerKey = BrokerKey.valueOf("badgedDealer");
        badgedDealer = Mockito.mock(Broker.class);
        Mockito.when(badgedDealer.getKey()).thenReturn(badgedDealerKey);
        Mockito.when(badgedDealer.getPositionName()).thenReturn("BadgedBroker");// needs to match test config xml
        Mockito.when(brokerService.getBroker(badgedDealerKey, serviceErrors)).thenReturn(badgedDealer);

        unbadgedAdviserKey = BrokerKey.valueOf("SecondBadgedBroker");
        unbadgedAdviser = Mockito.mock(Broker.class);
        Mockito.when(unbadgedAdviser.getKey()).thenReturn(unbadgedAdviserKey);
        Mockito.when(unbadgedAdviser.getPositionName()).thenReturn("Unbadged Adviser");
        Mockito.when(unbadgedAdviser.getDealerKey()).thenReturn(unbadgedDealerKey);
        Mockito.when(unbadgedAdviser.getKey()).thenReturn(unbadgedDealerKey);
        Mockito.when(brokerService.getBroker(unbadgedAdviserKey, serviceErrors)).thenReturn(unbadgedAdviser);

        panoramaAdviserKey = BrokerKey.valueOf("panorama");
        panoramaAdviser = Mockito.mock(Broker.class);
        Mockito.when(panoramaAdviser.getKey()).thenReturn(panoramaAdviserKey);
        Mockito.when(panoramaAdviser.getPositionName()).thenReturn("Panorama");
        Mockito.when(panoramaAdviser.getDealerKey()).thenReturn(panoramaDealerKey);
        Mockito.when(brokerService.getBroker(panoramaAdviserKey, serviceErrors)).thenReturn(panoramaAdviser);

        badgedAdviserKey = BrokerKey.valueOf("badgedAdviser");
        badgedAdviser = Mockito.mock(Broker.class);
        Mockito.when(badgedAdviser.getKey()).thenReturn(badgedAdviserKey);
        Mockito.when(badgedAdviser.getPositionName()).thenReturn("badged Adviser");
        Mockito.when(badgedAdviser.getDealerKey()).thenReturn(badgedDealerKey);
        Mockito.when(brokerService.getBroker(badgedAdviserKey, serviceErrors)).thenReturn(badgedAdviser);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenNotLoggedIn_thenDefaultBadgeShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(false);
        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsDefaultBadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsUnbadgedAdviser_thenDefaultBadgeShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(false);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(panoramaDealer);
        Mockito.when(unbadgedDealer.getDealerKey()).thenReturn(panoramaDealerKey);
        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsDefaultBadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsBadgedAdviser_thenBadgeShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(false);

        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(badgedDealer);
        Mockito.when(badgedDealer.getKey()).thenReturn(unbadgedAdviserKey);

        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsAABadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsAnBrokerWithoutADealer_thenDefaultShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(false);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(null);

        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsDefaultBadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsAnInvestorWithAnUnbadgedDealer_thenDefaultBadgeShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(true);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        WrapAccount account1 = Mockito.mock(WrapAccount.class);
        Mockito.when(account1.getAdviserPositionId()).thenReturn(panoramaDealerKey);
        accountMap.put(account1.getAccountKey(), account1);

        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsDefaultBadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsAnInvestorWithAnBadgedDealer_thenBadgeShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(true);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        WrapAccount account1 = Mockito.mock(WrapAccount.class);
        Mockito.when(account1.getAdviserPositionId()).thenReturn(badgedAdviserKey);
        accountMap.put(account1.getAccountKey(), account1);

        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);
        Mockito.when(badgedDealer.getKey()).thenReturn(unbadgedAdviserKey);
        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsAABadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsAnInvestorWithMultipleDealers_thenDefaultShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(true);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        WrapAccount account1 = Mockito.mock(WrapAccount.class);
        Mockito.when(account1.getAccountKey()).thenReturn(AccountKey.valueOf("account1"));
        Mockito.when(account1.getAdviserPositionId()).thenReturn(panoramaAdviserKey);
        accountMap.put(account1.getAccountKey(), account1);

        WrapAccount account2 = Mockito.mock(WrapAccount.class);
        Mockito.when(account2.getAccountKey()).thenReturn(AccountKey.valueOf("account2"));
        Mockito.when(account2.getAdviserPositionId()).thenReturn(badgedAdviserKey);
        accountMap.put(account2.getAccountKey(), account2);

        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsDefaultBadge(badge);
    }

    @Test
    public void testGetBadgeForCurrentUser_whenLoggedInAsAnInvestorWithMultipleAccountsOnABadgedDealer_thenBadgedShouldBeReturned() {
        Mockito.when(userProfileService.isLoggedIn()).thenReturn(true);
        Mockito.when(userProfileService.isInvestor()).thenReturn(true);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        WrapAccount account1 = Mockito.mock(WrapAccount.class);
        Mockito.when(account1.getAdviserPositionId()).thenReturn(badgedAdviserKey);
        accountMap.put(account1.getAccountKey(), account1);

        WrapAccount account2 = Mockito.mock(WrapAccount.class);
        Mockito.when(account2.getAdviserPositionId()).thenReturn(badgedAdviserKey);
        accountMap.put(account2.getAccountKey(), account2);

        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);
        Mockito.when(badgedDealer.getKey()).thenReturn(unbadgedAdviserKey);
        Badge badge = badgingService.getBadgeForCurrentUser(serviceErrors);
        Assert.assertNotNull(badge);
        assertIsAABadge(badge);
    }


    private void assertIsDefaultBadge(Badge badge) {
        Assert.assertEquals("Panorama", badge.getBadgeName());
        Assert.assertEquals("panoramaLogo", badge.getLogo());
        Assert.assertEquals("panoramaReportLogo", badge.getReportLogo());
    }

    private void assertIsAABadge(Badge badge) {
        Assert.assertEquals("Asset Administrator", badge.getBadgeName());
        Assert.assertEquals("aaLogo", badge.getLogo());
        Assert.assertEquals("aaReportLogo", badge.getReportLogo());
    }

}
