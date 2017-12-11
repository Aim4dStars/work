package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.ContactValue;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * Wrap a JSON {@code ContactValue} instance to implement the {@code IContactValue} interface.
 */
class ContactValueContact extends AbstractContactValue {

    private final ContactValue contact;

    public ContactValueContact(@Nonnull ContactValue contact, boolean preferred) {
        super(preferred);
        Assert.notNull(contact, "Cannot wrap a null ContactValue");
        this.contact = contact;
    }

    @Override
    public String getValue() {
        return contact.getValue();
    }

    @Override
    public String getCountryCode() {
        return null;
    }

    @Override
    public String getAreaCode() {
        return null;
    }

}
