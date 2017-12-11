package com.bt.nextgen.service.integration.externalasset.builder;

import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.asset.AssetKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.swing.text.Position;

/**
 * Converts a String into a PositionIdentifier
 */
@Component
public class PositionIdentifierConverter implements Converter<String, PositionIdentifier>
{
    public PositionIdentifier convert(String s)
    {
        return new PositionIdentifierImpl(s);
    }
}
