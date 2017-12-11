package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.marketdata.v1.model.PodCastDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.marketondemand.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MarkitOnDemandPodCastDtoServiceImpl implements MarkitOnDemandPodCastDtoService {

    @Autowired
    private RssFeedService rssFeedService;

    private static final String MOD_FEED_URL = "mod.podcast.feed.url";

    @Override
    public PodCastDto findOne(ServiceErrors serviceErrors) {
        final RssFeed rssFeed = rssFeedService.readRSSFeed(MOD_FEED_URL, serviceErrors);
        if (rssFeed != null) {
            return convertToPodCast(rssFeed);
        }
        return null;
    }

    private PodCastDto convertToPodCast(RssFeed rssFeed) {
        final PodCastDto podCastDto = new PodCastDto();
        RssFeedChannel rssFeedChannel = rssFeed.getRssFeedChannel();
        if (rssFeedChannel != null) {

            final List<RssFeedItem> itemList = rssFeedChannel.getRssFeedItemList();
            final RssFeedItem latestRssFeedItem = CollectionUtils.isNotEmpty(itemList) ? itemList.get(0) : null;
            final RssFeedImage rssFeedImage = rssFeedChannel.getRssFeedImage();

            if (latestRssFeedItem != null) {
                podCastDto.setTitle(latestRssFeedItem.getTitle());
                podCastDto.setDescription(rssFeedChannel.getDescription());
                podCastDto.setDocumentUrl(convertToSecure(latestRssFeedItem.getDocumentLink()));
                podCastDto.setAudioUrl(convertToSecure(latestRssFeedItem.getAudioLink()));
                podCastDto.setPublishDate(latestRssFeedItem.getPublishDate());
                podCastDto.setAudioDuration(latestRssFeedItem.getDuration());
            }

            if (rssFeedImage != null) {
                podCastDto.setImageUrl(convertToSecure(rssFeedImage.getUrl()));
                podCastDto.setImageTitle(rssFeedImage.getTitle());
            }
        }
        return podCastDto;
    }

    private String convertToSecure(String urlString) {
        return StringUtils.replace(urlString, "http://", "https://");
    }
}
