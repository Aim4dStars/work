package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.PreservationAgeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

/**
 * Created by F058391 on 15/06/2016.
 */
@Service
public class PreservationAgeServiceImpl implements PreservationAgeService {

    public static final String PSV_AGE = "psv_age";
    public static final String BIRTH_DATE_FROM = "birth_date_from";
    public static final String BIRTH_DATE_TO = "birth_date_to";
    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public List<PreservationAgeDto> findAll(ServiceErrors serviceErrors) {
        final Collection<Code> codes = staticIntegrationService.loadCodes(CodeCategory.PRESERVATION_AGE, serviceErrors);
        return Lambda.convert(codes, new Converter<Code, PreservationAgeDto>() {
            @Override
            public PreservationAgeDto convert(Code code) {
                PreservationAgeDto preservationAgeDto = new PreservationAgeDto();
                final Collection<Field> fields = code.getFields();
                Map<String, Field> fieldMap = Lambda.index(fields, on(Field.class).getName());
                preservationAgeDto.setAge(Integer.parseInt(fieldMap.get(PSV_AGE).getValue()));

                final Field birthDateFrom = fieldMap.get(BIRTH_DATE_FROM);
                if (birthDateFrom != null) {
                    preservationAgeDto.setBirthDateFrom(birthDateFrom.getValue());
                }

                final Field birthDateTo = fieldMap.get(BIRTH_DATE_TO);
                if (birthDateTo != null) {
                    preservationAgeDto.setBirthDateTo(birthDateTo.getValue());
                }
                return preservationAgeDto;
            }
        });
    }
}
