package com.bt.nextgen.api.modelportfolio.v2.model.detail;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * 
 * @deprecated ModelOffer will be removed once the Packaging changes have been finalised. Currently targeting the April '18
 *             release.
 * 
 */
@Deprecated
public class ModelOfferDto extends BaseDto {
    @JsonView(JsonViews.Write.class)
    private String offerId;

    private String offerName;

    public ModelOfferDto() {
        super();
    }

    public ModelOfferDto(String offerId) {
        super();
        this.offerId = offerId;
    }

    public ModelOfferDto(String offerId, String offerName) {
        super();
        this.offerId = offerId;
        this.offerName = offerName;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getOfferName() {
        return offerName;
    }

}
