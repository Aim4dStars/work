package com.bt.nextgen.service.group.customer.groupesb.phone.v7;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PhoneAddressContactMethod;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAdapter;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Phone;

import static com.bt.nextgen.service.group.customer.groupesb.phone.v7.CustomerPhoneV7Converter.convertResponseInAddressMedium;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Adapter class to convert a {@code PhoneAddressContactMethod} instance (from GESB service SVC0258) into the
 * {@code Phone} interface.
 */
public class PhoneAdapterV7 extends PhoneAdapter implements Phone {

    private AddressKey phoneKey;

    private boolean preferred;

    private AddressType category;

    private PhoneAddressContactMethod phone;

    public PhoneAdapterV7(PhoneAddressContactMethod phoneAddressContactMethod){
        this.phone = phoneAddressContactMethod;
    }

    @Override
    public AddressKey getPhoneKey()
    {
        return phoneKey;
    }

    public void setPhoneKey(AddressKey phoneKey)
    {
        this.phoneKey = phoneKey;
    }

    @Override
    public AddressMedium getType()
    {
        return convertResponseInAddressMedium(phone);
    }

    @Override
    public String getNumber()
    {
        final String localNumber = phone.getHasAddress().getLocalNumber();
        return isNotBlank(localNumber) ? localNumber : null;
    }

    @Override
    public String getCountryCode()
    {
        String countryCode = phone.getHasAddress().getCountryCode();
        if (isNotBlank(countryCode)) {
            if (countryCode.startsWith("+")) {
                countryCode = countryCode.substring(1);
            }
            return countryCode;
        }
        return null;
    }

    @Override
    public String getModificationSeq() {
        final String contactMethodId = phone.getContactMethodIdentifier().getContactMethodId();
        return isNotBlank(contactMethodId) ? contactMethodId : null;
    }

    @Override
    public String getAreaCode()
    {
        String areaCode = phone.getHasAddress().getAreaCode();
        if (isNotBlank(areaCode)) {
            if (areaCode.startsWith("0")) {
                areaCode = areaCode.substring(1);
            }
            return areaCode;
        }
        return null;
    }

    public String getPhoneCategory() {
        return phone.getContactMedium();
    }

    @Override
    public boolean isPreferred()
    {
        return preferred;
    }

    public void setPreferred(boolean preferred)
    {
        this.preferred = preferred;
    }

    @Override
    public AddressType getCategory()
    {
        return category;
    }

    public void setCategory(AddressType category)
    {
        this.category = category;
    }
}
