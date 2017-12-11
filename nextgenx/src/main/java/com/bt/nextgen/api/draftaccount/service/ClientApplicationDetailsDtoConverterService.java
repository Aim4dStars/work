package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v2.model.AccountPaymentPermission;
import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.service.ClientListDtoServiceImpl;
import com.bt.nextgen.api.client.util.ClientDetailDtoConverter;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.InvestmentChoiceDto;
import com.bt.nextgen.api.draftaccount.model.PensionEligibility;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IInvestmentChoiceForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.util.BsbFormatter;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.BPClassListImpl;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.accountactivation.AccountStructure;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.bt.nextgen.service.avaloq.accountactivation.AssetInfo;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.avaloq.fees.AnnotatedPercentageFeesComponent;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesMiscType;
import com.bt.nextgen.service.avaloq.fees.OneOffFeesComponent;
import com.bt.nextgen.service.avaloq.fees.RegularFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.avaloq.pension.ConditionOfRelease;
import com.bt.nextgen.service.avaloq.pension.EligibilityCriteria;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.BPClassList;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.util.matcher.MainApplicantRoleMatcher;
import com.bt.nextgen.util.matcher.NominatedInvestorRoleMatcher;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.COMPANY;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.CORPORATE_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.CORPORATE_TRUST;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.INDIVIDUAL;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.INDIVIDUAL_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.INDIVIDUAL_TRUST;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.JOINT;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.SUPER_ACCUMULATION;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.SUPER_PENSION;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.UNKNOWN;
import static com.bt.nextgen.api.draftaccount.service.OrderType.orderOf;
import static org.hamcrest.core.Is.is;

@Service
@Transactional
public class ClientApplicationDetailsDtoConverterService {

    private static final String ESTAMOUNT = "estamount";
    private static final String ONGOING_FEES = "ongoingFees";
    private static final String LICENSEE_FEES = "licenseeFees";
    private static final String OFFLINE_APPROVAL_STR = "offline";
    private static final String NO_EXEMPTION = "No exemption";
    private static final String ADVISER_CONTRIBUTION = "contributionFees";
    private static final String ONE_OFF_FEE = "OneOffFee";
    private static final String REGULAR_FEE = "RegularFee";

    @Autowired
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private IndividualDtoConverter individualDtoConverter;

    @Autowired
    private OrganizationDtoConverter organizationDtoConverter;

    @Autowired
    private OrganizationDtoConverterForApplicationDocument organizationDtoConverterForApplicationDocument;

    @Autowired
    private ClientListDtoServiceImpl clientListDtoService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private InvestorDtoConverterForPersonDetail investorDtoConverterForPersonDetail;

    @Autowired
    private PersonMapperService personMapperService;


    @Autowired
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    @Autowired
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private FeatureTogglesService featureTogglesService;


    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper jsonObjectMapper;

    enum FeesComponentKey{

        EMPLOYER_CONTRIBUTION("employercontribution","employer"),
        REGULAR_DEPOSIT("regulardeposit","deposit"),
        REGULAR_PERSONAL_CONTRIBUTION("regularpersonalcontribution","personal"),
        REGULAR_SPOUSE_CONTRIBUTION("regularspousecontribution","spouse"),
        ONEOFF_DEPOSIT("oneoffdeposit","deposit"),
        ONEOFF_PERSONAL_CONTRIBUTION("oneoffpersonalcontribution","personal"),
        ONEOFF_SPOUSE_CONTRIBUTION("oneoffspousecontribution","spouse");

        private String feeMiscTypeLabel;
        private String feeDisplayOption;

        FeesComponentKey(String feeMiscTypeLabel, String feeDisplayOption){
            this.feeMiscTypeLabel = feeMiscTypeLabel;
            this.feeDisplayOption = feeDisplayOption;
        }

        public static String getFeesDisplayValue(String feeMiscTypeLabelToDerive){

            for(FeesComponentKey feesComponentKey : FeesComponentKey.values()){
                if(feesComponentKey.feeMiscTypeLabel.equalsIgnoreCase(feeMiscTypeLabelToDerive)){
                    return feesComponentKey.feeDisplayOption;
                }
            }
            return StringUtils.EMPTY;
        }

    }
    /*
    For Testing purpose
     */
    public void setJsonObjectMapper(ObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDetailsDtoConverterService.class);

    public ClientApplicationDetailsDto convert(ClientApplication clientApplication, ServiceErrors serviceErrors) {
        LOGGER.info("Retrieving application details from Front-end database");
        ClientApplicationDto clientApplicationDto = clientApplicationDtoConverterService.convertToDto(clientApplication,
                serviceErrors);
        IClientApplicationForm form = clientApplication.getClientApplicationForm();

        String accountType = form.getAccountType().value();

        BrokerDto brokerDto = getBrokerDto(clientApplication.getAdviserPositionId(), serviceErrors);
        List<InvestorDto> investors = toInvestorDtos(getAllPersonDetails(form), serviceErrors, form.getAccountType());
        List<LinkedAccountDto> linkedAccounts = null;
        if(!(form.isDirectAccount() && form.getAccountType() == SUPER_ACCUMULATION)) {
            linkedAccounts = getLinkedAccounts(form);
        }
        if (form.isDirectAccount()) {
            InvestmentChoiceDto investmentChoice = getInvestmentChoiceDto(form);
            PensionEligibility pensionEligibility = toPensionEligibility(form.getPensionEligibility(), serviceErrors);
            return ClientApplicationDetailsDtoFactory.make()
                    .withInvestorsDirectorsTrustees(investors)
                    .withPensionEligibility(pensionEligibility)
                    .withAccountType(accountType).withAccountName(form.getAccountName())
                    .withLinkedAccounts(linkedAccounts)
                    .withInvestmentChoice(investmentChoice)
                    .withApplicationOriginType(form.getApplicationOrigin())
                    .withProductName(clientApplicationDto.getProductName())
                    .withLastModifiedAt(clientApplicationDto.getLastModified()).collect();
        } else {
            AccountSettingsDto accountSettings = getAccountSettings(form, brokerDto);
            Object fees = getFees(form);
            RegisteredEntityDto organisationDetails = getOrganizationDetails(form);
            List<InvestorDto> shareholdersAndMembers = toInvestorDtos(form.getAdditionalShareholdersAndMembers(), serviceErrors, form.getAccountType());
            boolean isNominated = identifyNominatedMembers(investors);
            String majorShareholder = form.getShareholderAndMembers().getMajorShareholder();
            PensionEligibility pensionEligibility = toPensionEligibility(form.getPensionEligibility(), serviceErrors);

            String approvalType = getApprovalType(form.getApplicationApprovalType());
            boolean isOfflineApprovalAccess = brokerDto.isOfflineApproval();
            if (!isOfflineApprovalAccess && OFFLINE_APPROVAL_STR.equals(approvalType)) {
                isOfflineApprovalAccess = true;
                LOGGER.warn(LoggingConstants.ONBOARDING + " client application with ref=" + clientApplicationDto.getReferenceNumber()
                        + " is strangely offline however the broker=" + brokerDto.getKey() + " ie <" + brokerDto.getFullName()
                        + "> does not have offline approval access.");
                // ie if the application was booked as offline, always show it in service ops failed view screen
            }

            return ClientApplicationDetailsDtoFactory.make().withOrganisationDetails(organisationDetails)
                    .withInvestorsDirectorsTrustees(investors).withAdditionalPersons(shareholdersAndMembers)
                    .withAccountType(accountType).withAccountName(form.getAccountName()).withAccountSettings(accountSettings)
                    .withApplicationOriginType(form.getApplicationOrigin())
                    .withLinkedAccounts(linkedAccounts).withDraftFees(fees).withAdviser(brokerDto)
                    .withAccountAvaloqStatus(OnboardingApplicationStatus.processing.toString())
                    .withReferenceNumber(clientApplicationDto.getReferenceNumber())
                    .withProductName(clientApplicationDto.getProductName()).withNominatedFlag(isNominated).withMajorShareholderFlag(majorShareholder)
                    .withLastModifiedAt(clientApplicationDto.getLastModified()).withPensionEligibility(pensionEligibility)
                    .withApprovalType(approvalType).withParentProductName(form.getParentProductName()).withOfflineApprovalAccess(isOfflineApprovalAccess).collect();
        }
    }

    private PensionEligibility toPensionEligibility(IPensionEligibilityForm pensionEligibility, ServiceErrors serviceErrors) {
        if (pensionEligibility != null) {
            PensionEligibility eligibility = new PensionEligibility();
            eligibility.setEligibilityCriteria(clientApplicationDetailsDtoHelperService.eligibilityCriteria(pensionEligibility.getEligibilityCriteria(), serviceErrors));
            eligibility.setConditionRelease(clientApplicationDetailsDtoHelperService.conditionOfRelease(pensionEligibility.getConditionRelease(), serviceErrors));
            return eligibility;
        }
        return null;
    }

    private Object getFees(IClientApplicationForm form) {
        Object fees = form.getFees().getFees();
        if (fees instanceof com.bt.nextgen.api.draftaccount.schemas.v1.fees.Fees) {
            //transform fees into a Map<String, Object> so that fees.jsp works fine (the pre-schema already returns the Map here)
            //TODO: remove this code when we remove the pre-schema form implementations & refactor "fees.jsp" to work with IFeesForm instead
            try {
                String feesJsonString = jsonObjectMapper.writeValueAsString(fees);
                fees = jsonObjectMapper.readValue(feesJsonString, new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                LOGGER.error("error parsing json", e);
                throw new IllegalStateException("error parsing fees", e);
            }
        }
        return fees;
    }

    /**
     * This method will covert the application document returned from Avaloq to Client application details dto
     *
     * @param applicationDocument
     * @param serviceErrors
     * @param userExperience
     * @return
     */
    public ClientApplicationDetailsDto convert(final ApplicationDocumentDetail applicationDocument,
                                               ServiceErrors serviceErrors, UserExperience userExperience) {
        LOGGER.info("Retrieving application details from Avaloq");
        personMapperService.mapPersonAccountSettings(applicationDocument.getPersons(),
                applicationDocument.getAccountSettingsForAllPersons());

        personMapperService.mapPersonAlternateNames(applicationDocument.getPersons(),
                applicationDocument.getAlternateNames());
        personMapperService.mapPersonTaxDetails(applicationDocument.getPersons(),applicationDocument.getPersonIdentityList());

        organisationMapper.mapOrganisationTaxDetails(applicationDocument.getOrganisations(),applicationDocument.getPersonIdentityList());
        LinkedPortfolioDetails linkedPortfolioDetails = applicationDocument.getPortfolio().get(0);
        BrokerDto brokerDto = getBrokerDto(applicationDocument.getAdviserKey(), serviceErrors);

        List<LinkedAccountDto> linkedAccounts = getLinkedAccountsForClientApplication(applicationDocument.getLinkedAccounts());
        Map<String,Boolean> existingCISKeysToOverseasDetails = clientApplicationDetailsDtoHelperService.getExistingPersonsByCISKey(applicationDocument.getAccountNumber(), Arrays.asList(getBrokerIdentifier(brokerDto.getKey().getBrokerId())),serviceErrors);
        List<InvestorDto> persons = getPersonDetailsFromApplicationDocument(applicationDocument.getPersons(), applicationDocument.getSuperAccountSubType(),existingCISKeysToOverseasDetails);

        List<InvestorDto> directors = Lambda
                .filter(having(on(InvestorDto.class).getPrimaryRole(), is(PersonRelationship.DIRECTOR)), persons);
        String productId = linkedPortfolioDetails.getProductId();
        Product product = productIntegrationService.getProductDetail(ProductKey.valueOf(productId), serviceErrors);

        boolean isCorporate = directors != null && !directors.isEmpty();

        IClientApplicationForm.AccountType accountType = getAccountTypeForClientApplication(linkedPortfolioDetails.getAccountType(),
                applicationDocument.getSuperAccountSubType(), isCorporate, applicationDocument.getOrderType());
        String accountTypeValue = accountType.value();

        String accountStatus = getAccountStatus(applicationDocument);
        List<InvestorDto> investorsDirectorsTrustees = getInvestors(persons, accountType);
        PensionEligibility pensionEligibility = null;
        if (SUPER_PENSION == accountType) {
            pensionEligibility = toPensionEligibility(new IPensionEligibilityForm() {
                @Override
                public String getConditionRelease() {
                    ConditionOfRelease cond = applicationDocument.getPensionEligibility().getConditionOfRelease();
                    return cond != null ? cond.toString() : null;
                }

                @Override
                public String getEligibilityCriteria() {
                    EligibilityCriteria criteria = applicationDocument.getPensionEligibility().getEligibilityCriteria();
                    return criteria != null ? criteria.toString() : null;
                }
            }, serviceErrors);
        }

        if (userExperience == UserExperience.DIRECT) {
            return ClientApplicationDetailsDtoFactory.make()
                    .withInvestorsDirectorsTrustees(investorsDirectorsTrustees)
                    .withAccountType(accountTypeValue).withAccountName(getAccountDisplayName(applicationDocument, accountType))
                    .withApplicationOriginType(IClientApplicationForm.ApplicationOriginType.WESTPAC_LIVE.value())
                    .withLinkedAccounts(linkedAccounts)
                    .withAccountAvaloqStatus(accountStatus).withReferenceNumber(null)
                    .withInvestmentChoice(getInvestmentChoiceDto(linkedPortfolioDetails, applicationDocument.getLinkedAccounts()))
                    .withProductName(product.getProductName()).withApplicationOpenDate(applicationDocument.getApplicationOpenDate())
                    .withParentProductName(StringUtils.isNotBlank(product.getParentProductName()) ? product.getParentProductName() : "")
                    .withPensionEligibility(pensionEligibility).collect();
        } else {
            RegisteredEntityDto organisationDetails = getOrganizationDetails(applicationDocument, accountType);
            Map<String, Object> fees = getFeesDto(applicationDocument.getFees());
            List<InvestorDto> shareholdersAndMembers = getShareholdersAndMembers(persons, accountType);
            shareholdersAndMembers.removeAll(investorsDirectorsTrustees);
            boolean isNominated = identifyNominatedMembers(investorsDirectorsTrustees);
            AccountSettingsDto accountSettings = getAccountSettings(applicationDocument, brokerDto);
            String approvalType = getApprovalType(applicationDocument.getApprovalType());
            boolean isOfflineApprovalAccess = brokerDto.isOfflineApproval();

            return ClientApplicationDetailsDtoFactory.make().withOrganisationDetails(organisationDetails)
                    .withInvestorsDirectorsTrustees(investorsDirectorsTrustees).withAdditionalPersons(shareholdersAndMembers)
                    .withAccountType(accountTypeValue).withAccountName(getAccountDisplayName(applicationDocument, accountType))
                    .withApplicationOriginType(IClientApplicationForm.ApplicationOriginType.BT_PANORAMA.value())
                    .withAccountSettings(accountSettings).withLinkedAccounts(linkedAccounts).withDraftFees(fees)
                    .withAdviser(brokerDto).withAccountAvaloqStatus(accountStatus).withReferenceNumber(null)
                    .withProductName(null != product ? product.getProductName() : "").withNominatedFlag(isNominated)
                    .withParentProductName(StringUtils.isNotBlank(product.getParentProductName()) ? product.getParentProductName() : "")
                    .withASIMFlag(userExperience == UserExperience.ASIM)    //We need to check with business for a default product that need to be set
                    .withApprovalType(approvalType).withOfflineApprovalAccess(isOfflineApprovalAccess).withPensionEligibility(pensionEligibility).withApplicationOpenDate(applicationDocument.getApplicationOpenDate()).collect();
        }
    }

    /**
     * Version for integration service.
     * @return "online" or "offline"
     */
    private @Nonnull String getApprovalType(ApprovalType approvalType) {
        return ApprovalType.ONLINE == approvalType ? approvalType.getApprovalType() : OFFLINE_APPROVAL_STR;
    }

    /**
     * Version for form.
     * @return "online" or "offline"
     */
    private @Nonnull String getApprovalType(IClientApplicationForm.ApprovalType approvalType) {
        return approvalType == IClientApplicationForm.ApprovalType.ONLINE ? ApprovalType.ONLINE.getApprovalType() : OFFLINE_APPROVAL_STR;
    }

    private String getAccountStatus(ApplicationDocumentDetail applicationDocument) {
        return applicationDocument.getAccountStatus() != null ? applicationDocument.getAccountStatus().getStatus()
                : AccountStatus.ACTIVE.getStatus();
    }

    private List<InvestorDto> getShareholdersAndMembers(List<InvestorDto> investors,
                                                        IClientApplicationForm.AccountType accountType) {
        switch (accountType) {
            case INDIVIDUAL:
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                return new ArrayList<>();
            default:
                return getShareholdersAndMembers(investors);
        }
    }

    private List<InvestorDto> getShareholdersAndMembers(List<InvestorDto> investors) {
        final Set<InvestorRole> validRoles = new HashSet<>();
        validRoles.add(InvestorRole.Beneficiary);
        validRoles.add(InvestorRole.Member);
        validRoles.add(InvestorRole.Also_Member);
        validRoles.add(InvestorRole.BeneficialOwner);
        validRoles.add(InvestorRole.ControllerOfTrust);
        validRoles.add(InvestorRole.Shareholder);

        Iterable<InvestorDto> shareholdersAndMembers = Iterables.filter(investors, new Predicate<InvestorDto>() {
            @Override
            public boolean apply(@Nullable InvestorDto investor) {
                if (investor != null && investor.getPersonRoles() != null) {
                    return Iterables.any(investor.getPersonRoles(), new Predicate<InvestorRole>() {
                        @Override
                        public boolean apply(@Nullable InvestorRole role) {
                            return validRoles.contains(role);
                        }
                    });
                }
                return false;
            }
        });
        return Lists.newArrayList(shareholdersAndMembers);
    }


    private boolean identifyNominatedMembers(List<InvestorDto> investors) {
        List<InvestorDto> nominatedInvestors = Lambda.filter(new LambdaMatcher<InvestorDto>() {
            @Override
            protected boolean matchesSafely(InvestorDto investorDto) {
                return Lambda.selectFirst(investorDto.getPersonRoles(), new NominatedInvestorRoleMatcher()) != null;
            }
        }, investors);
        if (nominatedInvestors != null && !nominatedInvestors.isEmpty()) {
            Lambda.forEach(nominatedInvestors).setIsNominated(true);
            return true;
        }
        return false;
    }

    private List<InvestorDto> getPersonDetailsFromApplicationDocument(List<PersonDetail> persons, final AccountSubType accountSubType, final Map<String,Boolean> existingPersonCISKeys) {
        return Lambda.convert(persons, new Converter<PersonDetail, InvestorDto>() {
            @Override
            public InvestorDto convert(PersonDetail person) {
                return investorDtoConverterForPersonDetail.convertFromPersonDetail(person, accountSubType,existingPersonCISKeys);
            }
        });
    }

    private List<LinkedAccountDto> getLinkedAccountsForClientApplication(List<RegisteredAccountImpl> registeredAccounts) {
        LinkedList<LinkedAccountDto> linkedAccountDtos = new LinkedList<>();
        if (CollectionUtils.isEmpty(registeredAccounts)) {
            return linkedAccountDtos;
        }

        for (RegisteredAccountImpl registeredAccount : registeredAccounts) {
            if (registeredAccount != null && !isLinkedAccountEmpty(registeredAccount.getName(), registeredAccount.getAccountNumber(), registeredAccount.getBsb())) {
                if (registeredAccount.isPrimary()) {
                    linkedAccountDtos.addFirst(getLinkedAccountDtoFromRegisteredAccount(registeredAccount));
                } else {
                    linkedAccountDtos.add(getLinkedAccountDtoFromRegisteredAccount(registeredAccount));
                }
            }
        }
        return linkedAccountDtos;
    }

    private LinkedAccountDto getLinkedAccountDtoFromRegisteredAccount(RegisteredAccountImpl registeredAccount) {
        LinkedAccountDto linkedAccountDto = new LinkedAccountDto();
        linkedAccountDto.setPrimary(registeredAccount.isPrimary());
        linkedAccountDto.setName(registeredAccount.getName());
        linkedAccountDto.setBsb(getDisplayBsbFormat(registeredAccount.getBsb()));
        linkedAccountDto.setAccountNumber(registeredAccount.getAccountNumber());
        linkedAccountDto.setNickName(registeredAccount.getNickName());
        linkedAccountDto.setDirectDebitAmount(registeredAccount.getInitialDeposit());
        return linkedAccountDto;
    }

    private IClientApplicationForm.AccountType getAccountTypeForClientApplication(AccountStructure accountStructure, AccountSubType accountSubtype,
                                                                                  boolean isCorporate, String orderType) {
        IClientApplicationForm.AccountType accountType = null;
        switch (accountStructure) {
            case I:
                accountType = INDIVIDUAL;
                break;
            case J:
                accountType = JOINT;
                break;
            case C:
                accountType = COMPANY;
                break;
            case T:
                accountType = getTrustAccountType(isCorporate);
                break;
            case S:
                accountType = getSmsfAccountType(isCorporate, orderType);
                break;
            case SU:
                Preconditions.checkNotNull(accountSubtype, "subtype is missing for SUPER account");
                switch (accountSubtype) {
                    case ACCUMULATION:
                        accountType = SUPER_ACCUMULATION;
                        break;
                    case PENSION:
                        accountType = SUPER_PENSION;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown SUPER Account SubType: " + accountSubtype);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Account Type: " + accountStructure.getName());
        }
        return accountType;
    }

    private IClientApplicationForm.AccountType getSmsfAccountType(boolean isCorporate, String orderType) {
        IClientApplicationForm.AccountType accountType = UNKNOWN;
        switch (orderOf(orderType)) {
            case FundAdmin:
            case ExistingSMSF:
                accountType = isCorporate ? CORPORATE_SMSF : INDIVIDUAL_SMSF;
                break;
            case NewIndividualSMSF:
                if (!isCorporate) {
                    accountType = NEW_INDIVIDUAL_SMSF;
                }
                break;
            case NewCorporateSMSF:
                if (isCorporate) {
                    accountType = NEW_CORPORATE_SMSF;
                }
                break;
            default:
                accountType = UNKNOWN;
        }
        return accountType;
    }

    private IClientApplicationForm.AccountType getTrustAccountType(boolean isCorporate) {

        if (isCorporate) {
            return CORPORATE_TRUST;
        } else {
            return INDIVIDUAL_TRUST;
        }

    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    private List<IExtendedPersonDetailsForm> getAllPersonDetails(IClientApplicationForm form) {
        List<IExtendedPersonDetailsForm> allPersonDetails;
        switch (form.getAccountType()) {
            case INDIVIDUAL:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
            case JOINT:
                allPersonDetails = form.getInvestors();
                break;
            case CORPORATE_SMSF:
            case CORPORATE_TRUST:
            case NEW_CORPORATE_SMSF:
            case COMPANY:
                allPersonDetails = form.getDirectors();
                break;
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
            case INDIVIDUAL_TRUST:
                allPersonDetails = new ArrayList<>();
                allPersonDetails.addAll(form.getTrustees());
                break;
            default:
                throw new IllegalArgumentException("Invalid account type " + form.getAccountType().value());
        }
        return allPersonDetails;
    }

    private RegisteredEntityDto getOrganizationDetails(IClientApplicationForm form) {
        switch (form.getAccountType()) {
            case INDIVIDUAL:
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                return null;
            default:
                return organizationDtoConverter.convertFromOrganizationForm(form);
        }
    }

    private RegisteredEntityDto getOrganizationDetails(ApplicationDocumentDetail applicationDocument,
                                                       IClientApplicationForm.AccountType accountType) {
        switch (accountType) {
            case INDIVIDUAL:
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                return null;
            default:
                return organizationDtoConverterForApplicationDocument
                        .getOrganisationDetailsFromApplicationDocument(applicationDocument, accountType);
        }
    }

    private List<LinkedAccountDto> getLinkedAccounts(IClientApplicationForm form) {
        ILinkedAccountsForm linkedAccounts = form.getLinkedAccounts();
        final LinkedAccountDto primaryLinkedAccountDto = getLinkedAccountDto(linkedAccounts.getPrimaryLinkedAccount(), true);
        final List<LinkedAccountDto> otherLinkedAccountsDtos = Lambda.convert(linkedAccounts.getOtherLinkedAccounts(),
                new Converter<ILinkedAccountForm, LinkedAccountDto>() {
                    @Override
                    public LinkedAccountDto convert(ILinkedAccountForm linkedAccount) {
                        LinkedAccountDto linkedAccountDto = getLinkedAccountDto(linkedAccount, false);
                        return linkedAccountDto;
                    }
                });

        LinkedList<LinkedAccountDto> linkedAccountDtos = new LinkedList<>();
        linkedAccountDtos.addFirst(primaryLinkedAccountDto);
        linkedAccountDtos.addAll(otherLinkedAccountsDtos);

        linkedAccountDtos.removeAll(Collections.singleton(null));

        return linkedAccountDtos;
    }

    private LinkedAccountDto getLinkedAccountDto(ILinkedAccountForm linkedAccount, boolean isPrimary) {
        LinkedAccountDto linkedAccountDto = null;
        if (linkedAccount != null && !isLinkedAccountEmpty(linkedAccount.getAccountName(), linkedAccount.getAccountNumber(), linkedAccount.getBsb())) {
            linkedAccountDto = new LinkedAccountDto();
            linkedAccountDto.setPrimary(isPrimary);
            linkedAccountDto.setName(linkedAccount.getAccountName());
            linkedAccountDto.setBsb(getDisplayBsbFormat(linkedAccount.getBsb()));
            linkedAccountDto.setAccountNumber(linkedAccount.getAccountNumber());
            linkedAccountDto.setNickName(linkedAccount.getNickName());
            linkedAccountDto.setDirectDebitAmount(getDirectDebitAmount(linkedAccount));
        }
        return linkedAccountDto;
    }

    private boolean isLinkedAccountEmpty(String accountName, String accountNumber, String bsb) {
        final boolean isBsbEmpty =  StringUtils.isBlank(bsb)
                                    || StringUtils.countMatches(bsb, "0") == 6;
        return StringUtils.isBlank(accountName)
                        && StringUtils.isBlank(accountNumber)
                        && isBsbEmpty;
    }

    private List<InvestorDto> toInvestorDtos(List<? extends IExtendedPersonDetailsForm> investors, final ServiceErrors serviceErrors, final IClientApplicationForm.AccountType accountType) {
        return Lambda.convert(investors, new Converter<IExtendedPersonDetailsForm, InvestorDto>() {
            @Override
            public InvestorDto convert(IExtendedPersonDetailsForm investor) {
                InvestorDto investorDto;
                if (investor.isExistingPerson()) {
                    LOGGER.info("Fetching existing investor details from avaloq for client : {}", investor.getClientKey());
                    investorDto = (InvestorDto) clientListDtoService.find(new ClientKey(investor.getClientKey()), serviceErrors);
                    investorDto.setPersonRoles(individualDtoConverter.getPersonRoles(investor));
                    investorDto.setIdvs(InvestorDtoConverterForPersonDetail.VERIFIED);
                    setExistingInvestorExemptionReason(investorDto, accountType, investor);
                    setUpdatedDetailsForExistingInvestor(investor, investorDto, serviceErrors);
                    if (investor.hasCrsTaxDetails()) {
                        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(investor,investorDto);
                    }
                } else {
                    investorDto = individualDtoConverter.convertFromIndividualForm(investor, serviceErrors, accountType);
                }
                return investorDto;
            }
        });
    }


    private void setExistingInvestorExemptionReason(InvestorDto investorDto, IClientApplicationForm.AccountType accountType, IExtendedPersonDetailsForm investor) {
        if (accountType == SUPER_PENSION) {
            if (StringUtils.isNotEmpty(investor.getTaxoption())) {
                investorDto.setExemptionReason(clientApplicationDetailsDtoHelperService.getCodeNameByIntlId(CodeCategory.PENSION_EXEMPTION_REASON, investor.getTaxoption(), new FailFastErrorsImpl()));
            } else {
                investorDto.setExemptionReason(clientApplicationDetailsDtoHelperService.getCodeNameByIntlId(CodeCategory.PENSION_EXEMPTION_REASON, investorDto.getPensionExemptionReason().toString(), new FailFastErrorsImpl()));
            }
        } else if (investor.hasExemptionReason() && !(NO_EXEMPTION).equals(investor.getExemptionReason())) {
            investorDto.setExemptionReason(individualDtoConverter.getExemptionReason(investor.getExemptionReason(), new FailFastErrorsImpl()));
        }
    }

    /**
     * Some details are updated/added to an existing investor on submission. If the app is still being processed by ABS, when get these updated
     * details from the pers db, as ABS won't have updated the client record yet.
     *
     * @param investor
     * @param investorDto
     * @param serviceErrors
     */
    private void setUpdatedDetailsForExistingInvestor(IExtendedPersonDetailsForm investor, InvestorDto investorDto, ServiceErrors serviceErrors) {
        if (investorDto instanceof IndividualDto) {
            clientApplicationDetailsDtoHelperService.setPlaceOfBirthDetails(investor, investorDto, serviceErrors);
            setFormerNameDetails(investor, investorDto);
        }
    }

    private void setFormerNameDetails(IExtendedPersonDetailsForm investor, InvestorDto investorDto) {
        if (StringUtils.isBlank(((IndividualDto) investorDto).getFormerName()) && StringUtils.isNotBlank(investor.getFormerName())) {
            ((IndividualDto) investorDto).setFormerName(investor.getFormerName());
        }
    }

    private BrokerDto getBrokerDto(String adviserPositionId, ServiceErrors serviceErrors) {
        return getBrokerDto(BrokerKey.valueOf(adviserPositionId), serviceErrors);
    }

    private BrokerDto getBrokerDto(final BrokerKey brokerKey, final ServiceErrors serviceErrors) {
        LOGGER.info("Fetching Broker details from Avaloq for : {}", brokerKey);
        BrokerDto brokerDto = new BrokerDto();
        EncodedString encodedBrokerKey = EncodedString.fromPlainText(brokerKey.getId());
        brokerDto.setKey(new com.bt.nextgen.api.broker.model.BrokerKey(encodedBrokerKey.toString()));

        setBrokerUserDetails(brokerDto, brokerKey, serviceErrors);
        setDealerGroupBrokerDetails(brokerDto, brokerKey, serviceErrors);
        return brokerDto;
    }

    private void setBrokerUserDetails(BrokerDto brokerDto, BrokerKey brokerKey, ServiceErrors serviceErrors) {
        BrokerUser brokerUser = brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors);
        brokerDto.setFirstName(brokerUser.getFirstName());
        brokerDto.setMiddleName(brokerUser.getMiddleName());
        brokerDto.setLastName(brokerUser.getLastName());
        brokerDto.setCorporateName(brokerUser.getCorporateName());
        brokerDto.setAddresses(getAddressList(brokerUser, serviceErrors));
        brokerDto.setEmail(getEmailList(brokerUser, serviceErrors));
        brokerDto.setPhone(getPhoneList(brokerUser, serviceErrors));
    }

    private void setDealerGroupBrokerDetails(BrokerDto brokerDto, BrokerKey brokerKey, ServiceErrors serviceErrors) {
        Broker broker = brokerIntegrationService.getBroker(brokerKey, serviceErrors);
        Broker dealerGroupBroker = getDealerGroupBroker(broker, serviceErrors);
        brokerDto.setOfflineApproval(getOfflineApprovalAccess(broker, dealerGroupBroker));
        brokerDto.setDealerGroupName(dealerGroupBroker.getPositionName());
    }

    /**
     * Get the dealer group broker for the adviser or practice broker if they are under a practice
     * @param broker
     * @param serviceErrors
     * @return
     */
    private Broker getDealerGroupBroker(Broker broker, ServiceErrors serviceErrors) {
        return null != broker.getDealerKey() ? brokerIntegrationService.getBroker(broker.getDealerKey(), serviceErrors)
                : brokerIntegrationService.getBroker(broker.getPracticeKey(), serviceErrors);
    }

    /**
     * Get the DG key of the adviser and check if the DG has offline approval access
     *
     * @param broker
     * @param dealerGroupBroker
     * @return
     */
    private boolean getOfflineApprovalAccess(Broker broker, Broker dealerGroupBroker) {
        Boolean isOfflineApproval = dealerGroupBroker != null ? dealerGroupBroker.isOfflineApproval() : broker.isOfflineApproval();
        return BooleanUtils.isTrue(isOfflineApproval);
    }

    private List<EmailDto> getEmailList(BrokerUser brokerUser, ServiceErrors serviceErrors) {
        List<EmailDto> emailList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(brokerUser.getEmails())) {
            for (Email email : brokerUser.getEmails()) {
                EmailDto emailDto = new EmailDto();
                ClientDetailDtoConverter.toEmailDto(email, emailDto, serviceErrors);
                emailList.add(emailDto);
            }
        }
        return emailList;
    }

    private List<PhoneDto> getPhoneList(BrokerUser brokerUser, ServiceErrors serviceErrors) {
        List<PhoneDto> phoneList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(brokerUser.getPhones())) {
            for (Phone phone : brokerUser.getPhones()) {
                PhoneDto phoneDto = new PhoneDto();
                ClientDetailDtoConverter.toPhoneDto(phone, phoneDto, serviceErrors);
                phoneList.add(phoneDto);
            }
        }
        return phoneList;
    }

    private List<AddressDto> getAddressList(BrokerUser brokerUser, ServiceErrors serviceErrors) {
        List<AddressDto> addressList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(brokerUser.getAddresses())) {
            for (Address addressModel : brokerUser.getAddresses()) {
                AddressDto addressDto = new AddressDto();
                ClientDetailDtoConverter.toAddressDto(addressModel, addressDto, serviceErrors);
                addressList.add(addressDto);
            }
        }
        return addressList;
    }

    private AccountSettingsDto getAccountSettings(IClientApplicationForm form, BrokerDto brokerDto) {
        AccountSettingsDto accountSettingsDto = new AccountSettingsDto();

        List<PersonRelationDto> personRelations = new ArrayList<>();
        personRelations.addAll(getAccountSettingsPeople(form));
        PaymentAuthorityEnum professionalsPayment = form.getAccountSettings().getProfessionalsPayment();
        PersonRelationDto adviser = getAccountSettingsAdviser(brokerDto, professionalsPayment);
        personRelations.add(adviser);
        accountSettingsDto.setPersonRelations(personRelations);
        if(null!=form.getAccountSettings().getPowerOfAttorney()) {
            accountSettingsDto.setPowerOfAttorney(form.getAccountSettings().getPowerOfAttorney() ? "Yes" : "No");
        }

        return accountSettingsDto;
    }

    private AccountSettingsDto getAccountSettings(ApplicationDocumentDetail applicationDocument,
                                                       BrokerDto brokerDto) {
        AccountSettingsDto accountSettingsDto = new AccountSettingsDto();
        List<PersonRelationDto> personRelations = new ArrayList<>();
        personRelations.addAll(getAccountSettingsForPersons(applicationDocument.getPersons()));
        personRelations.add(getAccountSettingsForAdviser(applicationDocument.getAdviserAccountSettings(), brokerDto));
        accountSettingsDto.setPersonRelations(personRelations);

        if(null != applicationDocument.getAccountClassList() && !applicationDocument.getAccountClassList().isEmpty()){
            accountSettingsDto.setPowerOfAttorney(getPowerOfAttorneyValue(applicationDocument.getAccountClassList()));
        }
        return accountSettingsDto;
    }


    private PersonRelationDto getAccountSettingsForAdviser(List<AccountAuthoriser> adviserAccountSettings, BrokerDto brokerDto) {
        PersonRelationDto adviser;
        String permission = CollectionUtils.isNotEmpty(adviserAccountSettings) ? getPermission(adviserAccountSettings).getPermissionDesc()
            : AccountPaymentPermission.NO_PAYMENTS.getPermissionDesc();
        adviser = getPersonRelationDto(brokerDto.getCorporateName(), permission, true, false);
        adviser.setPersonRoles(new HashSet());
        return adviser;
    }

    private AccountPaymentPermission getPermission(PaymentAuthorityEnum permission) {
        switch (permission) {
            case NOPAYMENTS:
                return AccountPaymentPermission.NO_PAYMENTS;
            case LINKEDACCOUNTSONLY:
                return AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS;
            case ALLPAYMENTS:
                return AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL;
            default:
                return AccountPaymentPermission.NA;
        }
    }

    private AccountPaymentPermission getPermission(AccountAuthoriser adviserAccountSettings) {
        if (adviserAccountSettings == null) {
            return AccountPaymentPermission.NA;
        }
        return getAccountPaymentPermission(adviserAccountSettings);
    }

    //For SMSF FE, advisers can have two permissions if app is in status company registration, we want to ignore the company registration permission
    private AccountPaymentPermission getPermission(List<AccountAuthoriser> adviserAccountSettings) {
        for (AccountAuthoriser accountAuthoriser : adviserAccountSettings) {
            if (TransactionPermission.Company_Registration != accountAuthoriser.getTxnType()) {
                return getPermission(accountAuthoriser);
            }
        }
        return AccountPaymentPermission.NO_PAYMENTS;
    }

    private AccountPaymentPermission getAccountPaymentPermission(AccountAuthoriser adviserAccountSettings) {
        switch (adviserAccountSettings.getTxnType()) {
            case No_Transaction:
            case Account_Maintenance:
                return AccountPaymentPermission.NO_PAYMENTS;
            case Payments_Deposits_To_Linked_Accounts:
                return AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS;
            case Payments_Deposits:
                return AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL;
            default:
                return AccountPaymentPermission.NA;
        }
    }

    private List<PersonRelationDto> getAccountSettingsForPersons(List<PersonDetail> persons) {
        final List<PersonRelationDto> investorsAccountSettings = getInvestorsAccountSettings(persons);
        List<PersonRelationDto> accountSettings = new ArrayList<>();
        accountSettings.addAll(investorsAccountSettings);
        return accountSettings;
    }

    private List<PersonRelationDto> getInvestorsAccountSettings(List<PersonDetail> persons) {
        List<PersonRelationDto> investorAccountSettings = new ArrayList<>();
        for (PersonDetail person : persons) {
            if (person.getAccountAuthorisationList() != null && !person.getAccountAuthorisationList().isEmpty()) {
                PersonRelationDto personRelationDto = getPersonRelationDto(person.getAccountAuthorisationList().get(0), person);
                investorAccountSettings.add(personRelationDto);
            }
        }
        return investorAccountSettings;
    }

    private PersonRelationDto getPersonRelationDto(IExtendedPersonDetailsForm person) {
        PersonRelationDto personRelationDto = getPersonRelationDto(
                StringUtils.join(new String[]{person.getFirstName(), person.getLastName()}, ' '),
                getPermission(person.getPaymentSetting()).getPermissionDesc(), false, person.isPrimaryContact());

        personRelationDto.setApprover(person.isApprover());
        return personRelationDto;
    }

    private PersonRelationDto getPersonRelationDto(AccountAuthoriser accountAuthoriser, PersonDetail person) {
        PersonRelationDto personRelationDto = getPersonRelationDto(person.getFullName(),
                getPermission(accountAuthoriser).getPermissionDesc(), false, person.isPrimaryContact());

        Set<InvestorRole> personRoles = new HashSet<>();
        personRoles.addAll(investorDtoConverterForPersonDetail.getPersonRoles(person));
        personRelationDto.setPersonRoles(personRoles);
        personRelationDto.setApprover(person.isApprover());
        personRelationDto.setClientKey(new com.bt.nextgen.api.client.model.ClientKey(
                EncodedString.fromPlainTextUsingTL(person.getClientKey().getId()).toString()));
        return personRelationDto;
    }

    private PersonRelationDto getPersonRelationDto(String fullName, String permission, boolean isAdviser,
                                                   boolean isPrimaryContact) {
        PersonRelationDto personRelationDto = new PersonRelationDto();
        personRelationDto.setName(fullName);
        personRelationDto.setPrimaryContactPerson(isPrimaryContact);
        personRelationDto.setAdviser(isAdviser);
        personRelationDto.setPermissions(permission);
        return personRelationDto;
    }

    private List<PersonRelationDto> getAccountSettingsPeople(IClientApplicationForm form) {
        final List<PersonRelationDto> investors = getInvestors(form);
        final List<PersonRelationDto> directors = getDirectors(form);
        final List<PersonRelationDto> trustees = getTrustees(form);

        ArrayList<PersonRelationDto> relations = new ArrayList<PersonRelationDto>();
        relations.addAll(investors);
        relations.addAll(directors);
        relations.addAll(trustees);

        return relations;
    }

    private PersonRelationDto getAccountSettingsAdviser(BrokerDto brokerDto, PaymentAuthorityEnum professionalsPayment) {
        PersonRelationDto adviserPersonRelationDto = getPersonRelationDto(brokerDto.getCorporateName(),
                getPermission(professionalsPayment).getPermissionDesc(), true, false);

        adviserPersonRelationDto.setPersonRoles(new HashSet());
        return adviserPersonRelationDto;
    }

    private PersonRelationDto getAccountSettingsPersonWithRoles(IExtendedPersonDetailsForm person, final Set<InvestorRole> roles) {
        PersonRelationDto dto = getPersonRelationDto(person);
        dto.setPersonRoles(roles);
        return dto;
    }

    private List<PersonRelationDto> getDirectors(IClientApplicationForm form) {
        List<IExtendedPersonDetailsForm> directors;
        if (form.getAccountType().equals(COMPANY)) {
            directors = form.getDirectorsSecretariesSignatories();
        } else {
            directors = form.getDirectors();
        }
        return Lambda.convert(directors, new Converter<IExtendedPersonDetailsForm, PersonRelationDto>() {
            @Override
            public PersonRelationDto convert(IExtendedPersonDetailsForm director) {
                Set<InvestorRole> roles = new HashSet<InvestorRole>();
                if (director instanceof IDirectorDetailsForm) {
                    roles.add(getInvestorRole(((IDirectorDetailsForm) director).getRole()));
                }
                return getAccountSettingsPersonWithRoles(director, roles);
            }
        });
    }

    private InvestorRole getInvestorRole(IOrganisationForm.OrganisationRole role) {
        switch (role) {
            case SIGNATORY:
                return InvestorRole.Signatory;
            case DIRECTOR:
                return InvestorRole.Director;
            case SECRETARY:
                return InvestorRole.Secretary;
            default:
                throw new UnsupportedOperationException("Unsupported role " + role);
        }
    }

    private List<PersonRelationDto> getTrustees(IClientApplicationForm form) {
        return Lambda.convert(form.getTrustees(), new Converter<IExtendedPersonDetailsForm, PersonRelationDto>() {
            @Override
            public PersonRelationDto convert(IExtendedPersonDetailsForm trustee) {
                Set<InvestorRole> roles = new HashSet<InvestorRole>();
                roles.add(InvestorRole.Trustee);
                return getAccountSettingsPersonWithRoles(trustee, roles);
            }
        });
    }

    private List<PersonRelationDto> getInvestors(IClientApplicationForm form) {
        return Lambda.convert(form.getInvestors(), new Converter<IExtendedPersonDetailsForm, PersonRelationDto>() {
            @Override
            public PersonRelationDto convert(IExtendedPersonDetailsForm investor) {
                Set<InvestorRole> roles = new HashSet<InvestorRole>();
                roles.add(InvestorRole.Owner);
                return getAccountSettingsPersonWithRoles(investor, roles);
            }
        });
    }

    private List<InvestorDto> getInvestors(List<InvestorDto> persons) {
        return Lambda.filter(new LambdaMatcher<InvestorDto>() {
            @Override
            protected boolean matchesSafely(InvestorDto investorDto) {
                return Lambda.selectFirst(investorDto.getPersonRoles(), new MainApplicantRoleMatcher()) != null;
            }
        }, persons);
    }

    private List<InvestorDto> getInvestors(List<InvestorDto> persons, IClientApplicationForm.AccountType accountType) {
        switch (accountType) {
            case INDIVIDUAL:
            case JOINT:
            case SUPER_ACCUMULATION:
                return persons;
            default:
                return getInvestors(persons);
        }
    }

    private String getDisplayBsbFormat(String bsb) {
        return BsbFormatter.formatBsb(StringUtils.leftPad(bsb, 6, '0'));
    }

    private BigDecimal getDirectDebitAmount(ILinkedAccountForm account) {
        return new BigDecimal(account.getDirectDebitAmount() != null ? account.getDirectDebitAmount() : "0");
    }

    private Map<String, Object> getFeesDto(List<FeesSchedule> fees) {
        Map<String, Object> feesMap = new HashMap<>();
        if (fees != null) {
            for (FeesSchedule fee : fees) {
                populateFeeMap(feesMap, fee);
            }
        }
        setDefaultFee(feesMap);
        return feesMap;
    }

    private void setDefaultFee(Map<String, Object> feesMap) {
        if (!feesMap.containsKey(ESTAMOUNT)) {
            feesMap.put(ESTAMOUNT, "0.00");
        }
        if (!feesMap.containsKey(ADVISER_CONTRIBUTION)) {
            Map<String, Object> blankMap = new HashMap<>();
            blankMap.put("feesComponent", new ArrayList<>());
            blankMap.put("type", "Adviser contribution fee");
            feesMap.put(ADVISER_CONTRIBUTION, blankMap);
        }
        if (!feesMap.containsKey(ONGOING_FEES)) {
            Map<String, Object> blankMap = new HashMap<>();
            blankMap.put("feesComponent", new ArrayList<>());
            blankMap.put("type", "Ongoing advice fee");
            feesMap.put(ONGOING_FEES, blankMap);
        }
        if (!feesMap.containsKey(LICENSEE_FEES)) {
            Map<String, Object> blankMap = new HashMap<>();
            blankMap.put("feesComponent", new ArrayList<>());
            blankMap.put("type", "Licensee advice fee");
            feesMap.put(LICENSEE_FEES, blankMap);
        }
    }

    private void populateFeeMap(Map<String, Object> feesMap, FeesSchedule fee) {
        if (fee.getFeesType() != null) {
            switch (fee.getFeesType()) {
                case AVSR_ESTAB:
                    feesMap.put(ESTAMOUNT,
                            getFormattedEstablishmentAmount(((DollarFeesComponent) fee.getFeesComponents().get(0)).getDollar()));
                    break;
                case ONGOING_FEE:
                    feesMap.put(ONGOING_FEES, getFeeComponentMap(fee));
                    break;
                case LICENSEE_FEE:
                    feesMap.put(LICENSEE_FEES, getFeeComponentMap(fee));
                    break;
                case CONTRIBUTION_FEE:
                    feesMap.put(ADVISER_CONTRIBUTION,getFeeComponentMap(fee));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid fee type " + fee.getFeesType());
            }
        }
    }

    private List<String> getSlidingFeeApplied(List<FeesMiscType> feesMiscTypes) {
        List<String> appliedTo = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(feesMiscTypes)) {
                 appliedTo = Lambda.convert(feesMiscTypes, new Converter<FeesMiscType, String>() {
                    @Override
                    public String convert(FeesMiscType feesAssetType) {
                        return feesAssetType.getLabel();
                    }
                });
        }
        return appliedTo;
    }

    private Map<String, Object> getFeeComponentMap(FeesSchedule feesSchedule) {
        List<FeesComponents> feesComponents = feesSchedule.getFeesComponents();
        List<String> slidingFeeAppliedTo = getSlidingFeeApplied(feesSchedule.getTransactionType());

        List<Map<String, Object>> feesComponentList = new ArrayList<>();
        Map<String, Object> percentageFeeComponentMap = new HashMap<>();
        Map<String, Object> oneOffFeeCompoenentMap = new HashMap<>();
        Map<String, Object> regularFeeComponentMap = new HashMap<>();
        if (feesComponents != null) {
            for (FeesComponents feesComponent : feesComponents) {
                if (feesComponent.getFeesComponentType() != null) {
                    switch (feesComponent.getFeesComponentType()) {
                        case DOLLAR_FEE:
                            feesComponentList.add(getDollarFeeComponentMap((DollarFeesComponent) feesComponent));
                            break;
                        case PERCENTAGE_FEE:
                          AnnotatedPercentageFeesComponent percentageFees = (AnnotatedPercentageFeesComponent) feesComponent;
                            percentageFeeComponentMap.put(percentageFees.getFeesMiscType().getLabel(),
                            getFormattedPercentage(percentageFees.getFactor()));
                            percentageFeeComponentMap.put("label", feesComponent.getFeesComponentType().getLabel());
                            break;
                        case SLIDING_SCALE_FEE:
                            feesComponentList.add(
                                    getSlidingFeeComponentMap((SlidingScaleFeesComponent) feesComponent, slidingFeeAppliedTo));
                            break;
                        case ONE_OFF_FEE:
                            OneOffFeesComponent oneOffFeesComponent = (OneOffFeesComponent)feesComponent;
                            oneOffFeeCompoenentMap.put(FeesComponentKey.getFeesDisplayValue(oneOffFeesComponent.getFeesMiscType().getDisplayName()),getFormattedPercentage(oneOffFeesComponent.getFactor()));
                            oneOffFeeCompoenentMap.put("label",feesComponent.getFeesComponentType().getLabel());
                            break;
                        case REGULAR_FEE:
                            RegularFeesComponent regularFeesComponent = (RegularFeesComponent)feesComponent;
                            regularFeeComponentMap.put(FeesComponentKey.getFeesDisplayValue(regularFeesComponent.getFeesMiscType().getDisplayName()),getFormattedPercentage(regularFeesComponent.getFactor()));
                            regularFeeComponentMap.put("label",feesComponent.getFeesComponentType().getLabel());
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid fee type " + feesComponent.getFeesComponentType());
                    }
                }
            }
        }
        if (!percentageFeeComponentMap.isEmpty()) {
            feesComponentList.add(percentageFeeComponentMap);
        }

        if(!oneOffFeeCompoenentMap.isEmpty()){
            feesComponentList.add(oneOffFeeCompoenentMap);
        }

        if(!regularFeeComponentMap.isEmpty()){
            feesComponentList.add(regularFeeComponentMap);
        }

        Map<String, Object> feesComponentsMap = new HashMap<>();
        feesComponentsMap.put("type", feesSchedule.getFeesType().getLabel());
        feesComponentsMap.put("feesComponent", feesComponentList);
        return feesComponentsMap;
    }

    private Map<String, Object> getSlidingFeeComponentMap(SlidingScaleFeesComponent feesComponent, List<String> feeCheck) {
        List<Map<String, Object>> slidingFeeComponentMapList = new ArrayList<>();
        List<SlidingScaleTiers> scaleFeeTiers = feesComponent.getTiers();
        for (SlidingScaleTiers scaleFeeTier : scaleFeeTiers) {
            Map<String, Object> slidingFeeComponentMap = new HashMap<>();
            slidingFeeComponentMap.put("lowerBound", getFormattedAmount(scaleFeeTier.getLowerBound()));
            slidingFeeComponentMap.put("upperBound", getFormattedAmount(scaleFeeTier.getUpperBound()));
            slidingFeeComponentMap.put("percentage", getFormattedPercentage(scaleFeeTier.getPercent()));
            slidingFeeComponentMapList.add(slidingFeeComponentMap);
        }
        Map<String, Object> slidingComponentMap = new LinkedHashMap<>();
        for (String labelFeeAppliedTo : feeCheck) {
            slidingComponentMap.put(labelFeeAppliedTo, "true");
        }
        slidingComponentMap.put("slidingScaleFeeTier", slidingFeeComponentMapList);
        slidingComponentMap.put("label", feesComponent.getFeesComponentType().getLabel());
        return slidingComponentMap;
    }

    private Map<String, Object> getDollarFeeComponentMap(DollarFeesComponent feesComponent) {
        Map<String, Object> dollarFeeComponentMap = new HashMap<>();
        dollarFeeComponentMap.put("cpiindex", feesComponent.isCpiindex());
        dollarFeeComponentMap.put("amount", getFormattedAmount(feesComponent.getDollar()));
        dollarFeeComponentMap.put("label", feesComponent.getFeesComponentType().getLabel());
        return dollarFeeComponentMap;
    }

    @SuppressFBWarnings(value = "squid:S2200")
    @SuppressWarnings("squid:S2200")
    private String getFormattedAmount(BigDecimal amount) {
        if (amount.compareTo(Constants.AVALOQ_NULL_FEE_UPPER_BOUND) == -1) {
            return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            return "";
        }
    }

    private String getFormattedPercentage(BigDecimal percentage) {
        return percentage.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private String getFormattedEstablishmentAmount(BigDecimal amount) {
        BigDecimal establishmentAmount = amount.multiply(new BigDecimal(-1));
        return getFormattedAmount(establishmentAmount);
    }

    private String getAccountDisplayName(ApplicationDocumentDetail applicationDocument,
                                         IClientApplicationForm.AccountType accountType) {
        switch (accountType) {
            case INDIVIDUAL:
            case JOINT:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                return getAccountDisplayNameForIndividualOrJoint(applicationDocument.getPersons());
            default:
                return organizationDtoConverterForApplicationDocument
                        .getOrganisation(applicationDocument.getOrganisations(), accountType).getFullName();
        }
    }

    private String getAccountDisplayNameForIndividualOrJoint(List<PersonDetail> persons) {
        final List<String> investorNames = Lambda.convert(persons, new Converter<PersonDetail, String>() {
            @Override
            public String convert(PersonDetail person) {
                return StringUtils.join(new String[]{person.getFirstName(), person.getLastName()}, ' ');
            }
        });

        return StringUtils.join(investorNames, ", ");
    }

    private InvestmentChoiceDto getInvestmentChoiceDto(LinkedPortfolioDetails linkedPortfolioDetails, List<RegisteredAccountImpl> linkedAccounts) {
        final List<AssetInfo> assetInfoList = linkedPortfolioDetails.getAssetInfoList();
        if (CollectionUtils.isNotEmpty(assetInfoList)) {
            final AssetInfo assetInfo = assetInfoList.get(0);
            final Asset asset = assetIntegrationService.loadAsset(assetInfo.getAssetId(), new ServiceErrorsImpl());
            RegisteredAccountImpl linkedAccount = Lambda.selectFirst(linkedAccounts, new LambdaMatcher<RegisteredAccountImpl>() {
                @Override
                protected boolean matchesSafely(RegisteredAccountImpl registeredAccount) {
                    return registeredAccount.isPrimary();
                }
            });
            InvestmentChoiceDto investmentChoiceDto = new InvestmentChoiceDto();
            investmentChoiceDto.setManagedPortfolio(asset.getAssetName());
            investmentChoiceDto.setInitialInvestmentAmount(linkedAccount.getInitialDeposit());
            return investmentChoiceDto;
        }
        return null;
    }

    private InvestmentChoiceDto getInvestmentChoiceDto(IClientApplicationForm form) {
        if (form.hasInvestmentChoice()) {
            IInvestmentChoiceForm investmentChoiceFormData = form.getInvestmentChoice();
            InvestmentChoiceDto investmentChoiceDto = new InvestmentChoiceDto();
            investmentChoiceDto.setManagedPortfolio(investmentChoiceFormData.getPortfolioName());
            if (investmentChoiceFormData.getInitialDeposit() != null) {
                investmentChoiceDto.setInitialInvestmentAmount(new BigDecimal(investmentChoiceFormData.getInitialDeposit()));
            }
            return investmentChoiceDto;
        }
        return null;
    }

    private String getPowerOfAttorneyValue(List<BPClassList> accountClassList){

        BPClassList bpClassList1 = Lambda.selectFirst(accountClassList,Lambda.having(Lambda.on(BPClassListImpl.class).getBPClassifierId(),Matchers.is(CashManagementAccountType.POWER_OF_ATTORNEY)));
        return  null != bpClassList1 && null !=  bpClassList1.getBPClassIdVal() ? bpClassList1.getBPClassIdVal().getValue() : null;

    }

    private BrokerIdentifier getBrokerIdentifier(String brokerId) {
        return new BrokerIdentifierImpl(EncodedString.toPlainText(brokerId));
    }

}
