package com.bt.nextgen.api.drawdown.v2.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownErrorMapperTest {

    @InjectMocks
    private DrawdownErrorMapperImpl errorMapper;

    @Test
    public void test_mapWarnings_emptyErrors() {
        List<ValidationError> errorList = errorMapper.mapWarnings(Collections.EMPTY_LIST);
        Assert.assertTrue(errorList.isEmpty());

        errorList = errorMapper.mapWarnings(null);
        Assert.assertTrue(errorList.isEmpty());
    }

    @Test
    public void test_mapWarnings_withErrors() {

        DomainApiErrorDto errorDto = mock(DomainApiErrorDto.class);
        when(errorDto.getErrorId()).thenReturn("Err.errorId");
        List<ValidationError> errorList = errorMapper.mapWarnings(Collections.singletonList(errorDto));
        Assert.assertTrue(errorList.isEmpty());

        when(errorDto.getErrorId()).thenReturn(null);
        errorList = errorMapper.mapWarnings(Collections.singletonList(errorDto));
        Assert.assertTrue(errorList.isEmpty());

        when(errorDto.getErrorId()).thenReturn("errorId");
        when(errorDto.getErrorType()).thenReturn("warnings");
        errorList = errorMapper.mapWarnings(Collections.singletonList(errorDto));
        Assert.assertTrue(errorList.size() == 1);
        Assert.assertEquals(ValidationError.ErrorType.WARNING, errorList.get(0).getType());

        when(errorDto.getErrorId()).thenReturn("errorId");
        when(errorDto.getErrorType()).thenReturn("error");
        errorList = errorMapper.mapWarnings(Collections.singletonList(errorDto));
        Assert.assertTrue(errorList.size() == 1);
        Assert.assertEquals(ValidationError.ErrorType.ERROR, errorList.get(0).getType());
    }

    @Test
    public void test_mapEmptyValidationError() {
        List<DomainApiErrorDto> dtoList = errorMapper.map(null);
        Assert.assertTrue(dtoList.isEmpty());

        dtoList = errorMapper.map(Collections.EMPTY_LIST);
        Assert.assertTrue(dtoList.isEmpty());
    }

    @Test
    public void test_mapValidationError() {

        ValidationError errorModel = mock(ValidationError.class);
        when(errorModel.getErrorId()).thenReturn("errorId");
        when(errorModel.getField()).thenReturn("errorField");
        when(errorModel.getMessage()).thenReturn("errorMessage");
        when(errorModel.isError()).thenReturn(Boolean.FALSE);

        List<DomainApiErrorDto> dtoList = errorMapper.map(Collections.singletonList(errorModel));
        Assert.assertTrue(dtoList.size() == 1);
        DomainApiErrorDto errorDto = dtoList.get(0);
        Assert.assertEquals(errorModel.getErrorId(), errorDto.getErrorId());
        Assert.assertEquals(errorModel.getMessage(), errorDto.getMessage());
        Assert.assertEquals(errorModel.getField(), errorDto.getDomain());
        Assert.assertEquals("warning", errorDto.getErrorType());

        when(errorModel.isError()).thenReturn(Boolean.TRUE);
        dtoList = errorMapper.map(Collections.singletonList(errorModel));
        Assert.assertTrue(dtoList.size() == 1);
        errorDto = dtoList.get(0);
        Assert.assertEquals("error", errorDto.getErrorType());
    }
}
