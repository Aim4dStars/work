package com.bt.nextgen.api.draftaccount.builder.v3;

import java.util.Map;

import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.FormDataValidatorImpl;
import com.bt.nextgen.api.draftaccount.model.form.ExtendedPersonDetailsFormFactory;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.identityverification.v1_1.DocumentType;
import ns.btfin_com.identityverification.v1_1.IdentificationIndDocumentType;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationInformationIndTypeType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationIndType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationsIndType;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class PartyIdentificationInformationBuilderTest {

    private PartyIdentificationInformationBuilder partyIdentificationInformationBuilder = new PartyIdentificationInformationBuilder();

    @Before
    public void setupServices() {
        ObjectMapper mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);
    }

    @Test
    public void shouldBuildIDInformationForAusDrivingLicense() throws Exception {
        String ausDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "     \"photodocuments\": {\n" +
                "        \"driverlicence\": {\n" +
                "        \"documentIssuer\": \"NEW SOUTH WALES\",\n" +
                "        \"expiryDate\": \"20/10/2020\",\n" +
                "        \"documentNumber\": \"111222\",\n" +
                "        \"verificationSource\": \"original\",\n" +
                "        \"englishTranslation\": \"Not Applicable\"\n" +
                "     }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(ausDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(1));
        validateAusIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "111222", true,
                PartyIdentificationInformationIndTypeType.DRIVERS_LICENSE, "NSW", null, "20/10/2020");
    }

    @Test
    public void shouldBuildIDInformationForAusAgeCard() throws Exception {
        String ausDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"photodocuments\": {\n" +
                "    \"agecard\": {\n" +
                "      \"documentIssuer\": \"ACT\",\n" +
                "      \"verificationSource\": \"certifiedcopy\",\n" +
                "      \"englishTranslation\": \"Not Applicable\",\n" +
                "      \"documentNumber\": \"123445\",\n" +
                "      \"expiryDate\": \"12/12/2020\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(ausDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(1));
        validateAusIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "123445", false,
                PartyIdentificationInformationIndTypeType.IDENTIFICATION_CARD, "ACT", null, "12/12/2020");
    }

    @Test
    public void shouldBuildIDInformationForAusPassport() throws Exception {
        String ausDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"photodocuments\": {\n" +
                "    \"passport\": {\n" +
                "      \"documentIssuer\": \"Australia\",\n" +
                "      \"issueDate\": \"12/12/2012\",\n" +
                "      \"expiryDate\": \"12/12/2022\",\n" +
                "      \"documentNumber\": \"sdadasd\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\",\n" +
                "      \"accreditedenglishtrans\": \"notapplicable\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(ausDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(1));
        validateAusIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "sdadasd", true,
                PartyIdentificationInformationIndTypeType.PASSPORT, null, "12/12/2012", "12/12/2022");
    }

    private void validateAusIdDocumentDetails(PartyIdentificationInformationIndType partyIdentification, String documentNumber, Boolean original,
                                              PartyIdentificationInformationIndTypeType idType, String state, String issueDate, String expiryDate) {
        validateIdDocumentDetails(partyIdentification, documentNumber, original, idType, state, issueDate, expiryDate);
        IdentificationIndDocumentType identificationDocument = partyIdentification.getIdentificationDocument();
        assertFalse(identificationDocument.isAccreditedEnglishTranslation());
        assertEquals(identificationDocument.getPlaceOfIssue().getAddress().getCountryCode(), "AU");
    }

    private void validateIntlIdDocumentDetails(PartyIdentificationInformationIndType partyIdentification, String documentNumber, Boolean original,
                                              PartyIdentificationInformationIndTypeType idType, String country, String issueDate, String expiryDate, Boolean isEnglishTranslation) {
        validateIdDocumentDetails(partyIdentification, documentNumber, original, idType, null, issueDate, expiryDate);
        IdentificationIndDocumentType identificationDocument = partyIdentification.getIdentificationDocument();
        assertEquals(identificationDocument.isAccreditedEnglishTranslation(), isEnglishTranslation);
        assertEquals(identificationDocument.getPlaceOfIssue().getAddress().getCountryCode(), country);
    }

    private void validateIdDocumentDetails(PartyIdentificationInformationIndType partyIdentification, String documentNumber, Boolean original,
                                              PartyIdentificationInformationIndTypeType idType, String state, String issueDate, String expiryDate) {
        assertEquals(partyIdentification.getIdentificationNumber(), documentNumber);
        IdentificationIndDocumentType identificationDocument = partyIdentification.getIdentificationDocument();
        if (original) {
            assertEquals(identificationDocument.getDocumentType(), DocumentType.ORIGINAL);
        } else {
            assertEquals(identificationDocument.getDocumentType(), DocumentType.CERTIFIED_COPY);
        }
        assertEquals(identificationDocument.getIdentificationType(), idType);
        assertEquals(identificationDocument.getPlaceOfIssue().getAddress().getState(), state);

        assertEquals(partyIdentification.getValidityPeriod().getStartDate(), XMLGregorianCalendarUtil.date(issueDate, "dd/MM/yyyy"));
        assertEquals(partyIdentification.getValidityPeriod().getEndDate(), XMLGregorianCalendarUtil.date(expiryDate, "dd/MM/yyyy"));
    }

    @Test
    public void shouldBuildIDInformationForNonPhotoDocuments_BirthCert_FinancialBenefits() throws Exception {
        String ausDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"nonphotodocuments\": {\n" +
                "    \"birthcertificate\": {\n" +
                "      \"documentIssuer\": \"ACT\",\n" +
                "      \"issueDate\": \"12/12/2000\",\n" +
                "      \"documentNumber\": \"123123\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\"\n" +
                "    },\n" +
                "    \"financialbenefits\": {\n" +
                "      \"documentIssuer\": \"ACT\",\n" +
                "      \"issueDate\": \"12/12/2013\",\n" +
                "      \"expiryDate\": null,\n" +
                "      \"documentNumber\": \"123456\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(ausDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(2));
        validateNonPhotoIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "123123", true,
                PartyIdentificationInformationIndTypeType.BIRTH_CERTIFICATE, null, "12/12/2000", null, null);
        validateNonPhotoIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(1), "123456", true,
                PartyIdentificationInformationIndTypeType.FINANCIAL_BENEFITS_NOTICE, "ACT", "12/12/2013", null, "ACT");
    }

    @Test
    public void shouldBuildIDInformationForNonPhotoDocuments_Citizenship_ATONotice() throws Exception {
        String ausDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"nonphotodocuments\": {\n" +
                "    \"citizenshipdocument\": {\n" +
                "      \"documentIssuer\": \"Australian Government\",\n" +
                "      \"issueDate\": \"12/12/2013\",\n" +
                "      \"documentNumber\": \"123\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\"\n" +
                "    },\n" +
                "    \"atonotice\": {\n" +
                "      \"documentIssuer\": \"ATO\",\n" +
                "      \"issueDate\": \"12/11/2013\",\n" +
                "      \"documentNumber\": \"1234\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(ausDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(2));
        validateNonPhotoIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "123", true,
                PartyIdentificationInformationIndTypeType.CITIZENSHIP_CERTIFICATE, null, "12/12/2013", null, "Australian Government");
        validateNonPhotoIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(1), "1234", true,
                PartyIdentificationInformationIndTypeType.TAXATION_NOTICE, null, "12/11/2013", null, "ATO");
    }

    @Test
    public void shouldBuildIDInformationForNonPhotoDocuments_PensionCard_UtilitiesNotice() throws Exception {
        String ausDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"nonphotodocuments\": {\n" +
                "    \"healthcard\": {\n" +
                "      \"documentIssuer\": \"Centrelink\",\n" +
                "      \"issueDate\": \"12/12/2013\",\n" +
                "      \"expiryDate\": \"12/12/2015\",\n" +
                "      \"documentNumber\": \"G1234\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\"\n" +
                "    },\n" +
                "    \"utilitiesnotice\": {\n" +
                "      \"documentIssuer\": \"AGL\",\n" +
                "      \"issueDate\": \"12/09/2014\",\n" +
                "      \"documentNumber\": \"123456\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"Not Applicable\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(ausDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(2));
        validateNonPhotoIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "G1234", true,
                PartyIdentificationInformationIndTypeType.HEALTH_CARD, null, "12/12/2013", "12/12/2015", "Centrelink");
        validateNonPhotoIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(1), "123456", true,
                PartyIdentificationInformationIndTypeType.UTILITIES_NOTICE, null, "12/09/2014", null, "AGL");
    }

    private void validateNonPhotoIdDocumentDetails(PartyIdentificationInformationIndType partyIdentification, String documentNumber, Boolean original,
                                                   PartyIdentificationInformationIndTypeType idType, String state, String issueDate, String expiryDate, String issuerName) {
        assertEquals(partyIdentification.getIdentificationNumber(), documentNumber);
        IdentificationIndDocumentType identificationDocument = partyIdentification.getIdentificationDocument();
        if (original) {
            assertEquals(identificationDocument.getDocumentType(), DocumentType.ORIGINAL);
        } else {
            assertEquals(identificationDocument.getDocumentType(), DocumentType.CERTIFIED_COPY);
        }
        assertEquals(identificationDocument.getIdentificationType(), idType);
        assertEquals(identificationDocument.getPlaceOfIssue().getAddress().getState(), state);

        assertFalse(identificationDocument.isAccreditedEnglishTranslation());
        assertEquals(identificationDocument.getPlaceOfIssue().getAddress().getCountryCode(), "AU");
        assertEquals(identificationDocument.getIssuerName(), issuerName);

        assertEquals(partyIdentification.getValidityPeriod().getStartDate(), XMLGregorianCalendarUtil.date(issueDate, "dd/MM/yyyy"));
        assertEquals(partyIdentification.getValidityPeriod().getEndDate(), XMLGregorianCalendarUtil.date(expiryDate, "dd/MM/yyyy"));
    }
    @Test
    public void shouldBuildIDInformationForIntlDrivingLicense() throws Exception {
        String intlDrivingLicense = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"internationaldocuments\": {\n" +
                "    \"driverlicence\": {\n" +
                "      \"documentNumber\": \"1234\",\n" +
                "      \"documentIssuer\": \"Ind\",\n" +
                "      \"issueDate\": \"12/12/2010\",\n" +
                "      \"expiryDate\": \"12/12/2020\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"accreditedenglishtrans\": \"notapplicable\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(intlDrivingLicense, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(1));
        validateIntlIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "1234", true,
                PartyIdentificationInformationIndTypeType.DRIVERS_LICENSE, "IND", "12/12/2010", "12/12/2020", false);
    }

    @Test
    public void shouldBuildIDInformationForIntlPassport() throws Exception {
        String intlPassport = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"internationaldocuments\": {\n" +
                "    \"passport\": {\n" +
                "      \"documentIssuer\": \"Ind\",\n" +
                "      \"issueDate\": \"12/12/2012\",\n" +
                "      \"expiryDate\": \"12/12/2022\",\n" +
                "      \"documentNumber\": \"123\",\n" +
                "      \"verificationSource\": \"original\",\n" +
                "      \"englishTranslation\": \"notapplicable\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(intlPassport, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(1));
        validateIntlIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "123", true,
                PartyIdentificationInformationIndTypeType.PASSPORT, "IND", "12/12/2012", "12/12/2022", false);
    }
    @Test
    public void shouldBuildIDInformationForForeignNationalId() throws Exception {
        String foreignNationId = "{\n" +
                "  \"identitydocument\": {\n" +
                "  \"internationaldocuments\": {\n" +
                "    \"nationalid\": {\n" +
                "      \"documentIssuer\": \"Ind\",\n" +
                "      \"documentNumber\": \"12345\",\n" +
                "      \"issueDate\": \"12/12/2012\",\n" +
                "      \"expiryDate\": null,\n" +
                "      \"verificationSource\": \"certifiedcopy\",\n" +
                "      \"englishTranslation\": \"sighted\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> idDetailsMap = mapper.readValue(foreignNationId, new TypeReference<Map<String, Object>>() {
        });
        PartyIdentificationInformationsIndType partyIdentificationInformation = partyIdentificationInformationBuilder.getPartyIdentificationInformation(ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(idDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(partyIdentificationInformation);
        assertThat(partyIdentificationInformation.getPartyIdentificationInformation(), hasSize(1));
        validateIntlIdDocumentDetails(partyIdentificationInformation.getPartyIdentificationInformation().get(0), "12345", false,
                PartyIdentificationInformationIndTypeType.IDENTIFICATION_CARD, "IND", "12/12/2012", null, true);
    }
}
