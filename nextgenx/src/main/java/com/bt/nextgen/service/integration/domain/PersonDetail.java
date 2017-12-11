package com.bt.nextgen.service.integration.domain;

import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;

import java.util.List;

/**
 *
 * Abstract Domain object represents Person object in Application Document.
 */
public interface PersonDetail extends IndividualDetail
{
    List<AccountAuthoriser> getAccountAuthorisationList();

    void setAccountAuthorisationList(List<AccountAuthoriser> accountAuthorisationList);

    void setAlternateNameList(List<AlternateNameImpl> alternateNameList);

    List<AlternateNameImpl> getAlternateNameList();

    PersonRelationship getPrimaryRole();
    
    boolean isPrimaryContact();

    boolean isApprover();

    void setAddresses(List<Address> addresses);

    void setEmails(List<Email> emails);

    void setPhones(List<Phone> phones);

    boolean isMember();

    boolean isShareholder();

    boolean isBeneficiary();

    boolean isBeneficialOwner();

    boolean isSecretary();

    boolean isControllerOfTrust();
    void setTaxResidenceCountries( List<TaxResidenceCountry> taxResidenceCountries );

}
