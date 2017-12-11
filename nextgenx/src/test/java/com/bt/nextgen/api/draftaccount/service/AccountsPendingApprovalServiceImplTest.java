package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.client.ClientIdentifier;
import com.bt.nextgen.service.avaloq.client.ClientIdentifierImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountsPendingApprovalServiceImplTest {

    private static final String CURRENT_CLIENT_KEY_VALUE = "CURRENT_CLIENT_KEY_VALUE";
    private static final String OTHER_CLIENT_KEY_VALUE = "OTHER_CLIENT_KEY_VALUE";

    private static final String DUMMY_ACCOUNT_KEY_VALUE_1 = "DUMMY_ACCOUNT_KEY_VALUE_1";
    private static final String DUMMY_ACCOUNT_KEY_VALUE_2 = "DUMMY_ACCOUNT_KEY_VALUE_2";

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @InjectMocks
    private AccountsPendingApprovalServiceImpl service;

    private ServiceErrors serviceErrors;

    @Before
    public void setUp(){
        serviceErrors = new ServiceErrorsImpl();
        UserInformation userInformation = new UserInformationImpl();
        userInformation.setClientKey(ClientKey.valueOf(CURRENT_CLIENT_KEY_VALUE));
        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(userInformation);
    }


    @Test
    public void findAll_shouldReturnApplicationDetailsForCurrentUser() throws Exception {
        WrapAccount pendingAccount1 = createAccount(AccountKey.valueOf(DUMMY_ACCOUNT_KEY_VALUE_1), AccountStatus.PEND_OPN, CURRENT_CLIENT_KEY_VALUE);
        WrapAccount pendingAccount2 = createAccount(AccountKey.valueOf(DUMMY_ACCOUNT_KEY_VALUE_2), AccountStatus.PEND_OPN, OTHER_CLIENT_KEY_VALUE);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(pendingAccount1.getAccountKey(), pendingAccount1);
        accountMap.put(pendingAccount2.getAccountKey(), pendingAccount2);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(accountIntegrationService.loadWrapAccountDetail(AccountKey.valueOf(DUMMY_ACCOUNT_KEY_VALUE_1), serviceErrors))
                .thenReturn(createWrapAccountDetail(CURRENT_CLIENT_KEY_VALUE));
        when(accountIntegrationService.loadWrapAccountDetail(AccountKey.valueOf(DUMMY_ACCOUNT_KEY_VALUE_2), serviceErrors))
                .thenReturn(createWrapAccountDetail(OTHER_CLIENT_KEY_VALUE));
        List<WrapAccount> wrapAccounts = service.getUserAccountsPendingApprovals(serviceErrors);
        assertThat(wrapAccounts, hasSize(1));
    }

    @Test
      public void findAll_shouldReturnApplicationDetailsFromOnlyPendingAccounts() throws Exception {
        WrapAccount activeAccount = createAccount(AccountKey.valueOf("ACTIVE_ID"), AccountStatus.ACTIVE,
                CURRENT_CLIENT_KEY_VALUE);
        WrapAccount pendingAccount1 = createAccount(AccountKey.valueOf("PENDING_ID1"), AccountStatus.PEND_OPN,
                CURRENT_CLIENT_KEY_VALUE);
        WrapAccount pendingAccount2 = createAccount(AccountKey.valueOf("PENDING_ID2"),
                AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS, CURRENT_CLIENT_KEY_VALUE);
        WrapAccount pendingAccount3 = createAccount(AccountKey.valueOf("PENDING_ID3"), AccountStatus.FUND_ESTABLISHMENT_PENDING,
                CURRENT_CLIENT_KEY_VALUE);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(activeAccount.getAccountKey(), activeAccount);
        accountMap.put(pendingAccount1.getAccountKey(), pendingAccount1);
        accountMap.put(pendingAccount2.getAccountKey(), pendingAccount2);
        accountMap.put(pendingAccount3.getAccountKey(), pendingAccount3);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(createWrapAccountDetail(CURRENT_CLIENT_KEY_VALUE));
        List<WrapAccount> wrapAccounts = service.getUserAccountsPendingApprovals(serviceErrors);
        assertThat(wrapAccounts, hasSize(3));
    }

    @Test
    public void findAll_shouldReturnApplicationDetailsFromOnlyPendingAccountsForCurrentUser() throws Exception {
        WrapAccount pendingAccount1 = createAccount(AccountKey.valueOf("PENDING_ID"), AccountStatus.PEND_OPN, CURRENT_CLIENT_KEY_VALUE);
        WrapAccount pendingAccount2 = createAccount(AccountKey.valueOf("PENDING_ID2"), AccountStatus.PEND_OPN, CURRENT_CLIENT_KEY_VALUE);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(pendingAccount1.getAccountKey(), pendingAccount1);
        accountMap.put(pendingAccount2.getAccountKey(), pendingAccount2);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(createWrapAccountDetail(CURRENT_CLIENT_KEY_VALUE));
        List<WrapAccount> wrapAccounts = service.getUserAccountsPendingApprovals(new ServiceErrorsImpl());
        assertThat(wrapAccounts, hasSize(2));
    }

    private WrapAccount createAccount(AccountKey key, AccountStatus status, String ownerId) {
        WrapAccount account = mock(WrapAccount.class);
        when(account.getAccountStatus()).thenReturn(status);
        when(account.getAccountKey()).thenReturn(key);
        List<ClientIdentifier> list= new ArrayList<>();
        list.add(new ClientIdentifierImpl(ownerId));
        when((account.getOwnerClientKeys())).thenReturn(list);

        return account;
    }

    private WrapAccountDetail createWrapAccountDetail(final String clientKeyValue) {
        WrapAccountDetailImpl detail = new WrapAccountDetailImpl();
        Map<ClientKey, PersonRelation> associatedPersons = new HashMap<ClientKey, PersonRelation>() {{
            put(ClientKey.valueOf(clientKeyValue), null);
        }};
        detail.setAssociatedPersons(associatedPersons);
        return detail;
    }
}
