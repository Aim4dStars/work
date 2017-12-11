package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressTypeV2;
import com.bt.nextgen.api.client.model.AddressV2Dto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPlaceOfBirth;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.PersonTitle;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.client.service.GlobalCustomerDtoServiceImpl.fullName;
import static org.springframework.util.StringUtils.hasText;

@Service
@Transactional
@SuppressWarnings("squid:S1200")
public class IndividualDtoConverter {

    @Autowired
    private StaticIntegrationService staticService;

    @Autowired
    private AddressDtoConverter addressDtoConverter;

    @Autowired
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    private static final String PENSIONER = "pensioner";

    private static final String VERIFIED = "Verified";
    /**
     * * Only used by unit tests
     * @param staticService
     */
    void setStaticService(StaticIntegrationService staticService) {
        this.staticService = staticService;
    }

    /**
     * Only used by unit tests
     * @param addressDtoConverter
     */
    void setAddressDtoConverter(AddressDtoConverter addressDtoConverter) {
        this.addressDtoConverter = addressDtoConverter;
    }

    @SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck"})
    public IndividualDto convertFromIndividualForm(IExtendedPersonDetailsForm investor, ServiceErrors serviceErrors, IClientApplicationForm.AccountType accountType) {
        IndividualDto investorDto = new IndividualDto();
        investorDto.setTitle(getPersonTitle(investor, serviceErrors));
        investorDto.setFullName(fullName(investor.getFirstName(), investor.getMiddleName(), investor.getLastName()));
        investorDto.setFirstName(investor.getFirstName());
        investorDto.setLastName(investor.getLastName());
        investorDto.setPreferredName(investor.getPreferredName());
        if (investor.getDateOfBirthAsCalendar() != null) {
            investorDto.setDateOfBirth(ApiFormatter.asShortDate(investor.getDateOfBirthAsCalendar().toGregorianCalendar().getTime()));
        }
        if (investor.hasGender()) {
            final Gender gender = investor.getGender();
            final String genderName = Gender.OTHER == gender ? "Other" : gender.getName();
            investorDto.setGender(genderName);
        }
        if(StringUtils.isNotBlank(investor.getFormerName())){
            investorDto.setFormerName(investor.getFormerName());
        }

        if(null!=investor.getAustralianTaxDetails()) {
            populateAusTaxOptions(investor.getAustralianTaxDetails(), investorDto, accountType, serviceErrors);
        }
        if (investor.hasCrsTaxDetails()) {
            crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(investor, investorDto);
        }

        setInvestorAdddresses(investor, investorDto, serviceErrors);

        if (investor.hasEmail()) {
            investorDto.setEmails(getEmails(investor));
        }
        if (investor.hasMobile()) {
            investorDto.setPhones(getPhones(investor));
        }
        investorDto.setPersonRoles(getPersonRoles(investor));
        setIdvsForInvestor(investor, investorDto);
        setPlaceOfBirthDetails(investor, investorDto, serviceErrors);

        return investorDto;
    }

    private void populateAusTaxOptions(IAusTaxDetailsForm ausTaxDetails, IndividualDto investorDto, IClientApplicationForm.AccountType accountType, ServiceErrors serviceErrors) {
        investorDto.setTfnProvided(ausTaxDetails.hasTaxFileNumber());
        if (accountType == IClientApplicationForm.AccountType.SUPER_PENSION && ausTaxDetails.getTaxOption().value().equals(PENSIONER)){
            investorDto.setExemptionReason(PensionExemptionReason.getPensionExemptionReason(ausTaxDetails.getTaxOption().value()).getValue());
        }
        else {
            if (StringUtils.isNotBlank(ausTaxDetails.getExemptionReason())) {
                ExemptionReason exemptionReason = ExemptionReason.getExemptionReason(ausTaxDetails.getExemptionReason());
                investorDto.setExemptionReason(null != exemptionReason ? exemptionReason.getValue() : null);
            }
        }
    }

    private void setInvestorAdddresses(IExtendedPersonDetailsForm investor, IndividualDto investorDto, ServiceErrors serviceErrors){
        List<AddressDto> addresses = new ArrayList<>();
        List<AddressV2Dto> addressesV2 = new ArrayList<>();

        if(investor.hasResidentialAddress()){
            if(StringUtils.isNotEmpty(investor.getResidentialAddress().getAddressIdentifier())) {
                addressesV2.add(new AddressV2Dto(investor.getResidentialAddress().getDisplayText(), AddressTypeV2.RESIDENTIAL));
            }else{
                addresses.add(addressDtoConverter.getAddressDto(investor.getResidentialAddress(), true, false, serviceErrors));
            }
        }

        if(investor.hasPostalAddress()){
            if(StringUtils.isNotEmpty(investor.getPostalAddress().getAddressIdentifier())) {
                addressesV2.add(new AddressV2Dto(investor.getPostalAddress().getDisplayText(), AddressTypeV2.POSTAL));
            }else{
                addresses.add(addressDtoConverter.getAddressDto(investor.getPostalAddress(), false, true, serviceErrors));
            }
        }

        investorDto.setAddressesV2(addressesV2);
        investorDto.setAddresses(addresses);

    }


    private void setPlaceOfBirthDetails(IExtendedPersonDetailsForm investor, IndividualDto investorDto, ServiceErrors serviceErrors) {
        IPlaceOfBirth placeOfBirth =  investor.getPlaceOfBirth();
        if (null!= placeOfBirth) {
            Code country = staticService.loadCodeByUserId(CodeCategory.COUNTRY, placeOfBirth.getCountryOfBirth(), serviceErrors);
            investorDto.setPlaceOfBirthCountry(country.getName());
            investorDto.setPlaceOfBirthState(placeOfBirth.getStateOfBirth());
            investorDto.setPlaceOfBirthSuburb(placeOfBirth.getCityOfBirth());
        }
    }

    private void setIdvsForInvestor(IExtendedPersonDetailsForm investor, IndividualDto investorDto) {
        if (!isOnlyBeneficiaryOrMember(investor)) {
            investorDto.setIdvs(VERIFIED); //already submitted
        }
    }

    private boolean isOnlyBeneficiaryOrMember(IExtendedPersonDetailsForm investor) {
        IIdentityVerificationForm verForm =  investor.getIdentityVerificationForm();
        return !verForm.hasInternationalDocuments() && !verForm.hasNonPhotoDocuments() && !verForm.hasPhotoDocuments() && !investor.isIdVerified();
    }

    private String getPersonTitle(IExtendedPersonDetailsForm investor, ServiceErrors serviceErrors) {
        Code code = staticService.loadCodeByUserId(CodeCategory.PERSON_TITLE, investor.getTitle(), serviceErrors);
        if (code != null) {
            return code.getName();
        }
        return PersonTitle.valueOf(investor.getTitle().toUpperCase()).getDescription();
    }

    public List<InvestorRole> getPersonRoles(IExtendedPersonDetailsForm investor) {
        List<InvestorRole> roles = new ArrayList<>();
        if (investor.isBeneficiary()) {
            roles.add(InvestorRole.Beneficiary);
        }
        if (investor.isMember()) {
            roles.add(InvestorRole.Member);
        }
        if (investor.isShareholder()) {
            roles.add(InvestorRole.BeneficialOwner);
        }
        if (investor.isBeneficialOwner()) {
            roles.add(InvestorRole.BeneficialOwner);
        }
        if (investor.isControllerOfTrust()) {
            roles.add(InvestorRole.ControllerOfTrust);
        }
        if(investor instanceof IDirectorDetailsForm)     {
            IDirectorDetailsForm director = (IDirectorDetailsForm)investor;
            if(director.isCompanySecretary()){
                roles.add(InvestorRole.Secretary);
            }
        }
        return roles;
    }

    public String getExemptionReason(String investorExemptionReason, ServiceErrors serviceErrors) {
        ExemptionReason exemptionReason = ExemptionReason.fromValue(investorExemptionReason);
        if (null != exemptionReason) {
            investorExemptionReason = exemptionReason.toString();
        }
        Code code = staticService.loadCodeByAvaloqId(CodeCategory.EXEMPTION_REASON, investorExemptionReason, serviceErrors);
        return code != null ? code.getName() : "";
    }


    private List<PhoneDto> getPhones(IPersonDetailsForm investor) {
        List<PhoneDto> phones = Lists.newArrayList(getPhoneDto(investor.getMobile(), AddressMedium.MOBILE_PHONE_PRIMARY));

        if (investor.hasSecondaryMobileNumber()) {
            phones.add(getPhoneDto(investor.getSecondaryMobile(), AddressMedium.MOBILE_PHONE_SECONDARY));
        }

        if (investor.hasHomeNumber()) {
            phones.add(getPhoneDto(investor.getHomeNumber(), AddressMedium.PERSONAL_TELEPHONE));
        }

        if (investor.hasWorkNumber()) {
            phones.add(getPhoneDto(investor.getWorkNumber(), AddressMedium.BUSINESS_TELEPHONE));
        }

        if (investor.hasOtherNumber()) {
            phones.add(getPhoneDto(investor.getOtherNumber(), AddressMedium.OTHER));
        }
        return phones;
    }

    private List<EmailDto> getEmails(IPersonDetailsForm investor) {
        List<EmailDto> emails = Lists.newArrayList(getEmailDto(investor.getEmail(), AddressMedium.EMAIL_PRIMARY));

        if (investor.hasSecondaryEmailAddress()) {
            emails.add(getEmailDto(investor.getSecondaryEmailContact(), AddressMedium.EMAIL_ADDRESS_SECONDARY));
        }
        return emails;
    }

    private PhoneDto getPhoneDto(IContactValue phone, AddressMedium addressMedium) {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPreferred(phone.isPreferredContact());
        phoneDto.setNumber(phone.getValue());
        if (hasText(phone.getAreaCode())) {
            phoneDto.setAreaCode(phone.getAreaCode());
        }
        if (hasText(phone.getCountryCode())) {
            phoneDto.setCountryCode(phone.getCountryCode());
        }
        phoneDto.setPhoneType(addressMedium.getAddressType());
        return phoneDto;
    }

    private EmailDto getEmailDto(IContactValue email, AddressMedium addressMedium) {
        EmailDto emailDto = new EmailDto();
        emailDto.setPreferred(email.isPreferredContact());
        emailDto.setEmail(email.getValue());
        emailDto.setEmailType(addressMedium.getAddressType());
        return emailDto;
    }

}
