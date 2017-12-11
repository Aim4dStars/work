package com.bt.nextgen.api.draftaccount.service;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ContactTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.DirectAdviceTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TrustTypeEnum;
import com.bt.nextgen.config.JsonObjectMapper;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by M040398 on 23/08/2016.
 */
public class JsonSchemaHelperServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaHelperServiceImplTest.class);

    private JsonSchemaHelperService schemaHelperService;

    private JsonObjectMapper jsonObjectMapper;

    //this value represents the number of defined enums in v1 schema packge
    private static final int ALL_SCHEMA_ENUM_SIZE = 22;

    @Before
    public void setUp() {
        schemaHelperService = new JsonSchemaHelperServiceImpl();
        jsonObjectMapper = new JsonObjectMapper();
        jsonObjectMapper.init();
    }

    @Test
    public void testFindSchemaEnums() throws JsonProcessingException, ClassNotFoundException {
        Map<String, Object> enums = schemaHelperService.getJsonSchemaEnums().getRoot();
        assertNotNull(enums);
        assertTrue(enums.size() == ALL_SCHEMA_ENUM_SIZE);
        assertTrue(enums.keySet().contains("AccountTypeEnum"));
        assertTrue(enums.keySet().contains("ApprovalTypeEnum"));

        assertTrue(enums.get("ApprovalTypeEnum") instanceof ApprovalTypeEnum[]);
        assertTrue(((ApprovalTypeEnum[])enums.get("ApprovalTypeEnum")).length == 2 );

        assertTrue(enums.keySet().contains("ContactTypeEnum"));
    }


    @Test
    public void testJsonEnumSerialization() throws JsonProcessingException, ClassNotFoundException {
        JsonSchemaEnumsDto dto = schemaHelperService.getJsonSchemaEnums();

        //check number of unique enums
        assertTrue(dto.getRoot().keySet().size() == ALL_SCHEMA_ENUM_SIZE);

        //check if map values are enum arrays
        assertTrue(dto.getRoot().get("TrustTypeEnum").getClass().isArray());
        TrustTypeEnum[] trustTypeArr = (TrustTypeEnum[]) dto.getRoot().get("TrustTypeEnum") ;
        assertTrue(trustTypeArr.length > 0);

        assertTrue(dto.getRoot().get("DirectAdviceTypeEnum").getClass().isArray());
        DirectAdviceTypeEnum[] adviceTypeArr = (DirectAdviceTypeEnum[]) dto.getRoot().get("DirectAdviceTypeEnum") ;
        assertTrue(adviceTypeArr.length > 0);

        assertTrue(dto.getRoot().get("ContactTypeEnum").getClass().isArray());
        ContactTypeEnum[] contactTypeArr = (ContactTypeEnum[]) dto.getRoot().get("ContactTypeEnum") ;
        assertTrue(contactTypeArr.length > 0);

        //check serialized json string for JsonSchemaEnumsDto
        String json = jsonObjectMapper.writeValueAsString(dto);

        //the assertion below will ignore properties order and whitespaces
        assertJsonEquals("{\"TrustTypeEnum\":" +
                "{\"FAMILY\":\"family\"," +
                "\"GOVSUPER\":\"govsuper\"," +
                "\"INVSCHEME\":\"invscheme\"," +
                "\"REGULATED\":\"regulated\"," +
                "\"OTHER\":\"other\"}," +
                "\"IdentificationTypeEnum\":{\"DRIVERLICENCE\":\"driverlicence\",\"NATIONALID\":\"nationalid\",\"AGECARD\":\"agecard\",\"PASSPORT\":\"passport\",\"ATONOTICE\":\"atonotice\",\"BIRTHCERTIFICATE\":\"birthcertificate\",\"CITIZENSHIPDOCUMENT\":\"citizenshipdocument\",\"FINANCIALBENEFITS\":\"financialbenefits\",\"HEALTHCARD\":\"healthcard\",\"PENSIONCARD\":\"pensioncard\",\"UTILITIESNOTICE\":\"utilitiesnotice\"}," +
                "\"DirectAdviceTypeEnum\":{\"NO_ADVICE\":\"NoAdvice\"}," +
                "\"ContactTypeEnum\":{\"EMAIL\":\"email\",\"SECONDARYEMAIL\":\"secondaryemail\",\"MOBILE\":\"mobile\",\"SECONDARYMOBILE\":\"secondarymobile\",\"HOMENUMBER\":\"homenumber\",\"WORKNUMBER\":\"worknumber\",\"OTHERNUMBER\":\"othernumber\",\"__EMPTY__\":\"\"}," +
                "\"GenderTypeEnum\":{\"MALE\":\"male\",\"FEMALE\":\"female\"}," +
                "\"DirectApplicationOriginTypeEnum\":{\"WESTPAC_LIVE\":\"WestpacLive\"}," +
                "\"ApprovalTypeEnum\":{\"ONLINE\":\"online\",\"OFFLINE\":\"offline\"}," +
                "\"TaxOptionTypeEnum\":{\"TAX_FILE_NUMBER_PROVIDED\":\"Tax File Number provided\",\"EXEMPTION_REASON_PROVIDED\":\"Exemption Reason provided\",\"TAX_FILE_NUMBER_OR_EXEMPTION_NOT_PROVIDED\":\"Tax File Number or exemption not provided\",\"WILL_PROVIDE_TAX_FILE_NUMBER_LATER\":\"Will provide Tax File Number later\",\"PENSIONER\":\"pensioner\"}," +
                "\"VerificationTypeEnum\":{\"VERIFIED\":\"Verified\",\"NOT_VERIFIED\":\"Not Verified\"}," +
                "\"ConfirmationTypeEnum\":{\"CONFIRMED\":\"Confirmed\"}," +
                "\"DocVerificationTypeEnum\":{\"ORIGINAL\":\"original\",\"CERTIFIEDCOPY\":\"certifiedcopy\"}," +
                "\"PersonTypeEnum\":{\"SHAREHOLDER\":\"shareholder\",\"MEMBER\":\"member\",\"BENEFICIAL_OWNER\":\"beneficialOwner\",\"BENEFICIARY\":\"beneficiary\",\"BENEFICIARY_AND_BENEFICIAL_OWNER\":\"beneficiaryAndBeneficialOwner\",\"SHAREHOLDER_AND_MEMBER\":\"shareholderAndMember\",\"BENEFICIARY_AND_SHAREHOLDER\":\"beneficiaryAndShareholder\",\"CONTROLLEROFTRUST\":\"controlleroftrust\",\"BENEFICIARY_AND_CONTROLLER\":\"beneficiaryAndController\"}," +
                "\"AccountTypeEnum\":{\"INDIVIDUAL\":\"individual\",\"JOINT\":\"joint\",\"COMPANY\":\"company\",\"INDIVIDUAL_TRUST\":\"individualTrust\",\"CORPORATE_TRUST\":\"corporateTrust\",\"NEW_INDIVIDUAL_SMSF\":\"newIndividualSMSF\",\"NEW_CORPORATE_SMSF\":\"newCorporateSMSF\",\"INDIVIDUAL_SMSF\":\"individualSMSF\",\"CORPORATE_SMSF\":\"corporateSMSF\",\"SUPER_ACCUMULATION\":\"superAccumulation\",\"SUPER_PENSION\":\"superPension\"}," +
                "\"BooleanTypeEnum\":{\"TRUE\":\"true\",\"FALSE\":\"false\"}," +
                "\"TrustDescriptionTypeEnum\":{\"TESTAMENTARY\":\"testamentary\",\"UNITTRUST\":\"unittrust\",\"UNREGMANAGEDINV\":\"unregmanagedinv\",\"OTHER\":\"other\"}," +
                "\"AnswerTypeEnum\":{\"YES\":\"yes\",\"NO\":\"no\",\"__EMPTY__\":\"\"}," +
                "\"DirectAccountTypeEnum\":{\"INDIVIDUAL\":\"individual\",\"SUPER_ACCUMULATION\":\"superAccumulation\",\"SUPER_PENSION\":\"superPension\"}," +
                "\"SettlorTypeEnum\":{\"INDIVIDUAL\":\"individual\",\"ORGANISATION\":\"organisation\"}," +
                "\"PaymentAuthorityEnum\":{\"ALLPAYMENTS\":\"allpayments\",\"LINKEDACCOUNTSONLY\":\"linkedaccountsonly\",\"NOPAYMENTS\":\"nopayments\",\"__EMPTY__\":\"\"}," +
                "\"TinOptionTypeEnum\":{\"TIN_PROVIDED\":\"TIN provided\",\"EXEMPTION_REASON_PROVIDED\":\"Exemption Reason provided\"}," +
                "\"TrustIdentityDocTypeEnum\":{\"TRUSTDEED\":\"trustdeed\",\"LETTERIDV\":\"letteridv\"}," +
                "\"InvestorRoleTypeEnum\": {\"DIRECTOR\":\"director\",\"DIRECTOR_\":\"Director\",\"SECRETARY\":\"secretary\",\"SECRETARY_\":\"Secretary\",\"SIGNATORY\":\"signatory\",\"SIGNATORY_\":\"Signatory\",\"TRUSTEE\":\"trustee\",\"TRUSTEE_\":\"Trustee\"}}", json);
    }
}
