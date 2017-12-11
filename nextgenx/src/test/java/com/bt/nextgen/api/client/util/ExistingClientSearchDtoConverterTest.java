package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ExistingClientSearchDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ExistingClientSearchDtoConverterTest {
    private ExistingClientSearchDtoConverter dtoConverter;

    @Before
    public void setup() {
        dtoConverter = new ExistingClientSearchDtoConverter();
    }

    @Test
    public void shouldSetAllPropertiesInResultDto() {
        ExistingClientSearchDto resultDto = (ExistingClientSearchDto) dtoConverter.toDTO(buildLegalClient());
        assertThat(resultDto.getDisplayName(), is("COMPANY NAME"));
        assertThat(resultDto.getInvestorType(), is("Legal"));
        assertThat(EncodedString.toPlainText(resultDto.getKey().getClientId()), is("CLIENT_KEY"));
        assertThat(resultDto.getFullName(), is("COMPANY NAME"));
        assertThat(resultDto.getAddresses().size(), is(1));
        AddressDto address = resultDto.getAddresses().get(0);
        assertThat(address.getCountry(), is("Australia"));
        assertThat(address.getState(), is("New South Wales"));
        assertThat(address.getSuburb(), is("Sydney"));
        assertThat(address.getPostcode(), is("2000"));
        assertThat(resultDto.isIdVerified(), is(true));
    }

    @Test
    public void shouldSetIdVerifiedToFalseIfIdVerificationIsNotComplete() {
        LegalClient legalClient = buildLegalClient();
        legalClient.setIdentityVerificationStatus(IdentityVerificationStatus.Pending);
        ExistingClientSearchDto resultDto = (ExistingClientSearchDto) dtoConverter.toDTO(legalClient);
        assertThat(resultDto.isIdVerified(), is(false));
    }

    public LegalClient buildLegalClient() {
        LegalClient legalClient = new LegalClient();
        legalClient.setLegalForm(InvestorType.COMPANY);
        legalClient.setClientKey(ClientKey.valueOf("CLIENT_KEY"));
        legalClient.setFullName("COMPANY NAME");
        AddressImpl address = new AddressImpl();
        address.setCountry("Australia");
        address.setState("New South Wales");
        address.setSuburb("Sydney");
        address.setPostCode("2000");
        legalClient.setAddresses(Arrays.<Address>asList(address));
        legalClient.setIdentityVerificationStatus(IdentityVerificationStatus.Completed);
        return legalClient;
    }
}
