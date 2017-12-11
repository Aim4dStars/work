package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;


public class CorporateActionMultiBlockAccountElectionsDtoImpl implements CorporateActionAccountElectionsDto {
    @JsonView(JsonViews.Write.class)
    private List<CorporateActionAccountElectionDto> options;

    public CorporateActionMultiBlockAccountElectionsDtoImpl(List<CorporateActionAccountElectionDto> options) {
        this.options = options;
    }

    public static CorporateActionMultiBlockAccountElectionsDtoImpl createSingleAccountElection(Integer electionId, BigDecimal units,
                                                                                               BigDecimal percent) {
        List<CorporateActionAccountElectionDto> options = new ArrayList<>();
        options.add(new CorporateActionMultiBlockAccountElectionDtoImpl(electionId, units, percent));

        return new CorporateActionMultiBlockAccountElectionsDtoImpl(options);
    }

    @Override
    public List<CorporateActionAccountElectionDto> getOptions() {
        return options;
    }

    @JsonIgnore
    @Override
    public CorporateActionAccountElectionDto getPrimaryAccountElection() {
        return getOptions().get(0);
    }
}
