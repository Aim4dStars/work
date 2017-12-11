package com.bt.nextgen.api.draftaccount.model.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class ShareholderAndMembersForm implements IShareholderAndMembersForm {
    private final Map<String, Object> map;

    public ShareholderAndMembersForm(Map<String, Object> map) {
        if (map != null) {
            this.map = map;
        } else {
            this.map = new HashMap<>();
        }
    }

    public boolean hasbeneficiaryClasses() {
    	return "yes".equalsIgnoreCase((String)map.get("hasbeneficiaryclasses"));
    }
    
    public String getBeneficiaryClassDetails() {
    	return (String) map.get("beneficiaryclassdetails");
    }

    public String getMajorShareholder(){ return (String)map.get("isMajorShareholder");}

    @Override
    public String getCompanySecretaryValue() {
         return (String)map.get("companysecretary");
    }

    /**
     * Not part of public interface. Package protected to be used from ClientApplicationForm
     * @return
     */
    List<Map<String, Object>> getInvestorsWithRoles() {
        return (List<Map<String, Object>>) map.get("investorsWithRoles");
    }

    /**
     * Package protected. Not part of public interface
     * @return
     */
    List<IExtendedPersonDetailsForm> getAdditionalShareholdersAndMembers() {
        List<IExtendedPersonDetailsForm> additionalMemberForms = new ArrayList<>();
        List<Map<String, Object>> additionalPersons =  (List<Map<String, Object>>) map.get("additionalShareHoldersAndMembers");
        if (additionalPersons != null) {
            for (int i = 0; i < additionalPersons.size(); i++) {
                additionalMemberForms.add(new ShareholderMemberDetailsForm(additionalPersons.get(i)));
            }
        }
        return additionalMemberForms;
    }
}
