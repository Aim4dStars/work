package com.bt.nextgen.api.modelpreference.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.IssuerAccountImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreferenceIntegrationService;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.modelpreferences.SubaccountModelPreferences;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SubaccountPreferencesDtoServiceImplTest
{
	@InjectMocks
    private SubaccountPreferencesDtoServiceImpl dtoService;

	@Mock
    private ModelPreferenceIntegrationService integrationService;

    @Mock
    private AccountIntegrationService accountService;

	@Before
	public void setup() throws Exception
	{

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
    public void testToModelPortfolioDto_whenSuppliedWithKey_thenKeyPassedToIntegrationTier()
	{
        final AccountKey key = new AccountKey(EncodedString.fromPlainText("accountKey").toString());
        final AccountModelPreferences prefs = Mockito.mock(AccountModelPreferences.class);
        Mockito.when(prefs.getPreferences()).thenReturn(new ArrayList<ModelPreference>());
        Mockito.when(prefs.getSubaccountPreferences()).thenReturn(new ArrayList<SubaccountModelPreferences>());
        Mockito.when(
                integrationService.getPreferencesForSubaccount(
                        Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<ModelPreference>>() {
            @Override
            public List<ModelPreference> answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("accountKey",
                        ((com.bt.nextgen.service.integration.account.AccountKey) invocation.getArguments()[0]).getId());
                return Collections.emptyList();
            }
        });
        SubaccountPreferencesDto result = dtoService.find(key, new FailFastErrorsImpl());
        assertNotNull(result);
	}


    @Test
    public void testToModelPortfolioDto_whenSubaccountHasPreferences_thenDtoForPreferencesProduced() {
        final AccountKey key = new AccountKey(EncodedString.fromPlainText("accountKey").toString());


        final ModelPreference preference = Mockito.mock(ModelPreference.class);

        Mockito.when(preference.getIssuer())
                .thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("issuerId"));
        Mockito.when(preference.getEffectiveDate()).thenReturn(new DateTime("2012-01-01"));
        Mockito.when(preference.getEndDate()).thenReturn(null);
        Mockito.when(preference.getPreference()).thenReturn(Preference.CASH);

        Mockito.when(
                integrationService.getPreferencesForSubaccount(
                        Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(preference));

        SubaccountPreferencesDto result = dtoService.find(key, new FailFastErrorsImpl());
        assertNotNull(result);
        assertEquals(1, result.getPreferences().size());
        assertEquals("issuerId", result.getPreferences().get(0).getIssuerId());
        assertEquals(new DateTime("2012-01-01"), result.getPreferences().get(0).getEffectiveDate());
        assertEquals("CASH", result.getPreferences().get(0).getPreference());
    }
}
