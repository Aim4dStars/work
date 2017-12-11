package com.bt.nextgen.api.subscriptions.service;

import com.bt.nextgen.core.repository.SubscriptionDetails;
import com.bt.nextgen.core.repository.SubscriptionStatus;
import com.bt.nextgen.core.repository.SubscriptionsRepository;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubscriptionDtoServiceImplRunnable implements Runnable{

    private List<ApplicationDocument> applicationDocuments;

    private SubscriptionsRepository repository;

    protected SubscriptionDtoServiceImplRunnable(List<ApplicationDocument> applicationDocuments, SubscriptionsRepository repository) {
        this.applicationDocuments = applicationDocuments;
        this.repository = repository;
    }

    private void updateInDatabase() {
        for (ApplicationDocument applicationDocument : applicationDocuments) {
            SubscriptionDetails subscriptionsDetails = toSubscriptionDetail(applicationDocument);
            repository.update(subscriptionsDetails);
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        updateInDatabase();
    }

    private static SubscriptionDetails toSubscriptionDetail(ApplicationDocument applicationDocument) {
        SubscriptionDetails subscriptionsDetails = new SubscriptionDetails();
        subscriptionsDetails.setDocId(applicationDocument.getAppNumber());
        subscriptionsDetails.setStatus(getStatus(applicationDocument).name());
        subscriptionsDetails.setSubscriptionType(applicationDocument.getOrderType());
        subscriptionsDetails.setAccountId(applicationDocument.getBpid().getId());
        return subscriptionsDetails;
    }

    private static SubscriptionStatus getStatus(ApplicationDocument applicationDocument) {
        if (ApplicationStatus.RUN_CANCEL.equals(applicationDocument.getAppState()) ||
                ApplicationStatus.DISCARDED.equals(applicationDocument.getAppState())) {
            return SubscriptionStatus.CANCELLED;
        } else if (ApplicationStatus.DONE.equals(applicationDocument.getAppState())) {
            return SubscriptionStatus.SUBSCRIBED;
        } else
            return SubscriptionStatus.INPROGRESS;
    }

    public static void updateDatabase(List<ApplicationDocument> subscriptions, SubscriptionsRepository repository) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SubscriptionDtoServiceImplRunnable subscriptionDtoServiceImplRunnable = new SubscriptionDtoServiceImplRunnable(subscriptions, repository);
        executorService.execute(subscriptionDtoServiceImplRunnable);
        executorService.shutdown();
    }
}