package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAsimAccountServiceTest {
    @InjectMocks
    private CorporateActionAsimAccountService corporateActionAsimAccountService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    private Map<AccountKey, WrapAccount> accountMap;

    private Map<AccountKey, WrapAccount> emptyAccountMap;

    @Before
    public void setup() {
        WrapAccount wrapAccount1 = mock(WrapAccount.class);
        WrapAccount wrapAccount2 = mock(WrapAccount.class);
        WrapAccount wrapAccount3 = mock(WrapAccount.class);

        when(wrapAccount1.getAccountKey()).thenReturn(AccountKey.valueOf("123"));
        when(wrapAccount2.getAccountKey()).thenReturn(AccountKey.valueOf("456"));
        when(wrapAccount3.getAccountKey()).thenReturn(AccountKey.valueOf("789"));

        accountMap = new HashMap<>();
        accountMap.put(wrapAccount1.getAccountKey(), wrapAccount1);
        accountMap.put(wrapAccount2.getAccountKey(), wrapAccount2);
        accountMap.put(wrapAccount3.getAccountKey(), wrapAccount3);

        emptyAccountMap = new HashMap<>();

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);
    }

    @Test
    public void testHasAsimAccounts() {
        List<String> accounts = Arrays.asList("123", "456", "789");

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        assertTrue(corporateActionAsimAccountService.hasAsimAccounts(accounts));

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(emptyAccountMap);
        assertFalse(corporateActionAsimAccountService.hasAsimAccounts(accounts));

        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(null);
        assertFalse(corporateActionAsimAccountService.hasAsimAccounts(accounts));

        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        assertFalse(corporateActionAsimAccountService.hasAsimAccounts(accounts));
    }

    @Test
    public void testIsAsimAccount() {
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);
        assertTrue(corporateActionAsimAccountService.isAsimAccount("123"));
        assertFalse(corporateActionAsimAccountService.isAsimAccount("111"));
    }

    @Test
    public void testHasAsimAccountWithUser() {
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);
        assertTrue(corporateActionAsimAccountService.hasAsimAccountWithUser());
    }
}
