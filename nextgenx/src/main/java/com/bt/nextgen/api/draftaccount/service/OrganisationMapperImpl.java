package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.util.matcher.PersonDetailMatcher;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by L069552 on 11/03/17.
 */

@Service
@Transactional
public class OrganisationMapperImpl implements OrganisationMapper {
    @Override
    public void mapOrganisationTaxDetails(List<Organisation> organisationList, List<PersonDetail> personIdentityList) {
        if(CollectionUtils.isNotEmpty(organisationList)){
            for (Organisation organisation : organisationList) {
                final ClientKey clientKey = organisation.getClientKey();
                if (CollectionUtils.isNotEmpty(personIdentityList)) {
                    PersonDetail taxCountrySettings = Lambda.selectFirst(personIdentityList, new PersonDetailMatcher(clientKey));
                    if (taxCountrySettings != null) {
                        organisation.setTaxResidenceCountries(taxCountrySettings.getTaxResidenceCountries());
                    }
                }
            }
        }
    }
}
