package com.bt.nextgen.api.tdmaturities.service;

import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.api.tdmaturities.model.TDMaturitiesDto;
import com.bt.nextgen.api.tdmaturities.model.TDMaturitiesStatus;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.termdepositstatus.TermDepositMaturityImpl;
import com.bt.nextgen.service.avaloq.termdepositstatus.TermDepositMaturityRequestImpl;
import com.bt.nextgen.service.avaloq.termdepositstatus.TermDepositMaturityResponseImpl;
import com.bt.nextgen.service.avaloq.termdepositstatus.TermDepositMaturityStatusImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturity;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturityIntegrationService;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturityStatus;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.Person;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TDMaturitiesDtoServiceTest {

    @InjectMocks
    private TDMaturitiesDtoServiceImpl tdmaturitiesDTOService;

    @Mock
    TermDepositMaturityIntegrationService termDepositMaturityIntegrationService;

    @Mock
    AssetIntegrationService assetIntegrationService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BankDateIntegrationService bankDateIntegrationService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private TermDepositPresentationService termDepositPresentationService;

    @Mock
    CmsService cmsService;

    private WrapAccountImpl account;
    private ProductImpl product;
    private List<TermDepositMaturity> tdmaturities;
    private List<ApiSearchCriteria> criteriaList;
    private ServiceErrors serviceErrors;
    private TermDepositPresentation termDepositPresentation;

    @Before
    public void setup() throws Exception {
        account = new WrapAccountImpl();
        account.setAccountName("Wrap Account Name 1");

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        Mockito.when(assetIntegrationService.getIssuerForBrand(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn("brand");

        product = new ProductImpl();
        product.setProductName("Investment 1");

        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(product);

        Person broker = new Person() {

            @Override
            public Collection<AccountKey> getWrapAccounts() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Collection<ClientDetail> getRelatedPersons() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public DateTime getCloseDate() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getFullName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ClientType getClientType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public InvestorType getLegalForm() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Email> getEmails() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Phone> getPhones() {
                // TODO Auto-generated method stub
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
            public ClientKey getClientKey() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setClientKey(ClientKey personId) {
                // TODO Auto-generated method stub

            }

            @Override
            public String getBankReferenceId() {
                // TODO Auto-generated method stub
                return "201601682";
            }

            @Override
            public UserKey getBankReferenceKey() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public String getFirstName() {
                // TODO Auto-generated method stub
                return "first name";
            }

            @Override
            public String getMiddleName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getLastName() {
                // TODO Auto-generated method stub
                return "last name";
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                // TODO Auto-generated method stub
                return null;
            }

        };

        Mockito.when(userProfileService.getUserId()).thenReturn("201601682");
        Mockito.when(brokerService.getPersonDetailsOfBrokerUser(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(broker);

        Mockito.when(cmsService.getContent(Constants.TD_BRAND_PREFIX + "brand")).thenReturn("Bank of Melbourne");

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(new DateTime());

        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);

        TermDepositMaturityRequestImpl request = new TermDepositMaturityRequestImpl();
        request.setOEIdentifier("201601682");
        request.setStartDate(new DateTime().withTimeAtStartOfDay());
        request.setEndDate(new DateTime().withTimeAtStartOfDay().plusDays(1));
        request.setVerificationDate(new DateTime());

        criteriaList = new ArrayList<ApiSearchCriteria>();
        {
            ApiSearchCriteria startDateCriteria = new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, request
                    .getStartDate().toString(), OperationType.STRING);
            ApiSearchCriteria endDateCriteria = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, request
                    .getEndDate().toString(), OperationType.STRING);
            ApiSearchCriteria oeIdentifierCriteria = new ApiSearchCriteria(Attribute.ADVISER_ID, SearchOperation.EQUALS,
                    EncodedString.fromPlainText("201601682").toString(), OperationType.STRING);
            criteriaList.add(startDateCriteria);
            criteriaList.add(endDateCriteria);
            criteriaList.add(oeIdentifierCriteria);
        }

        tdmaturities = new ArrayList<TermDepositMaturity>();
        {

            List<TermDepositMaturityStatus> tdMaturityItems = new ArrayList<TermDepositMaturityStatus>();
            TermDepositMaturityStatusImpl tdMaturityItem = new TermDepositMaturityStatusImpl();
            tdMaturityItem.setBrandId("80000053");
            tdMaturityItem.setCloseDate(new DateTime());
            tdMaturityItem.setDaysToMaturity(123);
            tdMaturityItem.setInterestFrequency("Monthly (Rolling)");
            tdMaturityItem.setInterestRate(".65");
            tdMaturityItem.setMaturityDate(new DateTime());
            tdMaturityItem.setMaturityInstruction("Pay Amount to Cash Account");
            tdMaturityItem.setOpenDate(new DateTime());
            tdMaturityItem.setPrincipalValue(new BigDecimal("45000"));
            tdMaturityItems.add(tdMaturityItem);
            tdMaturityItem.setBrandId("80000053");
            tdMaturityItem.setPrincipalValue(new BigDecimal("55000"));
            tdMaturityItems.add(tdMaturityItem);

            TermDepositMaturityImpl tdMaturity = new TermDepositMaturityImpl();
            tdMaturity.setAdviserId("29955");
            tdMaturity.setAccountId("36846");
            tdMaturity.setAccountNumber("120011200");
            tdMaturity.setAccountType(AccountStructureType.Individual);
            tdMaturity.setProductId("43326");
            tdMaturity.setTermDepositMaturityStatus(tdMaturityItems);

            tdmaturities.add(tdMaturity);

        }


        TermDepositMaturityResponseImpl responseImpl = new TermDepositMaturityResponseImpl();
        responseImpl.setTermDepositMaturity(tdmaturities);

        Mockito.when(
                termDepositMaturityIntegrationService.loadTermDepositAdviser(Mockito.any(TermDepositMaturityRequestImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(responseImpl);

        termDepositPresentation = new TermDepositPresentation();
        termDepositPresentation.setTerm("6 months");
        Mockito.when(
                termDepositPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(termDepositPresentation);
    }

    @Test
    public void testToTDMaturitiesDto_sizeMatches() {
        List<TDMaturitiesDto> tdaturitiesDto = tdmaturitiesDTOService.toTDMaturitiesDto(tdmaturities, serviceErrors);
        assertNotNull(tdaturitiesDto);
        Assert.assertEquals(2, tdaturitiesDto.size());
    }

    @Test
    public void testToTDMaturitiesDto_valueMatches_whenTermDepositMaturity_passed() {
        List<TDMaturitiesDto> tdaturitiesDto = tdmaturitiesDTOService.toTDMaturitiesDto(tdmaturities, serviceErrors);
        assertNotNull(tdaturitiesDto);
        Assert.assertEquals(2, tdaturitiesDto.size());

        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountKey().getId(),
                EncodedString.toPlainText(tdaturitiesDto.get(0).getAccountId()));
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountNumber(), tdaturitiesDto.get(0).getAccountNumber());
        Assert.assertEquals("Wrap Account Name 1", tdaturitiesDto.get(0).getAccountName());
        Assert.assertEquals("last name, first name", tdaturitiesDto.get(0).getAdviserName());
        Assert.assertEquals("201601682", tdaturitiesDto.get(0).getAdviserId());
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountStructureType().name(), tdaturitiesDto.get(0)
                .getAccountType());
        Assert.assertEquals(tdmaturities.get(0).getProduct().getProductKey().getId(), tdaturitiesDto.get(0).getProductId());
        Assert.assertEquals(2, tdaturitiesDto.size());

        Assert.assertEquals("brand", tdaturitiesDto.get(0).getBrandId());
        Assert.assertEquals("Bank of Melbourne", tdaturitiesDto.get(0).getBrandName());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getInterestFrequency(),
                tdaturitiesDto.get(0).getInterestFrequency());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getInterestRate(), tdaturitiesDto.get(0)
                .getInterestRate());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getMaturityInstruction()
                .getDisplayDescription(),
                tdaturitiesDto
                .get(0).getMaturityInstruction());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getCloseDate(), tdaturitiesDto.get(0)
                .getCloseDate());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getDaysToMaturity(), tdaturitiesDto.get(0)
                .getDaysToMaturity());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getOpenDate(), tdaturitiesDto.get(0)
                .getOpenDate());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getPrincipalValue(), tdaturitiesDto.get(0)
                .getPrincipalValue());
        Assert.assertEquals("6 months", tdaturitiesDto.get(0).getTerm());
    }

    @Test
    public void testToTDMaturitiesDtoStatus() {

        DateTime futureDate = new DateTime().plusMonths(24);
        DateTime previousDate = new DateTime().minusMonths(24);

        // TM
        Assert.assertEquals(TDMaturitiesStatus.OPEN.getStatus(), tdmaturitiesDTOService.getStatus(null, futureDate));

        // MT
        Assert.assertEquals(TDMaturitiesStatus.MATURED.getStatus(), tdmaturitiesDTOService.getStatus(null, previousDate));

        // TMC , TCM
        Assert.assertEquals(TDMaturitiesStatus.OPEN.getStatus(),
                tdmaturitiesDTOService.getStatus(futureDate, futureDate.plusMonths(3)));
        Assert.assertEquals(TDMaturitiesStatus.OPEN.getStatus(),
                tdmaturitiesDTOService.getStatus(futureDate.plusMonths(3), futureDate));

        // MTC
        Assert.assertEquals(TDMaturitiesStatus.MATURED.getStatus(), tdmaturitiesDTOService.getStatus(futureDate, previousDate));

        // MCT CTM CMT
        Assert.assertEquals(TDMaturitiesStatus.WITHDRAWN.getStatus(),
                tdmaturitiesDTOService.getStatus(previousDate, previousDate.minusMonths(3)));
        Assert.assertEquals(TDMaturitiesStatus.WITHDRAWN.getStatus(), tdmaturitiesDTOService.getStatus(previousDate, futureDate));
        Assert.assertEquals(TDMaturitiesStatus.WITHDRAWN.getStatus(),
                tdmaturitiesDTOService.getStatus(previousDate.minusMonths(3), previousDate));

    }

    @Test
    public void testSearchTdMaturities() {
        List<TDMaturitiesDto> tdMaturitiesDto = tdmaturitiesDTOService.search(criteriaList, serviceErrors);
        Assert.assertNotNull(tdMaturitiesDto);
        Assert.assertEquals(2, tdMaturitiesDto.size());
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountKey().getId(),
                EncodedString.toPlainText(tdMaturitiesDto.get(0).getAccountId()));
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountNumber(), tdMaturitiesDto.get(0).getAccountNumber());
        Assert.assertEquals("201601682", tdMaturitiesDto.get(0).getAdviserId());
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountStructureType().name(), tdMaturitiesDto.get(0)
                .getAccountType());

        Assert.assertEquals("brand", tdMaturitiesDto.get(0).getBrandId());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getInterestFrequency(), tdMaturitiesDto
                .get(0).getInterestFrequency());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getInterestRate(), tdMaturitiesDto.get(0)
                .getInterestRate());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getMaturityInstruction()
                .getDisplayDescription(),
                tdMaturitiesDto
                .get(0).getMaturityInstruction());
    }

    @Test
    public void testSearchTdMaturities_multiAdviserSearch() {
        TermDepositMaturityRequestImpl request = new TermDepositMaturityRequestImpl();
        request.setOEIdentifier("201654184");
        request.setStartDate(new DateTime().withTimeAtStartOfDay());
        request.setEndDate(new DateTime().withTimeAtStartOfDay().plusDays(90));
        request.setVerificationDate(new DateTime());

        criteriaList = new ArrayList<ApiSearchCriteria>();
        {
            ApiSearchCriteria startDateCriteria = new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, request
                    .getStartDate().toString(), OperationType.STRING);
            ApiSearchCriteria endDateCriteria = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, request
                    .getEndDate().toString(), OperationType.STRING);
            ApiSearchCriteria oeIdentifierCriteria = new ApiSearchCriteria(Attribute.ADVISER_ID, SearchOperation.EQUALS,
                    EncodedString.fromPlainText("201654184").toString(), OperationType.STRING);
            ApiSearchCriteria multiAdviserSearchCriteria = new ApiSearchCriteria("multiAdviserSearch", SearchOperation.EQUALS,
                    "true", OperationType.STRING);
            criteriaList.add(startDateCriteria);
            criteriaList.add(endDateCriteria);
            criteriaList.add(oeIdentifierCriteria);
            criteriaList.add(multiAdviserSearchCriteria);
        }
        List<TDMaturitiesDto> tdMaturitiesDto = tdmaturitiesDTOService.search(criteriaList, serviceErrors);
        Assert.assertNotNull(tdMaturitiesDto);
        Assert.assertEquals(2, tdMaturitiesDto.size());
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountKey().getId(),
                EncodedString.toPlainText(tdMaturitiesDto.get(0).getAccountId()));
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountNumber(), tdMaturitiesDto.get(0).getAccountNumber());
        Assert.assertEquals(tdmaturities.get(0).getAccount().getAccountStructureType().name(), tdMaturitiesDto.get(0)
                .getAccountType());
        Assert.assertEquals("brand", tdMaturitiesDto.get(0).getBrandId());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getInterestFrequency(), tdMaturitiesDto
                .get(0).getInterestFrequency());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getInterestRate(), tdMaturitiesDto.get(0)
                .getInterestRate());
        Assert.assertEquals(tdmaturities.get(0).getTermDepositMaturityStatus().get(0).getMaturityInstruction()
                .getDisplayDescription(),
                tdMaturitiesDto
                .get(0).getMaturityInstruction());
        Assert.assertEquals("Withdrawn", tdMaturitiesDto.get(0).getStatus());
        Assert.assertEquals("Investment 1", tdMaturitiesDto.get(0).getProductName());
    }

    @Test
    public void testTdMaturities_With_ReinvestMaturityInstruction() {
        TermDepositMaturityRequestImpl request = new TermDepositMaturityRequestImpl();
        request.setOEIdentifier("201654184");
        request.setStartDate(new DateTime().withTimeAtStartOfDay());
        request.setEndDate(new DateTime().withTimeAtStartOfDay().plusDays(90));
        request.setVerificationDate(new DateTime());

        criteriaList = new ArrayList<ApiSearchCriteria>();
        {
            ApiSearchCriteria startDateCriteria = new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, request
                    .getStartDate().toString(), OperationType.STRING);
            ApiSearchCriteria endDateCriteria = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, request
                    .getEndDate().toString(), OperationType.STRING);
            ApiSearchCriteria oeIdentifierCriteria = new ApiSearchCriteria(Attribute.ADVISER_ID, SearchOperation.EQUALS,
                    EncodedString.fromPlainText("201654184").toString(), OperationType.STRING);
            ApiSearchCriteria multiAdviserSearchCriteria = new ApiSearchCriteria("multiAdviserSearch", SearchOperation.EQUALS,
                    "true", OperationType.STRING);
            criteriaList.add(startDateCriteria);
            criteriaList.add(endDateCriteria);
            criteriaList.add(oeIdentifierCriteria);
            criteriaList.add(multiAdviserSearchCriteria);
        }

        List<TermDepositMaturity> tdmaturities1 = new ArrayList<TermDepositMaturity>();


        List<TermDepositMaturityStatus> tdMaturityItems = new ArrayList<TermDepositMaturityStatus>();
        TermDepositMaturityStatusImpl tdMaturityItem = new TermDepositMaturityStatusImpl();
        tdMaturityItem.setBrandId("80000053");
        tdMaturityItem.setCloseDate(new DateTime());
        tdMaturityItem.setDaysToMaturity(123);
        tdMaturityItem.setInterestFrequency("Monthly (Rolling)");
        tdMaturityItem.setInterestRate(".65");
        tdMaturityItem.setMaturityDate(new DateTime());
        tdMaturityItem.setOpenDate(new DateTime());
        tdMaturityItem.setPrincipalValue(new BigDecimal("45000"));
        tdMaturityItems.add(tdMaturityItem);
        tdMaturityItem.setBrandId("80000053");
        tdMaturityItem.setPrincipalValue(new BigDecimal("55000"));
        tdMaturityItems.add(tdMaturityItem);

        TermDepositMaturityImpl tdMaturity = new TermDepositMaturityImpl();
        tdMaturity.setAdviserId("29955");
        tdMaturity.setAccountId("36846");
        tdMaturity.setAccountNumber("120011200");
        tdMaturity.setAccountType(AccountStructureType.Individual);
        tdMaturity.setProductId("43326");
        tdMaturity.setTermDepositMaturityStatus(tdMaturityItems);

        tdmaturities1.add(tdMaturity);



        TermDepositMaturityResponseImpl responseImpl1 = new TermDepositMaturityResponseImpl();
        responseImpl1.setTermDepositMaturity(tdmaturities1);

        Mockito.when(
                termDepositMaturityIntegrationService.loadTermDepositAdviser(Mockito.any(TermDepositMaturityRequestImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(responseImpl1);

        List<TDMaturitiesDto> tdMaturitiesDto = tdmaturitiesDTOService.search(criteriaList, serviceErrors);
        Assert.assertEquals("Deposit all money into cash",
 tdMaturitiesDto.get(0).getMaturityInstruction());
    }
}
