package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/")
public class SubaccountPreferencesHolder {

    @ServiceElementList(xpath = "//data/bp_list/bp/cont_list/cont/cont_head_list/cont_head/cont_pref_list/cont_pref", type = ModelPreferenceImpl.class)
    private List<ModelPreference> preferences;

    public List<ModelPreference> getPreferences() {
        if (preferences == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(preferences);
    }

}
