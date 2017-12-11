package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.btfin.panorama.onboarding.helper.EmailHelper;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bt.nextgen.api.draftaccount.builder.v3.ContactsTypeBuilder.standardContactNumber;
import static com.btfin.panorama.onboarding.helper.ContactHelper.contact;
import static com.btfin.panorama.onboarding.helper.ContactHelper.contacts;
import static com.btfin.panorama.onboarding.helper.EmailHelper.emailAddresses;
import static com.btfin.panorama.onboarding.helper.EmailHelper.primaryEmailAddress;
import static com.btfin.panorama.onboarding.helper.EmailHelper.secondaryEmailAddress;

@Service
public class ContactDetailsBuilder {

    public void populateContactDetailsField(InvolvedPartyDetailsType investorDetails, IPersonDetailsForm form) {
        investorDetails.setEmailAddresses(getEmailAddressesType(form));
        investorDetails.setContacts(getContactsType(form));
    }

    private EmailAddressesType getEmailAddressesType(IPersonDetailsForm individualinvestordetailsMap) {
        IContactValue contact = individualinvestordetailsMap.getEmail();
        final EmailAddressType primary = primaryEmailAddress(contact.getValue(), contact.isPreferredContact());
        if (individualinvestordetailsMap.hasSecondaryEmailAddress()) {
            contact = individualinvestordetailsMap.getSecondaryEmailContact();
            final EmailAddressType secondary = secondaryEmailAddress(contact.getValue(), contact.isPreferredContact());
            return emailAddresses(primary, secondary);
        }
        return emailAddresses(primary);
    }

    private static EmailAddressType emailAddress(String email, EmailUsageTypeCode... usages) {
        EmailAddressType emailAddress = EmailHelper.emailAddress(email);
        ((EmailAddressDetailType)emailAddress.getEmailAddressDetail().getValue()).getEmailUsage().addAll(Arrays.asList(usages));
        return emailAddress;
    }

    private static EmailAddressType primaryEmailAddress(String email, boolean preferred) {
        return preferred?emailAddress(email, new EmailUsageTypeCode[]{EmailUsageTypeCode.ALL_HOURS, EmailUsageTypeCode.PREFERRED}):emailAddress(email, new EmailUsageTypeCode[]{EmailUsageTypeCode.ALL_HOURS});
    }

    private static EmailAddressType secondaryEmailAddress(String email, boolean preferred) {
        return preferred?emailAddress(email, new EmailUsageTypeCode[]{EmailUsageTypeCode.AFTER_HOURS, EmailUsageTypeCode.PREFERRED}):emailAddress(email, new EmailUsageTypeCode[]{EmailUsageTypeCode.AFTER_HOURS});
    }

    private ContactsType getContactsType(IPersonDetailsForm form) {
        List<ContactType> contactsList = new ArrayList<>();
        contactsList.add(getContactType(form.getMobile(), ContactNumberUsageTypeCode.ALL_HOURS));
        if (form.hasSecondaryMobileNumber()) {
            contactsList.add(getContactType(form.getSecondaryMobile(), ContactNumberUsageTypeCode.AFTER_HOURS));
        }
        if(form.hasOtherNumber()){
            contactsList.add(getContactType(form.getOtherNumber(), ContactNumberUsageTypeCode.OTHER));
        }
        return contacts(contactsList.toArray(new ContactType[contactsList.size()]));
    }

    private ContactType getContactType(IContactValue contact, ContactNumberUsageTypeCode contactUsageType) {
        final Set<ContactNumberUsageTypeCode> usages = EnumSet.of(contactUsageType);
        if (contact.isPreferredContact()) {
            usages.add(ContactNumberUsageTypeCode.PREFERRED);
        }
        if(contactUsageType.equals(ContactNumberUsageTypeCode.OTHER)){
            return contact(standardContactNumber(contact), ContactNumberTypeCode.PHONE, usages);
        }else{
            return contact(standardContactNumber(contact.getValue()), ContactNumberTypeCode.MOBILE, usages);
        }

    }

    private static ContactType contact(ContactNumberType number, ContactNumberTypeCode numberType, Collection<ContactNumberUsageTypeCode> usages) {
        ContactType contact = new ContactType();
        ContactDetailType detail = new ContactDetailType();
        detail.setContactNumber(number);
        detail.setContactNumberType(numberType);
        if(usages != null && !usages.isEmpty()) {
            detail.getContactNumberUsage().addAll(usages);
        }
        contact.setContactDetail(detail);
        return contact;
    }

}
