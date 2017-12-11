package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;

public class CorporateActionSelectedOptionsDto {
    @JsonView(JsonViews.Write.class)
    private List<CorporateActionSelectedOptionDto> options;

    @JsonView(JsonViews.Write.class)
    private Integer minimumPriceId;

    public CorporateActionSelectedOptionsDto() {
        // Empty constructor
    }

    public CorporateActionSelectedOptionsDto(List<CorporateActionSelectedOptionDto> options) {
        this.options = options;
    }

    public CorporateActionSelectedOptionsDto(List<CorporateActionSelectedOptionDto> options, Integer minimumPriceId) {
        this.options = options;
        this.minimumPriceId = minimumPriceId;
    }

    public CorporateActionSelectedOptionsDto(CorporateActionSelectedOptionDto corporateActionSelectedOptionDto) {
        options = new ArrayList<>(1);
        options.add(corporateActionSelectedOptionDto);
    }

    public static CorporateActionSelectedOptionsDto createSingleAccountElection(Integer electionId, BigDecimal units, BigDecimal percent,
                                                                                BigDecimal oversubscribe) {
        CorporateActionSelectedOptionsDto electionsDto =
                new CorporateActionSelectedOptionsDto(new CorporateActionSelectedOptionDto(electionId, units, percent, oversubscribe));

        return electionsDto;
    }

    public List<CorporateActionSelectedOptionDto> getOptions() {
        return options;
    }

    public Integer getMinimumPriceId() {
        return minimumPriceId;
    }

    public CorporateActionSelectedOptionDto getPrimarySelectedOption() {
        return options.get(0);
    }
}
