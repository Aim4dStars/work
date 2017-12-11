package com.bt.nextgen.service.cmis.converter;

/**
 * Created by L062329 on 21/07/2015.
 */
public enum ConverterMapper {
    CmisPropertyId(new CmisPropertyIDConverter()),
    CmisPropertyString(new CmisPropertyStringConverter()),
    CmisPropertyDateTime(new CmisPropertyDateTimeConverter()),
    CmisPropertyInteger(new CmisPropertyIntegerConverter()),
    CmisPropertyBoolean(new CmisPropertyBooleanConverter()),
    CmisPropertyDateTimeToString(new CmisPropertyDateTimeToStringConverter()),
    CmisPropertyEmptyString(new CmisPropertyEmptyStringConverter());

    private transient Converter converter;

    ConverterMapper(Converter converter) {
        this.converter = converter;
    }

    public Converter getConverter() {
        return converter;
    }
}
