package com.bt.nextgen.api.country.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base country DTO.
 * @author M013938
 */
public class CountryDto extends BaseDto implements KeyedDto<CountryCode> {

    /** International country code. */
    private final CountryCode code;

    /** Display name of the country. */
    private final String name;

    /** International dialling code. */
    private final String diallingCode;

    /** Three-letter UCM code (used in Westpac GCM). */
    private final String ucmCode;

    /** Two-Letter IM Code */
    private final String imCode;

    /**
     * Full constructor.
     * @param code country code.
     * @param name country name.
     * @param diallingCode international dialling code.
     * @param ucmCode Westpac UCM code.
     */
    public CountryDto(@Nonnull String code, @Nonnull String name, @Nullable String diallingCode, @Nullable String ucmCode, @Nullable String imCode) {
        this.code = new CountryCode(code);
        this.name = name;
        this.diallingCode = diallingCode;
        this.ucmCode = ucmCode;
        this.imCode = imCode;
    }

    @Override
    @Nonnull
    public CountryCode getKey() {
        return code;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public String getDiallingCode() {
        return diallingCode;
    }

    @Nullable
    public String getUcmCode() {
        return ucmCode;
    }

    @Nullable
    public String getImCode() {
        return imCode;
    }
}