package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.util.EmailConverter;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailDtoConverter {

    private final EmailConverter converter = new EmailConverter();

    public EmailDto getEmailDto(Email email) {
        return converter.convert(email);
    }

    public EmailDto getEmailDto(IContactValue email, AddressMedium addressMedium) {
        EmailDto emailDto = new EmailDto();
        emailDto.setPreferred(email.isPreferredContact());
        emailDto.setEmail(email.getValue());
        emailDto.setEmailType(addressMedium.getAddressType());
        return emailDto;
    }
}
