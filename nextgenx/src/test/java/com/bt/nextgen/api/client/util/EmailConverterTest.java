package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import org.junit.Before;
import org.junit.Test;

import static com.bt.nextgen.service.integration.domain.AddressMedium.EMAIL_PRIMARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Tests for the {@code EmailConverter} class.
 */
public class EmailConverterTest {

    private EmailConverter converter;

    @Before
    public void initConverter() {
        converter = new EmailConverter();
    }

    @Test
    public void convertWithBareBonesEmail() {
        EmailDto dto = converter.convert(email("test@gmail.com"));
        assertEquals("test@gmail.com", dto.getEmail());
        assertFalse(dto.isPreferred());
        assertNull(dto.getEmailKey());
        assertNull(dto.getEmailType());
    }

    @Test
    public void convertWithFullyFledgedEmail() {
        EmailDto dto = converter.convert(email("test@gmail.com", true, "key", EMAIL_PRIMARY));
        assertEquals("test@gmail.com", dto.getEmail());
        assertTrue(dto.isPreferred());
        assertEquals("key", dto.getEmailKey().getAddressId());
        assertEquals("Primary", dto.getEmailType());
    }

    private static Email email(String address, boolean preferred, String key, AddressMedium type) {
        EmailImpl email = new EmailImpl();
        email.setEmail(address);
        email.setPreferred(preferred);
        email.setType(type);
        if (key != null) {
            email.setEmailKey(AddressKey.valueOf(key));
        }
        return email;
    }

    private static Email email(String address, boolean preferred, String key) {
        return email(address, preferred, key, null);
    }

    private static Email email(String address, boolean preferred) {
        return email(address, preferred, null);
    }

    private static Email email(String address) {
        return email(address, false);
    }
}