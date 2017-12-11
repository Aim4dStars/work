package com.bt.nextgen.service.group.customer.groupesb.address.v7;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.StandardPostalAddress;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.integration.domain.Address;
import org.apache.commons.lang.StringUtils;

/**
 * Created by F057654 on 6/08/2015.
 */
@Deprecated
public class AddressAdapterV7 extends AddressAdapter implements Address {

    private StandardPostalAddress standardPostalAddress;

    public AddressAdapterV7() {}

    public AddressAdapterV7(StandardPostalAddress standardPostalAddress){
        this.standardPostalAddress = standardPostalAddress;
    }

    @Override
    public String getUnit()
    {
        if(null!=standardPostalAddress && StringUtils.isNotBlank(standardPostalAddress.getUnitNumber())){
            return standardPostalAddress.getUnitNumber();
        }
        return null;
    }

    @Override
    public String getFloor()
    {
        if(null!=standardPostalAddress && StringUtils.isNotBlank(standardPostalAddress.getFloorNumber())){
            return standardPostalAddress.getFloorNumber();
        }
        return null;
    }

    @Override
    public String getStreetName()
    {
        if(null!=standardPostalAddress && StringUtils.isNotBlank(standardPostalAddress.getStreetName())){
            return standardPostalAddress.getStreetName();
        }
        return null;
    }

    @Override
    public String getStreetNumber()
    {
        if(null!=standardPostalAddress && StringUtils.isNotBlank(standardPostalAddress.getStreetNumber())){
            return standardPostalAddress.getStreetNumber();
        }
        return null;
    }

    @Override
    public String getStreetType()
    {
        if(null!=standardPostalAddress && StringUtils.isNotBlank(standardPostalAddress.getStreetType())){
            return standardPostalAddress.getStreetType();
        }
        return null;
    }

    @Override
    public String getBuilding()
    {
        if(null!=standardPostalAddress && StringUtils.isNotBlank(standardPostalAddress.getBuildingName())){
            return standardPostalAddress.getBuildingName();
        }
        return null;
    }

    @Override
    public String getState()
    {
        if(StringUtils.isNotBlank(standardPostalAddress.getState())){
            return standardPostalAddress.getState();
        }
        return null;
    }

    @Override
    public String getCity()
    {
        if (StringUtils.isNotBlank(standardPostalAddress.getCity())) {
            return standardPostalAddress.getCity();
        }
        return null;
    }

    @Override
    public String getPostCode()
    {
        if (StringUtils.isNotBlank(standardPostalAddress.getPostCode())) {
            return standardPostalAddress.getPostCode();
        }
        return null;
    }

    @Override
    public String getCountry()
    {
        if (StringUtils.isNotBlank(standardPostalAddress.getCountry())) {
            return standardPostalAddress.getCountry();
        }
        return null;
    }
}

