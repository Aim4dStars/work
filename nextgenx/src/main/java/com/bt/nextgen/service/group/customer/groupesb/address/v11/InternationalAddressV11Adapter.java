package com.bt.nextgen.service.group.customer.groupesb.address.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.NonStandardPostalAddress;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.AddressAdapterV10;
import org.apache.commons.lang.StringUtils;

/**
 * Created by F057654 on 14/08/2015.
 */
public class InternationalAddressV11Adapter extends AddressAdapterV11{

    private NonStandardPostalAddress nonStandardPostalAddress;
    private static final String AUSTRALIA_COUNTRY_CODE = "AU";

    public InternationalAddressV11Adapter(NonStandardPostalAddress nonStandardPostalAddress){
        this.nonStandardPostalAddress = nonStandardPostalAddress;
    }

    @Override
    public String getAddressLine1() {
        if(StringUtils.isNotBlank(nonStandardPostalAddress.getAddressLine1())){
            return nonStandardPostalAddress.getAddressLine1();
        }
        return null;
    }

    @Override
    public String getAddressLine2() {
        if(StringUtils.isNotBlank(nonStandardPostalAddress.getAddressLine2())){
            return nonStandardPostalAddress.getAddressLine2();
        }
        return null;
    }

    @Override
    public String getAddressLine3() {
        if(StringUtils.isNotBlank(nonStandardPostalAddress.getAddressLine3())){
            return nonStandardPostalAddress.getAddressLine3();
        }
        return null;
    }

    @Override
    public String getState()
    {
        if (StringUtils.isNotBlank(nonStandardPostalAddress.getCountry()) && nonStandardPostalAddress.getCountry().equalsIgnoreCase(AUSTRALIA_COUNTRY_CODE)) {
           return StringUtils.isNotBlank(nonStandardPostalAddress.getState()) ? nonStandardPostalAddress.getState() : null;
        }
       return null;
    }

    @Override
    public String getCity()
    {
        if (StringUtils.isNotBlank(nonStandardPostalAddress.getCity())) {
            return nonStandardPostalAddress.getCity();
        }
        return null;
    }

    @Override
    public String getPostCode()
    {
        if (StringUtils.isNotBlank(nonStandardPostalAddress.getPostCode())) {
            return nonStandardPostalAddress.getPostCode();
        }
        return null;
    }

    @Override
    public String getCountry()
    {
        if (StringUtils.isNotBlank(nonStandardPostalAddress.getCountry())) {
            return nonStandardPostalAddress.getCountry();
        }
        return null;
    }

}
