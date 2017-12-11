package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.context.ApplicationContext;

/**
 * Created by L079353 on 23/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoHelperServiceTest extends AbstractJsonReaderTest {

    @Mock
    private StaticIntegrationService staticService;

    @InjectMocks
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    @Mock
    private Code pensionExemption;

    @Mock
    private Code eligibilityCriteria;

    @Mock
    private Code conditionOfRelease;

    @Mock
    private Code country;

    @Mock
    private IPersonDetailsForm personDetailsForm;

    @Mock
    ClientApplicationRepository clientApplicationRepository;

    @Mock
    OnboardingAccountRepository onboardingAccountRepository;

    @Mock
    ApplicationContext applicationContext;


    @Test
    public void test_getCodeNameByIntlId(){
        when(pensionExemption.getIntlId()).thenReturn("pensioner");
        when(pensionExemption.getName()).thenReturn("Exempt payee as pensioner");
        when(staticService.loadCodes(eq(CodeCategory.PENSION_EXEMPTION_REASON), any(ServiceErrors.class))).thenReturn(Arrays.asList(pensionExemption));
        String pensionExemptionReason = clientApplicationDetailsDtoHelperService.getCodeNameByIntlId(CodeCategory.PENSION_EXEMPTION_REASON,"pensioner",null);
        assertThat(pensionExemptionReason,is("Exempt payee as pensioner"));
    }

    @Test
    public void test_eligibilityCriteria(){
        when(eligibilityCriteria.getIntlId()).thenReturn("unpsv");
        when(eligibilityCriteria.getName()).thenReturn("eligibility criteria long name for unpsv");
        when(staticService.loadCodeByUserId(eq(CodeCategory.PENSION_ELIGIBILITY_CRITERIA),eq("unpsv"),any(ServiceErrors.class))).thenReturn(eligibilityCriteria);
        String eligibilityCriteria = clientApplicationDetailsDtoHelperService.eligibilityCriteria("unpsv",null);
        assertThat(eligibilityCriteria,is("eligibility criteria long name for unpsv"));
    }

    @Test
    public void test_conditionofrelease(){
        when(conditionOfRelease.getIntlId()).thenReturn("oth");
        when(conditionOfRelease.getName()).thenReturn("condition of release long name for oth");
        when(staticService.loadCodeByUserId(eq(CodeCategory.PENSION_CONDITION_RELEASE),eq("oth"),any(ServiceErrors.class))).thenReturn(conditionOfRelease);
        String eligibilityCriteria = clientApplicationDetailsDtoHelperService.conditionOfRelease("oth",null);
        assertThat(eligibilityCriteria,is("condition of release long name for oth"));
    }

    @Test
    public void testGetExistingPersonsByCISKey() throws IOException {
        OnboardingAccount onboardingAccount = createOnboardingAccount();
        when(onboardingAccountRepository.findByAccountNumber(Mockito.anyString())).thenReturn(onboardingAccount);
        ClientApplication clientApplication = createClientApplication();
        BrokerIdentifier brokerIdentifier = new BrokerIdentifierImpl("12345678");
        when(clientApplicationRepository.findByOnboardingApplicationKey(any(OnboardingApplicationKey.class),any(ArrayList.class))).thenReturn(clientApplication);
        Map<String,Boolean> existingPersonCISkeys = clientApplicationDetailsDtoHelperService.getExistingPersonsByCISKey("123456789",Arrays.asList(brokerIdentifier),new ServiceErrorsImpl());
        assertThat(existingPersonCISkeys.size(),is(1));
        assertThat(existingPersonCISkeys.get("1234567"),is(false));

    }

    @Test
    public void testGetExistingPersonsByCISKey_Invalid() throws IOException {
        OnboardingAccount onboardingAccount = null;
        when(onboardingAccountRepository.findByAccountNumber(Mockito.anyString())).thenReturn(onboardingAccount);
        ClientApplication clientApplication = createClientApplication();
        BrokerIdentifier brokerIdentifier = new BrokerIdentifierImpl("12345678");
        when(clientApplicationRepository.findByOnboardingApplicationKey(any(OnboardingApplicationKey.class),any(ArrayList.class))).thenReturn(clientApplication);
        Map<String,Boolean> existingPersonCISkeys = clientApplicationDetailsDtoHelperService.getExistingPersonsByCISKey("123456789",Arrays.asList(brokerIdentifier),new ServiceErrorsImpl());
        assertNull(existingPersonCISkeys);
    }


    @Test
    public void testGetExistingPersonsByCISKey_InvalidKey() throws IOException {
        OnboardingAccount onboardingAccount = createOnboardingAccount();
        when(onboardingAccount.getOnboardingApplicationKey()).thenReturn(null);
        when(onboardingAccountRepository.findByAccountNumber(Mockito.anyString())).thenReturn(onboardingAccount);
        ClientApplication clientApplication = createClientApplication();
        BrokerIdentifier brokerIdentifier = new BrokerIdentifierImpl("12345678");
        when(clientApplicationRepository.findByOnboardingApplicationKey(any(OnboardingApplicationKey.class),any(ArrayList.class))).thenReturn(clientApplication);
        Map<String,Boolean> existingPersonCISkeys = clientApplicationDetailsDtoHelperService.getExistingPersonsByCISKey("123456789",Arrays.asList(brokerIdentifier),new ServiceErrorsImpl());
        assertNull(existingPersonCISkeys);

    }

    private OnboardingAccount createOnboardingAccount() {
        OnboardingAccount onBoardingAccount = mock(OnboardingAccount.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(123);
        when(onBoardingAccount.getOnboardingApplicationKey()).thenReturn(onboardingApplicationKey);
        return onBoardingAccount;
    }

    private ClientApplication createClientApplication() throws IOException{
        ClientApplication clientApplication = new ClientApplication();
        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"), any(Class.class))).thenReturn(jsonObjectMapper);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"))).thenReturn(jsonObjectMapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);
        clientApplication.setApplicationContext(applicationContext);
        clientApplication.setFormData(readJsonFromFile("client_application_form_data_2.json"));
        return clientApplication;
    }

}
