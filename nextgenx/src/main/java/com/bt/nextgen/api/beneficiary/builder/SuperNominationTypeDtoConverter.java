package com.bt.nextgen.api.beneficiary.builder;

/**
 * Created by L067218 on 5/07/2016.
 */

import com.bt.nextgen.api.beneficiary.model.SuperNominationTypeDto;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.springframework.util.StringUtils.trimArrayElements;

/**
 * Converter for super beneficiary details
 */
public class SuperNominationTypeDtoConverter {
    private static final String NOMINATION_LIST_NAME = "au_sa_death_benf";
    private static final String EXTL_FLD_NAME_LABEL = "btfg$ui_name";
    private static final String EXTL_FLD_NAME_DEPENDENTS_ONLY = "btfg$ui_dep_only";
    private static final String EXTL_FLD_NAME_PERCENT = "btfg$ui_pct";
    private static final String EXTL_FLD_NAME_SUPER_ACCOUNT_TYPE = "btfg$ui_super_acc_type";
    private static final String EXTL_FLD_VALUE_TRUE = "+";
    private static final String EXTL_FLD_VALUE_100 = "100";
    private static final String PENSION_VALUE = "pension";
    private static final String ACCUMULATION_VALUE = "super";
    private static final Map<String, String> SUPER_SUBTYPES;

    /**
     * Initialise map for super subtypes.
     */
    static {
        SUPER_SUBTYPES = new HashMap<>();

        SUPER_SUBTYPES.put(AccountSubType.ACCUMULATION.name(), ACCUMULATION_VALUE);
        SUPER_SUBTYPES.put(AccountSubType.PENSION.name(), PENSION_VALUE);
    }


    /**
     * Get a DTO list of nomination types
     *
     * @param categoryCodes
     *
     * @return DTO list of SuperNominationTypeDto
     */
    public List<SuperNominationTypeDto> createNominationTypeList(final Collection<Code> categoryCodes,
                                                                 AccountSubType accountSubType, String filter) {
        List<SuperNominationTypeDto> nominationList = new ArrayList<SuperNominationTypeDto>();

        for (Code code : categoryCodes) {
            final SuperNominationTypeDto nominationDto = new SuperNominationTypeDto();

            setSuperNominationTypeDtoProperties(code, nominationDto);
            nominationList.add(nominationDto);
        }

        if ("true".equals(filter) && accountSubType != null) {
            nominationList = select(nominationList,
                    having(on(SuperNominationTypeDto.class).getSupportedSuperAccountSubTypes()
                            .contains(SUPER_SUBTYPES.get(accountSubType.name()))));
        }

        sortNominationTypes(nominationList);

        return nominationList;
    }

    private void setSuperNominationTypeDtoProperties(final Code code, final SuperNominationTypeDto nominationDto) {
        nominationDto.setId(code.getCodeId());
        nominationDto.setValue(code.getUserId());
        nominationDto.setIntlId(code.getIntlId());
        nominationDto.setListName(NOMINATION_LIST_NAME);

        for (final Field field : code.getFields()) {
            final String name = field.getName();
            final String value = field.getValue();

            if (EXTL_FLD_NAME_LABEL.equals(name)) {
                nominationDto.setLabel(value);
            }
            else if (EXTL_FLD_NAME_SUPER_ACCOUNT_TYPE.equals(name)) {
                final String[] superAccountSubTypes = trimArrayElements(StringUtils.split(value, ","));

                nominationDto.setSupportedSuperAccountSubTypes(Arrays.asList(superAccountSubTypes));
            }
            else if (EXTL_FLD_NAME_DEPENDENTS_ONLY.equals(name)) {
                nominationDto.setDependentOnly(value != null && EXTL_FLD_VALUE_TRUE.equals(value));
            }
            else if (EXTL_FLD_NAME_PERCENT.equals(name)) {
                nominationDto.setSoleNominationOnly(value != null && EXTL_FLD_VALUE_100.equals(value));
            }
        }
    }

    /**
     * Sort the nomination list in ascending order by label.
     *
     * @param nominationList to sort.
     */
    private void sortNominationTypes(final List<SuperNominationTypeDto> nominationList) {
        Collections.sort(nominationList, new Comparator<SuperNominationTypeDto>() {
            @Override
            public int compare(SuperNominationTypeDto dto1, SuperNominationTypeDto dto2) {
                return dto1.getLabel().compareTo(dto2.getLabel());
            }
        });
    }
}
