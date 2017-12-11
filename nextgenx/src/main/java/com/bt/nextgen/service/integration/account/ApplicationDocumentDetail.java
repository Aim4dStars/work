package com.bt.nextgen.service.integration.account;

import com.bt.nextgen.service.avaloq.account.AccountAuthoriser;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.avaloq.pension.PensionEligibility;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.fees.FeesSchedule;

import java.util.Date;
import java.util.List;

public interface ApplicationDocumentDetail extends ApplicationDocument {
    /**
     * Linked account list for the account
     * @return
     */
    List<RegisteredAccountImpl> getLinkedAccounts();

    List<PersonDetail> getPersons();

    List<Organisation> getOrganisations();

    List<PersonDetail> getAccountSettingsForAllPersons();

    List<AlternateNameImpl> getAlternateNames();

    List<AccountAuthoriser> getAdviserAccountSettings();

    BrokerKey getAdviserKey();

    Date getApplicationOpenDate();

    List<FeesSchedule> getFees();

    PensionEligibility getPensionEligibility();

    AccountSubType getSuperAccountSubType();

    List<BPClassList> getAccountClassList();

    List<PersonDetail> getPersonIdentityList();

    String getAccountNumber();
}
