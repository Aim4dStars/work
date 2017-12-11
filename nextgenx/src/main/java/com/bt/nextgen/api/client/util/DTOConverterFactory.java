package com.bt.nextgen.api.client.util;

import com.bt.nextgen.service.avaloq.client.ClientListImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.avaloq.domain.TrustImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class DTOConverterFactory {

    private static Map<Class, DTOConverter> domainConverterMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(DTOConverterFactory.class);

    private DTOConverterFactory() {

    }

    static{
        domainConverterMap.put(IndividualImpl.class, new IndividualDtoConverter());
        domainConverterMap.put(SmsfImpl.class, new ClientDtoConverter());
        domainConverterMap.put(TrustImpl.class, new ClientDtoConverter());
        domainConverterMap.put(CompanyImpl.class, new ClientDtoConverter());
        domainConverterMap.put(ClientListImpl.class, new ClientDtoConverter());
        domainConverterMap.put(LegalClient.class, new ExistingClientSearchDtoConverter());
        domainConverterMap.put(IndividualWithAccountDataImpl.class, new IndividualWithAdvisersDtoConverter());
    }

    public static DTOConverter getConverter(Object domainClazz) {
        LOGGER.info("Class to be fetched :: {}",domainClazz.getClass().getName());
        LOGGER.info("Converter for class::",domainConverterMap.get(domainClazz.getClass()));
        return domainConverterMap.get(domainClazz.getClass());
    }
}
