package com.bt.nextgen.api.draftaccount.model.form.v1;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentTier;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.SlidingScaleFeeTier;

/**
 * Wrapper class for {@code SlidingScaleFeeTier} that implements {@code IFeeComponentTier}.
 */
class FeeComponentTier implements IFeeComponentTier {

    /** Convenient converter. */
    public static final Converter<SlidingScaleFeeTier, IFeeComponentTier> CONVERTER = new Converter<SlidingScaleFeeTier, IFeeComponentTier>() {
        @Override
        public IFeeComponentTier convert(SlidingScaleFeeTier tier) {
            return new FeeComponentTier(tier);
        }
    };

    private final SlidingScaleFeeTier tier;

    private FeeComponentTier(SlidingScaleFeeTier tier) {
        this.tier = tier;
    }

    @Override
    public String getLowerBound() {
        return tier.getLowerBound();
    }

    @Override
    public String getUpperBound() {
        return tier.getUpperBound();
    }

    @Override
    public String getPercentage() {
        return tier.getPercentage();
    }
}
