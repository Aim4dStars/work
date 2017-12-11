package com.bt.nextgen.service.marketondemand;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.marketondemand.RssFeed;
import com.bt.nextgen.service.web.UrlProxyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RssFeedServiceTest {

    @InjectMocks
    RssFeedServiceImpl rssFeedService = new RssFeedServiceImpl();

    @Mock
    private UrlProxyService urlProxyService;

    private ServiceErrors serviceErrors;
    private static final String MOD_FEED_URL = "mod.podcast.feed.url";
    private URLConnection urlConnection;
    private RssFeed response;

    @Before
    public void setup() throws IOException {
        serviceErrors = new ServiceErrorsImpl();
        urlConnection = mock(URLConnection.class);
        when(urlProxyService.connect(any(URL.class))).thenReturn(urlConnection);
    }

    @Test
    public void testRSSFeedSuccess() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("com/bt/nextgen/service/integration/marketondemand/caton-markets-feed.xml").getFile());
        when(urlConnection.getInputStream()).thenReturn(new FileInputStream(file));

        response = rssFeedService.readRSSFeed(MOD_FEED_URL, serviceErrors);
        assertNotNull(response.getRssFeedChannel());
        assertNotNull(response.getRssFeedChannel().getDescription());
        assertEquals(response.getRssFeedChannel().getTitle(), "BT Three Minute Markets Update");
        assertEquals(response.getRssFeedChannel().getRssFeedItemList().size(), 207);
    }

    @Test
    public void testRSSFeedFailure() throws IOException {
        when(urlConnection.getInputStream()).thenReturn(null);
        try {
            response = rssFeedService.readRSSFeed(MOD_FEED_URL, serviceErrors);
        } catch (RuntimeException e) {
            assertNull(response);
        }
    }
}
