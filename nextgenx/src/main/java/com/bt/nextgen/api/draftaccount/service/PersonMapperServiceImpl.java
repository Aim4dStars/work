package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.util.matcher.PersonDetailMatcher;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
@Transactional
public class PersonMapperServiceImpl implements PersonMapperService {

    @Override
    public void mapPersonAccountSettings(List<PersonDetail> personList, List<PersonDetail> investorAccountSettingsList) {
        if(personList != null){
            for (PersonDetail personDetail : personList) {
                final ClientKey clientKey = personDetail.getClientKey();
                if (!investorAccountSettingsList.isEmpty()) {
                    PersonDetail accountSettings = Lambda.selectFirst(investorAccountSettingsList, new PersonDetailMatcher(clientKey));
                    if (accountSettings != null) {
                        personDetail.setAccountAuthorisationList(accountSettings.getAccountAuthorisationList());
                    }
                }
            }

        }
    }

    @Override
    public void mapPersonAlternateNames(List<PersonDetail> personList, List<AlternateNameImpl> alternateNameList) {
        if(!isEmpty(personList)){
            for (PersonDetail personDetail : personList) {
                final ClientKey personClientKey = personDetail.getClientKey();
                if (!isEmpty(alternateNameList)) {
                    List<AlternateNameImpl> filteredNameList = Lambda.select(alternateNameList, Lambda.having(Lambda.on(AlternateNameImpl.class).getClientKey(), Matchers.equalTo(personClientKey)));
                    personDetail.setAlternateNameList(filteredNameList);
                }
            }
        }
    }

    @Override
    public void mapPersonTaxDetails(List<PersonDetail> personList, List<PersonDetail> personIdentityList) {
        if(CollectionUtils.isNotEmpty(personIdentityList) && CollectionUtils.isNotEmpty(personList)){

                for (PersonDetail personDetail : personList) {
                    final ClientKey clientKey = personDetail.getClientKey();

                        PersonDetail personDetail1 = Lambda.selectFirst(personIdentityList, new PersonDetailMatcher(clientKey));
                        if (personDetail1 != null) {
                          personDetail.setTaxResidenceCountries(personDetail1.getTaxResidenceCountries());
                        }
                }

        }
    }
}
