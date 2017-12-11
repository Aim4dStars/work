package com.bt.nextgen.api.user.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

public class TermsAndConditionsDto extends BaseDto implements KeyedDto<TermsAndConditionsDtoKey> {
    private TermsAndConditionsDtoKey key;
    private String description;
    private DateTime conditionsUpdated;
    private DateTime conditionsAccepted;

    public TermsAndConditionsDto(TermsAndConditionsDtoKey key, String description, DateTime conditionsUpdated,
            DateTime conditionsAccepted) {
        this(key);
        this.description = description;
        this.conditionsUpdated = conditionsUpdated;
        this.conditionsAccepted = conditionsAccepted;
    }

    public TermsAndConditionsDto(TermsAndConditionsDtoKey key) {
        super();
        this.key = key;
    }

    public TermsAndConditionsDtoKey getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getConditionsUpdated() {
        return conditionsUpdated;
    }

    public DateTime getConditionsAccepted() {
        return conditionsAccepted;
    }

}
