package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.Individual;

import java.util.Arrays;

public class IndividualDtoConverter implements DTOConverter {

    public Object toDTO(Object client) {
        return convertToIndividualDto((Client) client, new IndividualDto());
    }

    protected IndividualDto convertToIndividualDto(Client client, IndividualDto individualDto) {
        ClientKey key = new ClientKey(EncodedString.fromPlainText(client.getClientKey().getId()).toString());

        individualDto.setKey(key);
        individualDto.setFullName(client.getFullName());

        Address address = client.getAddresses().get(0);

        individualDto.setAddresses(Arrays.asList(addressDto(address)));
        individualDto.setState(address.getState());
        individualDto.setCountry(address.getCountry());

        Individual individual = (Individual) client;
        if (individual.getDateOfBirth() != null) {
            individualDto.setDateOfBirth(individual.getDateOfBirth().toString());
        }

        if (individual.getTitle() != null) {
            individualDto.setTitle(individual.getTitle());
        }

        individualDto.setFirstName(individual.getFirstName());
        individualDto.setLastName(individual.getLastName());
        individualDto.setDisplayName(individualDto.getLastName() + ", " + individualDto.getFirstName());

        individualDto.setInvestorType("Individual");

        individualDto.setIdVerified(isVerified(individual.getIdentityVerificationStatus()));
        return individualDto;
    }

    private boolean isVerified(IdentityVerificationStatus identityVerificationStatus) {
        return identityVerificationStatus == IdentityVerificationStatus.Completed;
    }

    private AddressDto addressDto(Address address)
    {
        AddressDto addressDto = new AddressDto();
        addressDto.setSuburb(address.getSuburb());
        addressDto.setState(address.getState());
        addressDto.setPostcode(address.getPostCode());
        addressDto.setCountry(address.getCountry());
        return addressDto;
    }
}
