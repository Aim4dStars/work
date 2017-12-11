package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.service.integration.domain.Gender;

import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class ShareholderMemberDetailsForm extends ExtendedPersonDetailsForm {
    public ShareholderMemberDetailsForm(Map<String, Object> map) {
        super(map, false, null, false);
    }

    @Override
    public boolean isBeneficiary() {
        String personType = (String) map.get("persontype");
        return personType.toLowerCase().contains("beneficiary");
    }

    @Override
    public boolean isShareholder() {
        String personType = (String) map.get("persontype");
        return personType.toLowerCase().contains("shareholder");
    }

    @Override
    public boolean isMember() {
        String personType = (String) map.get("persontype");
        return personType.toLowerCase().contains("member");
    }

    @Override
    public boolean isBeneficialOwner() {
        String personType = (String) map.get("persontype");
        return personType.toLowerCase().contains("beneficialowner");
    }

    public boolean hasGender() {
        return map.containsKey("additionalshareholdergender");
    }

    public Gender getGender() {
        final String gender = (String) map.get("additionalshareholdergender");
        return gender!=null ? Gender.valueOf(gender.toUpperCase()): null;
    }
}
