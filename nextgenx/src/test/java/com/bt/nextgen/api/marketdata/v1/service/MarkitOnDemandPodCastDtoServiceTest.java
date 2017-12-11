package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.marketdata.v1.model.PodCastDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.marketondemand.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MarkitOnDemandPodCastDtoServiceTest {

    @Mock
    RssFeedService rssFeedService;

    @InjectMocks
    private MarkitOnDemandPodCastDtoServiceImpl modService;

    private ServiceErrors serviceErrors;
    private RssFeed rssFeed;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        rssFeed = getRssFeed();
    }

    @Test
    public void testFindOneRssFeedFail() {
        Mockito.when(rssFeedService.readRSSFeed(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(null);
        PodCastDto podCastDto = modService.findOne(serviceErrors);
        Assert.assertNull(podCastDto);
    }

    @Test
    public void testFindOneRssFeedSuccess() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Mockito.when(rssFeedService.readRSSFeed(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(rssFeed);
        PodCastDto podCastDto = modService.findOne(serviceErrors);
        Assert.assertNotNull(podCastDto);
        Assert.assertEquals(podCastDto.getTitle(), "BT's MOD updates");
        Assert.assertEquals(podCastDto.getDescription(), "Channel description");
        Assert.assertEquals(podCastDto.getDocumentUrl(), "https://www.bt.com.au/downloads/podcasts/2015/12-december/20151224-Chris-Caton-Update.pdf");
        Assert.assertEquals(podCastDto.getAudioUrl(), "https://www.bt.com.au/downloads/podcasts/2015/12-december/20151224-Chris-Caton-Update.mp3");
        Assert.assertEquals(podCastDto.getPublishDate(), "Fri, 16 Oct 2015 08:45:00 +1000");
        Assert.assertEquals(podCastDto.getImageTitle(), "image title");
        Assert.assertEquals(podCastDto.getImageUrl(), "https://www.bt.com.au/rss/Chris-Caton-itunes.jpg");
        Assert.assertEquals(podCastDto.getAudioDuration(), "3:40");
    }


    private RssFeed getRssFeed() {
        RssFeed rssFeed1 = new RssFeed() {
            @Override
            public RssFeedChannel getRssFeedChannel() {
                return getNewRssFeedChannel();
            }
        };
        return rssFeed1;
    }

    private RssFeedChannel getNewRssFeedChannel() {

        RssFeedChannel rssFeedChannel = new RssFeedChannel() {
            @Override
            public String getTitle() {
                return "Channel title";
            }

            @Override
            public String getDescription() {
                return "Channel description";
            }

            @Override
            public RssFeedImage getRssFeedImage() {
                return getNewRssFeedImage();
            }

            @Override
            public List<RssFeedItem> getRssFeedItemList() {
                return Collections.singletonList(getNewRssFeedItem());
            }
        };

        return rssFeedChannel;
    }

    private RssFeedImage getNewRssFeedImage() {
        RssFeedImage rssFeedImage = new RssFeedImage() {
            @Override
            public String getUrl() {
                return "http://www.bt.com.au/rss/Chris-Caton-itunes.jpg";
            }

            @Override
            public String getTitle() {
                return "image title";
            }

            @Override
            public String getHeight() {
                return null;
            }

            @Override
            public String getWidth() {
                return null;
            }
        };

        return rssFeedImage;
    }

    private RssFeedItem getNewRssFeedItem() {

        RssFeedItem rssFeedItem = new RssFeedItem() {
            @Override
            public String getTitle() {
                return "BT's MOD updates";
            }

            @Override
            public String getDescription() {
                return "BT's MOD updates";
            }

            @Override
            public String getDocumentLink() {
                return "http://www.bt.com.au/downloads/podcasts/2015/12-december/20151224-Chris-Caton-Update.pdf";
            }

            @Override
            public String getPublishDate() {
                return "Fri, 16 Oct 2015 08:45:00 +1000";
            }

            @Override
            public String getAudioLink() {
                return "http://www.bt.com.au/downloads/podcasts/2015/12-december/20151224-Chris-Caton-Update.mp3";
            }

            @Override
            public String getDuration() {
                return "3:40";
            }
        };

        return rssFeedItem;
    }

}
