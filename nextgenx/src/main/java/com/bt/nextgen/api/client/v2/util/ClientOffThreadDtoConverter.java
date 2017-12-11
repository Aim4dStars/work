package com.bt.nextgen.api.client.v2.util;

import com.bt.nextgen.api.client.model.*;
import com.bt.nextgen.api.client.util.DTOConverter;
import com.bt.nextgen.api.client.v2.model.ClientDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.userinformation.Client;

import java.util.Arrays;

/**
 * Client Dto converter for ClientAccountOffThread Implementation
 * Copied from ClientDtoConverter and IndividualDtoConverter
 */
public class ClientOffThreadDtoConverter implements DTOConverter {

    @Override
    public Object toDTO(Object object) {
        Client client = (Client) object;
        if (client.getLegalForm() != null && InvestorType.INDIVIDUAL.equals(client.getLegalForm())){
            convertToIndividualDto(client, new IndividualDto());
        }

        ClientDto clientDto = new ClientDto();
        String clientId = EncodedString.fromPlainText(client.getClientKey().getId()).toString();
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey(clientId);
        clientDto.setKey(key);
        clientDto.setFullName(client.getFullName());
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(client.getLastName());
        if (client.getLegalForm() != null && !InvestorType.INDIVIDUAL.equals(client.getLegalForm())) {
            clientDto.setDisplayName(client.getFullName());
        } else {
            clientDto.setDisplayName(client.getLastName() + Constants.COMMA + Constants.SPACE_STRING + client.getFirstName());
        }
        for (Address address : client.getAddresses()) {
            clientDto.setState(address.getState());
            clientDto.setCountry(address.getCountry());
        }
        clientDto.setRegisteredOnline(client.isRegistrationOnline());
        return clientDto;
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


    private AddressDto addressDto(Address address)
    {
        AddressDto addressDto = new AddressDto();
        addressDto.setSuburb(address.getSuburb());
        addressDto.setState(address.getState());
        addressDto.setPostcode(address.getPostCode());
        addressDto.setCountry(address.getCountry());
        return addressDto;
    }

    private boolean isVerified(IdentityVerificationStatus identityVerificationStatus) {
        return identityVerificationStatus == IdentityVerificationStatus.Completed;
    }
}
