package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AusTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.BooleanValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.StringValue;
import org.apache.commons.lang.StringUtils;

import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPlaceOfBirth;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ContactTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ContactValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PhoneNumber;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Email;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Phone;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Placeofbirth;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Key;
import com.bt.nextgen.service.integration.domain.Gender;

import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.EMAIL;
import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.HOME_NUMBER;
import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.MOBILE;
import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.OTHER_NUMBER;
import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.SECONDARY_EMAIL;
import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.SECONDARY_MOBILE;
import static com.bt.nextgen.api.draftaccount.model.form.IContactValue.ContactType.WORK_NUMBER;
import static com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil.date;
import static org.apache.commons.lang.BooleanUtils.isFalse;
import static org.apache.commons.lang.BooleanUtils.isTrue;

/**
 * Created by m040398 on 14/03/2016.
 *
 * IPersonDetailsForm implementation using customerSchema.json
 */
@SuppressWarnings("squid:S1200")
class PersonDetailsForm implements IPersonDetailsForm {

    final Customer customer;
    private final Integer index;

    public PersonDetailsForm(Integer index, Customer personDetails) {
        this.customer = personDetails;
        this.index = index;
    }

    @Override
    public boolean hasPostalAddress() {
        return this.customer.getPostaladdress() != null || this.customer.getPostaladdressv2() != null;
    }

    @Override
    public AddressForm getPostalAddress() {
        if(this.customer.getPostaladdressv2() != null) {
            return  new AddressForm(customer.getPostaladdressv2());
        }
        return new AddressForm(customer.getPostaladdress());
    }

    @Override
    public boolean hasResidentialAddress() {
        return null != this.customer.getResaddress() || null != this.customer.getResaddressv2();
    }

    @Override
    public IAddressForm getResidentialAddress() {
        if(this.customer.getResaddressv2() != null) {
            return  new AddressForm(customer.getResaddressv2());
        }
        return new AddressForm(customer.getResaddress());
    }


    @Override
    public String getTitle() {
        return this.customer.getTitle();
    }

    @Override
    public void setTitle(String title) {
        this.customer.setTitle(title);
    }

    public String getFirstName() {
        return this.customer.getFirstname();
    }

    @Override
    public void setFirstName(String firstName) {
        this.customer.setFirstname(firstName);
    }

    @Override
    public String getMiddleName() {
        return this.customer.getMiddlename();
    }

    @Override
    public void setMiddleName(String middleName) {
        this.customer.setMiddlename(middleName);
    }

    @Override
    public String getLastName() {
        return this.customer.getLastname();
    }

    @Override
    public void  setLastName(String lastName) {
        this.customer.setLastname(lastName);
    }

    @Override
    public String getPreferredName() {
        return this.customer.getPreferredname();
    }

    @Override
    public void setPreferredName(String preferredName) {
        this.customer.setPreferredname(preferredName);
    }

    @Override
    public String getAlternateName() {
        return this.customer.getAlternatename();
    }

    @Override
    public boolean hasGender() {
        return this.customer.getGender() != null;
    }

    @Override
    public Gender getGender() {
        return Gender.valueOf(this.customer.getGender().toUpperCase());
    }

    @Override
    public void setGender(String gender) {
        this.customer.setGender(gender);
    }

    @Override
    public String getGenderAsString() {
        return this.customer.getGender();
    }

    @Override
    public void setGcmUpdated(boolean value) {
        if (value && !isGcmUpdated()) {
            this.customer.setGcmUpdated(true);
        }
    }

    @Override
    public boolean isGcmUpdated() {
        if (this.customer.getGcmUpdated() != null) {
            return this.customer.getGcmUpdated();
        }
        return false;
    }

    @Override
    public void setPanoramanumber(String gcmId){
        this.customer.setGcmId(gcmId);
    }

    @Override
    public XMLGregorianCalendar getDateOfBirthAsCalendar() {
        String dateOfBirth = this.customer.getDateofbirth();
        XMLGregorianCalendar xmlGregorianCalendar = date(dateOfBirth, "dd/MM/yyyy");
        if(xmlGregorianCalendar == null){
            xmlGregorianCalendar = date(dateOfBirth, "dd MMM yyyy");
        }
        return xmlGregorianCalendar;
    }

    @Override
    public String getDateOfBirth() {
        return  this.customer.getDateofbirth();
    }

    @Override
    public void setDateOfBirth(String dateofBirth){
        this.customer.setDateofbirth(dateofBirth);
    }

    @Override
    public void setRegistered(boolean registered){
       this.customer.setRegistered(registered);
    }

    @Override
    public void setClientKey(String clientKey){
        Key key = new Key();
        key.setClientId(clientKey);
        this.customer.setKey(key);
    }

    @Override
    public void updateResidentialAddress(AddressDto address) {
        if (hasResidentialAddress()) {
            Address resAdr = customer.getResaddress();
            resAdr.setAddressLine1(address.getAddressLine1());
            resAdr.setAddressLine2(address.getAddressLine2());
            resAdr.setBuilding(address.getBuilding());
            resAdr.setCity(address.getCity());
            resAdr.setSuburb(address.getSuburb());
            resAdr.setCountry(address.getCountry());
            resAdr.setCountryCode(address.getCountryCode());
            resAdr.setFloor(address.getFloor());
            resAdr.setPin(address.getPoBox());
            resAdr.setPostcode(address.getPostcode());
            resAdr.setState(address.getState());
            resAdr.setStreetName(address.getStreetName());
            resAdr.setStreetNumber(address.getStreetNumber());
            resAdr.setStreetType(address.getStreetType());
            resAdr.setUnitNumber(address.getUnitNumber());
            resAdr.setComponentised(true);
            resAdr.setStandardAddressFormat(address.isStandardAddressFormat());
        }
    }

    @Override
    public void updatePostalAddress(AddressDto address) {
        if (hasPostalAddress()) {
            Address potalAddress = customer.getPostaladdress();
            potalAddress.setAddressLine1(address.getAddressLine1());
            potalAddress.setAddressLine2(address.getAddressLine2());
            potalAddress.setBuilding(address.getBuilding());
            potalAddress.setCity(address.getCity());
            potalAddress.setCountry(address.getCountry());
            potalAddress.setCountryCode(address.getCountryCode());
            potalAddress.setFloor(address.getFloor());
            potalAddress.setSuburb(address.getSuburb());
            potalAddress.setPin(address.getPoBox());
            potalAddress.setPostcode(address.getPostcode());
            potalAddress.setState(address.getState());
            potalAddress.setStreetName(address.getStreetName());
            potalAddress.setStreetNumber(address.getStreetNumber());
            potalAddress.setStreetType(address.getStreetType());
            potalAddress.setUnitNumber(address.getUnitNumber());
            potalAddress.setComponentised(true);
            potalAddress.setStandardAddressFormat(address.isStandardAddressFormat());
        }
    }

    @Override
    public boolean hasHomeNumber() {
        return customer.getHomenumber() != null;
    }

    @Override
    public IContactValue getHomeNumber() {
        final PhoneNumber homeNumber = this.customer.getHomenumber();
        IContactValue v = null;
        if (homeNumber != null) {
            v = new PhoneNumberContact(homeNumber, isPreferredContact(HOME_NUMBER));
        }
        return v;
    }

    @Override
    public boolean hasWorkNumber() {
        return this.customer.getWorknumber() != null;
    }

    @Override
    public IContactValue getWorkNumber() {
        final PhoneNumber worknumber = this.customer.getWorknumber();
        IContactValue v = null;
        if (worknumber != null) {
            v = new PhoneNumberContact(worknumber, isPreferredContact(WORK_NUMBER));
        }
        return v;
    }

    @Override
    public boolean hasEmail() {
        return customer.getEmail() != null;
    }

    @Override
    public IContactValue getEmail() {
        final ContactValue email = this.customer.getEmail();
        IContactValue v = null;
        if (email != null) {
            v = new ContactValueContact(email, isPreferredContact(EMAIL));
        }
        return v;
    }

    @Override
    public void removeEmail() {
        this.customer.setEmail(null);
    }

    @Override
    public void removeMobile() {
        this.customer.setMobile(null);
    }

    @Override
    public boolean hasSecondaryEmailAddress() {
        return this.customer.getSecondaryemail() != null;
    }

    @Override
    public IContactValue getSecondaryEmailContact() {
        final ContactValue secondaryemail = this.customer.getSecondaryemail();
        IContactValue v = null;
        if (secondaryemail != null) {
            v = new ContactValueContact(secondaryemail, isPreferredContact(SECONDARY_EMAIL));
        }
        return v;
    }

    @Override
    public boolean hasMobile() {
        return customer.getMobile() != null;
    }

    @Override
    public IContactValue getMobile() {
        final ContactValue mobile = this.customer.getMobile();
        IContactValue v = null;
        if (mobile != null) {
            v = new ContactValueContact(mobile, isPreferredContact(MOBILE));
        }
        return v;
    }

    @Override
    public boolean hasSecondaryMobileNumber() {
        return customer.getSecondarymobile() != null;
    }

    @Override
    public IContactValue getSecondaryMobile() {
        final ContactValue secondaryMobile = this.customer.getSecondarymobile();
        IContactValue v = null;
        if (secondaryMobile != null) {
            v = new ContactValueContact(secondaryMobile, isPreferredContact(SECONDARY_MOBILE));
        }
        return v;
    }

    @Override
    public IContactValue getContactValue(String type) {
        IContactValue value = null;
        final IContactValue.ContactType contactType = IContactValue.ContactType.fromJson(type);
        if (contactType != null) {
            switch (contactType) {
                case MOBILE:
                    value = getMobile();
                    break;
                case SECONDARY_MOBILE:
                    value = getSecondaryMobile();
                    break;
                case EMAIL:
                    value = getEmail();
                    break;
                case SECONDARY_EMAIL:
                    value = getSecondaryEmailContact();
                    break;
                case HOME_NUMBER:
                    value = getHomeNumber();
                    break;
                case WORK_NUMBER:
                    value = getWorkNumber();
                    break;
                case OTHER_NUMBER:
                    value = getOtherNumber();
                    break;
                default:
                    // Screw you, Sonar
                    break;
            }
        }
        return value;
    }

    @Override
    public void removeContactValue(String type) {
        final IContactValue.ContactType contactType = IContactValue.ContactType.fromJson(type);
        if (contactType != null) {
            switch (contactType) {
                case MOBILE:
                    removeMobile();
                    break;
                case SECONDARY_MOBILE:
                    customer.setSecondarymobile(null);
                    break;
                case EMAIL:
                    removeEmail();
                    break;
                case WORK_NUMBER:
                    customer.setWorknumber(null);
                    break;
                case HOME_NUMBER:
                    customer.setHomenumber(null);
                    break;
                case SECONDARY_EMAIL:
                    customer.setSecondaryemail(null);
                    break;
                default:
                    // Screw you, Sonar
                    break;
            }
        }
    }

    @Override
    public boolean isIdVerified(){
        return isTrue(customer.getIdVerified());
    }

    @Override
    public void setIdVerified(boolean idVerified){
        this.customer.setIdVerified(idVerified);
    }

    @Override
    public boolean hasIdVerified() {
        return this.customer.getIdVerified() != null;
    }

    @Override
    public IIdentityVerificationForm getIdentityVerificationForm() {
        return new IdentityVerificationForm(customer.getIdentitydocument());
    }

    @Override
    public String getClientKey() {
        if (this.customer.getKey() != null) {
            return this.customer.getKey().getClientId();
        }
        return null;
    }

    @Override
    public boolean isExistingPerson() {
        return this.customer.getKey() != null;
    }

    @Override
    public boolean isGcmRetrievedPerson() {
        return isFalse(customer.getRegistered());
    }

    @Override
    public String getPanoramaNumber() {
        return this.customer.getGcmId();
    }

    @Override
    public boolean hasSourceOfWealth() {
        return getSourceOfWealth() != null;
    }

    @Override
    public String getSourceOfWealth() {
        return this.customer.getWealthsource();
    }

    @Override
    public String getAdditionalSourceOfWealth() {
        return this.customer.getAdditionalsourceofwealth();
    }

    @Override
    public String getCisId() {
        return this.customer.getCisId();
    }


    @Override
    public List<?> getAddresses(){
        throw new IllegalStateException("using JSON schemas this method should not be called directly");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAddresses(List<?> addresses){
        throw new IllegalStateException("using JSON schemas this method should not be called");
    }

    @Override
    public IAddressForm getGCMRetAddresses() {
        return getResidentialAddress();
    }

    @Override
    public List<?> getEmails() {
        return this.customer.getEmails();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setEmails(List<?> emails){
        this.customer.setEmails((List<Email>) emails);
    }

    @Override
    public List<Phone> getPhones() {
        return this.customer.getPhones();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setPhones(List phones){
        this.customer.setPhones((List<Phone>) phones);
    }

    @Override
    public String getFormerName(){
        return this.customer.getFormername();
    }

    public IPlaceOfBirth getPlaceOfBirth(){
        final Placeofbirth pob = this.customer.getPlaceofbirth();
        return pob != null ? new PlaceOfBirth(pob) : null;
    }

    public String getUserName() {
        return this.customer.getUserName();
    }

    public String getPreferredContact() {
        return customer.getPreferredcontact()!=null ? customer.getPreferredcontact().toString() : null;
    }

    public void removePreferredContact() {
        customer.setPreferredcontact(ContactTypeEnum.__EMPTY__);
    }

    public void removeContactDetails() {
        customer.setContactdetails(null);
    }

    @Override
    public boolean hasJsonSchema() {
        return true; //this form implementation is based on JSON schema v1
    }

    @Override
    public boolean hasOtherNumber() {
        return customer.getOthernumber() != null;
    }

    @Override
    public IContactValue getOtherNumber() {
        final PhoneNumber otherNumber = this.customer.getOthernumber();
        IContactValue v = null;
        if (otherNumber != null) {
            v = new PhoneNumberContact(otherNumber, isPreferredContact(OTHER_NUMBER));
        }
        return v;
    }

    @Override
    public boolean hasTaxFileNumber() {
        return StringUtils.isNotEmpty(getTaxFileNumber());
    }

    @Override
    public String getTaxFileNumber() {
        return this.customer.getTfn();
    }

    @Override
    public String getExemptionReason() {
        return StringUtils.isNotBlank(this.customer.getExemptionreason()) ? this.customer.getExemptionreason() : this.customer.getExemptionReason();
    }

    @Override
    public boolean hasExemptionReason() {
        return StringUtils.isNotEmpty(getExemptionReason());
    }

    @Override
    public String getTaxoption() {
        return customer.getTaxoption();
    }

    @Override
    public String getIsForeignRegistered() {
        return customer.getIsForeignRegistered();
    }

    @Override
    public void setIsForeignRegistered(String isForeignRegistered) {
        customer.setIsForeignRegistered(isForeignRegistered);
    }

    @Override
    public void setIsOverseasTaxRes(Boolean isOverseasTaxRes) {
        final BooleanValue booleanValue = new BooleanValue();
        booleanValue.setValue(isOverseasTaxRes);
        booleanValue.setValid(true);
        customer.getTaxdetails().setIsOverseasTaxRes(booleanValue);
    }

    @Override
    public Boolean getIsOverseasTaxRes(){
        return customer.getTaxdetails()!=null && customer.getTaxdetails().getIsOverseasTaxRes()!=null ? customer.getTaxdetails().getIsOverseasTaxRes().getValue() : null;
    }



    @Override
    public String getTaxCountryCode() {
        return this.customer.getTaxcountry();
    }

    private boolean isPreferredContact(IContactValue.ContactType contact) {
        final ContactTypeEnum preferred = customer.getPreferredcontact();
        return preferred != null && contact.getJsonValue().equals(preferred.toString());
    }

    @Override
    public Integer getCorrelationSequenceNumber() {
        return index;
    }

    @Override
    public IAusTaxDetailsForm getAustralianTaxDetails() {
        return new AusTaxDetailsForm(this.customer.getTaxdetails().getAusTaxDetails());
    }

    @Override
    public List<IOverseasTaxDetailsForm> getOverseasTaxDetails() {
        return Lambda.convert(this.customer.getTaxdetails().getOverseasTaxDetails(), new Converter<OverseasTaxDetails, IOverseasTaxDetailsForm>() {
            @Override
            public IOverseasTaxDetailsForm convert(OverseasTaxDetails overseasTaxDetails) {
                return new OverseasTaxDetailsForm(overseasTaxDetails);
            }
        });
    }

    @Override
    public void setOverseasTaxDetails(List<OverseasTaxDetails> overseasTaxDetails) {
        if(hasCrsTaxDetails()){
            this.customer.getTaxdetails().setOverseasTaxDetails(overseasTaxDetails);
        }
    }

    @Override
    public String getOverseasTaxCountry() {
        return this.hasOverseasTaxCountry() ? this.customer.getTaxdetails().getSelectedOverseasTaxCountry().getValue(): "";
    }

    @Override
    public void setOverseasTaxCountry(StringValue value) {
        if(this.hasOverseasTaxCountry()){
            this.customer.getTaxdetails().setSelectedOverseasTaxCountry(value);
        }
    }

    @Override
    public boolean hasOverseasTaxCountry() {
        return null!=this.customer.getTaxdetails() && null!=this.customer.getTaxdetails().getSelectedOverseasTaxCountry();
    }


    @Override
    public boolean hasCrsTaxDetails() {
        return null!=this.customer.getTaxdetails();
    }
}
