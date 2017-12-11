package com.bt.nextgen.service.group.customer.groupesb.address.v10;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import org.apache.commons.lang.StringUtils;

/**
 * Created by F057654 on 6/08/2015.
 */
public class AddressAdapterV10 extends AddressAdapter implements Address {

    private AddressKey addressKey;
    private String careOf;
    private String streetTypeId;
    private String streetTypeUserId;
    private String suburb;
    private String poBox;
    private String stateAbbr;
    private String stateCode;
    private String countryAbbr;
    private String countryCode;
    private String modificationSeq;
    private String poBoxPrefix;
    private boolean domicile;
    private boolean mailingAddress;
    private int categoryId;
    private String preferredIntId;
    private boolean preferred;
    private String electronicAddress;
    private String stateOther;
    private boolean internationalAddress;
    private StandardPostalAddress standardPostalAddress;
    private String occupierName;

    public AddressAdapterV10(){}

    public AddressAdapterV10(StandardPostalAddress standardPostalAddress){
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

    @Override
    public String getModificationSeq()
    {

        return modificationSeq;
    }

    public String getPoBoxPrefix()
    {
        return poBoxPrefix;
    }

    public void setPoBoxPrefix(String poBoxPrefix)
    {
        this.poBoxPrefix = poBoxPrefix;
    }

    public void setStateCode(String stateCode)
    {
        this.stateCode = stateCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public void setModificationSeq(String modificationSeq)
    {
        this.modificationSeq = modificationSeq;
    }

    @Override
    public boolean isMailingAddress()
    {
        return mailingAddress;
    }

    public void setMailingAddress(boolean mailingAddress)
    {
        this.mailingAddress = mailingAddress;
    }

    public int getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(int categoryId)
    {
        this.categoryId = categoryId;
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

    public String getElectronicAddress()
    {
        return electronicAddress;
    }

    @Override
    public AddressMedium getAddressType() {
        return null;
    }

    @Override
    public AddressType getPostAddress() {
        return null;
    }

    public void setElectronicAddress(String electronicAddress)
    {
        this.electronicAddress = electronicAddress;
    }

    public void setStateOther(String stateOther)
    {
        this.stateOther = stateOther;
    }

    @Override
    public String getStateOther()
    {
        return stateOther;
    }

    @Override
    public String getAddressLine1() {
        return null;
    }

    @Override
    public String getAddressLine2() {
        return null;
    }

    @Override
    public String getAddressLine3() {
        return null;
    }

    @Override
    public boolean isInternationalAddress() {
        return internationalAddress;
    }

    public void setInternationalAddress(boolean internationalAddress){
        this.internationalAddress = internationalAddress;
    }

    @Override
    public String getStreetTypeId()	{
        return streetTypeId;
    }

    public void setStreetTypeId(String streetTypeId)
    {
        this.streetTypeId = streetTypeId;
    }

    @Override
    public String getStreetTypeUserId() {
        return streetTypeUserId;
    }

    public void setStreetTypeUserId(String streetTypeUserId) {
        this.streetTypeUserId = streetTypeUserId;
    }

    @Override
    public String getStateAbbr()
    {
        return stateAbbr;
    }

    public void setStateAbbr(String stateAbbr)
    {
        this.stateAbbr = stateAbbr;
    }

    @Override
    public String getCountryAbbr()
    {
        return countryAbbr;
    }

    public void setCountryAbbr(String countryAbbr)
    {
        this.countryAbbr = countryAbbr;
    }

    public String getPreferredIntId() {
        return preferredIntId;
    }

    public void setPreferredIntId(String preferredIntId) {
        this.preferredIntId = preferredIntId;
    }


    @Override
    public AddressKey getAddressKey()
    {
        return addressKey;
    }

    public void setAddressKey(AddressKey addressKey)
    {
        this.addressKey = addressKey;
    }

    @Override
    public String getCareOf()
    {
        return careOf;
    }

    public void setCareOf(String careOf)
    {
        this.careOf = careOf;
    }

    @Override
    public String getSuburb()
    {
        return suburb;
    }

    public void setSuburb(String suburb)
    {
        this.suburb = suburb;
    }

    @Override
    public String getPoBox()
    {
        return poBox;
    }

    public void setPoBox(String poBox)
    {
        this.poBox = poBox;
    }

    @Override
    public boolean isDomicile() {
        return domicile;
    }

    @Override
    public String getStateCode()
    {
        return stateCode;
    }

    @Override
    public String getCountryCode()
    {
        return countryCode;
    }

    public void setDomicile(boolean domicile) {
        this.domicile = domicile;
    }

    @Override
    public String getOccupierName() {
        return occupierName;
    }

    public void setOccupierName(String occupierName) {
        this.occupierName = occupierName;
    }
}

