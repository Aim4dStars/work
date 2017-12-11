package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.AddressDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPlaceOfBirth;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.GenderTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PersonTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.StringValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.AdditionalShareholderAndMember;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.service.integration.domain.Gender;

/**
 * Created by m040398 on 24/03/2016.
 * Most methods do not have an implementation because the inherited old design model was wrong: the additional shareholders
 * are not fully IExtendedPersonDetailsForm types (they have some common methods but much less than IExtendedPersonDetailsForm).
 * <br>
 * All the unnecessary methods for this type will return NULL or FALSE. <br>
 *  TODO: refactor builders so that AdditionalSharehodersAndMembers will have a different type interface (different than IExtendedPersonDetailsForm)
 */
@SuppressWarnings({ "checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.MethodCountCheck"})
public class ShareholderMemberDetailsForm implements IExtendedPersonDetailsForm {

    private final AdditionalShareholderAndMember additionalShareholder;
    private final Integer index;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareholderMemberDetailsForm.class);

    public ShareholderMemberDetailsForm(Integer index, AdditionalShareholderAndMember additionalShareholder) {
        this.index = index;
        this.additionalShareholder = additionalShareholder;
    }

    @Override
    public boolean isPrimaryContact() {
        return false;
    }

    @Override
    public PaymentAuthorityEnum getPaymentSetting() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPaymentSetting() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean isApprover() {
        return false;
    }

    @Override
    public String getClientKey() {
        LOGGER.warn("ShareholderMemberDetailsForm.getClientKey() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    /**
     * Existing person flag.
     * @return existing person flag.
     * @deprecated not useful info in the form data.
     */
    @Override
    @Deprecated
    public boolean isExistingPerson() {
        return false;
    }

    @Override
    public boolean isGcmRetrievedPerson() {
        return false;
    }

    @Override
    public String getPanoramaNumber() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPanoramaNumber() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean hasSourceOfWealth() {
        return false;
    }

    @Override
    public String getSourceOfWealth() {
        LOGGER.warn("ShareholderMemberDetailsForm.getSourceOfWealth() - method should not be called for AdditionalShareholderAndMember type");
        return  null;
    }

    @Override
    public String getAdditionalSourceOfWealth() {
        LOGGER.warn("ShareholderMemberDetailsForm.getAdditionalSourceOfWealth() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getCisId() {
        LOGGER.warn("ShareholderMemberDetailsForm.getCisId() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public List<?> getAddresses() {
        LOGGER.warn("ShareholderMemberDetailsForm.getAddresses() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void setAddresses(List<?> addresses) {
        LOGGER.warn("ShareholderMemberDetailsForm.setAddresses() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public void setRegistered(boolean registered){
        LOGGER.warn("ShareholderMemberDetailsForm.setRegistered() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public void setClientKey(String clientKey){
        LOGGER.warn("ShareholderMemberDetailsForm.setClientKey() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public void setPanoramanumber(String gcmId){
        LOGGER.warn("ShareholderMemberDetailsForm.setPanoramaNumber - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public IAddressForm getGCMRetAddresses() {
        LOGGER.warn("ShareholderMemberDetailsForm.getGCMRetAddresses() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public List<?> getEmails() {
        LOGGER.warn("ShareholderMemberDetailsForm.getEmails() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void setEmails(List<?> emails) {
        LOGGER.warn("ShareholderMemberDetailsForm.setEmails() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public List<?> getPhones() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPhones() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void setPhones(List<?> phones) {
        LOGGER.warn("ShareholderMemberDetailsForm.setPhones() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public String getFormerName() {
        LOGGER.warn("ShareholderMemberDetailsForm.getFormerName() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public IPlaceOfBirth getPlaceOfBirth() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPlaceOfBirth() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getUserName() {
        LOGGER.warn("ShareholderMemberDetailsForm.getUserName() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getPreferredContact() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPreferredContact() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void removePreferredContact() {
        LOGGER.warn("ShareholderMemberDetailsForm.removePreferredContact() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public void removeContactDetails() {
        LOGGER.warn("ShareholderMemberDetailsForm.removeContactDetails() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public boolean hasJsonSchema() {
        return true;
    }

    @Override
    public boolean hasOtherNumber() {
        return false;
    }

    @Override
    public IContactValue getOtherNumber() {
        LOGGER.warn("ShareholderMemberDetailsForm.getOtherNumber() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getTaxoption() {
        LOGGER.warn("ShareholderMemberDetailsForm.getTaxoption() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getIsForeignRegistered() {
        LOGGER.warn("ShareholderMemberDetailsForm.getIsForeignRegistered() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void setIsForeignRegistered(String isForeignRegistered) {
        LOGGER.warn("ShareholderMemberDetailsForm.setIsForeignRegistered() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public void setIsOverseasTaxRes(Boolean isOverseasTaxRes) {
        LOGGER.error("ShareholderMemberDetailsForm.setIsOverseasTaxRes() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public Boolean getIsOverseasTaxRes() {
        LOGGER.error("ShareholderMemberDetailsForm.getIsOverseasTaxRes() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean isBeneficiary() {
        return additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY) ||
            additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY_AND_SHAREHOLDER) ||
            additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY_AND_BENEFICIAL_OWNER) ||
            additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY_AND_CONTROLLER);
    }

    @Override
    public boolean isShareholder() {
        return additionalShareholder.getPersontype().contains(PersonTypeEnum.SHAREHOLDER) ||
            additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY_AND_SHAREHOLDER) ||
                additionalShareholder.getPersontype().contains(PersonTypeEnum.SHAREHOLDER_AND_MEMBER);
    }

    @Override
    public boolean isMember() {
        return additionalShareholder.getPersontype().contains(PersonTypeEnum.MEMBER) ||
            additionalShareholder.getPersontype().contains(PersonTypeEnum.SHAREHOLDER_AND_MEMBER);
    }

    @Override
    public boolean isBeneficialOwner() {
        return additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIAL_OWNER) ||
            additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY_AND_BENEFICIAL_OWNER);
    }

    @Override
    public boolean isControllerOfTrust() {
        return additionalShareholder.getPersontype().contains(PersonTypeEnum.CONTROLLEROFTRUST) || additionalShareholder.getPersontype().contains(PersonTypeEnum.BENEFICIARY_AND_CONTROLLER);
    }

    @Override
    public boolean hasPostalAddress() {
        return false;
    }

    @Override
    public IAddressForm getPostalAddress() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPostalAddress() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getTitle() {
        return additionalShareholder.getTitle();
    }

    @Override
    public void setTitle(String title) {
        additionalShareholder.setTitle(title);
    }

    @Override
    public String getFirstName() {
        return additionalShareholder.getFirstname();
    }

    @Override
    public void setFirstName(String firstName) {
        additionalShareholder.setFirstname(firstName);
    }

    @Override
    public String getMiddleName() {
        return additionalShareholder.getMiddlename();
    }

    @Override
    public void setMiddleName(String middleName) {
        additionalShareholder.setMiddlename(middleName);
    }

    @Override
    public String getLastName() {
        return additionalShareholder.getLastname();
    }

    @Override
    public void setLastName(String lastName) {
        additionalShareholder.setLastname(lastName);
    }

    @Override
    public String getPreferredName() {
        LOGGER.warn("ShareholderMemberDetailsForm.getPreferredName() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void setPreferredName(String preferredName) {
        LOGGER.warn("ShareholderMemberDetailsForm.setPreferredName() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public String getAlternateName() {
        return additionalShareholder.getAlternatename();
    }

    public boolean hasGender() {
        return additionalShareholder.getAdditionalshareholdergender()!=null;
    }

    public Gender getGender() {
        if (hasGender()) {
            return Gender.valueOf(additionalShareholder.getAdditionalshareholdergender().toString().toUpperCase());
        }
        return null;
    }

    @Override
    public void setGender(String gender) {
        additionalShareholder.setAdditionalshareholdergender(GenderTypeEnum.fromValue(gender));
    }

    @Override
    public String getGenderAsString() {
         return hasGender() ? additionalShareholder.getAdditionalshareholdergender().toString() : null;
    }

    @Override
    public void setGcmUpdated(boolean value) {
        LOGGER.warn("ShareholderMemberDetailsForm.setGcmUpdated() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public boolean isGcmUpdated() {
        return false;
    }

    @Override
    public XMLGregorianCalendar getDateOfBirthAsCalendar() {
        String dateOfBirth = getDateOfBirth();
        XMLGregorianCalendar xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(dateOfBirth, "dd/MM/yyyy");
        if(xmlGregorianCalendar == null) {
            xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(dateOfBirth, "dd MMM yyyy");
        }
        return xmlGregorianCalendar;

    }

    @Override
    public String getDateOfBirth() {
        return additionalShareholder.getDateofbirth();
    }

    @Override
    public void setDateOfBirth(String dateofBirth) {
        additionalShareholder.setDateofbirth(dateofBirth);
    }

    @Override
    public IAddressForm getResidentialAddress() {
        if(additionalShareholder.getResaddressv2() != null){
            return new AddressForm(additionalShareholder.getResaddressv2());
        }
        return new AddressForm(additionalShareholder.getResaddress());
    }

    @Override
    public void updateResidentialAddress(AddressDto address) {
        throw new IllegalStateException("this method should not be called directly for AdditionalShareholderAndMember type");
    }

    @Override
    public void updatePostalAddress(AddressDto address) {
        throw new IllegalStateException("this method should not be called directly for AdditionalShareholderAndMember type");
    }

    @Override
    public boolean hasResidentialAddress() {
        return additionalShareholder.getResaddress() != null || additionalShareholder.getResaddressv2() != null;
    }

    @Override
    public boolean hasHomeNumber() {
        return false;
    }

    @Override
    public IContactValue getHomeNumber() {
        LOGGER.warn("ShareholderMemberDetailsForm.getHomeNumber() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean hasWorkNumber() {
        return false;
    }

    @Override
    public IContactValue getWorkNumber() {
        LOGGER.warn("ShareholderMemberDetailsForm.getWorkNumber() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean hasEmail() {
        return false;
    }

    @Override
    public IContactValue getEmail() {
        LOGGER.warn("ShareholderMemberDetailsForm.getEmail() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void removeEmail() {
        LOGGER.warn("ShareholderMemberDetailsForm.removeEmail() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public void removeMobile() {
        LOGGER.warn("ShareholderMemberDetailsForm.removeMobile() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public boolean hasSecondaryEmailAddress() {
        return false;
    }

    @Override
    public IContactValue getSecondaryEmailContact() {
        LOGGER.warn("ShareholderMemberDetailsForm.getSecondaryEmailContact() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean hasMobile() {
        return false;
    }

    @Override
    public IContactValue getMobile() {
        LOGGER.warn("ShareholderMemberDetailsForm.getMobile() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean hasSecondaryMobileNumber() {
        return false;
    }

    @Override
    public IContactValue getSecondaryMobile() {
        LOGGER.warn("ShareholderMemberDetailsForm.getSecondaryMobile() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public IContactValue getContactValue(String type) {
        LOGGER.warn("ShareholderMemberDetailsForm.getContactValue() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public void removeContactValue(String type) {
        LOGGER.warn("ShareholderMemberDetailsForm.removeContactValue() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public boolean isIdVerified() {
        return false;
    }

    @Override
    public void setIdVerified(boolean idVerified) {
        LOGGER.warn("ShareholderMemberDetailsForm.setIdVerified() - method should not be called for AdditionalShareholderAndMember type");
    }

    @Override
    public boolean hasIdVerified() {
        return false;
    }

    @Override
    public IIdentityVerificationForm getIdentityVerificationForm() {
        return new IdentityVerificationForm(additionalShareholder.getIdentitydocument());
    }

    @Override
    public boolean hasTaxFileNumber() {
        return false;
    }

    @Override
    public String getTaxFileNumber() {
        LOGGER.warn("ShareholderMemberDetailsForm.getTaxFileNumber() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public String getExemptionReason() {
        LOGGER.warn("ShareholderMemberDetailsForm.getExemptionReason() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public boolean hasExemptionReason() {
        return false;
    }

    @Override
    public String getTaxCountryCode() {
        LOGGER.warn("ShareholderMemberDetailsForm.getTaxCountryCode() - method should not be called for AdditionalShareholderAndMember type");
        return null;
    }

    @Override
    public Integer getCorrelationSequenceNumber() {
        return index;
    }

    @Override
    public IAusTaxDetailsForm getAustralianTaxDetails() {
        return this.hasCrsTaxDetails() ? new AusTaxDetailsForm(this.additionalShareholder.getTaxdetails().getAusTaxDetails()) : null;
    }

    @Override
    public List<IOverseasTaxDetailsForm> getOverseasTaxDetails() {
        if (null != this.additionalShareholder.getTaxdetails()) {
            return Lambda.convert(this.additionalShareholder.getTaxdetails().getOverseasTaxDetails(), new Converter<OverseasTaxDetails, IOverseasTaxDetailsForm>() {
                @Override
                public IOverseasTaxDetailsForm convert(OverseasTaxDetails overseasTaxDetails) {
                    return new OverseasTaxDetailsForm(overseasTaxDetails);
                }
            });
        }
        return new ArrayList<>();
    }

    @Override
    public void setOverseasTaxDetails(List<OverseasTaxDetails> overseasTaxDetails) {
        LOGGER.error("ShareholderMemberDetailsForm.setOverseasTaxDetails() - method should not be called for AdditionalShareholderAndMember type since its used during GCM retrieve");
    }

    @Override
    public String getOverseasTaxCountry() {
        return this.hasOverseasTaxCountry() ? this.additionalShareholder.getTaxdetails().getSelectedOverseasTaxCountry().getValue(): "";
    }

    @Override
    public void setOverseasTaxCountry(StringValue value) {
        LOGGER.error("ShareholderMemberDetailsForm.setOverseasTaxCountry() - method should not be called for AdditionalShareholderAndMember type since its used during GCM retrieve");
    }

    @Override
    public boolean hasCrsTaxDetails() {
        return null!=this.additionalShareholder && null!=this.additionalShareholder.getTaxdetails();
    }

    @Override
    public boolean hasOverseasTaxCountry() {
        return null!=this.additionalShareholder.getTaxdetails() && null!=this.additionalShareholder.getTaxdetails().getSelectedOverseasTaxCountry();
    }

    public AdditionalShareholderAndMember getAdditionalShareholder() {
        return additionalShareholder;
    }
}
