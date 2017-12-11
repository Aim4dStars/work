package com.bt.nextgen.api.draftaccount.model.form;

import java.util.List;


/**
 * Created by m040398 on 14/03/2016.
 */
public interface IShareholderAndMembersForm {

    public boolean hasbeneficiaryClasses();
    public String getBeneficiaryClassDetails();
    public String getMajorShareholder();
    public String getCompanySecretaryValue();
}
