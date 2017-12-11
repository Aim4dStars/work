package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.builder.BeneficiaryDtoConverter;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.service.avaloq.beneficiary.AccountBeneficiaryDetailsResponseImpl;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.domain.Gender;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit Test for {@link BeneficiaryDtoConverter}
 * Created by M035995 on 13/07/2016.
 */
public class BeneficiaryDtoConverterTest {
    private static final String NOMINATION_TYPE_EXTL_FLD_NAME_PERCENT = "btfg$ui_pct";

    private List<BeneficiaryDetails> beneficiaryDetailsList;

    private BeneficiaryDtoConverter beneficiaryDtoConverter;

    private AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail;

    @Before
    public void init() {
        beneficiaryDetailsList = new ArrayList<>();

        BeneficiaryDetailsImpl details = new BeneficiaryDetailsImpl();
        details.setAllocationPercent(new BigDecimal(60));
        details.setDateOfBirth(new DateTime(1984, 5, 20, 12, 0, 0, 0));
        details.setEmail("Test@unittest.com");
        details.setFirstName(null);
        details.setLastName(null);
        details.setNominationType(getCode("4", "NOMN_BIND_NLAPS_TRUSTD",
                "Australian Superannuation Death Benefits Nomination - Binding - Non Lapsing (Trust Deed)",
                "nomn_bind_nlaps_trustd", "btfg$au_sa_death_benf", null));
        details.setPhoneNumber("0436598632");
        details.setRelationshipType(RelationshipType.LPR);
        details.setGender(Gender.MALE);
        beneficiaryDetailsList.add(details);

        details = new BeneficiaryDetailsImpl();
        details.setAllocationPercent(new BigDecimal(40));
        details.setDateOfBirth(new DateTime(1984, 3, 26, 12, 0, 0, 0));
        details.setEmail("Test@unittest.com");
        details.setFirstName("Kristy");
        details.setLastName("Schzen");
        details.setNominationType(getCode("2", "NOMN_NBIND_SIS",
                "Australian Superannuation Death Benefits Nomination - Non Binding (SIS)", "nomn_nbind_sis",
                "btfg$au_sa_death_benf", null));
        details.setPhoneNumber("0436598632");
        details.setRelationshipType(RelationshipType.SPOUSE);
        details.setGender(Gender.MALE);
        beneficiaryDetailsList.add(details);

        details = new BeneficiaryDetailsImpl();
        // no allocation percent
        details.setDateOfBirth(new DateTime(1986, 4, 27, 14, 0, 0, 0));
        details.setEmail("Test3@unittest.com");
        details.setFirstName("Kristy3");
        details.setLastName("Schzen3");
        details.setNominationType(getCode("3", "NOM_ID_3",
                "nomination 3", "nom_id_3",
                "btfg$au_sa_death_benf", "100"));
        details.setPhoneNumber("0536598632");
        details.setRelationshipType(RelationshipType.FINANCIAL_DEPENDENT);
        details.setGender(Gender.FEMALE);
        beneficiaryDetailsList.add(details);

        accountBeneficiaryDetail = new AccountBeneficiaryDetailsResponseImpl();
        accountBeneficiaryDetail.setBeneficiariesLastUpdatedTime(new DateTime("2016-05-11"));
        accountBeneficiaryDetail.setAutoReversionaryActivationDate(new DateTime("2017-03-21"));
        accountBeneficiaryDetail.setBeneficiaryDetails(beneficiaryDetailsList);
        accountBeneficiaryDetail.setAccountKey(AccountKey.valueOf("427576"));

        beneficiaryDtoConverter = new BeneficiaryDtoConverter();
    }

    @Test
    public void testGetBeneficiaryDetails() {

        BeneficiaryDto beneficiaryDto = beneficiaryDtoConverter.getBeneficiaryDetails(accountBeneficiaryDetail);

        assertThat("Beneficiary Dto is not null", beneficiaryDto, is(notNullValue()));
        assertThat("Beneficiary - Last Updated Date", beneficiaryDto.getBeneficiariesLastUpdatedTime(),
                is(equalTo(new DateTime("2016-05-11"))));
        assertThat("Beneficiary - Auto reversionary Activation Date", beneficiaryDto.getAutoReversionaryActivationDate(),
                is(equalTo(new DateTime("2017-03-21"))));

        assertThat("Account Id", beneficiaryDto.getKey().getAccountId(), equalTo("FE7D30DE884FA141"));
        assertThat("Number of beneficiaries", beneficiaryDto.getBeneficiaries().size(), equalTo(3));
        assertThat("Total allocation percent:", beneficiaryDto.getTotalAllocationPercent(), is("200.00"));

        Beneficiary beneficiary = beneficiaryDto.getBeneficiaries().get(0);
        assertThat("Beneficiary Details 0 - NominationType", beneficiary.getNominationType(), is(equalTo("nomn_bind_nlaps_trustd")));
        assertThat("Beneficiary Details 0 - AllocationPercent", beneficiary.getAllocationPercent(), is(equalTo("60.00")));
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiary.getRelationshipType(), is(equalTo(RelationshipType.LPR.getAvaloqInternalId())));
        assertThat("Beneficiary Details 0 - FirstName", beneficiary.getFirstName(), isEmptyOrNullString());
        assertThat("Beneficiary Details 0 - LastName", beneficiary.getLastName(), isEmptyOrNullString());
        assertThat("Beneficiary Details 0 - DOB", beneficiary.getDateOfBirth(), is(equalTo("20 May 1984")));
        assertThat("Beneficiary Details 0 - gender", beneficiary.getGender(), equalTo(Gender.MALE.toString()));
        assertThat("Beneficiary Details 0 - phone number", beneficiary.getPhoneNumber(), is(equalTo("0436598632")));
        assertThat("Beneficiary Details 0 - email", beneficiary.getEmail(), is(equalTo("Test@unittest.com")));

        beneficiary = beneficiaryDto.getBeneficiaries().get(1);
        assertThat("Beneficiary Details 1 - NominationType", beneficiary.getNominationType(), is(equalTo("nomn_nbind_sis")));
        assertThat("Beneficiary Details 1 - AllocationPercent", beneficiary.getAllocationPercent(), is(equalTo("40.00")));
        assertThat("Beneficiary Details 1 - RelationshipType", beneficiary.getRelationshipType(),
                is(equalTo(RelationshipType.SPOUSE.getAvaloqInternalId())));
        assertThat("Beneficiary Details 1 - FirstName", beneficiary.getFirstName(), is(equalTo("Kristy")));
        assertThat("Beneficiary Details 1 - LastName", beneficiary.getLastName(), is(equalTo("Schzen")));
        assertThat("Beneficiary Details 1 - DOB", beneficiary.getDateOfBirth(), is(equalTo("26 Mar 1984")));
        assertThat("Beneficiary Details 1 - gender", beneficiary.getGender(), equalTo(Gender.MALE.toString()));
        assertThat("Beneficiary Details 1 - phone number", beneficiary.getPhoneNumber(), is(equalTo("0436598632")));
        assertThat("Beneficiary Details 1 - email", beneficiary.getEmail(), is(equalTo("Test@unittest.com")));

        beneficiary = beneficiaryDto.getBeneficiaries().get(2);
        assertThat("Beneficiary Details 2 - NominationType", beneficiary.getNominationType(), is(equalTo("nom_id_3")));
        assertThat("Beneficiary Details 2 - AllocationPercent", beneficiary.getAllocationPercent(), is(equalTo("100.00")));
        assertThat("Beneficiary Details 2 - RelationshipType", beneficiary.getRelationshipType(),
                is(equalTo(RelationshipType.FINANCIAL_DEPENDENT.getAvaloqInternalId())));
        assertThat("Beneficiary Details 2 - FirstName", beneficiary.getFirstName(), is(equalTo("Kristy3")));
        assertThat("Beneficiary Details 2 - LastName", beneficiary.getLastName(), is(equalTo("Schzen3")));
        assertThat("Beneficiary Details 2 - DOB", beneficiary.getDateOfBirth(), is(equalTo("27 Apr 1986")));
        assertThat("Beneficiary Details 2 - gender", beneficiary.getGender(), equalTo(Gender.FEMALE.toString()));
        assertThat("Beneficiary Details 2 - phone number", beneficiary.getPhoneNumber(), is(equalTo("0536598632")));
        assertThat("Beneficiary Details 2 - email", beneficiary.getEmail(), is(equalTo("Test3@unittest.com")));
    }

    @Test
    public void testGetBeneficiaryDetailsRelationshipTypeNull() {
        accountBeneficiaryDetail.getBeneficiaryDetails().get(0).setRelationshipType(null);
        BeneficiaryDto beneficiaryDto = beneficiaryDtoConverter.getBeneficiaryDetails(accountBeneficiaryDetail);
        Beneficiary beneficiary = beneficiaryDto.getBeneficiaries().get(0);
        assertThat("Beneficiary Details 0 - RelationshipType", beneficiary.getRelationshipType(), is(equalTo(null)));
    }

    private Code getCode(final String codeId, final String userId, final String name, final String intlId,
                         final String category, final String nominationTypePercent) {
        return new Code() {
            final List<Field> fields = new ArrayList<>();

            @Override
            public String getCodeId() {
                return codeId;
            }

            @Override
            public String getUserId() {
                return userId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getIntlId() {
                return intlId;
            }

            @Override
            public String getCategory() {
                return category;
            }

            @Override
            public Collection<Field> getFields() {
                return fields;
            }

            @Override
            public Field getField(String fieldName) {
                if (NOMINATION_TYPE_EXTL_FLD_NAME_PERCENT.equals(fieldName)) {
                    return new Field() {
                        @Override
                        public String getName() {
                            return NOMINATION_TYPE_EXTL_FLD_NAME_PERCENT;
                        }

                        @Override
                        public String getValue() {
                            return nominationTypePercent;
                        }
                    };
                }

                return null;
            }
        };
    }
}
