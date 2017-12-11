package com.bt.nextgen.api.draftaccount.builder.v3;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrusteeDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.util.IdInsertion;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import ns.btfin_com.party.v3_0.CustomerIdentifiers;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType;
import ns.btfin_com.party.v3_0.OrganisationType;
import ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType;
import ns.btfin_com.party.v3_0.RegulatedTrustType;
import ns.btfin_com.party.v3_0.SettlorOfTrustPartyDetailType;
import ns.btfin_com.party.v3_0.TFNRegistrationType;
import ns.btfin_com.party.v3_0.TrustDetailsType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfileType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfilesType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorsType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.draftaccount.FormDataConstants.FIELD_CORRELATION_ID;
import static com.bt.nextgen.api.draftaccount.builder.v3.AuthorityTypeMatcher.hasAuthorityType;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.CORPORATE_SMSF;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_DIRECTOR_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SECRETARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_MEMBER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_TRUSTEE_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_TRUSTEE_ROLE;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestorsTypeBuilderTest extends AbstractJsonReaderTest {

    @InjectMocks
    private InvestorsTypeBuilder investorsTypeBuilder;

    @Mock
    private TaxFieldsBuilder taxFieldsBuilder;

    @Mock
    private AddressTypeBuilder addressTypeBuilder;

    @Mock
    private IndividualTypeBuilder individualTypeBuilder;

    @Mock
    private ContactDetailsBuilder contactDetailsBuilder;

    @Mock
    private AuthorityTypeBuilder authorityTypeBuilder;

    @Mock
    private CustomerIdentifiersBuilder customerIdentifiersBuilder;

    @Mock
    private ExistingInvestorTypeBuilder existingInvestorTypeBuilder;

    @Mock
    private OrganisationTypeBuilder organisationTypeBuilder;

    @Mock
    private BrokerUser adviser;

    @Mock
    private IAccountSettingsForm accountSettingsForm;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private Broker dealer;

    @Mock
    private AddressV2CacheService addressV2CacheService;

    private ServiceErrors serviceErrors;

    private String directorJson() {
        return "{" +
                "       \"title\": \"mr\",\n" +
                "        \"firstname\": \"John\",\n" +
                "        \"middlename\": \"Doe\",\n" +
                "        \"lastname\": \"Smith\",\n" +
                "        \"preferredname\": \"Joe\",\n" +
                "        \"dateofbirth\": \"01/01/1980\",\n" +
                "        \"gender\": \"male\"," +
                "        \"preferredcontact\": \"homeNumber\",\n" +
                "        \"homeNumber\": {\n" +
                "            \"value\": \"123456\",\n" +
                "            \"" + FormDataConstants.FIELD_CORRELATION_ID + "\": 4\n" +
                "        },\n" +
                "        \"workNumber\": {\n" +
                "            \"value\": \"0212345678\",\n" +
                "            \"" + FormDataConstants.FIELD_CORRELATION_ID + "\": 6\n" +
                "        }," +
                "\"postaladdress\": {},\n" +
                "\"resaddress\": {}\n}";
    }

    @Before
    public void setupMocks() {
        serviceErrors = new ServiceErrorsImpl();
        when(organisationTypeBuilder.getCompanyAsTrustee(any(IOrganisationForm.class), any(IOrganisationForm.class), any(IAccountSettingsForm.class), any(BrokerUser.class), any(Broker.class), eq(serviceErrors))).thenReturn(new OrganisationType());
        when(organisationTypeBuilder.getOrganisation(any(IOrganisationForm.class), any(IAccountSettingsForm.class), any(BrokerUser.class), any(Broker.class), eq(serviceErrors))).thenReturn(new OrganisationType());
        when(adviser.getCISKey()).thenReturn(CISKey.valueOf("123456789"));
        when(adviser.getFirstName()).thenReturn("Test");
        when(adviser.getLastName()).thenReturn("Adviser");
        when(dealer.getPositionName()).thenReturn("Test Dealer Group");
        //
        when(accountSettingsForm.hasSourceOfFunds()).thenReturn(true);
        when(accountSettingsForm.getSourceOfFunds()).thenReturn("Compensation payment");
    }

    @Test
    public void getAllInvestorsRoles_ShouldReturnPrimayRoleForIndividualApplication() throws Exception {
        String json = "{\n" +
                "  \"accountType\": \"individual\",\n" +
                "  \"investors\": [\n" +
                "    {\n" +
                "      \"displayTaxOptions\": true,\n" +
                "      \"cismandatory\": true,\n" +
                "      \"title\": \"\",\n" +
                "      \"firstname\": \"\",\n" +
                "      \"middlename\": \"\",\n" +
                "      \"lastname\": \"\",\n" +
                "      \"preferredname\": \"\",\n" +
                "      \"gender\": \"\",\n" +
                "      \"mobile\": {\n" +
                "        \"value\": \"\"\n" +
                "      },\n" +
                "      \"email\": {\n" +
                "        \"value\": \"\"\n" +
                "      },\n" +
                "      \"preferredcontact\": \"\",\n" +
                "      \"isvalid\": false,\n" +
                "      \"existing\": true,\n" +
                "      \"identitydocument\": {\n" +
                "        \n" +
                "      },\n" +
                "      \"resaddress\": {\n" +
                "        \"componentised\": false,\n" +
                "        \"country\": \"AU\",\n" +
                "        \"addressType\": \"residential\",\n" +
                "        \"sameasaddress\": \"0\",\n" +
                "        \"valid\": false\n" +
                "      },\n" +
                "      \"postaladdress\": {\n" +
                "        \"componentised\": false,\n" +
                "        \"country\": \"AU\",\n" +
                "        \"addressType\": \"postal\",\n" +
                "        \"sameasaddress\": \"0\",\n" +
                "        \"valid\": false\n" +
                "      },\n" +
                "      \"taxcountry\": \"AU\",\n" +
                "      \"taxoption\": \"\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"accountsettings\": {\n" +
                "    \"investorAccountSettings\": [\n" +
                "      {\n" +
                "        \"hasRoles\": false,\n" +
                "        \"paymentSetting\": \"nopayments\",\n" +
                "        \"hasApprovers\": false\n" +
                "      }\n" +
                "    ],\n" +
                "    \"primarycontact\": \"0\",\n" +
                "    \"adviserName\": \"Kate Kimmorley\",\n" +
                "    \"adviserLocation\": \"Queensland\"\n" +
                "  },\n" +
                "  \"linkedaccounts\": {\n" +
                "    \"primaryLinkedAccount\": {\n" +
                "      \"existing\": true\n" +
                "    },\n" +
                "    \"otherLinkedAccount\": [\n" +
                "      \n" +
                "    ]\n" +
                "  },\n" +
                "  \"fees\": {\n" +
                "    \"estamount\": \"0.00\",\n" +
                "    \"ongoingFees\": {\n" +
                "      \"feesComponent\": [\n" +
                "        \n" +
                "      ],\n" +
                "      \"type\": \"Ongoing advice fee\"\n" +
                "    },\n" +
                "    \"licenseeFees\": {\n" +
                "      \"feesComponent\": [\n" +
                "        \n" +
                "      ],\n" +
                "      \"type\": \"Licensee advice fee\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> theInvestors = (List<Map<String, Object>>) individualDetailsMap.get("investors");
        for (int i = 0; i < theInvestors.size(); i++) {
            theInvestors.get(i).put(FIELD_CORRELATION_ID, i);
        }
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        InvestorAuthorityProfilesType mockInvestorAuthoriProfileType = Mockito.mock(InvestorAuthorityProfilesType.class);
        when(mockInvestorAuthoriProfileType.getAuthorityProfile()).thenReturn(new ArrayList<InvestorAuthorityProfileType>());
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenReturn(mockInvestorAuthoriProfileType);

        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(clientApplicationForm, adviser, dealer, new ServiceErrorsImpl());
        assertThat(investorsType.getInvestor().get(0).getInvestmentAccountPartyRole(), hasItem(PRIMARY_OWNER_ROLE));

    }

    @Test
    public void getAllInvestorsRoles_ShouldReturnPrimayRoleForJointApplication() throws Exception {
        String json = "{\n" +
                "  \"accountType\": \"joint\",\n" +
                "  \"investors\": [\n" +
                "    {\n" +
                "      \"displayTaxOptions\": true,\n" +
                "      \"cismandatory\": true,\n" +
                "      \"fullName\": \"Mr Jim Cary\",\n" +
                "      \"title\": \"mr\",\n" +
                "      \"firstname\": \"Jim\",\n" +
                "      \"middlename\": \"\",\n" +
                "      \"lastname\": \"Cary\",\n" +
                "      \"preferredname\": \"jim\",\n" +
                "      \"dateofbirth\": \"11\\/11\\/1984\",\n" +
                "      \"gender\": \"male\",\n" +
                "      \"mobile\": {\n" +
                "        \"value\": \"0411111111\"\n" +
                "      },\n" +
                "      \"email\": {\n" +
                "        \"value\": \"jim@test.com\"\n" +
                "      },\n" +
                "      \"preferredcontact\": \"mobile\",\n" +
                "      \"isvalid\": true,\n" +
                "      \"existing\": true,\n" +
                "      \"alternatename\": \"\",\n" +
                "      \"identitydocument\": {\n" +
                "        \"photodocuments\": {\n" +
                "          \"driverlicence\": {\n" +
                "            \"documentIssuer\": \"ACT\",\n" +
                "            \"expiryDate\": \"11\\/11\\/2020\",\n" +
                "            \"documentNumber\": \"ASD234\",\n" +
                "            \"verificationSource\": \"original\",\n" +
                "            \"englishTranslation\": \"Not Applicable\",\n" +
                "            \"accreditedenglishtrans\": \"notapplicable\"\n" +
                "          },\n" +
                "          \"valid\": true\n" +
                "        }\n" +
                "      },\n" +
                "      \"resaddress\": {\n" +
                "        \"componentised\": true,\n" +
                "        \"unitNumber\": \"\",\n" +
                "        \"streetNumber\": \"\",\n" +
                "        \"streetName\": \"West\",\n" +
                "        \"suburb\": \"NORTH SYDNEY\",\n" +
                "        \"state\": \"NSW\",\n" +
                "        \"postcode\": \"2060\",\n" +
                "        \"country\": \"AU\",\n" +
                "        \"verified\": true,\n" +
                "        \"confirmed\": true,\n" +
                "        \"valid\": true,\n" +
                "        \"sameasaddress\": \"267069\",\n" +
                "        \"addressType\": \"residential\",\n" +
                "        \"id\": \"267069\",\n" +
                "        \"existingAddressList\": [\n" +
                "          {\n" +
                "            \"componentised\": true,\n" +
                "            \"unitNumber\": \"\",\n" +
                "            \"streetNumber\": \"\",\n" +
                "            \"streetName\": \"West\",\n" +
                "            \"suburb\": \"NORTH SYDNEY\",\n" +
                "            \"state\": \"NSW\",\n" +
                "            \"postcode\": \"2060\",\n" +
                "            \"country\": \"AU\",\n" +
                "            \"verified\": true,\n" +
                "            \"confirmed\": true,\n" +
                "            \"valid\": true,\n" +
                "            \"sameasaddress\": \"0\",\n" +
                "            \"addressType\": \"residential\",\n" +
                "            \"id\": \"267069\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"postaladdress\": {\n" +
                "        \"componentised\": true,\n" +
                "        \"unitNumber\": \"\",\n" +
                "        \"streetNumber\": \"\",\n" +
                "        \"streetName\": \"West\",\n" +
                "        \"suburb\": \"NORTH SYDNEY\",\n" +
                "        \"state\": \"NSW\",\n" +
                "        \"postcode\": \"2060\",\n" +
                "        \"country\": \"AU\",\n" +
                "        \"verified\": true,\n" +
                "        \"confirmed\": true,\n" +
                "        \"valid\": true,\n" +
                "        \"sameasaddress\": \"802267\",\n" +
                "        \"addressType\": \"residential\",\n" +
                "        \"id\": \"802267\",\n" +
                "        \"existingAddressList\": [\n" +
                "          {\n" +
                "            \"componentised\": true,\n" +
                "            \"unitNumber\": \"\",\n" +
                "            \"streetNumber\": \"\",\n" +
                "            \"streetName\": \"West\",\n" +
                "            \"suburb\": \"NORTH SYDNEY\",\n" +
                "            \"state\": \"NSW\",\n" +
                "            \"postcode\": \"2060\",\n" +
                "            \"country\": \"AU\",\n" +
                "            \"verified\": true,\n" +
                "            \"confirmed\": true,\n" +
                "            \"valid\": true,\n" +
                "            \"sameasaddress\": \"0\",\n" +
                "            \"addressType\": \"residential\",\n" +
                "            \"id\": \"802267\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"taxcountry\": \"AU\",\n" +
                "      \"taxoption\": \"Tax File Number or exemption not provided\",\n" +
                "      \"wealthsource\": \"Gift/Donation\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"displayTaxOptions\": true,\n" +
                "      \"cismandatory\": true,\n" +
                "      \"fullName\": \"Mr Silvestor Stelon\",\n" +
                "      \"title\": \"mr\",\n" +
                "      \"firstname\": \"Silvestor\",\n" +
                "      \"middlename\": \"\",\n" +
                "      \"lastname\": \"Stelon\",\n" +
                "      \"preferredname\": \"Silvestor\",\n" +
                "      \"dateofbirth\": \"11\\/11\\/1974\",\n" +
                "      \"gender\": \"male\",\n" +
                "      \"mobile\": {\n" +
                "        \"value\": \"0422222222\"\n" +
                "      },\n" +
                "      \"email\": {\n" +
                "        \"value\": \"silv@test.com\"\n" +
                "      },\n" +
                "      \"preferredcontact\": \"mobile\",\n" +
                "      \"isvalid\": true,\n" +
                "      \"existing\": true,\n" +
                "      \"alternatename\": \"\",\n" +
                "      \"identitydocument\": {\n" +
                "        \"photodocuments\": {\n" +
                "          \"driverlicence\": {\n" +
                "            \"documentIssuer\": \"NSW\",\n" +
                "            \"expiryDate\": \"11\\/11\\/2021\",\n" +
                "            \"documentNumber\": \"RTY654\",\n" +
                "            \"verificationSource\": \"original\",\n" +
                "            \"englishTranslation\": \"Not Applicable\",\n" +
                "            \"accreditedenglishtrans\": \"notapplicable\"\n" +
                "          },\n" +
                "          \"valid\": true\n" +
                "        }\n" +
                "      },\n" +
                "      \"resaddress\": {\n" +
                "        \"componentised\": true,\n" +
                "        \"unitNumber\": \"\",\n" +
                "        \"streetNumber\": \"\",\n" +
                "        \"streetName\": \"West\",\n" +
                "        \"suburb\": \"NORTH SYDNEY\",\n" +
                "        \"state\": \"NSW\",\n" +
                "        \"postcode\": \"2060\",\n" +
                "        \"country\": \"AU\",\n" +
                "        \"verified\": true,\n" +
                "        \"confirmed\": true,\n" +
                "        \"valid\": true,\n" +
                "        \"sameasaddress\": \"643768\",\n" +
                "        \"addressType\": \"residential\",\n" +
                "        \"id\": \"643768\",\n" +
                "        \"existingAddressList\": [\n" +
                "          {\n" +
                "            \"componentised\": true,\n" +
                "            \"unitNumber\": \"\",\n" +
                "            \"streetNumber\": \"\",\n" +
                "            \"streetName\": \"West\",\n" +
                "            \"suburb\": \"NORTH SYDNEY\",\n" +
                "            \"state\": \"NSW\",\n" +
                "            \"postcode\": \"2060\",\n" +
                "            \"country\": \"AU\",\n" +
                "            \"verified\": true,\n" +
                "            \"confirmed\": true,\n" +
                "            \"valid\": true,\n" +
                "            \"sameasaddress\": \"0\",\n" +
                "            \"addressType\": \"residential\",\n" +
                "            \"id\": \"643768\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"postaladdress\": {\n" +
                "        \"componentised\": true,\n" +
                "        \"unitNumber\": \"\",\n" +
                "        \"streetNumber\": \"\",\n" +
                "        \"streetName\": \"West\",\n" +
                "        \"suburb\": \"NORTH SYDNEY\",\n" +
                "        \"state\": \"NSW\",\n" +
                "        \"postcode\": \"2060\",\n" +
                "        \"country\": \"AU\",\n" +
                "        \"verified\": true,\n" +
                "        \"confirmed\": true,\n" +
                "        \"valid\": true,\n" +
                "        \"sameasaddress\": \"483232\",\n" +
                "        \"addressType\": \"residential\",\n" +
                "        \"id\": \"483232\",\n" +
                "        \"existingAddressList\": [\n" +
                "          {\n" +
                "            \"componentised\": true,\n" +
                "            \"unitNumber\": \"\",\n" +
                "            \"streetNumber\": \"\",\n" +
                "            \"streetName\": \"West\",\n" +
                "            \"suburb\": \"NORTH SYDNEY\",\n" +
                "            \"state\": \"NSW\",\n" +
                "            \"postcode\": \"2060\",\n" +
                "            \"country\": \"AU\",\n" +
                "            \"verified\": true,\n" +
                "            \"confirmed\": true,\n" +
                "            \"valid\": true,\n" +
                "            \"sameasaddress\": \"0\",\n" +
                "            \"addressType\": \"residential\",\n" +
                "            \"id\": \"483232\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"taxcountry\": \"AU\",\n" +
                "      \"taxoption\": \"Tax File Number or exemption not provided\",\n" +
                "      \"wealthsource\": \"Gift/Donation\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"accountsettings\": {\n" +
                "    \"professionalspayment\": \"linkedaccountsonly\",\n" +
                "    \"investorAccountSettings\": [\n" +
                "      {\n" +
                "        \"paymentSetting\": \"nopayments\",\n" +
                "        \"hasRoles\": false,\n" +
                "        \"hasApprovers\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"paymentSetting\": \"nopayments\",\n" +
                "        \"hasRoles\": false,\n" +
                "        \"hasApprovers\": false\n" +
                "      }\n" +
                "    ],\n" +
                "    \"primarycontact\": \"1\",\n" +
                "    \"adviserName\": \"Gareth Locke\",\n" +
                "    \"adviserLocation\": \"New South Wales\",\n" +
                "    \"sourceoffunds\": \"SF001004\"\n" +
                "  },\n" +
                "  \"linkedaccounts\": {\n" +
                "    \"primaryLinkedAccount\": {\n" +
                "      \"accountname\": \"HolyWood birst\",\n" +
                "      \"bsb\": \"062003\",\n" +
                "      \"accountnumber\": \"321654\",\n" +
                "      \"nickname\": \"\",\n" +
                "      \"directdebitamount\": \"10000.00\",\n" +
                "      \"existing\": true,\n" +
                "      \"bsbValidationResultVal\": true\n" +
                "    },\n" +
                "    \"otherLinkedAccount\": [\n" +
                "      \n" +
                "    ]\n" +
                "  },\n" +
                "  \"fees\": {\n" +
                "    \"estamount\": \"0.00\",\n" +
                "    \"ongoingFees\": {\n" +
                "      \"feesComponent\": [\n" +
                "        \n" +
                "      ],\n" +
                "      \"type\": \"Ongoing advice fee\"\n" +
                "    },\n" +
                "    \"licenseeFees\": {\n" +
                "      \"feesComponent\": [\n" +
                "        \n" +
                "      ],\n" +
                "      \"type\": \"Licensee advice fee\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> theInvestors = (List<Map<String, Object>>) individualDetailsMap.get("investors");
        for (int i = 0; i < theInvestors.size(); i++) {
            theInvestors.get(i).put(FIELD_CORRELATION_ID, i);
        }
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        InvestorAuthorityProfilesType mockInvestorAuthoriProfileType = Mockito.mock(InvestorAuthorityProfilesType.class);
        when(mockInvestorAuthoriProfileType.getAuthorityProfile()).thenReturn(new ArrayList<InvestorAuthorityProfileType>());
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenReturn(mockInvestorAuthoriProfileType);
        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(clientApplicationForm, adviser, dealer, new ServiceErrorsImpl());

        assertThat(investorsType.getInvestor().get(1).getInvestmentAccountPartyRole(), hasItem(PRIMARY_OWNER_ROLE));

    }

    @Test
    public void getAllDirectors_ShouldReturnAnEmptyList_WhenThereAreNoDirectors() throws Exception {
        String json = "{\"accountType\": \"corporateSMSF\"," +
                "\"directors\": []" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        List<InvestorType> directors = investorsTypeBuilder.getNewDirectors(clientApplicationForm.getDirectors(), CORPORATE_SMSF,
                adviser, dealer, accountSettingsForm, new ServiceErrorsImpl());

        assertThat(directors, is(empty()));
    }

    @Test
    public void getAllDirectors_ShouldReturnAListOfInvestorTypes_FromTheDirectorsKeyInTheFormData() throws Exception {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        String json = "{\"accountType\": \"corporateSMSF\"," +
                " \"directors\": [" + directorJson() + "]," +
                " \"accountsettings\": {\"primarycontact\": \"1\", \"investorAccountSettings\": [{\"isApprover\": \"true\", \"role\": \"director\", \"paymentSetting\": \"nopayments\"}], \"investorAccountSettings\": [{\"paymentSetting\":\"allpayments\"}]}," +
                " \"shareholderandmembers\": {\"investorsWithRoles\": [{\"isMember\": \"true\", \"isShareholder\": \"false\"}]}" +
                "}";


        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenReturn(new AddressType());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        List<Map<String, Object>> theDirectors = (List<Map<String, Object>>) individualDetailsMap.get("directors");
        for (int i = 0; i < theDirectors.size(); i++) {
            theDirectors.get(i).put(FIELD_CORRELATION_ID, i);
        }
        List<InvestorType> directors = investorsTypeBuilder.getNewDirectors(clientApplicationForm.getDirectors(), CORPORATE_SMSF,
                adviser, dealer, accountSettingsForm, serviceErrors);

        assertThat(directors.size(), is(1));
        assertThat(directors.get(0).getPartyRoleInRelatedOrganisation(), hasItem(COMPANY_DIRECTOR_ROLE));
    }


    @Test
    public void getAllDirectors_ShouldReturnAListOfInvestorTypes_WithCompanySecretaryRole() throws Exception {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        String json = "{\"accountType\": \"corporateSMSF\"," +
                " \"directors\": [" + directorJson() + "]," +
                " \"accountsettings\": {\"primarycontact\": \"1\", \"investorAccountSettings\": [{\"isApprover\": \"true\", \"role\": \"director\", \"paymentSetting\": \"nopayments\"}], \"investorAccountSettings\": [{\"paymentSetting\":\"allpayments\"}]}," +
                " \"shareholderandmembers\": {\"companysecretary\": \"0\", \"investorsWithRoles\": [{\"isMember\": \"true\", \"isShareholder\": \"false\"}]}" +
                "}";


        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenReturn(new AddressType());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        List<Map<String, Object>> theDirectors = (List<Map<String, Object>>) individualDetailsMap.get("directors");
        for (int i = 0; i < theDirectors.size(); i++) {
            theDirectors.get(i).put(FIELD_CORRELATION_ID, i);
        }
        List<InvestorType> directors = investorsTypeBuilder.getNewDirectors(clientApplicationForm.getDirectors(), CORPORATE_SMSF,
                adviser, dealer, accountSettingsForm, serviceErrors);

        assertThat(directors.size(), is(1));
        assertThat(directors.get(0).getPartyRoleInRelatedOrganisation(), hasItem(COMPANY_DIRECTOR_ROLE));
        assertThat(directors.get(0).getPartyRoleInRelatedOrganisation(), hasItem(COMPANY_SECRETARY_ROLE));
    }


    @Test
    public void getAllDirectors_ShouldSetThePrimaryContactCorrectly() throws Exception {
        String json = "{\"accountType\": \"corporateSMSF\"," +
                " \"directors\": [" + directorJson() + ", " + directorJson() + ", " + directorJson() + "]," +
                " \"accountsettings\": {\"primarycontact\": \"1\", \"investorAccountSettings\": [{\"isApprover\": \"true\", \"role\": \"director\", \"paymentSetting\": \"nopayments\"},{\"isApprover\": \"true\", \"role\": \"director\", \"paymentSetting\": \"nopayments\"},{\"isApprover\": \"true\", \"role\": \"director\", \"paymentSetting\": \"nopayments\"}]}," +
                "\"shareholderandmembers\": {\"investorsWithRoles\": [{\"isMember\": \"true\", \"isShareholder\": \"false\"},{\"isMember\": \"true\", \"isShareholder\": \"true\"},{\"isMember\": \"true\", \"isShareholder\": \"true\"}]}" +
                "}";

        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenReturn(new AddressType());
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> theDirectors = (List<Map<String, Object>>) individualDetailsMap.get("directors");
        for (int i = 0; i < theDirectors.size(); i++) {
            theDirectors.get(i).put(FIELD_CORRELATION_ID, i);
        }

        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        List<InvestorType> directors = investorsTypeBuilder.getNewDirectors(clientApplicationForm.getDirectors(), CORPORATE_SMSF, adviser, dealer, accountSettingsForm, new ServiceErrorsImpl());

        assertThat(directors.size(), is(3));
        assertThat(directors.get(0).getInvestmentAccountPartyRole(), not(hasItem(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE)));
        assertThat(directors.get(1).getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
        assertThat(directors.get(2).getInvestmentAccountPartyRole(), not(hasItem(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE)));
    }

    @Test
    public void getAllDirectors_ShouldSetTheApproversCorrectly() throws Exception {
        String json = "{\"accountType\": \"corporateSMSF\"," +
                " \"directors\": [" + directorJson() + ", " + directorJson() + ", " + directorJson() + "]," +
                " \"accountsettings\": " +
                "   {" +
                "       \"primarycontact\": \"1\", " +
                "       \"investorAccountSettings\": [ {" +
                "               \"isApprover\": \"true\"," +
                "               \"paymentSetting\": \"nopayments\"" +
                "           },{\"isApprover\": \"false\", \"paymentSetting\": \"nopayments\"}," +
                "           {\"isApprover\": \"false\", \"paymentSetting\": \"nopayments\"}" +
                "       ]" +
                "   }," +
                " \"shareholderandmembers\": {\"investorsWithRoles\": [{\"isMember\": \"true\", \"isShareholder\": \"false\"},{\"isMember\": \"true\", \"isShareholder\": \"true\"},{\"isMember\": \"true\", \"isShareholder\": \"true\"}]}" +
                "}";

        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenReturn(new AddressType());
        when(authorityTypeBuilder.getAuthorityType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfileType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfileTypeForApplicationApproval()).thenCallRealMethod();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> theDirectors = (List<Map<String, Object>>) individualDetailsMap.get("directors");
        for (int i = 0; i < theDirectors.size(); i++) {
            theDirectors.get(i).put(FIELD_CORRELATION_ID, i);
        }
        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        List<InvestorType> directors = investorsTypeBuilder.getNewDirectors(clientApplicationForm.getDirectors(), CORPORATE_SMSF, adviser, dealer, accountSettingsForm, new ServiceErrorsImpl());

        List<List<AuthorityTypeType>> authorities = Lambda.convert(directors, new Converter<InvestorType, List<AuthorityTypeType>>() {
            @Override
            public List<AuthorityTypeType> convert(InvestorType investorType) {
                return Lambda.convert(investorType.getAuthorityProfiles().getAuthorityProfile(), new Converter<InvestorAuthorityProfileType, AuthorityTypeType>() {
                    @Override
                    public AuthorityTypeType convert(InvestorAuthorityProfileType investorAuthorityProfileType) {
                        return investorAuthorityProfileType.getAuthorityType();
                    }
                });
            }
        });

        assertThat(directors.size(), is(3));
        List<AuthorityTypeType> director1AuthorityProfile = authorities.get(0);
        assertThat(director1AuthorityProfile, hasItem(AuthorityTypeType.LIMITED_CHANGE));
        assertThat(director1AuthorityProfile, hasItem(AuthorityTypeType.APPLICATION_APPROVAL));

        List<AuthorityTypeType> director2AuthorityProfile = authorities.get(1);
        assertThat(director2AuthorityProfile, hasItem(AuthorityTypeType.LIMITED_CHANGE));
        assertThat(director2AuthorityProfile, not(hasItem(AuthorityTypeType.APPLICATION_APPROVAL)));

        List<AuthorityTypeType> director3AuthorityProfile = authorities.get(2);
        assertThat(director3AuthorityProfile, hasItem(AuthorityTypeType.LIMITED_CHANGE));
        assertThat(director3AuthorityProfile, not(hasItem(AuthorityTypeType.APPLICATION_APPROVAL)));
    }

    @Test
    public void getAllDirectors_ShouldSetThePaymentSettingsCorrectly() throws Exception {
        String json = "{" +
                "\"accountType\": \"corporateSMSF\"," +
                "\"directors\": [" + directorJson() + "]," +
                "\"accountsettings\": {\"primarycontact\": \"1\", \"investorAccountSettings\": [{\"isApprover\": \"false\", \"paymentSetting\": \"nopayments\"},{\"isApprover\": \"true\", \"paymentSetting\": \"linkedaccountsonly\"},{\"isApprover\": \"true\", \"paymentSetting\": \"allpayments\"}]}," +
                "\"shareholderandmembers\": {\"investorsWithRoles\": [{\"isMember\": \"true\", \"isShareholder\": \"false\"},{\"isMember\": \"true\", \"isShareholder\": \"true\"},{\"isMember\": \"true\", \"isShareholder\": \"true\"}]}" +
                "}";

        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenReturn(new AddressType());
        when(authorityTypeBuilder.getAuthorityType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfileType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> individualDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> theDirectors = (List<Map<String, Object>>) individualDetailsMap.get("directors");
        for (int i = 0; i < theDirectors.size(); i++) {
            theDirectors.get(i).put(FIELD_CORRELATION_ID, i);
        }

        IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(individualDetailsMap);
        List<InvestorType> directors = investorsTypeBuilder.getNewDirectors(clientApplicationForm.getDirectors(), CORPORATE_SMSF, adviser, dealer, accountSettingsForm, new ServiceErrorsImpl());

        List<List<AuthorityTypeType>> authorities = Lambda.convert(
                directors, new Converter<InvestorType, List<AuthorityTypeType>>() {
            @Override
            public List<AuthorityTypeType> convert(InvestorType investorType) {
                return Lambda.convert(
                        investorType.getAuthorityProfiles().getAuthorityProfile(), new Converter<InvestorAuthorityProfileType, AuthorityTypeType>() {
                    @Override
                    public AuthorityTypeType convert(InvestorAuthorityProfileType investorAuthorityProfileType) {
                        return investorAuthorityProfileType.getAuthorityType();
                    }
                });
            }
        });

        assertThat(directors.size(), is(1));
        List<AuthorityTypeType> directorAuthorityProfile = authorities.get(0);
        assertThat(directorAuthorityProfile.size(), is(2));
        assertThat(directorAuthorityProfile, hasItem(AuthorityTypeType.LIMITED_CHANGE));
        assertThat(directorAuthorityProfile, hasItem(AuthorityTypeType.APPLICATION_MAINTENANCE));
    }

    @Test
    public void getInvestorsType_WhenCorporateSMSF_ShouldIncludeDirectors() throws Exception {

        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();

        InvestorsType directors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertThat(directors.getInvestor().get(0).getPartyRoleInRelatedOrganisation(), hasItem(COMPANY_DIRECTOR_ROLE));
    }

    @Test
    public void getInvestorsType_WhenCorporateSMSF_ShouldHaveRegisteredDateAndState() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();
        when(form.getSmsf().getCorrelationSequenceNumber()).thenReturn(1);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        TrustDetailsType trustDetails = smsf.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertNotNull(trustDetails.getTrustType().getRegulated());
        assertThat(trustDetails.getTrustRegisteredState(), is("NSW"));
        assertThat(trustDetails.getTrustRegisteredDate(), is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy")));

    }

    @Test
    public void getInvestorsType_WhenCorporateSMSF_ShouldIncludeCompanyDetailsOnCompanyTrustee() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();

        ICompanyForm companyForm = mock(ICompanyForm.class);
        when(companyForm.getCorrelationSequenceNumber()).thenReturn(1);
        when(form.getCompanyTrustee()).thenReturn(companyForm);

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getCompanyAsTrustee(form.getSmsf(), companyForm, accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);

        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertSame(organisationBuiltByBuilder, findInvestorTypeWithCorrelationSequence(investorsType.getInvestor(), "1").getInvestorDetails().getPartyDetails().getOrganisation());
    }

    @Test
    public void getInvestorsType_WhenCorporateSMSF_ShouldIncludeCompanyDetailsOnSmsf() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();

        ISmsfForm ISmsfForm = mock(ISmsfForm.class);
        when(ISmsfForm.getCorrelationSequenceNumber()).thenReturn(1);
        when(form.getSmsf()).thenReturn(ISmsfForm);

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getOrganisation(ISmsfForm, accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);

        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertSame(organisationBuiltByBuilder, findInvestorTypeWithCorrelationSequence(investorsType.getInvestor(), "1").getInvestorDetails().getPartyDetails().getOrganisation());
    }

    @Test
    public void getInvestorsType_CorporateSMSF_smsfInvestorDetails_GetCisKey() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();
        when(form.getSmsf().getCorrelationSequenceNumber()).thenReturn(1);
        when(form.getSmsf().getCisKey()).thenReturn("MY CIS KEY");

        CustomerIdentifiers smsfCustomerIdentifiers = mock(CustomerIdentifiers.class);
        when(customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKey("MY CIS KEY")).thenReturn(smsfCustomerIdentifiers);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        assertSame(smsfCustomerIdentifiers, smsf.getInvestorDetails().getCustomerIdentifiers());
    }

    @Test
    public void getInvestorsType_CorporateSMSF_smsfInvestorDetails_ShouldHaveRegisteredAddressAsPostalAddress() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();
        ISmsfForm ISmsfForm = form.getSmsf();
        when(ISmsfForm.getCorrelationSequenceNumber()).thenReturn(1);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(ISmsfForm.getRegisteredAddress()).thenReturn(addressForm);

        AddressType postalAddress = mock(AddressType.class);
        when(addressTypeBuilder.getDefaultAddressType(eq(addressForm), any(AddressType.class), eq(serviceErrors))).thenReturn(postalAddress);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        assertSame(postalAddress, smsf.getInvestorDetails().getPostalAddresses().getAddress().get(0));
    }

    @Test
    public void getInvestorsType_NewIndividualSMSF_InvestorDetails_ShouldHaveTFN() throws Exception {
        IClientApplicationForm form = getMockSMSFApplicationForm(IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF);
        ISmsfForm ISmsfForm = form.getSmsf();
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertThat(investors.getInvestor().size(), is(2));
        InvestorType investorType = (InvestorType) investors.getInvestor().get(0);
        verify(taxFieldsBuilder, times(1)).populateCrsTaxRelatedFieldsForNewInvestor(any(InvolvedPartyDetailsType.class), any(IExtendedPersonDetailsForm.class));
    }

    @Test
    public void getInvestorsType_NewIndividualSMSF_InvestorDetails_WithCRS() throws Exception {
        IClientApplicationForm form = getMockSMSFApplicationForm(IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF);
        ISmsfForm ISmsfForm = form.getSmsf();
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertThat(investors.getInvestor().size(), is(2));
        InvestorType investorType = (InvestorType) investors.getInvestor().get(0);
        verify(taxFieldsBuilder, times(1)).populateCrsTaxRelatedFieldsForNewInvestor(any(InvolvedPartyDetailsType.class), any(IExtendedPersonDetailsForm.class));
    }

    @Test
    public void getInvestorsType_CorporateSMSF_CompanyAsTrustee_ShouldHavePostalAddress() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();
        ICompanyForm companyTrustee = form.getCompanyTrustee();
        when(companyTrustee.getCorrelationSequenceNumber()).thenReturn(1);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(companyTrustee.getPlaceOfBusinessAddress()).thenReturn(addressForm);

        AddressType postalAddress = mock(AddressType.class);
        when(addressTypeBuilder.getDefaultAddressType(eq(addressForm), any(AddressType.class), eq(serviceErrors))).thenReturn(postalAddress);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        assertSame(postalAddress, smsf.getInvestorDetails().getPostalAddresses().getAddress().get(0));
    }

    @Test
    public void getInvestorsType_CorporateSMSF_Directors_ShouldHavePostalAddress() throws Exception {
        IClientApplicationForm form = getMockSmsfCorporateApplicationForm();
        IDirectorDetailsForm directorDetailsForm = (IDirectorDetailsForm) form.getDirectors().get(0);
        IAddressForm addressForm = mock(IAddressForm.class);
        when(directorDetailsForm.getPostalAddress()).thenReturn(addressForm);
        when(directorDetailsForm.hasPostalAddress()).thenReturn(true);

        final AddressType postalAddress = mock(AddressType.class);
        when(addressTypeBuilder.getDefaultAddressType(eq(addressForm), any(AddressType.class), eq(false), eq(serviceErrors))).thenReturn(postalAddress);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertSame(postalAddress, investors.getInvestor().get(0).getInvestorDetails().getPostalAddresses().getAddress().get(0));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_ShouldIncludeDirectors() throws Exception {

        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        InvestorsType directors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        assertThat(directors.getInvestor().get(2).getPartyRoleInRelatedOrganisation(), hasItem(COMPANY_DIRECTOR_ROLE));
        assertThat(directors.getInvestor().get(2).getPartyRoleInRelatedOrganisation(), hasItem(COMPANY_BENEFICIAL_OWNER_ROLE));
        assertThat(directors.getInvestor().get(2).getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIARY_ROLE));

    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_FamilyType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        InvestorType trust = (InvestorType) investors.getInvestor().get(0);

        verify(addressTypeBuilder, times(2)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getStandard().getTrustDescription(), is("Discretionary/family trust"));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredDate(), is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_RegulatedType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockCorporateRegulatedTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        InvestorType trust = (InvestorType) investors.getInvestor().get(0);

        verify(addressTypeBuilder, times(2)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        RegulatedTrustType regulatedTrustType = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getRegulated();
        assertThat(regulatedTrustType.getRegulatorLicensingNumber(), is("MY REGULATOR LICENSING NUMBER"));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredDate(), is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_RegisteredMISType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockCorporateRegisteredMISTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        assertThat(investors.getInvestor(), hasSize(3));
        InvestorType trust = investors.getInvestor().get(0);
        TrustDetailsType trustDetails = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustType().getInvestmentScheme().getARSN(), is("MY ARSN NUMBER"));
        assertThat(trustDetails.getTrustRegisteredDate(), is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trustDetails.getTrustMembershipClassDetails(), is("Beneficiary class details"));
        assertThat(trustDetails.getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_GovtSuperType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockCorporateGovtSuperTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        verify(addressTypeBuilder, times(2)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        InvestorType trust = investors.getInvestor().get(0);
        TrustDetailsType trustDetails = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustType().getSuperannuationFund().getLegislationName(), is("NAME OF LEGISLATION"));
        assertThat(trustDetails.getTrustRegisteredDate(), is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trustDetails.getTrustMembershipClassDetails(), is("Beneficiary class details"));
        assertThat(trustDetails.getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_ShouldIncludeBeneficiaryClassDetails() throws Exception {

        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        verify(addressTypeBuilder, times(2)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));
        InvestorType trust = investors.getInvestor().get(0);
        TrustDetailsType trustDetails = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustMembershipClassDetails(), is("Beneficiary class details"));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_ShouldIncludeTrustDetails_WithTheCompanyASICName() throws Exception {
        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();
        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getOrganisation(form.getTrust(), accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        assertThat(investorsType.getInvestor().get(0).getInvestorDetails().getPartyDetails().getOrganisation().getASICName(), is("My trust business name"));
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_ShouldIncludeTrustDetails_WithTheCISKey() throws Exception {
        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();

        ITrustForm ITrustForm = form.getTrust();
        when(ITrustForm.getCisKey()).thenReturn("CIS KEY");

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getOrganisation(ITrustForm, accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        verify(customerIdentifiersBuilder).buildCustomerIdentifiersWithCisKey("CIS KEY");
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_ShouldIncludeTrustDetails_WithBlankCISKey() throws Exception {
        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();

        ITrustForm ITrustForm = form.getTrust();
        when(ITrustForm.getCisKey()).thenReturn("");

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getOrganisation(ITrustForm, accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        verify(customerIdentifiersBuilder, never()).buildCustomerIdentifiersWithCisKey("");
    }

    @Test
    public void getInvestorsType_WhenCorporateTrust_ShouldIncludeCompanyDetailsOnCompanyTrustee() throws Exception {
        IClientApplicationForm form = getMockCorporateFamilyTrustApplicationForm();
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        ICompanyForm companyForm = mock(ICompanyForm.class);
        when(companyForm.getCorrelationSequenceNumber()).thenReturn(1);
        when(companyForm.getAsicName()).thenReturn("MY ASIC NAME");
        when(companyForm.getCisKey()).thenReturn("CIS KEY");
        when(companyForm.getPlaceOfBusinessAddress()).thenReturn(mock(IAddressForm.class));
        when(companyForm.getSourceOfWealth()).thenReturn("Business profits");


        when(form.getCompanyTrustee()).thenReturn(companyForm);

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getCompanyAsTrustee(form.getTrust(), companyForm, accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);

        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        verify(customerIdentifiersBuilder).buildCustomerIdentifiersWithCisKey("CIS KEY");
        InOrder inOrder = inOrder(addressTypeBuilder);

        inOrder.verify(addressTypeBuilder).getDefaultAddressType(eq(form.getTrust().getRegisteredAddress()), any(ns.btfin_com.sharedservices.common.address.v3_0.AddressType.class), eq(serviceErrors));
        inOrder.verify(addressTypeBuilder).getDefaultAddressType(eq(companyForm.getPlaceOfBusinessAddress()), any(ns.btfin_com.sharedservices.common.address.v3_0.AddressType.class), eq(serviceErrors));

        assertSame(organisationBuiltByBuilder, findInvestorTypeWithCorrelationSequence(investorsType.getInvestor(), "1").getInvestorDetails().getPartyDetails().getOrganisation());

        InvestorType companyTrustee = (InvestorType) investorsType.getInvestor().get(1);
        assertThat(companyTrustee.getInvestorDetails().getPartyDetails().getOrganisation().getASICName(), is("MY ASIC NAME"));
        assertThat(companyTrustee.getPartyRoleInRelatedOrganisation(), hasItem(TRUST_TRUSTEE_ROLE));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldIncludeTrustees() throws Exception {

        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();

        InvestorsType trustees = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertThat(trustees.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(TRUST_TRUSTEE_ROLE));
        assertThat(trustees.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIAL_OWNER_ROLE));
        assertThat(trustees.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIARY_ROLE));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldHavePrimaryOwnerRole() throws Exception {
        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();
        InvestorsType trustees = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        assertThat(trustees.getInvestor().get(0).getInvestmentAccountPartyRole(), hasItem(PRIMARY_OWNER_ROLE));

    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldHaveAccountServiceRole() throws Exception {
        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();
        InvestorsType trustees = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        assertThat(trustees.getInvestor().get(1).getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
        assertThat(trustees.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldIncludeAuthorityProfileForTrustees() throws Exception {

        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        InvestorsType trustees = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        List<InvestorAuthorityProfileType> trusteeAuthorityProfile = trustees.getInvestor().get(1).getAuthorityProfiles().getAuthorityProfile();
        assertThat(trusteeAuthorityProfile.get(1).getAuthorityType(), is(AuthorityTypeType.APPLICATION_MAINTENANCE));
    }

    @Test
    public void getInvestorType_WhenIndividualTrustOther_ShouldHaveOtherDescription() throws Exception {
        IClientApplicationForm form = getMockIndividualTrustOtherApplicationForm();


        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        String otherDescription = investors.getInvestor().get(0).getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getStandard().getTrustOtherDescription();
        String description = investors.getInvestor().get(0).getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getStandard().getTrustDescription();

        assertThat(otherDescription, is("MyTRUST DESCRIPTION OTHER"));
        assertThat(description, is("Other"));
        assertThat(investors.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getInvestorType_WhenIndividualTrustOther_ShouldHaveSettlerOrg() throws Exception {
        IClientApplicationForm form = getMockIndividualTrustSettlorOrgForm();
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        String orgName = investors.getInvestor().get(0).getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getStandard().getSettlorOfTrust().getOrganisation().getOrganisationName();
        assertThat(orgName, is("TesOrgName"));
    }

    @Test
    public void getInvestorType_WhenIndividualTrustOther_ShouldHaveSettlerInd() throws Exception {
        IClientApplicationForm form = getMockIndividualTrustSettlorIndForm();
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        SettlorOfTrustPartyDetailType settlorOfTrust = investors.getInvestor().get(0).getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getStandard().getSettlorOfTrust();

        assertThat(settlorOfTrust.getIndividual().getGivenName(), is("firstName"));
        assertThat(settlorOfTrust.getIndividual().getLastName(), is("lastName"));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_FamilyType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        InvestorType trust = (InvestorType) investors.getInvestor().get(0);

        verify(addressTypeBuilder, times(1)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getStandard().getTrustDescription(),
                is("Discretionary/family trust"));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredDate(),
                is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_RegulatedType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockIndividualRegulatedTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        InvestorType trust = (InvestorType) investors.getInvestor().get(0);

        verify(addressTypeBuilder, times(1)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        RegulatedTrustType regulatedTrustType = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustType().getRegulated();
        assertThat(regulatedTrustType.getRegulatorLicensingNumber(), is("MY REGULATOR LICENSING NUMBER"));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredDate(),
                is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails().getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_RegisteredMISType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockIndividualRegisteredMISTrustApplicationForm();
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);
        InvestorType trust = investors.getInvestor().get(0);
        verify(addressTypeBuilder, times(1)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        TrustDetailsType trustDetails = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustType().getInvestmentScheme().getARSN(), is("MY ARSN NUMBER"));
        assertThat(trustDetails.getTrustRegisteredDate(),
                is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trustDetails.getTrustMembershipClassDetails(), is("BENEFICIARY CLASS DETAILS"));
        assertThat(trustDetails.getTrustRegisteredState(), is("My registration state"));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_GovtSuperType_ShouldIncludeTrustDetails() throws Exception {

        IClientApplicationForm form = getMockIndividualGovtSuperTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        InvestorType trust = (InvestorType) investors.getInvestor().get(0);

        verify(addressTypeBuilder, times(1)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        TrustDetailsType trustDetails = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustType().getSuperannuationFund().getLegislationName(), is("NAME OF LEGISLATION"));
        assertThat(trustDetails.getTrustRegisteredDate(),
                is(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy")));
        assertThat(trustDetails.getTrustMembershipClassDetails(), is("BENEFICIARY CLASS DETAILS"));
        assertThat(trustDetails.getTrustRegisteredState(), is("My registration state"));
        assertThat(investors.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldIncludeBeneficiaryClassDetails() throws Exception {

        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        verify(addressTypeBuilder, times(1)).getDefaultAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors));

        InvestorType trust = investors.getInvestor().get(0);
        TrustDetailsType trustDetails = trust.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustMembershipClassDetails(), is("BENEFICIARY CLASS DETAILS"));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldIncludeTrustDetails_WithTheCompanyASICName() throws Exception {
        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getOrganisation(form.getTrust(), accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);

        InvestorsType investorsType = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        assertThat(investorsType.getInvestor().get(0).getInvestorDetails().getPartyDetails().getOrganisation().getASICName(), is("MY INDIVIDUAL TRUST BUSINESS NAME"));
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldIncludeTrustDetails_WithTheCISKey() throws Exception {
        IClientApplicationForm form = getMockIndividualFamilyTrustApplicationForm();

        ITrustForm ITrustForm = form.getTrust();
        when(ITrustForm.getCisKey()).thenReturn("CIS KEY");

        OrganisationType organisationBuiltByBuilder = new OrganisationType();
        when(organisationTypeBuilder.getOrganisation(ITrustForm, accountSettingsForm, adviser, dealer, serviceErrors)).thenReturn(organisationBuiltByBuilder);

        investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        verify(customerIdentifiersBuilder).buildCustomerIdentifiersWithCisKey("CIS KEY");
    }

    @Test
    public void getInvestorsType_WhenIndividualTrust_ShouldIncludeAdditionalBeneficiaries() throws Exception {
        IClientApplicationForm clientApplicationForm = getMockIndividualFamilyTrustApplicationForm();
        IExtendedPersonDetailsForm beneficiaryForm = mock(IExtendedPersonDetailsForm.class);
        when(beneficiaryForm.getCorrelationSequenceNumber()).thenReturn(1);
        when(clientApplicationForm.getAdditionalShareholdersAndMembers()).thenReturn(Arrays.<IExtendedPersonDetailsForm>asList(beneficiaryForm));
        when(beneficiaryForm.hasResidentialAddress()).thenReturn(false);
        when(beneficiaryForm.isBeneficiary()).thenReturn(true);
        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenCallRealMethod();
        when(individualTypeBuilder.getIndividualType(any(IPersonDetailsForm.class), eq(adviser), eq(dealer), eq(accountSettingsForm),
                any(ServiceErrors.class))).thenReturn(new IndividualType());

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(clientApplicationForm, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType beneficiary = investors.getInvestor().get(2);

        assertThat(beneficiary.getInvestorDetails().getCorrelationSequenceNumber(), is("1"));
        assertThat(beneficiary.getPartyRoleInRelatedOrganisation(), hasItem(TRUST_BENEFICIARY_ROLE));
    }

    @Test
    public void getInvestorType_WhenIndividualTrustMIS_ShouldHaveResponsibleEntityRole() throws Exception {
        IClientApplicationForm clientApplicationForm = getMockIndividualRegisteredMISTrustApplicationForm();
        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenCallRealMethod();
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(clientApplicationForm, adviser, dealer, serviceErrors);
        assertThat(investors.getInvestor().size(), is(2));
        assertThat(investors.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.TRUST_RESPONSIBLE_ENTITY_ROLE));
        assertThat(investors.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE));
    }


    @Test
    public void getInvestorsType_IndividualSMSF_smsfOrganisation_TrustDetails_ShouldHaveRegisteredDate() throws Exception {
        IClientApplicationForm form = getMockSmsfIndividualApplicationForm();
        when(form.getSmsf().getCorrelationSequenceNumber()).thenReturn(1);
        XMLGregorianCalendar myRegisteredDate = mock(XMLGregorianCalendar.class);
        when(form.getSmsf().getDateOfRegistration()).thenReturn(myRegisteredDate);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        TrustDetailsType trustDetails = smsf.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustRegisteredDate(), is(myRegisteredDate));
    }

    @Test
    public void getInvestorsType_IndividualSMSF_smsfOrganisation_TrustDetails_ShouldHaveRegisteredState() throws Exception {
        IClientApplicationForm form = getMockSmsfIndividualApplicationForm();
        when(form.getSmsf().getCorrelationSequenceNumber()).thenReturn(1);

        when(form.getSmsf().getRegistrationState()).thenReturn("My State");

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        TrustDetailsType trustDetails = smsf.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertThat(trustDetails.getTrustRegisteredState(), is("My State"));
    }

    @Test
    public void getInvestorsType_IndividualSMSF_smsfOrganisation_TrustDetails_ShouldBeRegulatedType() throws Exception {
        IClientApplicationForm form = getMockSmsfIndividualApplicationForm();
        when(form.getSmsf().getCorrelationSequenceNumber()).thenReturn(1);

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(form, adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType smsf = findInvestorTypeWithCorrelationSequence(investors.getInvestor(), "1");
        TrustDetailsType trustDetails = smsf.getInvestorDetails().getPartyDetails().getOrganisation().getTrustDetails();
        assertNotNull(trustDetails.getTrustType().getRegulated());
    }

    @Test
    public void getInvestorsType_WhenIndividualSMSF_ShouldIncludeTheSMSFTrustees() throws Exception {
        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenCallRealMethod();
        when(authorityTypeBuilder.getAuthorityType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfileType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(individualTypeBuilder.getIndividualType(any(IPersonDetailsForm.class), eq(adviser), eq(dealer), eq(accountSettingsForm),
                any(ServiceErrors.class))).thenReturn(new IndividualType());

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(ClientApplicationFormFactory.getNewClientApplicationForm(getIndividualSmsfFromJson()), adviser, dealer, serviceErrors);
        verify(customerIdentifiersBuilder, times(1)).buildCustomerIdentifiersWithCisKey("12312312312");
        verify(individualTypeBuilder, times(4)).getIndividualType(any(IExtendedPersonDetailsForm.class), eq(adviser), eq(dealer),
                any(IAccountSettingsForm.class), any(ServiceErrors.class));
        verify(contactDetailsBuilder, times(2)).populateContactDetailsField(any(InvolvedPartyDetailsType.class), any(IPersonDetailsForm.class));

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType trustee1 = investors.getInvestor().get(1);
        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType trustee2 = investors.getInvestor().get(2);

        assertNotNull(trustee1.getInvestorDetails().getCorrelationSequenceNumber());

        assertThat(trustee1.getAuthorityProfiles().getAuthorityProfile(), hasAuthorityType(AuthorityTypeType.LIMITED_CHANGE));
        assertThat(trustee1.getAuthorityProfiles().getAuthorityProfile(), hasAuthorityType(AuthorityTypeType.LIMITED_CHANGE));
        assertThat(trustee1.getPartyRoleInRelatedOrganisation(), hasItem(SMSF_TRUSTEE_ROLE));
        assertThat(trustee1.getPartyRoleInRelatedOrganisation(), hasItem(SMSF_MEMBER_ROLE));

        assertThat(trustee2.getAuthorityProfiles().getAuthorityProfile(), hasAuthorityType(AuthorityTypeType.LIMITED_TRANSACTION));
        assertThat(trustee2.getAuthorityProfiles().getAuthorityProfile(), hasAuthorityType(AuthorityTypeType.LIMITED_TRANSACTION));
        assertThat(trustee2.getPartyRoleInRelatedOrganisation(), hasItem(SMSF_TRUSTEE_ROLE));
        assertThat(trustee2.getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
        assertThat(trustee2.getPartyRoleInRelatedOrganisation(), hasItem(SMSF_MEMBER_ROLE));

        assertNotNull(trustee2.getInvestorDetails().getCorrelationSequenceNumber());
    }

    @Test
    public void getCompanyDetails_shouldReturnInvestorWithSetOrganisationInfo() throws IOException {
        Map form = getCompanyFromJson();
        InvestorType investor = investorsTypeBuilder.getCompanyDetails(ClientApplicationFormFactory.getNewClientApplicationForm(form),
                adviser, dealer, serviceErrors);

        // No Account owners in V3
        // assertThat(investor.getInvestmentAccountPartyRole().get(0), is(InvestmentAccountPartyRoleTypeType.ACCOUNT_OWNER_ROLE));

        ns.btfin_com.party.v3_0.OrganisationType organisation = investor.getInvestorDetails().getPartyDetails().getOrganisation();
        assertThat(organisation.getASICName(), is("Testus2895"));
    }

    @Test
    public void getInvestorsType_WhenIndividualSMSF_ShouldIncludeAdditionalMembers() throws Exception {
        when(addressTypeBuilder.getAddressType(any(IAddressForm.class), any(AddressType.class), eq(serviceErrors))).thenCallRealMethod();
        when(authorityTypeBuilder.getAuthorityType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(authorityTypeBuilder.getInvestorAuthorityProfileType(any(PaymentAuthorityEnum.class))).thenCallRealMethod();
        when(individualTypeBuilder.getIndividualType(any(IPersonDetailsForm.class), eq(adviser), eq(dealer), eq(accountSettingsForm),
                any(ServiceErrors.class))).thenReturn(new IndividualType());

        InvestorsType investors = investorsTypeBuilder.getInvestorsType(ClientApplicationFormFactory.getNewClientApplicationForm(getIndividualSmsfFromJson()), adviser, dealer, serviceErrors);

        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType member1 = investors.getInvestor().get(3);
        ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType member2 = investors.getInvestor().get(4);

        assertNotNull(member1.getInvestorDetails().getCorrelationSequenceNumber());
        assertThat(member1.getPartyRoleInRelatedOrganisation(), hasItem(SMSF_MEMBER_ROLE));

        assertNotNull(member2.getInvestorDetails().getCorrelationSequenceNumber());
        assertThat(member2.getPartyRoleInRelatedOrganisation(), hasItem(SMSF_MEMBER_ROLE));
    }

    @Test
    public void getCompanyDetails_WhenCompany_ShouldIncludePrimayOwnerRole() throws Exception {
        Map form = getCompanyFromJson();
        InvestorType investor = investorsTypeBuilder.getCompanyDetails(ClientApplicationFormFactory.getNewClientApplicationForm(form), adviser, dealer, serviceErrors);
        assertThat(investor.getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE));
    }


    @Test
    public void getCompanyDetails_WhenCompanyInvestor_ShouldIncludeAccountServiceRole() throws Exception {
        Map form = getCompanyFromJson();
        InvestorAuthorityProfilesType investorAuthorityProfilesType = mock(InvestorAuthorityProfilesType.class);
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenReturn(investorAuthorityProfilesType);

        //investorsType.getInvestor().addAll(getNewDirectors(getNewPersons(directorsSecretariesSignatories), accountType, adviser, dealer, form.getAccountSettings()));
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(ClientApplicationFormFactory.getNewClientApplicationForm(form), adviser, dealer, serviceErrors);
        assertThat(investors.getInvestor().get(1).getInvestmentAccountPartyRole(), hasItem(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE));
    }


    @Test
    public void getCompanyDetails_WhenCompanyInvestorShareholder_ShouldHaveBenOwnerRole() throws Exception {
        Map form = getCompanyFromJson();
        InvestorAuthorityProfilesType investorAuthorityProfilesType = mock(InvestorAuthorityProfilesType.class);
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenReturn(investorAuthorityProfilesType);
        //investorsType.getInvestor().addAll(getNewDirectors(getNewPersons(directorsSecretariesSignatories), accountType, adviser, dealer, form.getAccountSettings()));
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(ClientApplicationFormFactory.getNewClientApplicationForm(form), adviser, dealer, serviceErrors);
        assertThat(investors.getInvestor().get(0).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.COMPANY_DIRECTOR_ROLE));
        assertThat(investors.getInvestor().get(0).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE));
        assertThat(investors.getInvestor().get(1).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.COMPANY_SECRETARY_ROLE));
    }

    @Test
    public void getCompanyDetails_WhenCompanyDirectorBenOwner_ShouldHaveBenOwnerRole() throws Exception {
        Map form = getCompanyBenOwnerFromJson();
        InvestorAuthorityProfilesType investorAuthorityProfilesType = mock(InvestorAuthorityProfilesType.class);
        when(authorityTypeBuilder.getInvestorAuthorityProfilesType(any(PaymentAuthorityEnum.class))).thenReturn(investorAuthorityProfilesType);
        InvestorsType investors = investorsTypeBuilder.getInvestorsType(ClientApplicationFormFactory.getNewClientApplicationForm(form), adviser, dealer, serviceErrors);
        assertThat(investors.getInvestor().get(0).getPartyRoleInRelatedOrganisation(), hasItem(PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE));
    }

    @Test
    public void getPostalAddress_forGcmAddress() throws Exception {
        IClientApplicationForm form = getMockIndividualApplicationForm();
        IExtendedPersonDetailsForm investor = getInvestor(false, true);
        when(form.getInvestors()).thenReturn(Arrays.asList(investor));

        investorsTypeBuilder.getInvestorsType(form, adviser, dealer, new ServiceErrorsImpl());
        ArgumentCaptor<IAddressForm> argument1 = ArgumentCaptor.forClass(IAddressForm.class);
        ArgumentCaptor<AddressType> argument2 = ArgumentCaptor.forClass(AddressType.class);
        ArgumentCaptor<Boolean> argument3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<ServiceErrors> argument4 = ArgumentCaptor.forClass(ServiceErrors.class);
        verify(addressTypeBuilder, times(1)).getDefaultAddressType(argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertEquals(argument3.getValue(), true);
    }

    @Test
    public void getPostalAddress_forAddress() throws Exception {
        IClientApplicationForm form = getMockIndividualApplicationForm();
        IExtendedPersonDetailsForm investor = getInvestor(false, false);
        when(form.getInvestors()).thenReturn(Arrays.asList(investor));
        when(form.getInvestors().get(0).hasPostalAddress()).thenReturn(true);

        investorsTypeBuilder.getInvestorsType(form, adviser, dealer, new ServiceErrorsImpl());
        ArgumentCaptor<IAddressForm> argument1 = ArgumentCaptor.forClass(IAddressForm.class);
        ArgumentCaptor<AddressType> argument2 = ArgumentCaptor.forClass(AddressType.class);
        ArgumentCaptor<Boolean> argument3 = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<ServiceErrors> argument4 = ArgumentCaptor.forClass(ServiceErrors.class);
        verify(addressTypeBuilder, times(1)).getDefaultAddressType(argument1.capture(), argument2.capture(), argument3.capture(), argument4.capture());
        assertEquals(argument3.getValue(), false);
    }

    private IExtendedPersonDetailsForm getInvestor(boolean isExisting, boolean isGcm) {
        IExtendedPersonDetailsForm investor = mock(IExtendedPersonDetailsForm.class);
        when(investor.isExistingPerson()).thenReturn(isExisting);
        when(investor.isGcmRetrievedPerson()).thenReturn(isGcm);
        return investor;
    }

    private Map<String, Object> getIndividualSmsfFromJson() throws IOException {
        Map<String, Object> stringObjectMap = readJsonFromFile("client_application_individual_smsf.json");
        IdInsertion.mergeIds(stringObjectMap);
        return stringObjectMap;
    }

    private Map<String, Object> getCompanyFromJson() throws IOException {
        Map<String, Object> stringObjectMap = readJsonFromFile("client_application_company_form_data_minimal.json");
        IdInsertion.mergeIds(stringObjectMap);
        return stringObjectMap;
    }

    private Map<String, Object> getCompanyBenOwnerFromJson() throws IOException {
        Map<String, Object> stringObjectMap = readJsonFromFile("client_application_company_from_data_onlyBeneficiaryOwner.json");
        IdInsertion.mergeIds(stringObjectMap);
        return stringObjectMap;
    }

    private IClientApplicationForm getMockSmsfIndividualApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL_SMSF);
        ISmsfForm ISmsfForm = getMockISmsfForm();
        when(form.getSmsf()).thenReturn(ISmsfForm);
        IShareholderAndMembersForm IShareholderAndMembersForm = mock(IShareholderAndMembersForm.class);
        when(IShareholderAndMembersForm.hasbeneficiaryClasses()).thenReturn(false);
        when(form.getShareholderAndMembers()).thenReturn(IShareholderAndMembersForm);
        return form;
    }

    private IClientApplicationForm getMockSmsfCorporateApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_SMSF);

        ICompanyForm companyForm = mock(ICompanyForm.class);
        when(companyForm.getABN()).thenReturn("");
        when(companyForm.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.COMPANY);
        when(companyForm.getSourceOfWealth()).thenReturn("Business profits");

        when(form.getCompanyTrustee()).thenReturn(companyForm);

        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        when(form.getDirectors()).thenReturn(Arrays.<IExtendedPersonDetailsForm>asList(directorDetailsForm));

        IShareholderAndMembersForm IShareholderAndMembersForm = mock(IShareholderAndMembersForm.class);
        when(form.getShareholderAndMembers()).thenReturn(IShareholderAndMembersForm);

        ISmsfForm ISmsfForm = getMockISmsfForm();
        when(form.getSmsf()).thenReturn(ISmsfForm);
        return form;
    }

    private IClientApplicationForm getMockSMSFApplicationForm(IClientApplicationForm.AccountType accountType) {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        when(form.getAccountType()).thenReturn(accountType);

        IExtendedPersonDetailsForm trusteeDetailsForm = mock(ITrusteeDetailsForm.class);
        when(trusteeDetailsForm.isBeneficiary()).thenReturn(true);
        when(form.getTrustees()).thenReturn(Arrays.asList(trusteeDetailsForm));

        ICompanyForm companyForm = mock(ICompanyForm.class);
        when(companyForm.getABN()).thenReturn("");
        when(companyForm.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.SMSF);
        when(companyForm.getSourceOfWealth()).thenReturn("Business profits");

        when(form.getCompanyTrustee()).thenReturn(companyForm);

        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        when(form.getDirectors()).thenReturn(Arrays.<IExtendedPersonDetailsForm>asList(directorDetailsForm));

        IShareholderAndMembersForm IShareholderAndMembersForm = mock(IShareholderAndMembersForm.class);
        when(form.getShareholderAndMembers()).thenReturn(IShareholderAndMembersForm);

        ISmsfForm ISmsfForm = getMockISmsfForm();
        when(form.getSmsf()).thenReturn(ISmsfForm);
        return form;
    }

    private IClientApplicationForm getMockCorporateFamilyTrustApplicationForm() {
        IClientApplicationForm form = getMockCorporateTrustApplicationForm();
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        ITrustForm trust = form.getTrust();
        when(trust.getSourceOfWealth()).thenReturn("Business profits");
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.FAMILY);
        when(trust.getIdentityDocument()).thenReturn(mock(ITrustIdentityVerificationForm.class));
        when(trust.getDescription()).thenReturn("family");

        return form;
    }

    private IClientApplicationForm getMockCorporateRegulatedTrustApplicationForm() {
        IClientApplicationForm form = getMockCorporateTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(trust.getRegulatorLicenseNumber()).thenReturn("MY REGULATOR LICENSING NUMBER");
        return form;
    }

    private IClientApplicationForm getMockCorporateRegisteredMISTrustApplicationForm() {
        IClientApplicationForm form = getMockCorporateTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.REGISTERED_MIS);
        when(trust.getArsn()).thenReturn("MY ARSN NUMBER");
        return form;
    }

    private IClientApplicationForm getMockCorporateGovtSuperTrustApplicationForm() {
        IClientApplicationForm form = getMockCorporateTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.GOVT_SUPER);
        when(trust.getNameOfLegislation()).thenReturn("NAME OF LEGISLATION");
        return form;
    }

    private IClientApplicationForm getMockCorporateTrustApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.CORPORATE_TRUST);

        ICompanyForm companyForm = mock(ICompanyForm.class);
        when(companyForm.getABN()).thenReturn("");
        when(companyForm.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.COMPANY);
        when(form.getCompanyTrustee()).thenReturn(companyForm);

        IDirectorDetailsForm directorDetailsForm = mock(IDirectorDetailsForm.class);
        when(directorDetailsForm.isBeneficiary()).thenReturn(true);
        when(directorDetailsForm.isShareholder()).thenReturn(true);
        when(directorDetailsForm.getRole()).thenReturn(IOrganisationForm.OrganisationRole.DIRECTOR);
        when(form.getDirectors()).thenReturn(Arrays.<IExtendedPersonDetailsForm>asList(directorDetailsForm));

        ITrustForm trustDetailsForm = getMockTrustDetailsForm();
        when(trustDetailsForm.getBusinessName()).thenReturn("My trust business name");
        when(form.getTrust()).thenReturn(trustDetailsForm);

        IShareholderAndMembersForm IShareholderAndMembersForm = mock(IShareholderAndMembersForm.class);
        when(IShareholderAndMembersForm.getBeneficiaryClassDetails()).thenReturn("Beneficiary class details");
        when(IShareholderAndMembersForm.hasbeneficiaryClasses()).thenReturn(true);
        when(form.getShareholderAndMembers()).thenReturn(IShareholderAndMembersForm);

        return form;
    }

    private IClientApplicationForm getMockIndividualTrustOtherApplicationForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.OTHER);
        when(trust.getIdentityDocument()).thenReturn(mock(ITrustIdentityVerificationForm.class));
        when(trust.getDescription()).thenReturn("other");
        when(trust.getDescriptionOther()).thenReturn("MyTRUST DESCRIPTION OTHER");
        setTrustTypeStaticReferenceCode("Other");
        return form;
    }

    private IClientApplicationForm getMockIndividualTrustSettlorOrgForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.OTHER);
        when(trust.getIdentityDocument()).thenReturn(mock(ITrustIdentityVerificationForm.class));
        when(trust.getDescription()).thenReturn("other");
        when(trust.getDescriptionOther()).thenReturn("MyTRUST DESCRIPTION OTHER");
        when(trust.hasSettlorOfTrust()).thenReturn(true);
        when(trust.getSettlorOfTrust()).thenReturn(ITrustForm.SettlorofTrustType.ORGANISATION);
        when(trust.getOrganisationName()).thenReturn("TesOrgName");
        setTrustTypeStaticReferenceCode("Other");
        return form;
    }

    private IClientApplicationForm getMockIndividualTrustSettlorIndForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.OTHER);
        when(trust.getIdentityDocument()).thenReturn(mock(ITrustIdentityVerificationForm.class));
        when(trust.getDescription()).thenReturn("other");
        when(trust.getDescriptionOther()).thenReturn("MyTRUST DESCRIPTION OTHER");
        when(trust.hasSettlorOfTrust()).thenReturn(true);
        when(trust.getSettlorOfTrust()).thenReturn(ITrustForm.SettlorofTrustType.INDIVIDUAL);
        when(trust.getFirstName()).thenReturn("firstName");
        when(trust.getLastName()).thenReturn("lastName");
        when(trust.getMiddleName()).thenReturn("middleName");
        when(trust.getTitle()).thenReturn("Mr");
        setTrustTypeStaticReferenceCode("Other");
        return form;
    }

    private void setTrustTypeStaticReferenceCode(String userId) {
        CodeImpl codeImpl = Mockito.mock(CodeImpl.class);
        when(codeImpl.getName()).thenReturn(userId);
        when(staticIntegrationService.loadCodeByUserId(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn(codeImpl);
    }

    private IClientApplicationForm getMockIndividualFamilyTrustApplicationForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.FAMILY);
        when(trust.getIdentityDocument()).thenReturn(mock(ITrustIdentityVerificationForm.class));
        when(trust.getDescription()).thenReturn("family");
        setTrustTypeStaticReferenceCode("Discretionary/family trust");
        return form;
    }

    private IClientApplicationForm getMockIndividualRegulatedTrustApplicationForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.REGULATED);
        when(trust.getRegulatorLicenseNumber()).thenReturn("MY REGULATOR LICENSING NUMBER");
        return form;
    }

    private IClientApplicationForm getMockIndividualRegisteredMISTrustApplicationForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.REGISTERED_MIS);
        when(trust.getArsn()).thenReturn("MY ARSN NUMBER");
        return form;
    }

    private IClientApplicationForm getMockIndividualGovtSuperTrustApplicationForm() {
        IClientApplicationForm form = getMockIndividualTrustApplicationForm();
        ITrustForm trust = form.getTrust();
        when(trust.getTrustType()).thenReturn(ITrustForm.TrustType.GOVT_SUPER);
        when(trust.getNameOfLegislation()).thenReturn("NAME OF LEGISLATION");
        return form;
    }

    private IClientApplicationForm getMockIndividualTrustApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL_TRUST);

        when(form.getCompanyTrustee()).thenReturn(null);

        IExtendedPersonDetailsForm IExtendedPersonDetailsForm = mock(ITrusteeDetailsForm.class);
        when(IExtendedPersonDetailsForm.isBeneficiary()).thenReturn(true);
        when(form.getTrustees()).thenReturn(Arrays.asList(IExtendedPersonDetailsForm));

        ITrustForm trustDetailsForm = getMockTrustDetailsForm();
        when(trustDetailsForm.getBusinessName()).thenReturn("MY INDIVIDUAL TRUST BUSINESS NAME");
        when(form.getTrust()).thenReturn(trustDetailsForm);

        IShareholderAndMembersForm IShareholderAndMembersForm = mock(IShareholderAndMembersForm.class);
        when(IShareholderAndMembersForm.getBeneficiaryClassDetails()).thenReturn("BENEFICIARY CLASS DETAILS");
        when(IShareholderAndMembersForm.hasbeneficiaryClasses()).thenReturn(true);
        when(form.getShareholderAndMembers()).thenReturn(IShareholderAndMembersForm);

        return form;
    }

    private ISmsfForm getMockISmsfForm() {
        ISmsfForm ISmsfForm = mock(ISmsfForm.class);
        when(ISmsfForm.getCorrelationSequenceNumber()).thenReturn(2);
        when(ISmsfForm.getABN()).thenReturn("DUMMY ABN");
        when(ISmsfForm.getName()).thenReturn("DUMMY NAME");
        when(ISmsfForm.getAnzsicCode()).thenReturn("DUMMY ANZSIC");
        when(ISmsfForm.getOrganisationType()).thenReturn(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType.SMSF);
        when(ISmsfForm.getIDVDocIssuer()).thenReturn("DUMMY SEARCH LOOKUP");
        when(ISmsfForm.getRegistrationState()).thenReturn("NSW");
        when(ISmsfForm.getDateOfRegistration()).thenReturn(XMLGregorianCalendarUtil.getXMLGregorianCalendar("10/10/2000", "dd/MM/yyyy"));
        return ISmsfForm;
    }

    private ITrustForm getMockTrustDetailsForm() {
        ITrustForm trustDetailsForm = mock(ITrustForm.class);

        ITaxDetailsForm mockTaxDetails = mock(ITaxDetailsForm.class);
        when(mockTaxDetails.getTaxFileNumber()).thenReturn("123456782");

        when(trustDetailsForm.getName()).thenReturn("MY TRUST");
        when(trustDetailsForm.getDateOfRegistration()).thenReturn(XMLGregorianCalendarUtil.getXMLGregorianCalendar("01/01/1990", "dd/MM/yyyy"));
        when(trustDetailsForm.getRegistrationState()).thenReturn("My registration state");
        when(trustDetailsForm.getAnzsicCode()).thenReturn("1234");
        when(trustDetailsForm.getTaxDetails()).thenReturn(mockTaxDetails);
        when(trustDetailsForm.getRegisteredAddress()).thenReturn(mock(IAddressForm.class));
        return trustDetailsForm;
    }

    private ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType findInvestorTypeWithCorrelationSequence(List<ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType> investors, final String id) {
        return Lambda.selectFirst(investors, new LambdaMatcher<ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType>() {
            @Override
            protected boolean matchesSafely(ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType item) {
                return id.equals(item.getInvestorDetails().getCorrelationSequenceNumber());
            }
        });
    }

    private IClientApplicationForm getMockIndividualApplicationForm() {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.INDIVIDUAL);

        IExtendedPersonDetailsForm investor = mock(IExtendedPersonDetailsForm.class);
        when(form.getInvestors()).thenReturn(Arrays.asList(investor));
        return form;
    }
}
