package com.bt.nextgen.api.draftaccount.builder.v3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ProcessInvestorApplicationRequestMsgType;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.draftaccount.builder.ProcessInvestorApplicationRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDtoConverterService;
import com.bt.nextgen.api.draftaccount.util.IdInsertion;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.UserKey;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DirectSubmissionTest extends BaseSecureIntegrationTest {

    @Autowired
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Autowired
    private ProcessInvestorApplicationRequestMsgTypeBuilder requestMsgTypeBuilder;

    private InvestorDetail investorDetail;

    private BrokerUser adviser;

    private Broker dealer;

    //@Autowired
    //private ClientIntegrationService clientIntegrationService;

    @Before
    public void setup() {
        System.out.println("Loading cacheable avaloq resources...");
        System.out.println("Done loading cacheable avaloq resources...");

        adviser = mock(BrokerUser.class);
        dealer = mock(Broker.class);
        when(adviser.getBankReferenceId()).thenReturn("SOME GCM ID");
        when(adviser.getFirstName()).thenReturn("AdviserFirstName");
        when(adviser.getLastName()).thenReturn("AdviserLastName");
        Email adviserEmail = mock(Email.class);
        when(adviserEmail.getEmail()).thenReturn("adviser@example.com");
        when(adviserEmail.getType()).thenReturn(AddressMedium.EMAIL_PRIMARY);
        when(adviser.getEmails()).thenReturn(Arrays.asList(adviserEmail));

        BrokerRole role = mock(BrokerRole.class);
        when(role.getRole()).thenReturn(JobRole.ADVISER);
        when(role.getKey()).thenReturn(BrokerKey.valueOf("123456789"));
        when(adviser.getRoles()).thenReturn(Arrays.asList(role));
        mockAdviserBusinessPhone();

        investorDetail = mock(InvestorDetail.class);
        //when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(investorDetail);

        Email ignoredEmail = createEmail("someemail@domain.com", AddressMedium.EMAIL_PRIMARY);
        when(investorDetail.getEmails()).thenReturn(Arrays.asList(ignoredEmail));
        Phone primaryPhone = createPhone("0456456456", AddressMedium.MOBILE_PHONE_PRIMARY);
        when(investorDetail.getPhones()).thenReturn(Arrays.asList(primaryPhone));
        when(investorDetail.getFirstName()).thenReturn("First name");
        when(investorDetail.getLastName()).thenReturn("Surname");
        when(investorDetail.getGcmId()).thenReturn("Some GCM ID");
    }

    private void mockAdviserBusinessPhone() {
        Phone businessPhone = mock(Phone.class);
        when(businessPhone.getCountryCode()).thenReturn("+61");
        when(businessPhone.getAreaCode()).thenReturn("0458");
        when(businessPhone.getNumber()).thenReturn("123123");
        when(businessPhone.getType()).thenReturn(AddressMedium.BUSINESS_TELEPHONE);

        Phone mobilePhone = mock(Phone.class);
        when(mobilePhone.getAreaCode()).thenReturn("02");
        when(mobilePhone.getNumber()).thenReturn("98765432");
        when(mobilePhone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_SECONDARY);


        when(adviser.getPhones()).thenReturn(Arrays.asList(businessPhone, mobilePhone));
    }

    private Phone createPhone(String phoneNumber, AddressMedium type) {
        Phone phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn(phoneNumber);
        when(phone.getType()).thenReturn(type);
        return phone;
    }

    private Email createEmail(String emailAddress, AddressMedium type) {
        Email email = mock(Email.class);
        when(email.getEmail()).thenReturn(emailAddress);
        when(email.getType()).thenReturn(type);
        return email;
    }

    @Ignore
    @Test
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void AnIndividualAccount_ShouldSubmitWithoutError_AndThenGetApproved() throws Exception {
        ClientApplicationDto app = //submitApplicationAndWaitForPartyStatus("client_application_form_data_direct.json");
                submitApplicationAndWaitForPartyStatus(
                        "com/bt/nextgen/api/draftaccount/builder/v3/client_application_individual_direct_form_data.json");

        ClientApplication draftAccount = new ClientApplication();
        submitDraftAccount(app,new FailFastErrorsImpl(),draftAccount);
    }

    private ClientApplicationDto submitApplicationAndWaitForPartyStatus(String jsonPath) throws Exception {
        ClientApplicationDto dto = submitApplication(jsonPath);

        return dto;
    }



    private void submitDraftAccount(ClientApplicationDto keyedDto, ServiceErrors serviceErrors, ClientApplication draftAccount) {
        // Note: We save formData again even though we don't expect that it has changed. This is to ensure that you always
        // submit the data that you are looking at on the summary page (in the case that multiple users are concurrently modifying
        // the same application). Joe accepted this approach in lieu of a complex locking solution.
        //LOGGER.info(FormDataConstants.ONBOARDING_LOGGING_SUBMIT + "begin");
        Object formData = keyedDto.getFormData();
        if (!keyedDto.isJsonSchemaSupported()) {
            IdInsertion.mergeIds((Map<String, Object>) formData);
        }
        draftAccount.setFormData(formData);

        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(formData);

        BrokerUser brokerUser = new BrokerUserImpl(UserKey.valueOf("79260"));
        ((BrokerUserImpl)brokerUser).addBroker(JobRole.ADVISER, BrokerKey.valueOf("79260"));
        List<Email> lstEmail = new ArrayList<Email>();
        EmailImpl email = new EmailImpl();
        email.setPreferred(true);
        email.setEmail("jim.jhonson@mail.com");
        lstEmail.add(email);

        ((BrokerUserImpl) brokerUser).setEmails(lstEmail);

        List<Phone> lstPhones = new ArrayList<Phone>();
        PhoneImpl phone = new PhoneImpl();
        phone.setPreferred(true);
        phone.setNumber("1234567890");
        lstPhones.add(phone);
        ((BrokerUserImpl) brokerUser).setPhones(lstPhones);

        OnboardingApplicationKey key =  OnboardingApplicationKey.valueOf(1234);

        String dealerGroupName = "Westpac Financial Planning";
        //Broker dealer = new BrokerUserImpl();

        //LOGGER.info(FormDataConstants.ONBOARDING_LOGGING_SUBMIT + submissionIDString);
        Object processInvestorsRequestMsgType = requestMsgTypeBuilder.buildFromForm(clientApplicationForm, adviser,
                key, keyedDto.getProductId(), dealer, new ServiceErrorsImpl());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JaxbUtil.marshall(baos, ProcessInvestorApplicationRequestMsgType.class, processInvestorsRequestMsgType);
        System.out.println(baos.toString());
    }

    private ClientApplicationDto submitApplication(String jsonPath) throws IOException {
        System.out.println("Finding a product...");
        ClientApplication application = new ClientApplication();

        System.out.println("Loading form data...");
        String json = loadFormData(jsonPath);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IdInsertion.mergeIds(map);
        application.setFormData(map);
        application.setAdviserPositionId("79260");
        application.setProductId("84965");

        System.out.println("Converting to application DTO...");
        ClientApplicationDto dto = clientApplicationDtoConverterService.convertToDto(application, new FailFastErrorsImpl());

        return dto;
    }

    private String loadFormData(String filename) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        return IOUtils.toString(in);
    }
}
