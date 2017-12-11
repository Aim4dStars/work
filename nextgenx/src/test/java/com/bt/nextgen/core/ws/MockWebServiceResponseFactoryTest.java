package com.bt.nextgen.core.ws;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MockWebServiceResponseFactoryTest {

    @Before
    public void setUp() {
        MockWebServiceResponseFactory.setDefaultResponses();
    }

    @Test
    public void testDefaultResponses() {

        List<MockWebServiceResponse> mockWebServiceResponseList = MockWebServiceResponseFactory.getDefaultResponses("stub");
        assertTrue(mockWebServiceResponseList.size() > 0);
    }

    @Test
    public void testSafiDefaultResponses() {

            List<MockWebServiceResponse> mockWebServiceResponseList = MockWebServiceResponseFactory.getDefaultResponses("safi");
            assertTrue(mockWebServiceResponseList.size() > 0);
        }

    }

