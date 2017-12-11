package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressDtoConverter {

    @Autowired
    private StaticIntegrationService staticService;

    /**
     * Used by unit tests only
     * @param staticService
     */
    void setStaticService(StaticIntegrationService staticService) {
        this.staticService = staticService;
    }


    public AddressDto getAddressDto(IAddressForm addressForm, boolean isDomicile, boolean isMailingAddress, ServiceErrors serviceErrors) {
        AddressDto addressDto = new AddressDto();

        if(!addressForm.isComponentised() && !addressForm.isStandardAddress()) {
            addressDto.setStreetName(addressForm.getAddressLine1());
            addressDto.setBuilding(addressForm.getAddressLine2());
            addressDto.setPostcode(null!=addressForm.getPin() ? addressForm.getPin(): addressForm.getPostcode());
        }
        else {
            addressDto.setFloor(addressForm.getFloor());
            addressDto.setBuilding(addressForm.getBuilding());
            addressDto.setUnitNumber(addressForm.getUnitNumber());
            addressDto.setStreetNumber(addressForm.getStreetNumber());
            addressDto.setStreetName(addressForm.getStreetName());
            addressDto.setStreetType(addressForm.getStreetType());
            addressDto.setPostcode(addressForm.getPostcode());
        }

        addressDto.setSuburb(addressForm.getSuburb());
        addressDto.setState(addressForm.getState());
        addressDto.setCity(addressForm.getCity());
        addressDto.setDomicile(isDomicile);
        addressDto.setMailingAddress(isMailingAddress);
        addressDto.setCountry(setCountryFromAddressForm(addressForm, serviceErrors));
        return addressDto;
    }

    public AddressDto getAddressDto(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setFloor(address.getFloor());
        addressDto.setUnitNumber(address.getUnit());
        addressDto.setStreetNumber(address.getStreetNumber());
        addressDto.setStreetName(address.getStreetName());
        addressDto.setBuilding(address.getBuilding());
        addressDto.setStreetType(address.getStreetType());
        addressDto.setPostcode(address.getPostCode());
        addressDto.setSuburb(address.getSuburb());
        addressDto.setState(address.getState());
        addressDto.setCity(address.getCity());
        addressDto.setDomicile(address.isDomicile());
        addressDto.setMailingAddress(address.isMailingAddress());
        addressDto.setCountry(address.getCountry());
        //QC18111 - store PO Box details when coming from Avalaoq
        addressDto.setPoBoxPrefix(address.getPoBoxPrefix());
        addressDto.setPoBox(address.getPoBox());
        return addressDto;
    }

    private String setCountryFromAddressForm(IAddressForm addressForm, ServiceErrors serviceErrors) {
        if (addressForm.getCountryCode() != null){
            return getCodeName(addressForm.getCountryCode(),serviceErrors);
        }

        if (addressForm.getCountry() != null) {
            return getCodeName(addressForm.getCountry(), serviceErrors);
        }

        return null;
    }

    private String getCodeName(String countryCode, ServiceErrors serviceErrors) {
        Code code = staticService.loadCodeByUserId(CodeCategory.COUNTRY, countryCode, serviceErrors);
        if (code != null && code.getName() != null){
            return code.getName();
        }
        return countryCode;
    }
}
