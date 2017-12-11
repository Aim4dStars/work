package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by F058391 on 15/06/2016.
 */
public class PreservationAgeDto extends BaseDto {

    private int age;
    private String birthDateFrom;
    private String birthDateTo;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthDateFrom() {
        return birthDateFrom;
    }

    public void setBirthDateFrom(String birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public String getBirthDateTo() {
        return birthDateTo;
    }

    public void setBirthDateTo(String birthDateTo) {
        this.birthDateTo = birthDateTo;
    }
}
