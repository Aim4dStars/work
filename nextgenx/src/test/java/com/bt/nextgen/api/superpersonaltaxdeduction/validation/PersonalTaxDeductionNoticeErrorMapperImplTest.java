package com.bt.nextgen.api.superpersonaltaxdeduction.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;


/**
 * Tests {@link PersonalTaxDeductionNoticeErrorMapperImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionNoticeErrorMapperImplTest {
    PersonalTaxDeductionNoticeErrorMapperImpl mapper;


    @Before
    public void init() {
        mapper = new PersonalTaxDeductionNoticeErrorMapperImpl();
    }


    @Test
    public void mapNull() {
        final List<DomainApiErrorDto> errors = mapper.map(null);

        assertThat("not null", errors, notNullValue());
        assertThat("no errors", errors.size(), equalTo(0));
    }


    @Test
    public void mapEmpty() {
        final List<DomainApiErrorDto> errors = mapper.map(new ArrayList<ValidationError>());

        assertThat("not null", errors, notNullValue());
        assertThat("no errors", errors.size(), equalTo(0));
    }


    @Test
    public void mapErrors() {
        final ValidationError error1 = new ValidationError("E1", "F1", "msg1", ValidationError.ErrorType.ERROR);
        final ValidationError error2 = new ValidationError("E2", "F2", "msg2", ValidationError.ErrorType.WARNING);
        final List<DomainApiErrorDto> errors = mapper.map(Arrays.asList(error1, error2));
        DomainApiErrorDto error;
        int index;

        assertThat("not null", errors, notNullValue());
        assertThat("no errors", errors.size(), equalTo(2));

        index = 0;
        error = errors.get(index);
        assertThat("errors[" + index + "] - id", error.getErrorId(), equalTo(error1.getErrorId()));
        assertThat("errors[" + index + "] - reason", error.getDomain(), equalTo(error1.getField()));
        assertThat("errors[" + index + "] - domain", error.getReason(), nullValue());
        assertThat("errors[" + index + "] - message", error.getMessage(), equalTo(error1.getMessage()));
        assertThat("errors[" + index + "] - errorType", error.getErrorType(),
                equalTo(DomainApiErrorDto.ErrorType.ERROR.toString()));

        index = 1;
        error = errors.get(index);
        assertThat("errors[" + index + "] - id", error.getErrorId(), equalTo(error2.getErrorId()));
        assertThat("errors[" + index + "] - reason", error.getDomain(), equalTo(error2.getField()));
        assertThat("errors[" + index + "] - domain", error.getReason(), nullValue());
        assertThat("errors[" + index + "] - message", error.getMessage(), equalTo(error2.getMessage()));
        assertThat("errors[" + index + "] - errorType", error.getErrorType(),
                equalTo(DomainApiErrorDto.ErrorType.WARNING.toString()));
    }
}
