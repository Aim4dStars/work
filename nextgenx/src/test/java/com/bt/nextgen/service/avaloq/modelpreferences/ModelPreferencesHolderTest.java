package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class ModelPreferencesHolderTest {

    @InjectMocks
    DefaultResponseExtractor<SubaccountPreferencesHolder> subaccountResponseExtractor;

    @InjectMocks
    DefaultResponseExtractor<AccountModelPreferencesImpl> accountResponseExtractor;

    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    AccountKeyConverter accountKeyConverter = new AccountKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(AccountKeyConverter.class)).thenReturn(accountKeyConverter);

        Mockito.when(codeConverter.convert("1", "PREFERENCE_TYPE")).thenReturn("do_not_hold");
        Mockito.when(codeConverter.convert("21", "PREFERENCE_TYPE")).thenReturn("cash");
    }

    @Test
    public void testExtractSubaccountData_whenValidResponse_thenIssuerAndChoicePopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/ModelPreferences_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        subaccountResponseExtractor = new DefaultResponseExtractor<>(SubaccountPreferencesHolder.class);
        SubaccountPreferencesHolder response = subaccountResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getPreferences());
        assertEquals(2, response.getPreferences().size());

        assertEquals("110585", response.getPreferences().get(0).getIssuer().getId());
        assertEquals(Preference.CASH, response.getPreferences().get(0).getPreference());
        assertEquals(new DateTime("2016-02-16"), response.getPreferences().get(0).getEffectiveDate());

        assertEquals("110580", response.getPreferences().get(1).getIssuer().getId());
        assertEquals(Preference.PRORATA, response.getPreferences().get(1).getPreference());
        assertEquals(new DateTime("2016-02-11"), response.getPreferences().get(1).getEffectiveDate());
    }

    @Test
    public void testExtractAccountData_whenValidResponse_thenIssuerAndChoicePopulated() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/ModelPreferences_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        accountResponseExtractor = new DefaultResponseExtractor<>(AccountModelPreferencesImpl.class);
        AccountModelPreferences response = accountResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getPreferences());
        assertEquals(2, response.getPreferences().size());

        assertEquals("110744", response.getPreferences().get(0).getIssuer().getId());
        assertEquals(Preference.PRORATA, response.getPreferences().get(0).getPreference());
        assertEquals(new DateTime("2016-02-21"), response.getPreferences().get(0).getEffectiveDate());
        assertNull(response.getPreferences().get(0).getEndDate());

        assertEquals("110523", response.getPreferences().get(1).getIssuer().getId());
        assertEquals(Preference.PRORATA, response.getPreferences().get(1).getPreference());
        assertEquals(new DateTime("2016-02-26"), response.getPreferences().get(1).getEffectiveDate());
        assertNull(response.getPreferences().get(1).getEndDate());
    }
}