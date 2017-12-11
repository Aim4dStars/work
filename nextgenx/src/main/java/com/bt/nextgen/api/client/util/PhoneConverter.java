package com.bt.nextgen.api.client.util;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.AddressKey;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.service.integration.domain.Phone;

/**
 * Convert phones from integration domain type to DTO version.
 */
public class PhoneConverter implements Converter<Phone, PhoneDto> {

    @Override
    public PhoneDto convert(Phone phone) {
        final PhoneDto dto = new PhoneDto();
        dto.setCountryCode(phone.getCountryCode());
        dto.setAreaCode(phone.getAreaCode());
        dto.setNumber(phone.getNumber());
        dto.setPreferred(phone.isPreferred());
        if (phone.getType() != null) {
            dto.setPhoneType(phone.getType().getAddressType());
        }
        if (phone.getPhoneKey() != null) {
            dto.setPhoneKey(new AddressKey(phone.getPhoneKey().getId()));
        }
        if (phone.getCategory() != null) {
            dto.setPhoneCategory(phone.getCategory().getValue());
        }
        return dto;
    }
}
