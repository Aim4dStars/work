package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.service.integration.ips.IpsKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IpsKeyConverter implements Converter<String, IpsKey> {

    @Override
    public IpsKey convert(String key) {
        return IpsKey.valueOf(key);
    }
}
