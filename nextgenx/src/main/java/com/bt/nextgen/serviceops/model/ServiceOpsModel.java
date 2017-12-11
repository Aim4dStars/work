package com.bt.nextgen.serviceops.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import org.joda.time.DateTime;

import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.repository.OnboardingPartyDisplayStatus;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.portfolio.domain.Portfolio;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.userauthority.web.Action;

@SuppressWarnings("all")
public class ServiceOpsModel {
    private String firstName;
    private String lastName;
    private String salaryId;
    private List<Email> email;
    private String filter;
    private String criteria;

    private List<JobProfile> jobProfiles;

    private String userId;
    private EncodedString clientId;
    private Set<String> dealerGroupList;
    private String primaryMobileNumber;

    //Added by AB to support onboarding
    private String safiDeviceId;
    private String gcmId;
    private String cisId;
    private String westpacCustomerNumber;

    //personal details
    private String registeredSince;
    private String dob;
    private String userName;
    private List<Phone> phone;
    private List<Phone> mobilePhones;
    private String secodryEmail;
    private String mobileNumber;
    private AddressModel residentialAddress;
    private AddressModel postalAddress;
    private String action;
    Map<Action, String> actionValues;
    private List<Portfolio> portfolios;
    private List<IntermediariesModel> intermediaries;
    private List<ClientModel> clients;
    private List<WrapAccountModel> accounts;
    private String role;

    private String secretKey;
    private String canPerformAction;
    private String canCreateAccount;

    private String message;

    private String informationMessage;
    private DateTime startDate;
    private boolean terminatedFlag;

    private UserAccountStatus loginStatus;
    private String fullName;
    private boolean avaloqStatusReg;

    private String practiceName;

    private boolean adviserFlag;

    private boolean paraPlannerFlag;

    //Fields to show Onboarding Status on ServiceOps Screen
    private OnboardingPartyDisplayStatus onboardingStatus;
    private String onboardingFailureReason;

    private String companyName;

    private boolean westpacLive;

    private String ppId;

    private String ppIdFromAvaloq;

    private List<LinkedClientModel> linkedClients;

    private WrapAccountDetailDto wrapAccountDetail;

    private String primaryContactPerson;

    private boolean wib;

    private boolean directInvestorFlag;

    private boolean migratedCustomer;


    private String resolvedPref;

    private String accountNumber;

    private String cmaStatementPref;

    private boolean mandatoryDetailMissing;


    public List<LinkedClientModel> getLinkedClients() {
        return linkedClients;
    }

    public void setLinkedClients(List<LinkedClientModel> linkedClients) {
        this.linkedClients = linkedClients;
    }

    public String getPrimaryContactPerson() {
        return primaryContactPerson;
    }

    public void setPrimaryContactPerson(String primaryContactPerson) {
        this.primaryContactPerson = primaryContactPerson;
    }

    public WrapAccountDetailDto getWrapAccountDetail() {
        return wrapAccountDetail;
    }

    public void setWrapAccountDetail(WrapAccountDetailDto wrapAccountDetail) {
        this.wrapAccountDetail = wrapAccountDetail;
    }

    public String getPpId() {
        return ppId;
    }

    public String getPpIdFromAvaloq() {
        return ppIdFromAvaloq;
    }

    public void setPpIdFromAvaloq(String ppIdFromAvaloq) {
        this.ppIdFromAvaloq = ppIdFromAvaloq;
    }

    public void setPpId(String ppId) {
        this.ppId = ppId;
    }

    public boolean isWestpacLive() {
        return westpacLive;
    }

    public void setWestpacLive(boolean westpacLive) {
        this.westpacLive = westpacLive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCanCreateAccount() {
        return canCreateAccount;
    }

    public void setCanCreateAccount(String canCreateAccount) {
        this.canCreateAccount = canCreateAccount;
    }

    public String getCanPerformAction() {
        return canPerformAction;
    }

    public void setCanPerformAction(String canPerformAction) {
        this.canPerformAction = canPerformAction;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(String salaryId) {
        this.salaryId = salaryId;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getDealerGroupList() {
        return dealerGroupList;
    }

    public void setDealerGroupList(Set<String> dealerGroupList) {
        this.dealerGroupList = dealerGroupList;
    }

    public String getPrimaryMobileNumber() {
        return primaryMobileNumber;
    }

    public void setPrimaryMobileNumber(String primaryMobileNumber) {
        this.primaryMobileNumber = primaryMobileNumber;
    }

    public String getRegisteredSince() {
        return registeredSince;
    }

    public void setRegisteredSince(String registeredSince) {
        this.registeredSince = registeredSince;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecodryEmail() {
        return secodryEmail;
    }

    public void setSecodryEmail(String secodryEmail) {
        this.secodryEmail = secodryEmail;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public AddressModel getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(AddressModel residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public AddressModel getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(AddressModel postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public List<Email> getEmail() {
        return email;
    }

    public void setEmail(List<Email> email) {
        this.email = email;
    }

    public List<Phone> getPhone() {
        return phone;
    }

    public void setPhone(List<Phone> phone) {
        this.phone = phone;
    }

    public List<IntermediariesModel> getIntermediaries() {
        return intermediaries;
    }

    public void setIntermediaries(List<IntermediariesModel> intermediaries) {
        this.intermediaries = intermediaries;
    }

    public List<ClientModel> getClients() {
        return clients;
    }

    public void setClients(List<ClientModel> clients) {
        this.clients = clients;
    }

    public List<WrapAccountModel> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<WrapAccountModel> accounts) {
        this.accounts = accounts;
    }

    public List<Phone> getMobilePhones() {
        return mobilePhones;
    }

    public void setMobilePhones(List<Phone> mobilePhones) {
        this.mobilePhones = mobilePhones;
    }

    public EncodedString getClientId() {
        return clientId;
    }

    public void setClientId(EncodedString clientId) {
        this.clientId = clientId;
    }

    public Map<Action, String> getActionValues() {
        return actionValues;
    }

    public void setActionValues(Map<Action, String> actionValues) {
        this.actionValues = actionValues;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getSafiDeviceId() {
        return safiDeviceId;
    }

    public void setSafiDeviceId(String safiDeviceId) {
        this.safiDeviceId = safiDeviceId;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public boolean isTerminatedFlag() {
        return terminatedFlag;
    }

    public void setTerminatedFlag(boolean terminatedFlag) {
        this.terminatedFlag = terminatedFlag;
    }

    public UserAccountStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(UserAccountStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isAvaloqStatusReg() {
        return avaloqStatusReg;
    }

    public void setAvaloqStatusReg(boolean avaloqStatusReg) {
        this.avaloqStatusReg = avaloqStatusReg;
    }

    public List<JobProfile> getJobProfiles() {
        return jobProfiles;
    }

    public void setJobProfiles(List<JobProfile> jobProfiles) {
        this.jobProfiles = jobProfiles;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public boolean isAdviserFlag() {
        return adviserFlag;
    }

    public void setAdviserFlag(boolean adviserFlag) {
        this.adviserFlag = adviserFlag;
    }

    public boolean isParaPlannerFlag() {
        return paraPlannerFlag;
    }

    public void setParaPlannerFlag(boolean paraPlannerFlag) {
        this.paraPlannerFlag = paraPlannerFlag;
    }

    /**
     * @return the onboardingStatus
     */
    public OnboardingPartyDisplayStatus getOnboardingStatus() {
        return onboardingStatus;
    }

    /**
     * @param onboardingStatus the onboardingStatus to set
     */
    public void setOnboardingStatus(OnboardingPartyDisplayStatus onboardingStatus) {
        this.onboardingStatus = onboardingStatus;
    }

    /**
     * @return the onboardingFailureReason
     */
    public String getOnboardingFailureReason() {
        return onboardingFailureReason;
    }

    /**
     * @param onboardingFailureReason the onboardingFailureReason to set
     */
    public void setOnboardingFailureReason(String onboardingFailureReason) {
        this.onboardingFailureReason = StringUtil.isNotNullorEmpty(onboardingFailureReason) ? onboardingFailureReason : Constants.EMPTY_STRING;
    }

    public String getInformationMessage() {
        return informationMessage;
    }

    public void setInformationMessage(String informationMessage) {
        this.informationMessage = informationMessage;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCisId() {
        return cisId;
    }

    public void setCisId(String cisId) {
        this.cisId = cisId;
    }

    public String getWestpacCustomerNumber() {
        return westpacCustomerNumber;
    }

    public void setWestpacCustomerNumber(String westpacCustomerNumber) {
        this.westpacCustomerNumber = westpacCustomerNumber;
    }

    public boolean isWib() {
        return wib;
    }

    public void setWib(boolean wib) {
        this.wib = wib;
    }

    public boolean isDirectInvestorFlag() {
        return directInvestorFlag;
    }

    public void setDirectInvestorFlag(boolean directInvestorFlag) {
        this.directInvestorFlag = directInvestorFlag;
    }

    public boolean isMigratedCustomer() {
        return migratedCustomer;
    }

    public void setMigratedCustomer(boolean migratedCustomer) {
        this.migratedCustomer = migratedCustomer;
    }


    public String getResolvedPref() {
        return resolvedPref;
    }

    public boolean isMandatoryDetailMissing() {
        return mandatoryDetailMissing;
    }

    public void setMandatoryDetailMissing(boolean mandatoryDetailMissing) {
        this.mandatoryDetailMissing = mandatoryDetailMissing;
    }



    public void setResolvedPref(String resolvedPref) {
        this.resolvedPref = resolvedPref;
    }

    public String getCmaStatementPref() {
        return cmaStatementPref;
    }

    public void setCmaStatementPref(String cmaStatementPref) {
        this.cmaStatementPref = cmaStatementPref;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
