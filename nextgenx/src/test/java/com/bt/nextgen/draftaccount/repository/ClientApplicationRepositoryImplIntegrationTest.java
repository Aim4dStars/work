package com.bt.nextgen.draftaccount.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.core.toggle.FeatureToggles.FILTER_DIRECT_ACCTS;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@TransactionConfiguration(defaultRollback = true)
public class ClientApplicationRepositoryImplIntegrationTest extends BaseSecureIntegrationTest
{
    public static final String DEFAULT_ADVISER_ID = "adviser-id";

    @Autowired
    ClientApplicationRepositoryImpl repository;

    @PersistenceContext
    EntityManager entityManager;

    FeatureTogglesService featureTogglesService;


    @Test
    @Transactional
    @Rollback(true)
    public void loadedDraftAccountShouldHaveSameAttributeValuesAsPersistedOne() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        DateTime date = DateTime.parse("2005-09-24T04:00:00");
        clientApplication.setAdviserPositionId(DEFAULT_ADVISER_ID);
        clientApplication.setLastModifiedAt(date);
        repository.save(clientApplication);

        assertNotNull(clientApplication.getId());

        ClientApplication retrieved = repository.find(clientApplication.getId(), getBrokerIdentifiers());
        assertEquals(DEFAULT_ADVISER_ID, retrieved.getAdviserPositionId());
        assertThat(retrieved.getId(), is(clientApplication.getId()));
        assertThat(retrieved.getLastModifiedAt(), is(clientApplication.getLastModifiedAt()));
    }

    private Collection<BrokerIdentifier> getBrokerIdentifiers() {
        BrokerIdentifierImpl brokerId = new BrokerIdentifierImpl();
        brokerId.setKey(BrokerKey.valueOf(DEFAULT_ADVISER_ID));
        return ImmutableList.of((BrokerIdentifier) brokerId).asList();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findShouldNotReturnDeletedRecords() throws Exception {
        BrokerIdentifierImpl brokerId = new BrokerIdentifierImpl();
        brokerId.setKey(BrokerKey.valueOf(DEFAULT_ADVISER_ID));
        List<BrokerIdentifier> ids = ImmutableList.of((BrokerIdentifier)brokerId).asList();

        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(DEFAULT_ADVISER_ID);
        Long draftAccountId = repository.save(clientApplication);
        clientApplication.markDeleted();

        try {
            repository.find(draftAccountId, ids);
            fail("Should have thrown an exception");
        } catch (NoResultException ex) {
            // did not find deleted record
        }
    }

    String directAccount = "{\n" +
            "   \"applicationOrigin\":\"WestpacLive\",\n" +
            "    }";
    String Account = "{\n" +
            "   \"accountType\": \"company\",\n" +
            "    }";

    @Test
    @Transactional
    @Rollback(true)
    public void shouldFilterDirectAccountsWhenToggledIsTrue() throws Exception {
        setDirectFilterToggle(true);
        repository.setTogglesService(featureTogglesService);
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        clientApplication.setFormData(directAccount);
        repository.save(clientApplication);

        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        clientApplication1.setFormData(Account);
        repository.save(clientApplication1);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(modifiedDate.minusDays(1).toDate(), modifiedDate.plus(1).toDate());
        assertThat(result.size(),is(1));
    }



    @Test
    @Transactional
    @Rollback(true)
    public void shouldNotFilterDirectAccountsWhenToggledIsFalse() throws Exception {
        setDirectFilterToggle(false);
        repository.setTogglesService(featureTogglesService);
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        clientApplication.setFormData(directAccount);
        repository.save(clientApplication);

        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        clientApplication1.setFormData(Account);
        repository.save(clientApplication1);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(modifiedDate.minusDays(1).toDate(), modifiedDate.plus(1).toDate());
        assertThat(result.size(),is(2));
    }

    private void setDirectFilterToggle(final Boolean flag) {
        featureTogglesService = new FeatureTogglesService() {
            @Override
            public FeatureToggles findOne(ServiceErrors serviceErrors) {
                FeatureToggles toggles = new FeatureToggles();
                toggles.setFeatureToggle(FILTER_DIRECT_ACCTS,flag);
                return toggles;
            }
        };
    }




    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldNotReturnDeletedRecords() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication deletedClientApplication =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        deletedClientApplication.markDeleted();
        repository.save(deletedClientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(modifiedDate.minusDays(1).toDate(), modifiedDate.plus(1).toDate(), getBrokerIdentifiers());
        assertThat(result, not(hasItem(deletedClientApplication)));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldNotReturnActiveRecords() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication activeClientApplication =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        activeClientApplication.markActive();
        repository.save(activeClientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(modifiedDate.minusDays(1).toDate(), modifiedDate.plus(1).toDate(), getBrokerIdentifiers());
        assertThat(result, not(hasItem(activeClientApplication)));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldNotFindDraftAccountsThatWereLastModifiedBeforeTheFromParameter() throws Exception {
        ClientApplication oldClientApplication =  createApplication(DEFAULT_ADVISER_ID, new DateTime(2001, 1, 1, 0, 0));

        repository.save(oldClientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(new DateTime(2011, 1, 1, 0, 0).toDate(), new DateTime().toDate(), getBrokerIdentifiers());
        assertThat(result, not(hasItem(oldClientApplication)));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldFindDraftAccountsThatWereLastModifiedBetweenTheFromAndToParameters() throws Exception {
        ClientApplication clientApplication = createApplication(DEFAULT_ADVISER_ID, new DateTime(2001, 1, 1, 0, 0));
        repository.save(clientApplication);
        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(new DateTime(2000, 1, 1, 0, 0).toDate(), new DateTime().toDate(), getBrokerIdentifiers());
        assertThat(result, hasItem(clientApplication));
            }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldNotFindDraftAccountsThatWereLastModifiedBetweenTheFromAndToParameters_ThatTheAdviserDoesntHavePermissionsTo() throws Exception {
        ClientApplication clientApplication =  createApplication("another-adviser-id", new DateTime(2001, 1, 1, 0, 0));
        repository.save(clientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(new DateTime(2000, 1, 1, 0, 0).toDate(), new DateTime().toDate(), getBrokerIdentifiers());
        assertThat(result, not(hasItem(clientApplication)));
        assertThat(result, empty());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldNotFindDraftAccountsThatWereLastModifiedAfterTheToParameter() throws Exception {
        ClientApplication newerClientApplication =  createApplication(DEFAULT_ADVISER_ID, new DateTime(2012, 1, 1, 0, 0));
        repository.save(newerClientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(new DateTime(2000, 1, 1, 0, 0).toDate(), new DateTime(2011, 1, 1, 0, 0).toDate(), getBrokerIdentifiers());
        assertThat(result, not(hasItem(newerClientApplication)));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldFindDraftAccountWithLastModifiedDateEqualsToToParameter() throws Exception {
        ClientApplication clientApplication =  createApplication(DEFAULT_ADVISER_ID, new DateTime(2012, 1, 1, 10, 12));
        repository.save(clientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(new DateTime(2000, 1, 1, 0, 0).toDate(), new DateTime(2012, 1, 1, 23, 59).toDate(), getBrokerIdentifiers());
        assertThat(result, hasItem(clientApplication));

    }

    @Test
    @Transactional
    @Rollback(true)
    public void findNonActiveApplicationsBetweenDates_ShouldFindDraftAccountWithLastModifiedDateEqualsToFromParameter() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication newerClientApplication = createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        repository.save(newerClientApplication);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(modifiedDate.toDate(), modifiedDate.plusDays(1).toDate(), getBrokerIdentifiers());
        assertThat(result, hasItem(newerClientApplication));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findAllBetweenDatesshouldReturnClientApplicationsOnlyForTheApplicableAdvisers() throws Exception {

        String adviserId2 = "ADVISER_ID_2";
        String adviserId3 = "ADVISER_ID_3";

        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication1 = createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        repository.save(clientApplication1);

        ClientApplication clientApplication2 = createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        repository.save(clientApplication2);

        ClientApplication clientApplication3 = createApplication(adviserId2, modifiedDate);
        repository.save(clientApplication3);

        ClientApplication clientApplication4 = createApplication(adviserId3, modifiedDate);
        repository.save(clientApplication4);

        List<ClientApplication> result = repository.findNonActiveApplicationsBetweenDates(modifiedDate.toDate(), modifiedDate.plusDays(1).toDate(), getBrokerIdentifiers());
        assertThat(result, hasSize(2));
        assertThat(result, hasItem(clientApplication1));
        assertThat(result, hasItem(clientApplication2));
        assertThat(result, not(hasItem(clientApplication3)));
        assertThat(result, not(hasItem(clientApplication4)));
    }

    private ClientApplication createApplication(String adviserPositionId, DateTime modifiedDate) {
        ClientApplication clientApplication1 = new ClientApplication();
        clientApplication1.setLastModifiedAt(modifiedDate);
        clientApplication1.setAdviserPositionId(adviserPositionId);
        clientApplication1.setOnboardingApplication(new OnBoardingApplication());
        return clientApplication1;
    }


    @Test
    @Transactional
    @Rollback(true)
    public void findShouldReturnSubmittedRecords() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        Collection<BrokerIdentifier> ids = getBrokerIdentifiers();
        String brokerId = ids.iterator().next().getKey().getId();
        clientApplication.setAdviserPositionId(brokerId);
        clientApplication.markSubmitted();

        Long draftAccountId = repository.save(clientApplication);
        ClientApplication retrieved = repository.find(draftAccountId, getBrokerIdentifiers());
        assertNotNull(retrieved);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findOnboardingApplicationByIdAndBrokerIdentifiers() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(DEFAULT_ADVISER_ID);
        clientApplication.setLastModifiedAt(DateTime.parse("2005-09-24T04:00:00"));

        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        entityManager.persist(onBoardingApplication);

        clientApplication.setOnboardingApplication(onBoardingApplication);

        repository.save(clientApplication);

        assertThat(repository.findByOnboardingApplicationKey(onBoardingApplication.getKey(), getBrokerIdentifiers()).getId(), equalTo(clientApplication.getId()));

    }

    @Test
    @Transactional
    @Rollback(true)
    public void findOnboardingApplicationById() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(DEFAULT_ADVISER_ID);
        clientApplication.setLastModifiedAt(DateTime.parse("2005-09-24T04:00:00"));

        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        entityManager.persist(onBoardingApplication);

        clientApplication.setOnboardingApplication(onBoardingApplication);

        repository.save(clientApplication);

        assertThat(repository.findByOnboardingApplicationKey(onBoardingApplication.getKey()).getId(), equalTo(clientApplication.getId()));

    }

    @Test
    @Transactional
    @Rollback(true)
    public void findCertainNumberOfLatestDraftAccounts_ShouldReturnOnlyCertainNumberOfRows() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(5));
        repository.save(clientApplication1);

        ClientApplication clientApplication2 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(2));
        repository.save(clientApplication2);

        ClientApplication clientApplication3 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        repository.save(clientApplication3);

        ClientApplication clientApplication4 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusHours(2));
        repository.save(clientApplication4);

        List<ClientApplication> result = repository.findCertainNumberOfMostRecentDraftAccounts(3, getBrokerIdentifiers());
        assertThat(result, not(hasItem(clientApplication1)));
        assertThat(result, hasItem(clientApplication2));
        assertThat(result, hasItem(clientApplication3));
        assertThat(result, hasItem(clientApplication4));
        assertThat(result, hasSize(3));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findCertainNumberOfLatestDraftAccounts_ShouldReturnLessThanCertainNumberOfRows() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(5));
        repository.save(clientApplication1);

        ClientApplication clientApplication2 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(2));
        repository.save(clientApplication2);

        List<ClientApplication> result = repository.findCertainNumberOfMostRecentDraftAccounts(3, getBrokerIdentifiers());
        assertThat(result, hasItem(clientApplication1));
        assertThat(result, hasItem(clientApplication2));
        assertThat(result, hasSize(2));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findCertainNumberOfLatestDraftAccounts_ShouldReturnEmptyListIfNoDraftApplications() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(5));
        clientApplication1.markSubmitted();

        repository.save(clientApplication1);

        List<ClientApplication> result = repository.findCertainNumberOfMostRecentDraftAccounts(3, getBrokerIdentifiers());
        assertThat(result, not(hasItem(clientApplication1)));
        assertThat(result, empty());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findCertainNumberOfLatestDraftAccounts_ShouldReturnOnlyDraftApplications() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(5));
        repository.save(clientApplication1);

        ClientApplication clientApplication2 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(2));
        clientApplication2.markDeleted();
        repository.save(clientApplication2);

        ClientApplication clientApplication3 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        clientApplication3.markSubmitted();
        repository.save(clientApplication3);

        ClientApplication clientApplication4 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusHours(2));
        repository.save(clientApplication4);

        List<ClientApplication> result = repository.findCertainNumberOfMostRecentDraftAccounts(3, getBrokerIdentifiers());
        assertThat(result, hasItem(clientApplication1));
        assertThat(result, not(hasItem(clientApplication2)));
        assertThat(result, not(hasItem(clientApplication3)));
        assertThat(result, hasItem(clientApplication4));
        assertThat(result, hasSize(2));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getNumberOfDraftAccounts_ShouldReturnOnlyCountOfDraftApplications() throws Exception {
        DateTime modifiedDate = DateTime.now();
        ClientApplication clientApplication1 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(5));
        repository.save(clientApplication1);

        ClientApplication clientApplication2 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusDays(2));
        clientApplication2.markDeleted();
        repository.save(clientApplication2);

        ClientApplication clientApplication3 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate);
        clientApplication3.markSubmitted();
        repository.save(clientApplication3);

        ClientApplication clientApplication4 =  createApplication(DEFAULT_ADVISER_ID, modifiedDate.minusHours(2));
        repository.save(clientApplication4);

        Long count = repository.getNumberOfDraftAccounts(getBrokerIdentifiers());
        assertThat(count,is(2l));
    }
}
