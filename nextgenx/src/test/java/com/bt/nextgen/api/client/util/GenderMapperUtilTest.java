package com.bt.nextgen.api.client.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GenderMapperUtilTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void shouldReturnCorrectLabelForGivenGCMGenderCode() throws Exception {
        String maleGenderLabel = GenderMapperUtil.getGenderFromGCMGenderCode("M");
        assertThat(maleGenderLabel, is("male"));
        String femaleGenderLabel = GenderMapperUtil.getGenderFromGCMGenderCode("F");
        assertThat(femaleGenderLabel, is("female"));
        String otherGenderLabel = GenderMapperUtil.getGenderFromGCMGenderCode("U");
        assertThat(otherGenderLabel, is("other"));
    }

    @Test
    public void shouldThrowExceptionIf() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Gender mapping doesn't exist");
        GenderMapperUtil.getGenderFromGCMGenderCode("NA");
    }
}