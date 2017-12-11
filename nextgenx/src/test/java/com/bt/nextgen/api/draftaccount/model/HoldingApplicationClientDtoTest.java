package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HoldingApplicationClientDtoTest {

    @Test
    public void constructorShouldConvertClientDtoToHoldingApplicationDto() throws Exception {
        HoldingApplicationClientDto holdingApplicationClientDto = new HoldingApplicationClientDto(createClientDto(), false, ApplicationClientStatus.APPROVED);
        assertThat(holdingApplicationClientDto.getFullName(), is("DEF, ABC"));
        assertThat(holdingApplicationClientDto.getEmail(), is("a@a.com"));
        assertThat(holdingApplicationClientDto.getPhoneNumber(), is("+33333"));
    }

    private ClientDto createClientDto() {
        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("ABC");
        clientDto.setLastName("DEF");

        EmailDto emailPrimary = new EmailDto();
        emailPrimary.setEmail("a@a.com");
        emailPrimary.setEmailType(AddressMedium.EMAIL_PRIMARY.getAddressType());

        EmailDto emailSecondary = new EmailDto();
        emailSecondary.setEmail("b@b.com");
        emailSecondary.setEmailType(AddressMedium.EMAIL_ADDRESS_SECONDARY.getAddressType());

        PhoneDto phoneNumberPrimary = new PhoneDto();
        phoneNumberPrimary.setNumber("+33333");
        phoneNumberPrimary.setPhoneType(AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType());

        PhoneDto phoneNumberSecondary = new PhoneDto();
        phoneNumberSecondary.setNumber("+8888888888");
        phoneNumberSecondary.setPhoneType(AddressMedium.MOBILE_PHONE_SECONDARY.getAddressType());

        clientDto.setEmails(asList(emailPrimary, emailSecondary));
        clientDto.setPhones(asList(phoneNumberPrimary, phoneNumberSecondary));
        return clientDto;
    }
}