package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.InvestorDto;

import java.util.List;

public class CompanyDetailsDto extends ClientApplicationDetailsDto {

    private CompanyDto company;
    private List<InvestorDto> directorsSecretariesSignatories;
    private List <InvestorDto> shareholders;

    public CompanyDetailsDto withCompany(CompanyDto company) {
        this.company = company;
        return this;
    }
    
    public CompanyDetailsDto withDirectorsSecretariesSignatories(List<InvestorDto> directorsSecretariesSignatories) {
        this.directorsSecretariesSignatories = directorsSecretariesSignatories;
        return this;
    }

    public CompanyDetailsDto withShareholders(List<InvestorDto> shareholders) {
        this.shareholders = shareholders;
        return this;
    }

    public List<InvestorDto> getInvestors() {
        return directorsSecretariesSignatories;
    }

    public List<InvestorDto> getShareholders() {
        return shareholders;
    }

    public CompanyDto getCompany() {
        return company;
    }
}
