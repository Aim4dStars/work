package com.bt.nextgen.service.avaloq.basil;

import java.util.List;

/**
 * Created by M035995 on 27/09/2016.
 */
public interface ImageResponse {

    /**
     * Get the list of {@link ImageDetails}
     *
     * @return list of ImageDetails object
     */
    public List<ImageDetails> getPolicyDocuments();

    /**
     * Returns the total number of mataching images
     *
     * @return Integer
     */
    public Integer getNumberOfMatchingImages();

    /**
     * Get the status
     *
     * @return String
     */
    public String getStatus();

    /**
     * Get the error details, if any
     *
     * @return String
     */
    public String getError();
}
