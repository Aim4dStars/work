package com.bt.nextgen.service.avaloq.basil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M035995 on 27/09/2016.
 */
public class ImageResponseImpl implements ImageResponse {

    private List<ImageDetails> imageDetailsList = new ArrayList<>();

    private String status;

    private Integer numberOfMatchingImages;

    private String error;

    public String getStatus() {
        return status;
    }

    public Integer getNumberOfMatchingImages() {
        return numberOfMatchingImages;
    }

    public String getError() {
        return error;
    }

    @Override
    public List<ImageDetails> getPolicyDocuments() {
        return imageDetailsList;
    }
}
