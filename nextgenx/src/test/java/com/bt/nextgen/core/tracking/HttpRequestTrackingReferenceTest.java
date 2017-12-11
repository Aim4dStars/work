package com.bt.nextgen.core.tracking;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.bt.nextgen.core.util.SETTINGS.TRACKINGREFERENCE_SESSION_HEADER;
import static com.bt.nextgen.core.util.SETTINGS.TRACKINGREFERENCE_TRANSACTION_HEADER;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class HttpRequestTrackingReferenceTest {

    HttpServletRequest httpRequest = null;

    @Before
    public void setup(){
        httpRequest = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void testGet_instance_neverNull() throws Exception {
        Mockito.when(httpRequest.getHeader(Matchers.any(String.class))).thenReturn("1");

        assertThat(HttpRequestTrackingReference.httpRequestTrackingGenerator(), notNullValue());

        // nothing in header
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(httpRequest.getSession()).thenReturn(session);
        Mockito.when(session.getAttribute(Matchers.any(String.class))).thenReturn("1");

        assertThat(HttpRequestTrackingReference.httpRequestTrackingGenerator(), notNullValue());
    }

    @Test
    public void testGenerate() throws Exception {
        HttpRequestTrackingReference gen = HttpRequestTrackingReference.httpRequestTrackingGenerator();

        final String UUID = java.util.UUID.randomUUID().toString();
        final String transactionNumber = "1";
        // nothing in header
        Mockito.when(httpRequest.getHeader(TRACKINGREFERENCE_SESSION_HEADER.value())).thenReturn(UUID);
        Mockito.when(httpRequest.getHeader(TRACKINGREFERENCE_TRANSACTION_HEADER.value())).thenReturn(transactionNumber);


        final TrackingReference response = gen.generate(httpRequest);
        assertThat(response, CoreMatchers.equalTo((TrackingReference) new TrackingReferenceImpl(UUID + ".0000000000" + transactionNumber)));
    }
}