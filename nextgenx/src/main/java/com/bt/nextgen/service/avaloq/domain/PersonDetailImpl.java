package com.bt.nextgen.service.avaloq.domain;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AccountAuthoriserImpl;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.domain.PersonDetail;

import java.util.List;

/**
 * Class for xpath mapping of Person details returned in DOC_DET service.
 *
 * This class will be used to map both natural and legal persons returned in the
 * response.
 */

@ServiceBean(xpath = "person")
public class PersonDetailImpl extends IndividualDetailImpl implements PersonDetail
{
    @ServiceElementList(xpath = "invstr_auth_list/invstr_auth", type = AccountAuthoriserImpl.class)
    private List<AccountAuthoriser> accountAuthorisationList;

    @ServiceElement(xpath="auth_role_id/val", staticCodeCategory = "AUTH_ROLE")
    private PersonRelationship primaryRole;

    private List<AlternateNameImpl> alternateNameList;

    @Override
    public List<AccountAuthoriser> getAccountAuthorisationList(){
        return accountAuthorisationList;
    }

    @Override
    public void setAccountAuthorisationList(List<AccountAuthoriser> accountAuthorisationList) {
        this.accountAuthorisationList = accountAuthorisationList;
    }

    @Override
    public void setAlternateNameList(List<AlternateNameImpl> alternateNameList) {
        this.alternateNameList = alternateNameList;
    }

    @Override
    public List<AlternateNameImpl> getAlternateNameList() {
        return alternateNameList;
    }


    @Override
    public PersonRelationship getPrimaryRole() {
        return primaryRole;
    }

}
