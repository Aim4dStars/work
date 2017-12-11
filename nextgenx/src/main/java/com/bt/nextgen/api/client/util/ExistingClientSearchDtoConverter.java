package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ExistingClientSearchDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;

import java.util.Arrays;

public class ExistingClientSearchDtoConverter implements DTOConverter {

    @Override
    public Object toDTO(Object object) {
        LegalClient client = (LegalClient) object;
        ExistingClientSearchDto clientDto = new ExistingClientSearchDto();

        if (client.getLegalForm() != null) {
            clientDto.setDisplayName(client.getFullName());
        }
        clientDto.setInvestorType("Legal");

        setCommonClientAttributes(clientDto, client);
        return clientDto;
    }

    protected void setCommonClientAttributes(ExistingClientSearchDto clientDto, Client client) {
        ClientKey key = new ClientKey(EncodedString.fromPlainText(client.getClientKey().getId()).toString());
        clientDto.setKey(key);
        clientDto.setFullName(client.getFullName());

        Address address = client.getAddresses().get(0);
        clientDto.setAddresses(Arrays.asList(addressDto(address)));

        clientDto.setIdVerified(isVerified(client.getIdentityVerificationStatus()));
    }

    private boolean isVerified(IdentityVerificationStatus identityVerificationStatus) {
        return identityVerificationStatus == IdentityVerificationStatus.Completed;
    }
    private AddressDto addressDto(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setSuburb(address.getSuburb());
        addressDto.setState(address.getState());
        addressDto.setPostcode(address.getPostCode());
        addressDto.setCountry(address.getCountry());
        return addressDto;
    }
}
