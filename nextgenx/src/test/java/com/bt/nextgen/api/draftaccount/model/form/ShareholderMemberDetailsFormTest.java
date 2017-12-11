package com.bt.nextgen.api.draftaccount.model.form;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ShareholderMemberDetailsFormTest {

    @Test
    public void testGenderIsNull() throws Exception {
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(Collections.EMPTY_MAP);
        assertThat(form.getGender(), nullValue());
    }

    @Test
    public void isBeneficiary_shouldReturnTrueForABeneficiary() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficiary");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isBeneficiary());
    }

    @Test
    public void isBeneficialOwner_shouldReturnTrueForBeneficialOwner(){
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficialOwner");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isBeneficialOwner());

    }

    @Test
    public void isBeneficialOwner_shouldReturnTrueForBothBeneficiaryAndBeneficialOwner(){
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficiaryAndBeneficialOwner");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isBeneficialOwner());

    }

    @Test
    public void isBeneficiary_shouldReturnTrueForBothBeneficiaryAndShareholder() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficiaryAndShareholder");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isBeneficiary());
    }

    @Test
    public void isBeneficiary_shouldReturnTrueForBothBeneficiaryAndBeneficialOwner() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficiaryAndBeneficialOwner");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isBeneficiary());
    }

    @Test
    public void isBeneficiary_shouldReturnFalseForBothShareholderAndMember() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "shareholderAndMember");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertFalse(form.isBeneficiary());
    }
    @Test
    public void isMember_shouldReturnTrueForAMember() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "member");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isMember());
    }
    @Test
    public void isMember_shouldReturnTrueForBothShareholderAndMember() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "shareholderAndMember");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isMember());
    }

    @Test
    public void isMember_shouldReturnFalseForBothBeneficiaryAndShareholder() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficiaryAndShareholder");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertFalse(form.isMember());
    }

    @Test
    public void isShareholder_shouldReturnTrueForAShareholder() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "shareholder");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isShareholder());
    }
    @Test
    public void isShareholder_shouldReturnTrueForBeneficiaryAndShareholder() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "beneficiaryAndShareholder");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isShareholder());
    }

    @Test
    public void isShareholder_shouldReturnTrueForShareholderAndMember() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "shareholderAndMember");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertTrue(form.isShareholder());
    }

    @Test
    public void isShareholder_shouldReturnFalseForMember() {
        Map<String, Object> map = new HashMap<>();
        map.put("persontype", "member");
        ShareholderMemberDetailsForm form = new ShareholderMemberDetailsForm(map);
        assertFalse(form.isShareholder());
    }
}
