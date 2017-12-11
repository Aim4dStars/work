package com.bt.nextgen.api.client.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.util.DTOConverterFactory;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ExistingClientSearchDtoServiceImpl implements ExistingClientSearchDtoService {
    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    private List<ClientIdentificationDto> getClientIdentificationDtos(Collection<Client> clients) {
        return Lambda.convert(clients, new Converter<Client, ClientIdentificationDto>() {
            @Override
            public ClientIdentificationDto convert(Client client) {
                return (ClientIdentificationDto) DTOConverterFactory.getConverter(client).toDTO(client);
            }
        });
    }

    @Override
    public List<ClientIdentificationDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        ApiSearchCriteria searchCriteria = criteriaList.get(0);
        Collection<Client> clients = clientIntegrationService.loadClientsForExistingClientSearch(serviceErrors, searchCriteria.getValue());
        return getClientIdentificationDtos(clients);
    }
}
