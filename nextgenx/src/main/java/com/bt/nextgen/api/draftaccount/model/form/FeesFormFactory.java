package com.bt.nextgen.api.draftaccount.model.form;

import org.apache.commons.lang.NotImplementedException;

import java.util.Map;

/**
 * Created by m040398 on 15/03/2016.
 */
public final class FeesFormFactory {

    private FeesFormFactory(){}

    public static IFeesForm getNewFeesForm(Object fees) {
        if (fees instanceof Map) {
            return new FeesForm((Map<String, Object>) fees);
        } else
            throw new IllegalArgumentException("unknown fees object: " + fees);
    }
}
