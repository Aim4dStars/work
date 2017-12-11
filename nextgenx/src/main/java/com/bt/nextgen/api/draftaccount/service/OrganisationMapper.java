package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.policy.model.Person;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;

import java.util.List;

/**
 * Created by L069552 on 11/03/17.
 */
public interface OrganisationMapper {

    void mapOrganisationTaxDetails(List<Organisation> organisationList,List<PersonDetail> organisationIdentityList);
}
