package com.bt.nextgen.core.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@TransactionConfiguration(defaultRollback = true)
public class OnboardingCommunicationRepositoryImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private OnboardingCommunicationRepository communicationRepository;

    @Autowired
    private OnboardingApplicationRepository applicationRepository;

    @Autowired
    private OnboardingPartyRepository partyRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @Transactional
    @Rollback(true)
    public void loadedOnboardingCommunicationShouldHaveSameAttributeValuesAsPersistedOne() throws Exception {

        String myGcmPan = "TEST_GCM";
        Date currentTime = new Date();

        OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
        applicationRepository.save(application);

        OnboardingParty party = new OnboardingParty(1,application.getKey().getId(),myGcmPan);
        partyRepository.save(party);

        OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId("TEST_COMM_ID");
        communication.setOnboardingApplicationId(application.getKey().getId());
        communication.setGcmPan(myGcmPan);
        communication.setEmailAddress("Test@email.com");

        communication.setCommunicationInitiationTime(currentTime);
        communication.setCreatedDate(currentTime);
        communicationRepository.save(communication);
        communicationRepository.flush();

        OnboardingCommunication searchedCommunication = communicationRepository.find(communication.getCommunicationId());
        assertThat(searchedCommunication.getOnboardingApplicationId(), is(application.getKey().getId()));
        assertThat(searchedCommunication.getEmailAddress(), is("Test@email.com"));
        assertThat(searchedCommunication.getCreatedDate(), is(currentTime));
        assertThat(searchedCommunication.getCommunicationInitiationTime(), is(currentTime));
        assertNotNull(searchedCommunication.getLastModifiedDate());
        assertThat(communication.toString(), containsString("trackingId=null, createdDate="));
        assertThat(communication.toString(), containsString(", failureMessage=null, communicationId=TEST_COMM_ID, onboardingApplicationId="));
        assertThat(communication.toString(), containsString(", status=null, lastModifiedDate="));
        assertThat(communication.toString(), containsString(", lastModifiedId=null"));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void shouldReturnAllCommunicationAssociatedWithGivenApplicationId() throws Exception {
        String myGcmPan = "TEST_GCM";
        Date currentTime = new Date();

        OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
        applicationRepository.save(application);

        OnboardingParty party = new OnboardingParty(1,application.getKey().getId(),myGcmPan);
        partyRepository.save(party);

        OnboardingCommunication communication1 = new OnboardingCommunication();
        communication1.setCommunicationId("TEST_COMM_ID_1");
        communication1.setOnboardingApplicationId(application.getKey().getId());
        communication1.setGcmPan(myGcmPan);
        communication1.setEmailAddress("Test1@email.com");

        communication1.setCommunicationInitiationTime(currentTime);
        communication1.setCreatedDate(currentTime);
        communicationRepository.save(communication1);

        OnboardingCommunication communication2 = new OnboardingCommunication();
        communication2.setCommunicationId("TEST_COMM_ID_2");
        communication2.setOnboardingApplicationId(application.getKey().getId());
        communication2.setGcmPan(myGcmPan);
        communication2.setEmailAddress("Test2@email.com");

        communication2.setCommunicationInitiationTime(currentTime);
        communication2.setCreatedDate(currentTime);
        communicationRepository.save(communication2);
        communicationRepository.flush();

        List<OnboardingCommunication> communications = communicationRepository.findByApplicationIdAndGCMId(application.getKey().getId(),myGcmPan);
        assertThat(communications.size(),is(2));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void shouldThrowEntityExistsExceptionWhilePersistingExistingCommunicationId() {
        OnboardingCommunication communicationOne = new OnboardingCommunication();
        communicationOne.setCommunicationId("TEST_123");
        communicationRepository.save(communicationOne);

        OnboardingCommunication communicationTwo = new OnboardingCommunication();
        communicationTwo.setCommunicationId("TEST_123");

        exception.expect(EntityExistsException.class);
        communicationRepository.save(communicationTwo);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void shouldThrowConstraintExceptionWhilePersistingCommunicationWithApplicationIdMissing() {
        OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId("TEST_COMM_ID");
        communication.setGcmPan("TEST_GCM");
        communication.setEmailAddress("Test@email.com");

        Date currentTime = new Date();
        communication.setCommunicationInitiationTime(currentTime);
        communication.setCreatedDate(currentTime);

        exception.expect(PersistenceException.class);
        communicationRepository.save(communication);
        communicationRepository.flush();
    }

    @Transactional
    @Rollback(true)
    public void shouldThrowConstraintExceptionWhilePersistingCommunicationWithOnlyCommunicationId() {
        OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId("TEST_123");
        communicationRepository.save(communication);

        exception.expect(PersistenceException.class);
        communicationRepository.flush();
    }


    @Test
    @Transactional
    @Rollback(true)
    public void shouldThrowConstraintExceptionWhilePersistingCommunicationWithoutParty() {
        Date currentTime = new Date();
        OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId("TEST_COMM_ID");
        communication.setOnboardingApplicationId(123434L);
        communication.setGcmPan("TEST_GCM");
        communication.setEmailAddress("Test@email.com");
        communication.setCommunicationInitiationTime(currentTime);
        communication.setCreatedDate(currentTime);
        communicationRepository.save(communication);
        exception.expect(PersistenceException.class);
        communicationRepository.flush();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void shouldThrowConstraintExceptionWhilePersistingCommunicationWithoutEmailAddress() throws Exception {
        String myGcmPan = "TEST_GCM";
        Date currentTime = new Date();

        OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
        applicationRepository.save(application);

        OnboardingParty party = new OnboardingParty(1,application.getKey().getId(),myGcmPan);
        partyRepository.save(party);

        OnboardingCommunication communication = new OnboardingCommunication();
        communication.setCommunicationId("TEST_COMM_ID");
        communication.setOnboardingApplicationId(application.getKey().getId());
        communication.setGcmPan(myGcmPan);
        communication.setCommunicationInitiationTime(currentTime);
        communication.setCreatedDate(currentTime);

        exception.expect(PersistenceException.class);

        communicationRepository.save(communication);
        communicationRepository.flush();
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void shouldReturnAllCommunicationAssociatedWithGivenGcmId() throws Exception {
        String myGcmPan = "TEST_GCM";
        Date currentTime = new Date();

        OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
        applicationRepository.save(application);

        OnboardingParty party = new OnboardingParty(1,application.getKey().getId(),myGcmPan);
        partyRepository.save(party);

        OnboardingCommunication communication1 = new OnboardingCommunication();
        communication1.setCommunicationId("TEST_COMM_ID_1");
        communication1.setOnboardingApplicationId(application.getKey().getId());
        communication1.setGcmPan(myGcmPan);
        communication1.setEmailAddress("Test1@email.com");

        communication1.setCommunicationInitiationTime(currentTime);
        communication1.setCreatedDate(currentTime);
        communicationRepository.save(communication1);

        OnboardingCommunication communication2 = new OnboardingCommunication();
        communication2.setCommunicationId("TEST_COMM_ID_2");
        communication2.setOnboardingApplicationId(application.getKey().getId());
        communication2.setGcmPan(myGcmPan);
        communication2.setEmailAddress("Test2@email.com");

        communication2.setCommunicationInitiationTime(currentTime);
        communication2.setCreatedDate(currentTime);
        communicationRepository.save(communication2);
        communicationRepository.flush();

        List<OnboardingCommunication> communications = communicationRepository.findCommunicationsByGcmId(myGcmPan);
        assertThat(communications.size(),is(2));
    }
    
    

}
