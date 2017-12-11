package com.bt.nextgen.integration.xml.extractor;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.jms.delegate.DomainObjectExtractor;
import com.bt.nextgen.core.jms.delegate.StreamExtractor;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.PartialInvalidationBrokerHolderImpl;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessIntegrationService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.*;
import static org.junit.Assert.*;

public class StreamResponseExtractorConcurrentTest extends BaseSecureIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(StreamResponseExtractorConcurrentTest.class);
    
    @Autowired
    BackGroundProcessIntegrationService bgpService;

    private static List<String> errors = Collections.synchronizedList(new ArrayList<String>());
    private static AtomicBoolean isStopped = new AtomicBoolean(false);
    private static int expectedValue;

    @Test
    public void extractData() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);

        BrokerParse t1 = new BrokerParse(latch);
        logger.debug("Start control");
        expectedValue = t1.parse();
        logger.debug("Expected value:" + expectedValue);

        Thread t2 = new BGPChecker();
        t1.start();
        t2.start();

        latch.await(2, TimeUnit.MINUTES);

        if (!errors.isEmpty()) {
            for (String error : errors) {
                logger.error(error);
            }
        }

        assertEquals("Expect empty errors", 0, errors.size());

        t2.join();
        logger.debug("end test");
    }

    private class BrokerParse extends Thread {

        private CountDownLatch latch;

        public BrokerParse(CountDownLatch latch) {
            this.latch = latch;
            setName("BrokerParseThread");
        }

        public int parse() {
            int size = -1;

            try {
                StreamExtractor extractor = new DomainObjectExtractor();
                PartialInvalidationBrokerHolderImpl extractedObject;
                extractedObject = extractor.extractData(getXml(), PartialInvalidationBrokerHolderImpl.class);
                size = extractedObject.getBrokerMap().size();

                if (expectedValue != 0 && size != expectedValue) {
                    errors.add(String.format("Value: %d doesn't match expected value: %d", size, expectedValue));
                }

                logger.debug("brokers" + size);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                errors.add(e.getMessage());
            }
            return size;
        }

        public void run() {
            while (latch.getCount() != 0) {
                parse();
                latch.countDown();
            }
            isStopped.set(true);
        }
    }

    private class BGPChecker extends Thread {

        public BGPChecker() {
            setName("BGPCheckerThread");
        }

        public void run() {
            logger.debug("BGPChecker started");
            while (!isStopped.get()) {
                FailFastErrorsImpl e = new FailFastErrorsImpl();
                List<BackGroundProcess> backGroundProcesses = bgpService.getBackGroundProcesses(e);
                logger.debug("Number of bg processes: " + backGroundProcesses.size());
            }
            logger.debug("BGPChecker stopped");
        }
    }

    private InputStream getXml() throws IOException, ParserConfigurationException, SAXException {
        final String path = "com/bt/nextgen/integration/xml/extractor/BrokerHierarchy.xml";
        final InputStream is = currentThread().getContextClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        return is;
    }
}
