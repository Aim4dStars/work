package com.bt.nextgen.api.country.service;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.country.model.CountryCode;
import com.bt.nextgen.api.country.model.CountryDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.btfin.panorama.core.conversion.CodeCategory.COUNTRY;
import static ch.lambdaj.Lambda.convert;

/**
 * Implementation of the {@code CountryDtoService}. Finds all countries, or a single country by code.
 * @author M013938
 */
@Service
public class CountryDtoServiceImpl implements CountryDtoService {

    /** External field name for UCM code. */
    public static final String UCM_CODE = "btfg$ucm_code";

    /** External field name for the international dialling prefix. */
    private static final String TEL_CODE = "btfg$tel_code";

    /** External field name for UCM code. */
    public static final String IM_CODE = "btfg$im_code";

    /** Converter for turning static codes into country DTO instances. */
    private static final Converter<Code, CountryDto> TO_COUNTRY_DTO = new Converter<Code, CountryDto>() {
        @Override
        public CountryDto convert(Code code) {
            final String telCode = fieldValue(code, TEL_CODE);
            final String ucmCode = fieldValue(code, UCM_CODE);
            final String imCode = fieldValue(code, IM_CODE);
            return new CountryDto(code.getUserId(), code.getName(), telCode, ucmCode, imCode);
        }
    };

    @Autowired
    private StaticIntegrationService staticCodes;

    @Override
    @Nonnull
    public List<CountryDto> findAll(@Nonnull ServiceErrors errors) {
        return convert(staticCodes.loadCodes(COUNTRY, errors), TO_COUNTRY_DTO);
    }

    @Override
    @Nullable
    public CountryDto find(@Nonnull CountryCode key, @Nonnull ServiceErrors errors) {
        final Code code = staticCodes.loadCodeByUserId(COUNTRY, key.getCode(), errors);
        return code == null ? null : TO_COUNTRY_DTO.convert(code);
    }

    /**
     * Retrieve the specified external field from the code, and if it's not null
     * @param code static code to be inspected.
     * @param fieldName name of the external field to look for.
     * @return the value of the field if found, otherwise {@code null}.
     */
    @Nullable
    public static String fieldValue(@Nonnull Code code, @Nonnull String fieldName) {
        final Field field = code.getField(fieldName);
        return field == null ? null : field.getValue();
    }
}