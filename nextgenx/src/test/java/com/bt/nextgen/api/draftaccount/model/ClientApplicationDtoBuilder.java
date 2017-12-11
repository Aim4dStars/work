package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class ClientApplicationDtoBuilder {

    private String productName = "my-product-name";
    private String adviserId = "DD17ADAF0D6E2F1847BDFDD2493B41C35AED2A82A53607B2";
    private LinkedHashMap<String,Object> formData = defaultFormData();
    private ClientApplicationKey clientApplicationKey = new ClientApplicationKey(124L);
    private ClientApplicationStatus status = ClientApplicationStatus.draft;
    private String encodedProductId = "A13EEBEC6074850121E40555F623A1A479986A9E316B5F70";

    public static ClientApplicationDtoBuilder aDraftAccountDto() {
        return new ClientApplicationDtoBuilder();
    }

    public <T> ClientApplicationDto build() {
        return build(ClientApplicationDtoMapImpl.class);
    }

    public <T> ClientApplicationDto build(Class<?> T) {
        final ClientApplicationDto dto;
        if (T.getCanonicalName().equalsIgnoreCase(ClientApplicationDtoDirectImpl.class.getCanonicalName())) {
            dto = new ClientApplicationDtoDirectImpl(clientApplicationKey);
        } else if (T.getCanonicalName().equalsIgnoreCase(ClientApplicationDtoAdvisedImpl.class.getCanonicalName())) {
            dto = new ClientApplicationDtoAdvisedImpl(clientApplicationKey);
        } else {
            dto = new ClientApplicationDtoMapImpl(clientApplicationKey, formData);
        }
        dto.setProductName(productName);
        dto.setAdviserId(adviserId);
        dto.setStatus(status);
        dto.setProductId(encodedProductId);
        dto.setOffline(false);
        return dto;
    }

    public ClientApplicationDtoBuilder withFormData(String formData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.formData = mapper.readValue(formData, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Unable to build DraftAccountDto using test data: " + formData ,e);
        }
        return this;
    }

    private LinkedHashMap<String,Object> defaultFormData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("accountType", "individual");
        map.put("investors", Arrays.asList(new LinkedHashMap<>()));
        return map;
    }

    public ClientApplicationDtoBuilder withProductId(String encodedProductId) {
        this.encodedProductId = encodedProductId;
        return this;
    }

    public ClientApplicationDtoBuilder withAdviserId(String adviserId) {
        this.adviserId = adviserId;
        return this;
    }
}
