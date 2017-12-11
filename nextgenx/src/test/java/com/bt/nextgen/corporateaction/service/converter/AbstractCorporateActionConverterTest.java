package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractCorporateActionConverterTest {
    protected CorporateActionOption createOptionMock(String key, String value) {
        CorporateActionOption corporateActionOption = mock(CorporateActionOption.class);

        when(corporateActionOption.getKey()).thenReturn(key);
        when(corporateActionOption.getValue()).thenReturn(value);
        when(corporateActionOption.hasValue()).thenReturn(value != null);

        return corporateActionOption;
    }

    protected CorporateActionOption createOptionMock(String key, BigDecimal bigDecimalValue) {
        CorporateActionOption corporateActionOption = mock(CorporateActionOption.class);

        when(corporateActionOption.getKey()).thenReturn(key);
        when(corporateActionOption.getBigDecimalValue()).thenReturn(bigDecimalValue);
        when(corporateActionOption.hasValue()).thenReturn(bigDecimalValue != null);

        return corporateActionOption;
    }

    protected CorporateActionOption createOptionMock(String key, String value, BigDecimal bigDecimalValue) {
        CorporateActionOption corporateActionOption = mock(CorporateActionOption.class);

        when(corporateActionOption.getKey()).thenReturn(key);
        when(corporateActionOption.getValue()).thenReturn(value);
        when(corporateActionOption.getBigDecimalValue()).thenReturn(bigDecimalValue);
        when(corporateActionOption.hasValue()).thenReturn(value != null || bigDecimalValue != null);

        return corporateActionOption;
    }

    protected CorporateActionSelectedOptionDto createSelectedOptionDtoMock(Integer optionId, BigDecimal units, BigDecimal oversubscribe) {
        CorporateActionSelectedOptionDto selectedOptionDto = mock(CorporateActionSelectedOptionDto.class);

        when(selectedOptionDto.getOptionId()).thenReturn(optionId);
        when(selectedOptionDto.getUnits()).thenReturn(units);
        when(selectedOptionDto.getOversubscribe()).thenReturn(oversubscribe);

        return selectedOptionDto;
    }

    protected CorporateActionOptionDto createOptionDtoMock(Integer id, String title, String summary, boolean defaultOption, boolean
            noAction) {
        CorporateActionOptionDto optionDto = mock(CorporateActionOptionDto.class);

        when(optionDto.getId()).thenReturn(id);
        when(optionDto.getTitle()).thenReturn(title);
        when(optionDto.getSummary()).thenReturn(summary);
        when(optionDto.getIsDefault()).thenReturn(defaultOption);
        when(optionDto.getIsNoAction()).thenReturn(noAction);

        return optionDto;
    }
}
