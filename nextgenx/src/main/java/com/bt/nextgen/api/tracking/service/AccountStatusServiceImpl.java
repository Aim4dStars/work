package com.bt.nextgen.api.tracking.service;


import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus.DONE;

@Service
public class AccountStatusServiceImpl implements AccountStatusService {

    @Override
    public @Nullable OnboardingApplicationStatus getApplicationStatus(@Nonnull ClientApplication application, @Nonnull Map<String, ApplicationDocument> applicationDocumentMap) {
        final OnBoardingApplication onboardingApplication = application.getOnboardingApplication();
        if (onboardingApplication != null) {
            if (isDiscarded(onboardingApplication, applicationDocumentMap)) {
                return OnboardingApplicationStatus.withdrawn;
            }
            final OnboardingApplicationStatus status = onboardingApplication.getStatus();
            if (status != null) {
                switch (status) {
                    case ServerFailure:
                    case ApplicationCreationInProgress:
                    case PartyCreationInProgress:
                        return OnboardingApplicationStatus.processing;
                    case ApplicationCreationFailed:
                    case PartyCreationFailed:
                        return OnboardingApplicationStatus.failed;
                    default:
                        return status;
                }
            } else {
                return null;
            }
        } else {
            return OnboardingApplicationStatus.convertFromClientApplicationStatus(application.getStatus());
        }
    }

    private boolean isDiscarded(OnBoardingApplication onboardingApplication, Map<String, ApplicationDocument> applicationDocumentMap) {
        final ApplicationDocument document = applicationDocumentMap.get(onboardingApplication.getAvaloqOrderId());
        if (document != null) {
            return ApplicationStatus.DISCARDED.equals(document.getAppState());
        }
        return false;
    }

    @Override
    public @Nonnull OnboardingApplicationStatus getAccountStatusByInvestorsStatuses(@Nonnull List<TrackingDto.Investor> investors) {
        final int count = investors.size();
        if (count == 0) {
            return OnboardingApplicationStatus.processing;
        }
        int approved = 0;
        int awaitingApproval = 0;
        int notRegistered = 0;
        for (TrackingDto.Investor investor : investors) {
            final ApplicationClientStatus status = investor.getStatus();
            if (ApplicationClientStatus.APPROVED.equals(status)) {
                approved++;
            } else if (ApplicationClientStatus.FAILED_EMAIL.equals(status)) {
                // If any approvers have a failed email status, then the account status is the same
                return OnboardingApplicationStatus.failedEmail;
            } else if (ApplicationClientStatus.AWAITING_APPROVAL.equals(status)) {
                awaitingApproval++;
            } else if (ApplicationClientStatus.NOT_REGISTERED.equals(status)) {
                notRegistered++;
            }
        }

        if (approved == count) {
            // All approvers have approved, so the account should be active.
            return OnboardingApplicationStatus.active;
        } else if (approved + awaitingApproval + notRegistered == count) {
            // No technical failures, just some approvers are yet to approve/register
            return OnboardingApplicationStatus.awaitingApproval;
        }
        // Some manner of technical failure(s) which are hopefully being looked at, so cover it up with the
        // generic "processing" status
        return OnboardingApplicationStatus.processing;
    }

    @Override
    /**
     * Check all investors have approved and account is set up for Fund Establishment Accounts then only return as Active.
     */
    public @Nonnull OnboardingApplicationStatus getStatusForAccountType(OnboardingApplicationStatus accountStatus,
            @Nonnull final ClientApplication application,
            @Nonnull final Map<String, ApplicationDocument> applicationDocumentMap) {

        AccountType accountType = application.getClientApplicationForm().getAccountType();
        OnBoardingApplication onboardingApplication = application.getOnboardingApplication();
        ApplicationDocument applicationDocument = applicationDocumentMap.get(onboardingApplication.getAvaloqOrderId());

        accountStatus = getStatusForNewSMSF(accountStatus, application, accountType, applicationDocument);
        return getStatusForOffline(accountStatus, onboardingApplication,applicationDocument,application );
    }

    private OnboardingApplicationStatus getStatusForNewSMSF(final OnboardingApplicationStatus accountStatus,
                                       final ClientApplication application,
                                       final AccountType accountType,
                                       ApplicationDocument applicationDocument) {
        if (AccountType.NEW_CORPORATE_SMSF.equals(accountType) || AccountType.NEW_INDIVIDUAL_SMSF.equals(accountType)) {
            if (accountStatus.equals(OnboardingApplicationStatus.active)) {
                if (applicationDocument.getAppState().equals(DONE)) {
                    application.markActive();
                    return OnboardingApplicationStatus.active;
                } else {
                    return OnboardingApplicationStatus.smsfinProgress;
                }
            } else if (checkDocumentNotNull(applicationDocument) && isApplicableNewCorporateSmsfAccount(accountType, accountStatus)) {
                return getAccountStatusForNewCorporate(applicationDocument.getAppState());
            }
        }
        return accountStatus;
    }

    private OnboardingApplicationStatus getStatusForOffline(OnboardingApplicationStatus accountStatus,
                                                            OnBoardingApplication onboardingApplication,
                                                            ApplicationDocument applicationDocument, ClientApplication clientApplication) {
        if (checkDocumentNotNull(applicationDocument) && onboardingApplication.isOffline()) {
            switch (applicationDocument.getAppState()) {
                case PEND_ACCEPT:
                    if(ClientApplicationStatus.docuploaded.equals(clientApplication.getStatus()))
                        return OnboardingApplicationStatus.offlineDocUpload;
                    else
                        return OnboardingApplicationStatus.awaitingApprovalOffline;
                case DONE:
                    clientApplication.markActive();
                    return OnboardingApplicationStatus.active;
                default:
                    return accountStatus;
            }
        }
        return accountStatus;
    }

    private boolean isApplicableNewCorporateSmsfAccount(@Nonnull final AccountType accountType,
            @Nonnull final OnboardingApplicationStatus accountStatus) {
        return AccountType.NEW_CORPORATE_SMSF.equals(accountType)
                && (OnboardingApplicationStatus.processing.equals(accountStatus) || OnboardingApplicationStatus.awaitingApproval.equals(accountStatus));
    }

    private boolean checkDocumentNotNull(@Nullable final ApplicationDocument document) {
        return null != document && null != document.getAppState();
    }

    private OnboardingApplicationStatus getAccountStatusForNewCorporate(@Nonnull final ApplicationStatus avaloqAppState) {
        switch(avaloqAppState){
            case ASIC_REGISTRATION:
            case ASIC_SUBMISSION:
                return OnboardingApplicationStatus.smsfcorporateinProgress;
            case PEND_ACCEPT:
                return OnboardingApplicationStatus.awaitingApproval;
            default:
                return OnboardingApplicationStatus.processing;
        }
    }
}
