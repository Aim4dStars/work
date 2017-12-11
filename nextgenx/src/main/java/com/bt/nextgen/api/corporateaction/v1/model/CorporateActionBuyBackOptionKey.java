package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.List;

public class CorporateActionBuyBackOptionKey {
    private List<CorporateActionSelectedOptionDto> options;
    private Integer minimumPriceId;

    public CorporateActionBuyBackOptionKey(List<CorporateActionSelectedOptionDto> options, Integer minimumPriceId) {
        this.options = options;
        this.minimumPriceId = minimumPriceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        CorporateActionBuyBackOptionKey other = (CorporateActionBuyBackOptionKey) obj;

        return objectsEqual(this.minimumPriceId, other.minimumPriceId) && optionsEqual(this.options, other.options);
    }

    private boolean optionsEqual(List<CorporateActionSelectedOptionDto> thisOptions,
                                 List<CorporateActionSelectedOptionDto> otherOptions) {
        if (thisOptions != null && otherOptions != null && thisOptions.size() == otherOptions.size()) {
            for (int i = 0; i < thisOptions.size(); i++) {
                CorporateActionSelectedOptionDto thisOption = thisOptions.get(i);
                CorporateActionSelectedOptionDto otherOption = otherOptions.get(i);

                if (!thisOption.equals(otherOption)) {
                    return false;
                }
            }

            return true;
        }

        return thisOptions == null && otherOptions == null;
    }

    private boolean objectsEqual(Object thisObject, Object otherObject) {
        if (thisObject == null && otherObject == null) {
            return true;
        }

        if (thisObject == null) {
            return false;
        }

        return thisObject.equals(otherObject);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        Integer minPrice = minimumPriceId != null ? minimumPriceId : 0;

        result = prime * result + minPrice.hashCode();

        if (options != null) {
            for (CorporateActionSelectedOptionDto optionDto : options) {
                result = prime * result + optionDto.hashCode();
            }
        }

        return result;
    }
}
