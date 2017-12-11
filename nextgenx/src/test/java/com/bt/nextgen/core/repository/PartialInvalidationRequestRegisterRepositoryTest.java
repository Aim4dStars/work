package com.bt.nextgen.core.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.repository.PartialInvalidationRequestKey;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegister;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegisterRepository;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by L054821 on 27/04/2015.
 */
public class PartialInvalidationRequestRegisterRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    PartialInvalidationRequestRegisterRepository partialInvalidationRequestRegisterRepository;

    private PartialInvalidationRequestRegister p1;
    private PartialInvalidationRequestRegister p2;
    private PartialInvalidationRequestRegister p3;
    private  PartialInvalidationRequestKey key1;
    private  PartialInvalidationRequestKey key2;
    private  PartialInvalidationRequestKey key3;
    List<PartialInvalidationRequestRegister> entries = new ArrayList<>();

    @Before
    public void setUp() {
        key1 = new PartialInvalidationRequestKey();
        key1.setEventType(EventType.CACHE_INVALIDATION.name());
        key1.setCreateDate(new Date());
        key1.setParamVal("1124");
        key1.setRequestType("CACHE_INV");

        key2 = new PartialInvalidationRequestKey();
        key2.setEventType(EventType.CACHE_INVALIDATION.name());
        key2.setCreateDate(new Date());
        key2.setParamVal("11245");
        key2.setRequestType("CACHE_INV");

        key3 = new PartialInvalidationRequestKey();
        key3.setEventType(EventType.CACHE_INVALIDATION.name());
        key3.setCreateDate(new Date());
        key3.setParamVal("11246");
        key3.setRequestType("CACHE_INV");

        p1 = new PartialInvalidationRequestRegister();
        p1.setRequestKey(key1);
        p1.setSentTime(new Date());
        p1.setCorrelationId("abcd-1234-5678");
        p1.setMode("CACHE_INV");

        p2 = new PartialInvalidationRequestRegister();
        p2.setRequestKey(key2);
        p2.setSentTime(new Date());
        p2.setCorrelationId("abcd56-1234-5678");

        p3 = new PartialInvalidationRequestRegister();
        p3.setRequestKey(key3);
        p3.setSentTime(new Date());
        p3.setCorrelationId("abcd23-1234-5678");

        List<PartialInvalidationRequestRegister> registers = new ArrayList<>();
        registers.add(p1);
        registers.add(p2);
        registers.add(p3);
        cleanDB();
        updateDB();

    }

    @Test
    public void test_updateRequestEntry(){
        entries = partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry();
        assertEquals(3, entries.size());
    }

    @Test
    public void test_findRequestEntry(){
        PartialInvalidationRequestRegister entry = partialInvalidationRequestRegisterRepository.findRequestEntry(key2);
        assertEquals(entry.getCorrelationId(), p2.getCorrelationId());
        assertEquals(entry.getMode(), p2.getMode());

        assertThat(p2.getRequestKey().getCreateDate(), is(entry.getRequestKey().getCreateDate()));
        assertThat(p2.getRequestKey().getEventType(), is(entry.getRequestKey().getEventType()));
        assertThat(p2.getRequestKey().getParamVal(), is(entry.getRequestKey().getParamVal()));
        assertThat(p2.getRequestKey().getRequestType(), is(entry.getRequestKey().getRequestType()));
    }

    @Test
    public void test_insertgEntry(){
        entries = partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry();
        assertEquals(3, entries.size());
        boolean success = partialInvalidationRequestRegisterRepository.insertRequestEntry(p2);
        assertThat(success, is(false));

    }

    @Test
    public void test_updateEntry(){
        entries = partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry();
        assertEquals(3, entries.size());
        p2.setCorrelationId("121213131");
        boolean success = partialInvalidationRequestRegisterRepository.updateRequestEntry(p2);
        PartialInvalidationRequestRegister  entity = partialInvalidationRequestRegisterRepository.findRequestEntry(key2);

        assertThat(success, is(true));
        assertThat(entity.getCorrelationId(), is("121213131"));
        p2.setCorrelationId("abcd56-1234-5678");
         success = partialInvalidationRequestRegisterRepository.updateRequestEntry(p2);
        assertThat(success, is(true));
        entity = partialInvalidationRequestRegisterRepository.findRequestEntry(key2);
        assertThat(entity.getCorrelationId(), is("abcd56-1234-5678"));


    }


    @Test
    public void test_fetchPartialInvalidationRequestRegisterEntry(){
        entries = partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry();
        assertEquals(3, entries.size());
    }

    @Test
    public void test_removeAllEntriesBeforeGivenDate(){
        DateTime dt = new DateTime();
        DateTime futureTime = dt.plusHours(1);
        partialInvalidationRequestRegisterRepository.removeAllEntriesBeforeGivenDate(futureTime.toDate());
        entries = partialInvalidationRequestRegisterRepository.fetchPartialInvalidationRequestRegisterEntry();
        assertEquals(0, entries.size());
    }

    public void cleanDB(){
        partialInvalidationRequestRegisterRepository.removeAllEntriesBeforeGivenDate(new DateTime().toDate());
    }

    public void updateDB(){
        partialInvalidationRequestRegisterRepository.insertRequestEntry(p1);
        partialInvalidationRequestRegisterRepository.insertRequestEntry(p2);
        partialInvalidationRequestRegisterRepository.insertRequestEntry(p3);
    }

}