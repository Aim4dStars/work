package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PhoneNumber;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * Contact value implementation that wraps around a JSON PhoneNumber instance.
 */
class PhoneNumberContact extends AbstractContactValue {

    private final PhoneNumber number;

    /**
     * Full gamut constructor.
     * @param number number instance (non-null).
     * @param preferred preferred contact flag.
     */
    public PhoneNumberContact(@Nonnull PhoneNumber number, boolean preferred) {
        super(preferred);
        Assert.notNull(number, "Cannot wrap a null PhoneNumber");
        this.number = number;
    }

    @Override
    public String getValue() {
        return number.getValue();
    }

    @Override
    public String getCountryCode() {
        return number.getCountryCode();
    }

    @Override
    public String getAreaCode() {
        return number.getAreaCode();
    }

}
