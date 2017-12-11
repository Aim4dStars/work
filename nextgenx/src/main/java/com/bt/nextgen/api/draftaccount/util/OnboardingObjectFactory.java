package com.bt.nextgen.api.draftaccount.util;

public class OnboardingObjectFactory {

    private static final ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory contactObjectFactory = new ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory();

    public static ns.btfin_com.sharedservices.common.contact.v1_1.ObjectFactory getContactObjectFactory() {
        return contactObjectFactory;
    }
}
