package com.bt.nextgen.api.profile.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.profile.model.AccountCategory;
import com.bt.nextgen.api.profile.model.HomePageEnum;
import com.bt.nextgen.api.profile.model.InvestorTncDetails;
import com.bt.nextgen.api.profile.model.InvestorTncDetailsImpl;
import com.bt.nextgen.api.profile.model.ProfileDetailsDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;

/**
 * Use package com.bt.nextgen.api.profile.v1.service.InvestorHomepageEvaluatorImpl
 */
@Deprecated
@SuppressWarnings({"squid:S1200", "squid:S1226"})
@Service("investorHomepageEvaluator")
public class InvestorHomepageEvaluatorImpl implements InvestorHomepageEvaluator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvestorHomepageEvaluatorImpl.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private AccActivationIntegrationService activationService;

    @Autowired
    private UserRoleTermsAndConditionsRepository userRoleTermsAndConditionsRepository;

    @Autowired
    private BrokerHelperService brokerHelperService;

    public void setInvestorHomepageDetails(ProfileDetailsDto profile, Map<AccountKey, WrapAccount> accountMap,
                                           ServiceErrors serviceErrors) {
        if (accountMap.isEmpty()) {
            LOGGER.error("No accounts found for this client.");
            profile.setHomePage(HomePageEnum.ERROR_PAGE.toString());
        } else {
            LOGGER.info("Checking user's account and T&C's status");
            List<ApplicationDocument> applications = getApplicationDocuments(accountMap.keySet(), serviceErrors);
            UserProfile activeProfile = profileService.getActiveProfile();
            getLegacyApplicationDocument(applications, accountMap, activeProfile);

            if (areAllApplicationsWithdrawn(applications)) {
                profile.setHomePage(HomePageEnum.WITHDRAWN.toString());
            } else {
                List<ApplicationDocument> doneAndPendingApplications = filterAndSortApplications(applications);
                profile.setHasMultipleAccounts(doneAndPendingApplications.size() > 1);
                if (doneAndPendingApplications.size() > 1) {
                    profile.setCanNavigate(true);
                    profile.setHomePage(HomePageEnum.CLIENT_ACCOUNT_OVERVIEW.toString());
                } else {
                    setHomePageForInvestorWithSingleAccount(profile, doneAndPendingApplications, serviceErrors, activeProfile, accountMap);
                }
            }
            // If we have an account id, need to verify and update the home page.
            if (profile.getAccountId() != null) {
                WrapAccount account = Lambda.selectFirst(accountMap, Lambda.having(Lambda.on(WrapAccount.class).getAccountKey().getId(),
                    Matchers.is(EncodedString.toPlainText(profile.getAccountId()))));
                profile.setAccountCategory(AccountCategory.ADVISED.toString());
                updateHomePage(profile, account, serviceErrors);
            }
        }
    }

    private List<ApplicationDocument> getApplicationDocuments(Set<AccountKey> accountKeys, ServiceErrors serviceErrors) {
        LOGGER.info("Retrieving user's list of application documents");
        List<ApplicationDocument> applications = activationService
            .loadAccApplicationForPortfolio(getAccountIdList(accountKeys),profileService.getActiveProfile().getJobRole(),profileService.getActiveProfile().getClientKey(), serviceErrors);

        if (CollectionUtils.isEmpty(applications)) {
            LOGGER.info("No application documents found when filtered by client");
            applications = getApplicationDocumentsWithNoFilter(accountKeys, serviceErrors);
        }

        LOGGER.info("Found {} application document(s)", applications.size());
        return applications;
    }

    /**
     * This retrieves all applications related to the investor's list of bp id's, even if the client isn't listed in the application
     * document. This scenario happens when an investor is added to an account, after it has alredy become active, in which case, they don't
     * get retroactively added to the corresponding application document.
     *
     * @param accountKeys
     * @param serviceErrors
     * @return
     */
    private List<ApplicationDocument> getApplicationDocumentsWithNoFilter(Set<AccountKey> accountKeys, ServiceErrors serviceErrors) {
        LOGGER.info("Retrieving list of application documents with no client key filtering");
        List<ApplicationDocument> applications = activationService
            .loadAccApplicationForPortfolioWithNoFilter(getAccountIdList(accountKeys), serviceErrors);
        LOGGER.info("Found {} application document(s)", applications.size());
        return applications;
    }

    private InvestorTncDetails getInvestorTncDetails(List<ApplicationDocument> applicationDocuments, UserProfile activeProfile,
                                                     Map<AccountKey, WrapAccount> accountList) {
        // filter out withdrawn applications
        accountList = removeWithdrawnApplications(accountList);
        InvestorTncDetails investorTncDetails = new InvestorTncDetailsImpl();
        UserRoleTermsAndConditions userRoleTncs = getUserRoleTncs(activeProfile);
        if (hasAcceptedTncs(userRoleTncs)) {
            investorTncDetails.setAcceptedTnc(true);
        } else {
            ApplicationDocument firstApp = getFirstSubmittedApp(applicationDocuments, accountList.keySet());
            // No application that is Done or Pending accept
            if (firstApp == null) {
                investorTncDetails.setAcceptedTnc(true);
            } else {
                setAccountApproverDetails(investorTncDetails, firstApp, activeProfile);
            }
            // Add user if not found in personalisation DB (may not be there in test envs)
            updateUserTncDetails(userRoleTncs, investorTncDetails, activeProfile);
        }
        return investorTncDetails;
    }

    private boolean hasAcceptedTncs(UserRoleTermsAndConditions userRoleTncs) {
        return userRoleTncs != null && userRoleTncs.getTncAcceptedOn() != null;
    }

    private UserRoleTermsAndConditions getUserRoleTncs(UserProfile activeProfile) {
        UserRoleTermsAndConditionsKey userRoleKey = new UserRoleTermsAndConditionsKey(activeProfile.getBankReferenceId(),
            activeProfile.getProfileId());
        return userRoleTermsAndConditionsRepository.find(userRoleKey);
    }

    private Map<AccountKey, WrapAccount> removeWithdrawnApplications(Map<AccountKey, WrapAccount> accountList) {
        Map<AccountKey, WrapAccount> accountListWithoutWithdrawn = new HashMap<>();
        for (Map.Entry<AccountKey, WrapAccount> accountEntry : accountList.entrySet()) {
            if (!AccountStatus.DISCARD.equals(accountEntry.getValue().getAccountStatus()) && !AccountStatus.COMPANY_SETUP_IN_PROGRESS
                .equals(accountEntry.getValue().getAccountStatus())) {
                accountListWithoutWithdrawn.put(accountEntry.getKey(), accountEntry.getValue());
            }
        }
        return accountListWithoutWithdrawn;
    }

    private boolean isAllAccountsActive(Collection<WrapAccount> accountList) {
        // Check if any accounts are in pending status
        List<WrapAccount> activeAccounts = filter(having(on(WrapAccount.class).getAccountStatus(),
            Matchers.anyOf(Matchers.equalTo(AccountStatus.ACTIVE), Matchers.equalTo(AccountStatus.CLOSE))), accountList);
        return accountList.size() == activeAccounts.size();
    }

    private ApplicationDocument getFirstSubmittedApp(List<ApplicationDocument> applications, Set<AccountKey> accountList) {
        List<ApplicationDocument> applicationDocuments = removeWithdrawnAccApplications(applications, accountList);
        List<ApplicationDocument> validApps = Lambda.sort(filter(having(on(ApplicationDocument.class).getAccountStatus(),
            Matchers.anyOf(Matchers.equalTo(AccountStatus.ACTIVE), Matchers.equalTo(AccountStatus.CLOSE),
                Matchers.equalTo(AccountStatus.PEND_OPN), Matchers.equalTo(AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS),
                Matchers.equalTo(AccountStatus.FUND_ESTABLISHMENT_PENDING))),
            applicationDocuments), on(ApplicationDocument.class).getAppSubmitDate());
        return CollectionUtils.isNotEmpty(validApps) ? validApps.get(0) : null;
    }

    private List<ApplicationDocument> removeWithdrawnAccApplications(List<ApplicationDocument> applications, Set<AccountKey> accountList) {
        List<ApplicationDocument> applicationDocuments = new ArrayList<>();
        List<ApplicationDocument> applicationsWithPortfolios = filter(
            having(on(ApplicationDocument.class).getPortfolio(), Matchers.notNullValue()), applications);
        for (ApplicationDocument applicationDocument : applicationsWithPortfolios) {
            for (LinkedPortfolioDetails portfolio : applicationDocument.getPortfolio()) {
                for (AccountKey accountKey : accountList) {
                    if (portfolio.getPortfolioId().equals(accountKey.getId())) {
                        applicationDocuments.add(applicationDocument);
                    }
                }
            }
        }
        return applicationDocuments;
    }

    private void setAccountApproverDetails(InvestorTncDetails userTncDetails, ApplicationDocument firstSubmittedApp,
                                           UserProfile activeProfile) {
        //Offline account types, require first time investors to accept non-approver T&Cs, so we have to ignore their offline T&C sign date below
        if (ApprovalType.ONLINE == firstSubmittedApp.getApprovalType()
            || firstSubmittedApp.getAccountStatus() == AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS) {
            for (AssociatedPerson user : firstSubmittedApp.getPersonDetails()) {
                if (user.getClientKey().equals(activeProfile.getClientKey())) {
                    setInvestorTncDetails(userTncDetails, user);
                    break;
                }
            }
        } else {
            LOGGER.info("Application has been approved via offline approval, redirecting first time investor to non-approver T&Cs");
        }
    }

    private void setInvestorTncDetails(InvestorTncDetails userTncDetails, AssociatedPerson user) {
        userTncDetails.setApprover(user.isHasToAcceptTnC());
        if (userTncDetails.isApprover()) {
            userTncDetails.setAcceptedTnc(user.isHasApprovedTnC());
            userTncDetails.setTncSignDate(user.getTncSignDate());
        }
    }

    private void updateUserTncDetails(UserRoleTermsAndConditions userRoleTncs, InvestorTncDetails userTncDetails,
                                      UserProfile activeProfile) {
        if (userRoleTncs == null) {
            UserRoleTermsAndConditionsKey userRoleKey = new UserRoleTermsAndConditionsKey(activeProfile.getBankReferenceId(),
                activeProfile.getProfileId());
            userRoleTncs = new UserRoleTermsAndConditions();
            userRoleTncs.setUserRoleTermsAndConditionsKey(userRoleKey);
            userRoleTncs.setVersion(1);
        }
        userRoleTncs.setTncAccepted(userTncDetails.isAcceptedTnc() ? "Y" : "N");
        userRoleTncs.setTncAcceptedOn(userTncDetails.getTncSignDate());
        userRoleTncs.setModifyDatetime(new Date());
        userRoleTermsAndConditionsRepository.save(userRoleTncs);
    }

    private void getLegacyApplicationDocument(List<ApplicationDocument> applications, Map<AccountKey, WrapAccount> accountMap,
                                              UserProfile activeProfile) {
        Set<AccountKey> accountKey = accountMap.keySet();
        WrapAccount activeAccount;
        boolean isAppDocumentPresent;
        for (AccountKey key : accountKey) {
            activeAccount = accountMap.get(key);
            isAppDocumentPresent = false;
            for (ApplicationDocument appDocument : applications) {
                if (key.getId().equals(appDocument.getBpid().getId())) {
                    isAppDocumentPresent = true;
                }
                if (isAppDocumentPresent) {
                    break;
                }
            }
            if (!isAppDocumentPresent) {
                loadLegacyApplicationDocument(activeAccount, applications, activeProfile);
            }
        }
    }

    private void loadLegacyApplicationDocument(WrapAccount activeAccount, List<ApplicationDocument> applications,
                                               UserProfile activeProfile) {
        LOGGER.info("No application document found, creating a dummy document for account: {}", activeAccount.getAccountKey().getId());
        ApplicationDocument legacyApplicationDocument = new ApplicationDocumentImpl();
        legacyApplicationDocument.setBpid(activeAccount.getAccountKey());
        legacyApplicationDocument.setAppState(ApplicationStatus.DONE);
        AssociatedPerson associatedPersonImpl = new AssociatedPersonImpl();
        associatedPersonImpl.setClientKey(activeProfile.getClientKey());
        associatedPersonImpl.setHasToAcceptTnC(true);
        associatedPersonImpl.setHasApprovedTnC(true);
        associatedPersonImpl.setTncSignDate(new Date());
        List<AssociatedPerson> personDetails = new ArrayList<>();
        personDetails.add(associatedPersonImpl);
        legacyApplicationDocument.setPersonDetails(personDetails);
        applications.add(legacyApplicationDocument);
    }

    private void setHomePageForInvestorWithSingleAccount(ProfileDetailsDto profile, List<ApplicationDocument> doneAndPendingApplications,
                                                         ServiceErrors serviceErrors, UserProfile activeProfile,
                                                         Map<AccountKey, WrapAccount> accountMap) {
        InvestorTncDetails tncDetails = getInvestorTncDetails(doneAndPendingApplications, activeProfile, accountMap);
        AccountKey accountKey = doneAndPendingApplications.iterator().next().getBpid();
        profile.setAccountId(EncodedString.fromPlainText(accountKey.getId()).toString());
        if (isAllAccountsActive(accountMap.values())) {
            if (!tncDetails.isApprover() && !tncDetails.isAcceptedTnc()) {
                profile.setHomePage(HomePageEnum.NON_APPROVER_TNCS.toString());
            } else {
                setPortfolioValuationPage(profile);
            }
        } else {
            WrapAccountDetail updatedAccount = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
            if (updatedAccount.getAccountStatus() == AccountStatus.ACTIVE || updatedAccount.getAccountStatus() == AccountStatus.CLOSE) {
                setPortfolioValuationPage(profile);
            } else {
                setHomePageForSinglePendingAccount(profile, doneAndPendingApplications, tncDetails);
            }
        }
    }

    private void setHomePageForSinglePendingAccount(ProfileDetailsDto profile, List<ApplicationDocument> doneAndPendingApplications,
                                                    InvestorTncDetails tncDetails) {
        if (tncDetails.isAcceptedTnc()) {
            profile.setHomePage(HomePageEnum.HOLDING.toString());
        } else if (tncDetails.isApprover()) {
            profile.setAccountId(EncodedString.fromPlainText(getPendingAccountId(doneAndPendingApplications)).toString());
            profile.setHomePage(HomePageEnum.ACCOUNT_APPROVAL.toString());
        } else {
            profile.setHomePage(HomePageEnum.NON_APPROVER_TNCS.toString());
        }
    }

    private void setPortfolioValuationPage(ProfileDetailsDto profile) {
        profile.setCanNavigate(true);
        profile.setHomePage(HomePageEnum.PORTFOLIO_VALUATION.toString());
    }

    private List<ApplicationDocument> filterApplicationsByStatus(List<ApplicationDocument> applications, ApplicationStatus... statuses) {
        return Lambda.filter(Lambda.having(Lambda.on(ApplicationDocument.class).getAppState(), Matchers.isIn(statuses)), applications);
    }

    private List<ApplicationDocument> filterAndSortApplications(List<ApplicationDocument> applications) {
        List<ApplicationDocument> filteredApplications = filterByOfflinePending(filterByApplicationStatus(applications));
        return Lambda.sort(filteredApplications, Lambda.on(ApplicationDocument.class).getAppSubmitDate());
    }

    /**
     * Applications which are offline approvals, will only be shown to the investor after the approval documents have been uploaded.
     * This is when the account status is active, closed or in fund establishment.
     *
     * @param applications
     * @return
     */
    private List<ApplicationDocument> filterByOfflinePending(List<ApplicationDocument> applications) {
        final List<AccountStatus> accountStatusList = Arrays.asList(AccountStatus.ACTIVE, AccountStatus.CLOSE,
            AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS);

        Iterator<ApplicationDocument> iterator = applications.iterator();
        while (iterator.hasNext()) {
            ApplicationDocument document = iterator.next();
            if (ApprovalType.OFFLINE == document.getApprovalType() && !accountStatusList.contains(document.getAccountStatus())) {
                iterator.remove();
            }
        }

        LOGGER.info("Found {} applications after filtering by application status and approval type.", applications.size());
        return applications;
    }

    private List<ApplicationDocument> filterByApplicationStatus(List<ApplicationDocument> applications) {
        final List statusList = new LinkedList<>(Arrays.asList(ApplicationStatus.values()));
        statusList.remove(ApplicationStatus.DISCARDED);
        statusList.remove(ApplicationStatus.REJECT_INVSTR);
        statusList.remove(ApplicationStatus.ASIC_REGISTRATION);
        statusList.remove(ApplicationStatus.ASIC_SUBMISSION);
        return Lambda.filter(Lambda.having(Lambda.on(ApplicationDocument.class).getAppState(), Matchers.isIn(statusList)), applications);
    }

    private boolean areAllApplicationsWithdrawn(List<ApplicationDocument> applications) {
        List<ApplicationDocument> withdrawnApplications = filterApplicationsByStatus(applications, ApplicationStatus.DISCARDED);
        return applications.size() == withdrawnApplications.size() && !applications.isEmpty();
    }

    private List<WrapAccountIdentifier> getAccountIdList(Set<AccountKey> accountList) {
        List<WrapAccountIdentifier> accountIdList = new ArrayList<>();
        for (AccountKey account : accountList) {
            WrapAccountIdentifier wrapAccId = new WrapAccountIdentifierImpl();
            wrapAccId.setBpId(account.getId());
            accountIdList.add(wrapAccId);
        }
        return accountIdList;
    }

    private String getPendingAccountId(List<ApplicationDocument> validApps) {
        ApplicationDocument app = selectFirst(validApps,
            having(on(ApplicationDocument.class).getAppState(), Matchers.equalTo(ApplicationStatus.PEND_ACCEPT)));
        return app != null ? app.getPortfolio().get(0).getPortfolioId() : "";
    }

    /**
     * Updates the home page based on the account id in the profile(to direct or IP )
     *
     * @param profile
     * @param serviceErrors
     */
    private void updateHomePage(ProfileDetailsDto profile, WrapAccount account, ServiceErrors serviceErrors) {
        // If user has accessed an account before, redirect to PV or investor experience with that account id
        if (isDirectInvestor(account, serviceErrors)) {
            profile.setAccountCategory(AccountCategory.DIRECT.toString());
            profile.setHomePage(HomePageEnum.DIRECT_ONLY.toString());
        } else {
            // If user has a pre-visited any account, direct to PV
            if (HomePageEnum.CLIENT_ACCOUNT_OVERVIEW.toString().equals(profile.getHomePage())) {
                profile.setHomePage(HomePageEnum.PORTFOLIO_VALUATION.toString());
            }
        }
    }

    private boolean isDirectInvestor(WrapAccount account, ServiceErrors serviceErrors) {
        // TODO: update the logic after update to the BP DETAILS service.
        LOGGER.info("Checking user's account is direct or not");
        return brokerHelperService.isDirectInvestor(account, serviceErrors);
    }
}
