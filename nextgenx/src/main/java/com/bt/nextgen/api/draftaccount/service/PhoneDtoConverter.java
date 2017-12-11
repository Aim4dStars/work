package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.util.PhoneConverter;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PhoneDtoConverter {

    private final PhoneConverter converter = new PhoneConverter();

    public PhoneDto getPhoneDto(IContactValue phone, AddressMedium addressMedium) {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPreferred(phone.isPreferredContact());
        phoneDto.setNumber(phone.getValue());
        phoneDto.setPhoneType(addressMedium.getAddressType());
        return phoneDto;
    }

    public PhoneDto getPhoneDto(Phone phone) {
        return converter.convert(phone);
    }
}
