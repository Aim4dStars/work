package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.StringValue;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import com.bt.nextgen.service.integration.domain.Gender;
import org.slf4j.LoggerFactory;


/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class PersonDetailsForm extends TaxDetailsForm implements IPersonDetailsForm {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PersonDetailsForm.class);

    public PersonDetailsForm(Map<String, Object> personDetails) {
        super(personDetails);
    }

    @Override
    public boolean hasPostalAddress() {
        return map.get("postaladdress") != null;
    }

    @Override
    public IAddressForm getPostalAddress() {
        return new AddressForm((Map<String, Object>) map.get("postaladdress"));
    }

    @Override
    public String getTitle() {
        return (String) map.get("title");
    }

    @Override
    public void setTitle(String title) {
        map.put("title", title);}

    @Override
    public String getFirstName() {
        return (String) map.get("firstname");
    }

    @Override
    public void setFirstName(String firstName) {
        map.put("firstname", firstName);}

    @Override
    public String getMiddleName() {
        return (String) map.get("middlename");
    }

    @Override
    public void setMiddleName(String middleName) {
        map.put("middlename", middleName);
    }

    @Override
    public String getLastName() {
        return (String) map.get("lastname");
    }

    @Override
    public void  setLastName(String lastName) {
        map.put("lastname", lastName);
    }

    @Override
    public String getPreferredName() {
        return (String) map.get("preferredname");
    }

    @Override
    public void setPreferredName(String preferredName) {
        map.put("preferredname",preferredName);
    }

    @Override
    public String getAlternateName() {
        return (String) map.get("alternatename");
    }

    @Override
    public boolean hasGender() {
        return map.containsKey("gender");
    }

    @Override
    public Gender getGender() {
        final String gender = (String) map.get("gender");
        return Gender.valueOf(gender.toUpperCase());
    }

    @Override
    public void setGender(String gender) {
        map.put("gender",gender);
    }

    @Override
    public String getGenderAsString() {
        return (String) map.get("gender");
    }

    @Override
    public void setGcmUpdated(boolean value) {
        if (value && !isGcmUpdated()) {
            map.put("gcmUpdated", value);
        }
    }

    @Override
    public boolean isGcmUpdated() {
        if (map.get("gcmUpdated") != null) {
            return (boolean) map.get("gcmUpdated");
        }
        return false;
    }

    @Override
    public void setPanoramanumber(String gcmId){
        map.put(FormDataConstants.FIELD_GCM_ID,gcmId);
    }

    @Override
    public XMLGregorianCalendar getDateOfBirthAsCalendar() {
        String dateOfBirth = (String) map.get("dateofbirth");
        XMLGregorianCalendar xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(dateOfBirth, "dd/MM/yyyy");
        if(xmlGregorianCalendar == null){
            xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(dateOfBirth, "dd MMM yyyy");
        }
        return xmlGregorianCalendar;
    }

    @Override
    public String getDateOfBirth() {
        return  (String)map.get("dateofbirth");
    }

    @Override
    public void setDateOfBirth(String dateofBirth){
        map.put("dateofbirth",dateofBirth);
    }

    @Override
    public IAddressForm getResidentialAddress() {
        return new AddressForm((Map<String, Object>) map.get("resaddress"));
    }

    @Override
    public void updateResidentialAddress(AddressDto address) {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }

    @Override
    public void updatePostalAddress(AddressDto address) {
        throw new IllegalStateException("this method should not be called directly on old draft applications");
    }

    @Override
    public boolean hasResidentialAddress() {
        return map.get("resaddress") != null;
    }

    @Override
    public boolean hasHomeNumber() {
        return map.get("homenumber") != null || map.get("homeNumber") != null;
    }

    @Override
    public IContactValue getHomeNumber() {
        IContactValue v = getContactValue("homenumber");
        if (v.isNull()) {
            v = getContactValue("homeNumber");
        }
        return v;
    }

    @Override
    public boolean hasOtherNumber() {
        return map.get("othernumber") != null;
    }

    @Override
    public IContactValue getOtherNumber() {
        IContactValue v = getContactValue("othernumber");
        return v;
    }

    @Override
    public String getIsForeignRegistered() {
        LOGGER.error("this method should not be called for old draft applications");
        return null;
    }

    @Override
    public void setIsForeignRegistered(String isForeignRegistered) {
        LOGGER.error("this method should not be called for old draft applications");
    }

    @Override
    public void setIsOverseasTaxRes(Boolean isOverseasTaxRes) {
        LOGGER.error("this method should not be called for old draft applications");
    }

    @Override
    public Boolean getIsOverseasTaxRes() {
        LOGGER.error("this method should not be called for old draft applications");
        return null;
    }

    @Override
    public boolean hasWorkNumber() {
        return map.get("worknumber") != null || map.get("workNumber") != null;
    }

    @Override
    public IContactValue getWorkNumber() {
        IContactValue v = getContactValue("worknumber");
        if (v.isNull()) {
            v = getContactValue("workNumber");
        }
        return v;
    }

    @Override
    public boolean hasEmail() {
        return map.get("email") != null;
    }

    @Override
    public IContactValue getEmail() {
        return getContactValue("email");
    }

    @Override
    public void removeEmail() {
        if (hasEmail()) {
            map.remove("email");
        }
    }

    @Override
    public void removeMobile() {
        if (hasMobile()) {
            map.remove("mobile");
        }
    }

    @Override
    public boolean hasSecondaryEmailAddress() {
        return map.get("secondaryemail") != null || map.get("secondaryEmail") != null;
    }

    @Override
    public IContactValue getSecondaryEmailContact() {
        IContactValue v = getContactValue("secondaryemail");
        if (v.isNull()) {
            v = getContactValue("secondaryEmail");
        }
        return v;
    }

    @Override
    public boolean hasMobile() {
        return map.get("mobile") != null;
    }

    @Override
    public IContactValue getMobile() {
        return getContactValue("mobile");
    }

    @Override
    public boolean hasSecondaryMobileNumber() {
        return map.get("secondarymobile") != null || map.get("secondaryMobile") != null;
    }

    @Override
    public IContactValue getSecondaryMobile() {
        IContactValue v = getContactValue("secondarymobile");
        if (v.isNull()) {
            v = getContactValue("secondaryMobile");
        }
        return v;
    }

    @Override
    public IContactValue getContactValue(String type) {
        return new ContactValue(map, type);
    }

    @Override
    public void removeContactValue(String type) {
        if (!getContactValue(type).isNull()) {
            map.remove(type);
        }
    }

    @Override
    public boolean isIdVerified(){
        if (map.get("idVerified") != null) {
            return (boolean) map.get("idVerified");
        }
        return false;
    }

    @Override
    public void setIdVerified(boolean idVerified){
        map.put("idVerified", idVerified);
    }

    @Override
    public boolean hasIdVerified() {
        return map.get("idVerified") != null;
    }

    @Override
    public void setRegistered(boolean registered){
            map.put("registered",registered);
    }

    @Override
    public void setClientKey(String clientId){

        Map<String,String> clientIdMap = new HashMap<>();
        clientIdMap.put("clientId",clientId);
        map.put("key",clientIdMap);
    }



    @Override
    public IIdentityVerificationForm getIdentityVerificationForm() {
        return new IdentityVerificationForm(map);
    }

    @Override
    public String getClientKey() {
        return (String) ((Map<String, Object>) map.get("key")).get("clientId");
    }

    @Override
    public boolean isExistingPerson() {
        return map.containsKey("key") && (map.get("key")!=null);
    }

    @Override
    public boolean isGcmRetrievedPerson() {
        if(map.containsKey("registered")) {
            return !((boolean) map.get("registered"));
        }
        return false;
    }

    @Override
    public String getPanoramaNumber() {
        return (String) map.get(FormDataConstants.FIELD_GCM_ID);
    }

    @Override
    public boolean hasSourceOfWealth() {
        return getSourceOfWealth() != null;
    }

    @Override
    public String getSourceOfWealth() {
        return (String) map.get("wealthsource");
    }

    @Override
    public String getAdditionalSourceOfWealth() {
        return (String) map.get("additionalsourceofwealth");
    }

    @Override
    public String getCisId() { return (String) map.get("cisId");}

    @Override
    public List<Map<String, Object>> getAddresses(){
        return (List<Map<String, Object>>) map.get("addresses");
    }

    @Override
    public void setAddresses(List<?> addresses){
        map.put("addresses",addresses);
    }

    @Override
    public IAddressForm getGCMRetAddresses(){
        return new AddressForm(getAddresses().get(0));
    }

    @Override
    public List<Map<String,Object>> getEmails() {
        return (List<Map<String, Object>>) map.get("emails");
    }

    @Override
    public void setEmails(List emails){
        map.put("emails",emails);
    }

    @Override
    public List<Map<String,Object>> getPhones() {
        return (List<Map<String, Object>>) map.get("phones");
    }

    @Override
    public void setPhones(List phones){
        map.put("phones",phones);
    }

    @Override
    public String getFormerName(){
        return (String)map.get("formername");
    }

    @Override
    public IPlaceOfBirth getPlaceOfBirth(){
        return map.get("placeofbirth")!=null ? new PlaceOfBirth((Map<String, Object>) map.get("placeofbirth")) : null;
    }

    @Override
    public String getUserName(){ return  (String)map.get("userName");}

    @Override
    public String getPreferredContact() {
        if (map.containsKey("preferredcontact")) {
            return (String)map.get("preferredcontact");
        }
        return null;
    }

    @Override
    public void removePreferredContact() {
        if (map.containsKey("preferredcontact")) {
            map.remove("preferredcontact");
        }
    }

    @Override
    public void removeContactDetails() {
        if (map.containsKey("contactdetails")) {
            map.remove("contactdetails");
        }
    }

    @Override
    public boolean hasJsonSchema() {
        return false;
    }

    @Override
    public IAusTaxDetailsForm getAustralianTaxDetails() {
        return null;
    }

    @Override
    public List<IOverseasTaxDetailsForm> getOverseasTaxDetails() {
        return Collections.emptyList();
    }

    @Override
    public void setOverseasTaxDetails(List<OverseasTaxDetails> overseasTaxDetails) {
        LOGGER.warn("setOverseasTaxDetails method should not be invoked for Deprecated PersonDetailsForm");
    }

    @Override
    public String getOverseasTaxCountry() {
        return null;
    }

    @Override
    public void setOverseasTaxCountry(StringValue value) {
        LOGGER.warn("setOverseasTaxDetails method should not be invoked for Deprecated PersonDetailsForm");
    }

    @Override
    public boolean hasCrsTaxDetails() {
        return false;
    }

    @Override
    public boolean hasOverseasTaxCountry() {
        return false;
    }

}
