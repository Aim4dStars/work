package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;

import java.util.Map;

/**
 * Created by L069552 on 23/03/17.
 */
public interface CRSTaxDetailHelperService {

    void populateCRSTaxDetailsForOrganization(Organisation organisation,RegisteredEntityDto registeredEntityDto);

    void populateCRSTaxDetailsForOrganization(IOrganisationForm organisationForm,RegisteredEntityDto registeredEntityDto);

    void populateCRSTaxDetailsForIndividual(IPersonDetailsForm personDetailsForm,InvestorDto investorDto);

    void populateCRSTaxDetailsForIndividual(PersonDetail personDetail,InvestorDto investorDto, boolean isExistingUser, Map<String,Boolean> cisKeysToOverseasDetails,String cisKey);


}
