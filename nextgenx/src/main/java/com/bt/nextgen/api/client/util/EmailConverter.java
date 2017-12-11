package com.bt.nextgen.api.client.util;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.AddressKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.service.integration.domain.Email;

/**
 * Convert from Email integration domain object to the JSON-friendly DTO version.
 */
public class EmailConverter implements Converter<Email, EmailDto> {

    @Override
    public EmailDto convert(Email email) {
        final EmailDto emailDto = new EmailDto();
        emailDto.setPreferred(email.isPreferred());
        emailDto.setEmail(email.getEmail());
        if (email.getType() != null) {
            emailDto.setEmailType(email.getType().getAddressType());
        }
        if (email.getEmailKey() != null) {
            emailDto.setEmailKey(new AddressKey(email.getEmailKey().getId()));
        }
        return emailDto;
    }
}
