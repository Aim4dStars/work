package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.config.TestConfig;
import com.bt.nextgen.core.repository.PartialInvalidationRequestKey;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegister;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegisterRepository;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by L054821 on 24/04/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class RequestRegisterSchedulerTest {

    @InjectMocks
    private RequestRegisterScheduler requestRegisterScheduler;

    @Mock
    private EntityManager entityManager;

    @Mock
    PartialInvalidationRequestRegisterRepository partialInvalidationRequestRegisterRepository;

    @Before
    public void setUp(){
        PartialInvalidationRequestKey key1 = new PartialInvalidationRequestKey();
        key1.setEventType(EventType.CACHE_INVALIDATION.name());
        key1.setCreateDate(new Date());
        key1.setParamVal("1124");
        key1.setRequestType("CACHE_INV");

        PartialInvalidationRequestKey key2 = new PartialInvalidationRequestKey();
        key2.setEventType(EventType.CACHE_INVALIDATION.name());
        key2.setCreateDate(new Date());
        key2.setParamVal("11245");
        key2.setRequestType("CACHE_INV");

        PartialInvalidationRequestKey key3 = new PartialInvalidationRequestKey();
        key3.setEventType(EventType.CACHE_INVALIDATION.name());
        key3.setCreateDate(new Date());
        key3.setParamVal("11246");
        key3.setRequestType("CACHE_INV");

        PartialInvalidationRequestRegister p1 = new PartialInvalidationRequestRegister();
        p1.setRequestKey(key1);
        p1.setSentTime(new Date());

        PartialInvalidationRequestRegister p2 = new PartialInvalidationRequestRegister();
        p2.setRequestKey(key2);
        p2.setSentTime(new Date());

        PartialInvalidationRequestRegister p3 = new PartialInvalidationRequestRegister();
        p3.setRequestKey(key3);
        p3.setSentTime(new Date());

        List<PartialInvalidationRequestRegister> registers = new ArrayList<>();
        registers.add(p1);
        registers.add(p2);
        registers.add(p3);

        Mockito.when(partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry()).thenReturn(registers);
    }

    @Test
    public void test_scheduling(){
        requestRegisterScheduler.removePartialInvalidationRecords();
        verify(partialInvalidationRequestRegisterRepository, times(1)).fetchPartialInvalidationRequestRegisterEntry();
    }

}
