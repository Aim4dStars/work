package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.avaloq.fees.FeesScheduleImpl;
import com.bt.nextgen.service.avaloq.pension.PensionEligibility;
import com.bt.nextgen.service.avaloq.pension.PensionEligibilityImpl;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.account.BPClassList;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.domain.*;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.messages.DateTypeConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * This is a mapping class for detailed response for application document.
 */
@ServiceBean(xpath = "doc")
public class ApplicationDocumentDetailImpl extends ApplicationDocumentImpl implements ApplicationDocumentDetail {

    private static final String XPATH_DOC_HEAD_LIST = "doc_head_list/";
    private static final String XPATH_CONSTANT = XPATH_DOC_HEAD_LIST + "doc_head/";

    // Xpath to get the first occurrence of Former Name/ Alternate Name for a Person as there could be multiple former names for a person
    private static final String XPATH_FORMER_NAME ="(//alt_name[alt_name_type/val='1'][1])|(//alt_name_list/alt_name[alt_name_type/val='2'][1])";

    @ServiceElementList(xpath = XPATH_CONSTANT + "reg_acc_list/reg_acc", type = RegisteredAccountImpl.class)
    private List<RegisteredAccountImpl> linkedAccounts;

    @ServiceElement(xpath = XPATH_CONSTANT + "avsr_oe_id/val", converter = BrokerKeyConverter.class)
    private BrokerKey adviserKey;

    @ServiceElement(xpath = XPATH_CONSTANT + "bp_nr/val")
    private String accountNumber;

    @ServiceElement(xpath = XPATH_CONSTANT + "opn_date/val", converter = DateTypeConverter.class)
    private Date applicationOpenDate;

    /**
     * Due to Avaloq technical constraints, Addresses related to one person is given as a separate person object.
     * This list of Persons will not have the address information.
     * Once the addresses are retrieved, this object needs to be updated with the address.
     */
    @ServiceElementList(xpath = XPATH_CONSTANT + "person_list/person[full_name and not(count(./legal_form_id)>0)]", type = PersonDetailImpl.class)
    private List<PersonDetail> persons;

    @ServiceElementList(xpath = XPATH_CONSTANT + "person_list/person[legal_form_id]", type = OrganisationImpl.class)
    private List<Organisation> organisations;

    @ServiceElementList(xpath = XPATH_DOC_HEAD_LIST + "doc_head", type = PensionEligibilityImpl.class)
    private PensionEligibility pensionEligibility;

    @ServiceElement(xpath = XPATH_CONSTANT + "sa_sub_type_id/val", staticCodeCategory = "SUPER_ACCOUNT_SUB_TYPE")
    private AccountSubType superAccountSubType;

    /**
     * Due to Avaloq technical constraints, Account settings information related to one person
     * is given as separate person object. We are mimicking the same behavior, as there is no workaround.
     * To get complete information of a person, find the corresponding account settings
     * information in 'investorAccountSettingsList' using client key.
     */

    @ServiceElementList(xpath = XPATH_CONSTANT + "person_list/person[invstr_auth_list]", type = PersonDetailImpl.class)
    private List<PersonDetail> investorAccountSettingsList;

    @ServiceElementList(xpath = XPATH_CONSTANT + XPATH_FORMER_NAME, type = AlternateNameImpl.class)
    private List<AlternateNameImpl> alternameList;

    @ServiceElementList(xpath = XPATH_CONSTANT + "fee_periodic_list/fee_periodic|" + XPATH_CONSTANT + "fee_adhoc_list/fee_adhoc", type = FeesScheduleImpl.class)
    private List<FeesSchedule> feesSchedule;

    @ServiceElementList(xpath = XPATH_CONSTANT + "intm_auth_list/intm_auth", type = AccountAuthoriserImpl.class)
    private List<AccountAuthoriser> adviserAccountSettings;

    @ServiceElementList(xpath = XPATH_CONSTANT + "bp_class_list_list/bp_class_list", type = BPClassListImpl.class)
    private List<BPClassList> bpClassLists;

    @ServiceElementList(xpath = XPATH_CONSTANT + "person_list/person[ident_list]", type = PersonDetailImpl.class)
    private List<PersonDetail> personIdentityList;

    @Override
    public AccountSubType getSuperAccountSubType() {
        return superAccountSubType;
    }

    @Override
    public List<BPClassList> getAccountClassList() {
        return bpClassLists;
    }

    public void setSuperAccountSubType(AccountSubType superAccountSubType) {
        this.superAccountSubType = superAccountSubType;
    }

    @Override
    public List<RegisteredAccountImpl> getLinkedAccounts() {
        return linkedAccounts;
    }

    @Override
    public List<PersonDetail> getPersons() {
        return persons;
    }

    @Override
    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setPersons(List<PersonDetail> persons) {
        this.persons = persons;
    }

    public void setLinkedAccounts(List<RegisteredAccountImpl> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    @Override
    public BrokerKey getAdviserKey() {
        return adviserKey;
    }

    public void setAdviserKey(BrokerKey adviserKey) {
        this.adviserKey = adviserKey;
    }

    @Override
    public List<PersonDetail> getAccountSettingsForAllPersons() {
        return investorAccountSettingsList;
    }

    @Override
    public List<AccountAuthoriser> getAdviserAccountSettings() {
        if (adviserAccountSettings == null){
            return new ArrayList<>();
        }
        return adviserAccountSettings;
    }

    @Override
    public Date getApplicationOpenDate() {
        return new Date(applicationOpenDate.getTime());
    }

    public void setApplicationOpenDate(Date applicationOpenDate) {
        this.applicationOpenDate = new Date(applicationOpenDate.getTime());
    }

    @Override
    public List<FeesSchedule> getFees() {
        return feesSchedule;
    }

    @Override
    public PensionEligibility getPensionEligibility() {
        return pensionEligibility;
    }

    public void setPensionEligibility(PensionEligibility pensionEligibility) {
        this.pensionEligibility = pensionEligibility;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }

    public void setInvestorAccountSettingsList(List<PersonDetail> investorAccountSettingsList) {
        this.investorAccountSettingsList = investorAccountSettingsList;
    }

    public void setFeesSchedule(List<FeesSchedule> feesSchedule) {
        this.feesSchedule = feesSchedule;
    }

    public void setAdviserAccountSettings(List<AccountAuthoriser> adviserAccountSettings) {
        this.adviserAccountSettings = adviserAccountSettings;
    }

    public List<AlternateNameImpl> getAlternateNames() {
        return alternameList;
    }

    public void setAlternateNameList(List<AlternateNameImpl> alternameList) {
        this.alternameList = alternameList;
    }

    public List<PersonDetail> getPersonIdentityList() {
        return personIdentityList;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

}
