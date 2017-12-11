package com.bt.nextgen.service.avaloq.client;

import com.bt.nextgen.service.integration.domain.Gender;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GenderTest {

    @Test
    public void testGenderValues() {

        Gender gender = Gender.OTHER;
        assertThat(gender.toString(), is("btfg$unspec"));
        assertThat(gender.getName(), is("Other"));

        gender = Gender.MALE;
        assertThat(gender.toString(), is("male"));
        assertThat(gender.getName(), is("Male"));

        gender = Gender.FEMALE;
        assertThat(gender.toString(), is("female"));
        assertThat(gender.getName(), is("Female"));
    }
}