package com.bt.nextgen.api.client.util;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressKey;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.integration.domain.Address;

/**
 * Convert a domain address object to the relevant Address DTO.
 */
public class AddressConverter implements Converter<Address, AddressDto> {

    @Override
    public AddressDto convert(Address address) {
        final AddressDto addressDto = new AddressDto();
        addressDto.setAddressLine1(address.getAddressLine1());
        addressDto.setAddressLine2(address.getAddressLine2());
        addressDto.setAddressLine3(address.getAddressLine3());
        addressDto.setPoBox(address.getPoBox());
        addressDto.setPoBoxPrefix(address.getPoBoxPrefix());
        addressDto.setBuilding(address.getBuilding());
        addressDto.setFloor(address.getFloor());
        addressDto.setStreetNumber(address.getStreetNumber());
        addressDto.setStreetName(address.getStreetName());
        addressDto.setStreetType(address.getStreetType());
        addressDto.setStreetTypeUserId(address.getStreetTypeUserId());
        addressDto.setSuburb(address.getSuburb());
        addressDto.setState(address.getState());
        addressDto.setStateAbbr(address.getStateAbbr());
        addressDto.setStateCode(address.getStateCode());
        addressDto.setPostcode(address.getPostCode());
        addressDto.setCity(address.getCity());
        addressDto.setCountry(address.getCountry());
        addressDto.setCountryAbbr(address.getCountryAbbr());
        addressDto.setCountryCode(address.getCountryCode());
        addressDto.setUnitNumber(address.getUnit());
        //TODO: to avoid instanceof check, need to update the interface and other implementation in other dependencies
        if( address instanceof AddressAdapter) {
            addressDto.setStandardAddressFormat(((AddressAdapter)address).isStandardAddressFormat());
        }
        if (address.getAddressKey() != null) {
            addressDto.setAddressKey(new AddressKey(address.getAddressKey().getId()));
        }
        if (address.getPostAddress() != null) {
            addressDto.setAddressType(address.getPostAddress().getValue());
        }
        addressDto.setMailingAddress(address.isMailingAddress());
        addressDto.setInternationalAddress(address.isInternationalAddress());
        addressDto.setDomicile(address.isDomicile());
        return addressDto;
    }
}
