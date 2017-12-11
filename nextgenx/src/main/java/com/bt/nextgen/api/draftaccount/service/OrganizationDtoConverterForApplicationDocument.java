package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.avaloq.account.BPClassListImpl;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.BPClassList;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrganizationDtoConverterForApplicationDocument {

    @Autowired
    private AddressDtoConverter addressDtoConverter;

    @Autowired
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    private final static String VERIFIED = "Verified";

    @SuppressFBWarnings(
            value = "squid:RightCurlyBraceStartLineCheck",
            justification = "Just another account switch"
    )
    public RegisteredEntityDto getOrganisationDetailsFromApplicationDocument(ApplicationDocumentDetail applicationDocument, IClientApplicationForm.AccountType accountType) {
        RegisteredEntityDto dto;
        switch (accountType) {
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                dto = getSmsfDto(applicationDocument);
                break;

            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
                dto = createCorporateSmsf(applicationDocument);
                break;

            case INDIVIDUAL_TRUST:
                dto = getTrustDto(applicationDocument);
                break;

            case CORPORATE_TRUST:
                dto = createCorporateTrust(applicationDocument);
                break;

            case COMPANY:
                dto = getCompanyDto(getCompany(applicationDocument.getOrganisations()));
                populateCMADetailsForAccountTypes(applicationDocument.getAccountClassList(),dto);
                break;

            default:
                throw new IllegalArgumentException("Unsupported account type " + accountType);
        }

        setCommonOrganisationProperties(dto, getOrganisation(applicationDocument.getOrganisations(), accountType));
        return dto;
    }

    /**
     * This method returns the organisation (smsf or trust) from a list of organisations.
     *
     * @param organisations
     * @param accountType
     * @return
     */
    public Organisation getOrganisation(List<Organisation> organisations, IClientApplicationForm.AccountType accountType) {

        switch(accountType){
            case COMPANY:
                    return getCompany(organisations);

            case NEW_CORPORATE_SMSF:
                    return getSmsf(organisations);

            default:
                    return getSmsfOrTrust(organisations);
        }
    }


    private Organisation getSmsfOrTrust(List<Organisation> organisations) {
        return Lambda.selectFirst(organisations, new LambdaMatcher<Organisation>() {
            @Override
            protected boolean matchesSafely(Organisation organisation) {
                return organisation.getAcn() == null;
            }
        });
    }

    /**
     *  ACN would be null for New corporate before company setup hence checking based on the Investor Type
     */
    private Organisation getSmsf(List<Organisation> organisations) {
        return Lambda.selectFirst(organisations, new LambdaMatcher<Organisation>() {
            @Override
            protected boolean matchesSafely(Organisation organisation) {
                return organisation.getInvestorType().equals(InvestorType.SMSF);
            }
        });
    }

    /**
     * This method returns the company from a list of organisations.
     *
     * @param organisations
     * @return
     */
    public Organisation getCompany(List<Organisation> organisations) {
        return Lambda.selectFirst(organisations, new LambdaMatcher<Organisation>() {
            @Override
            protected boolean matchesSafely(Organisation organisation) {
                return organisation.getAcn() != null;
            }
        });
    }

    /**
     * This method returns the company from a list of organisations where investor type is Company
     *
     * @param organisations
     * @return
     */
    public Organisation getCompanyType(List<Organisation> organisations) {
        return Lambda.selectFirst(organisations, new LambdaMatcher<Organisation>() {
            @Override
            protected boolean matchesSafely(Organisation organisation) {
                return InvestorType.COMPANY == organisation.getInvestorType();
            }
        });
    }

    private void setCommonOrganisationProperties(RegisteredEntityDto organisationDto, Organisation organisation) {
        organisationDto.setIndustry(organisation.getIndustry());
        organisationDto.setFullName(organisation.getFullName());
        organisationDto.setAbn(organisation.getAbn());
        organisationDto.setRegistrationDate(organisation.getRegistrationDate());
        organisationDto.setRegistrationState(organisation.getRegistrationState());
        organisationDto.setRegistrationForGst(organisation.isRegistrationForGst());
        if (organisation.getExemptionReason() != null) {
            organisationDto.setExemptionReason(organisation.getExemptionReason().getValue());
            organisationDto.setTfnExemptId(organisation.getTfnExemptId());
        }
        organisationDto.setTfnProvided(organisation.getTfnProvided());
        organisationDto.setIdVerified(IdentityVerificationStatus.Completed.equals(organisation.getIdentityVerificationStatus()));
        organisationDto.setIdvs(VERIFIED);
    }

    private RegisteredEntityDto getSmsfDto(ApplicationDocumentDetail applicationDocument) {
        SmsfDto smsf = new SmsfDto();
        smsf.setAddresses(Arrays.asList(addressDtoConverter.getAddressDto(getRegisteredAddress(getSmsfOrTrust(applicationDocument.getOrganisations())))));
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(getSmsfOrTrust(applicationDocument.getOrganisations()),smsf);
        return smsf;
    }

    private RegisteredEntityDto createCorporateSmsf(ApplicationDocumentDetail applicationDocument) {
        SmsfDto smsf = (SmsfDto) getSmsfDto(applicationDocument);
        smsf.setCompany(getCompanyDto(getCompanyType(applicationDocument.getOrganisations())));
        return smsf;
    }

    private List <IndividualDto> getBeneficiaries(List<PersonDetail> personDetails) {
        List <IndividualDto> beneficiaries = new ArrayList<IndividualDto>();

        for (PersonDetail personDetail: personDetails)
        {
            IndividualDto individualDto = new IndividualDto();
            individualDto.setTitle(personDetail.getTitle());
            individualDto.setFullName(personDetail.getFullName());
            individualDto.setPrimaryRole(personDetail.getPrimaryRole());
            individualDto.setPersonRoles(getPersonRoles(personDetail));

            beneficiaries.add(individualDto);
        }

        return beneficiaries;
    }

    private List<InvestorRole> getPersonRoles(PersonDetail person) {

        Set<InvestorRole> personRoles = new HashSet<>();
        personRoles.add(InvestorDtoConverterForPersonDetail.personRolesMap.get(person.getPrimaryRole()));

        if (person.isBeneficiary()) {
            personRoles.add(InvestorRole.Beneficiary);
        }
        if (person.isMember()) {
            personRoles.add(InvestorRole.Member);
        }
        if (person.isShareholder()) {
            personRoles.add(InvestorRole.Shareholder);
        }

        return new ArrayList<>(personRoles);
    }


    private TrustDto getTrustDto(ApplicationDocumentDetail applicationDocumentDetail) {
        TrustDto trustDto = new TrustDto();
        Organisation trust = getSmsfOrTrust(applicationDocumentDetail.getOrganisations());

        trustDto.setTrustType(trust.getTrustType().getTrustTypeValue());
        trustDto.setTrustMemberClass(trust.getTrustMemberClass());
        trustDto.setAddresses(Arrays.asList(addressDtoConverter.getAddressDto(getRegisteredAddress(trust))));
        trustDto.setBusinessName(trust.getAsicName());
        trustDto.setBeneficiaries(getBeneficiaries(applicationDocumentDetail.getPersons()));
        crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(trust,trustDto);
        switch (trust.getTrustType()) {
            case OTHER:
                setTrustDescription(trustDto, trust);
                populateCMADetailsForAccountTypes(applicationDocumentDetail.getAccountClassList(),trustDto);
                break;
            case REGU_TRUST:
                trustDto.setTrustReguName(trust.getTrustReguName());
                trustDto.setLicencingNumber(trust.getLicencingNumber());
                break;
            case REGI_MIS:
                trustDto.setArsn(trust.getArsn());
                break;
            case GOVT_SUPER_FUND:
                trustDto.setLegEstFund(trust.getLegEstFund());
                break;
            default:
                break;
        }
       return trustDto;
    }

    private void populateCMADetailsForAccountTypes(List<BPClassList> accountClassList,RegisteredEntityDto registeredEntityDto){
        if(CollectionUtils.isNotEmpty(accountClassList)){
            if(registeredEntityDto instanceof TrustDto){
                ((TrustDto)registeredEntityDto).setPersonalInvestmentEntity(getPersonalEntityValue(accountClassList));
            }else if(registeredEntityDto instanceof CompanyDto){
                ((CompanyDto)registeredEntityDto).setPersonalInvestmentEntity(getPersonalEntityValue(accountClassList));
            }
        }
    }
    private String getPersonalEntityValue(List<BPClassList> accountClassList){

        BPClassList bpClassList1 = Lambda.selectFirst(accountClassList,Lambda.having(Lambda.on(BPClassListImpl.class).getBPClassifierId(), Matchers.is(CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY)));
        return  null != bpClassList1 && null != bpClassList1.getBPClassIdVal() ? bpClassList1.getBPClassIdVal().getValue() : null;

    }
    private void setTrustDescription(TrustDto trustDto, Organisation trust) {
        if(trust.getTrustTypeDesc() != null){
            trustDto.setTrustTypeDesc(trust.getTrustTypeDesc().getTrustTypeDescValue());
            if (trust.getBusinessClassificationDesc() != null){
                trustDto.setBusinessClassificationDesc(trust.getTrustTypeDesc().getTrustTypeDescValue()
                        + " - " + trust.getBusinessClassificationDesc());
            } else {
                trustDto.setBusinessClassificationDesc(trust.getTrustTypeDesc().getTrustTypeDescValue());
            }
        }
    }


    private RegisteredEntityDto createCorporateTrust(ApplicationDocumentDetail applicationDocumentDetail) {
        TrustDto trust = getTrustDto(applicationDocumentDetail);
        trust.setCompany(getCompanyDto(getCompany(applicationDocumentDetail.getOrganisations())));
        return trust;
    }

    private CompanyDto getCompanyDto(Organisation company) {
        CompanyDto companyDto = new CompanyDto();
        if (company != null) {
            companyDto.setFullName(company.getFullName());
            companyDto.setAsicName(company.getAsicName());
            companyDto.setAcn(company.getAcn());
            companyDto.setAbn(company.getAbn());
            companyDto.setIndustry(company.getIndustry());
            Address registeredAddress = getRegisteredAddress(company);
            AddressDto registeredAddressDto = addressDtoConverter.getAddressDto(registeredAddress);

            Address mailingAddress = getMailingAddress(company);
            AddressDto placeOfBusinessAddressDto = addressDtoConverter.getAddressDto(mailingAddress);
            companyDto.setAddresses(Arrays.asList(registeredAddressDto, placeOfBusinessAddressDto));
            companyDto.setIdvs(VERIFIED);

            String occupierName = StringUtils.isNotBlank(registeredAddress.getOccupierName())
                ? registeredAddress.getOccupierName() : mailingAddress.getOccupierName();
            companyDto.setOccupierName(occupierName);
            crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(company,companyDto);
        }
        return companyDto;
    }

    private Address getRegisteredAddress(Organisation organisation) {
        List<Address> addresses = organisation.getAddresses();
        return Lambda.selectFirst(addresses, new LambdaMatcher<Address>() {
            @Override
            protected boolean matchesSafely(Address address) {
                return address.isDomicile();
            }
        });
    }

    private Address getMailingAddress(Organisation organisation) {
        List<Address> addresses = organisation.getAddresses();
        return Lambda.selectFirst(addresses, new LambdaMatcher<Address>() {
            @Override
            protected boolean matchesSafely(Address address) {
                return address.isMailingAddress();
            }
        });
    }

}
