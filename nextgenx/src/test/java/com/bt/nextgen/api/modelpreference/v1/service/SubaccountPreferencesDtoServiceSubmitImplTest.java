package com.bt.nextgen.api.modelpreference.v1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesActionDto;
import com.bt.nextgen.api.order.model.PreferenceActionDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.IssuerAccountImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreferenceIntegrationService;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.PreferenceAction;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class SubaccountPreferencesDtoServiceSubmitImplTest {
    @InjectMocks
    private SubaccountPreferencesDtoServiceSubmitImpl dtoService;

    @Mock
    private ModelPreferenceIntegrationService integrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Before
    public void setup() throws Exception {

        final List<IssuerAccount> accounts = new ArrayList<>();
        IssuerAccountImpl account = new IssuerAccountImpl();
        account.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("issuerId"));
        account.setAccountName("issuerName");
        accounts.add(account);

        Mockito.when(accountService.loadIssuerAccount(Mockito.anyList(), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<IssuerAccount>>() {
                    @Override
                    public List<IssuerAccount> answer(InvocationOnMock invocation) throws Throwable {
                        return accounts;
                    }
                });
    }

    @Test
    public void testToModelPortfolioDto_whenSuppliedWithKey_thenKeyPassedToIntegrationTier() {
        List<PreferenceActionDto> actions = new ArrayList<>();
        actions.add(new PreferenceActionDto(EncodedString.fromPlainText("issuerId").toString(), "issuerName", Preference
                .valueOf("CASH"), PreferenceAction.valueOf("SET")));

        SubaccountPreferencesActionDto preferences = new SubaccountPreferencesActionDto(
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("accountKey").toString()),
                actions);

        Mockito.when(integrationService.updatePreferencesForSubaccount(Mockito.any(AccountKey.class), Mockito.anyList(),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<ModelPreference>>() {
                    @Override
                    public List<ModelPreference> answer(InvocationOnMock invocation) throws Throwable {
                        AccountKey key = (AccountKey) invocation.getArguments()[0];
                        List<ModelPreferenceAction> actions = (List<ModelPreferenceAction>) invocation.getArguments()[1];
                        Assert.assertEquals("accountKey", key.getId());
                        Assert.assertEquals(1, actions.size());
                        Assert.assertEquals(PreferenceAction.SET, actions.get(0).getAction());
                        Assert.assertEquals(Preference.CASH, actions.get(0).getPreference());
                        return Collections.emptyList();
                    }
                });
        dtoService.submit(preferences, new FailFastErrorsImpl());
    }
}
