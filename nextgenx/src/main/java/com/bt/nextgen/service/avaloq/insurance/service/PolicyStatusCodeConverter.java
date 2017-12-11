package com.bt.nextgen.service.avaloq.insurance.service;

import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PolicyStatusCodeConverter implements Converter<String, PolicyStatusCode> {
    @Override
    public PolicyStatusCode convert(String status) {
        if ("Waiver".equalsIgnoreCase(status) || "Holiday".equalsIgnoreCase(status)) {
            return PolicyStatusCode.IN_FORCE;
        }
        return PolicyStatusCode.forStatus(status);
    }
}