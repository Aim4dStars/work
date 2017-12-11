package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.beneficiary.AccountBeneficiaryDetailsResponseImpl;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsImpl;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsIntegrationService;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsIntegrationServiceFactoryImpl;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsResponseHolderImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.code.FieldImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * BeneficiaryDtoSevice test cases
 */
@RunWith(MockitoJUnitRunner.class)
public class BeneficiaryDtoServiceImplTest {
    @InjectMocks
    private BeneficiaryDtoServiceImpl beneficiaryDtoService;

    @Mock
    private BeneficiaryDetailsIntegrationService beneficiaryDetailsIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private BeneficiaryDetailsIntegrationServiceFactoryImpl beneficiaryDetailsIntegrationServiceFactory;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Before
    public void setup() {

        //GENDER CodeCategory

        CodeImpl codeMale = new CodeImpl("1", "M", "Male", "male");
        CodeImpl codeFemale = new CodeImpl("2", "F", "Female", "female");
        CodeImpl codeOther = new CodeImpl("101", "X", "Unspecified", "btfg$unspec");
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.GENDER), eq("male"), any(ServiceErrorsImpl.class))).
                thenReturn(codeMale);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.GENDER), eq("female"), any(ServiceErrorsImpl.class))).
                thenReturn(codeFemale);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.GENDER), eq("btfg$unspec"), any(ServiceErrorsImpl.class))).
                thenReturn(codeOther);

        UserProfile userProfile = Mockito.mock(UserProfile.class);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);

        //Support staff with one adviser
        Collection<BrokerIdentifier> brokers = new ArrayList<>();
        BrokerAnnotationHolder brokerAnnotationHolder = new BrokerAnnotationHolder();
        brokerAnnotationHolder.setBrokerId("45678");
        brokers.add(brokerAnnotationHolder);

        when(brokerIntegrationService.getAdvisersForUser((UserProfile) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(brokers);
        when(beneficiaryDetailsIntegrationServiceFactory.getInstance(Matchers.anyString())).thenReturn(beneficiaryDetailsIntegrationService);
        when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getAccountBeneficiaries());
        when(beneficiaryDetailsIntegrationService.getBeneficiaryDetails(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(getBeneficiaries());
        when(staticIntegrationService.loadCodes(Mockito.eq(CodeCategory.SUPER_NOMINATION_TYPE), Mockito.any(ServiceErrors.class))).thenReturn(getNominationCodelist());
        when(staticIntegrationService.loadCodes(Mockito.eq(CodeCategory.SUPER_RELATIONSHIP_TYPE), Mockito.any(ServiceErrors.class))).thenReturn(getRelationshipTypelist());

    }

    private Collection<Code> getNominationCodelist() {
        Collection<Code> typeCodeList = new ArrayList<>();

        Code code1 = new CodeImpl("4", "NOMN_BIND_NLAPS_TRUSTD", "Australian Superannuation Death Benefits Nomination - Binding - Non Lapsing (Trust Deed)", "nomn_bind_nlaps_trustd");
        ((CodeImpl) code1).addField("btfg$ui_name", "Trustee discretion");
        ((CodeImpl) code1).addField("btfg$ui_dep_only", "");
        ((CodeImpl) code1).addField("btfg$ui_pct", "");
        ((CodeImpl) code1).addField("btfg$ui_super_acc_type", "super,pension");
        Code code2 = new CodeImpl("2", "NOMN_NBIND_SIS", "Australian Superannuation Death Benefits Nomination - Non Binding (SIS)", "nomn_nbind_sis");
        ((CodeImpl) code2).addField("btfg$ui_name", "Non-lapsing nomination");
        ((CodeImpl) code2).addField("btfg$ui_dep_only", "");
        ((CodeImpl) code2).addField("btfg$ui_pct", "");
        ((CodeImpl) code2).addField("btfg$ui_super_acc_type", "super,pension");
        Code code3 = new CodeImpl("21", "NOMN_AUTO_REVSNRY", "Australian Superannuation Death Benefits Nomination - Automatic Reversionary", "nomn_auto_revsnry");
        ((CodeImpl) code3).addField("btfg$ui_name", "Auto reversionary");
        ((CodeImpl) code3).addField("btfg$ui_dep_only", "+");
        ((CodeImpl) code3).addField("btfg$ui_pct", "100");
        ((CodeImpl) code3).addField("btfg$ui_super_acc_type", "pension");
        typeCodeList.add(code1);
        typeCodeList.add(code2);
        typeCodeList.add(code3);
        return typeCodeList;

    }

    private Collection<Code> getRelationshipTypelist() {
        Collection<Code> typeCodeList = new ArrayList<>();
        //Id, userId, name, intlId
        Code code1 = new CodeImpl("2", "CHILD", "Child", "child");
        ((CodeImpl) code1).addField("btfg$ui_dep", "+");
        Code code2 = new CodeImpl("3", "FIN_DEP", "Financial dependant", "fin_dep");
        ((CodeImpl) code2).addField("btfg$ui_dep", "+");
        Code code3 = new CodeImpl("4", "INTERDEPENDENT", "Interdependent", "interdependent");
        ((CodeImpl) code3).addField("btfg$ui_dep", "+");
        Code code4 = new CodeImpl("5", "LPR", "Legal personal representative", "lpr");
        ((CodeImpl) code4).addField("btfg$ui_dep", "+");
        Code code5 = new CodeImpl("1", "SPOUSE", "Spouse", "spouse");
        ((CodeImpl) code5).addField("btfg$ui_dep", "+");
        typeCodeList.add(code1);
        typeCodeList.add(code2);
        typeCodeList.add(code3);
        typeCodeList.add(code4);
        typeCodeList.add(code5);
        return typeCodeList;
    }

    private BeneficiaryDetailsResponseHolderImpl getAccountBeneficiaries() {
        BeneficiaryDetailsResponseHolderImpl beneficiaries = new BeneficiaryDetailsResponseHolderImpl();
        List<AccountBeneficiaryDetailsResponseImpl> accountBeneficiaryDetails = new ArrayList<>();

        //First account, two beneficiaries
        AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail1 = new AccountBeneficiaryDetailsResponseImpl();
        accountBeneficiaryDetail1.setAccountKey(AccountKey.valueOf("427576"));
        String dateTime = "2016-05-11T00:00:00+10:00";
        accountBeneficiaryDetail1.setBeneficiariesLastUpdatedTime(ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime).toDateTime());
        BeneficiaryDetailsImpl beneficiaryDetail1 = new BeneficiaryDetailsImpl();
        beneficiaryDetail1.setFirstName("Samantha");
        beneficiaryDetail1.setLastName("Super");
        beneficiaryDetail1.setDateOfBirth(new DateTime(1970, 02, 03, 0, 0));//null
        beneficiaryDetail1.setGender(Gender.FEMALE);
        beneficiaryDetail1.setNominationType(getNominationCodeTrustDeed());
        beneficiaryDetail1.setRelationshipType(RelationshipType.LPR);
        beneficiaryDetail1.setEmail("test@test.com");
        beneficiaryDetail1.setAllocationPercent(new BigDecimal("65"));
        beneficiaryDetail1.setPhoneNumber("0410274222");
        BeneficiaryDetailsImpl beneficiaryDetail2 = new BeneficiaryDetailsImpl();
        beneficiaryDetail2.setFirstName("Jess");
        beneficiaryDetail2.setLastName("Super");
        beneficiaryDetail2.setDateOfBirth(new DateTime(1970, 02, 03, 0, 0)); //null?
        beneficiaryDetail2.setGender(Gender.FEMALE);
        beneficiaryDetail2.setNominationType(getNominationCodeNonLapse());
        beneficiaryDetail2.setRelationshipType(RelationshipType.FINANCIAL_DEPENDENT);
        beneficiaryDetail2.setEmail("jess@test.com");
        beneficiaryDetail2.setAllocationPercent(new BigDecimal("35"));
        beneficiaryDetail2.setPhoneNumber("0456789456");

        List<BeneficiaryDetails> beneficiaryDetails = new ArrayList<>();
        beneficiaryDetails.add(beneficiaryDetail1);
        beneficiaryDetails.add(beneficiaryDetail2);
        accountBeneficiaryDetail1.setBeneficiaryDetails(beneficiaryDetails);
        accountBeneficiaryDetails.add(accountBeneficiaryDetail1);
        beneficiaries.setBeneficiaryDetailsList(accountBeneficiaryDetails);

        return beneficiaries;

    }

    private BeneficiaryDetailsResponseHolderImpl getBeneficiaries() {
        BeneficiaryDetailsResponseHolderImpl beneficiaries = new BeneficiaryDetailsResponseHolderImpl();
        List<AccountBeneficiaryDetailsResponseImpl> accountBeneficiaryDetails = new ArrayList<>();

        //First account, two beneficiaries
        AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail1 = new AccountBeneficiaryDetailsResponseImpl();
        accountBeneficiaryDetail1.setAccountKey(AccountKey.valueOf("507185"));
        String dateTime = "2016-05-11T00:00:00+10:00";
        accountBeneficiaryDetail1.setBeneficiariesLastUpdatedTime(ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime).toDateTime());
        BeneficiaryDetailsImpl beneficiaryDetail1 = new BeneficiaryDetailsImpl();
        beneficiaryDetail1.setFirstName("Samantha");
        beneficiaryDetail1.setLastName("Super");
        beneficiaryDetail1.setDateOfBirth(new DateTime(1970, 02, 03, 0, 0));//null
        beneficiaryDetail1.setGender(Gender.FEMALE);
        beneficiaryDetail1.setNominationType(getNominationCodeTrustDeed());
        beneficiaryDetail1.setRelationshipType(RelationshipType.CHILD);
        beneficiaryDetail1.setEmail("test@test.com");
        beneficiaryDetail1.setAllocationPercent(new BigDecimal("65"));
        beneficiaryDetail1.setPhoneNumber("0410274222");
        BeneficiaryDetailsImpl beneficiaryDetail2 = new BeneficiaryDetailsImpl();
        beneficiaryDetail2.setFirstName("Jess");
        beneficiaryDetail2.setLastName("Super");
        beneficiaryDetail2.setDateOfBirth(new DateTime(1970, 02, 03, 0, 0)); //null?
        beneficiaryDetail2.setGender(Gender.FEMALE);
        beneficiaryDetail2.setNominationType(getNominationCodeNonLapse());
        beneficiaryDetail2.setRelationshipType(RelationshipType.SPOUSE);
        beneficiaryDetail2.setEmail("jess@test.com");
        beneficiaryDetail2.setAllocationPercent(new BigDecimal("35"));
        beneficiaryDetail2.setPhoneNumber("0456789456");

        List<BeneficiaryDetails> beneficiaryDetails = new ArrayList<>();
        beneficiaryDetails.add(beneficiaryDetail1);
        beneficiaryDetails.add(beneficiaryDetail2);
        accountBeneficiaryDetail1.setBeneficiaryDetails(beneficiaryDetails);

        //Second Account Zero beneficiary
        AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail2 = new AccountBeneficiaryDetailsResponseImpl();
        accountBeneficiaryDetail2.setAccountKey(AccountKey.valueOf("337832"));
        accountBeneficiaryDetail2.setBeneficiariesLastUpdatedTime(null);
        List<BeneficiaryDetails> beneficiaryDetails2 = new ArrayList<>();
        accountBeneficiaryDetail2.setBeneficiaryDetails(beneficiaryDetails2);

        //Third account, One beneficiary
        AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail3 = new AccountBeneficiaryDetailsResponseImpl();
        accountBeneficiaryDetail3.setAccountKey(AccountKey.valueOf("507186"));
        String dateTime3 = "2016-05-11T00:00:00+10:00";
        accountBeneficiaryDetail3.setBeneficiariesLastUpdatedTime(ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime3).toDateTime());
        BeneficiaryDetailsImpl beneficiaryDetail3 = new BeneficiaryDetailsImpl();
        beneficiaryDetail3.setFirstName("Dennis");
        beneficiaryDetail3.setLastName("Beecham");
        beneficiaryDetail3.setDateOfBirth(new DateTime(1970, 02, 03, 0, 0)); //1991-04-03T00:00:00.000+10:00
        beneficiaryDetail3.setGender(Gender.MALE);
        beneficiaryDetail3.setNominationType(getNominationCodeAutoRevsnry());
        beneficiaryDetail3.setRelationshipType(RelationshipType.SPOUSE);
        beneficiaryDetail3.setEmail("dennis@test.com");
        beneficiaryDetail3.setAllocationPercent(new BigDecimal("100"));
        beneficiaryDetail3.setPhoneNumber("0423456789");
        List<BeneficiaryDetails> beneficiaryDetails3 = new ArrayList<>();
        beneficiaryDetails3.add(beneficiaryDetail3);
        accountBeneficiaryDetail3.setBeneficiaryDetails(beneficiaryDetails3);
        //

        accountBeneficiaryDetails.add(accountBeneficiaryDetail1);
        accountBeneficiaryDetails.add(accountBeneficiaryDetail2);
        accountBeneficiaryDetails.add(accountBeneficiaryDetail3);
        beneficiaries.setBeneficiaryDetailsList(accountBeneficiaryDetails);

        return beneficiaries;
    }

    private Code getNominationCodeTrustDeed() {
        return new Code() {
            private List<Field> fields;

            @Override
            public String getCodeId() {
                return "4";
            }

            @Override
            public String getUserId() {
                return "NOMN_BIND_NLAPS_TRUSTD";
            }

            @Override
            public String getName() {
                return "Australian Superannuation Death Benefits Nomination - Binding - Non Lapsing (Trust Deed)";
            }

            @Override
            public String getIntlId() {
                return "nomn_bind_nlaps_trustd";
            }

            @Override
            public String getCategory() {
                return "btfg$au_sa_death_benf";
            }

            @Override
            public Collection<Field> getFields() {
                FieldImpl field1 = new FieldImpl("btfg$ui_name", "Trustee discretion");
                FieldImpl field2 = new FieldImpl("btfg$ui_pct", null);
                FieldImpl field3 = new FieldImpl("btfg$ui_dep_only", null);
                FieldImpl field4 = new FieldImpl("btfg$ui_super_acc_type", "super,pension");
                fields = new ArrayList<>();
                fields.add(field1);
                fields.add(field2);
                fields.add(field3);
                fields.add(field4);
                return fields;
            }

            @Override
            public Field getField(String s) {
                for (Field field : getFields()) {
                    if (field.getName().equals(s)) {
                        return field;
                    }
                }
                return null;
            }
        };
    }

    private Code getNominationCodeAutoRevsnry() {
        return new Code() {
            private List<Field> fields;

            @Override
            public String getCodeId() {
                return "21";
            }

            @Override
            public String getUserId() {
                return "NOMN_AUTO_REVSNRY";
            }

            @Override
            public String getName() {
                return "Australian Superannuation Death Benefits Nomination - Automatic Reversionary";
            }

            @Override
            public String getIntlId() {
                return "nomn_auto_revsnry";
            }

            @Override
            public String getCategory() {
                return "btfg$au_sa_death_benf";
            }

            @Override
            public Collection<Field> getFields() {
                FieldImpl field1 = new FieldImpl("btfg$ui_name", "Auto reversionary");
                FieldImpl field2 = new FieldImpl("btfg$ui_pct", "100");
                FieldImpl field3 = new FieldImpl("btfg$ui_dep_only", null);
                FieldImpl field4 = new FieldImpl("btfg$ui_super_acc_type", "pension");
                fields = new ArrayList<>();
                fields.add(field1);
                fields.add(field2);
                fields.add(field3);
                fields.add(field4);
                return fields;
            }

            @Override
            public Field getField(String s) {
                for (Field field : getFields()) {
                    if (field.getName().equals(s)) {
                        return field;
                    }
                }
                return null;
            }
        };
    }

    private Code getNominationCodeNonLapse() {
        return new Code() {
            private List<Field> fields;

            @Override
            public String getCodeId() {
                return "2";
            }

            @Override
            public String getUserId() {
                return "NOMN_NBIND_SIS";
            }

            @Override
            public String getName() {
                return "Australian Superannuation Death Benefits Nomination - Non Binding (SIS)";
            }

            @Override
            public String getIntlId() {
                return "nomn_nbind_sis";
            }

            @Override
            public String getCategory() {
                return "btfg$au_sa_death_benf";
            }

            @Override
            public Collection<Field> getFields() {
                FieldImpl field1 = new FieldImpl("btfg$ui_name", "Non-lapsing nomination");
                FieldImpl field2 = new FieldImpl("btfg$ui_pct", null);
                FieldImpl field3 = new FieldImpl("btfg$ui_dep_only", null);
                FieldImpl field4 = new FieldImpl("btfg$ui_super_acc_type", "super,pension");
                fields = new ArrayList<>();
                fields.add(field1);
                fields.add(field2);
                fields.add(field3);
                fields.add(field4);
                return fields;
            }

            @Override
            public Field getField(String s) {
                for (Field field : getFields()) {
                    if (field.getName().equals(s)) {
                        return field;
                    }
                }
                return null;
            }
        };
    }

    @Test
    public void testSearchAccountBeneficiary() {

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        searchCriterias.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, "427576"));
        List<BeneficiaryDto> beneficiaryDtoList = beneficiaryDtoService.search(searchCriterias, new FailFastErrorsImpl());
        assertNotNull(beneficiaryDtoList);
        assertEquals(beneficiaryDtoList.size(), 1);

        BeneficiaryDto beneficiaryDto = beneficiaryDtoList.get(0);
        String dateTime = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("427576").toString());
        assertEquals(beneficiaryDto.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime).toDateTime());
        assertEquals(beneficiaryDto.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto.getTotalBeneficiaries(), "2");
        assertEquals(beneficiaryDto.getBeneficiaries().size(), 2);

        Beneficiary beneficiary = beneficiaryDto.getBeneficiaries().get(0);
        assertEquals(beneficiary.getNominationType(), "nomn_bind_nlaps_trustd");
        assertEquals(beneficiary.getAllocationPercent(), "65.00");
        assertEquals(beneficiary.getRelationshipType(), "lpr");
        assertEquals(beneficiary.getFirstName(), "Samantha");
        assertEquals(beneficiary.getLastName(), "Super");
        assertEquals(beneficiary.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary.getGender(), "female");
        assertEquals(beneficiary.getPhoneNumber(), "0410274222");
        assertEquals(beneficiary.getEmail(), "test@test.com");

        Beneficiary beneficiary2 = beneficiaryDto.getBeneficiaries().get(1);
        assertEquals(beneficiary2.getNominationType(), "nomn_nbind_sis");
        assertEquals(beneficiary2.getAllocationPercent(), "35.00");
        assertEquals(beneficiary2.getRelationshipType(), "fin_dep");
        assertEquals(beneficiary2.getFirstName(), "Jess");
        assertEquals(beneficiary2.getLastName(), "Super");
        assertEquals(beneficiary2.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary2.getGender(), "female");
        assertEquals(beneficiary2.getPhoneNumber(), "0456789456");
        assertEquals(beneficiary2.getEmail(), "jess@test.com");
    }

    @Test
    public void testSearchAdviserBeneficiaries() {

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        when(userProfileService.isAdviser()).thenReturn(true);
        Mockito.when(userProfileService.getPositionId()).thenReturn("12345");
        List<BeneficiaryDto> beneficiaryDtoList = beneficiaryDtoService.search(searchCriterias, new FailFastErrorsImpl());
        assertNotNull(beneficiaryDtoList);
        assertEquals(beneficiaryDtoList.size(), 3);

        BeneficiaryDto beneficiaryDto = beneficiaryDtoList.get(0);
        String dateTime = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("507185").toString());
        assertEquals(beneficiaryDto.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime).toDateTime());
        assertEquals(beneficiaryDto.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto.getTotalBeneficiaries(), "2");
        assertEquals(beneficiaryDto.getBeneficiaries().size(), 2);

        Beneficiary beneficiary = beneficiaryDto.getBeneficiaries().get(0);
        assertEquals(beneficiary.getNominationType(), "Trustee discretion");
        assertEquals(beneficiary.getAllocationPercent(), "65.00");
        assertEquals(beneficiary.getRelationshipType(), "Child");
        assertEquals(beneficiary.getFirstName(), "Samantha");
        assertEquals(beneficiary.getLastName(), "Super");
        assertEquals(beneficiary.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary.getGender(), "Female");
        assertEquals(beneficiary.getPhoneNumber(), "0410274222");
        assertEquals(beneficiary.getEmail(), "test@test.com");

        Beneficiary beneficiary2 = beneficiaryDto.getBeneficiaries().get(1);
        assertEquals(beneficiary2.getNominationType(), "Non-lapsing nomination");
        assertEquals(beneficiary2.getAllocationPercent(), "35.00");
        assertEquals(beneficiary2.getRelationshipType(), "Spouse");
        assertEquals(beneficiary2.getFirstName(), "Jess");
        assertEquals(beneficiary2.getLastName(), "Super");
        assertEquals(beneficiary2.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary2.getGender(), "Female");
        assertEquals(beneficiary2.getPhoneNumber(), "0456789456");
        assertEquals(beneficiary2.getEmail(), "jess@test.com");

        BeneficiaryDto beneficiaryDto2 = beneficiaryDtoList.get(1);
        assertEquals(beneficiaryDto2.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("337832").toString());
        assertNull(beneficiaryDto2.getTotalBeneficiaries());

        BeneficiaryDto beneficiaryDto3 = beneficiaryDtoList.get(2);
        assertEquals(beneficiaryDto3.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("507186").toString());
        String dateTime3 = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto3.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime3).toDateTime());
        assertEquals(beneficiaryDto3.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto3.getBeneficiaries().size(), 1);

        Beneficiary beneficiary31 = beneficiaryDto3.getBeneficiaries().get(0);
        assertEquals(beneficiary31.getNominationType(), "Auto reversionary");
        assertEquals(beneficiary31.getAllocationPercent(), "100.00");
        assertEquals(beneficiary31.getRelationshipType(), "Spouse");
        assertEquals(beneficiary31.getFirstName(), "Dennis");
        assertEquals(beneficiary31.getLastName(), "Beecham");
        assertEquals(beneficiary31.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary31.getGender(), "Male");
        assertEquals(beneficiary31.getPhoneNumber(), "0423456789");
        assertEquals(beneficiary31.getEmail(), "dennis@test.com");

    }

    @Test
    public void testSearchSupportStaffBeneficiaries() {

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        searchCriterias.add(new ApiSearchCriteria(Attribute.BROKER_ID, ConsistentEncodedString.fromPlainText("45678").toString()));
        when(userProfileService.isAdviser()).thenReturn(false);
        Mockito.when(userProfileService.getPositionId()).thenReturn("12345");
        List<BeneficiaryDto> beneficiaryDtoList = beneficiaryDtoService.search(searchCriterias, new FailFastErrorsImpl());
        assertNotNull(beneficiaryDtoList);
        assertEquals(beneficiaryDtoList.size(), 3);

        BeneficiaryDto beneficiaryDto = beneficiaryDtoList.get(0);
        String dateTime = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("507185").toString());
        assertEquals(beneficiaryDto.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime).toDateTime());
        assertEquals(beneficiaryDto.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto.getTotalBeneficiaries(), "2");
        assertEquals(beneficiaryDto.getBeneficiaries().size(), 2);

        Beneficiary beneficiary = beneficiaryDto.getBeneficiaries().get(0);
        assertEquals(beneficiary.getNominationType(), "Trustee discretion");
        assertEquals(beneficiary.getAllocationPercent(), "65.00");
        assertEquals(beneficiary.getRelationshipType(), "Child");
        assertEquals(beneficiary.getFirstName(), "Samantha");
        assertEquals(beneficiary.getLastName(), "Super");
        assertEquals(beneficiary.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary.getGender(), "Female");
        assertEquals(beneficiary.getPhoneNumber(), "0410274222");
        assertEquals(beneficiary.getEmail(), "test@test.com");

        Beneficiary beneficiary2 = beneficiaryDto.getBeneficiaries().get(1);
        assertEquals(beneficiary2.getNominationType(), "Non-lapsing nomination");
        assertEquals(beneficiary2.getAllocationPercent(), "35.00");
        assertEquals(beneficiary2.getRelationshipType(), "Spouse");
        assertEquals(beneficiary2.getFirstName(), "Jess");
        assertEquals(beneficiary2.getLastName(), "Super");
        assertEquals(beneficiary2.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary2.getGender(), "Female");
        assertEquals(beneficiary2.getPhoneNumber(), "0456789456");
        assertEquals(beneficiary2.getEmail(), "jess@test.com");

        BeneficiaryDto beneficiaryDto2 = beneficiaryDtoList.get(1);
        assertEquals(beneficiaryDto2.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("337832").toString());
        assertNull(beneficiaryDto2.getTotalBeneficiaries());

        BeneficiaryDto beneficiaryDto3 = beneficiaryDtoList.get(2);
        assertEquals(beneficiaryDto3.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("507186").toString());
        String dateTime3 = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto3.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime3).toDateTime());
        assertEquals(beneficiaryDto3.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto3.getBeneficiaries().size(), 1);

        Beneficiary beneficiary31 = beneficiaryDto3.getBeneficiaries().get(0);
        assertEquals(beneficiary31.getNominationType(), "Auto reversionary");
        assertEquals(beneficiary31.getAllocationPercent(), "100.00");
        assertEquals(beneficiary31.getRelationshipType(), "Spouse");
        assertEquals(beneficiary31.getFirstName(), "Dennis");
        assertEquals(beneficiary31.getLastName(), "Beecham");
        assertEquals(beneficiary31.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary31.getGender(), "Male");
        assertEquals(beneficiary31.getPhoneNumber(), "0423456789");
        assertEquals(beneficiary31.getEmail(), "dennis@test.com");


    }

    @Test
    public void testSearchSupportStaffOneAdviserBeneficiaries() {

        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        when(userProfileService.isAdviser()).thenReturn(false);
        List<BeneficiaryDto> beneficiaryDtoList = beneficiaryDtoService.search(searchCriterias, new FailFastErrorsImpl());
        assertNotNull(beneficiaryDtoList);
        assertEquals(beneficiaryDtoList.size(), 3);

        BeneficiaryDto beneficiaryDto = beneficiaryDtoList.get(0);
        String dateTime = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("507185").toString());
        assertEquals(beneficiaryDto.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime).toDateTime());
        assertEquals(beneficiaryDto.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto.getTotalBeneficiaries(), "2");
        assertEquals(beneficiaryDto.getBeneficiaries().size(), 2);

        Beneficiary beneficiary = beneficiaryDto.getBeneficiaries().get(0);
        assertEquals(beneficiary.getNominationType(), "Trustee discretion");
        assertEquals(beneficiary.getAllocationPercent(), "65.00");
        assertEquals(beneficiary.getRelationshipType(), "Child");
        assertEquals(beneficiary.getFirstName(), "Samantha");
        assertEquals(beneficiary.getLastName(), "Super");
        assertEquals(beneficiary.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary.getGender(), "Female");
        assertEquals(beneficiary.getPhoneNumber(), "0410274222");
        assertEquals(beneficiary.getEmail(), "test@test.com");

        Beneficiary beneficiary2 = beneficiaryDto.getBeneficiaries().get(1);
        assertEquals(beneficiary2.getNominationType(), "Non-lapsing nomination");
        assertEquals(beneficiary2.getAllocationPercent(), "35.00");
        assertEquals(beneficiary2.getRelationshipType(), "Spouse");
        assertEquals(beneficiary2.getFirstName(), "Jess");
        assertEquals(beneficiary2.getLastName(), "Super");
        assertEquals(beneficiary2.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary2.getGender(), "Female");
        assertEquals(beneficiary2.getPhoneNumber(), "0456789456");
        assertEquals(beneficiary2.getEmail(), "jess@test.com");

        BeneficiaryDto beneficiaryDto2 = beneficiaryDtoList.get(1);
        assertEquals(beneficiaryDto2.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("337832").toString());
        assertNull(beneficiaryDto2.getTotalBeneficiaries());

        BeneficiaryDto beneficiaryDto3 = beneficiaryDtoList.get(2);
        assertEquals(beneficiaryDto3.getKey().getAccountId(), ConsistentEncodedString.fromPlainText("507186").toString());
        String dateTime3 = "2016-05-11T00:00:00+10:00";
        assertEquals(beneficiaryDto3.getBeneficiariesLastUpdatedTime(), ISODateTimeFormat.dateTimeNoMillis().parseLocalDateTime(dateTime3).toDateTime());
        assertEquals(beneficiaryDto3.getTotalAllocationPercent(), "100.00");
        assertEquals(beneficiaryDto3.getBeneficiaries().size(), 1);

        Beneficiary beneficiary31 = beneficiaryDto3.getBeneficiaries().get(0);
        assertEquals(beneficiary31.getNominationType(), "Auto reversionary");
        assertEquals(beneficiary31.getAllocationPercent(), "100.00");
        assertEquals(beneficiary31.getRelationshipType(), "Spouse");
        assertEquals(beneficiary31.getFirstName(), "Dennis");
        assertEquals(beneficiary31.getLastName(), "Beecham");
        assertEquals(beneficiary31.getDateOfBirth(), "03 Feb 1970");
        assertEquals(beneficiary31.getGender(), "Male");
        assertEquals(beneficiary31.getPhoneNumber(), "0423456789");
        assertEquals(beneficiary31.getEmail(), "dennis@test.com");
    }

}