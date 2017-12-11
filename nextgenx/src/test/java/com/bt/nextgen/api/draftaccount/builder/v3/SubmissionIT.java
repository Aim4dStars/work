package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.SubmissionTransacter;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationApprovalDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationSubmitDto;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationApprovalDtoService;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDtoConverterService;
import com.bt.nextgen.api.draftaccount.util.IdInsertion;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.api.product.service.ProductDtoServiceImpl;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.util.SamlUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SubmissionIT extends BaseSecureIntegrationTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ProductDtoServiceImpl products;

    @Autowired
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private SubmissionTransacter transacter;

    @Autowired
    private ClientApplicationApprovalDtoService clientApplicationApprovalDtoService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Ignore
    @Transactional
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void AnIndividualAccount_ShouldSubmitWithoutError_AndThenGetApproved() throws Exception {
        ClientApplicationSubmitDto app = submitApplicationAndWaitForPartyStatus("client_application_form_data.json");

        OnboardingParty investor = investorForApp(app);
        insertInvestorTncRecord(investor);
        loginAs(investor.getGcmPan());

        approveApplication(app);
        verifyAccountIsActive(app);
    }

    private void verifyAccountIsActive(ClientApplicationSubmitDto app) {
        entityManager.flush();
        entityManager.clear();

        ClientApplication clientApplication = entityManager.find(ClientApplication.class, app.getKey().getClientApplicationKey());
        assertThat(clientApplication.getStatus(), is(ClientApplicationStatus.active));
    }

    private void approveApplication(ClientApplicationSubmitDto app) {
        entityManager.clear();
        ClientApplication clientApplication = entityManager.find(ClientApplication.class, app.getKey().getClientApplicationKey());
        OnBoardingApplication onBoardingApplication = clientApplication.getOnboardingApplication();

        ClientApplicationApprovalDto clientApplicationApprovalDto = new ClientApplicationApprovalDto(onBoardingApplication.getKey(), null);
        clientApplicationApprovalDtoService.submit(clientApplicationApprovalDto, new FailFastErrorsImpl());
    }

    private void insertInvestorTncRecord(OnboardingParty investor) {
        User user = userRepository.newUser(investor.getGcmPan());
        userRepository.update(user);
    }

    private OnboardingParty investorForApp(ClientApplicationSubmitDto app) {
        entityManager.clear();
        ClientApplication clientApplication = entityManager.find(ClientApplication.class, app.getKey().getClientApplicationKey());
        OnBoardingApplication onBoardingApplication = clientApplication.getOnboardingApplication();
        List<OnboardingParty> parties = onBoardingApplication.getParties();

        return parties.get(0);
    }

    private void loginAs(String gcmPan) {
        String[] authorities = {"ROLE_INVESTOR"};
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                "investor",
                "investor",
                authorities);

        Profile dummyProfile = new Profile(new SamlToken(
                SamlUtil.loadSaml("investor", authorities, gcmPan
                )));

        authentication.setDetails(dummyProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Ignore
    @Test
    @Transactional
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void AnIndividualAccount_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_form_data.json");

    }


    @Ignore
    @Test
    @Transactional
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void ACompanyAccount_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_company_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void ACompanyAccount_OneDirector_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_company_form_data_one_director.json");
    }

    @Ignore // TODO need to fix. fails due to party status not changing to NotificationSent
    @Test
    @Transactional
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            profileId = "715",
            jobId = "104188")
    public void ACompanyAccount_TenDirectors_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_company_form_data_ten_directors.json");
    }

    @Ignore // TODO need to fix. fails due to party status not changing to NotificationSent
    @Test
    @Transactional
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            profileId = "715",
            jobId = "104188")
    public void AJointAccount_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_form_data_joint.json");

    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            profileId = "715",
            jobId = "104188")
    public void CorporateSmsf_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corpsmsf_form_data.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void MinimalIndividualSmsf_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_form_data_minimal_individual_smsf.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void CorpSmsfWithNonApprover_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corpsmsf_with_non_approver_form_data.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void MinimalCorporateSmsf_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corpsmsf_form_data.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void CorporateSmsfWithAdditionalShareholdersAndMembers_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corpsmsf_form_data_with_addl_members.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void CorporateSmsfWithExistingDirectors_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corpsmsf_form_data_existing_director.json");
    }

    @Test
    @Ignore //TODO correct it throws error -- Bad Request [400]
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void FullyPopulatedIndividualSmsf_ShouldSubmitWithoutError() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_form_data_fully_populated_individual_smsf.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustFamilyIdvDocumentSolicitorLetter() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_family_form_data_idv_solicitor_letter.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustFamilyIdvDocumentTrustDeed() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_family_form_data_idv_trustdeed.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustFamilyIdvDocumentAtoNotice() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_family_form_data_idv_ato_notice.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustFamilyBeneficiaryClass() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_family_form_data_beneficiary_class.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustFamilyWithAdditionalBeneficiaryAndShareholder() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_family_form_data_addl_shareholder_beneficiary.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustOtherWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_other_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustRegulatedWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_regulated_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustRegisteredMISWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_mis_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitCorporateTrustGovtSuperWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_govtsuper_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            profileId = "715",
            jobId = "104188")
    public void SubmitCorporateTrustWithTenDirectors() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_corptrust_with_10_directors_form_data.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            profileId = "715",
            jobId = "104188")
    public void SubmitIndividualTrustFamilyWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_individualtrust_family_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitIndividualTrustFamilyWithAdditionalBeneficiary() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_individualtrust_family_form_data_with_addl_beneficiary.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitIndividualTrustOtherWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_individualtrust_other_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitIndividualTrustGovtSuperWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_individualtrust_govtsuper_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitIndividualTrustRegisteredMISWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_individualtrust_mis_form_data_minimal.json");
    }

    @Test
    @Ignore
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
            // job_user_id
            profileId = "715",
            // job_id
            jobId = "104188")
    public void SubmitIndividualTrustRegulatedWithMinimalData() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_individualtrust_regulated_form_data_minimal.json");
    }

    @Test
         @Ignore
         @Transactional(value = "springJpaTransactionManager")
         @Rollback(false)
         @SecureTestContext(username = "adviser", customerId = "201601533", jobRole = "ADVISER",
                 profileId = "715",
                 jobId = "104188")
         public void SubmitIndividualTrustInvScheme() throws Exception {
        submitApplicationAndWaitForPartyStatus("client_application_form_data_individualtrust_invscheme.json");
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(false)
    @SecureTestContext(username = "adviser", customerId = "217160011", jobRole = "ADVISER",
            // job_user_id
            profileId = "711",
            // job_id
            jobId = "71819")
    public void SubmitIndividualDirect() throws Exception {
        submitApplicationAndWaitForPartyStatus("com/bt/nextgen/api/draftaccount/builder/v3/client_application_individual_direct_form_data.json");
    }

    private ClientApplicationSubmitDto submitApplicationAndWaitForPartyStatus(String jsonPath) throws Exception {
        ClientApplicationSubmitDto dto = submitApplication(jsonPath);

        transacter.waitForPartyStatus(dto, Arrays.asList("NotificationSent","ExistingPanoramaOnlineUser"));

        return dto;
    }

    private ClientApplicationSubmitDto submitApplication(String jsonPath) throws IOException {
        System.out.println("Finding a product...");
        List<ApiSearchCriteria> searchCriterias = new ArrayList<>(1);

        searchCriterias.add(new ApiSearchCriteria("positionId",
                        ApiSearchCriteria.SearchOperation.EQUALS,
                        EncodedString.fromPlainText(userProfileService.getPositionId()).toString(),
                        ApiSearchCriteria.OperationType.STRING)
        );

        ProductDto product = products.search(searchCriterias, new FailFastErrorsImpl()).get(0);
        ClientApplication application = new ClientApplication();

        System.out.println("Loading form data...");
        System.out.println("product.getKey().getProductId():" + EncodedString.toPlainText(product.getKey().getProductId()));
        String json = loadFormData(jsonPath);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        IdInsertion.mergeIds(map);
        application.setFormData(map);
        application.setAdviserPositionId(userProfileService.getPositionId());
        application.setProductId(EncodedString.toPlainText(product.getKey().getProductId()));
        //application.setProductId("84965");

        transacter.save(application);

        System.out.println("Converting to application DTO...");
        ClientApplicationSubmitDto dto = new ClientApplicationSubmitDto(new ClientApplicationKey(application.getId()));
        dto.setOffline(false);
        dto.setAdviserId(application.getAdviserPositionId());
        dto.setProductId(application.getProductId());


        transacter.submit(dto);

        return dto;
    }

    private String loadFormData(String filename) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        return IOUtils.toString(in);
    }
}
