package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

/**
 * Created by l079353 on 10/04/2017.
 */
public class AccountSettingsDto extends BaseDto {

    private List<PersonRelationDto> personRelations;
    private String powerOfAttorney;

    public List<PersonRelationDto> getPersonRelations() {
        return personRelations;
    }

    public void setPersonRelations(List<PersonRelationDto> personRelations) {
        this.personRelations = personRelations;
    }

    public String getPowerOfAttorney() {
        return powerOfAttorney;
    }

    public void setPowerOfAttorney(String powerOfAttorney) {
        this.powerOfAttorney = powerOfAttorney;
    }
}
