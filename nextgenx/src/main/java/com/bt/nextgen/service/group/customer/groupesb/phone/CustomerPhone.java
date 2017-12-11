package com.bt.nextgen.service.group.customer.groupesb.phone;

import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Phone;
import org.apache.commons.lang.StringUtils;

/**
 * Created by F057654 on 1/09/2015.
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck","squid:S1142", "squid:MethodCyclomaticComplexity"})
public class CustomerPhone implements Phone{

    private AddressKey phoneKey;

    private AddressMedium type;

    private String number;

    private String countryCode;

    private String areaCode;

    private String modificationSeq;

    private String preferredIntId;

    private boolean preferred;

    private AddressType category;

    private PhoneAction phoneAction;

    private boolean gcm;

    public CustomerPhone()
    {}

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
        return type;
    }

    public void setType(AddressMedium type)
    {
        this.type = type;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getAreaCode()
    {
        return areaCode;
    }

    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public String getModificationSeq()
    {
        return modificationSeq;
    }

    public void setModificationSeq(String modificationSeq)
    {
        this.modificationSeq = modificationSeq;
    }

    public boolean isPreferred()
    {
        if (StringUtils.isNotEmpty(preferredIntId) && preferredIntId.equals(Constants.PREFERRED_CONTACT)){
            return true;
        }else{
            return preferred;
        }

    }

    public void setPreferred(boolean preferred)
    {
        this.preferred = preferred;
    }

    public AddressType getCategory()
    {
        return category;
    }

    public void setCategory(AddressType category)
    {
        this.category = category;
    }

    public String getPreferredIntId() {
        return preferredIntId;
    }

    public void setPreferredIntId(String preferredIntId) {
        this.preferredIntId = preferredIntId;
    }
    public PhoneAction getAction() {
        return phoneAction;
    }

    public void setAction(PhoneAction phoneAction) {
        this.phoneAction = phoneAction;
    }

    public boolean isGcm() {
        return gcm;
    }

    public void setGcm(boolean gcm) {
        this.gcm = gcm;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomerPhone other = (CustomerPhone) obj;
        if (!number.equalsIgnoreCase(other.number))
            return false;
        if (StringUtils.isEmpty(countryCode)) {
            if (!StringUtils.isEmpty(other.countryCode))
                return false;
        } else if (!countryCode.equals(other.countryCode))
            return false;
        if (type != other.type)
            return false;
        if (StringUtils.isEmpty(areaCode)) {
            if (!StringUtils.isEmpty(other.areaCode))
                return false;
        } else if (!areaCode.equals(other.areaCode))
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((areaCode == null) ? 0 : areaCode.hashCode());
        result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
}
