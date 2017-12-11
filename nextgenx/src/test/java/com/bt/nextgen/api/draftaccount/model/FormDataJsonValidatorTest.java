package com.bt.nextgen.api.draftaccount.model;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.FormDataValidatorImpl;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.config.JsonObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


/**
 * Created by m040398 on 3/02/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FormDataJsonValidatorTest.Config.class)
public final class FormDataJsonValidatorTest extends AbstractJsonReaderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormDataJsonValidatorTest.class);

    @Autowired
    private FormDataValidator formDataValidator;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper objectMapper;

    // ------------------------------------------------------
    // Individual
    // ------------------------------------------------------

    @Test
    public void testSchemaFactoryLoader() throws  Exception {
        final String NAMESPACE_SCHEMAS = "resource:/com/bt/nextgen/api/draftaccount/schemas/v1/";
        final String APPLICATION_JSON_SCHEMA = "accountApplicationSchema.json";
        final JsonSchemaFactory factory =
                JsonSchemaFactory.newBuilder()
                        .setLoadingConfiguration(LoadingConfiguration.newBuilder()
                                .setURITranslatorConfiguration(URITranslatorConfiguration.newBuilder().setNamespace(NAMESPACE_SCHEMAS).freeze())
                                .freeze())
                        .freeze();

        final JsonSchema schema = factory.getJsonSchema(APPLICATION_JSON_SCHEMA);
        assertNotNull(schema);
        LOGGER.debug("## json schema : {}", schema);

        final JsonNode jsonNode = JsonLoader.fromString(readJsonStringFromFile("individual-new-1.json"));
        assertNotNull(jsonNode);

        CanonicalSchemaTree sTree = (CanonicalSchemaTree) FieldUtils.readField(schema, "schema", true);
        LOGGER.debug("## schema tree: {}", sTree);
        LOGGER.debug("## schema tree asJson: {}", sTree.asJson());
        LOGGER.debug("## schema tree BaseNode: {}", sTree.getBaseNode());
    }

    @Test
    public void testNewIndividual1() throws Exception  {
        testFormData("individual-new-1.json", true);
    }

    @Test
    public void testNewIndividualRemoveXSSCharsAssertions() throws Exception  {
        final JsonNode json = JsonLoader.fromString(readJsonStringFromFile("individual-xss-names.json")).get("formData");
        ((FormDataValidatorImpl)formDataValidator).replaceChars(json);
        JsonNode investor = json.get("investors").get(0);
        //check field values after replaceChars executed
        assertThat(investor.get("firstname").asText(), is("Florin script alert('XSS') /script"));
        assertThat(investor.get("middlename").asText(), is("middle $$% #@#"));
        assertThat(investor.get("lastname").asText(), is("test $$ %#@#"));
        assertThat(investor.get("preferredname").asText(), is("flo$$ % script alert('XSS') /script #@#"));
        assertThat(investor.get("formername").asText(), is("$$% #@#"));
    }

    @Test
    public void testNewIndividualWhenXSSCharsPresent() throws Exception  {
        String jsonFile = "individual-xss-names.json";
        Map<String, Object> formData = (Map<String, Object>)readJsonFromFile(jsonFile).get("formData");
        ProcessingReport report = formDataValidator.validate(formData);
        logMessages(jsonFile, report);
        assertThat(report.isSuccess(), is(true));
    }

    @Test
    public void testNewIndividual2() throws Exception  {
        testFormData("individual-new-2.json", true);
    }

    @Test
    public void testNewIndividual3() throws Exception {
        testFormData("individual-new-3.json", true);
    }

    @Test
    public void testNewIndividual4Empty() throws Exception {
        testFormData("individual-new-4-empty.json", true);
    }

    @Test
    public void testPanoramaIndividual1() throws Exception {
        testFormData("individual-panorama-1.json", true);
    }

    @Test
    public void testExistingGCMIndividual1() throws Exception {
        testFormData("individual-gcm-1.json", true);
    }

    @Test
    public void testNewIndividualCRS() throws Exception {
        testFormData("individual-new-crs.json", true);
    }

    @Test
    public void testNewIndividualCRS_CountryForTax_Blank() throws Exception {
        testFormData("individual-new-crs-blank-countryoftax-cal.json", true);
    }

    @Test
    public void testNewIndividualCRSFailed() throws Exception {
        testFormData("individual-new-crs-failed.json", false);
    }

    @Test
    public void testNewIndividualExistingCisCRSFailed() throws Exception {
        testFormData("individual_existing_cis_failed_crs.json", false);
    }

    @Test
    public void testIndividualCRS_InvalidForeignTaxCountry() throws Exception {
        // Should throw Schema Error when more than Five Tax Countries are present in jSON
        testFormData("com/bt/nextgen/api/draftaccount/builder/v3_JsonSchema/client_application_individual_InvalidOverseasCRS.json", false);
    }

    // ------------------------------------------------------
    // Joint
    // ------------------------------------------------------

    @Test
    public void testJointEmpty() throws Exception {
        testFormData("joint-empty.json", true);
    }

    @Test
    public void testJointNewInvestor1() throws Exception {
        testFormData("joint-new-1.json", true);
    }

    @Test
    public void testJointNewInvestor2() throws Exception {
        testFormData("joint-new-2.json", true);
    }

    @Test
    public void testJointNewAndExistingPanoramaInvestor3() throws Exception {
        testFormData("joint-new-and-panorama-3.json", true);
    }

    @Test
    public void testJointNewAndExistingPanoramaAndExistingGCMInvestor4() throws Exception {
        testFormData("joint-new-and-panorama-and-gcm-4.json", true);
    }

    // ------------------------------------------------------
    // Company
    // ------------------------------------------------------

    @Test
    public void testCompanyNew1() throws Exception {
        testFormData("company-new-1.json", true);
    }

    @Test
    public void testCompanyEmpty1() throws Exception {
        testFormData("company-empty-1.json", true);
    }

    @Test
    public void testCompanyNewAndExistingPanoramaDirector2() throws Exception {
        testFormData("company-new-and-panorama-2.json", true);
    }

    @Test
    public void testCompanyCRS() throws Exception {
        testFormData("company_crs.json", true);
    }

    @Test
    public void testCompanyNewAndExistingPanoramaAndGCMDirector3() throws Exception {
        testFormData("company-new-panorama-gcm-3.json", true);
    }

    @Test
    public void testCompanyforCMA() throws Exception {
        testFormData("company_cma.json", true);
    }

    @Test
    public void testCompanyforCMA_withInvalidData() throws Exception {
        testFormData("company_cma_invalid.json", false);
    }

    // ------------------------------------------------------
    // Trust Individual : family
    // ------------------------------------------------------


    @Test
    public void testTrustIndividualFamilyEmpty1() throws Exception {
        testFormData("trust-individual-family-empty-1.json", true);
    }

    @Test
    public void testTrustIndividualFamily2() throws Exception {
        testFormData("trust-individual-family-2.json", true);
    }

    @Test
    public void testTrustIndividualFamilyWithTrustees3() throws Exception {
        testFormData("trust-individual-family-with-trustees-3.json", true);
    }

    @Test
    public void testTrustIndividualFamily4() throws Exception {
        testFormData("trust-individual-family-4.json", true);
    }

    // ------------------------------------------------------
    // Trust Individual : govsuper
    // ------------------------------------------------------

    @Test
    public void testTrustIndividualGovsuperEmpty1() throws Exception {
        testFormData("trust-individual-govsuper-empty-1.json", true);
    }

    @Test
    public void testTrustIndividualGovsuper2() throws Exception {
        testFormData("trust-individual-govsuper-2.json", true);
    }

    @Test
    public void testTrustIndividualGovsuper3() throws Exception {
        testFormData("trust-individual-govsuper-3.json", true);
    }

    @Test
    public void testExistingCorporateSMSFForCRS() throws Exception {
        testFormData("existing-smsf-corporate-crs.json", true);
    }

    @Test
    public void testExistingIndividualSMSFForCRS() throws Exception {
        testFormData("smsf-new-individual-crs.json", true);
    }

    @Test
    public void testTrustIndividualGovsuper4() throws Exception {
        String jsonFile = "trust-individual-govsuper-4.json";
        testFormData(jsonFile, true);
        String jsonString = readJsonStringFromFile(jsonFile);
        ObjectMapper jsonObjectMapper = new JsonObjectMapper();
        OnboardingApplicationFormData formData = jsonObjectMapper.readValue(jsonString, OnboardingApplicationFormData.class);
        LOGGER.debug(">> formData.getShareholderandmembers: {}", formData.getShareholderandmembers());
        LOGGER.debug(">> formData.getTrustees: {}", formData.getTrustees());
    }


    // ------------------------------------------------------
    // TRUST Individual :  Registered managed investment scheme
    // ------------------------------------------------------

    @Test
    public void testTrustIndividualInvschemeEmpty1() throws Exception {
        testFormData("trust-individual-invscheme-empty-1.json", true);
    }

    @Test
    public void testTrustIndividualInvscheme2() throws Exception {
        testFormData("trust-individual-invscheme-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Individual :   Regulated trust
    // ------------------------------------------------------

    @Test
    public void testTrustIndividualRegulatedEmpty1() throws Exception {
        testFormData("trust-individual-regulated-empty-1.json", true);
    }

    @Test
    public void testTrustIndividualRegulated2() throws Exception {
        testFormData("trust-individual-regulated-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Individual :   Others
    // ------------------------------------------------------

    @Test
    public void testTrustIndividualOthersEmpty1() throws Exception {
        testFormData("trust-individual-other-empty-1.json", true);
    }

    @Test
    public void testTrustIndividualOthers2() throws Exception {
        testFormData("trust-individual-other-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Corporate :   Family
    // ------------------------------------------------------

    @Test
    public void testTrustCorporateFamilyEmpty1() throws Exception {
        testFormData("trust-corporate-family-empty-1.json", true);
    }

    @Test
    public void testTrustCorporateFamilyBeneficiaryClasses_Exceed255chars() throws Exception {
        testFormData("trust-corporate-family-beneficiaryclasses-1.json", false);
    }

    @Test
    public void testTrustCorporateFamily2() throws Exception {
        testFormData("trust-corporate-family-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Corporate :   G0vSuper
    // ------------------------------------------------------

    @Test
    public void testTrustCorporateGovsuperEmpty1() throws Exception {
        testFormData("trust-corporate-govsuper-empty-1.json", true);
    }

    @Test
    public void testTrustCorporateGovsuper2() throws Exception {
        testFormData("trust-corporate-govsuper-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Corporate :   Registered managed investment scheme (invscheme)
    // ------------------------------------------------------

    @Test
    public void testTrustCorporateInvestmentSchemeEmpty1() throws Exception {
        testFormData("trust-corporate-invscheme-empty-1.json", true);
    }

    @Test
    public void testTrustCorporateInvestmentScheme2() throws Exception {
        testFormData("trust-corporate-invscheme-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Corporate :   Regulated
    // ------------------------------------------------------

    @Test
    public void testTrustCorporateRegulatedEmpty1() throws Exception {
        testFormData("trust-corporate-regulated-empty-1.json", true);
    }

    @Test
    public void testTrustCorporateRegulated2() throws Exception {
        testFormData("trust-corporate-regulated-2.json", true);
    }

    // ------------------------------------------------------
    // TRUST Corporate :   Other
    // ------------------------------------------------------

    @Test
    public void testTrustCorporateOtherEmpty1() throws Exception {
        testFormData("trust-corporate-other-empty-1.json", true);
    }

    @Test
    public void testTrustCorporateOther2() throws Exception {
        testFormData("trust-corporate-other-2.json", true);
    }


    // ------------------------------------------------------
    // NEW SMSFS :  Individual Trustee
    // ------------------------------------------------------

    @Test
    public void testNewSMSFIndividualEmpty1() throws Exception {
        testFormData("smsf-new-individual-empty-1.json", true);
    }

    @Test
    public void testNewSMSFIndividual2() throws Exception {
        testFormData("smsf-new-individual-2.json", true);
    }

    // ------------------------------------------------------
    // NEW SMSFS :  Corporate Trustee
    // ------------------------------------------------------

    @Test
    public void testNewSMSFCorporateEmpty1() throws Exception {
        testFormData("smsf-new-corporate-empty-1.json", true);
    }

    @Test
    public void testNewSMSFCorporate2() throws Exception {
        testFormData("smsf-new-corporate-2.json", true);
    }

    // ------------------------------------------------------
    // Existing SMSFS :  Individual Trustee
    // ------------------------------------------------------

    @Test
    public void testExistingSMSFIndividualEmpty1() throws Exception {
        testFormData("smsf-existing-individual-empty-1.json", true);
    }

    @Test
    public void testExistingSMSFIndividual2() throws Exception {
        testFormData("smsf-existing-individual-2.json", true);
    }

    // ------------------------------------------------------
    // Existing SMSFS :  Corporate Trustee
    // ------------------------------------------------------

    @Test
    public void testExistingSMSFCorporateEmpty1() throws Exception {
        testFormData("smsf-existing-corporate-empty-1.json", true);
    }

    @Test
    public void testExistingSMSFCorporate2() throws Exception {
        testFormData("smsf-existing-corporate-2.json", true);
    }

    @Test
    public void testSuperPension() throws Exception {
        testFormData("superpension-with-eligibility.json", true);
    }

    @Test
    public void testCompanyDirNewCorpSMSF() throws Exception {
        testFormData("new-corporate-smsf-company-secretary.json", true);
    }

    @Test
    public void testTrustIndividual_trustdetails_withcrschanges() throws Exception {
        testFormData("trustindividual_crs.json", true);
    }

    @Test
    public void testCompanyDirNewCorpSMSF_Failure_DirectorCount() throws Exception {
        testFormData("new-corporate-smsf-company-secretary-fail.json", false);
    }

    @Test
    public void testCompanyDirNewCorpSMSF_Failure_Secretary_Format() throws Exception {
        testFormData("new-corporate-smsf-company-secretary-fail-format.json", false);
    }

    @Test
    public void testTrustIndividual_cma_typeFamily() throws Exception {
        testFormData("trustindividual_cma.json", true);
    }

    @Test
    public void testTrustIndividual_cma_typeFamily_withInvalidData() throws Exception {
        testFormData("trustindividual_cma_invalid.json", false);
    }

    //===================== BAD DATA TESTS ========================
    @Test
    public void testIndividualBadFormData() throws Exception {
        testFormData("com/bt/nextgen/api/draftaccount/builder/v3/individual.json", false);
    }

    private void testFormData(String jsonFile, boolean expectedSuccess) throws Exception {
        Map<String, Object> formData = readJsonFromFile(jsonFile);
        ProcessingReport report = formDataValidator.validate(formData);
        logMessages(jsonFile, report);
        assertThat(report.isSuccess(), is(expectedSuccess));
    }

    private void logMessages(String jsonFile, ProcessingReport report) throws JsonProcessingException {
        for (ProcessingMessage message : report) {
//            LOGGER.error(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message.asJson()));
            if (message.getLogLevel().equals(LogLevel.FATAL) || message.getLogLevel().equals(LogLevel.ERROR)) {
                LOGGER.error("JSON file: {} has errors: {}", jsonFile, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message.asJson()));
            } else {
                LOGGER.info(message.getMessage());
            }
        }
    }

    @Configuration
    public static class Config {
        @Bean
        public FormDataValidator formDataValidatorImpl() {
            return new FormDataValidatorImpl();
        }

        @Bean
        public ObjectMapper jsonObjectMapper() {
            return new ObjectMapper();
        }
    }
}


