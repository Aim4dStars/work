package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;

/**
 * @deprecated Class to be removed as part of Packaging changes. Target release April '18.
 * @author m028796
 * 
 */
@Deprecated
@ServiceBean(xpath = "offer")
public class OfferDetailImpl implements OfferDetail {

    @ServiceElement(xpath = "offer_id/val")
    private String offerId;

    @Override
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

}
