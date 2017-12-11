package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.InvestorDto;

import java.util.List;

public class IndividualTrustApplicationDetailsDto extends TrustApplicationDetailsDto {
    private List<InvestorDto> trustees;

    public IndividualTrustApplicationDetailsDto withTrustees(List<InvestorDto> trustees) {
        this.trustees = trustees;
        return this;
    }

    public List<InvestorDto> getTrustees() {
        return trustees;
    }
}
