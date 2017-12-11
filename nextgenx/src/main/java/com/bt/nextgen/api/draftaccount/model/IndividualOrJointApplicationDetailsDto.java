package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.InvestorDto;

import java.util.List;

public class IndividualOrJointApplicationDetailsDto extends ClientApplicationDetailsDto {
    private List<InvestorDto> investors;

    public IndividualOrJointApplicationDetailsDto withInvestors(List<InvestorDto> investors){
        this.investors = investors;
        return this;
        
    }

    public List<InvestorDto> getInvestors() {
        return investors;
    }
}
