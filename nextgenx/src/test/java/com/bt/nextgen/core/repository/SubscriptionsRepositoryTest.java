package com.bt.nextgen.core.repository;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionsRepositoryTest {

    @InjectMocks
    private SubscriptionsRepositoryImpl subscriptionsRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    public void findAllByStatus_queryTest() throws Exception {

        List<String> accountIds = Arrays.asList("11", "22", "33", "44", "55");

        final Map<String, List<String>> accountIdsMap = new HashMap<>();
        String queryStatement = subscriptionsRepository.getInnerQuery(accountIdsMap, accountIds);

        Assert.assertThat(queryStatement, CoreMatchers.equalTo(" ( ( a.accountId in (:accountIds0) ) ) "));

    }

    @Test
    public void findAllByStatus_queryTest_1000() throws Exception {

        String[] array = new String[1001];
        for (int a = 0; a < array.length; a++) {
            array[a] = a + "";
        }

        List<String> accountIds = Arrays.asList(array);

        final Map<String, List<String>> accountIdsMap = new HashMap<>();
        String queryStatement = subscriptionsRepository.getInnerQuery(accountIdsMap, accountIds);

        Assert.assertThat(queryStatement, CoreMatchers.equalTo(" ( ( a.accountId in (:accountIds0)) or ( a.accountId in (:accountIds1) ) ) "));
        Assert.assertThat(accountIdsMap.size(), is(equalTo(2)));
        Assert.assertThat(accountIdsMap, hasKey(equalTo("accountIds0")));
        Assert.assertThat(accountIdsMap, hasKey(equalTo("accountIds1")));
    }

}