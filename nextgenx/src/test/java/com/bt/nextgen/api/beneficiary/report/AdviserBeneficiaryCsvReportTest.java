package com.bt.nextgen.api.beneficiary.report;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryCsvDto;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.*;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.*;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.*;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class AdviserBeneficiaryCsvReportTest {

    @InjectMocks
    private AdviserBeneficiaryCsvReport adviserBeneficiaryCsvReport;

    @Mock
    private BeneficiaryDtoService beneficiaryDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ContentDtoService contentService;

    @Before
    public void setUp() {
        List<BeneficiaryDto> beneficiaryDtos = new ArrayList<>();

        com.bt.nextgen.api.account.v3.model.AccountKey accountKey1 = new com.bt.nextgen.api.account.v3.model.AccountKey("5DD60351C23FFAEA");
        com.bt.nextgen.api.account.v3.model.AccountKey accountKey2 = new com.bt.nextgen.api.account.v3.model.AccountKey("905A26A8645DAED9");
        com.bt.nextgen.api.account.v3.model.AccountKey accountKey3 = new com.bt.nextgen.api.account.v3.model.AccountKey("DE8A159D1657CB58");
        com.bt.nextgen.api.account.v3.model.AccountKey accountKey4 = new com.bt.nextgen.api.account.v3.model.AccountKey("837AB44A1357D633");
        com.bt.nextgen.api.account.v3.model.AccountKey accountKey5 = new com.bt.nextgen.api.account.v3.model.AccountKey("837AB44A1347D633");

        BeneficiaryDto beneficiaryDto1 = new BeneficiaryDto();
        BeneficiaryDto beneficiaryDto2 = new BeneficiaryDto();
        BeneficiaryDto beneficiaryDto3 = new BeneficiaryDto();
        BeneficiaryDto beneficiaryDto4 = new BeneficiaryDto();
        BeneficiaryDto beneficiaryDto5 = new BeneficiaryDto();
        BeneficiaryDto beneficiaryDto6 = new BeneficiaryDto();

        beneficiaryDto1.setKey(accountKey1);
        beneficiaryDto1.setBeneficiaries(new ArrayList<Beneficiary>());

        beneficiaryDto2.setKey(accountKey2);
        beneficiaryDto2.setBeneficiariesLastUpdatedTime(new DateTime());
        beneficiaryDto2.setBeneficiaries(new ArrayList<Beneficiary>());

        beneficiaryDto3.setKey(accountKey3);
        beneficiaryDto3.setBeneficiariesLastUpdatedTime(new DateTime());
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setFirstName("Jane");
        beneficiary.setLastName("Doe");
        List<Beneficiary> beneficiaries = new ArrayList<>();
        beneficiaries.add(beneficiary);
        beneficiaryDto3.setBeneficiaries(beneficiaries);

        beneficiaryDto4.setKey(accountKey4);
        beneficiaryDto4.setBeneficiariesLastUpdatedTime(new DateTime());
        beneficiaryDto4.setBeneficiaries(new ArrayList<Beneficiary>());

        beneficiaryDto5.setKey(accountKey4);
        beneficiaryDto5.setBeneficiariesLastUpdatedTime(new DateTime());
        beneficiaryDto5.setBeneficiaries(new ArrayList<Beneficiary>());

        beneficiaryDto6.setKey(accountKey4);
        beneficiaryDto6.setBeneficiariesLastUpdatedTime(new DateTime());
        beneficiaryDto6.setBeneficiaries(new ArrayList<Beneficiary>());

        beneficiaryDtos.add(beneficiaryDto1);
        beneficiaryDtos.add(beneficiaryDto2);
        beneficiaryDtos.add(beneficiaryDto3);
        beneficiaryDtos.add(beneficiaryDto4);
        beneficiaryDtos.add(beneficiaryDto5);
        beneficiaryDtos.add(beneficiaryDto6);

        PensionAccountDetailImpl wrapAccount1 = new PensionAccountDetailImpl();
        wrapAccount1.setAccountNumber("236598745");
        wrapAccount1.setAccountName("Account1");
        wrapAccount1.setAccountStructureType(AccountStructureType.SUPER);
        wrapAccount1.setSuperAccountSubType(AccountSubType.PENSION);
        wrapAccount1.setPensionType(PensionType.DEATH_BENEFITS);
        wrapAccount1.setOpenDate(new DateTime());
        wrapAccount1.setAccountStatus(AccountStatus.ACTIVE);

        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        wrapAccount2.setAccountNumber("236597745");
        wrapAccount2.setAccountName("Account2");
        wrapAccount2.setAccountStructureType(AccountStructureType.SUPER);
        wrapAccount2.setSuperAccountSubType(AccountSubType.ACCUMULATION);
        wrapAccount2.setOpenDate(new DateTime());
        wrapAccount2.setAccountStatus(AccountStatus.ACTIVE);

        PensionAccountDetailImpl wrapAccount3 = new PensionAccountDetailImpl();
        wrapAccount3.setAccountNumber("236558745");
        wrapAccount3.setAccountName("Account3");
        wrapAccount3.setAccountStructureType(AccountStructureType.SUPER);
        wrapAccount3.setSuperAccountSubType(AccountSubType.PENSION);
        wrapAccount3.setPensionType(PensionType.TTR);
        wrapAccount3.setOpenDate(new DateTime());
        wrapAccount3.setAccountStatus(AccountStatus.ACTIVE);

        PensionAccountDetailImpl wrapAccount4 = new PensionAccountDetailImpl();
        wrapAccount4.setAccountNumber("236558745");
        wrapAccount4.setAccountName("Account4");
        wrapAccount4.setAccountStructureType(AccountStructureType.SUPER);
        wrapAccount4.setSuperAccountSubType(AccountSubType.PENSION);
        wrapAccount4.setPensionType(PensionType.TTR);
        wrapAccount4.setOpenDate(new DateTime());
        wrapAccount4.setAccountStatus(AccountStatus.CLOSE);

        WrapAccountImpl wrapAccount5 = new WrapAccountImpl();
        wrapAccount5.setAccountNumber("236577745");
        wrapAccount5.setAccountName("Account5");
        wrapAccount5.setAccountStructureType(AccountStructureType.Company);
        wrapAccount5.setOpenDate(new DateTime());
        wrapAccount5.setAccountStatus(AccountStatus.ACTIVE);

        PensionAccountDetailImpl wrapAccount6 = new PensionAccountDetailImpl();
        wrapAccount6.setAccountNumber("236558745");
        wrapAccount6.setAccountName("Account6");
        wrapAccount6.setAccountStructureType(AccountStructureType.SUPER);
        wrapAccount6.setSuperAccountSubType(AccountSubType.PENSION);
        wrapAccount6.setPensionType(PensionType.TTR_RETIR_PHASE);
        wrapAccount6.setOpenDate(new DateTime());
        wrapAccount6.setAccountStatus(AccountStatus.ACTIVE);


        Mockito.when(beneficiaryDtoService.getBeneficiaryDetails(Matchers.anyString(), (ServiceErrors) Matchers.anyObject(), Matchers.anyString()))
                .thenReturn(beneficiaryDtos);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((AccountKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject()))
                .thenReturn(wrapAccount1)
                .thenReturn(wrapAccount2)
                .thenReturn(wrapAccount3)
                .thenReturn(wrapAccount4)
                .thenReturn(wrapAccount5)
                .thenReturn(wrapAccount6);
    }

    @Test
    public void testGetBeneficiaryDetailsWithoutCache() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("useCache", "false");
        Collection<?> results = adviserBeneficiaryCsvReport.getData(paramMap, null);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 5);

        List<BeneficiaryCsvDto> beneficiaryCsvDtos = new ArrayList(results);
        BeneficiaryCsvDto beneficiaryCsvDto1 = beneficiaryCsvDtos.get(0);
        Assert.assertEquals("236598745", beneficiaryCsvDto1.getAccountNumber());
        Assert.assertEquals("Account1", beneficiaryCsvDto1.getAccountName());
        Assert.assertEquals("Pension", beneficiaryCsvDto1.getAccountType());
        Assert.assertEquals("0", beneficiaryCsvDto1.getNumberOfBeneficiaries());

        BeneficiaryCsvDto beneficiaryCsvDto2 = beneficiaryCsvDtos.get(1);
        Assert.assertEquals("236597745", beneficiaryCsvDto2.getAccountNumber());
        Assert.assertEquals("Account2", beneficiaryCsvDto2.getAccountName());
        Assert.assertEquals("Super", beneficiaryCsvDto2.getAccountType());
        Assert.assertEquals("0", beneficiaryCsvDto2.getNumberOfBeneficiaries());

        //when Pension TTR type is Pension TTR - taxed
        BeneficiaryCsvDto beneficiaryCsvDto3 = beneficiaryCsvDtos.get(2);
        Assert.assertEquals("236558745", beneficiaryCsvDto3.getAccountNumber());
        Assert.assertEquals("Account3", beneficiaryCsvDto3.getAccountName());
        Assert.assertEquals(PensionType.TTR.getLabel(), beneficiaryCsvDto3.getAccountType());
        Assert.assertEquals("1", beneficiaryCsvDto3.getNumberOfBeneficiaries());

        //when Pension TTR type is Pension TTR - retirement
        BeneficiaryCsvDto beneficiaryCsvDto4 = beneficiaryCsvDtos.get(4);
        Assert.assertEquals("236558745", beneficiaryCsvDto4.getAccountNumber());
        Assert.assertEquals("Account6", beneficiaryCsvDto4.getAccountName());
        Assert.assertEquals(PensionType.TTR_RETIR_PHASE.getLabel(), beneficiaryCsvDto4.getAccountType());
        Assert.assertEquals("0", beneficiaryCsvDto4.getNumberOfBeneficiaries());
    }

    @Test
    public void testRetrieveAdviserName() {
        Mockito.when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(getBroker());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("brokerid", "5DD60351C23FFAEA");
        String adviserName = adviserBeneficiaryCsvReport.retrieveAdviserName(paramMap);
        Assert.assertNotNull(adviserName);
        Assert.assertEquals("AdviserFName AdviserLName", adviserName);

        Mockito.when(brokerIntegrationService.getBrokerUser((JobProfileIdentifier) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject()))
                .thenReturn(getBroker());
        paramMap = new HashMap<>();
        adviserName = adviserBeneficiaryCsvReport.retrieveAdviserName(paramMap);
        Assert.assertNotNull(adviserName);
        Assert.assertEquals("AdviserFName AdviserLName", adviserName);
    }

    @Test
    public void testDefaultValue() {
        String defaultValue = adviserBeneficiaryCsvReport.getDefaultValue(null);
        Assert.assertNotNull(defaultValue);
        Assert.assertEquals("-", defaultValue);
    }

    @Test
    public void testReportType() {
        String reportType = adviserBeneficiaryCsvReport.getReportType(null);
        Assert.assertNotNull(reportType);
        Assert.assertEquals("Panorama Beneficiary List", reportType);
    }

    @Test
    public void testReportFileName() {
        String reportFileName = adviserBeneficiaryCsvReport.getReportFileName(null);
        Assert.assertNotNull(reportFileName);
        Assert.assertEquals("Beneficiary list", reportFileName);
    }

    @Test
    public void testDisclaimer() {
        ContentDto contentDto = new ContentDto("", "Disclaimer");
        Mockito.when(contentService.find((ContentKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(contentDto);
        String disclaimer = adviserBeneficiaryCsvReport.getDisclaimer(null);
        Assert.assertNotNull(disclaimer);
        Assert.assertEquals("Disclaimer", disclaimer);

        Mockito.when(contentService.find((ContentKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn(null);
        disclaimer = adviserBeneficiaryCsvReport.getDisclaimer(null);
        Assert.assertNotNull(disclaimer);
        Assert.assertEquals("", disclaimer);
    }

    private BrokerUser getBroker() {
        return new BrokerUser() {
            @Override
            public Collection<BrokerRole> getRoles() {
                return null;
            }

            @Override
            public boolean isRegisteredOnline() {
                return false;
            }

            @Override
            public String getPracticeName() {
                return null;
            }

            @Override
            public String getEntityId() {
                return null;
            }

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
            public String getFirstName() {
                return "Demo";
            }

            @Override
            public String getMiddleName() {
                return null;
            }

            @Override
            public String getLastName() {
                return "Adviser";
            }

            @Override
            public Collection<AccountKey> getWrapAccounts() {
                return null;
            }

            @Override
            public Collection<ClientDetail> getRelatedPersons() {
                return null;
            }

            @Override
            public List<Email> getEmails() {
                return null;
            }

            @Override
            public List<Phone> getPhones() {
                return null;
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return null;
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public DateTime getCloseDate() {
                return null;
            }

            @Override
            public String getFullName() {
                return "AdviserFName" + " " + "AdviserLName";
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                return null;
            }

            @Override
            public InvestorType getLegalForm() {
                return null;
            }

            @Override
            public ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(ClientKey personId) {

            }

            @Override
            public JobKey getJob() {
                return null;
            }

            @Override
            public String getProfileId() {
                return "id1";
            }

            @Override
            public String getBankReferenceId() {
                return "12345";
            }

            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf(getBankReferenceId());
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                return null;
            }
        };
    }
}
