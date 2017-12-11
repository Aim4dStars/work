package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

import static com.bt.nextgen.api.draftaccount.FormDataConstants.FIELD_CORRELATION_ID;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class Correlated {

    final Map<String, Object> map;

    Correlated(Map<String, Object> map) {
        this.map = map;
    }

    public Integer getCorrelationSequenceNumber() {
        final Object id = map.get(FIELD_CORRELATION_ID);
        return id == null ? null : (Integer) id;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> getSubMap(String key) {
        return (Map<String, Object>) map.get(key);
    }
}
