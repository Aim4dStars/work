package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.builder.AddressStreetTypeMapper;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.integration.domain.Address;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressDetailType;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressTypeDetailType;
import ns.btfin_com.sharedservices.common.address.v3_0.NonStandardAddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.StandardAddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.StructuredAddressDetailType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.btfin.panorama.core.util.StringUtil.nullIfBlank;
import static org.apache.commons.beanutils.BeanUtils.getProperty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.upperCase;

@Service
public class AddressTypeBuilder {

    private final AddressStreetTypeMapper streetTypeMapper = new AddressStreetTypeMapper();

    @Autowired
    AddressV2CacheService addressV2CacheService;


    public <T extends AddressType> T getAddressType(IAddressForm address, T addressType, ServiceErrors serviceErrors) {
        if(StringUtils.isNotEmpty(address.getAddressIdentifier())) {
            PostalAddress postalAddress = addressV2CacheService.getAddress(address.getAddressIdentifier(), serviceErrors);
            return getAddressType(postalAddress, addressType);
        }
        return getAddressType(address, addressType, false, serviceErrors);
    }

    public <T extends AddressType> T getAddressType(IAddressForm address, T addressType, boolean gcmAddress, ServiceErrors serviceErrors) {
        if(StringUtils.isNotEmpty(address.getAddressIdentifier())) {
            PostalAddress postalAddress = addressV2CacheService.getAddress(address.getAddressIdentifier(), serviceErrors);
            return getAddressType(postalAddress, addressType);
        }
        addressType.setAddressDetail(getAddressDetailType(address,gcmAddress));
        return addressType;
    }

    public <T extends AddressType> T getAddressType(Address address, T addressType) {
        addressType.setAddressDetail(getAddressDetailType(address));
        return addressType;
    }

    public <T extends AddressType> T getAddressTypeWithOccupier(IOrganisationForm form, T addressType, ServiceErrors serviceErrors) {
        T defaultAddressType = getAddressType(form.getRegisteredAddress(), addressType, serviceErrors);
        if(form instanceof ICompanyForm && isNotBlank(((ICompanyForm) form).getOccupierName())) {
            defaultAddressType.setOccupierName(((ICompanyForm)form).getOccupierName());
        }
        return defaultAddressType;
    }

    public <T extends AddressType> T getDefaultAddressType(IAddressForm address, T addressType, ServiceErrors serviceErrors) {
        return getDefaultAddressType(address, addressType, false, serviceErrors);
    }

    public <T extends AddressType> T getDefaultAddressType(IAddressForm address, T addressType, boolean gcmAddress, ServiceErrors serviceErrors) {
        T defaultAddressType = getAddressType(address, addressType, gcmAddress, serviceErrors);
        defaultAddressType.setDefaultAddress(true);
        return defaultAddressType;
    }


    private AddressDetailType getAddressDetailType(IAddressForm address, boolean gcmAddress) {
        AddressDetailType addressDetailType = new AddressDetailType();
        StructuredAddressDetailType structuredAddressDetailType = gcmAddress ? getGCMStructuredAddressDetailType(address) : getStructuredAddressDetailType(address);
        addressDetailType.setStructuredAddressDetail(structuredAddressDetailType);
        return addressDetailType;
    }

    private AddressDetailType getAddressDetailType(Address address) {
        AddressDetailType addressDetailType = new AddressDetailType();
        StructuredAddressDetailType structuredAddressDetailType = getStructuredAddressDetailType(address);
        addressDetailType.setStructuredAddressDetail(structuredAddressDetailType);
        return addressDetailType;
    }

    private StructuredAddressDetailType getGCMStructuredAddressDetailType(IAddressForm address){

        StructuredAddressDetailType structuredAddressDetailType = new StructuredAddressDetailType();
        structuredAddressDetailType.setState(address.getState());
        structuredAddressDetailType.setCountryCode(address.getCountryCode());
        structuredAddressDetailType.setCity(address.getCity());
        structuredAddressDetailType.setPostcode(address.getPostcode());

        AddressTypeDetailType addressTypeDetailType = new AddressTypeDetailType();
        if (address.isStandardAddress()) {
            addressTypeDetailType.setStandardAddress(getStandardAddressType(address));
        }
        else{
            addressTypeDetailType.setNonStandardAddress(getNonStandardAddressType(address));
        }
        structuredAddressDetailType.setAddressTypeDetail(addressTypeDetailType);
        return structuredAddressDetailType;
    }

    private StructuredAddressDetailType getStructuredAddressDetailType(IAddressForm address) {

        StructuredAddressDetailType structuredAddressDetailType = new StructuredAddressDetailType();
        structuredAddressDetailType.setState(address.getState());
        structuredAddressDetailType.setCountryCode(address.getCountry());

        AddressTypeDetailType addressTypeDetailType = new AddressTypeDetailType();
        if (address.isComponentised()) {
            structuredAddressDetailType.setCity(address.getSuburb());
            structuredAddressDetailType.setPostcode(address.getPostcode());
            addressTypeDetailType.setStandardAddress(getStandardAddressType(address));
        } else {
            structuredAddressDetailType.setCity(address.getCity());
            structuredAddressDetailType.setPostcode(address.getPin());
            addressTypeDetailType.setNonStandardAddress(getNonStandardAddressType(address));
        }
        structuredAddressDetailType.setAddressTypeDetail(addressTypeDetailType);
        return structuredAddressDetailType;
    }

    private StructuredAddressDetailType getStructuredAddressDetailType(Address address) {

        StructuredAddressDetailType structuredAddressDetailType = new StructuredAddressDetailType();
        structuredAddressDetailType.setState(address.getStateAbbr());
        structuredAddressDetailType.setCountryCode(address.getCountryAbbr());
        structuredAddressDetailType.setCity(address.getSuburb());
        if(isNotBlank(address.getCity())){
            structuredAddressDetailType.setCity(address.getCity());
        }
        structuredAddressDetailType.setPostcode(address.getPostCode());
        AddressTypeDetailType addressTypeDetailType = new AddressTypeDetailType();
        if(address.getAddressLine1() != null) {
            addressTypeDetailType.setNonStandardAddress(getNonStandardAddressType(address));
        } else {
            addressTypeDetailType.setStandardAddress(getStandardAddressType(address));
        }
        structuredAddressDetailType.setAddressTypeDetail(addressTypeDetailType);
        return structuredAddressDetailType;
    }

    private NonStandardAddressType getNonStandardAddressType(IAddressForm address) {
        NonStandardAddressType nonStandardAddressType = new NonStandardAddressType();
        final List<String> adrLine = nonStandardAddressType.getAddressLine();
        if (isNotBlank(address.getAddressLine1())) {
            adrLine.add(address.getAddressLine1());
        }
        if (isNotBlank(address.getAddressLine2())) {
            adrLine.add(address.getAddressLine2());
        }
        return nonStandardAddressType;
    }

    private NonStandardAddressType getNonStandardAddressType(Address address) {
        NonStandardAddressType nonStandardAddressType = new NonStandardAddressType();
        final List<String> adrLine = nonStandardAddressType.getAddressLine();
        if (isNotBlank(address.getAddressLine1())) {
            adrLine.add(address.getAddressLine1());
        }
        if (isNotBlank(address.getAddressLine2())) {
            adrLine.add(address.getAddressLine2());
        }
        return nonStandardAddressType;
    }

    /**
     * Generic method to build a StandardAddressType from an 'IAddressForm' or 'Address' object.
     * Except for a single method (unit number) all the method names are identical between the model beans.
     *
     * @param address an 'IAddressForm' or 'Address' bean
     * @return StandardAddressType
     */
    private StandardAddressType getStandardAddressType(Object address) {
        final StandardAddressType standardAddressType = new StandardAddressType();
        if (address != null) {
                String unitNumber = null;
                if (address instanceof IAddressForm) {
                unitNumber = ((IAddressForm) address).getUnitNumber();
                } else if (address instanceof Address) {
                unitNumber = ((Address) address).getUnit();
                }
                if(isNotBlank(unitNumber)){
                    standardAddressType.setUnitNumber(unitNumber);
                }

                updateStandardAddressType(standardAddressType, address);
            }
        return standardAddressType;
    }

  
    /**
     * This method is here only because of STUPID Sonar Cyclomatic complexity rule:
     * "The Cyclomatic Complexity of this method "getStandardAddressType" is 14 which is greater than 10 authorized"
     *
     * @param standardAddressType
     * @param address
     */
    private void updateStandardAddressType(StandardAddressType standardAddressType, Object address) {
        try {
            final String streetNumber = getProperty(address, "streetNumber");
            final String floorNumber = getProperty(address, "floor");
            final String streetName = getProperty(address, "streetName");
            final String streetType = getProperty(address, "streetType");
            final String building = getProperty(address,"building");

            standardAddressType.setStreetNumber(nullIfBlank(streetNumber));
            standardAddressType.setFloorNumber(nullIfBlank(floorNumber));
            standardAddressType.setStreetName(nullIfBlank(streetName));
            if (isNotBlank(streetType)) {
                String strType = streetTypeMapper.getStandardStreetType(streetType);
                if (isNotBlank(strType)) {
                    standardAddressType.setStreetType(strType);
                } else {
                    //Only GCM Retrieved address should come in this flow. GCM Retrieved Street type is already in canonical code.
                    standardAddressType.setStreetType(upperCase(streetType));
                }
            }
            standardAddressType.setPropertyName(nullIfBlank(building));
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
