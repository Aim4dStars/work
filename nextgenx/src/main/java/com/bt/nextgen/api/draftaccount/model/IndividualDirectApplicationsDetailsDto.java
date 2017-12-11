package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.InvestorDto;

import java.util.List;

public class IndividualDirectApplicationsDetailsDto extends ClientApplicationDetailsDto {

    private List<InvestorDto> investors;

    private InvestmentChoiceDto investmentChoice;

    public IndividualDirectApplicationsDetailsDto withInvestors(List<InvestorDto> investors) {
        this.investors = investors;
        return this;
    }

    public IndividualDirectApplicationsDetailsDto withInvestmentChoice(InvestmentChoiceDto investmentChoiceDto) {
        this.investmentChoice = investmentChoiceDto;
        return this;
    }

    public IndividualDirectApplicationsDetailsDto withApplicationOriginType(String applicationOriginType) {
        this.applicationOriginType = applicationOriginType;
        return this;
    }

    public List<InvestorDto> getInvestors() {
        return investors;
    }

    public InvestmentChoiceDto getInvestmentChoice() {
        return investmentChoice;
    }
}
