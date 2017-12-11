package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressTypeV2;
import com.bt.nextgen.api.client.model.AddressV2Dto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

@Service
@Transactional
public class OrganizationDtoConverter {

    @Autowired
    private AddressDtoConverter addressDtoConverter;

    @Autowired
    private StaticIntegrationService staticService;

    @Autowired
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    @Autowired
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    private final static String VERIFIED = "Verified";

    public RegisteredEntityDto convertFromOrganizationForm(IClientApplicationForm form) {
        RegisteredEntityDto organization = getOrganisationDto(form);
        IOrganisationForm organisationForm = getOrganisationForm(form);

        setCommonOrganisationProperties(organization, organisationForm);

        return organization;
    }

    private void setCommonOrganisationProperties(RegisteredEntityDto organization, IOrganisationForm organisationForm) {
        // TODO: breaks here when using UCM codes rather than standard ANZSIC
        Code industry = staticService.loadCodeByUserId(
                CodeCategory.ANZSIC_INDUSTRY,
                organisationForm.getAnzsicCode(),
                new FailFastErrorsImpl()
        );
        organization.setIndustry(null != industry ? industry.getName():null);

        organization.setFullName(organisationForm.getName());
        organization.setAbn(organisationForm.getABN());
        if (organisationForm.getDateOfRegistration() != null) {
            // ie no time component
            Date date = new Date(organisationForm.getDateOfRegistration().toGregorianCalendar().getTime().getTime());
            organization.setRegistrationDate(date);
        }
        organization.setRegistrationState(organisationForm.getRegistrationState());
        organization.setRegistrationForGst(organisationForm.getRegisteredForGST());

        if(organisationForm.getCrsTaxDetails() != null){
            organization.setTfnProvided(organisationForm.getCrsTaxDetails().getAustralianTaxDetails().hasTaxFileNumber());
            if(StringUtils.isNotBlank(organisationForm.getCrsTaxDetails().getAustralianTaxDetails().getExemptionReason())){
                Code tfnExemption = getTfnExemptionCode(organisationForm.getCrsTaxDetails().getAustralianTaxDetails().getExemptionReason());
                if (null != tfnExemption) {
                    organization.setExemptionReason(tfnExemption.getName());
                }
            }
        }

        organization.setIdVerified(organisationForm.hasIDVDocument());
        organization.setIdvs(VERIFIED);
    }


    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    private IOrganisationForm getOrganisationForm(IClientApplicationForm form) {
        switch (form.getAccountType()) {
            case CORPORATE_SMSF:
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
            case NEW_CORPORATE_SMSF:
                return form.getSmsf();

            case INDIVIDUAL_TRUST:
            case CORPORATE_TRUST:
                return form.getTrust();

            case COMPANY:
                return form.getCompanyDetails();

            default:
                throw new IllegalArgumentException("Unsupported account type " + form.getAccountType());
        }
    }

    @SuppressFBWarnings(
            value = "squid:RightCurlyBraceStartLineCheck",
            justification = "Just another account switch"
    )
    private RegisteredEntityDto getOrganisationDto(IClientApplicationForm form) {
        RegisteredEntityDto dto;
        switch (form.getAccountType()) {
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                dto = createIndividualSmsf(form);
                break;

            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
                dto = createCorporateSmsf(form);
                break;

            case INDIVIDUAL_TRUST:
                dto = getTrustBasedOnTrustType(form);
                break;

            case CORPORATE_TRUST:
                dto = createCorporateTrust(form);
                break;

            case COMPANY:
                dto = getCompanyDto(form.getCompanyDetails());
                break;

            default:
                throw new IllegalArgumentException("Unsupported account type " + form.getAccountType());
        }

        return dto;
    }

    private RegisteredEntityDto createCorporateSmsf(IClientApplicationForm form) {
        SmsfDto smsf = new SmsfDto();
        setSmsfAddress(form, smsf);
        smsf.setCompany(getCompanyDto(form.getCompanyTrustee()));
        smsf.setOccupierName(form.getCompanyTrustee().getOccupierName());
        if(form.getSmsf().getCrsTaxDetails() != null){
            crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(form.getSmsf(), smsf);
        }
        return smsf;
    }

    private void setSmsfAddress(IClientApplicationForm form, SmsfDto smsf) {
        if(StringUtils.isNotEmpty(form.getSmsf().getRegisteredAddress().getAddressIdentifier())){
            smsf.setAddressesV2(Arrays.asList(new AddressV2Dto(form.getSmsf().getRegisteredAddress().getDisplayText(), AddressTypeV2.REGISTERED)));
        }
        else {
            smsf.setAddresses(Arrays.asList(addressDtoConverter.getAddressDto(form.getSmsf().getRegisteredAddress(), true, false, new FailFastErrorsImpl())));
        }
    }

    private RegisteredEntityDto createIndividualSmsf(IClientApplicationForm form) {
        SmsfDto smsf = new SmsfDto();
        setSmsfAddress(form, smsf);
        if(form.getSmsf().getCrsTaxDetails() != null){
            crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(form.getSmsf(), smsf);
        }
        return smsf;
    }



    private RegisteredEntityDto createCorporateTrust(IClientApplicationForm form) {
        TrustDto trust = getTrustBasedOnTrustType(form);
        trust.setCompany(getCompanyDto(form.getCompanyTrustee()));
        return trust;
    }

    private TrustDto getTrustBasedOnTrustType(IClientApplicationForm form) {
        TrustDto trust = new TrustDto();
        ITrustForm trustForm = form.getTrust();
        trust.setTrustMemberClass(form.getShareholderAndMembers().getBeneficiaryClassDetails());
        if(StringUtils.isNotEmpty(form.getTrust().getRegisteredAddress().getAddressIdentifier())) {
            trust.setAddressesV2(Arrays.asList(new AddressV2Dto(form.getTrust().getRegisteredAddress().getDisplayText(), AddressTypeV2.REGISTERED)));
        }
        else{
            trust.setAddresses(Arrays.asList(addressDtoConverter.getAddressDto(form.getTrust().getRegisteredAddress(), true, false, new FailFastErrorsImpl())));
        }
        trust.setBusinessName(trustForm.getBusinessName());
        trust.setTrustType(trustForm.getTrustType().value());
        if(trustForm.getCrsTaxDetails() != null){
            crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(trustForm, trust);
        }

        switch (trustForm.getTrustType()) {
            case FAMILY:
            case OTHER:
                trust.setBusinessClassificationDesc(trustForm.getDescription());
                populateCMADetailsForTrust(trustForm.getPersonalInvestmentEntity(),trust);
                break;

            case REGULATED:
                trust.setTrustReguName(trustForm.getRegulatorName());
                trust.setLicencingNumber(trustForm.getRegulatorLicenseNumber());
                break;

            case REGISTERED_MIS:
                trust.setArsn(trustForm.getArsn());
                break;

            case GOVT_SUPER:
                trust.setLegEstFund(trustForm.getNameOfLegislation());
                break;

            default:
                throw new IllegalArgumentException("Unsupported trust type " + trustForm.getTrustType());
        }
        return trust;
    }

    private void populateCMADetailsForTrust(Boolean personalInvestmentEntity, TrustDto trust){
        if(personalInvestmentEntity != null){
            trust.setPersonalInvestmentEntity(personalInvestmentEntity ?"Yes":"No");
        }

    }

    private Code getTfnExemptionCode(String intlId) {
        ExemptionReason exemptionReason = ExemptionReason.fromValue(intlId);
        if (null != exemptionReason) {
            intlId = exemptionReason.toString();
        }
        return staticService.loadCodeByAvaloqId(CodeCategory.EXEMPTION_REASON, intlId, new FailFastErrorsImpl());
    }

    private CompanyDto getCompanyDto(ICompanyForm companyTrustee) {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setFullName(companyTrustee.getName());
        companyDto.setAsicName(companyTrustee.getAsicName());
        companyDto.setAcn(companyTrustee.getACN());
        companyDto.setAbn(companyTrustee.getABN());
        companyDto.setIndustry(staticService.loadCodeByUserId(CodeCategory.ANZSIC_INDUSTRY, companyTrustee.getAnzsicCode(), new FailFastErrorsImpl()).getName());
        setCompanyAddress(companyTrustee, companyDto);
        companyDto.setIdvs(VERIFIED);
        companyDto.setOccupierName(companyTrustee.getOccupierName());
        if(companyTrustee.getPersonalInvestmentEntity() != null){
            companyDto.setPersonalInvestmentEntity(companyTrustee.getPersonalInvestmentEntity() ? "Yes":"No");
        }
        if(companyTrustee.getCrsTaxDetails() != null){
            crsTaxDetailHelperService.populateCRSTaxDetailsForOrganization(companyTrustee, companyDto);
        }
        return companyDto;
    }

    private void setCompanyAddress(ICompanyForm companyTrustee, CompanyDto companyDto) {
        if(StringUtils.isNotEmpty(companyTrustee.getRegisteredAddress().getAddressIdentifier())) {
            AddressV2Dto registeredAddressV2 = new AddressV2Dto(companyTrustee.getRegisteredAddress().getDisplayText(), AddressTypeV2.REGISTERED);
            AddressV2Dto placeOfBusinessAddressV2 = new AddressV2Dto(companyTrustee.getPlaceOfBusinessAddress().getDisplayText(), AddressTypeV2.PLACEOFBUSINESS);
            companyDto.setAddressesV2(Arrays.asList(registeredAddressV2, placeOfBusinessAddressV2));
        }
        else{
            IAddressForm registeredAddress = companyTrustee.getRegisteredAddress();
            IAddressForm placeOfBusinessAddress = companyTrustee.getPlaceOfBusinessAddress();
            AddressDto registeredAddressDto = addressDtoConverter.getAddressDto(registeredAddress, true, false, new FailFastErrorsImpl());
            AddressDto placeOfBusinessAddressDto = addressDtoConverter.getAddressDto(placeOfBusinessAddress, false, true, new FailFastErrorsImpl());
            companyDto.setAddresses(Arrays.asList(registeredAddressDto, placeOfBusinessAddressDto));
        }
    }

}
