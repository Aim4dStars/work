package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.integration.domain.PersonDetail;

import java.util.List;

public interface PersonMapperService {

    void mapPersonAccountSettings(List<PersonDetail> personList, List<PersonDetail> investorAccountSettingsList);

    void mapPersonAlternateNames(List<PersonDetail> personList, List<AlternateNameImpl> alternateNameList);

    void mapPersonTaxDetails(List<PersonDetail> personList,List<PersonDetail> personIdentityList);
}
