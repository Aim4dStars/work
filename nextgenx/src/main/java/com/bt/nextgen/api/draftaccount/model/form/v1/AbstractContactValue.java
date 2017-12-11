package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IContactValue;

/**
 * Base class for the {@code PhoneNumberContact} and {@code ContactValueContact}. Takes care of the preferred flag and
 * correspondence
 */
abstract class AbstractContactValue implements IContactValue {

    private final boolean preferred;

    AbstractContactValue(boolean preferred) {
        this.preferred = preferred;
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final boolean isPreferredContact() {
        return preferred;
    }

}
