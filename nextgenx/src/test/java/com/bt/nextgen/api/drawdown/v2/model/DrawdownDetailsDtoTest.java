package com.bt.nextgen.api.drawdown.v2.model;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownDetailsDtoTest {

    @Test
    public void test_hasValidationError_nullWarnings() {
        DrawdownDetailsDto dto = new DrawdownDetailsDto();
        Assert.assertNull(dto.getWarnings());
        Assert.assertFalse(dto.hasValidationError());

        dto.setWarnings(Collections.EMPTY_LIST);
        Assert.assertFalse(dto.hasValidationError());
    }

    @Test
    public void test_hasValidationError_onlyWarnings() {

        DomainApiErrorDto error = mock(DomainApiErrorDto.class);
        when(error.getErrorType()).thenReturn("warning");
        DrawdownDetailsDto dto = new DrawdownDetailsDto();
        dto.setWarnings(Collections.singletonList(error));

        Assert.assertFalse(dto.hasValidationError());
    }

    @Test
    public void test_hasValidationError_warningsAndErrors() {

        List<DomainApiErrorDto> errorList = new ArrayList<>();
        DomainApiErrorDto error1 = mock(DomainApiErrorDto.class);
        when(error1.getErrorType()).thenReturn("warning");
        errorList.add(error1);

        DomainApiErrorDto error2 = mock(DomainApiErrorDto.class);
        when(error2.getErrorType()).thenReturn("error");
        errorList.add(error2);

        DrawdownDetailsDto dto = new DrawdownDetailsDto();
        dto.setWarnings(errorList);

        Assert.assertTrue(dto.hasValidationError());
    }
}
