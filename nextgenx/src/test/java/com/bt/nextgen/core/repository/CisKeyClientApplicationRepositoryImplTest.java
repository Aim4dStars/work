package com.bt.nextgen.core.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepositoryImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by F058391 on 19/11/2015.
 */
public class CisKeyClientApplicationRepositoryImplTest extends BaseSecureIntegrationTest {

    private static final String ADVISER_ID_1 = "adviser-id-1";
    private static final String ADVISER_ID_2 = "adviser-id-2";
    private static final String DEFAULT_CIS_KEY = "12345678";

    @Autowired
    private CisKeyClientApplicationRepositoryImpl repository;

    @Autowired
    private ClientApplicationRepositoryImpl clientApplicationRepository;

    @Test
    @Transactional
    @Rollback(true)
    public void shouldReturnAListOfClientApplicationsForGivenCisKey(){
        DateTime date = DateTime.parse("2005-09-24T04:00:00");

        ClientApplication clientApplication1 = new ClientApplication();
        clientApplication1.setAdviserPositionId(ADVISER_ID_1);
        clientApplication1.setLastModifiedAt(date);
        clientApplicationRepository.save(clientApplication1);

        ClientApplication clientApplication2 = new ClientApplication();
        clientApplication2.setAdviserPositionId(ADVISER_ID_2);
        clientApplication2.setLastModifiedAt(date);
        clientApplicationRepository.save(clientApplication2);

        CisKeyClientApplication cisKeyClientApplication1 = new CisKeyClientApplication(DEFAULT_CIS_KEY, clientApplication1);
        repository.save(cisKeyClientApplication1);

        CisKeyClientApplication cisKeyClientApplication2 = new CisKeyClientApplication(DEFAULT_CIS_KEY, clientApplication2);
        repository.save(cisKeyClientApplication2);

        List<ClientApplication> clientApplications = repository.findClientApplicationsForCisKey(DEFAULT_CIS_KEY);
        assertThat(clientApplications.size(), is(2));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void shouldReturnAnEmptyListIfNoCisKeyEntryIsFound(){
        List<ClientApplication> clientApplications = repository.findClientApplicationsForCisKey("123AAA");
        assertThat(clientApplications.size(), is(0));
    }
}